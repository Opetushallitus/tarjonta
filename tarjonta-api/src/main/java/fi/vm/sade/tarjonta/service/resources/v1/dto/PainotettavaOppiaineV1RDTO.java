package fi.vm.sade.tarjonta.service.resources.v1.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;

@ApiModel(value = "V1 Hakukohde's painotettava oppiaine REST-api model")
public class PainotettavaOppiaineV1RDTO extends BaseV1RDTO {

  @ApiModelProperty(value = "Oppiainee's name uri", required = true)
  private String oppiaineUri;

  @ApiModelProperty(value = "Oppiainee's weight", required = true)
  private BigDecimal painokerroin;

  public String getOppiaineUri() {
    return oppiaineUri;
  }

  public void setOppiaineUri(String oppiaineUri) {
    this.oppiaineUri = oppiaineUri;
  }

  public BigDecimal getPainokerroin() {
    return painokerroin;
  }

  public void setPainokerroin(BigDecimal painokerroin) {
    this.painokerroin = painokerroin;
  }
}
