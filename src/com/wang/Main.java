package com.wang;

import org.apache.commons.logging.Log;

import com.wang.core.util.SimpleLogger;
import com.wang.proxy.ProxyHttpClient;
import com.wang.zhihu.ZhihuHttpClient;
/**
 * 爬虫入口
 *
 * @author 王亚楼(1374178468@qq.com)
 * @since 2016.10
 */
public class Main {

	public static Log log = SimpleLogger.getLog(Main.class);
	public static void main(String[] args){
		ProxyHttpClient.getInstance().startCrawl();
		ZhihuHttpClient.getInstance().startCrawl();
	}
}
