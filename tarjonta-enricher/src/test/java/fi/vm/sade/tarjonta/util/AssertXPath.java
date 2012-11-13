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
package fi.vm.sade.tarjonta.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import junit.framework.Assert;
import org.xml.sax.InputSource;

/**
 * Convenience class that helps to evaluate XPath expression.
 *
 * @author Jukka Raanamo
 */
public class AssertXPath {

    private static XPath sXpath;

    static {
        sXpath = XPathFactory.newInstance().newXPath();
    }

    public static void assertEvals(String msg, String expected, String expression, InputSource input) {

        try {
            String actual = sXpath.evaluate(expression, input);
            Assert.assertEquals(msg, expected, actual);
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException("bad xpath expression: " + expression, e);
        }

    }

    public static void assertEvals(String msg, String expected, String expression, byte[] data) {
        assertEvals(msg, expected, expression, new InputSource(new ByteArrayInputStream(data)));
    }

    public static void assertEvals(String msg, String expected, String expression, ByteArrayOutputStream out) {
        assertEvals(msg, expected, expression, out.toByteArray());
    }

}

