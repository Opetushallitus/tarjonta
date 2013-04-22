package fi.vm.sade.tarjonta.service.search;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.koodisto.util.KoodistoHelper;

public class IndexingUtils {


    public static final String KOODI_URI_AND_VERSION_SEPARATOR = "#";

    private static final String KEVAT = "Kevät";

    private static final String SYKSY = "Syksy";
    
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

    public static String parseYear(Date koulutuksenAlkamisPvm) {
        if (koulutuksenAlkamisPvm == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(koulutuksenAlkamisPvm);
        return "" + cal.get(Calendar.YEAR);
    }
    
}
