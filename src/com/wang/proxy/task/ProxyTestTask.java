package com.wang.proxy.task;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;

import com.wang.core.util.Constants;
import com.wang.core.util.HttpClientUtil;
import com.wang.core.util.SimpleLogger;
import com.wang.proxy.ProxyPool;
import com.wang.proxy.entity.Proxy;
import com.wang.zhihu.ZhihuHttpClient;
import com.wang.zhihu.entity.Page;
/**
 * 测试代理可用性
 * 
 * @author 王亚楼(1374178468@qq.com)
 * @since 2016.10
 */
public class ProxyTestTask implements Runnable{

	private static final Log log = SimpleLogger.getLog(ProxyTestTask.class);

	private Proxy proxy;
	public ProxyTestTask(Proxy p) {
		this.proxy = p;
	}
	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		HttpGet request = new HttpGet(Constants.INDEX_URL);
		RequestConfig requestConfig = HttpClientUtil.INSTANCE.getRequestConfigBuilder()
				.setProxy(new HttpHost(proxy.getIp(), proxy.getPort()))
				.setCookieSpec(CookieSpecs.STANDARD)
				.build();
		request.setConfig(requestConfig);
		try {
			Page page = ZhihuHttpClient.getInstance().getWebPage(request);
			long endTime = System.currentTimeMillis();
			String logStr = Thread.currentThread().getName() + "-"
					+ " executing request " + page.getUrl()
					+ " response statusCode " + page.getStatusCode()
					+ " request cost time  " + (endTime - startTime) + "ms";
			if(page == null || page.getStatusCode() != 200){
				log.warn(logStr);
				return ;
			}
			request.releaseConnection();
			
			log.debug(proxy.toString() + "-------" + page.toString());
			if(!ProxyPool.INSTANCE.getProxySet().contains(proxy)){
				log.debug(proxy.toString() + "----代理可用----请求耗时" + (endTime - startTime) + "ms" );
				ProxyPool.INSTANCE.getLock().writeLock().lock();
				try {
					ProxyPool.INSTANCE.getProxySet().add(proxy);
				} finally {
					ProxyPool.INSTANCE.getLock().writeLock().unlock();
				}
				ProxyPool.INSTANCE.getProxyQueue().add(proxy);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(request != null){
				request.releaseConnection();
			}
		}
	}
}
