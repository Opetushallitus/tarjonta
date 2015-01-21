/**
 * Created by alexGofore on 29.9.2014.
 */
var app = angular.module('app.koulutus.ctrl');
app.controller('OrganizationSelectionController', function($location, $q, $scope, Koodisto, $modal,
                                   OrganisaatioService, SharedStateService, AuthService, $log, $timeout) {
    $scope.lkorganisaatio = {
        currentNode: null
    };
    $scope.$watch('lkorganisaatio.currentNode', function(organization) {
        if (organization && organization.oid && _.findWhere($scope.selectedOrganizations, {
                oid: organization.oid
            }) === undefined) {
            $scope.selectedOrganizations.push(organization);
        }
    });
    $scope.cancel = function() {
        $scope.organizationSelectionDialog.dismiss('cancel');
    };
    $scope.done = function() {
        var actions = {
            TARJOAJA: function() {
                $scope.model.organisaatiot = [];
                $scope.model.opetusTarjoajat = [];
                _.each($scope.selectedOrganizations, function(org) {
                    $scope.model.organisaatiot.push(org);
                    $scope.model.opetusTarjoajat.push(org.oid);
                });
            },
            JARJESTAJA: function() {
                $scope.model.jarjestavatOrganisaatiot = [];
                $scope.model.opetusJarjestajat = [];
                _.each($scope.selectedOrganizations, function(org) {
                    $scope.model.jarjestavatOrganisaatiot.push(org);
                    $scope.model.opetusJarjestajat.push(org.oid);
                });
            }
        };
        actions[$scope.organizationSelectionType]();
        $scope.organizationSelectionDialog.dismiss();
    };
    var searchOrganizationTimeout = null;
    $scope.searchOrganizations = function(qterm) {
        if (searchOrganizationTimeout !== null) {
            $timeout.cancel(searchOrganizationTimeout);
        }
        if (qterm.length < 4) {
            return;
        }
        searchOrganizationTimeout = $timeout(function() {
            OrganisaatioService.etsi({
                searchStr: qterm,
                lakkautetut: false,
                skipparents: false,
                suunnitellut: false
            }).then(function(result) {
                $scope.lkorganisaatiot = result.organisaatiot;
            });
        }, 500);
    };
    $scope.deleteOrganization = function(oid) {
        var index;
        _.find($scope.selectedOrganizations, function(org, i) {
            index = i;
            return org.oid === oid;
        });
        $scope.selectedOrganizations.splice(index, 1);
    };
});