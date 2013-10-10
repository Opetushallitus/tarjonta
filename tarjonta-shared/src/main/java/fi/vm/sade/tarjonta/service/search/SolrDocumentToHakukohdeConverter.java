package fi.vm.sade.tarjonta.service.search;

import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.ALOITUSPAIKAT;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.HAKUKOHTEEN_NIMI_EN;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.HAKUKOHTEEN_NIMI_FI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.HAKUKOHTEEN_NIMI_SV;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.HAKUKOHTEEN_NIMI_URI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.HAKUTAPA_EN;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.HAKUTAPA_FI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.HAKUTAPA_SV;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.HAKUTAPA_URI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.HAKUTYYPPI_URI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.POHJAKOULUTUSVAATIMUS_URI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.POHJAKOULUTUSVAATIMUS_FI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.POHJAKOULUTUSVAATIMUS_SV;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.POHJAKOULUTUSVAATIMUS_EN;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.HAUN_ALKAMISPVM;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.HAUN_PAATTYMISPVM;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.KAUSI_FI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.KAUSI_URI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.KAUSI_SV;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.KAUSI_EN;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.KOULUTUSLAJI_EN;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.KOULUTUSLAJI_FI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.KOULUTUSLAJI_SV;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.OID;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.VUOSI_KOODI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.KOULUTUSLAJI_URI;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;

public class SolrDocumentToHakukohdeConverter {
    
    public HakukohteetVastaus convertSolrToHakukohteetVastaus(SolrDocumentList solrHakukohdeList, Map<String, OrganisaatioPerustieto> orgResponse) {
        HakukohteetVastaus vastaus = new HakukohteetVastaus();
        for (int i = 0 ; i < solrHakukohdeList.size(); ++i) {
            SolrDocument hakukohdeDoc = solrHakukohdeList.get(i);
            HakukohdePerustieto hakukohde = convertHakukohde(hakukohdeDoc, orgResponse);
            if(hakukohde!=null) {
                vastaus.getHakukohteet().add(hakukohde);
            }
        }
        
        return vastaus;
    }

    private HakukohdePerustieto convertHakukohde(SolrDocument hakukohdeDoc,
            Map<String, OrganisaatioPerustieto> orgResponse) {
        HakukohdePerustieto hakukohde = new HakukohdePerustieto();
        
        if(hakukohdeDoc.getFieldValue(ALOITUSPAIKAT)!=null) {
            hakukohde.setAloituspaikat(Integer.parseInt((String)hakukohdeDoc.getFieldValue(ALOITUSPAIKAT)));
        }
        hakukohde.setHakuAlkamisPvm(parseDate(hakukohdeDoc, HAUN_ALKAMISPVM));
        hakukohde.setHakuPaattymisPvm(parseDate(hakukohdeDoc, HAUN_PAATTYMISPVM));
        
        hakukohde.setHakutapaKoodi(IndexDataUtils.createKoodistoKoodi(HAKUTAPA_URI, HAKUTAPA_FI, HAKUTAPA_SV, HAKUTAPA_EN, hakukohdeDoc));

        hakukohde.setKoodistoNimi("" + hakukohdeDoc.getFieldValue(HAKUKOHTEEN_NIMI_URI));

        hakukohde.setKoulutuksenAlkamiskausiUri(IndexDataUtils.createKoodistoKoodi(KAUSI_URI, KAUSI_FI, KAUSI_SV, KAUSI_EN, hakukohdeDoc));

        if(hakukohdeDoc.getFieldValue(VUOSI_KOODI)!=null) {
            hakukohde.setKoulutuksenAlkamisvuosi(Integer.parseInt((String)hakukohdeDoc.getFieldValue(VUOSI_KOODI)));
        }
        copyHakukohdeNimi(hakukohde, hakukohdeDoc);
        hakukohde.setPohjakoulutusvaatimus(IndexDataUtils.createKoodistoKoodi(POHJAKOULUTUSVAATIMUS_URI, POHJAKOULUTUSVAATIMUS_FI, POHJAKOULUTUSVAATIMUS_SV, POHJAKOULUTUSVAATIMUS_EN, hakukohdeDoc));
        hakukohde.setKoulutuslaji(IndexDataUtils.createKoodistoKoodi(KOULUTUSLAJI_URI,  KOULUTUSLAJI_FI,  KOULUTUSLAJI_SV,  KOULUTUSLAJI_EN, hakukohdeDoc));
        
        hakukohde.setOid("" + hakukohdeDoc.getFieldValue(OID));
        hakukohde.setHakutyyppiUri("" + hakukohdeDoc.getFieldValue(HAKUTYYPPI_URI));
        hakukohde.setTila(IndexDataUtils.createTila(hakukohdeDoc));
        hakukohde.setTarjoajaOid((String)hakukohdeDoc.getFieldValue(SolrFields.Hakukohde.ORG_OID));
        copyTarjoajaNimi(hakukohde, orgResponse.get(hakukohde.getTarjoajaOid()));
        if(hakukohde.getTarjoajaOid()==null) {
            return null;
        }
        return hakukohde;
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

    private void copyHakukohdeNimi(HakukohdePerustieto hakukohde, SolrDocument hakukohdeDoc) {
        asetaNimi(hakukohde.getNimi(), hakukohdeDoc, "fi", HAKUKOHTEEN_NIMI_FI);
        asetaNimi(hakukohde.getNimi(), hakukohdeDoc, "sv", HAKUKOHTEEN_NIMI_SV);
        asetaNimi(hakukohde.getNimi(), hakukohdeDoc, "en", HAKUKOHTEEN_NIMI_EN);
    }

    private void copyTarjoajaNimi(HakukohdePerustieto hakukohde,
            OrganisaatioPerustieto organisaatio) {
        if (organisaatio != null) {
            for (Entry<String, String> nimi : organisaatio.getNimi().entrySet()) {
                hakukohde.setTarjoajaNimi(nimi.getKey(), nimi.getValue());
            }
        }
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
