package com.wang.proxy.util;

import com.wang.proxy.entity.Proxy;
/**
 * 失败次数大于三，计算一次失败率，如果失败率大于0.6，判定为可抛弃
 * 
 * @author 王亚楼(1374178468@qq.com)
 * @since 2016.10
 */
public class ProxyUtil {
	
	public static boolean isDiscardProxy(Proxy proxy){
		int succTimes = proxy.getSuccessfulTimes();
		int failTimes = proxy.getFailureTimes();
		if(failTimes >= 3){
			double failrate = (double)failTimes/(succTimes + failTimes);
			if(failrate > 0.6)return true;
		}
		return false;
	}
}
