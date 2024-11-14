package fi.vm.sade.tarjonta.service.search;

import com.google.common.collect.ImmutableList;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.IndexerDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.service.business.IndexService;
import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import fi.vm.sade.tarjonta.shared.types.Tilamuutokset;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Transactional(readOnly = true)
@Component
@Path("/indexer")
public class IndexerResource {

  private static Logger logger = LoggerFactory.getLogger(IndexerResource.class);

  private SolrServer hakukohdeSolr;
  private SolrServer koulutusSolr;

  @Autowired private HakukohdeDAO hakukohdeDAO;

  @Autowired private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

  @Autowired private IndexerDAO indexerDao;

  @Autowired private OrganisaatioService organisaatioService;

  @Autowired private IndexService indexService;

  @GET
  @Path("/koulutukset/clear")
  @Produces("text/plain")
  public Response clearKoulutusIndex() {
    try {
      clearIndex(koulutusSolr);
      return Response.ok().build();
    } catch (Throwable t) {
      return Response.serverError().entity(t.getMessage()).build();
    }
  }

  /** Clear hakukohdeindex. */
  @GET
  @Path("/hakukohteet/clear")
  @Produces("text/plain")
  public Response clearHakukohdeIndex() {
    try {
      clearIndex(hakukohdeSolr);
      return Response.ok().build();
    } catch (Throwable t) {
      return Response.serverError().entity(t.getMessage()).build();
    }
  }

  private void clearIndex(SolrServer solr) throws SolrServerException, IOException {
    solr.deleteByQuery("*:*");
    solr.commit(true, true, false);
  }

  @GET
  @Path("/hakukohteet")
  @Produces("text/plain")
  public String buildHakukohdeIndex(@QueryParam("clear") final boolean clear)
      throws SolrServerException, IOException {
    if (clear) {
      clearIndex(hakukohdeSolr);
    }
    return indexerDao.setHakukohdeViimindeksointiPvmToNull().toString();
  }

  @Autowired
  public void setSolrServerFactory(SolrServerFactory factory) {
    this.hakukohdeSolr = factory.getSolrServer("hakukohteet");
    this.koulutusSolr = factory.getSolrServer("koulutukset");
  }

  public void deleteKoulutus(List<String> oids) throws IOException {
    deleteByOid(oids, koulutusSolr);
  }

  private void deleteByOid(List<String> oids, final SolrServer solr) {
    final List<String> localOids = ImmutableList.copyOf(oids);

    afterCommit(
        new TransactionSynchronizationAdapter() {
          @Override
          public void afterCommit() {
            try {
              solr.deleteById(localOids);
              solr.commit(true, true, false);
            } catch (Exception e) {
              throw new RuntimeException("indexing.error", e);
            }
          }
        });
  }

  private static void afterCommit(TransactionSynchronization sync) {
    if (TransactionSynchronizationManager.isSynchronizationActive()) {
      logger.debug("Transaction synchronization is ACTIVE. Executing later!");
      TransactionSynchronizationManager.registerSynchronization(sync);
    } else {
      logger.debug("Transaction synchronization is NOT ACTIVE. Executing right now!");
      sync.afterCommit();
    }
  }

  public void deleteHakukohde(ArrayList<String> oids) throws IOException {
    deleteByOid(oids, hakukohdeSolr);
  }

  /**
   * Index hakukohteet in batches
   *
   * @param hakukohdeIdt hakukohde ids to index
   */
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  public void indexHakukohteet(List<Long> hakukohdeIdt) {
    int batch_size = 50;
    LocalDateTime indexingStarted = this.logIndexingStart(hakukohdeIdt.size(), "hakukohde");
    int index = 0;
    do {
      index = this.indexService.indexHakukohdeBatch(hakukohdeIdt, batch_size, index);
    } while (index < hakukohdeIdt.size());
    this.logIndexingReady(hakukohdeIdt.size(), indexingStarted, "hakukohde");
  }

  /**
   * Index koulutukset in batches
   *
   * @param koulutukset id's of koulutukset to index
   */
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  public void indexKoulutukset(List<Long> koulutukset) {
    int batch_size = 50;
    LocalDateTime indexingStarted = this.logIndexingStart(koulutukset.size(), "koulutus");
    int index = 0;
    do {
      index = this.indexService.indexKoulutusBatch(koulutukset, batch_size, index);
    } while (index < koulutukset.size());
    this.logIndexingReady(koulutukset.size(), indexingStarted, "koulutus");
  }

  @GET
  @Path("/koulutukset")
  @Produces("text/plain")
  public String rebuildKoulutusIndex(@QueryParam("clear") final boolean clear)
      throws SolrServerException, IOException {
    if (clear) {
      clearIndex(koulutusSolr);
    }
    return indexerDao.setKoulutusViimindeksointiPvmToNull().toString();
  }

  public void indexMuutokset(Tilamuutokset tm) {
    if (tm.getMuutetutKomotot().size() > 0) {
      LocalDateTime indexingStarted = logIndexingStart(tm.getMuutetutKomotot().size(), "koulutus");
      this.indexService.indexKoulutukset(
          koulutusmoduuliToteutusDAO.findIdsByoids(tm.getMuutetutKomotot()));
      logIndexingReady(tm.getMuutetutKomotot().size(), indexingStarted, "koulutus");
    }
    if (tm.getMuutetutHakukohteet().size() > 0) {
      LocalDateTime indexingStarted =
          logIndexingStart(tm.getMuutetutHakukohteet().size(), "hakukohde");
      this.indexService.indexHakukohteet(hakukohdeDAO.findIdsByoids(tm.getMuutetutHakukohteet()));
      logIndexingReady(tm.getMuutetutHakukohteet().size(), indexingStarted, "hakukohde");
    }
  }

  private LocalDateTime logIndexingStart(int entitysToIndex, String type) {
    if (entitysToIndex > 0) {
      logger.info("Starting {} indexing with {} to index", type, entitysToIndex);
    }
    return LocalDateTime.now();
  }

  private void logIndexingReady(int entitysToIndex, LocalDateTime started, String type) {
    if (entitysToIndex > 0) {
      logger.info(
          "Finished {} indexing with time {} millis",
          type,
          ChronoUnit.MILLIS.between(started, LocalDateTime.now()));
    }
  }
}
