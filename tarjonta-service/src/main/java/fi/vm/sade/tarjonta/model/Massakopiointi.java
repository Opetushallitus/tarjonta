/*
 * Copyright (c) 2014 The Finnish Board of Education - Opetushallitus
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
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(
        name = "massakopiointi",
        uniqueConstraints = @UniqueConstraint(columnNames = {"haku_oid", "old_oid"})
)
public class Massakopiointi extends BaseEntity {

    private static Logger LOG = LoggerFactory.getLogger(Massakopiointi.class);

    private static long serialVersionUID = 1;

    @Column(name = "haku_oid", length = 255, nullable = false)
    private String hakuOid;

    @Column(name = "old_oid", length = 255, nullable = false)
    private String oldOid;

    @Column(name = "new_oid", length = 255, nullable = false)
    private String newOid;

    @Column(name = "process_id", length = 255, nullable = false)
    private String processId;

    @Column(name = "content_type", length = 255, nullable = false)
    @Enumerated(EnumType.STRING)
    private Tyyppi type;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "json", length = 100000, nullable = false)
    private String json;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "meta", length = 100000)
    private String meta;

    @Column(name = "tila", length = 32, nullable = false)
    @Enumerated(EnumType.STRING)
    private KopioinninTila tila = KopioinninTila.COPIED;

    @Column(name = "updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated = null;

    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created = null;

    /**
     * Constructor for JPA.
     */
    public Massakopiointi() {
    }

    public Massakopiointi(String hakuOid, String oldOid, String newOid, String processId, Tyyppi type, String json, String meta) {
        this.hakuOid = hakuOid;
        this.oldOid = oldOid;
        this.newOid = newOid;
        this.processId = processId;
        this.json = json;
        this.meta = meta;
        this.type = type;
        this.created = new Date();
    }

    /**
     * @return the LOG
     */
    public static Logger getLOG() {
        return LOG;
    }

    /**
     * @param aLOG the LOG to set
     */
    public static void setLOG(Logger aLOG) {
        LOG = aLOG;
    }

    /**
     * @return the serialVersionUID
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * @param aSerialVersionUID the serialVersionUID to set
     */
    public static void setSerialVersionUID(long aSerialVersionUID) {
        serialVersionUID = aSerialVersionUID;
    }

    /**
     * @return the hakuOid
     */
    public String getHakuOid() {
        return hakuOid;
    }

    /**
     * @param hakuOid the hakuOid to set
     */
    public void setHakuOid(String hakuOid) {
        this.hakuOid = hakuOid;
    }

    /**
     * @return the json
     */
    public String getJson() {
        return json;
    }

    /**
     * @param json the json to set
     */
    public void setJson(String json) {
        this.json = json;
    }

    /**
     * @return the meta
     */
    public String getMeta() {
        return meta;
    }

    /**
     * @param meta the meta to set
     */
    public void setMeta(String meta) {
        this.meta = meta;
    }

    /**
     * @return the kopioinninTila
     */
    public KopioinninTila getKopioinninTila() {
        return tila;
    }

    /**
     * @param kopioinninTila the kopioinninTila to set
     */
    public void setKopioinninTila(KopioinninTila kopioinninTila) {
        this.tila = kopioinninTila;
    }

    /**
     * @return the updated
     */
    public Date getUpdated() {
        return updated;
    }

    /**
     * @param updated the updated to set
     */
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    /**
     * @return the created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * @return the type
     */
    public Tyyppi getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Tyyppi type) {
        this.type = type;
    }

    /**
     * @return the oldOid
     */
    public String getOldOid() {
        return oldOid;
    }

    /**
     * @param oldOid the oldOid to set
     */
    public void setOldOid(String oldOid) {
        this.oldOid = oldOid;
    }

    /**
     * @return the newOid
     */
    public String getNewOid() {
        return newOid;
    }

    /**
     * @param newOid the newOid to set
     */
    public void setNewOid(String newOid) {
        this.newOid = newOid;
    }

    /**
     * @return the processId
     */
    public String getProcessId() {
        return processId;
    }

    /**
     * @param processId the processId to set
     */
    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public enum KopioinninTila {

        READY_FOR_COPY, PROSESSING, COPIED, ERROR
    };

    public enum Tyyppi {

        HAKUKOHDE_ENTITY, KOMOTO_ENTITY
    };

}
