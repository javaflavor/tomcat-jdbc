package org.openshift.quickstarts.tomcat.dao;

import static org.hamcrest.Matchers.*;

import static org.junit.Assert.*;

import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openshift.quickstarts.tomcat.model.TomcatEntry;

public class JdbcTomcatDAOTest {
	
	JdbcTomcatDAO dao;

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
		if (dao != null) {
			dao.getConnection().close();
		}
	}
	
	@Test
	public void testNew() {
		dao = new JdbcTomcatDAO();
	}

	@Test(expected=RuntimeException.class)
	public void testNewFailed() {
		String orig = JdbcTomcatDAO.DB_JNDI;
		JdbcTomcatDAO.DB_JNDI = "UNKNOWN";
		try {
			dao = new JdbcTomcatDAO();
		} finally {
			JdbcTomcatDAO.DB_JNDI = orig;
		}
	}

	@Test
	public void testSave() {
		dao = new JdbcTomcatDAO();
		try {
			dao.getConnection().createStatement().execute("delete from todo_entries");
		} catch (Exception e) {}
		
		TomcatEntry entry = new TomcatEntry("summary", "descripiton");
		dao.save(entry);
		
		List<TomcatEntry> list = dao.list();
		assertThat(list.size(), is(1));
	}

	@Test(expected=RuntimeException.class)
	public void testSaveFailed() throws Exception {
		dao = new JdbcTomcatDAO();
		dao.getConnection().createStatement().execute("drop table todo_entries");
		TomcatEntry entry = new TomcatEntry("summary", "descripiton");
		dao.save(entry);
	}

	@Test
	public void testList() {
		dao = new JdbcTomcatDAO();
		List<TomcatEntry> list = dao.list();
		assertThat(list, is(not(nullValue())));
	}

	@Test(expected=RuntimeException.class)
	public void testListFailed() throws Exception {
		dao = new JdbcTomcatDAO();
		dao.getConnection().createStatement().execute("drop table todo_entries");
		dao.list();
	}

}
