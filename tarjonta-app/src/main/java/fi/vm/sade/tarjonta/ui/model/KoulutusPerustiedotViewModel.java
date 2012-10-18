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
package fi.vm.sade.tarjonta.ui.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 *
 * @author mlyly
 */
public class KoulutusPerustiedotViewModel extends BaseUIViewModel {

    /*
     * Koodisto data
     */
    private String koulutusKoodi;
    private String koulutusohjelmaKoodi;
    /*
     * KOMO data 
     */
    private String koulutuksenTyyppi = "";
    private String koulutusala = "";
    private String tutkinto = "";
    private String tutkintonimike = "";
    private String opintojenLaajuusyksikko = "";
    private String opintojenLaajuus = "";
    private String opintoala = "";
    // Koodisto: kieli
    private Set<String> opetuskielet = new HashSet<String>();
    private boolean opetuskieletKaikki;
    private Date koulutuksenAlkamisPvm = new Date();
    private String suunniteltuKesto;
    // Koodisto: suunniteltuKesto
    private String suunniteltuKestoTyyppi;
    // Koodisto: teema
    private Set<String> avainsanat = new HashSet<String>();
    // Koodisto: opetusmuoto
    private String opetusmuoto;
    // Koodisto: koulutuslaji
    private String koulutuslaji;
    private List<KoulutusYhteyshenkiloViewModel> yhteyshenkilot = new ArrayList<KoulutusYhteyshenkiloViewModel>(0);
    private boolean koulutusOnMaksullista;
    private boolean koulutusStipendiMahdollisuus;
    private List<KoulutusLinkkiViewModel> koulutusLinkit = new ArrayList<KoulutusLinkkiViewModel>(0);

    public String getKoulutusala() {
        return koulutusala;
    }

    public void setKoulutusala(String koulutusala) {
        this.koulutusala = koulutusala;
    }

    public String getTutkinto() {
        return tutkinto;
    }

    public void setTutkinto(String tutkinto) {
        this.tutkinto = tutkinto;
    }

    public String getTutkintonimike() {
        return tutkintonimike;
    }

    public void setTutkintonimike(String tutkintonimike) {
        this.tutkintonimike = tutkintonimike;
    }

    public String getOpintojenLaajuusyksikko() {
        return opintojenLaajuusyksikko;
    }

    public void setOpintojenLaajuusyksikko(String opintojenLaajuusyksikko) {
        this.opintojenLaajuusyksikko = opintojenLaajuusyksikko;
    }

    public String getOpintojenLaajuus() {
        return opintojenLaajuus;
    }

    public void setOpintojenLaajuus(String opintojenLaajuus) {
        this.opintojenLaajuus = opintojenLaajuus;
    }

    public String getOpintoala() {
        return opintoala;
    }

    public void setOpintoala(String opintoala) {
        this.opintoala = opintoala;
    }

    public Set<String> getOpetuskielet() {
        return opetuskielet;
    }

    public void setOpetuskielet(Set<String> opetuskielet) {
        this.opetuskielet = opetuskielet;
    }

    public boolean isOpetuskieletKaikki() {
        return opetuskieletKaikki;
    }

    public void setOpetuskieletKaikki(boolean opetuskieletKaikki) {
        this.opetuskieletKaikki = opetuskieletKaikki;
    }

    public Date getKoulutuksenAlkamisPvm() {
        return koulutuksenAlkamisPvm;
    }

    public void setKoulutuksenAlkamisPvm(Date koulutuksenAlkamisPvm) {
        this.koulutuksenAlkamisPvm = koulutuksenAlkamisPvm;
    }

    public String getSuunniteltuKesto() {
        return suunniteltuKesto;
    }

    public void setSuunniteltuKesto(String suunniteltuKesto) {
        this.suunniteltuKesto = suunniteltuKesto;
    }

    public String getSuunniteltuKestoTyyppi() {
        return suunniteltuKestoTyyppi;
    }

    public void setSuunniteltuKestoTyyppi(String suunniteltuKestoTyyppi) {
        this.suunniteltuKestoTyyppi = suunniteltuKestoTyyppi;
    }


    public String getOpetusmuoto() {
        return opetusmuoto;
    }

    public void setOpetusmuoto(String opetusmuoto) {
        this.opetusmuoto = opetusmuoto;
    }

    public String getKoulutuslaji() {
        return koulutuslaji;
    }

    public void setKoulutuslaji(String koulutuslaji) {
        this.koulutuslaji = koulutuslaji;
    }

    public boolean isKoulutusOnMaksullista() {
        return koulutusOnMaksullista;
    }

    public void setKoulutusOnMaksullista(boolean koulutusOnMaksullista) {
        this.koulutusOnMaksullista = koulutusOnMaksullista;
    }

    public boolean isKoulutusStipendiMahdollisuus() {
        return koulutusStipendiMahdollisuus;
    }

    public void setKoulutusStipendiMahdollisuus(boolean koulutusStipendiMahdollisuus) {
        this.koulutusStipendiMahdollisuus = koulutusStipendiMahdollisuus;
    }

    public List<KoulutusLinkkiViewModel> getKoulutusLinkit() {
        if (koulutusLinkit == null) {
            koulutusLinkit = new ArrayList<KoulutusLinkkiViewModel>();
        }
        return koulutusLinkit;
    }

    public void setKoulutusLinkit(List<KoulutusLinkkiViewModel> linkit) {
        this.koulutusLinkit = linkit;
    }

    public List<KoulutusYhteyshenkiloViewModel> getYhteyshenkilot() {
        if (yhteyshenkilot == null) {
            yhteyshenkilot = new ArrayList<KoulutusYhteyshenkiloViewModel>();
        }
        return yhteyshenkilot;
    }

    public void setYhteyshenkilot(List<KoulutusYhteyshenkiloViewModel> _yhteyshenkilot) {
        this.yhteyshenkilot = _yhteyshenkilot;
    }

    /**
     * @return the koulutuksenTyyppi
     */
    public String getKoulutuksenTyyppi() {
        return koulutuksenTyyppi;
    }

    /**
     * @param koulutuksenTyyppi the koulutuksenTyyppi to set
     */
    public void setKoulutuksenTyyppi(String koulutuksenTyyppi) {
        this.koulutuksenTyyppi = koulutuksenTyyppi;
    }

    /**
     * @return the koulutusKoodi
     */
    public String getKoulutusKoodi() {
        return koulutusKoodi;
    }

    /**
     * @param koulutusKoodi the koulutusKoodi to set
     */
    public void setKoulutusKoodi(String koulutusKoodi) {
        this.koulutusKoodi = koulutusKoodi;
    }

    /**
     * @return the koulutusohjelmaKoodi
     */
    public String getKoulutusohjelmaKoodi() {
        return koulutusohjelmaKoodi;
    }

    /**
     * @param koulutusohjelmaKoodi the koulutusohjelmaKoodi to set
     */
    public void setKoulutusohjelmaKoodi(String koulutusohjelmaKoodi) {
        this.koulutusohjelmaKoodi = koulutusohjelmaKoodi;
    }

    /**
     * @return the avainsanat
     */
    public Set<String> getAvainsanat() {
        return avainsanat;
    }

    /**
     * @param avainsanat the avainsanat to set
     */
    public void setAvainsanat(Set<String> avainsanat) {
        this.avainsanat = avainsanat;
    }
}
