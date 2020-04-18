package commonUtilities;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class DriverPage {
	WebDriver driver;
	String	rootPath;

	public DriverPage(WebDriver driver) {
	
	this.driver = driver ;
	rootPath = System.getProperty("user.dir");
	
	}

	public WebDriver launchDriver(String browser) {

		String Webdrivertype = browser;
		WebDriver driver = null;

		if (Webdrivertype.equalsIgnoreCase("chrome")) {
			System.setProperty("webdriver.chrome.driver",
					"C:\\Users\\Madan\\git\\SampleMavenProject\\SampleMavenProject\\chromedriver.exe");
			driver = new ChromeDriver();
			driver.manage().window().maximize();

		} else if (Webdrivertype.equalsIgnoreCase("firefox")) {
			System.setProperty("webdriver.gecko.driver", rootPath + "//drivers//geckodriver.exe");
			driver = new FirefoxDriver();
		}

		return driver;
	}

	public void CloseBrowser( ) {
		driver.close();
	}
}
