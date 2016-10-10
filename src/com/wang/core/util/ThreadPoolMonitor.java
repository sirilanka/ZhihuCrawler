package com.wang.core.util;

import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
/**
 * @author 王亚楼(1374178468@qq.com)
 * @since 2016.10
 */
public class ThreadPoolMonitor implements Runnable{

	private static Log log = SimpleLogger.getLog(ThreadPoolMonitor.class);
	private ThreadPoolExecutor executor;
	private static volatile boolean isStopMonitor = false;
	private String name = "";
	
	public ThreadPoolMonitor(ThreadPoolExecutor executor,String name) {
		this.executor = executor;
		this.name = name;
	}
	@Override
	public void run() {
		while(!isStopMonitor){
			log.debug(name + 
					String.format("[monitor] [%d/%d] Active: %d, "
							+ "completed:%d, "
							+ "queueSize: %d, Task: %d, "
							+ "isShutDown: %s, isTerminated: %s",
							this.executor.getPoolSize(),
							this.executor.getCorePoolSize(),
							this.executor.getActiveCount(),
							this.executor.getCompletedTaskCount(),
							this.executor.getQueue().size(),
							this.executor.getTaskCount(),
							this.executor.isShutdown(),
							this.executor.isTerminated()));
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
