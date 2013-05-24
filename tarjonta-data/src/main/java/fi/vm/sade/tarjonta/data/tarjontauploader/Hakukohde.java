package fi.vm.sade.tarjonta.data.tarjontauploader;

import fi.vm.sade.tarjonta.data.dto.AbstractReadableRow;
import org.apache.commons.lang.StringUtils;

public class Hakukohde extends AbstractReadableRow {
    private String alkamisvuosi;
    private String alkamiskausi;
    private String hakutyyppi;
    private String yhkoulu;
    private String oppilaitosnumero;
    private String toimipisteJno;
    private String hakukohdekoodi;
    private Integer valinnanAloituspaikka;
    private Integer aloituspaikka;
    private String valintakoe;

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

    public String getHakutyyppi() {
        return hakutyyppi;
    }

    public void setHakutyyppi(final String hakutyyppi) {
        this.hakutyyppi = hakutyyppi;
    }

    public String getYhkoulu() {
        return yhkoulu;
    }

    public void setYhkoulu(final String yhkoulu) {
        this.yhkoulu = yhkoulu;
    }

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

    public String getHakukohdekoodi() {
        return hakukohdekoodi;
    }

    public void setHakukohdekoodi(final String hakukohdekoodi) {
        this.hakukohdekoodi = hakukohdekoodi;
    }

    public Integer getValinnanAloituspaikka() {
        return valinnanAloituspaikka;
    }

    public void setValinnanAloituspaikka(final Integer valinnanAloituspaikka) {
        this.valinnanAloituspaikka = valinnanAloituspaikka;
    }

    public Integer getAloituspaikka() {
        return aloituspaikka;
    }

    public void setAloituspaikka(final Integer aloituspaikka) {
        this.aloituspaikka = aloituspaikka;
    }

    public String getValintakoe() {
        return valintakoe;
    }

    public void setValintakoe(final String valintakoe) {
        this.valintakoe = valintakoe;
    }

    @Override
    public boolean isEmpty() {
        return (StringUtils.isBlank(alkamisvuosi) && StringUtils.isBlank(alkamiskausi) && StringUtils.isBlank(hakutyyppi) && StringUtils.isBlank(yhkoulu)
                && StringUtils.isBlank(oppilaitosnumero) && StringUtils.isBlank(toimipisteJno) && StringUtils.isBlank(hakukohdekoodi)
                && valinnanAloituspaikka == null && aloituspaikka == null && StringUtils.isBlank(valintakoe));
    }
}
