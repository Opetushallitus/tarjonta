
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


    public MonikielinenTekstiTyyppi getNimi() {
        return nimi;
    }

    public void setNimi(MonikielinenTekstiTyyppi value) {
        this.nimi = value;
    }

    public String getKoulutusmoduuli() {
        return koulutusmoduuli;
    }

    public void setKoulutusmoduuli(String value) {
        this.koulutusmoduuli = value;
    }

    public String getKoulutusmoduuliToteutus() {
        return koulutusmoduuliToteutus;
    }

    public void setKoulutusmoduuliToteutus(String value) {
        this.koulutusmoduuliToteutus = value;
    }

    public TarjoajaTyyppi getTarjoaja() {
        return tarjoaja;
    }

    public void setTarjoaja(TarjoajaTyyppi value) {
        this.tarjoaja = value;
    }

    public TarjontaTila getTila() {
        return tila;
    }

    public void setTila(TarjontaTila value) {
        this.tila = value;
    }

    public KoodistoKoodiTyyppi getKoulutuskoodi() {
        return koulutuskoodi;
    }

    public void setKoulutuskoodi(KoodistoKoodiTyyppi value) {
        this.koulutuskoodi = value;
    }

    public KoodistoKoodiTyyppi getKoulutusohjelmakoodi() {
        return koulutusohjelmakoodi;
    }

    public void setKoulutusohjelmakoodi(KoodistoKoodiTyyppi value) {
        this.koulutusohjelmakoodi = value;
    }

    public KoodistoKoodiTyyppi getTutkintonimike() {
        return tutkintonimike;
    }

    public void setTutkintonimike(KoodistoKoodiTyyppi value) {
        this.tutkintonimike = value;
    }

    public String getAjankohta() {
        return ajankohta;
    }

    public void setAjankohta(String value) {
        this.ajankohta = value;
    }

    public String getKomotoOid() {
        return komotoOid;
    }

    public void setKomotoOid(String value) {
        this.komotoOid = value;
    }

    public String getPohjakoulutusVaatimus() {
        return pohjakoulutusVaatimus;
    }

    public void setPohjakoulutusVaatimus(String value) {
        this.pohjakoulutusVaatimus = value;
    }

    public String getKoulutuslaji() {
        return koulutuslaji;
    }

    public void setKoulutuslaji(String value) {
        this.koulutuslaji = value;
    }

    public KoulutusasteTyyppi getKoulutustyyppi() {
        return koulutustyyppi;
    }

    public void setKoulutustyyppi(KoulutusasteTyyppi value) {
        this.koulutustyyppi = value;
    }

    public KoodistoKoodiTyyppi getLukiolinjakoodi() {
        return lukiolinjakoodi;
    }

    public void setLukiolinjakoodi(KoodistoKoodiTyyppi value) {
        this.lukiolinjakoodi = value;
    }

    public String getKoulutuksenAlkamiskausiUri() {
        return koulutuksenAlkamiskausiUri;
    }

    public void setKoulutuksenAlkamiskausiUri(String value) {
        this.koulutuksenAlkamiskausiUri = value;
    }

    public Integer getKoulutuksenAlkamisVuosi() {
        return koulutuksenAlkamisVuosi;
    }

    public void setKoulutuksenAlkamisVuosi(Integer value) {
        this.koulutuksenAlkamisVuosi = value;
    }

}
