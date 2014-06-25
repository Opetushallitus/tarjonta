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
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.BaseKoulutusmoduuli;
import fi.vm.sade.tarjonta.model.BinaryData;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.model.WebLinkki;
import fi.vm.sade.tarjonta.model.Yhteyshenkilo;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.FieldNames;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidator;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusLukioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NayttotutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.valmistava.ValmistavaV1RDTO;
import fi.vm.sade.tarjonta.service.search.IndexerResource;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Conversions to entity objects.
 *
 * @author Jani Wilén
 */
@Component
public class KoulutusDTOConverterToEntity {

    private static final boolean ALLOW_NULL_KOODI_URI = true;
    private static final Logger LOG = LoggerFactory.getLogger(KoulutusDTOConverterToEntity.class);
    @Autowired(required = true)
    private KoulutusKuvausV1RDTO<KomoTeksti> komoKuvausConverters;
    @Autowired(required = true)
    private KoulutusKuvausV1RDTO<KomotoTeksti> komotoKuvausConverters;
    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    @Autowired
    private OidService oidService;
    @Autowired
    private KoulutusCommonConverter commonConverter;
    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    @Autowired(required = true)
    private IndexerResource indexerResource;


    /*
     * KORKEAKOULU RDTO CONVERSION TO ENTITY
     */
    public KoulutusmoduuliToteutus convert(final KoulutusKorkeakouluV1RDTO dto, final String userOid, final boolean addNewKomotoToKomo) {
        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
        if (dto == null) {
            return komoto;
        }

        Koulutusmoduuli komo = null;
        if (addNewKomotoToKomo) {
            //use previously created komo, and do change or update data in the komo
            Preconditions.checkNotNull(dto.getKomoOid(), "KOMO OID cannot be null.");
            komo = koulutusmoduuliDAO.findByOid(dto.getKomoOid());
            Preconditions.checkNotNull(komo, "KOMO not found by OID '%s'!", dto.getKomoOid());
            try {
                komoto.setOid(oidService.get(TarjontaOidType.KOMOTO));
            } catch (OIDCreationException ex) {
                //XXX Should signal error!
                LOG.error("OIDService failed!", ex);
            }
            komoto.setKoulutusmoduuli(komo);
        } else if (dto.getOid() != null) {
            //search by komoto oid, and update both komo & komoto
            komoto = koulutusmoduuliToteutusDAO.findByOid(dto.getOid());
            Preconditions.checkNotNull(komoto, "KOMOTO not found by OID '%s'!", dto.getOid());
            komo = komoto.getKoulutusmoduuli();
        } else {
            //insert new komo&komoto data to database.
            komo = new Koulutusmoduuli();
            komoto.setKoulutusmoduuli(komo);
            try {
                komo.setOid(oidService.get(TarjontaOidType.KOMO));
                komoto.setOid(oidService.get(TarjontaOidType.KOMOTO));
            } catch (OIDCreationException ex) {
                //XXX Should signal error!
                LOG.error("OIDService failed!", ex);
            }
        }

        if (!addNewKomotoToKomo) {
            /* 
             * Only when user create new komo + komoto.
             */
            korkeakouluKomoDataUpdate(komo, dto);
        }

        /*
         * KOMOTO custom data conversion
         */
        korkeakouluKomotoDataUpdate(komoto, dto, userOid);
        addOrRemoveImages(dto, komoto, userOid);

        return komoto;
    }

    /*
     * LUKIO RDTO CONVERSION TO ENTITY
     */
    public KoulutusmoduuliToteutus convert(final KoulutusLukioV1RDTO dto, final String userOid) {
        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
        if (dto == null) {
            return komoto;
        }

        Koulutusmoduuli komo = null;
        if (dto.getOid() != null) {
            //update komo & komoto
            komoto = koulutusmoduuliToteutusDAO.findByOid(dto.getOid());
            komo = komoto.getKoulutusmoduuli();
        } else {
            //insert only new komoto data to database, do not change or update komo. 
            Preconditions.checkNotNull(dto.getKomoOid(), "KOMO OID cannot be null.");
            komo = koulutusmoduuliDAO.findByOid(dto.getKomoOid());
            Preconditions.checkNotNull(komo, "KOMO object not found.");
            komoto.setKoulutusmoduuli(komo);
            try {
                komoto.setOid(oidService.get(TarjontaOidType.KOMOTO));
            } catch (OIDCreationException ex) {
                //XXX Should signal error!
                LOG.error("OIDService failed!", ex);
            }
        }

        Preconditions.checkNotNull(komo, "KOMO object cannot be null.");
        Preconditions.checkNotNull(komoto, "KOMOTO object cannot be null.");
        Preconditions.checkNotNull(komoto.getOid(), "KOMOTO OID cannot be null.");

        /*
         * KOMOTO common data conversion
         */
        convertKomotoCommonData(komoto, dto, userOid);

        /*
         * KOMOTO custom data conversion
         */
        komoto.setSuunniteltuKesto(commonConverter.convertToUri(dto.getSuunniteltuKestoTyyppi(), FieldNames.SUUNNITELTUKESTO, ALLOW_NULL_KOODI_URI), dto.getSuunniteltuKestoArvo());
        komoto.setLukiolinjaUri(commonConverter.convertToUri(dto.getKoulutusohjelma(), FieldNames.LUKIOLINJA));
        komoto.setTutkintonimikeUri(commonConverter.convertToUri(dto.getTutkintonimike(), FieldNames.TUTKINTONIMIKE));
        komoto.setPohjakoulutusvaatimusUri(commonConverter.convertToUri(dto.getPohjakoulutusvaatimus(), FieldNames.POHJALKOULUTUSVAATIMUS));

        if (dto.getOpetuskielis() != null) {
            komoto.getOpetuskielis().clear();
            komoto.setOpetuskieli(commonConverter.convertToUris(dto.getOpetuskielis(), komoto.getOpetuskielis(), FieldNames.OPETUSKIELIS));
        }

        if (dto.getOpetusmuodos() != null) {
            komoto.getOpetusmuotos().clear();
            komoto.setOpetusmuoto(commonConverter.convertToUris(dto.getOpetusmuodos(), komoto.getOpetusmuotos(), FieldNames.OPETUSMUODOS));
        }

        if (dto.getOpetusAikas() != null) {
            komoto.getOpetusAikas().clear();
            komoto.setOpetusAikas(commonConverter.convertToUris(dto.getOpetusAikas(), komoto.getOpetusAikas(), FieldNames.OPETUSAIKAS));
        }

        if (dto.getOpetusPaikkas() != null) {
            komoto.getOpetusPaikkas().clear();
            komoto.setOpetusPaikkas(commonConverter.convertToUris(dto.getOpetusPaikkas(), komoto.getOpetusPaikkas(), FieldNames.OPETUSPAIKKAS));
        }

        if (dto.getLukiodiplomit() != null) {
            komoto.getLukiodiplomit().clear();
            komoto.setLukiodiplomit(commonConverter.convertToUris(dto.getLukiodiplomit(), komoto.getLukiodiplomit(), FieldNames.LUKIODIPLOMI));
        }

        if (dto.getLinkkiOpetussuunnitelmaan() != null) {
            komoto.getLinkkis().clear();
            komoto.setLinkkis(
                    commonConverter.convertToLinkkis(WebLinkki.LinkkiTyyppi.OPETUSSUUNNITELMA,
                            dto.getLinkkiOpetussuunnitelmaan(),
                            komoto.getLinkkis()));
        }

        if (dto.getKielivalikoima() != null) {
            commonConverter.convertToKielivalikoima(dto.getKielivalikoima(), komoto);
        }

        if (dto.getKoulutuslaji() != null) {
            komoto.getKoulutuslajis().clear();
            komoto.setKoulutuslajis(commonConverter.convertToUris(dto.getKoulutuslaji(), komoto.getKoulutuslajis(), FieldNames.KOULUTUSLAJI));
        }

        return komoto;
    }

    /*
     * AMMATILLINEN RDTO CONVERSION TO ENTITY
     */
    public KoulutusmoduuliToteutus convert(final NayttotutkintoV1RDTO dto, final String userOid) {
        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
        if (dto == null) {
            return komoto;
        }

        Koulutusmoduuli komo = null;
        if (dto.getOid() != null) {
            //update komo & komoto
            komoto = koulutusmoduuliToteutusDAO.findByOid(dto.getOid());
            komo = komoto.getKoulutusmoduuli();
        } else {
            //insert only new komoto data to database, do not change or update komo. 
            Preconditions.checkNotNull(dto.getKomoOid(), "KOMO OID cannot be null.");
            komo = koulutusmoduuliDAO.findByOid(dto.getKomoOid());
            Preconditions.checkNotNull(komo, "KOMO object not found.");
            komoto.setKoulutusmoduuli(komo);
            try {
                komoto.setOid(oidService.get(TarjontaOidType.KOMOTO));
            } catch (OIDCreationException ex) {
                //XXX Should signal error!
                LOG.error("OIDService failed!", ex);
            }
        }

        Preconditions.checkNotNull(komo, "KOMO object cannot be null.");
        Preconditions.checkNotNull(komoto, "KOMOTO object cannot be null.");
        Preconditions.checkNotNull(komoto.getOid(), "KOMOTO OID cannot be null.");

        /*
         * KOMOTO common data conversion
         */
        convertKomotoCommonData(komoto, dto, userOid);

        /*
         * KOMOTO custom data conversion
         */
        komoto.setOsaamisalaUri(commonConverter.convertToUri(dto.getKoulutusohjelma(), FieldNames.OSAAMISALA));
        komoto.setTutkintonimikeUri(commonConverter.convertToUri(dto.getTutkintonimike(), FieldNames.TUTKINTONIMIKE, ALLOW_NULL_KOODI_URI));

        if (dto.getOpetuskielis() != null) {
            komoto.getOpetuskielis().clear();
            komoto.setOpetuskieli(commonConverter.convertToUris(dto.getOpetuskielis(), komoto.getOpetuskielis(), FieldNames.OPETUSKIELIS));
        }

        if (dto.getKoulutuslaji() != null) {
            komoto.getKoulutuslajis().clear();
            komoto.setKoulutuslajis(commonConverter.convertToUris(dto.getKoulutuslaji(), komoto.getKoulutuslajis(), FieldNames.KOULUTUSLAJI));
        }

        /* CUSTOM DATA by object type */
        komoto.setHinta(dto.getHinta() != null ? new BigDecimal(dto.getHinta().toString()) : null);
        komoto.setMaksullisuus(dto.getOpintojenMaksullisuus() != null ? dto.getOpintojenMaksullisuus().toString() : Boolean.FALSE.toString());
        komoto.setAmmattinimikes(commonConverter.convertToUris(dto.getAmmattinimikkeet(), komoto.getAmmattinimikes(), FieldNames.AMMATTINIMIKKEET));
        komoto.setJarjesteja(dto.getJarjestavaOrganisaatio() != null ? dto.getJarjestavaOrganisaatio().getOid() : null);

        if (dto.getValmistavaKoulutus() == null) { //delete if any
            //delete row from db
            final KoulutusmoduuliToteutus valmistava = komoto.getValmistavaKoulutus();
            if (valmistava != null && valmistava.getId() != null) {
                //remove child komoto from base komoto
                try {
                    indexerResource.deleteKoulutus(Lists.<String>newArrayList(valmistava.getValmistavaKoulutus().getOid()));
                } catch (IOException ex) {
                    LOG.error("Index delete failed by OID : {}", valmistava.getOid(), ex);
                }

                komoto.setValmistavaKoulutus(null);
                koulutusmoduuliToteutusDAO.remove(valmistava);
            }
        } else {
            KoulutusmoduuliToteutus valmistavaKomoto = convert(dto.getValmistavaKoulutus(), komoto, userOid);

            if (valmistavaKomoto.getId() == null) {
                valmistavaKomoto = koulutusmoduuliToteutusDAO.insert(valmistavaKomoto);
            }

            komoto.setValmistavaKoulutus(valmistavaKomoto);
        }

        return komoto;
    }

    /*
     * Update korkeakoulu komo data.
     */
    private void korkeakouluKomoDataUpdate(Koulutusmoduuli komo, final KoulutusKorkeakouluV1RDTO dto) {
        Preconditions.checkNotNull(dto, "KoulutusKorkeakouluV1RDTO object cannot be null.");
        Preconditions.checkNotNull(komo, "KoulutusmoduuliToteutus object cannot be null.");
        Preconditions.checkNotNull(dto.getKoulutusmoduuliTyyppi(), "KoulutusmoduuliTyyppi enum cannot be null.");
        Preconditions.checkNotNull(dto.getToteutustyyppi(), "Toteutustyyppi enum cannot be null.");

        final String organisationOId = dto.getOrganisaatio().getOid();
        Preconditions.checkNotNull(organisationOId, "Organisation OID cannot be null.");

        /*
         * KOMO/KOMOTO common data fields:
         */
        base(komo, dto);

        //other data
        komo.setOmistajaOrganisaatioOid(organisationOId); //is this correct?
        //Kandidaatti can be null object:
        komo.setKandidaatinKoulutusUri(commonConverter.convertToUri(dto.getKandidaatinKoulutuskoodi(), FieldNames.KOULUTUSKOODI_KANDIDAATTI, ALLOW_NULL_KOODI_URI));
        komo.setModuuliTyyppi(KoulutusmoduuliTyyppi.valueOf(dto.getKoulutusmoduuliTyyppi().name()));
        komo.setKoulutustyyppiEnum(ModuulityyppiEnum.KORKEAKOULUTUS);
        komo.setKoulutustyyppiUri(toListUri(dto.getToteutustyyppi()));
        komo.setTutkintonimikes(commonConverter.convertToUris(dto.getTutkintonimikes(), komo.getTutkintonimikes(), FieldNames.TUTKINTONIMIKE));
        komoKuvausConverters.convertTekstiDTOToMonikielinenTeksti(dto.getKuvausKomo(), komo.getTekstit());
    }

    /*
     * Update korkeakoulu komo data.
     */
    private void korkeakouluKomotoDataUpdate(KoulutusmoduuliToteutus komoto, final KoulutusKorkeakouluV1RDTO dto, final String userOid) {
        /*
         * KOMOTO common data conversion
         */
        convertKomotoCommonData(komoto, dto, userOid);

        komoto.setSuunniteltuKesto(commonConverter.convertToUri(dto.getSuunniteltuKestoTyyppi(), FieldNames.SUUNNITELTUKESTO, ALLOW_NULL_KOODI_URI), dto.getSuunniteltuKestoArvo());
        komoto.setKandidaatinKoulutusUri(commonConverter.convertToUri(dto.getKandidaatinKoulutuskoodi(), FieldNames.KOULUTUSKOODI_KANDIDAATTI, ALLOW_NULL_KOODI_URI));
        komoto.setTutkintonimikes(commonConverter.convertToUris(dto.getTutkintonimikes(), komoto.getTutkintonimikes(), FieldNames.TUTKINTONIMIKE));
        Preconditions.checkNotNull(dto.getOpintojenMaksullisuus(), "OpintojenMaksullisuus boolean cannot be null.");
        komoto.setMaksullisuus(dto.getOpintojenMaksullisuus().toString());
        komoto.setKkPohjakoulutusvaatimus(commonConverter.convertToUris(dto.getPohjakoulutusvaatimukset(), komoto.getKkPohjakoulutusvaatimus(), FieldNames.POHJALKOULUTUSVAATIMUS));
        komoto.setAmmattinimikes(commonConverter.convertToUris(dto.getAmmattinimikkeet(), komoto.getAmmattinimikes(), FieldNames.AMMATTINIMIKKEET));
        komoto.setHinta(dto.getHinta() != null ? new BigDecimal(dto.getHinta().toString()) : null);
        komoto.setNimi(commonConverter.convertToTexts(dto.getKoulutusohjelma(), FieldNames.KOULUTUSOHJELMA)); //OVT-7531

        if (dto.getAihees() != null) {
            komoto.getAihees().clear();
            komoto.getAihees().addAll(commonConverter.convertToUris(dto.getAihees(), new HashSet(), FieldNames.AIHEES));
        }
        if (dto.getOpetuskielis() != null) {
            komoto.getOpetuskielis().clear();
            komoto.setOpetuskieli(commonConverter.convertToUris(dto.getOpetuskielis(), komoto.getOpetuskielis(), FieldNames.OPETUSKIELIS));
        }

        if (dto.getOpetusmuodos() != null) {
            komoto.getOpetusmuotos().clear();
            komoto.setOpetusmuoto(commonConverter.convertToUris(dto.getOpetusmuodos(), komoto.getOpetusmuotos(), FieldNames.OPETUSMUODOS));
        }

        if (dto.getOpetusAikas() != null) {
            komoto.getOpetusAikas().clear();
            komoto.setOpetusAikas(commonConverter.convertToUris(dto.getOpetusAikas(), komoto.getOpetusAikas(), FieldNames.OPETUSAIKAS));
        }

        if (dto.getOpetusPaikkas() != null) {
            komoto.getOpetusPaikkas().clear();
            komoto.setOpetusPaikkas(commonConverter.convertToUris(dto.getOpetusPaikkas(), komoto.getOpetusPaikkas(), FieldNames.OPETUSPAIKKAS));
        }
    }

    /**
     * Valmistava koulutus data conversion!
     *
     * @param dto
     * @param nayttoKomoto
     * @param userOid
     * @return
     */
    private KoulutusmoduuliToteutus convert(final ValmistavaV1RDTO dto, KoulutusmoduuliToteutus nayttoKomoto, final String userOid) {
        Preconditions.checkNotNull(nayttoKomoto, "Base komoto cannot be null.");
        Preconditions.checkNotNull(nayttoKomoto.getKoulutusmoduuli(), "Base komo cannot be null.");
        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();

        if (nayttoKomoto.getValmistavaKoulutus() != null) {
            //update komo & komoto
            komoto = nayttoKomoto.getValmistavaKoulutus();
        } else {
            //follow base komoto
            try {
                komoto.setOid(oidService.get(TarjontaOidType.KOMOTO));
            } catch (OIDCreationException ex) {
                //XXX Should signal error!
                LOG.error("OIDService failed!", ex);
            }
        }

        Preconditions.checkNotNull(komoto, "KOMOTO object cannot be null.");
        Preconditions.checkNotNull(komoto.getOid(), "KOMOTO OID cannot be null.");

        //copy&overwrite data from the base komoto
        komoto.setKoulutusmoduuli(nayttoKomoto.getKoulutusmoduuli());
        komoto.setToteutustyyppi(ToteutustyyppiEnum.convertToValmistava(nayttoKomoto.getToteutustyyppi()));
        komoto.setTila(nayttoKomoto.getTila());
        komoto.setTarjoaja(nayttoKomoto.getTarjoaja());

        //copy&overwrite other data
        komoto.setTutkintoUri(nayttoKomoto.getTutkintoUri());
        komoto.setKoulutusasteUri(nayttoKomoto.getKoulutusasteUri());
        komoto.setKoulutusalaUri(nayttoKomoto.getKoulutusalaUri());
        komoto.setOpintoalaUri(nayttoKomoto.getOpintoalaUri());
        komoto.setEqfUri(nayttoKomoto.getEqfUri());
        komoto.setNqfUri(nayttoKomoto.getNqfUri());
        komoto.setKoulutusUri(nayttoKomoto.getKoulutusUri());
        komoto.setKoulutustyyppiUri(nayttoKomoto.getKoulutustyyppiUri());

        komoto.setSuunniteltuKesto(commonConverter.convertToUri(dto.getSuunniteltuKestoTyyppi(), FieldNames.SUUNNITELTUKESTO, ALLOW_NULL_KOODI_URI), dto.getSuunniteltuKestoArvo());
        komoto.setHinta(dto.getHinta() != null ? new BigDecimal(dto.getHinta().toString()) : null);
        komoto.setMaksullisuus(dto.getOpintojenMaksullisuus() != null ? dto.getOpintojenMaksullisuus().toString() : Boolean.FALSE.toString());

        if (dto.getOpetuskielis() != null) {
            komoto.getOpetuskielis().clear();
            komoto.setOpetuskieli(commonConverter.convertToUris(dto.getOpetuskielis(), komoto.getOpetuskielis(), FieldNames.OPETUSKIELIS));
        }

        if (dto.getOpetusmuodos() != null) {
            komoto.getOpetusmuotos().clear();
            komoto.setOpetusmuoto(commonConverter.convertToUris(dto.getOpetusmuodos(), komoto.getOpetusmuotos(), FieldNames.OPETUSMUODOS));
        }

        if (dto.getOpetusAikas() != null) {
            komoto.getOpetusAikas().clear();
            komoto.setOpetusAikas(commonConverter.convertToUris(dto.getOpetusAikas(), komoto.getOpetusAikas(), FieldNames.OPETUSAIKAS));
        }

        if (dto.getOpetusPaikkas() != null) {
            komoto.getOpetusPaikkas().clear();
            komoto.setOpetusPaikkas(commonConverter.convertToUris(dto.getOpetusPaikkas(), komoto.getOpetusPaikkas(), FieldNames.OPETUSPAIKKAS));
        }

        if (dto.getLinkkiOpetussuunnitelmaan() != null) {
            komoto.getLinkkis().clear();
            komoto.setLinkkis(
                    commonConverter.convertToLinkkis(WebLinkki.LinkkiTyyppi.OPETUSSUUNNITELMA,
                            dto.getLinkkiOpetussuunnitelmaan(),
                            komoto.getLinkkis()));
        }

        commonConverter.handleDates(komoto, dto); //set dates
        HashSet<Yhteyshenkilo> yhteyshenkilos = Sets.<Yhteyshenkilo>newHashSet();
        EntityUtils.copyYhteyshenkilos(dto.getYhteyshenkilos(), yhteyshenkilos);
        komoto.setYhteyshenkilos(yhteyshenkilos);
        komotoKuvausConverters.convertTekstiDTOToMonikielinenTeksti(dto.getKuvaus(), komoto.getTekstit());
        komoto.setLastUpdatedByOid(userOid);

        return komoto;
    }

    /**
     * Convert common data from DTO to entity (komo/komoto).
     */
    private void base(BaseKoulutusmoduuli base, KoulutusV1RDTO dto) {
        base.setTutkintoUri(commonConverter.convertToUri(dto.getTutkinto(), FieldNames.TUTKINTO, ALLOW_NULL_KOODI_URI));
        base.setOpintojenLaajuus(
                commonConverter.convertToUri(dto.getOpintojenLaajuusyksikko(), FieldNames.OPINTOJEN_LAAJUUSYKSIKKO, ALLOW_NULL_KOODI_URI),
                commonConverter.convertToUri(dto.getOpintojenLaajuusarvo(), FieldNames.OPINTOJEN_LAAJUUSARVO, ALLOW_NULL_KOODI_URI));
        base.setKoulutusasteUri(commonConverter.convertToUri(dto.getKoulutusaste(), FieldNames.KOULUTUSASTE, ALLOW_NULL_KOODI_URI));
        base.setKoulutusalaUri(commonConverter.convertToUri(dto.getKoulutusala(), FieldNames.KOULUTUSALA));
        base.setOpintoalaUri(commonConverter.convertToUri(dto.getOpintoala(), FieldNames.OPINTOALA));
        base.setEqfUri(commonConverter.convertToUri(dto.getEqf(), FieldNames.EQF, ALLOW_NULL_KOODI_URI));
        base.setNqfUri(commonConverter.convertToUri(dto.getNqf(), FieldNames.NQF, ALLOW_NULL_KOODI_URI));
        base.setTila(dto.getTila()); //has the same status as the komoto 
        base.setKoulutusUri(commonConverter.convertToUri(dto.getKoulutuskoodi(), FieldNames.KOULUTUS));
        base.setKoulutustyyppiUri(commonConverter.convertToUri(dto.getKoulutustyyppi(), FieldNames.KOULUTUSTYYPPI, ALLOW_NULL_KOODI_URI));
        base.setUlkoinenTunniste(dto.getTunniste());
    }

    /**
     * DO NOT ADD HERE ANYTHING THAT IS NOT COMMON FOR ALL KOULUTUS TYPES!
     * KOMOTO common data conversion for all 'koulutus' types.
     */
    private void convertKomotoCommonData(KoulutusmoduuliToteutus komoto, KoulutusV1RDTO dto, String userOid) {
        Preconditions.checkNotNull(dto, "KoulutusV1RDTO object cannot be null.");
        Preconditions.checkNotNull(komoto, "KoulutusmoduuliToteutus object cannot be null.");

        final String organisationOId = dto.getOrganisaatio().getOid();
        Preconditions.checkNotNull(organisationOId, "Organisation OID cannot be null.");

        base(komoto, dto);
        komoto.setTarjoaja(organisationOId);
        commonConverter.handleDates(komoto, dto); //set dates
        komoto.setToteutustyyppi(dto.getToteutustyyppi());

        HashSet<Yhteyshenkilo> yhteyshenkilos = Sets.<Yhteyshenkilo>newHashSet();
        EntityUtils.copyYhteyshenkilos(dto.getYhteyshenkilos(), yhteyshenkilos);
        komoto.setYhteyshenkilos(yhteyshenkilos);
        komotoKuvausConverters.convertTekstiDTOToMonikielinenTeksti(dto.getKuvausKomoto(), komoto.getTekstit());
        komoto.setLastUpdatedByOid(userOid);
    }

    private void addOrRemoveImages(final KoulutusKorkeakouluV1RDTO dto, KoulutusmoduuliToteutus komoto, final String userOid) {
        if (dto.getOpintojenRakenneKuvas() != null && !dto.getOpintojenRakenneKuvas().isEmpty()) {
            for (Map.Entry<String, KuvaV1RDTO> e : dto.getOpintojenRakenneKuvas().entrySet()) {
                if (e.getValue() == null && e.getKey() != null && !e.getKey().isEmpty()) {
                    //delete image
                    komoto.getKuvat().remove(e.getKey());
                } else {
                    //add or overwrite previous image
                    addImageToKomoto(komoto, e.getValue(), e.getKey(), userOid);
                }
            }
        }
    }

    private void addImageToKomoto(KoulutusmoduuliToteutus komoto, final KuvaV1RDTO kuva, final String mapKeyKieliUri, final String userOid) {
        Preconditions.checkNotNull(komoto, "KOMOTO object cannot be null!");
        Preconditions.checkNotNull(kuva, "Image DTO cannot be null!");
        //select a map key as kieli uri, when kuva object kieliUri field is null
        final String imageKieliUri = kuva.getKieliUri() != null ? kuva.getKieliUri() : mapKeyKieliUri;
        Preconditions.checkNotNull(imageKieliUri, "Image language uri cannot be null!");
        Preconditions.checkNotNull(kuva.getFilename(), "Image filename cannot be null!");
        Preconditions.checkNotNull(kuva.getMimeType(), "Image mime type cannot be null!");
        Preconditions.checkNotNull(kuva.getBase64data(), "Image binary data cannot be null!");
        /*
         * Update or insert uploaded binary data
         */
        BinaryData bin = null;
        if (komoto.isKuva(imageKieliUri)) {
            bin = komoto.getKuvat().get(imageKieliUri);
        } else {
            bin = new BinaryData();
        }

        final byte[] decoded = Base64.decodeBase64(KoulutusValidator.getValidBase64Image(kuva.getBase64data()));
        bin.setData(decoded);
        bin.setFilename(kuva.getFilename());
        bin.setMimeType(kuva.getMimeType());
        komoto.setKuvaByUri(imageKieliUri, bin);
        komoto.setLastUpdatedByOid(userOid);
    }

    public void saveHtml5Image(KoulutusmoduuliToteutus komoto, final KuvaV1RDTO kuva, final String userOid) {
        addImageToKomoto(komoto, kuva, null, userOid);
        this.koulutusmoduuliToteutusDAO.update(komoto);
    }

    private static String toListUri(ToteutustyyppiEnum e) {
        return EntityUtils.joinListToString(e.uri());
    }

}
