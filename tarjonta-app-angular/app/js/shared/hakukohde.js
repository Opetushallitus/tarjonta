/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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

var app = angular.module('Hakukohde', ['ngResource','config']);


app.factory('Hakukohde',function($resource, $log,$q, Config){



    //var hakukohdeUri = Config.env.tarjontaRestUrlPrefix+"hakukohde/ui/:oid";
    var hakukohdeUri = "http://localhost:9090/tarjonta-service/rest/hakukohde/ui/:oid";

    return $resource(hakukohdeUri,{oid:'@oid'},{
        update: {
            method: 'PUT',
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        },
        save: {
            method: 'POST',
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        },
        get : {
            method: 'GET',
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        }
    });

});