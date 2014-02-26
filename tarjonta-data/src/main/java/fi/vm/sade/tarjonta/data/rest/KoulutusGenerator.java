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

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import fi.vm.sade.tarjonta.data.util.KoodistoUtil;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import java.util.ArrayList;

import java.util.Date;
import java.util.Map;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

//import com.fasterxml.jackson.core.type.TypeReference;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusmoduuliKorkeakouluRelationV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import java.io.IOException;

import javax.ws.rs.core.MediaType;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Jani Wilén
 */
@Component
@Configurable(preConstruction = false)
public class KoulutusGenerator extends AbstractGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusGenerator.class);
    ;
    private static final String OID_TYPE = "LOI_";
    private static final Date DATE = new DateTime(2014, 1, 1, 1, 1).toDate();
    private static final String JSON_UTF8 = MediaType.APPLICATION_JSON + ";charset=UTF-8";
    //private static final Random random = new Random(System.currentTimeMillis());

    private List< Map.Entry<String, String>> komoPairs = new ArrayList< Map.Entry<String, String>>();

    private String threadName;
    private WebResource tarjontaKoulutusRest;
    private WebResource permissionResource;

    private Map<KTYPE, KoulutusmoduuliKorkeakouluRelationV1RDTO> map = Maps.<KTYPE, KoulutusmoduuliKorkeakouluRelationV1RDTO>newHashMap();

    private String ticket;
    private String jsessionId;
    private String id;

    public enum KTYPE {

        kandi, maisteri, wrapper, amk
    }

    public KoulutusGenerator() {
        super(OID_TYPE);
    }

    public KoulutusGenerator(String threadName, WebResource tarjontaKoulutusRest, WebResource permissionResource, final boolean isAmk) throws IOException {
        super(OID_TYPE);
        this.threadName = threadName;
        this.tarjontaKoulutusRest = tarjontaKoulutusRest;
        this.permissionResource = permissionResource;
        this.id = generateDate();

//
//        this.ticket = CasClient.getTicket(KorkeakoulutusDataUploader.ENV_CAS + "/cas", "ophadmin", "ilonkautta!", KorkeakoulutusDataUploader.TARJONTA_SERVICE);
////        ResultV1RDTO<String> resource = permissionResource.path("authorize").
////                accept(MediaType.APPLICATION_JSON + ";charset=UTF-8").
////                header("Content-Type", "application/json; charset=UTF-8").
////                header("CasSecurityTicket", this.ticket).
////                header("Connection", "keep-alive").
////                header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8").
////                header("Accept-Encoding", "gzip,deflate,sdch").
////                header("Accept-Language", "en,en-US;q=0.8,fi;q=0.6").
////                header("Content-Type", "application/json; charset=UTF-8").
////                header("Host", "itest-virkailija.oph.ware.fi").
////                header("Cookie", "JSESSIONID=8365F4958766DF67077A193F4F778E6C;").
////                header("DNT", "1").
////                header("Origin", KorkeakoulutusDataUploader.ENV).
////                header("Referer", KorkeakoulutusDataUploader.TARJONTA_SERVICE).
////                header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36").
////                get(new GenericType<ResultV1RDTO<String>>() {
////                });
//
//        HttpClient client = new HttpClient();
//
//        HttpMethod request1 = new GetMethod("http://localhost:8585/tarjonta-service/rest/v1/permission/authorize");
//        request1.setRequestHeader("CasSecurityTicket", ticket);
//        request1.setRequestHeader("Connection", "keep-alive");
//        request1.setRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
//        request1.setRequestHeader("Accept-Encoding", "gzip,deflate,sdch");
//        request1.setRequestHeader("Accept-Language", "en,en-US;q=0.8,fi;q=0.6");
//        request1.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
//        request1.setRequestHeader("Host", "itest-virkailija.oph.ware.fi");
//        request1.setRequestHeader("Cookie", "JSESSIONID=8365F4958766DF67077A193F4F778E6C;");
//        request1.setRequestHeader("DNT", "1");
//        request1.setRequestHeader("Origin", KorkeakoulutusDataUploader.ENV);
//        request1.setRequestHeader("Referer", KorkeakoulutusDataUploader.TARJONTA_SERVICE);
//        request1.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36");
//
//        int executeMethod = client.executeMethod(request1);
//        System.out.println("executeMethod :" + executeMethod);
//        this.jsessionId = getJsessionId(request1, client);
        if (isAmk) {
            map.put(KTYPE.amk, getKoodiRelations("621101"));
        } else {
            map.put(KTYPE.amk, getKoodiRelations("621101"));
            map.put(KTYPE.kandi, getKoodiRelations("612103"));
            KoulutusmoduuliKorkeakouluRelationV1RDTO koodiRelations = getKoodiRelations("712102");
            map.put(KTYPE.maisteri, koodiRelations);
            map.put(KTYPE.wrapper, koodiRelations);
        }
    }

    public KoulutusKorkeakouluV1RDTO create(final String organisationOid, final KTYPE type) throws IOException {
        Preconditions.checkNotNull(organisationOid, "Organisation OID cannot be null.");
        Preconditions.checkNotNull(type, "Koulutus enum cannot be null.");
        LOG.info("TYPE : {}", type);

        KoulutusKorkeakouluV1RDTO dto = new KoulutusKorkeakouluV1RDTO();

        dto.setTila(fi.vm.sade.tarjonta.shared.types.TarjontaTila.JULKAISTU);
        dto.setOrganisaatio(new OrganisaatioV1RDTO(organisationOid, organisationOid, null));

        KoulutusmoduuliKorkeakouluRelationV1RDTO relation = map.get(type);
        Preconditions.checkNotNull(relation, "Relation data object cannot be null.");

        dto.setKoulutusaste(relation.getKoulutusaste());
        dto.setKoulutusala(relation.getKoulutusala());
        dto.setOpintoala(relation.getOpintoala());
        dto.setTutkinto(relation.getTutkinto());
        dto.setEqf((relation.getEqf()));
        dto.setTila(fi.vm.sade.tarjonta.shared.types.TarjontaTila.JULKAISTU);

        dto.setKoulutusmoduuliTyyppi(fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.TUTKINTO);

        dto.setTunniste(this.id);
        dto.setHinta(1.11);
        dto.setOpintojenMaksullisuus(Boolean.TRUE);

        dto.setKoulutuskoodi(relation.getKoulutuskoodi());
        dto.setKoulutusasteTyyppi(KoulutusasteTyyppi.KORKEAKOULUTUS);

        dto.getKoulutuksenAlkamisPvms().add(DATE);

        KoodistoUtil.koodiUrisMap(dto.getTutkintonimikes(), "tutkintonimikekk_121");
        KoodistoUtil.koodiUrisMap(dto.getOpetusAikas(), "opetusaikakk_1", "opetusaikakk_2");
        KoodistoUtil.koodiUrisMap(dto.getOpetusPaikkas(), "opetusaikakk_1", "opetuspaikkakk_2");
        KoodistoUtil.koodiUrisMap(dto.getAihees(), "aiheet_32", "aiheet_31");
        KoodistoUtil.koodiUrisMap(dto.getOpetuskielis(), "kieli_fi", "kieli_sv");
        KoodistoUtil.koodiUrisMap(dto.getOpetusmuodos(), "opetusmuotokk_2");
        KoodistoUtil.koodiUrisMap(dto.getAmmattinimikkeet(), "ammattiluokitus_73530-2");
        //KoodistoUtil.koodiUrisMap(dto.getPohjakoulutusvaatimukset());

        dto.setSuunniteltuKestoTyyppi(KoodistoUtil.toKoodiUri("suunniteltukesto_01"));
        dto.setSuunniteltuKestoArvo(
                "1-5");

        dto.getYhteyshenkilos(); //TODO!!!
        KuvausV1RDTO<KomoTeksti> komoKuvaus = new KuvausV1RDTO<KomoTeksti>();
        for (KomoTeksti k : KomoTeksti.values()) {
            NimiV1RDTO nimi = new NimiV1RDTO();
            nimi.getTekstis().put(LANGUAGE_URI_FI, LOREM);
            nimi.getTekstis().put(LANGUAGE_URI_SV, LOREM);
            komoKuvaus.put(k, nimi);
        }
        dto.setKuvausKomo(komoKuvaus);
        KuvausV1RDTO<KomotoTeksti> komotoKuvaus = new KuvausV1RDTO<KomotoTeksti>();
        for (KomotoTeksti k : KomotoTeksti.values()) {
            NimiV1RDTO nimi = new NimiV1RDTO();
            nimi.getTekstis().put(LANGUAGE_URI_FI, LOREM);
            nimi.getTekstis().put(LANGUAGE_URI_SV, LOREM);
            komotoKuvaus.put(k, nimi);
        }
        dto.setKuvausKomoto(komotoKuvaus);

        String opintojenLaajuusarvoUri = null;
        Set<Map.Entry<String, Integer>> entrySet = relation.getOpintojenLaajuusarvos().getUris().entrySet();
        for (Entry<String, Integer> e : entrySet) {
            opintojenLaajuusarvoUri = e.getKey();
            break;
        }
        if (opintojenLaajuusarvoUri == null) {
            opintojenLaajuusarvoUri = "opintojenlaajuus_2";
        }

        dto.setOpintojenLaajuusarvo(KoodistoUtil.toKoodiUri(opintojenLaajuusarvoUri));

        if (relation.getOpintojenLaajuusyksikko() != null && relation.getOpintojenLaajuusyksikko().getArvo() == null) {
            dto.setOpintojenLaajuusyksikko(relation.getOpintojenLaajuusyksikko());
        } else {
            dto.setOpintojenLaajuusyksikko(KoodistoUtil.toKoodiUri("opintojenlaajuus_120"));
        }

        createKoulutusohjelmaNames(dto, type, relation.getKoulutuskoodi().getArvo());

        ObjectMapper mapper = new ObjectMapper();
        String writeValueAsString = mapper.writeValueAsString(dto);
        ResultV1RDTO<KoulutusKorkeakouluV1RDTO> post = tarjontaKoulutusRest.path("KORKEAKOULUTUS").
                accept(MediaType.APPLICATION_JSON + ";charset=UTF-8").
                header("Content-Type", "application/json; charset=UTF-8").
                header("CasSecurityTicket", ticket).
                header("Connection", "keep-alive").
                header("Cookie", jsessionId).
                post(new GenericType<ResultV1RDTO<KoulutusKorkeakouluV1RDTO>>() {
                }, writeValueAsString);

        KoulutusKorkeakouluV1RDTO result = post.getResult();

        if (post.getErrors() != null) {
            for (ErrorV1RDTO e : post.getErrors()) {
                LOG.error("Error key : {} {}", relation.getKoulutuskoodi().getArvo(), e.getErrorMessageKey());
            }
        }

        Preconditions.checkNotNull(result, "Koulutus save failed?");
        Preconditions.checkNotNull(result.getKomotoOid(), "Komoto OID cannot be null.");
        Preconditions.checkNotNull(result.getKomoOid(), "Komo OID cannot be null.");

        return result;
    }

    private String getJsessionId(HttpMethod method, HttpClient client) throws IOException {

        String responseTxt = method.getResponseBodyAsString();

        System.out.println("----\n\nStatus : " + method.getStatusCode());
        System.out.println("\nURI: " + method.getURI());
        System.out.println("\nResponse Path: " + method.getPath());
        System.out.println("\nRequest Headers: " + method.getRequestHeaders().length);
        for (Header h : method.getRequestHeaders()) {
            System.out.println("  " + h.getName() + " = " + h.getValue());
        }

        String jsessionId = "";
        System.out.println("\nCookies: " + client.getState().getCookies().length);
        for (org.apache.commons.httpclient.Cookie c : client.getState().getCookies()) {
            jsessionId = c.getName() + " = " + c.getValue();
            System.out.println("  " + jsessionId);
            break;
        }
        System.out.println("Response Text: ");
        System.out.println(responseTxt);

        return jsessionId;
    }

    private KoulutusmoduuliKorkeakouluRelationV1RDTO getKoodiRelations(final String koulutuskoodi) {
        WebResource.Builder bulder = tarjontaKoulutusRest.
                path("koulutuskoodi").
                path(koulutuskoodi).
                path("Korkeakoulutus").accept(JSON_UTF8);
        ResultV1RDTO<KoulutusmoduuliKorkeakouluRelationV1RDTO> get = bulder.get(new GenericType<ResultV1RDTO<KoulutusmoduuliKorkeakouluRelationV1RDTO>>() {
        });

        if (!get.getStatus().equals(ResultV1RDTO.ResultStatus.OK)) {
            throw new RuntimeException("ERROR, DATA NOT FOUND! " + tarjontaKoulutusRest.getURI());
        }

        return get.getResult();
    }

    private void createKoulutusohjelmaNames(KoulutusKorkeakouluV1RDTO dto, KTYPE text, final String koulutuskoodi) {        
        dto.getKoulutusohjelma().getTekstis().put(LANGUAGE_URI_FI, KoodistoUtil.toNimiValue(id + " " + text + " " + koulutuskoodi, LANGUAGE_URI_FI));
        dto.getKoulutusohjelma().getTekstis().put(LANGUAGE_URI_SV, KoodistoUtil.toNimiValue(id + " " + text + " " + koulutuskoodi, LANGUAGE_URI_SV));
    }
}
