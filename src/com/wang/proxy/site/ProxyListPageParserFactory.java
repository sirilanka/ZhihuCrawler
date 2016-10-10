package com.wang.proxy.site;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wang.proxy.ProxyListPageParser;

public class ProxyListPageParserFactory {

	private static Map<String, ProxyListPageParser> map = new ConcurrentHashMap<>();
	
	public static ProxyListPageParser getProxyListPageParser(Class clzz){
		String parseName = clzz.getSimpleName();
		ProxyListPageParser proxyListPageParser = null;
		if(map.containsKey(parseName)){
			return map.get(parseName);
		}else{
			try {
				proxyListPageParser = (ProxyListPageParser) clzz.newInstance();
				parseName = clzz.getSimpleName();
				map.put(parseName, proxyListPageParser);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return proxyListPageParser;
	}
}
