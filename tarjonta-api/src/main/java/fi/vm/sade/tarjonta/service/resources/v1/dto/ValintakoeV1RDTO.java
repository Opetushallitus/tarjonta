package fi.vm.sade.tarjonta.service.resources.v1.dto;

import fi.vm.sade.tarjonta.service.resources.dto.BaseRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.TekstiRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeAjankohtaRDTO;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "V1 Hakukohde's valintakoe REST-api model, used by KK-ui")
public class ValintakoeV1RDTO extends BaseRDTO {

  @Parameter(name = "Valintakoe's hakukohde oid", required = true)
  private String hakukohdeOid;

  @Parameter(name = "Valintakoe's name language uri", required = true)
  private String kieliUri;

  @Parameter(
          name = "Valintakoe's name's language's name, used for displaying the value",
      required = false)
  private String kieliNimi;

  @Parameter(name = "Valintakoe's name ", required = true)
  private String valintakoeNimi;

  @Parameter(name = "Valintakoe's tyyppi", required = true)
  private String valintakoetyyppi;

  private TekstiRDTO valintakokeenKuvaus;

  @Parameter(name = "Valintakoe's dates")
  private List<ValintakoeAjankohtaRDTO> valintakoeAjankohtas;

  @Parameter(name = "Valintakoe's pisterajat")
  private List<ValintakoePisterajaV1RDTO> pisterajat = new ArrayList<ValintakoePisterajaV1RDTO>();

  @Parameter(name = "Valintakoe's multilanguage kuvaukset")
  private Map<String, String> kuvaukset = new HashMap<String, String>();

  @Parameter(name = "Valintakoe's multilanguage lisänäytöt")
  private Map<String, String> lisanaytot = new HashMap<String, String>();

  public String getKieliUri() {
    return kieliUri;
  }

  public void setKieliUri(String kieliUri) {
    this.kieliUri = kieliUri;
  }

  public String getValintakoeNimi() {
    return valintakoeNimi;
  }

  public void setValintakoeNimi(String valintakoeNimi) {
    this.valintakoeNimi = valintakoeNimi;
  }

  public TekstiRDTO getValintakokeenKuvaus() {
    return valintakokeenKuvaus;
  }

  public void setValintakokeenKuvaus(TekstiRDTO valintakokeenKuvaus) {
    this.valintakokeenKuvaus = valintakokeenKuvaus;
  }

  public List<ValintakoeAjankohtaRDTO> getValintakoeAjankohtas() {
    if (valintakoeAjankohtas == null) {
      valintakoeAjankohtas = new ArrayList<ValintakoeAjankohtaRDTO>();
    }

    return valintakoeAjankohtas;
  }

  public void setValintakoeAjankohtas(List<ValintakoeAjankohtaRDTO> valintakoeAjankohtas) {
    this.valintakoeAjankohtas = valintakoeAjankohtas;
  }

  public String getHakukohdeOid() {
    return hakukohdeOid;
  }

  public void setHakukohdeOid(String hakukohdeOid) {
    this.hakukohdeOid = hakukohdeOid;
  }

  public String getKieliNimi() {
    return kieliNimi;
  }

  public void setKieliNimi(String kieliNimi) {
    this.kieliNimi = kieliNimi;
  }

  public String getValintakoetyyppi() {
    return valintakoetyyppi;
  }

  public void setValintakoetyyppi(String valintakoetyyppi) {
    this.valintakoetyyppi = valintakoetyyppi;
  }

  public List<ValintakoePisterajaV1RDTO> getPisterajat() {
    return pisterajat;
  }

  public void setPisterajat(List<ValintakoePisterajaV1RDTO> pisterajat) {
    this.pisterajat = pisterajat;
  }

  public Map<String, String> getKuvaukset() {
    return kuvaukset;
  }

  public void setKuvaukset(Map<String, String> kuvaukset) {
    this.kuvaukset = kuvaukset;
  }

  public Map<String, String> getLisanaytot() {
    return lisanaytot;
  }

  public void setLisanaytot(Map<String, String> lisanaytot) {
    this.lisanaytot = lisanaytot;
  }

  public ValintakoePisterajaV1RDTO getValintakoePisteraja(String tyyppi) {
    for (ValintakoePisterajaV1RDTO valintakoePisterajaV1RDTO : pisterajat) {
      if (tyyppi.equals(valintakoePisterajaV1RDTO.getPisterajatyyppi())) {
        return valintakoePisterajaV1RDTO;
      }
    }
    return null;
  }

  public boolean hasPisterajat() {
    return !getPisterajat().isEmpty();
  }
}
