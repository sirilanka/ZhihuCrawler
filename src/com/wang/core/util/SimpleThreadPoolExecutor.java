package com.wang.core.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * @author 王亚楼(1374178468@qq.com)
 * @since 2016.10
 */
public class SimpleThreadPoolExecutor extends ThreadPoolExecutor{
	
	private String poolName;

	public SimpleThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue,String poolName) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		this.poolName = poolName;
	}

	public SimpleThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler,String poolName) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
		this.poolName = poolName;
	}

	public SimpleThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler,String poolName) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
		this.poolName = poolName;
	}

	public SimpleThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,String poolName) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
		this.poolName = poolName;
	}
	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		if(t.getName().startsWith("pool-")){
			t.setName(t.getName().replaceAll("pool-\\d", this.poolName));
		}
	}
	
}
