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
package fi.vm.sade.tarjonta.publication.utils;

import java.util.ArrayDeque;
import java.util.Deque;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author Jukka Raanamo
 */
public class StringUtilsTest {

    @Test
    public void testJoin() {

        Deque<String> deque = new ArrayDeque<String>();

        deque.push("one");
        deque.push("two");
        deque.push("three");

        Assert.assertEquals("/one/two/three", StringUtils.join(deque, "/"));

        Assert.assertEquals("three", deque.pop());
        Assert.assertEquals("two", deque.pop());
        Assert.assertEquals("one", deque.pop());

    }

}

