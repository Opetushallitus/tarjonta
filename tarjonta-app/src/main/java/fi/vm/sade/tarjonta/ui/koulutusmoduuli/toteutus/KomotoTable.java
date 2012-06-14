package fi.vm.sade.tarjonta.ui.koulutusmoduuli.toteutus;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliTila;
import com.vaadin.ui.Table;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.data.Container;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.themes.BaseTheme;


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
public class KomotoTable extends CustomComponent {
        
        private GridLayout tableGrid;
        private final float hdrRowLesserRatio = 0.3f;
        private final float hdrRowHigherRatio = 1.7f;
        private Label tableHdr;
        private Button tableButton;
        private Button selectAllButton;
        private Table komotoList = new Table();
        
        //Should this be enum ?
        private KoulutusmoduuliTila tila;
        
        //TODO: KomotoTable must listen to blackboard events which 
        //tells about changes in komoto state table removes or adds
        //komotos to it depending on which state it is interested on
        
        protected KomotoTable(KoulutusmoduuliTila tableTila) {
            tableGrid = new GridLayout(2, 3);
            tableGrid.setSizeFull();
            tila = tableTila;
            
            setCompositionRoot(tableGrid);
        }
               
        protected void addLabelToHdr(String labelStr) {
            tableHdr = new Label(labelStr);
            tableGrid.addComponent(tableHdr, 0, 0);
            tableGrid.setComponentAlignment(tableHdr, Alignment.BOTTOM_LEFT);
        }
        
        protected void addFieldFactory(TableFieldFactory tableFieldFactory) {
            if (komotoList != null) 
            komotoList.setTableFieldFactory(tableFieldFactory);
            
        }
        
        
        
        protected void addColumnGenerator(String propertyName,Table.ColumnGenerator generator) {
            if (komotoList != null)
            komotoList.addGeneratedColumn(propertyName, generator);
        }
        
        protected void addSelectAllButton(String selectAllButtonLbl,boolean linkStyle) {
            selectAllButton = new Button(selectAllButtonLbl);
            if (linkStyle) {
                selectAllButton.setStyleName(BaseTheme.BUTTON_LINK);
            }
            tableGrid.addComponent(selectAllButton, 0, 1);
            tableGrid.setComponentAlignment(selectAllButton, Alignment.BOTTOM_LEFT);
        }
        
        protected void addButtonToHdr(String buttonLabel, boolean linkStyle) {
            tableButton = new Button(buttonLabel);
            if (linkStyle) {
            tableButton.setStyleName(BaseTheme.BUTTON_LINK);
            }
            tableGrid.addComponent(tableButton, 1, 0);
            tableGrid.setComponentAlignment(tableButton, Alignment.BOTTOM_RIGHT);
        }
        
        protected void addTable(Container tableContainer, String[] visibleColumns) {
            
            komotoList.setContainerDataSource(tableContainer);
            komotoList.setVisibleColumns(visibleColumns);
            komotoList.setSizeFull();
            
            tableGrid.addComponent(komotoList,0, 2, 1, 2);
            
        }
        
        public Table getKomotoTable() {
            if (komotoList != null) {
            return komotoList;
            } else {
                return null;
            }
        }
        
        public Button getTableButton() {
            if (tableButton != null) {
                return tableButton;
            } else {
                return null;
            }
        }
        @Override
        public void setWidth(String width) {
            tableGrid.setWidth(width);
        }
        @Override
        public void setHeight(String height) {
            tableGrid.setHeight(height);
        }
}
