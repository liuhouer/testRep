package com.sohu.crawler;

import com.sohu.SohuNews;
import java.util.HashSet;
import java.util.Set;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 *  这个类是用来搜集新闻链接地址的。将符合正则表达式的URL添加到URL数组中。
 * @author Administrator
 */
public class LinkParser {
    // 获取一个网站上的链接,filter 用来过滤链接

    public static Set<String> extracLinks(String url, LinkFilter filter) {

        Set<String> links = new HashSet<String>();
        try {
            Parser parser = new Parser(url);
            parser.setEncoding("gb2312");
            // 过滤 <frame >标签的 filter，用来提取 frame 标签里的 src 属性所表示的链接
            NodeFilter frameFilter = new NodeFilter() {

                public boolean accept(Node node) {
                    if (node.getText().startsWith("frame src=")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            // OrFilter 来设置过滤 <a> 标签，和 <frame> 标签
            OrFilter linkFilter = new OrFilter(new NodeClassFilter(
                    LinkTag.class), frameFilter);
            // 得到所有经过过滤的标签
            NodeList list = parser.extractAllNodesThatMatch(linkFilter);
            for (int i = 0; i < list.size(); i++) {
                Node tag = list.elementAt(i);
                if (tag instanceof LinkTag)// <a> 标签
                {
                    LinkTag link = (LinkTag) tag;
                    String linkUrl = link.getLink();// url
                    if (filter.accept(linkUrl)) {
                        links.add(linkUrl);
                    }
                } else// <frame> 标签
                {
                    // 提取 frame 里 src 属性的链接如 <frame src="test.html"/>
                    String frame = tag.getText();
                    int start = frame.indexOf("src=");
                    frame = frame.substring(start);
                    int end = frame.indexOf(" ");
                    if (end == -1) {
                        end = frame.indexOf(">");
                    }
                    String frameUrl = frame.substring(5, end - 1);
                    if (filter.accept(frameUrl)) {
                        links.add(frameUrl);
                    }
                }
            }
        } catch (ParserException e) {
            e.printStackTrace();
        }
        return links;
    }

    public void doParser(String url) {
        SohuNews news = new SohuNews();
        Set<String> links = LinkParser.extracLinks(
                url, new LinkFilter() {
            //提取以 http://www.twt.edu.cn 开头的链接

            public boolean accept(String url) {
                if (url.matches("http://news.sohu.com/[\\d]+/n[\\d]+.shtml")) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        //循环迭代出连接，然后提取该连接中的新闻。
        for (String link : links) {
            System.out.println(link);
            news.parser(link);
            
        }
    }

    //测试主页新闻，可以得到主页上所有符合要求的网页地址，并进行访问。
    public static void main(String[] args) {
        String url = "http://news.sohu.com/";
        LinkParser parser = new LinkParser();
        parser.doParser(url);

    }
}

