package fi.vm.sade.tarjonta.ui.koulutusmoduuli.toteutus;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import fi.vm.sade.tarjonta.ui.service.TarjontaUiService;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliToteutusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import sun.awt.VerticalBagLayout;
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
@Configurable(preConstruction = true)
public class KoulutusmoduuliToteutusListView extends CustomComponent {

    private HorizontalLayout rootLayout;
    //Should this contain Organisaatio search tree ?
    private Panel leftPanel;
    private Panel rightPanel;
    private String[] visibleColumns = new String[] {"nimi","toteutettavaKoulutusmoduuliOID"};
    private BeanContainer<String,KoulutusmoduuliToteutusDTO> koulutusModulot = new BeanContainer<String, KoulutusmoduuliToteutusDTO>(KoulutusmoduuliToteutusDTO.class);
    @Autowired
    private TarjontaUiService uiService;

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
       KomotoTable viimeisimmat = KomotoTableBuilder.komotoTable("Viim")
               .withLabel("Viimeisimmat toteutuneet KOMOTOt")
               .withButton("Siirra suunnitteluun")
               .withSearchAllButton("Valitse kaikki")
               .withHeightAndWidth("400px", "200px")
               .withTable(koulutusModulot, visibleColumns)
               .build();
       
      tableHolder.addComponent(viimeisimmat);
      
      KomotoTable suunnitteilla = KomotoTableBuilder.komotoTable("Suunnitteilla")
              .withLabel("Suunnitteilla oleva koulutustarjonta")
              .withButton("Luo uusi komoto")
              .withHeightAndWidth("400px", "200px")
              .withTable(koulutusModulot, visibleColumns)
              .build();
      
      tableHolder.addComponent(suunnitteilla);
      
      KomotoTable julkaistava = KomotoTableBuilder.komotoTable("Julkaistava")
              .withLabel("Julkaistavaksi valmis koulutustarjonta")
              .withHeightAndWidth("400px", "200px")
              .withTable(koulutusModulot, visibleColumns)
              .build();
      
      tableHolder.addComponent(julkaistava);
      
      KomotoTable julkaistu = KomotoTableBuilder.komotoTable("Julkaistu")
              .withLabel("Julkaistu koulutustarjonta")
              .withHeightAndWidth("400px", "200px")
              .withTable(koulutusModulot, visibleColumns)
              .build();
      
      tableHolder.addComponent(julkaistu);
      return tableHolder;
    }
}
