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

import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusohjelmaModel;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusasteModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
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
     * OID of the loaded enity.
     * Update database
     */
    protected String oid;
    protected String koulutusmoduuliOid;
    /*
     * Status of active form.
     */
    protected DocumentStatus documentStatus;
    protected String userFrienlyDocumentStatus;
    /*
     * Organisaatio data:
     */
    protected String organisaatioName;
    protected String organisaatioOid;
    /*
     * Koodisto logic data:
     */
    protected KoulutusohjelmaModel koulutusohjelmaModel;
    protected KoulutusasteModel koulutusasteModel;
    protected KoulutuskoodiModel koulutuskoodiModel;
    /*
     * KOMO data (static labels):
     */
    protected String koulutuksenTyyppi;
    protected String koulutusala;
    protected String tutkinto;
    protected String tutkintonimike;
    protected String opintojenLaajuusyksikko;
    protected String opintojenLaajuus;
    protected String opintoala;
    //8.11.2012
    protected String koulutuksenRakenne;
    protected String tavoitteet;
    protected String jakoopintomahdollisuudet;
    /*
     * Form fields:
     */
    protected Date koulutuksenAlkamisPvm;
    protected String suunniteltuKesto;
    protected String suunniteltuKestoTyyppi;
    protected String koulutuslaji;
   
    //protected String opetuskieli; //only one language accepted
    
    protected Set<String> opetusmuoto;
    protected Set<String> opetuskielet; //allow one or many
    protected Set<String> avainsanat;
    protected List<KoulutusYhteyshenkiloViewModel> yhteyshenkilot;
    protected List<KoulutusLinkkiViewModel> koulutusLinkit;

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

    /**
     * @return the koulutuslaji
     */
    public String getKoulutuslaji() {
        return koulutuslaji;
    }

    /**
     * @param koulutuslaji the koulutuslaji to set
     */
    public void setKoulutuslaji(String koulutuslaji) {
        this.koulutuslaji = koulutuslaji;
    }

    /**
     * @return the opetusmuoto
     */
    public Set<String> getOpetusmuoto() {
        return opetusmuoto;
    }

    /**
     * @param opetusmuoto the opetusmuoto to set
     */
    public void setOpetusmuoto(Set<String> opetusmuoto) {
        this.opetusmuoto = opetusmuoto;
    }

    /**
     * KOMOTO OID
     *
     * @return the oid
     */
    public String getOid() {
        return oid;
    }

    /**
     * @param oid the oid to set
     */
    public void setOid(String oid) {
        this.oid = oid;
    }

    /**
     * @return the koulutusohjelma
     */
    public KoulutusohjelmaModel getKoulutusohjelmaModel() {
        return koulutusohjelmaModel;
    }

    /**
     * @param koulutusohjelmaModel the koulutusohjelma to set
     */
    public void setKoulutusohjelmaModel(KoulutusohjelmaModel koulutusohjelmaModel) {
        this.koulutusohjelmaModel = koulutusohjelmaModel;
    }

    /**
     * @return the koulutusmoduuliOid
     */
    public String getKoulutusmoduuliOid() {
        return koulutusmoduuliOid;
    }

    /**
     * @param koulutusmoduuliOid the koulutusmoduuliOid to set
     */
    public void setKoulutusmoduuliOid(String koulutusmoduuliOid) {
        this.koulutusmoduuliOid = koulutusmoduuliOid;
    }

    /**
     * @return the koulutusasteModel
     */
    public KoulutusasteModel getKoulutusasteModel() {
        return koulutusasteModel;
    }

    /**
     * @param koulutusasteModel the koulutusasteModel to set
     */
    public void setKoulutusasteModel(KoulutusasteModel koulutusasteModel) {
        this.koulutusasteModel = koulutusasteModel;
    }

    /**
     * @return the koulutuskoodiModel
     */
    public KoulutuskoodiModel getKoulutuskoodiModel() {
        return koulutuskoodiModel;
    }

    /**
     * @param koulutuskoodiModel the koulutuskoodiModel to set
     */
    public void setKoulutuskoodiModel(KoulutuskoodiModel koulutuskoodiModel) {
        this.koulutuskoodiModel = koulutuskoodiModel;
    }

    /**
     * @return the koulutuksenRakenne
     */
    public String getKoulutuksenRakenne() {
        return koulutuksenRakenne;
    }

    /**
     * @param koulutuksenRakenne the koulutuksenRakenne to set
     */
    public void setKoulutuksenRakenne(String koulutuksenRakenne) {
        this.koulutuksenRakenne = koulutuksenRakenne;
    }

    /**
     * @return the tavoitteet
     */
    public String getTavoitteet() {
        return tavoitteet;
    }

    /**
     * @param tavoitteet the tavoitteet to set
     */
    public void setTavoitteet(String tavoitteet) {
        this.tavoitteet = tavoitteet;
    }

    /**
     * @return the jakoopintomahdollisuudet
     */
    public String getJakoopintomahdollisuudet() {
        return jakoopintomahdollisuudet;
    }

    /**
     * @param jakoopintomahdollisuudet the jakoopintomahdollisuudet to set
     */
    public void setJakoopintomahdollisuudet(String jakoopintomahdollisuudet) {
        this.jakoopintomahdollisuudet = jakoopintomahdollisuudet;
    }

}
