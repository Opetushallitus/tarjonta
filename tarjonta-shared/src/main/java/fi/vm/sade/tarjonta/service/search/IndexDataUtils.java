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

import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.ORG_OID;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.TILA;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi.Teksti;
import fi.vm.sade.tarjonta.service.types.TarjoajaTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;

/**
 * 
 * @author Markus
 *
 */
public class IndexDataUtils {
	
    private static final String LANG_EN = "en";


    private static final String LANG_SV = "sv";


    private static final String LANG_FI = "fi";


    private static final Logger log = LoggerFactory.getLogger(IndexDataUtils.class);


    public static final String KOODI_URI_AND_VERSION_SEPARATOR = "#";

    private static final String KEVAT = "kevat";
    private static final String SYKSY = "syksy";

    private static final String KEVAT_URI = "kausi_k#1";
    private static final String SYKSY_URI = "kausi_s#1";

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

    public static KoodiType getKoodiByUriWithVersion(String uriWithVersion, KoodiService koodiService) {
        SearchKoodisCriteriaType searchCriteria;

        String[] koodiUriAndVersion = splitKoodiURI(uriWithVersion);

        int version = Integer.parseInt(koodiUriAndVersion[1]);
        String uri = koodiUriAndVersion[0];

        if (version < 0) {
            searchCriteria = KoodiServiceSearchCriteriaBuilder.latestKoodisByUris(uri);
        } else {
            searchCriteria = KoodiServiceSearchCriteriaBuilder.koodiByUriAndVersion(uri, version);
        }

        List<KoodiType> queryResult = koodiService.searchKoodis(searchCriteria);
        return queryResult.isEmpty() ? null : queryResult.get(0);
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

    public static String parseYear(Date koulutuksenAlkamisPvm) {
        if (koulutuksenAlkamisPvm == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(koulutuksenAlkamisPvm);
        return "" + cal.get(Calendar.YEAR);
    }
    
    
    public static KoodistoKoodi createKoodistoKoodi(String koodiUri,
            String koodiFi, String koodiSv,
            String koodiEn, SolrDocument koulutusDoc) {
        final Object valueO = koulutusDoc.getFieldValue(koodiUri);
        String value;
        if(valueO instanceof List) {
            value = (String)((List)valueO).get(0);
        } else {
            value = (String) valueO;
        }
        final KoodistoKoodi koodi = new KoodistoKoodi(value);
        koodi.getNimi().put(Nimi.FI, (String)koulutusDoc.getFieldValue(koodiFi));
        koodi.getNimi().put(Nimi.SV, (String)koulutusDoc.getFieldValue(koodiSv));
        koodi.getNimi().put(Nimi.EN, (String)koulutusDoc.getFieldValue(koodiEn));
        return koodi;
    }
    
    public static TarjontaTila createTila(SolrDocument doc) {
        String tila = "" + doc.getFieldValue(TILA);
        if (tila.isEmpty()) {
            return null;
        }
        try {
			return TarjontaTila.valueOf(tila);
		} catch (Exception e) {
			log.debug("Ignored unknown state: "+tila, e);
			return null;
		}
    }
    
    public static TarjoajaTyyppi createTarjoaja(SolrDocument koulutusDoc,
            Map<String, OrganisaatioPerustieto> orgResponse) {
        final TarjoajaTyyppi tarjoaja = new TarjoajaTyyppi();
        tarjoaja.setTarjoajaOid("" + koulutusDoc.getFieldValue(ORG_OID));
        final OrganisaatioPerustieto organisaatio = orgResponse.get(tarjoaja
                .getTarjoajaOid());
        if (organisaatio != null) {
            tarjoaja.setNimi(getOrganisaatioNimi(orgResponse.get(tarjoaja.getTarjoajaOid())));
        }
        
        return tarjoaja;
    }

   private static MonikielinenTekstiTyyppi getOrganisaatioNimi(
            OrganisaatioPerustieto org) {
        MonikielinenTekstiTyyppi nimi = new MonikielinenTekstiTyyppi();
        if (org != null) {
            for(String lang: new String[]{LANG_FI, LANG_SV, LANG_EN}){
                final String val = org.getNimi(lang);
                if(val!=null) {
                    Teksti nimiFi = new Teksti(val, lang);
                    nimi.getTeksti().add(nimiFi);
                }
            }
        }
        
        Preconditions.checkArgument(nimi.getTeksti().size() > 0);
        return nimi;
    }
    
}
