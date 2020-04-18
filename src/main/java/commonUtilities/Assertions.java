package commonUtilities;

import org.testng.Assert;




public class Assertions {

	public Reporting Report;
	public boolean testStatus;
	
	public Assertions(Reporting Report)
	{
		this.Report = Report;
		testStatus = true;
	}
	
	// *****************************************************************************************
    // * Name : assertTrue
    // * Description : This method verifies if the given condition is true
    // * Author : Prakash Bastola
    // * Input Params : condition -> boolean condition to be verified
    //*                 conditionInString -> condition expressed in string
    // * Return Values : boolean
    // *****************************************************************************************
    public void assertTrue(Boolean condition, String conditionInString) {
           
           try{
                 Assert.assertTrue(condition);  
                 Report.fnWriteToHtmlOutput("Verifying the condition: " + conditionInString , conditionInString + " should be true",
                               conditionInString+" is "+ condition , "Pass");
           }
           catch(AssertionError ex)
           {
                 Report.fnWriteToHtmlOutput("Verifying the condition: " + conditionInString , conditionInString + " should be true",
                               conditionInString +" is "+ condition , "Fail");
                 throw new AssertionError();
           }
    }
    
  
}
