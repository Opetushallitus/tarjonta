package fi.vm.sade.tarjonta.service.search;

import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.*;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.NIMET;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.NIMIEN_KIELET;

public class SolrDocumentToHakukohdeConverter {

    public HakukohteetVastaus convertSolrToHakukohteetVastaus(SolrDocumentList solrHakukohdeList, Map<String,
            OrganisaatioPerustieto> orgResponse, String defaultTarjoaja) {

        HakukohteetVastaus vastaus = new HakukohteetVastaus();
        for (int i = 0; i < solrHakukohdeList.size(); ++i) {
            SolrDocument hakukohdeDoc = solrHakukohdeList.get(i);
            HakukohdePerustieto hakukohde = convertHakukohde(hakukohdeDoc, orgResponse, defaultTarjoaja);
            if (hakukohde != null) {
                vastaus.getHakukohteet().add(hakukohde);
            }
        }

        return vastaus;
    }

    private HakukohdePerustieto convertHakukohde(SolrDocument hakukohdeDoc,
                                                 Map<String, OrganisaatioPerustieto> orgResponse, String defaultTarjoaja) {
        HakukohdePerustieto hakukohde = new HakukohdePerustieto();

        if (hakukohdeDoc.getFieldValue(ALOITUSPAIKAT) != null) {
            hakukohde.setAloituspaikat(Integer.parseInt((String) hakukohdeDoc.getFieldValue(ALOITUSPAIKAT)));
        }
        hakukohde.setHakuAlkamisPvm(parseDate(hakukohdeDoc, HAUN_ALKAMISPVM));
        hakukohde.setHakuPaattymisPvm(parseDate(hakukohdeDoc, HAUN_PAATTYMISPVM));

        hakukohde.setHakutapaKoodi(IndexDataUtils.createKoodistoKoodi(HAKUTAPA_URI, HAKUTAPA_FI, HAKUTAPA_SV, HAKUTAPA_EN, hakukohdeDoc));

        hakukohde.setKoodistoNimi("" + hakukohdeDoc.getFieldValue(HAKUKOHTEEN_NIMI_URI));

        hakukohde.setKoulutusastetyyppi(createKoulutusastetyyppi(hakukohdeDoc));

        hakukohde.setKoulutuksenAlkamiskausi(IndexDataUtils.createKoodistoKoodi(KAUSI_URI, KAUSI_FI, KAUSI_SV, KAUSI_EN, hakukohdeDoc));

        if (hakukohdeDoc.getFieldValue(VUOSI_KOODI) != null) {
            hakukohde.setKoulutuksenAlkamisvuosi(Integer.parseInt((String) hakukohdeDoc.getFieldValue(VUOSI_KOODI)));
        }

        copyHakukohdeNimi(hakukohde, hakukohdeDoc);
        copyAloituspaikatKuvaus(hakukohde, hakukohdeDoc);
        copyRyhmaliitokset(hakukohde, hakukohdeDoc);

        hakukohde.setPohjakoulutusvaatimus(IndexDataUtils.createKoodistoKoodi(POHJAKOULUTUSVAATIMUS_URI, POHJAKOULUTUSVAATIMUS_FI, POHJAKOULUTUSVAATIMUS_SV, POHJAKOULUTUSVAATIMUS_EN, hakukohdeDoc));
        hakukohde.setKoulutuslaji(IndexDataUtils.createKoodistoKoodi(KOULUTUSLAJI_URI, KOULUTUSLAJI_FI, KOULUTUSLAJI_SV, KOULUTUSLAJI_EN, hakukohdeDoc));
        if (hakukohdeDoc.getFieldValue(HAUN_OID) != null) {
            hakukohde.setHakuOid(hakukohdeDoc.getFieldValue(HAUN_OID).toString());
        }
        hakukohde.setOid("" + hakukohdeDoc.getFieldValue(OID));
        hakukohde.setHakutyyppiUri("" + hakukohdeDoc.getFieldValue(HAKUTYYPPI_URI));
        hakukohde.setTila(IndexDataUtils.createTila(hakukohdeDoc));

        if (hakukohdeDoc.getFieldValue(SolrFields.Hakukohde.ORG_OID) != null) {

            ArrayList<String> orgOidCandidates = (ArrayList<String>) hakukohdeDoc.getFieldValue(SolrFields.Hakukohde.ORG_OID);

            // If query param for organization -> try to find matching organization in Solr doc
            if (defaultTarjoaja != null) {
                for (String tmpOrgOid : orgOidCandidates) {

                    // Need to check whole organization path
                    OrganisaatioPerustieto organisaatioPerustieto = orgResponse.get(tmpOrgOid);
                    ArrayList<String> path = new ArrayList<String>();
                    path.add(tmpOrgOid);
                    path.addAll(Arrays.asList(organisaatioPerustieto.getParentOidPath().split("/")));

                    if (path.indexOf(defaultTarjoaja) != -1) {
                        hakukohde.setTarjoajaOid(tmpOrgOid);
                        break;
                    }
                }
            }

            // If no query param or invalid query param -> use first matching tarjoaja
            if (hakukohde.getTarjoajaOid() == null) {
                hakukohde.setTarjoajaOid(orgOidCandidates.get(0));
            }
        }

        // KJOH-778 fallback
        else {
            hakukohde.setTarjoajaOid((String) hakukohdeDoc.getFieldValue("orgoid_s"));
        }

        copyTarjoajaNimi(hakukohde, orgResponse.get(hakukohde.getTarjoajaOid()));
        if (hakukohde.getTarjoajaOid() == null) {
            return null;
        }
        hakukohde.setKoulutusastetyyppi(createKoulutusastetyyppi(hakukohdeDoc));
        if (hakukohdeDoc.getFieldValue(KOULUTUSMODUULITYYPPI_ENUM) != null) {
            hakukohde.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.fromValue("" + hakukohdeDoc.getFieldValue(KOULUTUSMODUULITYYPPI_ENUM)));
        }
        return hakukohde;
    }

    private void copyRyhmaliitokset(HakukohdePerustieto hakukohde, SolrDocument hakukohdeDoc) {
        if (hakukohdeDoc.getFieldValues(RYHMA_OIDS) != null) {
            List<Object> oids = new ArrayList<Object>(hakukohdeDoc.getFieldValues(RYHMA_OIDS));
            List<Object> prioriteetit = new ArrayList<Object>(hakukohdeDoc.getFieldValues(RYHMA_PRIORITEETIT));

            for (int i = 0; i < oids.size(); i++) {
                SolrRyhmaliitos ryhmaliitos = new SolrRyhmaliitos();
                ryhmaliitos.setRyhmaOid((String) oids.get(i));
                if (!SolrFields.RYHMA_PRIORITEETTI_EI_MAARITELTY.equals(prioriteetit.get(i))) {
                    ryhmaliitos.setPrioriteetti(Integer.valueOf((String) prioriteetit.get(i)));
                }
                hakukohde.addRyhmaliitos(ryhmaliitos);
            }
        }
    }

    private void copyAloituspaikatKuvaus(HakukohdePerustieto hakukohde, SolrDocument hakukohdeDoc) {
        if (hakukohdeDoc.getFieldValues(ALOITUSPAIKAT_KUVAUKSET) != null) {
            ArrayList<Object> kuvaukset = new ArrayList<Object>(hakukohdeDoc.getFieldValues(ALOITUSPAIKAT_KUVAUKSET));
            ArrayList<Object> kuvauksienKielet = new ArrayList<Object>(hakukohdeDoc.getFieldValues(ALOITUSPAIKAT_KIELET));
            for (int i = 0; i < kuvaukset.size(); i++) {
                addMonikielinenTekstiEntry(hakukohde.getAloituspaikatKuvaukset(), (String) kuvauksienKielet.get(i), (String) kuvaukset.get(i));
            }
        }
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
        if (hakukohdeDoc.getFieldValues(NIMET) != null) {
            ArrayList<Object> nimet = new ArrayList<Object>(hakukohdeDoc.getFieldValues(NIMET));
            ArrayList<Object> nimienKielet = new ArrayList<Object>(hakukohdeDoc.getFieldValues(NIMIEN_KIELET));
            for (int i = 0; i < nimet.size(); i++) {
                addMonikielinenTekstiEntry(hakukohde.getNimi(), (String) nimienKielet.get(i), (String) nimet.get(i));
            }
        } else {
            asetaNimi(hakukohde.getNimi(), hakukohdeDoc, "fi", HAKUKOHTEEN_NIMI_FI);
            asetaNimi(hakukohde.getNimi(), hakukohdeDoc, "sv", HAKUKOHTEEN_NIMI_SV);
            asetaNimi(hakukohde.getNimi(), hakukohdeDoc, "en", HAKUKOHTEEN_NIMI_EN);
        }
    }

    private void copyTarjoajaNimi(HakukohdePerustieto hakukohde,
                                  OrganisaatioPerustieto organisaatio) {
        if (organisaatio != null) {
            for (Entry<String, String> nimi : organisaatio.getNimi().entrySet()) {
                hakukohde.setTarjoajaNimi(nimi.getKey(), nimi.getValue());
            }
        }
    }

    private KoulutusasteTyyppi createKoulutusastetyyppi(SolrDocument hakukohdeDoc) {
        if (hakukohdeDoc.getFieldValue(KOULUTUSASTETYYPPI) != null) {
            try {
                //1st, also catch the conversion failed exception.
                return KoulutusasteTyyppi.valueOf("" + hakukohdeDoc.getFieldValue(KOULUTUSASTETYYPPI));
            } catch (IllegalArgumentException e) {
                //2nd try. Throw an exception if not found .
                return KoulutusasteTyyppi.fromValue("" + hakukohdeDoc.getFieldValue(KOULUTUSASTETYYPPI));
            }

        } else {
            return null;
        }
    }

    /**
     * Asettaa yhden nimen
     *
     * @param nimiMap
     * @param hakukohdeDoc
     * @param targetLanguage (fi,sv,en)
     * @param fieldName      solr dokumentin kentän nimi josta data otetaan.
     */
    private void asetaNimi(Map<String, String> nimiMap,
                           SolrDocument hakukohdeDoc, String targetLanguage, String fieldName) {

        if (hakukohdeDoc.getFieldValue(fieldName) != null) {
            nimiMap.put(targetLanguage,
                    hakukohdeDoc.getFieldValue(fieldName).toString());
        }
    }

    private void addMonikielinenTekstiEntry(Map<String, String> monikielinenTekstiMap, String language, String value) {
        if (value != null) {
            monikielinenTekstiMap.put(language, value);
        }
    }
}
