/* Controllers */
var app = angular.module('app.koulutus.copy.ctrl', []);
app.controller('CopyMoveKoulutusController', [
    '$modalInstance',
    'targetKoulutus',
    'targetOrganisaatio',
    'TarjontaService',
    'LocalisationService',
    '$q',
    '$scope',
    'OrganisaatioService',
    'AuthService',
    'PermissionService',
    '$location', function($modalInstance, targetKoulutus, targetOrganisaatio, TarjontaService, LocalisationService,
                          $q, $scope, OrganisaatioService, AuthService, PermissionService, $location) {
        'use strict';
        // Tähän populoidaan formin valinnat:
        $scope.model = {
            text: {
                help: LocalisationService.t('koulutus.copy.help', [targetKoulutus.nimi])
            },
            errors: [],
            targetKoulutus: targetKoulutus,
            targetOrganisaatio: targetOrganisaatio,
            organisaatiot: [],
            mode: 'COPY'
        };

        function buildDialog(data) {
            $scope.alkorganisaatiot = data.organizations;
            $scope.organisaatiomap = data.organizationMap;
        }

        var isJarjestettyKoulutus = targetKoulutus.koulutuksenTarjoajaKomoto;
        if (isJarjestettyKoulutus) {
            $scope.model.disableCopy = true;
            $scope.model.mode = 'MOVE';
            TarjontaService.getJarjestajaCandidates(targetKoulutus.koulutuksenTarjoajaKomoto).then(function(orgs) {
                OrganisaatioService.buildOrganizationSelectionDialog(orgs).then(buildDialog);
            });
        } else {
            OrganisaatioService.buildOrganizationSelectionDialog().then(buildDialog);
        }

        $scope.alkorganisaatio = $scope.alkorganisaatio || {
            currentNode: undefined
        };
        // Watchi valitulle organisaatiolle
        $scope.$watch('alkorganisaatio.currentNode', function(organisaatio, oldVal) {
            console.log('oprganisaatio valittu', organisaatio);
            //XXX nyt vain yksi organisaatio valittavissa
            $scope.model.organisaatiot = [];
            if (!angular.isUndefined(organisaatio) && organisaatio !== null) {
                lisaaOrganisaatio(organisaatio);
            }
        });

        $scope.valitut = $scope.valitut || [];

        var lisaaOrganisaatio = function(organisaatio) {
            $scope.model.organisaatiot.push(organisaatio);
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
                console.log('Authorization check : ' + authResponse.result);
                $scope.model.errors = [];
                if (authResponse.status !== 'OK') {
                    //not authenticated
                    $scope.model.errors.push($scope.uiModel, 'ERROR', ['koulutus.error.auth']);
                    return;
                }
                var apiModel = {
                    mode: $scope.model.mode,
                    organisationOids: orgOids
                };
                TarjontaService.koulutus($scope.model.targetKoulutus.oid).copyAndMove(apiModel, function(response) {
                    if (response.status === 'OK') {
                        $modalInstance.close(response);
                        if (response.result.to.length > 0) {
                            //TODO: handle multiple copies
                            $location.path('/koulutus/' + response.result.to[0].oid + '/edit');
                        }
                    }
                    else {
                        if (!angular.isUndefined(response.errors) && response.errors.length > 0) {
                            $scope.model.errors.push({
                                msg: LocalisationService.t('koulutus.copy.error.yleisvirhe', [])
                            });
                            for (var i = 0; i < response.errors.length; i++) {
                                $scope.model.errors.push({
                                    msg: LocalisationService.t('koulutus.copy.error.'
                                        + response.errors[i].errorMessageKey, [])
                                });
                            }
                        }
                    }
                });
            });
        };
        /**
         * Organisaatio valittu
         */
        $scope.organisaatioValittu = function() {
            return $scope.model.organisaatiot.length > 0;
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
    }
]);