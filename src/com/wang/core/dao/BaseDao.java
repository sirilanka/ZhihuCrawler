package com.wang.core.dao;

import com.wang.zhihu.entity.User;

public interface BaseDao {
	void  insert(User user);
	void deleteByUserId(String userId);
	void isConstains(String userId);
}
