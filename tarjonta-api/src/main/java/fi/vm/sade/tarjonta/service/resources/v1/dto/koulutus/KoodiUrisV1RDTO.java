package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApiModel(value = "Monen koodisto koodi uri:n syötämiseen ja näyttämiseen käytettävä rajapintaolio")
public class KoodiUrisV1RDTO extends KoodiV1RDTO {

  @Override
  public String toString() {
    return "KoodiUrisV1RDTO [uris=" + uris + "]";
  }

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(
      value = "Avain-arvopari, jossa avain on koodisto koodi uri ja arvo on koodin versionumero",
      required = true)
  private Map<String, Integer> uris;

  public KoodiUrisV1RDTO() {}

  public KoodiUrisV1RDTO(Map<String, Integer> uris) {
    setUris(uris);
  }

  public Map<String, Integer> getUris() {
    return uris;
  }

  public void setUris(Map<String, Integer> uris) {
    this.uris = uris;
  }

  public List<String> getUrisAsStringList(boolean addVersionToUri) {
    List<String> list = new ArrayList<String>();

    if (uris != null) {
      uris.forEach(
          (uri, value) -> {
            if (addVersionToUri) {
              uri = uri.concat("#" + value.toString());
            }

            list.add(uri);
          });
    }

    return list;
  }
}
