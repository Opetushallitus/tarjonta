package fi.vm.sade.tarjonta.service.search;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;


import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi.KoulutusTulos;
import fi.vm.sade.tarjonta.service.types.KoulutusListausTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi.Teksti;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.*;

public class SolrDocumentToKoulutusmoduuliToteutusConverter {

    public HaeKoulutuksetVastausTyyppi convertSolrToKoulutuksetVastaus(SolrDocumentList solrKomotoList, SolrDocumentList solrOrgList) {
        HaeKoulutuksetVastausTyyppi vastaus = new HaeKoulutuksetVastausTyyppi();
        for (int i = 0; i < solrKomotoList.size(); ++i) {
            SolrDocument curDoc = solrKomotoList.get(i);
            KoulutusTulos tulos = convertKoulutus(curDoc, solrOrgList);
            if(tulos!=null) {
                vastaus.getKoulutusTulos().add(tulos);
            }
        }
        return vastaus;
    }
    
    private KoulutusTulos convertKoulutus(SolrDocument koulutusDoc, SolrDocumentList solrOrgList) {
        KoulutusTulos tulos = new KoulutusTulos();
        KoulutusListausTyyppi koulutus = new KoulutusListausTyyppi();
        koulutus.setAjankohta(koulutusDoc.getFieldValue(KAUSI_KOODI) + " " + koulutusDoc.getFieldValue(VUOSI_KOODI));
        koulutus.setKomotoOid("" + koulutusDoc.getFieldValue(OID));
        koulutus.setKoulutuskoodi(IndexingUtils.createKoodiTyyppi(KOULUTUSKOODI_URI, KOULUTUSKOODI_FI, KOULUTUSKOODI_SV, KOULUTUSKOODI_EN, koulutusDoc));
        koulutus.setKoulutusmoduuli("" + koulutusDoc.getFieldValue(KOULUTUSMODUULI_OID));
        koulutus.setKoulutusmoduuliToteutus("" + koulutusDoc.getFieldValue(OID));
        koulutus.setKoulutustyyppi(createKoulutustyyppi(koulutusDoc));
        if (koulutus.getKoulutustyyppi().equals(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS)) {
            koulutus.setKoulutusohjelmakoodi(IndexingUtils.createKoodiTyyppi(KOULUTUSOHJELMA_URI, KOULUTUSOHJELMA_FI, KOULUTUSOHJELMA_SV, KOULUTUSOHJELMA_EN, koulutusDoc));
        } else if (koulutus.getKoulutustyyppi().equals(KoulutusasteTyyppi.LUKIOKOULUTUS)) {
            koulutus.setLukiolinjakoodi(IndexingUtils.createKoodiTyyppi(KOULUTUSOHJELMA_URI, KOULUTUSOHJELMA_FI, KOULUTUSOHJELMA_SV, KOULUTUSOHJELMA_EN, koulutusDoc));
        }
        koulutus.setNimi(createKoulutusNimi(koulutusDoc));
        koulutus.setTila(IndexingUtils.createTila(koulutusDoc));
        koulutus.setTutkintonimike(IndexingUtils.createKoodiTyyppi(TUTKINTONIMIKE_URI, TUTKINTONIMIKE_FI, TUTKINTONIMIKE_SV, TUTKINTONIMIKE_EN, koulutusDoc));
        koulutus.setTarjoaja(IndexingUtils.createTarjoaja(koulutusDoc, solrOrgList));
        
        if(koulutus.getTarjoaja().getNimi()==null) {
            return null;
        }
       
        koulutus.setPohjakoulutusVaatimus("" + koulutusDoc.getFieldValue(POHJAKOULUTUSVAATIMUS_URI));
        tulos.setKoulutus(koulutus);
        return tulos;
    }
    
    private KoulutusasteTyyppi createKoulutustyyppi(SolrDocument koulutusDoc) {
        return KoulutusasteTyyppi.fromValue("" + koulutusDoc.getFieldValue(KOULUTUSTYYPPI));
    }

    private MonikielinenTekstiTyyppi createKoulutusNimi(SolrDocument koulutusDoc) {
        MonikielinenTekstiTyyppi nimi = new MonikielinenTekstiTyyppi();
        Teksti nimiFi = new Teksti();
        nimiFi.setKieliKoodi("fi");
        nimiFi.setValue("" + koulutusDoc.getFieldValue(KOULUTUSOHJELMA_FI));
        nimi.getTeksti().add(nimiFi);
        Teksti nimiSv = new Teksti();
        nimiSv.setKieliKoodi("sv");
        nimiSv.setValue("" + koulutusDoc.getFieldValue(KOULUTUSOHJELMA_SV));
        nimi.getTeksti().add(nimiSv);
        Teksti nimiEn = new Teksti();
        nimiEn.setKieliKoodi("en");
        nimiEn.setValue("" + koulutusDoc.getFieldValue(KOULUTUSOHJELMA_EN));
        nimi.getTeksti().add(nimiEn);
        return nimi;
    }

   
    
}
