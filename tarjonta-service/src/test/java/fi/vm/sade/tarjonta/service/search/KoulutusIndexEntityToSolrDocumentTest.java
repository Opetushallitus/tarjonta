package fi.vm.sade.tarjonta.service.search;

import java.util.Date;
import java.util.GregorianCalendar;
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
import fi.vm.sade.tarjonta.model.index.KoulutusIndexEntity;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

public class KoulutusIndexEntityToSolrDocumentTest {

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
        Date d = new GregorianCalendar(VUOSI, 1, 1).getTime();
        KoulutusIndexEntity e = new KoulutusIndexEntity(1l, OID, d, d,
                TarjontaTila.JULKAISTU,
                BASE_AMM_KOULUTUSTYYPPI,
                SUB_AMM_KOULUTUSTYYPPI_AMMATILLINEN_PERUSTUTKINTO,
                KOMO_OID,
                KOULUTUS_URI,
                LUKIOLINJA_URI,
                KOULUTUSOHJELMA_URI,
                OSAAMISALA_URI,
                TARJOAJA_OID,
                POHJAKOULUTUSVAATIMUS_URI,
                KAUSI_URI,
                VUOSI,
                KOULUTUSTYYPPI_URI);

        KoulutusIndexEntityToSolrDocument converter = new KoulutusIndexEntityToSolrDocument();

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

        List<SolrInputDocument> docs = converter.apply(e);
        Assert.assertEquals("lukumäärä ei vastaa odotettua", 1, docs.size());

        SolrInputDocument doc = docs.get(0);

        // verifioi dokumentin kentät
        Assert.assertEquals(OID, doc.removeField(SolrFields.Koulutus.OID)
                .getValue());
        Assert.assertEquals(TARJOAJA_OID,
                doc.removeField(SolrFields.Koulutus.ORG_OID).getValue());
        Assert.assertEquals(TarjontaTila.JULKAISTU,
                doc.removeField(SolrFields.Koulutus.TILA).getValue());
        Assert.assertEquals(KOMO_OID,
                doc.removeField(SolrFields.Koulutus.KOULUTUSMODUULI_OID)
                .getValue());

        Assert.assertEquals(VUOSI + "",
                doc.removeField(SolrFields.Koulutus.VUOSI_KOODI).getValue());
        Assert.assertEquals(KOULUTUS_URI + "-nimi-EN",
                doc.removeField(SolrFields.Koulutus.KOULUTUSKOODI_EN)
                .getValue());
        Assert.assertEquals(KOULUTUS_URI + "-nimi-FI",
                doc.removeField(SolrFields.Koulutus.KOULUTUSKOODI_FI)
                .getValue());
        Assert.assertEquals(KOULUTUS_URI + "-nimi-SV",
                doc.removeField(SolrFields.Koulutus.KOULUTUSKOODI_SV)
                .getValue());
        Assert.assertEquals(KOULUTUS_URI,
                doc.removeField(SolrFields.Koulutus.KOULUTUSKOODI_URI)
                .getValue());

        Assert.assertEquals(KOULUTUSOHJELMA_URI  + "-nimi-EN",
                doc.removeField(SolrFields.Koulutus.KOULUTUSOHJELMA_EN)
                .getValue());
        Assert.assertEquals(KOULUTUSOHJELMA_URI + "-nimi-SV",
                doc.removeField(SolrFields.Koulutus.KOULUTUSOHJELMA_SV)
                .getValue());
        Assert.assertEquals(KOULUTUSOHJELMA_URI + "-nimi-FI",
                doc.removeField(SolrFields.Koulutus.KOULUTUSOHJELMA_FI)
                .getValue());
        Assert.assertEquals(KOULUTUSOHJELMA_URI,
                doc.removeField(SolrFields.Koulutus.KOULUTUSOHJELMA_URI)
                .getValue());
        Assert.assertEquals(KOULUTUSTYYPPI_URI,
                doc.removeField(SolrFields.Koulutus.KOULUTUSTYYPPI_URI)
                .getValue());

//        Assert.assertEquals(TUTKINTONIMIKEKOODI,
//                doc.removeField(SolrFields.Koulutus.TUTKINTONIMIKE_URI)
//                .getValue());
//        Assert.assertEquals(TUTKINTONIMIKEKOODI + "-nimi-EN",
//                doc.removeField(SolrFields.Koulutus.TUTKINTONIMIKE_EN)
//                .getValue());
//        Assert.assertEquals(TUTKINTONIMIKEKOODI + "-nimi-SV",
//                doc.removeField(SolrFields.Koulutus.TUTKINTONIMIKE_SV)
//                .getValue());
//        Assert.assertEquals(TUTKINTONIMIKEKOODI + "-nimi-FI",
//                doc.removeField(SolrFields.Koulutus.TUTKINTONIMIKE_FI)
//                .getValue());
        //System.out.println(doc);
        Assert.assertEquals("kausi_k#0",
                doc.removeField(SolrFields.Koulutus.KAUSI_URI).getValue());
        Assert.assertEquals(BASE_AMM_KOULUTUSTYYPPI.getKoulutusasteTyyppi().value(),
                doc.removeField(SolrFields.Koulutus.KOULUTUSASTETYYPPI_ENUM).getValue());
        Assert.assertEquals(POHJAKOULUTUSVAATIMUS_URI + "#0",
                doc.removeField(SolrFields.Koulutus.POHJAKOULUTUSVAATIMUS_URI)
                .getValue());

        // tekstihaku contains something
        Assert.assertTrue(doc.removeField(SolrFields.Koulutus.TEKSTIHAKU)
                .getValueCount() > 1);
    }

    @Test
    public void testNullAlkamispvm() {
        KoulutusIndexEntity e = new KoulutusIndexEntity(1l, OID, null, null,
                TarjontaTila.JULKAISTU,
                BASE_AMM_KOULUTUSTYYPPI,
                SUB_AMM_KOULUTUSTYYPPI_AMMATILLINEN_PERUSTUTKINTO,
                KOMO_OID,
                KOULUTUS_URI,
                LUKIOLINJA_URI,
                KOULUTUSOHJELMA_URI,
                OSAAMISALA_URI,
                TARJOAJA_OID,
                POHJAKOULUTUSVAATIMUS_URI,
                KAUSI_URI,
                VUOSI,
                KOULUTUSTYYPPI_URI
        );

        KoulutusIndexEntityToSolrDocument converter = new KoulutusIndexEntityToSolrDocument();

        OrganisaatioSearchService organisaatioSearchService = Mockito
                .mock(OrganisaatioSearchService.class);
        Whitebox.setInternalState(converter, "organisaatioSearchService",
                organisaatioSearchService);
        Mockito.stub(organisaatioSearchService.findByOidSet(Mockito.anySet()))
                .toReturn(Lists.newArrayList(getOrg(TARJOAJA_OID)));

        KoodiService koodiService = Mockito.mock(KoodiService.class);
        Whitebox.setInternalState(converter, "koodiService", koodiService);
        Mockito.reset(koodiService);
        stubKoodi(koodiService, KAUSI_URI);

        IndexerDaoImpl indexerDao = Mockito.mock(IndexerDaoImpl.class);
        Whitebox.setInternalState(converter, "indexerDao", indexerDao);

        List<SolrInputDocument> docs = converter.apply(e);
        Assert.assertEquals("lukumäärä ei vastaa odotettua", 1, docs.size());

        SolrInputDocument doc = docs.get(0);

        Assert.assertEquals(KAUSI_URI + "#0", doc.removeField(SolrFields.Koulutus.KAUSI_URI).getValue());
        Assert.assertEquals(new Integer(VUOSI), (Integer) doc.removeField(SolrFields.Koulutus.VUOSI_KOODI).getValue());
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
