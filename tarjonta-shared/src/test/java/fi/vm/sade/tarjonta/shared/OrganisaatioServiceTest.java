package fi.vm.sade.tarjonta.shared;

import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class OrganisaatioServiceTest {

    OrganisaatioService service = new OrganisaatioService(null, null, null, null);

    @Test
    public void surviveUnknownOrganisationTypesInResponse() throws Exception {
        assertNotNull(service.objectReader.forType(OrganisaatioPerustieto.class).readValue("{\"organisaatiotyypit\": [\"FOO\"]}"));
    }
}
