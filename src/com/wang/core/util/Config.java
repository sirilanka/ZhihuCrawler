package com.wang.core.util;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.http.Consts;
/**
 * 爬虫配置
 *
 * @author 王亚楼(1374178468@qq.com)
 * @since 2016.10
 */
public enum Config {
	INSTANCE;
	
	private Log log = SimpleLogger.getLog(this.getClass());
	public boolean dbEnable;
	
	public boolean isProxy;
	
	public int downloadThreadSize;
	
	public String verificationCodepath;
	
	public String emailOrPhoneNum;
	
	public String password;
	
	public String startURL;
	
	public String startUserToken;
	
	public int downloadPageCount;
	
	//db configurations
	public String dbDriverClassName;
	
	public String dbUserName;
	
	public String dbPassword;
	
	public String dbUrl;
	
	public String dbInitialSize;
	
	public String dbMaxActive;
	
	public String dbMaxIdle;
	
	public String createUrlTable;
	
	public String createUserTable;
	
	public String createUrlIndex;
	
	public String createUserIndex;
	
	public String cookiePath;
	
	public String proxyPath;
	
	private Config(){
		init();
	}
	
	private void init(){
		Properties p = new Properties();
		try {
			p.load(ClassLoader.getSystemClassLoader().getResourceAsStream("config.properties"));
		} catch (IOException e) {
			log.error("配置文件未找到",e);
		}
		dbEnable = Boolean.parseBoolean(p.getProperty("db.enable"));
		verificationCodepath = p.getProperty("verificationCodePath");
		emailOrPhoneNum = p.getProperty("emailOrPhoneNum");
		password = p.getProperty("password");
		startURL = p.getProperty("startURL");
		startUserToken = p.getProperty("startUserToken");
		downloadPageCount = Integer.parseInt(p.getProperty("downloadPageCount"));
		downloadThreadSize = Integer.parseInt(p.getProperty("downloadThreadSize"));
		cookiePath = p.getProperty("cookiePath");
		proxyPath = p.getProperty("proxypath");
		isProxy = Boolean.parseBoolean(p.getProperty("isProxy"));
		if(dbEnable){
			dbUserName = p.getProperty("db.userName");
			dbPassword = p.getProperty("db.password");
			dbUrl = p.getProperty("db.url");
			dbDriverClassName = p.getProperty("db.DriverClassName");
			dbInitialSize = p.getProperty("db.initialSize");
			dbMaxActive = p.getProperty("db.maxActive");
			dbMaxIdle = p.getProperty("db.maxIdle");
			createUrlTable = p.getProperty("createUrlTable");
			createUserTable = p.getProperty("createUserTable");
			createUrlIndex = p.getProperty("createUrlIndex");
			createUserIndex = p.getProperty("createUserIndex");
		}
	}
}
