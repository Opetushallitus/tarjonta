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
package fi.vm.sade.tarjonta.model;

import fi.vm.sade.generic.model.BaseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 *
 * @author Antti Salonen
 */
@Entity
@Table(name = Hakuera.TABLE_NAME)
@Inheritance(strategy = InheritanceType.JOINED)
public class Hakuera extends BaseEntity {


    private static Logger log = LoggerFactory.getLogger(Hakuera.class);
    public static final String TABLE_NAME = "hakuera";
    public static final String HAUN_ALKAMIS_PVM = "haunAlkamisPvm";
    public static final String HAUN_LOPPUMIS_PVM = "haunLoppumisPvm";

    @NotNull
    private String oid;
    @NotNull
    private String nimiFi;
    @NotNull
    private String nimiSv;
    @NotNull
    private String nimiEn;
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date haunAlkamisPvm;
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date haunLoppumisPvm;
    
    private String hakutyyppi;
    private String hakukausi;
    private String koulutuksenAlkaminen;
    private String kohdejoukko;
    private String hakutapa;
    private boolean sijoittelu;
    private String lomake;
    

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getNimiFi() {
        return nimiFi;
    }

    public void setNimiFi(String nimiFi) {
        this.nimiFi = nimiFi;
    }

    public String getNimiSv() {
        return nimiSv;
    }

    public void setNimiSv(String nimiSv) {
        this.nimiSv = nimiSv;
    }

    public String getNimiEn() {
        return nimiEn;
    }

    public void setNimiEn(String nimiEn) {
        this.nimiEn = nimiEn;
    }

    public Date getHaunAlkamisPvm() {
        return haunAlkamisPvm;
    }

    public void setHaunAlkamisPvm(Date haunAlkamisPvm) {
        this.haunAlkamisPvm = haunAlkamisPvm;
    }

    public Date getHaunLoppumisPvm() {
        return haunLoppumisPvm;
    }

    public void setHaunLoppumisPvm(Date haunLoppumisPvm) {
        this.haunLoppumisPvm = haunLoppumisPvm;
    }

    public String getHakutyyppi() {
        return hakutyyppi;
    }

    public void setHakutyyppi(String hakutyyppi) {
        this.hakutyyppi = hakutyyppi;
    }

    public String getHakukausi() {
        return hakukausi;
    }

    public void setHakukausi(String hakukausi) {
        this.hakukausi = hakukausi;
    }

    public String getKoulutuksenAlkaminen() {
        return koulutuksenAlkaminen;
    }

    public void setKoulutuksenAlkaminen(String koulutuksenAlkaminen) {
        this.koulutuksenAlkaminen = koulutuksenAlkaminen;
    }

    public String getKohdejoukko() {
        return kohdejoukko;
    }

    public void setKohdejoukko(String kohdejoukko) {
        this.kohdejoukko = kohdejoukko;
    }

    public String getHakutapa() {
        return hakutapa;
    }

    public void setHakutapa(String hakutapa) {
        this.hakutapa = hakutapa;
    }

    public boolean isSijoittelu() {
        return sijoittelu;
    }

    public void setSijoittelu(boolean sijoittelu) {
        this.sijoittelu = sijoittelu;
    }

    public String getLomake() {
        return lomake;
    }

    public void setLomake(String lomake) {
        this.lomake = lomake;
    }
}

