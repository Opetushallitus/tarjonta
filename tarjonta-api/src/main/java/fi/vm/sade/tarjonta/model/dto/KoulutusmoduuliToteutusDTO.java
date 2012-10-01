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
package fi.vm.sade.tarjonta.model.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 *
 * @author Jukka Raanamo
 */
@SuppressWarnings("serial")
public abstract class KoulutusmoduuliToteutusDTO implements Serializable {

    private String tila;

    private String oid;

    private String nimi;

    private String toteutettavaKoulutusmoduuliOID;

    private KoulutusmoduuliPerustiedotDTO perustiedot = new KoulutusmoduuliPerustiedotDTO();

    /*
     * TODO: Miksi tätä ei ole koulutusmoduulin toteutuksessa tietomallissa? Tietomallissa tämä näyttää olevan Koulutusmoduulissa. Vastaus Sepolta:
     * Tietomallissa virhe
     */
    private String koulutuslajiUri;

    /*
     * TODO: Miksi näitä voi olla tietomallin mukaan monta, vaikka lomakkeessa voi valita vain yhden? Vastaus Sepolta: Oikeasti on vain yksi. Monesta moneen
     * suhde käytännössä syntyy vain monimutkaisten organisaatioiden välisten suhteiden kautta.
     */
    private Set<String> tarjoajat;

    private Date koulutuksenAlkamisPvm;

    private String maksullisuus;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public void setPerustiedot(KoulutusmoduuliPerustiedotDTO perustiedot) {
        this.perustiedot = perustiedot;
    }

    public KoulutusmoduuliPerustiedotDTO getPerustiedot() {
        return perustiedot;
    }

    public String getToteutettavaKoulutusmoduuliOID() {
        return toteutettavaKoulutusmoduuliOID;
    }

    public void setToteutettavaKoulutusmoduuliOID(String toteutettavaKoulutusmoduuliOID) {
        this.toteutettavaKoulutusmoduuliOID = toteutettavaKoulutusmoduuliOID;
    }

    @Override
    public String toString() {
        
        
        
        return new StringBuilder().append("oid : ").append(oid).append("nimi : ").
            append(nimi).
            append("tila :").
            append(getTila()).
            append("toteutettavaKoulutusmoduuliOID : ").
            append(toteutettavaKoulutusmoduuliOID).
            append(perustiedot).toString();
    }

    /**
     * @return the tila
     */
    public String getTila() {
        return tila;
    }

    /**
     * @param tila the tila to set
     */
    public void setTila(String tila) {
        this.tila = tila;
    }

    /**
     *
     * @return the koulutuslaji
     */
    public String getKoulutuslajiUri() {
        return koulutuslajiUri;
    }

    /**
     *
     * @param koulutuslaji the koulutuslaji to set
     */
    public void setKoulutuslajiUri(String koulutuslajiUri) {
        this.koulutuslajiUri = koulutuslajiUri;
    }

    /**
     *
     * @return the list of tarjoaja organisaatio oids.
     */
    public Set<String> getTarjoajat() {
        return tarjoajat;
    }

    /**
     *
     * @param tarjoajat the list of tarjoaja organisaatio oids to set
     */
    public void setTarjoajat(Set<String> tarjoajat) {
        this.tarjoajat = tarjoajat;
    }

    /**
     *
     * @return the koulutuksenAlkamisPvm, i.e. the date the education/training begins
     */
    public Date getKoulutuksenAlkamisPvm() {
        return koulutuksenAlkamisPvm;
    }

    /**
     *
     * @param koulutuksenAlkamisPvm
     */
    public void setKoulutuksenAlkamisPvm(Date koulutuksenAlkamisPvm) {
        this.koulutuksenAlkamisPvm = koulutuksenAlkamisPvm;
    }

    /**
     *
     * @return the maksullisuus
     */
    public String getMaksullisuus() {
        return maksullisuus;
    }

    /**
     *
     * @param maksullisuus the maksuullisuus to set
     */
    public void setMaksullisuus(String maksullisuus) {
        this.maksullisuus = maksullisuus;
    }

}

