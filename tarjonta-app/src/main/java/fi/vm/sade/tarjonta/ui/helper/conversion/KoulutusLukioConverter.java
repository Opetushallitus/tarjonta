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

import com.google.common.base.Preconditions;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.oid.service.types.NodeClassCode;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusLisatietoModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioKuvailevatTiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import fi.vm.sade.organisaatio.helper.OrganisaatioDisplayHelper;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutuksenKestoTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import static fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConveter.INVALID_DATA;
import static fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConveter.convertListToSet;
import static fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConveter.createKoodi;
import static fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConveter.fromKoodistoKoodiTyyppi;
import static fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConveter.getUri;
import static fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConveter.mapOpetussuunnitelmaLinkkiToTyyppi;
import static fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConveter.mapToValidKoodistoKoodiTyyppi;
import static fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConveter.mapYhteyshenkiloToTyyppi;
import static fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConveter.toKoodistoKoodiTyypi;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.LukiolinjaModel;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Jani Wilén
 */
@Component
public class KoulutusLukioConverter extends KoulutusConveter {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusLukioConverter.class);
    @Autowired(required = true)
    private OIDService oidService;

    public KoulutusLukioConverter() {
    }

    public LisaaKoulutusTyyppi createLisaaLukioKoulutusTyyppi(TarjontaModel tarjontaModel, final String organisaatioOid) throws ExceptionMessage {
        final KoulutusLukioPerustiedotViewModel perustiedotModel = tarjontaModel.getKoulutusLukioPerustiedot();
        final KoulutusLukioKuvailevatTiedotViewModel kuvailevatTiedotModel = tarjontaModel.getKoulutusLukioKuvailevatTiedot();
        final OrganisaatioDTO organisaatio = searchOrganisatioByOid(organisaatioOid);

        LisaaKoulutusTyyppi lisaa = new LisaaKoulutusTyyppi();
        convertToLukioKoulutusTyyppi(lisaa, perustiedotModel, oidService.newOid(NodeClassCode.TEKN_5), organisaatio);
        convertToLukioKoulutusLisatiedotTyyppi(lisaa, kuvailevatTiedotModel);

        return lisaa;
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

        KoulutusLukioPerustiedotViewModel perustiedotModel = tarjontaModel.getKoulutusLukioPerustiedot();
        final OrganisaatioDTO organisaatio = searchOrganisatioByOid(perustiedotModel.getOrganisaatioOid());

        PaivitaKoulutusTyyppi paivita = new PaivitaKoulutusTyyppi();
        convertToLukioKoulutusTyyppi(paivita, perustiedotModel, komotoOid, organisaatio);
        convertToLukioKoulutusLisatiedotTyyppi(paivita, tarjontaModel.getKoulutusLukioKuvailevatTiedot());

        return paivita;
    }

    public void loadLueKoulutusVastausTyyppiToModel(final TarjontaModel tarjontaModel, final LueKoulutusVastausTyyppi koulutus, final Locale locale) {
        final OrganisaatioDTO organisaatio = searchOrganisatioByOid(koulutus.getTarjoaja());
        KoulutusLukioPerustiedotViewModel perustiedot = createToKoulutusLukioPerustiedotViewModel(koulutus, DocumentStatus.LOADED, organisaatio, locale);
        tarjontaModel.setKoulutusLukioPerustiedot(perustiedot);
        KoulutusLukioKuvailevatTiedotViewModel kuvailevatTiedot = createKoulutusLukioKuvailevatTiedotViewModel(koulutus, DocumentStatus.LOADED);
        tarjontaModel.setKoulutusLukioKuvailevatTiedot(kuvailevatTiedot);
    }

    private void convertToLukioKoulutusLisatiedotTyyppi(KoulutusTyyppi target, KoulutusLukioKuvailevatTiedotViewModel source) {
        target.getA1A2Kieli().addAll(Lists.newArrayList(Iterables.transform(source.getKieliA(), toKoodistoKoodiTyypi)));
        target.getB1Kieli().addAll(Lists.newArrayList(Iterables.transform(source.getKieliB1(), toKoodistoKoodiTyypi)));
        target.getB2Kieli().addAll(Lists.newArrayList(Iterables.transform(source.getKieliB2(), toKoodistoKoodiTyypi)));
        target.getB3Kieli().addAll(Lists.newArrayList(Iterables.transform(source.getKieliB3(), toKoodistoKoodiTyypi)));
        target.getMuutKielet().addAll(Lists.newArrayList(Iterables.transform(source.getKieletMuu(), toKoodistoKoodiTyypi)));
        target.getLukiodiplomit().addAll(Lists.newArrayList(Iterables.transform(source.getDiplomit(), toKoodistoKoodiTyypi)));

        clear(target.getSisalto());
        clear(target.getKansainvalistyminen());
        clear(target.getYhteistyoMuidenToimijoidenKanssa());

        for (String kieliUri : source.getTekstikentat().keySet()) {
            LOG.debug("koulutusLisatiedotModel.getLisatiedot().keySet() '" + kieliUri + "', " + source.getTekstikentat().keySet());

            KoulutusLisatietoModel lisatieto = source.getLisatiedot(kieliUri);

            if (target.getSisalto() == null) {
                target.setSisalto(new MonikielinenTekstiTyyppi());
            }

            target.getSisalto().getTeksti().add(convertToMonikielinenTekstiTyyppi(kieliUri, lisatieto.getSisalto()));

            if (target.getKansainvalistyminen() == null) {
                target.setKansainvalistyminen(new MonikielinenTekstiTyyppi());
            }

            target.getKansainvalistyminen().getTeksti().add(convertToMonikielinenTekstiTyyppi(kieliUri, lisatieto.getKansainvalistyminen()));

            if (target.getYhteistyoMuidenToimijoidenKanssa() == null) {
                target.setYhteistyoMuidenToimijoidenKanssa(new MonikielinenTekstiTyyppi());
            }

            target.getYhteistyoMuidenToimijoidenKanssa().getTeksti().add(convertToMonikielinenTekstiTyyppi(kieliUri, lisatieto.
                    getYhteistyoMuidenToimijoidenKanssa()));

        }
    }

    /**
     * Create KoulutusLukioKuvailevatTiedotViewModel
     *
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

    public static KoulutusTyyppi convertToLukioKoulutusTyyppi(KoulutusTyyppi tyyppi, final KoulutusLukioPerustiedotViewModel model, final String komotoOid, OrganisaatioDTO organisatio) {
        Preconditions.checkNotNull(tyyppi, INVALID_DATA + "KoulutusTyyppi object cannot be null.");
        Preconditions.checkNotNull(model, INVALID_DATA + "KoulutusLukioPerustiedotViewModel object cannot be null.");
        Preconditions.checkNotNull(komotoOid, INVALID_DATA + "KOMOTO OID cannot be null.");
        Preconditions.checkNotNull(organisatio, INVALID_DATA + "Organisatio DTO cannot be null.");

        tyyppi.setTarjoaja(organisatio.getOid());
        tyyppi.setOid(komotoOid);
        tyyppi.setKoulutustyyppi(KoulutusasteTyyppi.LUKIOKOULUTUS);
        tyyppi.setKoulutusKoodi(mapToValidKoodistoKoodiTyyppi(false, model.getKoulutuskoodiModel()));

        //URI data example : "lukiolinja/xxxx#1"
        tyyppi.setLukiolinjaKoodi(mapToValidKoodistoKoodiTyyppi(false, model.getLukiolinja()));
        tyyppi.setKoulutusaste(mapToValidKoodistoKoodiTyyppi(false, model.getKoulutusaste()));
        tyyppi.setPohjakoulutusvaatimus(mapToValidKoodistoKoodiTyyppi(false, model.getPohjakoulutusvaatimus()));

        tyyppi.setKoulutuksenAlkamisPaiva(model.getKoulutuksenAlkamisPvm());
        KoulutuksenKestoTyyppi koulutuksenKestoTyyppi = new KoulutuksenKestoTyyppi();
        koulutuksenKestoTyyppi.setArvo(model.getSuunniteltuKesto());
        koulutuksenKestoTyyppi.setYksikko(model.getSuunniteltuKestoTyyppi());
        tyyppi.setKesto(koulutuksenKestoTyyppi);

        //TODO: create a different form model for every level of education: 
        //The datatypes on bottom must be list types as in future we need to have 
        //an option to select multiple languages etc. (lukio, AMK etc...)

        for (String opetusmuoto : model.getOpetusmuoto()) {
            tyyppi.getOpetusmuoto().add(createKoodi(opetusmuoto, true, "opetusmuoto"));
        }

        tyyppi.getOpetuskieli().add(createKoodi(model.getOpetuskieli(), true, "opetuskieli"));

        if (model.getKoulutuslaji() != null && model.getKoulutuslaji().getKoodistoUriVersio() != null) {
            tyyppi.getKoulutuslaji().add(createKoodi(model.getKoulutuslaji().getKoodistoUriVersio(), true, "opetuskieli"));
        }

        if (model.getYhteyshenkilo().getYhtHenkKokoNimi() != null && !model.getYhteyshenkilo().getYhtHenkKokoNimi().isEmpty()) {
            tyyppi.getYhteyshenkiloTyyppi().add(mapYhteyshenkiloToTyyppi(model.getYhteyshenkilo()));
        }

        if (model.getOpsuLinkki() != null && !model.getOpsuLinkki().isEmpty()) {
            tyyppi.getLinkki().add(mapOpetussuunnitelmaLinkkiToTyyppi(model.getOpsuLinkki()));
        }

        return tyyppi;
    }

    public static Map<Map.Entry, KoulutusmoduuliKoosteTyyppi> fullLukioKomoCacheMap(Collection<KoulutusmoduuliKoosteTyyppi> komos) {
        Map<Map.Entry, KoulutusmoduuliKoosteTyyppi> hashMap = new HashMap<Map.Entry, KoulutusmoduuliKoosteTyyppi>();

        for (KoulutusmoduuliKoosteTyyppi komo : komos) {
            Map.Entry e = new AbstractMap.SimpleEntry<String, String>(komo.getKoulutuskoodiUri(), komo.getLukiolinjakoodiUri());
            hashMap.put(e, komo);
        }

        return hashMap;
    }

    private KoulutusLukioPerustiedotViewModel createToKoulutusLukioPerustiedotViewModel(LueKoulutusVastausTyyppi koulutus, DocumentStatus status,
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

        KoulutusLukioPerustiedotViewModel perustiedot = new KoulutusLukioPerustiedotViewModel(status);
        perustiedot.setTila(koulutus.getTila());
        perustiedot.setKomotoOid(koulutus.getOid());
        perustiedot.setKoulutusmoduuliOid( koulutus.getKoulutusmoduuli().getOid());
        perustiedot.setOrganisaatioOid(organisatio.getOid());
        perustiedot.setOrganisaatioName(OrganisaatioDisplayHelper.getClosest(locale, organisatio));

        /* Select KOMO by koulutusaste, koulutuskoodi and koulutusohjelma */
        perustiedot.setKoulutuskoodiModel(mapToKoulutuskoodiModel(koulutus.getKoulutusKoodi(), locale));
        perustiedot.setLukiolinja(mapToLukiolinjaModel(koulutus.getLukiolinjaKoodi(), locale));

        perustiedot.setKoulutuksenAlkamisPvm(
                koulutus.getKoulutuksenAlkamisPaiva() != null ? koulutus.getKoulutuksenAlkamisPaiva().toGregorianCalendar().getTime() : null);

        if (koulutus.getKesto() != null) {
            perustiedot.setSuunniteltuKesto(koulutus.getKesto().getArvo());
            perustiedot.setSuunniteltuKestoTyyppi(koulutus.getKesto().getYksikko());
        }

        if (koulutus.getOpetusmuoto() != null && !koulutus.getOpetusmuoto().isEmpty()) {
            perustiedot.setOpetusmuoto(convertListToSet(koulutus.getOpetusmuoto()));
        }

        if (koulutus.getOpetuskieli() != null && !koulutus.getOpetuskieli().isEmpty()) {
            perustiedot.setOpetuskieli(getUri(koulutus.getOpetuskieli().get(0)));
        }
        
        mapYhteyshenkiloToViewModel(perustiedot.getYhteyshenkilo(), koulutus);

//        if (koulutus.getKoulutuslaji() != null && !koulutus.getKoulutuslaji().isEmpty()) {
//            Set<String> convertListToSet = convertListToSet(koulutus.getKoulutuslaji());
//    
//            model2Aste.setKoulutuslaji());
//        }
        final KoulutusmoduuliKoosteTyyppi koulutusmoduuliTyyppi = koulutus.getKoulutusmoduuli();
        koulutusKoodisto.listaaLukioSisalto(perustiedot.getKoulutuskoodiModel(), perustiedot.getLukiolinja(), koulutusmoduuliTyyppi, locale);

        return perustiedot;
    }

    public LukiolinjaModel mapToLukiolinjaModel(final KoodistoKoodiTyyppi tyyppi, final Locale locale) {
        if (tyyppi != null && tyyppi.getUri() != null) {
            return koulutusKoodisto.listaaLukiolinja(tyyppi, locale);
        }

        LOG.warn("Data conversion error - lukiolinja koodi URI not found.");

        return null;
    }
}