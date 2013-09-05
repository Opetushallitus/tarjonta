package fi.vm.sade.tarjonta.service.search;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus.HakukohdeTulos;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi.Teksti;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.*;

public class SolrDocumentToHakukohdeConverter {
    
    public HakukohteetVastaus convertSolrToHakukohteetVastaus(SolrDocumentList solrHakukohdeList, Map<String, OrganisaatioPerustieto> orgResponse) {
        HakukohteetVastaus vastaus = new HakukohteetVastaus();
        for (int i = 0 ; i < solrHakukohdeList.size(); ++i) {
            SolrDocument hakukohdeDoc = solrHakukohdeList.get(i);
            HakukohdeTulos tulos = convertHakukohde(hakukohdeDoc, orgResponse);
            if(tulos!=null) {
                vastaus.getHakukohdeTulos().add(tulos);
            }
        }
        
        return vastaus;
    }

    private HakukohdeTulos convertHakukohde(SolrDocument hakukohdeDoc,
            Map<String, OrganisaatioPerustieto> orgResponse) {
        HakukohdeTulos vastaus = new HakukohdeTulos();
        HakukohdeListaus hakukohde = new HakukohdeListaus();
        hakukohde.setAloituspaikat("" + hakukohdeDoc.getFieldValue(ALOITUSPAIKAT));
        hakukohde.setHakuAlkamisPvm(parseDate(hakukohdeDoc, HAUN_ALKAMISPVM));
        hakukohde.setHakuPaattymisPvm(parseDate(hakukohdeDoc, HAUN_PAATTYMISPVM));
        hakukohde.setHakutapaKoodi(IndexDataUtils.createKoodiTyyppi(HAKUTAPA_URI, HAKUTAPA_FI, HAKUTAPA_SV, HAKUTAPA_EN, hakukohdeDoc));
        hakukohde.setKoodistoNimi("" + hakukohdeDoc.getFieldValue(HAKUKOHTEEN_NIMI_URI));
        hakukohde.setKoulutuksenAlkamiskausiUri("" + hakukohdeDoc.getFieldValue(KAUSI_FI));
        hakukohde.setKoulutuksenAlkamisvuosi("" + hakukohdeDoc.getFieldValue(VUOSI_KOODI));
        hakukohde.setNimi(createHakukohdeNimi(hakukohdeDoc));
        hakukohde.setHakukohteenKoulutuslaji(createHakukohteenKoulutuslaji(hakukohdeDoc));
        hakukohde.setOid("" + hakukohdeDoc.getFieldValue(OID));
        hakukohde.setTila(IndexDataUtils.createTila(hakukohdeDoc));
        hakukohde.setTarjoaja(IndexDataUtils.createTarjoaja(hakukohdeDoc, orgResponse));
        if(hakukohde.getTarjoaja().getNimi()==null) {
            return null;
        }
        vastaus.setHakukohde(hakukohde);
        return vastaus;
    }
    
    private MonikielinenTekstiTyyppi createHakukohteenKoulutuslaji(
            SolrDocument hakukohdeDoc) {
        MonikielinenTekstiTyyppi nimi = new MonikielinenTekstiTyyppi();
        if (hakukohdeDoc.getFieldValue(KOULUTUSLAJI_FI) != null) {
            Teksti nimiFi = new Teksti();
            nimiFi.setKieliKoodi("fi");
            nimiFi.setValue(hakukohdeDoc.getFieldValue(KOULUTUSLAJI_FI)
                    .toString());
            nimi.getTeksti().add(nimiFi);
        }
        if (hakukohdeDoc.getFieldValue(KOULUTUSLAJI_SV) != null) {
            Teksti nimiSv = new Teksti();
            nimiSv.setKieliKoodi("sv");
            nimiSv.setValue(hakukohdeDoc.getFieldValue(KOULUTUSLAJI_SV)
                    .toString());
            nimi.getTeksti().add(nimiSv);
        }
        if (hakukohdeDoc.getFieldValue(KOULUTUSLAJI_EN) != null) {
            Teksti nimiEn = new Teksti();
            nimiEn.setKieliKoodi("en");
            nimiEn.setValue(hakukohdeDoc.getFieldValue(KOULUTUSLAJI_EN)
                    .toString());
            nimi.getTeksti().add(nimiEn);
        }
        return nimi;
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
