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
propTestStep = myTestCase.getTestStepByName("Properties") // get the Property TestStep object
propTestStep.setPropertyValue("id", "")
propTestStep.setPropertyValue("firstName", "")
propTestStep.setPropertyValue("middleName", "")
propTestStep.setPropertyValue("lastName", "")
propTestStep.setPropertyValue("code", "")
propTestStep.setPropertyValue("_total", "")
propTestStep.setPropertyValue("_count", "1")
propTestStep.setPropertyValue("_next", "")
propTestStep.setPropertyValue("_stopVal", "")
