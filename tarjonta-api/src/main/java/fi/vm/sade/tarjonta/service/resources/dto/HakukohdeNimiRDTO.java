package fi.vm.sade.tarjonta.service.resources.dto;

import java.io.Serializable;
import java.util.Map;

public class HakukohdeNimiRDTO implements Serializable {

  private String hakukohdeOid;

  private String tarjoajaOid;
  private Map<String, String> tarjoajaNimi;

  private String hakukohdeNameUri;
  private Map<String, String> hakukohdeNimi;
  private String hakukohdeTila;

  private int hakuVuosi;
  private int koulutusVuosi;

  private Map<String, String> hakuKausi;
  private Map<String, String> koulutusKausi;

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

  public String getHakukohdeOid() {
    return hakukohdeOid;
  }

  public void setHakukohdeOid(String hakukohdeOid) {
    this.hakukohdeOid = hakukohdeOid;
  }

  public Map<String, String> getHakukohdeNimi() {
    return hakukohdeNimi;
  }

  public void setHakukohdeNimi(Map<String, String> hakukohdeNimi) {
    this.hakukohdeNimi = hakukohdeNimi;
  }

  public String getHakukohdeNameUri() {
    return hakukohdeNameUri;
  }

  public void setHakukohdeNameUri(String hakukohdeNameUri) {
    this.hakukohdeNameUri = hakukohdeNameUri;
  }

  public String getHakukohdeTila() {
    return hakukohdeTila;
  }

  public void setHakukohdeTila(String hakukohdeTila) {
    this.hakukohdeTila = hakukohdeTila;
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

  public void setKoulutusVuosi(int koulutusVuosi) {
    this.koulutusVuosi = koulutusVuosi;
  }

  public int getKoulutusVuosi() {
    return koulutusVuosi;
  }

  public Map<String, String> getKoulutusKausi() {
    return koulutusKausi;
  }

  public void setKoulutusKausi(Map<String, String> koulutusKausi) {
    this.koulutusKausi = koulutusKausi;
  }
}
