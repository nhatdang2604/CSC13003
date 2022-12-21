package test.flow;

//import junit.framework.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import test.auth.BaseTest;
import test.flow.customer.CustomerFlow;
import test.flow.project.ProjectFlow;
import utils.ExcelReader;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class AssignProjectFlow extends BaseTest {

    private CustomerFlow customerFlow;
    private ProjectFlow projectFlow;
    @BeforeMethod
    private void init() {
        customerFlow = new CustomerFlow(driver, wait);
        projectFlow = new ProjectFlow(driver, wait);
    }

    @Test(dataProviderClass = ExcelReader.class, dataProvider = ExcelReader.DATASET_NAME)
    public void assignProjectTest(String[] testRecord) throws Exception {

        customerFlow.createCustomer(testRecord);
//        projectFlow.createProject();
    }

    @AfterTest
    public void tearDown() throws Exception {

        Connection connection = getConnection(CONNECTION_STR, USERNAME, PASSWORD);
        Statement statement = connection.createStatement();

        customerFlow.clear(statement);

        statement.close();
    }

}
