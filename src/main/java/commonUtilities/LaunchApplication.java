package commonUtilities;

import java.util.HashMap;

import org.openqa.selenium.WebDriver;



public class LaunchApplication {

	
	private WebDriver driver;
	private HashMap<String, String> Dictionary;
	private HashMap<String, String> Environment;
	Reporting Report = new Reporting(driver, Environment, Dictionary);
	
	public LaunchApplication(WebDriver GDriver,
			HashMap<String, String> GDictionary,
			HashMap<String, String> GEnvironment, Reporting GReporter) {
		Report = GReporter;
		driver = GDriver;
		Dictionary = GDictionary;
		Environment = GEnvironment;
	}
	
	public void LaunchDemosite() {
		
		driver.get(Environment.get("TEST_URL"));
		Report.fnWriteToHtmlOutput("Navigate to specified URL", "URL: "
				+ Environment.get("TEST_URL"),
				"Navigated to URL: " + Environment.get("TEST_URL"), "Done");
		
	}
	
}
