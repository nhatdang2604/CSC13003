package test.auth;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeTest;

import java.time.Duration;

public class LoginTest {

    public static WebDriver driver;

    private static final String IP = "localhost";
    private static final String PORT = "8088";

    private static final String BASE_URL = "http://" + IP + ":" + PORT + "/orangehrm-5.1/web/index.php";
    private static final String LOGIN_URL = BASE_URL + "/auth/login";

    @BeforeTest
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        driver.get(LOGIN_URL);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("input[name='username']")));

        driver.findElement(By.cssSelector("input[name='username']")).sendKeys("admin");
        driver.findElement(By.cssSelector("input[name='password']")).sendKeys("Dangkl123812010@");
        driver.findElement(By.className("orangehrm-login-button")).click();
    }

}
