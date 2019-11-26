package fi.vm.sade.tarjonta.service.tasks;

import fi.vm.sade.tarjonta.shared.UrlConfiguration;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class IndexSyncronizerUtils {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(IndexSyncronizerUtils.class);

    private final UrlConfiguration urlConfiguration;

    @Autowired
    public IndexSyncronizerUtils(UrlConfiguration urlConfiguration) {
        this.urlConfiguration = urlConfiguration;
    }

    public List<String> getChangedOrganizationOids() {
        String yesterday = new SimpleDateFormat("yyyy-MM-dd").format(new DateTime().minusDays(1).toDate());
        String apiUril =  urlConfiguration.url("organisaatio-service.changedOrganizationOids", yesterday);
        logger.info("Loading changed organizations from " + apiUril);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new HeaderRequestInterceptor("Caller-Id", "1.2.246.562.10.00000000001.tarjonta-backend"));

        String response = restTemplate.getForObject(apiUril, String.class);
        List<String> oids = new ArrayList<>();

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

    private class HeaderRequestInterceptor implements ClientHttpRequestInterceptor {

        private final String headerName;
        private final String headerValue;

        public HeaderRequestInterceptor(String headerName, String headerValue) {
            this.headerName = headerName;
            this.headerValue = headerValue;
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            request.getHeaders().set(headerName, headerValue);
            return execution.execute(request, body);
        }
    }

}
