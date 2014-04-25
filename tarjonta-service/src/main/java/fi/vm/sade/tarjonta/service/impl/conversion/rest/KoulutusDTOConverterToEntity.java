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
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.model.WebLinkki;
import fi.vm.sade.tarjonta.model.Yhteyshenkilo;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.FieldNames;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusLukioV1RDTO;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import java.math.BigDecimal;
import java.util.HashSet;
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


    /*
     * KORKEAKOULU RDTO CONVERSION TO ENTITY
     */
    public KoulutusmoduuliToteutus convert(final KoulutusKorkeakouluV1RDTO dto, final String userOid, final boolean addKomotoToKomo) {
        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
        if (dto == null) {
            return komoto;
        }

        Koulutusmoduuli komo = null;
        if (addKomotoToKomo) {
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
        final String organisationOId = dto.getOrganisaatio().getOid();
        //do not update komo, when we use previously created komo
        if (!addKomotoToKomo) {
            //only when we create new koulutus
            korkeakouluKomoDataUpdate(komo, dto, organisationOId);
        }

        /*
         * KOMOTO data fields
         */
        komoto.setTila(dto.getTila());

        Preconditions.checkNotNull(organisationOId, "Organisation OID cannot be null.");
        komoto.setTarjoaja(organisationOId);
        Preconditions.checkNotNull(dto.getOpintojenMaksullisuus(), "OpintojenMaksullisuus boolean cannot be null.");
        komoto.setMaksullisuus(dto.getOpintojenMaksullisuus().toString());

        //set dates
        commonConverter.handleDates(komoto, dto);

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
        komoto.setKkPohjakoulutusvaatimus(commonConverter.convertToUris(dto.getPohjakoulutusvaatimukset(), komoto.getKkPohjakoulutusvaatimus(), FieldNames.POHJALKOULUTUSVAATIMUS));
        komoto.setAmmattinimikes(commonConverter.convertToUris(dto.getAmmattinimikkeet(), komoto.getAmmattinimikes(), FieldNames.AMMATTINIMIKKEET));

        komoto.setHinta(dto.getHinta() != null ? new BigDecimal(dto.getHinta().toString()) : null);

        komoto.setSuunniteltuKesto(commonConverter.convertToUri(dto.getSuunniteltuKestoTyyppi(), FieldNames.SUUNNITELTUKESTO), dto.getSuunniteltuKestoArvo());
        HashSet<Yhteyshenkilo> yhteyshenkilos = Sets.<Yhteyshenkilo>newHashSet();
        EntityUtils.copyYhteyshenkilos(dto.getYhteyshenkilos(), yhteyshenkilos);
        komoto.setYhteyshenkilos(yhteyshenkilos);
        komotoKuvausConverters.convertTekstiDTOToMonikielinenTeksti(dto.getKuvausKomoto(), komoto.getTekstit());
        komoto.setLastUpdatedByOid(userOid);
        komoto.setNimi(commonConverter.convertToTexts(dto.getKoulutusohjelma(), FieldNames.KOULUTUSOHJELMA)); //OVT-7531

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

        final String organisationOId = dto.getOrganisaatio().getOid();
        Preconditions.checkNotNull(komo, "KOMO object cannot be null.");
        Preconditions.checkNotNull(komoto, "KOMOTO object cannot be null.");
        Preconditions.checkNotNull(komoto.getOid(), "KOMOTO OID cannot be null.");
        Preconditions.checkNotNull(organisationOId, "Organisation OID cannot be null.");
        /*
         * KOMOTO data fields
         */
        komoto.setTila(dto.getTila());

        komoto.setTarjoaja(organisationOId);
       
        //TODO:
        //komoto.setTutkintonimike( dto.getTutkintonimike());

        //set dates
        commonConverter.handleDates(komoto, dto);

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

        komoto.setPohjakoulutusvaatimusUri(commonConverter.convertToUri(dto.getPohjakoulutusvaatimus(), FieldNames.POHJALKOULUTUSVAATIMUS));
        komoto.setSuunniteltuKesto(commonConverter.convertToUri(dto.getSuunniteltuKestoTyyppi(), FieldNames.SUUNNITELTUKESTO), dto.getSuunniteltuKestoArvo());
        HashSet<Yhteyshenkilo> yhteyshenkilos = Sets.<Yhteyshenkilo>newHashSet();
        EntityUtils.copyYhteyshenkilos(dto.getYhteyshenkilos(), yhteyshenkilos);
        komoto.setYhteyshenkilos(yhteyshenkilos);
        komotoKuvausConverters.convertTekstiDTOToMonikielinenTeksti(dto.getKuvausKomoto(), komoto.getTekstit());

        komoto.setLastUpdatedByOid(userOid);
        return komoto;
    }

    private void korkeakouluKomoDataUpdate(Koulutusmoduuli komo, final KoulutusKorkeakouluV1RDTO dto, final String organisationOId) {
        /*
         * KOMO data fields:
         */

        komo.setTutkintoUri(commonConverter.convertToUri(dto.getTutkinto(), FieldNames.TUTKINTO)); //correct data mapping?
        komo.setOpintojenLaajuus(
                commonConverter.convertToUri(dto.getOpintojenLaajuusyksikko(), FieldNames.OPINTOJEN_LAAJUUSYKSIKKO),
                commonConverter.convertToUri(dto.getOpintojenLaajuusarvo(), FieldNames.OPINTOJEN_LAAJUUSARVO));
        komo.setOmistajaOrganisaatioOid(organisationOId); //is this correct?
        komo.setKoulutusasteUri(commonConverter.convertToUri(dto.getKoulutusaste(), FieldNames.KOULUTUSASTE));
        komo.setKoulutusalaUri(commonConverter.convertToUri(dto.getKoulutusala(), FieldNames.KOULUTUSALA));
        komo.setOpintoalaUri(commonConverter.convertToUri(dto.getOpintoala(), FieldNames.OPINTOALA));
        komo.setEqfUri(commonConverter.convertToUri(dto.getEqf(), FieldNames.EQF));
        komo.setTila(dto.getTila()); //has the same status as teh komoto 

        Preconditions.checkNotNull(dto.getKoulutusmoduuliTyyppi(), "KoulutusmoduuliTyyppi enum cannot be null.");
        komo.setModuuliTyyppi(KoulutusmoduuliTyyppi.valueOf(dto.getKoulutusmoduuliTyyppi().name()));
        komo.setKoulutusUri(commonConverter.convertToUri(dto.getKoulutuskoodi(), FieldNames.KOULUTUSKOODI));

        //Kandidaatti can be null object:
        komo.setKandidaatinKoulutuskoodi(commonConverter.convertToUri(dto.getKandidaatinKoulutuskoodi(), FieldNames.KOULUTUSKOODI_KANDIDAATTI, true));

        komo.setUlkoinenTunniste(dto.getTunniste());

        Preconditions.checkNotNull(dto.getKoulutusasteTyyppi(), "KoulutusasteTyyppi enum cannot be null.");
        komo.setKoulutustyyppi(dto.getKoulutusasteTyyppi().value());

        komo.setTutkintonimikes(commonConverter.convertToUris(dto.getTutkintonimikes(), komo.getTutkintonimikes(), FieldNames.TUTKINTONIMIKE));
        //Preconditions.checkArgument(dto.getTutkintonimikes().getUris().isEmpty(), "Set of Tutkintonimike objects cannot be empty.");

        komoKuvausConverters.convertTekstiDTOToMonikielinenTeksti(dto.getKuvausKomo(), komo.getTekstit());
    }
}
