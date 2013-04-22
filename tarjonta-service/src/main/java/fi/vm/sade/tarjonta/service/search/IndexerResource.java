package fi.vm.sade.tarjonta.service.search;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Produces;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;

@Component
@javax.ws.rs.Path("/indexer")
public class IndexerResource {

    
    @Autowired
    private PlatformTransactionManager transactionManager;

    //TODO add required daos...
    
    private Logger logger = LoggerFactory.getLogger(getClass());

    private SolrServer hakukohdeSolr;
    private SolrServer koulutusSolr;
    private HakukohdeToSolrInputDocumentFunction koulutusConverter = new HakukohdeToSolrInputDocumentFunction();
    private KoulutusmoduuliToteutusToSolrInputDocumentFunction hakukohdeConverter = new KoulutusmoduuliToteutusToSolrInputDocumentFunction();

    
    
    @javax.ws.rs.Path("/koulutus/start")
    @Produces("text/plain")
    public String rebuildKoulutuIndex(final boolean clean){
        //TODO fetch all, index em
        Preconditions.checkNotNull(transactionManager, "need TM!");

        // sigh... annotations, for some reason, did not work
        TransactionTemplate tt = new TransactionTemplate(transactionManager);
        int count = tt.execute(new TransactionCallback<Integer>() {
            @Override
            public Integer doInTransaction(TransactionStatus arg0) {

//                List<Jotain> koulutukset = jokuDAOImpl
//                        .findAll();
//                try {
//                    if (clean) {
//                        koulutusSolr.deleteByQuery("*:*");
//                    }
//                } catch (SolrServerException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//                indexKoulutus(koulutukset);
//                return koulutukset.size();
                
                return 0;
            }
        });
        
        return Integer.toString(count);

    }

    @javax.ws.rs.Path("/hakukohde/start")
    @Produces("text/plain")
    public String rebuildHakukohdeIndex(final boolean clean){
        //TODO fetch all, index em
        TransactionTemplate tt = new TransactionTemplate(transactionManager);
        int count = tt.execute(new TransactionCallback<Integer>() {
            @Override
            public Integer doInTransaction(TransactionStatus arg0) {

//                List<Jotain> hakukohteet = jokuDAOImpl
//                        .findAll();
//                try {
//                    if (clean) {
//                        hakukohdeSolr.deleteByQuery("*:*");
//                    }
//                } catch (SolrServerException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//                indexHakukohde(hakukohteet);
//                return hakukohteet.size();
                return 0;
            }
        });
        return Integer.toString(count);
    }
    
    @Autowired
    public IndexerResource(SolrServerFactory factory) {
        this.hakukohdeSolr = factory.getSolrServer("hakukohteet");
        this.koulutusSolr = factory.getSolrServer("koulutukset");
    }

    // TODO is this proper object to pass in the api??
    public void indexHakukohde(List<KoulutusmoduuliToteutus> hakukohteet) {
        final List<SolrInputDocument> docs = Lists.newArrayList();
        //docs.addAll(Lists.transform(hakukohteet, hakukohdeConverter));
        index(hakukohdeSolr, docs);
    }

    // TODO is this proper object to pass in the api??
    public void indexKoulutus(List<Koulutusmoduuli> koulutukset) {
        final List<SolrInputDocument> docs = Lists.newArrayList();
        //docs.addAll(Lists.transform(koulutukset, koulutusConverter));
        index(koulutusSolr, docs);
    }

    private void index(final SolrServer solr, final List<SolrInputDocument> docs) {
        if (docs.size() > 0) {
            try {
                logger.info("Indexing {} docs.", docs.size());
                solr.add(docs);
                logger.info("Committing changes to index.", docs.size());
                solr.commit(true, true, false);
                logger.info("Done.");
            } catch (SolrServerException e) {
                logger.error("Indexing failed", e);
            } catch (IOException e) {
                logger.error("Indexing failed", e);
            }
        }
    }

}
