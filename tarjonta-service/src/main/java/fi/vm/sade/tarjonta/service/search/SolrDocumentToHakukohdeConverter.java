package fi.vm.sade.tarjonta.service.search;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi.HakukohdeTulos;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi.Teksti;
import fi.vm.sade.tarjonta.service.types.HakukohdeListausTyyppi;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.*;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSOHJELMA_EN;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSOHJELMA_FI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSOHJELMA_SV;

public class SolrDocumentToHakukohdeConverter {
    
    public HaeHakukohteetVastausTyyppi convertSolrToHakukohteetVastaus(SolrDocumentList solrHakukohdeList, SolrDocumentList solrOrgList) {
        HaeHakukohteetVastausTyyppi vastaus = new HaeHakukohteetVastausTyyppi();
        for (int i = 0 ; i < solrHakukohdeList.size(); ++i) {
            SolrDocument hakukohdeDoc = solrHakukohdeList.get(i);
            HakukohdeTulos tulos = convertHakukohde(hakukohdeDoc, solrOrgList);
            if(tulos!=null) {
                vastaus.getHakukohdeTulos().add(tulos);
            }
        }
        
        return vastaus;
    }

    private HakukohdeTulos convertHakukohde(SolrDocument hakukohdeDoc,
            SolrDocumentList solrOrgList) {
        HakukohdeTulos vastaus = new HakukohdeTulos();
        HakukohdeListausTyyppi hakukohde = new HakukohdeListausTyyppi();
        hakukohde.setAloituspaikat("" + hakukohdeDoc.getFieldValue(ALOITUSPAIKAT));
        hakukohde.setHakuAlkamisPvm(parseDate(hakukohdeDoc, HAUN_ALKAMISPVM));
        hakukohde.setHakuPaattymisPvm(parseDate(hakukohdeDoc, HAUN_PAATTYMISPVM));
        hakukohde.setHakutapaKoodi(IndexingUtils.createKoodiTyyppi(HAKUTAPA_URI, HAKUTAPA_FI, HAKUTAPA_SV, HAKUTAPA_EN, hakukohdeDoc));
        hakukohde.setKoodistoNimi("" + hakukohdeDoc.getFieldValue(HAKUKOHTEEN_NIMI_URI));
        hakukohde.setKoulutuksenAlkamiskausiUri("" + hakukohdeDoc.getFieldValue(KAUSI_FI));
        hakukohde.setKoulutuksenAlkamisvuosi("" + hakukohdeDoc.getFieldValue(VUOSI_KOODI));
        hakukohde.setNimi(createHakukohdeNimi(hakukohdeDoc));
        hakukohde.setOid("" + hakukohdeDoc.getFieldValue(OID));
        hakukohde.setTila(IndexingUtils.createTila(hakukohdeDoc));
        hakukohde.setTarjoaja(IndexingUtils.createTarjoaja(hakukohdeDoc, solrOrgList));
        if(hakukohde.getTarjoaja().getNimi()==null) {
            return null;
        }
        vastaus.setHakukohde(hakukohde);
        return vastaus;
    }
    
    private Date parseDate(SolrDocument hakukohdeDoc, String dateField) {
        String pvmStr = "" + hakukohdeDoc.getFieldValue(dateField);
        if (!pvmStr.isEmpty()) {
            try {
                return new SimpleDateFormat("MM.dd.yyyy HH:mm:ss").parse(pvmStr);
            } catch (Exception ex) {
                
            }
        }
        return null;
    }
    
    private MonikielinenTekstiTyyppi createHakukohdeNimi(SolrDocument hakukohdeDoc) {
        MonikielinenTekstiTyyppi nimi = new MonikielinenTekstiTyyppi();
        Teksti nimiFi = new Teksti();
        nimiFi.setKieliKoodi("fi");
        nimiFi.setValue("" + hakukohdeDoc.getFieldValue(HAKUKOHTEEN_NIMI_FI));
        nimi.getTeksti().add(nimiFi);
        Teksti nimiSv = new Teksti();
        nimiSv.setKieliKoodi("sv");
        nimiSv.setValue("" + hakukohdeDoc.getFieldValue(HAKUKOHTEEN_NIMI_SV));
        nimi.getTeksti().add(nimiSv);
        Teksti nimiEn = new Teksti();
        nimiEn.setKieliKoodi("en");
        nimiEn.setValue("" + hakukohdeDoc.getFieldValue(HAKUKOHTEEN_NIMI_EN));
        nimi.getTeksti().add(nimiEn);
        return nimi;
    }

}
