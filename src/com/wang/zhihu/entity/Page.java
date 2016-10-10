package com.wang.zhihu.entity;

import com.wang.proxy.entity.Proxy;
/**
 * 页面数据
 * 
 * @author 王亚楼(1374178468@qq.com)
 * @since 2016.10
 */
public class Page {
	private String url;
	private int statusCode;
	private String html;
	private Proxy proxy;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
	public Proxy getProxy() {
		return proxy;
	}
	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o)return true;
		if(o == null || getClass() != o.getClass())return false;
		Page page = (Page)o;
		return url.equals(page.url);
	}
	
	@Override
	public int hashCode() {
		return url.hashCode();
	}
	
}
