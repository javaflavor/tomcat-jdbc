package org.openshift.quickstarts.tomcat.model;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.Serializable;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TomcatEntryTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		TomcatEntry entry = new TomcatEntry();
		entry.setId("001");
		assertThat(entry.getId(), is((Serializable)"001"));
		entry.setSummary("summary");
		assertThat(entry.getSummary(), is("summary"));
		entry.setDescription("description");
		assertThat(entry.getDescription(), is("description"));
	}

}
