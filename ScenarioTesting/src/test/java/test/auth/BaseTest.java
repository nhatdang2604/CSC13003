package test.auth;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import utils.ExcelReader;

import java.sql.*;
import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static utils.ExcelReader.DATASET_PATH;
import static utils.ExcelReader.RESOURCE_PATH;

public class BaseTest {

    protected WebDriver driver;

    private static final String IP = "localhost";
    private static final String PORT = "8088";

    public static final String BASE_URL = "http://" + IP + ":" + PORT + "/orangehrm-5.1/web/index.php";
    private static final String LOGIN_URL = BASE_URL + "/auth/login";
    private static final String EMPLOYEE_LIST_URL = BASE_URL + "/pim/viewEmployeeList";


    //SQL stuffs
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String DBS_IP = "127.0.0.1";                   //editable
    private static final String DBS_PORT = "3306";                      //editable
    private static final String SCHEMA_NAME = "orangehrm_test";    //editable

    public static final String CONNECTION_STR = "jdbc:mysql://" + DBS_IP + ":" + DBS_PORT + "/" + SCHEMA_NAME + "?useSSL=false";
    public static final String USERNAME = "root";  //editable
    public static final String PASSWORD = "";      //editable

    //Dataset's stuff
    private static final String EMPLOYEE_DATASET_SHEET_NAME = "employee";

    //XPaths
    private static final String ADD_EMPLOYEE_BUTTON_XPATH = "/html/body/div/div[1]/div[2]/div[2]/div/div[2]/div[1]/button";
    private static final String FIRSTNAME_FIELD_XPATH = "/html/body/div/div[1]/div[2]/div[2]/div/div/form/div[1]/div[2]/div[1]/div[1]/div/div/div[2]/div[1]/div[2]/input";
    private static final String MIDDLENAME_FIELD_XPATH = "/html/body/div/div[1]/div[2]/div[2]/div/div/form/div[1]/div[2]/div[1]/div[1]/div/div/div[2]/div[2]/div[2]/input";
    private static final String LASTNAME_FIELD_XPATH = "/html/body/div/div[1]/div[2]/div[2]/div/div/form/div[1]/div[2]/div[1]/div[1]/div/div/div[2]/div[3]/div[2]/input";
    private static final String EMPLOYEE_ID_FIELD_XPATH = "/html/body/div/div[1]/div[2]/div[2]/div/div/form/div[1]/div[2]/div[1]/div[2]/div/div/div[2]/input";
    private static final String LOGIN_CHECKBOX_XPATH = "/html/body/div/div[1]/div[2]/div[2]/div/div/form/div[1]/div[2]/div[2]/div/label/span";
    private static final String LOGIN_ENABLE_RADIOBOX_XPATH = "/html/body/div/div[1]/div[2]/div[2]/div/div/form/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[1]/div[2]/div/label/input";
    private static final String LOGIN_DISABLE_RADIOBOX_XPATH = "/html/body/div/div[1]/div[2]/div[2]/div/div/form/div[1]/div[2]/div[3]/div/div[2]/div/div[2]/div[2]/div[2]/div/label";
    private static final String LOGIN_USERNAME_FIELD_XPATH = "/html/body/div/div[1]/div[2]/div[2]/div/div/form/div[1]/div[2]/div[3]/div/div[1]/div/div[2]/input";
    private static final String LOGIN_PASSWORD_FIELD_XPATH = "/html/body/div/div[1]/div[2]/div[2]/div/div/form/div[1]/div[2]/div[4]/div/div[1]/div/div[2]/input";
    private static final String LOGIN_PASSWORD2_FIELD_XPATH = "/html/body/div/div[1]/div[2]/div[2]/div/div/form/div[1]/div[2]/div[4]/div/div[2]/div/div[2]/input";
    private static final String SAVE_BUTTON_XPATH = "/html/body/div/div[1]/div[2]/div[2]/div/div/form/div[2]/button[2]";
    private static final String INFO_SAVE_BUTTON_XPATH = "/html/body/div/div[1]/div[2]/div[2]/div/div/div/div[2]/div[1]/form/div[4]/button";


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

    //Login with administration account
    private void login() {
        driver.get(LOGIN_URL);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name='username']")));

        driver.findElement(By.cssSelector("input[name='username']")).sendKeys("admin");
        driver.findElement(By.cssSelector("input[name='password']")).sendKeys("Dangkl123812010@");
        driver.findElement(By.className("orangehrm-login-button")).click();
    }

    //Add some employees for convenience testing
    private void addSomeEmployees() throws Exception {

        ExcelReader reader = new ExcelReader();
        String[][] employeeDatas = reader.getData(DATASET_PATH, EMPLOYEE_DATASET_SHEET_NAME);
        int size = employeeDatas.length;

        boolean employeeIdCustomable = true;
        boolean employeeCredentialEnable = true;

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        //Add all employees in file EMPLOYEE_DATASET_PATH
        for (String[] employeeData: employeeDatas) {

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(ADD_EMPLOYEE_BUTTON_XPATH)));
            WebElement element = driver.findElement(By.xpath(ADD_EMPLOYEE_BUTTON_XPATH));

            //Wait until the add employee button can be clicked, then click it
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();

            //Wait until the add employee form appeared
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(FIRSTNAME_FIELD_XPATH)));

            driver.findElement(By.xpath(FIRSTNAME_FIELD_XPATH)).sendKeys(employeeData[1]);
            driver.findElement(By.xpath(MIDDLENAME_FIELD_XPATH)).sendKeys(employeeData[2]);
            driver.findElement(By.xpath(LASTNAME_FIELD_XPATH)).sendKeys(employeeData[3]);

            //Using the default employee id or not
            if(employeeIdCustomable) {
                element = driver.findElement(By.xpath(EMPLOYEE_ID_FIELD_XPATH));
                element.sendKeys(Keys.CONTROL + "a");
                element.sendKeys(Keys.DELETE);
                element.sendKeys(employeeData[4]);
            }

            //Create an account for employee or not
            if (employeeCredentialEnable) {
                element = driver.findElement(By.xpath(LOGIN_CHECKBOX_XPATH));
                wait.until(ExpectedConditions.elementToBeClickable(element));
                element.click();

                //Wait for the credential form open
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(LOGIN_USERNAME_FIELD_XPATH)));

                //Fill the credential form
                driver.findElement(By.xpath(LOGIN_USERNAME_FIELD_XPATH)).sendKeys(employeeData[5]);
                driver.findElement(By.xpath(LOGIN_PASSWORD_FIELD_XPATH)).sendKeys(employeeData[6]);
                driver.findElement(By.xpath(LOGIN_PASSWORD2_FIELD_XPATH)).sendKeys(employeeData[7]);

                //Check the status for the account
                String accountStatus = employeeData[8].trim();
                if ("0".equals(accountStatus)) {
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(LOGIN_DISABLE_RADIOBOX_XPATH)));
                    element = driver.findElement(By.xpath(LOGIN_DISABLE_RADIOBOX_XPATH));
                    wait.until(ExpectedConditions.elementToBeClickable(element));
                    element.click();
                }

            }

            //Click the save button
            driver.findElement(By.xpath(SAVE_BUTTON_XPATH)).click();

            //After adding an employee, we will be redirected to that employee info page, wait for this page load
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(INFO_SAVE_BUTTON_XPATH)));

            //Return to the employee list page
            driver.get(EMPLOYEE_LIST_URL);
        }
    }

    @BeforeTest
    public void setUp() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        login();
        addSomeEmployees();
    }

    //Clear after testing
    @AfterTest
    public void clear() throws Exception {

        //Close the driver
        driver.close();

        //Delete the employee and user which has been added
        Connection connection = getConnection(CONNECTION_STR, USERNAME, PASSWORD);
        Statement statement = connection.createStatement();

        //Get the id of the employee first
        String sql =
                "SELECT e.emp_number " +
                "FROM hs_hr_employee e " +
                "WHERE e.employee_id LIKE 'TEST_%'";

        ResultSet rs = statement.executeQuery(sql);
        List<String> ids = new LinkedList();
        while (rs.next()) {
            ids.add(rs.getString(1));
        }

        //idSet = '(emp_1, emp_2, emp_3, ...)'
        String idSet = String.join( String.join(", ", ids), "(", ")");
        System.out.println(idSet);

        //Delete data from hs_hr_employee
        sql =
                "DELETE " +
                "FROM hs_hr_employee " +
                "WHERE emp_number IN " + idSet;

        System.out.println(sql);
        statement.addBatch(sql);

        //Delete data from ohrm_user
        sql =
                "DELETE " +
                "FROM ohrm_user " +
                "WHERE emp_number IN " + idSet;

        statement.addBatch(sql);
        System.out.println(sql);

        statement.executeBatch();

        statement.close();
    }
}
