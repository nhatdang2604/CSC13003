// IMPORT THE LIBRARIES WE NEED
import com.eviware.soapui.support.XmlHolder
import jxl.*
import jxl.write.*
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.util.*;
import org.apache.poi.ss.usermodel.*;

// DECLARE THE VARIABLES

def myTestCase = context.testCase //myTestCase contains the test case
def counter,next,previous,size //Variables used to handle the loop and to move inside the file
def fs = new FileInputStream ("/home/dangkl123/IdeaProjects/CSC13003/SoapUI_stuffs/dataset.xls") 

Workbook workbook1 = WorkbookFactory.create(fs)

Sheet sheet1 = workbook1.getSheetAt(0) //Index 0 will read the first sheet from the workbook, you can also specify the sheet name with "Sheet1"
//Sheet sheet1 = workbook1.getSheet("Sheet1")

size = sheet1.getPhysicalNumberOfRows()//get the number of rows, each row is a data set

log.info size

propTestStep = myTestCase.getTestStepByName("Properties") // get the Property TestStep object
propTestStep.setPropertyValue("_total", size.toString())

counter = propTestStep.getPropertyValue("_count").toString() //counter variable contains iteration number

counter = counter.toInteger()

next = (counter > size - 2? 0: counter+1) //set the next value

// OBTAINING THE DATA YOU NEED

//Get cell (0, counter) on the excel files
Cell f1 = sheet1.getRow(counter).getCell(0) // getCell(column,row) //obtains field1
Cell f2 = sheet1.getRow(counter).getCell(1) // getCell(column,row) //obtains field2
Cell f3 = sheet1.getRow(counter).getCell(2)
Cell f4 = sheet1.getRow(counter).getCell(3)
Cell f5 = sheet1.getRow(counter).getCell(4)

workbook1.close() //close the file

////////////////////////////////////

id = f1.getNumericCellValue().toString()
firstName = f2.getStringCellValue()
middleName = f3.getStringCellValue() 
lastName = f4.getStringCellValue()
code = f5.getNumericCellValue().toString()

propTestStep.setPropertyValue("id", id) //the value is saved in the property
propTestStep.setPropertyValue("firstName", firstName) //the value is saved in the property
propTestStep.setPropertyValue("middleName", middleName) //the value is saved in the property
propTestStep.setPropertyValue("lasttName", lastName) //the value is saved in the property
propTestStep.setPropertyValue("code", code) //the value is saved in the property


propTestStep.setPropertyValue("_count", next.toString()) //increase Count value

next++ //increase next value

propTestStep.setPropertyValue("_next", next.toString()) //set Next value on the properties step

//Decide if the test has to be run again or not
if (counter == size - 1)
	//do nothing
{

propTestStep.setPropertyValue("_stopVal", "T")

log.info "Setting the StopVal property now..."

}

else if (counter==0)

{

def runner = new com.eviware.soapui.impl.wsdl.testcase.WsdlTestCaseRunner(testRunner.testCase, null)

propTestStep.setPropertyValue("_stopVal", "F")

}

else

{

propTestStep.setPropertyValue("_stopVal", "F")

}
