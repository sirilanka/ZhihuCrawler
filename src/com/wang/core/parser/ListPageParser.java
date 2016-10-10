package com.wang.core.parser;

import java.util.List;

import com.wang.zhihu.entity.Page;

public interface ListPageParser {
	List<?> parseListPage(Page page);
}
