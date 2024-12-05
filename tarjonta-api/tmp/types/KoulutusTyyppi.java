package fi.vm.sade.tarjonta.service.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KoulutusTyyppi implements Serializable {

  private static final long serialVersionUID = 100L;
  protected String oid;
  protected TarjontaTila tila;
  protected KoodistoKoodiTyyppi koulutusKoodi;
  protected KoodistoKoodiTyyppi koulutusohjelmaKoodi;
  protected List<KoodistoKoodiTyyppi> opetusmuoto;
  protected List<KoodistoKoodiTyyppi> opetuskieli;
  protected Date koulutuksenAlkamisPaiva;
  protected KoulutuksenKestoTyyppi kesto;
  protected List<KoodistoKoodiTyyppi> koulutuslaji;
  protected KoodistoKoodiTyyppi pohjakoulutusvaatimus;
  protected List<WebLinkkiTyyppi> linkki;
  protected List<YhteyshenkiloTyyppi> yhteyshenkiloTyyppi;
  protected KoodistoKoodiTyyppi koulutusaste;
  protected String tarjoaja;
  protected MonikielinenTekstiTyyppi nimi;
  protected KoulutuksenKestoTyyppi laajuus;
  protected KoulutusasteTyyppi koulutustyyppi;
  protected List<KoodistoKoodiTyyppi> ammattinimikkeet;
  protected List<NimettyMonikielinenTekstiTyyppi> tekstit;
  protected KoodistoKoodiTyyppi lukiolinjaKoodi;
  protected List<KoodistoKoodiTyyppi> lukiodiplomit;
  protected List<KoodistoKoodiTyyppi> a1A2Kieli;
  protected List<KoodistoKoodiTyyppi> b1Kieli;
  protected List<KoodistoKoodiTyyppi> b2Kieli;
  protected List<KoodistoKoodiTyyppi> b3Kieli;
  protected List<KoodistoKoodiTyyppi> muutKielet;
  protected String viimeisinPaivittajaOid;
  protected Date viimeisinPaivitysPvm;
  protected Long version;
  protected List<KoodistoKoodiTyyppi> teemat;
  protected List<KoodistoKoodiTyyppi> pohjakoulutusvaatimusKorkeakoulu;
  protected Boolean maksullisuus;
  protected KoulutusmoduuliKoosteTyyppi koulutusmoduuli;
  protected String hinta;
  protected String ulkoinenTunniste;

  /** Default no-arg constructor */
  public KoulutusTyyppi() {
    super();
  }

  /** Fully-initialising value constructor */
  public KoulutusTyyppi(
      final String oid,
      final TarjontaTila tila,
      final KoodistoKoodiTyyppi koulutusKoodi,
      final KoodistoKoodiTyyppi koulutusohjelmaKoodi,
      final List<KoodistoKoodiTyyppi> opetusmuoto,
      final List<KoodistoKoodiTyyppi> opetuskieli,
      final Date koulutuksenAlkamisPaiva,
      final KoulutuksenKestoTyyppi kesto,
      final List<KoodistoKoodiTyyppi> koulutuslaji,
      final KoodistoKoodiTyyppi pohjakoulutusvaatimus,
      final List<WebLinkkiTyyppi> linkki,
      final List<YhteyshenkiloTyyppi> yhteyshenkiloTyyppi,
      final KoodistoKoodiTyyppi koulutusaste,
      final String tarjoaja,
      final MonikielinenTekstiTyyppi nimi,
      final KoulutuksenKestoTyyppi laajuus,
      final KoulutusasteTyyppi koulutustyyppi,
      final List<KoodistoKoodiTyyppi> ammattinimikkeet,
      final List<NimettyMonikielinenTekstiTyyppi> tekstit,
      final KoodistoKoodiTyyppi lukiolinjaKoodi,
      final List<KoodistoKoodiTyyppi> lukiodiplomit,
      final List<KoodistoKoodiTyyppi> a1A2Kieli,
      final List<KoodistoKoodiTyyppi> b1Kieli,
      final List<KoodistoKoodiTyyppi> b2Kieli,
      final List<KoodistoKoodiTyyppi> b3Kieli,
      final List<KoodistoKoodiTyyppi> muutKielet,
      final String viimeisinPaivittajaOid,
      final Date viimeisinPaivitysPvm,
      final Long version,
      final List<KoodistoKoodiTyyppi> teemat,
      final List<KoodistoKoodiTyyppi> pohjakoulutusvaatimusKorkeakoulu,
      final Boolean maksullisuus,
      final KoulutusmoduuliKoosteTyyppi koulutusmoduuli,
      final String hinta,
      final String ulkoinenTunniste) {
    this.oid = oid;
    this.tila = tila;
    this.koulutusKoodi = koulutusKoodi;
    this.koulutusohjelmaKoodi = koulutusohjelmaKoodi;
    this.opetusmuoto = opetusmuoto;
    this.opetuskieli = opetuskieli;
    this.koulutuksenAlkamisPaiva = koulutuksenAlkamisPaiva;
    this.kesto = kesto;
    this.koulutuslaji = koulutuslaji;
    this.pohjakoulutusvaatimus = pohjakoulutusvaatimus;
    this.linkki = linkki;
    this.yhteyshenkiloTyyppi = yhteyshenkiloTyyppi;
    this.koulutusaste = koulutusaste;
    this.tarjoaja = tarjoaja;
    this.nimi = nimi;
    this.laajuus = laajuus;
    this.koulutustyyppi = koulutustyyppi;
    this.ammattinimikkeet = ammattinimikkeet;
    this.tekstit = tekstit;
    this.lukiolinjaKoodi = lukiolinjaKoodi;
    this.lukiodiplomit = lukiodiplomit;
    this.a1A2Kieli = a1A2Kieli;
    this.b1Kieli = b1Kieli;
    this.b2Kieli = b2Kieli;
    this.b3Kieli = b3Kieli;
    this.muutKielet = muutKielet;
    this.viimeisinPaivittajaOid = viimeisinPaivittajaOid;
    this.viimeisinPaivitysPvm = viimeisinPaivitysPvm;
    this.version = version;
    this.teemat = teemat;
    this.pohjakoulutusvaatimusKorkeakoulu = pohjakoulutusvaatimusKorkeakoulu;
    this.maksullisuus = maksullisuus;
    this.koulutusmoduuli = koulutusmoduuli;
    this.hinta = hinta;
    this.ulkoinenTunniste = ulkoinenTunniste;
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
   * Gets the value of the tila property.
   *
   * @return possible object is {@link TarjontaTila }
   */
  public TarjontaTila getTila() {
    return tila;
  }

  /**
   * Sets the value of the tila property.
   *
   * @param value allowed object is {@link TarjontaTila }
   */
  public void setTila(TarjontaTila value) {
    this.tila = value;
  }

  /**
   * Gets the value of the koulutusKoodi property.
   *
   * @return possible object is {@link KoodistoKoodiTyyppi }
   */
  public KoodistoKoodiTyyppi getKoulutusKoodi() {
    return koulutusKoodi;
  }

  /**
   * Sets the value of the koulutusKoodi property.
   *
   * @param value allowed object is {@link KoodistoKoodiTyyppi }
   */
  public void setKoulutusKoodi(KoodistoKoodiTyyppi value) {
    this.koulutusKoodi = value;
  }

  /**
   * Gets the value of the koulutusohjelmaKoodi property.
   *
   * @return possible object is {@link KoodistoKoodiTyyppi }
   */
  public KoodistoKoodiTyyppi getKoulutusohjelmaKoodi() {
    return koulutusohjelmaKoodi;
  }

  /**
   * Sets the value of the koulutusohjelmaKoodi property.
   *
   * @param value allowed object is {@link KoodistoKoodiTyyppi }
   */
  public void setKoulutusohjelmaKoodi(KoodistoKoodiTyyppi value) {
    this.koulutusohjelmaKoodi = value;
  }

  /**
   * Gets the value of the opetusmuoto property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the opetusmuoto property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getOpetusmuoto().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link KoodistoKoodiTyyppi }
   */
  public List<KoodistoKoodiTyyppi> getOpetusmuoto() {
    if (opetusmuoto == null) {
      opetusmuoto = new ArrayList<KoodistoKoodiTyyppi>();
    }
    return this.opetusmuoto;
  }

  /**
   * Gets the value of the opetuskieli property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the opetuskieli property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getOpetuskieli().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link KoodistoKoodiTyyppi }
   */
  public List<KoodistoKoodiTyyppi> getOpetuskieli() {
    if (opetuskieli == null) {
      opetuskieli = new ArrayList<KoodistoKoodiTyyppi>();
    }
    return this.opetuskieli;
  }

  /**
   * Gets the value of the koulutuksenAlkamisPaiva property.
   *
   * @return possible object is {@link String }
   */
  public Date getKoulutuksenAlkamisPaiva() {
    return koulutuksenAlkamisPaiva;
  }

  /**
   * Sets the value of the koulutuksenAlkamisPaiva property.
   *
   * @param value allowed object is {@link String }
   */
  public void setKoulutuksenAlkamisPaiva(Date value) {
    this.koulutuksenAlkamisPaiva = value;
  }

  /**
   * Gets the value of the kesto property.
   *
   * @return possible object is {@link KoulutuksenKestoTyyppi }
   */
  public KoulutuksenKestoTyyppi getKesto() {
    return kesto;
  }

  /**
   * Sets the value of the kesto property.
   *
   * @param value allowed object is {@link KoulutuksenKestoTyyppi }
   */
  public void setKesto(KoulutuksenKestoTyyppi value) {
    this.kesto = value;
  }

  /**
   * Gets the value of the koulutuslaji property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the koulutuslaji property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getKoulutuslaji().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link KoodistoKoodiTyyppi }
   */
  public List<KoodistoKoodiTyyppi> getKoulutuslaji() {
    if (koulutuslaji == null) {
      koulutuslaji = new ArrayList<KoodistoKoodiTyyppi>();
    }
    return this.koulutuslaji;
  }

  /**
   * Gets the value of the pohjakoulutusvaatimus property.
   *
   * @return possible object is {@link KoodistoKoodiTyyppi }
   */
  public KoodistoKoodiTyyppi getPohjakoulutusvaatimus() {
    return pohjakoulutusvaatimus;
  }

  /**
   * Sets the value of the pohjakoulutusvaatimus property.
   *
   * @param value allowed object is {@link KoodistoKoodiTyyppi }
   */
  public void setPohjakoulutusvaatimus(KoodistoKoodiTyyppi value) {
    this.pohjakoulutusvaatimus = value;
  }

  /**
   * Gets the value of the linkki property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the linkki property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getLinkki().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link WebLinkkiTyyppi }
   */
  public List<WebLinkkiTyyppi> getLinkki() {
    if (linkki == null) {
      linkki = new ArrayList<WebLinkkiTyyppi>();
    }
    return this.linkki;
  }

  /**
   * Gets the value of the yhteyshenkiloTyyppi property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the yhteyshenkiloTyyppi property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getYhteyshenkiloTyyppi().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link YhteyshenkiloTyyppi }
   */
  public List<YhteyshenkiloTyyppi> getYhteyshenkiloTyyppi() {
    if (yhteyshenkiloTyyppi == null) {
      yhteyshenkiloTyyppi = new ArrayList<YhteyshenkiloTyyppi>();
    }
    return this.yhteyshenkiloTyyppi;
  }

  /**
   * Gets the value of the koulutusaste property.
   *
   * @return possible object is {@link KoodistoKoodiTyyppi }
   */
  public KoodistoKoodiTyyppi getKoulutusaste() {
    return koulutusaste;
  }

  /**
   * Sets the value of the koulutusaste property.
   *
   * @param value allowed object is {@link KoodistoKoodiTyyppi }
   */
  public void setKoulutusaste(KoodistoKoodiTyyppi value) {
    this.koulutusaste = value;
  }

  /**
   * Gets the value of the tarjoaja property.
   *
   * @return possible object is {@link String }
   */
  public String getTarjoaja() {
    return tarjoaja;
  }

  /**
   * Sets the value of the tarjoaja property.
   *
   * @param value allowed object is {@link String }
   */
  public void setTarjoaja(String value) {
    this.tarjoaja = value;
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

  /**
   * Gets the value of the laajuus property.
   *
   * @return possible object is {@link KoulutuksenKestoTyyppi }
   */
  public KoulutuksenKestoTyyppi getLaajuus() {
    return laajuus;
  }

  /**
   * Sets the value of the laajuus property.
   *
   * @param value allowed object is {@link KoulutuksenKestoTyyppi }
   */
  public void setLaajuus(KoulutuksenKestoTyyppi value) {
    this.laajuus = value;
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
   * Gets the value of the ammattinimikkeet property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the ammattinimikkeet property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getAmmattinimikkeet().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link KoodistoKoodiTyyppi }
   */
  public List<KoodistoKoodiTyyppi> getAmmattinimikkeet() {
    if (ammattinimikkeet == null) {
      ammattinimikkeet = new ArrayList<KoodistoKoodiTyyppi>();
    }
    return this.ammattinimikkeet;
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
   * Gets the value of the lukiolinjaKoodi property.
   *
   * @return possible object is {@link KoodistoKoodiTyyppi }
   */
  public KoodistoKoodiTyyppi getLukiolinjaKoodi() {
    return lukiolinjaKoodi;
  }

  /**
   * Sets the value of the lukiolinjaKoodi property.
   *
   * @param value allowed object is {@link KoodistoKoodiTyyppi }
   */
  public void setLukiolinjaKoodi(KoodistoKoodiTyyppi value) {
    this.lukiolinjaKoodi = value;
  }

  /**
   * Gets the value of the lukiodiplomit property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the lukiodiplomit property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getLukiodiplomit().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link KoodistoKoodiTyyppi }
   */
  public List<KoodistoKoodiTyyppi> getLukiodiplomit() {
    if (lukiodiplomit == null) {
      lukiodiplomit = new ArrayList<KoodistoKoodiTyyppi>();
    }
    return this.lukiodiplomit;
  }

  /**
   * Gets the value of the a1A2Kieli property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the a1A2Kieli property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getA1A2Kieli().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link KoodistoKoodiTyyppi }
   */
  public List<KoodistoKoodiTyyppi> getA1A2Kieli() {
    if (a1A2Kieli == null) {
      a1A2Kieli = new ArrayList<KoodistoKoodiTyyppi>();
    }
    return this.a1A2Kieli;
  }

  /**
   * Gets the value of the b1Kieli property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the b1Kieli property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getB1Kieli().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link KoodistoKoodiTyyppi }
   */
  public List<KoodistoKoodiTyyppi> getB1Kieli() {
    if (b1Kieli == null) {
      b1Kieli = new ArrayList<KoodistoKoodiTyyppi>();
    }
    return this.b1Kieli;
  }

  /**
   * Gets the value of the b2Kieli property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the b2Kieli property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getB2Kieli().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link KoodistoKoodiTyyppi }
   */
  public List<KoodistoKoodiTyyppi> getB2Kieli() {
    if (b2Kieli == null) {
      b2Kieli = new ArrayList<KoodistoKoodiTyyppi>();
    }
    return this.b2Kieli;
  }

  /**
   * Gets the value of the b3Kieli property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the b3Kieli property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getB3Kieli().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link KoodistoKoodiTyyppi }
   */
  public List<KoodistoKoodiTyyppi> getB3Kieli() {
    if (b3Kieli == null) {
      b3Kieli = new ArrayList<KoodistoKoodiTyyppi>();
    }
    return this.b3Kieli;
  }

  /**
   * Gets the value of the muutKielet property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the muutKielet property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getMuutKielet().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link KoodistoKoodiTyyppi }
   */
  public List<KoodistoKoodiTyyppi> getMuutKielet() {
    if (muutKielet == null) {
      muutKielet = new ArrayList<KoodistoKoodiTyyppi>();
    }
    return this.muutKielet;
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
   * Gets the value of the version property.
   *
   * @return possible object is {@link Long }
   */
  public Long getVersion() {
    return version;
  }

  /**
   * Sets the value of the version property.
   *
   * @param value allowed object is {@link Long }
   */
  public void setVersion(Long value) {
    this.version = value;
  }

  /**
   * Gets the value of the teemat property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the teemat property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getTeemat().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link KoodistoKoodiTyyppi }
   */
  public List<KoodistoKoodiTyyppi> getTeemat() {
    if (teemat == null) {
      teemat = new ArrayList<KoodistoKoodiTyyppi>();
    }
    return this.teemat;
  }

  /**
   * Gets the value of the pohjakoulutusvaatimusKorkeakoulu property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the pohjakoulutusvaatimusKorkeakoulu property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getPohjakoulutusvaatimusKorkeakoulu().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link KoodistoKoodiTyyppi }
   */
  public List<KoodistoKoodiTyyppi> getPohjakoulutusvaatimusKorkeakoulu() {
    if (pohjakoulutusvaatimusKorkeakoulu == null) {
      pohjakoulutusvaatimusKorkeakoulu = new ArrayList<KoodistoKoodiTyyppi>();
    }
    return this.pohjakoulutusvaatimusKorkeakoulu;
  }

  /**
   * Gets the value of the maksullisuus property.
   *
   * @return possible object is {@link Boolean }
   */
  public Boolean isMaksullisuus() {
    return maksullisuus;
  }

  /**
   * Sets the value of the maksullisuus property.
   *
   * @param value allowed object is {@link Boolean }
   */
  public void setMaksullisuus(Boolean value) {
    this.maksullisuus = value;
  }

  /**
   * Gets the value of the koulutusmoduuli property.
   *
   * @return possible object is {@link KoulutusmoduuliKoosteTyyppi }
   */
  public KoulutusmoduuliKoosteTyyppi getKoulutusmoduuli() {
    return koulutusmoduuli;
  }

  /**
   * Sets the value of the koulutusmoduuli property.
   *
   * @param value allowed object is {@link KoulutusmoduuliKoosteTyyppi }
   */
  public void setKoulutusmoduuli(KoulutusmoduuliKoosteTyyppi value) {
    this.koulutusmoduuli = value;
  }

  /**
   * Gets the value of the hinta property.
   *
   * @return possible object is {@link String }
   */
  public String getHinta() {
    return hinta;
  }

  /**
   * Sets the value of the hinta property.
   *
   * @param value allowed object is {@link String }
   */
  public void setHinta(String value) {
    this.hinta = value;
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
}
