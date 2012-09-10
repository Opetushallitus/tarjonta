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
import fi.vm.sade.tarjonta.model.dto.KoulutusTila;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliToteutusDTO;

import com.vaadin.ui.Table;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.themes.BaseTheme;

/**
 *
 * @author Tuomas Katva
 */
public class KoulutusModuulinToteutusTable extends CustomComponent {
        

        private GridLayout tableGrid;
        private final float hdrRowLesserRatio = 0.3f;
        private final float hdrRowHigherRatio = 1.7f;
        private Label tableHdr;
        private Button tableButton;
        private Button selectAllButton;
        private Table komotoList = new Table();
        private static int counter = 0;
        
        private boolean clickable = true;
        
        private KoulutusmoduuliToteutusEditView komotoEdit;
        
        //Should this be enum ?
        private KoulutusTila tila;
        
        //TODO: KomotoTable must listen to blackboard events which 
        //tells about changes in komoto state table removes or adds
        //komotos to it depending on which state it is interested on
        
        protected KoulutusModuulinToteutusTable(KoulutusTila tableTila, boolean clickable) {
            this.clickable = clickable;
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
            selectAllButton.setDebugId(createDebugId(selectAllButtonLbl));
            tableGrid.addComponent(selectAllButton, 0, 1);
            tableGrid.setComponentAlignment(selectAllButton, Alignment.BOTTOM_LEFT);
        }
        
        protected void addButtonToHdr(String buttonLabel, boolean linkStyle) {
            tableButton = new Button(buttonLabel);
            if (linkStyle) {
            tableButton.setStyleName(BaseTheme.BUTTON_LINK);
            }
            tableButton.setDebugId(createDebugId(buttonLabel));
            tableGrid.addComponent(tableButton, 1, 0);
            tableGrid.setComponentAlignment(tableButton, Alignment.BOTTOM_RIGHT);
        }
        
        protected void addTable(Container tableContainer, String[] visibleColumns) {
            
            komotoList.setContainerDataSource(tableContainer);
            komotoList.setVisibleColumns(visibleColumns);
            komotoList.setSizeFull();
            komotoList.setDebugId(createDebugId("komotoTable" +tila.name()));
            komotoList.setImmediate(true);
            komotoList.setSelectable(true);
            if (clickable) {
                komotoList.addListener(new Property.ValueChangeListener() {
                    public void valueChange(ValueChangeEvent event) {
                        Window komotoEditWindow = new Window();
                        komotoEdit = new KoulutusmoduuliToteutusEditView();
                        KoulutusmoduuliToteutusDTO ktd = (KoulutusmoduuliToteutusDTO)((BeanItem)(komotoList.getItem(komotoList.getValue()))).getBean();
                        komotoEdit.bind(ktd);

                        komotoEditWindow.addComponent(komotoEdit);

                        komotoEditWindow.setModal(true);
                        komotoEditWindow.setWidth(komotoEdit.getKomotoEditViewWidth()+ 50, Sizeable.UNITS_PIXELS);
                        komotoEditWindow.setHeight(komotoEdit.getKomotoEditViewHeight() + 100, Sizeable.UNITS_PIXELS);
                        getApplication().getMainWindow().addWindow(komotoEditWindow);
                    }
                });
            }
            
            
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

    /**
     * @return the tila
     */
    public KoulutusTila getTila() {
        return tila;
    }
    
    private String createDebugId(String prefix) {
        prefix.replaceAll("\\s", "");
        counter++;
        return prefix + counter;
        
    }

    /**
     * @param tila the tila to set
     */
    public void setTila(KoulutusTila tila) {
        this.tila = tila;
    }

    /**
     * 
     * @return the clickable
     */
    public boolean isClickable() {
        return clickable;
    }

    /**
     * 
     * @param clickable the clickable to set
     */
    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    //Getter for komoto edit for selenium use
    
    public KoulutusmoduuliToteutusEditView getKomotoEdit() {
        return komotoEdit;
    }
}
