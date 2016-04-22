
package fi.vm.sade.tarjonta.service.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;


public class HakukohteetKysely implements Serializable {

    private final static long serialVersionUID = 100L;

    private String nimi;
    private String nimiKoodiUri;
    private String koulutuksenAlkamiskausi;
    private String hakuOid;
    private String hakukohdeOid;
    private String hakutapa;
    private String hakutyyppi;
    private String koulutuslaji;
    private String kohdejoukko;
    private String oppilaitostyyppi;
    private List<String> organisaatioRyhmaOid = new ArrayList<>();
    private String kunta;

    private Integer koulutuksenAlkamisvuosi;

    private Integer offset;
    private Integer limit;

    private List<String> tarjoajaOids = new ArrayList<String>();
    private List<String> koulutusOids = new ArrayList<String>();
    private List<String> opetuskielet = new ArrayList<String>();

    private List<TarjontaTila> tilat = new ArrayList<TarjontaTila>();
    private List<KoulutusasteTyyppi> koulutusasteTyypit = new ArrayList<KoulutusasteTyyppi>();

    private Set<String> koulutustyyppi = new HashSet<String>();
    private Set<ToteutustyyppiEnum> toteutustyypit = new HashSet<ToteutustyyppiEnum>();

    private List<KoulutusmoduuliTyyppi> koulutusmoduuliTyyppi = new ArrayList<KoulutusmoduuliTyyppi>();

    public List<String> getOrganisaatioRyhmaOid() {
        return organisaatioRyhmaOid;
    }

    public List<KoulutusasteTyyppi> getKoulutusasteTyypit() {
        return koulutusasteTyypit;
    }

    public String getHakukohdeOid() {
        return hakukohdeOid;
    }

    public void setHakukohdeOid(String hakukohdeOid) {
        this.hakukohdeOid = hakukohdeOid;
    }

    public String getHakuOid() {
        return hakuOid;
    }

    public void setHakuOid(String hakuOid) {
        this.hakuOid = hakuOid;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String value) {
        this.nimi = value;
    }

    public String getNimiKoodiUri() {
        return nimiKoodiUri;
    }

    public void setNimiKoodiUri(String value) {
        this.nimiKoodiUri = value;
    }

    public List<String> getTarjoajaOids() {
        return this.tarjoajaOids;
    }

    public List<String> getKoulutusOids() {
        return this.koulutusOids;
    }

    public Integer getKoulutuksenAlkamisvuosi() {
        return koulutuksenAlkamisvuosi;
    }

    public void setKoulutuksenAlkamisvuosi(Integer value) {
        this.koulutuksenAlkamisvuosi = value;
    }

    /**
     * Palauta hakuehdon tilat.
     */
    public List<TarjontaTila> getTilat() {
        return tilat;
    }

    /**
     * Lisää tila hakuehtoihin (haussa käytetään OR).
     *
     * @param value allowed object is
     *              {@link TarjontaTila }
     */
    public void addTila(TarjontaTila value) {
        this.tilat.add(value);
    }

    public String getKoulutuksenAlkamiskausi() {
        return koulutuksenAlkamiskausi;
    }

    public void setKoulutuksenAlkamiskausi(String value) {
        this.koulutuksenAlkamiskausi = value;
    }

    public static final HakukohteetKysely byHakukohdeOid(String oid) {
        HakukohteetKysely kys = new HakukohteetKysely();
        kys.hakukohdeOid = oid;
        return kys;
    }

    public void setOrganisaatioRyhmaOid(List<String> organisaatioRyhmaOid) {
        this.organisaatioRyhmaOid = organisaatioRyhmaOid;
    }

    public Set<ToteutustyyppiEnum> getTotetustyyppi() {
        return this.toteutustyypit;
    }

    public Set<String> getKoulutustyyppi() {
        return koulutustyyppi;
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

    public String getKoulutuslaji() {
        return koulutuslaji;
    }

    public void setKoulutuslaji(String koulutuslaji) {
        this.koulutuslaji = koulutuslaji;
    }

    public String getKohdejoukko() {
        return kohdejoukko;
    }

    public void setKohdejoukko(String kohdejoukko) {
        this.kohdejoukko = kohdejoukko;
    }

    public void setOppilaitostyyppi(String oppilaitostyyppi) {
        this.oppilaitostyyppi = oppilaitostyyppi;
    }

    public String getOppilaitostyyppi() {
        return oppilaitostyyppi;
    }

    public void setKunta(String kunta) {
        this.kunta = kunta;
    }

    public String getKunta() {
        return kunta;
    }

    public void setOpetuskielet(List<String> opetuskielet) {
        this.opetuskielet = opetuskielet;
    }

    public List<String> getOpetuskielet() {
        return opetuskielet;
    }

    public List<KoulutusmoduuliTyyppi> getKoulutusmoduuliTyyppi() {
        return koulutusmoduuliTyyppi;
    }

    public void setKoulutusmoduuliTyyppi(List<KoulutusmoduuliTyyppi> koulutusmoduuliTyyppi) {
        this.koulutusmoduuliTyyppi = koulutusmoduuliTyyppi;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

}
