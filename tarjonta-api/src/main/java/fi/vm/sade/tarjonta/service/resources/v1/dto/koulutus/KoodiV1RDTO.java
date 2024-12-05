package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import java.io.Serializable;
import java.util.Map;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;

@Tag(name = "Koodisto koodi uri:n syötämiseen ja näyttämiseen käytettävä rajapintaolio")
public class KoodiV1RDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  @Parameter(name = "Käytetyn koodisto koodin kieli uri (lisätietoa)")
  private String kieliUri;

  @Parameter(name = "Käytetyn koodisto koodin kieli uri:n versio (lisätietoa)")
  private Integer kieliVersio;

  @Parameter(name = "Käytetyn koodisto koodin kieli uri:n iso-kielikoodi (lisätietoa)")
  private String kieliArvo;

  @Parameter(name = "Käytetyn koodisto koodin kieli uri:n nimen kielikäännös (lisätietoa)")
  private String kieliKaannos;

  @Parameter(name = "Koodisto koodin uri", required = true)
  private String uri;

  @Parameter(
          name = "Koodisto koodin versio, koodisto koodi uri:a syötettäessä pakollinen tieto",
      required = true)
  private Integer versio = 1;

  @Parameter(name = "Koodisto koodin uri:n arvo (lisätietoa)")
  private String arvo;

  @Parameter(name = "Koodisto koodin uri:n nimen kielikäännos (lisätietoa)")
  private String nimi;

  @Parameter(
          name =
          "Monikielisen lisätiedon näyttämiseen tarkoitettu avain-arvopari, jossa avain on koodisto kieli uri ja arvo on rajapintaolio",
      required = false)
  private Map<String, KoodiV1RDTO> meta;

  public KoodiV1RDTO() {}

  public KoodiV1RDTO(String uri, Integer versio, String arvo) {
    this.uri = uri;
    this.versio = versio;
    this.arvo = arvo;
  }

  public KoodiV1RDTO(String uri, Integer versio, String arvo, String nimi) {
    this.uri = uri;
    this.versio = versio;
    this.arvo = arvo;
    this.nimi = nimi;
  }

  public void setKoodi(String uri, Integer versio, String arvo, String nimi) {
    this.uri = uri;
    this.versio = versio;
    this.arvo = arvo;
    this.nimi = nimi;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public Integer getVersio() {
    return versio;
  }

  public void setVersio(Integer versio) {
    this.versio = versio;
  }

  public String getArvo() {
    return arvo;
  }

  public void setArvo(String arvo) {
    this.arvo = arvo;
  }

  public String getNimi() {
    return nimi;
  }

  public void setNimi(String nimi) {
    this.nimi = nimi;
  }

  public String getKieliUri() {
    return kieliUri;
  }

  public void setKieliUri(String kieliUri) {
    this.kieliUri = kieliUri;
  }

  public Integer getKieliVersio() {
    return kieliVersio;
  }

  public void setKieliVersio(Integer kieliVersio) {
    this.kieliVersio = kieliVersio;
  }

  public String getKieliArvo() {
    return kieliArvo;
  }

  public void setKieliArvo(String kieliArvo) {
    this.kieliArvo = kieliArvo;
  }

  public String getKieliKaannos() {
    return kieliKaannos;
  }

  public void setKieliKaannos(String kieliKaannos) {
    this.kieliKaannos = kieliKaannos;
  }

  public void setMeta(Map<String, KoodiV1RDTO> meta) {
    this.meta = meta;
  }

  public Map<String, KoodiV1RDTO> getMeta() {
    return meta;
  }

  public static boolean notEmpty(KoodiV1RDTO dto) {
    return dto != null && !StringUtils.isBlank(dto.getUri());
  }

  public static String stripVersionFromKoodiUri(String koodiUri) {
    return StringUtils.defaultString(koodiUri).split("#")[0];
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName()
        + "[kieliUri="
        + this.kieliUri
        + ", kieliVersio="
        + this.kieliVersio
        + ", kieliArvi="
        + this.kieliArvo
        + ", kieliKaannos="
        + this.kieliKaannos
        + ", uri="
        + this.uri
        + ", version="
        + this.versio
        + ", arvo="
        + this.arvo
        + ", nimi="
        + this.nimi
        + "]";
  }
}
