
package fi.vm.sade.tarjonta.service.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;


public class HakukohteetKysely implements Serializable
{

    private final static long serialVersionUID = 100L;
    private String nimi;
    private String nimiKoodiUri;
    private List<String> tarjoajaOids = new ArrayList<String>();
    private List<String> koulutusOids = new ArrayList<String>();
    private Integer koulutuksenAlkamisvuosi;
    private List<TarjontaTila> tilat = new ArrayList<TarjontaTila>();
    private String koulutuksenAlkamiskausi;
    private String hakuOid;
    private String hakukohdeOid;
    private List<KoulutusasteTyyppi> koulutusasteTyypit = new ArrayList<KoulutusasteTyyppi>();
    private String organisaatioRyhmaOid;


    public String getOrganisaatioRyhmaOid() {
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
     * @param value
     *     allowed object is
     *     {@link TarjontaTila }
     *     
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
    
    public static final HakukohteetKysely byHakukohdeOid(String oid){
        HakukohteetKysely kys = new HakukohteetKysely();
        kys.hakukohdeOid = oid;
        return kys;
    }

    public void setOrganisaatioRyhmaOid(String organisaatioRyhmaOid) {
        this.organisaatioRyhmaOid = organisaatioRyhmaOid;
    }

}
