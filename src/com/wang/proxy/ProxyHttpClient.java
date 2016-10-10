package com.wang.proxy;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;

import com.wang.core.client.AbstractHttpClient;
import com.wang.core.util.Config;
import com.wang.core.util.Constants;
import com.wang.core.util.HttpClientUtil;
import com.wang.core.util.SimpleLogger;
import com.wang.core.util.SimpleThreadPoolExecutor;
import com.wang.core.util.ThreadPoolMonitor;
import com.wang.proxy.entity.Proxy;
import com.wang.proxy.task.ProxyPageTask;
import com.wang.proxy.task.ProxySerializeTask;
import com.wang.zhihu.entity.Page;

public class ProxyHttpClient extends AbstractHttpClient{
	private final Log log = SimpleLogger.getLog(ProxyHttpClient.class);
	public static Set<Page> downloadFailureProxyPageSet = new HashSet<>(ProxyPool.INSTANCE.getProxyMap().size());
	
	private static volatile ProxyHttpClient instance;
	
	public static ProxyHttpClient getInstance(){
		ProxyHttpClient proxyHttpClient = instance;
		if(proxyHttpClient == null){
			synchronized(ProxyHttpClient.class){
				proxyHttpClient = instance;
				if(proxyHttpClient == null){
					proxyHttpClient = instance = new ProxyHttpClient();
				}
			}
		}
		return proxyHttpClient;
	}
	
	private ThreadPoolExecutor proxyTestThreadPoolExecutor;
	private ThreadPoolExecutor proxyDownloadThreadExecutor;
	
	public ProxyHttpClient(){
		initThreadPool();
		initProxy();
	}
	
	private void initThreadPool(){
		proxyTestThreadPoolExecutor = new SimpleThreadPoolExecutor(100, 100, 
				0l, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(10000),
				new ThreadPoolExecutor.DiscardPolicy(),
				"proxyTestThreadExecutor");
		proxyDownloadThreadExecutor = new SimpleThreadPoolExecutor(10, 10, 
				0l, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(), 
				"proxyDownloadThreadExecutor");
		new Thread(new ThreadPoolMonitor(proxyTestThreadPoolExecutor, "proxyTestthreadPool")).start();
		new Thread(new ThreadPoolMonitor(proxyDownloadThreadExecutor, "proxyDownloadThreadExecutor")).start();
	}
	
	private void initProxy(){
		try {
			Proxy[] proxyArray = (Proxy[])HttpClientUtil.INSTANCE.deserializeObject(Config.INSTANCE.proxyPath);
			int usableProxyCount = 0;
			for(Proxy p : proxyArray){
				if(p == null){
					continue;
				}
				p.setTimeInterval(Constants.TIME_INTERVAL);
				p.setFailureTimes(0);
				p.setSuccessfulTimes(0);
				long curr = System.currentTimeMillis();
				if(curr - p.getLastSuccessfulTime() < 1000 * 60 * 60){
					ProxyPool.INSTANCE.getProxyQueue().add(p);
					ProxyPool.INSTANCE.getProxySet().add(p);
					usableProxyCount++;
				}
			}
			log.info("反序列化成功，" + proxyArray.length + "个代理，可用代理" + usableProxyCount + "个");
		} catch (ClassNotFoundException | IOException e) {
			log.warn("反序列化失败");
		}
		
	}
	
	public void startCrawl(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					for(String url : ProxyPool.INSTANCE.getProxyMap().keySet()){
						proxyDownloadThreadExecutor.execute(new ProxyPageTask(url, false));
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					try {
						Thread.sleep(1000 * 60 * 60);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		new Thread(new ProxySerializeTask()).start();
	}

	public ThreadPoolExecutor getProxyTestThreadPoolExecutor() {
		return proxyTestThreadPoolExecutor;
	}

	public ThreadPoolExecutor getProxyDownloadThreadExecutor() {
		return proxyDownloadThreadExecutor;
	}
	
	
}
