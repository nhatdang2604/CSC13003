package dbunit;

import org.dbunit.DBTestCase;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlMetadataHandler;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.sql.*;
import java.time.Year;

public class TestSuite extends DBTestCase {

    public static final String DATASET_PATH = "dataset.xml";

    //Database connection
    private Connection connection;

    //Database configuration
    private static final int BATCH_SIZE = 97;

    //Connection profile
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String IP = "127.0.0.1";                   //editable
    private static final String PORT = "3306";                      //editable
    private static final String SCHEMA_NAME = "orangehrm_test";    //editable

    private static final String CONNECTION_STR = "jdbc:mysql://" + IP + ":" + PORT + "/" + SCHEMA_NAME + "?useSSL=false";
    private static final String USERNAME = "root";  //editable
    private static final String PASSWORD = "";      //editable


    //Initialize and connect to the database with the given profile
    public TestSuite(String name) {
        super(name);

        //Setup connection profile
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, DRIVER_CLASS);
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, CONNECTION_STR);
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, USERNAME);
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, PASSWORD);
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_SCHEMA, SCHEMA_NAME);

        try {
            connection = DriverManager.getConnection(CONNECTION_STR, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    //Load the default dataset
    @Override
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(new FileInputStream(DATASET_PATH));
    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.REFRESH;
    }

    @Override
    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.DELETE_ALL;
    }

    @Override
    protected void setUpDatabaseConfig(DatabaseConfig config) {
        config.setProperty(DatabaseConfig.PROPERTY_BATCH_SIZE, BATCH_SIZE); //setup batch size for sql execution
        config.setProperty(DatabaseConfig.PROPERTY_METADATA_HANDLER, new MySqlMetadataHandler());
        config.setProperty(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, true);
    }

    @Test
    public void testQuery() throws SQLException {

        //Variable to store the result after querying
        Integer result = 0;

        //Parameters for the query, because the is no sensitive information
        //  => there is no need to use binding parameter technique
        String thisYear = Year.now().toString();
        final int maxLeaveDayLength = 12;

        //JDBC's statement to query
        Statement statement = connection.createStatement();

        String sql =
                "SELECT COUNT(*) " +
                "FROM " +
                        "(" +
                            "SELECT DISTINCT e1.emp_number " +
                            "FROM hs_hr_employee e1 " +
                                "JOIN ohrm_leave_request lr1 ON e1.emp_number = lr1.emp_number " +
                                "JOIN ohrm_leave l1 ON l1.leave_request_id = lr1.id " +
                                "JOIN ohrm_leave_type lt1 ON l1.leave_type_id = lt1.id " +
                                "JOIN ohrm_leave_status ls1 ON ls1.status = l1.status " +
                            "WHERE ls1.name IN ('TAKEN', 'SCHEDULED') AND YEAR(l1.date) = "  + thisYear + " " +
                            "GROUP BY e1.emp_number " +
                            "HAVING SUM(l1.length_days) > " + maxLeaveDayLength + " " +
                        ") as temp ";

        //Try to measure the execution time, in milisecond
        long start = System.currentTimeMillis();
        ResultSet rs = statement.executeQuery(sql);
        long end = System.currentTimeMillis();

        //Parse the answer to the result variable
        while (rs.next()) {
            result = rs.getInt(1);
        }

        //clear the resource after using
        rs.close();

        //Assert if the answer is TLE: the execution time is larger 1 second
        Assert.assertTrue(result < 1000L);

        //Output the result
        System.out.println("Number of employees who leave more than " + maxLeaveDayLength + " days in this year: " + result);
        System.out.println("Execution time: "+ (end-start) + "ms");

    }
}
