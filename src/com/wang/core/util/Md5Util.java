package com.wang.core.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * @author 王亚楼(1374178468@qq.com)
 * @since 2016.10
 */
public class Md5Util {

	public static String convert2Md5(String password){
		MessageDigest md5 = null;
		StringBuilder sb = new StringBuilder(32);
		try {
			md5 = MessageDigest.getInstance("md5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		byte[] digested = md5.digest(password.getBytes());
		System.out.println(digested.length);
		int val;
		for(byte b : digested){
			val = ((int)b) & 0xff;
			if(val < 16)sb.append("0");
			sb.append(Integer.toHexString(val));
		}
		return sb.toString();
	}
}