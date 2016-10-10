package com.wang.proxy.entity;
/**
 * @author 王亚楼(1374178468@qq.com)
 * @since 2016.10
 */
public class Direct extends Proxy{

	public Direct(String ip, int port, long timeIntervalMills) {
		super(ip, port, timeIntervalMills);
	}
	
	public Direct(long delayTime){
		this("", 0, delayTime);
	}

}
