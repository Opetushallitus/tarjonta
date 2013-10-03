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
package fi.vm.sade.tarjonta.service.impl.conversion.rest;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import fi.vm.sade.generic.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.oid.service.types.NodeClassCode;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.KoodistoUri;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.Yhteyshenkilo;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.resources.dto.KoodiUriDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KorkeakouluDTO;
import fi.vm.sade.tarjonta.service.resources.dto.UiDTO;
import fi.vm.sade.tarjonta.service.resources.dto.UiMetaDTO;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Jani Wilén
 */
public class KorkeakouluDTOConverterToKomoto extends AbstractToDomainConverter<KorkeakouluDTO, KoulutusmoduuliToteutus> {

    private static final Logger LOG = LoggerFactory.getLogger(KorkeakouluDTOConverterToKomoto.class);
    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    @Autowired
    private OIDService oidService;

    @Override
    public KoulutusmoduuliToteutus convert(KorkeakouluDTO dto) {
        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
        if (dto == null) {
            return komoto;
        }

        Koulutusmoduuli komo = new Koulutusmoduuli();
        if (dto.getOid() != null) {
            //update komo & komoto
            komoto = koulutusmoduuliToteutusDAO.findByOid(dto.getOid());
            komo = komoto.getKoulutusmoduuli();
        } else {
            //insert new komo&komoto data to database.
            komoto.setKoulutusmoduuli(komo);
            try {
                komo.setOid(oidService.newOid(NodeClassCode.TEKN_5));
                komoto.setOid(oidService.newOid(NodeClassCode.TEKN_5));
            } catch (ExceptionMessage ex) {
                LOG.error("OIDService failed!", ex);
            }
        }

        /*
         * KOMO data fields:
         */
        komo.setOmistajaOrganisaatioOid(dto.getOrganisaatioOid());
        komo.setKoulutusAste(convertToUri(dto.getKoulutusaste(), "koulutusaste"));
        komo.setKoulutusala(convertToUri(dto.getKoulutusala(), "koulutusala"));
        komo.setOpintoala(convertToUri(dto.getOpintoala(), "opintoala"));
        //komo.get(getUri(dto.getTutkinto(), "tutkinto")); TODO???
        komo.setTutkintonimike(convertToUri(dto.getTutkintonimike(), "tutkintonimike"));
        komo.setEqfLuokitus(convertToValue(dto.getEqf(), "EQF-luokitus")); //not a koodisto koodi URI
        komo.setModuuliTyyppi(KoulutusmoduuliTyyppi.valueOf(dto.getKoulutusmoduuliTyyppi().name()));
        komo.setKoulutusKoodi(convertToUri(dto.getKoulutuskoodi(), "koulutuskoodi"));
        komo.setNimi(convertToTexts(dto.getKoulutusohjelma(), komo.getNimi(), "koulutusohjelma"));
        komo.setUlkoinenTunniste(dto.getTunniste());

        /*
         * KOMOTO data fields
         */
        komoto.setTarjoaja(dto.getOrganisaatioOid());
        komoto.setMaksullisuus(dto.getOpintojenMaksullisuus().toString());
        komoto.setKoulutuksenAlkamisPvm(dto.getKoulutuksenAlkamisPvm());
        komoto.setTeemas(convertToUris(dto.getTeemas(), komoto.getTeemas(), "teemas"));
        komoto.setOpetuskieli(convertToUris(dto.getOpetuskielis(), komoto.getOpetuskielis(), "opetuskielis"));
        komoto.setOpetusmuoto(convertToUris(dto.getOpetusmuodos(), komoto.getOpetusmuotos(), "opetusmuodos"));
        komoto.setKkPohjakoulutusvaatimus(convertToUris(dto.getPohjakoulutusvaatimukset(), komoto.getKkPohjakoulutusvaatimus(), "pohjakoulutusvaatimukset"));
        komoto.setSuunniteltuKesto(convertToUri(dto.getSuunniteltuKestoTyyppi(), "SuunniteltuKestoTyyppi"), komoto.getSuunniteltuKestoArvo());
        HashSet<Yhteyshenkilo> yhteyshenkilos = Sets.<Yhteyshenkilo>newHashSet(komoto.getYhteyshenkilos());
        EntityUtils.copyYhteyshenkilos(dto.getYhteyshenkilos(), yhteyshenkilos);
        komoto.setYhteyshenkilos(yhteyshenkilos);
        return komoto;
    }

    private static String convertToUri(final KoodiUriDTO dto, final String msg) {
        Preconditions.checkNotNull(dto, "KoodiUriDTO object cannot be null! Error in field : " + msg);
        Preconditions.checkNotNull(dto.getUri(), "KoodiUriDTO's koodisto koodi URI cannot be null! Error in field : " + msg);
        Preconditions.checkNotNull(dto.getVersio(), "KoodiUriDTO's koodisto koodi version cannot be null! Error in field : " + msg);
        return new StringBuilder(dto.getUri())
                .append('#')
                .append(dto.getVersio()).toString();
    }

    private static String convertToUri(final UiDTO dto, final String msg) {
        Preconditions.checkNotNull(dto, "UiDTO object cannot be null! Error field : " + msg);
        Preconditions.checkNotNull(dto.getKoodi(), "UiDTO's KoodiUriDTO object cannot be null! Error in field : " + msg);
        return convertToUri(dto.getKoodi(), msg);
    }

    private static Set<KoodistoUri> convertToUris(final UiMetaDTO dto, Set<KoodistoUri> koodistoUris, final String msg) {
        Preconditions.checkNotNull(dto, "UiDTO object cannot be null! Error field : " + msg);

        Set<KoodistoUri> modifiedUris = Sets.<KoodistoUri>newHashSet(koodistoUris);
        if (koodistoUris == null) {
            modifiedUris = Sets.<KoodistoUri>newHashSet();
        }

        for (UiDTO uiDto : dto.getMeta().values()) {
            Preconditions.checkNotNull(uiDto.getKoodi(), "UI text's KoodiUriDTO object cannot be null! Error in field : " + msg);
            modifiedUris.add(new KoodistoUri(convertToUri(uiDto.getKoodi(), msg)));
        }

        return modifiedUris;
    }

    private static MonikielinenTeksti convertToText(final UiMetaDTO dto, MonikielinenTeksti mt, final String msg) {
        Preconditions.checkNotNull(dto, "UiListDTO object cannot be null! Error field : " + msg);
        Preconditions.checkNotNull(dto.getMeta(), "UiListDTO's map of UiDTO objects cannot be null! Error in field : " + msg);
        Preconditions.checkNotNull(dto.getArvo(), "UI text object value cannot be null! Error in field : " + msg);

        if (mt == null) {
            mt = new MonikielinenTeksti();
        }
        mt.addTekstiKaannos(convertToUri(dto.getKoodi(), msg), dto.getArvo());
        return mt;
    }

    private static MonikielinenTeksti convertToTexts(final UiMetaDTO dto, MonikielinenTeksti mt, final String msg) {
        Preconditions.checkNotNull(dto, "UiListDTO object cannot be null! Error field : " + msg);
        Preconditions.checkNotNull(dto.getMeta(), "UiListDTO's map of UiDTO objects cannot be null! Error in field : " + msg);

        if (mt == null) {
            mt = new MonikielinenTeksti();
        }

        for (UiDTO uiDto : dto.getMeta().values()) {
            Preconditions.checkNotNull(uiDto.getKoodi(), "UI text's KoodiUriDTO object cannot be null! Error in field : " + msg);
            Preconditions.checkNotNull(uiDto.getKoodi().getArvo(), "UI text's KoodiUriDTO object value cannot be null! Error in field : " + msg);
            mt.addTekstiKaannos(convertToUri(uiDto.getKoodi(), msg), uiDto.getArvo());
        }

        return mt;
    }

    private static String convertToValue(final UiDTO dto, final String msg) {
        Preconditions.checkNotNull(dto, "UiDTO object cannot be null! Error in field : " + msg);
        Preconditions.checkNotNull(dto.getArvo(), "UiDTO's value cannot be null! Error in field : " + msg);

        return dto.getArvo();
    }
}
