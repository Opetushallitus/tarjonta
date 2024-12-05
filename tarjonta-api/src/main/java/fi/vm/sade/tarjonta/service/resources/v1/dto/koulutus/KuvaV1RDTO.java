package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.Serializable;

@Tag(name = "Kuvan syöttämiseen ja hakemiseen käytettävä rajapintaolio")
public class KuvaV1RDTO implements Serializable {

  @Parameter(name = "Koodisto kieli uri", required = false)
  private String kieliUri;

  @Parameter(name = "Tiedoston alkuperäinen nimi", required = true)
  private String filename;

  @Parameter(name = "Tiedoston tyyppi (image/jpeg, image/png jne.)", required = true)
  private String mimeType;

  @Parameter(name = "Kuvatiedosto base64-enkoodauksella", required = true)
  private String base64data;

  public KuvaV1RDTO() {}

  public KuvaV1RDTO(String filename, String mimeType, String kieliUri, String base64data) {
    this.filename = filename;
    this.mimeType = mimeType;
    this.kieliUri = kieliUri;
    this.base64data = base64data;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public String getBase64data() {
    return base64data;
  }

  public void setBase64data(String base64data) {
    this.base64data = base64data;
  }

  public String getKieliUri() {
    return kieliUri;
  }

  public void setKieliUri(String kieliUri) {
    this.kieliUri = kieliUri;
  }
}
