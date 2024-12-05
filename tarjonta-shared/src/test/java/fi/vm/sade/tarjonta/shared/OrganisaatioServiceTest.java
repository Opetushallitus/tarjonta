package fi.vm.sade.tarjonta.shared;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import org.junit.jupiter.api.Test;

public class OrganisaatioServiceTest {

  OrganisaatioService service = new OrganisaatioService(null, null, null, null);

  @Test
  public void surviveUnknownOrganisationTypesInResponse() throws Exception {
    assertNotNull(
        service
            .objectReader
            .forType(OrganisaatioPerustieto.class)
            .readValue("{\"organisaatiotyypit\": [\"FOO\"]}"));
  }
}
