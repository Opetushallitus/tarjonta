var app = angular.module('app.koulutus.extend.ctrl', []);

app.controller('ExtendKoulutusController',
    function($modalInstance, targetKoulutus,
            TarjontaService, LocalisationService, $q, $scope,
            OrganisaatioService, AuthService, PermissionService, $location, KoulutusService, koulutusMap) {

        'use strict';

        // Tähän populoidaan formin valinnat:
        $scope.model = {
            text: {
                help: LocalisationService.t('koulutus.extend.help', [targetKoulutus[0].nimi])
            },
            errors: [],
            targetKoulutus: targetKoulutus,
            organisaatiot: [],
            mode: 'EXTEND'
        };
        $scope.alkorganisaatio = $scope.alkorganisaatio || {currentNode: undefined};
        // Watchi valitulle organisaatiolle
        $scope.$watch('alkorganisaatio.currentNode', function(organisaatio, oldVal) {
            $scope.model.organisaatiot = [];
            if (!angular.isUndefined(organisaatio) && organisaatio !== null) {
                lisaaOrganisaatio(organisaatio);
            }

        });
        $scope.valitut = $scope.valitut || [];

        TarjontaService.getJarjestajaCandidates(targetKoulutus[0].oid).then(function(filteredOrganizations) {
            OrganisaatioService.buildOrganizationSelectionDialog(filteredOrganizations).then(function(data) {
                $scope.alkorganisaatiot = data.organizations;
                $scope.organisaatiomap = data.organizationMap;
            });
        });

        var lisaaOrganisaatio = function(organisaatio) {
            $scope.model.jarjestettyKoulutus = koulutusMap[organisaatio.oid];
            if (!$scope.model.jarjestettyKoulutus) {
                $scope.model.organisaatiot.push(organisaatio);
            }
        };

        /**
         * Peruuta nappulaa klikattu, sulje dialogi
         */
        $scope.peruuta = function() {
            $modalInstance.dismiss();
        };
        $scope.jatka = function() {
            var orgOids = [];
            for (var i = 0; i < $scope.model.organisaatiot.length; i++) {
                orgOids.push($scope.model.organisaatiot[i].oid);
            }

            PermissionService.permissionResource().authorize({}, function(authResponse) {
                $scope.model.errors = [];

                if (authResponse.status !== 'OK') {
                    //not authenticated
                    $scope.model.errors.push($scope.uiModel, 'ERROR', ['koulutus.error.auth']);
                    return;
                }

                // Piilota dialogi
                $modalInstance.dismiss();

                KoulutusService.jarjestaKoulutus(
                    targetKoulutus[0].oid,
                    $scope.model.organisaatiot[0].oid
                );
            });
        };
        /**
         * Organisaatio valittu
         */
        $scope.organisaatioValittu = function() {
            return $scope.model.organisaatiot.length > 0;
        };

        $scope.reviewExistingKoulutus = function() {
            $modalInstance.dismiss();
            $location.path('/koulutus/' + $scope.model.jarjestettyKoulutus.oid);
        };

        /**
         * Poista valittu organisaatio ruksista
         */
        $scope.poistaValittu = function(organisaatio) {
            var valitut = [];
            for (var i = 0; i < $scope.model.organisaatiot.length; i++) {
                if ($scope.model.organisaatiot[i] !== organisaatio) {
                    valitut.push($scope.model.organisaatiot[i]);
                }
            }
            $scope.model.organisaatiot = valitut;
        };
    });