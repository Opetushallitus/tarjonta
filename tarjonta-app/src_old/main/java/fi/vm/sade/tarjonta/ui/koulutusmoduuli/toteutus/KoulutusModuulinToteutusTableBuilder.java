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


package fi.vm.sade.tarjonta.ui.koulutusmoduuli.toteutus;

import com.vaadin.data.Container;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;




/**
 *
 * @author Tuomas Katva
 */
public class KoulutusModuulinToteutusTableBuilder {

    KoulutusModuulinToteutusTable tableToBuild; 
    
     public static KoulutusModuulinToteutusTableBuilder komotoTable(String tableTilaType, boolean clickable) {
         
         return new KoulutusModuulinToteutusTableBuilder(tableTilaType, clickable);
     }
     
     private KoulutusModuulinToteutusTableBuilder(String tilaType, boolean clickable) {
         tableToBuild = new KoulutusModuulinToteutusTable(tilaType, clickable);
     }
     
     public KoulutusModuulinToteutusTableBuilder withLabel(String label) {
         tableToBuild.addLabelToHdr(label);
         return this;
     }
     
     public KoulutusModuulinToteutusTableBuilder withHeightAndWidth(String width,String height) {
         tableToBuild.setWidth(width);
         return this;
     }
     
     public KoulutusModuulinToteutusTableBuilder withButton(String btnLabel,boolean linkStyle) {
         tableToBuild.addButtonToHdr(btnLabel,linkStyle);
         return this;
     }
     
     public KoulutusModuulinToteutusTableBuilder withAddTableContainerProperty(String propertyId,Class clazz) {
         if (tableToBuild.getKomotoTable() != null) {
             tableToBuild.getKomotoTable().addContainerProperty(propertyId, clazz, null);
         }
         return this;
     }
     
     public KoulutusModuulinToteutusTableBuilder withColumnGenerator(String propertyId,Table.ColumnGenerator generator) {
         tableToBuild.addColumnGenerator(propertyId, generator);
         return this;
     }
     
     public KoulutusModuulinToteutusTableBuilder withTable(Container dataContainer,String[] visibleColumns) {
         tableToBuild.addTable(dataContainer, visibleColumns);
         return this;
     }
     
     public KoulutusModuulinToteutusTableBuilder withFieldFactory(TableFieldFactory ff) {
         tableToBuild.addFieldFactory(ff);
         return this;
     }
     
     public KoulutusModuulinToteutusTableBuilder withSearchAllButton(String searchAllBtnLbl,boolean linkStyle) {
         tableToBuild.addSelectAllButton(searchAllBtnLbl,linkStyle);
         return this;
     }
     
     public KoulutusModuulinToteutusTable build() {
         return tableToBuild;
     }
     
}
