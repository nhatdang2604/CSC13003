package test.leave;

//import junit.framework.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;
import test.auth.BaseTest;
import utils.ExcelReader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;


public class AddLeaveTypeTest extends BaseTest {

    private static int ENTITLEMENT_SITUATION_YES_IDX = 1;
    private static int  ENTITLEMENT_SITUATION_NO_IDX = 0;

    //XPaths
    private static final String LEAVE_TYPE_NAME_XPATH = "//html/body/div/div[1]/div[2]/div[2]/div/div/form/div[1]/div/div[2]/input";
    private static final String ENTITLEMENT_SITUTATION_YES_XPATH = "/html/body/div/div[1]/div[2]/div[2]/div/div/form/div[2]/div/div/div/div[2]/div[1]/div[2]/div/label/span";
    private static final String ENTITLEMENT_SITUTATION_NO_XPATH = "/html/body/div/div[1]/div[2]/div[2]/div/div/form/div[2]/div/div/div/div[2]/div[2]/div[2]/div/label/span";
    private static final String SAVE_BUTTON_XPATH = "/html/body/div/div[1]/div[2]/div[2]/div/div/form/div[3]/button[2]";
//    private static final String ALREADY_EXISTED_ERROR_TEXT_XPATH = "/html/body/div/div[1]/div[2]/div[2]/div/div/form/div[1]/div/span";

    private static final String ERROR_TEXT_CLASS = "oxd-input-field-error-message";

    private static final String SUCCESSFUL_TOAST_CLASS = "oxd-toast";

    private static final String ADD_LEAVE_TYPE_URL = BaseTest.BASE_URL + "/leave/defineLeaveType";

    //Expected output id
    private static final int NO_ERROR_OUTPUT_ID = 0;
    private static final int LEAVE_NAME_EXISTED_ERROR_ID = 1;
    private static final int REQUIRE_NAME_ERROR_ID = 2;
    private static final int LEAVE_NAME_TOO_LONG_ERROR_ID = 3;

    @Test(dataProviderClass = ExcelReader.class, dataProvider = ExcelReader.DATASET_NAME)
    public void addLeaveTypeTest(String[] testRecord) throws SQLException, InterruptedException {

        //Find and wait for the appearance of the leave type name text field,
        // then fill it with the given data.
        driver.get(ADD_LEAVE_TYPE_URL);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(LEAVE_TYPE_NAME_XPATH)));
        driver.findElement(By.xpath(LEAVE_TYPE_NAME_XPATH)).sendKeys(testRecord[1]);

        //Find and wait for the appearance of the entitlement situtation radio BoxIndexbutton,
        //  then click the button base on the value of raido.
        int radioBoxIndex = Integer.parseInt(testRecord[2].trim());
        WebElement element = null;
        if (ENTITLEMENT_SITUATION_YES_IDX == radioBoxIndex) {
            element =
                    driver.findElement(By.xpath(ENTITLEMENT_SITUTATION_YES_XPATH));

        } else if (ENTITLEMENT_SITUATION_NO_IDX == radioBoxIndex){
            element =
                    driver.findElement(By.xpath(ENTITLEMENT_SITUTATION_NO_XPATH));
        }

        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();

        //Find and wait for the appearance of the 'save' button, then click it.
        element = driver.findElement(By.xpath(SAVE_BUTTON_XPATH));
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();

        //Get the expected output to use the exact validation for each type
        int expectedOutputId = Integer.parseInt(testRecord[3].trim());

        if (NO_ERROR_OUTPUT_ID == expectedOutputId) {

            //Sleep the thread, waiting for the system to save the leave type to database first
            Thread.sleep(1000L);

            //Validate the appearence of the leave type on the database
            //  by query to the database
            Connection connection = getConnection(CONNECTION_STR, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            String sql =
                    "SELECT 1 " +
                    "FROM ohrm_leave_type " +
                    "WHERE name = '" + testRecord[1] + "' AND exclude_in_reports_if_no_entitlement = " + testRecord[2];
            ResultSet resultSet = statement.executeQuery(sql);
            Integer result = null;
            while (resultSet.next()) {
                result = resultSet.getInt(1);
            }
            resultSet.close();
            statement.close();
            Assert.assertNotNull(result);

            //Validate if the successful toast appeared
            element = driver.findElement(By.className(SUCCESSFUL_TOAST_CLASS));
            wait.until(ExpectedConditions.visibilityOf(element));

        } else if (LEAVE_NAME_EXISTED_ERROR_ID == expectedOutputId) {

            //Validate if the "Already exists" text pop out
            List<String> errorMessages = Arrays.asList("Already exists");
            assertErrorTextWithGivenText(ERROR_TEXT_CLASS, errorMessages, wait);

        } else if (REQUIRE_NAME_ERROR_ID == expectedOutputId) {

            //Validate if the "Required" text pop out
            List<String> errorMessages = Arrays.asList("Required");
            assertErrorTextWithGivenText(ERROR_TEXT_CLASS, errorMessages, wait);

        } else if (LEAVE_NAME_TOO_LONG_ERROR_ID == expectedOutputId) {

            //Validate if the "Should not exceed 50 characters" text pop out
            List<String> errorMessages = Arrays.asList("Should not exceed 50 characters");
            assertErrorTextWithGivenText(ERROR_TEXT_CLASS, errorMessages, wait);
        }

    }

    @AfterTest
    public void tearDown() throws SQLException {

        //Delete data from ohrm_leave_type table
        Connection connection = getConnection(CONNECTION_STR, USERNAME, PASSWORD);
        Statement statement = connection.createStatement();

        String sql =
                "DELETE " +
                "FROM ohrm_leave_type";
        statement.executeUpdate(sql);

        statement.close();
    }

}
