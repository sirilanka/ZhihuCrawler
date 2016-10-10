package com.wang.proxy.task;

import org.apache.commons.logging.Log;

import com.wang.core.util.Config;
import com.wang.core.util.HttpClientUtil;
import com.wang.core.util.SimpleLogger;
import com.wang.proxy.ProxyPool;
import com.wang.proxy.entity.Proxy;
import com.wang.proxy.util.ProxyUtil;
import com.wang.zhihu.ZhihuHttpClient;
/**
 * 序列化代理对象
 * 
 * @author 王亚楼(1374178468@qq.com)
 * @since 2016.10
 */
public class ProxySerializeTask implements Runnable{

	private static final Log log = SimpleLogger.getLog(ProxySerializeTask.class);
	
	@Override
	public void run() {
		while(!ZhihuHttpClient.isStop){
			Proxy[] proxies = null;
			int i = 0;
			try {
				Thread.sleep(1000 * 60);
				ProxyPool.INSTANCE.getLock().readLock().lock();
				proxies = new Proxy[ProxyPool.INSTANCE.getProxySet().size()];
				for(Proxy p : ProxyPool.INSTANCE.getProxySet()){
					if(!ProxyUtil.isDiscardProxy(p)){
						proxies[i++] = p;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}finally {
				ProxyPool.INSTANCE.getLock().readLock().unlock();
			}
			
			if(proxies != null){
				HttpClientUtil.INSTANCE.serializeObject(proxies, Config.INSTANCE.proxyPath);
				log.info("成功序列化" + proxies.length + "个代理");
			}
		}
	}

}
