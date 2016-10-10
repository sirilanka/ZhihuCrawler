package com.wang.zhihu.task;

import static com.wang.zhihu.ZhihuHttpClient.parseUserCount;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.apache.commons.logging.Log;
import org.apache.http.client.methods.HttpGet;

import com.wang.core.parser.DetailPageParser;
import com.wang.core.util.Config;
import com.wang.core.util.SimpleInvocationHandler;
import com.wang.core.util.SimpleLogger;
import com.wang.zhihu.ZhihuHttpClient;
import com.wang.zhihu.entity.Page;
import com.wang.zhihu.entity.User;
import com.wang.zhihu.parser.ZhihuNewUserDetailPageParser;;

/**
 * 知乎用户详情页task
 * 下载成功解析出用户信息并添加到数据库，获取该用户的关注用户list url，添加到ListPageDownloadThreadPool
 * 
 * @author 王亚楼(1374178468@qq.com)
 * @since 2016.10
 */
public class DetailPageTask extends AbstractPageTask{
	 private static Log logger = SimpleLogger.getLog(DetailPageTask.class);
	    private static DetailPageParser proxyDetailPageParser;
	    static {
	        proxyDetailPageParser = getProxyDetailParser();
	    }

	    public DetailPageTask(String url, boolean proxyFlag) {
	        super(url, proxyFlag);
	    }

	    @Override
	    protected void retry() {
	        zhiHuHttpClient.getDetailPageThreadPool().execute(new DetailPageTask(url, Config.INSTANCE.isProxy));
	    }

	    @Override
	    protected void handle(Page page) {
	        DetailPageParser parser = null;
//	        parser = ZhiHuNewUserDetailPageParser.getInstance();
	        parser = proxyDetailPageParser;
	        User u = parser.parseDetailPage(page);
	        logger.info("解析用户成功:" + u.toString());
	        if(Config.INSTANCE.dbEnable){
//	            ZhiHuDAO.insertUser(u);
	            zhihudao.insertUser(u);
	        }
	        parseUserCount.incrementAndGet();
	        for(int i = 0;i < u.getFollowees() / 20 + 1;i++) {
	            String userFolloweesUrl = formatUserFolloweesUrl(u.getUserToken(), 20 * i);
	            handleUrl(userFolloweesUrl);
	        }
	    }
	    public String formatUserFolloweesUrl(String userToken, int offset){
	        String url = "https://www.zhihu.com/api/v4/members/" + userToken + "/followees?include=data%5B*%5D.answer_count%2Carticles_count%2Cfollower_count%2C" +
	                "is_followed%2Cis_following%2Cbadge%5B%3F(type%3Dbest_answerer)%5D.topics&offset=" + offset + "&limit=20";
	        return url;
	    }
	    private void handleUrl(String url){
	        HttpGet request = new HttpGet(url);
	        request.setHeader("authorization", "oauth " + ZhihuHttpClient.getAuthorization());
	        if(!Config.INSTANCE.dbEnable){
	            zhiHuHttpClient.getListPageThreadPool().execute(new ListPageTask(request, Config.INSTANCE.isProxy));
	            return ;
	        }
	        zhiHuHttpClient.getListPageThreadPool().execute(new ListPageTask(request, Config.INSTANCE.isProxy));
	    }

	    /**
	     * 代理类
	     * @return
	     */
	    private static DetailPageParser getProxyDetailParser(){
	        DetailPageParser detailPageParser = ZhihuNewUserDetailPageParser.getInstance();
	        InvocationHandler invocationHandler = new SimpleInvocationHandler(detailPageParser);
	        DetailPageParser proxyDetailPageParser = (DetailPageParser) Proxy.newProxyInstance(detailPageParser.getClass().getClassLoader(),
	                detailPageParser.getClass().getInterfaces(), invocationHandler);
	        return proxyDetailPageParser;
	    }
}
