package test.leave;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import test.auth.BaseTest;

import java.time.Duration;


public class AddLeaveTypeTest extends BaseTest {

    private static int ENTITLEMENT_SITUATION_YES_IDX = 0;
    private static int  ENTITLEMENT_SITUATION_NO_IDX = 1;

    private static final String ADD_LEAVE_TYPE_URL = BaseTest.BASE_URL + "/leave/defineLeaveType";

    @Test
    public void addLeaveTest() {

        String[] dummyData = {"test hehe", "1"};

        driver.get(ADD_LEAVE_TYPE_URL);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//html/body/div/div[1]/div[2]/div[2]/div/div/form/div[1]/div/div[2]/input")));
        driver.findElement(By.xpath("//html/body/div/div[1]/div[2]/div[2]/div/div/form/div[1]/div/div[2]/input")).sendKeys(dummyData[0]);

        int radioBoxIndex = Integer.parseInt(dummyData[1].trim());
        WebElement element = null;
        if (ENTITLEMENT_SITUATION_YES_IDX == radioBoxIndex) {
            element =
                    driver.findElement(By.xpath("/html/body/div/div[1]/div[2]/div[2]/div/div/form/div[2]/div/div/div/div[2]/div[1]/div[2]/div/label/span"));

        } else if (ENTITLEMENT_SITUATION_NO_IDX == radioBoxIndex){
            element =
                    driver.findElement(By.xpath("/html/body/div/div[1]/div[2]/div[2]/div/div/form/div[2]/div/div/div/div[2]/div[2]/div[2]/div/label/span"));
        }

        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();

        element = driver.findElement(By.xpath("/html/body/div/div[1]/div[2]/div[2]/div/div/form/div[3]/button[2]"));
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();

    }

}
