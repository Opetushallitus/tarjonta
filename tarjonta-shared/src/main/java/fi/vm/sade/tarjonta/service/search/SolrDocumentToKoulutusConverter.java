package fi.vm.sade.tarjonta.service.search;

import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.KAUSI_FI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.KAUSI_SV;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.KAUSI_EN;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.*;

import java.util.*;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

import org.apache.solr.common.util.Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrDocumentToKoulutusConverter {

    private static final Logger LOG = LoggerFactory.getLogger(SolrDocumentToKoulutusConverter.class);

    public KoulutuksetVastaus convertSolrToKoulutuksetVastaus(SolrDocumentList solrKomotoList, Map<String,
          OrganisaatioPerustieto> orgs, String defaultTarjoaja) {

        KoulutuksetVastaus vastaus = new KoulutuksetVastaus();
        for (int i = 0; i < solrKomotoList.size(); ++i) {
            SolrDocument curDoc = solrKomotoList.get(i);
            KoulutusPerustieto koulutus = convertKoulutus(curDoc, orgs, defaultTarjoaja);
            if (koulutus != null) {
                vastaus.getKoulutukset().add(koulutus);
            }
        }
        return vastaus;
    }

    private KoulutusPerustieto convertKoulutus(SolrDocument koulutusDoc, Map<String, OrganisaatioPerustieto> orgs, String defaultTarjoaja) {
        KoulutusPerustieto perustieto = new KoulutusPerustieto();
        perustieto.setKomotoOid("" + koulutusDoc.getFieldValue(OID));
        perustieto.setKoulutusKoodi(IndexDataUtils.createKoodistoKoodi(KOULUTUSKOODI_URI, KOULUTUSKOODI_FI, KOULUTUSKOODI_SV, KOULUTUSKOODI_EN, koulutusDoc));
        perustieto.setKoulutusmoduuli("" + koulutusDoc.getFieldValue(KOULUTUSMODUULI_OID));
        perustieto.setKoulutusmoduuliToteutus("" + koulutusDoc.getFieldValue(OID));
        perustieto.setKoulutusasteTyyppi(createKoulutustyyppi(koulutusDoc));
        perustieto.setKoulutustyyppi("" + koulutusDoc.getFieldValue(KOULUTUSTYYPPI_URI));
        perustieto.setOpetuskielet((ArrayList<String>) koulutusDoc.getFieldValue(OPETUSKIELI_URIS));
        if (koulutusDoc.getFieldValue(TOTEUTUSTYYPPI_ENUM) != null) {
            perustieto.setToteutustyyppi(ToteutustyyppiEnum.valueOf("" + koulutusDoc.getFieldValue(TOTEUTUSTYYPPI_ENUM)));
        }

        perustieto.setKoulutusohjelma(IndexDataUtils.createKoodistoKoodi(KOULUTUSOHJELMA_URI, KOULUTUSOHJELMA_FI, KOULUTUSOHJELMA_SV, KOULUTUSOHJELMA_EN, koulutusDoc));
        perustieto.setKoulutuslaji(IndexDataUtils.createKoodistoKoodi(KOULUTUSLAJI_URIS, KOULUTUSLAJI_FI, KOULUTUSLAJI_SV, KOULUTUSLAJI_EN, koulutusDoc));
        copyKoulutusNimi(perustieto, koulutusDoc);
        perustieto.setTila(IndexDataUtils.createTila(koulutusDoc));
        perustieto.setTutkintonimike(IndexDataUtils.createKoodistoKoodi(TUTKINTONIMIKE_URI, TUTKINTONIMIKE_FI, TUTKINTONIMIKE_SV, TUTKINTONIMIKE_EN, koulutusDoc));
        perustieto.setKoulutuksenTarjoajaKomoto((String) koulutusDoc.getFieldValue(KOULUTUKSEN_TARJOAJA_KOMOTO));
        perustieto.setTarjoaja(IndexDataUtils.createTarjoaja(koulutusDoc, orgs, defaultTarjoaja));
        perustieto.setParentKomo((String) koulutusDoc.getFieldValue(PARENT_KOULUTUSMODUULI_OID));
        perustieto.setSiblingKomotos((ArrayList<String>) koulutusDoc.getFieldValue(SIBLING_KOMOTOS));

        // KJOH-778 monta tarjoajaa
        if (koulutusDoc.getFieldValue(ORG_OID) != null) {
            perustieto.setTarjoajat((ArrayList<String>) koulutusDoc.getFieldValue(ORG_OID));
        }

        if (koulutusDoc.containsKey(KAUSI_URI)) {
            perustieto.setKoulutuksenAlkamiskausiUri(IndexDataUtils.createKoodistoKoodi(KAUSI_URI, KAUSI_FI, KAUSI_SV, KAUSI_EN, koulutusDoc));
        }
        if (koulutusDoc.containsKey(VUOSI_KOODI)) {
            try {
                perustieto.setKoulutuksenAlkamisVuosi(new Integer((String) koulutusDoc.getFieldValue(VUOSI_KOODI)));
            } catch (Exception e) {
                throw new NumberFormatException("For input string: '" + koulutusDoc.getFieldValue(VUOSI_KOODI) + "' in KOMOTO " + koulutusDoc.getFieldValue(OID));
            }
        }

        if (perustieto.getTarjoaja().getNimi() == null) {
            return null;
        }

        if (koulutusDoc.getFieldValue(POHJAKOULUTUSVAATIMUS_URI) != null) {
            perustieto.setPohjakoulutusvaatimus(IndexDataUtils.createKoodistoKoodi(POHJAKOULUTUSVAATIMUS_URI, POHJAKOULUTUSVAATIMUS_FI, POHJAKOULUTUSVAATIMUS_SV, POHJAKOULUTUSVAATIMUS_EN, koulutusDoc));
        }

        if (koulutusDoc.getFieldValue(KOULUTUSMODUULITYYPPI_ENUM) != null) {
            perustieto.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.valueOf("" + koulutusDoc.getFieldValue(KOULUTUSMODUULITYYPPI_ENUM))); 
        }

        createKoulutuksenAlkamisPvm(koulutusDoc, perustieto);

        return perustieto;
    }

    /**
     * Extract koulutus alkamis pvm (min/max) information from solr document.
     *
     * @param koulutusDoc
     * @param perustieto
     */
    private void createKoulutuksenAlkamisPvm(SolrDocument koulutusDoc, KoulutusPerustieto perustieto) {
        perustieto.setKoulutuksenAlkamisPvmMin((Date) koulutusDoc.get(SolrFields.Koulutus.KOULUTUALKAMISPVM_MIN));
        perustieto.setKoulutuksenAlkamisPvmMax((Date) koulutusDoc.get(SolrFields.Koulutus.KOULUTUALKAMISPVM_MAX));
    }

    private KoulutusasteTyyppi createKoulutustyyppi(SolrDocument koulutusDoc) {
        if (koulutusDoc.getFieldValue(KOULUTUSASTETYYPPI_ENUM) != null) {
            try {
                //1st, also catch the conversion failed exception.
                return KoulutusasteTyyppi.valueOf("" + koulutusDoc.getFieldValue(KOULUTUSASTETYYPPI_ENUM));
            } catch (IllegalArgumentException e) {
                //2nd try. Throw an exception if not found .
                return KoulutusasteTyyppi.fromValue("" + koulutusDoc.getFieldValue(KOULUTUSASTETYYPPI_ENUM));
            }
        } else {
            return null;
        }
    }

    private void copyKoulutusNimi(KoulutusPerustieto koulutus, SolrDocument koulutusDoc) {
        if (koulutusDoc.getFieldValues(NIMET) != null) {
            ArrayList<Object> nimet = new ArrayList<Object>(koulutusDoc.getFieldValues(NIMET));
            ArrayList<Object> nimienKielet = new ArrayList<Object>(koulutusDoc.getFieldValues(NIMIEN_KIELET));
            for (int i = 0; i < nimet.size(); i++) {
                asetaNimiArvosta(koulutus.getNimi(), koulutusDoc, (String) nimienKielet.get(i), (String) nimet.get(i));
            }
        } else if (koulutusDoc.getFieldValues(KOULUTUSOHJELMA_URI) != null) { //no name set
            asetaNimi(koulutus.getNimi(), koulutusDoc, Nimi.FI, KOULUTUSOHJELMA_FI);
            asetaNimi(koulutus.getNimi(), koulutusDoc, Nimi.SV, KOULUTUSOHJELMA_SV);
            asetaNimi(koulutus.getNimi(), koulutusDoc, Nimi.EN, KOULUTUSOHJELMA_EN);
        } else {
            asetaNimi(koulutus.getNimi(), koulutusDoc, Nimi.FI, KOULUTUSKOODI_FI);
            asetaNimi(koulutus.getNimi(), koulutusDoc, Nimi.SV, KOULUTUSKOODI_SV);
            asetaNimi(koulutus.getNimi(), koulutusDoc, Nimi.EN, KOULUTUSKOODI_EN);
        }
    }

    /**
     * Asettaa yhden nimen
     *
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

    /**
     * Asettaa yhden nimen
     *
     * @param nimiMap
     * @param hakukohdeDoc
     * @param targetLanguage (fi,sv,en)
     * @param fieldName solr dokumentin kentän nimi josta data otetaan.
     */
    private void asetaNimiArvosta(Map<String, String> nimiMap,
            SolrDocument hakukohdeDoc, String targetLanguage, String value) {
        if (value != null) {
            nimiMap.put(targetLanguage, value);
        }
    }

}
