package com.wang.zhihu.parser;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.wang.core.parser.ListPageParser;
import com.wang.core.util.SimpleLogger;
import com.wang.zhihu.entity.Page;
import com.wang.zhihu.entity.User;
/**
 * 用户详情列表页
 * 
 * @author 王亚楼(1374178468@qq.com)
 * @since 2016.10
 */
public class ZhihuUserListPageParser implements ListPageParser{

	private static final Log log = SimpleLogger.getLog(ZhihuUserListPageParser.class);
	private static volatile ZhihuUserListPageParser instance;
	
	public static ZhihuUserListPageParser getInstance(){
		ZhihuUserListPageParser curr = instance;
		if(curr == null){
			synchronized (ZhihuUserListPageParser.class) {
				curr = instance;
				if(curr == null){
					curr = instance = new ZhihuUserListPageParser();
				}
			}
		}
		return curr;
	}
	
	@Override
	public List<?> parseListPage(Page page) {
		List<User> userList = new LinkedList<>();
		String baseJsonPath = "$.data.length()";
		DocumentContext documentContext = JsonPath.parse(page.getHtml());
		Integer userCount = documentContext.read(baseJsonPath);
		for(int i = 0;i < userCount;i++){
			User user = new User();
			String userBaseJsonpath = "$.data[" + i + "]";
			setUserInfoByJsonPth(user, "userToken", documentContext, userBaseJsonpath);
			setUserInfoByJsonPth(user, "username", documentContext, userBaseJsonpath);
			setUserInfoByJsonPth(user, "hashId", documentContext, userBaseJsonpath);
			setUserInfoByJsonPth(user, "followees", documentContext, userBaseJsonpath);
			setUserInfoByJsonPth(user, "location", documentContext, userBaseJsonpath);
			setUserInfoByJsonPth(user, "business", documentContext, userBaseJsonpath);
			setUserInfoByJsonPth(user, "employment", documentContext, userBaseJsonpath);
			setUserInfoByJsonPth(user, "position", documentContext, userBaseJsonpath);
			setUserInfoByJsonPth(user, "education", documentContext, userBaseJsonpath);
			setUserInfoByJsonPth(user, "answers", documentContext, userBaseJsonpath);
			setUserInfoByJsonPth(user, "asks", documentContext, userBaseJsonpath);
			setUserInfoByJsonPth(user, "posts", documentContext, userBaseJsonpath);
			setUserInfoByJsonPth(user, "followers", documentContext, userBaseJsonpath);
			setUserInfoByJsonPth(user, "agrees", documentContext, userBaseJsonpath);
			setUserInfoByJsonPth(user, "thanks", documentContext, userBaseJsonpath);
			try{
				Integer gender = documentContext.read(userBaseJsonpath + ".gender");
				if(gender != null)user.setSex(gender == 1 ? "male" : "female");
				userList.add(user);
			} catch(PathNotFoundException e){
				log.error(e);
			}
		}
		return userList;
	}
	
	private void setUserInfoByJsonPth(User user, 
			String fileName, DocumentContext dc,String jsonPath){
		try {
			Object o = dc.read(jsonPath);
			Field field = user.getClass().getDeclaredField(fileName);
			field.setAccessible(true);
			field.set(user, o);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
}
