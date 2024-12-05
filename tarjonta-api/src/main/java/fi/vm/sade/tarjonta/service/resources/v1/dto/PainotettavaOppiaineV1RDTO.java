package fi.vm.sade.tarjonta.service.resources.v1.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.math.BigDecimal;

@Tag(name = "V1 Hakukohde's painotettava oppiaine REST-api model")
public class PainotettavaOppiaineV1RDTO extends BaseV1RDTO {

  @Parameter(name = "Oppiainee's name uri", required = true)
  private String oppiaineUri;

  @Parameter(name = "Oppiainee's weight", required = true)
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
