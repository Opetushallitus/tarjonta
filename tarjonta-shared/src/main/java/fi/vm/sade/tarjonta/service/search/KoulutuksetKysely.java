package fi.vm.sade.tarjonta.service.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

import java.util.Date;

public class KoulutuksetKysely implements Serializable {

    private final static long serialVersionUID = 100L;

    private String nimi;
    private String komoOid;
    private String koulutuslaji;
    private String hakutapa;
    private String hakutyyppi;
    private String kohdejoukko;
    private String oppilaitostyyppi;
    private String kunta;
    private String koulutuksenAlkamiskausi;
    private String koulutusKoodi;
    private String koulutusOid;
    private boolean showAllKoulutukset = false;
    private List<String> koulutuskoodis = new ArrayList<String>();
    private List<String> opintoalakoodis = new ArrayList<String>();
    private List<String> koulutusalakoodis = new ArrayList<String>();

    private Integer koulutuksenAlkamisvuosi;

    private List<String> tarjoajaOids = new ArrayList<String>();
    private List<String> jarjestajaOids = new ArrayList<String>();
    private List<String> koulutusOids = new ArrayList<String>();
    private List<String> hakukohdeOids = new ArrayList<String>();
    private List<String> koulutustyyppi = new ArrayList<String>();
    private List<String> opetuskielet = new ArrayList<String>();
    private List<String> hakuOids = new ArrayList<String>();

    @Deprecated
    private List<KoulutusasteTyyppi> koulutusasteTyypit = new ArrayList<KoulutusasteTyyppi>(); //use the koulutustyyppi

    private List<ToteutustyyppiEnum> totetustyyppi = new ArrayList<ToteutustyyppiEnum>(); //more detailed data than in koulutusasteTyyppi
    private List<KoulutusmoduuliTyyppi> koulutusmoduuliTyyppi = new ArrayList<KoulutusmoduuliTyyppi>();

    private TarjontaTila koulutuksenTila;
    private Date koulutuksenAlkamisPvmAlkaen = null;
    private String hakukohderyhma;

    public String getKomoOid() {
        return komoOid;
    }

    public void setKomoOid(String komoOid) {
        this.komoOid = komoOid;
    }

    @Deprecated
    public List<KoulutusasteTyyppi> getKoulutusasteTyypit() {
        return koulutusasteTyypit;
    }

    public String getKoulutusOid() {
        return koulutusOid;
    }

    public KoulutuksetKysely() {
        super();
    }

    /**
     * Fully-initialising value constructor
     *
     */
    public KoulutuksetKysely(
            final String nimi,
            final List<String> tarjoajaOids,
            final List<String> koulutusOids,
            final TarjontaTila koulutuksenTila,
            final Integer koulutuksenAlkamisvuosi,
            final String koulutuksenAlkamiskausi,
            final String koulutusKoodi,
            final List<String> hakukohdeOids) {
        this.nimi = nimi;
        this.tarjoajaOids = tarjoajaOids;
        this.koulutusOids = koulutusOids;
        this.koulutuksenTila = koulutuksenTila;
        this.koulutuksenAlkamisvuosi = koulutuksenAlkamisvuosi;
        this.koulutuksenAlkamiskausi = koulutuksenAlkamiskausi;
        this.koulutusKoodi = koulutusKoodi;
        this.hakukohdeOids = hakukohdeOids;
    }

    /**
     * Gets the value of the nimi property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getNimi() {
        return nimi;
    }

    public void setNimi(String value) {
        this.nimi = value;
    }

    public List<String> getTarjoajaOids() {
        return this.tarjoajaOids;
    }

    public List<String> getJarjestajaOids() {
        return this.jarjestajaOids;
    }

    public List<String> getKoulutusOids() {
        return this.koulutusOids;
    }

    public TarjontaTila getKoulutuksenTila() {
        return koulutuksenTila;
    }

    public void setKoulutuksenTila(TarjontaTila value) {
        this.koulutuksenTila = value;
    }

    public Integer getKoulutuksenAlkamisvuosi() {
        return koulutuksenAlkamisvuosi;
    }

    public void setKoulutuksenAlkamisvuosi(Integer value) {
        this.koulutuksenAlkamisvuosi = value;
    }

    public String getKoulutuksenAlkamiskausi() {
        return koulutuksenAlkamiskausi;
    }

    public void setKoulutuksenAlkamiskausi(String value) {
        this.koulutuksenAlkamiskausi = value;
    }

    public String getKoulutusKoodi() {
        return koulutusKoodi;
    }

    public void setKoulutusKoodi(String value) {
        this.koulutusKoodi = value;
    }

    public List<String> getHakukohdeOids() {
        return this.hakukohdeOids;
    }

    /**
     * Hakuehto joka hakee koulutuksia koulutusoidin perusteella.
     */
    public static KoulutuksetKysely byKoulutusOid(String oid) {
        KoulutuksetKysely kysely = new KoulutuksetKysely();
        kysely.setKoulutusOid(oid);
        return kysely;
    }

    public void setKoulutusOid(String oid) {
        this.koulutusOid = oid;
    }

    public void setkomoOid(String komoOid) {
        this.komoOid = komoOid;
    }

    /**
     * @return the koulutustyyppi
     */
    public List<String> getKoulutustyyppi() {
        return koulutustyyppi;
    }

    /**
     * @return the totetustyyppi
     */
    public List<ToteutustyyppiEnum> getTotetustyyppi() {
        return totetustyyppi;
    }

    /**
     * @param totetustyyppi the totetustyyppi to set
     */
    public void setTotetustyyppi(List<ToteutustyyppiEnum> totetustyyppi) {
        this.totetustyyppi = totetustyyppi;
    }

    public Date getKoulutuksenAlkamisPvmAlkaen() {
        return koulutuksenAlkamisPvmAlkaen;
    }

    public void setKoulutuksenAlkamisPvmAlkaen(Date koulutuksenAlkamisPvmAlkaen) {
        this.koulutuksenAlkamisPvmAlkaen = koulutuksenAlkamisPvmAlkaen;
    }

    /**
     * @return the koulutuslaji
     */
    public String getKoulutuslaji() {
        return koulutuslaji;
    }

    /**
     * @param koulutuslaji the koulutuslaji to set
     */
    public void setKoulutuslaji(String koulutuslaji) {
        this.koulutuslaji = koulutuslaji;
    }

    public String getHakutapa() {
        return hakutapa;
    }

    public void setHakutapa(String hakutapa) {
        this.hakutapa = hakutapa;
    }

    public String getHakutyyppi() {
        return hakutyyppi;
    }

    public void setHakutyyppi(String hakutyyppi) {
        this.hakutyyppi = hakutyyppi;
    }

    public String getKohdejoukko() {
        return kohdejoukko;
    }

    public void setKohdejoukko(String kohdejoukko) {
        this.kohdejoukko = kohdejoukko;
    }

    public String getOppilaitostyyppi() {
        return oppilaitostyyppi;
    }

    public void setOppilaitostyyppi(String oppilaitostyyppi) {
        this.oppilaitostyyppi = oppilaitostyyppi;
    }

    public void setKunta(String kunta) {
        this.kunta = kunta;
    }

    public String getKunta() {
        return kunta;
    }


    public void opetuskielet(List<String> opetuskielet) {
        this.opetuskielet = opetuskielet;
    }

    public List<String> getOpetuskielet() {
        return opetuskielet;
    }

    public List<KoulutusmoduuliTyyppi> getKoulutusmoduuliTyyppi() {
        return koulutusmoduuliTyyppi;
    }

    public String getHakukohderyhma() {
        return hakukohderyhma;
    }

    public void setHakukohderyhma(String hakukohderyhma) {
        this.hakukohderyhma = hakukohderyhma;
    }

    public void setKoulutuskoodis(List<String> koulutuskoodis) {
        this.koulutuskoodis = koulutuskoodis;
    }

    public List<String> getKoulutuskoodis() {
        return koulutuskoodis;
    }

    public void setOpintoalakoodis(List<String> opintoalakoodis) {
        this.opintoalakoodis = opintoalakoodis;
    }

    public List<String> getOpintoalakoodis() {
        return opintoalakoodis;
    }

    public void setKoulutusalakoodis(List<String> koulutusalakoodis) {
        this.koulutusalakoodis = koulutusalakoodis;
    }

    public List<String> getKoulutusalakoodis() {
        return koulutusalakoodis;
    }

    public boolean showAllKoulutukset() {
        return showAllKoulutukset;
    }

    public void setShowAllKoulutukset(boolean showAllKoulutukset) {
        this.showAllKoulutukset = showAllKoulutukset;
    }

    public void setHakuOids(List<String> hakuOids) {
        this.hakuOids = Lists.newArrayList(hakuOids);
    }

    public List<String> getHakuOids() {
        return hakuOids;
    }

}
