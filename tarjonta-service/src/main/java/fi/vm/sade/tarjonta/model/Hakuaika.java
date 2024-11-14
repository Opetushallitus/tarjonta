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

import jakarta.persistence.*;
import java.util.Date;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/** */
@Entity
@JsonIgnoreProperties({"version", "haku", "hibernateLazyInitializer", "handler"})
@Table(name = Hakuaika.TABLE_NAME)
public class Hakuaika extends TarjontaBaseEntity {

  public static final String TABLE_NAME = "hakuaika";

  private static final long serialVersionUID = 1492826641481066295L;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "nimi_teksti_id")
  private MonikielinenTeksti nimi;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "alkamispvm")
  private Date alkamisPvm;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "paattymispvm")
  private Date paattymisPvm;

  @ManyToOne private Haku haku;

  public Date getAlkamisPvm() {
    return alkamisPvm;
  }

  public void setAlkamisPvm(Date alkamisPvm) {
    this.alkamisPvm = alkamisPvm;
  }

  public Date getPaattymisPvm() {
    return paattymisPvm;
  }

  public void setPaattymisPvm(Date paattymisPvm) {
    this.paattymisPvm = paattymisPvm;
  }

  public Haku getHaku() {
    return haku;
  }

  public void setHaku(Haku haku) {
    this.haku = haku;
  }

  public MonikielinenTeksti getNimi() {
    return nimi;
  }

  public void setNimi(MonikielinenTeksti nimi) {
    this.nimi = MonikielinenTeksti.merge(this.nimi, nimi);
  }
}
