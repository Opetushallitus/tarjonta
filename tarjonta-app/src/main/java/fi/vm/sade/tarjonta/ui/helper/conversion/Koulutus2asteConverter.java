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
package fi.vm.sade.tarjonta.ui.helper.conversion;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutuksenKestoTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.NimettyMonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.enums.KoulutusasteType;
import fi.vm.sade.tarjonta.ui.enums.Koulutustyyppi;
import fi.vm.sade.tarjonta.ui.helper.OidCreationException;
import fi.vm.sade.tarjonta.ui.helper.OidHelper;
import static fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConveter.INVALID_DATA;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusohjelmaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusLisatiedotModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusLisatietoModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.org.OrganisationOidNamePair;

/**
 *
 * @author Jani Wilén
 */
@Component
public class Koulutus2asteConverter extends KoulutusConveter {

    private static final Logger LOG = LoggerFactory.getLogger(Koulutus2asteConverter.class);
    @Autowired(required = true)
    private OidHelper oidHelper;

    public Koulutus2asteConverter() {
        super();
    }

    /**
     * Full data copy from UI model to tyyppi.
     *
     * @param model
     * @return
     * @throws ExceptionMessage
     */
    public PaivitaKoulutusTyyppi createPaivitaKoulutusTyyppi(final TarjontaModel tarjontaModel, final OrganisationOidNamePair pair, final String komotoOid) throws OidCreationException {
        KoulutusToisenAsteenPerustiedotViewModel model = tarjontaModel.getKoulutusPerustiedotModel();
        Preconditions.checkNotNull(komotoOid, "KOMOTO OID cannot be null.");
        final OrganisaatioPerustieto dto = searchOrganisationByOid(tarjontaModel.getTarjoajaModel().getSelectedOrganisationOid(), pair);

        PaivitaKoulutusTyyppi paivita = new PaivitaKoulutusTyyppi();
        paivita.setVersion(tarjontaModel.getKoulutusPerustiedotModel().getVersion());
        mapToKoulutusTyyppi(paivita, model, komotoOid, dto);

        //convert yhteyshenkilo model objects to yhteyshenkilo type objects.
        //addToYhteyshenkiloTyyppiList(model.getYhteyshenkilot(), paivita.getYhteyshenkiloTyyppi());
        if (model.getYhtHenkKokoNimi() != null && !model.getYhtHenkKokoNimi().isEmpty()) {
            paivita.getYhteyshenkiloTyyppi().add(mapYhteyshenkiloToTyyppi(model));
        }

        //convert linkki model objects to linkki type objects.
        //addToWebLinkkiTyyppiList(model.getKoulutusLinkit(), paivita.getLinkki());
        if (model.getOpsuLinkki() != null && !model.getOpsuLinkki().isEmpty()) {
            paivita.getLinkki().add(mapOpetussuunnitelmaLinkkiToTyyppi(model.getOpsuLinkki()));
        }

        // Lisätiedot
        mapToKoulutusLisatiedotTyyppi(paivita, tarjontaModel.getKoulutusLisatiedotModel());

        return paivita;
    }

    public LisaaKoulutusTyyppi createLisaaKoulutusTyyppi(TarjontaModel tarjontaModel, OrganisationOidNamePair selectedOrganisation) throws OidCreationException {
        final String organisaatioOid = tarjontaModel.getTarjoajaModel().getSelectedOrganisationOid();
        KoulutusToisenAsteenPerustiedotViewModel model = tarjontaModel.getKoulutusPerustiedotModel();

        final OrganisaatioPerustieto organisaatio = searchOrganisationByOid(organisaatioOid, selectedOrganisation);

        LisaaKoulutusTyyppi lisaa = new LisaaKoulutusTyyppi();
        mapToKoulutusTyyppi(lisaa, model, oidHelper.getOid(TarjontaOidType.KOMOTO), organisaatio);

        //convert yhteyshenkilo model objects to yhteyshenkilo type objects.
        //addToYhteyshenkiloTyyppiList(model.getYhteyshenkilot(), lisaa.getYhteyshenkilo());
        if (model.getYhtHenkKokoNimi() != null && !model.getYhtHenkKokoNimi().isEmpty()) {
            lisaa.getYhteyshenkiloTyyppi().add(mapYhteyshenkiloToTyyppi(model));
        }

        //convert linkki model objects to linkki type objects.
        //addToWebLinkkiTyyppiList(model.getKoulutusLinkit(), lisaa.getLinkki());
        if (model.getOpsuLinkki() != null && !model.getOpsuLinkki().isEmpty()) {
            lisaa.getLinkki().add(mapOpetussuunnitelmaLinkkiToTyyppi(model.getOpsuLinkki()));
        }

        // Lisätiedot
        mapToKoulutusLisatiedotTyyppi(lisaa, tarjontaModel.getKoulutusLisatiedotModel());

        return lisaa;
    }

    /**
     *
     * Full data copy from tyyppi to UI model.
     *
     * @param tyyppi
     * @param status
     * @return
     * @throws ExceptionMessage
     */
    public KoulutusToisenAsteenPerustiedotViewModel createKoulutusPerustiedotViewModel(
            TarjontaModel model,
            final LueKoulutusVastausTyyppi tyyppi,
            Locale locale,
            final boolean searchLatestKoodistoUris) throws OidCreationException {
        //set selected tarjoaja to UI model
        final OrganisationOidNamePair pair = new OrganisationOidNamePair();
        final OrganisaatioPerustieto organisaatio = searchOrganisationByOid(tyyppi.getTarjoaja(), pair);
        model.getTarjoajaModel().setSelectedOrganisation(pair);

        KoulutusToisenAsteenPerustiedotViewModel model2Aste = mapToKoulutusToisenAsteenPerustiedotViewModel(tyyppi, DocumentStatus.NEW, organisaatio, locale, searchLatestKoodistoUris);
        //addToKoulutusYhteyshenkiloViewModel(tyyppi.getYhteyshenkilo(), model2Aste.getYhteyshenkilot());
        mapYhteyshenkiloToViewModel(model2Aste, tyyppi);
        //addToKoulutusLinkkiViewModel(tyyppi.getLinkki(), model2Aste.getKoulutusLinkit());
        if (tyyppi.getLinkki() != null && !tyyppi.getLinkki().isEmpty()) {
            model2Aste.setOpsuLinkki(tyyppi.getLinkki().get(0).getUri());
        }

        if (tyyppi.getNimi() != null && tyyppi.getNimi().getTeksti().size() == 1) {
            //aseta nimi jos on (valmentava ka kuntouttava)
            model2Aste.setNimi(tyyppi.getNimi().getTeksti().get(0).getValue());
        }

        return model2Aste;
    }

    public static KoulutusTyyppi mapToKoulutusTyyppi(KoulutusTyyppi tyyppi, final KoulutusToisenAsteenPerustiedotViewModel model, final String komotoOid,
            OrganisaatioPerustieto organisatio) {
        Preconditions.checkNotNull(tyyppi, INVALID_DATA + "KoulutusTyyppi object cannot be null.");
        Preconditions.checkNotNull(model, INVALID_DATA + "KoulutusToisenAsteenPerustiedotViewModel object cannot be null.");
        Preconditions.checkNotNull(komotoOid, INVALID_DATA + "KOMOTO OID cannot be null.");
        Preconditions.checkNotNull(organisatio, INVALID_DATA + "Organisation DTO cannot be null.");
        Preconditions.checkNotNull(organisatio.getOid(), INVALID_DATA + "Organisation OID cannot be null.");

        tyyppi.setTarjoaja(organisatio.getOid());
        tyyppi.setOid(komotoOid);
        tyyppi.setKoulutustyyppi(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);
        //tyyppi.setKoulutusaste(mapToValidKoodistoKoodiTyyppi(false, model.getKoulutuskoodiModel().getKoulutusaste()));
        tyyppi.setKoulutusKoodi(mapToValidKoodistoKoodiTyyppi(false, model.getKoulutuskoodiModel()));

        //URI data example : "koulutusohjelma/1603#1"
        tyyppi.setKoulutusohjelmaKoodi(mapToValidKoodistoKoodiTyyppi(true, model.getKoulutusohjelmaModel()));
        tyyppi.setKoulutuksenAlkamisPaiva(model.getKoulutuksenAlkamisPvm());
        KoulutuksenKestoTyyppi koulutuksenKestoTyyppi = new KoulutuksenKestoTyyppi();
        koulutuksenKestoTyyppi.setArvo(model.getSuunniteltuKesto());
        koulutuksenKestoTyyppi.setYksikko(model.getSuunniteltuKestoTyyppi());

        ConversionUtils.setTeksti(tyyppi.getTekstit(), KomotoTeksti.PAINOTUS, mapToMonikielinenTekstiTyyppi(model.getPainotus()));
        tyyppi.setKesto(koulutuksenKestoTyyppi);
        tyyppi.setPohjakoulutusvaatimus(createKoodi(model.getPohjakoulutusvaatimus(), true, "pohjakoulutusvaatimus"));

        //TODO: create a different form model for every level of education: 
        //The datatypes on bottom must be list types as in future we need to have 
        //an option to select multiple languages etc. (lukio, AMK etc...)
        tyyppi.getOpetusmuoto().add(createKoodi(model.getOpetusmuoto(), true, "opetusmuoto"));
        tyyppi.getOpetuskieli().add(createKoodi(model.getOpetuskieli(), true, "opetuskieli"));
        tyyppi.getKoulutuslaji().add(createKoodi(model.getKoulutuslaji(), true, "koulutuslaji"));
        tyyppi.setViimeisinPaivittajaOid(model.getViimeisinPaivittajaOid());
        if (model.getViimeisinPaivitysPvm() != null) {
            tyyppi.setViimeisinPaivitysPvm(model.getViimeisinPaivitysPvm());
        } else {
            tyyppi.setViimeisinPaivitysPvm(new Date());
        }

        if (model.getOpintojenLaajuusTot() != null && !model.getOpintojenLaajuusTot().isEmpty()) {
            KoulutuksenKestoTyyppi laajuus = new KoulutuksenKestoTyyppi();
            laajuus.setArvo(model.getOpintojenLaajuusTot());
            laajuus.setYksikko(model.getOpintojenLaajuusyksikkoTot());
            tyyppi.setLaajuus(laajuus);
        }

        if (model.getNimi() != null) {
            String lang = model.getOpetuskieli();
            tyyppi.setNimi(new MonikielinenTekstiTyyppi(Lists.newArrayList(new MonikielinenTekstiTyyppi.Teksti(model.getNimi(), lang))));
        }

        //update latest komoto uris to database
        convertCommonModelToKoulutusmoduuliKoosteTyyppi(model.getKoulutuskoodiModel(), model.getKoulutusohjelmaModel(), tyyppi);

        return tyyppi;
    }

    private KoulutusToisenAsteenPerustiedotViewModel mapToKoulutusToisenAsteenPerustiedotViewModel(
            LueKoulutusVastausTyyppi koulutus,
            DocumentStatus status,
            OrganisaatioPerustieto organisatio, Locale locale,
            final boolean searchLatestKoodistoUris
    ) {
        Preconditions.checkNotNull(koulutus, INVALID_DATA + "LueKoulutusVastausTyyppi object cannot be null.");
        Preconditions.checkNotNull(status, INVALID_DATA + "DocumentStatus enum cannot be null.");
        Preconditions.checkNotNull(organisatio, INVALID_DATA + "Organisation DTO cannot be null.");
        Preconditions.checkNotNull(organisatio.getOid(), INVALID_DATA + "Organisation OID cannot be null.");

        KoulutusToisenAsteenPerustiedotViewModel model2Aste = new KoulutusToisenAsteenPerustiedotViewModel(status);
        model2Aste.setTila(koulutus.getTila());
        model2Aste.setOid(koulutus.getOid());
        model2Aste.setVersion(koulutus.getVersion());

        /* Select KOMO by koulutusaste, koulutuskoodi and koulutusohjelma */
        model2Aste.setKoulutuskoodiModel(mapToKoulutuskoodiModel(koulutus.getKoulutusKoodi(), locale));
        model2Aste.setKoulutusohjelmaModel(mapToKoulutusohjelmaModel(koulutus.getKoulutusohjelmaKoodi(), locale));
        model2Aste.setPainotus(mapToKielikaannosViewModel(ConversionUtils.getTeksti(koulutus.getTekstit(), KomotoTeksti.PAINOTUS)));

        if (koulutus.getViimeisinPaivitysPvm() != null) {
            model2Aste.setViimeisinPaivitysPvm(koulutus.getViimeisinPaivitysPvm());
        }
        model2Aste.setViimeisinPaivittajaOid(koulutus.getViimeisinPaivittajaOid());
        model2Aste.setKoulutuksenAlkamisPvm(
                koulutus.getKoulutuksenAlkamisPaiva() != null ? koulutus.getKoulutuksenAlkamisPaiva() : null);

        if (koulutus.getKesto() != null) {
            model2Aste.setSuunniteltuKesto(koulutus.getKesto().getArvo());
            model2Aste.setSuunniteltuKestoTyyppi(koulutus.getKesto().getYksikko());
        }

        if (koulutus.getLaajuus() != null) {
            model2Aste.setOpintojenLaajuusTot(koulutus.getLaajuus().getArvo());
            model2Aste.setOpintojenLaajuusyksikkoTot(koulutus.getLaajuus().getYksikko());
        }

        model2Aste.setKoulutusaste(convert(koulutus.getKoulutusaste()));

        if (koulutus.getPohjakoulutusvaatimus() != null) {
            model2Aste.setPohjakoulutusvaatimus(getUri(koulutus.getPohjakoulutusvaatimus()));
        }

        if (koulutus.getOpetusmuoto() != null && !koulutus.getOpetusmuoto().isEmpty()) {
            model2Aste.setOpetusmuoto(getUri(koulutus.getOpetusmuoto().get(0)));
        }

        if (koulutus.getOpetuskieli() != null && !koulutus.getOpetuskieli().isEmpty()) {
            model2Aste.setOpetuskieli(getUri(koulutus.getOpetuskieli().get(0)));
        }

        if (koulutus.getKoulutuslaji() != null && !koulutus.getKoulutuslaji().isEmpty()) {
            model2Aste.setKoulutuslaji(getUri(koulutus.getKoulutuslaji().get(0)));
        }

        /*
         * KOMO
         */
        final KoulutusmoduuliKoosteTyyppi koulutusmoduuliTyyppi = koulutus.getKoulutusmoduuli();

        koulutusKoodisto.listaa2asteSisalto(model2Aste.getKoulutuskoodiModel(), model2Aste.getKoulutusohjelmaModel(), koulutusmoduuliTyyppi, locale, searchLatestKoodistoUris);

        /*
         * Create real visible name, the name is also used in koulutus search.  
         */
        koulutus.getNimi();

        return model2Aste;
    }

    private KoodiModel convert(KoodistoKoodiTyyppi koodistoKoodiTyyppi) {
        if (koodistoKoodiTyyppi == null) {
            return null;
        }
        KoodiModel koulutusAste = new KoodiModel();
        koulutusAste.setKoodistoUri(koodistoKoodiTyyppi.getUri());
        return koulutusAste;
    }

    public KoulutusLisatiedotModel createKoulutusLisatiedotViewModel(LueKoulutusVastausTyyppi lueKoulutus) {
        KoulutusLisatiedotModel result = new KoulutusLisatiedotModel();

        result.setAmmattinimikkeet(mapToKoodistoKoodis(lueKoulutus.getAmmattinimikkeet()));

        for (NimettyMonikielinenTekstiTyyppi nmt : lueKoulutus.getTekstit()) {
            KomotoTeksti kt = KomotoTeksti.valueOf(nmt.getTunniste());
            for (MonikielinenTekstiTyyppi.Teksti t : nmt.getTeksti()) {
                KoulutusLisatietoModel klm = result.getLisatiedot(t.getKieliKoodi());
                switch (kt) {
                    case KUVAILEVAT_TIEDOT:
                        klm.setKuvailevatTiedot(t.getValue());
                        break;
                    case KANSAINVALISTYMINEN:
                        klm.setKansainvalistyminen(t.getValue());
                        break;
                    case SIJOITTUMINEN_TYOELAMAAN:
                        klm.setSijoittuminenTyoelamaan(t.getValue());
                        break;
                    case SISALTO:
                        klm.setSisalto(t.getValue());
                        break;
                    case YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA:
                        klm.setYhteistyoMuidenToimijoidenKanssa(t.getValue());
                        break;
                    case KOULUTUSOHJELMAN_VALINTA:
                        klm.setKoulutusohjelmanValinta(t.getValue());
                        break;
                    default:
                        break; //ignore
                }
            }
        }

        return result;
    }

    private KoulutusohjelmaModel mapToKoulutusohjelmaModel(final KoodistoKoodiTyyppi tyyppi, final Locale locale) {
        if (tyyppi != null && tyyppi.getUri() != null) {

            if (tyyppi.getUri() == null && tyyppi.getVersio() == null) {
                throw new RuntimeException(INVALID_DATA + "URI cannot be null.");
            }

            return koulutusKoodisto.listaaKoulutusohjelma(tyyppi, locale);
        }

        LOG.warn("Data conversion error - koulutusohjelma koodi URI not found.");

        return null;
    }

    /*
     *  Sanity check for the data.
     */
    public void validateSaveData(KoulutusTyyppi lisaa, KoulutusToisenAsteenPerustiedotViewModel model) {
        Preconditions.checkNotNull(lisaa.getTarjoaja(), "Data validation failed - organisation OID is required!");
        Preconditions.checkNotNull(lisaa.getKoulutusKoodi(), "Data validation failed - koulutuskoodi URI is required!");

        if (lisaa.getTarjoaja().length() != 0) {
        } else {
            throw new RuntimeException("Data validation failed - organisation OID value is empty!");
        }

        final KoodistoKoodiTyyppi koulutusohjelmaKoodi = lisaa.getKoulutusohjelmaKoodi();

        if (model.getKoulutuskoodiModel().getKoulutusaste() == null) {
            throw new RuntimeException("Data validation failed - koulutusaste is required!");
        }

        final String koulutusaste = model.getKoulutuskoodiModel().getKoulutusaste().getKoodi();

        if (koulutusaste == null) {
            throw new RuntimeException("Data validation failed - koulutusaste numeric code is required!");
        }

        final KoulutusasteTyyppi koulutusastetyyppi = lisaa.getKoulutustyyppi();

        Preconditions.checkNotNull(koulutusaste, "Data validation failed - koulutustyyppi is required!");

        if ((koulutusaste.equals(KoulutusasteType.TOINEN_ASTE_AMMATILLINEN_KOULUTUS.getKoulutusaste())
                || koulutusaste.equals(KoulutusasteType.TUNTEMATON.getKoulutusaste())
                || koulutusaste.equals(KoulutusasteType.PERUSOPETUKSEN_LISAOPETUS)
                || koulutusastetyyppi == KoulutusasteTyyppi.MAAHANM_LUKIO_VALMISTAVA_KOULUTUS)
                && koulutusohjelmaKoodi == null || koulutusohjelmaKoodi.getUri() == null) {
            throw new RuntimeException("Persist failed - koulutusohjelma URI is required!");
        } else if (koulutusastetyyppi != KoulutusasteTyyppi.MAAHANM_LUKIO_VALMISTAVA_KOULUTUS
                && koulutusaste.equals(KoulutusasteType.TOINEN_ASTE_LUKIO.getKoulutusaste())) {
            //Lukio tutkinto do not have koulutusohjema data.
            lisaa.setKoulutusohjelmaKoodi(new KoodistoKoodiTyyppi());
            //just to make sure that Lukio do not send 'koulutuslaji' data to back-end.
            lisaa.getKoulutuslaji().clear();
            LOG.debug("Koulutuskoodi URI : '" + lisaa.getKoulutusKoodi().getUri() + "'");
        } else {
            LOG.debug("Koulutuskoodi URI : '" + lisaa.getKoulutusKoodi().getUri() + "', koulutusohjelma URI : '" + koulutusohjelmaKoodi.getUri() + "' ");
        }
    }

    private void mapToKoulutusLisatiedotTyyppi(KoulutusTyyppi koulutus, KoulutusLisatiedotModel koulutusLisatiedotModel) {

        koulutus.getAmmattinimikkeet().clear();
        for (String uri : koulutusLisatiedotModel.getAmmattinimikkeet()) {
            koulutus.getAmmattinimikkeet().add(toKoodistoKoodiTyypi.apply(uri));
        }

        koulutus.getTekstit().clear();
        for (String kieliUri : koulutusLisatiedotModel.getLisatiedot().keySet()) {

            LOG.debug("koulutusLisatiedotModel.getLisatiedot().keySet() '" + kieliUri + "', " + koulutusLisatiedotModel.getLisatiedot().keySet());
            KoulutusLisatietoModel lisatieto = koulutusLisatiedotModel.getLisatiedot(kieliUri);
            ConversionUtils.setTeksti(koulutus.getTekstit(), KomotoTeksti.KUVAILEVAT_TIEDOT, kieliUri, lisatieto.getKuvailevatTiedot());
            ConversionUtils.setTeksti(koulutus.getTekstit(), KomotoTeksti.SISALTO, kieliUri, lisatieto.getSisalto());
            ConversionUtils.setTeksti(koulutus.getTekstit(), KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN, kieliUri, lisatieto.getSijoittuminenTyoelamaan());
            ConversionUtils.setTeksti(koulutus.getTekstit(), KomotoTeksti.KANSAINVALISTYMINEN, kieliUri, lisatieto.getKansainvalistyminen());
            ConversionUtils.setTeksti(koulutus.getTekstit(), KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA, kieliUri, lisatieto.getYhteistyoMuidenToimijoidenKanssa());
            ConversionUtils.setTeksti(koulutus.getTekstit(), KomotoTeksti.KOULUTUSOHJELMAN_VALINTA, kieliUri, lisatieto.getKoulutusohjelmanValinta());
        }
    }

    public static Map<Map.Entry<String, String>, KoulutusmoduuliKoosteTyyppi> full2asteKomoCacheMap(Collection<KoulutusmoduuliKoosteTyyppi> komos) {
        Map<Map.Entry<String, String>, KoulutusmoduuliKoosteTyyppi> hashMap = new HashMap<Map.Entry<String, String>, KoulutusmoduuliKoosteTyyppi>();

        for (KoulutusmoduuliKoosteTyyppi komo : komos) {

            Map.Entry<String, String> e = new AbstractMap.SimpleEntry<String, String>(
                    TarjontaKoodistoHelper.getKoodiURIFromVersionedUri(komo.getKoulutuskoodiUri()),
                    TarjontaKoodistoHelper.getKoodiURIFromVersionedUri(komo.getKoulutusohjelmakoodiUri())
            );
            hashMap.put(e, komo);
        }

        return hashMap;
    }

    public void updateKoulutuskoodiAndKoulutusohjelmaAndRelationsFromKoodisto(KoulutusToisenAsteenPerustiedotViewModel model, KoulutusmoduuliKoosteTyyppi tyyppi, Locale locale) {
        model.setKoulutuskoodiModel(koulutusKoodisto.listaaKoulutuskoodi(model.getKoulutuskoodiModel().getKoodistoUri(), locale));
        model.setKoulutusohjelmaModel(koulutusKoodisto.listaaKoulutusohjelma(model.getKoulutusohjelmaModel().getKoodistoUri(), locale));

        Preconditions.checkNotNull(model.getKoulutuskoodiModel(), INVALID_DATA + " koulutuskoodi model cannot be null.");
        Preconditions.checkNotNull(model.getKoulutusohjelmaModel(), INVALID_DATA + " koulutusohjelma model cannot be null.");

        koulutusKoodisto.listaa2asteSisalto(model.getKoulutuskoodiModel(), model.getKoulutusohjelmaModel(), tyyppi, locale, true);
    }
}
