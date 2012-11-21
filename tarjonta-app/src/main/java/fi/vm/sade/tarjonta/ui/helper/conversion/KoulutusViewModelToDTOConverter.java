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
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi.Teksti;
import fi.vm.sade.tarjonta.service.types.WebLinkkiTyyppi;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.enums.KoulutusasteType;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusKoodistoModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusLinkkiViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusLisatiedotModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusLisatietoModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusohjelmaModel;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.NimiModel;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
public class KoulutusViewModelToDTOConverter {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaPresenter.class);
    private static final String INVALID_DATA = "Invalid data exception - ";
    @Autowired(required = true)
    private OIDService oidService;
    @Autowired(required = true)
    private KoulutusKoodistoConverter koulutusKoodisto;
    @Autowired(required = true)
    private OrganisaatioService organisaatioService;

    public KoulutusViewModelToDTOConverter() {
    }

    /**
     * Full data conversion of model to tyyppi.
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
        addToYhteyshenkiloTyyppiList(model.getYhteyshenkilot(), paivita.getYhteyshenkiloTyyppi());

        //convert linkki model objects to linkki type objects.
        addToWebLinkkiTyyppiList(model.getKoulutusLinkit(), paivita.getLinkki());

        // Lisätiedot
        mapToKoulutusLisatiedotModel(paivita, tarjontaModel.getKoulutusLisatiedotModel());

        return paivita;
    }

    public LisaaKoulutusTyyppi createLisaaKoulutusTyyppi(TarjontaModel tarjontaModel, final String organisaatioOid) throws ExceptionMessage {

        KoulutusToisenAsteenPerustiedotViewModel model = tarjontaModel.getKoulutusPerustiedotModel();

        final OrganisaatioDTO organisaatio = searchOrganisatioByOid(organisaatioOid);

        LisaaKoulutusTyyppi lisaa = new LisaaKoulutusTyyppi();
        mapToKoulutusTyyppi(lisaa, model, oidService.newOid(NodeClassCode.TEKN_5), organisaatio);

        //convert yhteyshenkilo model objects to yhteyshenkilo type objects.
        addToYhteyshenkiloTyyppiList(model.getYhteyshenkilot(), lisaa.getYhteyshenkilo());

        //convert linkki model objects to linkki type objects.
        addToWebLinkkiTyyppiList(model.getKoulutusLinkit(), lisaa.getLinkki());

        // Lisätiedot
        mapToKoulutusLisatiedotModel(lisaa, tarjontaModel.getKoulutusLisatiedotModel());

        return lisaa;
    }

    /**
     *
     * Full data conversion of tyyppi to model.
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
        addToKoulutusYhteyshenkiloViewModel(tyyppi.getYhteyshenkilo(), model2Aste.getYhteyshenkilot());
        addToKoulutusLinkkiViewModel(tyyppi.getLinkki(), model2Aste.getKoulutusLinkit());

        return model2Aste;
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

        tyyppi.setKesto(koulutuksenKestoTyyppi);

        //TODO: change List type to String... minor priority
        tyyppi.getOpetusmuoto().add(createKoodi(model.getOpetusmuoto()));


        for (String opetuskielet : model.getOpetuskielet()) {
            tyyppi.getOpetuskieli().add(createKoodi(opetuskielet));
        }

        //TODO: change List type to String... minor priority
        tyyppi.getKoulutuslaji().add(createKoodi(model.getKoulutuslaji()));
        //TODO: change List type to String...  minor priority
        tyyppi.getPohjakoulutusvaatimus().add(createKoodi(model.getPohjakoulutusvaatimus()));

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
        model2Aste.setOrganisaatioName(OrganisaatioDisplayHelper.getClosest(I18N.getLocale(), organisatio));

        /* Select KOMO by koulutusaste, koulutuskoodi and koulutusohjelma */
        model2Aste.setKoulutuskoodiModel(mapToKoulutuskoodiModel(koulutus.getKoulutusKoodi(), locale));
        model2Aste.setKoulutusohjelmaModel(mapToKoulutusohjelmaModel(koulutus.getKoulutusohjelmaKoodi(), locale));

        model2Aste.setKoulutuksenAlkamisPvm(
                koulutus.getKoulutuksenAlkamisPaiva() != null ? koulutus.getKoulutuksenAlkamisPaiva().toGregorianCalendar().getTime() : null);
        model2Aste.setOpetuskielet(convertOpetuskielet(koulutus.getOpetuskieli()));

        if (koulutus.getKesto() != null) {
            model2Aste.setSuunniteltuKesto(koulutus.getKesto().getArvo());
            model2Aste.setSuunniteltuKestoTyyppi(koulutus.getKesto().getYksikko());
        }

        for (KoodistoKoodiTyyppi typeOpetusmuoto : koulutus.getOpetusmuoto()) {
            model2Aste.setOpetusmuoto(getUri(typeOpetusmuoto));
        }

        for (KoodistoKoodiTyyppi typeOpetuskielet : koulutus.getOpetuskieli()) {
            model2Aste.getOpetuskielet().add(getUri(typeOpetuskielet));
        }

        //UI allow only one value
        if (koulutus.getKoulutuslaji() != null && !koulutus.getKoulutuslaji().isEmpty()) {
            model2Aste.setKoulutuslaji(getUri(koulutus.getKoulutuslaji().get(0)));
        }

        if (koulutus.getPohjakoulutusvaatimus() != null && !koulutus.getPohjakoulutusvaatimus().isEmpty()) {
            model2Aste.setPohjakoulutusvaatimus(getUri(koulutus.getPohjakoulutusvaatimus().get(0)));
        }
        /*
         * KOMO
         */
        final KoulutusmoduuliKoosteTyyppi koulutusmoduuliTyyppi = koulutus.getKoulutusmoduuli();

        if (koulutusmoduuliTyyppi != null) {
            model2Aste.setTutkinto(koulutusKoodisto.listaaKoodi(koulutusmoduuliTyyppi.getTutkintoOhjelmaUri(), locale)); //Autoalan perustutkinto
            model2Aste.setTutkintonimike(koulutusKoodisto.listaaKoodi(koulutusmoduuliTyyppi.getTutkintonimikeUri(), locale)); //Automaalari
            model2Aste.setOpintojenLaajuusyksikko(koulutusKoodisto.listaaKoodi(koulutusmoduuliTyyppi.getLaajuusyksikkoUri(), locale)); //Opintoviikot
            model2Aste.setOpintojenLaajuus(koulutusKoodisto.listaaKoodi(koulutusmoduuliTyyppi.getLaajuusyksikkoUri(), locale)); //120 ov

//            model2Aste.setKoulutuksenRakenne(koulutusKoodisto.listaaKoodi(koulutusmoduuliTyyppi.getKoul, locale));
//            model2Aste.setTavoitteet(koulutusKoodisto.listaaKoodi(koulutusmoduuliTyyppi.getTavoiteetUri(), locale));
//            model2Aste.setJakoopintomahdollisuudet(koulutusKoodisto.listaaKoodi(koulutusmoduuliTyyppi.getJatkoopintomahdollisuudetUri(), locale));
        }

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
        return result;
    }

    public KoulutusohjelmaModel mapToKoulutusohjelmaModel(final KoodistoKoodiTyyppi tyyppi, final Locale locale) {
        if (tyyppi != null && tyyppi.getUri() != null) {

            if (tyyppi.getUri() == null && tyyppi.getVersio() == null) {
                throw new RuntimeException(INVALID_DATA + "URI cannot be null.");
            }

            return koulutusKoodisto.listaaKoulutusohjelma(tyyppi, locale);
        }

        return null;
    }

    public KoulutuskoodiModel mapToKoulutuskoodiModel(final KoodistoKoodiTyyppi koulutusKoodi, final Locale locale) {
        if (koulutusKoodi != null && koulutusKoodi.getUri() != null) {
            return koulutusKoodisto.listaaKoulutuskoodi(koulutusKoodi, locale);
        }

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

        if (model.getKielet() != null && !model.getKielet().isEmpty()) {
            for (NimiModel nimiModel : model.getKielet()) {
                KoodistoKoodiTyyppi.Nimi nimiTyyppi = new KoodistoKoodiTyyppi.Nimi();
                nimiTyyppi.setKieli(nimiModel.getType().name());
                nimiTyyppi.setValue(nimiModel.getNimi());
                koodit.getNimi().add(nimiTyyppi);
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

    /**
     * Helper method that wraps uri string into KoodistoKoodiTyypi. No other
     * attribute populated.
     *
     * @param uri
     * @return
     */
    private static KoodistoKoodiTyyppi createKoodi(final String uri) {
        final KoodistoKoodiTyyppi koodi = new KoodistoKoodiTyyppi();
        koodi.setUri(uri);
        return koodi;
    }

    private static KoodistoKoodiTyyppi createKoodi(final String uri, final String arvo) {
        final KoodistoKoodiTyyppi koodi = new KoodistoKoodiTyyppi();
        koodi.setUri(uri);
        koodi.setArvo(arvo);
        return koodi;
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

        if (koulutusaste.equals(KoulutusasteType.TOINEN_ASTE_AMMATILLINEN_KOULUTUS) && koulutusohjelmaKoodi == null && koulutusohjelmaKoodi.getUri() == null) {
            throw new RuntimeException("Persist failed - koulutusohjelma URI is required!");
        } else if (koulutusaste.equals(KoulutusasteType.TOINEN_ASTE_LUKIO)) {
            //Lukio tutkinto do not have koulutusohjema data.
            lisaa.setKoulutusohjelmaKoodi(new KoodistoKoodiTyyppi());
            //just to make sure that Lukio do not send 'koulutuslaji' data to back-end.
            lisaa.getKoulutuslaji().clear();
            LOG.debug("Koulutuskoodi URI : '" + lisaa.getKoulutusKoodi().getUri() + "'");
        } else {
            LOG.debug("Koulutuskoodi URI : '" + lisaa.getKoulutusKoodi().getUri() + "', koulutusohjelma URI : '" + koulutusohjelmaKoodi.getUri() + "' ");
        }
    }

    private static Set<String> mapToKoodistoKoodis(List<KoodistoKoodiTyyppi> ammattinimikkeet) {
        Set<String> result = new HashSet<String>();

        if (ammattinimikkeet != null) {
            for (KoodistoKoodiTyyppi koodistoKoodiTyyppi : ammattinimikkeet) {
                result.add(koodistoKoodiTyyppi.getUri());
            }
        }

        return result;
    }

    private void mapToKoulutusLisatiedotModel(KoulutusTyyppi koulutus, KoulutusLisatiedotModel koulutusLisatiedotModel) {

        koulutus.getAmmattinimikkeet().clear();
        for (String uri : koulutusLisatiedotModel.getAmmattinimikkeet()) {
            koulutus.getAmmattinimikkeet().add(createKoodi(uri));
        }

        clear(koulutus.getKuvailevatTiedot());
        clear(koulutus.getSisalto());
        clear(koulutus.getSijoittuminenTyoelamaan());
        clear(koulutus.getKansainvalistyminen());
        clear(koulutus.getYhteistyoMuidenToimijoidenKanssa());

        for (String kieliUri : koulutusLisatiedotModel.getLisatiedot().keySet()) {
            KoulutusLisatietoModel lisatieto = koulutusLisatiedotModel.getLisatiedot(kieliUri);

            koulutus.getKuvailevatTiedot().getTeksti().add(convertToMonikielinenTekstiTyyppi(kieliUri, lisatieto.getKuvailevatTiedot()));
            koulutus.getSisalto().getTeksti().add(convertToMonikielinenTekstiTyyppi(kieliUri, lisatieto.getSisalto()));
            koulutus.getSijoittuminenTyoelamaan().getTeksti().add(convertToMonikielinenTekstiTyyppi(kieliUri, lisatieto.getSijoittuminenTyoelamaan()));
            koulutus.getKansainvalistyminen().getTeksti().add(convertToMonikielinenTekstiTyyppi(kieliUri, lisatieto.getKansainvalistyminen()));
            koulutus.getYhteistyoMuidenToimijoidenKanssa().getTeksti().add(convertToMonikielinenTekstiTyyppi(kieliUri, lisatieto.
                    getYhteistyoMuidenToimijoidenKanssa()));
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
    private MonikielinenTekstiTyyppi.Teksti convertToMonikielinenTekstiTyyppi(String languageUri, String teksti) {
        MonikielinenTekstiTyyppi.Teksti mktt = new MonikielinenTekstiTyyppi.Teksti();
        mktt.setValue(teksti);
        mktt.setKieliKoodi(languageUri);
        return mktt;
    }

    public static Map<KieliType, StringBuilder> multilanguageKomotoName(KoulutusToisenAsteenPerustiedotViewModel model) {
        Map<KieliType, StringBuilder> type = new EnumMap<KieliType, StringBuilder>(KieliType.class);

        if (model.getKoulutuskoodiModel() == null) {
            throw new RuntimeException("KOulutuskoodiModel object cannot be null");
        }


        for (NimiModel nimiModel : model.getKoulutuskoodiModel().getKielet()) {
            if (type.containsKey(nimiModel.getType())) {
                type.get(nimiModel.getType()).append(",").append(nimiModel.getNimi());
            } else {
                type.put(nimiModel.getType(), new StringBuilder().append(nimiModel.getNimi()));
            }
        }

        if (model.getKoulutusohjelmaModel() != null && model.getKoulutusohjelmaModel().getKielet() != null) {
            for (NimiModel nimiModel : model.getKoulutusohjelmaModel().getKielet()) {
                if (type.containsKey(nimiModel.getType())) {
                    type.get(nimiModel.getType()).append(",").append(nimiModel.getNimi());
                } else {
                    type.put(nimiModel.getType(), new StringBuilder().append(nimiModel.getNimi()));
                }
            }
        }

        return type;

    }

    public static MonikielinenTekstiTyyppi mapToMonikielinenTekstiTyyppi(final Set<KielikaannosViewModel> kielet) {
        MonikielinenTekstiTyyppi tyyppi = new MonikielinenTekstiTyyppi();

        for (KielikaannosViewModel nimi : kielet) {
            Teksti teksti = new MonikielinenTekstiTyyppi.Teksti();
            teksti.setKieliKoodi(nimi.getKielikoodi());
            teksti.setValue(nimi.getNimi());
        }

        return tyyppi;
    }

    public static Set<KielikaannosViewModel> mapToKoodiModel(final MonikielinenTekstiTyyppi tyyppi) {
        Set<KielikaannosViewModel> model = new HashSet<KielikaannosViewModel>();

        for (Teksti teksti : tyyppi.getTeksti()) {
            model.add(new KielikaannosViewModel(teksti.getKieliKoodi(), teksti.getValue()));
        }

        return model;
    }
}
