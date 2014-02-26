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

/**
 *
 * @author jani
 */
public class ImageMimeValidatorTest {

    @Test
    public void validateImageMimeType() {
        // TODO review the generated test code and remove the default call to fail.
        boolean validate = ImageMimeValidator.validate("image/bmp");
        assertTrue(validate);

        validate = ImageMimeValidator.validate("image/jpg");
        assertTrue(validate);

        validate = ImageMimeValidator.validate("image/png");
        assertTrue(validate);

        validate = ImageMimeValidator.validate(" image/png ");
        assertFalse(validate);

        validate = ImageMimeValidator.validate("image/xxx");
        assertFalse(validate);

        validate = ImageMimeValidator.validate("mage/png");
        assertFalse(validate);

        validate = ImageMimeValidator.validate("");
        assertFalse(validate);
    }

}
