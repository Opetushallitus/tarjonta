package fi.vm.sade.tarjonta.model.index;

import java.util.Date;

import com.mysema.query.annotations.QueryProjection;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

/**
 * Entity containing all fields from database needed for indexing
 */
public class KoulutusIndexEntity {

    private Long koulutusId;
    private final String oid;
    private String tarjoaja;
    private ModuulityyppiEnum baseKoulutustyyppiEnum;
    private ToteutustyyppiEnum subKoulutustyyppiEnum;
    private TarjontaTila tila;
    private String koulutusmoduuliOid;
    private String pohjakoulutusVaatimus;
    private String koulutuslaji;
    private String kausi;
    private Integer vuosi;

    //required uri for all data objects
    private String koulutusUri;

    //child komo/komoto name data fields:
    private String koulutusohjelmaUri;
    private String lukiolinjaUri;
    private String osaamisalaUri;
    private String koulutustyyppiUri;
    private Date koulutuksenAlkamisPvmMin;
    private Date koulutuksenAlkamisPvmMax;
    
    @QueryProjection
    public KoulutusIndexEntity(String oid, String tarjoaja, String koulutuslaji, String pohjakoulutusVaatimusUri,
            ModuulityyppiEnum baseKoulutustyyppiEnum, ToteutustyyppiEnum subKoulutustyyppiEnum, String koulutusUri, String koulutuksenAlkamiskausi, Integer koulutuksenAlkamivuosi) {
        this.oid = oid;
        this.tarjoaja = tarjoaja;
        this.koulutuslaji = koulutuslaji;
        this.pohjakoulutusVaatimus = pohjakoulutusVaatimusUri;
        this.baseKoulutustyyppiEnum = baseKoulutustyyppiEnum;
        this.subKoulutustyyppiEnum = subKoulutustyyppiEnum;
        this.koulutusUri = koulutusUri;
        this.kausi = koulutuksenAlkamiskausi;
        this.vuosi = koulutuksenAlkamivuosi;
    }

    @QueryProjection
    public KoulutusIndexEntity(
            Long id,
            String oid,
            Date koulutuksenAlkamisPvmMin,
            Date koulutuksenAlkamisPvmMax,
            TarjontaTila tila,
            ModuulityyppiEnum baseKoulutustyyppiEnum,
            ToteutustyyppiEnum subKoulutustyyppiEnum,
            String koulutusmoduuliOid,
            String koulutusUri,
            String lukiolinjaUri,
            String koulutusohjelmaUri,
            String osaamisalaUri,
            String tarjoaja,
            String pohjakoulutusVaatimus,
            String kausi,
            Integer vuosi,
            String koulutustyyppiUri) {

        this.koulutusId = id;
        this.oid = oid;
        this.koulutuksenAlkamisPvmMin = koulutuksenAlkamisPvmMin;
        this.koulutuksenAlkamisPvmMax = koulutuksenAlkamisPvmMax;
        this.tila = tila;
        this.koulutusmoduuliOid = koulutusmoduuliOid;
        this.koulutusUri = koulutusUri;
        this.baseKoulutustyyppiEnum = baseKoulutustyyppiEnum;
        this.subKoulutustyyppiEnum = subKoulutustyyppiEnum;
        this.lukiolinjaUri = lukiolinjaUri;
        this.koulutusohjelmaUri = koulutusohjelmaUri;
        this.osaamisalaUri = osaamisalaUri;
        this.tarjoaja = tarjoaja;
        this.pohjakoulutusVaatimus = pohjakoulutusVaatimus;
        this.kausi = kausi;
        this.vuosi = vuosi;
        this.koulutustyyppiUri = koulutustyyppiUri;
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

    public ModuulityyppiEnum getBaseKoulutustyyppiEnum() {
        return baseKoulutustyyppiEnum;
    }

    public String getKoulutusohjelmaKoodi() {
        return koulutusohjelmaUri;
    }

    public String getLukiolinja() {
        return lukiolinjaUri;
    }

    public String getKoulutusUri() {
        return koulutusUri;
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
                + ", tarjoaja=" + tarjoaja + ", "
                + "baseKoulutusTyyppi=" + baseKoulutustyyppiEnum
                + "subKoulutusTyyppi=" + subKoulutustyyppiEnum
                + ", koulutusohjelmaKoodi="
                + koulutusohjelmaUri + ", lukiolinja=" + lukiolinjaUri
                + ", koulutusKoodi=" + koulutusUri + ", koulutuksenAlkamisPvmMin="
                + koulutuksenAlkamisPvmMin + ", koulutuksenAlkamisPvmMax="
                + koulutuksenAlkamisPvmMax + ", tila=" + tila
                + ", koulutusmoduuliOid=" + koulutusmoduuliOid
                + ", pohjakoulutusVaatimus=" + pohjakoulutusVaatimus
                + ", kausi=" + kausi
                + ", vuosi=" + vuosi
                + ", koulutuslaji=" + koulutuslaji
                + ", koulutustyyppi=" + getKoulutustyyppiUri()
                + "]";
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

    /**
     * @return the subKoulutustyyppiEnum
     */
    public ToteutustyyppiEnum getSubKoulutustyyppiEnum() {
        return subKoulutustyyppiEnum;
    }

    /**
     * @param subKoulutustyyppiEnum the subKoulutustyyppiEnum to set
     */
    public void setSubKoulutustyyppiEnum(ToteutustyyppiEnum subKoulutustyyppiEnum) {
        this.subKoulutustyyppiEnum = subKoulutustyyppiEnum;
    }

    /**
     * @return the osaamisalaUri
     */
    public String getOsaamisalaUri() {
        return osaamisalaUri;
    }

    /**
     * @param osaamisalaUri the osaamisalaUri to set
     */
    public void setOsaamisalaUri(String osaamisalaUri) {
        this.osaamisalaUri = osaamisalaUri;
    }

    /**
     * @return the koulutustyyppiUri
     */
    public String getKoulutustyyppiUri() {
        return koulutustyyppiUri;
    }

    /**
     * @param koulutustyyppiUri the koulutustyyppiUri to set
     */
    public void setKoulutustyyppiUri(String koulutustyyppiUri) {
        this.koulutustyyppiUri = koulutustyyppiUri;
    }

    public void setKoulutuksenAlkamisPvmMax(Date koulutuksenAlkamisPvmMax) {
        this.koulutuksenAlkamisPvmMax = koulutuksenAlkamisPvmMax;
    }
    
    public Date getKoulutuksenAlkamisPvmMax() {
        return koulutuksenAlkamisPvmMax;
    }

    public void setKoulutuksenAlkamisPvmMin(Date koulutuksenAlkamisPvmMin) {
        this.koulutuksenAlkamisPvmMin = koulutuksenAlkamisPvmMin;
    }
    
    public Date getKoulutuksenAlkamisPvmMin() {
        return koulutuksenAlkamisPvmMin;
    }
    
}
