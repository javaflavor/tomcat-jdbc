package org.openshift.quickstarts.tomcat.service;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openshift.quickstarts.tomcat.dao.TestDataSource;
import org.openshift.quickstarts.tomcat.model.TomcatEntry;

public class TomcatServiceTest {
	
	TomcatService service;

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
		service = new TomcatService();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddEntry() {
		TomcatEntry entry = new TomcatEntry("summary", "descripiton");
		service.addEntry(entry);
		
		List<TomcatEntry> list = service.dao.list();
		assertThat(list.size(), is(1));		
	}

	@Test
	public void testGetAllEntries() {
		List<TomcatEntry> list = service.getAllEntries();
		assertThat(list, is(not(nullValue())));		
	}

}
