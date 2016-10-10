package com.wang.core.client;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;

import com.wang.core.util.HttpClientUtil;
import com.wang.core.util.SimpleLogger;
import com.wang.zhihu.entity.Page;

public class AbstractHttpClient {
	private static final Log log = SimpleLogger.getLog(AbstractHttpClient.class);	
	
	public  InputStream getWebPageInputStream(String url){
		try {
			CloseableHttpResponse response = HttpClientUtil.INSTANCE.getResponse(url);
			return response.getEntity().getContent();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Page getWebPage(String url) throws IOException{
		return getWebPage(url,"utf-8");
	}
	public Page getWebPage(String url, String encoding) throws IOException{
		Page page = new Page();
		CloseableHttpResponse response = HttpClientUtil.INSTANCE.getResponse(url);
		page.setStatusCode(response.getStatusLine().getStatusCode());
		page.setUrl(url);
		try {
			if(page.getStatusCode() == 200){
				page.setHtml(EntityUtils.toString(response.getEntity()));
			}
		} catch (IOException e) {
			log.error(e);
		}finally {
			try {
				response.close();
				
			} catch (IOException e2) {
				log.error(e2);
			}
		}
		return page;
	}
	
	public Page getWebPage(HttpRequestBase request) throws IOException{
		Page page = new Page();
		CloseableHttpResponse response = HttpClientUtil.INSTANCE.getResponse(request);
		page.setHtml(EntityUtils.toString(response.getEntity()));
		page.setStatusCode(response.getStatusLine().getStatusCode());
		page.setUrl(request.getURI().toString());
		return page;
	}
	public boolean deserializeCookieStore(String path) throws ClassNotFoundException, IOException{
		CookieStore cookieStore = (CookieStore) HttpClientUtil.INSTANCE.deserializeObject(path);
		HttpClientUtil.INSTANCE.setCookieStore(cookieStore);
		return cookieStore == null ? false : true;
	}
	
}
