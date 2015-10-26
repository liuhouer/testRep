package com.sohu.crawler;

import com.sohu.SohuNews;
import java.util.Set;

/**
 *
 * @author guanminglin <guanminglin@gmail.com>
 */
public class Crawler {

    SohuNews news = new SohuNews();

    /* 使用种子 url 初始化 URL 队列*/
    private void initCrawlerWithSeeds(String[] seeds) {
        for (int i = 0; i < seeds.length; i++) {
            LinkDB.addUnvisitedUrl(seeds[i]);
        }
    }

    /* 爬取方法*/
    public void crawling(String[] seeds) {
        LinkFilter filter = new LinkFilter() {
            //提取以 http://www.twt.edu.cn 开头的链接

            public boolean accept(String url) {
                if (url.matches("http://news.sohu.com/[\\d]+/n[\\d]+.shtml")) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        //初始化 URL 队列
        initCrawlerWithSeeds(seeds);
        //循环条件：待抓取的链接不空且抓取的网页不多于 1000
        while (!LinkDB.unVisitedUrlsEmpty() && LinkDB.getVisitedUrlNum() <= 10) {
            
           
            //队头 URL 出对
            String visitUrl = LinkDB.unVisitedUrlDeQueue();
            if (visitUrl == null) {
                
                continue;
            }
           //该 url 放入到已访问的 URL 中
            LinkDB.addVisitedUrl(visitUrl);
            //提取出下载网页中的 URL
            Set<String> links = LinkParser.extracLinks(visitUrl, filter);
            //新的未访问的 URL 入队
            for (String link : links) {
                LinkDB.addUnvisitedUrl(link);
                System.out.println(link);
                 news.parser(link);
            }
        }
    }

    //main 方法入口，更加base url 进行分析
    public static void main(String[] args) {
        Crawler crawler = new Crawler();
        crawler.crawling(new String[]{"http://news.sohu.com"});
    }
}

