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
package fi.vm.sade.tarjonta.ui.loader.xls.dto;

import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 *
 * @author Jani Wilén
 */
public class Relaatiot5RowDTO extends KoulutusluokitusRowDTO {

    private static final String KOULUTUSASTE_AMMATTILLINEN_KOODI_ARVO = "32";
    private static final String KOULUTUSASTE_LUKIO_AMMATTILLINEN_KOODI_ARVO = "31";
    private String tyyppi;
    /*
     * 2-aste + common data
     */
    ///private String koulutuksenRakenne;
    /// private String tavoitteet;
    //private String jatkoOpinto;
    private String koulutusohjelmanNimi;
    private String koulutusohjelmanKoodiarvo;
    private String tutkintonimike;
    private String tutkintonimikkeenKoodiarvo;
    private String tutkinnonNimi;
    private String koulutusaste;
    private String koulutusasteenKoodiarvo;
    private String laajuus;
    private String laajuusyksikko;
    private String eqf;
    private String koulutusohjelmanTavoitteet;
    private List<String> oppilaitostyyppis;
    /*
     * Lukio data
     */
    private String lukiolinjaNimi;
    private String lukiolinjaKoodiarvo;
    /*
     * Koulutusohjelman lisatiedot
     */
    private KuvausDTO tutkinnonKuvaukset;
    private KuvausDTO koulutusohjelmanKuvaukset;

//    /**
//     * @return the koulutuksenRakenne
//     */
//    public String getKoulutuksenRakenne() {
//        return koulutuksenRakenne;
//    }
//
//    /**
//     * @param koulutuksenRakenne the koulutuksenRakenne to set
//     */
//    public void setKoulutuksenRakenne(String koulutuksenRakenne) {
//        this.koulutuksenRakenne = koulutuksenRakenne;
//    }
//
//    /**
//     * @return the tavoitteet
//     */
//    public String getTavoitteet() {
//        return tavoitteet;
//    }
//
//    /**
//     * @param tavoitteet the tavoitteet to set
//     */
//    public void setTavoitteet(String tavoitteet) {
//        this.tavoitteet = tavoitteet;
//    }
//
//    /**
//     * @return the jatkoOpinto
//     */
//    public String getJatkoOpinto() {
//        return jatkoOpinto;
//    }
//
//    /**
//     * @param jatkoOpinto the jatkoOpinto to set
//     */
//    public void setJatkoOpinto(String jatkoOpinto) {
//        this.jatkoOpinto = jatkoOpinto;
//    }
    /**
     * @return the koulutusohjelmanNimi
     */
    public String getKoulutusohjelmanNimi() {
        return koulutusohjelmanNimi;
    }

    /**
     * @param koulutusohjelmanNimi the koulutusohjelmanNimi to set
     */
    public void setKoulutusohjelmanNimi(String koulutusohjelmanNimi) {
        this.koulutusohjelmanNimi = koulutusohjelmanNimi;
    }

    /**
     * @return the koulutusohjelmanKoodiarvo
     */
    public String getKoulutusohjelmanKoodiarvo() {
        return koulutusohjelmanKoodiarvo;
    }

    /**
     * @param koulutusohjelmanKoodiarvo the koulutusohjelmanKoodiarvo to set
     */
    public void setKoulutusohjelmanKoodiarvo(String koulutusohjelmanKoodiarvo) {
        this.koulutusohjelmanKoodiarvo = koulutusohjelmanKoodiarvo;
    }

    /**
     * @return the tutkintonimike
     */
    public String getTutkintonimike() {
        return tutkintonimike;
    }

    /**
     * @param tutkintonimike the tutkintonimike to set
     */
    public void setTutkintonimike(String tutkintonimike) {
        this.tutkintonimike = tutkintonimike;
    }

    /**
     * @return the tutkintonimikkeenKoodiarvo
     */
    public String getTutkintonimikkeenKoodiarvo() {
        return tutkintonimikkeenKoodiarvo;
    }

    /**
     * @param tutkintonimikkeenKoodiarvo the tutkintonimikkeenKoodiarvo to set
     */
    public void setTutkintonimikkeenKoodiarvo(String tutkintonimikkeenKoodiarvo) {
        this.tutkintonimikkeenKoodiarvo = tutkintonimikkeenKoodiarvo;
    }

    /**
     * @return the tutkinnonNimi
     */
    public String getTutkinnonNimi() {
        return tutkinnonNimi;
    }

    /**
     * @param tutkinnonNimi the tutkinnonNimi to set
     */
    public void setTutkinnonNimi(String tutkinnonNimi) {
        this.tutkinnonNimi = tutkinnonNimi;
    }

    /**
     * @return the koulutusasteenKoodiarvo
     */
    public String getKoulutusasteenKoodiarvo() {
        return koulutusasteenKoodiarvo;
    }

    /**
     * @param koulutusasteenKoodiarvo the koulutusasteenKoodiarvo to set
     */
    public void setKoulutusasteenKoodiarvo(String koulutusasteenKoodiarvo) {
        this.koulutusasteenKoodiarvo = koulutusasteenKoodiarvo;
    }

    /**
     * @return the laajuus
     */
    public String getLaajuus() {
        return laajuus;
    }

    /**
     * @param laajuus the laajuus to set
     */
    public void setLaajuus(String laajuus) {
        this.laajuus = laajuus;
    }

    /**
     * @return the laajuusyksikko
     */
    public String getLaajuusyksikko() {
        return laajuusyksikko.toUpperCase();
    }

    /**
     * @param laajuusyksikko the laajuusyksikko to set
     */
    public void setLaajuusyksikko(String laajuusyksikko) {
        this.laajuusyksikko = laajuusyksikko;
    }

    /**
     * @return the eqf
     */
    public String getEqf() {
        return eqf;
    }

    /**
     * @param eqf the eqf to set
     */
    public void setEqf(String eqf) {
        this.eqf = eqf;
    }

    /**
     * @return the koulutusaste
     */
    public String getKoulutusaste() {
        return koulutusaste;
    }

    /**
     * @param koulutusaste the koulutusaste to set
     */
    public void setKoulutusaste(String koulutusaste) {
        this.koulutusaste = koulutusaste;
    }
    //Toisen_asteen_tutkinnot/321101
    private static final String SEPARATOR = "/";
    private static final String VERSION = "#1";

    public String getLaajuusyksikkoUri() {
        return laajuusyksikko.toUpperCase() + VERSION;
    }

    public String getLaajuusUri() {
        return laajuus + VERSION;
    }

    public String getEqfUri() {
        return getEqf() + VERSION;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Relaatiot5RowDTO other = (Relaatiot5RowDTO) obj;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(koulutuskoodiKoodiarvo, other.koulutuskoodiKoodiarvo).
                append(koulutusohjelmanKoodiarvo, other.koulutusohjelmanKoodiarvo);

        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(koulutuskoodiKoodiarvo)
                .append(koulutusohjelmanKoodiarvo)
                .append(lukiolinjaKoodiarvo).toHashCode();
    }

    public String getKoulutusohjelmanTavoitteet() {
        return koulutusohjelmanTavoitteet;
    }

    public void setKoulutusohjelmanTavoitteet(String koulutusohjelmanTavoitteet) {
        this.koulutusohjelmanTavoitteet = koulutusohjelmanTavoitteet;
    }

    /**
     * @return the lukiolinjaNimi
     */
    public String getLukiolinjaNimi() {
        return lukiolinjaNimi;
    }

    /**
     * @param lukiolinjaNimi the lukiolinjaNimi to set
     */
    public void setLukiolinjaNimi(String lukiolinjaNimi) {
        this.lukiolinjaNimi = lukiolinjaNimi;
    }

    /**
     * @return the lukiolinjaKoodiarvo
     */
    public String getLukiolinjaKoodiarvo() {
        if (lukiolinjaKoodiarvo != null) {
            //just to be sure that the import excel has 
            //more data than empty string;
            final String trim = lukiolinjaKoodiarvo.trim();

            if (!trim.isEmpty()) {
                return trim;
            } else {
                //no empty strings
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * @param lukiolinjaKoodiarvo the lukiolinjaKoodiarvo to set
     */
    public void setLukiolinjaKoodiarvo(String lukiolinjaKoodiarvo) {
        this.lukiolinjaKoodiarvo = lukiolinjaKoodiarvo;
    }

    public KoulutusasteTyyppi getKoulutusTyyppi() {
        if (koulutusasteenKoodiarvo != null && koulutusasteenKoodiarvo.contains(KOULUTUSASTE_AMMATTILLINEN_KOODI_ARVO)) {
            return KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS;
        } else if (koulutusasteenKoodiarvo != null && koulutusasteenKoodiarvo.contains(KOULUTUSASTE_LUKIO_AMMATTILLINEN_KOODI_ARVO)) {
            return KoulutusasteTyyppi.LUKIOKOULUTUS;
        } else if (!koulutusasteenKoodiarvo.isEmpty()) {
            throw new RuntimeException("No valid KoulutusasteTyyppi enum found by koodi value '" + koulutusaste + "'");
        }

        return null;
    }

    /**
     * @return the tyyppi
     */
    public String getTyyppi() {
        return tyyppi;
    }

    /**
     * @param tyyppi the tyyppi to set
     */
    public void setTyyppi(String tyyppi) {
        this.tyyppi = tyyppi;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    /**
     * @return the oppilaitostyyppis
     */
    public List<String> getOppilaitostyyppis() {
        if (oppilaitostyyppis == null) {
            oppilaitostyyppis = new ArrayList<String>();
        }

        return oppilaitostyyppis;
    }

    /**
     * @param oppilaitostyyppis the oppilaitostyyppis to set
     */
    public void setOppilaitostyyppis(List<String> oppilaitostyyppis) {
        this.oppilaitostyyppis = oppilaitostyyppis;
    }

    /**
     * @return the tutkinnonKuvaukset
     */
    public KuvausDTO getTutkinnonKuvaukset() {
        if (tutkinnonKuvaukset == null) {
            tutkinnonKuvaukset = new KuvausDTO();
        }
        return tutkinnonKuvaukset;
    }

    /**
     * @param tutkinnonKuvaukset the tutkinnonKuvaukset to set
     */
    public void setTutkinnonKuvaukset(KuvausDTO tutkinnonKuvaukset) {

        this.tutkinnonKuvaukset = tutkinnonKuvaukset;
    }

    /**
     * @param koulutusohjelmanKuvaukset the koulutusohjelmanKuvaukset to set
     */
    public void setKoulutusohjelmanKuvaukset(KuvausDTO koulutusohjelmanKuvaukset) {
        this.koulutusohjelmanKuvaukset = koulutusohjelmanKuvaukset;
    }

    /**
     * @return the koulutusohjelmanKuvaukset
     */
    public KuvausDTO getKoulutusohjelmanKuvaukset() {
        if (koulutusohjelmanKuvaukset == null) {
            koulutusohjelmanKuvaukset = new KuvausDTO();
        }

        return koulutusohjelmanKuvaukset;
    }
}
