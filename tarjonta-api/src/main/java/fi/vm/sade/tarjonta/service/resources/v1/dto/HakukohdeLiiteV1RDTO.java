package fi.vm.sade.tarjonta.service.resources.v1.dto;

import fi.vm.sade.tarjonta.service.resources.dto.BaseRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "V1 Hakukohde's attachment(liite) REST-api model, used by KK-ui")
public class HakukohdeLiiteV1RDTO extends BaseRDTO {

  private static final long serialVersionUID = 1L;

  @Parameter(name = "Liite's hakukohde oid", required = true)
  private String hakukohdeOid;

  @Parameter(name = "Liite's name language uri", required = true)
  private String kieliUri;

  @Parameter(
          name = "Liite's name's language's name, used for displaying the value",
      required = false)
  private String kieliNimi;

  @Parameter(name = "Liite's name", required = true)
  private String liitteenNimi;

  @Parameter(name = "Liite's receiver", required = false)
  private String liitteenVastaanottaja;

  @Parameter(name = "Liite's tyyppi", required = true)
  private String liitteenTyyppi;

  @Parameter(name = "Liite's order", required = false)
  private Integer jarjestys;

  @Parameter(name = "Should this liite be used in hakulomake")
  private boolean kaytetaanHakulomakkeella = true;

  private Map<String, String> liitteenKuvaukset;

  private Date toimitettavaMennessa;

  private OsoiteRDTO liitteenToimitusOsoite;

  private String sahkoinenToimitusOsoite;

  public String getKieliUri() {
    return kieliUri;
  }

  public void setKieliUri(String kieliUri) {
    this.kieliUri = kieliUri;
  }

  public String getLiitteenNimi() {
    return liitteenNimi;
  }

  public void setLiitteenNimi(String liitteenNimi) {
    this.liitteenNimi = liitteenNimi;
  }

  public Date getToimitettavaMennessa() {
    return toimitettavaMennessa;
  }

  public void setToimitettavaMennessa(Date toimitettavaMennessa) {
    this.toimitettavaMennessa = toimitettavaMennessa;
  }

  public OsoiteRDTO getLiitteenToimitusOsoite() {
    return liitteenToimitusOsoite;
  }

  public void setLiitteenToimitusOsoite(OsoiteRDTO liitteenToimitusOsoite) {
    this.liitteenToimitusOsoite = liitteenToimitusOsoite;
  }

  public String getSahkoinenToimitusOsoite() {
    return sahkoinenToimitusOsoite;
  }

  public void setSahkoinenToimitusOsoite(String sahkoinenToimitusOsoite) {
    this.sahkoinenToimitusOsoite = sahkoinenToimitusOsoite;
  }

  public String getKieliNimi() {
    return kieliNimi;
  }

  public void setKieliNimi(String kieliNimi) {
    this.kieliNimi = kieliNimi;
  }

  public String getHakukohdeOid() {
    return hakukohdeOid;
  }

  public void setHakukohdeOid(String hakukohdeOid) {
    this.hakukohdeOid = hakukohdeOid;
  }

  public Map<String, String> getLiitteenKuvaukset() {
    return liitteenKuvaukset;
  }

  public void setLiitteenKuvaukset(Map<String, String> liitteenKuvaukset) {
    if (liitteenKuvaukset != null) {
      Map<String, String> cleanMap = new HashMap<String, String>();
      liitteenKuvaukset.forEach(
          (key, value) -> {
            String kieli = key.split("#")[0];
            cleanMap.put(kieli, value);
          });
      liitteenKuvaukset = cleanMap;
    }
    this.liitteenKuvaukset = liitteenKuvaukset;
  }

  public String getLiitteenTyyppi() {
    return liitteenTyyppi;
  }

  public void setLiitteenTyyppi(String liitteenTyyppi) {
    this.liitteenTyyppi = liitteenTyyppi;
  }

  public void setJarjestys(Integer jarjestys) {
    this.jarjestys = jarjestys;
  }

  public Integer getJarjestys() {
    return jarjestys;
  }

  public boolean isKaytetaanHakulomakkeella() {
    return kaytetaanHakulomakkeella;
  }

  public void setKaytetaanHakulomakkeella(boolean kaytetaanHakulomakkeella) {
    this.kaytetaanHakulomakkeella = kaytetaanHakulomakkeella;
  }

  public String getLiitteenVastaanottaja() {
    return liitteenVastaanottaja;
  }

  public void setLiitteenVastaanottaja(String liitteenVastaanottaja) {
    this.liitteenVastaanottaja = liitteenVastaanottaja;
  }
}
