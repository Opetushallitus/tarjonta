package fi.vm.sade.tarjonta.service.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

public class KoulutuksetKysely implements Serializable {

    private final static long serialVersionUID = 100L;
    private String nimi;
    private List<String> tarjoajaOids = new ArrayList<String>();
    private List<String> koulutusOids = new ArrayList<String>();
    private TarjontaTila koulutuksenTila;
    private Integer koulutuksenAlkamisvuosi;
    private String koulutuksenAlkamiskausi;
    private String koulutusKoodi;
    private List<String> hakukohdeOids = new ArrayList<String>();
    private String koulutusOid;
    @Deprecated
    private List<KoulutusasteTyyppi> koulutusasteTyypit = new ArrayList<KoulutusasteTyyppi>(); //use the koulutustyyppi
    private String komoOid;

    private List<ToteutustyyppiEnum> koulutustyyppi = new ArrayList<ToteutustyyppiEnum>();

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
    public KoulutuksetKysely(final String nimi, final List<String> tarjoajaOids, final List<String> koulutusOids, final TarjontaTila koulutuksenTila, final Integer koulutuksenAlkamisvuosi, final String koulutuksenAlkamiskausi, final String koulutusKoodi, final List<String> hakukohdeOids) {
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
    public List<ToteutustyyppiEnum> getKoulutustyyppi() {
        return koulutustyyppi;
    }

}
