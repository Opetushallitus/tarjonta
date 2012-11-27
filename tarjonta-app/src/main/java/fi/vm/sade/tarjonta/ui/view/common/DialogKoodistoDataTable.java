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
package fi.vm.sade.tarjonta.ui.view.common;

import com.vaadin.data.Property;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jani Wilén
 */
public class DialogKoodistoDataTable<MODEL> extends DialogDataTable<MODEL> {

    private static final Logger LOG = LoggerFactory.getLogger(TwinColSelectKoodisto.class);
    private TarjontaUIHelper tarjontaUiHelper;
    private String[] koodistoColumns;

    public DialogKoodistoDataTable(Class objectItem, Collection<MODEL> data) {
        super(objectItem, data);
        tarjontaUiHelper = new TarjontaUIHelper();
    }

    public void setKoodistoColumns(String[] columns) {
        if (columns != null) {
            koodistoColumns = columns;
        }
    }

    @Override
    protected String formatPropertyValue(Object rowId, Object colId,
            Property property) {

        if (koodistoColumns != null && koodistoColumns.length > 0) {
            for (String koodistoColumnId : koodistoColumns) {
                final String columnId = (String) colId;
                final Object uriValues = property.getValue();

                if (columnId.equals(koodistoColumnId)) {
                    LOG.debug("found object : " + uriValues);
                    if (uriValues instanceof Collection) {
                        final Collection<String> values = (Collection<String>) uriValues;
                        Set nameValues = new HashSet(values.size());

                        for (String uri : values) {
                            String strData = validateOutputData(uri, getKoodistoData(uri));
                            nameValues.add(strData);
                        }

                        return nameValues.toString();
                    } else if (uriValues instanceof String) {
                        return validateOutputData((String) uriValues, getKoodistoData((String) uriValues));
                    } else {
                        LOG.warn("An unhandled data type was encountered : " + uriValues);
                    }
                }
            }
        }

        return super.formatPropertyValue(rowId, colId, property);
    }

    private String getKoodistoData(String uriWithVersion) {
        final String koodiNimi = tarjontaUiHelper.getKoodiNimi(uriWithVersion);
        LOG.debug("koodiNimi : " + koodiNimi);
        return koodiNimi;
    }

    private String validateOutputData(String uri, String strData) {
        if (strData != null && strData.isEmpty()) {
            // Fallback to uri value when no koodi result.
            return uri;
        }
        return strData;
    }
}
