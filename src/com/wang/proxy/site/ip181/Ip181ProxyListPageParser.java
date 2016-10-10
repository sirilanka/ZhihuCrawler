package com.wang.proxy.site.ip181;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wang.core.util.Constants;
import com.wang.proxy.ProxyListPageParser;
import com.wang.proxy.entity.Proxy;

public class Ip181ProxyListPageParser implements ProxyListPageParser{

	@Override
	public List<Proxy> parse(String content) {
		Document document = Jsoup.parse(content);
		Elements elements = document.select("table tr:gt(0)");
		List<Proxy> proxyList = new ArrayList<>(elements.size());
		for(Element element : elements){
			String ip = element.select("td:eq(0)").first().text();
			String port = element.select("td:eq(1)").first().text();
			String isAnonymous = element.select("td:eq(2)").first().text();
			if(!anonymousFlag || isAnonymous.contains("匿")){
				proxyList.add(new Proxy(ip, Integer.parseInt(port), Constants.TIME_INTERVAL));
			}
		}
		return proxyList;
	}

}
