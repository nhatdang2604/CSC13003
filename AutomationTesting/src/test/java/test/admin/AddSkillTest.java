package test.admin;

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


public class AddSkillTest extends BaseTest {

    //XPaths
    private static final String SKILL_NAME_XPATH = "/html/body/div/div[1]/div[2]/div[2]/div/div/form/div[1]/div/div[2]/input";
    private static final String DESCRIPTION_XPATH = "/html/body/div/div[1]/div[2]/div[2]/div/div/form/div[2]/div/div[2]/textarea";
    private static final String SAVE_BUTTON_XPATH = "/html/body/div/div[1]/div[2]/div[2]/div/div/form/div[3]/button[2]";
    private static final String ADD_SKILL_URL = BaseTest.BASE_URL + "/admin/saveSkills";

    private static final String SUCCESSFUL_TOAST_CLASS = "oxd-toast--success";
    private static final String ERROR_TEXT_CLASS = "oxd-input-field-error-message";

    //Expected output id
    private static final int NO_ERROR_OUTPUT_ID = 0;
    private static final int SKILL_NAME_EXISTED_ERROR_ID = 1;
    private static final int REQUIRE_NAME_ERROR_ID = 2;
    private static final int SKILL_NAME_TOO_LONG_ERROR_ID = 3;
    private static final int SKILL_NAME_AND_DESCRIPTION_TOO_LONG_ERROR_ID = 4;

    private static final int SKILL_NAME_REQUIRED_AND_DESCRIPTION_TOO_LONG_ERROR_ID = 5;
    @Test(dataProviderClass = ExcelReader.class, dataProvider = ExcelReader.DATASET_NAME)
    public void addSkillTest(String[] testRecord) throws SQLException, InterruptedException {

        //Initialization
        driver.get(ADD_SKILL_URL);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        //Find and wait for the appearance of the skill name text field,
        // then fill it with the given data.
        WebElement element = null;
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(SKILL_NAME_XPATH)));
        element = driver.findElement(By.xpath(SKILL_NAME_XPATH));
        element.sendKeys(testRecord[1]);

        //Find and wait for the appearance of the description text area,
        //  then fill it with the given data.
        element = driver.findElement(By.xpath(DESCRIPTION_XPATH));
        wait.until(ExpectedConditions.visibilityOf(element));
        element.sendKeys(testRecord[2]);

        //Find and wait for the appearance of the 'save' button, then click it.
        element = driver.findElement(By.xpath(SAVE_BUTTON_XPATH));
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();

        //Get the expected output to use the exact validation for each type
        int expectedOutputId = Integer.parseInt(testRecord[3].trim());

        if (NO_ERROR_OUTPUT_ID == expectedOutputId) {

            //Sleep the thread, waiting for the system to save the skill to database first
            Thread.sleep(1000L);

            //Validate the appearence of the skill on the database
            //  by query to the database
            Connection connection = getConnection(CONNECTION_STR, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            String sql =
                    "SELECT 1 " +
                    "FROM ohrm_skill " +
                    "WHERE name = '" + testRecord[1] + "' AND description = '" + testRecord[2] + "'";
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

        } else if (SKILL_NAME_EXISTED_ERROR_ID == expectedOutputId) {

            //Validate if the "Already exists" text pop out
            List<String> errorMessages = Arrays.asList("Already exists");
            assertErrorTextWithGivenText(ERROR_TEXT_CLASS, errorMessages, wait);

        } else if (REQUIRE_NAME_ERROR_ID == expectedOutputId) {

            //Validate if the "Required" text pop out
            List<String> errorMessages = Arrays.asList("Required");
            assertErrorTextWithGivenText(ERROR_TEXT_CLASS, errorMessages, wait);


        } else if (SKILL_NAME_TOO_LONG_ERROR_ID == expectedOutputId) {

            //Validate if the "Should not exceed 120 characters" text pop out
            List<String> errorMessages = Arrays.asList("Should not exceed 120 characters");
            assertErrorTextWithGivenText(ERROR_TEXT_CLASS, errorMessages, wait);

        } else if (SKILL_NAME_AND_DESCRIPTION_TOO_LONG_ERROR_ID == expectedOutputId) {

            //Validate if the "Should not exceed 120 characters" and "Should not exceed 400 characters" pop out
            List<String> errorMessages = Arrays.asList("Should not exceed 120 characters", "Should not exceed 400 characters");
            assertErrorTextWithGivenText(ERROR_TEXT_CLASS, errorMessages, wait);

        } else if (SKILL_NAME_REQUIRED_AND_DESCRIPTION_TOO_LONG_ERROR_ID == expectedOutputId) {

            //Validate if the "Required" and "Should not exceed 400 characters" pop out
            List<String> errorMessages = Arrays.asList("Required", "Should not exceed 400 characters");
            assertErrorTextWithGivenText(ERROR_TEXT_CLASS, errorMessages, wait);
        }
    }

    @AfterTest
    public void tearDown() throws SQLException {

        //Delete data from ohrm_skill table
        Connection connection = getConnection(CONNECTION_STR, USERNAME, PASSWORD);
        Statement statement = connection.createStatement();
        String sql =
                "DELETE " +
                "FROM ohrm_skill";
        statement.executeUpdate(sql);

        statement.close();
    }

}
