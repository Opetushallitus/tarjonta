/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui.view.common;

import com.vaadin.data.Property;
import java.util.Locale;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jani
 */
public class DateTimeFieldTest {

    @Test
    public void testParseError() {
        DateTimeField dateField = new DateTimeField();
        dateField.setLocale(new Locale("fi", "FI"));
        dateField.setEmptyErrorMessage("1");
        dateField.setMissingTimeMessage("2");
        dateField.setParseErrorMessage("3");
        dateField.setMissingDateMessage("4");

        try {
            dateField.parseErrors("18.1.2013 11:32");
            fail("yes, it's correct format, but test must fail");
        } catch (Property.ConversionException e) {
            assertEquals("3", e.getMessage());
        }

        try {
            dateField.parseErrors("    18.1.2013  ");
            fail("test must fail");
        } catch (Property.ConversionException e) {
            assertEquals("2", e.getMessage());
        }

        try {
            dateField.parseErrors("18.11.2013");
            fail("test must fail");
        } catch (Property.ConversionException e) {
            assertEquals("2", e.getMessage());
        }

        try {
            dateField.parseErrors("8.1.2013");
            fail("test must fail");
        } catch (Property.ConversionException e) {
            assertEquals("2", e.getMessage());
        }

        try {
            dateField.parseErrors("12:44");
            fail("test must fail");
        } catch (Property.ConversionException e) {
            assertEquals("4", e.getMessage());
        }

        try {
            dateField.parseErrors(null);
            fail("test must fail");
        } catch (Property.ConversionException e) {
            assertEquals("1", e.getMessage());
        }
    }
}