package dbunit;

import org.dbunit.DBTestCase;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlMetadataHandler;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;

import java.io.FileInputStream;
import java.sql.*;

public class TestSuite extends DBTestCase {

    public static final String DATASET_PATH = "dataset.xml";

    //SQL stuffs
    private Connection connection;
    private Statement statement;

    //Database configuration
    private static final int BATCH_SIZE = 97;

    //Connection profile
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String IP = "127.0.0.1";                   //editable
    private static final String PORT = "3306";                      //editable
    private static final String SCHEMA_NAME = "orangehrm_mysql";    //editable

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
    public void testBasicQuery() throws SQLException {

        String actual = "";
        String expected = "abc";

        statement = connection.createStatement();
        String sql = "SELECT user_name FROM ohrm_login WHERE id = '12'";
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            actual = rs.getString("user_name");
        }
        rs.close();

        System.out.println("Actual: " + actual + "\r\n" + "Expected: " + expected);
        assertEquals(actual, expected);
        System.out.println("Passed");
    }
}
