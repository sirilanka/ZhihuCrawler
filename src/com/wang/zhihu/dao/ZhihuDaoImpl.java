package com.wang.zhihu.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;

import com.wang.core.dao.ConnectionManager;
import com.wang.core.util.Config;
import com.wang.core.util.SimpleLogger;
import com.wang.zhihu.entity.User;
/**
 * @author 王亚楼(1374178468@qq.com)
 * @since 2016.10
 */
public class ZhihuDaoImpl implements IZhihhuDao{

	private static Log log = SimpleLogger.getLog(ZhihuDaoImpl.class);
	public static void DBTableInit(){
		ResultSet rs = null;
		Statement st = null;
		Connection conn = ConnectionManager.INSTANCE.getConnection();
		try {
			rs = conn.getMetaData().getTables(null, null, "url", null);
			st = conn.createStatement();
			if(!rs.next()){
				st.execute(Config.INSTANCE.createUrlTable);
				log.info("create url table succeed!");
				st.execute(Config.INSTANCE.createUrlIndex);
				log.info("create url index succeed!");
			}else{
				log.info("url talbe exists!");
			}
			rs.close();
			rs = conn.getMetaData().getTables(null, null, "user", null);
			if(!rs.next()){
				st.execute(Config.INSTANCE.createUserTable);
				log.info("create user table succeed!");
				st.execute(Config.INSTANCE.createUserIndex);
				log.info("create user index succeed!");
			}else{
				log.info("user talbe exist!");
			}
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			ConnectionManager.INSTANCE.closeConn(st, rs, conn);
		}
	}
	@Override
	public boolean isExistRecord(String sql) throws SQLException {
		return isExistRecord(ConnectionManager.INSTANCE.getConnection(),
				sql);
	}

	@Override
	public boolean isExistRecord(Connection conn, String sql) throws SQLException {
		PreparedStatement pst= conn.prepareStatement(sql);
		ResultSet rs = pst.executeQuery();
		int num = rs.next() ? rs.getInt("count(*)") : 0;
		ConnectionManager.INSTANCE.closeConn(pst, rs, conn);
		return num == 0 ? false : true;
	}

	@Override
	public boolean isExistUser(String userToken) {
		return isExistUser(ConnectionManager.INSTANCE.getConnection(),
				userToken);
	}

	@Override
	public boolean isExistUser(Connection conn, String userToken) {
		String sql = "select count(*) from user WHERE user_token='"
				+ userToken + "'";
		try {
			if(isExistRecord(conn, sql))return true;
		} catch (SQLException e) {
			log.error(e);
		}
		return false;
	}
	

	@Override
	public boolean insertUser(User user) {
		return insertUser(ConnectionManager.INSTANCE.getConnection(), user);
	}

	@Override
	public boolean insertUser(Connection conn, User user) {
		String column = "location, business, sex, employment, username, "
				+ "url, agrees, thanks, asks, answer, followers, followees, "
				+ "hashId, education, user_token";
		String values = "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";
		String sql = "INSERT INTO user (" + column + ") values (" + values + ")";
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, user.getLocation());
			pst.setString(2, user.getBusiness());
			pst.setString(3, user.getSex());
			pst.setString(4,user.getEmployment());
			pst.setString(5, user.getUsername());
			pst.setString(6, user.getUrl());
			pst.setInt(7, user.getAgrees());
			pst.setInt(8, user.getThanks());
			pst.setInt(9, user.getAsks());
			pst.setInt(10, user.getAnswers());
			pst.setInt(11, user.getPosts());
			pst.setInt(12, user.getFollowers());
			pst.setInt(13, user.getFollowees());
			pst.setString(14, user.getHashId());
			pst.setString(15, user.getEducation());
			pst.setString(16, user.getUserToken());
			pst.executeUpdate();
			return true;
		}catch (SQLException e) {
			log.error(e);
		}finally {
			if(pst != null)
				try {
					pst.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return false;
	}

	@Override
	public boolean insertUrl(Connection conn, String md5url) {
		String isContainSql = "select count(*) from url WHERE md5_url='" + md5url + "'";
		PreparedStatement pst = null;
		try {
			if(isExistRecord(conn, isContainSql)){
				log.info("this url already exists:" + md5url);
				return false;
			}
			String sql = "insert into url (md5_rul) values (?)";
			pst = conn.prepareStatement(sql);
			pst.setString(1, md5url);
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(pst != null)
				try {
					pst.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return true;
	}

}
