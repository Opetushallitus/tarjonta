package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import io.swagger.annotations.ApiModel;
import java.util.HashMap;

@ApiModel(value = "Kuvastekstien syöttämiseen ja hakemiseen käytettävä rajapintaolio")
public class KoodiValikoimaV1RDTO extends HashMap<String, KoodiUrisV1RDTO> {

  private static final long serialVersionUID = 1L;

  public KoodiValikoimaV1RDTO() {}
}
