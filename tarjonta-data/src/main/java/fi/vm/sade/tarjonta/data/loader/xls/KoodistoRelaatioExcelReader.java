package fi.vm.sade.tarjonta.data.loader.xls;

import fi.vm.sade.tarjonta.data.dto.KoodiRelaatio;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: Tuomas Katva
 * Specific class for creating KoodistoRelaatio-objects
 * Date: 19.2.2013
 * Time: 17:20
 */
public class KoodistoRelaatioExcelReader {

    private HSSFWorkbook workbook;
    private static Logger log = LoggerFactory.getLogger(KoodistoRelaatioExcelReader.class);

    public List<KoodiRelaatio> readKoodiRelaatioExcel(final String pathToFile) throws IOException {
        List<KoodiRelaatio> koodiRelaatios = new ArrayList<KoodiRelaatio>();
        if (pathToFile == null) {
            throw new RuntimeException("Missing file path.");
        }

        FileInputStream fileInputStream = new FileInputStream(pathToFile);

        workbook = new HSSFWorkbook(fileInputStream);
        HSSFSheet sheet = workbook.getSheetAt(0);
        List<String> headers = new ArrayList<String>();
        for (int rowNumber = 0;rowNumber <= sheet.getLastRowNum(); rowNumber ++) {
            HSSFRow currentRow = sheet.getRow(rowNumber);
           if (rowNumber == 0) {

              for (int cellCount = 0; cellCount <= currentRow.getLastCellNum(); cellCount ++) {
                  String header = getCellValueAsString(currentRow.getCell(cellCount));
                  if (header != null) {
                  headers.add(header);
                  }
              }

           } else {
               for (int cellCount = 0; cellCount < headers.size(); cellCount ++) {
                   if (cellCount > 0) {
                       KoodiRelaatio relaatio = new KoodiRelaatio();
                       relaatio.setYlaArvoKoodisto(headers.get(0));
                       String ylaArvo = getCellValueAsString(currentRow.getCell(0));
                       if (ylaArvo != null && ylaArvo.trim().length() > 0) {
                       relaatio.setKoodiYlaArvo(ylaArvo);
                       relaatio.setAlaArvoKoodisto(headers.get(cellCount));

                       relaatio.setKoodiAlaArvo(getCellValueAsString(currentRow.getCell(cellCount)));
                       koodiRelaatios.add(relaatio);
                       }
                   }
               }
           }
        }


        return koodiRelaatios;
    }

    private String getCellValueAsString(HSSFCell cell) {
        if (cell == null) {
            return null;
        }
         cell.setCellType(Cell.CELL_TYPE_STRING);
        return cell.getStringCellValue();
        //return cell.getCellType() == Cell.CELL_TYPE_NUMERIC ? cell.getNumericCellValue() + "" : cell.getStringCellValue();
    }
}
