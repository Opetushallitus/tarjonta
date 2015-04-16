package fi.vm.sade.tarjonta.service.search;

import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.*;
import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SolrDocumentToHakukohdeConverterTest {

    @InjectMocks
    private SolrDocumentToHakukohdeConverter converter;

    @Test
    public void thatSolrDocumentIsConvertedToHakukohde() {
        final String HAKUAIKA_TEST_STRING = "Testi hakuaika 1";

        SolrDocumentList solrDocumentList = new SolrDocumentList();
        SolrDocument solrDocument = mock(SolrDocument.class);
        solrDocumentList.add(solrDocument);

        List<Object> aloituspaikatKuvaukset = new ArrayList<Object>();
        aloituspaikatKuvaukset.add("Maksimi 10");
        aloituspaikatKuvaukset.add("Max 10");

        List<Object> aloituspaikatKielet = new ArrayList<Object>();
        aloituspaikatKielet.add("kieli_fi");
        aloituspaikatKielet.add("kieli_en");

        when(solrDocument.getFieldValues(ALOITUSPAIKAT_KUVAUKSET)).thenReturn(aloituspaikatKuvaukset);
        when(solrDocument.getFieldValues(ALOITUSPAIKAT_KIELET)).thenReturn(aloituspaikatKielet);
        when(solrDocument.getFieldValue("orgoid_s")).thenReturn("1.2.3");
        when(solrDocument.getFieldValue(KOULUTUSMODUULITYYPPI_ENUM)).thenReturn(KoulutusmoduuliTyyppi.TUTKINTO);
        when(solrDocument.getFieldValues(RYHMA_OIDS)).thenReturn(getRyhmaOids());
        when(solrDocument.getFieldValues(RYHMA_PRIORITEETIT)).thenReturn(getPrioriteetit());
        when(solrDocument.getFieldValue(HAKUAIKA_STRING)).thenReturn(HAKUAIKA_TEST_STRING);

        HakukohteetVastaus hakukohteetVastaus = converter.convertSolrToHakukohteetVastaus(solrDocumentList, new HashMap<String, OrganisaatioPerustieto>(), "1.2.3");

        assertTrue(hakukohteetVastaus.getHakukohteet().size() == 1);

        HakukohdePerustieto perustieto = hakukohteetVastaus.getHakukohteet().get(0);

        assertTrue(perustieto.getAloituspaikatKuvaukset().size() == 2);
        assertEquals("Maksimi 10", perustieto.getAloituspaikatKuvaukset().get("kieli_fi"));
        assertEquals("Max 10", perustieto.getAloituspaikatKuvaukset().get("kieli_en"));

        List<SolrRyhmaliitos> ryhmaliitokset = perustieto.getRyhmaliitokset();
        assertTrue(ryhmaliitokset.size() == 2);
        assertEquals("1.2.3", ryhmaliitokset.get(0).getRyhmaOid());
        assertEquals(new Integer(0), ryhmaliitokset.get(0).getPrioriteetti());
        assertEquals("4.5.6", ryhmaliitokset.get(1).getRyhmaOid());
        assertNull(ryhmaliitokset.get(1).getPrioriteetti());
        assertEquals(hakukohteetVastaus.getHakukohteet().iterator().next().getHakuaikaString(), HAKUAIKA_TEST_STRING);
    }

    private List<Object> getPrioriteetit() {
        return Arrays.asList(new Object[]{"0", SolrFields.RYHMA_PRIORITEETTI_EI_MAARITELTY});
    }

    private List<Object> getRyhmaOids() {
        return Arrays.asList(new Object[]{"1.2.3", "4.5.6"});
    }
}
