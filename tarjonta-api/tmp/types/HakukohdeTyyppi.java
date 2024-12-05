
package fi.vm.sade.tarjonta.service.types;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter1;


/**
 * 
 *                 yksittaisen atomisen hakukohteen kuvaava elementti.
 *             
 * 
 * <p>Java class for HakukohdeTyyppi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HakukohdeTyyppi"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="oid" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="hakukohdeNimi" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="hakukohdeKoodistoNimi" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="hakukohteenHakuOid" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="hakukohteenHakutyyppiUri" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="sisaisetHakuajat" type="{http://service.tarjonta.sade.vm.fi/types}SisaisetHakuAjat" minOccurs="0"/&gt;
 *         &lt;element name="hakukohteenHaunNimi" type="{http://service.tarjonta.sade.vm.fi/types}MonikielinenTekstiTyyppi" minOccurs="0"/&gt;
 *         &lt;element name="hakukohteenTila" type="{http://service.tarjonta.sade.vm.fi/types}TarjontaTila"/&gt;
 *         &lt;element name="hakukohteenKoulutusOidit" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/&gt;
 *         &lt;element name="aloituspaikat" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="valinnanAloituspaikat" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="hakukelpoisuusVaatimukset" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="lisatiedot" type="{http://service.tarjonta.sade.vm.fi/types}MonikielinenTekstiTyyppi" minOccurs="0"/&gt;
 *         &lt;element name="liitteidenToimitusOsoite" type="{http://service.tarjonta.sade.vm.fi/types}OsoiteTyyppi"/&gt;
 *         &lt;element name="sahkoinenToimitusOsoite" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="liitteidenToimitusPvm" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="kaytetaanHaunPaattymisenAikaa" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="kaksoisTutkinto" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="hakukohdeKoulutukses" type="{http://service.tarjonta.sade.vm.fi/types}KoulutusKoosteTyyppi" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="valintaperustekuvausKoodiUri" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="valintaperustekuvausTeksti" type="{http://service.tarjonta.sade.vm.fi/types}MonikielinenTekstiTyyppi" minOccurs="0"/&gt;
 *         &lt;element name="soraKuvausKoodiUri" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="soraKuvausTeksti" type="{http://service.tarjonta.sade.vm.fi/types}MonikielinenTekstiTyyppi" minOccurs="0"/&gt;
 *         &lt;element name="painotettavatOppiaineet" type="{http://service.tarjonta.sade.vm.fi/types}PainotettavaOppiaineTyyppi" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="alinHyvaksyttavaKeskiarvo" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="hakukohteenKoulutusaste" type="{http://service.tarjonta.sade.vm.fi/types}KoulutusasteTyyppi" minOccurs="0"/&gt;
 *         &lt;element name="viimeisinPaivittajaOid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="viimeisinPaivitysPvm" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="hakuaikaAlkuPvm" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="hakuaikaLoppuPvm" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="opetuskieliUris" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="Version" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HakukohdeTyyppi", propOrder = {
    "oid",
    "hakukohdeNimi",
    "hakukohdeKoodistoNimi",
    "hakukohteenHakuOid",
    "hakukohteenHakutyyppiUri",
    "sisaisetHakuajat",
    "hakukohteenHaunNimi",
    "hakukohteenTila",
    "hakukohteenKoulutusOidit",
    "aloituspaikat",
    "valinnanAloituspaikat",
    "hakukelpoisuusVaatimukset",
    "lisatiedot",
    "liitteidenToimitusOsoite",
    "sahkoinenToimitusOsoite",
    "liitteidenToimitusPvm",
    "kaytetaanHaunPaattymisenAikaa",
    "kaksoisTutkinto",
    "hakukohdeKoulutukses",
    "valintaperustekuvausKoodiUri",
    "valintaperustekuvausTeksti",
    "soraKuvausKoodiUri",
    "soraKuvausTeksti",
    "painotettavatOppiaineet",
    "alinHyvaksyttavaKeskiarvo",
    "hakukohteenKoulutusaste",
    "viimeisinPaivittajaOid",
    "viimeisinPaivitysPvm",
    "hakuaikaAlkuPvm",
    "hakuaikaLoppuPvm",
    "opetuskieliUris",
    "version"
})
public class HakukohdeTyyppi implements Serializable
{

    private final static long serialVersionUID = 100L;
    @XmlElement(required = true)
    protected String oid;
    @XmlElement(required = true)
    protected String hakukohdeNimi;
    @XmlElement(required = true)
    protected String hakukohdeKoodistoNimi;
    @XmlElement(required = true)
    protected String hakukohteenHakuOid;
    @XmlElement(required = true)
    protected String hakukohteenHakutyyppiUri;
    protected SisaisetHakuAjat sisaisetHakuajat;
    protected MonikielinenTekstiTyyppi hakukohteenHaunNimi;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected TarjontaTila hakukohteenTila;
    @XmlElement(required = true)
    protected List<String> hakukohteenKoulutusOidit;
    protected Integer aloituspaikat;
    protected Integer valinnanAloituspaikat;
    @XmlElement(required = true)
    protected String hakukelpoisuusVaatimukset;
    protected MonikielinenTekstiTyyppi lisatiedot;
    @XmlElement(required = true)
    protected OsoiteTyyppi liitteidenToimitusOsoite;
    protected String sahkoinenToimitusOsoite;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date liitteidenToimitusPvm;
    protected Boolean kaytetaanHaunPaattymisenAikaa;
    protected boolean kaksoisTutkinto;
    protected List<KoulutusKoosteTyyppi> hakukohdeKoulutukses;
    protected String valintaperustekuvausKoodiUri;
    protected MonikielinenTekstiTyyppi valintaperustekuvausTeksti;
    protected String soraKuvausKoodiUri;
    protected MonikielinenTekstiTyyppi soraKuvausTeksti;
    protected List<PainotettavaOppiaineTyyppi> painotettavatOppiaineet;
    protected BigDecimal alinHyvaksyttavaKeskiarvo;
    @XmlSchemaType(name = "string")
    protected KoulutusasteTyyppi hakukohteenKoulutusaste;
    protected String viimeisinPaivittajaOid;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date viimeisinPaivitysPvm;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date hakuaikaAlkuPvm;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date hakuaikaLoppuPvm;
    protected List<String> opetuskieliUris;
    @XmlElement(name = "Version")
    protected Long version;

    /**
     * Default no-arg constructor
     * 
     */
    public HakukohdeTyyppi() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public HakukohdeTyyppi(final String oid, final String hakukohdeNimi, final String hakukohdeKoodistoNimi, final String hakukohteenHakuOid, final String hakukohteenHakutyyppiUri, final SisaisetHakuAjat sisaisetHakuajat, final MonikielinenTekstiTyyppi hakukohteenHaunNimi, final TarjontaTila hakukohteenTila, final List<String> hakukohteenKoulutusOidit, final Integer aloituspaikat, final Integer valinnanAloituspaikat, final String hakukelpoisuusVaatimukset, final MonikielinenTekstiTyyppi lisatiedot, final OsoiteTyyppi liitteidenToimitusOsoite, final String sahkoinenToimitusOsoite, final Date liitteidenToimitusPvm, final Boolean kaytetaanHaunPaattymisenAikaa, final boolean kaksoisTutkinto, final List<KoulutusKoosteTyyppi> hakukohdeKoulutukses, final String valintaperustekuvausKoodiUri, final MonikielinenTekstiTyyppi valintaperustekuvausTeksti, final String soraKuvausKoodiUri, final MonikielinenTekstiTyyppi soraKuvausTeksti, final List<PainotettavaOppiaineTyyppi> painotettavatOppiaineet, final BigDecimal alinHyvaksyttavaKeskiarvo, final KoulutusasteTyyppi hakukohteenKoulutusaste, final String viimeisinPaivittajaOid, final Date viimeisinPaivitysPvm, final Date hakuaikaAlkuPvm, final Date hakuaikaLoppuPvm, final List<String> opetuskieliUris, final Long version) {
        this.oid = oid;
        this.hakukohdeNimi = hakukohdeNimi;
        this.hakukohdeKoodistoNimi = hakukohdeKoodistoNimi;
        this.hakukohteenHakuOid = hakukohteenHakuOid;
        this.hakukohteenHakutyyppiUri = hakukohteenHakutyyppiUri;
        this.sisaisetHakuajat = sisaisetHakuajat;
        this.hakukohteenHaunNimi = hakukohteenHaunNimi;
        this.hakukohteenTila = hakukohteenTila;
        this.hakukohteenKoulutusOidit = hakukohteenKoulutusOidit;
        this.aloituspaikat = aloituspaikat;
        this.valinnanAloituspaikat = valinnanAloituspaikat;
        this.hakukelpoisuusVaatimukset = hakukelpoisuusVaatimukset;
        this.lisatiedot = lisatiedot;
        this.liitteidenToimitusOsoite = liitteidenToimitusOsoite;
        this.sahkoinenToimitusOsoite = sahkoinenToimitusOsoite;
        this.liitteidenToimitusPvm = liitteidenToimitusPvm;
        this.kaytetaanHaunPaattymisenAikaa = kaytetaanHaunPaattymisenAikaa;
        this.kaksoisTutkinto = kaksoisTutkinto;
        this.hakukohdeKoulutukses = hakukohdeKoulutukses;
        this.valintaperustekuvausKoodiUri = valintaperustekuvausKoodiUri;
        this.valintaperustekuvausTeksti = valintaperustekuvausTeksti;
        this.soraKuvausKoodiUri = soraKuvausKoodiUri;
        this.soraKuvausTeksti = soraKuvausTeksti;
        this.painotettavatOppiaineet = painotettavatOppiaineet;
        this.alinHyvaksyttavaKeskiarvo = alinHyvaksyttavaKeskiarvo;
        this.hakukohteenKoulutusaste = hakukohteenKoulutusaste;
        this.viimeisinPaivittajaOid = viimeisinPaivittajaOid;
        this.viimeisinPaivitysPvm = viimeisinPaivitysPvm;
        this.hakuaikaAlkuPvm = hakuaikaAlkuPvm;
        this.hakuaikaLoppuPvm = hakuaikaLoppuPvm;
        this.opetuskieliUris = opetuskieliUris;
        this.version = version;
    }

    /**
     * Gets the value of the oid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOid() {
        return oid;
    }

    /**
     * Sets the value of the oid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOid(String value) {
        this.oid = value;
    }

    /**
     * Gets the value of the hakukohdeNimi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHakukohdeNimi() {
        return hakukohdeNimi;
    }

    /**
     * Sets the value of the hakukohdeNimi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHakukohdeNimi(String value) {
        this.hakukohdeNimi = value;
    }

    /**
     * Gets the value of the hakukohdeKoodistoNimi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHakukohdeKoodistoNimi() {
        return hakukohdeKoodistoNimi;
    }

    /**
     * Sets the value of the hakukohdeKoodistoNimi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHakukohdeKoodistoNimi(String value) {
        this.hakukohdeKoodistoNimi = value;
    }

    /**
     * Gets the value of the hakukohteenHakuOid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHakukohteenHakuOid() {
        return hakukohteenHakuOid;
    }

    /**
     * Sets the value of the hakukohteenHakuOid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHakukohteenHakuOid(String value) {
        this.hakukohteenHakuOid = value;
    }

    /**
     * Gets the value of the hakukohteenHakutyyppiUri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHakukohteenHakutyyppiUri() {
        return hakukohteenHakutyyppiUri;
    }

    /**
     * Sets the value of the hakukohteenHakutyyppiUri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHakukohteenHakutyyppiUri(String value) {
        this.hakukohteenHakutyyppiUri = value;
    }

    /**
     * Gets the value of the sisaisetHakuajat property.
     * 
     * @return
     *     possible object is
     *     {@link SisaisetHakuAjat }
     *     
     */
    public SisaisetHakuAjat getSisaisetHakuajat() {
        return sisaisetHakuajat;
    }

    /**
     * Sets the value of the sisaisetHakuajat property.
     * 
     * @param value
     *     allowed object is
     *     {@link SisaisetHakuAjat }
     *     
     */
    public void setSisaisetHakuajat(SisaisetHakuAjat value) {
        this.sisaisetHakuajat = value;
    }

    /**
     * Gets the value of the hakukohteenHaunNimi property.
     * 
     * @return
     *     possible object is
     *     {@link MonikielinenTekstiTyyppi }
     *     
     */
    public MonikielinenTekstiTyyppi getHakukohteenHaunNimi() {
        return hakukohteenHaunNimi;
    }

    /**
     * Sets the value of the hakukohteenHaunNimi property.
     * 
     * @param value
     *     allowed object is
     *     {@link MonikielinenTekstiTyyppi }
     *     
     */
    public void setHakukohteenHaunNimi(MonikielinenTekstiTyyppi value) {
        this.hakukohteenHaunNimi = value;
    }

    /**
     * Gets the value of the hakukohteenTila property.
     * 
     * @return
     *     possible object is
     *     {@link TarjontaTila }
     *     
     */
    public TarjontaTila getHakukohteenTila() {
        return hakukohteenTila;
    }

    /**
     * Sets the value of the hakukohteenTila property.
     * 
     * @param value
     *     allowed object is
     *     {@link TarjontaTila }
     *     
     */
    public void setHakukohteenTila(TarjontaTila value) {
        this.hakukohteenTila = value;
    }

    /**
     * Gets the value of the hakukohteenKoulutusOidit property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the hakukohteenKoulutusOidit property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHakukohteenKoulutusOidit().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getHakukohteenKoulutusOidit() {
        if (hakukohteenKoulutusOidit == null) {
            hakukohteenKoulutusOidit = new ArrayList<String>();
        }
        return this.hakukohteenKoulutusOidit;
    }

    /**
     * Gets the value of the aloituspaikat property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAloituspaikat() {
        return aloituspaikat;
    }

    /**
     * Sets the value of the aloituspaikat property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAloituspaikat(Integer value) {
        this.aloituspaikat = value;
    }

    /**
     * Gets the value of the valinnanAloituspaikat property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getValinnanAloituspaikat() {
        return valinnanAloituspaikat;
    }

    /**
     * Sets the value of the valinnanAloituspaikat property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setValinnanAloituspaikat(Integer value) {
        this.valinnanAloituspaikat = value;
    }

    /**
     * Gets the value of the hakukelpoisuusVaatimukset property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHakukelpoisuusVaatimukset() {
        return hakukelpoisuusVaatimukset;
    }

    /**
     * Sets the value of the hakukelpoisuusVaatimukset property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHakukelpoisuusVaatimukset(String value) {
        this.hakukelpoisuusVaatimukset = value;
    }

    /**
     * Gets the value of the lisatiedot property.
     * 
     * @return
     *     possible object is
     *     {@link MonikielinenTekstiTyyppi }
     *     
     */
    public MonikielinenTekstiTyyppi getLisatiedot() {
        return lisatiedot;
    }

    /**
     * Sets the value of the lisatiedot property.
     * 
     * @param value
     *     allowed object is
     *     {@link MonikielinenTekstiTyyppi }
     *     
     */
    public void setLisatiedot(MonikielinenTekstiTyyppi value) {
        this.lisatiedot = value;
    }

    /**
     * Gets the value of the liitteidenToimitusOsoite property.
     * 
     * @return
     *     possible object is
     *     {@link OsoiteTyyppi }
     *     
     */
    public OsoiteTyyppi getLiitteidenToimitusOsoite() {
        return liitteidenToimitusOsoite;
    }

    /**
     * Sets the value of the liitteidenToimitusOsoite property.
     * 
     * @param value
     *     allowed object is
     *     {@link OsoiteTyyppi }
     *     
     */
    public void setLiitteidenToimitusOsoite(OsoiteTyyppi value) {
        this.liitteidenToimitusOsoite = value;
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
     * Gets the value of the liitteidenToimitusPvm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getLiitteidenToimitusPvm() {
        return liitteidenToimitusPvm;
    }

    /**
     * Sets the value of the liitteidenToimitusPvm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLiitteidenToimitusPvm(Date value) {
        this.liitteidenToimitusPvm = value;
    }

    /**
     * Gets the value of the kaytetaanHaunPaattymisenAikaa property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isKaytetaanHaunPaattymisenAikaa() {
        return kaytetaanHaunPaattymisenAikaa;
    }

    /**
     * Sets the value of the kaytetaanHaunPaattymisenAikaa property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setKaytetaanHaunPaattymisenAikaa(Boolean value) {
        this.kaytetaanHaunPaattymisenAikaa = value;
    }

    /**
     * Gets the value of the kaksoisTutkinto property.
     * 
     */
    public boolean isKaksoisTutkinto() {
        return kaksoisTutkinto;
    }

    /**
     * Sets the value of the kaksoisTutkinto property.
     * 
     */
    public void setKaksoisTutkinto(boolean value) {
        this.kaksoisTutkinto = value;
    }

    /**
     * Gets the value of the hakukohdeKoulutukses property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the hakukohdeKoulutukses property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHakukohdeKoulutukses().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KoulutusKoosteTyyppi }
     * 
     * 
     */
    public List<KoulutusKoosteTyyppi> getHakukohdeKoulutukses() {
        if (hakukohdeKoulutukses == null) {
            hakukohdeKoulutukses = new ArrayList<KoulutusKoosteTyyppi>();
        }
        return this.hakukohdeKoulutukses;
    }

    /**
     * Gets the value of the valintaperustekuvausKoodiUri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValintaperustekuvausKoodiUri() {
        return valintaperustekuvausKoodiUri;
    }

    /**
     * Sets the value of the valintaperustekuvausKoodiUri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValintaperustekuvausKoodiUri(String value) {
        this.valintaperustekuvausKoodiUri = value;
    }

    /**
     * Gets the value of the valintaperustekuvausTeksti property.
     * 
     * @return
     *     possible object is
     *     {@link MonikielinenTekstiTyyppi }
     *     
     */
    public MonikielinenTekstiTyyppi getValintaperustekuvausTeksti() {
        return valintaperustekuvausTeksti;
    }

    /**
     * Sets the value of the valintaperustekuvausTeksti property.
     * 
     * @param value
     *     allowed object is
     *     {@link MonikielinenTekstiTyyppi }
     *     
     */
    public void setValintaperustekuvausTeksti(MonikielinenTekstiTyyppi value) {
        this.valintaperustekuvausTeksti = value;
    }

    /**
     * Gets the value of the soraKuvausKoodiUri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSoraKuvausKoodiUri() {
        return soraKuvausKoodiUri;
    }

    /**
     * Sets the value of the soraKuvausKoodiUri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSoraKuvausKoodiUri(String value) {
        this.soraKuvausKoodiUri = value;
    }

    /**
     * Gets the value of the soraKuvausTeksti property.
     * 
     * @return
     *     possible object is
     *     {@link MonikielinenTekstiTyyppi }
     *     
     */
    public MonikielinenTekstiTyyppi getSoraKuvausTeksti() {
        return soraKuvausTeksti;
    }

    /**
     * Sets the value of the soraKuvausTeksti property.
     * 
     * @param value
     *     allowed object is
     *     {@link MonikielinenTekstiTyyppi }
     *     
     */
    public void setSoraKuvausTeksti(MonikielinenTekstiTyyppi value) {
        this.soraKuvausTeksti = value;
    }

    /**
     * Gets the value of the painotettavatOppiaineet property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the painotettavatOppiaineet property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPainotettavatOppiaineet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PainotettavaOppiaineTyyppi }
     * 
     * 
     */
    public List<PainotettavaOppiaineTyyppi> getPainotettavatOppiaineet() {
        if (painotettavatOppiaineet == null) {
            painotettavatOppiaineet = new ArrayList<PainotettavaOppiaineTyyppi>();
        }
        return this.painotettavatOppiaineet;
    }

    /**
     * Gets the value of the alinHyvaksyttavaKeskiarvo property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAlinHyvaksyttavaKeskiarvo() {
        return alinHyvaksyttavaKeskiarvo;
    }

    /**
     * Sets the value of the alinHyvaksyttavaKeskiarvo property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAlinHyvaksyttavaKeskiarvo(BigDecimal value) {
        this.alinHyvaksyttavaKeskiarvo = value;
    }

    /**
     * Gets the value of the hakukohteenKoulutusaste property.
     * 
     * @return
     *     possible object is
     *     {@link KoulutusasteTyyppi }
     *     
     */
    public KoulutusasteTyyppi getHakukohteenKoulutusaste() {
        return hakukohteenKoulutusaste;
    }

    /**
     * Sets the value of the hakukohteenKoulutusaste property.
     * 
     * @param value
     *     allowed object is
     *     {@link KoulutusasteTyyppi }
     *     
     */
    public void setHakukohteenKoulutusaste(KoulutusasteTyyppi value) {
        this.hakukohteenKoulutusaste = value;
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

    /**
     * Gets the value of the hakuaikaAlkuPvm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getHakuaikaAlkuPvm() {
        return hakuaikaAlkuPvm;
    }

    /**
     * Sets the value of the hakuaikaAlkuPvm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHakuaikaAlkuPvm(Date value) {
        this.hakuaikaAlkuPvm = value;
    }

    /**
     * Gets the value of the hakuaikaLoppuPvm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getHakuaikaLoppuPvm() {
        return hakuaikaLoppuPvm;
    }

    /**
     * Sets the value of the hakuaikaLoppuPvm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHakuaikaLoppuPvm(Date value) {
        this.hakuaikaLoppuPvm = value;
    }

    /**
     * Gets the value of the opetuskieliUris property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the opetuskieliUris property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOpetuskieliUris().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getOpetuskieliUris() {
        if (opetuskieliUris == null) {
            opetuskieliUris = new ArrayList<String>();
        }
        return this.opetuskieliUris;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setVersion(Long value) {
        this.version = value;
    }

}
