package fi.vm.sade.tarjonta.service.search;

import com.google.common.collect.Lists;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.IndexerDAO;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.index.HakukohdeIndexEntity;
import fi.vm.sade.tarjonta.model.index.KoulutusIndexEntity;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.apache.solr.common.SolrInputDocument;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HakukohdeIndexEntityToSolrDocumentTest {

    private static final String RYHMA_OID = "oid-1";
    private static final String TARJOAJA_OID = "tarjoaja-oid";

    @Mock
    private OrganisaatioSearchService organisaatioSearchService;

    @Mock
    private KoodiService koodiService;

    @Mock
    private IndexerDAO indexerDAO;

    @Mock
    private HakukohdeDAO hakukohdeDAO;

    @InjectMocks
    private HakukohdeIndexEntityToSolrDocument converter;

    @Before
    public void before() {
        when(organisaatioSearchService.findByOidSet(anySet())).thenReturn(Lists.newArrayList(getOrg(TARJOAJA_OID)));
        when(koodiService.searchKoodis(Matchers.argThat(new KoodistoCriteriaMatcher("kieli_fi")))).thenReturn(Lists.newArrayList(getKoodiType("kieli_fi")));

        when(hakukohdeDAO.findHakukohdeByOid(anyString())).thenReturn(getHakukohde());

        when(indexerDAO.getAloituspaikatKuvausForHakukohde(1L)).thenReturn(getAloituspaikatKuvaus());
        when(indexerDAO.findKoulutusmoduuliToteutusesByHakukohdeId(anyLong())).thenReturn(Lists.newArrayList(new KoulutusIndexEntity("oid", "tarjoaja-oid", "foo", "bar",
                ModuulityyppiEnum.AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS, ToteutustyyppiEnum.AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA, "koulutus_1", "kevat_k", Integer.valueOf(2014))));
    }

    @Test
    public void testThatHakukohdeIsConvertedToDocument() {
        HakukohdeIndexEntity hk = new HakukohdeIndexEntity(1l, "hk-oid", "NIMI", null, null, null, null, null, null, null, null, RYHMA_OID);

        List<SolrInputDocument> docs = converter.apply(hk);

        assertTrue(docs.size() == 1);

        SolrInputDocument doc = docs.get(0);

        assertEquals(RYHMA_OID, doc.get(SolrFields.Hakukohde.ORGANISAATIORYHMAOID).getFirstValue().toString());

        Collection<Object> aloituspaikatValues = doc.getFieldValues(SolrFields.Hakukohde.ALOITUSPAIKAT_KUVAUKSET);
        assertTrue(aloituspaikatValues.size() == 1);
        assertEquals("Maksimi 20", aloituspaikatValues.iterator().next().toString());

        Collection<Object> aloituspaikatkielet = doc.getFieldValues(SolrFields.Hakukohde.ALOITUSPAIKAT_KIELET);
        assertTrue(aloituspaikatkielet.size() == 1);
        assertEquals("kieli_fi", aloituspaikatkielet.iterator().next().toString());
    }

    private Hakukohde getHakukohde() {
        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setId(1L);
        return new Hakukohde();
    }

    private MonikielinenTeksti getAloituspaikatKuvaus() {
        MonikielinenTeksti aloituspaikatKuvaus = new MonikielinenTeksti();
        aloituspaikatKuvaus.addTekstiKaannos("kieli_fi", "Maksimi 20");
        return aloituspaikatKuvaus;
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
    }
}
