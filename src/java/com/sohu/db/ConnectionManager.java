package com.sohu.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class ConnectionManager {

    private Connection con = null;
   
    private boolean autoCommit = true;

    public ConnectionManager() {
       
    }

    /**
     * 获得数据库连接
     */
    public Connection getConnection(){
         try {

            String url = "jdbc:mysql://localhost:3306/sohuNews";
            String usr = "root";
            String pwd = "root";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(url, usr, pwd);
            con.setAutoCommit(autoCommit);
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
         return  con;
    }
    /**
     *关闭数据库连接
     */
    public void close() {
        if (con != null) {
            try {
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                con = null;
            }
        }
    }
    public static void main(String[] args) {
        ConnectionManager conn = new ConnectionManager();
       Connection c = conn.getConnection();
       System.out.println(c.toString());
    }
}
