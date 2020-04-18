package com.Pages;

import java.util.HashMap;

import org.openqa.selenium.WebDriver;

import commonUtilities.CommonFuntionDemo;
import commonUtilities.DriverPage;
import commonUtilities.Reporting;

public class UrlLandingPage {

	CommonFuntionDemo commonfunc;
	WebDriver driver;
	DriverPage asapdriver = new DriverPage(driver);
	HashMap<String, String> Environment = new HashMap<String, String>();
	HashMap<String, String> Dictionary = new HashMap<String, String>();
	Reporting Report;

	public String AccountButton = "xpath:=(//a[@id='dropdownCurrency'])[2]:= Account button";
	public String LoginButton = "xpath:=(//a[contains(text(),'Login')])[1]:= Login button";
	public String Accountdropdown = "xpath:=(//a[@id='dropdownCurrency'])[2]:= Accountdropdown verification";
	
	public UrlLandingPage(WebDriver GDriver,
			HashMap<String, String> GDictionary,
			HashMap<String, String> GEnvironment, Reporting GReporter) {
		Report = GReporter;
		driver = GDriver;
		Dictionary = GDictionary;
		Environment = GEnvironment;
		commonfunc = new CommonFuntionDemo(driver, Environment, Dictionary, Report);
		
		
	}

	public  LoginPage clickOnAccount() {

		commonfunc.waitForObjectToBeClickable(10, AccountButton);
		openAccountDropdown();
		commonfunc.fGuiClick(LoginButton);
		return new LoginPage(driver, Dictionary, Environment, Report);
		

	}

	public void openAccountDropdown() {

		System.out.println("Open Acconts dropdown");
		if (!isaccountdropdownopen()) {

			commonfunc.fGuiClick(AccountButton);
			System.out.println("Account dropdown opened");

		}

	}

	public boolean isaccountdropdownopen() {
		System.out.println("Verifying if Action dropdown is open");
		boolean check = commonfunc.fGuiGetAttributeVal(Accountdropdown, "aria-expanded").contains("true");
		return check;

	}
}
