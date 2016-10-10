package com.wang.proxy.entity;

import java.io.Serializable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
/**
 * 代理类
 * 
 * @author 王亚楼(1374178468@qq.com)
 * @since 2016.10
 */
public class Proxy implements Delayed, Serializable{
	private static final long serialVersionUID = 1L;
	private long timeInterval;
	private String ip;
	private int port;
	private boolean availableFlag;
	private boolean anonymousFlag;
	private long lastSuccessfulTime;
	private long successfulTotalTime;
	private int failureTimes;
	private int successfulTimes;
	private double successfulAverageTime;
	public Proxy(String ip, int port, long timeIntervalMills) {
		this.ip = ip;
		this.port = port;
		this.timeInterval = TimeUnit.NANOSECONDS.
				convert(timeIntervalMills, TimeUnit.MILLISECONDS) + System.nanoTime();
	}
	public long getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(long timeIntervalMills) {
		this.timeInterval = TimeUnit.NANOSECONDS.
				convert(timeIntervalMills, TimeUnit.MILLISECONDS) 
				+ System.nanoTime();
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isAvailableFlag() {
		return availableFlag;
	}

	public void setAvailableFlag(boolean availableFlag) {
		this.availableFlag = availableFlag;
	}

	public boolean isAnonymousFlag() {
		return anonymousFlag;
	}

	public void setAnonymousFlag(boolean anonymousFlag) {
		this.anonymousFlag = anonymousFlag;
	}

	public long getLastSuccessfulTime() {
		return lastSuccessfulTime;
	}

	public void setLastSuccessfulTime(long lastSuccessfulTime) {
		this.lastSuccessfulTime = lastSuccessfulTime;
	}

	public long getSuccessfulTotalTime() {
		return successfulTotalTime;
	}

	public void setSuccessfulTotalTime(long successfulTotalTime) {
		this.successfulTotalTime = successfulTotalTime;
	}

	public int getFailureTimes() {
		return failureTimes;
	}

	public void setFailureTimes(int failureTimes) {
		this.failureTimes = failureTimes;
	}

	public int getSuccessfulTimes() {
		return successfulTimes;
	}

	public void setSuccessfulTimes(int successfulTimes) {
		this.successfulTimes = successfulTimes;
	}

	public double getSuccessfulAverageTime() {
		return successfulAverageTime;
	}

	public void setSuccessfulAverageTime(double successfulAverageTime) {
		this.successfulAverageTime = successfulAverageTime;
	}


	@Override
	public int compareTo(Delayed o) {
		Proxy element = (Proxy)o;
		if(successfulAverageTime == 0d || element.successfulAverageTime == 0d){
			return 0;
		}
		return successfulAverageTime > element.successfulAverageTime ? 1
				: (successfulAverageTime < element.successfulAverageTime ? -1 : 0);
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(timeInterval - System.nanoTime(), TimeUnit.NANOSECONDS);
	}
	@Override
	public String toString() {
		return "Proxy {timeInterval=" + timeInterval + ", ip=" + ip + ", port=" + port + ", availableFlag="
				+ availableFlag + ", anonymousFlag=" + anonymousFlag + ", lastSuccessfulTime=" + lastSuccessfulTime
				+ ", successfulTotalTime=" + successfulTotalTime + ", failureTimes=" + failureTimes
				+ ", successfulTime=" + successfulTimes + ", successfulAverageTime=" + successfulAverageTime + "}";
	}

	@Override
	public boolean equals(Object o) {
		if(this == o)return true;
		if(o == null || getClass() != o.getClass())return false;
		
		Proxy proxy = (Proxy)o;
		if(port != proxy.port)return false;
		return ip.equals(proxy.ip);
	}
	@Override
	public int hashCode() {
		int res = ip.hashCode();
		res = 31 * res + port;
		return res;
	}
	
	public String getProxyStr(){
		return ip + ":" + port;
	}
	
}
