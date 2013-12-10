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
import fi.vm.sade.koodisto.service.types.common.KoodiType;
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
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.MetaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Jani Wilén
 */
public class KoulutusKorkeakouluDTOConverterToEntity extends AbstractToDomainConverter<KoulutusKorkeakouluV1RDTO, KoulutusmoduuliToteutus> {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusKorkeakouluDTOConverterToEntity.class);
    @Autowired(required = true)
    private CommonRestKoulutusConverters<KomoTeksti> komoKoulutusConverters;
    @Autowired(required = true)
    private CommonRestKoulutusConverters<KomotoTeksti> komotoKoulutusConverters;
    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    @Autowired
    private OIDService oidService;
    @Autowired
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;

    @Override
    public KoulutusmoduuliToteutus convert(KoulutusKorkeakouluV1RDTO dto) {
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
        final String organisationOId = dto.getOrganisaatio().getOid();
       
       
        komo.setTutkintoOhjelmanNimi(convertToUri(dto.getTutkinto(), "tutkinto")); //correct data mapping?
        komo.setLaajuus(null, convertToUri(dto.getOpintojenLaajuus(), "laajuus")); //TODO : missing type
        komo.setOmistajaOrganisaatioOid(organisationOId); //is this correct?
        komo.setKoulutusAste(convertToUri(dto.getKoulutusaste(), "koulutusaste"));
        komo.setKoulutusala(convertToUri(dto.getKoulutusala(), "koulutusala"));
        komo.setOpintoala(convertToUri(dto.getOpintoala(), "opintoala"));
        //komo.get(getUri(dto.getTutkinto(), "tutkinto")); TODO???
        komo.setTutkintonimike(convertToUri(dto.getTutkintonimike(), "tutkintonimike"));
        komo.setEqfLuokitus(convertToUri(dto.getEqf(), "EQF-luokitus")); //TODO: CHANGE THE BOOLEAN TO false
        komo.setTila(TarjontaTila.JULKAISTU); //is this correct state for a new komo?

        Preconditions.checkNotNull(dto.getKoulutusmoduuliTyyppi(), "KoulutusmoduuliTyyppi enum cannot be null.");
        komo.setModuuliTyyppi(KoulutusmoduuliTyyppi.valueOf(dto.getKoulutusmoduuliTyyppi().name()));
        komo.setKoulutusKoodi(convertToUri(dto.getKoulutuskoodi(), "koulutuskoodi"));

        komo.setNimi(MonikielinenTeksti.merge(komo.getNimi(), convertToTexts(dto.getKoulutusohjelma(), null, "koulutusohjelma text")));
        komo.setUlkoinenTunniste(dto.getTunniste());

        Preconditions.checkNotNull(dto.getKoulutusasteTyyppi(), "KoulutusasteTyyppi enum cannot be null.");
        komo.setKoulutustyyppi(dto.getKoulutusasteTyyppi().value());

        komoKoulutusConverters.convertTekstiDTOToMonikielinenTeksti(dto.getKuvausKomo(), komo.getTekstit());

        /*
         * KOMOTO data fields
         */
        komoto.setTila(dto.getTila());

        Preconditions.checkNotNull(organisationOId, "Organisation OID cannot be null.");
        komoto.setTarjoaja(organisationOId);
        Preconditions.checkNotNull(dto.getOpintojenMaksullisuus(), "OpintojenMaksullisuus boolean cannot be null.");
        komoto.setMaksullisuus(dto.getOpintojenMaksullisuus().toString());
        komoto.setKoulutuksenAlkamisPvm(dto.getKoulutuksenAlkamisPvm());
        komoto.setTeemas(convertToUris(dto.getTeemas(), komoto.getTeemas(), "teemas"));
        if (dto.getAihees() != null) {
//            System.out.println("aiheet:" + dto.getAihees());
//            System.out.println("converted aiheet:" + convertToUris(dto.getAihees(),komoto.getAihees(),"aihees"));
            komoto.getAihees().clear();
            komoto.getAihees().addAll(convertToUris(dto.getAihees(),new HashSet(),"aihees"));
        }
        komoto.setOpetuskieli(convertToUris(dto.getOpetuskielis(), komoto.getOpetuskielis(), "opetuskielis"));
        komoto.setOpetusmuoto(convertToUris(dto.getOpetusmuodos(), komoto.getOpetusmuotos(), "opetusmuodos"));
        if (dto.getOpetusAikas() != null) {
            komoto.setOpetusAikas(convertToUris(dto.getOpetusAikas(),komoto.getOpetusAikas(),"opetusaikas"));
        }
        if (dto.getOpetusPaikkas() != null) {
            komoto.setOpetusPaikkas(convertToUris(dto.getOpetusPaikkas(), komoto.getOpetusPaikkas(),"opetuspaikkas"));
        }
        komoto.setKkPohjakoulutusvaatimus(convertToUris(dto.getPohjakoulutusvaatimukset(), komoto.getKkPohjakoulutusvaatimus(), "pohjakoulutusvaatimukset"));
        komoto.setAmmattinimikes(convertToUris(dto.getAmmattinimikkeet(), komoto.getAmmattinimikes(), "ammattinimikkeet"));

        if (dto.getHinta() != null) {
            komoto.setHinta(new BigDecimal(dto.getHinta().toString()));
        }

        komoto.setSuunniteltuKesto(convertToUri(dto.getSuunniteltuKestoTyyppi(), "SuunniteltuKesto"), dto.getSuunniteltuKestoArvo());
        HashSet<Yhteyshenkilo> yhteyshenkilos = Sets.<Yhteyshenkilo>newHashSet(komoto.getYhteyshenkilos());
        EntityUtils.copyYhteyshenkilos(dto.getYhteyshenkilos(), yhteyshenkilos);
        komoto.setYhteyshenkilos(yhteyshenkilos);
        komotoKoulutusConverters.convertTekstiDTOToMonikielinenTeksti(dto.getKuvausKomoto(), komoto.getTekstit());
        return komoto;
    }

    private String convertToUri(final KoodiV1RDTO dto, final String msg) {
        Preconditions.checkNotNull(dto, "KoodiV1RDTO object cannot be null! Error in field : %s.", msg);
        Preconditions.checkNotNull(dto.getUri(), "KoodiV1RDTO's koodisto koodi URI cannot be null! Error in field : %s.", msg);
        Preconditions.checkNotNull(dto.getVersio(), "KoodiV1RDTO's koodisto koodi version for koodi '%s' cannot be null! Error in field : %s.", dto.getUri(), msg);

        return convertToKoodiUri(dto.getUri(), dto.getVersio(), msg);
    }

    private String convertToKoodiUri(final String uri, final Integer version, final String msg) {
        //check data
        Integer checkVersion = version;
        if (checkVersion == null || checkVersion == -1) {
            //search latest koodi version for the koodi uri.
            final KoodiType koodi = tarjontaKoodistoHelper.getKoodiByUri(uri);
            Preconditions.checkNotNull(koodi, "Koodisto koodi not found! Error in field : " + msg);
            checkVersion = koodi.getVersio();
        }

        return new StringBuilder(uri)
                .append('#')
                .append(checkVersion).toString();
    }

    private Set<KoodistoUri> convertToUris(final KoodiUrisV1RDTO dto, Set<KoodistoUri> koodistoUris, final String msg) {
        Preconditions.checkNotNull(dto, "UiDTO object cannot be null! Error field : " + msg);

        Set<KoodistoUri> modifiedUris = Sets.<KoodistoUri>newHashSet(koodistoUris);
        if (koodistoUris == null) {
            modifiedUris = Sets.<KoodistoUri>newHashSet();
        }

        for (Entry<String, Integer> uriWithVersion : dto.getUris().entrySet()) {
            modifiedUris.add(new KoodistoUri(convertToKoodiUri(uriWithVersion.getKey(), uriWithVersion.getValue(), msg)));
        }

        return modifiedUris;
    }

    private MonikielinenTeksti convertToTexts(final NimiV1RDTO dto, MonikielinenTeksti mt, final String msg) {
        Preconditions.checkNotNull(dto, "UiListDTO object cannot be null! Error field : " + msg);
        Preconditions.checkNotNull(dto.getMeta(), "UiListDTO's map of UiDTO objects cannot be null! Error in field : " + msg);

        if (mt == null) {
            mt = new MonikielinenTeksti();
        }

        for (Entry<String, String> kieliAndText : dto.getTekstis().entrySet()) {
            mt.addTekstiKaannos(kieliAndText.getKey(), kieliAndText.getValue());
        }

        return mt;
    }
}
