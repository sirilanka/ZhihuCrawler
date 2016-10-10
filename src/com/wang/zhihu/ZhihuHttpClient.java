package com.wang.zhihu;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.http.client.methods.HttpGet;

import com.wang.core.client.AbstractHttpClient;
import com.wang.core.util.Config;
import com.wang.core.util.Constants;
import com.wang.core.util.HttpClientUtil;
import com.wang.core.util.SimpleLogger;
import com.wang.core.util.SimpleThreadPoolExecutor;
import com.wang.core.util.ThreadPoolMonitor;
import com.wang.zhihu.dao.ZhihuDaoImpl;
import com.wang.zhihu.task.DetailListPageTask;
/**
 * @author 王亚楼(1374178468@qq.com)
 * @since 2016.10
 */
public class ZhihuHttpClient extends AbstractHttpClient{

	private static Log log = SimpleLogger.getLog(ZhihuHttpClient.class);
	private volatile static ZhihuHttpClient instance;
	
	public static AtomicInteger parseUserCount = new AtomicInteger(0);
	private static long startTime = System.currentTimeMillis();
	public static volatile boolean isStop = false;
	
	public static ZhihuHttpClient getInstance() {
		ZhihuHttpClient curr = instance;
		if(curr == null){
			synchronized (ZhihuHttpClient.class) {
				curr = instance;
				if(curr == null){
					curr = instance = new ZhihuHttpClient();
				}
			}
		}
		return curr;
	}
	
	private ZhihuHttpClient(){
		initHttpClient();
		initThreadPool();
	};
	private ThreadPoolExecutor detailPageThreadPool;
	
	private ThreadPoolExecutor listPageThreadPool;
	
	private ThreadPoolExecutor detailListPageThreadPool;
	
	private static String authorization;
	
	private void initHttpClient(){
		authorization = initAuthorization();
		if(Config.INSTANCE.dbEnable){
			ZhihuDaoImpl.DBTableInit();
		}
	}
	
	private void initThreadPool(){
		detailListPageThreadPool = new SimpleThreadPoolExecutor(Config.INSTANCE.downloadThreadSize,
				Config.INSTANCE.downloadThreadSize, 
				0l, TimeUnit.MILLISECONDS, 
				new LinkedBlockingQueue<Runnable>(2000), new ThreadPoolExecutor.DiscardPolicy(),
				"detailListPageThreadPool");
		new Thread(new ThreadPoolMonitor(detailListPageThreadPool, "detailListPageThreadPool")).start();;
	}
	
	private String initAuthorization(){
		String content = null;
		try {
			content = HttpClientUtil.INSTANCE.getWebPage(Config.INSTANCE.startURL);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Pattern pattern = Pattern.compile("http://static\\.zhihu\\.com/heifetz/main\\.app\\.\\w*\\.js");
		Matcher matcher = pattern.matcher(content);
		String jsSrc = null;
		if(matcher.find()){
			jsSrc = matcher.group(0);
		}else{
			throw new RuntimeException("find no js pattern src");
		}
		String jsContent = null;
		try {
			jsContent = HttpClientUtil.INSTANCE.getWebPage(jsSrc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pattern = Pattern.compile("CLIENT_ALIAS=\"((\\w)*)\")");
		matcher = pattern.matcher(jsContent);
		if(matcher.find()){
			return matcher.group(1);
		}else{
			throw new RuntimeException("not get a authorization");
		}
	}
	
	public void startCrawl(){
		String startToken = Config.INSTANCE.startUserToken;
		String startUrl = String.format(Constants.USER_FOLLOWEES_URL, startToken,0);
		HttpGet request = new HttpGet(startToken);
		request.setHeader("authorization", "oauth" + authorization);
		detailListPageThreadPool.execute(new DetailListPageTask(request, Config.INSTANCE.isProxy));
	}

	public ThreadPoolExecutor getDetailListPageThreadPool() {
		return detailListPageThreadPool;
	}

	public static String getAuthorization() {
		return authorization;
	}

	public ThreadPoolExecutor getDetailPageThreadPool() {
		return detailPageThreadPool;
	}

	public ThreadPoolExecutor getListPageThreadPool() {
		return listPageThreadPool;
	}

	public void setDetailListPageThreadPool(ThreadPoolExecutor detailListPageThreadPool) {
		this.detailListPageThreadPool = detailListPageThreadPool;
	}
	
	
}
