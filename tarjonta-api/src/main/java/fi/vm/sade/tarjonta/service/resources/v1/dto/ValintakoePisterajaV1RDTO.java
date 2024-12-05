package fi.vm.sade.tarjonta.service.resources.v1.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.math.BigDecimal;

@Tag(name = "V1 Valintakoe's pisteraja REST-api model")
public class ValintakoePisterajaV1RDTO {

  public static final String PAASYKOE = "Paasykoe";
  public static final String LISAPISTEET = "Lisapisteet";
  public static final String KOKONAISPISTEET = "Kokonaispisteet";

  @Parameter(name = "Pisteraja's pisterajatyyppi", required = true)
  private String pisterajatyyppi;

  @Parameter(name = "Pisteraja's alin pistemäärä")
  private BigDecimal alinPistemaara;

  @Parameter(name = "Pisteraja's ylin pistemäärä")
  private BigDecimal ylinPistemaara;

  @Parameter(name = "Pisteraja's alin hyväksytty pistemäärä")
  private BigDecimal alinHyvaksyttyPistemaara;

  public String getPisterajatyyppi() {
    return pisterajatyyppi;
  }

  public void setPisterajatyyppi(String pisterajatyyppi) {
    this.pisterajatyyppi = pisterajatyyppi;
  }

  public BigDecimal getAlinPistemaara() {
    return alinPistemaara;
  }

  public void setAlinPistemaara(BigDecimal alinPistemaara) {
    this.alinPistemaara = alinPistemaara;
  }

  public BigDecimal getYlinPistemaara() {
    return ylinPistemaara;
  }

  public void setYlinPistemaara(BigDecimal ylinPistemaara) {
    this.ylinPistemaara = ylinPistemaara;
  }

  public BigDecimal getAlinHyvaksyttyPistemaara() {
    return alinHyvaksyttyPistemaara;
  }

  public void setAlinHyvaksyttyPistemaara(BigDecimal alinHyvaksyttyPistemaara) {
    this.alinHyvaksyttyPistemaara = alinHyvaksyttyPistemaara;
  }

  public boolean isKokonaispisteet() {
    return KOKONAISPISTEET.equals(getPisterajatyyppi());
  }

  public boolean isPaasykoe() {
    return PAASYKOE.equals(getPisterajatyyppi());
  }

  public boolean isLisapisteet() {
    return LISAPISTEET.equals(getPisterajatyyppi());
  }
}
