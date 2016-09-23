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

var app = angular.module('Koulutus', ['ngResource', 'config', 'Logging']);

app.factory('KoulutusService', function($resource, Config, $location, $modal, TarjontaService) {

    return {
        /**
         * Luo järjestävä koulutus olemassa olevan pohjalta (extend).
         */
        extendKorkeakouluOpinto: function(koulutus, locale) {
            $modal.open({
                templateUrl: 'partials/koulutus/copy/extend-koulutus.html',
                controller: 'ExtendKoulutusController',
                resolve: {
                    targetKoulutus: function() {
                        var ohjelma = koulutus.koulutusohjelma;
                        var strName = '';
                        if (ohjelma.tekstis && Object.keys(ohjelma.tekstis).length > 0) {
                            //korkeakoulu etc.
                            strName = ohjelma.tekstis['kieli_' + locale];
                        }
                        return [{
                            oid: koulutus.oid,
                            nimi: strName
                        }];
                    },
                    jarjestetytKoulutukset: function() {
                        return TarjontaService.getJarjestettavatKoulutukset(koulutus.oid, koulutus.opetusJarjestajat);
                    }
                }
            });
        },

        /**
         * Avaa korkeakouluopinnon luontilomake.
         */
        luoKorkeakouluOpinto: function(koulutustyyppiKoodiUri, organisaatioOid, koulutusmoduuliTyyppi,
                                       opetusTarjoajat) {
            $location.path('/koulutus/KORKEAKOULUOPINTO/'
                    + koulutustyyppiKoodiUri
                    + '/edit/'
                    + organisaatioOid);
            $location.search('koulutusmoduuliTyyppi', koulutusmoduuliTyyppi);
            $location.search('opetusTarjoajat', _.pluck(opetusTarjoajat, 'oid').join(','));
        },

        jarjestaKoulutus: function(koulutusOid, organisaatioOid) {
            $location.path('/koulutus/'
            + koulutusOid
            + '/jarjesta/'
            + organisaatioOid);
        }
    };

});