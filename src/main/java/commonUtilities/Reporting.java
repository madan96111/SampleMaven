package commonUtilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class Reporting {

	public WebDriver driver;
	HashMap<String, String> Environment = new HashMap<String, String>();
	HashMap<String, String> Dictionary = new HashMap<String, String>();
	private int g_iSnapshotCount;
	private int g_OperationCount;
	private int g_iPassCount;
	private int g_iFailCount = 0;
	private int g_iTCPassed = 0;
	private int g_iTestCaseNo;
	public int classPassCount = 0;
	public int classFailCount = 0;
	private Date g_SummaryStartTime;
	private FileOutputStream foutStrm = null;
	private String g_strTestCaseReport;
	private String g_strSnapshotFolderName;
	private String g_strSnapshotRelativePath;
	private String g_strScriptName;
	private Date g_StartTime;
	private Date g_EndTime;
	private Date g_SummaryEndTime;
	private static int moduleCounter = 0;
	private String executionDetails;
	
	public Reporting(WebDriver driver,HashMap<String, String> Environment,HashMap<String, String> Dictionary) {
		
		this.driver =driver;
		this.Environment = Environment;
		this.Dictionary = Dictionary;
		
	}
	
	
	public void fnCreateSummaryReport() {
		// Setting counter value
		g_iTCPassed = 0;
		g_iTestCaseNo = 0;
		classPassCount = 0;
		classFailCount = 0;
		g_SummaryStartTime = new Date();

		try {
			System.out.println("*** Html report path: "
					+ Environment.get("HTMLREPORTSPATH"));
			// Open the test case report for writing
			foutStrm = new FileOutputStream(Environment.get("HTMLREPORTSPATH")
					+ "/SummaryReport.html", true);

			// Close the html file
			new PrintStream(foutStrm)
					.println("<HTML><BODY><TABLE BORDER=0 CELLPADDING=3 CELLSPACING=1 WIDTH=100% BGCOLOR=BLACK>");
			new PrintStream(foutStrm)
					.println("<TR><TD WIDTH=90% ALIGN=CENTER BGCOLOR=WHITE><FONT FACE=VERDANA COLOR=ORANGE SIZE=3><B>Testing Purpose Report</B></FONT></TD></TR><TR><TD ALIGN=CENTER BGCOLOR=ORANGE><FONT FACE=VERDANA COLOR=WHITE SIZE=3><B>Selenium Framework Reporting</B></FONT></TD></TR></TABLE><TABLE CELLPADDING=3 WIDTH=100%><TR height=30><TD WIDTH=100% ALIGN=CENTER BGCOLOR=WHITE><FONT FACE=VERDANA COLOR=//0073C5 SIZE=2><B>&nbsp; Automation Result : "
							+ new Date()
							+ " on Machine "
							+ InetAddress.getLocalHost().getHostName()
							+ " by user "
							+ System.getProperty("user.name") + " on Environment " + Environment.get("ENV_CODE") + " and " + Environment.get("BROWSER") + " browser" 
							+ "</B></FONT></TD></TR><TR HEIGHT=5></TR></TABLE>");
			new PrintStream(foutStrm)
					.println("<TABLE  CELLPADDING=3 CELLSPACING=1 WIDTH=100%>");
			new PrintStream(foutStrm)
					.println("<TR COLS=6 BGCOLOR=ORANGE><TD WIDTH=10%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><B>TC No.</B></FONT></TD><TD  WIDTH=70%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><B>Test Name</B></FONT></TD><TD BGCOLOR=ORANGE WIDTH=30%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><B>Status</B></FONT></TD></TR>");

			// Close the object
			foutStrm.close();

		} catch (IOException io) {
			io.printStackTrace();
		}

		foutStrm = null;
	}

	public void createConsolidatedBrowserReport(String browser) {
		try {
			File file = new File(Environment.get("JENKINS_REPORT")
					+ browser.toUpperCase() + "_SummaryReport.html");

			if (!file.exists()) {
				System.out
						.println("*** Consolidated Browser Html report path: "
								+ Environment.get("JENKINS_REPORT"));
				// Open the test case report for writing
				foutStrm = new FileOutputStream(
						Environment.get("JENKINS_REPORT")
								+ browser.toUpperCase() + "_SummaryReport.html",
						true);

				// Close the html file
				new PrintStream(foutStrm)
						.println("<HTML><BODY><TABLE BORDER=0 CELLPADDING=3 CELLSPACING=1 WIDTH=100% BGCOLOR=BLACK>");
				executionDetails="Automation Result : "+ new Date()+ " on Machine "+ InetAddress.getLocalHost().getHostName()+ " by user "
						+ System.getProperty("user.name") + " on Environment " + Environment.get("ENV_CODE") + " and " + Environment.get("BROWSER") + " browser" ;
				new PrintStream(foutStrm)
						.println("<TR><TD WIDTH=90% ALIGN=CENTER BGCOLOR=WHITE><FONT FACE=VERDANA COLOR=ORANGE SIZE=3><B>AT&T Business Center</B></FONT></TD></TR><TR><TD ALIGN=CENTER BGCOLOR=ORANGE><FONT FACE=VERDANA COLOR=WHITE SIZE=3><B>Selenium Framework Reporting</B></FONT></TD></TR></TABLE><TABLE CELLPADDING=3 WIDTH=100%><TR height=30><TD WIDTH=100% ALIGN=CENTER BGCOLOR=WHITE><FONT FACE=VERDANA COLOR=//0073C5 SIZE=2><B>"
								+ executionDetails
								+ "</B></FONT></TD></TR><TR HEIGHT=5></TR></TABLE>");
				new PrintStream(foutStrm)
						.println("<TABLE  CELLPADDING=3 CELLSPACING=1 WIDTH=100%>");
				new PrintStream(foutStrm)
						.println("<TR COLS=6 BGCOLOR=ORANGE><TD WIDTH=10%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><B>SR No.</B></FONT></TD><TD  WIDTH=70%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><B>Module</B></FONT></TD><TD BGCOLOR=ORANGE WIDTH=10%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><B>Num TCs</B></FONT></TD><TD BGCOLOR=ORANGE WIDTH=10%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><B>Passed</B></FONT></TD><TD BGCOLOR=ORANGE WIDTH=10%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><B>Failed</B></FONT></TD></TR>");

				// Close the object
				foutStrm.close();
			}

		} catch (IOException io) {
			io.printStackTrace();
		}

		foutStrm = null;
	}
	
	public void fnWriteToHtmlOutput(String strDescription,
			String strExpectedValue, String strObtainedValue, String strResult) {

		// Declaring Variables
		String snapshotFilePath, sRowColor;
		String snapshotFile = null;
		String sStep;
		if (Dictionary.containsKey("STEP")) {
			sStep = Dictionary.get("STEP") + "<NS>" + strDescription + "<ND>"
					+ strExpectedValue + "<ND>" + strObtainedValue + "<ND>"
					+ strResult;
			Dictionary.remove("STEP");
		} else {
			sStep = strDescription + "<ND>" + strExpectedValue + "<ND>"
					+ strObtainedValue + "<ND>" + strResult;
		}

		Dictionary.put("STEP", sStep);

		// Open the test case report for writing
		// Open the HTML file
		// Open the report file to write the report
		try {
			foutStrm = new FileOutputStream(g_strTestCaseReport, true);
			// System.out.print("reports path is " + g_strTestCaseReport );

		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		}

		// Increment the Operation Count
		g_OperationCount = g_OperationCount + 1;

		// Row Color
		if (g_OperationCount % 2 == 0) {
			sRowColor = "#EEEEEE";
		} else {
			sRowColor = "#D3D3D3";

		}

		// Check if the result is Pass or Fail
		if (strResult.toUpperCase().equals("PASS")) {
			if (Environment.containsKey("IS_API")) {
				if (Environment.get("IS_API").equals("YES")) {
					snapshotFile = Dictionary.get("APIXML");
					System.out.println("snapshot::: " + snapshotFile);
					g_iPassCount++;
				}
			} else {
				// Increment the Pass Count
				g_iPassCount++;
				// Increment the snapshot count
				g_iSnapshotCount++;

				// Get the Full path of the snapshot
				snapshotFilePath = g_strSnapshotFolderName + "/SS_"
						+ g_iSnapshotCount + ".png";

				// Get the relative path of the snapshot
				snapshotFile = g_strSnapshotRelativePath + "/SS_"
						+ g_iSnapshotCount + ".png";

				// Capture the Snapshot
				fTakeScreenshot(snapshotFilePath);
			}

			// Write the result into the file
			// new PrintStream(foutStrm).println("<TR WIDTH=100%><TD  BGCOLOR="
			// + sRowColor +
			// " WIDTH=5% ALIGN=CENTER><FONT FACE=VERDANA SIZE=2><B>" +
			// g_OperationCount + "</B></FONT></TD><TD BGCOLOR=" + sRowColor +
			// " WIDTH=28%><FONT FACE=VERDANA SIZE=2>" + strDescription +
			// " </FONT></TD><TD BGCOLOR=" + sRowColor +
			// " WIDTH=25%><FONT FACE=VERDANA SIZE=2>" + strExpectedValue +
			// " </FONT></TD><TD BGCOLOR=" + sRowColor +
			// " WIDTH=25%><FONT FACE=VERDANA SIZE=2>" + strObtainedValue +
			// " </FONT></TD><TD BGCOLOR=" + sRowColor +
			// " WIDTH=7% ALIGN=CENTER><FONT FACE=VERDANA SIZE=2 COLOR=GREEN><B>"
			// + strResult + "</B></FONT></TD></TR>");
			new PrintStream(foutStrm).println("<TR WIDTH=100%><TD BGCOLOR="
					+ sRowColor
					+ " WIDTH=5% ALIGN=CENTER><FONT FACE=VERDANA SIZE=2 ><B>"
					+ g_OperationCount + "</B></FONT></TD><TD BGCOLOR="
					+ sRowColor + " WIDTH=28%><FONT FACE=VERDANA SIZE=2>"
					+ strDescription + " </FONT></TD><TD BGCOLOR=" + sRowColor
					+ " WIDTH=25%><FONT FACE=VERDANA SIZE=2>"
					+ strExpectedValue + " </FONT></TD><TD BGCOLOR="
					+ sRowColor + " WIDTH=25%><FONT FACE=VERDANA SIZE=2>"
					+ strObtainedValue + " </FONT></TD><TD BGCOLOR="
					+ sRowColor + " WIDTH=7% ALIGN=CENTER><A HREF='"
					+ snapshotFile
					+ "'><FONT FACE=VERDANA SIZE=2 COLOR=GREEN><B>" + strResult
					+ " </B></FONT></A></TD></TR>");
			String S= "<TR WIDTH=100%><TD BGCOLOR="
					+ sRowColor
					+ " WIDTH=5% ALIGN=CENTER><FONT FACE=VERDANA SIZE=2 ><B>"
					+ g_OperationCount + "</B></FONT></TD><TD BGCOLOR="
					+ sRowColor + " WIDTH=28%><FONT FACE=VERDANA SIZE=2>"
					+ strDescription + " </FONT></TD><TD BGCOLOR=" + sRowColor
					+ " WIDTH=25%><FONT FACE=VERDANA SIZE=2>"
					+ strExpectedValue + " </FONT></TD><TD BGCOLOR="
					+ sRowColor + " WIDTH=25%><FONT FACE=VERDANA SIZE=2>"
					+ strObtainedValue + " </FONT></TD><TD BGCOLOR="
					+ sRowColor + " WIDTH=7% ALIGN=CENTER><A HREF='"
					+ snapshotFile
					+ "'><FONT FACE=VERDANA SIZE=2 COLOR=GREEN><B>" + strResult
					+ " </B></FONT></A></TD></TR>";
			System.out.println(S);
		} else {
			if (strResult.toUpperCase().equals("FAIL")) {
				if (Environment.containsKey("IS_API")) {
					if (Environment.get("IS_API").equals("YES")) {
						snapshotFile = Dictionary.get("APIXML");
						g_iFailCount++;
					}
				} else {
					// Increment the SnapShot count
					g_iSnapshotCount++;

					// Increment the Fail Count
					g_iFailCount++;

					// Get the Full path of the snapshot
					snapshotFilePath = g_strSnapshotFolderName + "/SS_"
							+ g_iSnapshotCount + ".png";

					// Get the relative path of the snapshot
					snapshotFile = g_strSnapshotRelativePath + "/SS_"
							+ g_iSnapshotCount + ".png";

					// Increment the snapshot count
					// g_iSnapshotCount++;

					// Capture the Snapshot
					fTakeScreenshot(snapshotFilePath);
				}
				// Write the result into the file
				new PrintStream(foutStrm)
						.println("<TR WIDTH=100%><TD BGCOLOR="
								+ sRowColor
								+ " WIDTH=5% ALIGN=CENTER><FONT FACE=VERDANA SIZE=2 ><B>"
								+ g_OperationCount
								+ "</B></FONT></TD><TD BGCOLOR=" + sRowColor
								+ " WIDTH=28%><FONT FACE=VERDANA SIZE=2>"
								+ strDescription + " </FONT></TD><TD BGCOLOR="
								+ sRowColor
								+ " WIDTH=25%><FONT FACE=VERDANA SIZE=2>"
								+ strExpectedValue
								+ " </FONT></TD><TD BGCOLOR=" + sRowColor
								+ " WIDTH=25%><FONT FACE=VERDANA SIZE=2>"
								+ strObtainedValue
								+ " </FONT></TD><TD BGCOLOR=" + sRowColor
								+ " WIDTH=7% ALIGN=CENTER><A HREF='"
								+ snapshotFile
								+ "'><FONT FACE=VERDANA SIZE=2 COLOR=RED><B>"
								+ strResult + " </B></FONT></A></TD></TR>");
			} else {
				// Write Results into the file
				new PrintStream(foutStrm)
						.println("<TR WIDTH=100%><TD BGCOLOR="
								+ sRowColor
								+ " WIDTH=5% ALIGN=CENTER><FONT FACE=VERDANA SIZE=2><B>"
								+ g_OperationCount
								+ "</B></FONT></TD><TD BGCOLOR="
								+ sRowColor
								+ " WIDTH=28%><FONT FACE=VERDANA SIZE=2>"
								+ strDescription
								+ "</FONT></TD><TD BGCOLOR="
								+ sRowColor
								+ " WIDTH=25%><FONT FACE=VERDANA SIZE=2>"
								+ strExpectedValue
								+ "</FONT></TD><TD BGCOLOR="
								+ sRowColor
								+ " WIDTH=25%><FONT FACE=VERDANA SIZE=2>"
								+ strObtainedValue
								+ "</FONT></TD><TD BGCOLOR="
								+ sRowColor
								+ " WIDTH=7% ALIGN=CENTER><FONT FACE=VERDANA SIZE=2 COLOR=orange><B>"
								+ strResult + "</B></FONT></TD></TR>");
			}

		}
		// Function call to write the summary in QC
		/*
		 * if (objTD!= null && objTD.connected() == true) { if
		 * (QC.fQCStepUpdate(objTD, Dictionary,strDescription, strDescription,
		 * strExpectedValue, strObtainedValue, strResult)==false) {
		 * System.out.println("Failed to update step in QC"); System.exit(0); }
		 * }
		 */

		try {
			// Close File stream
			foutStrm.close();

		} catch (IOException io) {
			io.printStackTrace();
		}
	}
	
	public String fnCreateHtmlReport(String strTestName) {

		System.out.println("Creating html report for " + strTestName);
		Environment.put("METHOD_NAME", strTestName);
		// Set the default Operation count as 0
		g_OperationCount = 0;

		// Number of default Pass and Fail cases to 0
		g_iPassCount = 0;
		g_iFailCount = 0;

		// Snapshot count to start from 0
		g_iSnapshotCount = 0;

		// script name
		g_strScriptName = strTestName;

		// Set the name for the Test Case Report File
		g_strTestCaseReport = Environment.get("HTMLREPORTSPATH") + "/Report_"
				+ g_strScriptName + ".html";
		// System.out.print(" Detailed Report path is " + g_strTestCaseReport);

		// Snap Shot folder
		g_strSnapshotFolderName = Environment.get("SNAPSHOTSFOLDER") + "/"
				+ g_strScriptName;

		if (Environment.containsKey("IS_API")) {
			if (Environment.get("IS_API").equals("YES")) {
				g_strSnapshotRelativePath = "APIResponse//" + g_strScriptName;
				Environment.put("APIRESPONSE", g_strSnapshotFolderName);
				Environment.put("APIRESPONSE_RELATIVE",
						g_strSnapshotRelativePath);
				System.out.println("environment relative@@@@ "
						+ g_strSnapshotRelativePath);
			}
		} else {
			// Snapshot relative path
			g_strSnapshotRelativePath = "Snapshots//" + g_strScriptName;
		}

		// Delete the Summary Folder if present
		File file = new File(g_strSnapshotFolderName);

		if (file.exists()) {
			file.delete();
		}

		// Make a new snapshot folder
		file.mkdir();

		// Open the report file to write the report

		try {
			foutStrm = new FileOutputStream(g_strTestCaseReport);
		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		}

		// Write the Test Case name and allied headers into the file
		// Write the Test Case name and allied headers into the file
		// Close the html file
		try {
			new PrintStream(foutStrm)
					.println("<HTML><BODY><TABLE BORDER=0 CELLPADDING=3 CELLSPACING=1 WIDTH=100% BGCOLOR=ORANGE>");
			new PrintStream(foutStrm)
					.println("<TR><TD WIDTH=90% ALIGN=CENTER BGCOLOR=WHITE><FONT FACE=VERDANA COLOR=ORANGE SIZE=3><B>HTML Report</B></FONT></TD></TR><TR><TD ALIGN=CENTER BGCOLOR=ORANGE><FONT FACE=VERDANA COLOR=WHITE SIZE=3><B>Selenium Framework Reporting</B></FONT></TD></TR></TABLE><TABLE CELLPADDING=3 WIDTH=100%><TR height=30><TD WIDTH=100% ALIGN=CENTER BGCOLOR=WHITE><FONT FACE=VERDANA COLOR=//0073C5 SIZE=2><B>&nbsp; Automation Result : "
							+ new Date()
							+ " on Machine "
							+ InetAddress.getLocalHost().getHostName()
							+ " by user "
							+ System.getProperty("user.name") + " on Environment " + Environment.get("ENV_CODE") + " and " + Environment.get("BROWSER") + " browser" 
							+ "</B></FONT></TD></TR><TR HEIGHT=5></TR></TABLE>");
			new PrintStream(foutStrm)
					.println("<TABLE BORDER=0 BORDERCOLOR=WHITE CELLPADDING=3 CELLSPACING=1 WIDTH=100%>");
			new PrintStream(foutStrm)
					.println("<TR><TD BGCOLOR=BLACK WIDTH=20%><FONT FACE=VERDANA COLOR=WHITE SIZE=2><B>Test  Name:</B></FONT></TD><TD COLSPAN=6 BGCOLOR=BLACK><FONT FACE=VERDANA COLOR=WHITE SIZE=2><B>"
							+ g_strScriptName + "</B></FONT></TD></TR>");
			// new
			// PrintStream(foutStrm).println("<TR><TD BGCOLOR=BLACK WIDTH=20%><FONT FACE=VERDANA COLOR=WHITE SIZE=2><B>Test    Iteration:</B></FONT></TD><TD COLSPAN=6 BGCOLOR=BLACK><FONT FACE=VERDANA COLOR=WHITE SIZE=2><B> </B></FONT></TD></TR>");
			new PrintStream(foutStrm)
					.println("</TABLE><BR/><TABLE WIDTH=100% CELLPADDING=3>");
			new PrintStream(foutStrm)
					.println("<TR WIDTH=100%><TH BGCOLOR=ORANGE WIDTH=5%><FONT FACE=VERDANA SIZE=2>Step No.</FONT></TH><TH BGCOLOR=ORANGE WIDTH=28%><FONT FACE=VERDANA SIZE=2>Step Description</FONT></TH><TH BGCOLOR=ORANGE WIDTH=25%><FONT FACE=VERDANA SIZE=2>Expected Value</FONT></TH><TH BGCOLOR=ORANGE WIDTH=25%><FONT FACE=VERDANA SIZE=2>Obtained Value</FONT></TH><TH BGCOLOR=ORANGE WIDTH=7%><FONT FACE=VERDANA SIZE=2>Result</FONT></TH></TR>");

			foutStrm.close();
		} catch (IOException io) {
			io.printStackTrace();
		}
		// Deference the file pointer
		foutStrm = null;

		// Get the start time of the execution
		g_StartTime = new Date();
		System.out.println("Html report will be found in: " + g_strTestCaseReport);
		return g_strTestCaseReport;

	}

	
	public void fTakeScreenshot(String SSPath) {
		try {
			File scrFile = ((TakesScreenshot) driver)
					.getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(scrFile, new File(SSPath));

		} catch (IOException io) {
			io.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void fnCloseTestSummary() {
		g_SummaryEndTime = new Date();

		int totalValidations = classPassCount + classFailCount;
		// Open the Test Summary Report File
		try {
			foutStrm = new FileOutputStream(Environment.get("HTMLREPORTSPATH")
					+ "/SummaryReport.html", true);

			new PrintStream(foutStrm).println("</TABLE><TABLE WIDTH=100%><TR>");
			new PrintStream(foutStrm)
					.println("<TD BGCOLOR=BLACK WIDTH=10%></TD><TD BGCOLOR=BLACK WIDTH=70%><FONT FACE=VERDANA SIZE=2 COLOR=WHITE><B><TABLE WIDTH=100%><TR><TD WIDTH=34%><FONT FACE=VERDANA SIZE=2 COLOR=WHITE><B>Total Validations: " + totalValidations
							+ "</B></FONT></TD><TD WIDTH=33%><FONT FACE=VERDANA SIZE=2 COLOR=WHITE><B>Passes: " + classPassCount + "</B></FONT></TD><TD WIDTH=33%><FONT FACE=VERDANA SIZE=2 COLOR=WHITE><B>Fails: " + classFailCount + "</B></FONT></TD></TR></TABLE></B></FONT></TD><TD BGCOLOR=BLACK WIDTH=20%><FONT FACE=WINGDINGS SIZE=4>2</FONT><FONT FACE=VERDANA SIZE=2 COLOR=WHITE><B>Passed TCs Count: "
							+ g_iTCPassed + "</B></FONT></TD>");
			new PrintStream(foutStrm).println("</TR></TABLE>");
			new PrintStream(foutStrm)
					.println("<TABLE WIDTH=100%><TR><TD ALIGN=RIGHT><FONT FACE=VERDANA COLOR=ORANGE SIZE=1>&copy; at&t business center - Integrated Customer Management</FONT></TD></TR></TABLE></BODY></HTML>");

			// Close File stream
			foutStrm.close();

		} catch (IOException io) {
			io.printStackTrace();
		}

		// Deference the file pointer
		foutStrm = null;
	}
	
	public void writeConsolidatedBrowserSummary(String browser) {

		String sRowColor;
	  String job = System.getProperty("jobName");	
	  //System.out.println("JENKINS JOB NAME IS " + job);
	  String buildUrl = System.getProperty("buildUrl");
	  //System.out.println("JENKINS Build Url iS " + buildUrl);
		ArrayList<Integer> list = new ArrayList<Integer>();
		char character = '/';
		String url = "";
		if(job != null && !job.equalsIgnoreCase("") && !job.equalsIgnoreCase(" ")){
		for(int i = 0; i < buildUrl.length(); i++){
		    if(buildUrl.charAt(i) == character){
		       list.add(i);
		    }
		}
		int a = list.get(list.size()-2);
		url = buildUrl.substring(0, a) + "/ws/codebase/Execution/"+Environment.get("TIMESTAMP") + "/"+ System.getProperty("user.name")
						+ "/"+ Environment.get("ENVIRONMENT")+ "/"+ Environment.get("CLASSNAME")+ "/"+ browser.toUpperCase()+ "/HTML_Reports/SummaryReport.html";
		System.out.println("JOB EXECUTION FOLDER URL IS " + url);
		}
		
		moduleCounter++;

		if (moduleCounter % 2 == 0) {
			// sRowColor = "/BEBEBE";
			sRowColor = "#EEEEEE";
		} else {
			sRowColor = "#D3D3D3";
		}

		int g_iTCFailed = g_iTestCaseNo - g_iTCPassed;
		// Close the file
		try {
			// Open the test case report for writing
			foutStrm = new FileOutputStream(Environment.get("JENKINS_REPORT")
					+ browser.toUpperCase() + "_SummaryReport.html", true);

//			if (!System.getProperty("user.name").equals("m86044")
//					&& !System.getProperty("user.name").equals(
//							"HVDIVD05CAF1138$") && !System.getProperty("user.name").equals(
//									"HVDIVD05CAF1067$") && !System.getProperty("user.name").equals(
//											"dt6430")) {
			if(job == null || job.equalsIgnoreCase("") || job.equalsIgnoreCase(" ")){
				new PrintStream(foutStrm).println("<TR COLS=6 BGCOLOR="
						+ sRowColor
						+ "><TD  WIDTH=10%><FONT FACE=VERDANA SIZE=2>"
						+ moduleCounter
						+ "</FONT></TD><TD  WIDTH=70%><A HREF='"
						+ Environment.get("HTMLREPORTSPATH")
						+ "/SummaryReport.html'><FONT FACE=VERDANA SIZE=2>"
						+ Environment.get("ClassName")
						+ "</FONT></A></TD><TD  WIDTH=10%>" + g_iTestCaseNo
						+ "</TD><TD  WIDTH=10%>" + g_iTCPassed
						+ "</TD><TD  WIDTH=10%>" + g_iTCFailed + "</TD></TR>");
			} else {
				new PrintStream(foutStrm)
				.println("<TR COLS=6 BGCOLOR="
						+ sRowColor
						+ "><TD  WIDTH=10%><FONT FACE=VERDANA SIZE=2>"
						+ moduleCounter
						+ "</FONT></TD><TD  WIDTH=70%><A HREF='" + url + "'><FONT FACE=VERDANA SIZE=2>"
						+ Environment.get("CLASSNAME")
						+ "</FONT></A></TD><TD  WIDTH=10%>"
						+ g_iTestCaseNo + "</TD><TD  WIDTH=10%>"
						+ g_iTCPassed + "</TD><TD  WIDTH=10%>"
						+ g_iTCFailed + "</TD></TR>");
			}
			foutStrm.close();
		} catch (IOException io) {
			io.printStackTrace();
		}
		foutStrm = null;
	}
	
	public void fnCloseHtmlReport(String strTestName) {

		classPassCount = classPassCount + g_iPassCount;
		classFailCount = classFailCount + g_iFailCount;
		// Declaring variables

		String strTestCaseResult = null;

		// Open the report file to write the report
		try {
			foutStrm = new FileOutputStream(g_strTestCaseReport, true);

		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		}

		// Get the current time
		g_EndTime = new Date();

		// Fetch the time difference
		String strTimeDifference = fnTimeDiffference(g_StartTime.getTime(),
				g_EndTime.getTime());

		// Close the html file
		try {
			// Write the number of test steps passed/failed and the time which
			// the test case took to run
			new PrintStream(foutStrm)
					.println("<TR></TR><TR><TD BGCOLOR=BLACK WIDTH=5%></TD><TD BGCOLOR=BLACK WIDTH=28%><FONT FACE=VERDANA COLOR=WHITE SIZE=2><B>Time Taken : "
							+ strTimeDifference
							+ "</B></FONT></TD><TD BGCOLOR=BLACK WIDTH=25%><FONT FACE=VERDANA COLOR=WHITE SIZE=2><B>Pass Count : "
							+ g_iPassCount
							+ "</B></FONT></TD><TD BGCOLOR=BLACK WIDTH=25%><FONT FACE=VERDANA COLOR=WHITE SIZE=2><B>Fail Count : "
							+ g_iFailCount
							+ "</b></FONT></TD><TD BGCOLOR=Black WIDTH=7%></TD></TR>");
			// Close the HTML tags
			new PrintStream(foutStrm)
					.println("</TABLE><TABLE WIDTH=100%><TR><TD ALIGN=RIGHT><FONT FACE=VERDANA COLOR=ORANGE SIZE=1>&copy; at&t business center- Integrated Customer Management</FONT></TD></TR></TABLE></BODY></HTML>");
			// Close File stream
			foutStrm.close();

		} catch (IOException io) {
			io.printStackTrace();
		}

		// Deference the file pointer
		foutStrm = null;

		// Check if test case passed or failed

		if (g_iFailCount != 0) {
			strTestCaseResult = "Fail";
		} else {
			strTestCaseResult = "Pass";
		}

		// fnCloseHtmlReport = strTestCaseResult

		// Write into the Summary Report
		fnWriteTestSummary("Report_" + strTestName, strTestCaseResult);

	}
	public void fnWriteTestSummary(String strTestCaseName, String strResult) {

		String sColor, sRowColor;

		// Close the file
		try {
			// Open the test case report for writing
			foutStrm = new FileOutputStream(Environment.get("HTMLREPORTSPATH")
					+ "/SummaryReport.html", true);

			// Check color result
			if (strResult.toUpperCase().equals("PASSED")
					|| strResult.toUpperCase().equals("PASS")) {
				sColor = "GREEN";
				g_iTCPassed++;
			} else if (strResult.toUpperCase().equals("FAILED")
					|| strResult.toUpperCase().equals("FAIL")) {
				sColor = "RED";

			} else {
				sColor = "ORANGE";
			}

			g_iTestCaseNo++;

			if (g_iTestCaseNo % 2 == 0) {
				// sRowColor = "/BEBEBE";
				sRowColor = "#EEEEEE";
			} else {
				sRowColor = "#D3D3D3";
			}
			// Write the result of Individual Test Case
			new PrintStream(foutStrm).println("<TR COLS=3 BGCOLOR=" + sRowColor
					+ "><TD  WIDTH=10%><FONT FACE=VERDANA SIZE=2>"
					+ g_iTestCaseNo
					+ "</FONT></TD><TD  WIDTH=70%><FONT FACE=VERDANA SIZE=2>"
					+ strTestCaseName + "</FONT></TD><TD  WIDTH=20%><A HREF='"
					+ strTestCaseName
					+ ".html'><FONT FACE=VERDANA SIZE=2 COLOR=" + sColor
					+ "><B>" + strResult + "</B></FONT></A></TD></TR>");

			foutStrm.close();
		} catch (IOException io) {
			io.printStackTrace();
		}
		foutStrm = null;

	}

	public String fnTimeDiffference(long startTime, long endTime) {

		// Finding the difference in milliseconds
		long delta = endTime - startTime;

		// Finding number of days
		int days = (int) delta / (24 * 3600 * 1000);

		// Finding the remainder
		delta = (int) delta % (24 * 3600 * 1000);

		// Finding number of hrs
		int hrs = (int) delta / (3600 * 1000);

		// Finding the remainder
		delta = (int) delta % (3600 * 1000);

		// Finding number of minutes
		int min = (int) delta / (60 * 1000);

		// Finding the remainder
		delta = (int) delta % (60 * 1000);

		// Finding number of seconds
		int sec = (int) delta / 1000;

		// Concatenting to get time difference in the form day:hr:min:sec
		String strTimeDifference = days + ":" + hrs + ":" + min + ":" + sec;
		return strTimeDifference;
	}
	
	public void writeConsolidatedBrowserSummaryReport(String browser) {

		String sRowColor;

		moduleCounter++;

		if (moduleCounter % 2 == 0) {
			// sRowColor = "/BEBEBE";
			sRowColor = "#EEEEEE";
		} else {
			sRowColor = "#D3D3D3";
		}

		int g_iTCFailed = g_iTestCaseNo - g_iTCPassed;
		// Close the file
		try {
			// Open the test case report for writing
			foutStrm = new FileOutputStream(Environment.get("JENKINS_REPORT")
					+ browser.toUpperCase() + "_SummaryReport.html", true);

			// Write the result of Individual Test Class
			// new PrintStream(foutStrm).println ("<TR COLS=6 BGCOLOR=" +
			// sRowColor + "><TD  WIDTH=10%><FONT FACE=VERDANA SIZE=2>" +
			// moduleCounter + "</FONT></TD><TD  WIDTH=70%><A HREF='" +
			// Environment.get("HTMLREPORTSPATH")+
			// "/SummaryReport.html'><FONT FACE=VERDANA SIZE=2>" +
			// Environment.get("CLASSNAME") + "</FONT></A></TD><TD  WIDTH=10%>"
			// + g_iTestCaseNo + "</TD><TD  WIDTH=10%>" + g_iTCPassed +
			// "</TD><TD  WIDTH=10%>" +g_iTCFailed + "</TD></TR>");

			if (!System.getProperty("user.name").equals("m86044")
					&& !System.getProperty("user.name").equals(
							"HVDIVD05CAF1138$") && !System.getProperty("user.name").equals(
									"dt6430")) {
				new PrintStream(foutStrm).println("<TR COLS=6 BGCOLOR="
						+ sRowColor
						+ "><TD  WIDTH=10%><FONT FACE=VERDANA SIZE=2>"
						+ moduleCounter
						+ "</FONT></TD><TD  WIDTH=70%><A HREF='"
						+ Environment.get("HTMLREPORTSPATH")
						+ "/SummaryReport.html'><FONT FACE=VERDANA SIZE=2>"
						+ Environment.get("ClassName")
						+ "</FONT></A></TD><TD  WIDTH=10%>" + g_iTestCaseNo
						+ "</TD><TD  WIDTH=10%>" + g_iTCPassed
						+ "</TD><TD  WIDTH=10%>" + g_iTCFailed + "</TD></TR>");
			} else {
				new PrintStream(foutStrm)
						.println("<TR COLS=6 BGCOLOR="
								+ sRowColor
								+ "><TD  WIDTH=10%><FONT FACE=VERDANA SIZE=2>"
								+ moduleCounter
								+ "</FONT></TD><TD  WIDTH=70%><A HREF='http://zltv8453.vci.att.com:18080/jenkins/view/1508HF_Dashboard/job/Selenium_API_Test_1508/ws/codebase/RecentReport/"
								+ System.getProperty("user.name")
								+ "/"
								+ Environment.get("ENVIRONMENT")
								+ "/"
								+ Environment.get("ClassName")
								+ "/"
								+ browser.toUpperCase()
								+ "/HTML_Reports/SummaryReport.html'><FONT FACE=VERDANA SIZE=2>"
								+ Environment.get("CLASSNAME")
								+ "</FONT></A></TD><TD  WIDTH=10%>"
								+ g_iTestCaseNo + "</TD><TD  WIDTH=10%>"
								+ g_iTCPassed + "</TD><TD  WIDTH=10%>"
								+ g_iTCFailed + "</TD></TR>");
			}
			foutStrm.close();
		} catch (IOException io) {
			io.printStackTrace();
		}
		foutStrm = null;
	}
}
