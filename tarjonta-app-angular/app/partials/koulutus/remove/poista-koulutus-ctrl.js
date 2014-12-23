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
var app = angular.module('app.koulutus.remove.ctrl', []);
app.controller('PoistaKoulutusCtrl', [
    '$scope',
    'Config',
    '$location',
    '$route',
    'Koodisto',
    'LocalisationService',
    'TarjontaService',
    '$q',
    '$modalInstance',
    'targetKomoto',
    'organisaatioOid',
    'PermissionService',
    '$log', function LiitaSisaltyvyysCtrl($scope, config, $location, $route, koodisto, LocalisationService,
                      TarjontaService, $q, $modalInstance, targetKomoto, organisaatio, PermissionService, $log) {
        $log = $log.getInstance('PoistaKoulutusCtrl');
        /*
             * Select koulutus data objects.
             */
        $scope.handleNimi = function(nimi) {
            if (!angular.isUndefined(nimi) && nimi !== null && nimi.length > 0) {
                return nimi;
            }
            else {
                //no localised name
                return targetKomoto.koulutuskoodi;
            }
        };
        $scope.model = {
            errors: [],
            komoto: targetKomoto,
            text: {
                info: LocalisationService.t('koulutus.poista.help', [$scope.handleNimi(targetKomoto.nimi)])
            },
            btnDisableRemove: false
        };
        $scope.cancel = function() {
            $modalInstance.dismiss();
        };
        $scope.remove = function() {
            if ($scope.model.btnDisableRemove) {
                return;
            }
            $scope.model.errors = [];
            PermissionService.permissionResource().authorize({}, function(authResponse) {
                $log.debug('Authorization check : ' + authResponse.result);
                if (authResponse.status !== 'OK') {
                    //not authenticated
                    return;
                }
                TarjontaService.koulutus().remove({
                    oid: $scope.model.komoto.oid
                }, function(response) {
                        if (response.status === 'OK') {
                            $modalInstance.close(response);
                        }
                        else {
                            if (!angular.isUndefined(response.errors) && response.errors.length > 0) {
                                $scope.model.errors.push({
                                    msg: LocalisationService.t('koulutus.poista.error.yleisvirhe',
                                        [$scope.handleNimi(targetKomoto.nimi)])
                                });
                                $scope.model.btnDisableRemove = true;
                            }
                        }
                    });
            });
        };
        return $scope;
    }
]);