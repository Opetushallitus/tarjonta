package fi.vm.sade.tarjonta.service.search;

import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.ALOITUSPAIKAT_KIELET;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.ALOITUSPAIKAT_KUVAUKSET;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SolrDocumentToHakukohdeConverterTest {

    @InjectMocks
    private SolrDocumentToHakukohdeConverter converter;

    @Test
    public void thatSolrDocumentIsConvertedToHakukohde() {
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

        HakukohteetVastaus hakukohteetVastaus = converter.convertSolrToHakukohteetVastaus(solrDocumentList, new HashMap<String, OrganisaatioPerustieto>(), "1.2.3");

        assertTrue(hakukohteetVastaus.getHakukohteet().size() == 1);

        HakukohdePerustieto perustieto = hakukohteetVastaus.getHakukohteet().get(0);

        assertTrue(perustieto.getAloituspaikatKuvaukset().size() == 2);
        assertEquals("Maksimi 10", perustieto.getAloituspaikatKuvaukset().get("kieli_fi"));
        assertEquals("Max 10", perustieto.getAloituspaikatKuvaukset().get("kieli_en"));
    }
}
