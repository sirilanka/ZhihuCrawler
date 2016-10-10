package com.wang.core.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.apache.commons.logging.Log;

import com.wang.core.util.Config;
import com.wang.core.util.SimpleLogger;

public enum ConnectionManager {
	INSTANCE;
	private Log log = SimpleLogger.getLog(ConnectionManager.class);
	private volatile DataSource dataSource;
	
	private ConnectionManager(){
		init();
	}
	private void init(){
		Properties p = new Properties();
		p.setProperty("url", Config.INSTANCE.dbUrl);
		p.setProperty("username", Config.INSTANCE.dbUserName);
		p.setProperty("password", Config.INSTANCE.dbPassword);
		p.setProperty("driverClassName", Config.INSTANCE.dbDriverClassName);
		p.setProperty("initialSize", Config.INSTANCE.dbInitialSize);
		p.setProperty("maxActive", Config.INSTANCE.dbMaxActive);
		p.setProperty("maxIdle", Config.INSTANCE.dbMaxIdle);
		try {
			dataSource = BasicDataSourceFactory.createDataSource(p);
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	public  Connection getConnection(){
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
		} catch (SQLException e) {
			log.error(e);
		}
		return connection;
	}
	
	public void closeConn(Statement st, ResultSet rs, Connection conn){
		try {
			if(rs != null)rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(st != null)
				try {
					st.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
					try {
						conn.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
	}
}
