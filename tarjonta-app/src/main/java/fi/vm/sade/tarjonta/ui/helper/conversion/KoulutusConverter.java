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

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.tarjonta.ui.model.KoulutusYhteyshenkiloViewModel;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.oid.service.types.NodeClassCode;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.organisaatio.helper.OrganisaatioDisplayHelper;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutuksenKestoTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi.Teksti;
import fi.vm.sade.tarjonta.service.types.WebLinkkiTyyppi;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.enums.KoulutusasteType;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import static fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper.splitKoodiURIAllowNull;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusKoodistoModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusLinkkiViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusLisatiedotModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusLisatietoModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioKuvailevatTiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.YhteyshenkiloModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusohjelmaModel;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.MonikielinenTekstiModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 *
 * @author Jani Wilén
 */
@Component
public class KoulutusConverter {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusConverter.class);
    public static final String INVALID_DATA = "Invalid data exception - ";
    @Autowired(required = true)
    private OIDService oidService;
    @Autowired(required = true)
    private KoulutusKoodistoConverter koulutusKoodisto;
    @Autowired(required = true)
    private OrganisaatioService organisaatioService;

    public KoulutusConverter() {
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

    /**
     * Full data copy from UI model to tyyppi.
     *
     * @param model
     * @return
     * @throws ExceptionMessage
     */
    public PaivitaKoulutusTyyppi createPaivitaLukioKoulutusTyyppi(final TarjontaModel tarjontaModel, final String komotoOid) throws ExceptionMessage {
        Preconditions.checkNotNull(komotoOid, INVALID_DATA + "KOMOTO OID cannot be null.");
  
        KoulutusLukioPerustiedotViewModel model = tarjontaModel.getKoulutusLukioPerustiedot();

        final OrganisaatioDTO organisaatio = searchOrganisatioByOid(model.getOrganisaatioOid());

        PaivitaKoulutusTyyppi paivita = new PaivitaKoulutusTyyppi();
        mapToLukioKoulutusTyyppi(paivita, model, komotoOid, organisaatio);

        //convert yhteyshenkilo model objects to yhteyshenkilo type objects.
        //addToYhteyshenkiloTyyppiList(model.getYhteyshenkilot(), paivita.getYhteyshenkiloTyyppi());
        if (model.getYhteyshenkilo().getYhtHenkKokoNimi() != null && !model.getYhteyshenkilo().getYhtHenkKokoNimi().isEmpty()) {
            paivita.getYhteyshenkiloTyyppi().add(mapYhteyshenkiloToTyyppi(model.getYhteyshenkilo()));
        }

        //convert linkki model objects to linkki type objects.
        //addToWebLinkkiTyyppiList(model.getKoulutusLinkit(), paivita.getLinkki());
        if (model.getOpsuLinkki() != null && !model.getOpsuLinkki().isEmpty()) {
            paivita.getLinkki().add(mapOpetussuunnitelmaLinkkiToTyyppi(model.getOpsuLinkki()));
        }

        // kuvailevat tiedot
        mapToLukioKoulutusLisatiedotTyyppi(paivita, tarjontaModel.getKoulutusLukioKuvailevatTiedot());

        return paivita;
    }

  //XXX Jani needs to fix this, not complete!!!
    public static KoulutusTyyppi mapToLukioKoulutusTyyppi(KoulutusTyyppi tyyppi, final KoulutusLukioPerustiedotViewModel model, final String komotoOid,
            OrganisaatioDTO organisatio) {
        Preconditions.checkNotNull(tyyppi, INVALID_DATA + "KoulutusTyyppi object cannot be null.");
        Preconditions.checkNotNull(model, INVALID_DATA + "KoulutusToisenAsteenPerustiedotViewModel object cannot be null.");
        Preconditions.checkNotNull(komotoOid, INVALID_DATA + "KOMOTO OID cannot be null.");
        Preconditions.checkNotNull(organisatio, INVALID_DATA + "Organisatio DTO cannot be null.");
        

        tyyppi.setTarjoaja(organisatio.getOid());
        tyyppi.setOid(komotoOid);
        tyyppi.setKoulutusaste(mapToValidKoodistoKoodiTyyppi(false, model.getKoulutuskoodiModel().getKoulutusaste()));
        tyyppi.setKoulutusKoodi(mapToValidKoodistoKoodiTyyppi(false, model.getKoulutuskoodiModel()));

        //URI data example : "koulutusohjelma/1603#1"
//        tyyppi.setKoulutusohjelmaKoodi(mapToValidKoodistoKoodiTyyppi(true, model.getKoulutusohjelmaModel()));
        tyyppi.setKoulutuksenAlkamisPaiva(model.getKoulutuksenAlkamisPvm());
        KoulutuksenKestoTyyppi koulutuksenKestoTyyppi = new KoulutuksenKestoTyyppi();
        koulutuksenKestoTyyppi.setArvo(model.getSuunniteltuKesto());
        koulutuksenKestoTyyppi.setYksikko(model.getSuunniteltuKestoTyyppi());
//        tyyppi.setPainotus(mapToMonikielinenTekstiTyyppi(model.getPainotus()));
        tyyppi.setKesto(koulutuksenKestoTyyppi);
//        tyyppi.setPohjakoulutusvaatimus(createKoodi(model.getPohjakoulutusvaatimus(), true, "pohjakoulutusvaatimus"));

        //TODO: create a different form model for every level of education: 
        //The datatypes on bottom must be list types as in future we need to have 
        //an option to select multiple languages etc. (lukio, AMK etc...)
        
        for(String opetusmuoto: model.getOpetusmuoto()) {
            tyyppi.getOpetusmuoto().add(createKoodi(opetusmuoto, true, "opetusmuoto"));
        }
        
        tyyppi.getOpetuskieli().add(createKoodi(model.getOpetuskieli(), true, "opetuskieli"));

        for(String koulutuslaji: model.getOpetusmuoto()) {
            tyyppi.getKoulutuslaji().add(createKoodi(koulutuslaji, true, "koulutuslaji"));
        }
        return tyyppi;
    }
    
    private void mapToLukioKoulutusLisatiedotTyyppi(KoulutusTyyppi koulutus, KoulutusLukioKuvailevatTiedotViewModel kuvailevatTiedot) {


        clear(koulutus.getSisalto());
        clear(koulutus.getKansainvalistyminen());
        clear(koulutus.getYhteistyoMuidenToimijoidenKanssa());


        for (String kieliUri : kuvailevatTiedot.getTekstikentat().keySet()) {

            LOG.debug("koulutusLisatiedotModel.getLisatiedot().keySet() '" + kieliUri + "', " + kuvailevatTiedot.getTekstikentat().keySet());


            KoulutusLisatietoModel lisatieto = kuvailevatTiedot.getLisatiedot(kieliUri);

            if (koulutus.getSisalto() == null) {
                koulutus.setSisalto(new MonikielinenTekstiTyyppi());
            }

            koulutus.getSisalto().getTeksti().add(convertToMonikielinenTekstiTyyppi(kieliUri, lisatieto.getSisalto()));

            if (koulutus.getKansainvalistyminen() == null) {
                koulutus.setKansainvalistyminen(new MonikielinenTekstiTyyppi());
            }

            koulutus.getKansainvalistyminen().getTeksti().add(convertToMonikielinenTekstiTyyppi(kieliUri, lisatieto.getKansainvalistyminen()));

            if (koulutus.getYhteistyoMuidenToimijoidenKanssa() == null) {
                koulutus.setYhteistyoMuidenToimijoidenKanssa(new MonikielinenTekstiTyyppi());
            }

            koulutus.getYhteistyoMuidenToimijoidenKanssa().getTeksti().add(convertToMonikielinenTekstiTyyppi(kieliUri, lisatieto.
                    getYhteistyoMuidenToimijoidenKanssa()));

        }
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

    //XXX not complete!! jani needs to fix this
    public LisaaKoulutusTyyppi createLisaaLukioKoulutusTyyppi(TarjontaModel tarjontaModel, final String organisaatioOid) throws ExceptionMessage {

        KoulutusLukioPerustiedotViewModel model = tarjontaModel.getKoulutusLukioPerustiedot();

        final OrganisaatioDTO organisaatio = searchOrganisatioByOid(organisaatioOid);

        LisaaKoulutusTyyppi lisaa = new LisaaKoulutusTyyppi();
        mapToLukioKoulutusTyyppi(lisaa, model, oidService.newOid(NodeClassCode.TEKN_5), organisaatio);

        //convert yhteyshenkilo model objects to yhteyshenkilo type objects.
        //addToYhteyshenkiloTyyppiList(model.getYhteyshenkilot(), lisaa.getYhteyshenkilo());

        //convert yhteyshenkilo model objects to yhteyshenkilo type objects.
        //addToYhteyshenkiloTyyppiList(model.getYhteyshenkilot(), paivita.getYhteyshenkiloTyyppi());
        if (model.getYhteyshenkilo().getYhtHenkKokoNimi() != null && !model.getYhteyshenkilo().getYhtHenkKokoNimi().isEmpty()) {
            lisaa.getYhteyshenkiloTyyppi().add(mapYhteyshenkiloToTyyppi(model.getYhteyshenkilo()));
        }

        //convert linkki model objects to linkki type objects.
        //addToWebLinkkiTyyppiList(model.getKoulutusLinkit(), lisaa.getLinkki());

        if (model.getOpsuLinkki() != null && !model.getOpsuLinkki().isEmpty()) {
            lisaa.getLinkki().add(mapOpetussuunnitelmaLinkkiToTyyppi(model.getOpsuLinkki()));
        }

        // Lisätiedot
        mapToLukioKoulutusLisatiedotTyyppi(lisaa, tarjontaModel.getKoulutusLukioKuvailevatTiedot());

        return lisaa;
    }

    private WebLinkkiTyyppi mapOpetussuunnitelmaLinkkiToTyyppi(String linkki) {
        WebLinkkiTyyppi webLink = new WebLinkkiTyyppi();
        webLink.setKieli(I18N.getLocale().getLanguage());
        webLink.setUri(checkWwwOsoite(linkki));
        webLink.setTyyppi(KoulutusLinkkiViewModel.LINKKI_TYYPIT[1]);
        return webLink;
    }

    private String checkWwwOsoite(String linkki) {
        return (!isValidUrl(linkki)) ? "http://" + linkki : linkki;
    }

    private boolean isValidUrl(String givenUrl) {
        return givenUrl == null
                || givenUrl.isEmpty()
                || givenUrl.startsWith("http://")
                || givenUrl.startsWith("https://")
                || givenUrl.startsWith("ftp://")
                || givenUrl.startsWith("file://");
    }

    private YhteyshenkiloTyyppi mapYhteyshenkiloToTyyppi(KoulutusToisenAsteenPerustiedotViewModel model) {
        YhteyshenkiloTyyppi yhteyshenkilo = new YhteyshenkiloTyyppi();
        String kokoNimi = model.getYhtHenkKokoNimi();
        String[] nimet = kokoNimi.split(" ");
        if (nimet.length > 1) {
            yhteyshenkilo.setEtunimet(kokoNimi.substring(0, kokoNimi.lastIndexOf(' ')));
            yhteyshenkilo.setSukunimi(nimet[nimet.length - 1]);
        } else {
            yhteyshenkilo.setEtunimet(kokoNimi);
        }
        yhteyshenkilo.setPuhelin(model.getYhtHenkPuhelin());
        yhteyshenkilo.setSahkoposti(model.getYhtHenkEmail());
        yhteyshenkilo.setTitteli(model.getYhtHenkTitteli());
        yhteyshenkilo.setHenkiloOid(model.getYhtHenkiloOid());
        return yhteyshenkilo;
    }
    
    private YhteyshenkiloTyyppi mapYhteyshenkiloToTyyppi(YhteyshenkiloModel model) {
        YhteyshenkiloTyyppi yhteyshenkilo = new YhteyshenkiloTyyppi();
        yhteyshenkilo.setEtunimet(model.getYhtHenkKokoNimi());
        yhteyshenkilo.setPuhelin(model.getYhtHenkPuhelin());
        yhteyshenkilo.setSahkoposti(model.getYhtHenkEmail());
        yhteyshenkilo.setTitteli(model.getYhtHenkTitteli());
        yhteyshenkilo.setHenkiloOid(model.getYhtHenkiloOid());
        return yhteyshenkilo;
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

    /**
     * Create KoulutusLukioKuvailevatTiedotViewModel
     * @return
     */
    public static KoulutusLukioKuvailevatTiedotViewModel createKoulutusLukioKuvailevatTiedotViewModel(final LueKoulutusVastausTyyppi input, final DocumentStatus status) {
        KoulutusLukioKuvailevatTiedotViewModel model = new KoulutusLukioKuvailevatTiedotViewModel(status);
        model.setKieliA(Lists.newArrayList(Iterables.transform(input.getA1A2Kieli(), fromKoodistoKoodiTyyppi)));
        model.setKieliB1(Lists.newArrayList(Iterables.transform(input.getB1Kieli(), fromKoodistoKoodiTyyppi)));
        model.setKieliB2(Lists.newArrayList(Iterables.transform(input.getB2Kieli(), fromKoodistoKoodiTyyppi)));
        model.setKieliB3(Lists.newArrayList(Iterables.transform(input.getB3Kieli(), fromKoodistoKoodiTyyppi)));
        model.setKieletMuu(Lists.newArrayList(Iterables.transform(input.getMuutKielet(), fromKoodistoKoodiTyyppi)));
        model.setDiplomit(Lists.newArrayList(Iterables.transform(input.getLukiodiplomit(), fromKoodistoKoodiTyyppi)));
        if (input.getSisalto() != null) {
            for (MonikielinenTekstiTyyppi.Teksti mkt : input.getSisalto().getTeksti()) {
                KoulutusLisatietoModel lisatieto = model.getLisatiedot(mkt.getKieliKoodi());
                lisatieto.setSisalto(mkt.getValue());
            }
        }

        if (input.getKansainvalistyminen() != null) {
            for (MonikielinenTekstiTyyppi.Teksti mkt : input.getKansainvalistyminen().getTeksti()) {
                KoulutusLisatietoModel lisatieto = model.getLisatiedot(mkt.getKieliKoodi());
                lisatieto.setKansainvalistyminen(mkt.getValue());
            }
        }

        if (input.getYhteistyoMuidenToimijoidenKanssa() != null) {
            for (MonikielinenTekstiTyyppi.Teksti mkt : input.getYhteistyoMuidenToimijoidenKanssa().getTeksti()) {
                KoulutusLisatietoModel lisatieto = model.getLisatiedot(mkt.getKieliKoodi());
                lisatieto.setYhteistyoMuidenToimijoidenKanssa(mkt.getValue());
            }
        }
        return model;
    }

    /**
     * Create KoulutusLukioKuvailevatTiedotViewModel
     * @return
     */
    public static KoulutusLukioKuvailevatTiedotViewModel addToKoucreateKoulutusLukioKuvailevatTiedotViewModel(final LueKoulutusVastausTyyppi input, final DocumentStatus status) {
        KoulutusLukioKuvailevatTiedotViewModel model = new KoulutusLukioKuvailevatTiedotViewModel(status);
        model.setKieliA(Lists.newArrayList(Iterables.transform(input.getA1A2Kieli(), fromKoodistoKoodiTyyppi)));
        model.setKieliB1(Lists.newArrayList(Iterables.transform(input.getB1Kieli(), fromKoodistoKoodiTyyppi)));
        model.setKieliB2(Lists.newArrayList(Iterables.transform(input.getB2Kieli(), fromKoodistoKoodiTyyppi)));
        model.setKieliB3(Lists.newArrayList(Iterables.transform(input.getB3Kieli(), fromKoodistoKoodiTyyppi)));
        model.setKieletMuu(Lists.newArrayList(Iterables.transform(input.getMuutKielet(), fromKoodistoKoodiTyyppi)));
        model.setDiplomit(Lists.newArrayList(Iterables.transform(input.getLukiodiplomit(), fromKoodistoKoodiTyyppi)));
        if (input.getSisalto() != null) {
            for (MonikielinenTekstiTyyppi.Teksti mkt : input.getSisalto().getTeksti()) {
                KoulutusLisatietoModel lisatieto = model.getLisatiedot(mkt.getKieliKoodi());
                lisatieto.setSisalto(mkt.getValue());
            }
        }

        if (input.getKansainvalistyminen() != null) {
            for (MonikielinenTekstiTyyppi.Teksti mkt : input.getKansainvalistyminen().getTeksti()) {
                KoulutusLisatietoModel lisatieto = model.getLisatiedot(mkt.getKieliKoodi());
                lisatieto.setKansainvalistyminen(mkt.getValue());
            }
        }

        if (input.getYhteistyoMuidenToimijoidenKanssa() != null) {
            for (MonikielinenTekstiTyyppi.Teksti mkt : input.getYhteistyoMuidenToimijoidenKanssa().getTeksti()) {
                KoulutusLisatietoModel lisatieto = model.getLisatiedot(mkt.getKieliKoodi());
                lisatieto.setYhteistyoMuidenToimijoidenKanssa(mkt.getValue());
            }
        }
        return model;
    }

    
    private void mapYhteyshenkiloToViewModel(KoulutusToisenAsteenPerustiedotViewModel model2Aste, LueKoulutusVastausTyyppi tyyppi) {
        if (tyyppi.getYhteyshenkilo().isEmpty()) {
            return;
        }

        YhteyshenkiloTyyppi yhtHenk = tyyppi.getYhteyshenkilo().get(0);
        model2Aste.setYhtHenkKokoNimi(yhtHenk.getEtunimet() + " " + yhtHenk.getSukunimi());
        model2Aste.setYhtHenkEmail(yhtHenk.getSahkoposti());
        model2Aste.setYhtHenkPuhelin(yhtHenk.getPuhelin());
        model2Aste.setYhtHenkTitteli(yhtHenk.getTitteli());
        model2Aste.setYhtHenkiloOid(yhtHenk.getHenkiloOid());
    }

    private OrganisaatioDTO searchOrganisatioByOid(final String organisaatioOid) {
        if (organisaatioOid == null) {
            throw new RuntimeException(INVALID_DATA + "organisation OID cannot be null.");
        }
        OrganisaatioDTO dto = this.organisaatioService.findByOid(organisaatioOid);

        if (dto == null || dto.getOid() == null) {
            throw new RuntimeException("No organisation found by OID : " + organisaatioOid);
        }

        return dto;
    }

    private void addToYhteyshenkiloTyyppiList(final Collection<KoulutusYhteyshenkiloViewModel> fromModel, List<YhteyshenkiloTyyppi> toTyyppi) throws
            ExceptionMessage {
        if (toTyyppi == null) {
            throw new RuntimeException(INVALID_DATA + "list of YhteyshenkiloTyyppi objects cannot be null.");
        }

        if (fromModel != null && !fromModel.isEmpty()) {
            for (KoulutusYhteyshenkiloViewModel yhteyshenkiloModel : fromModel) {
                if (yhteyshenkiloModel.getHenkiloOid() == null) {
                    //generate OID to new yhteyshenkilo.
                    //back-end not not accept null OIDs.
                    yhteyshenkiloModel.setHenkiloOid(oidService.newOid(NodeClassCode.TEKN_5));
                }

                toTyyppi.add(mapToYhteyshenkiloTyyppiDto(yhteyshenkiloModel));
            }
        }
    }

    /*
     *
     *
     * Static helper methods:
     *
     *
     */
    private static void addToWebLinkkiTyyppiList(final Collection<KoulutusLinkkiViewModel> model, List<WebLinkkiTyyppi> listTyyppi) throws ExceptionMessage {
        if (listTyyppi == null) {
            throw new RuntimeException(INVALID_DATA + "list of WebLinkkiTyyppi objects cannot be null.");
        }

        if (model != null && !model.isEmpty()) {
            for (KoulutusLinkkiViewModel linkkiModel : model) {
                listTyyppi.add(mapToWebLinkkiTyyppiDto(linkkiModel));
            }
        }
    }

    public static KoulutusLinkkiViewModel mapToKoulutusLinkkiViewModel(WebLinkkiTyyppi type) {
        KoulutusLinkkiViewModel koulutusLinkkiViewModel = new KoulutusLinkkiViewModel();
        koulutusLinkkiViewModel.setKieli(type.getKieli());
        koulutusLinkkiViewModel.setLinkkityyppi(type.getTyyppi());
        koulutusLinkkiViewModel.setUrl(type.getUri());

        return koulutusLinkkiViewModel;
    }

    public static void addToKoulutusLinkkiViewModel(List<WebLinkkiTyyppi> linkki, List<KoulutusLinkkiViewModel> listLinkkiModel) {
        if (listLinkkiModel == null) {
            throw new RuntimeException(INVALID_DATA + "list of KoulutusLinkkiViewModel objects cannot be null.");
        }

        if (linkki != null && !linkki.isEmpty()) {
            for (WebLinkkiTyyppi type : linkki) {
                listLinkkiModel.add(mapToKoulutusLinkkiViewModel(type));
            }
        }
    }

    public static YhteyshenkiloTyyppi mapToYhteyshenkiloTyyppiDto(final KoulutusYhteyshenkiloViewModel model) throws ExceptionMessage {
        YhteyshenkiloTyyppi yhteyshenkiloTyyppi = new YhteyshenkiloTyyppi();

        yhteyshenkiloTyyppi.setHenkiloOid(model.getHenkiloOid());
        yhteyshenkiloTyyppi.setEtunimet(model.getEtunimet());
        yhteyshenkiloTyyppi.setSukunimi(model.getSukunimi());
        yhteyshenkiloTyyppi.setSahkoposti(model.getEmail());
        yhteyshenkiloTyyppi.setTitteli(model.getTitteli());
        yhteyshenkiloTyyppi.setPuhelin(model.getPuhelin());

        if (model.getKielet() != null && !model.getKielet().isEmpty()) {
            for (String kieliUri : model.getKielet()) {
                yhteyshenkiloTyyppi.getKielet().add(kieliUri);
            }
        }

        return yhteyshenkiloTyyppi;
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

    public static WebLinkkiTyyppi mapToWebLinkkiTyyppiDto(final KoulutusLinkkiViewModel model) throws ExceptionMessage {
        WebLinkkiTyyppi web = new WebLinkkiTyyppi();
        web.setKieli(model.getKieli());
        web.setTyyppi(model.getLinkkityyppi());
        web.setUri(model.getUrl());

        return web;
    }

    public static KoulutusYhteyshenkiloViewModel mapToKoulutusYhteyshenkiloViewModel(final YhteyshenkiloTyyppi tyyppi) {
        KoulutusYhteyshenkiloViewModel yhteyshenkiloModel = new KoulutusYhteyshenkiloViewModel();
        yhteyshenkiloModel.setHenkiloOid(tyyppi.getHenkiloOid());
        yhteyshenkiloModel.setEmail(tyyppi.getSahkoposti());
        yhteyshenkiloModel.setEtunimet(tyyppi.getEtunimet());
        yhteyshenkiloModel.setPuhelin(tyyppi.getPuhelin());
        yhteyshenkiloModel.setSukunimi(tyyppi.getSukunimi());
        yhteyshenkiloModel.setTitteli(tyyppi.getTitteli());
        final List<String> kielet = tyyppi.getKielet();
        if (kielet != null && !kielet.isEmpty()) {
            for (String kieliUri : kielet) {
                yhteyshenkiloModel.getKielet().add(kieliUri);
            }
        }
        return yhteyshenkiloModel;
    }

    private static void addToKoulutusYhteyshenkiloViewModel(List<YhteyshenkiloTyyppi> yhteyshenkilo, Collection<KoulutusYhteyshenkiloViewModel> yhteistietoModel) {
        if (yhteyshenkilo != null && !yhteyshenkilo.isEmpty()) {
            for (YhteyshenkiloTyyppi type : yhteyshenkilo) {
                yhteistietoModel.add(mapToKoulutusYhteyshenkiloViewModel(type));
            }
        }
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

        koulutusKoodisto.listaaSisalto(model2Aste.getKoulutuskoodiModel(), model2Aste.getKoulutusohjelmaModel(), koulutusmoduuliTyyppi, locale);

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

    public KoulutusohjelmaModel mapToKoulutusohjelmaModel(final KoodistoKoodiTyyppi tyyppi, final Locale locale) {
        if (tyyppi != null && tyyppi.getUri() != null) {

            if (tyyppi.getUri() == null && tyyppi.getVersio() == null) {
                throw new RuntimeException(INVALID_DATA + "URI cannot be null.");
            }

            return koulutusKoodisto.listaaKoulutusohjelma(tyyppi, locale);
        }

        LOG.warn("Data conversion error - koulutusohjelma koodi URI not found.");

        return null;
    }

    public KoulutuskoodiModel mapToKoulutuskoodiModel(final KoodistoKoodiTyyppi koulutusKoodi, final Locale locale) {
        if (koulutusKoodi != null && koulutusKoodi.getUri() != null) {
            KoulutuskoodiModel model = koulutusKoodisto.listaaKoulutuskoodi(koulutusKoodi, locale);

            if (model == null) {
                /**
                 * Serious external system error has occured: Koodisto koodi
                 * search has returned a null data object. It's very likely that
                 * someone has changed Koodisto service koodi uris or the
                 * service is having other an internal problem.
                 *
                 * Let just show koodi uris in all UI data fields, as there is
                 * no way to get correct language data from the Koodisto
                 * service.
                 */
                model = new KoulutuskoodiModel();
                model.setNimi(koulutusKoodi.getUri());
                model.setKuvaus(koulutusKoodi.getUri());
                model.setKoodi(koulutusKoodi.getUri());
                final String[] koodiUriAndVersion = splitKoodiURIAllowNull(koulutusKoodi.getUri());

                model.setKoodistoUri(koodiUriAndVersion[0]);
                model.setKoodistoVersio(Integer.parseInt(koodiUriAndVersion[1]));
                model.getKielikaannos().add(new KielikaannosViewModel("FI", koulutusKoodi.getUri()));
                model.setKielikoodi("FI");

                LOG.error("Data conversion error - Koodisto service do not have a koodi with URI '{}' and version '{}'.", koodiUriAndVersion[0], koodiUriAndVersion[1]);
            }

            return model;
        }

        LOG.error("Data conversion error - koulutus Koodisto service koodi URI was null.");

        return null;
    }

    /**
     * Return KoodistoKoodiTyyppi search data object.
     *
     * @param model
     * @return
     */
    public static KoodistoKoodiTyyppi mapToValidKoodistoKoodiTyyppi(final boolean allowNull, final KoulutusKoodistoModel model) {
        if (model != null && model.getKoodistoUri() != null) {
            return mapToKoodistoKoodiTyyppi(model);
        } else if (allowNull) {
            //KoulutusohjelmaModel obejct can have null uri
            return null;
        }

        throw new RuntimeException("KoulutusasteModel or KoulutuskoodiModel cannot be null! Object " + model);
    }

    private static KoodistoKoodiTyyppi mapToKoodistoKoodiTyyppi(KoulutusKoodistoModel model) {
        KoodistoKoodiTyyppi koodit = createKoodiVersionUri(
                model.getKoodistoUri(),
                model.getKoodistoVersio(),
                model.getNimi());

        if (model instanceof MonikielinenTekstiModel) {
            //Some UI objects do not need multilanguage data.
            MonikielinenTekstiModel o = (MonikielinenTekstiModel) model;
            if (o.getKielikaannos() != null && !o.getKielikaannos().isEmpty()) {
                for (KielikaannosViewModel nimiModel : o.getKielikaannos()) {
                    KoodistoKoodiTyyppi.Nimi tyyppi = new KoodistoKoodiTyyppi.Nimi();
                    tyyppi.setKieli(nimiModel.getKielikoodi());
                    tyyppi.setValue(nimiModel.getNimi());
                    koodit.getNimi().add(tyyppi);
                }
            }
        }
        return koodit;
    }

    public static HaeKoulutusmoduulitKyselyTyyppi mapToHaeKoulutusmoduulitKyselyTyyppi(final KoulutusToisenAsteenPerustiedotViewModel model) {
        HaeKoulutusmoduulitKyselyTyyppi kysely = new HaeKoulutusmoduulitKyselyTyyppi();
        final KoulutuskoodiModel koulutuskoodi = model.getKoulutuskoodiModel();
        //Combine URI's with version number if you need to make DB search.

        kysely.setKoulutuskoodiUri(koulutuskoodi.getKoodistoUriVersio());

        final KoulutusohjelmaModel koulutusohjema = model.getKoulutusohjelmaModel();
        if (koulutusohjema != null && koulutusohjema.getKoodistoUri() != null) {
            kysely.setKoulutusohjelmakoodiUri(koulutusohjema.getKoodistoUriVersio());
            LOG.debug("Koulutusohjelma URI : '" + kysely.getKoulutusohjelmakoodiUri() + "'");

        }

        LOG.debug("Koulutuskoodi URI : '" + koulutuskoodi.getKoodistoUriVersio() + "'");

        return kysely;
    }

    public static String mapToVersionUri(String uri, int version) {
        if (uri != null) {
            return TarjontaUIHelper.createVersionUri(uri, version);
        }

        throw new RuntimeException(INVALID_DATA + "URI cannot be null.");
    }

    private static KoodistoKoodiTyyppi createKoodi(final String uri, final boolean noNullValues, final String errorInField) {
        if (noNullValues && uri == null) {
            throw new RuntimeException("URI cannot be null in field name " + errorInField);
        }

        return toKoodistoKoodiTyypi.apply(uri);
    }

    private static KoodistoKoodiTyyppi createKoodiVersionUri(final String uri, final int version, final String arvo) {
        final KoodistoKoodiTyyppi koodi = new KoodistoKoodiTyyppi();
        final String uriVersion = mapToVersionUri(uri, version);
        koodi.setUri(uriVersion);
        koodi.setArvo(arvo);

        return koodi;
    }

    private static String getUri(final KoodistoKoodiTyyppi type) {
        return type.getUri();
    }

    private static Set<String> convertOpetuskielet(final List<KoodistoKoodiTyyppi> opetuskieliKoodit) {
        Set<String> opetuskielet = new HashSet<String>();
        for (KoodistoKoodiTyyppi curKoodi : opetuskieliKoodit) {
            opetuskielet.add(curKoodi.getUri());
        }
        return opetuskielet;
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

    
    /**
     * Converter KoodistoKoodiTyyppi -> String
     */
    private static final Function<KoodistoKoodiTyyppi, String> fromKoodistoKoodiTyyppi = new Function<KoodistoKoodiTyyppi, String>() {
        public String apply(KoodistoKoodiTyyppi koodi) {
            return koodi.getUri();
        }
    };

    /**
     * Converter String -> KoodistoKoodiTyyppi
     */
    private static final Function<String, KoodistoKoodiTyyppi> toKoodistoKoodiTyypi = new Function<String, KoodistoKoodiTyyppi>() {
        public KoodistoKoodiTyyppi apply(String uri) {
            final KoodistoKoodiTyyppi tyyppi = new KoodistoKoodiTyyppi();
            tyyppi.setUri(uri);
            return tyyppi;
        }
    };

    private static Set<String> mapToKoodistoKoodis(List<KoodistoKoodiTyyppi> ammattinimikkeet) {
        return Sets.newHashSet(Iterables.transform(ammattinimikkeet,  fromKoodistoKoodiTyyppi));
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

    private void clear(MonikielinenTekstiTyyppi tekstis) {
        if (tekstis != null) {
            tekstis.getTeksti().clear();
        }
    }

    /**
     * Convert language uri and value to MonikielinenTekstiTyyppi.
     *
     * @param languageUri
     * @param teksti
     * @return
     */
    private MonikielinenTekstiTyyppi.Teksti convertToMonikielinenTekstiTyyppi(final String languageUri, final String teksti) {
        MonikielinenTekstiTyyppi.Teksti mktt = new MonikielinenTekstiTyyppi.Teksti();
        mktt.setValue(teksti);

        if (languageUri == null) {
            throw new RuntimeException("Language URI cannot be null.");
        }
        mktt.setKieliKoodi(languageUri);
        return mktt;
    }

    public static Map<String, StringBuilder> multilanguageKomotoName(final KoulutusToisenAsteenPerustiedotViewModel model) {
        Map<String, StringBuilder> type = new HashMap<String, StringBuilder>();

        if (model.getKoulutuskoodiModel() == null) {
            throw new RuntimeException("KoulutuskoodiModel object cannot be null");
        }

        for (KielikaannosViewModel kieliModel : model.getKoulutuskoodiModel().getKielikaannos()) {
            if (type.containsKey(kieliModel.getKielikoodi())) {
                type.get(kieliModel.getKielikoodi()).append(",").append(kieliModel.getNimi());
            } else {
                type.put(kieliModel.getKielikoodi(), new StringBuilder().append(kieliModel.getNimi()));
            }
        }

        if (model.getKoulutusohjelmaModel() != null && model.getKoulutusohjelmaModel().getKielikaannos() != null) {
            for (KielikaannosViewModel kieliModel : model.getKoulutusohjelmaModel().getKielikaannos()) {
                if (type.containsKey(kieliModel.getKielikoodi())) {
                    type.get(kieliModel.getKielikoodi()).append(",").append(kieliModel.getNimi());
                } else {
                    type.put(kieliModel.getKielikoodi(), new StringBuilder().append(kieliModel.getNimi()));
                }
            }
        }

        return type;

    }

    public static MonikielinenTekstiTyyppi mapToMonikielinenTekstiTyyppi(final Collection<KielikaannosViewModel> kielet) {
        MonikielinenTekstiTyyppi tyyppi = new MonikielinenTekstiTyyppi();

        for (KielikaannosViewModel nimi : kielet) {
            Teksti teksti = new MonikielinenTekstiTyyppi.Teksti();
            teksti.setKieliKoodi(nimi.getKielikoodi());
            teksti.setValue(nimi.getNimi());
            tyyppi.getTeksti().add(teksti);
        }

        return tyyppi;
    }

    public static List<KielikaannosViewModel> mapToKielikaannosViewModel(final MonikielinenTekstiTyyppi kielet) {
        List<KielikaannosViewModel> models = new ArrayList<KielikaannosViewModel>();

        if (kielet == null) {
            return models;
        }

        for (MonikielinenTekstiTyyppi.Teksti nimi : kielet.getTeksti()) {
            models.add(new KielikaannosViewModel(nimi.getKieliKoodi(), nimi.getValue()));
        }

        return models;
    }

    public static Set<KielikaannosViewModel> mapToKoodiModel(final MonikielinenTekstiTyyppi tyyppi) {
        Set<KielikaannosViewModel> model = new HashSet<KielikaannosViewModel>();

        for (Teksti teksti : tyyppi.getTeksti()) {
            model.add(new KielikaannosViewModel(teksti.getKieliKoodi(), teksti.getValue()));
        }

        return model;
    }

    public static MonikielinenTekstiModel mapToMonikielinenTekstiModel(final MonikielinenTekstiTyyppi tyyppi, final Locale locale) {
        MonikielinenTekstiModel m = new MonikielinenTekstiModel();

        if (tyyppi == null) {
            LOG.warn("MonikielinenTekstiTyyppi object was null, the missing data cannot be show on UI.");
            return m;
        }

        if (locale != null) {
            final Teksti teksti = TarjontaUIHelper.searchTekstiTyyppiByLanguage(tyyppi.getTeksti(), locale);

            if (teksti != null) {
                m.setKielikoodi(teksti.getKieliKoodi());
                m.setNimi(teksti.getValue());

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Language code : " + teksti.getKieliKoodi());
                    LOG.debug("Text value : " + (teksti != null ? teksti.getValue() : teksti));
                }
            } else {
                LOG.debug("No text data found for locale " + locale.getLanguage());
            }
        }

        if (m.getNimi() == null || m.getNimi().isEmpty()) {
            //FI default fallback
            final Locale locale1 = new Locale("FI");
            final Teksti teksti = TarjontaUIHelper.searchTekstiTyyppiByLanguage(tyyppi.getTeksti(), locale1);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Language code fallback : " + locale1.getLanguage());
                LOG.debug("Text value : " + (teksti != null ? teksti.getValue() : teksti));
            }

            if (teksti != null) {
                m.setKielikoodi(teksti.getKieliKoodi());
                m.setNimi(teksti.getValue());
            } else {
                LOG.error("An invalid data error -´MonikielinenTekstiModel object was missing Finnish language data.");
            }
        }

        m.setKielikaannos(mapToKoodiModel(tyyppi));
        return m;
    }

    public static Map<Entry, KoulutusmoduuliKoosteTyyppi> fullKomoCacheMap(Collection<KoulutusmoduuliKoosteTyyppi> komos) {
        Map<Entry, KoulutusmoduuliKoosteTyyppi> hashMap = new HashMap<Entry, KoulutusmoduuliKoosteTyyppi>();

        for (KoulutusmoduuliKoosteTyyppi komo : komos) {
            Entry e = new AbstractMap.SimpleEntry<String, String>(komo.getKoulutuskoodiUri(), komo.getKoulutusohjelmakoodiUri());
            hashMap.put(e, komo);
        }

        return hashMap;
    }

    public static Map<String, List<KoulutusmoduuliKoosteTyyppi>> komoCacheMapByKoulutuskoodi(Collection<KoulutusmoduuliKoosteTyyppi> komos) {
        Map<String, List<KoulutusmoduuliKoosteTyyppi>> hashMap = new HashMap<String, List<KoulutusmoduuliKoosteTyyppi>>();

        for (KoulutusmoduuliKoosteTyyppi komo : komos) {
            final String uri = komo.getKoulutuskoodiUri();

            if (hashMap.containsKey(uri)) {
                hashMap.get(uri).add(komo);
            } else {
                List<KoulutusmoduuliKoosteTyyppi> l = new ArrayList<KoulutusmoduuliKoosteTyyppi>();
                l.add(komo);
                hashMap.put(uri, l);
            }

        }
        return hashMap;

    }
}