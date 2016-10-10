package com.wang.proxy;

import java.util.List;

import com.wang.proxy.entity.Proxy;

public interface ProxyListPageParser {

	static final boolean anonymousFlag = true;
	List<Proxy> parse(String content);
}
