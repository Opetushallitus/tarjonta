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
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.generic.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.model.KoodistoUri;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.enums.KoulutustyyppiEnum;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.FieldNames;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KomoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiUrisV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author Jani Wilén
 */
public class KomoRDTOConverterToEntity extends AbstractToDomainConverter<KomoV1RDTO, Koulutusmoduuli> {

    private static final Logger LOG = LoggerFactory.getLogger(KomoRDTOConverterToEntity.class);
    private static final boolean ALLOW_NULL_KOODI_URI = true;
    @Autowired(required = true)
    private KoulutusKuvausV1RDTO<KomoTeksti> komoKuvausConverters;
    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    @Autowired
    private OidService oidService;
    @Autowired
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;
    private final KoodistoURI koodistoUri = new KoodistoURI();

    @Value("${root.organisaatio.oid}")
    String rootOrgOid;

    @Override
    public Koulutusmoduuli convert(KomoV1RDTO dto) {
        Koulutusmoduuli komo = new Koulutusmoduuli();
        if (dto == null) {
            return komo;
        }

        if (dto.getOid() != null) {
            //update komo & komoto
            komo = koulutusmoduuliDAO.findByOid(dto.getOid());
        } else {
            try {
                komo.setOid(oidService.get(TarjontaOidType.KOMO));
            } catch (OIDCreationException ex) {
                LOG.error("OIDService failed!", ex);
            }
        }
        Preconditions.checkNotNull("Komo entity cannot be null.", komo);
        Preconditions.checkNotNull("KoulutusasteTyyppi enum cannot be null.", dto.getKoulutusasteTyyppi());

        /*
         * KOMO data fields:
         */
        switch (dto.getKoulutusasteTyyppi()) {
            case KORKEAKOULUTUS:
            case YLIOPISTOKOULUTUS:
            case AMMATTIKORKEAKOULUTUS:
                korkeakoulu(komo, dto);
                break;
            default:
                toinenAste(komo, dto);
                break;

        }
        return komo;
    }

    private String convertToUri(final KoodiV1RDTO dto, final FieldNames msg, boolean allowNull) {
        if (allowNull && dto.getUri() == null) {
            return null;
        }

        Preconditions.checkNotNull(dto, "KoodiV1RDTO object cannot be null! Error in field : %s.", msg);
        Preconditions.checkNotNull(dto.getUri(), "KoodiV1RDTO's koodisto koodi URI cannot be null! Error in field : %s.", msg);
        Preconditions.checkNotNull(dto.getVersio(), "KoodiV1RDTO's koodisto koodi version for koodi '%s' cannot be null! Error in field : %s.", dto.getUri(), msg);

        return convertToKoodiUri(dto.getUri(), dto.getVersio(), msg);
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

    private Koulutusmoduuli toinenAste(Koulutusmoduuli komo, KomoV1RDTO dto) {
        komo.setTutkintoUri(convertToUri(dto.getTutkinto(), FieldNames.TUTKINTO, ALLOW_NULL_KOODI_URI));
        komo.setOpintojenLaajuus(
                convertToUri(dto.getOpintojenLaajuusyksikko(), FieldNames.OPINTOJEN_LAAJUUSYKSIKKO, ALLOW_NULL_KOODI_URI),
                convertToUri(dto.getOpintojenLaajuusarvo(), FieldNames.OPINTOJEN_LAAJUUSARVO, ALLOW_NULL_KOODI_URI));
        komo.setKoulutusasteUri(convertToUri(dto.getKoulutusaste(), FieldNames.KOULUTUSASTE, ALLOW_NULL_KOODI_URI));
        komo.setKoulutusalaUri(convertToUri(dto.getKoulutusala(), FieldNames.KOULUTUSALA, ALLOW_NULL_KOODI_URI));
        komo.setOpintoalaUri(convertToUri(dto.getOpintoala(), FieldNames.OPINTOALA, ALLOW_NULL_KOODI_URI));
        komo.setEqfUri(convertToUri(dto.getEqf(), FieldNames.EQF, ALLOW_NULL_KOODI_URI));
        komo.setKoulutusohjelmaUri(convertToUri(dto.getKoulutusohjelma(), FieldNames.KOULUTUSOHJELMA, ALLOW_NULL_KOODI_URI));
        komo.setTila(dto.getTila());
        komo.setOmistajaOrganisaatioOid(rootOrgOid); //is this correct?

        Preconditions.checkNotNull(dto.getKoulutusmoduuliTyyppi(), "KoulutusmoduuliTyyppi enum cannot be null.");
        komo.setModuuliTyyppi(KoulutusmoduuliTyyppi.valueOf(dto.getKoulutusmoduuliTyyppi().name()));
        komo.setKoulutusUri(convertToUri(dto.getKoulutuskoodi(), FieldNames.KOULUTUSKOODI));
        komo.setUlkoinenTunniste(dto.getTunniste());

        Preconditions.checkNotNull(dto.getKoulutusasteTyyppi(), "KoulutusasteTyyppi enum cannot be null.");
        komo.setKoulutustyyppiEnum(KoulutustyyppiEnum.fromEnum(dto.getKoulutusasteTyyppi()));
        komo.setTutkintonimikes(convertToUris(dto.getTutkintonimikes(), komo.getTutkintonimikes(), FieldNames.TUTKINTONIMIKE));

        komoKuvausConverters.convertTekstiDTOToMonikielinenTeksti(dto.getKuvausKomo(), komo.getTekstit());

        //legacy stuff:
        komo.setOppilaitostyyppi(join(dto.getOppilaitostyyppis(), Sets.<KoodistoUri>newHashSet(), FieldNames.OPPILAITOSTYYPPI));

        return komo;
    }

    private Koulutusmoduuli korkeakoulu(Koulutusmoduuli komo, KomoV1RDTO dto) {
        final String organisationOId = dto.getOrganisaatio().getOid();

        komo.setTutkintoUri(convertToUri(dto.getTutkinto(), FieldNames.TUTKINTO));
        komo.setOpintojenLaajuus(
                convertToUri(dto.getOpintojenLaajuusyksikko(), FieldNames.OPINTOJEN_LAAJUUSYKSIKKO),
                convertToUri(dto.getOpintojenLaajuusarvo(), FieldNames.OPINTOJEN_LAAJUUSARVO));
        komo.setOmistajaOrganisaatioOid(organisationOId); //is this correct?
        komo.setKoulutusasteUri(convertToUri(dto.getKoulutusaste(), FieldNames.KOULUTUSASTE));
        komo.setKoulutusalaUri(convertToUri(dto.getKoulutusala(), FieldNames.KOULUTUSALA));
        komo.setOpintoalaUri(convertToUri(dto.getOpintoala(), FieldNames.OPINTOALA));
        komo.setEqfUri(convertToUri(dto.getEqf(), FieldNames.EQF));
        komo.setTila(TarjontaTila.JULKAISTU); //is this correct state for a new komo?

        Preconditions.checkNotNull(dto.getKoulutusmoduuliTyyppi(), "KoulutusmoduuliTyyppi enum cannot be null.");
        komo.setModuuliTyyppi(KoulutusmoduuliTyyppi.valueOf(dto.getKoulutusmoduuliTyyppi().name()));
        komo.setKoulutusUri(convertToUri(dto.getKoulutuskoodi(), FieldNames.KOULUTUSKOODI));

        komo.setNimi(convertToTexts(dto.getKoulutusohjelma(), FieldNames.KOULUTUSOHJELMA));
        komo.setUlkoinenTunniste(dto.getTunniste());

        Preconditions.checkNotNull(dto.getKoulutusasteTyyppi(), "KoulutusasteTyyppi enum cannot be null.");
        komo.setKoulutustyyppiEnum(KoulutustyyppiEnum.fromEnum(dto.getKoulutusasteTyyppi()));

        komo.setTutkintonimikes(convertToUris(dto.getTutkintonimikes(), komo.getTutkintonimikes(), FieldNames.TUTKINTONIMIKE));

        komoKuvausConverters.convertTekstiDTOToMonikielinenTeksti(dto.getKuvausKomo(), komo.getTekstit());

        return komo;
    }

    private String join(final KoodiUrisV1RDTO dto, Set<KoodistoUri> koodistoUris, final FieldNames msg) {
        Set<KoodistoUri> convertToUris = convertToUris(dto, koodistoUris, msg);
        List<String> list = Lists.<String>newArrayList();
        for (KoodistoUri str : convertToUris) {
            list.add(str.getKoodiUri());
        }

        return EntityUtils.joinListToString(list);
    }
}
