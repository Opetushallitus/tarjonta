
package fi.vm.sade.tarjonta.service.search;

import java.io.Serializable;
import java.util.Date;

import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjoajaTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;

public class HakukohdeListaus implements Serializable
{

    private final static long serialVersionUID = 100L;
    protected String oid;
    protected MonikielinenTekstiTyyppi nimi;
    protected String koodistoNimi;
    protected TarjontaTila tila;
    protected String aloituspaikat;
    protected String koulutuksenAlkamiskausiUri;
    protected String koulutuksenAlkamisvuosi;
    protected KoodistoKoodiTyyppi hakutapaKoodi;
    protected TarjoajaTyyppi tarjoaja;
    protected Date hakuAlkamisPvm;
    protected Date hakuPaattymisPvm;
    protected MonikielinenTekstiTyyppi hakukohteenKoulutuslaji;
    protected String hakutyyppiUri;


    /**
     * Default no-arg constructor
     * 
     */
    public HakukohdeListaus() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public HakukohdeListaus(final String oid, final MonikielinenTekstiTyyppi nimi, final String koodistoNimi, final TarjontaTila tila, final String aloituspaikat, final String koulutuksenAlkamiskausiUri, final String koulutuksenAlkamisvuosi, final KoodistoKoodiTyyppi hakutapaKoodi, final TarjoajaTyyppi tarjoaja, final Date hakuAlkamisPvm, final Date hakuPaattymisPvm, final MonikielinenTekstiTyyppi hakukohteenKoulutuslaji, final String hakutyyppiUri) {
        this.oid = oid;
        this.nimi = nimi;
        this.koodistoNimi = koodistoNimi;
        this.tila = tila;
        this.aloituspaikat = aloituspaikat;
        this.koulutuksenAlkamiskausiUri = koulutuksenAlkamiskausiUri;
        this.koulutuksenAlkamisvuosi = koulutuksenAlkamisvuosi;
        this.hakutapaKoodi = hakutapaKoodi;
        this.tarjoaja = tarjoaja;
        this.hakuAlkamisPvm = hakuAlkamisPvm;
        this.hakuPaattymisPvm = hakuPaattymisPvm;
        this.hakukohteenKoulutuslaji = hakukohteenKoulutuslaji;
        this.hakutyyppiUri = hakutyyppiUri;
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
     * Gets the value of the koodistoNimi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKoodistoNimi() {
        return koodistoNimi;
    }

    /**
     * Sets the value of the koodistoNimi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKoodistoNimi(String value) {
        this.koodistoNimi = value;
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
     * Gets the value of the aloituspaikat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAloituspaikat() {
        return aloituspaikat;
    }

    /**
     * Sets the value of the aloituspaikat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAloituspaikat(String value) {
        this.aloituspaikat = value;
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
     * Gets the value of the koulutuksenAlkamisvuosi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKoulutuksenAlkamisvuosi() {
        return koulutuksenAlkamisvuosi;
    }

    /**
     * Sets the value of the koulutuksenAlkamisvuosi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKoulutuksenAlkamisvuosi(String value) {
        this.koulutuksenAlkamisvuosi = value;
    }

    /**
     * Gets the value of the hakutapaKoodi property.
     * 
     * @return
     *     possible object is
     *     {@link KoodistoKoodiTyyppi }
     *     
     */
    public KoodistoKoodiTyyppi getHakutapaKoodi() {
        return hakutapaKoodi;
    }

    /**
     * Sets the value of the hakutapaKoodi property.
     * 
     * @param value
     *     allowed object is
     *     {@link KoodistoKoodiTyyppi }
     *     
     */
    public void setHakutapaKoodi(KoodistoKoodiTyyppi value) {
        this.hakutapaKoodi = value;
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
     * Gets the value of the hakuAlkamisPvm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getHakuAlkamisPvm() {
        return hakuAlkamisPvm;
    }

    /**
     * Sets the value of the hakuAlkamisPvm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHakuAlkamisPvm(Date value) {
        this.hakuAlkamisPvm = value;
    }

    /**
     * Gets the value of the hakuPaattymisPvm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getHakuPaattymisPvm() {
        return hakuPaattymisPvm;
    }

    /**
     * Sets the value of the hakuPaattymisPvm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHakuPaattymisPvm(Date value) {
        this.hakuPaattymisPvm = value;
    }

    /**
     * Gets the value of the hakukohteenKoulutuslaji property.
     * 
     * @return
     *     possible object is
     *     {@link MonikielinenTekstiTyyppi }
     *     
     */
    public MonikielinenTekstiTyyppi getHakukohteenKoulutuslaji() {
        return hakukohteenKoulutuslaji;
    }

    /**
     * Sets the value of the hakukohteenKoulutuslaji property.
     * 
     * @param value
     *     allowed object is
     *     {@link MonikielinenTekstiTyyppi }
     *     
     */
    public void setHakukohteenKoulutuslaji(MonikielinenTekstiTyyppi value) {
        this.hakukohteenKoulutuslaji = value;
    }
    

    public String getHakutyyppiUri() {
        return hakutyyppiUri;
    }

    public void setHakutyyppiUri(String hakutyyppiUri) {
        this.hakutyyppiUri = hakutyyppiUri;
    }

}
