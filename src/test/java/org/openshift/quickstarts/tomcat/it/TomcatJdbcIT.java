package org.openshift.quickstarts.tomcat.it;

import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Configuration.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.*;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TomcatJdbcIT {
	static String targetHost = System.getProperty("target.host");
	static String targetPort = System.getProperty("target.port");
	static String targetBaseUrl = "http://"+targetHost+":"+targetPort+"/";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.printf("### target.host = %s%n", targetHost);
		System.out.printf("### target.port = %s%n", targetPort);

	    timeout = 10000;
	    baseUrl = targetBaseUrl;
	    startMaximized = false;

	    // Wait for application loaded.
	    TimeUnit.SECONDS.sleep(5);

		open(targetBaseUrl);
		// Wait for page loaded.
		$("h1").waitUntil(appears, 10000);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		closeWebDriver();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testShow() {
		open("index.html");

		// 入力フォームが表示されていること。
		$("#summary").shouldBe(visible);
		$("#description").shouldBe(visible);

		// フォーム入力してサブミット。
		$("#summary").val("Test Summary");
		$("#description").val("Test Description");
		$("button[type = 'submit']").click();

		// 正常に表示。
		$$(byXpath("//h3[text()='Test Summary']")).shouldHave(sizeGreaterThan(0));
		$$(byXpath("//div[normalize-space()='Test Description']")).shouldHave(sizeGreaterThan(0));
	}

}
