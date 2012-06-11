/*
 *
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
package fi.vm.sade.tarjonta.ui.hakuera;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;

import fi.vm.sade.tarjonta.service.types.dto.HakueraDTO;

/**
 * Component for specifying the time range of applying for a Haku (Hakuerä).
 * There might be multiple time ranges.
 * 
 * @author markus
 *
 */
public class HakuaikaRange extends HorizontalLayout {
    
    private DateField hakuaikaStart;
    private DateField hakuaikaEnd;
    
    public HakuaikaRange() {
        hakuaikaStart = new DateField();
        hakuaikaEnd = new DateField();
        addComponent(hakuaikaStart);
        addComponent(hakuaikaEnd);
    }
    
    public DateField getHakuaikaStart() {
        return hakuaikaStart;
    }

    public DateField getHakuaikaEnd() {
        return hakuaikaEnd;
    }
   
    /**
     * Populating the date fields according to the model.
     * 
     * @param model 
     */
    public void populateVoimassaoloDates(HakueraDTO model) {
        populateDate(model.getHaunAlkamisPvm(), hakuaikaStart);
        populateDate(model.getHaunLoppumisPvm(), hakuaikaEnd);
    }
    
    /**
     * Populating the model with dates according to the date fields.
     * 
     * @param model
     */
    public void getVoimassaoloDates (HakueraDTO model) {
        model.setHaunAlkamisPvm(getDate(hakuaikaStart));
        model.setHaunLoppumisPvm(getDate(hakuaikaEnd));
    }
    
    //The wsdl2java generates an XMLGregorianCalendar so a conversion has to be done.
    private void populateDate(XMLGregorianCalendar cal, DateField dateField) {
        if (cal != null) {
            dateField.setValue(cal.toGregorianCalendar().getTime());
        } else {
            dateField.setValue(null);
        }
    }
    
    //The wsdl2java generates an XMLGregorianCalendar so a conversion has to be done.
    private XMLGregorianCalendar getDate(DateField dateField) {
        Date origDate = (Date)(dateField.getValue());
        XMLGregorianCalendar date2 = null;
        if (origDate != null) {
            try {
                GregorianCalendar c = new GregorianCalendar();
                c.setTime(origDate);
                date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            } catch (Exception ex) {
                
            }
        }
        return date2;
    }

}
