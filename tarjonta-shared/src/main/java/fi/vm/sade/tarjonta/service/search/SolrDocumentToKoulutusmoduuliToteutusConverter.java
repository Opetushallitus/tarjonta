package fi.vm.sade.tarjonta.service.search;

import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KAUSI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KAUSI_KOODI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSKOODI_EN;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSKOODI_FI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSKOODI_SV;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSKOODI_URI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSLAJI_URIS;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSMODUULI_OID;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSOHJELMA_EN;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSOHJELMA_FI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSOHJELMA_SV;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSOHJELMA_URI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSTYYPPI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.OID;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.POHJAKOULUTUSVAATIMUS_URI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.TUTKINTONIMIKE_EN;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.TUTKINTONIMIKE_FI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.TUTKINTONIMIKE_SV;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.TUTKINTONIMIKE_URI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.VUOSI_KOODI;

import java.util.Map;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;

public class SolrDocumentToKoulutusmoduuliToteutusConverter {

    public KoulutuksetVastaus convertSolrToKoulutuksetVastaus(SolrDocumentList solrKomotoList, Map<String, OrganisaatioPerustieto> orgs) {
        KoulutuksetVastaus vastaus = new KoulutuksetVastaus();
        for (int i = 0; i < solrKomotoList.size(); ++i) {
            SolrDocument curDoc = solrKomotoList.get(i);
            KoulutusPerustieto koulutus = convertKoulutus(curDoc, orgs);
            if (koulutus != null) {
                vastaus.getKoulutukset().add(koulutus);
            }
        }
        return vastaus;
    }
    
    private KoulutusPerustieto convertKoulutus(SolrDocument koulutusDoc, Map<String, OrganisaatioPerustieto> orgs) {
        KoulutusPerustieto koulutus = new KoulutusPerustieto();
        koulutus.setAjankohta(koulutusDoc.getFieldValue(KAUSI) + " " + koulutusDoc.getFieldValue(VUOSI_KOODI));
        koulutus.setKomotoOid("" + koulutusDoc.getFieldValue(OID));
        koulutus.setKoulutuskoodi(IndexDataUtils.createKoodiTyyppi(KOULUTUSKOODI_URI, KOULUTUSKOODI_FI, KOULUTUSKOODI_SV, KOULUTUSKOODI_EN, koulutusDoc));
        koulutus.setKoulutusmoduuli("" + koulutusDoc.getFieldValue(KOULUTUSMODUULI_OID));
        koulutus.setKoulutusmoduuliToteutus("" + koulutusDoc.getFieldValue(OID));
        koulutus.setKoulutustyyppi(createKoulutustyyppi(koulutusDoc));
        if(koulutus.getKoulutustyyppi()!=null){
        if (koulutus.getKoulutustyyppi().equals(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS)) {
            koulutus.setKoulutusohjelmakoodi(IndexDataUtils.createKoodiTyyppi(KOULUTUSOHJELMA_URI, KOULUTUSOHJELMA_FI, KOULUTUSOHJELMA_SV, KOULUTUSOHJELMA_EN, koulutusDoc));
            koulutus.setKoulutuslaji(getKoulutuslaji(koulutusDoc));
        } else if (koulutus.getKoulutustyyppi().equals(KoulutusasteTyyppi.LUKIOKOULUTUS)) {
            koulutus.setLukiolinjakoodi(IndexDataUtils.createKoodiTyyppi(KOULUTUSOHJELMA_URI, KOULUTUSOHJELMA_FI, KOULUTUSOHJELMA_SV, KOULUTUSOHJELMA_EN, koulutusDoc));
        }
        }
        copyKoulutusNimi(koulutus, koulutusDoc);
        koulutus.setTila(IndexDataUtils.createTila(koulutusDoc));
        koulutus.setTutkintonimike(IndexDataUtils.createKoodiTyyppi(TUTKINTONIMIKE_URI, TUTKINTONIMIKE_FI, TUTKINTONIMIKE_SV, TUTKINTONIMIKE_EN, koulutusDoc));
        koulutus.setTarjoaja(IndexDataUtils.createTarjoaja(koulutusDoc, orgs));
        if (koulutusDoc.containsKey(KAUSI_KOODI)) {
            koulutus.setKoulutuksenAlkamiskausiUri("" + koulutusDoc.getFieldValue(KAUSI_KOODI));
        }
        if (koulutusDoc.containsKey(VUOSI_KOODI)) {
            koulutus.setKoulutuksenAlkamisVuosi(new Integer((String)koulutusDoc.getFieldValue(VUOSI_KOODI)));
        }

        if(koulutus.getTarjoaja().getNimi()==null) {
            return null;
        }
       
        if (koulutusDoc.getFieldValue(POHJAKOULUTUSVAATIMUS_URI) != null) {
            koulutus.setPohjakoulutusVaatimus("" + koulutusDoc.getFieldValue(POHJAKOULUTUSVAATIMUS_URI));
        }
        return koulutus;
    }

    private String getKoulutuslaji(SolrDocument doc) {
       try {
       return (String)doc.getFirstValue(KOULUTUSLAJI_URIS);
       } catch (Exception exp) {
           return null;
       }
    }
    
    private KoulutusasteTyyppi createKoulutustyyppi(SolrDocument koulutusDoc) {
        if(koulutusDoc.getFieldValue(KOULUTUSTYYPPI)!=null){
            return KoulutusasteTyyppi.fromValue("" + koulutusDoc.getFieldValue(KOULUTUSTYYPPI));
        } else return null;
    }

    private void copyKoulutusNimi(KoulutusPerustieto koulutus, SolrDocument koulutusDoc) {
        asetaNimi(koulutus.getNimi(), koulutusDoc, "fi", KOULUTUSOHJELMA_FI);
        asetaNimi(koulutus.getNimi(), koulutusDoc, "sv", KOULUTUSOHJELMA_SV);
        asetaNimi(koulutus.getNimi(), koulutusDoc, "en", KOULUTUSOHJELMA_EN);
    }
        
    
    /**
     * Asettaa yhden nimen
     * @param nimiMap
     * @param hakukohdeDoc
     * @param targetLanguage (fi,sv,en)
     * @param fieldName solr dokumentin kentän nimi josta data otetaan.
     */
    private void asetaNimi(Map<String, String> nimiMap,
            SolrDocument hakukohdeDoc, String targetLanguage, String fieldName) {
        if (hakukohdeDoc.getFieldValue(fieldName) != null) {
            nimiMap.put(targetLanguage,
                    hakukohdeDoc.getFieldValue(fieldName).toString());
        }
    }

   
    
}
