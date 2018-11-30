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
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
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
        final List<SolrInputDocument> docs = Lists.newArrayList();
        for (int j = index; j < index + batch_size && j < hakukohdeIdt.size(); j++) {

            Long hakukohdeId = hakukohdeIdt.get(j);
            logger.debug(j + ". Fetching hakukohde:" + hakukohdeId);
            docs.addAll(hakukohdeConverter.apply(hakukohdeId));
            indexerDao.updateHakukohdeIndexed(hakukohdeId,  new Date());
        }
        index += batch_size;
        logger.debug("indexing:" + docs.size() + " docs");
        index(hakukohdeSolr, docs);
        docs.clear();
        commit(hakukohdeSolr);
        return index;
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
                            logger.debug("Indexing {} docs, try {}", localDocs.size(), i);
                            solr.add(localDocs);
                            logger.debug("Committing changes to index.");
                            solr.commit(true, true, false);
                            logger.debug("Done.");
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

    private void commit(SolrServer solr) {
        try {
            solr.commit(true, true, false);
        } catch (SolrServerException | IOException e) {
            throw new RuntimeException("indexing.error", e);
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
