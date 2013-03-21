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
import fi.vm.sade.tarjonta.service.types.KoulutuksenKestoTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import static fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConveter.fromKoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioPerustiedotViewModel;

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

    /**
     * Create KoulutusLukioKuvailevatTiedotViewModel
     *
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
        Preconditions.checkNotNull(model, INVALID_DATA + "KoulutusLukioPerustiedotViewModel object cannot be null.");
        Preconditions.checkNotNull(komotoOid, INVALID_DATA + "KOMOTO OID cannot be null.");
        Preconditions.checkNotNull(organisatio, INVALID_DATA + "Organisatio DTO cannot be null.");


        tyyppi.setTarjoaja(organisatio.getOid());
        tyyppi.setOid(komotoOid);
        tyyppi.setKoulutusaste(mapToValidKoodistoKoodiTyyppi(false, model.getKoulutuskoodiModel().getKoulutusaste()));
        tyyppi.setKoulutusKoodi(mapToValidKoodistoKoodiTyyppi(false, model.getKoulutuskoodiModel()));

        //URI data example : "koulutusohjelma/1603#1"
        tyyppi.setLukiolinjaKoodi(mapToValidKoodistoKoodiTyyppi(true, model.getLukiolinja()));
        tyyppi.setKoulutuksenAlkamisPaiva(model.getKoulutuksenAlkamisPvm());
        KoulutuksenKestoTyyppi koulutuksenKestoTyyppi = new KoulutuksenKestoTyyppi();
        koulutuksenKestoTyyppi.setArvo(model.getSuunniteltuKesto());
        koulutuksenKestoTyyppi.setYksikko(model.getSuunniteltuKestoTyyppi());
        tyyppi.setKesto(koulutuksenKestoTyyppi);
//        tyyppi.setPohjakoulutusvaatimus(createKoodi(model.getPohjakoulutusvaatimus(), true, "pohjakoulutusvaatimus"));

        //TODO: create a different form model for every level of education: 
        //The datatypes on bottom must be list types as in future we need to have 
        //an option to select multiple languages etc. (lukio, AMK etc...)

        for (String opetusmuoto : model.getOpetusmuoto()) {
            tyyppi.getOpetusmuoto().add(createKoodi(opetusmuoto, true, "opetusmuoto"));
        }

        tyyppi.getOpetuskieli().add(createKoodi(model.getOpetuskieli(), true, "opetuskieli"));

        for (String koulutuslaji : model.getOpetusmuoto()) {
            tyyppi.getKoulutuslaji().add(createKoodi(koulutuslaji, true, "koulutuslaji"));
        }
        return tyyppi;
    }
}