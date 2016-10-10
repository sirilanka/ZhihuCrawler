package com.wang.core.parser;

import com.wang.zhihu.entity.Page;
import com.wang.zhihu.entity.User;

public interface DetailPageParser {
	User parseDetailPage(Page page);
}
