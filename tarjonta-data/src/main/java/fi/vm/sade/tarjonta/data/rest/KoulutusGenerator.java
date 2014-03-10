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
import fi.vm.sade.tarjonta.data.test.GenerateTestData;
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
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Jani Wilén
 */
@Component
@Configurable(preConstruction = false)
public class KoulutusGenerator extends AbstractGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusGenerator.class);
    private static final String OID_TYPE = "LOI_";
    private static final Date DATE = new DateTime(2014, 1, 1, 1, 1).toDate();
    private static final String JSON_UTF8 = MediaType.APPLICATION_JSON + ";charset=UTF-8";
    //private static final Random random = new Random(System.currentTimeMillis());

    private List< Map.Entry<String, String>> komoPairs = new ArrayList< Map.Entry<String, String>>();

    private String threadName;
    private WebResource tarjontaKoulutusRest;

    private Map<KTYPE, KoulutusmoduuliKorkeakouluRelationV1RDTO> map = Maps.<KTYPE, KoulutusmoduuliKorkeakouluRelationV1RDTO>newHashMap();

    private String ticket;
    private String jsessionId;
    private String id;
    private String tarjontaServiceTicket;

    public enum KTYPE {

        kandi, maisteri, wrapper, amk
    }

    public KoulutusGenerator() {
        super(OID_TYPE);
    }

    public KoulutusGenerator(String threadName, String tarjontaServiceTicket, WebResource tarjontaKoulutusRest, WebResource permissionResource, final boolean isAmk) throws IOException {
        super(OID_TYPE);
        this.threadName = threadName;
        this.tarjontaServiceTicket = tarjontaServiceTicket;
        this.tarjontaKoulutusRest = tarjontaKoulutusRest;
        this.id = generateDate();

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
        final String writeValueAsString = mapper.writeValueAsString(dto);
        LOG.info("tarjontaServiceTicket : '{}'", tarjontaServiceTicket);

        ResultV1RDTO<KoulutusKorkeakouluV1RDTO> post =  tarjontaKoulutusRest.path("KORKEAKOULUTUS").
                queryParam("ticket", tarjontaServiceTicket).
                accept(MediaType.APPLICATION_JSON + ";charset=UTF-8").
                header("Content-Type", "application/json; charset=UTF-8").
                header("Cookie", GenerateTestData.getJsessionId(tarjontaServiceTicket)).
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
