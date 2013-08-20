package fi.vm.sade.tarjonta.model.index;

import java.util.Date;

import com.mysema.query.annotations.QueryProjection;

import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

/**
 * Entity containing all fields from database needed for indexing
 */
public class KoulutusIndexEntity {

    private Long koulutusId;
    private final String oid;
    private String tarjoaja;
    private String koulutusTyyppi;
    private String koulutusohjelmaKoodi;
    private String lukiolinja;
    private String koulutusKoodi;
    private String tutkintonimike;
    private Date koulutuksenAlkamisPvm;
    private TarjontaTila tila;
    private String koulutusmoduuliOid;
    private String pohjakoulutusVaatimus;
    private String koulutuslaji;

    @QueryProjection
    public KoulutusIndexEntity(String oid, String tarjoaja, String koulutuslaji) {
        this.oid = oid;
        this.tarjoaja = tarjoaja;
        this.koulutuslaji = koulutuslaji;
    }

    @QueryProjection
    public KoulutusIndexEntity(Long id, String oid, Date koulutuksenAlkamisPvm,
            TarjontaTila tila, String koulutusTyyppi,
            String koulutusmoduuliOid, String koulutusKoodi,
            String tutkintoNimike, String koulutustyyppi, String lukiolinja,
            String koulutusohjelmaKoodi, String tarjoaja, String pohjakoulutusVaatimus) {
        this.koulutusId = id;
        this.oid = oid;
        this.koulutuksenAlkamisPvm = koulutuksenAlkamisPvm;
        this.tila = tila;
        this.koulutusTyyppi = koulutusTyyppi;
        this.koulutusmoduuliOid = koulutusmoduuliOid;
        this.koulutusKoodi = koulutusKoodi;
        this.tutkintonimike = tutkintoNimike;
        this.koulutusTyyppi = koulutusTyyppi;
        this.lukiolinja = lukiolinja;
        this.koulutusohjelmaKoodi = koulutusohjelmaKoodi;
        this.tarjoaja = tarjoaja;
        this.pohjakoulutusVaatimus = pohjakoulutusVaatimus;
    }

    /**
     * Return koulutusmoduulitoteutus.id
     * 
     * @return
     */
    public Long getKoulutusId() {
        return koulutusId;
    }

    /**
     * Return koulutus oid.
     * 
     * @return
     */
    public String getOid() {
        return oid;
    }

    /**
     * Return tarjoaja oid.
     * 
     * @return
     */
    public String getTarjoaja() {
        return tarjoaja;
    }

    public String getKoulutusTyyppi() {
        return koulutusTyyppi;
    }

    public String getKoulutusohjelmaKoodi() {
        return koulutusohjelmaKoodi;
    }

    public String getLukiolinja() {
        return lukiolinja;
    }

    public String getKoulutusKoodi() {
        return koulutusKoodi;
    }

    public String getTutkintonimike() {
        return tutkintonimike;
    }

    public Date getKoulutuksenAlkamisPvm() {
        return koulutuksenAlkamisPvm;
    }

    public TarjontaTila getTila() {
        return tila;
    }

    public String getKoulutusmoduuliOid() {
        return koulutusmoduuliOid;
    }

    public String getKoulutustyyppi() {
        return koulutusTyyppi;
    }

    public String getPohjakoulutusvaatimus() {
        return pohjakoulutusVaatimus;
    }

    public String getKoulutuslaji() {
        return koulutuslaji;
    }

}
