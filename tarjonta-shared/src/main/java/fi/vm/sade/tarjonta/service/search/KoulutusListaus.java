
package fi.vm.sade.tarjonta.service.search;

import java.io.Serializable;

import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjoajaTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;


public class KoulutusListaus implements Serializable
{

    private final static long serialVersionUID = 100L;
    protected MonikielinenTekstiTyyppi nimi;
    protected String koulutusmoduuli;
    protected String koulutusmoduuliToteutus;
    protected TarjoajaTyyppi tarjoaja;
    protected TarjontaTila tila;
    protected KoodistoKoodiTyyppi koulutuskoodi;
    protected KoodistoKoodiTyyppi koulutusohjelmakoodi;
    protected KoodistoKoodiTyyppi tutkintonimike;
    protected String ajankohta;
    protected String komotoOid;
    protected String pohjakoulutusVaatimus;
    protected String koulutuslaji;
    protected KoulutusasteTyyppi koulutustyyppi;
    protected KoodistoKoodiTyyppi lukiolinjakoodi;
    protected String koulutuksenAlkamiskausiUri;
    protected Integer koulutuksenAlkamisVuosi;

    /**
     * Default no-arg constructor
     * 
     */
    public KoulutusListaus() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public KoulutusListaus(final MonikielinenTekstiTyyppi nimi, final String koulutusmoduuli, final String koulutusmoduuliToteutus, final TarjoajaTyyppi tarjoaja, final TarjontaTila tila, final KoodistoKoodiTyyppi koulutuskoodi, final KoodistoKoodiTyyppi koulutusohjelmakoodi, final KoodistoKoodiTyyppi tutkintonimike, final String ajankohta, final String komotoOid, final String pohjakoulutusVaatimus, final String koulutuslaji, final KoulutusasteTyyppi koulutustyyppi, final KoodistoKoodiTyyppi lukiolinjakoodi, final String koulutuksenAlkamiskausiUri, final Integer koulutuksenAlkamisVuosi) {
        this.nimi = nimi;
        this.koulutusmoduuli = koulutusmoduuli;
        this.koulutusmoduuliToteutus = koulutusmoduuliToteutus;
        this.tarjoaja = tarjoaja;
        this.tila = tila;
        this.koulutuskoodi = koulutuskoodi;
        this.koulutusohjelmakoodi = koulutusohjelmakoodi;
        this.tutkintonimike = tutkintonimike;
        this.ajankohta = ajankohta;
        this.komotoOid = komotoOid;
        this.pohjakoulutusVaatimus = pohjakoulutusVaatimus;
        this.koulutuslaji = koulutuslaji;
        this.koulutustyyppi = koulutustyyppi;
        this.lukiolinjakoodi = lukiolinjakoodi;
        this.koulutuksenAlkamiskausiUri = koulutuksenAlkamiskausiUri;
        this.koulutuksenAlkamisVuosi = koulutuksenAlkamisVuosi;
    }

    /**
     * Gets the value of the nimi property.
     * 
     * @return
     *     possible object is
     *     {@link MonikielinenTekstiTyyppi }
     *     
     */
    public MonikielinenTekstiTyyppi getNimi() {
        return nimi;
    }

    /**
     * Sets the value of the nimi property.
     * 
     * @param value
     *     allowed object is
     *     {@link MonikielinenTekstiTyyppi }
     *     
     */
    public void setNimi(MonikielinenTekstiTyyppi value) {
        this.nimi = value;
    }

    /**
     * Gets the value of the koulutusmoduuli property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKoulutusmoduuli() {
        return koulutusmoduuli;
    }

    /**
     * Sets the value of the koulutusmoduuli property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKoulutusmoduuli(String value) {
        this.koulutusmoduuli = value;
    }

    /**
     * Gets the value of the koulutusmoduuliToteutus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKoulutusmoduuliToteutus() {
        return koulutusmoduuliToteutus;
    }

    /**
     * Sets the value of the koulutusmoduuliToteutus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKoulutusmoduuliToteutus(String value) {
        this.koulutusmoduuliToteutus = value;
    }

    /**
     * Gets the value of the tarjoaja property.
     * 
     * @return
     *     possible object is
     *     {@link TarjoajaTyyppi }
     *     
     */
    public TarjoajaTyyppi getTarjoaja() {
        return tarjoaja;
    }

    /**
     * Sets the value of the tarjoaja property.
     * 
     * @param value
     *     allowed object is
     *     {@link TarjoajaTyyppi }
     *     
     */
    public void setTarjoaja(TarjoajaTyyppi value) {
        this.tarjoaja = value;
    }

    /**
     * Gets the value of the tila property.
     * 
     * @return
     *     possible object is
     *     {@link TarjontaTila }
     *     
     */
    public TarjontaTila getTila() {
        return tila;
    }

    /**
     * Sets the value of the tila property.
     * 
     * @param value
     *     allowed object is
     *     {@link TarjontaTila }
     *     
     */
    public void setTila(TarjontaTila value) {
        this.tila = value;
    }

    /**
     * Gets the value of the koulutuskoodi property.
     * 
     * @return
     *     possible object is
     *     {@link KoodistoKoodiTyyppi }
     *     
     */
    public KoodistoKoodiTyyppi getKoulutuskoodi() {
        return koulutuskoodi;
    }

    /**
     * Sets the value of the koulutuskoodi property.
     * 
     * @param value
     *     allowed object is
     *     {@link KoodistoKoodiTyyppi }
     *     
     */
    public void setKoulutuskoodi(KoodistoKoodiTyyppi value) {
        this.koulutuskoodi = value;
    }

    /**
     * Gets the value of the koulutusohjelmakoodi property.
     * 
     * @return
     *     possible object is
     *     {@link KoodistoKoodiTyyppi }
     *     
     */
    public KoodistoKoodiTyyppi getKoulutusohjelmakoodi() {
        return koulutusohjelmakoodi;
    }

    /**
     * Sets the value of the koulutusohjelmakoodi property.
     * 
     * @param value
     *     allowed object is
     *     {@link KoodistoKoodiTyyppi }
     *     
     */
    public void setKoulutusohjelmakoodi(KoodistoKoodiTyyppi value) {
        this.koulutusohjelmakoodi = value;
    }

    /**
     * Gets the value of the tutkintonimike property.
     * 
     * @return
     *     possible object is
     *     {@link KoodistoKoodiTyyppi }
     *     
     */
    public KoodistoKoodiTyyppi getTutkintonimike() {
        return tutkintonimike;
    }

    /**
     * Sets the value of the tutkintonimike property.
     * 
     * @param value
     *     allowed object is
     *     {@link KoodistoKoodiTyyppi }
     *     
     */
    public void setTutkintonimike(KoodistoKoodiTyyppi value) {
        this.tutkintonimike = value;
    }

    /**
     * Gets the value of the ajankohta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAjankohta() {
        return ajankohta;
    }

    /**
     * Sets the value of the ajankohta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAjankohta(String value) {
        this.ajankohta = value;
    }

    /**
     * Gets the value of the komotoOid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKomotoOid() {
        return komotoOid;
    }

    /**
     * Sets the value of the komotoOid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKomotoOid(String value) {
        this.komotoOid = value;
    }

    /**
     * Gets the value of the pohjakoulutusVaatimus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPohjakoulutusVaatimus() {
        return pohjakoulutusVaatimus;
    }

    /**
     * Sets the value of the pohjakoulutusVaatimus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPohjakoulutusVaatimus(String value) {
        this.pohjakoulutusVaatimus = value;
    }

    /**
     * Gets the value of the koulutuslaji property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKoulutuslaji() {
        return koulutuslaji;
    }

    /**
     * Sets the value of the koulutuslaji property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKoulutuslaji(String value) {
        this.koulutuslaji = value;
    }

    /**
     * Gets the value of the koulutustyyppi property.
     * 
     * @return
     *     possible object is
     *     {@link KoulutusasteTyyppi }
     *     
     */
    public KoulutusasteTyyppi getKoulutustyyppi() {
        return koulutustyyppi;
    }

    /**
     * Sets the value of the koulutustyyppi property.
     * 
     * @param value
     *     allowed object is
     *     {@link KoulutusasteTyyppi }
     *     
     */
    public void setKoulutustyyppi(KoulutusasteTyyppi value) {
        this.koulutustyyppi = value;
    }

    /**
     * Gets the value of the lukiolinjakoodi property.
     * 
     * @return
     *     possible object is
     *     {@link KoodistoKoodiTyyppi }
     *     
     */
    public KoodistoKoodiTyyppi getLukiolinjakoodi() {
        return lukiolinjakoodi;
    }

    /**
     * Sets the value of the lukiolinjakoodi property.
     * 
     * @param value
     *     allowed object is
     *     {@link KoodistoKoodiTyyppi }
     *     
     */
    public void setLukiolinjakoodi(KoodistoKoodiTyyppi value) {
        this.lukiolinjakoodi = value;
    }

    /**
     * Gets the value of the koulutuksenAlkamiskausiUri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKoulutuksenAlkamiskausiUri() {
        return koulutuksenAlkamiskausiUri;
    }

    /**
     * Sets the value of the koulutuksenAlkamiskausiUri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKoulutuksenAlkamiskausiUri(String value) {
        this.koulutuksenAlkamiskausiUri = value;
    }

    /**
     * Gets the value of the koulutuksenAlkamisVuosi property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getKoulutuksenAlkamisVuosi() {
        return koulutuksenAlkamisVuosi;
    }

    /**
     * Sets the value of the koulutuksenAlkamisVuosi property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setKoulutuksenAlkamisVuosi(Integer value) {
        this.koulutuksenAlkamisVuosi = value;
    }

}
