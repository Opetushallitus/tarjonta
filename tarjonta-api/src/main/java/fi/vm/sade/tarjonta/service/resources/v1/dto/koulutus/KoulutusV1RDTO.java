package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OppiaineV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.types.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@Tag(name = "Koulutuksien yleiset tiedot sisältävä rajapintaolio")
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "toteutustyyppi",
    include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes({
  @Type(value = AmmattitutkintoV1RDTO.class, name = "AMMATTITUTKINTO"),
  @Type(value = ErikoisammattitutkintoV1RDTO.class, name = "ERIKOISAMMATTITUTKINTO"),
  @Type(
      value = KoulutusAmmatillinenPeruskoulutusErityisopetuksenaV1RDTO.class,
      name = "AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA"),
  @Type(
      value = KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO.class,
      name = "AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA"),
  @Type(value = KoulutusAmmatillinenPerustutkintoV1RDTO.class, name = "AMMATILLINEN_PERUSTUTKINTO"),
  @Type(
      value = KoulutusAmmatilliseenPeruskoulutukseenOhjaavaJaValmistavaV1RDTO.class,
      name = "AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS"),
  @Type(value = KoulutusKorkeakouluV1RDTO.class, name = "KORKEAKOULUTUS"),
  @Type(value = KorkeakouluOpintoV1RDTO.class, name = "KORKEAKOULUOPINTO"),
  @Type(
      value = KoulutusLukioAikuistenOppimaaraV1RDTO.class,
      name = "LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA"),
  @Type(value = KoulutusEbRpIshV1RDTO.class, name = "EB_RP_ISH"),
  @Type(value = KoulutusLukioV1RDTO.class, name = "LUKIOKOULUTUS"),
  @Type(
      value = KoulutusAmmatilliseenPeruskoulutukseenValmentavaV1RDTO.class,
      name = "AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA"),
  @Type(
      value = KoulutusAmmatilliseenPeruskoulutukseenValmentavaERV1RDTO.class,
      name = "AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER"),
  @Type(
      value = KoulutusMaahanmuuttajienAmmatilliseenPeruskoulutukseenValmistavaV1RDTO.class,
      name = "MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS"),
  @Type(
      value = KoulutusMaahanmuuttajienJaVieraskielistenLukiokoulutukseenValmistavaV1RDTO.class,
      name = "MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS"),
  @Type(value = KoulutusPerusopetuksenLisaopetusV1RDTO.class, name = "PERUSOPETUKSEN_LISAOPETUS"),
  @Type(
      value = KoulutusValmentavaJaKuntouttavaV1RDTO.class,
      name = "VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS"),
  @Type(value = KoulutusVapaanSivistystyonV1RDTO.class, name = "VAPAAN_SIVISTYSTYON_KOULUTUS"),
  @Type(value = KoulutusAikuistenPerusopetusV1RDTO.class, name = "AIKUISTEN_PERUSOPETUS"),
  @Type(value = PelastusalanKoulutusV1RDTO.class, name = "PELASTUSALAN_KOULUTUS"),
  @Type(
      value = KoulutusAmmatillinenPerustutkintoAlk2018V1RDTO.class,
      name = "AMMATILLINEN_PERUSTUTKINTO_ALK_2018")
})
public abstract class KoulutusV1RDTO extends KoulutusmoduuliStandardRelationV1RDTO {

  @Parameter(
      name = "Koulutuksen toteutuksen tarkasti yksiloiva enumeraatio",
      required = true)
  private ToteutustyyppiEnum toteutustyyppi;

  @Parameter(name = "Koulutusmoduulin karkeasti yksilöivä enumeraatio", required = true)
  private ModuulityyppiEnum moduulityyppi;

  @Parameter(name = "Koulutusmoduulin yksilöivä tunniste")
  private String komoOid;

  @Parameter(name = "Koulutusmoduulin totetuksen yksilöivä tunniste")
  private String komotoOid;

  @Parameter(
          name = "Tarjoaja tai organisaation johon koulutus on liitetty",
      required = true)
  private OrganisaatioV1RDTO organisaatio;

  @Parameter(
          name = "Tutkinto-ohjelman nimi monella kielella, ainakin yksi kieli pitää olla täytetty",
      required = true)
  private NimiV1RDTO koulutusohjelma;

  @Parameter(
          name =
          "Tutkinto-ohjelman tunniste, oppilaitoksen oma tunniste järjestettävälle koulutukselle",
      required = false)
  private String tunniste;

  @Parameter(
          name = "Oppilaitoksen globaalisti uniikki tunniste koulutukselle",
      required = false)
  private String uniqueExternalId;

  @Parameter(name = "Hakijalle näytettävä tunniste", required = false)
  private String hakijalleNaytettavaTunniste;

  // OTHER DATA
  @Parameter(name = "Koulutuksen julkaisun tila", required = true)
  // allowableValues = "LUONNOS,VALMIS,JULKAISTU,PERUTTU,KOPIOITU"
  private TarjontaTila tila;

  @Parameter(name = "Koulutuksen koulutusmoduulin tyyppi", required = true)
  private KoulutusmoduuliTyyppi koulutusmoduuliTyyppi;

  @Parameter(name = "Koulutuksen koulutusmoduulin monikieliset kuvaustekstit")
  private KuvausV1RDTO<KomoTeksti> kuvausKomo;

  @Parameter(name = "Koulutuksen koulutusmoduulin toteutuksen monikieliset kuvaustekstit")
  private KuvausV1RDTO<KomotoTeksti> kuvausKomoto;

  @Parameter(name = "Koulutuksen suunntellun keston arvo", required = true)
  private String suunniteltuKestoArvo;

  @Parameter(
          name = "Koulutuksen suunntellun keston tyyppi (koodisto koodi uri)",
      required = true)
  private KoodiV1RDTO suunniteltuKestoTyyppi;

  @Parameter(
          name =
          "Koulutuksen alkamiskausi koodisto koodi uri, jos ei määritetty ainakin yksi alkamispvm pitää olla valittuna")
  private KoodiV1RDTO koulutuksenAlkamiskausi;

  @Parameter(
          name =
          "Koulutuksen alkamisvuosi, jos ei määritetty ainakin yksi alkamispvm pitää olla valittuna")
  private Integer koulutuksenAlkamisvuosi;

  @Parameter(
          name =
          "Koulutuksen alkamispvm, voi olla tyhjä, jos tyhjä alkamiskausi ja alkamisvuosi pitää olla valittuna")
  private Set<Date> koulutuksenAlkamisPvms;

  @Parameter(
          name =
          "Koulutuksen opetuskielet, ainakin yksi kieli pitää olla syötetty (sisältää koodisto koodi uri:a)",
      required = true)
  private KoodiUrisV1RDTO opetuskielis;

  @Parameter(
          name = "Koulutuksen opetusmuodot (sisältää koodisto koodi uri:a)",
      required = true)
  private KoodiUrisV1RDTO opetusmuodos;

  @Parameter(
          name = "Koulutuksen opetusajat (esim. Iltaopetus) (sisältää koodisto koodi uri:a)",
      required = true)
  private KoodiUrisV1RDTO opetusAikas;

  @Parameter(
          name = "Koulutuksen opetuspaikat (sisältää koodisto koodi uri:a)",
      required = true)
  private KoodiUrisV1RDTO opetusPaikkas;

  @Parameter(name = "Opintojen laajuuden arvo", required = true)
  private KoodiV1RDTO opintojenLaajuusarvo;

  @Parameter(name = "Opintojen järjestäjät", required = false)
  private Set<String> opetusJarjestajat;

  @Parameter(name = "Opintojen tarjoajat", required = false)
  private Set<String> opetusTarjoajat;

  @Parameter(name = "Koulutuksen ammattinimikkeet (sisältää koodisto koodi uri:a)")
  private KoodiUrisV1RDTO ammattinimikkeet;

  @Parameter(name = "Koulutuksen aiheet (sisältää koodisto koodi uri:a)")
  private KoodiUrisV1RDTO aihees;

  @Parameter(name = "Koulutuksen yläpuoliset kouloutukset")
  private Set<String> parents;

  @Parameter(name = "Koulutuksen lapset")
  private Set<String> children;

  @Parameter(
          name =
          "Koulutuksen hinta (korvaa vanhan Double-tyyppisen hinnan, koska pitää tukea myös muita kun numeroita)")
  private String hintaString;

  @Parameter(
          name = "Koulutuksen hinta, on pakollinen jos koulutus on merkitty maksulliseksi",
      required = false)
  private Double hinta;

  @Parameter(name = "Valitaan opintojen maksullisuuden (false=koulutus ei vaadi maksua)")
  private Boolean opintojenMaksullisuus;

  @Parameter(name = "Onko koulutus avoimen yliopiston/ammattikorkeakoulun koulutus")
  private Boolean isAvoimenYliopistonKoulutus;

  @Parameter(name = "Oppiaineet")
  private Set<OppiaineV1RDTO> oppiaineet;

  @Parameter(name = "Opintopolussa näytettävä koulutuksen alkaminen")
  private Map opintopolkuAlkamiskausi;

  @Parameter(
          name =
          "Map-rakenne ylimääräisille parametreille, joita voi tarvittaessa hyödyntää tallennuksen yhteydessä")
  private Map<String, String> extraParams;

  @Parameter(name = "Koulutukseen sisältyvät koulutuskoodit", required = false)
  private KoodiUrisV1RDTO sisaltyvatKoulutuskoodit;

  @Parameter(name = "Koulutukset, joihin tämä koulutus sisältyy", required = false)
  private Set<KoulutusIdentification> sisaltyyKoulutuksiin;

  @Parameter(name = "Koulutuslaji-koodi", required = false)
  private KoodiV1RDTO koulutuslaji;

  public KoulutusV1RDTO(ToteutustyyppiEnum toteutustyyppi, ModuulityyppiEnum moduulityyppi) {
    this.setToteutustyyppi(toteutustyyppi);
    this.moduulityyppi = moduulityyppi;
  }

  public KoulutusV1RDTO() {}

  public void setHintaString(String hintaString) {
    this.hintaString = hintaString;
  }

  public String getHintaString() {
    return hintaString;
  }

  public Double getHinta() {
    return hinta;
  }

  public void setHinta(Double hinta) {
    this.hinta = hinta;
  }

  public Boolean getOpintojenMaksullisuus() {
    return opintojenMaksullisuus;
  }

  public void setOpintojenMaksullisuus(Boolean opintojenMaksullisuus) {
    this.opintojenMaksullisuus = opintojenMaksullisuus;
  }

  public String getKomoOid() {
    return komoOid;
  }

  public void setKomoOid(String _komoOid) {
    this.komoOid = _komoOid;
  }

  private Set<YhteyshenkiloTyyppi> yhteyshenkilos;

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

  public Set<YhteyshenkiloTyyppi> getYhteyshenkilos() {
    return yhteyshenkilos;
  }

  public void setYhteyshenkilos(Set<YhteyshenkiloTyyppi> yhteyshenkilos) {
    this.yhteyshenkilos = yhteyshenkilos;
  }

  public OrganisaatioV1RDTO getOrganisaatio() {
    return organisaatio;
  }

  public void setOrganisaatio(OrganisaatioV1RDTO organisaatio) {
    this.organisaatio = organisaatio;
  }

  public Boolean getIsAvoimenYliopistonKoulutus() {
    return isAvoimenYliopistonKoulutus;
  }

  public void setIsAvoimenYliopistonKoulutus(Boolean isAvoimenYliopistonKoulutus) {
    this.isAvoimenYliopistonKoulutus = isAvoimenYliopistonKoulutus;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.reflectionToString(this);
  }

  public KuvausV1RDTO<KomoTeksti> getKuvausKomo() {
    return kuvausKomo;
  }

  public void setKuvausKomo(KuvausV1RDTO<KomoTeksti> kuvausKomo) {
    this.kuvausKomo = kuvausKomo;
  }

  public KuvausV1RDTO<KomotoTeksti> getKuvausKomoto() {
    return kuvausKomoto;
  }

  public void setKuvausKomoto(KuvausV1RDTO<KomotoTeksti> kuvausKomoto) {
    this.kuvausKomoto = kuvausKomoto;
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

  public NimiV1RDTO getKoulutusohjelma() {
    return koulutusohjelma;
  }

  public void setKoulutusohjelma(NimiV1RDTO koulutusohjelma) {
    this.koulutusohjelma = koulutusohjelma;
  }

  public String getTunniste() {
    return tunniste;
  }

  public void setTunniste(String tunniste) {
    this.tunniste = tunniste;
  }

  public Set<Date> getKoulutuksenAlkamisPvms() {
    return koulutuksenAlkamisPvms;
  }

  public void setKoulutuksenAlkamisPvms(Set<Date> koulutuksenAlkamisPvms) {
    this.koulutuksenAlkamisPvms = koulutuksenAlkamisPvms;
  }

  public KoodiV1RDTO getKoulutuksenAlkamiskausi() {
    return koulutuksenAlkamiskausi;
  }

  public void setKoulutuksenAlkamiskausi(KoodiV1RDTO koulutuksenAlkamiskausi) {
    this.koulutuksenAlkamiskausi = koulutuksenAlkamiskausi;
  }

  public Integer getKoulutuksenAlkamisvuosi() {
    return koulutuksenAlkamisvuosi;
  }

  public void setKoulutuksenAlkamisvuosi(Integer koulutuksenAlkamisvuosi) {
    this.koulutuksenAlkamisvuosi = koulutuksenAlkamisvuosi;
  }

  public KoodiUrisV1RDTO getOpetuskielis() {
    return opetuskielis;
  }

  public void setOpetuskielis(KoodiUrisV1RDTO opetuskielis) {
    this.opetuskielis = opetuskielis;
  }

  public KoodiUrisV1RDTO getOpetusmuodos() {
    return opetusmuodos;
  }

  public void setOpetusmuodos(KoodiUrisV1RDTO opetusmuodos) {
    this.opetusmuodos = opetusmuodos;
  }

  public KoodiUrisV1RDTO getOpetusAikas() {
    return opetusAikas;
  }

  public void setOpetusAikas(KoodiUrisV1RDTO opetusAikas) {
    this.opetusAikas = opetusAikas;
  }

  public KoodiUrisV1RDTO getOpetusPaikkas() {
    return opetusPaikkas;
  }

  public void setOpetusPaikkas(KoodiUrisV1RDTO opetusPaikkas) {
    this.opetusPaikkas = opetusPaikkas;
  }

  public KoodiV1RDTO getOpintojenLaajuusarvo() {
    return opintojenLaajuusarvo;
  }

  public void setOpintojenLaajuusarvo(KoodiV1RDTO opintojenLaajuusarvo) {
    this.opintojenLaajuusarvo = opintojenLaajuusarvo;
  }

  public String getKomotoOid() {
    return komotoOid;
  }

  public void setKomotoOid(String _komotoOid) {
    this.komotoOid = _komotoOid;
  }

  public ToteutustyyppiEnum getToteutustyyppi() {
    return toteutustyyppi;
  }

  public void setToteutustyyppi(ToteutustyyppiEnum toteutustyyppi) {
    this.toteutustyyppi = toteutustyyppi;
  }

  public ModuulityyppiEnum getModuulityyppi() {
    return moduulityyppi;
  }

  public void setModuulityyppi(ModuulityyppiEnum moduulityyppi) {
    this.moduulityyppi = moduulityyppi;
  }

  public Set<String> getOpetusJarjestajat() {
    return opetusJarjestajat;
  }

  public void setOpetusJarjestajat(Set<String> opetusJarjestajat) {
    opetusJarjestajat = (opetusJarjestajat != null) ? opetusJarjestajat : new HashSet<String>();
    this.opetusJarjestajat = opetusJarjestajat;
  }

  public Set<String> getOpetusTarjoajat() {
    return opetusTarjoajat;
  }

  public void setOpetusTarjoajat(Set<String> opetusTarjoajat) {
    opetusTarjoajat = (opetusTarjoajat != null) ? opetusTarjoajat : new HashSet<String>();
    this.opetusTarjoajat = opetusTarjoajat;
  }

  public KoodiUrisV1RDTO getAmmattinimikkeet() {
    return ammattinimikkeet;
  }

  public void setAmmattinimikkeet(KoodiUrisV1RDTO ammattinimikkeet) {
    this.ammattinimikkeet = ammattinimikkeet;
  }

  public KoodiUrisV1RDTO getAihees() {
    return aihees;
  }

  public void setAihees(KoodiUrisV1RDTO aihees) {
    this.aihees = aihees;
  }

  public void setParents(Set<String> parents) {
    this.parents = parents;
  }

  public Set<String> getParents() {
    return parents;
  }

  public void setChildren(Set<String> children) {
    this.children = children;
  }

  public Set<String> getChildren() {
    return children;
  }

  public Set<OppiaineV1RDTO> getOppiaineet() {
    return oppiaineet;
  }

  public void setOppiaineet(Set<OppiaineV1RDTO> oppiaineet) {
    this.oppiaineet = oppiaineet;
  }

  public Map getOpintopolkuAlkamiskausi() {
    return opintopolkuAlkamiskausi;
  }

  public void setOpintopolkuAlkamiskausi(Map opintopolkuAlkamiskausi) {
    this.opintopolkuAlkamiskausi = opintopolkuAlkamiskausi;
  }

  public Map<String, String> getExtraParams() {
    return extraParams;
  }

  public void setExtraParams(Map<String, String> extraParams) {
    this.extraParams = extraParams;
  }

  public KoodiUrisV1RDTO getSisaltyvatKoulutuskoodit() {
    return sisaltyvatKoulutuskoodit;
  }

  public void setSisaltyvatKoulutuskoodit(KoodiUrisV1RDTO sisaltyvatKoulutuskoodit) {
    this.sisaltyvatKoulutuskoodit = sisaltyvatKoulutuskoodit;
  }

  public String getHakijalleNaytettavaTunniste() {
    return hakijalleNaytettavaTunniste;
  }

  public void setHakijalleNaytettavaTunniste(String hakijalleNaytettavaTunniste) {
    this.hakijalleNaytettavaTunniste = hakijalleNaytettavaTunniste;
  }

  public Set<KoulutusIdentification> getSisaltyyKoulutuksiin() {
    return sisaltyyKoulutuksiin;
  }

  public void setSisaltyyKoulutuksiin(Set<KoulutusIdentification> sisaltyyKoulutuksiin) {
    this.sisaltyyKoulutuksiin = sisaltyyKoulutuksiin;
  }

  public String getUniqueExternalId() {
    return uniqueExternalId;
  }

  public void setUniqueExternalId(String uniqueExternalId) {
    this.uniqueExternalId = uniqueExternalId;
  }

  public KoodiV1RDTO getKoulutuslaji() {
    return koulutuslaji;
  }

  public void setKoulutuslaji(KoodiV1RDTO koulutuslaji) {
    this.koulutuslaji = koulutuslaji;
  }
}
