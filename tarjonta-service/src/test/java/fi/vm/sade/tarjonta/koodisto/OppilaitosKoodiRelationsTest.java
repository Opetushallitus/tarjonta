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
package fi.vm.sade.tarjonta.koodisto;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jani
 */
public class OppilaitosKoodiRelationsTest {

    @Test
    public void splitOrganisationPath() {
        List<String> splitOrganisationPath = OppilaitosKoodiRelations.splitOrganisationPath("|1.2.246.562.10.00000000001|1.2.246.562.10.70829532053|1.2.246.562.10.33517818648|");

        assertEquals("1.2.246.562.10.00000000001", splitOrganisationPath.get(0));
        assertEquals("1.2.246.562.10.70829532053", splitOrganisationPath.get(1));
        assertEquals("1.2.246.562.10.33517818648", splitOrganisationPath.get(2));

        assertEquals(3, splitOrganisationPath.size());

        splitOrganisationPath = OppilaitosKoodiRelations.splitOrganisationPath("|1.2.246.562.10.00000000001|");
        assertEquals(1, splitOrganisationPath.size());
    }
}
