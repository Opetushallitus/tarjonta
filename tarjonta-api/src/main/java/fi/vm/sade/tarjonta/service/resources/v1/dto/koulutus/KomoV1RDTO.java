package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
    name = "Ysittäisen koulutusmoduulin luontiin ja tiedon hakemiseen käytettävä rajapintaolio")
public class KomoV1RDTO extends KoulutusmoduuliStandardRelationV1RDTO {

  private static final long serialVersionUID = 1L;

  @Parameter(name = "Koulutusmoduulin yksilöivä tunniste")
  private String komoOid;

  @Parameter(
          name = "Tarjoaja tai organisaation johon koulutus on liitetty",
      required = true)
  private OrganisaatioV1RDTO organisaatio;

  @Parameter(
          name =
          "Tutkinto-ohjelman tunniste, oppilaitoksen oma tunniste järjestettävälle koulutukselle",
      required = true)
  private String tunniste;

  @Parameter(name = "Nimi monella kielella", required = false)
  private NimiV1RDTO nimi;

  @Parameter(
          name = "Moduulin julkaisun tila",
      required = true) // allowableValues = "LUONNOS,VALMIS,JULKAISTU,PERUTTU,KOPIOITU"
  private TarjontaTila tila;

  @Parameter(name = "Koulutuksen koulutusmoduulin tyyppi", required = true)
  private KoulutusmoduuliTyyppi koulutusmoduuliTyyppi;

  @Parameter(name = "Koulutuksen koulutusastetyyppi", required = true)
  private KoulutusasteTyyppi koulutusasteTyyppi;

  @Parameter(name = "Koulutuksen koulutusmoduulin monikieliset kuvaustekstit")
  private KuvausV1RDTO<KomoTeksti> kuvausKomo;

  @Parameter(name = "Koulutuksen suunntellun keston arvo", required = true)
  private String suunniteltuKestoArvo;

  @Parameter(
          name = "Koulutuksen suunntellun keston tyyppi (koodisto koodi uri)",
      required = true)
  private KoodiV1RDTO suunniteltuKestoTyyppi;

  @Parameter(
          name =
          "OPH oppilaitostyyppi-koodit (vain ammatillisella- ja lukio-koulutuksella) Huom! Tieto saattaa poistu tulevissa versioissa-",
      required = true)
  private KoodiUrisV1RDTO oppilaitostyyppis;

  @Parameter(
          name =
          "OPH tutkintonimike-koodit (korkeakoulutuksella eri koodistot kuin ammatillisella- ja lukio-koulutuksella)",
      required = true)
  private KoodiUrisV1RDTO tutkintonimikes;

  @Parameter(name = "Opintojen laajuuden arvo", required = true)
  private KoodiV1RDTO opintojenLaajuusarvo;

  @Parameter(name = "OPH koulutustyyppi-koodit", required = false)
  private KoodiUrisV1RDTO koulutustyyppis;

  @Parameter(name = "OPH lukiolinja-koodi", required = false)
  private KoodiV1RDTO lukiolinja;

  @Parameter(name = "OPH osaamisala-koodi", required = false)
  private KoodiV1RDTO osaamisala;

  @Parameter(name = "OPH koulutusohjelma-koodi", required = false)
  private KoodiV1RDTO koulutusohjelma;

  public KomoV1RDTO() {}

  public KoodiUrisV1RDTO getOppilaitostyyppis() {
    if (oppilaitostyyppis == null) {
      oppilaitostyyppis = new KoodiUrisV1RDTO();
    }

    return oppilaitostyyppis;
  }

  public void setOppilaitostyyppis(KoodiUrisV1RDTO oppilaitostyyppis) {
    this.oppilaitostyyppis = oppilaitostyyppis;
  }

  public KoodiUrisV1RDTO getTutkintonimikes() {
    if (tutkintonimikes == null) {
      tutkintonimikes = new KoodiUrisV1RDTO();
    }

    return tutkintonimikes;
  }

  public void setTutkintonimikes(KoodiUrisV1RDTO tutkintonimikes) {
    this.tutkintonimikes = tutkintonimikes;
  }

  public KoodiV1RDTO getOpintojenLaajuusarvo() {
    return opintojenLaajuusarvo;
  }

  public void setOpintojenLaajuusarvo(KoodiV1RDTO opintojenLaajuusarvo) {
    this.opintojenLaajuusarvo = opintojenLaajuusarvo;
  }

  public String getKomoOid() {
    return komoOid;
  }

  public void setKomoOid(String komoOid) {
    this.komoOid = komoOid;
  }

  public String getTunniste() {
    return tunniste;
  }

  public void setTunniste(String tunniste) {
    this.tunniste = tunniste;
  }

  public TarjontaTila getTila() {
    return tila;
  }

  public void setTila(TarjontaTila tila) {
    this.tila = tila;
  }

  public KoulutusmoduuliTyyppi getKoulutusmoduuliTyyppi() {
    return koulutusmoduuliTyyppi;
  }

  public void setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi koulutusmoduuliTyyppi) {
    this.koulutusmoduuliTyyppi = koulutusmoduuliTyyppi;
  }

  public KoulutusasteTyyppi getKoulutusasteTyyppi() {
    return koulutusasteTyyppi;
  }

  public void setKoulutusasteTyyppi(KoulutusasteTyyppi koulutusasteTyyppi) {
    this.koulutusasteTyyppi = koulutusasteTyyppi;
  }

  public KuvausV1RDTO<KomoTeksti> getKuvausKomo() {
    if (kuvausKomo == null) {
      kuvausKomo = new KuvausV1RDTO<KomoTeksti>();
    }

    return kuvausKomo;
  }

  public void setKuvausKomo(KuvausV1RDTO<KomoTeksti> kuvausKomo) {
    this.kuvausKomo = kuvausKomo;
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

  public OrganisaatioV1RDTO getOrganisaatio() {
    if (organisaatio == null) {
      organisaatio = new OrganisaatioV1RDTO();
    }

    return organisaatio;
  }

  public void setOrganisaatio(OrganisaatioV1RDTO organisaatio) {
    this.organisaatio = organisaatio;
  }

  public KoodiUrisV1RDTO getKoulutustyyppis() {
    if (koulutustyyppis == null) {
      koulutustyyppis = new KoodiUrisV1RDTO();
    }

    return koulutustyyppis;
  }

  public void setKoulutustyyppis(KoodiUrisV1RDTO koulutustyyppis) {
    this.koulutustyyppis = koulutustyyppis;
  }

  public KoodiV1RDTO getLukiolinja() {
    return lukiolinja;
  }

  public void setLukiolinja(KoodiV1RDTO lukiolinja) {
    this.lukiolinja = lukiolinja;
  }

  public KoodiV1RDTO getOsaamisala() {
    return osaamisala;
  }

  public void setOsaamisala(KoodiV1RDTO osaamisala) {
    this.osaamisala = osaamisala;
  }

  public NimiV1RDTO getNimi() {
    if (nimi == null) {
      nimi = new NimiV1RDTO();
    }

    return nimi;
  }

  public void setNimi(NimiV1RDTO nimi) {
    this.nimi = nimi;
  }

  public KoodiV1RDTO getKoulutusohjelma() {
    return koulutusohjelma;
  }

  public void setKoulutusohjelma(KoodiV1RDTO koulutusohjelma) {
    this.koulutusohjelma = koulutusohjelma;
  }
}
