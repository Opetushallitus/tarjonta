package fi.vm.sade.tarjonta.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

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
