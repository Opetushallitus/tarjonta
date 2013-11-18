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
package fi.vm.sade.tarjonta.shared;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Common helper access for Koodisto URIs. These should be configured from
 * Spring.
 *
 * @author Tuomas Katva
 * @author mlyly
 * @author Jani Wilen
 */
@Component
@Configurable(preConstruction = false)
public class KoodistoURI {

    /*
     * Language koodi uris
     */
    public static String KOODI_LANG_FI_URI;
    public static String KOODI_LANG_EN_URI;
    public static String KOODI_LANG_SV_URI;

    /*
     * Haku URIs
     */
    public static String KOODISTO_HAKUTAPA_URI;
    public static String KOODISTO_KIELI_URI;
    public static String KOODISTO_HAKUKOHDE_URI;
    public static String KOODISTO_SUUNNITELTU_KESTO_URI;
    public static String KOODISTO_KOULUTUSLAJI_URI;
    public static String KOODISTO_LIITTEEN_TYYPPI_URI;
    /*
     * Organization navi URIs
     */
    public static String KOODISTO_OPPILAITOSTYYPPI_URI;
    /*
     * Top search area URIs
     */
    public static String KOODISTO_ALKAMISKAUSI_URI; // kevät, syksy etc.
    public static String KOODISTO_HAKUTYYPPI_URI;
    public static String KOODISTO_HAUN_KOHDEJOUKKO_URI;
    /*
     * KOMO URIs
     */
    public static String KOODISTO_TUTKINTO_URI; //please rename to KOULUTUS
    public static String KOODISTO_TUTKINTO_NIMI_URI; //please rename to TUTKINTO
    public static String KOODISTO_KOULUTUSOHJELMA_URI;
    public static String KOODISTO_KOULUTUSASTE_URI;
    public static String KOODISTO_KOULUTUSALA_URI;
    public static String KOODISTO_TUTKINTONIMIKE_URI;
    public static String KOODISTO_OPINTOALA_URI;
    public static String KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI;
    public static String KOODISTO_OPINTOJEN_LAAJUUSARVO_URI;
    public static String KOODISTO_POHJAKOULUTUSVAATIMUKSET_URI;
    public static String KOODISTO_EQF_LUOKITUS_URI;
    /*
     * KOMOTO URIs
     */
    public static String KOODISTO_AMMATTINIMIKKEET_URI;
    public static String KOODISTO_OPETUSMUOTO_URI;
    public static String KOODISTO_POSTINUMERO_URI;
    /*
     * Valintaperustekuvaus URIs
     */
    public static String KOODISTO_VALINTAPERUSTEKUVAUSRYHMA_URI;
    public static String KOODISTO_SORA_KUVAUSRYHMA_URI;
    /*
     * Lukiotutkinto URIs
     */
    public static String KOODISTO_LUKIOLINJA_URI;
    public static String KOODI_KOULUTUSLAJI_NUORTEN_KOULUTUS_URI;
    public static String LUKIO_KOODI_POHJAKOULUTUSVAATIMUS_URI;
    public static String KOODISTO_LUKIODIPLOMIT_URI;
    /**
     * Oppiaineet
     */
    public static String KOODISTO_OPPIAINEET_URI;
    /**
     * Hakukohde / valintakoe
     */
    public static String KOODISTO_VALINTAKOE_TYYPPI_URI;
//    /*
//     * For tutkinto dialog
//     */
//    // public static String KOODISTO_TARJONTA_KOULUTUSASTE;
    /*
     * For korkeakoulu
     */
    public static String KOODISTO_TEEMAT_URI;
    public static String KOODISTO_HAKUKELPOISUUSVAATIMUS_URI;
    public static String KOODISTO_POHJAKOULUTUSVAATIMUKSET_KORKEAKOULU_URI;
    public static String KOODISTO_TUTKINTONIMIKE_KORKEAKOULU_URI;
    /*
     * For tutkinto dialog
     */
    public static String KOODISTO_TARJONTA_KOULUTUSTYYPPI;
    public static String KOODI_LISAHAKU_URI;

    public static String KOODI_YKSILOLLISTETTY_PERUSOPETUS_URI;
    public static String KOODI_YHTEISHAKU_URI;
    public static String KOODI_ERILLISHAKU_URI;//koodisto-uris.erillishaku=hakutyyppi_02#1
    public static String KOODI_HAASTATTELU_URI;//koodisto-uris.valintakoeHaastattelu=valintakokeentyyppi_6#1
    public static String KOODI_TODISTUKSET_URI;

    public static String KOODI_KOHDEJOUKKO_ERITYISOPETUS_URI;
    public static String KOODI_KOHDEJOUKKO_VALMENTAVA_URI;
    public static String KOODI_KOHDEJOUKKO_AMMATILLINEN_LUKIO_URI; //
    public static String KOODI_POHJAKOULUTUS_PERUSKOULU_URI;
    public static String KOODI_KOHDEJOUKKO_VALMISTAVA_URI;
    public static String KOODI_KOHDEJOUKKO_VAPAASIVISTYS_URI;

    @Value("${koodisto-uris.vapaaSivistys:haunkohdejoukko_18#1}")
    public void setKoodiKohdejoukkoVapaaSivistysUri(String uri) {
        KOODI_KOHDEJOUKKO_VAPAASIVISTYS_URI = uri;
    }

    @Value("${koodisto-uris.valmistavaOpetus:haunkohdejoukko_17#1}")
    public void setKoodiKohdejoukkoValmistavaUri(String uri) {
        KOODI_KOHDEJOUKKO_VALMISTAVA_URI = uri;
    }

    @Value("${koodisto-uris.pohjakoulutusPeruskoulu:pohjakoulutusvaatimustoinenaste_pk#1}")
    public void setKoodiPohjakoulutusperuskouluUri(String uri) {
        KOODI_POHJAKOULUTUS_PERUSKOULU_URI = uri;
    }

    @Value("${koodisto-uris.ammatillinenLukio:haunkohdejoukko_11#1}")
    public void setKoodiKohdejoukkoAmmatillinenLukioUri(String uri) {
        KOODI_KOHDEJOUKKO_AMMATILLINEN_LUKIO_URI = uri;
    }

    @Value("${koodisto-uris.kohdejoukkoErityisopetus:haunkohdejoukko_15#1}")
    public void setKoodiKohdejoukkoErityisopets(String uri) {
        KOODI_KOHDEJOUKKO_ERITYISOPETUS_URI = uri;
    }

    @Value("${koodisto-uris.valmentavaKuntouttava:haunkohdejoukko_16#1}")
    public void setKoodiKohdejoukkoValmentavaUri(String uri) {
        KOODI_KOHDEJOUKKO_VALMENTAVA_URI = uri;
    }

    @Value("${koodisto-uris.liiteTodistukset:liitetyypitamm_3#1}")
    public void setKoodiTodistuksetUri(String uri) {
        KOODI_TODISTUKSET_URI = uri;
    }

    @Value("${koodisto-uris.valintakoeHaastattelu:valintakokeentyyppi_6#1}")
    public void setKoodiHaastatteluUri(String uri) {
        KOODI_HAASTATTELU_URI = uri;
    }

    @Value("${koodisto-uris.erillishaku:hakutapa_02#1}")
    public void setKoodiErillishakuUri(String uri) {
        KOODI_ERILLISHAKU_URI = uri;
    }

    @Value("${koodisto-uris.yhteishaku:hakutapa_01#1}")
    public void setKoodiYhteishakuUri(String uri) {
        KOODI_YHTEISHAKU_URI = uri;
    }

    @Value("${kodisto-uris.yksilollistettyPerusopetus:pohjakoulutusvaatimustoinenaste_er}")
    public void setKoodiYksilollistettyPerusopetusUri(String uri) {
        KOODI_YKSILOLLISTETTY_PERUSOPETUS_URI = uri;
    }

    @Value("${koodisto-uris.lisahaku:hakutyyppi_03#1}")
    public void setKoodiLisahakuUri(String uri) {
        KOODI_LISAHAKU_URI = uri;
    }

    @Value("${koodisto.language.fi.uri:kieli_fi}")
    public void setKoodiLangFiUri(String uri) {
        KOODI_LANG_FI_URI = uri;
    }

    @Value("${koodisto.language.en.uri:kieli_en}")
    public void setKoodiLangEnUri(String uri) {
        KOODI_LANG_EN_URI = uri;
    }

    @Value("${koodisto.language.sv.uri:kieli_sv}")
    public void setKoodiLangSvUri(String uri) {
        KOODI_LANG_SV_URI = uri;
    }

    @Value("${koodisto-uris.valintakokeentyyppi}")
    public void setKoodistoValintakoetyyppiUri(String uri) {
        KOODISTO_VALINTAKOE_TYYPPI_URI = uri;
    }

//    @Value("${koodisto-uris.tarjontakoulutusaste}")
//    public void setKoodistoTarjontaKoulutusaste(String uri) {
//        KOODISTO_TARJONTA_KOULUTUSASTE = uri;
//    }
    @Value("${koodisto-uris.hakutyyppi}")
    public void setKoodistoHakutyyppiUri(String uri) {
        KOODISTO_HAKUTYYPPI_URI = uri;
    }

    @Value("${koodisto-uris.kieli}")
    public void setKoodistoKieliUri(String uri) {
        KOODISTO_KIELI_URI = uri;
    }

    @Value("${koodisto-uris.hakutapa}")
    public void setKoodistoHakutapaUri(String uri) {
        KOODISTO_HAKUTAPA_URI = uri;
    }

    @Value("${koodisto-uris.haunKohdejoukko}")
    public void setKoodistoHaunKohdejoukkoUri(String uri) {
        KOODISTO_HAUN_KOHDEJOUKKO_URI = uri;
    }

    @Value("${koodisto-uris.alkamiskausi}")
    public void setKoodistoAlkamiskausiUri(String uri) {
        KOODISTO_ALKAMISKAUSI_URI = uri;
    }

    @Value("${koodisto-uris.oppilaitostyyppi}")
    public void setKoodistoOppilaitosTyyppiUri(String uri) {
        KOODISTO_OPPILAITOSTYYPPI_URI = uri;
    }

    @Value("${koodisto-uris.hakukohde}")
    public void setKoodistoHakukohdeUri(String uri) {
        KOODISTO_HAKUKOHDE_URI = uri;
    }

    @Value("${koodisto-uris.suunniteltuKesto}")
    public void setKoodistoSuunniteltuKestoUri(String uri) {
        KOODISTO_SUUNNITELTU_KESTO_URI = uri;
    }

    @Value("${koodisto-uris.opetusmuoto}")
    public void setKoodistoOpetusmuotoUri(String uri) {
        KOODISTO_OPETUSMUOTO_URI = uri;
    }

    @Value("${koodisto-uris.koulutuslaji}")
    public void setKoodistoKoulutuslajiUri(String uri) {
        KOODISTO_KOULUTUSLAJI_URI = uri;
    }

    @Value("${koodisto-uris.ammattinimikkeet}")
    public void setAmmattinimikkeetUri(String uri) {
        KOODISTO_AMMATTINIMIKKEET_URI = uri;
    }

    /*
     *
     * KOMO URIs
     *
     */
    @Value("${koodisto-uris.koulutus}")
    public void setKoodistoKoulutusUri(String uri) {
        KOODISTO_TUTKINTO_URI = uri;
    }

    @Value("${koodisto-uris.tutkinto}")
    public void setKoodistoTutkintoUri(String uri) {
        KOODISTO_TUTKINTO_NIMI_URI = uri;
    }

    @Value("${koodisto-uris.koulutusohjelma}")
    public void setKoodistoKoulutusohjelmaUri(String uri) {
        KOODISTO_KOULUTUSOHJELMA_URI = uri;
    }

    @Value("${koodisto-uris.koulutusaste}")
    public void setKoodistoasteUri(String uri) {
        KOODISTO_KOULUTUSASTE_URI = uri;
    }

    @Value("${koodisto-uris.koulutusala}")
    public void setKoulutusalaUri(String uri) {
        KOODISTO_KOULUTUSALA_URI = uri;
    }

    @Value("${koodisto-uris.tutkintonimike}")
    public void setTutkintonimikeUri(String uri) {
        KOODISTO_TUTKINTONIMIKE_URI = uri;
    }

    @Value("${koodisto-uris.opintoala}")
    public void setOpintoalaUri(String uri) {
        KOODISTO_OPINTOALA_URI = uri;
    }

    @Value("${koodisto-uris.opintojenLaajuusyksikko}")
    public void setOpintojenLaajuusyksikkoUri(String uri) {
        KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI = uri;
    }

    @Value("${koodisto-uris.opintojenLaajuusarvo}")
    public void setOpintojenLaajuusarvoUri(String uri) {
        KOODISTO_OPINTOJEN_LAAJUUSARVO_URI = uri;
    }

    @Value("${koodisto-uris.pohjakoulutusvaatimus}")
    public void setPohjakoulutusvaatimuksetUri(String uri) {
        KOODISTO_POHJAKOULUTUSVAATIMUKSET_URI = uri;
    }

    @Value("${koodisto-uris.postinumero}")
    public void setPostinumero(String postinumero) {
        KOODISTO_POSTINUMERO_URI = postinumero;
    }

    @Value("${koodisto-uris.liitteentyyppi}")
    public void setLiitteenTyyppi(String liitteenTyyppi) {
        KOODISTO_LIITTEEN_TYYPPI_URI = liitteenTyyppi;
    }

    @Value("${koodisto-uris.valintaperustekuvausryhma}")
    public void setValintaperustekuvausryhma(String valintaperustekuvausryhma) {
        KOODISTO_VALINTAPERUSTEKUVAUSRYHMA_URI = valintaperustekuvausryhma;
    }

    @Value("${koodisto-uris.sorakuvausryhma}")
    public void setSoraKuvausryhma(String soraKuvausryhma) {
        KOODISTO_SORA_KUVAUSRYHMA_URI = soraKuvausryhma;
    }

    @Value("${koodisto-uris.lukiolinja}")
    public void setLukiolinja(String lukiolinja) {
        KOODISTO_LUKIOLINJA_URI = lukiolinja;
    }

    @Value("${koodi-uri.lukio.pohjakoulutusvaatimus}")
    public void setLukioKoodiPohjakoulutusvaatimus(String pohjakoulutusvaatimus) {
        LUKIO_KOODI_POHJAKOULUTUSVAATIMUS_URI = pohjakoulutusvaatimus;
    }

    @Value("${koodi-uri.koulutuslaji.nuortenKoulutus}")
    public void setKoodiKoulutuslajiNuortenKoulutus(String koulutuslaji) {
        KOODI_KOULUTUSLAJI_NUORTEN_KOULUTUS_URI = koulutuslaji;
    }

    @Value("${koodisto-uris.oppiaineet}")
    public void setOppiaineet(String oppiaineet) {
        KOODISTO_OPPIAINEET_URI = oppiaineet;
    }

    @Value("${koodisto-uris.lukiodiplomit}")
    public void setLukiodiplomit(String lukiodiplomit) {
        KOODISTO_LUKIODIPLOMIT_URI = lukiodiplomit;
    }

    @Value("${koodisto-uris.teemat}")
    public void setTeemat(String teemat) {
        KOODISTO_TEEMAT_URI = teemat;
    }

    @Value("${koodisto-uris.hakukelpoisuusvaatimus}")
    public void setKoodistoHakukelpoisuusvaatimusUri(String uri) {
        KOODISTO_HAKUKELPOISUUSVAATIMUS_URI = uri;
    }

    @Value("${koodisto-uris.tarjontakoulutustyyppi}")
    public void setKoodistoTarjontaKoulutustyyppi(String uri) {
        KOODISTO_TARJONTA_KOULUTUSTYYPPI = uri;
    }

    @Value("${koodisto-uris.eqf-luokitus}")
    public void setKoodistoEqfLuokitus(String uri) {
        KOODISTO_EQF_LUOKITUS_URI = uri;
    }

    @Value("${koodisto-uris.pohjakoulutusvaatimus_kk}")
    public void setKoodistoPohjakoulutusvaatimuksetKk(String uri) {
        KOODISTO_POHJAKOULUTUSVAATIMUKSET_KORKEAKOULU_URI = uri;
    }

    @Value("${koodisto-uris.tutkintonimike_kk}")
    public void setKoodistoTutkintonimikeKk(String uri) {
        KOODISTO_TUTKINTONIMIKE_KORKEAKOULU_URI = uri;
    }
}
