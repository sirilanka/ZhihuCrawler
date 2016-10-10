package com.wang.proxy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.wang.core.util.Constants;
import com.wang.proxy.entity.Direct;
import com.wang.proxy.entity.Proxy;
import com.wang.proxy.site.ip66.Ip66ProxyListPageParser;
import com.wang.proxy.site.mimiip.MimiipProxyListPageParser;
import com.wang.proxy.site.xici.XiciProxyPageListParser;

public enum ProxyPool {
	INSTANCE;
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Set<Proxy> proxySet = new HashSet<>();
	
	private final DelayQueue<Proxy> proxyQueue = new DelayQueue<>();
	private final Map<String , Class<?>> proxyMap = new HashMap<>();
	
	private ProxyPool(){
		init();
	}
	
	private void init(){
		int pages = 8;
		for(int i = 1;i <= pages;i++){
			proxyMap.put("http://www.xicidaili.com/wt/" + i + ".html", XiciProxyPageListParser.class);
            proxyMap.put("http://www.xicidaili.com/nn/" + i + ".html", XiciProxyPageListParser.class);
            proxyMap.put("http://www.xicidaili.com/wn/" + i + ".html", XiciProxyPageListParser.class);
            proxyMap.put("http://www.xicidaili.com/nt/" + i + ".html", XiciProxyPageListParser.class);
            proxyMap.put("http://www.ip181.com/daili/" + i + ".html", XiciProxyPageListParser.class);
            proxyMap.put("http://www.mimiip.com/gngao/" + i, MimiipProxyListPageParser.class);//高匿
            proxyMap.put("http://www.mimiip.com/gnpu/" + i, MimiipProxyListPageParser.class);//普匿
            proxyMap.put("http://www.66ip.cn/" + i + ".html", Ip66ProxyListPageParser.class);
            for(int j = 1;j < 34;j++){
                proxyMap.put("http://www.66ip.cn/areaindex_" + j + "/" + i + ".html", Ip66ProxyListPageParser.class);
            }
		}
		proxyQueue.add(new Direct(Constants.TIME_INTERVAL));
	}

	public Set<Proxy> getProxySet() {
		return proxySet;
	}

	public DelayQueue<Proxy> getProxyQueue() {
		return proxyQueue;
	}

	public Map<String, Class<?>> getProxyMap() {
		return proxyMap;
	}

	public ReentrantReadWriteLock getLock() {
		return lock;
	}
	
	
}
