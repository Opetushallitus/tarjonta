package fi.vm.sade.tarjonta.service.tasks;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class IndexSyncronizerUtils {

    @Value("${organisaatio.api.rest.url}")
    private String organizationServiceRestApi;

    final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(IndexSyncronizerUtils.class);

    public List<String> getChangedOrganizationOids() {
        String yesterday = new SimpleDateFormat("yyyy-MM-dd").format(new DateTime().minusDays(1).toDate());
        String apiUril = organizationServiceRestApi + "organisaatio/v2/muutetut/oid?lastModifiedSince=" + yesterday;
        logger.info("Loading changed organizations from " + apiUril);

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(apiUril, String.class);
        List<String> oids = new ArrayList<String>();

        try {
            JSONObject json = new JSONObject(response);
            JSONArray arr = json.getJSONArray("oids");
            for (int i = 0; i < arr.length(); i ++) {
                if (!arr.getString(i).isEmpty()) {
                    oids.add(arr.getString(i));
                }
            }
        }
        catch (JSONException e) {
            logger.error("Error parsing JSON");
        }

        return oids;
    }

}
