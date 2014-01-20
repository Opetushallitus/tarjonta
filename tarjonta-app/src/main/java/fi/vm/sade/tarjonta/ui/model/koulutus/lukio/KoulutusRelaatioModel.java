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
package fi.vm.sade.tarjonta.ui.model.koulutus.lukio;

import com.google.common.base.Preconditions;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.ui.model.BaseUIViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.MonikielinenTekstiModel;

import java.util.Date;

/**
 *
 * @author mlyly
 */
public class KoulutusRelaatioModel extends BaseUIViewModel {

    private static final long serialVersionUID = -3444254965510159782L;
    /**
     * for optimistic locking
     */
    private Long version;
    /*
     * OID of the loaded enity.
     * Update database
     */
    protected String komotoOid;
    protected String koulutusmoduuliOid;
    protected TarjontaTila tila;

    /*
     * Koodisto logic data:
     */
    protected KoulutuskoodiModel koulutuskoodiModel;

    /*
     * From KOMO, koodisto URIs (static labels):
     */
    protected KoodiModel koulutusaste;
    protected KoodiModel koulutusala;
    protected KoodiModel tutkinto;
    protected KoodiModel tutkintonimike;
    protected KoodiModel opintojenLaajuusyksikko;
    protected KoodiModel opintojenLaajuus; //Example : '120', '75' etc.
    protected KoodiModel opintoala;
    protected KoodiModel pohjakoulutusvaatimus;
    protected KoodiModel koulutuslaji;
    
    /*
     * From KOMO, but not koodi URIs (static labels)
     */
   
    /*
     * From KOMO, text data (static labels):
     * TODO:change to KielikaannosViewModel
     */
    protected MonikielinenTekstiModel koulutuksenRakenne;
    protected MonikielinenTekstiModel tavoitteet;
    protected MonikielinenTekstiModel jatkoopintomahdollisuudet;

    /*
     * Other info
     */
    protected String koulutuskoodi; //6-numero koodi arvo
    protected String userKoodiLangUri; //like FI_fi -> kieli_fi
    /*
     * Updated by
     */
    private Date viimeisinPaivitysPvm;
    private String viimeisinPaivittajaOid;

    /**
     * @return the oid
     */
    public String getKomotoOid() {
        return komotoOid;
    }

    /**
     * @param oid the oid to set
     */
    public void setKomotoOid(String oid) {
        this.komotoOid = oid;
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
    public KoodiModel getOpintojenLaajuus() {
        return opintojenLaajuus;
    }

    /**
     * @param opintojenLaajuus the opintojenLaajuus to set
     */
    public void setOpintojenLaajuus(KoodiModel opintojenLaajuus) {
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
     * @return the tila
     */
    public TarjontaTila getTila() {
        return tila;
    }

    /**
     * @param tila the tila to set
     */
    public void setTila(TarjontaTila tila) {
        this.tila = tila;
    }

    /**
     * @return the koulutuslaji
     */
    public KoodiModel getKoulutuslaji() {
        return koulutuslaji;
    }

    /**
     * @param koulutuslaji the koulutuslaji to set
     */
    public void setKoulutuslaji(KoodiModel koulutuslaji) {
        this.koulutuslaji = koulutuslaji;
    }

    /**
     * @return the pohjakoulutusvaatimus
     */
    public KoodiModel getPohjakoulutusvaatimus() {
        return pohjakoulutusvaatimus;
    }

    /**
     * @param pohjakoulutusvaatimus the pohjakoulutusvaatimus to set
     */
    public void setPohjakoulutusvaatimus(KoodiModel pohjakoulutusvaatimus) {
        this.pohjakoulutusvaatimus = pohjakoulutusvaatimus;
    }

    /**
     * Return true, when data was loaded from database.
     *
     * @return Boolean
     */
    public boolean isLoaded() {
        return getKomotoOid() != null;
    }

    /**
     * @return the koulutuskoodi
     */
    public String getKoulutuskoodi() {
        return koulutuskoodi;
    }

    /**
     * @param koulutuskoodi the koulutuskoodi to set
     */
    public void setKoulutuskoodi(String koulutuskoodi) {
        this.koulutuskoodi = koulutuskoodi;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * @return the userKoodiLangUri
     */
    public String getUserKoodiLangUri() {
        return userKoodiLangUri;
    }

    /**
     * @param userKoodiLangUri the userKoodiLangUri to set
     */
    public void setUserKoodiLangUri(String userKoodiLangUri) {
        Preconditions.checkNotNull(userKoodiLangUri, "User koodi URI language code cannot be null.");
        Preconditions.checkArgument(!userKoodiLangUri.isEmpty(), "User koodi URI language code cannot be an empty string.");

        this.userKoodiLangUri = userKoodiLangUri;
    }
}
