package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Monikielisen tekstin syötämiseen ja näyttämiseen käytettävä rajapintaolio")
public class NimiV1RDTO extends KoodiV1RDTO {

  private static final long serialVersionUID = 1L;

  @Parameter(
          name = "Avain-arvopari, jossa avain on koodisto kieli uri ja arvo on kuvausteksti",
      required = true)
  private Map<String, String> tekstis;

  public NimiV1RDTO() {}

  public NimiV1RDTO(Map<String, String> tekstit) {
    this.setTekstis(tekstit);
  }

  public Map<String, String> getTekstis() {
    if (tekstis == null) {
      tekstis = new HashMap<String, String>();
    }
    return tekstis;
  }

  public void setTekstis(Map<String, String> teksti) {
    this.tekstis = teksti;
  }

  @Override
  public int hashCode() {
    return getTekstis().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof NimiV1RDTO) {
      return getTekstis().equals(((NimiV1RDTO) obj).getTekstis());
    }
    return false;
  }
}
