<%-- 
    Document   : index
    Created on : 2009-5-18, 14:53:18
    Author     : Administrator
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>新闻抓取</title>
    </head>
    <body>
        <form action="GetNewsServlet" method="post" id="newsform">
        <div align="center">
        新闻首页地址： <input name="newsfield" id="newsfield" type="text" value="" style="border-width:inherit">
        <input type="submit" id="newsSubmit" value="提交">
        </div>
        </form>
        </body>
</html>
