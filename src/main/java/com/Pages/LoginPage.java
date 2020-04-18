package com.Pages;

import java.util.HashMap;

import org.openqa.selenium.WebDriver;


import commonUtilities.CommonFuntionDemo;
import commonUtilities.DriverPage;
import commonUtilities.Reporting;

public class LoginPage {
	
	

	CommonFuntionDemo commonfunc;

	WebDriver driver;
	DriverPage asapdriver = new DriverPage(driver);
	HashMap<String, String> Environment = new HashMap<String, String>();
	HashMap<String, String> Dictionary = new HashMap<String, String>();
	Reporting Report;
	
	public LoginPage(WebDriver GDriver,
			HashMap<String, String> GDictionary,
			HashMap<String, String> GEnvironment, Reporting GReporter) {
		Report = GReporter;
		driver = GDriver;
		Dictionary = GDictionary;
		Environment = GEnvironment;
		commonfunc = new CommonFuntionDemo(driver, Environment, Dictionary, Report);
		
		
	}

	public String usernameElement = "xpath:=//*[@name='username']:=username";

	public String passwordElement = "xpath:=//*[@name='password']:=password";
	public String Login = "xpath:=//button[contains(text(),'Login')]:=Login";
	

	public HomePage enterCredential(String Username, String Password) {

		
		setUserName(Username);
		passWord(Password);
		clickLogin();
		return new HomePage(driver, Environment, Dictionary, Report);
	}

	public void passWord(String password) {
		commonfunc.fGuiIsDisplayed(passwordElement);
		commonfunc.fGuiEnterText(passwordElement, password);

	}

	public void setUserName(String username) {
		
		commonfunc.fGuiIsDisplayed(usernameElement);
		commonfunc.fGuiEnterText(usernameElement, username);

	}
	
	public void clickLogin()
    {
		commonfunc.fGuiClick(Login);
    }
	
	

}