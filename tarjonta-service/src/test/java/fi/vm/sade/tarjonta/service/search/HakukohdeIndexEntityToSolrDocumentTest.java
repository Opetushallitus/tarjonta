package fi.vm.sade.tarjonta.service.search;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.apache.solr.common.SolrInputDocument;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import com.google.common.collect.Lists;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;
import fi.vm.sade.tarjonta.dao.impl.IndexerDaoImpl;
import fi.vm.sade.tarjonta.model.index.HakukohdeIndexEntity;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

public class HakukohdeIndexEntityToSolrDocumentTest {

    

    private static final String RYHMA_OID = "oid-1";
    private static final String KOMO_OID = "komo-oid";
    private static final String OID = "oid";
    private static final String TUTKINTONIMIKEKOODI = "tutkintonimikekoodi";
    private static final ModuulityyppiEnum BASE_AMM_KOULUTUSTYYPPI = ModuulityyppiEnum.AMMATILLINEN_PERUSKOULUTUS;
    private static final ToteutustyyppiEnum SUB_AMM_KOULUTUSTYYPPI_AMMATILLINEN_PERUSTUTKINTO = ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO;
    private static final String LUKIOLINJA_URI = "lukiolinjakoodi";
    private static final String KOULUTUSOHJELMA_URI = "koulutusohjelmakoodi";
    private static final String OSAAMISALA_URI = "osaamisala_uri";

    private static final String KOULUTUS_URI = "koulutuskoodi";
    private static final String POHJAKOULUTUSVAATIMUS_URI = "pohjakoulutusvaatimuskoodi";
    private static final String TARJOAJA_OID = "tarjoaja-oid";
    private static final String KAUSI_URI = "kausi_uri";
    private static final int VUOSI = 2014;
    private static final String KOULUTUSTYYPPI_URI = SUB_AMM_KOULUTUSTYYPPI_AMMATILLINEN_PERUSTUTKINTO.uri();
    
    @Test
    public void test() {
        HakukohdeIndexEntityToSolrDocument converter = new HakukohdeIndexEntityToSolrDocument();
        
        OrganisaatioSearchService organisaatioSearchService = Mockito
                .mock(OrganisaatioSearchService.class);
        Whitebox.setInternalState(converter, "organisaatioSearchService",
                organisaatioSearchService);
        Mockito.stub(organisaatioSearchService.findByOidSet(Mockito.anySet()))
                .toReturn(Lists.newArrayList(getOrg(TARJOAJA_OID)));

        KoodiService koodiService = Mockito.mock(KoodiService.class);
        Whitebox.setInternalState(converter, "koodiService", koodiService);
        Mockito.reset(koodiService);
        stubKoodi(koodiService, LUKIOLINJA_URI);
        stubKoodi(koodiService, KOULUTUSOHJELMA_URI);
        stubKoodi(koodiService, OSAAMISALA_URI);
        stubKoodi(koodiService, KOULUTUS_URI);
        stubKoodi(koodiService, POHJAKOULUTUSVAATIMUS_URI);
        stubKoodi(koodiService, "kausi_k");

        IndexerDaoImpl indexerDao = Mockito.mock(IndexerDaoImpl.class);
        Whitebox.setInternalState(converter, "indexerDao", indexerDao);
        
        HakukohdeIndexEntity hk = new HakukohdeIndexEntity(1l,  "hk-oid",  "NIMI",  null,  null,  null,  null,  null,  null,  null,  null,  RYHMA_OID);
        List<SolrInputDocument> doc = converter.apply(hk);
        Assert.assertSame(1,  doc.size());
        Assert.assertEquals(RYHMA_OID,  doc.get(0).get(SolrFields.Hakukohde.ORGANISAATIORYHMAOID).getFirstValue());
    }

    
    private void stubKoodi(KoodiService koodiService, String uri) {
        List<KoodiType> vastaus = Lists.newArrayList(getKoodiType(uri));
        Mockito.stub(
                koodiService.searchKoodis(Matchers
                        .argThat(new KoodistoCriteriaMatcher(uri)))).toReturn(
                        vastaus);
    }

    private KoodiType getKoodiType(String uri) {
        KoodiType kt = new KoodiType();
        kt.setKoodiArvo(uri);
        kt.setKoodiUri(uri);
        kt.getMetadata().add(getKoodiMeta(uri, KieliType.FI));
        kt.getMetadata().add(getKoodiMeta(uri, KieliType.SV));
        kt.getMetadata().add(getKoodiMeta(uri, KieliType.EN));
        return kt;
    }

    private KoodiMetadataType getKoodiMeta(String arvo, KieliType kieli) {
        KoodiMetadataType type = new KoodiMetadataType();
        type.setKieli(kieli);
        type.setNimi(arvo + "-nimi-" + kieli.toString());
        return type;
    }

    private OrganisaatioPerustieto getOrg(String tarjoajaOid) {
        OrganisaatioPerustieto org = new OrganisaatioPerustieto();
        org.setOid(tarjoajaOid);
        org.setNimi("fi", "Organisaatio");
        return org;
    }

    private static class KoodistoCriteriaMatcher implements
            Matcher<SearchKoodisCriteriaType> {

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
        public void describeTo(Description arg0) {
        }

        @Override
        public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {
        }
    };

}
