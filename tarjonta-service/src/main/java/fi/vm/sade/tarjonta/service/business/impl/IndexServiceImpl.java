package fi.vm.sade.tarjonta.service.business.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fi.vm.sade.tarjonta.dao.IndexerDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.business.IndexService;
import fi.vm.sade.tarjonta.service.search.HakukohdeToSolrDocument;
import fi.vm.sade.tarjonta.service.search.KoulutusToSolrDocument;
import fi.vm.sade.tarjonta.service.search.SolrServerFactory;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Qualifier("indexservice")
public class IndexServiceImpl implements IndexService {
    private static Logger logger = LoggerFactory.getLogger(IndexServiceImpl.class);

    private KoulutusToSolrDocument koulutusConverter;

    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    private SolrServer koulutusSolr;

    private SolrServer hakukohdeSolr;

    private HakukohdeToSolrDocument hakukohdeConverter;

    private IndexerDAO indexerDao;

    @Autowired
    public IndexServiceImpl(KoulutusToSolrDocument koulutusConverter,
                             KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO,
                             HakukohdeToSolrDocument hakukohdeConverter,
                             IndexerDAO indexerDao,
                             SolrServerFactory factory) {
        this.koulutusConverter = koulutusConverter;
        this.koulutusmoduuliToteutusDAO = koulutusmoduuliToteutusDAO;
        this.hakukohdeSolr = factory.getSolrServer("hakukohteet");
        this.koulutusSolr = factory.getSolrServer("koulutukset");
        this.hakukohdeConverter = hakukohdeConverter;
        this.indexerDao = indexerDao;
    }

    @Transactional
    @Override
    public void indexKoulutukset(List<Long> koulutusIds) {
        this.indexKoulutusBatch(koulutusIds, koulutusIds.size() + 1, 0);
    }

    @Transactional
    @Override
    public void indexHakukohteet(List<Long> ids) {
        this.indexHakukohdeBatch(ids, ids.size() + 1, 0);
    }

    @Transactional
    @Override
    public int indexKoulutusBatch(List<Long> koulutukset, int batch_size, int index) {
        final List<SolrInputDocument> docs = Lists.newArrayList();

        for (int j = index; j < index + batch_size && j < koulutukset.size(); j++) {
            Long koulutusId = koulutukset.get(j);
            logger.debug(j + ". Fetching koulutus:" + koulutusId);
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
            indexerDao.updateKoulutusIndexed(koulutusId, new Date());
        }
        index += batch_size;
        logger.debug("indexing:" + docs.size() + " docs");
        index(koulutusSolr, docs);
        docs.clear();
        commit(koulutusSolr);
        return index;
    }

    @Transactional
    @Override
    public int indexHakukohdeBatch(List<Long> hakukohdeIdt, int batch_size, int index) {
        logger.info("Indexing hakukohde batch, new implementation. Total ids: " + hakukohdeIdt.size());
        final List<Long> hakukohdeIdsInThisBatch = hakukohdeIdt.subList(index, Math.min(index+batch_size, hakukohdeIdt.size()));
        final List<Pair<SolrInputDocument, Long>> docsToBeIndexed = new ArrayList<>();
        for (Long hakukohdeId : hakukohdeIdsInThisBatch) {
            List<SolrInputDocument> toAdd = hakukohdeConverter.apply(hakukohdeId);
            toAdd.forEach(doc -> docsToBeIndexed.add(Pair.of(doc, hakukohdeId)));
            indexerDao.updateHakukohdeIndexed(hakukohdeId,  new Date());
        }
        index += hakukohdeIdsInThisBatch.size();
        logger.debug("indexing:" + docsToBeIndexed.size() + " docs");
        List<Long> successes = indexToSolrAndReportSuccesses(hakukohdeSolr, docsToBeIndexed);
        logger.info("Successes: {}. Committing", successes.size());
        commit(hakukohdeSolr);
        return index;
    }

    private List<Long> indexToSolrAndReportSuccesses(final SolrServer solr, List<Pair<SolrInputDocument, Long>> docsToIndex) {
        List<Long> successes = new ArrayList<>();

        logger.info("Got call to index {} documents if ", docsToIndex.size());

        if (docsToIndex.size() > 0) {
            final List<Pair<SolrInputDocument, Long>> localDocs = ImmutableList.copyOf(docsToIndex);
            afterCommit(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    Exception lastException = null;
                    logger.info("Now actually indexing {} documents", localDocs.size());
                    try {
                        for (Pair<SolrInputDocument, Long> doc : localDocs) {
                            System.out.println("adding " + doc.getRight());
                            UpdateResponse res = solr.add(doc.getLeft());
                            if (res != null && res.getStatus() == 0) {
                                successes.add(doc.getRight());
                            } else if (res != null) {
                                logger.error("Something went wrong while indexing document id " + doc.getRight() +
                                        " with oid " + doc.getLeft().get("id") +
                                        ", status code from solr " + res.getStatus());
                            } else {
                                logger.warn("Whoa! Why does solr return a null? id: " + doc.getRight());
                            }
                        }
                        return; // Return peacefully if no errors caught
                    } catch (Exception e) {
                        logger.error("Exception happened while indexing: ", e);
                        lastException = e;
                    }

                    throw new RuntimeException(
                            "indexing.error, last exception:", lastException);
                }
            });
        }
        return successes;
    }

    private void index(final SolrServer solr, List<SolrInputDocument> docs) {
        if (docs.size() > 0) {
            final List<SolrInputDocument> localDocs = ImmutableList
                    .copyOf(docs);
            afterCommit(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    Exception lastException = null;

                    logger.info("Now actually indexing {} documents", localDocs.size());
                    // try 3 times
                    for (int i = 0; i < 5; i++) {
                        try {
                            logger.debug("Indexing {} docs, try {}", localDocs.size(), i);
                            solr.add(localDocs);
                            logger.debug("Committing changes to index.");
                            solr.commit(true, true, false);
                            logger.debug("Done.");
                            return; //exit on success!
                        } catch (Exception e) {
                            logger.error("There was an exception while indexing: ", e);
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

    private void commit(SolrServer solr) {
        try {
            solr.commit(true, true, false);
        } catch (SolrServerException | IOException e) {
            throw new RuntimeException("indexing.error", e);
        }
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

}
