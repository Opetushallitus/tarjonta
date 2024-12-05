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
package fi.vm.sade.tarjonta.service.search.it;

import fi.vm.sade.tarjonta.service.search.SolrServerFactory;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("embedded-solr")
public class EmbeddedSolrServerFactory extends SolrServerFactory {

  private EmbeddedSolrServer server = null;

  @Override
  public SolrServer getSolrServer(String collection) {
    if (server == null) {

      String solrHome = "src/main/resources/solr";

      System.setProperty("solr.solr.home", solrHome);
      System.setProperty("solr.data.dir", "target/solr-data");

      CoreContainer coreContainer = new CoreContainer(solrHome);
      coreContainer.load();
      server = new EmbeddedSolrServer(coreContainer, collection);

      System.clearProperty("solr.solr.home");
      System.clearProperty("solr.data.dir");
    }
    return server;
  }

  @Override
  public SolrServer getOrganisaatioSolrServer() {
    return getSolrServer("organisaatiot");
  }

  @Override
  public void afterPropertiesSet() throws Exception {}
}
