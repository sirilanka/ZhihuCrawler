package com.wang.zhihu.task;

import java.util.List;

import org.apache.http.client.methods.HttpRequestBase;

import com.jayway.jsonpath.JsonPath;
import com.wang.core.util.Config;
import com.wang.core.util.Constants;
import com.wang.zhihu.entity.Page;

/**
 * 知乎用户关注列表页task
 * 下载成功解析出用户token，去重,构造用户详情url，获，添加到DetailPageDownloadThreadPool
 * 
 * @author 王亚楼(1374178468@qq.com)
 * @since 2016.10
 */
public class ListPageTask extends AbstractPageTask {

    public ListPageTask(HttpRequestBase request, boolean proxyFlag) {
        super(request, proxyFlag);
    }


    @Override
    protected void retry() {
    	zhiHuHttpClient.getListPageThreadPool().execute(new ListPageTask(request, Config.INSTANCE.isProxy));
    }

    @Override
    protected void handle(Page page) {
        /**
         * "我关注的人"列表页
         */
        List<String> urlTokenList = JsonPath.parse(page.getHtml()).read("$.data..url_token");
        for (String s : urlTokenList){
            if (s == null){
                continue;
            }
            handleUserToken(s);
        }
    }
    private void handleUserToken(String userToken){
        String url = Constants.INDEX_URL + "/people/" + userToken + "/following";
        if(!Config.INSTANCE.dbEnable){
            zhiHuHttpClient.getDetailPageThreadPool().execute(new DetailPageTask(url, Config.INSTANCE.isProxy));
            return ;
        }
//        boolean existUserFlag = ZhiHuDAO.isExistUser(userToken);
        boolean existUserFlag = zhihudao.isExistUser(userToken);
        while (zhiHuHttpClient.getDetailPageThreadPool().getQueue().size() > 1000){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(!existUserFlag || zhiHuHttpClient.getDetailPageThreadPool().getActiveCount() == 0){
            /**
             * 防止互相等待，导致死锁
             */
            zhiHuHttpClient.getDetailPageThreadPool().execute(new DetailPageTask(url, Config.INSTANCE.isProxy));

        }
    }
}
