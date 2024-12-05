
package fi.vm.sade.tarjonta.service.types;

import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter1;


/**
 * <p>Java class for HakukohdeLiiteTyyppi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HakukohdeLiiteTyyppi"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="liitteenId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="liitteenTyyppi" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="liitteenKuvaus" type="{http://service.tarjonta.sade.vm.fi/types}MonikielinenTekstiTyyppi" minOccurs="0"/&gt;
 *         &lt;element name="toimitettavaMennessa" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="liitteenToimitusOsoite" type="{http://service.tarjonta.sade.vm.fi/types}OsoiteTyyppi"/&gt;
 *         &lt;element name="sahkoinenToimitusOsoite" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="liitteenTyyppiKoodistoNimi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="viimeisinPaivittajaOid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="viimeisinPaivitysPvm" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HakukohdeLiiteTyyppi", propOrder = {
    "liitteenId",
    "liitteenTyyppi",
    "liitteenKuvaus",
    "toimitettavaMennessa",
    "liitteenToimitusOsoite",
    "sahkoinenToimitusOsoite",
    "liitteenTyyppiKoodistoNimi",
    "viimeisinPaivittajaOid",
    "viimeisinPaivitysPvm"
})
public class HakukohdeLiiteTyyppi implements Serializable
{

    private final static long serialVersionUID = 100L;
    protected String liitteenId;
    @XmlElement(required = true)
    protected String liitteenTyyppi;
    protected MonikielinenTekstiTyyppi liitteenKuvaus;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date toimitettavaMennessa;
    @XmlElement(required = true)
    protected OsoiteTyyppi liitteenToimitusOsoite;
    protected String sahkoinenToimitusOsoite;
    protected String liitteenTyyppiKoodistoNimi;
    protected String viimeisinPaivittajaOid;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date viimeisinPaivitysPvm;

    /**
     * Default no-arg constructor
     * 
     */
    public HakukohdeLiiteTyyppi() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public HakukohdeLiiteTyyppi(final String liitteenId, final String liitteenTyyppi, final MonikielinenTekstiTyyppi liitteenKuvaus, final Date toimitettavaMennessa, final OsoiteTyyppi liitteenToimitusOsoite, final String sahkoinenToimitusOsoite, final String liitteenTyyppiKoodistoNimi, final String viimeisinPaivittajaOid, final Date viimeisinPaivitysPvm) {
        this.liitteenId = liitteenId;
        this.liitteenTyyppi = liitteenTyyppi;
        this.liitteenKuvaus = liitteenKuvaus;
        this.toimitettavaMennessa = toimitettavaMennessa;
        this.liitteenToimitusOsoite = liitteenToimitusOsoite;
        this.sahkoinenToimitusOsoite = sahkoinenToimitusOsoite;
        this.liitteenTyyppiKoodistoNimi = liitteenTyyppiKoodistoNimi;
        this.viimeisinPaivittajaOid = viimeisinPaivittajaOid;
        this.viimeisinPaivitysPvm = viimeisinPaivitysPvm;
    }

    /**
     * Gets the value of the liitteenId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLiitteenId() {
        return liitteenId;
    }

    /**
     * Sets the value of the liitteenId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLiitteenId(String value) {
        this.liitteenId = value;
    }

    /**
     * Gets the value of the liitteenTyyppi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLiitteenTyyppi() {
        return liitteenTyyppi;
    }

    /**
     * Sets the value of the liitteenTyyppi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLiitteenTyyppi(String value) {
        this.liitteenTyyppi = value;
    }

    /**
     * Gets the value of the liitteenKuvaus property.
     * 
     * @return
     *     possible object is
     *     {@link MonikielinenTekstiTyyppi }
     *     
     */
    public MonikielinenTekstiTyyppi getLiitteenKuvaus() {
        return liitteenKuvaus;
    }

    /**
     * Sets the value of the liitteenKuvaus property.
     * 
     * @param value
     *     allowed object is
     *     {@link MonikielinenTekstiTyyppi }
     *     
     */
    public void setLiitteenKuvaus(MonikielinenTekstiTyyppi value) {
        this.liitteenKuvaus = value;
    }

    /**
     * Gets the value of the toimitettavaMennessa property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getToimitettavaMennessa() {
        return toimitettavaMennessa;
    }

    /**
     * Sets the value of the toimitettavaMennessa property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToimitettavaMennessa(Date value) {
        this.toimitettavaMennessa = value;
    }

    /**
     * Gets the value of the liitteenToimitusOsoite property.
     * 
     * @return
     *     possible object is
     *     {@link OsoiteTyyppi }
     *     
     */
    public OsoiteTyyppi getLiitteenToimitusOsoite() {
        return liitteenToimitusOsoite;
    }

    /**
     * Sets the value of the liitteenToimitusOsoite property.
     * 
     * @param value
     *     allowed object is
     *     {@link OsoiteTyyppi }
     *     
     */
    public void setLiitteenToimitusOsoite(OsoiteTyyppi value) {
        this.liitteenToimitusOsoite = value;
    }

    /**
     * Gets the value of the sahkoinenToimitusOsoite property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSahkoinenToimitusOsoite() {
        return sahkoinenToimitusOsoite;
    }

    /**
     * Sets the value of the sahkoinenToimitusOsoite property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSahkoinenToimitusOsoite(String value) {
        this.sahkoinenToimitusOsoite = value;
    }

    /**
     * Gets the value of the liitteenTyyppiKoodistoNimi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLiitteenTyyppiKoodistoNimi() {
        return liitteenTyyppiKoodistoNimi;
    }

    /**
     * Sets the value of the liitteenTyyppiKoodistoNimi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLiitteenTyyppiKoodistoNimi(String value) {
        this.liitteenTyyppiKoodistoNimi = value;
    }

    /**
     * Gets the value of the viimeisinPaivittajaOid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getViimeisinPaivittajaOid() {
        return viimeisinPaivittajaOid;
    }

    /**
     * Sets the value of the viimeisinPaivittajaOid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setViimeisinPaivittajaOid(String value) {
        this.viimeisinPaivittajaOid = value;
    }

    /**
     * Gets the value of the viimeisinPaivitysPvm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getViimeisinPaivitysPvm() {
        return viimeisinPaivitysPvm;
    }

    /**
     * Sets the value of the viimeisinPaivitysPvm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setViimeisinPaivitysPvm(Date value) {
        this.viimeisinPaivitysPvm = value;
    }

}
