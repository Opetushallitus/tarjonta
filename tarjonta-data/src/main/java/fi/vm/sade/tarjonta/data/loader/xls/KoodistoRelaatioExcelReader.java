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
    private final Logger log = LoggerFactory.getLogger(KoodistoRelaatioExcelReader.class);

    @Autowired
    private TarjontaDataKoodistoHelper koodistoHelper;

    private HSSFWorkbook workbook;

    public List<KoodiRelaatio> readKoodiRelaatioExcel(final String pathToFile) throws IOException {
        final List<KoodiRelaatio> koodiRelaatios = new ArrayList<KoodiRelaatio>();
        if (pathToFile == null) {
            throw new RuntimeException("Missing file path.");
        }

        final FileInputStream fileInputStream = new FileInputStream(pathToFile);

        workbook = new HSSFWorkbook(fileInputStream);
        final HSSFSheet sheet = workbook.getSheetAt(0);
        final List<String> headers = new ArrayList<String>();
        rows: for (int rowNumber = 0; rowNumber <= sheet.getLastRowNum(); rowNumber++) {
            final HSSFRow currentRow = sheet.getRow(rowNumber);
            if (rowNumber == 0) {
                for (int cellCount = 0; cellCount <= currentRow.getLastCellNum(); cellCount++) {
                    final String koodistoNimi = getCellValueAsString(currentRow.getCell(cellCount));
                    if (koodistoNimi != null) {
                        final String koodistoUri = DataUtils.createKoodistoUriFromName(koodistoNimi);
                        if (koodistoHelper.isKoodisto(koodistoUri)) {
                            headers.add(koodistoUri);
                        } else {
                            log.warn("Koodisto not found with koodistoUri [{}], skipping this column in file [{}]", koodistoUri, pathToFile);
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
                    final String alaKoodistoUri = headers.get(cellCount);
                    if (!StringUtils.equals(alaKoodistoUri, "-1")) {
                        final KoodiRelaatio relaatio = new KoodiRelaatio();
                        relaatio.setYlaArvoKoodisto(headers.get(0));
                        final String ylaArvo = getCellValueAsString(currentRow.getCell(0));
                        if (ylaArvo != null && ylaArvo.trim().length() > 0) {
                            relaatio.setKoodiYlaArvo(ylaArvo);
                            relaatio.setAlaArvoKoodisto(alaKoodistoUri);
                            relaatio.setKoodiAlaArvo(getCellValueAsString(currentRow.getCell(cellCount)));
                            koodiRelaatios.add(relaatio);
                        }
                    }
                }
            }
        }


        return koodiRelaatios;
    }

    private String getCellValueAsString(final HSSFCell cell) {
        if (cell == null) {
            return null;
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        return cell.getStringCellValue();
    }
}
