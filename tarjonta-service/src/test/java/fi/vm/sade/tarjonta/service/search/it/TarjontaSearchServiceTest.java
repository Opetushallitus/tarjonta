package fi.vm.sade.tarjonta.service.search.it;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.copy.NullAwareBeanUtilsBean;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.KoulutusDTOConverterToEntityTest;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.HakukohdeV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.KoulutusV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import fi.vm.sade.tarjonta.service.search.*;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeVastausTyyppi;
import fi.vm.sade.tarjonta.shared.KoodiService;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.contains;

@RunWith(MockitoJUnitRunner.class)
public class TarjontaSearchServiceTest {
    private HakukohdeSearchService hakukohdeSearchService;

    @Mock
    private SolrServerFactory solrServerFactory;

    @Mock
    private KoodiService koodiService;

    @Mock
    private OrganisaatioService organisaatioService;

    @Mock
    private OidService oidService;

    @Mock
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;

    private KoulutusSearchService koulutusSearchService;

    @Mock
    private SolrServer hakukohdeSolr;

    @Mock
    private SolrServer koulutusSolr;

    private static final String ophOid = "1.2.246.562.10.00000000001";

    private OrganisaatioPerustieto getOrganisaatio(String orgOid) {
        OrganisaatioPerustieto perus = new OrganisaatioPerustieto();
        perus.setOid(orgOid);
        perus.setParentOidPath(Joiner.on("/").join(ophOid, orgOid));
        perus.setOppilaitostyyppi("oppilaitostyyppi");
        perus.setKotipaikkaUri("kunta");
        perus.setNimi("fi", "org nimi fi for oid:" + orgOid);
        perus.setNimi("sv", "org nimi sv for oid:" + orgOid);
        perus.setNimi("en", "org nimi en for oid:" + orgOid);
        return perus;
    }

    @Before
    public void before() {
        Mockito.when(this.solrServerFactory.getSolrServer("hakukohteet")).thenReturn(this.hakukohdeSolr);
        this.hakukohdeSearchService = new HakukohdeSearchService(this.solrServerFactory, this.organisaatioService);

        Mockito.when(this.solrServerFactory.getSolrServer("koulutukset")).thenReturn(this.koulutusSolr);
        this.koulutusSearchService = new KoulutusSearchService(this.solrServerFactory, this.organisaatioService);

        try {
            Mockito.when(oidService.get(TarjontaOidType.KOMO)).thenReturn("oid-komo");
            Mockito.when(oidService.get(TarjontaOidType.KOMOTO)).thenReturn("oid-komoto");
            Mockito.when(oidService.get(TarjontaOidType.HAKUKOHDE)).thenReturn("oid-hakukohde");
        } catch (OIDCreationException e1) {
            e1.printStackTrace();
        }

        KoodistoURI.KOODISTO_KIELI_URI = "kieli";

        Mockito.when(organisaatioService.findByOidSet(Sets.newHashSet("1.2.3.4.555")))
                .thenReturn(Lists.newArrayList(getOrganisaatio("1.2.3.4.555")));
        Mockito.when(organisaatioService.findByUsingHakukohdeIndexingCache(Sets.newHashSet("1.2.3.4.555")))
                .thenReturn(Lists.newArrayList(getOrganisaatio("1.2.3.4.555")));
        Mockito.when(organisaatioService.findByOidSet(Sets.newHashSet("1.2.3.4.556")))
                .thenReturn(Lists.newArrayList(getOrganisaatio("1.2.3.4.556")));
        Mockito.when(organisaatioService.findByUsingHakukohdeIndexingCache(Sets.newHashSet("1.2.3.4.556")))
                .thenReturn(Lists.newArrayList(getOrganisaatio("1.2.3.4.556")));
        Mockito.when(organisaatioService.findByOidSet(Sets.newHashSet("1.2.3.4.557")))
                .thenReturn(Lists.newArrayList(getOrganisaatio("1.2.3.4.557")));
        Mockito.when(organisaatioService.findByUsingHakukohdeIndexingCache(Sets.newHashSet("1.2.3.4.557")))
                .thenReturn(Lists.newArrayList(getOrganisaatio("1.2.3.4.557")));
        Mockito.when(organisaatioService.findByOidSet(Sets.newHashSet("1.2.3.4.555", "1.2.3.4.556")))
                .thenReturn(Lists.newArrayList(getOrganisaatio("1.2.3.4.555"),getOrganisaatio("1.2.3.4.556")));
        Mockito.when(organisaatioService.findByUsingHakukohdeIndexingCache(Sets.newHashSet("1.2.3.4.555", "1.2.3.4.556")))
                .thenReturn(Lists.newArrayList(getOrganisaatio("1.2.3.4.555"),getOrganisaatio("1.2.3.4.556")));
        Mockito.when(organisaatioService.findByOid("1.2.3.4.555"))
                .thenReturn(getOrgDTO("1.2.3.4.555"));

        KoodiType kausiK = new KoodiType();
        kausiK.setKoodiArvo("kausi_k");
        kausiK.setKoodiUri("kausi_k");
        KoodiMetadataType metadata = new KoodiMetadataType() {{
            setKieli(KieliType.FI);
            setNimi("kausi_k");
        }};
        kausiK.getMetadata().add(metadata);
        Mockito.when(tarjontaKoodistoHelper.getKoodiByUri("kausi_k#1")).thenReturn(kausiK);
        Mockito.when(tarjontaKoodistoHelper.getUniqueKoodistoRelation(any(String.class), any(String.class), any(SuhteenTyyppiType.class), any(Boolean.class))).thenReturn("tutkintoonjohtavakoulutus_1");

        KoodiType kieliFi = new KoodiType();
        kieliFi.setKoodiArvo("fi");
        kieliFi.setKoodiUri("kieli_fi");
        Mockito.when(tarjontaKoodistoHelper.getKoodiByUri("kieli_fi")).thenReturn(kieliFi);

        stubKoodi(koodiService, "kieli_fi", "FI");
        stubKoodi(koodiService, "tutkinto-uri", "FI");
        stubKoodi(koodiService, "laajuus-uri", "FI");
        stubKoodi(koodiService, "laajuusyksikko-uri", "FI");
        stubKoodi(koodiService, "koulutusaste-uri", "FI");
        stubKoodi(koodiService, "koulutusala-uri", "FI");
        stubKoodi(koodiService, "opintoala-uri", "FI");
        stubKoodi(koodiService, "tutkintonimike-uri", "FI");
        stubKoodi(koodiService, "kausi_k", "FI");
        stubKoodi(koodiService, "EQF-uri", "FI");
        stubKoodi(koodiService, "suunniteltu-kesto-uri", "FI");

        KoulutusDTOConverterToEntityTest.mockKoodiService(koodiService);

    }

    private OrganisaatioRDTO getOrgDTO(String string) {
        OrganisaatioRDTO organisaatioDTO = new OrganisaatioRDTO();
        organisaatioDTO.setOid(string);
        organisaatioDTO.setNimi(ImmutableMap.of("fi", "organisaation nimi"));
        return organisaatioDTO;
    }

    @Test
    public void testEtsiKoulutukset() throws Exception {
        QueryResponse response = Mockito.mock(QueryResponse.class);
        SolrDocumentList solrDocuments = new SolrDocumentList();
        SolrDocument solrDocument = new SolrDocument();
        solrDocument.addField("orgoid_s", "1.2.3.4.555");
        solrDocuments.add(solrDocument);
        SolrDocument solrDocument2 = new SolrDocument();
        solrDocument2.addField("orgoid_s", "1.2.3.4.556");
        solrDocuments.add(solrDocument2);
        SolrDocument solrDocument3 = new SolrDocument();
        solrDocument3.addField("orgoid_s", "1.2.3.4.557");
        solrDocuments.add(solrDocument3);
        Mockito.when(response.getResults()).thenReturn(solrDocuments);
        Mockito.when(this.koulutusSolr.query(any())).thenReturn(response);
        Mockito.when(organisaatioService.findByOidSet(any()))
                .thenReturn(Lists.newArrayList(getOrganisaatio("1.2.3.4.555"),
                        getOrganisaatio("1.2.3.4.556"),
                        getOrganisaatio("1.2.3.4.557")));

        KoulutuksetKysely kysely = new KoulutuksetKysely();
        KoulutuksetVastaus vastaus = koulutusSearchService.haeKoulutukset(kysely);

        assertNotNull(vastaus);

        assertEquals(3, vastaus.getKoulutukset().size());
    }

    @Test
    public void testEtsiHakukohteet() throws Exception {
        QueryResponse response = Mockito.mock(QueryResponse.class);
        SolrDocumentList solrDocuments = new SolrDocumentList();
        SolrDocument solrDocument = new SolrDocument();
        solrDocument.addField("orgoid_s", "1.2.3.4.555");
        solrDocuments.add(solrDocument);
        SolrDocument solrDocument2 = new SolrDocument();
        solrDocument2.addField("orgoid_s", "1.2.3.4.556");
        solrDocuments.add(solrDocument2);
        SolrDocument solrDocument3 = new SolrDocument();
        solrDocument3.addField("orgoid_s", "1.2.3.4.557");
        solrDocuments.add(solrDocument3);
        Mockito.when(response.getResults()).thenReturn(solrDocuments);
        Mockito.when(this.hakukohdeSolr.query(any())).thenReturn(response);
        Mockito.when(organisaatioService.findByOidSet(any()))
                .thenReturn(Lists.newArrayList(getOrganisaatio("1.2.3.4.555"),
                        getOrganisaatio("1.2.3.4.556"),
                        getOrganisaatio("1.2.3.4.557")));

        HakukohteetKysely kysely = new HakukohteetKysely();
        HakukohteetVastaus vastaus = hakukohdeSearchService.haeHakukohteet(kysely);

        assertNotNull(vastaus);

        assertEquals(3, vastaus.getHakukohteet().size());
    }

    @Test
    public void testEtsiHakukohteetByHakuOid() throws Exception {
        QueryResponse response = Mockito.mock(QueryResponse.class);
        SolrDocumentList solrDocuments = new SolrDocumentList();
        SolrDocument solrDocument = new SolrDocument();
        solrDocument.addField("orgoid_s", "1.2.3.4.555");
        solrDocuments.add(solrDocument);
        Mockito.when(response.getResults()).thenReturn(solrDocuments);
        Mockito.when(this.hakukohdeSolr.query(any())).thenReturn(response);

        HakukohteetKysely kysely = new HakukohteetKysely();
        kysely.setHakuOid("1.2.3.4.5");
        HakukohteetVastaus vastaus = hakukohdeSearchService.haeHakukohteet(kysely);

        assertNotNull(vastaus);

        assertEquals(1, vastaus.getHakukohteet().size());
    }

    @Test
    public void testEtsiHakukohteetByTarjoajaOid() throws Exception {
        QueryResponse response = Mockito.mock(QueryResponse.class);
        SolrDocumentList solrDocuments = new SolrDocumentList();
        SolrDocument solrDocument = new SolrDocument();
        solrDocument.addField("orgoid_s", "1.2.3.4.555");
        solrDocuments.add(solrDocument);
        Mockito.when(response.getResults()).thenReturn(solrDocuments);
        Mockito.when(this.hakukohdeSolr.query(any())).thenReturn(response);

        HakukohteetKysely kysely = new HakukohteetKysely();
        kysely.getTarjoajaOids().add("1.2.3.4.555");
        HakukohteetVastaus vastaus = hakukohdeSearchService.haeHakukohteet(kysely);
        assertNotNull(vastaus);
        assertEquals(1, vastaus.getHakukohteet().size());
        assertEquals("1.2.3.4.555", vastaus.getHakukohteet().get(0)
                .getTarjoajaOid());

        response = Mockito.mock(QueryResponse.class);
        solrDocuments = new SolrDocumentList();
        solrDocument = new SolrDocument();
        solrDocument.addField("orgoid_s", "1.2.3.4.555");
        solrDocuments.add(solrDocument);
        SolrDocument solrDocument2 = new SolrDocument();
        solrDocument2.addField("orgoid_s", "1.2.3.4.556");
        solrDocuments.add(solrDocument2);
        Mockito.when(response.getResults()).thenReturn(solrDocuments);
        Mockito.when(this.hakukohdeSolr.query(any())).thenReturn(response);

        kysely.getTarjoajaOids().add("1.2.3.4.556");
        vastaus = hakukohdeSearchService.haeHakukohteet(kysely);
        assertNotNull(vastaus);
        assertEquals(2, vastaus.getHakukohteet().size());
        assertEquals("1.2.3.4.555", vastaus.getHakukohteet().get(0)
                .getTarjoajaOid());
        assertEquals("1.2.3.4.556", vastaus.getHakukohteet().get(1)
                .getTarjoajaOid());
    }

    @Test
    public void testKKKoulutus() throws Exception {
        Mockito.when(organisaatioService.findByOidSet(Mockito.anySet()))
                .thenReturn(Arrays.asList(getOrganisaatio("1.2.3.4.5.6.7.8.9")));
        Mockito.when(organisaatioService.findByUsingKoulutusIndexingCache(Mockito.anySet()))
                .thenReturn(Arrays.asList(getOrganisaatio("1.2.3.4.5.6.7.8.9")));

        QueryResponse response = Mockito.mock(QueryResponse.class);
        SolrDocumentList solrDocuments = new SolrDocumentList();
        SolrDocument solrDocument = new SolrDocument();
        solrDocument.addField("orgoid_s", "1.2.3.4.5.6.7.8.9");
        solrDocuments.add(solrDocument);
        Mockito.when(response.getResults()).thenReturn(solrDocuments);
        Mockito.when(this.koulutusSolr.query(any())).thenReturn(response);

        KoulutuksetKysely kysely = new KoulutuksetKysely();
        kysely.setNimi("otsikko");
        kysely.getTotetustyyppi().add(ToteutustyyppiEnum.KORKEAKOULUTUS);
        KoulutuksetVastaus vastaus = koulutusSearchService.haeKoulutukset(kysely);

        assertNotNull(vastaus);
        assertEquals(1, vastaus.getKoulutukset().size());
    }


    @Test
    public void testKKHakukohde() throws Exception {
        QueryResponse response = Mockito.mock(QueryResponse.class);
        SolrDocumentList solrDocuments = new SolrDocumentList();
        SolrDocument solrDocument = new SolrDocument();
        solrDocument.addField("orgoid_s", "1.2.3.4.555");
        solrDocument.addField("vuosikoodi_s", "2011");
        solrDocument.addField("kausiuri_s", "kausi_k#0");
        solrDocument.addField("hakukohteennimifi_t", "kkhakukohdenimi");
        solrDocuments.add(solrDocument);
        Mockito.when(response.getResults()).thenReturn(solrDocuments);
        Mockito.when(this.hakukohdeSolr.query(any())).thenReturn(response);

        HakukohteetKysely kysely = new HakukohteetKysely();
        kysely.getTotetustyyppi().add(ToteutustyyppiEnum.KORKEAKOULUTUS);
        kysely.setNimi("kkhakukohdenimi");

        HakukohteetVastaus vastaus = hakukohdeSearchService.haeHakukohteet(kysely);
        assertNotNull(vastaus);
        assertEquals(1, vastaus.getHakukohteet().size());

        assertEquals(Integer.valueOf(2011), vastaus.getHakukohteet().get(0).getKoulutuksenAlkamisvuosi());
        assertEquals("kausi_k#0", vastaus.getHakukohteet().get(0).getKoulutuksenAlkamiskausi().getUri());
        assertEquals("kkhakukohdenimi", vastaus.getHakukohteet().get(0).getNimi("fi"));
    }


    public static void stubKoodi(KoodiService koodiService, String uri, String arvo) {
        List<KoodiType> vastaus = Lists.newArrayList(getKoodiType(uri, arvo));
        Mockito.when(koodiService.searchKoodis(Matchers.argThat(new KoodistoCriteriaMatcher(uri))))
                .thenReturn(vastaus);
    }

    public static KoodiType getKoodiType(String uri, String arvo) {
        KoodiType kt = new KoodiType();
        kt.setKoodiArvo(arvo);
        kt.setKoodiUri(uri);
        kt.getMetadata().add(getKoodiMeta(arvo, KieliType.FI));
        kt.getMetadata().add(getKoodiMeta(arvo, KieliType.SV));
        kt.getMetadata().add(getKoodiMeta(arvo, KieliType.EN));
        return kt;
    }

    public static KoodiMetadataType getKoodiMeta(String arvo, KieliType kieli) {
        KoodiMetadataType type = new KoodiMetadataType();
        type.setKieli(kieli);
        type.setNimi(arvo + "-nimi-" + kieli.toString());
        return type;
    }

    public static class KoodistoCriteriaMatcher extends BaseMatcher<SearchKoodisCriteriaType> {

        private String uri;

        public KoodistoCriteriaMatcher(String uri) {
            this.uri = uri;
        }

        @Override
        public boolean matches(Object arg0) {
            SearchKoodisCriteriaType type = (SearchKoodisCriteriaType) arg0;
            return type != null && type.getKoodiUris().contains(uri);
        }

        @Override
        public void describeTo(Description description) {

        }
    }

}
