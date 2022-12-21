package utils;

import org.apache.poi.ss.usermodel.*;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;

public class ExcelReader {

    public static final String DATASET_NAME = "dataset";
//    private static final String DATASET_PATH = DATASET_NAME + ".xlsx";

    public static final String RESOURCE_PATH = "./src/main/resources/";
    public static final String DATASET_PATH = RESOURCE_PATH + DATASET_NAME + ".xlsx";

    //Read data from an Excel file with 'path' and 'sheet name'
    public String[][] getData(String path, String sheetName) throws Exception  {

        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        Workbook workbook = WorkbookFactory.create(fis);
        Sheet sheet = workbook.getSheet(sheetName);

        //Get row and col
        int totalRows = sheet.getPhysicalNumberOfRows();
        Row row = sheet.getRow(0);
        int totalCols = row.getLastCellNum();

        //Read data and parse to array
        DataFormatter formatter = new DataFormatter();
        String[][] data = new String[totalRows - 1][totalCols];
        for (int i = 0; i < totalRows - 1; ++i) {
            for (int j = 0; j < totalCols; ++j) {
                data[i][j] = formatter.formatCellValue(sheet.getRow(i + 1).getCell(j));
            }
        }

        return data;
    }

    @DataProvider(name=DATASET_NAME)
    public String[][] getData(Method m) throws Exception {

        //Open file;
        String sheetName = m.getName();
        String[][] result = getData(DATASET_PATH, sheetName);
        return result;
    }

}
