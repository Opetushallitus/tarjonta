angular.module('search.hakutulokset.hakukohteet', [])
    .factory('HakukohderyhmatActions', function(TarjontaService, OrganisaatioService, $modal) {

        'use strict';

        var AddHakukohdeToGroupController = function($scope, $modalInstance, valitutHakukohteet, ryhmat) {
            function getNimi(ryhma) {
                var nimi = ryhma.nimi;
                return nimi.fi || nimi.sv || nimi.en;
            }
            var init = function() {
                $scope.model = $scope.model ? $scope.model : {};
                $scope.model.hakukohteet = valitutHakukohteet;
                $scope.model.ryhmat = ryhmat;
                _.each($scope.model.ryhmat, function(ryhma) {
                    ryhma.displayName = getNimi(ryhma);
                });
                $scope.model.completed = false;
            };
            init();
            $scope.okLiita = function() {
                $scope.teeLiitosRyhmaan('LISAA');
            };
            $scope.okPoista = function() {
                $scope.teeLiitosRyhmaan('POISTA');
            };
            $scope.teeLiitosRyhmaan = function(tyyppi) {
                var valitutRyhmat = $scope.model.ryhmat.filter(function(ryhma) {
                    return ryhma.selected;
                });
                var requestData = [];
                angular.forEach(valitutRyhmat, function(ryhma) {
                    angular.forEach($scope.model.hakukohteet, function(hakukohdeOid) {
                        requestData.push({
                            toiminto: tyyppi,
                            hakukohdeOid: hakukohdeOid,
                            ryhmaOid: ryhma.oid
                        });
                    });
                });
                TarjontaService.hakukohdeRyhmaOperaatiot(requestData).then(function(res) {
                    $scope.model.completed = true;
                    $scope.model.result.status = res.status;
                    $scope.model.result.errors = res.errors;
                }, function(err) {
                    $scope.model.completed = true;
                    $scope.model.result.status = 'ERROR';
                    $scope.model.result.errors = [{
                        errorCode: 'ERROR',
                        errorField: 'none',
                        errorMessageKey: 'system.error'
                    }];
                });
                $scope.model.completed = true;
                $scope.model.result = {
                    status: 'OK',
                    errors: []
                };
            };
            $scope.cancel = function() {
                $modalInstance.dismiss();
            };
            $scope.close = function() {
                $modalInstance.close();
            };
        };
        function liitaHakukohteetRyhmaan($scope) {
            $modal.open({
                templateUrl: 'partials/search/liita-hakukohde-ryhmaan-dialog.html',
                controller: AddHakukohdeToGroupController,
                scope: $scope,
                resolve: {
                    valitutHakukohteet: function() {
                        return $scope.selection.hakukohteet;
                    },
                    ryhmat: function() {
                        return OrganisaatioService.getRyhmat();
                    }
                }
            });
        }

        return {
            liitaHakukohteetRyhmaan: function($scope) {
                liitaHakukohteetRyhmaan($scope);
            }
        };
    });
