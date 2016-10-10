package com.wang.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;

import org.apache.commons.logging.Log;
import org.apache.http.Consts;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
/**
 * httpclient工具类
 *
 * @author 王亚楼(1374178468@qq.com)
 * @since 2016.10
 */
public enum HttpClientUtil {

	INSTANCE;
	
	private Log log = SimpleLogger.getLog(this.getClass());
	private CookieStore cookieStore = new BasicCookieStore();
	private CloseableHttpClient httpClient;
	private HttpHost proxy;
	private RequestConfig requestConfig;
	public static final String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36";
	
	private HttpClientUtil(){
		init();
	}
	private void init(){
		try {
			SSLContext sslContext = SSLContexts.custom()
					.loadTrustMaterial(KeyStore.getInstance(KeyStore.getDefaultType()), new TrustStrategy() {
						@Override
						public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
							return true;
						}
					}).build();
			sslContext.getSocketFactory();
			SSLConnectionSocketFactory sslFactroy = new SSLConnectionSocketFactory(sslContext);
			Registry<ConnectionSocketFactory> socketFactoryRegistry = 
					RegistryBuilder.<ConnectionSocketFactory>create().
					register("http", PlainConnectionSocketFactory.INSTANCE)
					.register("https", sslFactroy)
					.build();
			PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			SocketConfig socketConfig = SocketConfig
					.custom()
					.setSoTimeout(Constants.TIMEOUT)
					.setTcpNoDelay(true)
					.build();
			connManager.setDefaultSocketConfig(socketConfig);
			ConnectionConfig connectionConfig = ConnectionConfig
					.custom()
					.setMalformedInputAction(CodingErrorAction.IGNORE)
					.setUnmappableInputAction(CodingErrorAction.IGNORE)
					.setCharset(Consts.UTF_8)
					.build();
			connManager.setDefaultConnectionConfig(connectionConfig);
			connManager.setMaxTotal(500);
			connManager.setDefaultMaxPerRoute(300);
			HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
				
				@Override
				public boolean retryRequest(IOException exception, int excutionCount, HttpContext context) {
					if(excutionCount > 2) return false;
					if(exception instanceof InterruptedIOException) return true;
					if(exception instanceof ConnectTimeoutException)return true;
					if(exception instanceof UnknownHostException)return true;
					if(exception instanceof SSLException)return true;
					HttpRequest request = HttpClientContext.adapt(context).getRequest();
					if(!(request instanceof HttpEntityEnclosingRequest)) return true;
					return false;
				}
			};
			HttpClientBuilder httpClientBuilder = HttpClients.custom()
					.setConnectionManager(connManager)
					.setRetryHandler(retryHandler)
					.setDefaultCookieStore(new BasicCookieStore())
					.setUserAgent(userAgent);
			if(proxy != null)httpClientBuilder.setRoutePlanner(new DefaultProxyRoutePlanner(proxy));
			httpClient = httpClientBuilder.build();
			
			requestConfig = getRequestConfigBuilder().build();
			
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			// TODO Auto-generated catch block
			log.error("Exception happened, idiot!!!:", e);
		}
	}
	
	public String getWebPage(String url) throws IOException{
		HttpGet request = new HttpGet(url);
		return getWebPage(request,"utf-8");
	}
	
	public String getWebpage(HttpRequestBase request) throws IOException{
		return getWebPage(request, "utf-8");
	}
	
	public String getWebPage(HttpRequestBase request,String encoding) throws IOException{
		CloseableHttpResponse response = null;
		response = getResponse(request);
		log.info("status ---" + response.getStatusLine().getStatusCode());
		String content = EntityUtils.toString(response.getEntity(),encoding);
		request.releaseConnection();
		return content;
	}
	
	public CloseableHttpResponse getResponse(HttpRequestBase request) throws IOException, IOException{
		if(request.getConfig() == null){
			request.setConfig(requestConfig);
		}
		request.setHeader("User-Agent",
				Constants.userAgentArray[new Random().nextInt(Constants.userAgentArray.length)]);
		HttpClientContext httpClientContext = HttpClientContext.create();
		httpClientContext.setCookieStore(cookieStore);
		CloseableHttpResponse response = httpClient.execute(request,httpClientContext);
		return response;
	}
	
	
	public String postRequest(String postUrl,Map<String, String> param) throws IOException{
		HttpPost post = new HttpPost(postUrl);
		setHttpPostParams(post, param);
		return getWebPage(post, "utf-8");
	}
	
	public void setHttpPostParams(HttpPost request,Map<String, String> param){
		List<NameValuePair> formParams = new ArrayList<>(param.size());
		for(String key : param.keySet()){
			formParams.add(new BasicNameValuePair(key, param.get(key)));
		}
		request.setEntity(new UrlEncodedFormEntity(formParams,Consts.UTF_8));
	}
	
	public Builder getRequestConfigBuilder(){
		return RequestConfig
				.custom()
				.setSocketTimeout(Constants.TIMEOUT)
				.setConnectTimeout(Constants.TIMEOUT)
				.setConnectionRequestTimeout(Constants.TIMEOUT)
				.setCookieSpec(CookieSpecs.STANDARD);

	}
	
	public CloseableHttpResponse getResponse(String url) throws IOException{
		HttpGet request = new HttpGet(url);
		return getResponse(request);
	}
	
	public void serializeObject(Object object,String filePath){
		OutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(filePath,false);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(object);
			log.info("序列化成功！");
			oos.flush();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				oos.close();
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public Object deserializeObject(String path) throws IOException, ClassNotFoundException{
		File file = new File(path);
		InputStream is = null;
		ObjectInputStream ois = null;
		Object object = null;
		is = new FileInputStream(file);
		ois = new ObjectInputStream(is);
		object = ois.readObject();
		ois.close();
		is.close();
		return object;
	}
	
	public void downloadFile(String url,String path,
			String saveFileName,boolean isReplaceFile){
		try {
			CloseableHttpResponse response = getResponse(url);
			log.info("status:" + response.getStatusLine().getStatusCode());
			File file = new File(path);
			if(!file.exists() && !file.isDirectory()){
				file.mkdirs();
				log.info("路径不存在，创建路径");
			}else{
				log.info("目录已存在");
			}
			file = new File(file, saveFileName);
			if(!file.exists() || isReplaceFile){
				OutputStream os = new FileOutputStream(file);
				InputStream is = response.getEntity().getContent();
				byte[] buff = new byte[(int) response.getEntity().getContentLength()];
				while(true){
					int readed = is.read(buff);
					if(readed == -1)break;
					byte[] tmp = new byte[readed];
					System.arraycopy(buff, 0, tmp, 0, readed);
					os.write(tmp, readed, readed);
					log.info("序列化" + readed + "个字节");
				}
				is.close();
				os.close();
			}
		} catch (IllegalArgumentException e) {
			log.error(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public CookieStore getCookieStore(){
		return cookieStore;
	}
	
	public void setCookieStore(CookieStore cookieStore) {
		this.cookieStore = cookieStore;
	}
	
	public String decodeUnicode(String dataStr){
		int start = 0;
		int end = 0;
		final StringBuilder buffer = new StringBuilder(dataStr.length() / 6);
		while(true){
			start = dataStr.indexOf("\\u",start - (6 - 1));
			if(start == -1)break;
			start = start + 2;
			end = start + 4;
			String tmpStr = dataStr.substring(start,end);
			buffer.append(String.valueOf((char)Integer.parseInt(tmpStr,16)));
			start = end;
		}
		return buffer.toString();
	}
}
