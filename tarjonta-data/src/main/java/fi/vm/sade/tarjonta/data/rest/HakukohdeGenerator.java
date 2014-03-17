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
package fi.vm.sade.tarjonta.data.rest;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import fi.vm.sade.tarjonta.data.test.GenerateTestData;
import fi.vm.sade.tarjonta.data.util.KoodistoUtil;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.types.AjankohtaTyyppi;
import fi.vm.sade.tarjonta.service.types.HakukohdeLiiteTyyppi;
import fi.vm.sade.tarjonta.service.types.ValintakoeTyyppi;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jani Wilén
 */
@Component
@Configurable(preConstruction = false)
public class HakukohdeGenerator extends AbstractGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(HakukohdeGenerator.class);
    private static final Date DATE = new DateTime(2020, 1, 1, 1, 1).toDate();
    private static final Date EXAM_START_DATE = new DateTime(2013, 1, 1, 1, 1).toDate();
    private static final Date EXAM_END_DATE = new DateTime(2022, 1, 1, 1, 1).toDate();
    private static final int MAX_ATTACHMENTS = 2;
    private static final int MAX_EXAMS = 2;
    private static final int MAX_EXAMS_DAYS = 2;
    public static final Integer[] HAKUKOHTEET_KOODISTO_ARVO = new Integer[]{
        582, 498, 490, 186
    };
    private static final String OID_TYPE = "AO_";
    private WebResource hakukohdeResource;
    private String orgOid;
    private String tarjontaServiceCasTicket;
    private String id;

    public HakukohdeGenerator() {
        super(OID_TYPE);
    }

    public HakukohdeGenerator(WebResource hakukohdeResource, final String tarjontaServiceCasTicket, final String orgOid) {
        super(OID_TYPE);
        this.tarjontaServiceCasTicket = tarjontaServiceCasTicket;
        this.hakukohdeResource = hakukohdeResource;
        this.orgOid = orgOid;
        this.id = generateDate();

    }

    public void create(final String hakuOid, final String hakukohdeKoodiarvo, final String komotoBaseOid, final List<String> komotoOids) {
        Preconditions.checkNotNull(hakuOid, "Haku OID cannot be null.");
        Preconditions.checkNotNull(komotoBaseOid, "List of KOMOTO OID cannot be null.");
        List<String> komotoOid = Lists.<String>newArrayList();
        komotoOid.add(komotoBaseOid);
        post(createHakukohde(hakuOid, orgOid, komotoOid, hakukohdeKoodiarvo));

        if (!komotoOids.isEmpty()) {
            post(createHakukohde(hakuOid, orgOid, komotoOids, hakukohdeKoodiarvo));
        }
    }

    private void post(HakukohdeV1RDTO hakukohde) {
        ResultV1RDTO<HakukohdeV1RDTO> post = hakukohdeResource.
                queryParam("ticket", tarjontaServiceCasTicket).
                accept(MediaType.APPLICATION_JSON + ";charset=UTF-8").
                header("Content-Type", "application/json; charset=UTF-8").
                header("Cookie", GenerateTestData.getJsessionId(tarjontaServiceCasTicket)).
                post(new GenericType<ResultV1RDTO<HakukohdeV1RDTO>>() {
                }, hakukohde);
        if (post.getErrors() != null) {
            for (ErrorV1RDTO e : post.getErrors()) {
                LOG.error("Error key : {}", e.getErrorMessageKey());
            }
        }
    }

    private HakukohdeV1RDTO createHakukohde(final String hakuOid, final String orgOid, final List<String> komotoOids, final String koodiarvo) {
        Preconditions.checkNotNull(koodiarvo, "Koodisto hakukohde code value cannot be null.");
        LOG.debug("bind the hakukohde to haku OID {} and tutkinto OIDs {}", hakuOid, komotoOids);
        HakukohdeV1RDTO dto = new HakukohdeV1RDTO();
        dto.setTila("JULKAISTU");

        dto.getHakukohdeKoulutusOids().addAll(komotoOids);

        dto.setHakuOid(hakuOid);
        dto.setHakuaikaAlkuPvm(new DateTime(2014, 1, 1, 1, 1).toDate());
        dto.setHakuaikaLoppuPvm(new DateTime(2020, 8, 1, 1, 1).toDate());
        // dto.setHakukohteenNimi("nimi " + hakuOid);
        HashMap<String, String> nimet = Maps.<String, String>newHashMap();
        nimet.put(LANGUAGE_URI_FI, this.id + " " + hakuOid + " fi");
        nimet.put(LANGUAGE_URI_SV, this.id + " " + hakuOid + " sv");
        dto.setHakukohteenNimet(nimet);

        HashMap<String, String> lisatiedot = Maps.<String, String>newHashMap();
        lisatiedot.put(LANGUAGE_URI_FI, LOREM);
        lisatiedot.put(LANGUAGE_URI_SV, LOREM);
        dto.setLisatiedot(lisatiedot);
        dto.setAloituspaikatLkm(2);
//        dto.setAlinHyvaksyttavaKeskiarvo(10);
//        dto.setAlinValintaPistemaara(10);
//        dto.setEdellisenVuodenHakijatLkm(100);

        dto.setKaytetaanHaunPaattymisenAikaa(Boolean.FALSE);

        dto.setSahkoinenToimitusOsoite(createUri(hakuOid));

        HashMap<String, String> hakukelpoisuus = Maps.<String, String>newHashMap();
        hakukelpoisuus.put(LANGUAGE_URI_FI, LOREM);
        hakukelpoisuus.put(LANGUAGE_URI_SV, LOREM);
        dto.setHakukelpoisuusVaatimusKuvaukset(hakukelpoisuus);

        List<String> vaatimukset = Lists.<String>newArrayList();
        vaatimukset.add("pohjakoulutusvaatimuskorkeakoulut_109");
        vaatimukset.add("pohjakoulutusvaatimuskorkeakoulut_108");
        dto.setHakukelpoisuusvaatimusUris(vaatimukset);

//        dto.setValintaperustekuvausKoodiUri(KoodistoUtil.toKoodiUri(KoodistoURI.KOODISTO_VALINTAPERUSTEKUVAUSRYHMA_URI, "4"));
//        dto.setSoraKuvausKoodiUri(KoodistoUtil.toKoodiUri(KoodistoURI.KOODISTO_HAKUKOHDE_URI, "1"));
        dto.setModifiedBy(UPDATED_BY_USER);
        dto.setModified(UPDATED_DATE);
        Set<String> orgOids = Sets.<String>newHashSet();
        orgOids.add(orgOid);
        dto.setTarjoajaOids(orgOids);

        OsoiteRDTO osoiteRDTO = new OsoiteRDTO();
        osoiteRDTO.setCreated(DATE);
        osoiteRDTO.setCreatedBy(UPDATED_BY_USER);
        osoiteRDTO.setOsoiterivi1("osoite1");
        osoiteRDTO.setOsoiterivi2("osoite2");
        osoiteRDTO.setPostinumero("13200");
        osoiteRDTO.setPostinumeroArvo("13200");
        osoiteRDTO.setPostitoimipaikka("HELSINKI");

        dto.setLiitteidenToimitusOsoite(osoiteRDTO);
        dto.setSahkoinenToimitusOsoite("email@oph.fi");
        return dto;
    }

    private List<HakukohdeLiiteTyyppi> createLiittees(final String hakuOid) {
        List<HakukohdeLiiteTyyppi> types = new ArrayList<HakukohdeLiiteTyyppi>();

        for (int i = 0; i < MAX_ATTACHMENTS; i++) {
            HakukohdeLiiteTyyppi tyyppi = new HakukohdeLiiteTyyppi();
            tyyppi.setLiitteenKuvaus(createKoodiUriLorem());
            tyyppi.setLiitteenToimitusOsoite(createPostiosoite());
            tyyppi.setLiitteenTyyppi(KoodistoUtil.toKoodiUri(KoodistoURI.KOODISTO_LIITTEEN_TYYPPI_URI, "1"));
            tyyppi.setSahkoinenToimitusOsoite(createUri(hakuOid));
            tyyppi.setLiitteenTyyppiKoodistoNimi("???");
            tyyppi.setToimitettavaMennessa(DATE);

            tyyppi.setViimeisinPaivittajaOid(UPDATED_BY_USER);
            tyyppi.setViimeisinPaivitysPvm(UPDATED_DATE);
            types.add(tyyppi);
        }

        return types;
    }

    private List<ValintakoeTyyppi> createValintakoes() {
        List<ValintakoeTyyppi> types = new ArrayList<ValintakoeTyyppi>();

        for (int i = 0; i < MAX_EXAMS; i++) {
            ValintakoeTyyppi tyyppi = new ValintakoeTyyppi();
            tyyppi.setKuvaukset(createKoodiUriLorem());
            tyyppi.setLisaNaytot(createKoodiUriLorem());
            // hakukohdeLiiteTyyppi.setValintakokeenTunniste(hakuOid + " " + komotoOid); //long ID not String OID
            tyyppi.setValintakokeenTyyppi("???");
            tyyppi.setViimeisinPaivittajaOid(UPDATED_BY_USER);
            tyyppi.setViimeisinPaivitysPvm(UPDATED_DATE);

            for (int indexDays = 0; indexDays < MAX_EXAMS_DAYS; indexDays++) {
                AjankohtaTyyppi ajankohtaTyyppi = new AjankohtaTyyppi();
                ajankohtaTyyppi.setAlkamisAika(EXAM_START_DATE);
                ajankohtaTyyppi.setPaattymisAika(EXAM_END_DATE);
                ajankohtaTyyppi.setKuvaus(LOREM.substring(0, 30));
                ajankohtaTyyppi.setValintakoeAjankohtaOsoite(createPostiosoite());
                tyyppi.getAjankohdat().add(ajankohtaTyyppi);
            }

            types.add(tyyppi);
        }

        return types;
    }

    private String createUri(final String hakuOid) {
        return "www.oph.fi/" + hakuOid + "/loremipsumdolorsitametconsecteturadipiscingelitintegersitametodioegetmetusporttitorrhoncusvitaeatnisi";
    }
}
