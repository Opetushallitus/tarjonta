package fi.vm.sade.tarjonta.service.search;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.IndexerDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.shared.types.Tilamuutokset;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Transactional(readOnly = true)
@Component
@Path("/indexer")
public class IndexerResource {

    private static Logger logger = LoggerFactory.getLogger(IndexerResource.class);

    private SolrServer hakukohdeSolr;
    private SolrServer koulutusSolr;

    @Autowired
    private HakukohdeDAO hakukohdeDAO;

    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Autowired
    private IndexerDAO indexerDao;

    @Autowired
    HakukohdeToSolrDocument hakukohdeConverter;

    @Autowired
    KoulutusToSolrDocument koulutusConverter;

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

    /**
     * Clear hakukohdeindex.
     */
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
    public String rebuildHakukohdeIndex(@QueryParam("clear") final boolean clear) throws SolrServerException, IOException {
        List<Long> hakukohteet = indexerDao.findAllHakukohdeIds();
        logger.info("Found {} hakukohdes to index.", hakukohteet.size());
        if (clear) {
            clearIndex(hakukohdeSolr);
        }
        indexHakukohteet(hakukohteet);
        return Integer.toString(hakukohteet.size());
    }

    @GET
    @Path("/hakukohteet")
    @Produces("text/plain")
    public String buildHakukohdeIndex(@QueryParam("clear") final boolean clear) throws SolrServerException, IOException {
        List<Long> hakukohdeIds = indexerDao.findAllHakukohdeIds();
        logger.info("Found {} hakukohdes to index.", hakukohdeIds.size());
        if (clear) {
            clearIndex(hakukohdeSolr);
        }
        indexHakukohdeIndexEntities(hakukohdeIds);
        return Integer.toString(hakukohdeIds.size());
    }

    @Autowired
    public void setSolrServerFactory(SolrServerFactory factory) {
        this.hakukohdeSolr = factory.getSolrServer("hakukohteet");
        this.koulutusSolr = factory.getSolrServer("koulutukset");
    }

    private void index(final SolrServer solr, List<SolrInputDocument> docs) {
        if (docs.size() > 0) {
            final List<SolrInputDocument> localDocs = ImmutableList
                    .copyOf(docs);
            afterCommit(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    Exception lastException = null;

                    // try 3 times
                    for (int i = 0; i < 5; i++) {
                        try {
                            logger.info("Indexing {} docs, try {}", localDocs.size(), i);
                            solr.add(localDocs);
                            logger.info("Committing changes to index.");
                            solr.commit(true, true, false);
                            logger.info("Done.");
                            return; //exit on success!
                        } catch (Exception e) {
                            lastException = e;
                        }
                    }
                    // fail
                    throw new RuntimeException(
                            "indexing.error, last exception:", lastException);

                }
            });
        }
    }

    public void deleteKoulutus(List<String> oids) throws IOException {
        deleteByOid(oids, koulutusSolr);
    }

    private void deleteByOid(List<String> oids, final SolrServer solr)
            throws IOException {
        final List<String> localOids = ImmutableList.copyOf(oids);

        afterCommit(((TransactionSynchronization) new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                try {
                    solr.deleteById(localOids);
                    solr.commit(true, true, false);
                } catch (Exception e) {
                    throw new RuntimeException("indexing.error", e);
                }
            }
        }));
    }

    private static void afterCommit(TransactionSynchronization sync) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            logger.info("Transaction synchronization is ACTIVE. Executing later!");
            TransactionSynchronizationManager.registerSynchronization(sync);
        } else {
            logger.info("Transaction synchronization is NOT ACTIVE. Executing right now!");
            sync.afterCommit();
        }
    }

    public void deleteHakukohde(ArrayList<String> oids) throws IOException {
        deleteByOid(oids, hakukohdeSolr);
    }

    public void indexHakukohteet(List<Long> hakukohdeIdt) {
        if (hakukohdeIdt.size() == 0) {
            return;
        }
        List<SolrInputDocument> docs = Lists.newArrayList();
        int batch_size = 50;
        int index = 0;
        do {
            for (int j = index; j < index + batch_size && j < hakukohdeIdt.size(); j++) {

                Long hakukohdeId = hakukohdeIdt.get(j);
                logger.info(j + ". Fetching hakukohde:" + hakukohdeId);
                docs.addAll(hakukohdeConverter.apply(hakukohdeId));
            }
            index += batch_size;
            logger.info("indexing:" + docs.size() + " docs");
            index(hakukohdeSolr, docs);
            docs.clear();
        } while (index < hakukohdeIdt.size());

        commit(hakukohdeSolr);
    }

    public void indexHakukohdeIndexEntities(List<Long> hakukohdeIds) throws SolrServerException,
            IOException {
        List<SolrInputDocument> docs = Lists.newArrayList();
        int batch_size = 100;
        for (Long hakukohdeId : hakukohdeIds) {
            docs.addAll(hakukohdeConverter.apply(hakukohdeId));
            if (docs.size() > batch_size) {
                logger.info("indexing:" + docs.size() + " docs");
                hakukohdeSolr.add(docs);
                docs.clear();
            }
        }
        if (docs.size() > 0) {
            hakukohdeSolr.add(docs);
            docs.clear();
        }

        commit(hakukohdeSolr);
    }

    /**
     * Index koulutukset
     *
     * @param koulutukset id's of koulutukset to index
     * @throws IOException
     * @throws SolrServerException
     */
    public void indexKoulutukset(List<Long> koulutukset) {
        if (koulutukset.size() == 0) {
            return;
        }
        int batch_size = 50;
        int index = 0;
        do {
            final List<SolrInputDocument> docs = Lists.newArrayList();

            for (int j = index; j < index + batch_size && j < koulutukset.size(); j++) {

                Long koulutusId = koulutukset.get(j);
                logger.info(j + ". Fetching koulutus:" + koulutusId);
                docs.addAll(koulutusConverter.apply(koulutusId));

                // Reindex sibling komotos
                KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.read(koulutusId);
                if (komoto != null) {
                    List<KoulutusmoduuliToteutus> siblings = koulutusmoduuliToteutusDAO.findSiblingKomotos(komoto);
                    if (siblings != null) {
                        for (KoulutusmoduuliToteutus sibling : siblings) {
                            if (!koulutukset.contains(sibling.getId())) {
                                docs.addAll(koulutusConverter.apply(sibling.getId()));
                            }
                        }
                    }
                }
            }
            index += batch_size;
            logger.info("indexing:" + docs.size() + " docs");
            index(koulutusSolr, docs);
            docs.clear();
        } while (index < koulutukset.size());

        commit(koulutusSolr);
    }

    private void commit(SolrServer solr) {
        try {
            solr.commit(true, true, false);
        } catch (SolrServerException e) {
            throw new RuntimeException("indexing.error", e);
        } catch (IOException e) {
            throw new RuntimeException("indexing.error", e);
        }
    }

    @GET
    @Path("/koulutukset")
    @Produces("text/plain")
    public String rebuildKoulutusIndex(@QueryParam("clear") final boolean clear) throws SolrServerException, IOException {
        List<Long> koulutukset = indexerDao.findAllKoulutusIds();
        logger.info("Found {} koulutukset to index.", koulutukset.size());
        if (clear) {
            clearIndex(koulutusSolr);
        }
        indexKoulutukset(koulutukset);
        return Integer.toString(koulutukset.size());
    }

    public void indexMuutokset(Tilamuutokset tm) {
        if (tm.getMuutetutKomotot().size() > 0) {
            indexKoulutukset(koulutusmoduuliToteutusDAO.findIdsByoids(tm.getMuutetutKomotot()));
        }
        if (tm.getMuutetutHakukohteet().size() > 0) {
            indexHakukohteet(hakukohdeDAO.findIdsByoids(tm.getMuutetutHakukohteet()));
        }

    }
}
