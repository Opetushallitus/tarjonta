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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import fi.vm.sade.tarjonta.data.util.KoodistoUtil;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuaikaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.types.SisaisetHakuAjat;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import java.util.ArrayList;

import java.util.Date;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jani Wilén
 */
@Component
@Configurable(preConstruction = false)
public class HakuGenerator extends AbstractGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(HakuGenerator.class);

    private static final String OID_TYPE = "AS_";
    private static final Date DATE_HAKUAIKA_BEGIN = new DateTime(2014, 1, 1, 1, 1).toDate();
    private static final Date DATE_HAKUAIKA_END = new DateTime(2020, 1, 1, 1, 1).toDate();
    private WebResource hakuResource;

    public HakuGenerator() {
        super(OID_TYPE);
    }

    public HakuGenerator(WebResource hakuResource) {
        super(OID_TYPE);
        this.hakuResource = hakuResource;
    }

    public String create() {
        final HakuV1RDTO dto = createHaku();

        ResultV1RDTO<HakuV1RDTO> post = hakuResource.
                accept(MediaType.APPLICATION_JSON + ";charset=UTF-8").
                header("Content-Type", "application/json; charset=UTF-8").
                post(new GenericType<ResultV1RDTO<HakuV1RDTO>>() {
                }, dto);

        if (post.getErrors() != null) {
            for (ErrorV1RDTO e : post.getErrors()) {
                LOG.error("Error key : {}", e.getErrorMessageKey());
            }
        }

        return post.getResult().getOid();
    }

    private HakuV1RDTO createHaku() {

        HakuV1RDTO dto = new HakuV1RDTO();
        final String id = generateDate();
        dto.setTila("JULKAISTU");
        dto.setHakukausiVuosi(2014);
        dto.setHakukausiUri(KoodistoUtil.toKoodiUri(KoodistoURI.KOODISTO_ALKAMISKAUSI_URI, "k"));
        dto.setKoulutuksenAlkamiskausiUri(KoodistoUtil.toKoodiUri(KoodistoURI.KOODISTO_ALKAMISKAUSI_URI, "k"));
        dto.setKoulutuksenAlkamisVuosi(2014);
        dto.setHakutyyppiUri("hakutyyppi_01#1");
        dto.setHakutapaUri(KoodistoUtil.toKoodiUri(KoodistoURI.KOODISTO_HAKUTAPA_URI, "01"));
        dto.setKohdejoukkoUri("haunkohdejoukko_12#1");
        dto.setHaunTunniste(dto.getOid());
        ArrayList<HakuaikaV1RDTO> newArrayList = Lists.<HakuaikaV1RDTO>newArrayList();
        HakuaikaV1RDTO hakuaikaV1RDTO = new HakuaikaV1RDTO();
        hakuaikaV1RDTO.setAlkuPvm(DATE_HAKUAIKA_BEGIN);
        hakuaikaV1RDTO.setLoppuPvm(DATE_HAKUAIKA_END);
        hakuaikaV1RDTO.setNimi("KK haku " + id);
        newArrayList.add(hakuaikaV1RDTO);

        dto.setHakuaikas(newArrayList);

        dto.setSijoittelu(true);
        dto.setKoulutuksenAlkamisVuosi(2014);
        Map<String, String> newHashSet = Maps.newHashMap();
        newHashSet.put(LANGUAGE_URI_FI, id + " fi");
        newHashSet.put(LANGUAGE_URI_SV, id + " sv");
        dto.setNimi(newHashSet);
        SisaisetHakuAjat sisaisetHakuAjat = new SisaisetHakuAjat();
        sisaisetHakuAjat.setHakuajanKuvaus("HakuajanKuvaus " + dto.getOid());
        sisaisetHakuAjat.setSisaisenHaunAlkamisPvm(DATE_HAKUAIKA_BEGIN);
        sisaisetHakuAjat.setSisaisenHaunPaattymisPvm(DATE_HAKUAIKA_END);
        dto.setCreated(UPDATED_DATE);
        dto.setCreatedBy(UPDATED_BY_USER);
        dto.setMaxHakukohdes(100);
        dto.setHaunTunniste(id);

        return dto;
    }
}
