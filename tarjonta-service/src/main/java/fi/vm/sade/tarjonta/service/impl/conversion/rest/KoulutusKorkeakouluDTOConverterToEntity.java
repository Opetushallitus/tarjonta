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
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.KoodistoUri;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.Yhteyshenkilo;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.FieldNames;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidationMessages;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.search.IndexDataUtils;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jani Wilén
 */
@Component
public class KoulutusKorkeakouluDTOConverterToEntity {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusKorkeakouluDTOConverterToEntity.class);
    @Autowired(required = true)
    private KoulutusKuvausV1RDTO<KomoTeksti> komoKuvausConverters;
    @Autowired(required = true)
    private KoulutusKuvausV1RDTO<KomotoTeksti> komotoKuvausConverters;
    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    @Autowired
    private OidService oidService;
    @Autowired
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;

    private final KoodistoURI koodistoUri = new KoodistoURI();

    public KoulutusmoduuliToteutus convert(final KoulutusKorkeakouluV1RDTO dto, final String userOid) {
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
                komo.setOid(oidService.get(TarjontaOidType.KOMO));
                komoto.setOid(oidService.get(TarjontaOidType.KOMOTO));
            } catch (OIDCreationException ex) {
                //XXX Should signal error!
                LOG.error("OIDService failed!", ex);
            }
        }

        /*
         * KOMO data fields:
         */
        final String organisationOId = dto.getOrganisaatio().getOid();

        komo.setTutkintoOhjelmanNimi(convertToUri(dto.getTutkinto(), FieldNames.TUTKINTO)); //correct data mapping?
        komo.setLaajuus(
                convertToUri(dto.getOpintojenLaajuusyksikko(), FieldNames.OPINTOJEN_LAAJUUSYKSIKKO),
                convertToUri(dto.getOpintojenLaajuusarvo(), FieldNames.OPINTOJEN_LAAJUUSARVO));
        komo.setOmistajaOrganisaatioOid(organisationOId); //is this correct?
        komo.setKoulutusAste(convertToUri(dto.getKoulutusaste(), FieldNames.KOULUTUSASTE));
        komo.setKoulutusala(convertToUri(dto.getKoulutusala(), FieldNames.KOULUTUSALA));
        komo.setOpintoala(convertToUri(dto.getOpintoala(), FieldNames.OPINTOALA));
        komo.setEqfLuokitus(convertToUri(dto.getEqf(), FieldNames.EQF));
        komo.setTila(dto.getTila()); //has the same status as teh komoto 

        Preconditions.checkNotNull(dto.getKoulutusmoduuliTyyppi(), "KoulutusmoduuliTyyppi enum cannot be null.");
        komo.setModuuliTyyppi(KoulutusmoduuliTyyppi.valueOf(dto.getKoulutusmoduuliTyyppi().name()));
        komo.setKoulutusKoodi(convertToUri(dto.getKoulutuskoodi(), FieldNames.KOULUTUSKOODI));

        komo.setNimi(convertToTexts(dto.getKoulutusohjelma(), FieldNames.KOULUTUSOHJELMA));
        komo.setUlkoinenTunniste(dto.getTunniste());

        Preconditions.checkNotNull(dto.getKoulutusasteTyyppi(), "KoulutusasteTyyppi enum cannot be null.");
        komo.setKoulutustyyppi(dto.getKoulutusasteTyyppi().value());

        komo.setTutkintonimikes(convertToUris(dto.getTutkintonimikes(), komo.getTutkintonimikes(), FieldNames.TUTKINTONIMIKE));
        //Preconditions.checkArgument(dto.getTutkintonimikes().getUris().isEmpty(), "Set of Tutkintonimike objects cannot be empty.");

        komoKuvausConverters.convertTekstiDTOToMonikielinenTeksti(dto.getKuvausKomo(), komo.getTekstit());

        /*
         * KOMOTO data fields
         */
        komoto.setTila(dto.getTila());

        Preconditions.checkNotNull(organisationOId, "Organisation OID cannot be null.");
        komoto.setTarjoaja(organisationOId);
        Preconditions.checkNotNull(dto.getOpintojenMaksullisuus(), "OpintojenMaksullisuus boolean cannot be null.");
        komoto.setMaksullisuus(dto.getOpintojenMaksullisuus().toString());

        //set dates
        handleDates(komoto, dto);

        if (dto.getAihees() != null) {
            komoto.getAihees().clear();
            komoto.getAihees().addAll(convertToUris(dto.getAihees(), new HashSet(), FieldNames.AIHEES));
        }
        if (dto.getOpetuskielis() != null) {
            komoto.getOpetuskielis().clear();
            komoto.setOpetuskieli(convertToUris(dto.getOpetuskielis(), komoto.getOpetuskielis(), FieldNames.OPETUSKIELIS));
        }

        if (dto.getOpetusmuodos() != null) {
            komoto.getOpetusmuotos().clear();
            komoto.setOpetusmuoto(convertToUris(dto.getOpetusmuodos(), komoto.getOpetusmuotos(), FieldNames.OPETUSMUODOS));
        }

        if (dto.getOpetusAikas() != null) {
            komoto.setOpetusAikas(convertToUris(dto.getOpetusAikas(), komoto.getOpetusAikas(), FieldNames.OPETUSAIKAS));
        }
        if (dto.getOpetusPaikkas() != null) {
            komoto.setOpetusPaikkas(convertToUris(dto.getOpetusPaikkas(), komoto.getOpetusPaikkas(), FieldNames.OPETUSPAIKKAS));
        }
        komoto.setKkPohjakoulutusvaatimus(convertToUris(dto.getPohjakoulutusvaatimukset(), komoto.getKkPohjakoulutusvaatimus(), FieldNames.POHJALKOULUTUSVAATIMUKSET));
        komoto.setAmmattinimikes(convertToUris(dto.getAmmattinimikkeet(), komoto.getAmmattinimikes(), FieldNames.AMMATTINIMIKKEET));

        komoto.setHinta(dto.getHinta() != null ? new BigDecimal(dto.getHinta().toString()) : null);

        komoto.setSuunniteltuKesto(convertToUri(dto.getSuunniteltuKestoTyyppi(), FieldNames.SUUNNITELTUKESTO), dto.getSuunniteltuKestoArvo());
        HashSet<Yhteyshenkilo> yhteyshenkilos = Sets.<Yhteyshenkilo>newHashSet();
        EntityUtils.copyYhteyshenkilos(dto.getYhteyshenkilos(), yhteyshenkilos);
        komoto.setYhteyshenkilos(yhteyshenkilos);
        komotoKuvausConverters.convertTekstiDTOToMonikielinenTeksti(dto.getKuvausKomoto(), komoto.getTekstit());

        komoto.setLastUpdatedByOid(userOid);
        return komoto;
    }

    private String convertToUri(final KoodiV1RDTO dto, final FieldNames msg) {
        Preconditions.checkNotNull(dto, "KoodiV1RDTO object cannot be null! Error in field : %s.", msg);
        Preconditions.checkNotNull(dto.getUri(), "KoodiV1RDTO's koodisto koodi URI cannot be null! Error in field : %s.", msg);
        Preconditions.checkNotNull(dto.getVersio(), "KoodiV1RDTO's koodisto koodi version for koodi '%s' cannot be null! Error in field : %s.", dto.getUri(), msg);

        return convertToKoodiUri(dto.getUri(), dto.getVersio(), msg);
    }

    private String convertToKoodiUri(final String uri, final Integer version, final FieldNames msg) {
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

    private Set<KoodistoUri> convertToUris(final KoodiUrisV1RDTO dto, Set<KoodistoUri> koodistoUris, final FieldNames msg) {
        Preconditions.checkNotNull(dto, "DTO object cannot be null! Error field : " + msg);

        Set<KoodistoUri> modifiedUris = Sets.<KoodistoUri>newHashSet(koodistoUris);
        if (koodistoUris == null) {
            modifiedUris = Sets.<KoodistoUri>newHashSet();
        }

        if (dto.getUris() != null) {
            for (Entry<String, Integer> uriWithVersion : dto.getUris().entrySet()) {
                modifiedUris.add(new KoodistoUri(convertToKoodiUri(uriWithVersion.getKey(), uriWithVersion.getValue(), msg)));
            }
        }

        return modifiedUris;
    }

    private MonikielinenTeksti convertToTexts(final NimiV1RDTO dto, final FieldNames msg) {
        Preconditions.checkNotNull(dto, "Language map object cannot be null! Error field : " + msg);
        Preconditions.checkNotNull(dto.getTekstis(), "Language map objects cannot be null! Error in field : " + msg);

        MonikielinenTeksti mt = new MonikielinenTeksti();
        for (Entry<String, String> kieliAndText : dto.getTekstis().entrySet()) {
            koodistoUri.validateKieliUri(kieliAndText.getKey());
            mt.addTekstiKaannos(kieliAndText.getKey(), kieliAndText.getValue());
        }

        return mt;
    }

    /**
     * Logic for handling dates.
     *
     * @param komoto
     * @param dto
     */
    public void handleDates(KoulutusmoduuliToteutus komoto, KoulutusKorkeakouluV1RDTO dto) {
        final Set<Date> koulutuksenAlkamisPvms = dto.getKoulutuksenAlkamisPvms();

        if (koulutuksenAlkamisPvms != null && !koulutuksenAlkamisPvms.isEmpty()) {
            //one or many dates   
            EntityUtils.keepSelectedDates(komoto.getKoulutuksenAlkamisPvms(), koulutuksenAlkamisPvms);
            final Date firstDate = koulutuksenAlkamisPvms.iterator().next();
            KoulutusValidationMessages checkDates = validateDates(firstDate, koulutuksenAlkamisPvms, komoto);
            Preconditions.checkArgument(checkDates.equals(KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_SUCCESS), "Alkamisaika validation error - key : %s.", checkDates);

            komoto.setAlkamisVuosi(IndexDataUtils.parseYearInt(firstDate));
            komoto.setAlkamiskausi(IndexDataUtils.parseKausiKoodi(firstDate));
        } else {
            //allowed only one kausi and year
            Preconditions.checkNotNull(dto.getKoulutuksenAlkamiskausi(), "Alkamiskausi cannot be null!");
            Preconditions.checkArgument(!convertToUri(dto.getKoulutuksenAlkamiskausi(), FieldNames.ALKAMISKAUSI).isEmpty(), "Alkamiskausi cannot be empty string.");
            Preconditions.checkNotNull(dto.getKoulutuksenAlkamisvuosi(), "Alkamisvuosi cannot be null!");

            komoto.clearKoulutuksenAlkamisPvms();
            //only kausi + year, no date objects   
            komoto.setAlkamisVuosi(dto.getKoulutuksenAlkamisvuosi());
            komoto.setAlkamiskausi(convertToUri(dto.getKoulutuksenAlkamiskausi(), FieldNames.ALKAMISKAUSI));
        }
    }

    public static KoulutusValidationMessages validateDates(Date targetDate, Set<Date> dates) {
        return validateDates(targetDate, dates, null);
    }

    private static KoulutusValidationMessages validateDates(Date targetDate, Set<Date> dates, KoulutusmoduuliToteutus komoto) {
        final String baseKausi = IndexDataUtils.parseKausiKoodi(targetDate);
        final Integer baseVuosi = IndexDataUtils.parseYearInt(targetDate);

        if (baseKausi == null) {
            return KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_KAUSI_MISSING;
        }

        if (baseVuosi == null) {
            return KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_VUOSI_INVALID;
        }

        //pre-check if the dates are within same date range of kausi + vuosi
        for (Date pvm : dates) {
            if (!baseKausi.equals(IndexDataUtils.parseKausiKoodi(pvm))) {
                return KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_KAUSI_INVALID;
            }

            if (!baseVuosi.equals(IndexDataUtils.parseYearInt(pvm))) {
                return KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_VUOSI_INVALID;
            }

            if (komoto != null) {
                komoto.addKoulutuksenAlkamisPvms(pvm);
            }
        }

        return KoulutusValidationMessages.KOULUTUS_ALKAMISPVM_SUCCESS;
    }
}
