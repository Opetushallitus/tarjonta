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
        extendKorkeakouluOpinto: function(koulutustyyppiKoodiUri, organisaatioOid, koulutusmoduuliTyyppi, $scope) {
            if (angular.isDefined($scope.model.sourceKoulutus)) {
                var extendModalDialog = $modal.open({
                    templateUrl: 'partials/koulutus/copy/extend-koulutus.html',
                    controller: 'ExtendKoulutusController',
                    resolve: {
                        targetKoulutus: function() {
                            var ohjelma = $scope.model.koulutus.koulutusohjelma;
                            var strName = '';
                            if (ohjelma.tekstis && Object.keys(ohjelma.tekstis).length > 0) {
                                //korkeakoulu etc.
                                strName = ohjelma.tekstis['kieli_' + $scope.model.koodistoLocale];
                            }
                            return [{oid: $scope.model.koulutus.oid, nimi: strName,
                                koulutustyyppiKoodiUri: koulutustyyppiKoodiUri,
                                koulutusmoduuliTyyppi: koulutusmoduuliTyyppi}];
                        },
                        targetOrganisaatio: function() {
                            return {
                                oid: $scope.model.koulutus.organisaatio.oid,
                                nimi: $scope.model.koulutus.organisaatio.nimi
                            };
                        },
                        koulutusMap: function() {
                            // Tarkista, onko koulutus jo järjestetty kyseiselle organisaatiolle (tai sen aliorganisaatiolle)
                            return TarjontaService.getJarjestettavatKoulutukset($scope.model.koulutus.oid);
                        }
                    }
                });
            }
        },

        /**
         * Avaa korkeakouluopinnon luontilomake.
         */
        luoKorkeakouluOpinto: function(koulutustyyppiKoodiUri, organisaatioOid, koulutusmoduuliTyyppi, $scope) {
            $location.path('/koulutus/KORKEAKOULUOPINTO/'
                    + koulutustyyppiKoodiUri
                    + '/edit/'
                    + organisaatioOid);
            $location.search('koulutusmoduuliTyyppi', koulutusmoduuliTyyppi);
        },

        jarjestaKoulutus: function(koulutusOid, organisaatioOid) {
            $location.path('/koulutus/'
            + koulutusOid
            + '/jarjesta/'
            + organisaatioOid);
        }
    };

});