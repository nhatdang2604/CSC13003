package utils;

import org.apache.poi.ss.usermodel.*;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;

public class ExcelReader {

    private static final String DATASET_NAME = "dataset";
    private static final String DATASET_PATH = "src/main/resources/" + DATASET_NAME + "xlsx";

    @DataProvider(name="dataset")
    public String[][] getData(Method m) throws Exception {

        //Open file;
        String sheetName = m.getName();
        File file = new File(DATASET_PATH);
        FileInputStream fis = new FileInputStream(file);
        Workbook workbook = WorkbookFactory.create(fis);
        Sheet sheet = workbook.getSheet(sheetName);

        //Get row and col
        int totalRows = sheet.getLastRowNum();
        Row row = sheet.getRow(0);
        int totalCols = row.getLastCellNum();

        //Read data and parse to array
        DataFormatter formatter = new DataFormatter();
        String[][] data = new String[totalRows][totalCols];
        for (int i = 1; i <= totalRows; ++i) {
            for (int j = 1; j <= totalCols; ++j) {
                data[i-1][j-1] = formatter.formatCellValue(sheet.getRow(i).getCell(j));
            }
        }

        return data;
    }

}
