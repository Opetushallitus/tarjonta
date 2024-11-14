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
package fi.vm.sade.tarjonta.publication;

import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.enums.MetaCategory;
import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.ws.WebServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gathers learning opportunity material (tarjonta) that is ready for publication. Invokes handler
 * to do something on the collected data, e.g. write to stream. Note: this class is not thread safe.
 *
 * @author Jukka Raanamo
 */
public class PublicationCollector {

  private EventHandler handler;
  private PublicationDataService dataService;
  private OrganisaatioService organisaatioService;
  private ExportParams params;

  /** Map used to avoid triggering events on re-occurring items. */
  private Map<String, String> notifiedMap = new HashMap<String, String>();

  private static final Logger log = LoggerFactory.getLogger(PublicationCollector.class);

  public void setHandler(EventHandler handler) {
    this.handler = handler;
  }

  public void setDataService(PublicationDataService dataService) {
    this.dataService = dataService;
  }

  /**
   * Starts the data collecting process. Invokes handler with data ready to be published.
   *
   * @throws fi.vm.sade.tarjonta.publication.PublicationCollector.ConfigurationException if
   *     collector has is not properly configured.
   * @throws Exception if data processing fails. Handler's onCollectFailed will be als called with
   *     the same exception.
   */
  public void start() throws ConfigurationException, Exception {

    reset();
    validateConfig();

    try {

      fireCollectStarted();
      processData();
      fireCollectCompleted();

    } catch (Exception e) {

      log.error("error while processing data", e);

      fireCollectFailed(e);
      throw e;
    }
  }

  protected void fireCollectStarted() throws Exception {

    handler.onCollectStart();
  }

  protected void fireCollectCompleted() throws Exception {

    handler.onCollectEnd();
  }

  protected void fireCollectFailed(Exception e) {

    handler.onCollectFailed(e);
  }

  protected void fireCollect(OrganisaatioRDTO dto) throws Exception {
    if (dto == null) {
      return;
    }

    if (!isNotifiedBefore(dto.getOid())) {
      handler.onCollect(dto);
    }
  }

  protected void fireCollect(KoulutusmoduuliToteutus t) throws Exception {

    if (!isNotifiedBefore(t.getOid())) {
      handler.onCollect(t);
    }
  }

  protected void fireCollect(Koulutusmoduuli m) throws Exception {

    if (!isNotifiedBefore(m.getOid())) {
      handler.onCollect(m);
    }
  }

  protected void fireCollect(
      Hakukohde h, List<MonikielinenMetadata> sora, List<MonikielinenMetadata> valintaperuste)
      throws Exception {

    if (!isNotifiedBefore(h.getOid())) {
      handler.onCollect(h, sora, valintaperuste);
    }
  }

  protected void fireCollect(Haku h) throws Exception {

    if (!isNotifiedBefore(h.getOid())) {
      handler.onCollect(h);
    }
  }

  private void processData() throws CollectorException, Exception {

    List<KoulutusmoduuliToteutus> koulutusList = dataService.listKoulutusmoduuliToteutus();

    if (koulutusList.isEmpty()) {
      handler.onCollectWarning("zero koulutusmoduuliToteutus found");
    }

    List<KoulutusmoduuliToteutus> parentKoulutusList = new ArrayList<KoulutusmoduuliToteutus>();

    for (KoulutusmoduuliToteutus t : koulutusList) {
      Koulutusmoduuli m = t.getKoulutusmoduuli();

      if (log.isDebugEnabled()) {
        log.debug("LOS OID : {}, LOI OID : {}", m.getOid(), t.getOid());
        log.debug(
            "LOS provider : {}, LOI provider : {}",
            m.getOmistajaOrganisaatioOid(),
            t.getTarjoaja());
      }

      if (m.getModuuliTyyppi().name().equals(KoulutusmoduuliTyyppi.TUTKINTO.name())) {
        parentKoulutusList.add(t);
      } else {
        fireCollect(m);

        fireCollect(t);

        fireCollect(findProviderByOid(m.getOmistajaOrganisaatioOid(), true));
        fireCollect(findProviderByOid(t.getTarjoaja(), false));
      }
    }

    for (KoulutusmoduuliToteutus t : parentKoulutusList) {
      Koulutusmoduuli m = t.getKoulutusmoduuli();

      if (log.isDebugEnabled()) {
        log.debug("LOS OID : {}, LOI OID : {}", m.getOid(), t.getOid());
        log.debug(
            "LOS provider : {}, LOI provider : {}",
            m.getOmistajaOrganisaatioOid(),
            t.getTarjoaja());
      }

      fireCollect(m, t);

      // fireCollect(t);
      fireCollect(findProviderByOid(m.getOmistajaOrganisaatioOid(), true));
      fireCollect(findProviderByOid(t.getTarjoaja(), false));
    }

    List<Hakukohde> hakukohdeList = dataService.listHakukohde();

    for (Hakukohde hakukohde : hakukohdeList) {
      List<MonikielinenMetadata> sora =
          dataService.searchMetaData(hakukohde.getSoraKuvausKoodiUri(), MetaCategory.SORA_KUVAUS);
      List<MonikielinenMetadata> valintaperuste =
          dataService.searchMetaData(
              hakukohde.getValintaperustekuvausKoodiUri(), MetaCategory.VALINTAPERUSTEKUVAUS);
      fireCollect(hakukohde, sora, valintaperuste);
    }

    List<Haku> hakuList = dataService.listHaku();
    for (Haku h : hakuList) {
      fireCollect(h);
    }
  }

  private void fireCollect(Koulutusmoduuli m, KoulutusmoduuliToteutus t) throws Exception {

    if (!isNotifiedBefore(m.getOid() + t.getOid())) {
      handler.onCollect(m, t);
    }
  }

  private OrganisaatioRDTO findProviderByOid(final String oid, final boolean allowNull) {
    if (allowNull) {
      return null;
    } else if (oid == null) {
      throw new IllegalArgumentException("Provider OID cannot be null");
    }

    OrganisaatioRDTO dto = null;
    try {
      dto = organisaatioService.findByOid(oid);

      if (dto == null) {
        throw new RuntimeException("Provider not found by OID  '" + oid + "'");
      }
    } catch (WebServiceException e) {
      log.error("Organisation service throws an exception - message : {}", e.getMessage());
    } catch (Exception e) {
      log.error("Caught an exception - cannot find provider.", e);
    }

    return dto;
  }

  /**
   * Resets internal processing state so that multiple calls to start should produce identical
   * results.
   */
  private void reset() {

    notifiedMap.clear();
  }

  private void validateConfig() {

    if (handler == null) {
      throw new ConfigurationException("handler must be non null");
    }
    if (dataService == null) {
      throw new ConfigurationException("dataService must be non null");
    }
  }

  /**
   * Returns true if isNotifiedBefore has been called with the same key before.
   *
   * @param key
   * @return
   */
  private boolean isNotifiedBefore(String key) {

    if (!notifiedMap.containsKey(key)) {
      notifiedMap.put(key, key);
      return false;
    }

    return true;
  }

  /**
   * @return the organisaatioService
   */
  public OrganisaatioService getOrganisaatioService() {
    return organisaatioService;
  }

  /**
   * @param organisaatioService the organisaatioService to set
   */
  public void setOrganisaatioService(OrganisaatioService organisaatioService) {
    this.organisaatioService = organisaatioService;
  }

  /**
   * @return the params
   */
  public ExportParams getParams() {
    return params;
  }

  /**
   * @param params the params to set
   */
  public void setParams(ExportParams params) {
    this.params = params;
  }

  /** Implement this interface to process tarjonta data. */
  public interface EventHandler {

    public void onCollectStart() throws Exception;

    public void onCollect(Koulutusmoduuli m, KoulutusmoduuliToteutus t) throws Exception;

    public void onCollectEnd() throws Exception;

    public void onCollectFailed(Exception e);

    public void onCollectWarning(String msg);

    public void onCollect(KoulutusmoduuliToteutus toteutus) throws Exception;

    public void onCollect(Koulutusmoduuli moduuli) throws Exception;

    public void onCollect(
        Hakukohde hakukohde,
        List<MonikielinenMetadata> sora,
        List<MonikielinenMetadata> valintaperuste)
        throws Exception;

    public void onCollect(Haku haku) throws Exception;

    public void onCollect(OrganisaatioRDTO tarjoaja) throws Exception;
  }

  /** Convenience class that implements all EventHandler's methods as no-op. */
  public static class EventHandlerSuppport implements EventHandler {

    @Override
    public void onCollect(Haku haku) throws Exception {}

    @Override
    public void onCollect(
        Hakukohde hakukohde,
        List<MonikielinenMetadata> sora,
        List<MonikielinenMetadata> valintaperuste)
        throws Exception {}

    @Override
    public void onCollect(Koulutusmoduuli moduuli) throws Exception {}

    @Override
    public void onCollect(KoulutusmoduuliToteutus toteutus) throws Exception {}

    @Override
    public void onCollect(OrganisaatioRDTO tarjoaja) throws Exception {}

    @Override
    public void onCollectEnd() throws Exception {}

    @Override
    public void onCollectFailed(Exception e) {}

    @Override
    public void onCollectStart() throws Exception {}

    @Override
    public void onCollectWarning(String msg) {}

    @Override
    public void onCollect(Koulutusmoduuli m, KoulutusmoduuliToteutus t) throws Exception {}
  }

  /** Thrown when collector has not been properly configured. */
  public static class ConfigurationException extends IllegalStateException {

    private static final long serialVersionUID = -2625814993656414626L;

    public ConfigurationException(String string) {
      super(string);
    }
  }

  /** Thrown when error occurs during data collection. */
  public static class CollectorException extends RuntimeException {

    private static final long serialVersionUID = 3794244904677393564L;

    public CollectorException(String string) {
      super(string);
    }
  }
}
