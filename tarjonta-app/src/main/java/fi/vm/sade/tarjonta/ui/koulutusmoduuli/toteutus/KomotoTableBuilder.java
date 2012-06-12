
package fi.vm.sade.tarjonta.ui.koulutusmoduuli.toteutus;

import com.vaadin.data.Container;


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

/**
 *
 * @author Tuomas Katva
 */
public class KomotoTableBuilder {

    KomotoTable tableToBuild; 
    
     public static KomotoTableBuilder komotoTable(String tableTilaType) {
         
         return new KomotoTableBuilder(tableTilaType);
     }
     
     private KomotoTableBuilder(String tilaType) {
         tableToBuild = new KomotoTable(tilaType);
     }
     
     public KomotoTableBuilder withLabel(String label) {
         tableToBuild.addLabelToHdr(label);
         return this;
     }
     
     public KomotoTableBuilder withHeightAndWidth(String width,String height) {
         tableToBuild.setWidth(width);
         return this;
     }
     
     public KomotoTableBuilder withButton(String btnLabel) {
         tableToBuild.addButtonToHdr(btnLabel);
         return this;
     }
     
     public KomotoTableBuilder withTable(Container dataContainer,String[] visibleColumns) {
         tableToBuild.addTable(dataContainer, visibleColumns);
         return this;
     }
     
     public KomotoTableBuilder withSearchAllButton(String searchAllBtnLbl) {
         tableToBuild.addSelectAllButton(searchAllBtnLbl);
         return this;
     }
     
     public KomotoTable build() {
         return tableToBuild;
     }
     
}
