package test.auth;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.sql.*;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class BaseTest {

    protected WebDriver driver;

    private static final String IP = "localhost";
    private static final String PORT = "8088";

    public static final String BASE_URL = "http://" + IP + ":" + PORT + "/orangehrm-5.1/web/index.php";
    private static final String LOGIN_URL = BASE_URL + "/auth/login";

    //SQL stuffs
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String DBS_IP = "127.0.0.1";                   //editable
    private static final String DBS_PORT = "3306";                      //editable
    private static final String SCHEMA_NAME = "orangehrm_test";    //editable

    public static final String CONNECTION_STR = "jdbc:mysql://" + DBS_IP + ":" + DBS_PORT + "/" + SCHEMA_NAME + "?useSSL=false";
    public static final String USERNAME = "root";  //editable
    public static final String PASSWORD = "";      //editable

    //Building connection to connect to database
    protected Connection getConnection(String dbURL, String userName, String password) {
        Connection conn = null;
        try {
            Class.forName(DRIVER_CLASS);
            conn = DriverManager.getConnection(dbURL, userName, password);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return conn;
    }

    protected void assertErrorTextWithGivenText(String errorTextClass, List<String> givenText, WebDriverWait wait) {
        List<WebElement> elements = driver.findElements(By.className(errorTextClass));
        wait.until(ExpectedConditions.visibilityOfAllElements(elements));
        int size = elements.size();
        for (int i = 0; i < size; ++i) {
            String errorMessage = elements.get(i).getAttribute("innerHTML");
            Assert.assertEquals(errorMessage, givenText.get(i));
        }
    }

    @BeforeTest
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        driver.get(LOGIN_URL);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name='username']")));

        driver.findElement(By.cssSelector("input[name='username']")).sendKeys("admin");
        driver.findElement(By.cssSelector("input[name='password']")).sendKeys("Dangkl123812010@");
        driver.findElement(By.className("orangehrm-login-button")).click();
    }

    @AfterTest
    public void closeDriver() {
        driver.close();
    }

}
