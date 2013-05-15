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

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * 
 * @author Markus
 *
 */
public abstract class ShowRelatedObjectsDialog extends VerticalLayout {
    
    private static final long serialVersionUID = 5874075966485522529L;
    
    public static final String[] ORDER_BY = new String[]{I18N.getMessage("ListHakukohdeViewImpl.jarjestys.Organisaatio")};
    public static final String COLUMN_A = "Kategoriat";
    public static final String COLUMN_PVM = "Ajankohta";
    public static final String COLUMN_TILA = "Tila";
    
    protected CategoryTreeView tree;
    protected TarjontaPresenter presenter;
    protected transient I18NHelper i18n = new I18NHelper(this);
    
    public ShowRelatedObjectsDialog(TarjontaPresenter presenter) {
        setSpacing(true);
        setSizeUndefined();
        setWidth("700px");
        this.presenter = presenter;
    }
    
    protected void buildLayout(String otsikko, String nimi, int pageSize) {
        VerticalLayout vl = UiUtil.verticalLayout();
        vl.setSpacing(true);
        vl.setMargin(true, false, true, true);
        Label dialogLabel = new Label();
        dialogLabel.setValue(otsikko);
        vl.addComponent(dialogLabel);
        Label hakukohdeNimi = new Label();
        hakukohdeNimi.setValue(nimi);
        vl.addComponent(hakukohdeNimi);
        addComponent(vl);
        buildKoulutusTree(pageSize);
    }
    
    private void buildKoulutusTree(int pageSize) {
        tree = new CategoryTreeView();
        tree.setPageLength(pageSize);
        addComponent(tree);
        setExpandRatio(tree, 1f);
        
        tree.addContainerProperty(COLUMN_A, CaptionItem.class, new CaptionItem());
        tree.addContainerProperty(COLUMN_PVM, String.class, "");
        tree.addContainerProperty(COLUMN_TILA, String.class, "");
        
        tree.setColumnExpandRatio(COLUMN_A, 1f);
        tree.setColumnExpandRatio(COLUMN_PVM, 0.3f);
        tree.setColumnExpandRatio(COLUMN_TILA, 0.3f);
    }
    
    protected abstract void populateTree(); 
    
    protected String T(String key, Object... args) {
        return i18n.getMessage(key, args);
    }
    
    public class CaptionItem extends HorizontalLayout {

        private static final long serialVersionUID = 312997606755010205L;
        
        private String caption;
        private Button linkButton;
        
        public CaptionItem() {
            
        }
       
        public CaptionItem(String caption, boolean withLink) {
            this.caption = caption;
            buildLayout(withLink);
        }
        
        public Button getLinkButton() {
            return linkButton;
        }
        
        private void buildLayout(boolean withLink) {
            if (withLink) {
                linkButton = UiUtil.buttonLink(null, caption);
                linkButton.setStyleName("link-row");
                linkButton.setSizeUndefined();
                linkButton.setHeight(7, Sizeable.UNITS_PIXELS);
                addComponent(linkButton);
            } else {
                Label orgLabel = new Label();
                orgLabel.setValue(caption);
                addComponent(orgLabel);
            }
        }
        
    }   
}
