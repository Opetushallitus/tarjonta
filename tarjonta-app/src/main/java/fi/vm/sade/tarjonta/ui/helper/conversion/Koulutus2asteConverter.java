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

import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.oid.service.types.NodeClassCode;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.organisaatio.helper.OrganisaatioDisplayHelper;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutuksenKestoTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.enums.KoulutusasteType;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusLisatiedotModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusLisatietoModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusohjelmaModel;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jani Wilén
 */
@Component
public class Koulutus2asteConverter extends KoulutusConveter {

    private static final Logger LOG = LoggerFactory.getLogger(Koulutus2asteConverter.class);
    @Autowired(required = true)
    private OIDService oidService;
    @Autowired(required = true)
    private KoulutusKoodistoConverter koulutusKoodisto;

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
    public PaivitaKoulutusTyyppi createPaivitaKoulutusTyyppi(final TarjontaModel tarjontaModel, final String komotoOid) throws ExceptionMessage {

        KoulutusToisenAsteenPerustiedotViewModel model = tarjontaModel.getKoulutusPerustiedotModel();

        if (komotoOid == null) {
            throw new RuntimeException(INVALID_DATA + "KOMOTO OID cannot be null.");
        }
        final OrganisaatioDTO organisaatio = searchOrganisatioByOid(model.getOrganisaatioOid());

        PaivitaKoulutusTyyppi paivita = new PaivitaKoulutusTyyppi();
        mapToKoulutusTyyppi(paivita, model, komotoOid, organisaatio);

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

    public LisaaKoulutusTyyppi createLisaaKoulutusTyyppi(TarjontaModel tarjontaModel, final String organisaatioOid) throws ExceptionMessage {

        KoulutusToisenAsteenPerustiedotViewModel model = tarjontaModel.getKoulutusPerustiedotModel();

        final OrganisaatioDTO organisaatio = searchOrganisatioByOid(organisaatioOid);

        LisaaKoulutusTyyppi lisaa = new LisaaKoulutusTyyppi();
        mapToKoulutusTyyppi(lisaa, model, oidService.newOid(NodeClassCode.TEKN_5), organisaatio);

        //convert yhteyshenkilo model objects to yhteyshenkilo type objects.
        //addToYhteyshenkiloTyyppiList(model.getYhteyshenkilot(), lisaa.getYhteyshenkilo());

        if (model.getYhtHenkKokoNimi() != null && !model.getYhtHenkKokoNimi().isEmpty()) {
            lisaa.getYhteyshenkilo().add(mapYhteyshenkiloToTyyppi(model));
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
    public KoulutusToisenAsteenPerustiedotViewModel createKoulutusPerustiedotViewModel(final LueKoulutusVastausTyyppi tyyppi, final DocumentStatus status,
            Locale locale) throws ExceptionMessage {
        final OrganisaatioDTO organisaatio = searchOrganisatioByOid(tyyppi.getTarjoaja());

        KoulutusToisenAsteenPerustiedotViewModel model2Aste = mapToKoulutusToisenAsteenPerustiedotViewModel(tyyppi, status, organisaatio, locale);
        //addToKoulutusYhteyshenkiloViewModel(tyyppi.getYhteyshenkilo(), model2Aste.getYhteyshenkilot());
        mapYhteyshenkiloToViewModel(model2Aste, tyyppi);
        //addToKoulutusLinkkiViewModel(tyyppi.getLinkki(), model2Aste.getKoulutusLinkit());
        if (tyyppi.getLinkki() != null && !tyyppi.getLinkki().isEmpty()) {
            model2Aste.setOpsuLinkki(tyyppi.getLinkki().get(0).getUri());
        }

        return model2Aste;
    }

    public static KoulutusTyyppi mapToKoulutusTyyppi(KoulutusTyyppi tyyppi, final KoulutusToisenAsteenPerustiedotViewModel model, final String komotoOid,
            OrganisaatioDTO organisatio) {
        if (tyyppi == null) {
            throw new RuntimeException(INVALID_DATA + "KoulutusTyyppi object cannot be null.");
        }

        if (model == null) {
            throw new RuntimeException(INVALID_DATA + "KoulutusToisenAsteenPerustiedotViewModel object cannot be null.");
        }

        if (komotoOid == null) {
            throw new RuntimeException(INVALID_DATA + "KOMOTO OID cannot be null.");
        }

        if (organisatio == null) {
            throw new RuntimeException(INVALID_DATA + "Organisatio DTO cannot be null.");
        }

        tyyppi.setTarjoaja(organisatio.getOid());
        tyyppi.setOid(komotoOid);
        tyyppi.setKoulutustyyppi(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);
        tyyppi.setKoulutusaste(mapToValidKoodistoKoodiTyyppi(false, model.getKoulutuskoodiModel().getKoulutusaste()));
        tyyppi.setKoulutusKoodi(mapToValidKoodistoKoodiTyyppi(false, model.getKoulutuskoodiModel()));

        //URI data example : "koulutusohjelma/1603#1"
        tyyppi.setKoulutusohjelmaKoodi(mapToValidKoodistoKoodiTyyppi(true, model.getKoulutusohjelmaModel()));
        tyyppi.setKoulutuksenAlkamisPaiva(model.getKoulutuksenAlkamisPvm());
        KoulutuksenKestoTyyppi koulutuksenKestoTyyppi = new KoulutuksenKestoTyyppi();
        koulutuksenKestoTyyppi.setArvo(model.getSuunniteltuKesto());
        koulutuksenKestoTyyppi.setYksikko(model.getSuunniteltuKestoTyyppi());
        tyyppi.setPainotus(mapToMonikielinenTekstiTyyppi(model.getPainotus()));
        tyyppi.setKesto(koulutuksenKestoTyyppi);
        tyyppi.setPohjakoulutusvaatimus(createKoodi(model.getPohjakoulutusvaatimus(), true, "pohjakoulutusvaatimus"));

        //TODO: create a different form model for every level of education: 
        //The datatypes on bottom must be list types as in future we need to have 
        //an option to select multiple languages etc. (lukio, AMK etc...)
        tyyppi.getOpetusmuoto().add(createKoodi(model.getOpetusmuoto(), true, "opetusmuoto"));
        tyyppi.getOpetuskieli().add(createKoodi(model.getOpetuskieli(), true, "opetuskieli"));
        tyyppi.getKoulutuslaji().add(createKoodi(model.getKoulutuslaji(), true, "koulutuslaji"));

        return tyyppi;
    }

    private KoulutusToisenAsteenPerustiedotViewModel mapToKoulutusToisenAsteenPerustiedotViewModel(LueKoulutusVastausTyyppi koulutus, DocumentStatus status,
            OrganisaatioDTO organisatio, Locale locale) {
        if (koulutus == null) {
            throw new RuntimeException(INVALID_DATA + "LueKoulutusVastausTyyppi object cannot be null.");
        }

        if (status == null) {
            throw new RuntimeException(INVALID_DATA + "DocumentStatus enum cannot be null.");
        }

        if (organisatio == null) {
            throw new RuntimeException(INVALID_DATA + "Organisatio DTO cannot be null.");
        }

        if (organisatio.getOid() == null) {
            throw new RuntimeException(INVALID_DATA + "Organisatio OID cannot be null.");
        }

        KoulutusToisenAsteenPerustiedotViewModel model2Aste = new KoulutusToisenAsteenPerustiedotViewModel(status);
        model2Aste.setTila(koulutus.getTila());
        model2Aste.setOid(koulutus.getOid());
        model2Aste.setOrganisaatioOid(organisatio.getOid());
        model2Aste.setOrganisaatioName(OrganisaatioDisplayHelper.getClosest(locale, organisatio));

        /* Select KOMO by koulutusaste, koulutuskoodi and koulutusohjelma */
        model2Aste.setKoulutuskoodiModel(mapToKoulutuskoodiModel(koulutus.getKoulutusKoodi(), locale));
        model2Aste.setKoulutusohjelmaModel(mapToKoulutusohjelmaModel(koulutus.getKoulutusohjelmaKoodi(), locale));
        model2Aste.setPainotus(mapToKielikaannosViewModel(koulutus.getPainotus()));

        model2Aste.setKoulutuksenAlkamisPvm(
                koulutus.getKoulutuksenAlkamisPaiva() != null ? koulutus.getKoulutuksenAlkamisPaiva().toGregorianCalendar().getTime() : null);


        if (koulutus.getKesto() != null) {
            model2Aste.setSuunniteltuKesto(koulutus.getKesto().getArvo());
            model2Aste.setSuunniteltuKestoTyyppi(koulutus.getKesto().getYksikko());
        }


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

        koulutusKoodisto.listaa2asteSisalto(model2Aste.getKoulutuskoodiModel(), model2Aste.getKoulutusohjelmaModel(), koulutusmoduuliTyyppi, locale);

        /*
         * Create real visible name, the name is also used in koulutus search.  
         */

        koulutus.getNimi();

        return model2Aste;
    }

    public KoulutusLisatiedotModel createKoulutusLisatiedotViewModel(LueKoulutusVastausTyyppi lueKoulutus) {
        KoulutusLisatiedotModel result = new KoulutusLisatiedotModel();

        result.setAmmattinimikkeet(mapToKoodistoKoodis(lueKoulutus.getAmmattinimikkeet()));

        if (lueKoulutus.getKuvailevatTiedot() != null) {
            for (MonikielinenTekstiTyyppi.Teksti mkt : lueKoulutus.getKuvailevatTiedot().getTeksti()) {

                LOG.debug("mkt.getKieliKoodi()" + mkt.getKieliKoodi());

                result.getLisatiedot(mkt.getKieliKoodi()).setKuvailevatTiedot(mkt.getValue());
            }
        }
        if (lueKoulutus.getKansainvalistyminen() != null) {
            for (MonikielinenTekstiTyyppi.Teksti mkt : lueKoulutus.getKansainvalistyminen().getTeksti()) {
                result.getLisatiedot(mkt.getKieliKoodi()).setKansainvalistyminen(mkt.getValue());
            }
        }
        if (lueKoulutus.getSijoittuminenTyoelamaan() != null) {
            for (MonikielinenTekstiTyyppi.Teksti mkt : lueKoulutus.getSijoittuminenTyoelamaan().getTeksti()) {
                result.getLisatiedot(mkt.getKieliKoodi()).setSijoittuminenTyoelamaan(mkt.getValue());
            }
        }
        if (lueKoulutus.getSisalto() != null) {
            for (MonikielinenTekstiTyyppi.Teksti mkt : lueKoulutus.getSisalto().getTeksti()) {
                result.getLisatiedot(mkt.getKieliKoodi()).setSisalto(mkt.getValue());
            }
        }
        if (lueKoulutus.getYhteistyoMuidenToimijoidenKanssa() != null) {
            for (MonikielinenTekstiTyyppi.Teksti mkt : lueKoulutus.getYhteistyoMuidenToimijoidenKanssa().getTeksti()) {
                result.getLisatiedot(mkt.getKieliKoodi()).setYhteistyoMuidenToimijoidenKanssa(mkt.getValue());
            }
        }
        if (lueKoulutus.getKoulutusohjelmanValinta() != null) {
            for (MonikielinenTekstiTyyppi.Teksti mkt : lueKoulutus.getKoulutusohjelmanValinta().getTeksti()) {
                result.getLisatiedot(mkt.getKieliKoodi()).setKoulutusohjelmanValinta(mkt.getValue());
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
        if (lisaa.getTarjoaja() == null) {
            throw new RuntimeException("Data validation failed - organisation OID is required!");
        }

        if (lisaa.getTarjoaja().length() == 0) {
            throw new RuntimeException("Data validation failed - organisation OID value is empty!");
        }

        if (lisaa.getKoulutusKoodi() == null || lisaa.getKoulutusKoodi().getUri() == null) {
            throw new RuntimeException("Data validation failed - koulutuskoodi URI is required!");

        }

        final KoodistoKoodiTyyppi koulutusohjelmaKoodi = lisaa.getKoulutusohjelmaKoodi();

        if (model.getKoulutuskoodiModel().getKoulutusaste() == null) {
            throw new RuntimeException("Data validation failed - koulutusaste is required!");
        }

        final String koulutusaste = model.getKoulutuskoodiModel().getKoulutusaste().getKoodi();

        if (koulutusaste == null) {
            throw new RuntimeException("Data validation failed - koulutusaste numeric code is required!");
        }

        if (koulutusaste.equals(KoulutusasteType.TOINEN_ASTE_AMMATILLINEN_KOULUTUS.getKoulutusaste()) && koulutusohjelmaKoodi == null && koulutusohjelmaKoodi.getUri() == null) {
            throw new RuntimeException("Persist failed - koulutusohjelma URI is required!");
        } else if (koulutusaste.equals(KoulutusasteType.TOINEN_ASTE_LUKIO.getKoulutusaste())) {
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

        clear(koulutus.getKuvailevatTiedot());
        clear(koulutus.getSisalto());
        clear(koulutus.getSijoittuminenTyoelamaan());
        clear(koulutus.getKansainvalistyminen());
        clear(koulutus.getYhteistyoMuidenToimijoidenKanssa());

        for (String kieliUri : koulutusLisatiedotModel.getLisatiedot().keySet()) {

            LOG.debug("koulutusLisatiedotModel.getLisatiedot().keySet() '" + kieliUri + "', " + koulutusLisatiedotModel.getLisatiedot().keySet());


            KoulutusLisatietoModel lisatieto = koulutusLisatiedotModel.getLisatiedot(kieliUri);

            if (koulutus.getKuvailevatTiedot() == null) {
                koulutus.setKuvailevatTiedot(new MonikielinenTekstiTyyppi());
            }

            koulutus.getKuvailevatTiedot().getTeksti().add(convertToMonikielinenTekstiTyyppi(kieliUri, lisatieto.getKuvailevatTiedot()));

            if (koulutus.getSisalto() == null) {
                koulutus.setSisalto(new MonikielinenTekstiTyyppi());
            }

            koulutus.getSisalto().getTeksti().add(convertToMonikielinenTekstiTyyppi(kieliUri, lisatieto.getSisalto()));

            if (koulutus.getSijoittuminenTyoelamaan() == null) {
                koulutus.setSijoittuminenTyoelamaan(new MonikielinenTekstiTyyppi());
            }

            koulutus.getSijoittuminenTyoelamaan().getTeksti().add(convertToMonikielinenTekstiTyyppi(kieliUri, lisatieto.getSijoittuminenTyoelamaan()));

            if (koulutus.getKansainvalistyminen() == null) {
                koulutus.setKansainvalistyminen(new MonikielinenTekstiTyyppi());
            }

            koulutus.getKansainvalistyminen().getTeksti().add(convertToMonikielinenTekstiTyyppi(kieliUri, lisatieto.getKansainvalistyminen()));

            if (koulutus.getYhteistyoMuidenToimijoidenKanssa() == null) {
                koulutus.setYhteistyoMuidenToimijoidenKanssa(new MonikielinenTekstiTyyppi());
            }

            koulutus.getYhteistyoMuidenToimijoidenKanssa().getTeksti().add(convertToMonikielinenTekstiTyyppi(kieliUri, lisatieto.
                    getYhteistyoMuidenToimijoidenKanssa()));

            if (koulutus.getKoulutusohjelmanValinta() == null) {
                koulutus.setKoulutusohjelmanValinta(new MonikielinenTekstiTyyppi());
            }

            koulutus.getKoulutusohjelmanValinta().getTeksti().add(convertToMonikielinenTekstiTyyppi(kieliUri, lisatieto.getKoulutusohjelmanValinta()));
        }
    }
    
     public static Map<Map.Entry, KoulutusmoduuliKoosteTyyppi> full2asteKomoCacheMap(Collection<KoulutusmoduuliKoosteTyyppi> komos) {
        Map<Map.Entry, KoulutusmoduuliKoosteTyyppi> hashMap = new HashMap<Map.Entry, KoulutusmoduuliKoosteTyyppi>();

        for (KoulutusmoduuliKoosteTyyppi komo : komos) {
            Map.Entry e = new AbstractMap.SimpleEntry<String, String>(komo.getKoulutuskoodiUri(), komo.getKoulutusohjelmakoodiUri());
            hashMap.put(e, komo);
        }

        return hashMap;
    }
}