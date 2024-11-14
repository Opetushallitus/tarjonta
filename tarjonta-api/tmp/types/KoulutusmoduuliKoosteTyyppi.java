package fi.vm.sade.tarjonta.service.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KoulutusmoduuliKoosteTyyppi implements Serializable {

  private static final long serialVersionUID = 100L;
  protected String oid;
  protected String parentOid;
  protected MonikielinenTekstiTyyppi koulutusmoduulinNimi;
  protected KoulutusasteTyyppi koulutustyyppi;
  protected String lukiolinjakoodiUri;
  protected String koulutuskoodiUri;
  protected String koulutusohjelmakoodiUri;
  protected KoulutusmoduuliTyyppi koulutusmoduuliTyyppi;
  protected String koulutusasteUri;
  protected String koulutusalaUri;
  protected String laajuusyksikkoUri;
  protected String laajuusarvoUri;
  protected String tutkintonimikeUri;
  protected String opintoalaUri;
  protected String eqfLuokitus;
  protected String nqfLuokitus;
  protected List<String> oppilaitostyyppi;
  protected List<NimettyMonikielinenTekstiTyyppi> tekstit;
  protected MonikielinenTekstiTyyppi tutkinnonTavoitteet;
  protected String ulkoinenTunniste;
  protected String viimeisinPaivittajaOid;
  protected Date viimeisinPaivitysPvm;
  protected MonikielinenTekstiTyyppi nimi;

  /** Default no-arg constructor */
  public KoulutusmoduuliKoosteTyyppi() {
    super();
  }

  /** Fully-initialising value constructor */
  public KoulutusmoduuliKoosteTyyppi(
      final String oid,
      final String parentOid,
      final MonikielinenTekstiTyyppi koulutusmoduulinNimi,
      final KoulutusasteTyyppi koulutustyyppi,
      final String lukiolinjakoodiUri,
      final String koulutuskoodiUri,
      final String koulutusohjelmakoodiUri,
      final KoulutusmoduuliTyyppi koulutusmoduuliTyyppi,
      final String koulutusasteUri,
      final String koulutusalaUri,
      final String laajuusyksikkoUri,
      final String laajuusarvoUri,
      final String tutkintonimikeUri,
      final String opintoalaUri,
      final String eqfLuokitus,
      final String nqfLuokitus,
      final List<String> oppilaitostyyppi,
      final List<NimettyMonikielinenTekstiTyyppi> tekstit,
      final MonikielinenTekstiTyyppi tutkinnonTavoitteet,
      final String ulkoinenTunniste,
      final String viimeisinPaivittajaOid,
      final Date viimeisinPaivitysPvm,
      final MonikielinenTekstiTyyppi nimi) {
    this.oid = oid;
    this.parentOid = parentOid;
    this.koulutusmoduulinNimi = koulutusmoduulinNimi;
    this.koulutustyyppi = koulutustyyppi;
    this.lukiolinjakoodiUri = lukiolinjakoodiUri;
    this.koulutuskoodiUri = koulutuskoodiUri;
    this.koulutusohjelmakoodiUri = koulutusohjelmakoodiUri;
    this.koulutusmoduuliTyyppi = koulutusmoduuliTyyppi;
    this.koulutusasteUri = koulutusasteUri;
    this.koulutusalaUri = koulutusalaUri;
    this.laajuusyksikkoUri = laajuusyksikkoUri;
    this.laajuusarvoUri = laajuusarvoUri;
    this.tutkintonimikeUri = tutkintonimikeUri;
    this.opintoalaUri = opintoalaUri;
    this.eqfLuokitus = eqfLuokitus;
    this.nqfLuokitus = nqfLuokitus;
    this.oppilaitostyyppi = oppilaitostyyppi;
    this.tekstit = tekstit;
    this.tutkinnonTavoitteet = tutkinnonTavoitteet;
    this.ulkoinenTunniste = ulkoinenTunniste;
    this.viimeisinPaivittajaOid = viimeisinPaivittajaOid;
    this.viimeisinPaivitysPvm = viimeisinPaivitysPvm;
    this.nimi = nimi;
  }

  /**
   * Gets the value of the oid property.
   *
   * @return possible object is {@link String }
   */
  public String getOid() {
    return oid;
  }

  /**
   * Sets the value of the oid property.
   *
   * @param value allowed object is {@link String }
   */
  public void setOid(String value) {
    this.oid = value;
  }

  /**
   * Gets the value of the parentOid property.
   *
   * @return possible object is {@link String }
   */
  public String getParentOid() {
    return parentOid;
  }

  /**
   * Sets the value of the parentOid property.
   *
   * @param value allowed object is {@link String }
   */
  public void setParentOid(String value) {
    this.parentOid = value;
  }

  /**
   * Gets the value of the koulutusmoduulinNimi property.
   *
   * @return possible object is {@link MonikielinenTekstiTyyppi }
   */
  public MonikielinenTekstiTyyppi getKoulutusmoduulinNimi() {
    return koulutusmoduulinNimi;
  }

  /**
   * Sets the value of the koulutusmoduulinNimi property.
   *
   * @param value allowed object is {@link MonikielinenTekstiTyyppi }
   */
  public void setKoulutusmoduulinNimi(MonikielinenTekstiTyyppi value) {
    this.koulutusmoduulinNimi = value;
  }

  /**
   * Gets the value of the koulutustyyppi property.
   *
   * @return possible object is {@link KoulutusasteTyyppi }
   */
  public KoulutusasteTyyppi getKoulutustyyppi() {
    return koulutustyyppi;
  }

  /**
   * Sets the value of the koulutustyyppi property.
   *
   * @param value allowed object is {@link KoulutusasteTyyppi }
   */
  public void setKoulutustyyppi(KoulutusasteTyyppi value) {
    this.koulutustyyppi = value;
  }

  /**
   * Gets the value of the lukiolinjakoodiUri property.
   *
   * @return possible object is {@link String }
   */
  public String getLukiolinjakoodiUri() {
    return lukiolinjakoodiUri;
  }

  /**
   * Sets the value of the lukiolinjakoodiUri property.
   *
   * @param value allowed object is {@link String }
   */
  public void setLukiolinjakoodiUri(String value) {
    this.lukiolinjakoodiUri = value;
  }

  /**
   * Gets the value of the koulutuskoodiUri property.
   *
   * @return possible object is {@link String }
   */
  public String getKoulutuskoodiUri() {
    return koulutuskoodiUri;
  }

  /**
   * Sets the value of the koulutuskoodiUri property.
   *
   * @param value allowed object is {@link String }
   */
  public void setKoulutuskoodiUri(String value) {
    this.koulutuskoodiUri = value;
  }

  /**
   * Gets the value of the koulutusohjelmakoodiUri property.
   *
   * @return possible object is {@link String }
   */
  public String getKoulutusohjelmakoodiUri() {
    return koulutusohjelmakoodiUri;
  }

  /**
   * Sets the value of the koulutusohjelmakoodiUri property.
   *
   * @param value allowed object is {@link String }
   */
  public void setKoulutusohjelmakoodiUri(String value) {
    this.koulutusohjelmakoodiUri = value;
  }

  /**
   * Gets the value of the koulutusmoduuliTyyppi property.
   *
   * @return possible object is {@link KoulutusmoduuliTyyppi }
   */
  public KoulutusmoduuliTyyppi getKoulutusmoduuliTyyppi() {
    return koulutusmoduuliTyyppi;
  }

  /**
   * Sets the value of the koulutusmoduuliTyyppi property.
   *
   * @param value allowed object is {@link KoulutusmoduuliTyyppi }
   */
  public void setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi value) {
    this.koulutusmoduuliTyyppi = value;
  }

  /**
   * Gets the value of the koulutusasteUri property.
   *
   * @return possible object is {@link String }
   */
  public String getKoulutusasteUri() {
    return koulutusasteUri;
  }

  /**
   * Sets the value of the koulutusasteUri property.
   *
   * @param value allowed object is {@link String }
   */
  public void setKoulutusasteUri(String value) {
    this.koulutusasteUri = value;
  }

  /**
   * Gets the value of the koulutusalaUri property.
   *
   * @return possible object is {@link String }
   */
  public String getKoulutusalaUri() {
    return koulutusalaUri;
  }

  /**
   * Sets the value of the koulutusalaUri property.
   *
   * @param value allowed object is {@link String }
   */
  public void setKoulutusalaUri(String value) {
    this.koulutusalaUri = value;
  }

  /**
   * Gets the value of the laajuusyksikkoUri property.
   *
   * @return possible object is {@link String }
   */
  public String getLaajuusyksikkoUri() {
    return laajuusyksikkoUri;
  }

  /**
   * Sets the value of the laajuusyksikkoUri property.
   *
   * @param value allowed object is {@link String }
   */
  public void setLaajuusyksikkoUri(String value) {
    this.laajuusyksikkoUri = value;
  }

  /**
   * Gets the value of the laajuusarvoUri property.
   *
   * @return possible object is {@link String }
   */
  public String getLaajuusarvoUri() {
    return laajuusarvoUri;
  }

  /**
   * Sets the value of the laajuusarvoUri property.
   *
   * @param value allowed object is {@link String }
   */
  public void setLaajuusarvoUri(String value) {
    this.laajuusarvoUri = value;
  }

  /**
   * Gets the value of the tutkintonimikeUri property.
   *
   * @return possible object is {@link String }
   */
  public String getTutkintonimikeUri() {
    return tutkintonimikeUri;
  }

  /**
   * Sets the value of the tutkintonimikeUri property.
   *
   * @param value allowed object is {@link String }
   */
  public void setTutkintonimikeUri(String value) {
    this.tutkintonimikeUri = value;
  }

  /**
   * Gets the value of the opintoalaUri property.
   *
   * @return possible object is {@link String }
   */
  public String getOpintoalaUri() {
    return opintoalaUri;
  }

  /**
   * Sets the value of the opintoalaUri property.
   *
   * @param value allowed object is {@link String }
   */
  public void setOpintoalaUri(String value) {
    this.opintoalaUri = value;
  }

  /**
   * Gets the value of the eqfLuokitus property.
   *
   * @return possible object is {@link String }
   */
  public String getEqfLuokitus() {
    return eqfLuokitus;
  }

  /**
   * Sets the value of the eqfLuokitus property.
   *
   * @param value allowed object is {@link String }
   */
  public void setEqfLuokitus(String value) {
    this.eqfLuokitus = value;
  }

  /**
   * Gets the value of the nqfLuokitus property.
   *
   * @return possible object is {@link String }
   */
  public String getNqfLuokitus() {
    return nqfLuokitus;
  }

  /**
   * Sets the value of the nqfLuokitus property.
   *
   * @param value allowed object is {@link String }
   */
  public void setNqfLuokitus(String value) {
    this.nqfLuokitus = value;
  }

  /**
   * Gets the value of the oppilaitostyyppi property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the oppilaitostyyppi property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getOppilaitostyyppi().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link String }
   */
  public List<String> getOppilaitostyyppi() {
    if (oppilaitostyyppi == null) {
      oppilaitostyyppi = new ArrayList<String>();
    }
    return this.oppilaitostyyppi;
  }

  /**
   * Gets the value of the tekstit property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the tekstit property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getTekstit().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link
   * NimettyMonikielinenTekstiTyyppi }
   */
  public List<NimettyMonikielinenTekstiTyyppi> getTekstit() {
    if (tekstit == null) {
      tekstit = new ArrayList<NimettyMonikielinenTekstiTyyppi>();
    }
    return this.tekstit;
  }

  /**
   * Gets the value of the tutkinnonTavoitteet property.
   *
   * @return possible object is {@link MonikielinenTekstiTyyppi }
   */
  public MonikielinenTekstiTyyppi getTutkinnonTavoitteet() {
    return tutkinnonTavoitteet;
  }

  /**
   * Sets the value of the tutkinnonTavoitteet property.
   *
   * @param value allowed object is {@link MonikielinenTekstiTyyppi }
   */
  public void setTutkinnonTavoitteet(MonikielinenTekstiTyyppi value) {
    this.tutkinnonTavoitteet = value;
  }

  /**
   * Gets the value of the ulkoinenTunniste property.
   *
   * @return possible object is {@link String }
   */
  public String getUlkoinenTunniste() {
    return ulkoinenTunniste;
  }

  /**
   * Sets the value of the ulkoinenTunniste property.
   *
   * @param value allowed object is {@link String }
   */
  public void setUlkoinenTunniste(String value) {
    this.ulkoinenTunniste = value;
  }

  /**
   * Gets the value of the viimeisinPaivittajaOid property.
   *
   * @return possible object is {@link String }
   */
  public String getViimeisinPaivittajaOid() {
    return viimeisinPaivittajaOid;
  }

  /**
   * Sets the value of the viimeisinPaivittajaOid property.
   *
   * @param value allowed object is {@link String }
   */
  public void setViimeisinPaivittajaOid(String value) {
    this.viimeisinPaivittajaOid = value;
  }

  /**
   * Gets the value of the viimeisinPaivitysPvm property.
   *
   * @return possible object is {@link String }
   */
  public Date getViimeisinPaivitysPvm() {
    return viimeisinPaivitysPvm;
  }

  /**
   * Sets the value of the viimeisinPaivitysPvm property.
   *
   * @param value allowed object is {@link String }
   */
  public void setViimeisinPaivitysPvm(Date value) {
    this.viimeisinPaivitysPvm = value;
  }

  /**
   * Gets the value of the nimi property.
   *
   * @return possible object is {@link MonikielinenTekstiTyyppi }
   */
  public MonikielinenTekstiTyyppi getNimi() {
    return nimi;
  }

  /**
   * Sets the value of the nimi property.
   *
   * @param value allowed object is {@link MonikielinenTekstiTyyppi }
   */
  public void setNimi(MonikielinenTekstiTyyppi value) {
    this.nimi = value;
  }
}
