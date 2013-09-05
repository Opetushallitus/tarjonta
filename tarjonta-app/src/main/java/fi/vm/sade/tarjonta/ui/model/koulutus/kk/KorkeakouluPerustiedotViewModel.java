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
package fi.vm.sade.tarjonta.ui.model.koulutus.kk;

import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusRelaatioModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.YhteyshenkiloModel;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 *
 * @author Jani Wilén
 */
public class KorkeakouluPerustiedotViewModel extends KoulutusRelaatioModel {

    private static final long serialVersionUID = 664589343157565981L;

    /*
     * Data for comboxes (komo + tutkintoohjelma) 
     */
    private TutkintoohjelmaModel tutkintoohjelma;
    private String tunniste; //tutkinto-ohjelman tunniste
    /*
     * Other user selected form input data
     */
    private Date koulutuksenAlkamisPvm;
    private String suunniteltuKesto;
    private String suunniteltuKestoTyyppi;
    private Set<String> opetuskielis;
    private Set<String> opetusmuodos;
    /*
     * KK
     */
    private Boolean opintojenMaksullisuus;
    private Set<String> pohjakoulutusvaatimukset;
    private Set<String> teemas;
    /*
     * Contact persons
     */
    private YhteyshenkiloModel yhteyshenkilo;
    private YhteyshenkiloModel ectsKoordinaattori;


    public KorkeakouluPerustiedotViewModel() {
        super();
        clearModel();
    }

    /**
     * @return the koulutuksenAlkamisPvm
     */
    public Date getKoulutuksenAlkamisPvm() {
        return koulutuksenAlkamisPvm;
    }

    /**
     * @param koulutuksenAlkamisPvm the koulutuksenAlkamisPvm to set
     */
    public void setKoulutuksenAlkamisPvm(Date koulutuksenAlkamisPvm) {
        this.koulutuksenAlkamisPvm = koulutuksenAlkamisPvm;
    }

    /**
     * @return the suunniteltuKesto
     */
    public String getSuunniteltuKesto() {
        return suunniteltuKesto;
    }

    /**
     * @param suunniteltuKesto the suunniteltuKesto to set
     */
    public void setSuunniteltuKesto(String suunniteltuKesto) {
        this.suunniteltuKesto = suunniteltuKesto;
    }

    /**
     * @return the suunniteltuKestoTyyppi
     */
    public String getSuunniteltuKestoTyyppi() {
        return suunniteltuKestoTyyppi;
    }

    /**
     * @param suunniteltuKestoTyyppi the suunniteltuKestoTyyppi to set
     */
    public void setSuunniteltuKestoTyyppi(String suunniteltuKestoTyyppi) {
        this.suunniteltuKestoTyyppi = suunniteltuKestoTyyppi;
    }

    /**
     * @return the yhteyshenkilo
     */
    public YhteyshenkiloModel getYhteyshenkilo() {
        if (yhteyshenkilo == null) {
            yhteyshenkilo = new YhteyshenkiloModel();
        }

        return yhteyshenkilo;
    }

    /**
     * @param yhteyshenkilo the yhteyshenkilo to set
     */
    public void setYhteyshenkilo(YhteyshenkiloModel yhteyshenkilo) {
        this.yhteyshenkilo = yhteyshenkilo;
    }

    /**
     * Initialize model with all default values.
     *
     * @param status of koulutus document
     */
    public void clearModel() {

        /*
         * Other save&load logic data
         */
        setTila(TarjontaTila.LUONNOS);

        /*
         * OIDs
         */
        setKomotoOid(null); //KOMOTO OID
        setKoulutusmoduuliOid(null); //KOMO OID


        /*
         *  Form selection logic
         */
        setKoulutuskoodiModel(null);
        setTutkintoohjelma(null);

        setTunniste(null);

        /*
         * Koodisto service koodi data
         */
        setKoulutusaste(null);
        setKoulutusala(null);
        setTutkintonimike(null);
        setOpintojenLaajuusyksikko(null);
        setOpintojenLaajuus(null);
        setOpintoala(null);

        /*
         * Tarjonta text data
         */
        setTavoitteet(null);
        setKoulutuksenRakenne(null);
        setJatkoopintomahdollisuudet(null);

        /*
         * Korkeakoulu form data
         */
        setKoulutuksenAlkamisPvm(new Date());
        setSuunniteltuKesto(null);
        setSuunniteltuKestoTyyppi(null);
        setOpetusmuodos(new HashSet<String>());
        setYhteyshenkilo(new YhteyshenkiloModel());
        setEctsKoordinaattori(new YhteyshenkiloModel());
        setOpetuskielis(new HashSet<String>());
        setPohjakoulutusvaatimukset(new HashSet<String>());
        setOpintojenMaksullisuus(false);
        setTeemas(new HashSet<String>());

        /*
         * Other from info
         */
        setKoulutuskoodi(null);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        KorkeakouluPerustiedotViewModel other = (KorkeakouluPerustiedotViewModel) obj;

        EqualsBuilder builder = new EqualsBuilder();
        builder.append(komotoOid, other.komotoOid);
        builder.append(koulutusaste, other.koulutusaste);
        builder.append(koulutusmoduuliOid, other.koulutusmoduuliOid);
        builder.append(getKoulutuskoodiModel(), other.getKoulutuskoodiModel());
        builder.append(getTutkintoohjelma(), other.getTutkintoohjelma());
        builder.append(getKoulutusala(), other.getKoulutusala());
        builder.append(getTutkinto(), other.getTutkinto());
        builder.append(getTutkintonimike(), other.getTutkintonimike());
        builder.append(getOpintojenLaajuusyksikko(), other.getOpintojenLaajuusyksikko());
        builder.append(getOpintojenLaajuus(), other.getOpintojenLaajuus());
        builder.append(getOpintoala(), other.getOpintoala());
        builder.append(koulutuksenAlkamisPvm, other.koulutuksenAlkamisPvm);
        builder.append(suunniteltuKesto, other.suunniteltuKesto);
        builder.append(suunniteltuKestoTyyppi, other.suunniteltuKestoTyyppi);
        builder.append(getOpetusmuodos(), other.getOpetusmuodos());
        builder.append(getKoulutuslaji(), other.getKoulutuslaji());
        builder.append(getPohjakoulutusvaatimus(), other.getPohjakoulutusvaatimus());

        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(komotoOid)
                .append(koulutusaste)
                .append(koulutusmoduuliOid)
                .append(getKoulutuskoodiModel())
                .append(getTutkintoohjelma())
                .append(getKoulutusala())
                .append(getTutkinto())
                .append(getTutkintonimike())
                .append(getOpintojenLaajuusyksikko())
                .append(getOpintojenLaajuus())
                .append(getOpintoala())
                .append(koulutuksenAlkamisPvm)
                .append(suunniteltuKesto)
                .append(suunniteltuKestoTyyppi)
                .append(getOpetusmuodos())
                .append(getKoulutuslaji())
                .append(getPohjakoulutusvaatimus())
                .append(yhteyshenkilo)
                .toHashCode();
    }

    /**
     * @return the ectsKoordinaattori
     */
    public YhteyshenkiloModel getEctsKoordinaattori() {
        return ectsKoordinaattori;
    }

    /**
     * @param ectsKoordinaattori the ectsKoordinaattori to set
     */
    public void setEctsKoordinaattori(YhteyshenkiloModel ectsKoordinaattori) {
        this.ectsKoordinaattori = ectsKoordinaattori;
    }

    /**
     * @return the teemas
     */
    public Set<String> getTeemas() {
        return teemas;
    }

    /**
     * @param teemas the teemas to set
     */
    public void setTeemas(Set<String> teemas) {
        this.teemas = teemas;
    }

    /**
     * @return the opintojenMaksullisuus
     */
    public Boolean getOpintojenMaksullisuus() {
        return opintojenMaksullisuus;
    }

    /**
     * @param opintojenMaksullisuus the opintojenMaksullisuus to set
     */
    public void setOpintojenMaksullisuus(Boolean opintojenMaksullisuus) {
        this.opintojenMaksullisuus = opintojenMaksullisuus;
    }

    /**
     * @return the opetuskielis
     */
    public Set<String> getOpetuskielis() {
        return opetuskielis;
    }

    /**
     * @param opetuskielis the opetuskielis to set
     */
    public void setOpetuskielis(Set<String> opetuskielis) {
        this.opetuskielis = opetuskielis;
    }

    /**
     * @return the opetusmuodos
     */
    public Set<String> getOpetusmuodos() {
        return opetusmuodos;
    }

    /**
     * @param opetusmuodos the opetusmuodos to set
     */
    public void setOpetusmuodos(Set<String> opetusmuodos) {
        this.opetusmuodos = opetusmuodos;
    }

    /**
     * @return the pohjakoulutusvaatimukset
     */
    public Set<String> getPohjakoulutusvaatimukset() {
        return pohjakoulutusvaatimukset;
    }

    /**
     * @param pohjakoulutusvaatimukset the pohjakoulutusvaatimukset to set
     */
    public void setPohjakoulutusvaatimukset(Set<String> pohjakoulutusvaatimukset) {
        this.pohjakoulutusvaatimukset = pohjakoulutusvaatimukset;
    }

    /**
     * @return the tutkintoohjelma
     */
    public TutkintoohjelmaModel getTutkintoohjelma() {
        if (tutkintoohjelma == null) {
            tutkintoohjelma = new TutkintoohjelmaModel();
        }

        return tutkintoohjelma;
    }

    /**
     * @param tutkintoohjelma the tutkintoohjelma to set
     */
    public void setTutkintoohjelma(TutkintoohjelmaModel tutkintoohjelma) {
        this.tutkintoohjelma = tutkintoohjelma;
    }

    /**
     * @return the tunniste
     */
    public String getTunniste() {
        return tunniste;
    }

    /**
     * @param tunniste the tunniste to set
     */
    public void setTunniste(String tunniste) {
        this.tunniste = tunniste;
    }

}
