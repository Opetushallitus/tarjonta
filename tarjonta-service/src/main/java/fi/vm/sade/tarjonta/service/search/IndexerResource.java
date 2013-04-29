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

import com.google.common.collect.Lists;

import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;

@Transactional
@Component
@Path("/indexer")
public class IndexerResource {

    @Autowired
    private HakukohdeDAO hakukohdeDao;

    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusDao;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private SolrServer hakukohdeSolr;
    private SolrServer koulutusSolr;
    
    @Autowired
    private HakukohdeToSolrInputDocumentFunction hakukohdeSolrConverter;// = new HakukohdeToSolrInputDocumentFunction();
    @Autowired
    private KoulutusmoduuliToteutusToSolrInputDocumentFunction koulutusSolrConverter;// = new KoulutusmoduuliToteutusToSolrInputDocumentFunction();

    @GET
    @Path("/koulutukset/clear")
    @Produces("text/plain")
    public Response clearKoulutusIndex() {
        try {
            hakukohdeSolr.deleteByQuery("*:*");
            hakukohdeSolr.commit(true, true, false);
            return Response.ok().build();
        } catch (Throwable t) {
            return Response.serverError().entity(t.getMessage()).build();
        }
    }

    @GET
    @Path("/hakukohteet/clear")
    @Produces("text/plain")
    public Response clearHakukohdeIndex() {
        try {
            koulutusSolr.deleteByQuery("*:*");
            koulutusSolr.commit(true, true, false);
            return Response.ok().build();
        } catch (Throwable t) {
            return Response.serverError().entity(t.getMessage()).build();
        }
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
    @Path("/hakukohteet/start")
    @Produces("text/plain")
    public String rebuildHakukohdeIndex(@QueryParam("clean") final boolean clean) {
        // TODO fetch all, index em
        List<Hakukohde> hakukohteet = hakukohdeDao.findAll();
        try {
            if (clean) {
                hakukohdeSolr.deleteByQuery("*:*");
            }
        } catch (SolrServerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        indexHakukohde(hakukohteet);
        return Integer.toString(hakukohteet.size());
    }
    
    
    @Autowired
    public void setSolrServerFactory(SolrServerFactory factory) {
        this.hakukohdeSolr = factory.getSolrServer("hakukohteet");
        this.koulutusSolr = factory.getSolrServer("koulutukset");
    }

    // TODO is this proper object to pass in the api??
    public void indexHakukohde(List<Hakukohde> hakukohteet) {
        final List<SolrInputDocument> docs = Lists.newArrayList();
        for (Hakukohde hakukohde : hakukohteet) {
            docs.addAll(hakukohdeSolrConverter.apply(hakukohde));
        }
        index(hakukohdeSolr, docs);
    }

    // TODO is this proper object to pass in the api??
    public void indexKoulutus(List<KoulutusmoduuliToteutus> koulutukset) {
        final List<SolrInputDocument> docs = Lists.newArrayList();
        for (KoulutusmoduuliToteutus koulutus : koulutukset) {
            docs.addAll(koulutusSolrConverter.apply(koulutus));
        }
        index(koulutusSolr, docs);
    }

    private void index(final SolrServer solr, final List<SolrInputDocument> docs) {
        if (docs.size() > 0) {
        	afterCommit(new TransactionSynchronizationAdapter() {
				@Override
				public void afterCommit() {
			    	try {
		                logger.info("Indexing {} docs.", docs.size());
		                solr.add(docs);
		                logger.info("Committing changes to index.", docs.size());
		                solr.commit(true, true, false);
		                logger.info("Done.");
			        } catch (Exception e) {
			        	logger.warn("Indexing failed", e);
			        }
				}
			});
        }
    }
    
    public void deleteKoulutus(List<String> oids) throws IOException {
        deleteByOid(oids, koulutusSolr);
    }

    private void deleteByOid(final List<String> oids, final SolrServer solr) throws IOException {
    	afterCommit(((TransactionSynchronization) new TransactionSynchronizationAdapter() {
			@Override
			public void afterCommit() {
		    	try {
		            solr.deleteById(oids);
		        } catch (Exception e) {
		        	logger.warn("Indexing failed", e);
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

}
