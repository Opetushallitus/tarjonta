package fi.vm.sade.tarjonta.service.resources.v1.dto;

import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;

import java.util.ArrayList;
import java.util.Map;

import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

import java.util.Date;

public class KoulutusHakutulosV1RDTO extends BaseV1RDTO {

    private static final long serialVersionUID = 2L;

    private String oid;
    private Map<String, String> nimi;
    private Map<String, String> kausi;
    private String kausiUri;
    private Integer vuosi;
    private Map<String, String> koulutusLaji;
    private String koulutuslajiUri;
    private TarjontaTila tila;
    private KoulutusasteTyyppi koulutusasteTyyppi;
    private ToteutustyyppiEnum toteutustyyppiEnum;
    private Map<String, String> pohjakoulutusvaatimus;
    private String koulutuskoodi;
    private Date koulutuksenAlkamisPvmMin = null;
    private Date koulutuksenAlkamisPvmMax = null;
    private ArrayList<String> tarjoajat;

    private String komoOid;

    public String getKomoOid() {
        return komoOid;
    }

    public Map<String, String> getPohjakoulutusvaatimus() {
        return pohjakoulutusvaatimus;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public Map<String, String> getNimi() {
        return nimi;
    }

    public void setNimi(Map<String, String> nimi) {
        this.nimi = nimi;
    }

    public Map<String, String> getKausi() {
        return kausi;
    }

    public void setKausi(Map<String, String> kausi) {
        this.kausi = kausi;
    }

    public Integer getVuosi() {
        return vuosi;
    }

    public void setVuosi(Integer vuosi) {
        this.vuosi = vuosi;
    }

    public Map<String, String> getKoulutuslaji() {
        return koulutusLaji;
    }

    public void setKoulutuslaji(Map<String, String> koulutusLaji) {
        this.koulutusLaji = koulutusLaji;
    }

    public TarjontaTila getTila() {
        return tila;
    }

    public void setTila(TarjontaTila tila) {
        this.tila = tila;
    }

    public KoulutusasteTyyppi getKoulutusasteTyyppi() {
        return koulutusasteTyyppi;
    }

    public void setKoulutusasteTyyppi(KoulutusasteTyyppi koulutusasteTyyppi) {
        this.koulutusasteTyyppi = koulutusasteTyyppi;
    }

    public void setPohjakoulutusvaatimus(
            Map<String, String> pohjakoulutusvaatimus) {
        this.pohjakoulutusvaatimus = pohjakoulutusvaatimus;
    }

    public void setKomoOid(String koulutusmoduuliOid) {
        this.komoOid = koulutusmoduuliOid;
    }

    /**
     * @return the koulutuskoodi
     */
    public String getKoulutuskoodi() {
        return koulutuskoodi;
    }

    /**
     * @param koulutuskoodi the koulutuskoodi to set
     */
    public void setKoulutuskoodi(String koulutuskoodi) {
        this.koulutuskoodi = koulutuskoodi;
    }

    public String getKausiUri() {
        return kausiUri;
    }

    public void setKausiUri(String kausiUri) {
        this.kausiUri = kausiUri;
    }

    /**
     * @return the koulutuslajiUri
     */
    public String getKoulutuslajiUri() {
        return koulutuslajiUri;
    }

    /**
     * @param koulutuslajiUri the koulutuslajiUri to set
     */
    public void setKoulutuslajiUri(String koulutuslajiUri) {
        this.koulutuslajiUri = koulutuslajiUri;
    }

    public Date getKoulutuksenAlkamisPvmMax() {
        return koulutuksenAlkamisPvmMax;
    }

    public void setKoulutuksenAlkamisPvmMax(Date koulutuksenAlkamisPvmMax) {
        this.koulutuksenAlkamisPvmMax = koulutuksenAlkamisPvmMax;
    }

    public Date getKoulutuksenAlkamisPvmMin() {
        return koulutuksenAlkamisPvmMin;
    }

    public void setKoulutuksenAlkamisPvmMin(Date koulutuksenAlkamisPvmMin) {
        this.koulutuksenAlkamisPvmMin = koulutuksenAlkamisPvmMin;
    }

    public void setTarjoajat(ArrayList<String> tarjoajat) {
        this.tarjoajat = tarjoajat;
    }

    public ArrayList<String> getTarjoajat() {
        return tarjoajat;
    }

    public void setToteutustyyppiEnum(ToteutustyyppiEnum toteutustyyppiEnum) {
        this.toteutustyyppiEnum = toteutustyyppiEnum;
    }

    public ToteutustyyppiEnum getToteutustyyppiEnum() {
        return toteutustyyppiEnum;
    }

}
