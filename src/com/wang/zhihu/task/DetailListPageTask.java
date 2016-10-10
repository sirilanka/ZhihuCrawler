package com.wang.zhihu.task;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import com.wang.core.dao.ConnectionManager;
import com.wang.core.parser.ListPageParser;
import com.wang.core.util.Config;
import com.wang.core.util.Constants;
import com.wang.core.util.Md5Util;
import com.wang.core.util.SimpleInvocationHandler;
import com.wang.core.util.SimpleLogger;
import com.wang.zhihu.ZhihuHttpClient;
import com.wang.zhihu.entity.Page;
import com.wang.zhihu.entity.User;
import com.wang.zhihu.parser.ZhihuUserListPageParser;
/**
 * @author 王亚楼(1374178468@qq.com)
 * @since 2016.10
 */
public class DetailListPageTask extends AbstractPageTask{

	private static final Log log = SimpleLogger.getLog(DetailListPageTask.class);
	private static ListPageParser proxyUserListPageParser;
	
	private static Map<Thread, Connection> connectionMap = new ConcurrentHashMap<>();

	public DetailListPageTask(HttpRequestBase request, boolean proxyFlag) {
		super(request, proxyFlag);
	}

	public DetailListPageTask(String url, boolean proxyFlag) {
		super(url, proxyFlag);
	}

	static{
		proxyUserListPageParser = getProxyUserListParser();
	}
	
	private static ListPageParser getProxyUserListParser(){
		ListPageParser userListPageParser = ZhihuUserListPageParser.getInstance();
		InvocationHandler invocationHandler = new SimpleInvocationHandler(userListPageParser);
		ListPageParser proxyUserListPageParser = (ListPageParser) Proxy.
				newProxyInstance(userListPageParser.getClass().getClassLoader(), 
				userListPageParser.getClass().getInterfaces(), invocationHandler);
		return proxyUserListPageParser;
	}

	@Override
	protected void handle(Page page) {
		@SuppressWarnings("unchecked")
		List<User> list = (List<User>) proxyUserListPageParser.parseListPage(page);
		for(User u : list){
			log.info("解析用户成功：" + u.toString());
			if(Config.INSTANCE.dbEnable){
				Connection conn = getConnection();
				if(zhihudao.insertUser(conn,u)) ZhihuHttpClient.parseUserCount.incrementAndGet();
				for(int j = 0;j < u.getFollowees()/20;j++){
					if(zhiHuHttpClient.getDetailListPageThreadPool().getQueue().size() > 1000)continue;
					String nextUrl = String.format(Constants.USER_FOLLOWEES_URL, u.getUserToken(), j * 20);
					if(zhihudao.insertUrl(conn, Md5Util.convert2Md5(nextUrl)) || 
							zhiHuHttpClient.getDetailListPageThreadPool().getActiveCount() == 1){
						HttpGet request = new HttpGet(nextUrl);
						request.setHeader("authorization", "oauth" + ZhihuHttpClient.getAuthorization());
						zhiHuHttpClient.getDetailListPageThreadPool().
							execute(new DetailListPageTask(request, Config.INSTANCE.isProxy));
					}
				}
			}
		}
	}

	@Override
	protected void retry() {
		zhiHuHttpClient.getDetailListPageThreadPool().execute(new DetailListPageTask(request, Config.INSTANCE.isProxy));
		
	}
	
	private Connection getConnection(){
		Thread currentThread = Thread.currentThread();
		Connection conn = null;
		if(!connectionMap.containsKey(currentThread)){
			conn = ConnectionManager.INSTANCE.getConnection();
			connectionMap.put(currentThread, conn);
		}else {
			conn = connectionMap.get(currentThread);
		}
		return conn;
	}
	
	public static Map<Thread, Connection> getConnectionMap(){
		return connectionMap;
	}
}
