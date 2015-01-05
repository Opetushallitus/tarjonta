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
package fi.vm.sade.tarjonta.shared;

import org.junit.Test;
import static org.junit.Assert.*;

public class ImageMimeValidatorTest {

    @Test
    public void validateImageMimeType() {

        boolean isValid = ImageMimeValidator.isValid("image/bmp");
        assertTrue(isValid);

        isValid = ImageMimeValidator.isValid("image/jpg");
        assertTrue(isValid);

        isValid = ImageMimeValidator.isValid("image/png");
        assertTrue(isValid);

        isValid = ImageMimeValidator.isValid("image/jpeg");
        assertTrue(isValid);

        isValid = ImageMimeValidator.isValid(" image/png ");
        assertFalse(isValid);

        isValid = ImageMimeValidator.isValid("image/xxx");
        assertFalse(isValid);

        isValid = ImageMimeValidator.isValid("mage/png");
        assertFalse(isValid);

        isValid = ImageMimeValidator.isValid("");
        assertFalse(isValid);
    }

}
