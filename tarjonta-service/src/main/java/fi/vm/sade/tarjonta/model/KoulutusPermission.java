package fi.vm.sade.tarjonta.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "koulutus_permissions")
public class KoulutusPermission {

    @Id
    @Column(
            name = "id",
            unique = true,
            nullable = false
    )
    @GeneratedValue
    private Long id;

    @Column(name = "org_oid")
    private String orgOid;

    @Column(name = "koodisto")
    private String koodisto;

    @Column(name = "koodi_uri")
    private String koodiUri;

    @Column(name = "alku_pvm")
    private Date alkuPvm;

    @Column(name = "loppu_pvm")
    private Date loppuPvm;

    public KoulutusPermission(String orgOid, String koodisto, String koodiUri, Date alkuPvm, Date loppuPvm) {
        this.orgOid = orgOid;
        this.koodisto = koodisto;
        this.koodiUri = koodiUri;
        this.alkuPvm = alkuPvm;
        this.loppuPvm = loppuPvm;
    }

    public KoulutusPermission() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrgOid() {
        return orgOid;
    }

    public void setOrgOid(String orgOid) {
        this.orgOid = orgOid;
    }

    public String getKoodisto() {
        return koodisto;
    }

    public void setKoodisto(String koodisto) {
        this.koodisto = koodisto;
    }

    public String getKoodiUri() {
        return koodiUri;
    }

    public void setKoodiUri(String koodiUri) {
        this.koodiUri = koodiUri;
    }

    public Date getAlkuPvm() {
        return alkuPvm;
    }

    public void setAlkuPvm(Date alkuPvm) {
        this.alkuPvm = alkuPvm;
    }

    public Date getLoppuPvm() {
        return loppuPvm;
    }

    public void setLoppuPvm(Date loppuPvm) {
        this.loppuPvm = loppuPvm;
    }

}
