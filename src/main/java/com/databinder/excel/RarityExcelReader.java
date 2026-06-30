package com.databinder.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RarityExcelReader {

    public List<RarityExcelModel> readRaritiesFromSheet(String fileName, String sheetName) throws IOException {
        System.out.println("RarityExcelReader: Reading file '" + fileName + "'");
        
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IOException("File not found in classpath: " + fileName);
            }
            
            System.out.println("RarityExcelReader: File found, reading...");
            
            try (Workbook workbook = new XSSFWorkbook(inputStream)) {
                Sheet sheet = workbook.getSheet(sheetName);
                
                if (sheet == null) {
                    throw new IllegalArgumentException("Sheet '" + sheetName + "' not found");
                }
                
                System.out.println("RarityExcelReader: Sheet found: " + sheetName);
                
                List<RarityExcelModel> rarities = new ArrayList<>();
                boolean foundEmptyRow = false;
                
                for (Row row : sheet) {
                    if (row == null) {
                        continue;
                    }
                    
                    // Skip header row (row 0)
                    if (row.getRowNum() == 0) {
                        System.out.println("RarityExcelReader: Skipping header row");
                        continue;
                    }
                    
                    // Check if row is completely empty (all cells are empty)
                    boolean isEmptyRow = true;
                    for (int i = 0; i < 10; i++) { // Check first 10 columns
                        Cell cell = row.getCell(i);
                        if (cell != null && cell.getCellType() != CellType.BLANK) {
                            String value = getCellValueAsString(cell);
                            if (!value.trim().isEmpty()) {
                                isEmptyRow = false;
                                break;
                            }
                        }
                    }
                    
                    // If row is empty, stop processing (break out of the loop)
                    if (isEmptyRow) {
                        System.out.println("RarityExcelReader: Empty row detected at row " + row.getRowNum() + 
                            ", stopping...");
                        foundEmptyRow = true;
                        break; // Stop reading further rows
                    }
                    
                    // Read data from columns A, B, C, D (index 0, 1, 2, 3)
                    String name = getCellValueAsString(row.getCell(0));   // Column A - Rarity Name
                    String code = getCellValueAsString(row.getCell(1));   // Column B - Code
                    String slug = getCellValueAsString(row.getCell(2));   // Column C - Slug
                    String hierarchy = getCellValueAsString(row.getCell(3)); // Column D - Hierarchy
                    
                    // Skip if name is empty (but continue to next row, don't break)
                    if (name == null || name.trim().isEmpty()) {
                        System.out.println("RarityExcelReader: Skipping row " + row.getRowNum() + " - empty name");
                        continue;
                    }
                    
                    RarityExcelModel rarity = new RarityExcelModel();
                    rarity.setRarityName(name);
                    rarity.setRarityCode(code);
                    rarity.setRaritySlug(slug);
                    rarity.setHierarchy(hierarchy);
                    
                    rarities.add(rarity);
                    System.out.println("RarityExcelReader: Row " + row.getRowNum() + 
                        ": Name='" + name + 
                        "', Code='" + code + 
                        "', Slug='" + slug + 
                        "', Hierarchy='" + hierarchy + "'");
                }
                
                if (!foundEmptyRow) {
                    System.out.println("RarityExcelReader: No empty row found, read all rows");
                }
                
                System.out.println("RarityExcelReader: Read " + rarities.size() + " rarities");
                return rarities;
            }
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                double numValue = cell.getNumericCellValue();
                if (numValue == (long) numValue) {
                    return String.valueOf((long) numValue);
                }
                return String.valueOf(numValue);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return String.valueOf(cell.getNumericCellValue());
                } catch (Exception e) {
                    return cell.getCellFormula();
                }
            default:
                return "";
        }
    }
}