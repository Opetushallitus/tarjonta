
package fi.vm.sade.tarjonta.service.search;

import java.io.Serializable;
import java.util.*;

import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;

public class HakukohdePerustieto implements Serializable {

    private final static long serialVersionUID = 100L;
    private String oid;
    private Nimi nimi = new Nimi();
    private String koodistoNimi;
    private TarjontaTila tila;
    private Integer aloituspaikat;
    private KoodistoKoodi koulutuksenAlkamiskausi;
    private Integer koulutuksenAlkamisvuosi;
    private KoodistoKoodi hakutapakoodi;
    private String tarjoajaOid;
    private String hakuOid;
    private Nimi tarjoajaNimi = new Nimi();
    private Date hakuAlkamisPvm;
    private Date hakuPaattymisPvm;
    private KoodistoKoodi koulutuslaji;
    private String hakutyyppiUri;
    private KoodistoKoodi pohjakoulutusvaatimus;
    private KoulutusasteTyyppi koulutusastetyyppi;
    private Map<String, String> aloituspaikatKuvaukset;
    private KoulutusmoduuliTyyppi koulutusmoduuliTyyppi;
    private List<SolrRyhmaliitos> ryhmaliitokset = new ArrayList<SolrRyhmaliitos>();
    private List<String> opetuskielet;
    private String toteutustyyppi;
    private String hakuaikaString;
    private String hakuaikaRyhma;
    private Integer ensikertalaistenAloituspaikat;

    public String getToteutustyyppi() {
        return toteutustyyppi;
    }

    public void setToteutustyyppi(String toteutustyyppi) {
        this.toteutustyyppi = toteutustyyppi;
    }

    public List<String> getOpetuskielet() {
        return opetuskielet;
    }

    public void setOpetuskielet(List<String> opetuskielet) {
        this.opetuskielet = opetuskielet;
    }

    public KoulutusasteTyyppi getKoulutusastetyyppi() {
        return koulutusastetyyppi;
    }

    public void setKoulutusastetyyppi(KoulutusasteTyyppi koulutusastetyyppi) {
        this.koulutusastetyyppi = koulutusastetyyppi;
    }

    public KoodistoKoodi getPohjakoulutusvaatimus() {
        return pohjakoulutusvaatimus;
    }

    public void setPohjakoulutusvaatimus(KoodistoKoodi pohjakoulutusvaatimus) {
        this.pohjakoulutusvaatimus = pohjakoulutusvaatimus;
    }

    public KoodistoKoodi getKoulutuslaji() {
        return koulutuslaji;
    }

    public void setKoulutuslaji(KoodistoKoodi koulutuslaji) {
        this.koulutuslaji = koulutuslaji;
    }

    public String getTarjoajaOid() {
        return tarjoajaOid;
    }

    public void setTarjoajaOid(String tarjoajaOid) {
        this.tarjoajaOid = tarjoajaOid;
    }

    public String getTarjoajaNimi(String locale) {
        return tarjoajaNimi.get(locale);
    }

    public void setTarjoajaNimi(String locale, String nimi) {
        tarjoajaNimi.put(locale, nimi);
    }

    public Map<String, String> getTarjoajaNimi() {
        return tarjoajaNimi;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String value) {
        this.oid = value;
    }

    /**
     * Palauta nimi
     *
     * @param locale kieli (fi,sv,en)
     * @return
     */
    public String getNimi(String locale) {
        return nimi.get(locale);
    }

    /**
     * Palauta kaiki nimet
     */
    public Map<String, String> getNimi() {
        return nimi;
    }

    /**
     * Aseta nimi
     *
     * @param locale kieli (fi,sv,en)
     * @param nimi   nimi
     */
    public void setNimi(String locale, String nimi) {
        this.nimi.put(locale, nimi);
    }

    public String getKoodistoNimi() {
        return koodistoNimi;
    }

    public void setKoodistoNimi(String value) {
        this.koodistoNimi = value;
    }

    public TarjontaTila getTila() {
        return tila;
    }

    public void setTila(TarjontaTila value) {
        this.tila = value;
    }

    public Integer getAloituspaikat() {
        return aloituspaikat;
    }

    public void setAloituspaikat(Integer value) {
        this.aloituspaikat = value;
    }

    public KoodistoKoodi getKoulutuksenAlkamiskausi() {
        return koulutuksenAlkamiskausi;
    }

    public void setKoulutuksenAlkamiskausi(KoodistoKoodi value) {
        this.koulutuksenAlkamiskausi = value;
    }

    public Integer getKoulutuksenAlkamisvuosi() {
        return koulutuksenAlkamisvuosi;
    }

    public void setKoulutuksenAlkamisvuosi(Integer value) {
        this.koulutuksenAlkamisvuosi = value;
    }

    public KoodistoKoodi getHakutapaKoodi() {
        return hakutapakoodi;
    }

    public void setHakutapaKoodi(KoodistoKoodi value) {
        this.hakutapakoodi = value;
    }

    public Date getHakuAlkamisPvm() {
        return hakuAlkamisPvm;
    }

    public void setHakuAlkamisPvm(Date value) {
        this.hakuAlkamisPvm = value;
    }

    public Date getHakuPaattymisPvm() {
        return hakuPaattymisPvm;
    }

    public void setHakuPaattymisPvm(Date value) {
        this.hakuPaattymisPvm = value;
    }

    public Nimi getHakutapaNimi() {
        return hakutapakoodi.getNimi();
    }

    public String getHakutyyppiUri() {
        return hakutyyppiUri;
    }

    public void setHakutyyppiUri(String hakutyyppiUri) {
        this.hakutyyppiUri = hakutyyppiUri;
    }

    public String getHakuOid() {
        return hakuOid;
    }

    public void setHakuOid(String hakuOid) {
        this.hakuOid = hakuOid;
    }

    @Override
    public String toString() {
        return "HakukohdePerustieto [oid=" + oid + ", nimi=" + nimi
                + ", koodistoNimi=" + koodistoNimi + ", tila=" + tila
                + ", aloituspaikat=" + aloituspaikat
                + ", koulutuksenAlkamiskausi=" + koulutuksenAlkamiskausi
                + ", koulutuksenAlkamisvuosi=" + koulutuksenAlkamisvuosi
                + ", hakutapakoodi=" + hakutapakoodi + ", tarjoajaOid="
                + tarjoajaOid + ", hakuOid=" + hakuOid + ", tarjoajaNimi="
                + tarjoajaNimi + ", hakuAlkamisPvm=" + hakuAlkamisPvm
                + ", hakuPaattymisPvm=" + hakuPaattymisPvm + ", koulutuslaji="
                + koulutuslaji + ", hakutyyppiUri=" + hakutyyppiUri
                + ", pohjakoulutusvaatimus=" + pohjakoulutusvaatimus
                + ", koulutusastetyyppi=" + koulutusastetyyppi
                + ", koulutusmoduuliTyyppi=" + koulutusmoduuliTyyppi + "]";
    }

    public Map<String, String> getAloituspaikatKuvaukset() {
        return aloituspaikatKuvaukset;
    }

    public void setAloituspaikatKuvaukset(Map<String, String> aloituspaikatKuvaukset) {
        this.aloituspaikatKuvaukset = aloituspaikatKuvaukset;
    }

    public KoulutusmoduuliTyyppi getKoulutusmoduuliTyyppi() {
        return koulutusmoduuliTyyppi;
    }

    public void setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi koulutusmoduuliTyyppi) {
        this.koulutusmoduuliTyyppi = koulutusmoduuliTyyppi;
    }

    public List<SolrRyhmaliitos> getRyhmaliitokset() {
        return ryhmaliitokset;
    }

    public void setRyhmaliitokset(List<SolrRyhmaliitos> ryhmaliitokset) {
        this.ryhmaliitokset = ryhmaliitokset;
    }

    public void addRyhmaliitos(SolrRyhmaliitos ryhmaliitos) {
        getRyhmaliitokset().add(ryhmaliitos);
    }

    public String getHakuaikaRyhma() {
        return hakuaikaRyhma;
    }

    public void setHakuaikaRyhma(String hakuaikaRyhma) {
        this.hakuaikaRyhma = hakuaikaRyhma;
    }

    public String getHakuaikaString() {
        return hakuaikaString;
    }

    public void setHakuaikaString(String hakuaikaString) {
        this.hakuaikaString = hakuaikaString;
    }

    public Integer getEnsikertalaistenAloituspaikat() {
        return ensikertalaistenAloituspaikat;
    }

    public void setEnsikertalaistenAloituspaikat(Integer ensikertalaistenAloituspaikat) {
        this.ensikertalaistenAloituspaikat = ensikertalaistenAloituspaikat;
    }
}
