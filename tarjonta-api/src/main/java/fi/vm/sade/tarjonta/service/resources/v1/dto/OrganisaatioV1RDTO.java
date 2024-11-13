package fi.vm.sade.tarjonta.service.resources.v1.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "Organisaation/tarjoajan syötämiseen ja näyttämiseen käytettävä rajapintaolio")
public class OrganisaatioV1RDTO extends BaseV1RDTO {

  @ApiModelProperty(value = "Organisaation yksilöivä tunniste", required = true)
  private String oid;

  private String nimi;
  private List<LokalisointiV1RDTO> _nimet;

  public OrganisaatioV1RDTO() {}

  public OrganisaatioV1RDTO(String oid) {
    this.oid = oid;
  }

  public OrganisaatioV1RDTO(String oid, String nimi, List<LokalisointiV1RDTO> _nimet) {
    this.oid = oid;
    this.nimi = nimi;
    this._nimet = _nimet;
  }

  public void addNimi(LokalisointiV1RDTO lokalisointi) {
    getNimet().add(lokalisointi);
  }

  public void addNimi(String kieli, String kieliUri, String arvo) {
    getNimet().add(new LokalisointiV1RDTO(kieli, kieliUri, arvo));
  }

  public List<LokalisointiV1RDTO> getNimet() {
    if (_nimet == null) {
      _nimet = new ArrayList<LokalisointiV1RDTO>();
    }
    return _nimet;
  }

  public void setNimet(List<LokalisointiV1RDTO> _nimet) {
    this._nimet = _nimet;
  }

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public String getNimi() {
    return nimi;
  }

  public void setNimi(String nimi) {
    this.nimi = nimi;
  }
}
