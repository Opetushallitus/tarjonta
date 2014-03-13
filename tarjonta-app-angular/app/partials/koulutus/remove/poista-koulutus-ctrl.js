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

app.controller('PoistaKoulutusCtrl', ['$scope', 'Config', 'Koodisto', 'LocalisationService', 'TarjontaService', '$q', '$modalInstance', 'targetKomoto', 'organisaatioOid', 'SisaltyvyysUtil', 'TreeHandlers', 'PermissionService',
    function LiitaSisaltyvyysCtrl($scope, config, koodisto, LocalisationService, TarjontaService, $q, $modalInstance, targetKomoto, organisaatio, SisaltyvyysUtil, TreeHandlers, PermissionService) {
        /*
         * Select koulutus data objects.
         */
        $scope.model = {
            errors: [],
            komoto: targetKomoto
        };

        $scope.cancel = function() {
            $modalInstance.dismiss();
        };

        $scope.remove = function() {
            $scope.model.errors = [];
            PermissionService.permissionResource().authorize({}, function(authResponse) {
                console.log("Authorization check : " + authResponse.result);

                if (authResponse.status !== 'OK') {
                    //not authenticated
                    $scope.controlFormMessages($scope.uiModel, "ERROR", "AUTH");
                    return;
                }

                TarjontaService.koulutus().remove({oid: $scope.model.komoto.oid}, function(response) {
                    if (response.status === 'OK') {
                        console.log("Success");
                        $modalInstance.dismiss();
                    } else {
                        console.log("failed", response);
                        if (!angular.isUndefined(response.errors)) {
                            for (var i = 0; i < response.errors.length; i++) {
                                $scope.model.errors.push({msg: LocalisationService.t(response.errors[i].errorMessageKey)});
                            }
                        }
                    }
                });
            });
        };
    }]);
