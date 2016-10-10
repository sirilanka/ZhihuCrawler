package com.wang.zhihu.dao;

import java.sql.Connection;
import java.sql.SQLException;

import com.wang.zhihu.entity.User;
/**
 * @author 王亚楼(1374178468@qq.com)
 * @since 2016.10
 */
public interface IZhihhuDao {
	boolean isExistRecord(String sql) throws SQLException;
	boolean isExistRecord(Connection conn, String userToken) throws SQLException;
	boolean isExistUser(String userToken);
	boolean isExistUser(Connection conn, String userToken);
	boolean insertUser(User user);
	boolean insertUser(Connection conn, User user);
	boolean insertUrl(Connection conn, String md5url);
}
