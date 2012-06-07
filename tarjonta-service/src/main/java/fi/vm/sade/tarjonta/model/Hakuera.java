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

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
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

    private String oid;
    private String nimi;
    private Date haunAlkamisPvm;
    private Date haunLoppumisPvm;

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
}

