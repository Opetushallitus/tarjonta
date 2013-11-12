package fi.vm.sade.tarjonta.service.resources.v1.dto;

import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import java.util.Map;

import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

public class KoulutusHakutulosV1RDTO extends BaseV1RDTO {

    private static final long serialVersionUID = 1L;

    private String oid;
    private Map<String, String> nimi;
    private Map<String, String> kausi;
    private Integer vuosi;
    private Map<String, String> koulutusLaji;
    private TarjontaTila tila;
    private KoulutusasteTyyppi koulutusasteTyyppi;
    private Map<String, String> pohjakoulutusvaatimus;

    public Map<String, String> getPohjakoulutusvaatimus() {
        return pohjakoulutusvaatimus;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public Map<String, String> getNimi() {
        return nimi;
    }

    public void setNimi(Map<String, String> nimi) {
        this.nimi = nimi;
    }

    public Map<String, String> getKausi() {
        return kausi;
    }

    public void setKausi(Map<String, String> kausi) {
        this.kausi = kausi;
    }

    public Integer getVuosi() {
        return vuosi;
    }

    public void setVuosi(Integer vuosi) {
        this.vuosi = vuosi;
    }

    public Map<String, String> getKoulutuslaji() {
        return koulutusLaji;
    }

    public void setKoulutuslaji(Map<String, String> koulutusLaji) {
        this.koulutusLaji = koulutusLaji;
    }

    public TarjontaTila getTila() {
        return tila;
    }

    public void setTila(TarjontaTila tila) {
        this.tila = tila;
    }

    public KoulutusasteTyyppi getKoulutusasteTyyppi() {
        return koulutusasteTyyppi;
    }

    public void setKoulutusasteTyyppi(KoulutusasteTyyppi koulutusasteTyyppi) {
        this.koulutusasteTyyppi = koulutusasteTyyppi;
    }

    public void setPohjakoulutusvaatimus(
            Map<String, String> pohjakoulutusvaatimus) {
        this.pohjakoulutusvaatimus = pohjakoulutusvaatimus;
    }
}