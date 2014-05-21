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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.tarjonta.service.types.HenkiloTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutuksenKestoTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.NimettyMonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.ui.enums.SaveButtonState;
import fi.vm.sade.tarjonta.ui.helper.OidCreationException;
import fi.vm.sade.tarjonta.ui.helper.OidHelper;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KorkeakouluKuvailevatTiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KorkeakouluLisatietoModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KorkeakouluPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KoulutuskoodiRowModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.TutkintoohjelmaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.ValitseKoulutusModel;
import fi.vm.sade.tarjonta.ui.model.org.OrganisationOidNamePair;

/**
 *
 * @author Jani Wilén
 */
@Component
public class KorkeakouluConverter extends KoulutusConveter {

    private static final Logger LOG = LoggerFactory.getLogger(KorkeakouluConverter.class);
    @Autowired(required = true)
    private OidHelper oidHelper;

    public KorkeakouluConverter() {
    }

    public LisaaKoulutusTyyppi createLisaaKoulutusTyyppi(TarjontaModel tarjontaModel, KoulutusasteTyyppi koulutusasteTyyppi, OrganisationOidNamePair selectedOrganisation, final SaveButtonState tila) throws OidCreationException {
        final String organisationOid = tarjontaModel.getTarjoajaModel().getSelectedOrganisationOid();
        final KorkeakouluPerustiedotViewModel perustiedotModel = tarjontaModel.getKorkeakouluPerustiedot();
        final KorkeakouluKuvailevatTiedotViewModel kuvailevatTiedotModel = tarjontaModel.getKorkeakouluKuvailevatTiedot();
        final OrganisaatioPerustieto organisaatio = searchOrganisationByOid(organisationOid, selectedOrganisation);

        LisaaKoulutusTyyppi lisaa = new LisaaKoulutusTyyppi();
        lisaa.setTila(tila.toTarjontaTila(perustiedotModel.getTila()));
        convertToKorkeakouluKoulutusTyyppi(lisaa, koulutusasteTyyppi, perustiedotModel, tarjontaModel.getValitseKoulutusModel(), oidHelper.getOid(TarjontaOidType.KOMOTO), organisaatio);
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
    public PaivitaKoulutusTyyppi createPaivitaKoulutusTyyppi(final TarjontaModel tarjontaModel, final String komotoOid, final KoulutusasteTyyppi koulutusaste, final SaveButtonState tila) throws OidCreationException {
        Preconditions.checkNotNull(komotoOid, INVALID_DATA + "KOMOTO OID cannot be null.");

        KorkeakouluPerustiedotViewModel perustiedotModel = tarjontaModel.getKorkeakouluPerustiedot();
        Preconditions.checkNotNull(perustiedotModel.getVersion(), INVALID_DATA + "Version ID for optimistic locking control cannot be null.");
        OrganisaatioPerustieto orgDto = searchOrganisationByOid(tarjontaModel.getTarjoajaModel().getSelectedOrganisationOid(), tarjontaModel.getTarjoajaModel().getSelectedOrganisation());

        PaivitaKoulutusTyyppi paivita = new PaivitaKoulutusTyyppi();
        convertToKorkeakouluKoulutusTyyppi(paivita, koulutusaste, perustiedotModel, tarjontaModel.getValitseKoulutusModel(), komotoOid, orgDto);

        convertToKorkeakouluLisatiedotTyyppi(paivita, tarjontaModel.getKorkeakouluKuvailevatTiedot());
        paivita.setTila(tila.toTarjontaTila(perustiedotModel.getTila()));

        return paivita;
    }

    public void loadLueKoulutusVastausTyyppiToModel(final TarjontaModel tarjontaModel, final LueKoulutusVastausTyyppi koulutus, final Locale locale) {
        //set tarjoaja data to UI model
        tarjontaModel.getTarjoajaModel().setSelectedOrganisation(searchOrganisationByOid(koulutus.getTarjoaja()));

        KorkeakouluPerustiedotViewModel perustiedot = createKorkeakouluPerustiedotViewModel(koulutus, locale);
        perustiedot.setViimeisinPaivittajaOid(koulutus.getViimeisinPaivittajaOid());
        if (koulutus.getViimeisinPaivitysPvm() != null) {
            perustiedot.setViimeisinPaivitysPvm(koulutus.getViimeisinPaivitysPvm());
        }
        tarjontaModel.setKorkeakouluPerustiedot(perustiedot);
        KorkeakouluKuvailevatTiedotViewModel kuvailevatTiedot = createKorkeakouluKuvailevatTiedotViewModel(koulutus);
        tarjontaModel.setKorkeakouluKuvailevatTiedot(kuvailevatTiedot);
    }

    private void convertToKorkeakouluLisatiedotTyyppi(KoulutusTyyppi target, KorkeakouluKuvailevatTiedotViewModel source) {
        // target.getLukiodiplomit().addAll(Lists.newArrayList(Iterables.transform(source.getDiplomit(), toKoodistoKoodiTyypi)));

    	ConversionUtils.clearTeksti(target.getTekstit(), KomotoTeksti.SISALTO);
    	ConversionUtils.clearTeksti(target.getTekstit(), KomotoTeksti.KANSAINVALISTYMINEN);
    	ConversionUtils.clearTeksti(target.getTekstit(), KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA);

        for (String kieliUri : source.getTekstikentat().keySet()) {
            LOG.debug("koulutusLisatiedotModel.getLisatiedot().keySet() '" + kieliUri + "', " + source.getTekstikentat().keySet());

            KorkeakouluLisatietoModel lisatieto = source.getLisatiedot(kieliUri);

            ConversionUtils.setTeksti(target.getTekstit(), KomotoTeksti.SISALTO, kieliUri, lisatieto.getKoulutuksenSisalto());
            ConversionUtils.setTeksti(target.getTekstit(), KomotoTeksti.KANSAINVALISTYMINEN, kieliUri, lisatieto.getKansainvalistyminen());
            ConversionUtils.setTeksti(target.getTekstit(), KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA, kieliUri, lisatieto.getYhteistyoMuidenToimijoidenKanssa());

            //TODO: MISSING conversions!!!?

        }
    }

    /**
     * Create KoulutusLukioKuvailevatTiedotViewModel
     *
     * @return
     */
    public static KorkeakouluKuvailevatTiedotViewModel createKorkeakouluKuvailevatTiedotViewModel(final LueKoulutusVastausTyyppi input) {
        KorkeakouluKuvailevatTiedotViewModel model = new KorkeakouluKuvailevatTiedotViewModel();

        NimettyMonikielinenTekstiTyyppi sis = ConversionUtils.getTeksti(input.getTekstit(), KomotoTeksti.SISALTO);
        if (sis != null) {
            for (MonikielinenTekstiTyyppi.Teksti mkt : sis.getTeksti()) {
                KorkeakouluLisatietoModel lisatiedot = model.getLisatiedot(mkt.getKieliKoodi());
                lisatiedot.setKoulutuksenSisalto(mkt.getValue());
            }
        }

        NimettyMonikielinenTekstiTyyppi kvl = ConversionUtils.getTeksti(input.getTekstit(), KomotoTeksti.SISALTO);
        if (kvl != null) {
            for (MonikielinenTekstiTyyppi.Teksti mkt : kvl.getTeksti()) {
                KorkeakouluLisatietoModel lisatieto = model.getLisatiedot(mkt.getKieliKoodi());
                lisatieto.setKansainvalistyminen(mkt.getValue());
            }
        }

        NimettyMonikielinenTekstiTyyppi ytmk = ConversionUtils.getTeksti(input.getTekstit(), KomotoTeksti.SISALTO);
        if (ytmk != null) {
            for (MonikielinenTekstiTyyppi.Teksti mkt : ytmk.getTeksti()) {
                KorkeakouluLisatietoModel lisatieto = model.getLisatiedot(mkt.getKieliKoodi());
                lisatieto.setYhteistyoMuidenToimijoidenKanssa(mkt.getValue());
            }
        }

        //TODO: missing conversions!!!!!

        return model;
    }

    public static KoulutusTyyppi convertToKorkeakouluKoulutusTyyppi(KoulutusTyyppi tyyppi, final KoulutusasteTyyppi koulutusasteTyyppi, final KorkeakouluPerustiedotViewModel model, ValitseKoulutusModel valitseKoulutusModel, final String komotoOid, OrganisaatioPerustieto organisation) {
        Preconditions.checkNotNull(tyyppi, INVALID_DATA + "KoulutusTyyppi object cannot be null.");
        Preconditions.checkNotNull(koulutusasteTyyppi, INVALID_DATA + "KoulutusasteTyyppi object cannot be null.");
        Preconditions.checkNotNull(model, INVALID_DATA + "KorkeakouluPerustiedotViewModel object cannot be null.");
        Preconditions.checkNotNull(komotoOid, INVALID_DATA + "KOMOTO OID cannot be null.");
        Preconditions.checkNotNull(organisation, INVALID_DATA + "Organisation DTO cannot be null.");
        Preconditions.checkNotNull(organisation.getOid(), INVALID_DATA + "Organisation OID cannot be null.");

        /*
         * set Optimistic lock version
         */
        tyyppi.setVersion(model.getVersion());
        /*
         * KOMO:
         * set the koulutus and tutkinto-ohjelma data to koulutusmoduuli tyyppi
         */
        tyyppi.setKoulutusmoduuli(convertModelToKoulutusmoduuliKoosteTyyppi(model, valitseKoulutusModel, koulutusasteTyyppi));

        /*
         * Other input fields
         */
        tyyppi.setKoulutustyyppi(koulutusasteTyyppi);
        tyyppi.setKoulutuksenAlkamisPaiva(model.getKoulutuksenAlkamisPvm());
        KoulutuksenKestoTyyppi koulutuksenKestoTyyppi = new KoulutuksenKestoTyyppi();
        koulutuksenKestoTyyppi.setArvo(model.getSuunniteltuKesto());
        koulutuksenKestoTyyppi.setYksikko(model.getSuunniteltuKestoTyyppi());
        tyyppi.setKesto(koulutuksenKestoTyyppi);
        tyyppi.setMaksullisuus(model.getOpintojenMaksullisuus());
        tyyppi.setUlkoinenTunniste(model.getTunniste());
        /*
         * OIDs
         */
        tyyppi.setTarjoaja(organisation.getOid());
        tyyppi.setOid(komotoOid);

        /*
         * Other optional(?) sets/list
         */
        if (model.getOpetusmuodos() != null && !model.getOpetusmuodos().isEmpty()) {
            for (String opetusmuoto : model.getOpetusmuodos()) {
                tyyppi.getOpetusmuoto().add(createKoodi(opetusmuoto, true, "opetusmuoto"));
            }
        }

        if (model.getOpetuskielis() != null && !model.getOpetuskielis().isEmpty()) {
            for (String kieli : model.getOpetuskielis()) {
                tyyppi.getOpetuskieli().add(createKoodi(kieli, true, "opetuskieli"));
            }
        }

        if (model.getTeemas() != null && !model.getTeemas().isEmpty()) {
            for (String teema : model.getTeemas()) {
                tyyppi.getTeemat().add(createKoodi(teema, true, "teema"));
            }
        }

        if (model.getPohjakoulutusvaatimukset() != null && !model.getPohjakoulutusvaatimukset().isEmpty()) {
            for (String pohjakoulutusvaatimus : model.getPohjakoulutusvaatimukset()) {
                tyyppi.getPohjakoulutusvaatimusKorkeakoulu().add(createKoodi(pohjakoulutusvaatimus, true, "pohjakoulutusvaatimus"));
            }
        }

        if (model.getYhteyshenkilo().getYhtHenkKokoNimi() != null && !model.getYhteyshenkilo().getYhtHenkKokoNimi().isEmpty()) {
            tyyppi.getYhteyshenkiloTyyppi().add(mapYhteyshenkiloToTyyppi(model.getYhteyshenkilo(), HenkiloTyyppi.YHTEYSHENKILO));
        }

        if (model.getEctsKoordinaattori().getYhtHenkKokoNimi() != null && !model.getEctsKoordinaattori().getYhtHenkKokoNimi().isEmpty()) {
            tyyppi.getYhteyshenkiloTyyppi().add(mapYhteyshenkiloToTyyppi(model.getEctsKoordinaattori(), HenkiloTyyppi.ECTS_KOORDINAATTORI));
        }

        /*
         * last updated by
         */
        tyyppi.setViimeisinPaivittajaOid(model.getViimeisinPaivittajaOid());
        if (model.getViimeisinPaivitysPvm() != null) {
            tyyppi.setViimeisinPaivitysPvm(model.getViimeisinPaivitysPvm());
        } else {
            tyyppi.setViimeisinPaivitysPvm(new Date());
        }


        return tyyppi;
    }

    private KorkeakouluPerustiedotViewModel createKorkeakouluPerustiedotViewModel(LueKoulutusVastausTyyppi vastaus, Locale locale) {
        Preconditions.checkNotNull(vastaus, INVALID_DATA + "LueKoulutusVastausTyyppi object cannot be null.");
        Preconditions.checkNotNull(vastaus.getKoulutusmoduuli(), INVALID_DATA + "KoulutusmoduuliKoosteTyyppi object cannot be null.");

        KorkeakouluPerustiedotViewModel perustiedotModel = new KorkeakouluPerustiedotViewModel();
        /*
         * set Optimistic lock version
         */
        Preconditions.checkNotNull(vastaus.getVersion(), INVALID_DATA + "Version ID for optimistic locking control cannot be null.");
        perustiedotModel.setVersion(vastaus.getVersion());

        /*
         * set basic data
         */
        perustiedotModel.setTila(vastaus.getTila());
        perustiedotModel.setKomotoOid(vastaus.getOid());
        perustiedotModel.setKoulutusmoduuliOid(vastaus.getKoulutusmoduuli().getOid());
        perustiedotModel.setOpintojenMaksullisuus(vastaus.isMaksullisuus());
        perustiedotModel.setTunniste(vastaus.getKoulutusmoduuli().getUlkoinenTunniste()); //TODO: fix ulkoinen tunniste
        /*
         * Combobox data;
         * 
         * support for the  case:
         * A) When KOMO is yet not saved to DB, it load all komo related data from Koodisto service.
         * 
         * B) When KOMO is loaded from DB, it uses the loaded URIs.
         * 
         */
        KoulutuskoodiModel koulutuskoodiModel = koulutusKoodisto.listaaKorkeakouluKoulutuskoodi(vastaus.getKoulutusKoodi(), vastaus.getKoulutusmoduuli(), locale);

        perustiedotModel.setKoulutuskoodiModel(koulutuskoodiModel);
        perustiedotModel.setTutkintoohjelma(convertKoosteTyyppiToTutkintoohjelmaModel(vastaus.getKoulutusmoduuli(), locale));

        Preconditions.checkNotNull(perustiedotModel.getKoulutuskoodiModel(), INVALID_DATA + "kolutuskoodi model cannot be null.");
        Preconditions.checkNotNull(perustiedotModel.getTutkintoohjelma(), INVALID_DATA + "tutkinto-ohjelma model cannot be null.");

        /*
         * Update the select koulutuskoodi dialog data object
         */
        //perustiedotModel.getValitseKoulutus().setKoulutuskoodiRow(new KoulutuskoodiRowModel(perustiedotModel.getKoulutuskoodiModel()));

        /*
         * Other UI fields
         */
        perustiedotModel.setKoulutuksenAlkamisPvm(vastaus.getKoulutuksenAlkamisPaiva() != null ? vastaus.getKoulutuksenAlkamisPaiva() : null);

        if (vastaus.getKesto() != null) {
            perustiedotModel.setSuunniteltuKesto(vastaus.getKesto().getArvo());
            perustiedotModel.setSuunniteltuKestoTyyppi(vastaus.getKesto().getYksikko());
        }

        if (vastaus.getOpetuskieli() != null && !vastaus.getOpetuskieli().isEmpty()) {
            perustiedotModel.setOpetuskielis(convertListToSet(vastaus.getOpetuskieli()));
        }

        if (vastaus.getOpetusmuoto() != null && !vastaus.getOpetusmuoto().isEmpty()) {
            perustiedotModel.setOpetusmuodos(convertListToSet(vastaus.getOpetusmuoto()));
        }

        if (vastaus.getTeemat() != null && !vastaus.getTeemat().isEmpty()) {
            perustiedotModel.setTeemas(convertListToSet(vastaus.getTeemat()));
        }

        if (vastaus.getPohjakoulutusvaatimusKorkeakoulu() != null && !vastaus.getPohjakoulutusvaatimusKorkeakoulu().isEmpty()) {
            perustiedotModel.setPohjakoulutusvaatimukset(convertListToSet(vastaus.getPohjakoulutusvaatimusKorkeakoulu()));
        }

        /*
         * contact person data conversion
         */
        mapYhteyshenkiloToViewModel(perustiedotModel.getYhteyshenkilo(), vastaus, HenkiloTyyppi.YHTEYSHENKILO);
        mapYhteyshenkiloToViewModel(perustiedotModel.getEctsKoordinaattori(), vastaus, HenkiloTyyppi.ECTS_KOORDINAATTORI);

        /*
         * Data fields used on UI only as extra information:
         */

        //6-numero koodi arvo 
        perustiedotModel.setKoulutuskoodi(perustiedotModel.getKoulutuskoodiModel().getKoodi());

        return perustiedotModel;
    }

    private TutkintoohjelmaModel convertKoosteTyyppiToTutkintoohjelmaModel(KoulutusmoduuliKoosteTyyppi tyyppi, final Locale locale) {
        Preconditions.checkNotNull(tyyppi, "TutkintoohjelmaTyyppi object for tutkinto-ohjelma cannot be null.");
        Preconditions.checkNotNull(tyyppi.getUlkoinenTunniste(), "UlkoinenTunniste (numeric value) for tutkinto-ohjelma cannot be null.");
        Preconditions.checkNotNull(tyyppi.getNimi(), "Tutkinto-ohjelma name object cannot be null.");
        Preconditions.checkArgument(!tyyppi.getNimi().getTeksti().isEmpty(), "No tutkinto-ohjelma names for KOMO.");

        UiModelBuilder<TutkintoohjelmaModel> builder = new UiModelBuilder<TutkintoohjelmaModel>(TutkintoohjelmaModel.class, helper);
        TutkintoohjelmaModel tutkintoohjelma = builder.build(tyyppi.getNimi(), locale);
        tutkintoohjelma.setKoodi(tyyppi.getUlkoinenTunniste());

        return tutkintoohjelma;
    }

    public List<TutkintoohjelmaModel> convertToTutkintoohjelmaModels(final List<KoulutusmoduuliKoosteTyyppi> komos, final Locale locale) {
        Preconditions.checkNotNull(komos, "List of KoulutusmoduuliKoosteTyyppi object cannot be null.");

        List<TutkintoohjelmaModel> tutkintoohjelma = Lists.<TutkintoohjelmaModel>newArrayList();
        for (KoulutusmoduuliKoosteTyyppi t : komos) {
            tutkintoohjelma.add(convertKoosteTyyppiToTutkintoohjelmaModel(t, locale));
        }

        return tutkintoohjelma;
    }

    public void updateKoulutuskoodiModel(KorkeakouluPerustiedotViewModel model, ValitseKoulutusModel valitseKoulutusModel, Locale locale) {
        Preconditions.checkNotNull(model, "KorkeakouluPerustiedotViewModel object cannot be null.");
        Preconditions.checkNotNull(valitseKoulutusModel, "ValitseKoulutusModel object cannot be null.");
        KoulutuskoodiRowModel koulutuskoodiRow = valitseKoulutusModel.getKoulutuskoodiRow();
        Preconditions.checkNotNull(koulutuskoodiRow, "KoulutuskoodiRowModel object cannot be null.");

        final String koulutuskoodiUri = koulutuskoodiRow.getKoodistoUri();
        Preconditions.checkNotNull(koulutuskoodiUri, "Koulutuskoodi URI cannot be null.");

        KoulutuskoodiModel listaaKoulutuskoodi = koulutusKoodisto.listaaKorkeakouluKoulutuskoodi(koulutuskoodiUri, locale);
        Preconditions.checkNotNull(listaaKoulutuskoodi, "KoulutuskoodiModel object cannot be null.");
        model.setKoulutuskoodiModel(listaaKoulutuskoodi);
    }

    /**
     * Tutkinto-ohjelma + koulutuskoodi (KOMO) data conversion to Tyyppi object.
     */
    private static KoulutusmoduuliKoosteTyyppi convertModelToKoulutusmoduuliKoosteTyyppi(KorkeakouluPerustiedotViewModel model, ValitseKoulutusModel valitseKoulutusModel, KoulutusasteTyyppi koulutusasteTyyppi) {
        Preconditions.checkNotNull(model, "KorkeakouluPerustiedotViewModel object cannot be null.");
        Preconditions.checkNotNull(model.getTutkintoohjelma(), "Tutkinto-ohjelma UI model cannot be null!");
        Preconditions.checkNotNull(model.getTutkintoohjelma().getKielikaannos(), "Tutkinto-ohjelma kielikaannos set cannot be null!");
        Preconditions.checkArgument(!model.getTutkintoohjelma().getKielikaannos().isEmpty(), "Tutkinto-ohjelma kielikaannos cannot be empty!");
        Preconditions.checkNotNull(model.getTunniste(), "Tutkinto-ohjelma external ID cannot be null!");

        KoulutusmoduuliKoosteTyyppi kooste = new KoulutusmoduuliKoosteTyyppi();
        kooste.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);
        kooste.setKoulutustyyppi(koulutusasteTyyppi);

        /*
         * get koulutusohjelma 'koodi' from the dialog model
         */
        KoulutuskoodiRowModel koulutuskoodiRow = valitseKoulutusModel.getKoulutuskoodiRow();
        Preconditions.checkNotNull(koulutuskoodiRow, "Koulutus not selected, or KoulutuskoodiRowModel object not initialized!");
        Preconditions.checkNotNull(koulutuskoodiRow.getKoodistoUriVersio(), "Koulutuskoodi URI cannot be null!");
        kooste.setKoulutuskoodiUri(koulutuskoodiRow.getKoodistoUriVersio());


        /*
         * KoulutuskoodiModel
         */
        KoulutuskoodiModel km = model.getKoulutuskoodiModel();
        if (km != null) {
            if (km.getKoulutusala() != null) {
                kooste.setKoulutusalaUri(km.getKoulutusala().getKoodistoUriVersio());
            }

            if (km.getOpintoala() != null) {
                kooste.setOpintoalaUri(km.getOpintoala().getKoodistoUriVersio());
            }

            if (km.getTutkintonimike() != null) {
                kooste.setTutkintonimikeUri(km.getTutkintonimike().getKoodistoUriVersio());
            }

            if (km.getOpintojenLaajuusyksikko() != null) {
                kooste.setLaajuusyksikkoUri(km.getOpintojenLaajuusyksikko().getKoodistoUriVersio());
            }
            
             if (km.getOpintojenLaajuus() != null) {
                kooste.setLaajuusyksikkoUri(km.getOpintojenLaajuus().getKoodistoUriVersio());
            }

            if (km.getKoulutusaste() != null) {
                kooste.setKoulutusasteUri(km.getKoulutusaste().getKoodistoUriVersio());
            }
        }

        //we use ulkoinen tunniste field for the 'numeric' code value.
        kooste.setOid(model.getKoulutusmoduuliOid());
        kooste.setUlkoinenTunniste(model.getTunniste());

        return kooste;
    }
 
}
