package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.HashMap;
import java.util.Map;

@ApiModel(value = "Korkeakoulutuksen luontiin ja tiedon hakemiseen käytettävä rajapintaolio")
public class KoulutusKorkeakouluV1RDTO extends KoulutusV1RDTO {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "Suhde hierarkian parent koulutusmoduuliin")
  private String parentKomoOid;

  @ApiModelProperty(value = "Suhde hierarkian parent koulutusmoduulin toteutukseen")
  private String parentKomotoOid;

  @ApiModelProperty(
      value = "Koulutuksen pohjakoulutusvaatimukset (sisältää koodisto koodi uri:a)",
      required = true)
  private KoodiUrisV1RDTO pohjakoulutusvaatimukset;

  @ApiModelProperty(
      value =
          "OPH tutkintonimike-koodit (korkeakoulutuksella eri koodistot kuin ammatillisella- ja lukio-koulutuksella)",
      required = true)
  private KoodiUrisV1RDTO tutkintonimikes;

  @ApiModelProperty(
      value =
          "Maisterin koulutukseen (maisteri+kandi) liitettävän kandidaatin koulutuksen koulutuskoodi",
      required = false)
  private KoodiV1RDTO kandidaatinKoulutuskoodi;

  @ApiModelProperty(
      value = "Opintojen rakenteen kuvat eroteltuna kooditon kieli uri:lla.",
      required = false)
  private Map<String, KuvaV1RDTO> opintojenRakenneKuvas;

  @ApiModelProperty(
      value =
          "tunniste, joka yksilöi KK-koulutuksen (aiemmin tähän käytettiin komonOidia, katso KJOH-973)")
  private String koulutuksenTunnisteOid;

  @ApiModelProperty(value = "Johtaa korkekoulututkintoon.")
  private boolean johtaaTutkintoon;

  @ApiModelProperty(value = "koulutuksen laajuus - koulutustyyppifasettikoodi KI:lle")
  private KoodiV1RDTO koulutuksenLaajuusKoodi;

  public KoodiUrisV1RDTO getTutkintonimikes() {
    if (this.tutkintonimikes == null) {
      this.tutkintonimikes = new KoodiUrisV1RDTO();
    }

    return tutkintonimikes;
  }

  public void setTutkintonimikes(KoodiUrisV1RDTO tutkintonimikes) {
    this.tutkintonimikes = tutkintonimikes;
  }

  public KoulutusKorkeakouluV1RDTO() {
    super(ToteutustyyppiEnum.KORKEAKOULUTUS, ModuulityyppiEnum.KORKEAKOULUTUS);
  }

  public KoodiUrisV1RDTO getPohjakoulutusvaatimukset() {
    if (pohjakoulutusvaatimukset == null) {
      pohjakoulutusvaatimukset = new KoodiUrisV1RDTO();
    }

    return pohjakoulutusvaatimukset;
  }

  public void setPohjakoulutusvaatimukset(KoodiUrisV1RDTO pohjakoulutusvaatimukset) {
    this.pohjakoulutusvaatimukset = pohjakoulutusvaatimukset;
  }

  public String getParentKomoOid() {
    return parentKomoOid;
  }

  public void setParentKomoOid(String _parentKomoOid) {
    this.parentKomoOid = _parentKomoOid;
  }

  public String getParentKomotoOid() {
    return parentKomotoOid;
  }

  public void setParentKomotoOid(String _parentKomotoOid) {
    this.parentKomotoOid = _parentKomotoOid;
  }

  public KoodiV1RDTO getKandidaatinKoulutuskoodi() {
    return kandidaatinKoulutuskoodi;
  }

  public void setKandidaatinKoulutuskoodi(KoodiV1RDTO kandidaatinKoulutuskoodi) {
    this.kandidaatinKoulutuskoodi = kandidaatinKoulutuskoodi;
  }

  public Map<String, KuvaV1RDTO> getOpintojenRakenneKuvas() {
    if (opintojenRakenneKuvas == null) {
      opintojenRakenneKuvas = new HashMap<String, KuvaV1RDTO>();
    }

    return opintojenRakenneKuvas;
  }

  public void setOpintojenRakenneKuvas(Map<String, KuvaV1RDTO> opintojenRakenneKuvas) {
    this.opintojenRakenneKuvas = opintojenRakenneKuvas;
  }

  public String getKoulutuksenTunnisteOid() {
    return koulutuksenTunnisteOid;
  }

  public void setKoulutuksenTunnisteOid(String koulutuksenTunnisteOid) {
    this.koulutuksenTunnisteOid = koulutuksenTunnisteOid;
  }

  public boolean isJohtaaTutkintoon() {
    return johtaaTutkintoon;
  }

  public void setJohtaaTutkintoon(boolean johtaaTutkintoon) {
    this.johtaaTutkintoon = johtaaTutkintoon;
  }

  public KoodiV1RDTO getKoulutuksenLaajuusKoodi() {
    return koulutuksenLaajuusKoodi;
  }

  public void setKoulutuksenLaajuusKoodi(KoodiV1RDTO koulutuksenLaajuusKoodi) {
    this.koulutuksenLaajuusKoodi = koulutuksenLaajuusKoodi;
  }
}
