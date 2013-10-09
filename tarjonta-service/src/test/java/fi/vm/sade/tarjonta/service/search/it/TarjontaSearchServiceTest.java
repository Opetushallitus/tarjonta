package fi.vm.sade.tarjonta.service.search.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;
import fi.vm.sade.tarjonta.SecurityAwareTestBase;
import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.dao.impl.KoulutusmoduuliToteutusDAOImpl;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.search.HakukohteetKysely;
import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus;
import fi.vm.sade.tarjonta.service.search.OrganisaatioHakukohdeGroup;
import fi.vm.sade.tarjonta.service.search.SolrServerFactory;
import fi.vm.sade.tarjonta.service.search.TarjontaSearchService;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeVastausTyyppi;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
})
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("embedded-solr")
public class TarjontaSearchServiceTest extends SecurityAwareTestBase {

    private OrganisaatioPerustieto getOrganisaatio(String orgOid) {
        OrganisaatioPerustieto perus = new OrganisaatioPerustieto();
        perus.setOid(orgOid);
        perus.setParentOidPath(Joiner.on("/").join(ophOid, orgOid));
        perus.setNimi("fi", "org nimi fi for oid:" + orgOid);
        perus.setNimi("sv", "org nimi sv for oid:" + orgOid);
        perus.setNimi("en", "org nimi en for oid:" + orgOid);
        return perus;
    }

    @Autowired
    private TarjontaSearchService tarjontaSearchService;
    @Autowired
    private OrganisaatioSearchService organisaatioSearchService;
    @Autowired
    private SolrServerFactory solrServerFactory;
    @Autowired
    private TarjontaAdminService adminService;
    @Autowired
    private TarjontaPublicService publicService;
    
    @Autowired
    private KoulutusmoduuliToteutusDAOImpl koulutusmoduuliToteutusDAO;

    @Autowired
    TarjontaFixtures tarjontaFixtures;
    protected Hakukohde hakukohde;

    @Before
    @Override
    public void before(){
        try {
            clearIndex(solrServerFactory.getOrganisaatioSolrServer());
            clearIndex(solrServerFactory.getSolrServer("hakukohteet"));
        } catch (SolrServerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        Mockito.stub(organisaatioSearchService.findByOidSet(Sets.newHashSet("1.2.3.4.555"))).toReturn(Lists.newArrayList(getOrganisaatio("1.2.3.4.555")));
        Mockito.stub(organisaatioSearchService.findByOidSet(Sets.newHashSet("1.2.3.4.556"))).toReturn(Lists.newArrayList(getOrganisaatio("1.2.3.4.556")));
        Mockito.stub(organisaatioSearchService.findByOidSet(Sets.newHashSet("1.2.3.4.557"))).toReturn(Lists.newArrayList(getOrganisaatio("1.2.3.4.557")));
        Mockito.stub(organisaatioSearchService.findByOidSet(Sets.newHashSet("1.2.3.4.555","1.2.3.4.556","1.2.3.4.557"))).toReturn(Lists.newArrayList(getOrganisaatio("1.2.3.4.555"),getOrganisaatio("1.2.3.4.556"),getOrganisaatio("1.2.3.4.557")));
        Mockito.stub(organisaatioSearchService.findByOidSet(Sets.newHashSet("1.2.3.4.555","1.2.3.4.556"))).toReturn(Lists.newArrayList(getOrganisaatio("1.2.3.4.555"),getOrganisaatio("1.2.3.4.556")));
        super.before();
    }

    private void clearIndex(SolrServer solrServer) throws SolrServerException, IOException {
        solrServer.deleteByQuery("*:*");
        solrServer.commit();
    }

    void createDataInTransaction(){
        
        TransactionTemplate tt = new TransactionTemplate(tm);
        tt.execute(new TransactionCallback<String>() {
            
            @Override
            public String doInTransaction(TransactionStatus status) {
                
                hakukohde = tarjontaFixtures.createPersistedHakukohdeWithKoulutus("1.2.3.4.555");
                LueHakukohdeVastausTyyppi hakukohdeVastaus = publicService.lueHakukohde(new LueHakukohdeKyselyTyyppi(hakukohde.getOid()));
                adminService.paivitaHakukohde(hakukohdeVastaus.getHakukohde());

                hakukohde = tarjontaFixtures.createPersistedHakukohdeWithKoulutus("1.2.3.4.556");
                hakukohdeVastaus = publicService.lueHakukohde(new LueHakukohdeKyselyTyyppi(hakukohde.getOid()));
                adminService.paivitaHakukohde(hakukohdeVastaus.getHakukohde());

                hakukohde = tarjontaFixtures.createPersistedHakukohdeWithKoulutus("1.2.3.4.557");
                hakukohdeVastaus = publicService.lueHakukohde(new LueHakukohdeKyselyTyyppi(hakukohde.getOid()));
                adminService.paivitaHakukohde(hakukohdeVastaus.getHakukohde());


                return null;
            }
            
        });
    }
    
    @Autowired
    PlatformTransactionManager tm;
    
    
    @Test
    public void testEtsiHakukohteet() throws SolrServerException {
        createDataInTransaction();
        
        HakukohteetKysely kysely = new HakukohteetKysely();
        HakukohteetVastaus vastaus = tarjontaSearchService.haeHakukohteet(kysely);

        assertNotNull(vastaus);

        assertEquals(3, vastaus.getHakukohteet().size());

        kysely.setNimi("foo");
        vastaus = tarjontaSearchService.haeHakukohteet(kysely);

        assertNotNull(vastaus);

        assertEquals(0, vastaus.getHakukohteet().size());

    }

    @Test
    public void testEtsiHakukohteetByHakuOid() throws SolrServerException {
        createDataInTransaction();
        
        HakukohteetKysely kysely = new HakukohteetKysely();
        kysely.setHakuOid(hakukohde.getHaku().getOid());
        HakukohteetVastaus vastaus = tarjontaSearchService.haeHakukohteet(kysely);

        assertNotNull(vastaus);

        assertEquals(1, vastaus.getHakukohteet().size());
    }

    @Test
    public void testEtsiHakukohteetByTarjoajaOid() throws SolrServerException {
        createDataInTransaction();
       
        HakukohteetKysely kysely = new HakukohteetKysely();
        kysely.getTarjoajaOids().add("1.2.3.4.555");
        HakukohteetVastaus vastaus = tarjontaSearchService.haeHakukohteet(kysely);
        assertNotNull(vastaus);
        assertEquals(1, vastaus.getHakukohteet().size());
        assertEquals("1.2.3.4.555", vastaus.getHakukohteet().get(0).getTarjoajaOid());

        kysely.getTarjoajaOids().add("1.2.3.4.556");
        vastaus = tarjontaSearchService.haeHakukohteet(kysely);
        assertNotNull(vastaus);
        assertEquals(2, vastaus.getHakukohteet().size());
        assertEquals("1.2.3.4.555", vastaus.getHakukohteet().get(0).getTarjoajaOid());
        assertEquals("1.2.3.4.556", vastaus.getHakukohteet().get(1).getTarjoajaOid());
    }
    
    
    @Test
    public void testEtsiHakukohteitaGrouped(){
        createDataInTransaction();
        
        HakukohteetKysely kysely = new HakukohteetKysely();
        List<OrganisaatioHakukohdeGroup> vastaus = tarjontaSearchService.haeHakukohteetGroupedByOrganisaatio(new Locale("fi"), kysely);
        assertEquals(3, vastaus.size());
        for(OrganisaatioHakukohdeGroup group: vastaus) {
            Assert.assertTrue(group.getOrganisaatioNimi().contains("fi"));
        }

        vastaus = tarjontaSearchService.haeHakukohteetGroupedByOrganisaatio(new Locale("sv"), kysely);
        assertEquals(3, vastaus.size());
        for(OrganisaatioHakukohdeGroup group: vastaus) {
            Assert.assertTrue(group.getOrganisaatioNimi().contains("sv"));
        }
}

}
