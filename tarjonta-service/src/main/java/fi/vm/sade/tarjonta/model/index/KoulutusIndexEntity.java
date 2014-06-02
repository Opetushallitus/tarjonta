package fi.vm.sade.tarjonta.model.index;

import java.util.Date;

import com.mysema.query.annotations.QueryProjection;
import fi.vm.sade.tarjonta.service.enums.KoulutustyyppiEnum;

import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

/**
 * Entity containing all fields from database needed for indexing
 */
public class KoulutusIndexEntity {

    private Long koulutusId;
    private final String oid;
    private String tarjoaja;
    private KoulutustyyppiEnum koulutustyyppiEnum;
    private String koulutusohjelmaKoodi;
    private String lukiolinja;
    private String koulutusKoodi;
    private Date koulutuksenAlkamisPvm;
    private TarjontaTila tila;
    private String koulutusmoduuliOid;
    private String pohjakoulutusVaatimus;
    private String koulutuslaji;
    private String kausi;
    private Integer vuosi;

    @QueryProjection
    public KoulutusIndexEntity(String oid, String tarjoaja, String koulutuslaji, String pohjakoulutusVaatimusUri, KoulutustyyppiEnum koulutustyyppiEnum) {
        this.oid = oid;
        this.tarjoaja = tarjoaja;
        this.koulutuslaji = koulutuslaji;
        this.pohjakoulutusVaatimus = pohjakoulutusVaatimusUri;
        this.koulutustyyppiEnum = koulutustyyppiEnum;
    }

    @QueryProjection
    public KoulutusIndexEntity(Long id, String oid, Date koulutuksenAlkamisPvm,
            TarjontaTila tila, KoulutustyyppiEnum koulutustyyppiEnum,
            String koulutusmoduuliOid, String koulutusKoodi, String lukiolinja,
            String koulutusohjelmaKoodi, String tarjoaja, String pohjakoulutusVaatimus, String kausi, Integer vuosi, String komotoKoulutuskoodi) {
        this.koulutusId = id;
        this.oid = oid;
        this.koulutuksenAlkamisPvm = koulutuksenAlkamisPvm;
        this.tila = tila;
        this.koulutusmoduuliOid = koulutusmoduuliOid;
        this.koulutusKoodi = koulutusKoodi != null ? koulutusKoodi
                : komotoKoulutuskoodi;
        this.koulutustyyppiEnum = koulutustyyppiEnum;
        this.lukiolinja = lukiolinja;
        this.koulutusohjelmaKoodi = koulutusohjelmaKoodi;
        this.tarjoaja = tarjoaja;
        this.pohjakoulutusVaatimus = pohjakoulutusVaatimus;
        this.kausi = kausi;
        this.vuosi = vuosi;
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

    public KoulutustyyppiEnum getKoulutustyyppiEnum() {
        return koulutustyyppiEnum;
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

    public Date getKoulutuksenAlkamisPvm() {
        return koulutuksenAlkamisPvm;
    }

    public TarjontaTila getTila() {
        return tila;
    }

    public String getKoulutusmoduuliOid() {
        return koulutusmoduuliOid;
    }

    public String getPohjakoulutusvaatimus() {
        return pohjakoulutusVaatimus;
    }

    public String getKoulutuslaji() {
        return koulutuslaji;
    }

    @Override
    public String toString() {
        return "KoulutusIndexEntity [koulutusId=" + koulutusId + ", oid=" + oid
                + ", tarjoaja=" + tarjoaja + ", koulutusTyyppi="
                + koulutustyyppiEnum + ", koulutusohjelmaKoodi="
                + koulutusohjelmaKoodi + ", lukiolinja=" + lukiolinja
                + ", koulutusKoodi=" + koulutusKoodi + ", koulutuksenAlkamisPvm="
                + koulutuksenAlkamisPvm + ", tila=" + tila
                + ", koulutusmoduuliOid=" + koulutusmoduuliOid
                + ", pohjakoulutusVaatimus=" + pohjakoulutusVaatimus
                + ", kausi=" + kausi
                + ", vuosi=" + vuosi
                + ", koulutuslaji=" + koulutuslaji + "]";
    }

    /**
     * @return the kausi
     */
    public String getKausi() {
        return kausi;
    }

    /**
     * @param kausi the kausi to set
     */
    public void setKausi(String kausi) {
        this.kausi = kausi;
    }

    /**
     * @return the vuosi
     */
    public Integer getVuosi() {
        return vuosi;
    }

    /**
     * @param vuosi the vuosi to set
     */
    public void setVuosi(Integer vuosi) {
        this.vuosi = vuosi;
    }

}
