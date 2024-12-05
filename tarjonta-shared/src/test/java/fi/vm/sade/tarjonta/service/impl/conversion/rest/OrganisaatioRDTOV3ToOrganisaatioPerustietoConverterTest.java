package fi.vm.sade.tarjonta.service.impl.conversion.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.dto.v3.OrganisaatioRDTOV3;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class OrganisaatioRDTOV3ToOrganisaatioPerustietoConverterTest {

  OrganisaatioRDTOV3ToOrganisaatioPerustietoConverter converter =
      new OrganisaatioRDTOV3ToOrganisaatioPerustietoConverter();

  @Test
  public void surviveUnknownOrganisationTypes() {

    OrganisaatioRDTOV3 dto = new OrganisaatioRDTOV3();
    dto.setTyypit(Arrays.asList(null, "Ryhma"));

    OrganisaatioPerustieto op = converter.convert(dto);

    assertEquals(1, op.getOrganisaatiotyypit().size());
  }
}
