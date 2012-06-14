package fi.vm.sade.tarjonta.ui.koulutusmoduuli.toteutus;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import fi.vm.sade.tarjonta.ui.service.TarjontaUiService;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliToteutusDTO;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliTila;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliToteutusSearchDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

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
    private final String[] visibleColumns = new String[]{"nimi", "toteutettavaKoulutusmoduuliOID"};
    private final String[] viimeisimmatVisibleColumns = new String[]{"selected","nimi", "toteutettavaKoulutusmoduuliOID"};
    private KomotoTable viimeisimmat;
    private KomotoTable suunnitteilla;
    private KomotoTable julkaistava;
    private KomotoTable julkaistu;
    @Autowired
    private KomotoTableFactory komotoTableFactory;

    public KoulutusmoduuliToteutusListView() {
        rootLayout = new HorizontalLayout();
        leftPanel = new Panel("Empty panel");
        rootLayout.addComponent(leftPanel);

        rightPanel = new Panel("Koulutustoimija, oppilaitos, toimipiste ....");
        rightPanel.addComponent(createTables());
        rootLayout.addComponent(rightPanel);

        setCompositionRoot(rootLayout);
    }

    private VerticalLayout createTables() {
        VerticalLayout tableHolder = new VerticalLayout();

        setViimeisimmat(getKomotoTableFactory().createViimTableWithTila(new KoulutusmoduuliToteutusSearchDTO(KoulutusmoduuliTila.VALMIS), viimeisimmatVisibleColumns));

        tableHolder.addComponent(getViimeisimmat());

        suunnitteilla = getKomotoTableFactory().createSuunnitteillaOlevaTable(new KoulutusmoduuliToteutusSearchDTO(KoulutusmoduuliTila.SUUNNITTELUSSA), visibleColumns);

        tableHolder.addComponent(suunnitteilla);

        setJulkaistava(getKomotoTableFactory().createCommonKomotoTable(new KoulutusmoduuliToteutusSearchDTO(KoulutusmoduuliTila.VALMIS), visibleColumns,"Julkaistavaksi valmis koulutustarjonta"));

        tableHolder.addComponent(getJulkaistava());

        julkaistu = getKomotoTableFactory().createCommonKomotoTable(new KoulutusmoduuliToteutusSearchDTO(KoulutusmoduuliTila.JULKAISTU), visibleColumns,"Julkaistu koulutustarjonta");

        tableHolder.addComponent(julkaistu);
        return tableHolder;
    }

  

    /**
     * @return the suunnitteilla
     */
    public KomotoTable getSuunnitteilla() {
        return suunnitteilla;
    }

    /**
     * @param suunnitteilla the suunnitteilla to set
     */
    public void setSuunnitteilla(KomotoTable suunnitteilla) {
        this.suunnitteilla = suunnitteilla;
    }

    /**
     * @return the julkaistu
     */
    public KomotoTable getJulkaistu() {
        return julkaistu;
    }

    /**
     * @param julkaistu the julkaistu to set
     */
    public void setJulkaistu(KomotoTable julkaistu) {
        this.julkaistu = julkaistu;
    }

    /**
     * @return the komotoTableFactory
     */
    public KomotoTableFactory getKomotoTableFactory() {
        return komotoTableFactory;
    }

    /**
     * @param komotoTableFactory the komotoTableFactory to set
     */
    public void setKomotoTableFactory(KomotoTableFactory komotoTableFactory) {
        this.komotoTableFactory = komotoTableFactory;
    }

    /**
     * @return the julkaistava
     */
    public KomotoTable getJulkaistava() {
        return julkaistava;
    }

    /**
     * @param julkaistava the julkaistava to set
     */
    public void setJulkaistava(KomotoTable julkaistava) {
        this.julkaistava = julkaistava;
    }

    /**
     * @return the viimeisimmat
     */
    public KomotoTable getViimeisimmat() {
        return viimeisimmat;
    }

    /**
     * @param viimeisimmat the viimeisimmat to set
     */
    public void setViimeisimmat(KomotoTable viimeisimmat) {
        this.viimeisimmat = viimeisimmat;
    }
}
