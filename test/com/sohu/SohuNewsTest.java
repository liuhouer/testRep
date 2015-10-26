/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sohu;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Administrator
 */
public class SohuNewsTest {

    public SohuNewsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

   
    /**
     * Test of newsToDataBase method, of class SohuNews.
     */
    @Test
    public void testNewsToDataBase() {
        System.out.println("newsToDataBase");
        SohuNews instance = new SohuNews();
        instance.newsToDataBase();
       }

   
    /**
     * Test of parser method, of class SohuNews.
     */
    @Test
    public void testParser() {
        System.out.println("parser");
        String url = "http://news.sohu.com/20090518/n264012864.shtml";
        SohuNews instance = new SohuNews();
        instance.parser(url);
        
    }
}