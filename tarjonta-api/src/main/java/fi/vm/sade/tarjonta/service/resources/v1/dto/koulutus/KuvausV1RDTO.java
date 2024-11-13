package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import io.swagger.annotations.ApiModel;
import java.util.HashMap;
import java.util.Map;

@ApiModel(value = "Kuvastekstien syöttämiseen ja hakemiseen käytettävä rajapintaolio")
public class KuvausV1RDTO<TYPE extends Enum> extends HashMap<TYPE, NimiV1RDTO> {

  private static final long serialVersionUID = 1L;

  public KuvausV1RDTO() {}

  public KuvausV1RDTO(Map<TYPE, NimiV1RDTO> tekstit) {
    this.putAll(tekstit);
  }
}
