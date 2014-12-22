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
var app = angular.module('app.koulutus.kuvausRemove.ctrl', []);
app.controller('PoistaValintaperustekuvausCtrl', function($scope, Kuvaus, selectedKuvaus, LocalisationService, $modalInstance, HakukohdeService, $location) {
    $scope.model = {
        errors: [],
        text: {
            info: LocalisationService.t('kuvaus.poista.help', [
                selectedKuvaus.kuvauksenNimi,
                selectedKuvaus.kausiNimi,
                selectedKuvaus.vuosi
            ])
        },
        btnDisableRemove: false
    };
    $scope.cancel = function() {
        $modalInstance.dismiss();
    };
    $scope.goToHakukohde = function(oid) {
        $modalInstance.dismiss();
        $location.path('/hakukohde/' + oid);
    };
    $scope.remove = function() {
        if ($scope.model.btnDisableRemove) {
            return;
        }
        $scope.model.errors = [];
        // Hae kuvaukseen liitetyt hakukohteet
        HakukohdeService.findHakukohdesByKuvausId(selectedKuvaus.kuvauksenTunniste).then(function(result) {
            if (result.data.result.length === 0) {
                var removedKuvausPromise = Kuvaus.removeKuvausWithId(selectedKuvaus.kuvauksenTunniste);
                removedKuvausPromise.then(function(removedKuvaus) {
                    if (removedKuvaus.status === 'OK') {
                        $modalInstance.close(removedKuvaus);
                    }
                    else {
                        $scope.model.errors.push(LocalisationService.t('tarjonta.tekninenvirhe.title'));
                    }
                });
            }
            else {
                $scope.liitetytHakukohteet = result.data.result;
                angular.forEach($scope.liitetytHakukohteet, function(hakukohde) {
                    hakukohde.nimi = _.reduce(hakukohde.hakukohteenNimet, function(memo, text) {
                        if ($.trim(text) != '') {
                            memo += text + ' / ';
                        }
                        return memo;
                    }, '');
                    hakukohde.nimi = hakukohde.nimi.substring(0, hakukohde.nimi.length - 3);
                });
            }
        });
    };
    return $scope;
});