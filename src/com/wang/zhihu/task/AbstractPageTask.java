package com.wang.zhihu.task;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import com.wang.core.util.Constants;
import com.wang.core.util.HttpClientUtil;
import com.wang.core.util.SimpleInvocationHandler;
import com.wang.core.util.SimpleLogger;
import com.wang.proxy.ProxyPool;
import com.wang.proxy.entity.Direct;
import com.wang.proxy.entity.Proxy;
import com.wang.proxy.util.ProxyUtil;
import com.wang.zhihu.ZhihuHttpClient;
import com.wang.zhihu.dao.IZhihhuDao;
import com.wang.zhihu.dao.ZhihuDaoImpl;
import com.wang.zhihu.entity.Page;
/**
 * @author 王亚楼(1374178468@qq.com)
 * @since 2016.10
 */
public abstract class AbstractPageTask implements Runnable{

	private static final Log log = SimpleLogger.getLog(AbstractPageTask.class);
	protected String url;
	protected HttpRequestBase request;
	protected boolean proxyFlag;
	private Proxy currProxy;
	protected static IZhihhuDao zhihudao;
	protected static ZhihuHttpClient zhiHuHttpClient;
	
	
	/**
     * 详情页下载线程池
     */
    private ThreadPoolExecutor detailPageThreadPool;
    /**
     * 列表页下载线程池
     */
    private ThreadPoolExecutor listPageThreadPool;
    /**
     * 详情列表页下载线程池
     */
    
    
    private ThreadPoolExecutor detailListPageThreadPool;
	static{
		zhihudao = getZhihuDao();
	}
	
	public AbstractPageTask(String url, boolean proxyFlag){
		this.url = url;
		this.proxyFlag = proxyFlag;
	}
	
	public AbstractPageTask(HttpRequestBase request, boolean proxyFlag){
		this.request = request;
		this.proxyFlag = proxyFlag;
	}
	
	private static IZhihhuDao getZhihuDao() {
		IZhihhuDao zhihhuDao = new ZhihuDaoImpl();
		InvocationHandler invocationHandler = new SimpleInvocationHandler(zhihudao);
		IZhihhuDao proxyZhihuDao = (IZhihhuDao)java.lang.reflect.Proxy.newProxyInstance(zhihudao.getClass().getClassLoader(), 
				zhihhuDao.getClass().getInterfaces(), invocationHandler);
		return proxyZhihuDao;
	}
	@Override
	public void run() {
		long startTime = 0l;
		HttpGet tmprequest = null;
		Page page = null;
		try {
			if(url != null){
				if(proxyFlag){
					tmprequest = new HttpGet(url);
						currProxy = ProxyPool.INSTANCE.getProxyQueue().take();
					if(!(currProxy instanceof Direct)){
						HttpHost proxy = new HttpHost(currProxy.getIp(), currProxy.getPort());
						tmprequest.setConfig(HttpClientUtil.INSTANCE.getRequestConfigBuilder().setProxy(proxy).build());
					}
					startTime = System.currentTimeMillis();
					page = zhiHuHttpClient.getWebPage(tmprequest);
				}else {
					startTime = System.currentTimeMillis();
					page = zhiHuHttpClient.getWebPage(url);
				}
			}else if(request != null){
				if(proxyFlag){
					currProxy = ProxyPool.INSTANCE.getProxyQueue().take();
					if(!(currProxy instanceof Direct)){
						HttpHost proxy = new HttpHost(currProxy.getIp(), currProxy.getPort());
						request.setConfig(HttpClientUtil
								.INSTANCE.getRequestConfigBuilder().setProxy(proxy).build());
					}
					startTime = System.currentTimeMillis();
					page = zhiHuHttpClient.getWebPage(request);
				}else {
					startTime = System.currentTimeMillis();
					page = zhiHuHttpClient.getWebPage(request);
				}
			}else return ;
			
			long endTime = System.currentTimeMillis();
			page.setProxy(currProxy);
			int status = page.getStatusCode();
			String logStr = Thread.currentThread().getName() + "-"
					+ " executing request: " + page.getUrl()
					+ " response statusCode: " + page.getStatusCode()
					+ " request cost time: " + (endTime - startTime) + "ms";
			if(status == HttpStatus.SC_OK){
				if(page.getHtml().contains("zhihu")){
					log.debug(logStr);
					currProxy.setSuccessfulTimes(currProxy.getSuccessfulTimes() + 1);
					currProxy.setSuccessfulTotalTime(currProxy.getSuccessfulTotalTime() 
							+ (endTime - startTime));
					double aTime = (currProxy.getSuccessfulTotalTime() + 0.0) / currProxy.getSuccessfulTimes();
					currProxy.setSuccessfulAverageTime(aTime);
					currProxy.setLastSuccessfulTime(System.currentTimeMillis());
					handle(page);
				}else {
					log.warn("proxy exception:" + currProxy.toString());
				}
			}else if(status == HttpStatus.SC_NOT_FOUND || status == HttpStatus.SC_UNAUTHORIZED
					|| status == HttpStatus.SC_GONE){
				log.warn(logStr);
			}else{
				log.error(logStr);;
				Thread.sleep(1000);
				retry();
			}
		} catch (InterruptedException e) {
			log.error(e);
		} catch (IOException e) {
			if(currProxy != null){
				currProxy.setFailureTimes(currProxy.getFailureTimes() + 1);
			}
			if(!zhiHuHttpClient.getDetailListPageThreadPool().isShutdown()){
				retry();
			}
		}finally {
			if(request != null)request.releaseConnection();
			if(tmprequest != null)tmprequest.releaseConnection();
			if (currProxy != null && !ProxyUtil.isDiscardProxy(currProxy)) {
				currProxy.setTimeInterval(Constants.TIME_INTERVAL);
				ProxyPool.INSTANCE.getProxyQueue().add(currProxy);
			}
		}
	}
	
	protected abstract void handle(Page page);

	protected abstract void retry();

	public ThreadPoolExecutor getDetailPageThreadPool() {
		return detailPageThreadPool;
	}

	public ThreadPoolExecutor getListPageThreadPool() {
		return listPageThreadPool;
	}

	public ThreadPoolExecutor getDetailListPageThreadPool() {
		return detailListPageThreadPool;
	}

}
