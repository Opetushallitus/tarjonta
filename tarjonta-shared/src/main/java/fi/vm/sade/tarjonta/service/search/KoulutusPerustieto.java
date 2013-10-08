
package fi.vm.sade.tarjonta.service.search;

import java.io.Serializable;

import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjoajaTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;


public class KoulutusPerustieto implements Serializable
{

    private final static long serialVersionUID = 100L;
    private Nimi nimi = new Nimi();
    private String koulutusmoduuli;
    private String koulutusmoduuliToteutus;
    private TarjoajaTyyppi tarjoaja;
    private TarjontaTila tila;
    private KoodistoKoodi koulutuskoodi;
    private KoodistoKoodi koulutusohjelmakoodi;
    private KoodistoKoodi tutkintonimike;
    private String ajankohta;
    private String komotoOid;
    private String pohjakoulutusVaatimus;
    private KoodistoKoodi koulutuslaji;
    private KoulutusasteTyyppi koulutustyyppi;
    private KoodistoKoodi lukiolinjakoodi;
    private KoodistoKoodi koulutuksenAlkamiskausi;
    private Integer koulutuksenAlkamisVuosi;


    public Nimi getNimi() {
        return nimi;
    }

    /**
     * Aseta nimi
     * @param locale (sv,fi,en);
     * @param nimi
     */
    public void setNimi(String locale, String nimi) {
        this.nimi.put(locale,  nimi);
    }

    /**
     * Anna nimi
     * @param locale (sv, fi, en);
     * @return nimi tai null jos ei määritelty
     */
    public String getNimi(String locale) {
        return nimi.get(locale);
    }

    public String getKoulutusmoduuli() {
        return koulutusmoduuli;
    }

    public void setKoulutusmoduuli(String value) {
        this.koulutusmoduuli = value;
    }

    public String getKoulutusmoduuliToteutus() {
        return koulutusmoduuliToteutus;
    }

    public void setKoulutusmoduuliToteutus(String value) {
        this.koulutusmoduuliToteutus = value;
    }

    public TarjoajaTyyppi getTarjoaja() {
        return tarjoaja;
    }

    public void setTarjoaja(TarjoajaTyyppi value) {
        this.tarjoaja = value;
    }

    public TarjontaTila getTila() {
        return tila;
    }

    public void setTila(TarjontaTila value) {
        this.tila = value;
    }

    public KoodistoKoodi getKoulutuskoodi() {
        return koulutuskoodi;
    }

    public void setKoulutuskoodi(KoodistoKoodi value) {
        this.koulutuskoodi = value;
    }

    public KoodistoKoodi getKoulutusohjelmakoodi() {
        return koulutusohjelmakoodi;
    }

    public void setKoulutusohjelmakoodi(KoodistoKoodi value) {
        this.koulutusohjelmakoodi = value;
    }

    public KoodistoKoodi getTutkintonimike() {
        return tutkintonimike;
    }

    public void setTutkintonimike(KoodistoKoodi value) {
        this.tutkintonimike = value;
    }

    public String getAjankohta() {
        return ajankohta;
    }

    public void setAjankohta(String value) {
        this.ajankohta = value;
    }

    public String getKomotoOid() {
        return komotoOid;
    }

    public void setKomotoOid(String value) {
        this.komotoOid = value;
    }

    public String getPohjakoulutusVaatimus() {
        return pohjakoulutusVaatimus;
    }

    public void setPohjakoulutusVaatimus(String value) {
        this.pohjakoulutusVaatimus = value;
    }

    public KoodistoKoodi getKoulutuslaji() {
        return koulutuslaji;
    }

    public void setKoulutuslaji(KoodistoKoodi value) {
        this.koulutuslaji = value;
    }

    public KoulutusasteTyyppi getKoulutustyyppi() {
        return koulutustyyppi;
    }

    public void setKoulutustyyppi(KoulutusasteTyyppi value) {
        this.koulutustyyppi = value;
    }

    public KoodistoKoodi getLukiolinjakoodi() {
        return lukiolinjakoodi;
    }

    public void setLukiolinjakoodi(KoodistoKoodi value) {
        this.lukiolinjakoodi = value;
    }

    public KoodistoKoodi getKoulutuksenAlkamiskausi() {
        return koulutuksenAlkamiskausi;
    }

    public void setKoulutuksenAlkamiskausiUri(KoodistoKoodi value) {
        this.koulutuksenAlkamiskausi = value;
    }

    public Integer getKoulutuksenAlkamisVuosi() {
        return koulutuksenAlkamisVuosi;
    }

    public void setKoulutuksenAlkamisVuosi(Integer value) {
        this.koulutuksenAlkamisVuosi = value;
    }

}
