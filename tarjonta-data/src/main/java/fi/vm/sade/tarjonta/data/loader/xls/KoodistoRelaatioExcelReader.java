package fi.vm.sade.tarjonta.data.loader.xls;

import fi.vm.sade.tarjonta.data.dto.KoodiRelaatio;
import fi.vm.sade.tarjonta.data.util.DataUtils;
import fi.vm.sade.tarjonta.data.util.TarjontaDataKoodistoHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Tuomas Katva
 * Specific class for creating KoodistoRelaatio-objects
 * Date: 19.2.2013
 * Time: 17:20
 */
@Service
public class KoodistoRelaatioExcelReader {
    @Autowired
    private TarjontaDataKoodistoHelper koodistoHelper;

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
        rows: for (int rowNumber = 0; rowNumber <= sheet.getLastRowNum(); rowNumber++) {
            HSSFRow currentRow = sheet.getRow(rowNumber);
            if (rowNumber == 0) {
                for (int cellCount = 0; cellCount <= currentRow.getLastCellNum(); cellCount++) {
                    String header = getCellValueAsString(currentRow.getCell(cellCount));
                    if (header != null) {
                        if (koodistoHelper.isKoodisto(DataUtils.createKoodiUriFromName(header))) {
                            headers.add(DataUtils.createKoodiUriFromName(header));
                        } else {
                            if (cellCount == 0) {
                                // ylaKoodisto not found, abort whole file
                                break rows;
                            }
                            // mark this koodisto to be skipped
                            headers.add("-1");
                        }
                    }
                }
            } else {
                for (int cellCount = 0; cellCount < headers.size(); cellCount++) {
                    // skip koodistos with "-1"
                    if (cellCount > 0 && !StringUtils.equals(headers.get(cellCount), "-1")) {
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
    }
}
