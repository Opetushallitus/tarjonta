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

/**
 *
 * @author mlyly
 */
public class KoulutusPerustiedotViewModel extends BaseUIViewModel {
    // Koodisto: koulutus
    private String _koulutus;
    // TODO Mistä nämä tulevat?
    private String _koulutusTyyppi;
    private String _koulutusala = "";
    private String _tutkinto = "";
    private String _tutkintonimike = "";
    private String _opintojenlaajuusyksikko = "";
    private String _opintojenlaajuus = "";
    private String _opintoala = "";
    // Koodisto: kieli
    private Set<String> _opetuskielet = new HashSet<String>();
    private boolean _opetuskieletKaikki;
    private Date _koulutuksenAlkamisPvm = new Date();
    private String _suunniteltuKesto;
    // Koodisto: suunniteltuKesto
    private String _suunniteltuKestoTyyppi;
    // Koodisto: teema
    private Set<String> _teemat = new HashSet<String>();
    // Koodisto: opetusmuoto
    private String _opetusmuoto;
    // Koodisto: koulutuslaji
    private String _koulutuslaji;

    private List<KoulutusYhteyshenkiloViewModel> _yhteyshenkilot;
    private boolean _koulutusOnMaksullista;
    private boolean _koulutusStipendiMahdollisuus;
    private List<KoulutusLinkkiViewModel> _koulutusLinkit;

    public String getKoulutus() {
        return _koulutus;
    }

    public void setKoulutus(String koulutus) {
        this._koulutus = koulutus;
    }

    public String getKoulutusTyyppi() {
        return _koulutusTyyppi;
    }

    public void setKoulutusTyyppi(String _koulutusTyyppi) {
        this._koulutusTyyppi = _koulutusTyyppi;
    }

    public String getKoulutusala() {
        return _koulutusala;
    }

    public void setKoulutusala(String koulutusala) {
        this._koulutusala = koulutusala;
    }

    public String getTutkinto() {
        return _tutkinto;
    }

    public void setTutkinto(String tutkinto) {
        this._tutkinto = tutkinto;
    }

    public String getTutkintonimike() {
        return _tutkintonimike;
    }

    public void setTutkintonimike(String tutkintonimike) {
        this._tutkintonimike = tutkintonimike;
    }

    public String getOpintojenlaajuusyksikko() {
        return _opintojenlaajuusyksikko;
    }

    public void setOpintojenlaajuusyksikko(String opintojenlaajuusyksikko) {
        this._opintojenlaajuusyksikko = opintojenlaajuusyksikko;
    }

    public String getOpintojenlaajuus() {
        return _opintojenlaajuus;
    }

    public void setOpintojenlaajuus(String opintojenlaajuus) {
        this._opintojenlaajuus = opintojenlaajuus;
    }

    public String getOpintoala() {
        return _opintoala;
    }

    public void setOpintoala(String opintoala) {
        this._opintoala = opintoala;
    }

    public Set<String> getOpetuskielet() {
        return _opetuskielet;
    }

    public void setOpetuskielet(Set<String> opetuskielet) {
        this._opetuskielet = opetuskielet;
    }

    public boolean isOpetuskieletKaikki() {
        return _opetuskieletKaikki;
    }

    public void setOpetuskieletKaikki(boolean opetuskieletKaikki) {
        this._opetuskieletKaikki = opetuskieletKaikki;
    }

    public Date getKoulutuksenAlkamisPvm() {
        return _koulutuksenAlkamisPvm;
    }

    public void setKoulutuksenAlkamisPvm(Date koulutuksenAlkamisPvm) {
        this._koulutuksenAlkamisPvm = koulutuksenAlkamisPvm;
    }

    public String getSuunniteltuKesto() {
        return _suunniteltuKesto;
    }

    public void setSuunniteltuKesto(String suunniteltuKesto) {
        this._suunniteltuKesto = suunniteltuKesto;
    }

    public String getSuunniteltuKestoTyyppi() {
        return _suunniteltuKestoTyyppi;
    }

    public void setSuunniteltuKestoTyyppi(String suunniteltuKestoTyyppi) {
        this._suunniteltuKestoTyyppi = suunniteltuKestoTyyppi;
    }

    public Set<String> getTeemat() {
        return _teemat;
    }

    public void setTeemat(Set<String> teemat) {
        this._teemat = teemat;
    }

    public String getOpetusmuoto() {
        return _opetusmuoto;
    }

    public void setOpetusmuoto(String opetusmuoto) {
        this._opetusmuoto = opetusmuoto;
    }

    public String getKoulutuslaji() {
        return _koulutuslaji;
    }

    public void setKoulutuslaji(String koulutuslaji) {
        this._koulutuslaji = koulutuslaji;
    }

    public boolean isKoulutusOnMaksullista() {
        return _koulutusOnMaksullista;
    }

    public void setKoulutusOnMaksullista(boolean koulutusOnMaksullista) {
        this._koulutusOnMaksullista = koulutusOnMaksullista;
    }


    public boolean isKoulutusStipendiMahdollisuus() {
        return _koulutusStipendiMahdollisuus;
    }

    public void setKoulutusStipendiMahdollisuus(boolean koulutusStipendiMahdollisuus) {
        this._koulutusStipendiMahdollisuus = koulutusStipendiMahdollisuus;
    }

    public List<KoulutusLinkkiViewModel> getKoulutusLinkit() {
        if (_koulutusLinkit == null) {
            _koulutusLinkit = new ArrayList<KoulutusLinkkiViewModel>();
        }
        return _koulutusLinkit;
    }

    public void setKoulutusLinkit(List<KoulutusLinkkiViewModel> linkit) {
        this._koulutusLinkit = linkit;
    }

    public List<KoulutusYhteyshenkiloViewModel> getYhteyshenkilot() {
        if (_yhteyshenkilot == null) {
            _yhteyshenkilot = new ArrayList<KoulutusYhteyshenkiloViewModel>();
        }
        return _yhteyshenkilot;
    }

    public void setYhteyshenkilot(List<KoulutusYhteyshenkiloViewModel> _yhteyshenkilot) {
        this._yhteyshenkilot = _yhteyshenkilot;
    }


}
