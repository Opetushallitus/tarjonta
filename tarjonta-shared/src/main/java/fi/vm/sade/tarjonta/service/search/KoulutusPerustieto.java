package fi.vm.sade.tarjonta.service.search;

import java.io.Serializable;

import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

public class KoulutusPerustieto implements Serializable {

    private final static long serialVersionUID = 100L;
    private Nimi nimi = new Nimi();
    private String koulutusmoduuli;
    private String koulutusmoduuliToteutus;
    private Tarjoaja tarjoaja;
    private TarjontaTila tila;
    private KoodistoKoodi koulutus;
    private KoodistoKoodi koulutusohjelma;
    private KoodistoKoodi tutkintonimike;
    private String ajankohta;
    private String komotoOid;
    private KoodistoKoodi pohjakoulutusvaatimus;
    private KoodistoKoodi koulutuslaji;
    private KoulutusasteTyyppi koulutusasteTyyppi;
    private KoodistoKoodi lukiolinjakoodi;
    private KoodistoKoodi koulutuksenAlkamiskausi;
    private Integer koulutuksenAlkamisVuosi;
    private String koulutustyyppi; //real uri not enum
    private ToteutustyyppiEnum toteutustyyppi;

    /**
     * oikeasti tämä on koulutusohjelman nimi
     *
     * @return
     */
    public Nimi getNimi() {
        return nimi;
    }

    /**
     * Anna nimi
     *
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

    public Tarjoaja getTarjoaja() {
        return tarjoaja;
    }

    public void setTarjoaja(Tarjoaja value) {
        this.tarjoaja = value;
    }

    public TarjontaTila getTila() {
        return tila;
    }

    public void setTila(TarjontaTila value) {
        this.tila = value;
    }

    public KoodistoKoodi getKoulutuskoodi() {
        return koulutus;
    }

    public void setKoulutus(KoodistoKoodi value) {
        this.koulutus = value;
    }

    /*
     * Data field for koulutusohjelma, lukiolinja and opintoala uris.
     */
    public KoodistoKoodi getKoulutusohjelma() {
        return koulutusohjelma;
    }

    public void setKoulutusohjelma(KoodistoKoodi value) {
        this.koulutusohjelma = value;
    }

    public KoodistoKoodi getTutkintonimike() {
        return tutkintonimike;
    }

    public void setTutkintonimike(KoodistoKoodi value) {
        this.tutkintonimike = value;
    }

    public String getKomotoOid() {
        return komotoOid;
    }

    public void setKomotoOid(String value) {
        this.komotoOid = value;
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

    public void setKoulutuslaji(KoodistoKoodi value) {
        this.koulutuslaji = value;
    }

    public KoulutusasteTyyppi getKoulutusasteTyyppi() {
        return koulutusasteTyyppi;
    }

    public void setKoulutusasteTyyppi(KoulutusasteTyyppi value) {
        this.koulutusasteTyyppi = value;
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

    /**
     * @return the koulutustyyppi
     */
    public String getKoulutustyyppi() {
        return koulutustyyppi;
    }

    /**
     * @param koulutustyyppi the koulutustyyppi to set
     */
    public void setKoulutustyyppi(String koulutustyyppi) {
        this.koulutustyyppi = koulutustyyppi;
    }

    /**
     * @return the toteutustyyppi
     */
    public ToteutustyyppiEnum getToteutustyyppi() {
        return toteutustyyppi;
    }

    /**
     * @param toteutustyyppi the toteutustyyppi to set
     */
    public void setToteutustyyppi(ToteutustyyppiEnum toteutustyyppi) {
        this.toteutustyyppi = toteutustyyppi;
    }

}
