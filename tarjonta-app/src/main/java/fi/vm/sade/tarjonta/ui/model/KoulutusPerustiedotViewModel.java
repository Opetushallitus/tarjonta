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

import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
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

    /*
     * Status of active form.
     */
    private DocumentStatus documentStatus;
    private String userFrienlyDocumentStatus;
    /*
     * Organisaatio data:
     */
    private String organisaatioName;
    private String organisaatioOid;
    /*
     * Koodisto data:
     */
    private String koulutusKoodi;
    private KoulutusohjelmaModel koulutusohjema;
    /*
     * KOMO data (static labels):
     */
    private String koulutuksenTyyppi = "";
    private String koulutusala = "";
    private String tutkinto = "";
    private String tutkintonimike = "";
    private String opintojenLaajuusyksikko = "";
    private String opintojenLaajuus = "";
    private String opintoala = "";
    /*
     * Form fields:
     */
    private Date koulutuksenAlkamisPvm = new Date();
    private String suunniteltuKesto;
    private String suunniteltuKestoTyyppi;
    private String opetusmuoto;
    private String koulutuslaji;
    private Set<String> opetuskielet = new HashSet<String>(0);
    private Set<String> avainsanat = new HashSet<String>(0);
    private List<KoulutusYhteyshenkiloViewModel> yhteyshenkilot = new ArrayList<KoulutusYhteyshenkiloViewModel>(0);
    private List<KoulutusLinkkiViewModel> koulutusLinkit = new ArrayList<KoulutusLinkkiViewModel>(0);

    public KoulutusPerustiedotViewModel() {
        this.documentStatus = DocumentStatus.NEW;
    }

    public KoulutusPerustiedotViewModel(DocumentStatus status) {
        this.documentStatus = status;
    }

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

    /**
     * @return the koulutusohjema
     */
    public KoulutusohjelmaModel getKoulutusohjema() {
        return koulutusohjema;
    }

    /**
     * @param koulutusohjema the koulutusohjema to set
     */
    public void setKoulutusohjema(KoulutusohjelmaModel koulutusohjema) {
        this.koulutusohjema = koulutusohjema;
    }

    /**
     * @return the document status enum
     */
    public DocumentStatus getDocumentStatus() {
        return documentStatus;
    }

    /**
     * @param status the document status enum to set
     */
    public void setDocumentStatus(DocumentStatus status) {
        this.documentStatus = status;
        this.setUserFrienlyDocumentStatus(status.getStatus());
    }

    /**
     * @return the organisaatioName
     */
    public String getOrganisaatioName() {
        return organisaatioName;
    }

    /**
     * @param organisaatioName the organisaatioName to set
     */
    public void setOrganisaatioName(String organisaatioName) {
        this.organisaatioName = organisaatioName;
    }

    /**
     * @return the organisaatioOid
     */
    public String getOrganisaatioOid() {
        return organisaatioOid;
    }

    /**
     * @param organisaatioOid the organisaatioOid to set
     */
    public void setOrganisaatioOid(String organisaatioOid) {
        this.organisaatioOid = organisaatioOid;
    }

    /**
     * @return the userFrienlyDocumentStatus
     */
    public String getUserFrienlyDocumentStatus() {
        return userFrienlyDocumentStatus;
    }

    /**
     * @param userFrienlyDocumentStatus the userFrienlyDocumentStatus to set
     */
    public void setUserFrienlyDocumentStatus(String userFrienlyDocumentStatus) {
        this.userFrienlyDocumentStatus = userFrienlyDocumentStatus;
    }
}
