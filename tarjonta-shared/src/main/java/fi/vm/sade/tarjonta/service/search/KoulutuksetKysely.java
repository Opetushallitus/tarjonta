
package fi.vm.sade.tarjonta.service.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fi.vm.sade.tarjonta.service.types.TarjontaTila;


public class KoulutuksetKysely implements Serializable
{

    private final static long serialVersionUID = 100L;
    protected String nimi;
    protected List<String> tarjoajaOids;
    protected List<String> koulutusOids;
    protected TarjontaTila koulutuksenTila;
    protected Integer koulutuksenAlkamisvuosi;
    protected String koulutuksenAlkamiskausi;
    protected String koulutusKoodi;
    protected List<String> hakukohdeOids;

    /**
     * Default no-arg constructor
     * 
     */
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
     * Gets the value of the koulutuksenTila property.
     * 
     * @return
     *     possible object is
     *     {@link TarjontaTila }
     *     
     */
    public TarjontaTila getKoulutuksenTila() {
        return koulutuksenTila;
    }

    /**
     * Sets the value of the koulutuksenTila property.
     * 
     * @param value
     *     allowed object is
     *     {@link TarjontaTila }
     *     
     */
    public void setKoulutuksenTila(TarjontaTila value) {
        this.koulutuksenTila = value;
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

    /**
     * Gets the value of the koulutusKoodi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKoulutusKoodi() {
        return koulutusKoodi;
    }

    /**
     * Sets the value of the koulutusKoodi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKoulutusKoodi(String value) {
        this.koulutusKoodi = value;
    }

    /**
     * Gets the value of the hakukohdeOids property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the hakukohdeOids property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHakukohdeOids().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getHakukohdeOids() {
        if (hakukohdeOids == null) {
            hakukohdeOids = new ArrayList<String>();
        }
        return this.hakukohdeOids;
    }

}
