
package fi.vm.sade.tarjonta.service.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LisaaKoulutusHakukohteelleTyyppi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LisaaKoulutusHakukohteelleTyyppi"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="hakukohdeOid" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="koulutusOids" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/&gt;
 *         &lt;element name="lisaa" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LisaaKoulutusHakukohteelleTyyppi", propOrder = {
    "hakukohdeOid",
    "koulutusOids",
    "lisaa"
})
public class LisaaKoulutusHakukohteelleTyyppi implements Serializable
{

    private final static long serialVersionUID = 100L;
    @XmlElement(required = true)
    protected String hakukohdeOid;
    @XmlElement(required = true)
    protected List<String> koulutusOids;
    protected boolean lisaa;

    /**
     * Default no-arg constructor
     * 
     */
    public LisaaKoulutusHakukohteelleTyyppi() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public LisaaKoulutusHakukohteelleTyyppi(final String hakukohdeOid, final List<String> koulutusOids, final boolean lisaa) {
        this.hakukohdeOid = hakukohdeOid;
        this.koulutusOids = koulutusOids;
        this.lisaa = lisaa;
    }

    /**
     * Gets the value of the hakukohdeOid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHakukohdeOid() {
        return hakukohdeOid;
    }

    /**
     * Sets the value of the hakukohdeOid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHakukohdeOid(String value) {
        this.hakukohdeOid = value;
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
     * Gets the value of the lisaa property.
     * 
     */
    public boolean isLisaa() {
        return lisaa;
    }

    /**
     * Sets the value of the lisaa property.
     * 
     */
    public void setLisaa(boolean value) {
        this.lisaa = value;
    }

}
