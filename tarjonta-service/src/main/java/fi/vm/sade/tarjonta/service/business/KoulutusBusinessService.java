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
package fi.vm.sade.tarjonta.service.business;

import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import java.util.List;

/** Business logic for manipulating classes inherited from LearningOpportunityObject. */
public interface KoulutusBusinessService {

  /**
   * List all KOMO objects.
   *
   * @return
   */
  public List<Koulutusmoduuli> findTutkintoOhjelmat();

  /**
   * Creates a new top level Koulutusmoduuli.
   *
   * @param moduuli
   * @return
   */
  public Koulutusmoduuli create(Koulutusmoduuli moduuli);

  /**
   * Creates new KoulutusmoduuliToteutus from passed data. Before storing, reference to given
   * Koulutusmoduuli is assigned. If given Koulutusmoduuli is also new, it is also created.
   *
   * @param toteutus
   * @param moduuli
   * @return
   */
  public KoulutusmoduuliToteutus create(KoulutusmoduuliToteutus toteutus, Koulutusmoduuli moduuli);

  /**
   * Palauttaa {@link KoulutusmoduuliTyyppi#TUTKINTO_OHJELMA} -tyyppisen Koulutusmoduulin jonka
   * koulutusLuokitus sekä koulutusOhjelma vastaavat annettuja arvoja.
   *
   * @param tutkintoUri
   * @param koulutusUri
   * @return
   */
  public Koulutusmoduuli findTutkintoOhjelma(String koulutusLuokitusUri, String koulutusOhjelmaUri);

  /**
   * Lisaa ja palauttaaa uuden koulutuksen (toteutus) annettujen arvojen perusteella.
   *
   * @param koulutus
   * @return
   */
  public KoulutusmoduuliToteutus createKoulutus(LisaaKoulutusTyyppi koulutus);

  /**
   * Päivittaa koulutuksen (toteutus) tiedot ja palauttaa päivitetyn toteutuksen.
   *
   * @param koulutus
   * @return
   */
  public KoulutusmoduuliToteutus updateKoulutus(PaivitaKoulutusTyyppi koulutus);
  /**
   * Once search criteria tyyppi is declared in WSDL expose this method. The implementation is in
   * place in DAO.
   *
   * @param oid
   * @param criteria
   * @return
   */
  // public List<? extends LearningOpportunityObject> search(KoulutusSearchTyyppi criteria);
}
