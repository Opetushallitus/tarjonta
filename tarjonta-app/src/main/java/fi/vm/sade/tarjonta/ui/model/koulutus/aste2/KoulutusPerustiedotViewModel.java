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
package fi.vm.sade.tarjonta.ui.model.koulutus.aste2;

import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusohjelmaModel;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.model.BaseUIViewModel;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusLinkkiViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusYhteyshenkiloViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.MonikielinenTekstiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.LukiolinjaModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author mlyly
 */
public class KoulutusPerustiedotViewModel extends BaseUIViewModel {
    private static final long serialVersionUID = 8039462177057261652L;

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
     * Koodisto logic data:
     */
    protected KoulutusohjelmaModel koulutusohjelmaModel;
    protected KoulutuskoodiModel koulutuskoodiModel;
    private LukiolinjaModel lukiolinjaModel;
    /*
     * KOMO koodisto URIs (static labels):
     */
    protected KoodiModel koulutusaste;
    protected KoodiModel koulutuksenTyyppi;
    protected KoodiModel koulutusala;
    protected KoodiModel tutkinto;
    protected KoodiModel tutkintonimike;
    protected KoodiModel opintojenLaajuusyksikko;
    protected String opintojenLaajuus;
    protected KoodiModel opintoala;
    
    protected String opintojenLaajuusTot;

    protected String opintojenLaajuusyksikkoTot;
    
    /*
     * KOMO text data (static labels):
     * TODO:change to KielikaannosViewModel
     */
    protected MonikielinenTekstiModel koulutuksenRakenne;
    protected MonikielinenTekstiModel tavoitteet;
    private MonikielinenTekstiModel jatkoopintomahdollisuudet;
    /*
     * Form fields:
     */
    protected String pohjakoulutusvaatimus;
    protected Date koulutuksenAlkamisPvm;
    protected String suunniteltuKesto;
    protected String suunniteltuKestoTyyppi;
    protected String koulutuslaji;
    protected String opetusmuoto;
    protected String opetuskieli;
    private List<KielikaannosViewModel> painotus;
    protected List<KoulutusYhteyshenkiloViewModel> yhteyshenkilot;
    protected List<KoulutusLinkkiViewModel> koulutusLinkit;

    private Date viimeisinPaivitysPvm;
    private String viimeisinPaivittajaOid;

    public KoulutusPerustiedotViewModel() {
        this.documentStatus = DocumentStatus.NEW;
    }

    public KoulutusPerustiedotViewModel(DocumentStatus status) {
        this.documentStatus = status;
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
    public KoodiModel getKoulutuksenTyyppi() {
        return koulutuksenTyyppi;
    }

    /**
     * @param koulutuksenTyyppi the koulutuksenTyyppi to set
     */
    public void setKoulutuksenTyyppi(KoodiModel koulutuksenTyyppi) {
        this.koulutuksenTyyppi = koulutuksenTyyppi;
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
    public String getOpetusmuoto() {
        return opetusmuoto;
    }

    /**
     * @param opetusmuoto the opetusmuoto to set
     */
    public void setOpetusmuoto(String opetusmuoto) {
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
    public MonikielinenTekstiModel getKoulutuksenRakenne() {
        return koulutuksenRakenne;
    }

    /**
     * @param koulutuksenRakenne the koulutuksenRakenne to set
     */
    public void setKoulutuksenRakenne(MonikielinenTekstiModel koulutuksenRakenne) {
        this.koulutuksenRakenne = koulutuksenRakenne;
    }

    /**
     * @return the tavoitteet
     */
    public MonikielinenTekstiModel getTavoitteet() {
        return tavoitteet;
    }

    /**
     * @param tavoitteet the tavoitteet to set
     */
    public void setTavoitteet(MonikielinenTekstiModel tavoitteet) {
        this.tavoitteet = tavoitteet;
    }

    /**
     * @return the pohjakoulutusvaatimus
     */
    public String getPohjakoulutusvaatimus() {
        return pohjakoulutusvaatimus;
    }

    /**
     * @param pohjakoulutusvaatimus the pohjakoulutusvaatimus to set
     */
    public void setPohjakoulutusvaatimus(String pohjakoulutusvaatimus) {
        this.pohjakoulutusvaatimus = pohjakoulutusvaatimus;
    }

    /**
     * @return the koulutusala
     */
    public KoodiModel getKoulutusala() {
        return koulutusala;
    }

    /**
     * @param koulutusala the koulutusala to set
     */
    public void setKoulutusala(KoodiModel koulutusala) {
        this.koulutusala = koulutusala;
    }

    /**
     * @return the tutkinto
     */
    public KoodiModel getTutkinto() {
        return tutkinto;
    }

    /**
     * @param tutkinto the tutkinto to set
     */
    public void setTutkinto(KoodiModel tutkinto) {
        this.tutkinto = tutkinto;
    }

    /**
     * @return the tutkintonimike
     */
    public KoodiModel getTutkintonimike() {
        return tutkintonimike;
    }

    /**
     * @param tutkintonimike the tutkintonimike to set
     */
    public void setTutkintonimike(KoodiModel tutkintonimike) {
        this.tutkintonimike = tutkintonimike;
    }

    /**
     * @return the opintojenLaajuusyksikko
     */
    public KoodiModel getOpintojenLaajuusyksikko() {
        return opintojenLaajuusyksikko;
    }

    /**
     * @param opintojenLaajuusyksikko the opintojenLaajuusyksikko to set
     */
    public void setOpintojenLaajuusyksikko(KoodiModel opintojenLaajuusyksikko) {
        this.opintojenLaajuusyksikko = opintojenLaajuusyksikko;
    }

    /**
     * @return the opintojenLaajuus
     */
    public String getOpintojenLaajuus() {
        return opintojenLaajuus;
    }

    /**
     * @param opintojenLaajuus the opintojenLaajuus to set
     */
    public void setOpintojenLaajuus(String opintojenLaajuus) {
        this.opintojenLaajuus = opintojenLaajuus;
    }

    /**
     * @return the opintoala
     */
    public KoodiModel getOpintoala() {
        return opintoala;
    }

    /**
     * @param opintoala the opintoala to set
     */
    public void setOpintoala(KoodiModel opintoala) {
        this.opintoala = opintoala;
    }

    /**
     * @return the koulutusaste
     */
    public KoodiModel getKoulutusaste() {
        return koulutusaste;
    }

    /**
     * @param koulutusaste the koulutusaste to set
     */
    public void setKoulutusaste(KoodiModel koulutusaste) {
        this.koulutusaste = koulutusaste;
    }

    /**
     * @return the painotus
     */
    public List<KielikaannosViewModel> getPainotus() {
        return painotus;
    }

    /**
     * @param painotus the painotus to set
     */
    public void setPainotus(List<KielikaannosViewModel> painotus) {
        this.painotus = painotus;
    }

    /**
     * @return the jatkoopintomahdollisuudet
     */
    public MonikielinenTekstiModel getJatkoopintomahdollisuudet() {
        return jatkoopintomahdollisuudet;
    }

    /**
     * @param jatkoopintomahdollisuudet the jatkoopintomahdollisuudet to set
     */
    public void setJatkoopintomahdollisuudet(MonikielinenTekstiModel jatkoopintomahdollisuudet) {
        this.jatkoopintomahdollisuudet = jatkoopintomahdollisuudet;
    }

    /**
     * @return the opetuskieli
     */
    public String getOpetuskieli() {
        return opetuskieli;
    }

    /**
     * @param opetuskieli the opetuskieli to set
     */
    public void setOpetuskieli(String opetuskieli) {
        this.opetuskieli = opetuskieli;
    }

    /**
     * @return the lukiolinjaModel
     */
    public LukiolinjaModel getLukiolinjaModel() {
        return lukiolinjaModel;
    }

    /**
     * @param lukiolinjaModel the lukiolinjaModel to set
     */
    public void setLukiolinjaModel(LukiolinjaModel lukiolinjaModel) {
        this.lukiolinjaModel = lukiolinjaModel;
    }

    public Date getViimeisinPaivitysPvm() {
        return viimeisinPaivitysPvm;
    }

    public void setViimeisinPaivitysPvm(Date viimeisinPaivitysPvm) {
        this.viimeisinPaivitysPvm = viimeisinPaivitysPvm;
    }

    public String getViimeisinPaivittajaOid() {
        return viimeisinPaivittajaOid;
    }

    public void setViimeisinPaivittajaOid(String viimeisinPaivittajaOid) {
        this.viimeisinPaivittajaOid = viimeisinPaivittajaOid;
    }
    
    public String getOpintojenLaajuusTot() {
        return opintojenLaajuusTot;
    }

    public void setOpintojenLaajuusTot(String opintojenLaajuusTot) {
        this.opintojenLaajuusTot = opintojenLaajuusTot;
    }

    public String getOpintojenLaajuusyksikkoTot() {
        return opintojenLaajuusyksikkoTot;
    }

    public void setOpintojenLaajuusyksikkoTot(String opintojenLaajuusyksikkoTot) {
        this.opintojenLaajuusyksikkoTot = opintojenLaajuusyksikkoTot;
    }

}
