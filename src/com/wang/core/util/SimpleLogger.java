package com.wang.core.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * @author 王亚楼(1374178468@qq.com)
 * @since 2016.10
 */
public class SimpleLogger{

	public static Log getLog(Class<?> clzz){
		return LogFactory.getLog(clzz);
	}
}
