/*
 *
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence. You may
 * obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the European Union Public Licence for more
 * details.
 */


package fi.vm.sade.tarjonta.ui.koulutusmoduuli.toteutus;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliTila;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliToteutusSearchDTO;
import fi.vm.sade.tarjonta.ui.util.I18NHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;


/**
 *
 * @author Tuomas Katva
 */
@Configurable(preConstruction = true)
public class KoulutusmoduuliToteutusListView extends CustomComponent {


    private HorizontalLayout rootLayout;
    //Should this contain Organisaatio search tree ?
    private Panel leftPanel;
    private Panel rightPanel;
    private final String[] visibleColumns = new String[]{"nimi", "oid"};
    private final String[] viimeisimmatVisibleColumns = new String[]{"selected","nimi", "oid"};
    private KoulutusModuulinToteutusTable viimeisimmat;
    private KoulutusModuulinToteutusTable suunnitteilla;
    private KoulutusModuulinToteutusTable julkaistava;
    private KoulutusModuulinToteutusTable julkaistu;
    
    @Autowired
    private KoulutusModuulinToteutusTableFactory komotoTableFactory;

    private static final I18NHelper i18n = new I18NHelper("KoulutusmoduuliToteutusListView.");
    
    public KoulutusmoduuliToteutusListView() {
        rootLayout = new HorizontalLayout();
        leftPanel = new Panel("Empty panel");
        rootLayout.addComponent(leftPanel);

        rightPanel = new Panel("Koulutustoimija, oppilaitos, toimipiste ....");
        rightPanel.addComponent(createTables());
        rootLayout.addComponent(rightPanel);

        addListernerToSuunnitteilla();
        setCompositionRoot(rootLayout);
    }

    private VerticalLayout createTables() {
        VerticalLayout tableHolder = new VerticalLayout();

        setViimeisimmat(getKomotoTableFactory().createViimTableWithTila(new KoulutusmoduuliToteutusSearchDTO(KoulutusmoduuliTila.VALMIS), viimeisimmatVisibleColumns, false));

        tableHolder.addComponent(getViimeisimmat());

        suunnitteilla = getKomotoTableFactory().createSuunnitteillaOlevaTable(new KoulutusmoduuliToteutusSearchDTO(KoulutusmoduuliTila.SUUNNITTELUSSA), visibleColumns, true);

        tableHolder.addComponent(suunnitteilla);

        setJulkaistava(getKomotoTableFactory().createCommonKomotoTable(new KoulutusmoduuliToteutusSearchDTO(KoulutusmoduuliTila.VALMIS), visibleColumns,getCaptionForString("julkaistavaKoulutustarjonta"), true));

        tableHolder.addComponent(getJulkaistava());

        julkaistu = getKomotoTableFactory().createCommonKomotoTable(new KoulutusmoduuliToteutusSearchDTO(KoulutusmoduuliTila.JULKAISTU), visibleColumns,getCaptionForString("julkaistuKoulutusTarjonta"), true);

        tableHolder.addComponent(julkaistu);
        return tableHolder;
    }

    
    protected String getCaptionForString(String key) {
        String retval = i18n.getMessage(key);
        if (retval == null || retval.length() < 1) {
            return key;
        } else {
            return retval;
        }
    }
    
    private void addListernerToSuunnitteilla() {
        if (suunnitteilla != null && suunnitteilla.getTableButton() != null) {
            suunnitteilla.getTableButton().addListener(new Button.ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    Window komotoEditWindow = new Window();
                    
                    KoulutusmoduuliToteutusEditView komotoEdit = new KoulutusmoduuliToteutusEditView();
                   
                    komotoEditWindow.addComponent(komotoEdit);
                    
                    komotoEditWindow.setModal(true);
                    komotoEditWindow.setWidth(komotoEdit.getKomotoEditViewWidth()+ 50, Sizeable.UNITS_PIXELS);
                    komotoEditWindow.setHeight(komotoEdit.getKomotoEditViewHeight() + 100, Sizeable.UNITS_PIXELS);
                    getApplication().getMainWindow().addWindow(komotoEditWindow);
                    
                   
                }
            });
        }
    }

    /**
     * @return the suunnitteilla
     */
    public KoulutusModuulinToteutusTable getSuunnitteilla() {
        return suunnitteilla;
    }

    /**
     * @param suunnitteilla the suunnitteilla to set
     */
    public void setSuunnitteilla(KoulutusModuulinToteutusTable suunnitteilla) {
        this.suunnitteilla = suunnitteilla;
    }

    /**
     * @return the julkaistu
     */
    public KoulutusModuulinToteutusTable getJulkaistu() {
        return julkaistu;
    }

    /**
     * @param julkaistu the julkaistu to set
     */
    public void setJulkaistu(KoulutusModuulinToteutusTable julkaistu) {
        this.julkaistu = julkaistu;
    }

    /**
     * @return the komotoTableFactory
     */
    public KoulutusModuulinToteutusTableFactory getKomotoTableFactory() {
        return komotoTableFactory;
    }

    /**
     * @param komotoTableFactory the komotoTableFactory to set
     */
    public void setKomotoTableFactory(KoulutusModuulinToteutusTableFactory komotoTableFactory) {
        this.komotoTableFactory = komotoTableFactory;
    }

    /**
     * @return the julkaistava
     */
    public KoulutusModuulinToteutusTable getJulkaistava() {
        return julkaistava;
    }

    /**
     * @param julkaistava the julkaistava to set
     */
    public void setJulkaistava(KoulutusModuulinToteutusTable julkaistava) {
        this.julkaistava = julkaistava;
    }

    /**
     * @return the viimeisimmat
     */
    public KoulutusModuulinToteutusTable getViimeisimmat() {
        return viimeisimmat;
    }

    /**
     * @param viimeisimmat the viimeisimmat to set
     */
    public void setViimeisimmat(KoulutusModuulinToteutusTable viimeisimmat) {
        this.viimeisimmat = viimeisimmat;
    }

}
