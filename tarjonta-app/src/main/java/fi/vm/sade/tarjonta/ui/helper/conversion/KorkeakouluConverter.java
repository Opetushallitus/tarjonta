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
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusLisatietoModel;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutuksenKestoTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.WebLinkkiTyyppi;
import fi.vm.sade.tarjonta.ui.enums.SaveButtonState;
import static fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConveter.INVALID_DATA;
import static fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConveter.createKoodi;
import static fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConveter.fromKoodistoKoodiTyyppi;
import static fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConveter.mapOpetussuunnitelmaLinkkiToTyyppi;
import static fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConveter.mapToValidKoodistoKoodiTyyppi;
import static fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConveter.mapYhteyshenkiloToTyyppi;
import static fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConveter.toKoodistoKoodiTyypi;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusohjelmaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KorkeakouluKuvailevatTiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KorkeakouluPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.org.OrganisationOidNamePair;

import java.util.*;

/**
 *
 * @author Jani Wilén
 */
@Component
public class KorkeakouluConverter extends KoulutusConveter {

    private static final Logger LOG = LoggerFactory.getLogger(KorkeakouluConverter.class);
    @Autowired(required = true)
    private OIDService oidService;

    public KorkeakouluConverter() {
    }

    public LisaaKoulutusTyyppi createLisaaLukioKoulutusTyyppi(TarjontaModel tarjontaModel, OrganisationOidNamePair selectedOrganisation, final SaveButtonState tila) throws ExceptionMessage {
        final String organisationOid = tarjontaModel.getTarjoajaModel().getSelectedOrganisationOid();
        final KorkeakouluPerustiedotViewModel perustiedotModel = tarjontaModel.getKorkeakouluPerustiedot();
        final KorkeakouluKuvailevatTiedotViewModel kuvailevatTiedotModel = tarjontaModel.getKorkeakouluKuvailevatTiedot();
        final OrganisaatioDTO organisaatio = searchOrganisationByOid(organisationOid, selectedOrganisation);

        LisaaKoulutusTyyppi lisaa = new LisaaKoulutusTyyppi();
        lisaa.setTila(tila.toTarjontaTila(perustiedotModel.getTila()));
        convertToKorkeakouluKoulutusTyyppi(lisaa, perustiedotModel, oidService.newOid(NodeClassCode.TEKN_5), organisaatio);
        convertToKorkeakouluLisatiedotTyyppi(lisaa, kuvailevatTiedotModel);

        return lisaa;
    }

    /**
     * Full data copy from UI model to tyyppi.
     *
     * @param model
     * @return
     * @throws ExceptionMessage
     */
    public PaivitaKoulutusTyyppi createPaivitaLukioKoulutusTyyppi(final TarjontaModel tarjontaModel, final String komotoOid, final SaveButtonState tila) throws ExceptionMessage {
        Preconditions.checkNotNull(komotoOid, INVALID_DATA + "KOMOTO OID cannot be null.");

        KorkeakouluPerustiedotViewModel perustiedotModel = tarjontaModel.getKorkeakouluPerustiedot();
        final OrganisaatioDTO dto = searchOrganisationByOid(tarjontaModel.getTarjoajaModel().getSelectedOrganisationOid(), tarjontaModel.getTarjoajaModel().getSelectedOrganisation());

        PaivitaKoulutusTyyppi paivita = new PaivitaKoulutusTyyppi();
        convertToKorkeakouluKoulutusTyyppi(paivita, perustiedotModel, komotoOid, dto);
        convertToKorkeakouluLisatiedotTyyppi(paivita, tarjontaModel.getKorkeakouluKuvailevatTiedot());
        paivita.setTila(tila.toTarjontaTila(perustiedotModel.getTila()));

        return paivita;
    }

    public void loadLueKoulutusVastausTyyppiToModel(final TarjontaModel tarjontaModel, final LueKoulutusVastausTyyppi koulutus, final Locale locale) {
        //set tarjoaja data to UI model
        tarjontaModel.getTarjoajaModel().setSelectedOrganisation(searchOrganisationByOid(koulutus.getTarjoaja()));

        KorkeakouluPerustiedotViewModel perustiedot = createToKoulutusLukioPerustiedotViewModel(koulutus, locale);
        perustiedot.setViimeisinPaivittajaOid(koulutus.getViimeisinPaivittajaOid());
        if (koulutus.getViimeisinPaivitysPvm() != null) {
            perustiedot.setViimeisinPaivitysPvm(koulutus.getViimeisinPaivitysPvm().toGregorianCalendar().getTime());
        }
        tarjontaModel.setKorkeakouluPerustiedot(perustiedot);
        KorkeakouluKuvailevatTiedotViewModel kuvailevatTiedot = createKorkeakouluKuvailevatTiedotViewModel(koulutus);
        tarjontaModel.setKorkeakouluKuvailevatTiedot(kuvailevatTiedot);

        //clear and set the loaded lukiolinja to combobox field data list
        perustiedot.getTutkintoohjelmas().clear();
        perustiedot.getTutkintoohjelmas().add(perustiedot.getTutkintoohjelma());
    }

    private void convertToKorkeakouluLisatiedotTyyppi(KoulutusTyyppi target, KorkeakouluKuvailevatTiedotViewModel source) {
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
    public static KorkeakouluKuvailevatTiedotViewModel createKorkeakouluKuvailevatTiedotViewModel(final LueKoulutusVastausTyyppi input) {
        KorkeakouluKuvailevatTiedotViewModel model = new KorkeakouluKuvailevatTiedotViewModel();
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

    public static KoulutusTyyppi convertToKorkeakouluKoulutusTyyppi(KoulutusTyyppi tyyppi, final KorkeakouluPerustiedotViewModel model, final String komotoOid, OrganisaatioDTO organisation) {
        Preconditions.checkNotNull(tyyppi, INVALID_DATA + "KoulutusTyyppi object cannot be null.");
        Preconditions.checkNotNull(model, INVALID_DATA + "KoulutusLukioPerustiedotViewModel object cannot be null.");
        Preconditions.checkNotNull(komotoOid, INVALID_DATA + "KOMOTO OID cannot be null.");
        Preconditions.checkNotNull(organisation, INVALID_DATA + "Organisatio DTO cannot be null.");

        tyyppi.setTarjoaja(organisation.getOid());
        tyyppi.setOid(komotoOid);
        tyyppi.setKoulutustyyppi(KoulutusasteTyyppi.LUKIOKOULUTUS);
        tyyppi.setKoulutusKoodi(mapToValidKoodistoKoodiTyyppi(false, model.getKoulutuskoodiModel()));

        //URI data example : "lukiolinja/xxxx#1"
        tyyppi.setLukiolinjaKoodi(mapToValidKoodistoKoodiTyyppi(false, model.getTutkintoohjelma()));
        tyyppi.setKoulutusaste(mapToValidKoodistoKoodiTyyppi(false, model.getKoulutusaste()));
        tyyppi.setPohjakoulutusvaatimus(mapToValidKoodistoKoodiTyyppi(true, model.getPohjakoulutusvaatimus()));

        tyyppi.setKoulutuksenAlkamisPaiva(model.getKoulutuksenAlkamisPvm());
        KoulutuksenKestoTyyppi koulutuksenKestoTyyppi = new KoulutuksenKestoTyyppi();
        koulutuksenKestoTyyppi.setArvo(model.getSuunniteltuKesto());
        koulutuksenKestoTyyppi.setYksikko(model.getSuunniteltuKestoTyyppi());
        tyyppi.setKesto(koulutuksenKestoTyyppi);

        //TODO: create a different form model for every level of education: 
        //The datatypes on bottom must be list types as in future we need to have 
        //an option to select multiple languages etc. (lukio, AMK etc...)

//        for (String opetusmuoto : model.getOpetusmuoto()) {
//            tyyppi.getOpetusmuoto().add(createKoodi(opetusmuoto, true, "opetusmuoto"));
//        }
//
//        tyyppi.getOpetuskieli().add(createKoodi(model.getOpetuskieli(), true, "opetuskieli"));

        if (model.getKoulutuslaji() != null && model.getKoulutuslaji().getKoodistoUriVersio() != null) {
            tyyppi.getKoulutuslaji().add(createKoodi(model.getKoulutuslaji().getKoodistoUriVersio(), true, "koulutuslaji"));
        }

        if (model.getYhteyshenkilo().getYhtHenkKokoNimi() != null && !model.getYhteyshenkilo().getYhtHenkKokoNimi().isEmpty()) {
            tyyppi.getYhteyshenkiloTyyppi().add(mapYhteyshenkiloToTyyppi(model.getYhteyshenkilo()));
        }

        if (model.getOpsuLinkki() != null && !model.getOpsuLinkki().isEmpty()) {
            tyyppi.getLinkki().add(mapOpetussuunnitelmaLinkkiToTyyppi(model.getOpsuLinkki()));
        }
        tyyppi.setViimeisinPaivittajaOid(model.getViimeisinPaivittajaOid());
        if (model.getViimeisinPaivitysPvm() != null) {
            tyyppi.setViimeisinPaivitysPvm(model.getViimeisinPaivitysPvm());
        } else {
            tyyppi.setViimeisinPaivitysPvm(new Date());
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

    private KorkeakouluPerustiedotViewModel createToKoulutusLukioPerustiedotViewModel(LueKoulutusVastausTyyppi koulutus, Locale locale) {
        Preconditions.checkNotNull(koulutus, INVALID_DATA + "LueKoulutusVastausTyyppi object cannot be null.");

        KorkeakouluPerustiedotViewModel perustiedot = new KorkeakouluPerustiedotViewModel();
        perustiedot.setTila(koulutus.getTila());
        perustiedot.setKomotoOid(koulutus.getOid());
        perustiedot.setKoulutusmoduuliOid(koulutus.getKoulutusmoduuli().getOid());

        /*
         * Combobox fields;
         */
        perustiedot.setKoulutuskoodiModel(mapToKoulutuskoodiModel(koulutus.getKoulutusKoodi(), locale));
        perustiedot.setTutkintoohjelma(mapToTutkintoohjelmaModel(koulutus.getKoulutusohjelmaKoodi(), locale));

        Preconditions.checkNotNull(perustiedot.getKoulutuskoodiModel(), INVALID_DATA + "kolutuskoodi model cannot be null.");
        Preconditions.checkNotNull(perustiedot.getTutkintoohjelma(), INVALID_DATA + "lukiolinja model cannot be null.");
        /*
         * Other UI fields
         */
        List<WebLinkkiTyyppi> wwwLinks = koulutus.getLinkki();
        if (wwwLinks != null && !wwwLinks.isEmpty()) {
            if (wwwLinks.size() > 1) {
                for (WebLinkkiTyyppi link : wwwLinks) {
                    LOG.warn("Too many links - lukiokoulutus can use only one. Link : {}", link);
                }
            }
            perustiedot.setOpsuLinkki(wwwLinks.get(0).getUri());
        }

        perustiedot.setKoulutuksenAlkamisPvm(
                koulutus.getKoulutuksenAlkamisPaiva() != null ? koulutus.getKoulutuksenAlkamisPaiva().toGregorianCalendar().getTime() : null);

        if (koulutus.getKesto() != null) {
            perustiedot.setSuunniteltuKesto(koulutus.getKesto().getArvo());
            perustiedot.setSuunniteltuKestoTyyppi(koulutus.getKesto().getYksikko());
        }

//        if (koulutus.getOpetusmuoto() != null && !koulutus.getOpetusmuoto().isEmpty()) {
//            perustiedot.setOpetusmuoto(convertListToSet(koulutus.getOpetusmuoto()));
//        }
//
//        if (koulutus.getOpetuskieli() != null && !koulutus.getOpetuskieli().isEmpty()) {
//            perustiedot.setOpetuskieli(getUri(koulutus.getOpetuskieli().get(0)));
//        }

        /*
         * contact person data conversion
         */
        mapYhteyshenkiloToViewModel(perustiedot.getYhteyshenkilo(), koulutus);

        /*
         * convert koodisto uris to UI models
         */
        final KoulutusmoduuliKoosteTyyppi koulutusmoduuliTyyppi = koulutus.getKoulutusmoduuli();
        koulutusKoodisto.listaa2asteSisalto(perustiedot.getKoulutuskoodiModel(), perustiedot.getTutkintoohjelma(), koulutusmoduuliTyyppi, locale);
        copySelectedKoodiDataToModel(perustiedot);
        /*
         * Data fields used on UI only as extra information:
         */

        //6-numero koodi arvo 
        perustiedot.setKoulutuskoodi(perustiedot.getKoulutuskoodiModel().getKoodi());


        return perustiedot;
    }

    public KoulutusohjelmaModel mapToTutkintoohjelmaModel(final KoodistoKoodiTyyppi tyyppi, final Locale locale) {
        if (tyyppi != null && tyyppi.getUri() != null) {
            return koulutusKoodisto.listaaKoulutusohjelma(tyyppi, locale);
        }

        LOG.warn("Data conversion error - lukiolinja koodi URI not found.");

        return null;
    }

    public static void copySelectedKoodiDataToModel(KorkeakouluPerustiedotViewModel model) {
        Preconditions.checkNotNull(model, "KorkeakouluPerustiedotViewModel cannot be null.");
        final KoulutuskoodiModel koulutuskoodi = model.getKoulutuskoodiModel();

        if (koulutuskoodi != null) {
            model.setKoulutuskoodi(koulutuskoodi.getKoodi()); //value
            model.setOpintoala(koulutuskoodi.getOpintoala());
            model.setKoulutusaste(koulutuskoodi.getKoulutusaste());
            model.setKoulutusala(koulutuskoodi.getKoulutusala());
            model.setOpintojenLaajuusyksikko(koulutuskoodi.getOpintojenLaajuusyksikko());
            model.setOpintojenLaajuus(koulutuskoodi.getOpintojenLaajuus());
            model.setTutkintonimike(koulutuskoodi.getTutkintonimike());
            model.setKoulutuksenRakenne(koulutuskoodi.getKoulutuksenRakenne());
            model.setTavoitteet(koulutuskoodi.getTavoitteet());
            model.setJatkoopintomahdollisuudet(koulutuskoodi.getJatkoopintomahdollisuudet());
        }
    }
}
