package test.leave;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import test.auth.BaseTest;

import java.time.Duration;


public class AddLeaveTypeTest extends BaseTest {

    private static int ENTITLEMENT_SITUATION_YES_IDX = 0;
    private static int  ENTITLEMENT_SITUATION_NO_IDX = 1;

    private static final String ADD_LEAVE_TYPE_URL = BaseTest.BASE_URL + "/leave/defineLeaveType";

    //@Test
    public void addLeaveTest() {

        String[] dummyData = {"test hehe", "1"};

        driver.get(ADD_LEAVE_TYPE_URL);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("oxd-input oxd-input--active")));
        driver.findElement(By.className("oxd-input oxd-input--active")).sendKeys(dummyData[0]);

        int radioBoxIndex = Integer.getInteger(dummyData[1].trim());
        if (ENTITLEMENT_SITUATION_YES_IDX == radioBoxIndex) {

        } else if (ENTITLEMENT_SITUATION_NO_IDX == radioBoxIndex){

        }

    }

}
