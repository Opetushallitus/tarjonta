package fi.vm.sade.tarjonta.model;

import fi.vm.sade.generic.model.BaseEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * This entity is used to store "looong" multilanguage texts by key (avain) and category (kategoria).
 *
 * @author mlyly
 */
@Entity
@Table(name = "monikielinen_metadata", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"avain", "kategoria", "kieli"})
})
public class MonikielinenMetadata extends BaseEntity {

    // Make translated texts behave more like "metadata".
    // For example: "uri: Sosiaali- ja Terveysala"
    private String avain = null;
    // For example: "SORA", "Valintaperustekuvaus"
    private String kategoria = null;
    // For example: "uri: Finnish 358#1"
    private String kieli = null;
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String arvo = null;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, nullable = false)
    private Date created = new Date();
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = true, nullable = false)
    private Date modified = new Date();

    public String getAvain() {
        return avain;
    }

    public void setAvain(String avain) {
        this.avain = avain;
    }

    public String getKategoria() {
        return kategoria;
    }

    public void setKategoria(String kategoria) {
        this.kategoria = kategoria;
    }

    public String getKieli() {
        return kieli;
    }

    public void setKieli(String kieli) {
        this.kieli = kieli;
    }

    public String getArvo() {
        return arvo;
    }

    public void setArvo(String arvo) {
        this.arvo = arvo;
    }

    public Date getCreated() {
        return created;
    }

    public Date getModified() {
        return modified;
    }
}
