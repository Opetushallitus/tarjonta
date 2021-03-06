package fi.vm.sade.tarjonta.service.business.impl;

import com.google.common.collect.ImmutableList;
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
import java.util.stream.Collectors;

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
        logger.info("Marking following koulutukses to viimindeksointipvm = null: {}", koulutusIds);
        indexerDao.updateKoulutuksesIndexed(koulutusIds, null); //Just mark, let IndexSynchronizer cronjob handle the actual indexing
    }

    @Transactional
    @Override
    public void indexHakukohteet(List<Long> hakukohdeIds) {
        logger.info("Marking following hakukohdeids to viimindeksointipvm = null: {}", hakukohdeIds);
        indexerDao.updateHakukohteesIndexed(hakukohdeIds, null); //Just mark, let IndexSynchronizer cronjob handle the actual indexing
    }

    @Transactional
    @Override
    public int indexKoulutusBatch(List<Long> koulutukset, int batchSize, int index) {
        final List<Long> koulutusIdsInThisBatch = koulutukset.subList(index, Math.min(index+ batchSize, koulutukset.size()));
        logger.info("Indexing koulutus batch. Total ids: {}, in this batch: {}, index: {} ",koulutukset.size(), koulutusIdsInThisBatch.size(), index);
        final List<Pair<SolrInputDocument, Long>> docsToBeIndexed = new ArrayList<>();
        final List<Long> siblingIds = new ArrayList<>();

        for (Long koulutusId : koulutusIdsInThisBatch) {
            List<SolrInputDocument> toAdd = koulutusConverter.apply(koulutusId);
            if (toAdd != null) {
                toAdd.forEach(doc -> docsToBeIndexed.add(Pair.of(doc, koulutusId)));
            }
            indexerDao.updateKoulutusIndexed(koulutusId, new Date());
            //Reindex sibling komotos
            KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.read(koulutusId);
            if (komoto != null) {
                List<KoulutusmoduuliToteutus> siblings = koulutusmoduuliToteutusDAO.findSiblingKomotos(komoto);
                if (siblings != null) {
                    for (KoulutusmoduuliToteutus sibling : siblings) {
                        Long siblingId = sibling.getId();
                        if (!koulutukset.contains(siblingId)) {
                            List<SolrInputDocument> siblingsToReindex = koulutusConverter.apply(siblingId);
                            if (siblingsToReindex != null) {
                                siblingsToReindex.forEach(doc -> {
                                    siblingIds.add(siblingId);
                                    docsToBeIndexed.add(Pair.of(doc, siblingId));
                                });
                                indexerDao.updateKoulutusIndexed(siblingId, new Date());
                            }
                        }
                    }
                }
            }
        }

        logger.info("Indexing {} koulutukses and {} of their siblings", docsToBeIndexed.size()-siblingIds.size(), siblingIds.size());
        indexToSolrVerbosely(koulutusSolr, docsToBeIndexed, "KOULUTUS");
        return index + koulutusIdsInThisBatch.size();
    }

    @Transactional
    @Override
    public int indexHakukohdeBatch(List<Long> hakukohdeIdt, int batchSize, int index) {
        final List<Long> hakukohdeIdsInThisBatch = hakukohdeIdt.subList(index, Math.min(index+ batchSize, hakukohdeIdt.size()));
        logger.info("Indexing hakukohde batch. Total ids: {}, in this batch: {}, index: {} ",hakukohdeIdt.size(), hakukohdeIdsInThisBatch.size(), index);
        final List<Pair<SolrInputDocument, Long>> docsToBeIndexed = new ArrayList<>();
        for (Long hakukohdeId : hakukohdeIdsInThisBatch) {
            List<SolrInputDocument> toAdd = hakukohdeConverter.apply(hakukohdeId);
            if (toAdd != null) {
                toAdd.forEach(doc -> docsToBeIndexed.add(Pair.of(doc, hakukohdeId)));
                indexerDao.updateHakukohdeIndexed(hakukohdeId, new Date());
            }
        }
        indexToSolrVerbosely(hakukohdeSolr, docsToBeIndexed, "HAKUKOHDE");
        return index + hakukohdeIdsInThisBatch.size();
    }

    private void indexToSolrVerbosely(final SolrServer solr, List<Pair<SolrInputDocument, Long>> docsToIndex, String type) {
        if (docsToIndex.size() > 0) {
            List<Long> successes = new ArrayList<>();
            final List<Pair<SolrInputDocument, Long>> localDocs = ImmutableList.copyOf(docsToIndex);
            afterCommit(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    logger.info("Now actually indexing documents of type {}: {}", type, localDocs.stream().map(Pair::getRight).collect(Collectors.toList()));
                    try {
                        for (Pair<SolrInputDocument, Long> doc : localDocs) {
                            logger.debug("Adding {}, oid: {}", doc.getRight(), doc.getLeft().get("id"));
                            UpdateResponse res = solr.add(doc.getLeft());
                            if (res != null && res.getStatus() == 0) {
                                successes.add(doc.getRight());
                            } else if (res != null) {
                                logger.error("Something went wrong while indexing document id {} of type {}, oid {}, status from solr: {}"
                                        ,doc.getRight(), type, doc.getLeft().get("id"), res.getStatus());
                            } else {
                                logger.warn("Whoa! Why does solr return a null? id: " + doc.getRight());
                            }
                        }
                        if (successes.size() != localDocs.size()) {
                            logger.warn("Got a different amount of successes ({}) than ingoing documents ({}). This might signal a problem.", successes.size(), localDocs.size());
                        }
                        logger.info("Committing {} with {} successes: {} ", type, successes.size(), successes);
                        UpdateResponse commitRes = solr.commit(true, true, false);
                        if (commitRes != null && commitRes.getStatus() != 0) {
                            logger.error("Something went wrong with commit! Status {}", commitRes.getStatus());
                        }
                        return; // Return peacefully if no errors caught
                    } catch (Exception e) {
                        logger.error("Exception happened while indexing: ", e);
                        throw new RuntimeException(
                                "indexing.error, last exception:", e);
                    }

                }
            });
        }
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

}
