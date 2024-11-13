package fi.vm.sade.tarjonta.service.resources.v1.dto;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

public class BaseV1RDTO implements Serializable {

  @ApiModelProperty(value = "Luontipäivä ja aika", required = true)
  private Date created;

  @ApiModelProperty(value = "Luonnin suorittajan nimi", required = true)
  private String createdBy;

  @ApiModelProperty(value = "Viimeinen muokkauspäivä ja aika", required = true)
  private Date modified;

  @ApiModelProperty(value = "Muokkauksen suorittajan nimi", required = true)
  private String modifiedBy;

  @ApiModelProperty(value = "Objektin yksilöivä tunniste", required = true)
  private String oid;

  @ApiModelProperty(value = "Objektin versio numero", required = true)
  private Long version;

  public BaseV1RDTO() {}

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public Date getModified() {
    return modified;
  }

  public void setModified(Date modified) {
    this.modified = modified;
  }

  public String getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }
}
