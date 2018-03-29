package org.openshift.quickstarts.tomcat.servlet;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openshift.quickstarts.tomcat.dao.TestDataSource;

public class MainServletTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TestDataSource.bind();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		TestDataSource.unbind();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDoGetHttpServletRequestHttpServletResponse() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletContext servletContext = mock(ServletContext.class);
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getResourceAsStream("/WEB-INF/index.html")).thenReturn(this.getClass().getResourceAsStream("/index.html"));

        StringWriter swr = new StringWriter();
        PrintWriter wr = new PrintWriter(swr);
        when(response.getWriter()).thenReturn(wr);

        new MainServlet().doGet(request, response);

        wr.flush();
        
        String html = swr.toString();
        
//      System.out.println("### html = "+html);
        assertThat(html, is(containsString("<h2>New Tomcat DB entry</h2>")));
	}

	@Test
	public void testDoPostHttpServletRequestHttpServletResponse() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletContext servletContext = mock(ServletContext.class);
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getResourceAsStream("/WEB-INF/index.html")).thenReturn(this.getClass().getResourceAsStream("/index.html"));

        StringWriter swr = new StringWriter();
        PrintWriter wr = new PrintWriter(swr);
        when(response.getWriter()).thenReturn(wr);

        when(request.getParameter("summary")).thenReturn("aaaa");
        when(request.getParameter("description")).thenReturn("AAAA");
        new MainServlet().doPost(request, response);
        
        verify(response, times(1)).sendRedirect("index.html");

        swr = new StringWriter();
        wr = new PrintWriter(swr);
        when(response.getWriter()).thenReturn(wr);
        new MainServlet().doGet(request, response);
        wr.flush();
        
        String html = swr.toString();
        
//      System.out.println("### html = "+html);
        assertThat(html, is(containsString("aaaa")));
        assertThat(html, is(containsString("AAAA")));
	}

}
