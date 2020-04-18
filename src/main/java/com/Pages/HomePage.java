package com.Pages;

import java.util.HashMap;

import org.openqa.selenium.WebDriver;



import commonUtilities.CommonFuntionDemo;
import commonUtilities.Reporting;

public class HomePage {
	
	private WebDriver driver;
	private HashMap<String, String> Dictionary;
	private HashMap<String, String> Environment;
	Reporting Report = new Reporting(driver, Environment, Dictionary);
	CommonFuntionDemo commonfunc;
	
	
	public String HomePageTitle ="xpath:=(//a/img)[1]:=HomePage Title";
	
	public HomePage(WebDriver Gdriver,HashMap<String, String> GEnvironment, HashMap<String, String> GDictionary,Reporting GReport) {
  
		Report = GReport;
		driver = Gdriver ;
		Dictionary = GDictionary;
		Environment = GEnvironment;
		commonfunc = new CommonFuntionDemo(driver, Environment, Dictionary, Report);
}
	
	public boolean verifyHomePageTitle() {
		
		return commonfunc.fGuiCheckObjectExistence(HomePageTitle);
	}
}
