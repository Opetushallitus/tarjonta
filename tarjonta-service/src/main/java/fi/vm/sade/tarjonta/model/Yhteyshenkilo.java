/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.vm.sade.tarjonta.service.types.HenkiloTyyppi;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.Collection;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/** Yhteyshenkilo's are always maintained in Henkilo service. */
@Entity
@JsonIgnoreProperties({"id", "version"})
@Table(name = Yhteyshenkilo.TABLE_NAME)
public class Yhteyshenkilo extends TarjontaBaseEntity {

  public static final String TABLE_NAME = "yhteyshenkilo";
  private static final long serialVersionUID = -1434499440678133630L;
  private static final String KIELI_SEPARATOR = ",";

  @Column(name = "nimi", nullable = false)
  private String nimi;

  @Column(name = "sahkoposti")
  private String sahkoposti;

  @Column(name = "puhelin")
  private String puhelin;

  @Column(name = "kielis")
  private String kielis;

  @Column(name = "henkilo_oid")
  private String henkioOid;

  @Column(name = "titteli")
  private String titteli;

  @Enumerated(EnumType.STRING)
  @Column(name = "tyyppi")
  private HenkiloTyyppi henkiloTyyppi;

  public Yhteyshenkilo() {}

  public Yhteyshenkilo(String henkioOid, String... kieli) {
    this.henkioOid = henkioOid;
    setMultipleKielis(kieli);
  }

  public final void setMultipleKielisByList(Collection<String> kielis) {

    if (kielis == null) {
      this.setKielis(null);
      return;
    }

    String[] kieliArray = new String[kielis.size()];
    setMultipleKielis(kielis.toArray(kieliArray));
  }

  public final void setMultipleKielis(String... kieli) {

    if (kieli == null || kieli.length == 0) {
      setKielis(null);
    }

    setKielis(StringUtils.join(formatKielis(kieli), KIELI_SEPARATOR));
  }

  public String[] getMultipleKielis() {
    return StringUtils.split(getKielis(), KIELI_SEPARATOR);
  }

  public String getHenkioOid() {
    return henkioOid;
  }

  public void setHenkioOid(String henkioOid) {
    this.henkioOid = henkioOid;
  }

  public String getSahkoposti() {
    return sahkoposti;
  }

  public void setSahkoposti(String sahkoposti) {
    this.sahkoposti = sahkoposti;
  }

  public String getNimi() {
    return nimi;
  }

  public void setNimi(String nimi) {
    this.nimi = nimi;
  }

  public String getPuhelin() {
    return puhelin;
  }

  public void setPuhelin(String puhelin) {
    this.puhelin = puhelin;
  }

  public String getTitteli() {
    return titteli;
  }

  public void setTitteli(String titteli) {
    this.titteli = titteli;
  }

  private static String[] formatKielis(String[] kielis) {

    String[] formatted = new String[kielis.length];

    for (int i = 0; i < kielis.length; i++) {
      final String kieli = kielis[i].trim();
      formatted[i] = kieli;
    }

    return formatted;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Yhteyshenkilo other = (Yhteyshenkilo) obj;
    return new EqualsBuilder()
        .append(henkioOid, other.henkioOid)
        .append(henkiloTyyppi, other.henkiloTyyppi)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(henkioOid).append(henkiloTyyppi).toHashCode();
  }

  /**
   * @return the henkiloTyyppi
   */
  public HenkiloTyyppi getHenkiloTyyppi() {
    return henkiloTyyppi;
  }

  /**
   * @param henkiloTyyppi the henkiloTyyppi to set
   */
  public void setHenkiloTyyppi(HenkiloTyyppi henkiloTyyppi) {
    this.henkiloTyyppi = henkiloTyyppi;
  }

  /**
   * @return the kielis
   */
  public String getKielis() {
    return kielis;
  }

  /**
   * @param kielis the kielis to set
   */
  public void setKielis(String kielis) {
    this.kielis = kielis;
  }
}
