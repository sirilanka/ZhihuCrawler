package com.wang.zhihu.parser;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wang.core.parser.ListPageParser;
import com.wang.zhihu.entity.Page;
/**
 * 关注用户页面解析器
 * 
 * 登录模式 “我关注的人”列表页面解析器
 * 2016-12-28起 不在使用
 * 
 * @author 王亚楼(1374178468@qq.com)
 * @since 2016.10
 */
@Deprecated
public class ZhihuUserFollowingListPageParser implements ListPageParser{

	private static ZhihuUserFollowingListPageParser zhiHuUserFollowingListPageParser;
    public static ZhihuUserFollowingListPageParser getInstance(){
        if(zhiHuUserFollowingListPageParser == null){
            zhiHuUserFollowingListPageParser = new ZhihuUserFollowingListPageParser();
        }
        return zhiHuUserFollowingListPageParser;
    }
    @Override
    public List<String> parseListPage(Page page) {
        List<String> list = new ArrayList<String>(20);
        Document doc = Jsoup.parse(page.getHtml());
        Elements es = doc.select(".zm-list-content-medium .zm-list-content-title a");
        String u = null;
        for(Element temp:es){
            u = (String) (temp.attr("href") + "/following");
            list.add(u);
        }
        return list;
    }

}
