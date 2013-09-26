
package fi.vm.sade.tarjonta.service.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fi.vm.sade.tarjonta.shared.types.TarjontaTila;


public class HakukohteetKysely implements Serializable
{

    private final static long serialVersionUID = 100L;
    private String nimi;
    private String nimiKoodiUri;
    private List<String> tarjoajaOids;
    private List<String> koulutusOids;
    private Integer koulutuksenAlkamisvuosi;
    private List<TarjontaTila> tilat = new ArrayList<TarjontaTila>();
    private String koulutuksenAlkamiskausi;
    private String hakuOid;
    private String hakukohdeOid;

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

    /**
     * Default no-arg constructor
     * 
     */
    public HakukohteetKysely() {
        super();
    }

    /**
     * Gets the value of the nimi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNimi() {
        return nimi;
    }

    /**
     * Sets the value of the nimi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNimi(String value) {
        this.nimi = value;
    }

    /**
     * Gets the value of the nimiKoodiUri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNimiKoodiUri() {
        return nimiKoodiUri;
    }

    /**
     * Sets the value of the nimiKoodiUri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNimiKoodiUri(String value) {
        this.nimiKoodiUri = value;
    }

    /**
     * Gets the value of the tarjoajaOids property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tarjoajaOids property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTarjoajaOids().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getTarjoajaOids() {
        if (tarjoajaOids == null) {
            tarjoajaOids = new ArrayList<String>();
        }
        return this.tarjoajaOids;
    }

    /**
     * Gets the value of the koulutusOids property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the koulutusOids property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKoulutusOids().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getKoulutusOids() {
        if (koulutusOids == null) {
            koulutusOids = new ArrayList<String>();
        }
        return this.koulutusOids;
    }

    /**
     * Gets the value of the koulutuksenAlkamisvuosi property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getKoulutuksenAlkamisvuosi() {
        return koulutuksenAlkamisvuosi;
    }

    /**
     * Sets the value of the koulutuksenAlkamisvuosi property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
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

    /**
     * Gets the value of the koulutuksenAlkamiskausi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKoulutuksenAlkamiskausi() {
        return koulutuksenAlkamiskausi;
    }

    /**
     * Sets the value of the koulutuksenAlkamiskausi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKoulutuksenAlkamiskausi(String value) {
        this.koulutuksenAlkamiskausi = value;
    }
    
    
    public static final HakukohteetKysely byHakukohdeOid(String oid){
        HakukohteetKysely kys = new HakukohteetKysely();
        kys.hakukohdeOid = oid;
        return kys;
    }

}
