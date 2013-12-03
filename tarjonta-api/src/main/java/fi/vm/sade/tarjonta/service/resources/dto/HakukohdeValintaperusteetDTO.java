package fi.vm.sade.tarjonta.service.resources.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kjsaila
 * Date: 02/12/13
 * Time: 15:45
 * To change this template use File | Settings | File Templates.
 */
public class HakukohdeValintaperusteetDTO extends BaseRDTO {

    private static final long serialVersionUID = 1L;

    private String tarjoajaOid;
    private Map<String, String> tarjoajaNimi;

    private Map<String, String> hakukohdeNimi;

    private int hakuVuosi;
    private Map<String, String> hakuKausi;

    private String hakuOid;

    private List<String> opetuskielet;
    private String hakukohdeNimiUri;

    private int valintojenAloituspaikatLkm;

    private List<ValintakoeRDTO> valintakokeet;

    private Map<String, String> painokertoimet;

    private String paasykoeTunniste;
    private String lisanayttoTunniste;
    private String lisapisteTunniste;
    private String kielikoeTunniste;

    private BigDecimal painotettuKeskiarvoHylkaysMin;
    private BigDecimal painotettuKeskiarvoHylkaysMax;

    private BigDecimal paasykoeMin;
    private BigDecimal paasykoeMax;

    private BigDecimal paasykoeHylkaysMin;
    private BigDecimal paasykoeHylkaysMax;

    private BigDecimal lisanayttoMin;
    private BigDecimal lisanayttoMax;

    private BigDecimal lisanayttoHylkaysMin;
    private BigDecimal lisanayttoHylkaysMax;

    private BigDecimal lisapisteMin;
    private BigDecimal lisapisteMax;

    private BigDecimal lisapisteHylkaysMin;
    private BigDecimal lisapisteHylkaysMax;

    private BigDecimal hylkaysMin;
    private BigDecimal hylkaysMax;

    public BigDecimal getPaasykoeMin() {
        return paasykoeMin;
    }

    public void setPaasykoeMin(BigDecimal paasykoeMin) {
        this.paasykoeMin = paasykoeMin;
    }

    public BigDecimal getPaasykoeMax() {
        return paasykoeMax;
    }

    public void setPaasykoeMax(BigDecimal paasykoeMax) {
        this.paasykoeMax = paasykoeMax;
    }

    public BigDecimal getLisanayttoMin() {
        return lisanayttoMin;
    }

    public void setLisanayttoMin(BigDecimal lisanayttoMin) {
        this.lisanayttoMin = lisanayttoMin;
    }

    public BigDecimal getLisanayttoMax() {
        return lisanayttoMax;
    }

    public void setLisanayttoMax(BigDecimal lisanayttoMax) {
        this.lisanayttoMax = lisanayttoMax;
    }

    public BigDecimal getLisapisteMin() {
        return lisapisteMin;
    }

    public void setLisapisteMin(BigDecimal lisapisteMin) {
        this.lisapisteMin = lisapisteMin;
    }

    public BigDecimal getLisapisteMax() {
        return lisapisteMax;
    }

    public void setLisapisteMax(BigDecimal lisapisteMax) {
        this.lisapisteMax = lisapisteMax;
    }

    public BigDecimal getPaasykoeHylkaysMin() {
        return paasykoeHylkaysMin;
    }

    public void setPaasykoeHylkaysMin(BigDecimal paasykoeHylkaysMin) {
        this.paasykoeHylkaysMin = paasykoeHylkaysMin;
    }

    public BigDecimal getPaasykoeHylkaysMax() {
        return paasykoeHylkaysMax;
    }

    public void setPaasykoeHylkaysMax(BigDecimal paasykoeHylkaysMax) {
        this.paasykoeHylkaysMax = paasykoeHylkaysMax;
    }

    public BigDecimal getLisanayttoHylkaysMin() {
        return lisanayttoHylkaysMin;
    }

    public void setLisanayttoHylkaysMin(BigDecimal lisanayttoHylkaysMin) {
        this.lisanayttoHylkaysMin = lisanayttoHylkaysMin;
    }

    public BigDecimal getLisanayttoHylkaysMax() {
        return lisanayttoHylkaysMax;
    }

    public void setLisanayttoHylkaysMax(BigDecimal lisanayttoHylkaysMax) {
        this.lisanayttoHylkaysMax = lisanayttoHylkaysMax;
    }

    public BigDecimal getLisapisteHylkaysMin() {
        return lisapisteHylkaysMin;
    }

    public void setLisapisteHylkaysMin(BigDecimal lisapisteHylkaysMin) {
        this.lisapisteHylkaysMin = lisapisteHylkaysMin;
    }

    public BigDecimal getLisapisteHylkaysMax() {
        return lisapisteHylkaysMax;
    }

    public void setLisapisteHylkaysMax(BigDecimal lisapisteHylkaysMax) {
        this.lisapisteHylkaysMax = lisapisteHylkaysMax;
    }

    public BigDecimal getPainotettuKeskiarvoHylkaysMin() {
        return painotettuKeskiarvoHylkaysMin;
    }

    public void setPainotettuKeskiarvoHylkaysMin(BigDecimal painotettuKeskiarvoHylkaysMin) {
        this.painotettuKeskiarvoHylkaysMin = painotettuKeskiarvoHylkaysMin;
    }

    public BigDecimal getPainotettuKeskiarvoHylkaysMax() {
        return painotettuKeskiarvoHylkaysMax;
    }

    public void setPainotettuKeskiarvoHylkaysMax(BigDecimal painotettuKeskiarvoHylkaysMax) {
        this.painotettuKeskiarvoHylkaysMax = painotettuKeskiarvoHylkaysMax;
    }

    public BigDecimal getHylkaysMin() {
        return hylkaysMin;
    }

    public void setHylkaysMin(BigDecimal hylkaysMin) {
        this.hylkaysMin = hylkaysMin;
    }

    public BigDecimal getHylkaysMax() {
        return hylkaysMax;
    }

    public void setHylkaysMax(BigDecimal hylkaysMax) {
        this.hylkaysMax = hylkaysMax;
    }

    public String getTarjoajaOid() {
        return tarjoajaOid;
    }

    public void setTarjoajaOid(String tarjoajaOid) {
        this.tarjoajaOid = tarjoajaOid;
    }

    public Map<String, String> getTarjoajaNimi() {
        return tarjoajaNimi;
    }

    public void setTarjoajaNimi(Map<String, String> tarjoajaNimi) {
        this.tarjoajaNimi = tarjoajaNimi;
    }

    public Map<String, String> getHakukohdeNimi() {
        return hakukohdeNimi;
    }

    public void setHakukohdeNimi(Map<String, String> hakukohdeNimi) {
        this.hakukohdeNimi = hakukohdeNimi;
    }

    public int getHakuVuosi() {
        return hakuVuosi;
    }

    public void setHakuVuosi(int hakuVuosi) {
        this.hakuVuosi = hakuVuosi;
    }

    public Map<String, String> getHakuKausi() {
        return hakuKausi;
    }

    public void setHakuKausi(Map<String, String> hakuKausi) {
        this.hakuKausi = hakuKausi;
    }

    public String getHakuOid() {
        return hakuOid;
    }

    public void setHakuOid(String hakuOid) {
        this.hakuOid = hakuOid;
    }

    public List<String> getOpetuskielet() {
        return opetuskielet;
    }

    public void setOpetuskielet(List<String> opetuskielet) {
        this.opetuskielet = opetuskielet;
    }

    public String getHakukohdeNimiUri() {
        return hakukohdeNimiUri;
    }

    public void setHakukohdeNimiUri(String hakukohdeNimiUri) {
        this.hakukohdeNimiUri = hakukohdeNimiUri;
    }

    public int getValintojenAloituspaikatLkm() {
        return valintojenAloituspaikatLkm;
    }

    public void setValintojenAloituspaikatLkm(int valintojenAloituspaikatLkm) {
        this.valintojenAloituspaikatLkm = valintojenAloituspaikatLkm;
    }

    public List<ValintakoeRDTO> getValintakokeet() {
        return valintakokeet;
    }

    public void setValintakokeet(List<ValintakoeRDTO> valintakokeet) {
        this.valintakokeet = valintakokeet;
    }

    public Map<String, String> getPainokertoimet() {
        return painokertoimet;
    }

    public void setPainokertoimet(Map<String, String> painokertoimet) {
        this.painokertoimet = painokertoimet;
    }

    public String getPaasykoeTunniste() {
        return paasykoeTunniste;
    }

    public void setPaasykoeTunniste(String paasykoeTunniste) {
        this.paasykoeTunniste = paasykoeTunniste;
    }

    public String getLisanayttoTunniste() {
        return lisanayttoTunniste;
    }

    public void setLisanayttoTunniste(String lisanayttoTunniste) {
        this.lisanayttoTunniste = lisanayttoTunniste;
    }

    public String getLisapisteTunniste() {
        return lisapisteTunniste;
    }

    public void setLisapisteTunniste(String lisapisteTunniste) {
        this.lisapisteTunniste = lisapisteTunniste;
    }

    public String getKielikoeTunniste() {
        return kielikoeTunniste;
    }

    public void setKielikoeTunniste(String kielikoeTunniste) {
        this.kielikoeTunniste = kielikoeTunniste;
    }
}
