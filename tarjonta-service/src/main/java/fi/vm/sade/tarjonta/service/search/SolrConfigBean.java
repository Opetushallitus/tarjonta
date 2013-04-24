package fi.vm.sade.tarjonta.service.search;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SolrConfigBean {

    public String getSolrHome() {
        return solrHome;
    }

    public String getSolrDataDir() {
        return solrDataDir;
    }

    @Value("${tarjonta.solr.home}")
    String solrHome;

    @Value("${tarjonta.solr.dataDir}")
    String solrDataDir;

    @Value("${tarjonta.solr.start:false}")
    String start;

    public Boolean getStart() {
        return Boolean.valueOf(start);
    }

}
