package fi.vm.sade.tarjonta.service.types;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class PaivitaKoulutusTyyppi extends KoulutusTyyppi implements Serializable {

  private static final long serialVersionUID = 100L;
  protected String komoOid;

  /** Default no-arg constructor */
  public PaivitaKoulutusTyyppi() {
    super();
  }

  /** Fully-initialising value constructor */
  public PaivitaKoulutusTyyppi(
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
      final String ulkoinenTunniste,
      final String komoOid) {
    super(
        oid,
        tila,
        koulutusKoodi,
        koulutusohjelmaKoodi,
        opetusmuoto,
        opetuskieli,
        koulutuksenAlkamisPaiva,
        kesto,
        koulutuslaji,
        pohjakoulutusvaatimus,
        linkki,
        yhteyshenkiloTyyppi,
        koulutusaste,
        tarjoaja,
        nimi,
        laajuus,
        koulutustyyppi,
        ammattinimikkeet,
        tekstit,
        lukiolinjaKoodi,
        lukiodiplomit,
        a1A2Kieli,
        b1Kieli,
        b2Kieli,
        b3Kieli,
        muutKielet,
        viimeisinPaivittajaOid,
        viimeisinPaivitysPvm,
        version,
        teemat,
        pohjakoulutusvaatimusKorkeakoulu,
        maksullisuus,
        koulutusmoduuli,
        hinta,
        ulkoinenTunniste);
    this.komoOid = komoOid;
  }

  /**
   * Gets the value of the komoOid property.
   *
   * @return possible object is {@link String }
   */
  public String getKomoOid() {
    return komoOid;
  }

  /**
   * Sets the value of the komoOid property.
   *
   * @param value allowed object is {@link String }
   */
  public void setKomoOid(String value) {
    this.komoOid = value;
  }
}
