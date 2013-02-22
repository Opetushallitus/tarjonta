package fi.vm.sade.tarjonta.data.util;/*
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

import fi.vm.sade.koodisto.service.KoodiAdminService;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.KoodistoAdminService;
import fi.vm.sade.koodisto.service.KoodistoService;
import fi.vm.sade.koodisto.service.types.CreateKoodiDataType;
import fi.vm.sade.koodisto.service.types.CreateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.SearchKoodistosCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.koodisto.util.KoodistoServiceSearchCriteriaBuilder;
import fi.vm.sade.tarjonta.data.CommonKoodiData;
import fi.vm.sade.tarjonta.data.dto.Koodi;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author: Tuomas Katva
 * Date: 14.2.2013
 */
@Component
public class TarjontaDataKoodistoHelper {

    @Autowired
    private KoodistoAdminService koodistoAdminService;
    @Autowired
    private KoodiAdminService koodiAdminService;
    @Autowired
    private KoodiService koodiService;
    @Autowired
    private KoodistoService koodistoService;

    private static final Date ACTIVATED_DATE = new DateTime(2013, 1, 1, 1, 1).toDate();

    private String organisaatioNimi;

    private String organisaatioOid;

    private final Logger log = LoggerFactory.getLogger(TarjontaDataKoodistoHelper.class);

    public TarjontaDataKoodistoHelper() {


    }

    public TarjontaDataKoodistoHelper(String orgNimi, String orgOid) {
         setOrganisaatioNimi(orgNimi);
         setOrganisaatioOid(orgOid);
    }

    public KoodiType addCodeItem(Koodi koodiData,String koodistoUri) {
        CreateKoodiDataType createKoodiDataType = DataUtils.createCreateKoodiDataType(koodiData,TilaType.LUONNOS);
        KoodiType createdKoodi = null;
        try {
            createdKoodi = koodiAdminService.createKoodi(koodistoUri, createKoodiDataType);
        } catch (Exception exp) {
            log.warn("Unable to create koodi : with arvo : {}",createKoodiDataType.getKoodiUri(),createKoodiDataType.getKoodiArvo());
        }
        return createdKoodi;
    }

    public KoodiType addCodeItem(String koodistoUri, String koodiUri, String arvo, String name) {
        CreateKoodiDataType createKoodiDataType = DataUtils.createCreateKoodiDataType(koodiUri,
                arvo, TilaType.HYVAKSYTTY, ACTIVATED_DATE, null, name);
        KoodiType createdKoodi = null;
        try {
        createdKoodi = koodiAdminService.createKoodi(koodistoUri, createKoodiDataType);
        } catch (Exception exp) {
          log.warn("Unable to create koodi : with arvo : {}",createKoodiDataType.getKoodiUri(),createKoodiDataType.getKoodiArvo());
        }
       return createdKoodi;
    }

    public CreateKoodistoDataType addCodeGroup(List<String> baseUri, String koodistoUri, String name) {
        log.info("Creating koodisto with uri: {} and base uri : {}",koodistoUri,baseUri.get(0));
        CreateKoodistoDataType createKoodistoDataType = DataUtils.createCreateKoodistoDataType(
                koodistoUri, getOrganisaatioNimi(), getOrganisaatioOid(), ACTIVATED_DATE, ACTIVATED_DATE,
                name);
        koodistoAdminService.createKoodisto(baseUri, createKoodistoDataType);

        return createKoodistoDataType;
    }

    public KoodiUriAndVersioType createKoodiUriAndVersioType(KoodiType koodi) {
        KoodiUriAndVersioType k = new KoodiUriAndVersioType();
        k.setKoodiUri(koodi.getKoodiUri());
        k.setVersio(koodi.getVersio());
        return k;
    }

    public List<KoodiType> getKoodiByArvoAndKoodistoNimi(String koodiArvo, String koodistoUri) {

        List<KoodiType> koodis =  koodiService.searchKoodisByKoodisto(KoodiServiceSearchCriteriaBuilder.koodisByArvoAndKoodistoUri(koodiArvo,koodistoUri));
        return koodis;
    }

    public KoodistoType getKoodistoByUri(String koodistoUri) {
        SearchKoodistosCriteriaType searchType = KoodistoServiceSearchCriteriaBuilder.latestKoodistoByUri(koodistoUri);

        List<KoodistoType> koodistos = koodistoService.searchKoodistos(searchType);
        if (koodistos.size() != 1) {
            throw new RuntimeException("Failing");
        }

        return koodistos.get(0);
    }

    public String getOrganisaatioNimi() {
        return organisaatioNimi;
    }

    public void setOrganisaatioNimi(String organisaatioNimi) {
        this.organisaatioNimi = organisaatioNimi;
    }

    public String getOrganisaatioOid() {
        return organisaatioOid;
    }

    public void setOrganisaatioOid(String organisaatioOid) {
        this.organisaatioOid = organisaatioOid;
    }
}
