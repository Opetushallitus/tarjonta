package fi.vm.sade.tarjonta.data.tarjontauploader;

import fi.vm.sade.tarjonta.data.dto.AbstractReadableRow;
import org.apache.commons.lang.StringUtils;

public class Koulutus extends AbstractReadableRow {
    private String oppilaitosnumero;
    private String toimipisteJno;
    private String yhkoodi;
    private String koulutus;
    private String koulutusohjelma;
    private String painotus;
    private String koulutuslaji;
    private String pohjakoulutusvaatimus;
    private String opetuskieli;
    private String opetusmuoto;
    private String alkamisvuosi;
    private String alkamiskausi;
    private Integer suunniteltuKesto;
    private String hakukohdekoodi;

    public String getOppilaitosnumero() {
        return oppilaitosnumero;
    }

    public void setOppilaitosnumero(final String oppilaitosnumero) {
        this.oppilaitosnumero = oppilaitosnumero;
    }

    public String getToimipisteJno() {
        return toimipisteJno;
    }

    public void setToimipisteJno(final String toimipisteJno) {
        this.toimipisteJno = toimipisteJno;
    }

    public String getYhkoodi() {
        return yhkoodi;
    }

    public void setYhkoodi(final String yhkoodi) {
        this.yhkoodi = yhkoodi;
    }

    public String getKoulutus() {
        return koulutus;
    }

    public void setKoulutus(final String koulutus) {
        this.koulutus = koulutus;
    }

    public String getKoulutusohjelma() {
        return koulutusohjelma;
    }

    public void setKoulutusohjelma(final String koulutusohjelma) {
        this.koulutusohjelma = koulutusohjelma;
    }

    public String getPainotus() {
        return painotus;
    }

    public void setPainotus(final String painotus) {
        this.painotus = painotus;
    }

    public String getKoulutuslaji() {
        return koulutuslaji;
    }

    public void setKoulutuslaji(final String koulutuslaji) {
        this.koulutuslaji = koulutuslaji;
    }

    public String getPohjakoulutusvaatimus() {
        return pohjakoulutusvaatimus;
    }

    public void setPohjakoulutusvaatimus(final String pohjakoulutusvaatimus) {
        this.pohjakoulutusvaatimus = pohjakoulutusvaatimus;
    }

    public String getOpetuskieli() {
        return opetuskieli;
    }

    public void setOpetuskieli(final String opetuskieli) {
        this.opetuskieli = opetuskieli;
    }

    public String getOpetusmuoto() {
        return opetusmuoto;
    }

    public void setOpetusmuoto(final String opetusmuoto) {
        this.opetusmuoto = opetusmuoto;
    }

    public String getAlkamisvuosi() {
        return alkamisvuosi;
    }

    public void setAlkamisvuosi(final String alkamisvuosi) {
        this.alkamisvuosi = alkamisvuosi;
    }

    public String getAlkamiskausi() {
        return alkamiskausi;
    }

    public void setAlkamiskausi(final String alkamiskausi) {
        this.alkamiskausi = alkamiskausi;
    }

    public Integer getSuunniteltuKesto() {
        return suunniteltuKesto;
    }

    public void setSuunniteltuKesto(final Integer suunniteltuKesto) {
        this.suunniteltuKesto = suunniteltuKesto;
    }

    public String getHakukohdekoodi() {
        return hakukohdekoodi;
    }

    public void setHakukohdekoodi(final String hakukohdekoodi) {
        this.hakukohdekoodi = hakukohdekoodi;
    }

    @Override
    public boolean isEmpty() {
        return (StringUtils.isBlank(oppilaitosnumero) && StringUtils.isBlank(toimipisteJno) && StringUtils.isBlank(yhkoodi)
                && StringUtils.isBlank(koulutus) && StringUtils.isBlank(koulutusohjelma) && StringUtils.isBlank(pohjakoulutusvaatimus)
                && StringUtils.isBlank(opetuskieli) && StringUtils.isBlank(opetusmuoto) && StringUtils.isBlank(alkamisvuosi)
                && StringUtils.isBlank(alkamiskausi) && suunniteltuKesto == null && StringUtils.isBlank(hakukohdekoodi));
    }
}
