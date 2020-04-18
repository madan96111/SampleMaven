package com.test;

import static org.testng.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.Pages.HomePage;
import com.Pages.LoginPage;
import com.Pages.UrlLandingPage;

import commonUtilities.Assertions;
import commonUtilities.CommonFuntionDemo;
import commonUtilities.DriverPage;
import commonUtilities.LaunchApplication;
import commonUtilities.Reporting;

public class SampleTestngClass {

	WebDriver driver;
	DriverPage asapdriver = new DriverPage(driver);
	HashMap<String, String> Environment = new HashMap<String, String>();
	HashMap<String, String> Dictionary = new HashMap<String, String>();
	CommonFuntionDemo commonfunc;
	String runonenv;
	Reporting Report;
	final String runonbrowser = "chrome";
	LoginPage LoginPage;
	HomePage HomePage;
	Assertions Assertionsobj;
	UrlLandingPage UrlLandingPage;
	
	@Parameters("browser")
	@BeforeClass
	public void BeforeClass(@Optional(runonbrowser) String browser) throws Exception {

		String String1[] = this.getClass().getName().split("\\.");
		String ClassName = String1[String1.length - 1];

		Environment.put("ClassName", ClassName);
		commonfunc = new CommonFuntionDemo(driver, Environment, Dictionary, Report);
		runonenv = commonfunc.loadPropertyFile("Environment");
		System.out.println("Execution will be run on " + runonenv );
		Environment.put("ENV_CODE", runonenv);
		commonfunc.fetchEnvironmentDetails();
		Report = new Reporting(driver, Environment, Dictionary);
		Assert.assertTrue(commonfunc.createExecutionFolders(browser));
		Report.fnCreateSummaryReport();
		Report.createConsolidatedBrowserReport(browser);
		
	}

	@Parameters("browser")
	@BeforeMethod
	public void beforMethod(@Optional(runonbrowser) String browser, Method Method)

	{
		
		driver = asapdriver.launchDriver(browser);
		Report.driver = driver;
		String MethodName =Method.getName();
		Report.fnCreateHtmlReport(MethodName);
		commonfunc.fGetDataForTest(MethodName);
		Assertionsobj = new Assertions(Report);
		asapdriver = new DriverPage(driver);
		UrlLandingPage = new UrlLandingPage(driver, Dictionary, Environment, Report);
	
		
	}

	@Test
	@Parameters("browser")
	public void demoTest1(@Optional(runonbrowser) String browser) {
		
		LaunchApplication LaunchApplication = new LaunchApplication(driver, Dictionary, Environment, Report); 
		LaunchApplication.LaunchDemosite();
		LoginPage LoginPage = new LoginPage(driver, Dictionary, Dictionary, Report);
		UrlLandingPage.clickOnAccount();
		LoginPage.enterCredential(Dictionary.get("Username"), Dictionary.get("Password"));
		HomePage = new HomePage(driver, Environment, Dictionary, Report);
		Assertionsobj.assertTrue(HomePage.verifyHomePageTitle(), "HomaPage title is present");

	}

	@Parameters("browser")
	@AfterMethod
	
	public void afterMethod(@Optional(runonbrowser) String browser,
			ITestResult result, Method method) {

		String testName = method.getName();
		Report.fnCloseHtmlReport(testName);
		asapdriver.CloseBrowser();
	
	}

	@AfterClass
	@Parameters("browser")
	//@Parameters("browser")
	public void AfterClass(@Optional(runonbrowser) String browser) {
		System.out.println("after class");
		Report.fnCloseTestSummary();
		Report.writeConsolidatedBrowserSummary(browser);
	}

}
