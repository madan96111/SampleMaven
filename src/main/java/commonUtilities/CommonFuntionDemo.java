package commonUtilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import junit.framework.AssertionFailedError;

public class CommonFuntionDemo {

	WebDriver driver;
	HashMap<String, String> Environment = new HashMap<String, String>();
	HashMap<String, String> Dictionary = new HashMap<String, String>();
	HashMap<String, String> orgDictionary = new HashMap<String, String>();
	FileInputStream fis;
	String Rootpath;
	String ExecutionPath;
	String enviromentsPath;
	String user;
	String curExecutionFolder;
	String htmlReportsPath;
	String snapShotsPath; 
	String dataSheetsPath, dataSheet;
	public Reporting Report;
	
	public static String timenow = null;

	public CommonFuntionDemo(WebDriver driver, HashMap<String, String> Environment, HashMap<String, String> Dictionary, Reporting Report) {
		
		this.driver=driver;
		this.Environment = Environment;
		this.Dictionary = Dictionary;
		this.Report=Report;
		

		user = System.getProperty("user.name");
		 Rootpath = System.getProperty("user.dir");
		Environment.put("RootPath", Rootpath);
		// ExecutionFolderpath

		ExecutionPath = Rootpath + "\\Execution";
		Environment.put("ExecutionFolderPath", ExecutionPath);


		dataSheetsPath = Rootpath + "/datasheets";
		dataSheet = dataSheetsPath + "/" + Environment.get("CLASSNAME")
		+ ".xls";
		
		// Current time
		if (timenow == null) {
			SimpleDateFormat dateformat = new SimpleDateFormat("MMddyyyyhhmm");
			timenow = dateformat.format(new Date());
		}

		// environmentDetails
		enviromentsPath = Rootpath + "//Environments//Environments.xlsx";
		Environment.put("EnviromentsPath", enviromentsPath);
		System.out.println(Environment);
	}

	public String loadPropertyFile(String Key) throws IOException {
		String RootPath = System.getProperty("user.dir");

		fis = new FileInputStream(RootPath + "\\Environments\\config.properties");
		Properties prop = new Properties();
		prop.load(fis);
		String value = prop.getProperty(Key);
		return value;

	}

	public boolean createExecutionFolders(String Browser) throws IOException {

		String browserType = Browser;
		if (Browser.equalsIgnoreCase("chrome")) {
			browserType = "Google Chrome";
		} else if (Browser.equalsIgnoreCase("firefox")) {
			browserType = "Firefox";
		}

		Environment.put("BROWSER", browserType);

		Environment.put("JENKINS_REPORT",ExecutionPath + "/" + timenow + "/" + user + "/" + Environment.get("ENV_CODE") + "/");

		curExecutionFolder = Environment.get("JENKINS_REPORT") + Environment.get("ClassName");

		htmlReportsPath = curExecutionFolder + "/" + Browser.toUpperCase() + "/HTML_Reports";
		snapShotsPath = htmlReportsPath + "/Snapshots";

		System.out.println("******" + curExecutionFolder);
		System.out.println("******" + htmlReportsPath);
		System.out.println("******" + snapShotsPath);

		// Put in Environments
		Environment.put("CURRENTEXECUTIONFOLDER", curExecutionFolder);
		Environment.put("HTMLREPORTSPATH", htmlReportsPath);
		Environment.put("SNAPSHOTSFOLDER", snapShotsPath);

		if (!Environment.get("ClassName").equalsIgnoreCase("htmlreporttest")) {
            // Delete if folder already exists
            if (new File(htmlReportsPath).exists())
                  delete(new File(htmlReportsPath));
            return (new File(snapShotsPath)).mkdirs();
     } else {
            return true;
     }
	}
	
	
	public static void delete(File file) throws IOException {

		if (file.isDirectory()) {

			// list all the directory contents
			String files[] = file.list();

			for (String temp : files) {
				// construct the file structure
				File fileDelete = new File(file, temp);

				// recursive delete
				delete(fileDelete);
			}

			// check the directory again, if empty then delete it
			if (file.list().length == 0) {
				file.delete();
				// System.out.println("Directory is deleted : " +
				// file.getAbsolutePath());

			}

		} else {
			// if file, then delete it
			file.delete();
			// System.out.println("File is deleted : " +
			// file.getAbsolutePath());
		}
	}

	public boolean fetchEnvironmentDetails() {

		try {
			int iVersion = -1;
			int iEnvironment = -1;
			boolean bFlag = false;

			/*
			 * //Get the Column Index for the VERSION Column iVersion =
			 * fGetColumnIndex(Environment.get("ENVIRONMENTXLSPATH"),
			 * "ENVIRONMENTS", "VERSION");
			 * 
			 * //Check if the index value is proper if (iVersion == -1 ){
			 * System.
			 * out.println("Failed to find the Version Column in the file " +
			 * Environment.get("ENVIRONMENTXLSPATH")); return false; }
			 */

			// Get the Column Index for the ENVIRONMENT Column
			iEnvironment = fGetColumnIndex(enviromentsPath, "ENVIRONMENTS",
					"ENVIRONMENT");

			// Check if the index value is proper
			if (iEnvironment == -1) {
				System.out
						.println("Failed to find the Environment Column in the file "
								+ enviromentsPath);
				return false;
			}

			// Create the FileInputStream obhect
			FileInputStream file = new FileInputStream(
					new File(enviromentsPath));
			// Get the workbook instance for XLS file
			XSSFWorkbook workbook = new XSSFWorkbook(file);

			// Get first sheet from the workbook
			XSSFSheet sheet = workbook.getSheet("ENVIRONMENTS");

			// Get the Number of Rows
			int iRowNum = sheet.getLastRowNum();

			// Get the Column count
			int iColCount = sheet.getRow(0).getLastCellNum();

			for (int iRow = 0; iRow <= iRowNum; iRow++) {
				// Check if the version and the environment value is matching
				// String strVersion =
				// sheet.getRow(iRow).getCell(iVersion).getStringCellValue().trim().toUpperCase();
				String strEnvironment = sheet.getRow(iRow)
						.getCell(iEnvironment).getStringCellValue().trim()
						.toUpperCase();
				// Currently checking only on the basis of environment
				if (!strEnvironment.equals(Environment.get("ENV_CODE"))) {
					continue;
				}

				// Set the flag value to true
				bFlag = true;
				String strKey = "";
				String strValue = "";
				// Loop through all the columns
				for (int iCell = 0; iCell < iColCount; iCell++) {
					// Put the Details in Environment Hashmap
					strKey = sheet.getRow(0).getCell(iCell)
							.getStringCellValue().trim().toUpperCase();

					// Fetch the value for the Header Row
					if (sheet
							.getRow(iRow)
							.getCell(
									iCell,
									org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK) == null) {
						strValue = "";
					} else {
						if (sheet.getRow(iRow).getCell(iCell).getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC) {
							strValue = String.valueOf((int) sheet.getRow(iRow)
									.getCell(iCell).getNumericCellValue());
						} else {
							strValue = sheet.getRow(iRow).getCell(iCell)
									.getStringCellValue();
						}

					}

					Environment.put(strKey.trim(), strValue.trim());
				}
				break;
			}
			// Close the file
			file.close();

			// If bFlag is true
			if (bFlag == false) {
				System.out.println("Environment Code "
						+ Environment.get("ENV_CODE")
						+ " not found in the Environment xlsx");
				return false;
			}

			return true;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public int fGetColumnIndex(String strXLS, String strSheetName,
			String strColumnName) {
		try {
			// Create the FileInputStream obhect
			FileInputStream file = new FileInputStream(new File(strXLS));
			// Get the workbook instance for XLS file
			XSSFWorkbook workbook = new XSSFWorkbook(file);

			// Get first sheet from the workbook
			XSSFSheet sheet = workbook.getSheet(strSheetName);

			// Iterate through each rows from first sheet
			Row row = sheet.getRow(0);

			// Get the Column count
			int iColCount = row.getLastCellNum();
			int iCell = 0;
			int iIndex = -1;
			String strTemp = "";

			// Loop through all the columns
			for (iCell = 0; iCell < iColCount; iCell++) {
				// Get the index for Version and Enviornment
				strTemp = sheet.getRow(0).getCell(iCell).getStringCellValue()
						.trim().toUpperCase();

				// if the strColumnName contains Header then check for HEADER or
				// HEADER_IND
				if (strColumnName.equals("HEADER_IND")
						|| strColumnName.equals("HEADER")) {
					if (strTemp.equals("HEADER")
							|| strTemp.equals("HEADER_IND")) {
						iIndex = iCell;
						// Exit the Loop
						break;
					}

				} else {
					if (strTemp.equals(strColumnName.trim().toUpperCase())) {
						iIndex = iCell;
						// Exit the Loop
						break;
					}
				}
			}
			// Close the file
			file.close();

			// Validate if index is returned properly or not
			if (iIndex != -1) {
				// Print the Column Index
				// System.out.println("Column Id for Column " + strColumnName +
				// " is " + iIndex);
				return iIndex;

			} else {
				System.out.println("Failed to find the Column Id for Column "
						+ strColumnName);
				return -1;

			}

		} catch (Exception e) {
			System.out
					.println("Got exception while finding the Index column. Exception is "
							+ e);
			return -1;
		}
	}
	
	public boolean fGetDataForTest(String testName) {
		// DataSheet
		final String dataSheet = dataSheetsPath + "/"
				+ Environment.get("ClassName") + ".xlsx";
		System.out.println("Data sheet is found at:" + dataSheet);
		final String mainSheet = "MAIN";
		final String testNameColumn = "TEST_NAME";

		// Clear Dictionary
		Dictionary.clear();
		orgDictionary.clear();

		// Get column index of test name column
		int iColTestName = fGetColumnIndex(dataSheet, mainSheet, testNameColumn);

		try {

			// Create the FileInputStream obhect
			FileInputStream file = new FileInputStream(new File(dataSheet));
			// Get the workbook instance for XLS file
			XSSFWorkbook workbook = new XSSFWorkbook(file);

			// Get MAIN sheet from the workbook
			XSSFSheet sheet = workbook.getSheet(mainSheet);

			// Iterate through each rows from first sheet
			int iRowCnt = sheet.getLastRowNum();

			// Loop
			int iRow;
			for (iRow = 0; iRow < iRowCnt; iRow++) {
				// Get row with test name and exist
				if (sheet.getRow(iRow).getCell(iColTestName)
						.getStringCellValue().equalsIgnoreCase(testName))
					break;
			}

			// Check if test found
			if (iRow == iRowCnt) {
				System.out.println("Test with name: " + testName
						+ " not found in datasheet: " + dataSheet);
				return false;
			}

			// Set Header & DataRow
			Row headerRow = sheet.getRow(iRow - 1);
			Row dataRow = sheet.getRow(iRow);

			// Get Column count for test-1 row
			int iParamCnt = headerRow.getLastCellNum();
			System.out.println("iParamCnt: " + iParamCnt);
			//
			String key = "";
			String value = "";

			// Loop through params
			int iCol;
			for (iCol = 0; iCol < iParamCnt; iCol++) {

				// Fetch the value for the Header Row
				if (headerRow.getCell(iCol,
						org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK) == null) {
					key = "";
				} else {
					//key = headerRow.getCell(iCol).getStringCellValue();
					if (headerRow.getCell(iCol).getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC) {
						key = String.valueOf((int) headerRow.getCell(iCol)
								.getNumericCellValue());
					} else {
						key = headerRow.getCell(iCol).getStringCellValue();
					}
				}

				// Fetch the value for the Header Row
				if (dataRow.getCell(iCol,
						org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK) == null) {
					value = "";
				} else {
					if (dataRow.getCell(iCol).getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC) {
						value = String.valueOf((int) dataRow.getCell(iCol)
								.getNumericCellValue());
					} else {
						value = dataRow.getCell(iCol).getStringCellValue();
					}
				}

				// Check key value
				if (key.isEmpty())
					break;
				else if (!value.isEmpty()) {
					Dictionary.put(key, value);
					orgDictionary.put(key, value);
				}

			}

			// Give call to get Reference data to replace & parameters
			//if (fGetReferenceData() == false)
			//	return false;

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception " + e
					+ " occured while fetching data from datasheet "
					+ dataSheet + " for test " + testName);
			return false;
		}
	}
	
	public boolean fGuiIsDisplayed(String strDesc) {
		try {

			// Get WebElement
			WebElement webElement = getObject(strDesc);

			String[] delimiters = new String[] { ":=" };
			String[] arrFindByValues = strDesc.split(delimiters[0]);

			// Get Findby and Value
			String FindBy = arrFindByValues[0];
			String val = arrFindByValues[1];

			String reportinString = strDesc;
			if (arrFindByValues.length > 2) {
				reportinString = arrFindByValues[arrFindByValues.length - 1];
			}

			// Check if the WebElement is displayed
			boolean bIsDisplayed = false;

			int intCount = 1;

			// Loop for around 10 secs to check whether object is being
			// displayed
			while (!(bIsDisplayed) && (intCount <= 10)) {
				try {
					bIsDisplayed = webElement.isDisplayed();

				} catch (Exception e) {
					Report.fnWriteToHtmlOutput(
							"Check if element with description  " + strDesc
									+ " is displayed", "Exception occurred",
							"Exception :" + e, "Fail");
					e.printStackTrace();
					return false;
				}

				// Sleep for a sec
				Thread.sleep(1000);
				intCount++;
			}

			// Validate if the element should be displayed or not
			if (bIsDisplayed) {
				Report.fnWriteToHtmlOutput(
						"Check if object with description  " + strDesc
								+ " is displayed", reportinString
								+ " should be displayed", reportinString
								+ " is Displayed", "Pass");
				return true;
			} else {
				Report.fnWriteToHtmlOutput(
						"Check if object with description  " + strDesc
								+ " is displayed", reportinString
								+ " should be displayed", reportinString
								+ " is not displayed", "Fail");
				return false;
			}

		} catch (Exception e) {
			Report.fnWriteToHtmlOutput("Check if element with description  "
					+ strDesc + " is displayed", "Exception occurred",
					"Exception :" + e, "Fail");
			e.printStackTrace();
			return false;
		}
	}

	public WebElement getObject(String objDesc) {
		// Delimiters
		String[] delimiters = new String[] { ":=" };
		String[] arrFindByValues = objDesc.split(delimiters[0]);

		// Get Findby and Value
		String FindBy = arrFindByValues[0];
		String val = arrFindByValues[1];

		String reportinString = objDesc;
		if (arrFindByValues.length > 2) {
			reportinString = arrFindByValues[arrFindByValues.length - 1];
		}

		try {
			// Handle all FindBy cases
			String strElement = FindBy.toLowerCase();
			if (strElement.equalsIgnoreCase("linktext")) {
				return driver.findElement(By.linkText(val));
			} else if (strElement.equalsIgnoreCase("partiallinktext")) {
				return driver.findElement(By.partialLinkText(val));
			} else if (strElement.equalsIgnoreCase("xpath")) {
				return driver.findElement(By.xpath(val));
			} else if (strElement.equalsIgnoreCase("name")) {
				return driver.findElement(By.name(val));
			} else if (strElement.equalsIgnoreCase("id")) {
				return driver.findElement(By.id(val));
			} else if (strElement.equalsIgnoreCase("classname")) {
				return driver.findElement(By.className(val));
			} else if (strElement.equalsIgnoreCase("cssselector")) {
				return driver.findElement(By.cssSelector(val));
			} else if (strElement.equalsIgnoreCase("tagname")) {
				return driver.findElement(By.tagName(val));
			} /*
			 * else if (strElement.equalsIgnoreCase("accessibility_id")) {
			 * return ((io.appium.java_client.AppiumDriver) driver)
			 * .findElement(MobileBy.AccessibilityId(val)); } else if
			 * (strElement.equalsIgnoreCase("appclassname")) { return
			 * ((io.appium.java_client.AppiumDriver) driver)
			 * .findElement(By.className(val)); } else if
			 * (strElement.equalsIgnoreCase("uiautomator")) { return
			 * ((AppiumDriver) driver).findElement(MobileBy
			 * .AndroidUIAutomator(val)); }
			 */else {
				 Report.fnWriteToHtmlOutput("Get object matching description "
						+ objDesc, reportinString
						+ " should be found and returned", "Property " + FindBy
						+ " specified for object is invalid", "Fail");
				System.out.println("Property name " + FindBy
						+ " specified for object " + objDesc + " is invalid");
				return null;
			}
		} catch (Exception e) {
//			String currentUrl = driver.getCurrentUrl();
//			
//			if(Global.envForRefreshLogic.equalsIgnoreCase(Global.currentEnv) && !currentUrl.equalsIgnoreCase(Global.refreshedUrl)  )
//			{
//				driver.navigate().refresh();
//				Reporter.fnWriteToHtmlOutputBold("Refreshing page","Page should be refreshed","Page is Refreshed", "Pass");
//				Global.refreshedUrl = currentUrl;
//				getObject(objDesc);
//			}
//			else{
			Report.fnWriteToHtmlOutput("Get object matching description "
					+ objDesc,
					reportinString + " should be found and returned",
					"Unable to find required object", "Fail");
			System.out.println("Exception " + e.toString()
					+ " occured while fetching the object");
			throw new AssertionFailedError();}
//		}
//		return null;
	}
	
	public boolean fGuiEnterText(String strDesc, String strText) {
		String reportinString = strDesc;
		try {
			WebElement objEdit;

			// Call the function to get the webelement based on the description
			objEdit = getObject(strDesc);

			String[] delimiters = new String[] { ":=" };
			String[] arrFindByValues = strDesc.split(delimiters[0]);

			if (arrFindByValues.length > 2) {
				reportinString = arrFindByValues[arrFindByValues.length - 1];
			}

			// if null is returned
			if (objEdit == null)
				return false;

			// Check if the object is enabled, if yes click the same
			if (objEdit.isEnabled()) {
				
				//scroll to Element  Edited by Rezwanul- mh802d 10/10/2017
				if (driver instanceof JavascriptExecutor) {
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", objEdit);
					try {
						((JavascriptExecutor) driver).executeScript("window.scrollBy(0, -180);");
					} catch (Exception e) {
						// TODO Auto-generated catch block
					}
					sleep(1000);
			    }
				// Enter the text in the edit box
				objEdit.clear();
				objEdit.sendKeys(strText);
				
				System.out.println("Entered " + strText + " in " + strDesc);
			} else {
				Report.fnWriteToHtmlOutput("Check if object is enabled "
						+ strDesc, reportinString + " should be enabled",
						reportinString + " is not enabled", "Fail");
				return false;
			}

			Report.fnWriteToHtmlOutput(
					"Set value in object with description " + strDesc, "Value "
							+ strText + " should be set in the "
							+ reportinString, "Value is set in "
							+ reportinString, "Done");
			return true;
		} catch (Exception e) {
			Report.fnWriteToHtmlOutput(
					"Set value in object with description " + strDesc, "Value "
							+ strText + " should be set in " + reportinString,
					"Exception " + e + " occured while setting the value",
					"Fail");
			return false;
		}

	}
	
	public void sleep(int miliSec) {
		try {
			Thread.sleep(miliSec);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out
					.println("$$$$$$$$$$$$$$$$$$$ Exception while using thread.sleep. Was sleeping for "
							+ miliSec + " miliseconds");
			e.printStackTrace();
		}
	}
	
	public boolean fGuiClick(String strDesc) {
		String reportinString = strDesc;

		try {

			// Initialize
			WebElement objClick;

			// Call the function to get the webelement based on the description
			objClick = getObject(strDesc);

			String[] delimiters = new String[] { ":=" };
			String[] arrFindByValues = strDesc.split(delimiters[0]);

			if (arrFindByValues.length > 2) {
				reportinString = arrFindByValues[arrFindByValues.length - 1];
			}
			// if null is returned
			if (objClick == null)
				return false;

			// Check if the object is enabled, if yes click the same
			if (objClick.isEnabled()) {
				
				//scroll to Element  Edited by Rezwanul(mh802d) 10/10/2017
				if (driver instanceof JavascriptExecutor) {
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", objClick);
					try {
						((JavascriptExecutor) driver).executeScript("window.scrollBy(0, -180);");
					} catch (Exception e) {
						// TODO Auto-generated catch block
					}
					sleep(1000);
			    }
				// Click on the object
				objClick.click();
				System.out.println("Clicked on " + strDesc);
			} else {
				Report.fnWriteToHtmlOutput("Check if object is enabled "
						+ strDesc, reportinString + " with description "
						+ strDesc + " should be enabled", reportinString
						+ " is not enabled", "Fail");
				return false;
			}

			Report.fnWriteToHtmlOutput("Click object matching description "
					+ strDesc, reportinString + " should be clicked",
					reportinString + " is clicked successfully", "Pass");
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			Report.fnWriteToHtmlOutput("Click object matching description "
					+ strDesc, "Could not click on " + reportinString,
					"Exception occured while click object", "Fail");
			return false;
		}
	}
	
	public boolean fGuiCheckObjectExistence(String strDesc) {

		String reportString = strDesc;

		// Delimiters
		String[] delimiters = new String[] { ":=" };
		String[] arrFindByValues = strDesc.split(delimiters[0]);

		// Get Findby and Value
		String FindBy = arrFindByValues[0];
		String val = arrFindByValues[1];

		if (arrFindByValues.length > 2) {
			reportString = arrFindByValues[arrFindByValues.length - 1];
		}

		// WebElement Collection
		List<WebElement> lst;

		try {
			// Handle all FindBy cases
			String strElement = FindBy.toLowerCase();
			if (strElement.equalsIgnoreCase("linktext")) {
				lst = driver.findElements(By.linkText(val));
			} else if (strElement.equalsIgnoreCase("xpath")) {
				lst = driver.findElements(By.xpath(val));
			} else if (strElement.equalsIgnoreCase("name")) {
				lst = driver.findElements(By.name(val));
			} else if (strElement.equalsIgnoreCase("id")) {
				lst = driver.findElements(By.id(val));
			} else if (strElement.equalsIgnoreCase("classname")) {
				lst = driver.findElements(By.className(val));
			} else if (strElement.equalsIgnoreCase("cssselector")) {
				lst = driver.findElements(By.cssSelector(val));
			} else if (strElement.equalsIgnoreCase("tagname")) {
				lst = driver.findElements(By.tagName(val));
			} else {
				Report.fnWriteToHtmlOutput("Check Existence of object "
						+ strDesc, reportString + " should exist", "Property "
						+ FindBy + " specified for object is invalid", "Fail");
				System.out.println("Property name " + FindBy
						+ " specified for object " + reportString
						+ " is invalid");
				return false;
			}

			if (lst.size() > 0) {
				Report.fnWriteToHtmlOutput("Check Existence of object "
						+ strDesc, reportString + " Should exist", reportString
						+ " Exist", "Pass");
				return true;
			} else {
				Report.fnWriteToHtmlOutput("Check Existence of object "
						+ strDesc, reportString + " Should exist", reportString
						+ " does not exist", "Fail");
				return false;
			}

		}

		// Catch Block
		catch (Exception e) {
			Report.fnWriteToHtmlOutput(
					"Check Existence of object " + strDesc, reportString
							+ " should exist",
					"Exception occured while checking existence", "Fail");
			System.out.println("Exception " + e.toString()
					+ " occured while checking object existence");
			return false;
		}
	}
	public boolean waitForObjectToBeClickable(int timeInSecs, String strDesc) {
		// Delimiters
		String[] delimiters = new String[] { ":=" };
		String[] arrFindByValues = strDesc.split(delimiters[0]);

		// Get Findby and Value
		String FindBy = arrFindByValues[0];
		String val = arrFindByValues[1];

		boolean dynamicElement;
		// WebElement Collection
		List<WebElement> lst;

		try {
			// Handle all FindBy cases
			String strElement = FindBy.toLowerCase();
			if (strElement.equalsIgnoreCase("linktext")) {
				dynamicElement = (new WebDriverWait(driver, timeInSecs)).until(
						ExpectedConditions.elementToBeClickable(By
								.linkText(val))).isDisplayed();
			} else if (strElement.equalsIgnoreCase("xpath")) {
				dynamicElement = (new WebDriverWait(driver, timeInSecs)).until(
						ExpectedConditions.elementToBeClickable(By.xpath(val)))
						.isDisplayed();
			} else if (strElement.equalsIgnoreCase("name")) {
				dynamicElement = (new WebDriverWait(driver, timeInSecs)).until(
						ExpectedConditions.elementToBeClickable(By.name(val)))
						.isDisplayed();
			} else if (strElement.equalsIgnoreCase("id")) {
				dynamicElement = (new WebDriverWait(driver, timeInSecs)).until(
						ExpectedConditions.elementToBeClickable(By.id(val)))
						.isDisplayed();
			} else if (strElement.equalsIgnoreCase("classname")) {
				dynamicElement = (new WebDriverWait(driver, timeInSecs)).until(
						ExpectedConditions.elementToBeClickable(By
								.className(val))).isDisplayed();
			} else if (strElement.equalsIgnoreCase("cssselector")) {
				dynamicElement = (new WebDriverWait(driver, timeInSecs)).until(
						ExpectedConditions.elementToBeClickable(By
								.className(val))).isDisplayed();
			} else if (strElement.equalsIgnoreCase("tagname")) {
				dynamicElement = (new WebDriverWait(driver, timeInSecs)).until(
						ExpectedConditions.elementToBeClickable(By
								.className(val))).isDisplayed();
			} else {
				System.out.println("False");
				Report.fnWriteToHtmlOutput("WARNING ITEM : Step for script wait time for obj/page load " 
						, "Script should wait for " +timeInSecs +" Seconds", "Script waited for " +timeInSecs +" Seconds and failed ", "Fail");
				return false;
				
			}
			System.out.println(true);
			return true;
		}

		// Catch Block
		catch (Exception e) {
			String exep= e.toString(); 
			String[] exep1= exep.split("Build");
			System.out.println("Value of the Exeption -"+exep1[0].toString());
			Report.fnWriteToHtmlOutput("WARNING ITEM : Step for script wait time for obj/page load " 
					, "Script should wait for " +timeInSecs +" Seconds", "Script waited for " +timeInSecs + " Seconds and failed due to: \n"+exep1[0].toString(), "Fail");
			return false;
		}

	}
	
	public String fGuiGetAttributeVal(String objDesc, String attName) {

		String attrValue = "";
		List<WebElement> elements = getObjects(objDesc);
		for (WebElement element : elements) {
			attrValue = element.getAttribute(attName);
			if (attrValue != null)
				break;
		}
		// String text = getObject(objDesc).getAttribute(attName);
		System.out.println("attrValue=" + attrValue);
		return attrValue;
	}

	public List<WebElement> getObjects(String objDesc) {
		// Delimiters
		String[] delimiters = new String[] { ":=" };
		String[] arrFindByValues = objDesc.split(delimiters[0]);

		// Get Findby and Value
		String FindBy = arrFindByValues[0];
		String val = arrFindByValues[1];

		String reportinString = objDesc;
		if (arrFindByValues.length > 2) {
			reportinString = arrFindByValues[arrFindByValues.length - 1];
		}

		try {
			// Handle all FindBy cases
			String strElement = FindBy.toLowerCase();
			if (strElement.equalsIgnoreCase("linktext")) {
				return driver.findElements(By.linkText(val));
			} else if (strElement.equalsIgnoreCase("partiallinktext")) {
				return driver.findElements(By.partialLinkText(val));
			} else if (strElement.equalsIgnoreCase("xpath")) {
				return driver.findElements(By.xpath(val));
			} else if (strElement.equalsIgnoreCase("name")) {
				return driver.findElements(By.name(val));
			} else if (strElement.equalsIgnoreCase("id")) {
				return driver.findElements(By.id(val));
			} else if (strElement.equalsIgnoreCase("classname")) {
				return driver.findElements(By.className(val));
			} else if (strElement.equalsIgnoreCase("cssselector")) {
				return driver.findElements(By.cssSelector(val));
			} else if (strElement.equalsIgnoreCase("tagname")) {
				return driver.findElements(By.tagName(val));
			}/*
			 * else if (strElement.equalsIgnoreCase("accessibility_id")) {
			 * return ((io.appium.java_client.AppiumDriver) driver)
			 * .findElements(MobileBy.AccessibilityId(val)); } else if
			 * (strElement.equalsIgnoreCase("appclassname")) { return
			 * ((io.appium.java_client.AppiumDriver) driver)
			 * .findElements(By.className(val)); }
			 */else {
				Report.fnWriteToHtmlOutput(
						"Get objects matching description " + objDesc,
						reportinString + " list should be found and returned",
						"Property " + FindBy
								+ " specified for object is invalid", "Fail");
				System.out.println("Property name " + FindBy
						+ " specified for objects " + objDesc + " is invalid");
				return null;
			}

		}

		// Catch Block
		catch (Exception e) {
			Report.fnWriteToHtmlOutput("Get objects matching description "
					+ objDesc,
					reportinString + " should be found and returned",
					"Unable to find required object", "Fail");
			System.out.println("Exception " + e.toString()
					+ " occured while fetching the object");
			return null;
		}

	}
	

}
