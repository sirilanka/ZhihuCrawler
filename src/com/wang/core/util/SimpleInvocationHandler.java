package com.wang.core.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
/**
 * @author 王亚楼(1374178468@qq.com)
 * @since 2016.10
 */
public class SimpleInvocationHandler implements InvocationHandler{

	private static Log log = SimpleLogger.getLog(HttpClientUtil.class);
	
	private Object target;

	public SimpleInvocationHandler() {
		super();
	}
	
	
	public SimpleInvocationHandler(Object target) {
		super();
		this.target = target;
	}


	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		long startTime = System.currentTimeMillis();
		Object res = method.invoke(target, args);
		long endTime = System.currentTimeMillis();
		log.debug(target.getClass().getSimpleName() + " " + method.getName()
			+ " cost time: " + (endTime - startTime) + " ms");
		return res;
	}
}
