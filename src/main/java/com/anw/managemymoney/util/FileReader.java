package com.anw.managemymoney.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;


public class FileReader {
	

	/**
	 * @param filePath
	 * @return List<List<String>> List of rows. Each rows contains List of columns
	 * @throws IOException
	 */
	 public static List<List<String>> readExcelFile(MultipartFile file) throws IOException {
	        List<List<String>> data = new ArrayList<>();

	        try (InputStream fis = file.getInputStream();
	             Workbook workbook = new HSSFWorkbook(fis)) { // Use HSSFWorkbook for .xls files

	            Sheet sheet = workbook.getSheetAt(0); // Assuming you want to read the first sheet

	            Iterator<Row> rowIterator = sheet.iterator();
	            while (rowIterator.hasNext()) {
	                Row row = rowIterator.next();
	                List<String> rowData = new ArrayList<>();

	                Iterator<Cell> cellIterator = row.cellIterator();
	                while (cellIterator.hasNext()) {
	                    Cell cell = cellIterator.next();

	                    switch (cell.getCellType()) {
	                        case STRING:
	                            rowData.add(cell.getStringCellValue());
	                            break;
	                        case NUMERIC:
	                            rowData.add(String.valueOf(cell.getNumericCellValue()));
	                            break;
	                        case BOOLEAN:
	                            rowData.add(String.valueOf(cell.getBooleanCellValue()));
	                            break;
	                        case BLANK:
	                            rowData.add("");
	                            break;
	                        // Handle other cell types as needed
	                        default:
	                            rowData.add("");
	                    }
	                }

	                data.add(rowData);
	            }
	        }

	        return data;
	    }
}
