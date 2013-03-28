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

import javax.annotation.PostConstruct;
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

    /**
     * Adds a new koodi to koodisto.
     *
     * @param koodiData
     * @param koodistoUri
     * @return
     */
    public KoodiType addKoodi(final Koodi koodiData, final String koodistoUri) {
        CreateKoodiDataType createKoodiDataType = DataUtils.createCreateKoodiDataType(koodiData);
        try {
            return koodiAdminService.createKoodi(koodistoUri, createKoodiDataType);
        } catch (final Exception exp) {
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
                return koodiAdminService.createKoodi(koodistoUri, createKoodiDataType);
            } catch (final Exception exx) {
                // gotta throw up
                throw new RuntimeException(String.format("Failed to re-create koodi with new arvo [%s], exception [%s]", createKoodiDataType.getKoodiArvo(), exx.getMessage()));
            }
        }
    }

    /**
     * Creates a new koodisto. If koodisto already exists, it will be removed and created again.
     *
     * @param baseUri
     * @param koodistoUri
     * @param name
     * @return
     */
    public KoodistoType addKoodisto(final List<String> baseUri, final String koodistoUri, final String name) {
        log.info("Creating koodisto with uri [{}] and base uri [{}]", koodistoUri, baseUri.get(0));
        final CreateKoodistoDataType createKoodistoDataType = DataUtils.createCreateKoodistoDataType(
                getOrganisaatioNimi(), getOrganisaatioOid(), ACTIVATED_DATE, null, name);
        return koodistoAdminService.createKoodisto(baseUri, createKoodistoDataType);
    }

    /**
     * Approves koodisto. Also approved all related koodis.
     *
     * @param koodistoType
     * @return
     */
    public KoodistoType approveKoodisto(final KoodistoType koodistoType) {
        final UpdateKoodistoDataType updateType = new UpdateKoodistoDataType();
        updateType.setKoodistoUri(koodistoType.getKoodistoUri());
        updateType.setLukittu(koodistoType.isLukittu());
        updateType.setOmistaja(koodistoType.getOmistaja());
        updateType.setOrganisaatioOid(koodistoType.getOrganisaatioOid());
        updateType.setTila(TilaType.HYVAKSYTTY);
        updateType.setVoimassaAlkuPvm(koodistoType.getVoimassaAlkuPvm());
        updateType.setVoimassaLoppuPvm(koodistoType.getVoimassaLoppuPvm());
        updateType.getMetadataList().addAll(koodistoType.getMetadataList());
        return koodistoAdminService.updateKoodisto(updateType);
    }

    /**
     * Removes koodisto.
     *
     * @param koodistoUri
     * @param orgOid
     * @return
     */
    public boolean removeKoodisto(final String koodistoUri, final String orgOid) {
        log.info("Removing koodisto with uri [{}], orgOid [{}]", koodistoUri, orgOid);
        try {
            final KoodistoType koodisto = getKoodistoByUri(koodistoUri);
            final UpdateKoodistoDataType update = new UpdateKoodistoDataType();
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
        } catch (final Exception exp) {
            exp.printStackTrace();
            log.warn("Unable to remove koodisto [{}], exception [{}]", koodistoUri, exp.toString());
            return false;
        }
    }

    public KoodiUriAndVersioType createKoodiUriAndVersioType(final KoodiType koodi) {
        final KoodiUriAndVersioType k = new KoodiUriAndVersioType();
        k.setKoodiUri(koodi.getKoodiUri());
        k.setVersio(koodi.getVersio());
        return k;
    }

    public List<KoodiType> getKoodiByArvoAndKoodistoNimi(final String koodiArvo, final String koodistoUri) {
        final List<KoodiType> koodis = koodiService.searchKoodisByKoodisto(KoodiServiceSearchCriteriaBuilder.koodisByArvoAndKoodistoUri(koodiArvo, koodistoUri));
        return koodis;
    }

    public KoodistoType getKoodistoByUri(final String koodistoUri) {
        final SearchKoodistosCriteriaType searchType = KoodistoServiceSearchCriteriaBuilder.latestKoodistoByUri(koodistoUri);

        final List<KoodistoType> koodistos = koodistoService.searchKoodistos(searchType);
        if (koodistos.size() != 1) {
            throw new RuntimeException("Failing");
        }

        return koodistos.get(0);
    }

    public boolean isKoodisto(final String koodistoUri) {
        final SearchKoodistosCriteriaType searchType = KoodistoServiceSearchCriteriaBuilder.latestKoodistoByUri(koodistoUri);

        final List<KoodistoType> koodistos = koodistoService.searchKoodistos(searchType);
        if (koodistos == null || koodistos.isEmpty()) {
            return false;
        }

        return true;
    }

    public String getOrganisaatioNimi() {
        return organisaatioNimi;
    }

    public void setOrganisaatioNimi(final String organisaatioNimi) {
        this.organisaatioNimi = organisaatioNimi;
    }

    public String getOrganisaatioOid() {
        return organisaatioOid;
    }

    public void setOrganisaatioOid(final String organisaatioOid) {
        this.organisaatioOid = organisaatioOid;
    }

    public HashMap<String, KoodiType> loadKoodisToKoodisto(final Set<Koodi> koodis, final String koodistoName) {
        final HashMap<String, KoodiType> koodiUriArvoPair = new HashMap<String, KoodiType>();

        for (final Koodi koodi : koodis) {
            addKoodi(koodi, DataUtils.createKoodiUriFromName(koodistoName));
        }

        return koodiUriArvoPair;
    }

    public void createKoodiRelations(final Set<KoodiRelaatio> koodiRelaatios) {
        for (KoodiRelaatio koodiRelaatio : koodiRelaatios) {
            addKoodiRelation(koodiRelaatio);
        }
    }

    public void addKoodiRelation(final KoodiRelaatio koodiRelaatio) {
        final List<KoodiUriAndVersioType> alakoodis = new ArrayList<KoodiUriAndVersioType>();
        alakoodis.add(createKoodiUriVersio(koodiRelaatio.getKoodiAlaArvo(), koodiRelaatio.getAlaArvoKoodisto()));
        try {
            log.info("Trying to create relation with yla-arvo [{}], ala-arvo [{}]", koodiRelaatio.getKoodiYlaArvo(), koodiRelaatio.getKoodiAlaArvo());
            koodiAdminService.addRelationByAlakoodi(createKoodiUriVersio(koodiRelaatio.getKoodiYlaArvo(), koodiRelaatio.getYlaArvoKoodisto()), alakoodis, SuhteenTyyppiType.SISALTYY);
        } catch (final Exception exp) {
            log.error("Unable to create relation with arvos [{}], exception [{}]", koodiRelaatio.getKoodiYlaArvo() + ", " + koodiRelaatio.getKoodiAlaArvo(), exp.toString());
        }
    }

    public KoodiUriAndVersioType createKoodiUriVersio(final String koodiArvo, final String koodistoUri) {
        final List<KoodiType> koodiTypes = getKoodiByArvoAndKoodistoNimi(koodiArvo, koodistoUri);

        if (koodiTypes != null && koodiTypes.size() > 0) {

            return createKoodiUriAndVersioType(koodiTypes.get(0));
        } else {
            return new KoodiUriAndVersioType();
        }
    }

    public void addRelaatioByAlakoodi(final KoodiUriAndVersioType ylaKoodi, final List<KoodiUriAndVersioType> alaKoodis,
                                      final SuhteenTyyppiType suhteenTyyppi) {
        try {
            log.info("Trying to create relation with yla-arvo [{}], ala-arvo [{}]", ylaKoodi.getKoodiUri(), alaKoodis);
            koodiAdminService.addRelationByAlakoodi(ylaKoodi, alaKoodis, suhteenTyyppi);
        } catch (Exception exp) {
            log.warn("Unable to create relation, exception [{}]", exp.toString());
        }
    }
}
