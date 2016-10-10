package com.wang.core.client;

public interface IClient {
	void initHttpClient();
	void startCraw(String url);
	void startCraw();
}
