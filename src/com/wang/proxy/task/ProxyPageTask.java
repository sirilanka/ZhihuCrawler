package com.wang.proxy.task;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import com.wang.core.util.Config;
import com.wang.core.util.Constants;
import com.wang.core.util.HttpClientUtil;
import com.wang.core.util.SimpleLogger;
import com.wang.proxy.ProxyHttpClient;
import com.wang.proxy.ProxyListPageParser;
import com.wang.proxy.ProxyPool;
import com.wang.proxy.entity.Direct;
import com.wang.proxy.entity.Proxy;
import com.wang.proxy.site.ProxyListPageParserFactory;
import com.wang.zhihu.ZhihuHttpClient;
import com.wang.zhihu.entity.Page;
/**
 * 抓取代理页面
 * 
 * @author 王亚楼(1374178468@qq.com)
 * @since 2016.10
 */
public class ProxyPageTask implements Runnable{
	private static Log log = SimpleLogger.getLog(ProxyPageTask.class);
	protected String url;
	private boolean proxyFlag;
	private Proxy currentProxy;
	
	protected static ProxyHttpClient proxyHttpClient = ProxyHttpClient.getInstance();
	
	public ProxyPageTask(String url, boolean proxyFlag){
		this.url = url;
		this.proxyFlag = proxyFlag;
	}

	@Override
	public void run() {
		long requestStartTime = System.currentTimeMillis();
		HttpGet request = null;
		Page page = null;
		try {
			if(proxyFlag){
			request = new HttpGet(url);
				currentProxy = ProxyPool.INSTANCE.getProxyQueue().take();
				if(!(currentProxy instanceof Direct)){
					HttpHost proxy = new HttpHost(currentProxy.getIp(), currentProxy.getPort());
					request.setConfig(HttpClientUtil.INSTANCE.getRequestConfigBuilder()
							.setProxy(proxy).build());
				}
				page = proxyHttpClient.getWebPage(request);
			}else{
				page = proxyHttpClient.getWebPage(url);
			}
			//page.setProxy(currentProxy);
			int status = page.getStatusCode();
			long requestEndTime = System.currentTimeMillis();
			String logStr = Thread.currentThread().getName() + "-" + currentProxy.getProxyStr()
					+ " executing request: " + page.getUrl()
					+ " response statusCode:" + status
					+ " request cost time:" + (requestEndTime - requestStartTime) + "ms";
			if(status == HttpStatus.SC_OK){
				log.debug(logStr);
				handle(page);
			}else{
				log.error(logStr);
				Thread.sleep(100);
				retry();
			}
		} catch (InterruptedException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		} finally {
			if(currentProxy != null){
				currentProxy.setTimeInterval(Constants.TIME_INTERVAL);
				ProxyPool.INSTANCE.getProxyQueue().add(currentProxy);
			}
			if(request != null)request.releaseConnection();
		}
	}
	
	private void retry(){
		proxyHttpClient.getProxyDownloadThreadExecutor().execute(new ProxyPageTask(url, Config.INSTANCE.isProxy));
	}
	
	public void handle(Page page){
		ProxyListPageParser parser = ProxyListPageParserFactory.
				getProxyListPageParser(ProxyPool.INSTANCE.getProxyMap().get(url));
		List<Proxy> proxyList = parser.parse(page.getHtml());
		for(Proxy proxy : proxyList){
			if(!ZhihuHttpClient.getInstance().getDetailListPageThreadPool().isTerminated()){
				if(!ProxyPool.INSTANCE.getProxySet().contains(proxy)){
					proxyHttpClient.getProxyTestThreadPoolExecutor().execute(new ProxyTestTask(proxy));
				}
			}
		}
	}
}
