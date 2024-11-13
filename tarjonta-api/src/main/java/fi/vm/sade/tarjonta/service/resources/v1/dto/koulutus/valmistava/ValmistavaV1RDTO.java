package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.valmistava;

import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@ApiModel(value = "Valmistavan osan tiedot sisältävä rajapintaolio")
public class ValmistavaV1RDTO implements Serializable {

  @ApiModelProperty(
      value = "Koulutuksen koulutusmoduulin toteutuksen monikieliset kuvaustekstit",
      required = false)
  private KuvausV1RDTO<KomotoTeksti> kuvaus;

  @ApiModelProperty(value = "Koulutuksen suunntellun keston arvo", required = true)
  private String suunniteltuKestoArvo;

  @ApiModelProperty(
      value = "Koulutuksen suunntellun keston tyyppi (koodisto koodi uri)",
      required = true)
  private KoodiV1RDTO suunniteltuKestoTyyppi;

  @ApiModelProperty(
      value =
          "Koulutuksen hinta (korvaa vanhan Double-tyyppisen hinnan, koska pitää tukea myös muita kun numeroita)")
  private String hintaString;

  @ApiModelProperty(
      value = "Koulutuksen hinta, on pakollinen jos koulutus on merkitty maksulliseksi",
      required = false)
  private Double hinta;

  @ApiModelProperty(value = "Valitaan opintojen maksullisuuden (false=koulutus ei vaadi maksua)")
  private Boolean opintojenMaksullisuus;

  @ApiModelProperty(value = "HTTP-linkki opetussuunnitelmaan", required = false)
  private String linkkiOpetussuunnitelmaan;

  @ApiModelProperty(
      value = "Koulutuksen opetusmuodot (sisältää koodisto koodi uri:a)",
      required = true)
  private KoodiUrisV1RDTO opetusmuodos;

  @ApiModelProperty(
      value = "Koulutuksen opetusajat (esim. Iltaopetus) (sisältää koodisto koodi uri:a)",
      required = true)
  private KoodiUrisV1RDTO opetusAikas;

  @ApiModelProperty(
      value = "Koulutuksen opetuspaikat (sisältää koodisto koodi uri:a)",
      required = true)
  private KoodiUrisV1RDTO opetusPaikkas;

  public ValmistavaV1RDTO() {}

  public void setHintaString(String hintaString) {
    this.hintaString = hintaString;
  }

  public String getHintaString() {
    return hintaString;
  }

  private Set<YhteyshenkiloTyyppi> yhteyshenkilos;

  public Set<YhteyshenkiloTyyppi> getYhteyshenkilos() {
    if (yhteyshenkilos == null) {
      yhteyshenkilos = new HashSet<YhteyshenkiloTyyppi>();
    }
    return yhteyshenkilos;
  }

  public void setYhteyshenkilos(Set<YhteyshenkiloTyyppi> yhteyshenkilos) {
    this.yhteyshenkilos = yhteyshenkilos;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.reflectionToString(this);
  }

  public KuvausV1RDTO<KomotoTeksti> getKuvaus() {
    if (kuvaus == null) {
      kuvaus = new KuvausV1RDTO<KomotoTeksti>();
    }
    return kuvaus;
  }

  public void setKuvaus(KuvausV1RDTO<KomotoTeksti> kuvaus) {
    this.kuvaus = kuvaus;
  }

  public String getSuunniteltuKestoArvo() {
    return suunniteltuKestoArvo;
  }

  public void setSuunniteltuKestoArvo(String suunniteltuKestoArvo) {
    this.suunniteltuKestoArvo = suunniteltuKestoArvo;
  }

  public KoodiV1RDTO getSuunniteltuKestoTyyppi() {
    return suunniteltuKestoTyyppi;
  }

  public void setSuunniteltuKestoTyyppi(KoodiV1RDTO suunniteltuKestoTyyppi) {
    this.suunniteltuKestoTyyppi = suunniteltuKestoTyyppi;
  }

  public Double getHinta() {
    return hinta;
  }

  public void setHinta(Double hinta) {
    this.hinta = hinta;
  }

  public Boolean getOpintojenMaksullisuus() {
    return opintojenMaksullisuus;
  }

  public void setOpintojenMaksullisuus(Boolean opintojenMaksullisuus) {
    this.opintojenMaksullisuus = opintojenMaksullisuus;
  }

  public String getLinkkiOpetussuunnitelmaan() {
    return linkkiOpetussuunnitelmaan;
  }

  public void setLinkkiOpetussuunnitelmaan(String linkkiOpetussuunnitelmaan) {
    this.linkkiOpetussuunnitelmaan = linkkiOpetussuunnitelmaan;
  }

  public KoodiUrisV1RDTO getOpetusmuodos() {
    if (opetusmuodos == null) {
      opetusmuodos = new KoodiUrisV1RDTO();
    }

    return opetusmuodos;
  }

  public void setOpetusmuodos(KoodiUrisV1RDTO opetusmuodos) {
    this.opetusmuodos = opetusmuodos;
  }

  public KoodiUrisV1RDTO getOpetusAikas() {

    if (opetusAikas == null) {
      opetusAikas = new KoodiUrisV1RDTO();
    }

    return opetusAikas;
  }

  public void setOpetusAikas(KoodiUrisV1RDTO opetusAikas) {
    this.opetusAikas = opetusAikas;
  }

  public KoodiUrisV1RDTO getOpetusPaikkas() {
    if (opetusPaikkas == null) {
      opetusPaikkas = new KoodiUrisV1RDTO();
    }

    return opetusPaikkas;
  }

  public void setOpetusPaikkas(KoodiUrisV1RDTO opetusPaikkas) {
    this.opetusPaikkas = opetusPaikkas;
  }
}
