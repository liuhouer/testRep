package com.sohu;

import com.sohu.bean.NewsBean;
import com.sohu.db.ConnectionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.beans.StringBean;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.HeadingTag;
import org.htmlparser.tags.Span;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 用于对搜狐网站上的新闻进行抓取
 * @author guanminglin <guanminglin@gmail.com>
 */
public class SohuNews {

    private Parser parser = null;   //用于分析网页的分析器。
    private List newsList = new ArrayList();    //暂存新闻的List；
    private NewsBean bean = new NewsBean();
    private ConnectionManager manager = null;    //数据库连接管理器。
    private PreparedStatement pstmt = null;

    public SohuNews() {
    }

    /**
     * 获得一条完整的新闻。
     * @param newsBean
     * @return
     */
    public List getNewsList(final NewsBean newsBean) {
        List list = new ArrayList();
        String newstitle = newsBean.getNewsTitle();
        String newsauthor = newsBean.getNewsAuthor();
        String newscontent = newsBean.getNewsContent();
        String newsdate = newsBean.getNewsDate();
        list.add(newstitle);
        list.add(newsauthor);
        list.add(newscontent);
        list.add(newsdate);
        return list;
    }

    /**
     *  设置新闻对象，让新闻对象里有新闻数据
     * @param newsTitle 新闻标题
     * @param newsauthor  新闻作者
     * @param newsContent 新闻内容
     * @param newsDate  新闻日期
     * @param url  新闻链接
     */
    public void setNews(String newsTitle, String newsauthor, String newsContent, String newsDate, String url) {
        bean.setNewsTitle(newsTitle);
        bean.setNewsAuthor(newsauthor);
        bean.setNewsContent(newsContent);
        bean.setNewsDate(newsDate);
        bean.setNewsURL(url);
    }

    /**
     * 该方法用于将新闻添加到数据库中。
     */
    protected void newsToDataBase() {

        //建立一个线程用来执行将新闻插入到数据库中。
        Thread thread = new Thread(new Runnable() {

            public void run() {
                boolean sucess = saveToDB(bean);
                if (sucess != false) {
                    System.out.println("插入数据失败");
                }
            }
        });
        thread.start();
    }

    /**
     * 将新闻插入到数据库中
     * @param bean
     * @return
     */
    public boolean saveToDB(NewsBean bean) {
        boolean flag = true;
        String sql = "insert into news(newstitle,newsauthor,newscontent,newsurl,newsdate) values(?,?,?,?,?)";
        manager = new ConnectionManager();
        String titleLength = bean.getNewsTitle();
        if (titleLength.length() > 60) {  //标题太长的新闻不要。
            return flag;
        }
        try {
            pstmt = manager.getConnection().prepareStatement(sql);
            pstmt.setString(1, bean.getNewsTitle());
            pstmt.setString(2, bean.getNewsAuthor());
            pstmt.setString(3, bean.getNewsContent());
            pstmt.setString(4, bean.getNewsURL());
            pstmt.setString(5, bean.getNewsDate());
            flag = pstmt.execute();

        } catch (SQLException ex) {
            Logger.getLogger(SohuNews.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                pstmt.close();
                manager.close();
            } catch (SQLException ex) {
                Logger.getLogger(SohuNews.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return flag;
    }

    /**
     * 获得新闻的标题
     * @param titleFilter
     * @param parser
     * @return
     */
    private String getTitle(NodeFilter titleFilter, Parser parser) {
        String titleName = "";
        try {

            NodeList titleNodeList = (NodeList) parser.parse(titleFilter);
            for (int i = 0; i < titleNodeList.size(); i++) {
                HeadingTag title = (HeadingTag) titleNodeList.elementAt(i);
                titleName = title.getStringText();
            }

        } catch (ParserException ex) {
            Logger.getLogger(SohuNews.class.getName()).log(Level.SEVERE, null, ex);
        }
        return titleName;
    }

    /**
     * 获得新闻的责任编辑，也就是作者。
     * @param newsauthorFilter
     * @param parser
     * @return
     */
    private String getNewsAuthor(NodeFilter newsauthorFilter, Parser parser) {
        String newsAuthor = "";
        try {
            NodeList authorList = (NodeList) parser.parse(newsauthorFilter);
            for (int i = 0; i < authorList.size(); i++) {
                Div authorSpan = (Div) authorList.elementAt(i);
                newsAuthor = authorSpan.getStringText();
            }

        } catch (ParserException ex) {
            Logger.getLogger(SohuNews.class.getName()).log(Level.SEVERE, null, ex);
        }
        return newsAuthor;

    }

    /*
     * 获得新闻的日期
     */
    private String getNewsDate(NodeFilter dateFilter, Parser parser) {
        String newsDate = null;
        try {
            NodeList dateList = (NodeList) parser.parse(dateFilter);
            for (int i = 0; i < dateList.size(); i++) {
                Span dateTag = (Span) dateList.elementAt(i);
                newsDate = dateTag.getStringText();
            }
        } catch (ParserException ex) {
            Logger.getLogger(SohuNews.class.getName()).log(Level.SEVERE, null, ex);
        }

        return newsDate;
    }

    /**
     * 获取新闻的内容
     * @param newsContentFilter
     * @param parser
     * @return  content 新闻内容
     */
    private String getNewsContent(NodeFilter newsContentFilter, Parser parser) {
        String content = null;
        StringBuilder builder = new StringBuilder();


        try {
            NodeList newsContentList = (NodeList) parser.parse(newsContentFilter);
            for (int i = 0; i < newsContentList.size(); i++) {
                Div newsContenTag = (Div) newsContentList.elementAt(i);
                builder = builder.append(newsContenTag.getStringText());
            }
            content = builder.toString();  //转换为String 类型。
            if (content != null) {
                parser.reset();
                parser = Parser.createParser(content, "gb2312");
                StringBean sb = new StringBean();
                sb.setCollapse(true);
                parser.visitAllNodesWith(sb);
                content = sb.getStrings();
//                String s = "\";} else{ document.getElementById('TurnAD444').innerHTML = \"\";} } showTurnAD444(intTurnAD444); }catch(e){}";
               
                content = content.replaceAll("\\\".*[a-z].*\\}", "");
             
                content = content.replace("[我来说两句]", "");


            } else {
               System.out.println("没有得到新闻内容！");
            }

        } catch (ParserException ex) {
            Logger.getLogger(SohuNews.class.getName()).log(Level.SEVERE, null, ex);
        }

        return content;
    }

    /**
     * 根据提供的URL，获取此URL对应网页所有的纯文本信息，次方法得到的信息不是很纯，
     *常常会得到我们不想要的数据。不过如果你只是想得到某个URL 里的所有纯文本信息，该方法还是很好用的。
     * @param url 提供的URL链接
     * @return RL对应网页的纯文本信息
     * @throws ParserException
     * @deprecated 该方法被 getNewsContent()替代。
     */
    @Deprecated
    public String getText(String url) throws ParserException {
        StringBean sb = new StringBean();

        //设置不需要得到页面所包含的链接信息
        sb.setLinks(false);
        //设置将不间断空格由正规空格所替代
        sb.setReplaceNonBreakingSpaces(true);
        //设置将一序列空格由一个单一空格所代替
        sb.setCollapse(true);
        //传入要解析的URL
        sb.setURL(url);

        //返回解析后的网页纯文本信息
        return sb.getStrings();
    }

    /**
     * 对新闻URL进行解析提取新闻，同时将新闻插入到数据库中。
     * @param url 新闻连接。
     */
    public void parser(String url) {
        try {
            parser = new Parser(url);
            NodeFilter titleFilter = new TagNameFilter("h1");
            NodeFilter contentFilter = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("id", "sohu_content"));
            NodeFilter newsdateFilter = new AndFilter(new TagNameFilter("span"), new HasAttributeFilter("class", "c"));
            NodeFilter newsauthorFilter = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "editUsr"));
            String newsTitle = getTitle(titleFilter, parser);
            parser.reset();   //记得每次用完parser后，要重置一次parser。要不然就得不到我们想要的内容了。
            String newsContent = getNewsContent(contentFilter, parser);
            System.out.println(newsContent);   //输出新闻的内容，查看是否符合要求
            parser.reset();
            String newsDate = getNewsDate(newsdateFilter, parser);
            parser.reset();
            String newsauthor = getNewsAuthor(newsauthorFilter, parser);

            //先设置新闻对象，让新闻对象里有新闻内容。
            setNews(newsTitle, newsauthor, newsContent, newsDate, url);
            //将新闻添加到数据中。
            this.newsToDataBase();
            
        } catch (ParserException ex) {
            Logger.getLogger(SohuNews.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //单个文件测试网页
    public static void main(String[] args) {
        SohuNews news = new SohuNews();
        news.parser("http://news.sohu.com/20090518/n264012864.shtml");   
    }
}
