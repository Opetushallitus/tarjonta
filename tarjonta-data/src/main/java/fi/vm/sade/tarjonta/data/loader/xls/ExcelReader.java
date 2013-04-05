/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.data.loader.xls;

import fi.vm.sade.tarjonta.data.dto.AbstractReadableRow;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author jani
 */
public class ExcelReader<T extends AbstractReadableRow> {
    private final Logger log = LoggerFactory.getLogger(ExcelReader.class);
    private int maxReadRows = 100;
    private static final String DATE_FORMAT = "dd.MM.yyyy";
    private Class dataObjectClass;
    private HSSFWorkbook workbook;
    //Data mapping config, map DTO property to a excel row column using reflection.
    private Column[] columns;

    public ExcelReader(final Class dataObjectClass, final Column[] columns, final int maxReadRows) {
        if (dataObjectClass == null) {
            throw new RuntimeException("Class instance cannot be null");
        }

        if (columns == null) {
            throw new RuntimeException("Column property String array cannot be null");
        }

        this.dataObjectClass = dataObjectClass;
        this.columns = columns;
        this.maxReadRows = maxReadRows;
    }

    public Set<T> read(final String pathToFile, final boolean verbose) throws IOException {
        if (pathToFile == null) {
            throw new RuntimeException("Missing file path.");
        }

        final FileInputStream fileInputStream = new FileInputStream(pathToFile);

        try {
            log.info("Read file : " + pathToFile);
            log.info("Read bytes : " + fileInputStream.available());
            return read(fileInputStream, verbose);
        } catch (final IOException ex) {
            log.error("File load failed", ex);
        } finally {
            try {
                fileInputStream.close();
            } catch (final Exception ex) {
                log.warn("File input stream close failed.");
            }
        }

        return null;
    }

    public Set<T> read(final InputStream excelAsInputStream, final boolean verbose) throws IOException {
        final HSSFWorkbook hssfWorkbook = new HSSFWorkbook(excelAsInputStream);
        this.setWorkbook(hssfWorkbook);
        excelAsInputStream.close();
        final HSSFSheet sheet = getWorkbook().getSheetAt(0); // first sheet

        if (verbose) {
            log.info("Read rows : " + maxReadRows);
            log.info("Max rows available : " + sheet.getLastRowNum());
        }

        final Set<T> list = new HashSet<T>();
        boolean stop = false;

        for (int rowNumber = 1; rowNumber <= maxReadRows; rowNumber++) {
            if (stop || rowNumber > sheet.getLastRowNum()) {
                break;
            }

            T dto;
            try {
                dto = (T) dataObjectClass.newInstance();
            } catch (final Exception ex) {
                throw new RuntimeException("Initialization of the data object instance failed.");
            }
            HSSFCell cell = null;
            HSSFRow currentRow = null;

            for (int i = 0; i < columns.length; i++) {
                try {
                    currentRow = sheet.getRow(rowNumber);
                    if (currentRow == null) {
                        stop = true;
                        break;
                    }

                    cell = currentRow.getCell(i);

                    final String cellValue = getCellValueAsString(cell);

                    if (cellValue == null || cellValue.isEmpty() || cellValue.trim().length() < 1) {
                        if (verbose) {
                            log.debug("Missing data column {} {}", rowNumber, i);
                        }
                        continue;
                    }

                    if (columns[i].getKey() == null) {
                        if (verbose) {
                            log.debug("Skip column {} {}", rowNumber, i);
                        }
                        continue;
                    }

                    switch (columns[i].getType()) {
                        case INTEGER:
                            if (cellValue.indexOf(".") == -1) {
                                BeanUtils.setProperty(dto, columns[i].getKey(), cellValue);
                            } else {
                                BeanUtils.setProperty(dto, columns[i].getKey(), cellValue.substring(0, cellValue.indexOf(".")));
                            }
                            break;
                        case STRING:
                            BeanUtils.setProperty(dto, columns[i].getKey(), cellValue);
                            break;
                        default:
                            if (verbose) {
                                log.debug("Found an unknown type " + cellValue);
                            }
                            //do nothing
                            break;
                    }
                } catch (final Exception ex) {
                    log.error("Excel reader failed.", ex);
                }
            }

            if (!stop && !dto.isEmpty()) {
                list.add(dto);
            }
        }

        return list;
    }

    private String getCellValueAsString(final HSSFCell cell) {
        if (cell == null) {
            return null;
        }
        try {
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                final Date cellDate = cell.getDateCellValue();
                final SimpleDateFormat smp = new SimpleDateFormat(DATE_FORMAT);
                return smp.format(cellDate);
            } else {
                cell.setCellType(Cell.CELL_TYPE_STRING);
                return cell.getStringCellValue();
            }
        } catch (final Exception exp) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            return cell.getStringCellValue();
        }
        //return cell.getCellType() == Cell.CELL_TYPE_NUMERIC ? cell.getNumericCellValue() + "" : cell.getStringCellValue();
    }

    /**
     * @return the workbook
     */
    public HSSFWorkbook getWorkbook() {
        return workbook;
    }

    /**
     * @param workbook the workbook to set
     */
    public void setWorkbook(final HSSFWorkbook workbook) {
        this.workbook = workbook;
    }
}
