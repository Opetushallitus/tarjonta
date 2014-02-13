/*
 * Copyright (c) 2014 The Finnish Board of Education - Opetushallitus
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
 */
package fi.vm.sade.tarjonta.ui;

import com.google.gson.GsonBuilder;
import fi.vm.sade.generic.healthcheck.HealthChecker;
import fi.vm.sade.generic.healthcheck.SpringAwareHealthCheckServlet;
import java.util.Map;

/**
 * TODO add koodisto check
 * TODO add tarjonta-service check
 *
 * @author mlyly
 */
public class HealthCheck extends SpringAwareHealthCheckServlet {

    /* Just make sure the correct artifact gets added... */
    private GsonBuilder _gsonbuiler;

    @Override
    protected Map<String, HealthChecker> registerHealthCheckers() {
        Map<String, HealthChecker> result = super.registerHealthCheckers();

        result.put("foo", new HealthChecker() {

            @Override
            public Object checkHealth() throws Throwable {
                return "OK";
            }
        });
        return result;
    }

}
