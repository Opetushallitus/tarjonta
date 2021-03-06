/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.service.search;

import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.*;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.ORG_OID;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.TILA;

/**
 * @author Markus
 */
public class IndexDataUtils {

    private static final Logger log = LoggerFactory.getLogger(IndexDataUtils.class);

    public static final String KOODI_URI_AND_VERSION_SEPARATOR = "#";

    private static final String KEVAT = "kevat";
    private static final String SYKSY = "syksy";

    public static final String KEVAT_URI = "kausi_k#1";
    public static final String SYKSY_URI = "kausi_s#1";

    @Autowired
    TarjontaKoodistoHelper koodistoHelper;

    /**
     * Extract components from the versioned koodi uri.
     *
     * @param koodiUriWithVersion
     * @return
     */
    public static String[] splitKoodiURI(final String koodiUriWithVersion) {
        if (koodiUriWithVersion == null) {
            throw new IllegalArgumentException("Koodi uri with version string object cannot be null.");
        }

        String[] result = new String[2];

        int index = koodiUriWithVersion.lastIndexOf(KOODI_URI_AND_VERSION_SEPARATOR);
        if (index > 0) {
            result[0] = koodiUriWithVersion.substring(0, index);
            result[1] = koodiUriWithVersion.substring(index + KOODI_URI_AND_VERSION_SEPARATOR.length());
        } else {
            result[0] = koodiUriWithVersion;
            result[1] = "-1";
        }

        return result;
    }

    /**
     * Get koodi metadata by locale with language fallback to FI
     *
     * @param koodiType
     * @param locale
     * @return
     */
    public static KoodiMetadataType getKoodiMetadataForLanguage(KoodiType koodiType, Locale locale) {
        KoodiMetadataType kmdt = KoodistoHelper.getKoodiMetadataForLanguage(koodiType, KoodistoHelper.getKieliForLocale(locale));
        if (kmdt == null || (kmdt.getNimi() == null || kmdt.getNimi().length() == 0)) {
            // Try finnish if current locale is not found
            kmdt = KoodistoHelper.getKoodiMetadataForLanguage(koodiType, KieliType.FI);
        }

        return kmdt;
    }

    public static String parseKausi(Date koulutuksenAlkamisPvm) {
        if (koulutuksenAlkamisPvm == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(koulutuksenAlkamisPvm);
        return cal.get(Calendar.MONTH) < 7 ? KEVAT : SYKSY;
    }

    public static String parseKausiKoodi(Date koulutuksenAlkamisPvm) {
        if (koulutuksenAlkamisPvm == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(koulutuksenAlkamisPvm);
        return cal.get(Calendar.MONTH) < 7 ? KEVAT_URI : SYKSY_URI;
    }

    public static Integer parseYearInt(Date koulutuksenAlkamisPvm) {
        if (koulutuksenAlkamisPvm == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(koulutuksenAlkamisPvm);
        return cal.get(Calendar.YEAR);
    }

    public static Date getDateFromYearAndKausi(Integer year, String kausi) {
        Integer month = 1; // kevät by default

        if (kausi != null && SYKSY_URI.split("#")[0].equals(kausi.split("#")[0])) {
            month = 8;
        }

        return new DateTime().withYear(year).withMonthOfYear(month).withDayOfMonth(1).toDate();
    }

    public static String parseYear(Date koulutuksenAlkamisPvm) {
        return "" + parseYearInt(koulutuksenAlkamisPvm);
    }

    public static KoodistoKoodi createKoodistoKoodi(String koodiUri,
                                                    String koodiFi, String koodiSv,
                                                    String koodiEn, SolrDocument koulutusDoc) {
        final Object valueO = koulutusDoc.getFieldValue(koodiUri);
        String value;
        if (valueO instanceof List) {
            value = (String) ((List) valueO).get(0);
        } else {
            value = (String) valueO;
        }
        final KoodistoKoodi koodi = new KoodistoKoodi(value);
        asetaArvoJosEiNull(koodi.getNimi(), Nimi.FI, (String) koulutusDoc.getFieldValue(koodiFi));
        asetaArvoJosEiNull(koodi.getNimi(), Nimi.SV, (String) koulutusDoc.getFieldValue(koodiSv));
        asetaArvoJosEiNull(koodi.getNimi(), Nimi.EN, (String) koulutusDoc.getFieldValue(koodiEn));
        return koodi;
    }

    private static void asetaArvoJosEiNull(Nimi nimi, String lang, String value) {
        if (value != null) {
            nimi.put(lang, value);
        }
    }

    public static TarjontaTila createTila(SolrDocument doc) {
        String tila = "" + doc.getFieldValue(TILA);
        if (tila.isEmpty()) {
            return null;
        }
        try {
            return TarjontaTila.valueOf(tila);
        } catch (Exception e) {
            log.debug("Ignored unknown state: " + tila, e);
            return null;
        }
    }

    public static Tarjoaja createTarjoaja(SolrDocument koulutusDoc,
                                          Map<String, OrganisaatioPerustieto> orgResponse, String defaultTarjoaja) {
        final Tarjoaja tarjoaja = new Tarjoaja();

        if (koulutusDoc.getFieldValue(ORG_OID) != null) {
            ArrayList<String> orgOidCandidates = (ArrayList<String>) koulutusDoc.getFieldValue(ORG_OID);

            // If query param for organization -> try to find matching organization in Solr doc
            if (defaultTarjoaja != null) {

                for (String tmpOrgOid : orgOidCandidates) {
                    try {
                        // Need to check whole organization path
                        OrganisaatioPerustieto organisaatioPerustieto = orgResponse.get(tmpOrgOid);
                        ArrayList<String> path = new ArrayList<String>();
                        path.add(tmpOrgOid);
                        path.addAll(Arrays.asList(organisaatioPerustieto.getParentOidPath().split("/")));

                        if (path.indexOf(defaultTarjoaja) != -1) {
                            tarjoaja.setOid(tmpOrgOid);
                            break;
                        }
                    } catch(Exception e) {
                        log.error("organisation:" + tmpOrgOid + " missing either perustieto or parentoidpath.");
                        throw e;
                    }
                }
            }

            // If no query param or invalid query param -> use first matching tarjoaja
            if (tarjoaja.getOid() == null) {
                for (String tmpOrgOid : orgOidCandidates) {
                    if (orgResponse.get(tmpOrgOid) != null) {
                        tarjoaja.setOid(tmpOrgOid);
                        break;
                    }
                }
            }
        }

        // Fallback KJOH-778 monta tarjoajaa
        else if (koulutusDoc.getFieldValue("orgoid_s") != null) {
            tarjoaja.setOid("" + koulutusDoc.getFieldValue("orgoid_s"));
        }

        final OrganisaatioPerustieto organisaatio = orgResponse.get(tarjoaja.getOid());
        if (organisaatio != null) {
            tarjoaja.setNimi(getOrganisaatioNimi(organisaatio));
        }

        return tarjoaja;
    }

    private static Nimi getOrganisaatioNimi(
            OrganisaatioPerustieto org) {
        Nimi nimi = new Nimi();
        nimi.putAll(org.getNimi());
        return nimi;
    }

    public static void addKausikoodiTiedot(SolrInputDocument doc, String kausikoodi, TarjontaKoodistoHelper koodistoHelper) {
        if (kausikoodi == null) {
            return;
        }

        KoodiType koodi = koodistoHelper.getKoodiByUri(kausikoodi);

        if (koodi != null) {
            KoodiMetadataType metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("fi"));
            add(doc, KAUSI_FI, metadata.getNimi());

            metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("sv"));
            add(doc, KAUSI_SV, metadata.getNimi());

            metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("en"));
            add(doc, KAUSI_EN, metadata.getNimi());

            add(doc, KAUSI_URI, koodi.getKoodiUri() + IndexDataUtils.KOODI_URI_AND_VERSION_SEPARATOR + koodi.getVersio());
        }
    }

    public static void addKoodiLyhytnimiTiedot(SolrInputDocument doc,
                                               String koodiUri,
                                               TarjontaKoodistoHelper koodistoHelper,
                                               String uriField,
                                               String fiField,
                                               String svField,
                                               String enField) {
        if (koodiUri == null) {
            return;
        }

        final KoodiType koodi = koodistoHelper.getKoodiByUri(koodiUri);

        if (koodi != null) {
            KoodiMetadataType metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("fi"));
            add(doc, fiField, metadata.getLyhytNimi());
            metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("sv"));
            add(doc, svField, metadata.getLyhytNimi());
            metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("en"));
            add(doc, enField, metadata.getLyhytNimi());
            add(doc, uriField,
                    koodi.getKoodiUri() + IndexDataUtils.KOODI_URI_AND_VERSION_SEPARATOR + koodi.getVersio());
        }
    }

    /**
     * Add field if value is not null
     *
     * @param doc
     * @param nimifi
     * @param string
     */
    private static void add(final SolrInputDocument doc, final String fieldName, final Object value) {
        if (value != null) {
            doc.addField(fieldName, value);
        }
    }

}
