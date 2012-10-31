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
import fi.vm.sade.tarjonta.ui.model.KoulutusYhteyshenkiloViewModel;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.oid.service.types.NodeClassCode;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.koodisto.KoodiHakuTyyppi;
import fi.vm.sade.tarjonta.service.types.koodisto.KoulutuskoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoulutuksenKestoTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.WebLinkkiTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.model.KoulutusLinkkiViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusohjelmaModel;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jani Wilén
 */
@Component
public class KoulutusViewModelToDTOConverter {

    @Autowired(required = true)
    private OIDService oidService;
    @Autowired(required = true)
    private KoulutusKoodistoConverter koulutusKoodisto;

    public KoulutusViewModelToDTOConverter() {
    }

    /**
     * Full data conversion of model to tyyppi.
     *
     * @param model
     * @return
     * @throws ExceptionMessage
     */
    public LisaaKoulutusTyyppi createLisaaKoulutusTyyppi(KoulutusToisenAsteenPerustiedotViewModel model) throws ExceptionMessage {
        LisaaKoulutusTyyppi lisaaKoulutusTyyppi = mapToLisaaKoulutusTyyppi(model, oidService.newOid(NodeClassCode.TEKN_5));

        //convert yhteyshenkilo model objects to yhteyshenkilo type objects.
        addToYhteyshenkiloTyyppiList(model.getYhteyshenkilot(), lisaaKoulutusTyyppi.getYhteyshenkilo());

        //convert linkki model objects to linkki type objects.
        addToWebLinkkiTyyppiList(model.getKoulutusLinkit(), lisaaKoulutusTyyppi.getLinkki());
        return lisaaKoulutusTyyppi;
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
    public KoulutusToisenAsteenPerustiedotViewModel createKoulutusPerustiedotViewModel(LueKoulutusVastausTyyppi tyyppi, DocumentStatus status) throws ExceptionMessage {
        KoulutusToisenAsteenPerustiedotViewModel model2Aste = mapToKoulutusToisenAsteenPerustiedotViewModel(tyyppi, status);
        addToKoulutusYhteyshenkiloViewModel(tyyppi.getYhteyshenkilo(), model2Aste.getYhteyshenkilot());
        addToKoulutusLinkkiViewModel(tyyppi.getLinkki(), model2Aste.getKoulutusLinkit());

        return model2Aste;
    }

    private void addToYhteyshenkiloTyyppiList(final Collection<KoulutusYhteyshenkiloViewModel> model, List<YhteyshenkiloTyyppi> listTyyppi) throws ExceptionMessage {
        if (listTyyppi == null) {
            throw new RuntimeException("Application error - List of YhteyshenkiloTyyppi objects cannot be null.");
        }

        if (model != null && !model.isEmpty()) {
            for (KoulutusYhteyshenkiloViewModel yhteyshenkiloModel : model) {
                listTyyppi.add(mapToYhteyshenkiloTyyppiDto(yhteyshenkiloModel, oidService.newOid(NodeClassCode.TEKN_5)));
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
            throw new RuntimeException("Application error - List of WebLinkkiTyyppi objects cannot be null.");
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
            throw new RuntimeException("Application error - List of KoulutusLinkkiViewModel objects cannot be null.");
        }

        if (linkki != null && !linkki.isEmpty()) {
            for (WebLinkkiTyyppi type : linkki) {
                listLinkkiModel.add(mapToKoulutusLinkkiViewModel(type));
            }
        }
    }

    public static YhteyshenkiloTyyppi mapToYhteyshenkiloTyyppiDto(final KoulutusYhteyshenkiloViewModel model, final String oid) throws ExceptionMessage {
        YhteyshenkiloTyyppi yhteyshenkiloTyyppi = new YhteyshenkiloTyyppi();
        yhteyshenkiloTyyppi.setHenkiloOid(oid);
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

    public static LisaaKoulutusTyyppi mapToLisaaKoulutusTyyppi(final KoulutusToisenAsteenPerustiedotViewModel model, final String oid) {
        if (model == null) {
            throw new RuntimeException("Application error - LisaaKoulutusTyyppi object cannot be null.");
        }

        LisaaKoulutusTyyppi tyyppi = new LisaaKoulutusTyyppi();
        // this.getDocumentStatus();  //TODO: status
        tyyppi.setOid(oid);
        //TODO: fix the test data
        final KoulutuskoodiTyyppi koulutuskoodi = model.getKoulutuskoodiTyyppi();
        if (koulutuskoodi != null) {
            KoulutuskoodiTyyppi koodi = model.getKoulutuskoodiTyyppi();
            tyyppi.setKoulutusKoodi(createKoodi(model.getKoulutuskoodiTyyppi().getKoodistoUri(), koodi.getKoulutuskoodi()));
        }

        final KoulutusohjelmaModel koulutusohjelma = model.getKoulutusohjema();
        //URI data example : "koulutusohjelma/1603"
        if (koulutusohjelma != null) {
            tyyppi.setKoulutusohjelmaKoodi(createKoodi(koulutusohjelma.getKoodiUri(), koulutusohjelma.getCode()));
        }
        tyyppi.setKoulutuksenAlkamisPaiva(model.getKoulutuksenAlkamisPvm());
        KoulutuksenKestoTyyppi koulutuksenKestoTyyppi = new KoulutuksenKestoTyyppi();
        koulutuksenKestoTyyppi.setArvo(model.getSuunniteltuKesto());
        koulutuksenKestoTyyppi.setYksikko(model.getSuunniteltuKestoTyyppi());
        tyyppi.setKesto(koulutuksenKestoTyyppi);

        for (String opetusmuoto : model.getOpetusmuoto()) {
            tyyppi.getOpetusmuoto().add(createKoodi(opetusmuoto));
        }

        for (String opetuskielet : model.getOpetuskielet()) {
            tyyppi.getOpetuskieli().add(createKoodi(opetuskielet));
        }

        //TODO: change API... minor priority 
        tyyppi.getKoulutuslaji().add(createKoodi(model.getKoulutuslaji()));

        return tyyppi;
    }

    public static WebLinkkiTyyppi mapToWebLinkkiTyyppiDto(final KoulutusLinkkiViewModel model) throws ExceptionMessage {
        WebLinkkiTyyppi web = new WebLinkkiTyyppi();
        web.setKieli(model.getKieli());
        web.setTyyppi(model.getLinkkityyppi());
        web.setUri(model.getUrl());

        return web;
    }

    public static KoulutusYhteyshenkiloViewModel mapToKoulutusYhteyshenkiloViewModel(YhteyshenkiloTyyppi tyyppi) {
        KoulutusYhteyshenkiloViewModel yhteyshenkiloModel = new KoulutusYhteyshenkiloViewModel();
        yhteyshenkiloModel.setEmail(tyyppi.getSahkoposti());
        yhteyshenkiloModel.setEtunimet(tyyppi.getEtunimet());
        yhteyshenkiloModel.setPuhelin(tyyppi.getPuhelin());
        yhteyshenkiloModel.setSukunimi(tyyppi.getSukunimi());
        yhteyshenkiloModel.setTitteli(tyyppi.getTitteli());

        if (tyyppi.getKielet() != null && !tyyppi.getKielet().isEmpty()) {
            for (String kieliUri : tyyppi.getKielet()) {
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

    private KoulutusToisenAsteenPerustiedotViewModel mapToKoulutusToisenAsteenPerustiedotViewModel(LueKoulutusVastausTyyppi koulutus, DocumentStatus status) {
        KoulutusToisenAsteenPerustiedotViewModel model2Aste = new KoulutusToisenAsteenPerustiedotViewModel(status);
        model2Aste.setOid(koulutus.getOid());
       
        //TODO: fix this
        final KoodistoKoodiTyyppi koulutusKoodi = koulutus.getKoulutusKoodi();
        if (koulutusKoodi != null && koulutusKoodi.getUri() != null) {
            KoodiHakuTyyppi koodiHakuTyyppi = new KoodiHakuTyyppi();
            koodiHakuTyyppi.setKieliKoodi("FI");
            koodiHakuTyyppi.setKoodistoUri(koulutusKoodi.getUri());
            //koodiHakuTyyppi.setKoodistoVersio(koulutusKoodi.getVersio());
            model2Aste.setKoulutuskoodiTyyppi(koulutusKoodisto.listaaKoulutuskoodi(koodiHakuTyyppi));
        }
        
        final KoodistoKoodiTyyppi koulutusohjelmaKoodi = koulutus.getKoulutusohjelmaKoodi();
        if (koulutusohjelmaKoodi != null) {
            final String koodiUri = koulutusohjelmaKoodi.getUri();
            final int koodiVersio = koulutusohjelmaKoodi.getVersio();
            final String arvo = koulutusohjelmaKoodi.getArvo();
            model2Aste.setKoulutusohjema(new KoulutusohjelmaModel(koodiUri, koodiVersio, arvo, null));
        }

        model2Aste.setKoulutuksenAlkamisPvm(koulutus.getKoulutuksenAlkamisPaiva() != null ? koulutus.getKoulutuksenAlkamisPaiva().toGregorianCalendar().getTime() : null);
        model2Aste.setOpetuskielet(convertOpetuskielet(koulutus.getOpetuskieli()));

        if (koulutus.getKesto() != null) {
            model2Aste.setSuunniteltuKesto(koulutus.getKesto().getArvo());
            model2Aste.setSuunniteltuKestoTyyppi(koulutus.getKesto().getYksikko());
        }

        for (KoodistoKoodiTyyppi typeOpetusmuoto : koulutus.getOpetusmuoto()) {
            model2Aste.getOpetusmuoto().add(getUri(typeOpetusmuoto));
        }

        for (KoodistoKoodiTyyppi typeOpetuskielet : koulutus.getOpetuskieli()) {
            model2Aste.getOpetuskielet().add(getUri(typeOpetuskielet));
        }

        //UI allow only one value 
        if (koulutus.getKoulutuslaji() != null && !koulutus.getKoulutuslaji().isEmpty()) {
            model2Aste.setKoulutuslaji(getUri(koulutus.getKoulutuslaji().get(0)));
        }

        return model2Aste;
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

    private static KoodistoKoodiTyyppi createKoodi(final String uri, final String name) {
        final KoodistoKoodiTyyppi koodi = new KoodistoKoodiTyyppi();
        koodi.setUri(uri);
        koodi.setArvo(name);
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
}
