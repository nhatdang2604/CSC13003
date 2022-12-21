package test.flow;

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


public class AssignProjectFlow extends BaseTest {

    @Test(dataProviderClass = ExcelReader.class, dataProvider = ExcelReader.DATASET_NAME)
    public void addLeaveTypeTest(String[] testRecord) throws SQLException, InterruptedException {

        //TODO:
    }

    @AfterTest
    public void tearDown() throws SQLException {
        //TODO:
    }

}
