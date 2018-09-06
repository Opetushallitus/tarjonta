package fi.vm.sade.tarjonta.shared;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import fi.vm.sade.javautils.httpclient.OphHttpClient;
import fi.vm.sade.javautils.httpclient.OphHttpRequest;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class KoodiServiceImpl implements KoodiService {

    private final static TypeReference<List<KoodiType>> KOODI_TYPE_LIST_TYPE_REFERENCE = new TypeReference<List<KoodiType>>() {};

    private final OphHttpClient httpClient;
    private final ObjectReader objectReader;

    @Autowired
    public KoodiServiceImpl(OphHttpClient httpClient) {
        this.httpClient = httpClient;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        this.objectReader = objectMapper.reader();
    }

    @Override
    public List<KoodiType> listKoodiByRelation(KoodiUriAndVersioType koodi, boolean onAlaKoodi, SuhteenTyyppiType suhdeTyyppi) {
        return getRelaatioRequest(suhdeTyyppi, onAlaKoodi, koodi)
                .expectStatus(200)
                .execute(response -> objectReader.forType(KOODI_TYPE_LIST_TYPE_REFERENCE).readValue(response.asInputStream()));
    }

    private OphHttpRequest getRelaatioRequest(SuhteenTyyppiType suhdeTyyppi, boolean onAlaKoodi, KoodiUriAndVersioType koodi) {
        switch (suhdeTyyppi) {
            case RINNASTEINEN:
                return httpClient.get("koodisto-service.relaatio.rinnasteinen.byKoodiUri", koodi.getKoodiUri());
            case SISALTYY:
                if (onAlaKoodi) {
                    return httpClient.get("koodisto-service.relaatio.sisaltyy-ylakoodit.byKoodiUri", koodi.getKoodiUri());
                } else {
                    return httpClient.get("koodisto-service.relaatio.sisaltyy-alakoodit.byKoodiUri", koodi.getKoodiUri());
                }
            default:
                throw new IllegalArgumentException("Tuntematon suhdeTyyppi: " + suhdeTyyppi);
        }
    }

    @Override
    public List<KoodiType> searchKoodisByKoodisto(String koodistoUri, String koodiArvo) {
        return httpClient.get("koodisto-service.koodisto.byUri.koodi.byArvo", koodistoUri, koodiArvo)
                .expectStatus(200)
                .execute(response -> objectReader.forType(KOODI_TYPE_LIST_TYPE_REFERENCE).readValue(response.asInputStream()));
    }

    @Override
    public List<KoodiType> searchKoodis(SearchKoodisCriteriaType searchCriteria) {
        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("koodiUris", searchCriteria.getKoodiUris());
        parameters.put("koodiArvo", searchCriteria.getKoodiArvo());
        parameters.put("koodiTilas", searchCriteria.getKoodiTilas());
        Optional.ofNullable(searchCriteria.getValidAt())
                .map(validAt -> validAt.toGregorianCalendar().toZonedDateTime()
                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.of("UTC"))))
                .ifPresent(validAt -> parameters.put("validAt", validAt));
        parameters.put("koodiVersio", searchCriteria.getKoodiVersio());
        parameters.put("koodiVersioSelection", searchCriteria.getKoodiVersioSelection());

        return httpClient.get("koodisto-service.searchKoodis", parameters)
                .expectStatus(200)
                .execute(response -> objectReader.forType(KOODI_TYPE_LIST_TYPE_REFERENCE).readValue(response.asInputStream()));
    }

}
