package fi.vm.sade.tarjonta.service.resources.dto;

import java.util.List;

public class HakukohdeKelaDTO {
  private String koulutuksenAlkamiskausiUri;
  private Integer koulutuksenAlkamisVuosi;
  private String hakukohdeOid;
  private String tarjoajaOid;
  private String oppilaitosKoodi;
  private List<KoulutusLaajuusarvoDTO> koulutusLaajuusarvos;

  public List<KoulutusLaajuusarvoDTO> getKoulutusLaajuusarvos() {
    return koulutusLaajuusarvos;
  }

  public void setKoulutusLaajuusarvos(List<KoulutusLaajuusarvoDTO> koulutusLaajuusarvos) {
    this.koulutusLaajuusarvos = koulutusLaajuusarvos;
  }

  public void setHakukohdeOid(String hakukohdeOid) {
    this.hakukohdeOid = hakukohdeOid;
  }

  public void setOppilaitosKoodi(String oppilaitosKoodi) {
    this.oppilaitosKoodi = oppilaitosKoodi;
  }

  public void setTarjoajaOid(String tarjoajaOid) {
    this.tarjoajaOid = tarjoajaOid;
  }

  public Integer getKoulutuksenAlkamisVuosi() {
    return koulutuksenAlkamisVuosi;
  }

  public void setKoulutuksenAlkamiskausiUri(String koulutuksenAlkamiskausiUri) {
    this.koulutuksenAlkamiskausiUri = koulutuksenAlkamiskausiUri;
  }

  public String getKoulutuksenAlkamiskausiUri() {
    return koulutuksenAlkamiskausiUri;
  }

  public void setKoulutuksenAlkamisVuosi(Integer koulutuksenAlkamisVuosi) {
    this.koulutuksenAlkamisVuosi = koulutuksenAlkamisVuosi;
  }

  public String getHakukohdeOid() {
    return hakukohdeOid;
  }

  public String getOppilaitosKoodi() {
    return oppilaitosKoodi;
  }

  public String getTarjoajaOid() {
    return tarjoajaOid;
  }
}
