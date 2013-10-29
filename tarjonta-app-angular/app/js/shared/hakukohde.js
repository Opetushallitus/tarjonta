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

//TODO: after refactoring the rest to v1 change this
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

app.factory('Liite',function($resource) {
    //var hakukohdeLiiteUri = Config.env.tarjontaRestUrlPrefix+"v1/hakukohde/:oid/liite/:liiteId";
    var hakukohdeLiiteUri = "http://localhost:9090/tarjonta-service/rest/v1/hakukohde/:hakukohdeOid/liite/:liiteId";

    return $resource(hakukohdeLiiteUri,{hakukohdeOid:'@hakukohdeOid',liiteId:'@liiteId'},{
        update: {
            method: 'PUT',
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        },
        insert: {
            method: 'POST',
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        },
        get : {
            method: 'GET',
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        },
        remove : {
            method: 'DELETE',
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        }
    });

});


app.factory('Valintakoe',function($resource, $log,$q, Config) {

    //var hakukohdeValintakoeUri = Config.env.tarjontaRestUrlPrefix+"v1/hakukohde/:oid/valintakoe/:valintakoeOid";

    var hakukohdeValintakoeUri = "http://localhost:9090/tarjonta-service/rest/v1/hakukohde/:hakukohdeOid/valintakoe/:valintakoeOid";

    return $resource(hakukohdeValintakoeUri,{hakukohdeOid:'@hakukohdeOid',valintakoeOid:'@valintakoeOid'},{
        update: {
            method: 'PUT',
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        },
        insert: {
            method: 'POST',
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        },
        getAll : {
            method: 'GET',
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        },
        remove : {
            method: 'DELETE',
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        }

    });

});