package com.sohu.servlet;

import com.sohu.SohuNews;
import com.sohu.crawler.LinkParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Administrator
 */
public class GetNewsServlet extends HttpServlet {

    SohuNews news = new SohuNews();
    Thread thread = null;
    List newsList = new ArrayList();

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        final LinkParser parser = new LinkParser();
        final String url = request.getParameter("newsfield");  
       
        thread = new Thread(new Runnable() {

            public void run() {
                parser.doParser(url);
            }
        });
        thread.start();
        request.getSession().setAttribute("newsList", newsList);
        response.sendRedirect("detail.jsp");


    }
//    /**
//     * 得到一条完整的新闻。
//     * @param bean
//     * @return
//     */
//    protected synchronized List listNews(NewsBean bean) {
//        List list = new ArrayList();
//        list = news.getNewsList(bean);
//        return list;
//
//    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet 方法。单击左侧的 + 号以编辑代码。">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
