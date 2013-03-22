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
import fi.vm.sade.koodisto.service.types.*;
import fi.vm.sade.koodisto.service.types.common.*;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.koodisto.util.KoodistoServiceSearchCriteriaBuilder;
import fi.vm.sade.tarjonta.data.dto.Koodi;
import fi.vm.sade.tarjonta.data.dto.KoodiRelaatio;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author: Tuomas Katva Date: 14.2.2013
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

    public KoodiType addCodeItem(Koodi koodiData, String koodistoUri) {
        CreateKoodiDataType createKoodiDataType = DataUtils.createCreateKoodiDataType(koodiData);
        KoodiType createdKoodi = null;
        try {
            createdKoodi = koodiAdminService.createKoodi(koodistoUri, createKoodiDataType);

            // TODO update tila to HYVAKSYTTY
            final KoodiType updated = koodiAdminService.updateKoodi(getUpdateKoodiType(createdKoodi));
        } catch (Exception exp) {
            log.warn("Unable to create koodi with arvo [{}], exception [{}], trying to re-create with another nimi", createKoodiDataType.getKoodiArvo(), exp.getMessage());
            //TODO: remove when Koodisto Team has managed to get this working
            try {
                koodiData.setKoodiNimiFi(String.format("%s (%s)", koodiData.getKoodiNimiFi(), koodiData.getKoodiArvo()));
                if (StringUtils.isNotBlank(koodiData.getKoodiNimiSv())) {
                    koodiData.setKoodiNimiSv(String.format("%s (%s)", koodiData.getKoodiNimiSv(), koodiData.getKoodiArvo()));
                }
                if (StringUtils.isNotBlank(koodiData.getKoodiNimiEn())) {
                    koodiData.setKoodiNimiEn(String.format("%s (%s)", koodiData.getKoodiNimiEn(), koodiData.getKoodiArvo()));
                }
                createKoodiDataType = DataUtils.createCreateKoodiDataType(koodiData);
                createdKoodi = koodiAdminService.createKoodi(koodistoUri, createKoodiDataType);

                // TODO update tila to HYVAKSYTTY
                final KoodiType updated = koodiAdminService.updateKoodi(getUpdateKoodiType(createdKoodi));
            } catch (Exception exx) {
                // gotta throw up
                throw new RuntimeException(String.format("Failed to re-create koodi with new arvo [%s], exception [%s]", createKoodiDataType.getKoodiArvo(), exx.getMessage()));
            }

        }
        return createdKoodi;
    }

    /**
     * Converts KoodiType to UpdateKoodiDataType with tila as HYVAKSYTTY.
     *
     * @param koodi
     * @return
     */
    private UpdateKoodiDataType getUpdateKoodiType(final KoodiType koodi) {
        final UpdateKoodiDataType updateType = new UpdateKoodiDataType();
        updateType.setKoodiArvo(koodi.getKoodiArvo());
        updateType.setKoodiUri(koodi.getKoodiUri());
        updateType.setTila(TilaType.HYVAKSYTTY);
        updateType.setVoimassaAlkuPvm(koodi.getVoimassaAlkuPvm());
        updateType.setVoimassaLoppuPvm(koodi.getVoimassaLoppuPvm());
        updateType.getMetadata().addAll(koodi.getMetadata());
        return updateType;
    }

    public CreateKoodistoDataType addCodeGroup(List<String> baseUri, String koodistoUri, String name) {
        log.info("Creating koodisto with uri [{}] and base uri [{}]", koodistoUri, baseUri.get(0));
        CreateKoodistoDataType createKoodistoDataType = DataUtils.createCreateKoodistoDataType(
                getOrganisaatioNimi(), getOrganisaatioOid(), ACTIVATED_DATE, null,
                name);
        final KoodistoType createdKoodisto = koodistoAdminService.createKoodisto(baseUri, createKoodistoDataType);

        // update tila to HYVAKSYTTY
        final KoodistoType updatedKoodisto = koodistoAdminService.updateKoodisto(getUpdateKoodistoType(createdKoodisto));

        return createKoodistoDataType;
    }

    /**
     * Converts KoodistoType to UpdateKoodistoDataType with tila as HYVAKSYTTY.
     *
     * @param koodisto
     * @return
     */
    private UpdateKoodistoDataType getUpdateKoodistoType(final KoodistoType koodisto) {
        final UpdateKoodistoDataType updateType = new UpdateKoodistoDataType();
        updateType.setKoodistoUri(koodisto.getKoodistoUri());
        updateType.setLukittu(koodisto.isLukittu());
        updateType.setOmistaja(koodisto.getOmistaja());
        updateType.setOrganisaatioOid(koodisto.getOrganisaatioOid());
        updateType.setTila(TilaType.HYVAKSYTTY);
        updateType.setVoimassaAlkuPvm(koodisto.getVoimassaAlkuPvm());
        updateType.setVoimassaLoppuPvm(koodisto.getVoimassaLoppuPvm());
        updateType.getMetadataList().addAll(koodisto.getMetadataList());
        return updateType;
    }

    public boolean removeKoodisto(String koodistoUri, String orgOid) {
        log.info("Removing koodisto with uri [{}], orgOid [{}]", koodistoUri, orgOid);
        try {
            KoodistoType koodisto = getKoodistoByUri(koodistoUri);
            UpdateKoodistoDataType update = new UpdateKoodistoDataType();
            update.setKoodistoUri(koodistoUri);
            update.setTila(TilaType.PASSIIVINEN);
            update.setOmistaja(koodisto.getOmistaja());
            if (orgOid != null) {
                update.setOrganisaatioOid(orgOid);
            }
            update.getMetadataList().addAll(koodisto.getMetadataList());
            update.setVoimassaAlkuPvm(koodisto.getVoimassaAlkuPvm());
            update.setVoimassaLoppuPvm(koodisto.getVoimassaLoppuPvm());

            koodistoAdminService.updateKoodisto(update);
            koodistoAdminService.deleteKoodistoVersion(koodistoUri, koodisto.getVersio());
            return true;
        } catch (Exception exp) {
            exp.printStackTrace();
            log.warn("Unable to remove koodisto [{}], exception [{}]", koodistoUri, exp.toString());
            return false;
        }
    }

    public KoodiUriAndVersioType createKoodiUriAndVersioType(KoodiType koodi) {
        KoodiUriAndVersioType k = new KoodiUriAndVersioType();
        k.setKoodiUri(koodi.getKoodiUri());
        k.setVersio(koodi.getVersio());
        return k;
    }

    public List<KoodiType> getKoodiByArvoAndKoodistoNimi(String koodiArvo, String koodistoUri) {

        List<KoodiType> koodis = koodiService.searchKoodisByKoodisto(KoodiServiceSearchCriteriaBuilder.koodisByArvoAndKoodistoUri(koodiArvo, koodistoUri));
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

    public boolean isKoodisto(String koodistoUri) {
        SearchKoodistosCriteriaType searchType = KoodistoServiceSearchCriteriaBuilder.latestKoodistoByUri(koodistoUri);

        List<KoodistoType> koodistos = koodistoService.searchKoodistos(searchType);
        if (koodistos == null || koodistos.isEmpty()) {
            return false;
        }

        return true;
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

    public HashMap<String, KoodiType> loadKoodisToKoodisto(Set<Koodi> koodis, String koodistoName) {
        final HashMap<String, KoodiType> koodiUriArvoPair = new HashMap<String, KoodiType>();

        for (final Koodi koodi : koodis) {
            addCodeItem(koodi, DataUtils.createKoodiUriFromName(koodistoName));
        }

        return koodiUriArvoPair;
    }

    public void createKoodiRelations(Set<KoodiRelaatio> koodiRelaatios) {
        for (KoodiRelaatio koodiRelaatio : koodiRelaatios) {
            addKoodiRelation(koodiRelaatio);
        }
    }

    public void addKoodiRelation(KoodiRelaatio koodiRelaatio) {
        List<KoodiUriAndVersioType> alakoodis = new ArrayList<KoodiUriAndVersioType>();
        alakoodis.add(createKoodiUriVersio(koodiRelaatio.getKoodiAlaArvo(), koodiRelaatio.getAlaArvoKoodisto()));
        try {
            log.info("Trying to create relation with yla-arvo [{}], ala-arvo [{}]", koodiRelaatio.getKoodiYlaArvo(), koodiRelaatio.getKoodiAlaArvo());
            koodiAdminService.addRelationByAlakoodi(createKoodiUriVersio(koodiRelaatio.getKoodiYlaArvo(), koodiRelaatio.getYlaArvoKoodisto()), alakoodis, SuhteenTyyppiType.SISALTYY);
        } catch (Exception exp) {
            log.error("Unable to create relation with arvos [{}], exception [{}]", koodiRelaatio.getKoodiYlaArvo() + ", " + koodiRelaatio.getKoodiAlaArvo(), exp.toString());
        }
    }

    public KoodiUriAndVersioType createKoodiUriVersio(String koodiArvo, String koodistoUri) {
        List<KoodiType> koodiTypes = getKoodiByArvoAndKoodistoNimi(koodiArvo, koodistoUri);

        if (koodiTypes != null && koodiTypes.size() > 0) {

            return createKoodiUriAndVersioType(koodiTypes.get(0));
        } else {
            return new KoodiUriAndVersioType();
        }
    }

    public void addRelationByAlakoodi(final KoodiUriAndVersioType ylaKoodi, final List<KoodiUriAndVersioType> alaKoodis,
                                      final SuhteenTyyppiType suhteenTyyppi) {
        try {
            log.info("Trying to create relation with yla-arvo [{}], ala-arvo [{}]", ylaKoodi.getKoodiUri(), alaKoodis);
            koodiAdminService.addRelationByAlakoodi(ylaKoodi, alaKoodis, suhteenTyyppi);
        } catch (Exception exp) {
            log.warn("Unable to create relation, exception [{}]", exp.toString());
        }
    }
}
