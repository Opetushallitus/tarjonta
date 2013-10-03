
package fi.vm.sade.tarjonta.service.search;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;

public class HakukohdePerustieto implements Serializable
{
    
    private final static long serialVersionUID = 100L;
    private String oid;
    private Map<String, String> nimi = new HashMap<String, String>();
    private String koodistoNimi;
    private TarjontaTila tila;
    private String aloituspaikat;
    private String koulutuksenAlkamiskausiUri;
    private String koulutuksenAlkamisvuosi;
    private KoodistoKoodiTyyppi hakutapaKoodi;

    private String tarjoajaOid;

    private Map<String, String> tarjoajaNimi = new HashMap<String, String>();
    private Date hakuAlkamisPvm;
    private Date hakuPaattymisPvm;
    private MonikielinenTekstiTyyppi hakukohteenKoulutuslaji;
    private String hakutyyppiUri;

    
    public String getTarjoajaOid() {
        return tarjoajaOid;
    }

    public void setTarjoajaOid(String tarjoajaOid) {
        this.tarjoajaOid = tarjoajaOid;
    }
    
    public String getTarjoajaNimi(String locale) {
        return tarjoajaNimi.get(locale);
    }

    public void setTarjoajaNimi(String locale, String nimi) {
        tarjoajaNimi.put(locale, nimi);
    }
    
    public Map<String, String> getTarjoajaNimi() {
        return tarjoajaNimi;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String value) {
        this.oid = value;
    }

    /**
     * Palauta nimi
     * @param locale kieli (fi,sv,en)
     * @return
     */
    public String getNimi(String locale) {
        return nimi.get(locale);
    }
    
    /**
     * Palauta kaiki nimet
     */
    public Map<String, String> getNimi() {
        return nimi;
    }

    /**
     * Aseta nimi
     * @param locale kieli (fi,sv,en)
     * @param nimi nimi
     */
    public void setNimi(String locale, String nimi) {
        this.nimi.put(locale,  nimi);
    }

    public String getKoodistoNimi() {
        return koodistoNimi;
    }

    public void setKoodistoNimi(String value) {
        this.koodistoNimi = value;
    }

    public TarjontaTila getTila() {
        return tila;
    }

    public void setTila(TarjontaTila value) {
        this.tila = value;
    }

    public String getAloituspaikat() {
        return aloituspaikat;
    }

    public void setAloituspaikat(String value) {
        this.aloituspaikat = value;
    }

    public String getKoulutuksenAlkamiskausiUri() {
        return koulutuksenAlkamiskausiUri;
    }

    public void setKoulutuksenAlkamiskausiUri(String value) {
        this.koulutuksenAlkamiskausiUri = value;
    }

    public String getKoulutuksenAlkamisvuosi() {
        return koulutuksenAlkamisvuosi;
    }

    public void setKoulutuksenAlkamisvuosi(String value) {
        this.koulutuksenAlkamisvuosi = value;
    }

    public KoodistoKoodiTyyppi getHakutapaKoodi() {
        return hakutapaKoodi;
    }

    public void setHakutapaKoodi(KoodistoKoodiTyyppi value) {
        this.hakutapaKoodi = value;
    }

    public Date getHakuAlkamisPvm() {
        return hakuAlkamisPvm;
    }

    public void setHakuAlkamisPvm(Date value) {
        this.hakuAlkamisPvm = value;
    }

    public Date getHakuPaattymisPvm() {
        return hakuPaattymisPvm;
    }

    public void setHakuPaattymisPvm(Date value) {
        this.hakuPaattymisPvm = value;
    }

    public MonikielinenTekstiTyyppi getHakukohteenKoulutuslaji() {
        return hakukohteenKoulutuslaji;
    }

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
