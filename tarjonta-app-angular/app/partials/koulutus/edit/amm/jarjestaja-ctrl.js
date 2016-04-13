var app = angular.module('app.edit.ctrl.amm');
app.controller('JarjestajaCtrl', function($modalInstance, targetOrganisaatio, TarjontaService, LocalisationService,
                                          $q, $scope, OrganisaatioService) {
    'use strict';

    var OPPILAITOSTYYPIT = window.CONFIG.app['nayttotutkinto.jarjestaja.oppilaitostyypit'] || [
            'oppilaitostyyppi_24',
            'oppilaitostyyppi_23',
            'oppilaitostyyppi_22',
            'oppilaitostyyppi_21',
            'oppilaitostyyppi_41',
            'oppilaitostyyppi_63',
            'oppilaitostyyppi_62',
            'oppilaitostyyppi_61',
            'oppilaitostyyppi_93',
            'oppilaitostyyppi_99',
            'oppilaitostyyppi_42',
            'oppilaitostyyppi_XX'
        ];

    $scope.model = {
        text: {
            help: LocalisationService.t('koulutus.jarjestaja.help', [])
        },
        errors: [],
        targetOrganisaatio: targetOrganisaatio,
        organisaatiot: [],
        mode: 'COPY'
    };

    $scope.alkorganisaatio = $scope.alkorganisaatio || {
        currentNode: undefined
    };

    $scope.$watch('alkorganisaatio.currentNode', function(organisaatio) {
        $scope.model.organisaatiot = [];
        if (organisaatio) {
            lisaaOrganisaatio(organisaatio);
        }
    });

    $scope.valitut = $scope.valitut || [];

    $scope.organisaatiomap = $scope.organisaatiomap || {};

    $scope.alkorganisaatiot = {};

    var promises = [];

    var deferred = $q.defer();

    promises.push(deferred.promise);

    OrganisaatioService.etsi({
        organisaatiotyyppi: 'Koulutustoimija',
        lakkautetut: false,
        skipparents: true,
        suunnitellut: false
    }).then(function(vastaus) {

        $scope.alkorganisaatiot = _.chain(vastaus.organisaatiot)
            .filter(function(org) {
                return org.organisaatiotyypit.indexOf('KOULUTUSTOIMIJA') !== -1;
            })
            .map(function(org) {
                org.children = _.filter(org.children, function(childOrg) {
                    var oppilaitostyyppi = window.oph.removeKoodiVersion(childOrg.oppilaitostyyppi);
                    return OPPILAITOSTYYPIT.indexOf(oppilaitostyyppi) !== -1;
                });
                return org;
            })
            .value();

        function buildOrgMap(orglist) {
            _.each(orglist, function(org) {
                $scope.organisaatiomap[org.oid] = org;
                if (org.children) {
                    buildOrgMap(org.children);
                }
            });
        }
        buildOrgMap(vastaus.organisaatiot);
    });

    function lisaaOrganisaatio(organisaatio) {
        $scope.model.organisaatiot.push(organisaatio);
    }

    $scope.peruuta = function() {
        $modalInstance.dismiss();
    };

    $scope.jatka = function() {
        OrganisaatioService.nimi($scope.model.organisaatiot[0].oid).then(function(vastaus) {
            $modalInstance.close({
                oid: $scope.model.organisaatiot[0].oid,
                nimi: vastaus
            });
        });
    };

    $scope.organisaatioValittu = function() {
        return $scope.model.organisaatiot.length > 0;
    };

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