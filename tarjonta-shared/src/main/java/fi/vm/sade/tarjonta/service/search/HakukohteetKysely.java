
package fi.vm.sade.tarjonta.service.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fi.vm.sade.tarjonta.shared.types.TarjontaTila;


public class HakukohteetKysely implements Serializable
{

    private final static long serialVersionUID = 100L;
    protected String nimi;
    protected String nimiKoodiUri;
    protected List<String> tarjoajaOids;
    protected List<String> koulutusOids;
    protected Integer koulutuksenAlkamisvuosi;
    protected TarjontaTila tilat;
    protected String koulutuksenAlkamiskausi;

    /**
     * Default no-arg constructor
     * 
     */
    public HakukohteetKysely() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public HakukohteetKysely(final String nimi, final String nimiKoodiUri, final List<String> tarjoajaOids, final List<String> koulutusOids, final Integer koulutuksenAlkamisvuosi, final TarjontaTila tilat, final String koulutuksenAlkamiskausi) {
        this.nimi = nimi;
        this.nimiKoodiUri = nimiKoodiUri;
        this.tarjoajaOids = tarjoajaOids;
        this.koulutusOids = koulutusOids;
        this.koulutuksenAlkamisvuosi = koulutuksenAlkamisvuosi;
        this.tilat = tilat;
        this.koulutuksenAlkamiskausi = koulutuksenAlkamiskausi;
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
     * Gets the value of the tilat property.
     * 
     * @return
     *     possible object is
     *     {@link TarjontaTila }
     *     
     */
    public TarjontaTila getTilat() {
        return tilat;
    }

    /**
     * Sets the value of the tilat property.
     * 
     * @param value
     *     allowed object is
     *     {@link TarjontaTila }
     *     
     */
    public void setTilat(TarjontaTila value) {
        this.tilat = value;
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

}