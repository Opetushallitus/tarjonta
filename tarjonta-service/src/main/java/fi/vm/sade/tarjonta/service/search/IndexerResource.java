package fi.vm.sade.tarjonta.service.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.IndexerDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.index.HakukohdeIndexEntity;
import fi.vm.sade.tarjonta.model.index.KoulutusIndexEntity;

@Transactional(readOnly=true)
@Component
@Path("/indexer")
public class IndexerResource {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private SolrServer hakukohdeSolr;
    private SolrServer koulutusSolr;

    @Autowired
    private HakukohdeDAO hakukohdeDao;

    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusDao;

    @Autowired
    private IndexerDAO indexerDao;
    @Autowired
    HakukohdeIndexEntityToSolrDocument hakukohdeConverter;
    @Autowired
    KoulutusIndexEntityToSolrDocument koulutusConverter;

    /**
     * Clear koulutusindex.
     */
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
    @Path("/koulutukset/start")
    @Produces("text/plain")
    public Response rebuildKoulutuIndex(@QueryParam("clean") final boolean clean) {
        List<KoulutusmoduuliToteutus> koulutukset = koulutusDao.findAll();
        try {
            if (clean) {
                koulutusSolr.deleteByQuery("*:*");
            }
        } catch (SolrServerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        indexKoulutus(koulutukset);
        return Response.ok(Integer.toString(koulutukset.size())).build();
    }

    @GET
    @Path("/hakukohteet")
    @Produces("text/plain")
    public String rebuildHakukohdeIndex(@QueryParam("clear") final boolean clear) throws SolrServerException, IOException {
        List<Long> hakukohteet = indexerDao.findAllHakukohdeIds();
        logger.info("Found {} hakukohdes to index.", hakukohteet.size());
        if(clear) {
            clearIndex(hakukohdeSolr);
        }
        indexHakukohteet(hakukohteet);
        return Integer.toString(hakukohteet.size());
    }

    @GET
    @Path("/hakukohteet")
    @Produces("text/plain")
    public String buildHakukohdeIndex(@QueryParam("clear") final boolean clear) throws SolrServerException, IOException {
        List<HakukohdeIndexEntity> hakukohteet = indexerDao.findAllHakukohteet();
        logger.info("Found {} hakukohdes to index.", hakukohteet.size());
        if (clear) {
            clearIndex(hakukohdeSolr);
        }
        indexHakukohdeIndexEntities(hakukohteet);
        return Integer.toString(hakukohteet.size());
    }

    
    @Autowired
    public void setSolrServerFactory(SolrServerFactory factory) {
        this.hakukohdeSolr = factory.getSolrServer("hakukohteet");
        this.koulutusSolr = factory.getSolrServer("koulutukset");
    }

    /**
     * @deprecated do not call this
     */
    public void indexHakukohde(List<Hakukohde> hakukohteet) {
        List<Long> ids = Lists.newArrayList();
        for(Hakukohde hakukohde: hakukohteet) {
            ids.add(hakukohde.getId());
        }
        try {
            indexHakukohteet(ids);
        } catch (Exception e) {
            throw new RuntimeException("indexing.error", e);
        }
    }

    /**
     * @deprecated do not call this
     */
    public void indexKoulutus(List<KoulutusmoduuliToteutus> koulutukset) {
        List<Long> ids = Lists.newArrayList();
        
        for (KoulutusmoduuliToteutus koulutus : koulutukset) {
            ids.add(koulutus.getId());
        }
        try {
            indexKoulutukset(ids);
        } catch (Exception e) {
            throw new RuntimeException("indexing.error", e);
        }
    }

    private void index(final SolrServer solr, List<SolrInputDocument> docs) {
        if (docs.size() > 0) {
            final List<SolrInputDocument> localDocs = ImmutableList.copyOf(docs);
            afterCommit(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    try {
                        logger.info("Indexing {} docs.", localDocs.size());
                        solr.add(localDocs);
                        logger.info("Committing changes to index.");
                        solr.commit(true, true, false);
                        logger.info("Done.");
                    } catch (Exception e) {
                        throw new RuntimeException("indexing.error", e);
                    }
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
                } catch (Exception e) {
                    throw new RuntimeException("indexing.error", e);
                }
            }
        }));
    }

    private static void afterCommit(TransactionSynchronization sync) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(sync);
        } else {
            sync.afterCommit();
        }
    }

    public void deleteHakukohde(ArrayList<String> oids) throws IOException {
        deleteByOid(oids, hakukohdeSolr);
    }
    
    public void indexHakukohteet(List<Long> hakukohdeIdt) throws SolrServerException, IOException {
        List<SolrInputDocument> docs = Lists.newArrayList();
        int batch_size = 50;
        int index = 0;
        do {
            for (int j = index; j < index + batch_size && j < hakukohdeIdt.size(); j++) {

                Long hakukohdeId = hakukohdeIdt.get(j);
                logger.info(j + ". Fetching hakukohde:" + hakukohdeId);
                HakukohdeIndexEntity hakukohde = indexerDao.findHakukohdeById(hakukohdeId);
                docs.addAll(hakukohdeConverter.apply(hakukohde));
            }
           index += batch_size;
           logger.info("indexing:" + docs.size() + " docs");
           index(hakukohdeSolr, docs);
           docs.clear();
        } while (index < hakukohdeIdt.size());

        commit(hakukohdeSolr);
    }
    
    public void indexHakukohdeIndexEntities(List<HakukohdeIndexEntity> hakukohteet) throws SolrServerException,
            IOException {
        List<SolrInputDocument> docs = Lists.newArrayList();
        int batch_size = 100;
        for (HakukohdeIndexEntity hkie : hakukohteet) {
            docs.addAll(hakukohdeConverter.apply(hkie));
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
     * @param koulutukset id's of koulutukset to index
     * @throws IOException 
     * @throws SolrServerException 
     */
    public void indexKoulutukset(List<Long> koulutukset) throws SolrServerException, IOException {
        int batch_size = 50;
        int index = 0;
        do {
            final List<SolrInputDocument> docs = Lists.newArrayList();

            for (int j = index; j < index + batch_size && j < koulutukset.size(); j++) {

                Long koulutusId = koulutukset.get(j);
                logger.info(j + ". Fetching koulutus:" + koulutusId);
                KoulutusIndexEntity koulutus = indexerDao.findKoulutusById(koulutusId);
                docs.addAll(koulutusConverter.apply(koulutus));
            }
           index += batch_size;
           logger.info("indexing:" + docs.size() + " docs");
           index(koulutusSolr, docs);
           docs.clear();
        } while (index < koulutukset.size());

        commit(koulutusSolr);
    }

    private void commit(SolrServer solr) throws SolrServerException, IOException {
        solr.commit(true,true,false);
    }

    @GET
    @Path("/koulutukset")
    @Produces("text/plain")
    public String rebuildKoulutusIndex(@QueryParam("clear") final boolean clear) throws SolrServerException, IOException {
        List<Long> koulutukset = indexerDao.findAllKoulutusIds();
        logger.info("Found {} koulutukset to index.", koulutukset.size());
        if(clear) {
            clearIndex(koulutusSolr);
        }
        indexKoulutukset(koulutukset);
        return Integer.toString(koulutukset.size());
    }

}
