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
     * Lukio tuktinto URIs
     */
    public static String KOODISTO_LUKIOLINJA_URI;
    public static String LUKIO_KOODI_KOULUTUSLAJI_URI;
    public static String LUKIO_KOODI_POHJAKOULUTUSVAATIMUS_URI;
    /**
     * Oppiaineet
     */
    public static String KOODISTO_OPPIAINEET_URI;

    @Value("${koodisto-uris.hakutyyppi:NOT_SET}")
    public void setKoodistoHakutyyppiUri(String uri) {
        KOODISTO_HAKUTYYPPI_URI = uri;
    }

    @Value("${koodisto-uris.kieli:NOT_SET}")
    public void setKoodistoKieliUri(String uri) {
        KOODISTO_KIELI_URI = uri;
    }

    @Value("${koodisto-uris.hakutapa:NOT_SET}")
    public void setKoodistoHakutapaUri(String uri) {
        KOODISTO_HAKUTAPA_URI = uri;
    }

    @Value("${koodisto-uris.haunKohdejoukko:NOT_SET}")
    public void setKoodistoHaunKohdejoukkoUri(String uri) {
        KOODISTO_HAUN_KOHDEJOUKKO_URI = uri;
    }

    @Value("${koodisto-uris.koulutuksenAlkamiskausi:NOT_SET}")
    public void setKoodistoKoulutuksenAlkamiskausiUri(String uri) {
        KOODISTO_KAUDEN_TARKENNE_URI = uri;
    }

    @Value("${koodisto-uris.alkamiskausi:NOT_SET}")
    public void setKoodistoAlkamiskausiUri(String uri) {
        KOODISTO_ALKAMISKAUSI_URI = uri;
    }

    @Value("${koodisto-uris.oppilaitostyyppi:NOT_SET}")
    public void setKoodistoOppilaitosTyyppiUri(String uri) {
        KOODISTO_OPPILAITOSTYYPPI_URI = uri;
    }

    @Value("${koodisto-uris.hakukelpoisuusVaatimukset:NOT_SET}")
    public void setKoodistoHakukelpoisuusVaatimuksetUri(String uri) {
        KOODISTO_HAKUKELPOISUUS_VAATIMUKSET_URI = uri;
    }

    @Value("${koodisto-uris.hakukohde:NOT_SET}")
    public void setKoodistoHakukohdeUri(String uri) {
        KOODISTO_HAKUKOHDE_URI = uri;
    }

    @Value("${koodisto-uris.suunniteltuKesto:NOT_SET}")
    public void setKoodistoSuunniteltuKestoUri(String uri) {
        KOODISTO_SUUNNITELTU_KESTO_URI = uri;
    }

    @Value("${koodisto-uris.opetusmuoto:NOT_SET}")
    public void setKoodistoOpetusmuotoUri(String uri) {
        KOODISTO_OPETUSMUOTO_URI = uri;
    }

    @Value("${koodisto-uris.koulutuslaji:NOT_SET}")
    public void setKoodistoKoulutuslajiUri(String uri) {
        KOODISTO_KOULUTUSLAJI_URI = uri;
    }

    @Value("${koodisto-uris.avainsanat:NOT_SET}")
    public void setKoodistoAvainsanatUri(String uri) {
        KOODISTO_AVAINSANAT_URI = uri;
    }

    @Value("${koodisto-uris.kielivalikoima:NOT_SET}")
    public void setKoodistoKielivalikoimaUri(String uri) {
        KOODISTO_KIELIVALIKOIMA_URI = uri;
    }

    @Value("${koodisto-uris.ammattinimikkeet:NOT_SET}")
    public void setAmmattinimikkeetUri(String uri) {
        KOODISTO_AMMATTINIMIKKEET_URI = uri;
    }

    /*
     * 
     * KOMO URIs
     * 
     */
    @Value("${koodisto-uris.tutkinto:NOT_SET}")
    public void setKoodistoTutkintoUri(String uri) {
        KOODISTO_TUTKINTO_URI = uri;
    }

    @Value("${koodisto-uris.koulutusohjelma:NOT_SET}")
    public void setKoodistoKoulutusohjelmaUri(String uri) {
        KOODISTO_KOULUTUSOHJELMA_URI = uri;
    }

    @Value("${koodisto-uris.koulutusaste:NOT_SET}")
    public void setKoodistoasteUri(String uri) {
        KOODISTO_KOULUTUSASTE_URI = uri;
    }

    @Value("${koodisto-uris.koulutusala:NOT_SET}")
    public void setKoulutusalaUri(String uri) {
        KOODISTO_KOULUTUSALA_URI = uri;
    }

    @Value("${koodisto-uris.tutkintonimike:NOT_SET}")
    public void setTutkintonimikeUri(String uri) {
        KOODISTO_TUTKINTONIMIKE_URI = uri;
    }

    @Value("${koodisto-uris.koulutuksenRakenne:NOT_SET}")
    public void setKoulutuksenRakenneUri(String uri) {
        KOODISTO_KOULUTUKSEN_RAKENNE_URI = uri;
    }

    @Value("${koodisto-uris.opintoala:NOT_SET}")
    public void setOpintoalaUri(String uri) {
        KOODISTO_OPINTOALA_URI = uri;
    }

    @Value("${koodisto-uris.opintojenLaajuus:NOT_SET}")
    public void setOpintojenLaajuusUri(String uri) {
        KOODISTO_OPINTOJEN_LAAJUUS_URI = uri;
    }

    @Value("${koodisto-uris.opintojenLaajuusyksikko:NOT_SET}")
    public void setOpintojenLaajuusyksikkoUri(String uri) {
        KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI = uri;
    }

    @Value("${koodisto-uris.tavoitteet:NOT_SET}")
    public void setOpintojenTavoitteetUri(String uri) {
        KOODISTO_TAVOITTEET_URI = uri;
    }

    @Value("${koodisto-uris.jatkoopintomahdollisuudet:NOT_SET}")
    public void setOpintojenJatkoopintomahdollisuudetUri(String uri) {
        KOODISTO_JATKOOPINTOMAHDOLLISUUDET_URI = uri;
    }

    @Value("${koodisto-uris.pohjakoulutusvaatimus:NOT_SET}")
    public void setPohjakoulutusvaatimuksetUri(String uri) {
        KOODISTO_POHJAKOULUTUSVAATIMUKSET_URI = uri;
    }

    @Value("${koodisto-uris.postinumero:NOT_SET}")
    public void setPostinumero(String postinumero) {
        KOODISTO_POSTINUMERO_URI = postinumero;
    }

    @Value("${koodisto-uris.liitteentyyppi:NOT_SET}")
    public void setLiitteenTyyppi(String liitteenTyyppi) {
        KOODISTO_LIITTEEN_TYYPPI_URI = liitteenTyyppi;
    }

    @Value("${koodisto-uris.valintaperustekuvausryhma:NOT_SET}")
    public void setValintaperustekuvausryhma(String valintaperustekuvausryhma) {
        KOODISTO_VALINTAPERUSTEKUVAUSRYHMA_URI = valintaperustekuvausryhma;
    }

    @Value("${koodisto-uris.sorakuvausryhma:NOT_SET}")
    public void setSoraKuvausryhma(String soraKuvausryhma) {
        KOODISTO_SORA_KUVAUSRYHMA_URI = soraKuvausryhma;
    }

    @Value("${koodisto-uris.lukiolinja:NOT_SET}")
    public void setLukiolinja(String lukiolinja) {
        KOODISTO_LUKIOLINJA_URI = lukiolinja;
    }

    @Value("${koodi-uri.lukio.pohjakoulutusvaatimus:NOT_SET}")
    public void setLukioKoodiPohjakoulutusvaatimus(String pohjakoulutusvaatimus) {
        LUKIO_KOODI_POHJAKOULUTUSVAATIMUS_URI = pohjakoulutusvaatimus;
    }

    @Value("${koodi-uri.lukio.kolutuslaji:NOT_SET}")
    public void setLukioKoodiKoulutuslaji(String koulutuslaji) {
        LUKIO_KOODI_KOULUTUSLAJI_URI = koulutuslaji;
    }

    @Value("${koodisto-uris.oppiaineet:NOT_SET}")
    public void setOppiaineet(String oppiaineet) {
        KOODISTO_OPPIAINEET_URI = oppiaineet;
    }
}
