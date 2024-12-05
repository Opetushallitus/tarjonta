package fi.vm.sade.tarjonta.service.resources.dto;

import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KomotoDTO extends BaseRDTO {

  private static final long serialVersionUID = 1L;

  private Date _koulutuksenAlkamisDate;
  private List<Date> koulutuksenAlkamisDates;
  private boolean _maksullisuus;
  private String _komoOid;
  private String _pohjakoulutusVaatimusUri;
  private Map<String, String> _webLinkkis;
  private List<String> _avainsanatUris;
  private List<String> _ammattinimikeUris;
  private List<String> _koulutuslajiUris;
  private List<String> _lukiodiplomitUris;
  private List<String> _opetuskieletUris;
  private List<String> _opetusmuodotUris;
  private List<String> _teematUris;
  private String _laajuusArvo;
  private String _laajuusYksikkoUri;
  private String suunniteltuKestoArvo;
  private String suunniteltuKestoYksikkoUri;
  private String _tarjoajaOid; // onko sama kuin organisaatioOid?
  private TarjontaTila _tila;
  private String _ulkoinenTunniste;
  private String _parentKomotoOid;
  private String koulutusohjelmanNimi; // vapaavalintainen nimi, er OVT-6619

  private String koulutusAlaUri;
  private String eqfLuokitusUri;
  private String nqfLuokitusUri;
  private String koulutusAsteUri;
  private String koulutusKoodiUri;
  private String koulutusohjelmaUri;
  private String tutkintoUri;
  private String opintojenLaajuusarvoUri;
  private String lukiolinjaUri;
  private String koulutustyyppiUri;
  private String opintoalaUri;
  private String kandidaatinKoulutusUri;
  private String tutkintonimikeUri;
  private List<String> tutkintonimikeUris;

  private List<String> _opetusmuotokk;
  private List<String> _opetusaikakk;
  private List<String> _opetuspaikkakk;
  private List<String> _koulutuksenlaajuus;

  private String koulutuksenAlkamiskausi;
  private Integer koulutuksenAlkamisvuosi;

  public String getKoulutusohjelmanNimi() {
    return koulutusohjelmanNimi;
  }

  public void setKoulutusohjelmanNimi(String koulutusohjelmanNimi) {
    this.koulutusohjelmanNimi = koulutusohjelmanNimi;
  }

  private Map<KomotoTeksti, Map<String, String>> _tekstit;

  private List<YhteyshenkiloRDTO> _yhteyshenkilos;

  // Lukio
  private Map<String, List<String>> _tarjotutKielet;

  public Map<KomotoTeksti, Map<String, String>> getTekstit() {
    if (_tekstit == null) {
      _tekstit = new EnumMap<KomotoTeksti, Map<String, String>>(KomotoTeksti.class);
    }
    return _tekstit;
  }

  public void setTekstit(Map<KomotoTeksti, Map<String, String>> _tekstit) {
    this._tekstit = _tekstit;
  }

  public Date getKoulutuksenAlkamisDate() {
    return _koulutuksenAlkamisDate;
  }

  public void setKoulutuksenAlkamisDate(Date koulutuksenAlkamisDate) {
    this._koulutuksenAlkamisDate = koulutuksenAlkamisDate;
  }

  public boolean isMaksullisuus() {
    return _maksullisuus;
  }

  public void setMaksullisuus(boolean maksullisuus) {
    this._maksullisuus = maksullisuus;
  }

  public String getKomoOid() {
    return _komoOid;
  }

  public void setKomoOid(String _komoOid) {
    this._komoOid = _komoOid;
  }

  public void setPohjakoulutusVaatimusUri(String _pohjakoulutusVaatimusUri) {
    this._pohjakoulutusVaatimusUri = _pohjakoulutusVaatimusUri;
  }

  public String getPohjakoulutusVaatimusUri() {
    return _pohjakoulutusVaatimusUri;
  }

  @Deprecated
  public Map<String, String> getArviointiKriteerit() {
    return getTekstit().get(KomotoTeksti.ARVIOINTIKRITEERIT);
  }

  @Deprecated
  public void setArviointiKriteerit(Map<String, String> arviointiKriteerit) {
    getTekstit().put(KomotoTeksti.ARVIOINTIKRITEERIT, arviointiKriteerit);
  }

  @Deprecated
  public Map<String, String> getKansainvalistyminen() {
    return getTekstit().get(KomotoTeksti.KANSAINVALISTYMINEN);
  }

  @Deprecated
  public void setKansainvalistyminen(Map<String, String> kansainvalistyminen) {
    getTekstit().put(KomotoTeksti.KANSAINVALISTYMINEN, kansainvalistyminen);
  }

  @Deprecated
  public Map<String, String> getKuvailevatTiedot() {
    return getTekstit().get(KomotoTeksti.KUVAILEVAT_TIEDOT);
  }

  @Deprecated
  public void setKuvailevatTiedot(Map<String, String> kuvailevatTiedot) {
    getTekstit().put(KomotoTeksti.KUVAILEVAT_TIEDOT, kuvailevatTiedot);
  }

  @Deprecated
  public Map<String, String> getLoppukoeVaatimukset() {
    return getTekstit().get(KomotoTeksti.LOPPUKOEVAATIMUKSET);
  }

  @Deprecated
  public void setLoppukoeVaatimukset(Map<String, String> loppukoeVaatimukset) {
    getTekstit().put(KomotoTeksti.LOPPUKOEVAATIMUKSET, loppukoeVaatimukset);
  }

  @Deprecated
  public Map<String, String> getMaksullisuusKuvaus() {
    return getTekstit().get(KomotoTeksti.MAKSULLISUUS);
  }

  @Deprecated
  public void setMaksullisuusKuvaus(Map<String, String> maksullisuusKuvaus) {
    getTekstit().put(KomotoTeksti.MAKSULLISUUS, maksullisuusKuvaus);
  }

  @Deprecated
  public Map<String, String> getSijoittuminenTyoelamaan() {
    return getTekstit().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN);
  }

  @Deprecated
  public void setSijoittuminenTyoelamaan(Map<String, String> sijoittuminenTyoelamaan) {
    getTekstit().put(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN, sijoittuminenTyoelamaan);
  }

  @Deprecated
  public Map<String, String> getSisalto() {
    return getTekstit().get(KomotoTeksti.SISALTO);
  }

  @Deprecated
  public void setSisalto(Map<String, String> sisalto) {
    getTekstit().put(KomotoTeksti.SISALTO, sisalto);
  }

  @Deprecated
  public Map<String, String> getYhteistyoMuidenToimijoidenKanssa() {
    return getTekstit().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA);
  }

  @Deprecated
  public void setYhteistyoMuidenToimijoidenKanssa(
      Map<String, String> yhteistyoMuidenToimijoidenKanssa) {
    getTekstit()
        .put(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA, yhteistyoMuidenToimijoidenKanssa);
  }

  @Deprecated
  public Map<String, String> getKoulutusohjelmanValinta() {
    return getTekstit().get(KomotoTeksti.KOULUTUSOHJELMAN_VALINTA);
  }

  @Deprecated
  public void setKoulutusohjelmanValinta(Map<String, String> koulutusohjelmanValinta) {
    getTekstit().put(KomotoTeksti.KOULUTUSOHJELMAN_VALINTA, koulutusohjelmanValinta);
  }

  @Deprecated
  public Map<String, String> getPainotus() {
    return getTekstit().get(KomotoTeksti.PAINOTUS);
  }

  @Deprecated
  public void setPainotus(Map<String, String> painotus) {
    getTekstit().put(KomotoTeksti.PAINOTUS, painotus);
  }

  public Map<String, String> getWebLinkkis() {
    return _webLinkkis;
  }

  public void setWebLinkkis(Map<String, String> webLinkkis) {
    this._webLinkkis = webLinkkis;
  }

  public List<String> getAvainsanatUris() {
    return _avainsanatUris;
  }

  public void setAvainsanatUris(List<String> avainsanatUris) {
    this._avainsanatUris = avainsanatUris;
  }

  public List<String> getAmmattinimikeUris() {
    return _ammattinimikeUris;
  }

  public void setAmmattinimikeUris(List<String> ammattinimikeUris) {
    this._ammattinimikeUris = ammattinimikeUris;
  }

  public List<String> getKoulutuslajiUris() {
    return _koulutuslajiUris;
  }

  public void setKoulutuslajiUris(List<String> koulutuslajiUris) {
    this._koulutuslajiUris = koulutuslajiUris;
  }

  public List<String> getLukiodiplomitUris() {
    return _lukiodiplomitUris;
  }

  public void setLukiodiplomitUris(List<String> lukiodiplomitUris) {
    this._lukiodiplomitUris = lukiodiplomitUris;
  }

  public List<String> getOpetuskieletUris() {
    return _opetuskieletUris;
  }

  public void setOpetuskieletUris(List<String> opetuskieletUris) {
    this._opetuskieletUris = opetuskieletUris;
  }

  public List<String> getOpetusmuodotUris() {
    return _opetusmuodotUris;
  }

  public void setOpetusmuodotUris(List<String> opetusmuodotUris) {
    this._opetusmuodotUris = opetusmuodotUris;
  }

  public List<String> getOpetusmuotokk() {
    return _opetusmuotokk;
  }

  public void setOpetusmuotokk(List<String> opetusmuotokk) {
    this._opetusmuotokk = opetusmuotokk;
  }

  public List<String> getOpetusaikakk() {
    return _opetusaikakk;
  }

  public void setOpetusaikakk(List<String> opetusaikakk) {
    this._opetusaikakk = opetusaikakk;
  }

  public List<String> getKoulutuksenlaajuus() {
    return _koulutuksenlaajuus;
  }

  public void setKoulutuksenlaajuus(List<String> koulutuksenlaajuus) {
    this._koulutuksenlaajuus = koulutuksenlaajuus;
  }

  public List<String> getOpetuspaikkakk() {
    return _opetuspaikkakk;
  }

  public void setOpetuspaikkakk(List<String> opetuspaikkakk) {
    this._opetuspaikkakk = opetuspaikkakk;
  }

  public List<String> getTeematUris() {
    return _teematUris;
  }

  public void setTeematUris(List<String> teematUris) {
    this._teematUris = teematUris;
  }

  public String getLaajuusArvo() {
    return _laajuusArvo;
  }

  public void setLaajuusArvo(String laajuusArvo) {
    this._laajuusArvo = laajuusArvo;
  }

  public String getLaajuusYksikkoUri() {
    return _laajuusYksikkoUri;
  }

  public void setLaajuusYksikkoUri(String laajuusYksikkoUri) {
    this._laajuusYksikkoUri = laajuusYksikkoUri;
  }

  public String getTarjoajaOid() {
    return _tarjoajaOid;
  }

  public void setTarjoajaOid(String tarjoajaOid) {
    this._tarjoajaOid = tarjoajaOid;
  }

  public TarjontaTila getTila() {
    return _tila;
  }

  public void setTila(TarjontaTila tila) {
    this._tila = tila;
  }

  public String getUlkoinenTunniste() {
    return _ulkoinenTunniste;
  }

  public void setUlkoinenTunniste(String ulkoinenTunniste) {
    this._ulkoinenTunniste = ulkoinenTunniste;
  }

  public String getParentKomotoOid() {
    return _parentKomotoOid;
  }

  public void setParentKomotoOid(String parentKomotoOid) {
    this._parentKomotoOid = parentKomotoOid;
  }

  public List<YhteyshenkiloRDTO> getYhteyshenkilos() {
    if (_yhteyshenkilos == null) {
      _yhteyshenkilos = new ArrayList<YhteyshenkiloRDTO>();
    }
    return _yhteyshenkilos;
  }

  public void setYhteyshenkilos(List<YhteyshenkiloRDTO> yhteyshenkilos) {
    this._yhteyshenkilos = yhteyshenkilos;
  }

  public Map<String, List<String>> getTarjotutKielet() {
    if (_tarjotutKielet == null) {
      _tarjotutKielet = new HashMap<String, List<String>>();
    }
    return _tarjotutKielet;
  }

  public void setTarjotutKielet(Map<String, List<String>> _tarjotutKielet) {
    this._tarjotutKielet = _tarjotutKielet;
  }

  public void setSuunniteltuKestoArvo(String suunniteltuKestoArvo) {
    this.suunniteltuKestoArvo = suunniteltuKestoArvo;
  }

  public void setSuunniteltuKestoYksikkoUri(String suunniteltuKestoYksikkoUri) {
    this.suunniteltuKestoYksikkoUri = suunniteltuKestoYksikkoUri;
  }

  public String getSuunniteltuKestoYksikkoUri() {
    return suunniteltuKestoYksikkoUri;
  }

  public String getSuunniteltuKestoArvo() {
    return suunniteltuKestoArvo;
  }

  public String getKoulutusAlaUri() {
    return koulutusAlaUri;
  }

  public void setKoulutusAlaUri(String koulutusAlaUri) {
    this.koulutusAlaUri = koulutusAlaUri;
  }

  public String getEqfLuokitusUri() {
    return eqfLuokitusUri;
  }

  public void setEqfLuokitusUri(String eqfUri) {
    this.eqfLuokitusUri = eqfUri;
  }

  public String getNqfLuokitusUri() {
    return nqfLuokitusUri;
  }

  public void setNqfLuokitusUri(String nqfUri) {
    this.nqfLuokitusUri = nqfUri;
  }

  public String getKoulutusAsteUri() {
    return koulutusAsteUri;
  }

  public void setKoulutusAsteUri(String koulutusAsteUri) {
    this.koulutusAsteUri = koulutusAsteUri;
  }

  public String getKoulutusKoodiUri() {
    return koulutusKoodiUri;
  }

  public void setKoulutusKoodiUri(String koulutusUri) {
    this.koulutusKoodiUri = koulutusUri;
  }

  public String getKoulutusohjelmaUri() {
    return koulutusohjelmaUri;
  }

  public void setKoulutusohjelmaUri(String koulutusohjelmaUri) {
    this.koulutusohjelmaUri = koulutusohjelmaUri;
  }

  public String getTutkintoUri() {
    return tutkintoUri;
  }

  public void setTutkintoUri(String tutkintoUri) {
    this.tutkintoUri = tutkintoUri;
  }

  public String getOpintojenLaajuusarvoUri() {
    return opintojenLaajuusarvoUri;
  }

  public void setOpintojenLaajuusarvoUri(String opintojenLaajuusarvoUri) {
    this.opintojenLaajuusarvoUri = opintojenLaajuusarvoUri;
  }

  public String getLukiolinjaUri() {
    return lukiolinjaUri;
  }

  public void setLukiolinjaUri(String lukiolinjaUri) {
    this.lukiolinjaUri = lukiolinjaUri;
  }

  public String getKoulutustyyppiUri() {
    return koulutustyyppiUri;
  }

  public void setKoulutustyyppiUri(String koulutustyyppiUri) {
    this.koulutustyyppiUri = koulutustyyppiUri;
  }

  public String getOpintoalaUri() {
    return opintoalaUri;
  }

  public void setOpintoalaUri(String opintoalaUri) {
    this.opintoalaUri = opintoalaUri;
  }

  public String getKandidaatinKoulutusUri() {
    return kandidaatinKoulutusUri;
  }

  public void setKandidaatinKoulutusUri(String kandidaatinKoulutusUri) {
    this.kandidaatinKoulutusUri = kandidaatinKoulutusUri;
  }

  public String getTutkintonimikeUri() {
    return tutkintonimikeUri;
  }

  public void setTutkintonimikeUri(String tutkintonimikeUri) {
    this.tutkintonimikeUri = tutkintonimikeUri;
  }

  public List<String> getTutkintonimikeUris() {
    return tutkintonimikeUris;
  }

  public void setTutkintonimikeUris(List<String> tutkintonimikeUris) {
    this.tutkintonimikeUris = tutkintonimikeUris;
  }

  public void setKoulutuksenAlkamiskausi(String alkamiskausi) {
    this.koulutuksenAlkamiskausi = alkamiskausi;
  }

  public String getKoulutuksenAlkamiskausi() {
    return koulutuksenAlkamiskausi;
  }

  public void setKoulutuksenAlkamisvuosi(Integer vuosi) {
    this.koulutuksenAlkamisvuosi = vuosi;
  }

  public Integer getKoulutuksenAlkamisvuosi() {
    return koulutuksenAlkamisvuosi;
  }

  public List<Date> getKoulutuksenAlkamisDates() {
    return koulutuksenAlkamisDates;
  }

  public void setKoulutuksenAlkamisDates(List<Date> koulutuksenAlkamisDates) {
    this.koulutuksenAlkamisDates = koulutuksenAlkamisDates;
  }
}
