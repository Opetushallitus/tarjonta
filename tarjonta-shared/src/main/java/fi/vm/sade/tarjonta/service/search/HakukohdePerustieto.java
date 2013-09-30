
package fi.vm.sade.tarjonta.service.search;

import java.io.Serializable;
import java.util.Date;

import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjoajaTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;

public class HakukohdePerustieto implements Serializable
{

    private final static long serialVersionUID = 100L;
    private String oid;
    private MonikielinenTekstiTyyppi nimi;
    private String koodistoNimi;
    private TarjontaTila tila;
    private String aloituspaikat;
    private String koulutuksenAlkamiskausiUri;
    private String koulutuksenAlkamisvuosi;
    private KoodistoKoodiTyyppi hakutapaKoodi;
    private TarjoajaTyyppi tarjoaja;
    private Date hakuAlkamisPvm;
    private Date hakuPaattymisPvm;
    private MonikielinenTekstiTyyppi hakukohteenKoulutuslaji;
    private String hakutyyppiUri;


    public String getOid() {
        return oid;
    }

    public void setOid(String value) {
        this.oid = value;
    }

    public MonikielinenTekstiTyyppi getNimi() {
        return nimi;
    }

    public void setNimi(MonikielinenTekstiTyyppi value) {
        this.nimi = value;
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

    public TarjoajaTyyppi getTarjoaja() {
        return tarjoaja;
    }

    public void setTarjoaja(TarjoajaTyyppi value) {
        this.tarjoaja = value;
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
