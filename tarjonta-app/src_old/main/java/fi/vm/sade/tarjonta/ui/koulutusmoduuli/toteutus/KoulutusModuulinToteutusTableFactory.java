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

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Table;
import fi.vm.sade.tarjonta.service.KoulutusmoduuliToteutusAdminService;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliToteutusDTO;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliToteutusSearchDTO;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tuomas Katva
 */
//@Configurable(preConstruction = true)
public class KoulutusModuulinToteutusTableFactory {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private KoulutusmoduuliToteutusAdminService koulutusModuuliToteutusAdminService;

    public KoulutusModuulinToteutusTable createViimTableWithTila(KoulutusmoduuliToteutusSearchDTO criteria, String[] visibleColumns, boolean clickable) {

        return KoulutusModuulinToteutusTableBuilder.komotoTable(criteria.getTila(), clickable)
                .withLabel("Viimeisimmat toteutuneet KOMOTOt")
                .withButton("Siirra suunnitteluun", false)
                .withSearchAllButton("Valitse kaikki",true)
                .withHeightAndWidth("400px", "200px")
                .withTable(createContainer(koulutusModuuliToteutusAdminService.findWithTila(criteria)), visibleColumns)
                .withColumnGenerator("selected", new Table.ColumnGenerator() {

                    @Override
                    public Object generateCell(Table source, Object itemId, Object columnId) {
                        Property prop = source.getItem(itemId).getItemProperty(columnId);
                        return new CheckBox(null,prop);
                    }
                })
                .build();

    }

    public KoulutusModuulinToteutusTable createSuunnitteillaOlevaTable(KoulutusmoduuliToteutusSearchDTO criteria, String[] visibleColumns, boolean clickable) {
        return KoulutusModuulinToteutusTableBuilder.komotoTable(criteria.getTila(), clickable)
                .withLabel("Suunnitteilla oleva koulutustarjonta")
                .withButton("Luo uusi Komoto", false)
                .withHeightAndWidth("400px", "200px")
                .withTable(createContainer(koulutusModuuliToteutusAdminService.findWithTila(criteria)), visibleColumns)
                .build();
    }

    public KoulutusModuulinToteutusTable createCommonKomotoTable(KoulutusmoduuliToteutusSearchDTO criteria, String[] visibleColumns, String tableLabel, boolean clickable) {
        return KoulutusModuulinToteutusTableBuilder.komotoTable(criteria.getTila(), clickable)
                .withLabel(tableLabel).withHeightAndWidth("400px", "200px")
                .withTable(createContainer(koulutusModuuliToteutusAdminService.findWithTila(criteria)), visibleColumns)
                .build();
    }

    private BeanContainer<String, TutkintoOhjelmaUiModel> createContainer(List<KoulutusmoduuliToteutusDTO> totModuulit)  {
        BeanContainer<String, TutkintoOhjelmaUiModel> komotos = new BeanContainer<String, TutkintoOhjelmaUiModel>(TutkintoOhjelmaUiModel.class);
        for (KoulutusmoduuliToteutusDTO totModuli : totModuulit) {
            try {
           TutkintoOhjelmaUiModel uiDto =  new TutkintoOhjelmaUiModel();
           if (totModuli.getKoulutuksenAlkamisPvm() == null) {
               totModuli.setKoulutuksenAlkamisPvm(new Date());
           }
           BeanUtils.copyProperties(uiDto, totModuli);
           
            komotos.addItem(uiDto.getOid(), uiDto);
            
            }catch (Exception exp) {
                log.warn(exp.getMessage());
               
            }
        }

        return komotos;
    }
    

    /**
     * @return the koulutusModuuliToteutusAdminService
     */
    public KoulutusmoduuliToteutusAdminService getKoulutusModuuliToteutusAdminService() {
        return koulutusModuuliToteutusAdminService;
    }

    /**
     * @param aKoulutusModuuliToteutusAdminService the
     * koulutusModuuliToteutusAdminService to set
     */
    public void setKoulutusModuuliToteutusAdminService(KoulutusmoduuliToteutusAdminService aKoulutusModuuliToteutusAdminService) {
        koulutusModuuliToteutusAdminService = aKoulutusModuuliToteutusAdminService;
    }
}
