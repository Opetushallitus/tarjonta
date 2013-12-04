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


var app = angular.module('Koodisto', ['ngResource', 'config']);

app.factory('Koodisto', function($resource, $log, $q, Config) {

    var host = Config.env["tarjontaKoodistoRestUrlPrefix"];

    var nimiWithLocale = function(locale, metadata) {
        var metas = _.select(metadata, function(koodiMetaData) {
            locale = locale || "fi"; // default locale is finnish

            if (koodiMetaData.kieli.toLowerCase() === locale.toLowerCase()) {

                return koodiMetaData.nimi;
            }
        });

        if (metas.length === 1 && metas.length > 0) {
            return metas[0].nimi;
        } else {
            return "";
        }
    };


    /*
     This JS-object is view representation of koodisto koodi.
     Example koodisto Koodi:

     {
     koodiArvo :"",
     koodiUri  : "",
     koodiTila : "",
     koodiVersio : "",
     koodiKoodisto : "",
     koodiOrganisaatioOid :"",
     -> Koodinimi is localized with given locale
     koodiNimi : ""
     }

     */

    var getKoodiViewModelFromKoodi = function(koodi, locale) {
        var tarjontaKoodi = {
            koodiArvo: koodi.koodiArvo,
            koodiUri: koodi.koodiUri,
            koodiTila: koodi.tila,
            koodiVersio: koodi.versio,
            koodiKoodisto: koodi.koodisto.koodistoUri,
            koodiOrganisaatioOid: koodi.koodisto.organisaatioOid,
            koodiNimi: nimiWithLocale(locale, koodi.metadata)
        };
        return tarjontaKoodi;
    };

    return {
        /*
         @param {array} array of koodis received from Koodisto.
         @param {string} locale in which koodi name should be shown
         @returns {array} array of koodi view model objects
         */

        convertKoodistoKoodiToViewModelKoodi: function(koodisParam, locale) {

            var koodis = [];

            angular.forEach(koodisParam, function(koodi) {
                koodis.push(getKoodiViewModelFromKoodi(koodi, locale));
            });
            return koodis;
        },
        /*
         @param {string} koodistouri from which koodis should be retrieved
         @param {string} locale in which koodi name should be shown
         @returns {promise} return promise which contains array of koodi view models
         */

        getYlapuolisetKoodit: function(koodiUriParam, locale) {

            $log.info('getYlapuolisetKoodit called with : ' + koodiUriParam + ' locale : ' + locale);

            var returnYlapuoliKoodis = $q.defer();

            var returnKoodis = [];

            var ylapuoliKoodiUri = host + 'relaatio/sisaltyy-ylakoodit/:koodiUri';

            $resource(ylapuoliKoodiUri, {koodiUri: '@koodiUri'}, {cache:true}).query({koodiUri: koodiUriParam}, function(koodis) {
                angular.forEach(koodis, function(koodi) {

                    returnKoodis.push(getKoodiViewModelFromKoodi(koodi, locale));
                });
                returnYlapuoliKoodis.resolve(returnKoodis);
            });


            return  returnYlapuoliKoodis.promise;

        },
        getAlapuolisetKoodit: function(koodiUriParam, locale) {

            $log.info('getAlapuolisetKoodi called with : ' + koodiUriParam + ' locale : ' + locale);

            var returnYlapuoliKoodis = $q.defer();

            var returnKoodis = [];

            var ylapuoliKoodiUri = host + 'relaatio/sisaltyy-alakoodit/:koodiUri';

            $resource(ylapuoliKoodiUri, {koodiUri: '@koodiUri'}, {cache:true}).query({koodiUri: koodiUriParam}, function(koodis) {
                angular.forEach(koodis, function(koodi) {

                    returnKoodis.push(getKoodiViewModelFromKoodi(koodi, locale));
                });
                returnYlapuoliKoodis.resolve(returnKoodis);
            });


            return  returnYlapuoliKoodis.promise;

        },
        /*
         @param {string} koodistouri from which koodis should be retrieved
         @param {string} locale in which koodi name should be shown
         @returns {promise} return promise which contains array of koodi view models
         */

        getAllKoodisWithKoodiUri: function(koodistoUriParam, locale) {


            $log.info('getAllKoodisWithKoodiUri called with ' + koodistoUriParam + ' ' + locale);

            var returnKoodisPromise = $q.defer();

            var returnKoodis = [];

            var koodiUri = host + ':koodistoUri/koodi';


            $resource(koodiUri, {koodistoUri: '@koodistoUri'},{cache:true}).query({koodistoUri: koodistoUriParam}, function(koodis) {



                angular.forEach(koodis, function(koodi) {



                    returnKoodis.push(getKoodiViewModelFromKoodi(koodi, locale));
                });
                returnKoodisPromise.resolve(returnKoodis);
            });

            return returnKoodisPromise.promise;
        },
        /*
         @param {string} koodistouri from which koodis should be retrieved
         @param {string} locale in which koodi name should be shown
         @returns {array} array of koodisto view model objects
         */

        getKoodistoWithKoodiUri: function(koodiUriParam, locale) {

            var returnKoodi = $q.defer();


            var koodiUri = host + ":koodistoUri";

            console.log('Calling getKoodistoWithKoodiUri with : ' + koodiUriParam + ' ' + locale);

            var resource = $resource(koodiUri, {koodistoUri: '@koodistoUri'}, {cache:true}).query({koodistoUri: koodiUriParam}, function(data) {
                var returnTarjontaKoodi = {
                    koodistoUri: data.koodistoUri,
                    tila: data.tila
                };
                returnKoodi.resolve(returnTarjontaKoodi);

            });
            console.log('Returning promise from getKoodistoWithKoodiUri');
            return returnKoodi.promise;
        },
        /*
         @param {string} koodisto URI from which koodis should be retrieved
         @param {string} koodi URI from which koodi should be retrieved
         @param {string} locale in which koodi name should be shown
         @returns {array} array of koodisto view model objects
         */
        getKoodi: function(koodistoUriParam, koodiUriParam, locale) {
            var returnKoodi = $q.defer();
            var koodiUri = host + ":koodistoUri/koodi/:koodiUri";
            //console.log('Calling getKoodistoWithKoodiUri with : ' + koodistoUriParam + '/koodi/'+ koodiUriParam +' ' + locale);

            var resource = $resource(koodiUri, {koodistoUri: '@koodistoUri', koodiUri: '@koodiUri' },{cache:true}).get({koodistoUri: koodistoUriParam, koodiUri: koodiUriParam}, function(koodi) {
                returnKoodi.resolve(getKoodiViewModelFromKoodi(koodi, locale));
            });
           // console.log('Returning promise from getKoodistoWithKoodiUri');
            return returnKoodi.promise;
        }

    };

});
