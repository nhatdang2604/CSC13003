package test.flow.customer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ExcelReader;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static test.auth.BaseTest.BASE_URL;
import static utils.ExcelReader.DATASET_PATH;

public class CustomerFlow {

    //URL
    private static final String CUSTOMER_URL = BASE_URL + "/time/viewCustomers";

    //Sheet which store the customer's information
    private static final String CUSTOMER_SHEET_NAME = "customer";
    private WebDriverWait wait;
    private WebDriver driver;

    private String[][] customers;

    //XPaths
    private static final String ADD_CUSTOMER_BUTTON_XPATH = "/html/body/div/div[1]/div[2]/div[2]/div/div/div[1]/div/button";
    private static final String NAME_FIELD_XPATH = "/html/body/div/div[1]/div[2]/div[2]/div/div/form/div[1]/div/div[2]/input";
    private static final String DESCRIPTION_FIELD_XPATH = "/html/body/div/div[1]/div[2]/div[2]/div/div/form/div[2]/div/div[2]/textarea";
    private static final String SAVE_BUTTON_XPATH = "/html/body/div/div[1]/div[2]/div[2]/div/div/form/div[3]/button[2]";

    public CustomerFlow(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void createCustomer(String[] testCaseData) throws Exception {

        //Goto the customer page
        driver.get(CUSTOMER_URL);

        //Get the test case id from the test case
        String testCaseId = testCaseData[0];

        //Goto the `customer` sheet, get all the customer with the same test case id
        ExcelReader reader = new ExcelReader();
        customers = reader.getData(DATASET_PATH, CUSTOMER_SHEET_NAME);
        List<String[]> filteredCustomers = Arrays
                .stream(customers)
                .filter(customer -> customer[0].equals(testCaseId))
                .collect(Collectors.toList());

        for (String[] customerData: filteredCustomers) {

            //Wait until the add customer appeared
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(ADD_CUSTOMER_BUTTON_XPATH)));
            WebElement element = driver.findElement(By.xpath(ADD_CUSTOMER_BUTTON_XPATH));

            //Wait until the add customer button can be clicked, then click it
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();

            //Wait until the add customer form appeared
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(NAME_FIELD_XPATH)));

            driver.findElement(By.xpath(NAME_FIELD_XPATH)).sendKeys(customerData[2]);
            element = driver.findElement(By.xpath(DESCRIPTION_FIELD_XPATH));
            element.sendKeys(customerData[3]);

            //Click the save button
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(SAVE_BUTTON_XPATH)));
            element = driver.findElement(By.xpath(SAVE_BUTTON_XPATH));
            wait.until(ExpectedConditions.elementToBeClickable(element));

            //Don't know why, but the submit button can not be clicked with just a single click
            try {
                for (int i = 0; i < 1000; ++i) {
                    element.click();
                }
            } catch (Exception e) {
                //do nothing
            }
        }

    }

    public void clear(Statement statement) throws Exception {

        //Get all the name of the customers, the name would have the quote outside
        Set<String> names = new HashSet<>();
        for (String[] customerData: customers) {
            names.add(String.join(customerData[2], "'", "'"));
        }

        //nameSet = ('John', 'James', ...)
        String nameSet = String.join(String.join(", ", names), "(", ")");

        //Delete data from ohrm_user
        String sql =
                "DELETE " +
                "FROM ohrm_customer " +
                "WHERE name IN " + nameSet;

        System.out.println(sql);

        statement.executeUpdate(sql);
    }
}
