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
package fi.vm.sade.tarjonta.ui.helper;

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
public class KoodistoURIHelper {

    public static String KOODISTO_HAKUTAPA_URI;
    public static String KOODISTO_KIELI_URI;
    public static String KOODISTO_HAKUKELPOISUUS_VAATIMUKSET_URI;
    public static String KOODISTO_HAKUKOHDE_URI;
    public static String KOODISTO_SUUNNITELTU_KESTO_URI;
    public static String KOODISTO_KOULUTUSLAJI_URI;
    public static String KOODISTO_AVAINSANAT_URI;
    public static String KOODISTO_KIELIVALIKOIMA_URI;
    public static String KOODISTO_LIITTEEN_TYYPPI_URI;
    /*
     * Haku URIs
     */
    public static String KOODISTO_HAKUKAUSI_URI;
    /*
     * Organization navi URIs
     */
    public static String KOODISTO_OPPILAITOSTYYPPI_URI;
    /*
     * Top search area URIs
     */
    public static String KOODISTO_KAUDEN_TARKENNE_URI; //koulutuksen alkamiskaus etc.
    public static String KOODISTO_ALKAMISKAUSI_URI; // kevät, syksy etc.
    public static String KOODISTO_HAKUTYYPPI_URI;
    public static String KOODISTO_HAUN_KOHDEJOUKKO_URI;
    /*
     * KOMO URIs
     */
    public static String KOODISTO_TUTKINTO_URI;
    public static String KOODISTO_KOULUTUSOHJELMA_URI;
    public static String KOODISTO_KOULUTUSASTE_URI;
    public static String KOODISTO_KOULUTUSALA_URI;
    public static String KOODISTO_TUTKINTONIMIKE_URI;
    public static String KOODISTO_KOULUTUKSEN_RAKENNE_URI;
    public static String KOODISTO_OPINTOALA_URI;
    public static String KOODISTO_OPINTOJEN_LAAJUUS_URI;
    public static String KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI;
    public static String KOODISTO_TAVOITTEET_URI;
    public static String KOODISTO_JATKOOPINTOMAHDOLLISUUDET_URI;
    public static String KOODISTO_POHJAKOULUTUSVAATIMUKSET_URI;
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
    public static String LUKIO_KOODI_KOULUTUSLAJI_URI;
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


    @Value("${koodisto-uris.valintakokeentyyppi:NOT_SET_valintakokeentyyppi}")
    public void setKoodistoValintakoetyyppiUri(String uri) {
        KOODISTO_VALINTAKOE_TYYPPI_URI = uri;
    }

    @Value("${koodisto-uris.hakutyyppi:NOT_SET_hakutyyppi}")
    public void setKoodistoHakutyyppiUri(String uri) {
        KOODISTO_HAKUTYYPPI_URI = uri;
    }

    @Value("${koodisto-uris.kieli:NOT_SET_kieli}")
    public void setKoodistoKieliUri(String uri) {
        KOODISTO_KIELI_URI = uri;
    }

    @Value("${koodisto-uris.hakutapa:NOT_SET_hakutapa}")
    public void setKoodistoHakutapaUri(String uri) {
        KOODISTO_HAKUTAPA_URI = uri;
    }

    @Value("${koodisto-uris.haunKohdejoukko:NOT_SET_haunKohdejoukko}")
    public void setKoodistoHaunKohdejoukkoUri(String uri) {
        KOODISTO_HAUN_KOHDEJOUKKO_URI = uri;
    }

    @Value("${koodisto-uris.koulutuksenAlkamiskausi:NOT_SET_koulutuksenAlkamiskausi}")
    public void setKoodistoKoulutuksenAlkamiskausiUri(String uri) {
        KOODISTO_KAUDEN_TARKENNE_URI = uri;
    }

    @Value("${koodisto-uris.alkamiskausi:NOT_SET_alkamiskausi}")
    public void setKoodistoAlkamiskausiUri(String uri) {
        KOODISTO_ALKAMISKAUSI_URI = uri;
    }

    @Value("${koodisto-uris.oppilaitostyyppi:NOT_SET_oppilaitostyyppi}")
    public void setKoodistoOppilaitosTyyppiUri(String uri) {
        KOODISTO_OPPILAITOSTYYPPI_URI = uri;
    }

    @Value("${koodisto-uris.hakukelpoisuusVaatimukset:NOT_SET_hakukelpoisuusVaatimukset}")
    public void setKoodistoHakukelpoisuusVaatimuksetUri(String uri) {
        KOODISTO_HAKUKELPOISUUS_VAATIMUKSET_URI = uri;
    }

    @Value("${koodisto-uris.hakukohde:NOT_SET_hakukohde}")
    public void setKoodistoHakukohdeUri(String uri) {
        KOODISTO_HAKUKOHDE_URI = uri;
    }

    @Value("${koodisto-uris.suunniteltuKesto:NOT_SET_suunniteltuKesto}")
    public void setKoodistoSuunniteltuKestoUri(String uri) {
        KOODISTO_SUUNNITELTU_KESTO_URI = uri;
    }

    @Value("${koodisto-uris.opetusmuoto:NOT_SET_opetusmuoto}")
    public void setKoodistoOpetusmuotoUri(String uri) {
        KOODISTO_OPETUSMUOTO_URI = uri;
    }

    @Value("${koodisto-uris.koulutuslaji:NOT_SET_koulutuslaji}")
    public void setKoodistoKoulutuslajiUri(String uri) {
        KOODISTO_KOULUTUSLAJI_URI = uri;
    }

    @Value("${koodisto-uris.avainsanat:NOT_SET_avainsanat}")
    public void setKoodistoAvainsanatUri(String uri) {
        KOODISTO_AVAINSANAT_URI = uri;
    }

    @Value("${koodisto-uris.kielivalikoima:NOT_SET_kielivalikoima}")
    public void setKoodistoKielivalikoimaUri(String uri) {
        KOODISTO_KIELIVALIKOIMA_URI = uri;
    }

    @Value("${koodisto-uris.ammattinimikkeet:NOT_SET_ammattinimikkeet}")
    public void setAmmattinimikkeetUri(String uri) {
        KOODISTO_AMMATTINIMIKKEET_URI = uri;
    }

    /*
     *
     * KOMO URIs
     *
     */
    @Value("${koodisto-uris.tutkinto:NOT_SET_tutkinto}")
    public void setKoodistoTutkintoUri(String uri) {
        KOODISTO_TUTKINTO_URI = uri;
    }

    @Value("${koodisto-uris.koulutusohjelma:NOT_SET_koulutusohjelma}")
    public void setKoodistoKoulutusohjelmaUri(String uri) {
        KOODISTO_KOULUTUSOHJELMA_URI = uri;
    }

    @Value("${koodisto-uris.koulutusaste:NOT_SET_koulutusaste}")
    public void setKoodistoasteUri(String uri) {
        KOODISTO_KOULUTUSASTE_URI = uri;
    }

    @Value("${koodisto-uris.koulutusala:NOT_SET_koulutusala}")
    public void setKoulutusalaUri(String uri) {
        KOODISTO_KOULUTUSALA_URI = uri;
    }

    @Value("${koodisto-uris.tutkintonimike:NOT_SET_tutkintonimike}")
    public void setTutkintonimikeUri(String uri) {
        KOODISTO_TUTKINTONIMIKE_URI = uri;
    }

    @Value("${koodisto-uris.koulutuksenRakenne:NOT_SET_koulutuksenRakenne}")
    public void setKoulutuksenRakenneUri(String uri) {
        KOODISTO_KOULUTUKSEN_RAKENNE_URI = uri;
    }

    @Value("${koodisto-uris.opintoala:NOT_SET_opintoala}")
    public void setOpintoalaUri(String uri) {
        KOODISTO_OPINTOALA_URI = uri;
    }

    @Value("${koodisto-uris.opintojenLaajuus:NOT_SET_opintojenLaajuus}")
    public void setOpintojenLaajuusUri(String uri) {
        KOODISTO_OPINTOJEN_LAAJUUS_URI = uri;
    }

    @Value("${koodisto-uris.opintojenLaajuusyksikko:NOT_SET_opintojenLaajuusyksikko}")
    public void setOpintojenLaajuusyksikkoUri(String uri) {
        KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI = uri;
    }

    @Value("${koodisto-uris.tavoitteet:NOT_SET_tavoitteet}")
    public void setOpintojenTavoitteetUri(String uri) {
        KOODISTO_TAVOITTEET_URI = uri;
    }

    @Value("${koodisto-uris.jatkoopintomahdollisuudet:NOT_SET_jatkoopintomahdollisuudet}")
    public void setOpintojenJatkoopintomahdollisuudetUri(String uri) {
        KOODISTO_JATKOOPINTOMAHDOLLISUUDET_URI = uri;
    }

    @Value("${koodisto-uris.pohjakoulutusvaatimus:NOT_SET_pohjakoulutusvaatimus}")
    public void setPohjakoulutusvaatimuksetUri(String uri) {
        KOODISTO_POHJAKOULUTUSVAATIMUKSET_URI = uri;
    }

    @Value("${koodisto-uris.postinumero:NOT_SET_postinumero}")
    public void setPostinumero(String postinumero) {
        KOODISTO_POSTINUMERO_URI = postinumero;
    }

    @Value("${koodisto-uris.liitteentyyppi:NOT_SET_liitteentyyppi}")
    public void setLiitteenTyyppi(String liitteenTyyppi) {
        KOODISTO_LIITTEEN_TYYPPI_URI = liitteenTyyppi;
    }

    @Value("${koodisto-uris.valintaperustekuvausryhma:NOT_SET_valintaperustekuvausryhma}")
    public void setValintaperustekuvausryhma(String valintaperustekuvausryhma) {
        KOODISTO_VALINTAPERUSTEKUVAUSRYHMA_URI = valintaperustekuvausryhma;
    }

    @Value("${koodisto-uris.sorakuvausryhma:NOT_SET_sorakuvausryhma}")
    public void setSoraKuvausryhma(String soraKuvausryhma) {
        KOODISTO_SORA_KUVAUSRYHMA_URI = soraKuvausryhma;
    }

    @Value("${koodisto-uris.lukiolinja:NOT_SET_lukiolinja}")
    public void setLukiolinja(String lukiolinja) {
        KOODISTO_LUKIOLINJA_URI = lukiolinja;
    }

    @Value("${koodi-uri.lukio.pohjakoulutusvaatimus:NOT_SET_pohjakoulutusvaatimus}")
    public void setLukioKoodiPohjakoulutusvaatimus(String pohjakoulutusvaatimus) {
        LUKIO_KOODI_POHJAKOULUTUSVAATIMUS_URI = pohjakoulutusvaatimus;
    }

    @Value("${koodi-uri.lukio.kolutuslaji:NOT_SET_kolutuslaji}")
    public void setLukioKoodiKoulutuslaji(String koulutuslaji) {
        LUKIO_KOODI_KOULUTUSLAJI_URI = koulutuslaji;
    }

    @Value("${koodisto-uris.oppiaineet:NOT_SET_oppiaineet}")
    public void setOppiaineet(String oppiaineet) {
        KOODISTO_OPPIAINEET_URI = oppiaineet;
    }


    @Value("${koodisto-uris.lukiodiplomit:NOT_SET_lukiodiplomit}")
    public void setLukiodiplomit(String lukiodiplomit) {
        KOODISTO_LUKIODIPLOMIT_URI = lukiodiplomit;
    }

}
