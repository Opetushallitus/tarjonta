'use strict';

/* Controllers */

var app = angular.module('app.edit.ctrl.amm');

app.controller('JarjestajaCtrl', ['$modalInstance', 'targetOrganisaatio',
    'TarjontaService', 'LocalisationService', '$q', '$scope',
    'OrganisaatioService', 'AuthService', 'PermissionService', '$location',
    function($modalInstance, targetOrganisaatio,
            TarjontaService, LocalisationService, $q, $scope,
            OrganisaatioService, AuthService, PermissionService, $location) {

        // Tähän populoidaan formin valinnat:
        $scope.model = {
            text: {
                help: LocalisationService.t("koulutus.jarjestaja.help", [])
            },
            errors: [],
            targetOrganisaatio: targetOrganisaatio,
            organisaatiot: [],
            mode: 'COPY'
        };
        $scope.alkorganisaatio = $scope.alkorganisaatio || {currentNode: undefined};
        // Watchi valitulle organisaatiolle
        $scope.$watch('alkorganisaatio.currentNode', function(organisaatio, oldVal) {
            console.log("oprganisaatio valittu", organisaatio);
            //XXX nyt vain yksi organisaatio valittavissa
            $scope.model.organisaatiot = [];
            if (!angular.isUndefined(organisaatio) && organisaatio !== null) {
                lisaaOrganisaatio(organisaatio);
            }

        });
        $scope.valitut = $scope.valitut || [];
        $scope.organisaatiomap = $scope.organisaatiomap || {};
        $scope.alkorganisaatiot = {};

        var promises = [];
        var deferred = $q.defer();
        promises.push(deferred.promise);


        // haetaan organisaatihierarkia joka valittuna kälissä tai jos mitään ei ole valittuna organisaatiot joihin käyttöoikeus
        OrganisaatioService.etsi({
            organisaatiotyyppi: 'Koulutustoimija',
            lakkautetut: false,
            skipparents: true,
            suunnitellut: false}).then(function(vastaus) {
            //console.log("asetetaan org hakutulos modeliin.");

            var typeUris = window.CONFIG.app["nayttotutkinto.jarjestaja.oppilaitostyypit"];

            //filtteroi vain oppilaitokset tietyillä oppilaitostyypeilla
            for (var i = 0; i < vastaus.organisaatiot.length; i++) {
                if (vastaus.organisaatiot[i].organisaatiotyypit.indexOf("KOULUTUSTOIMIJA") !== -1) {
                    var arr = [];
                    for (var c = 0; c < vastaus.organisaatiot[i].children.length; c++) {
                        if (!angular.isString(vastaus.organisaatiot[i].children[c].oppilaitostyyppi)) {
                            continue;
                        }
                        var uri = vastaus.organisaatiot[i].children[c].oppilaitostyyppi.split("#")[0];

                        if (typeUris.indexOf(uri) !== -1) {
                            arr.push(vastaus.organisaatiot[i].children[c]);
                            break;
                        }

                    }
                    vastaus.organisaatiot[i].children = arr;
                }
            }

            $scope.alkorganisaatiot = vastaus.organisaatiot;
            //rakennetaan mappi oid -> organisaatio jotta löydetään parentit helposti
            var buildMapFrom = function(orglist) {
                for (var i = 0; i < orglist.length; i++) {
                    var organisaatio = orglist[i];
                    $scope.organisaatiomap[organisaatio.oid] = organisaatio;
                    if (organisaatio.children) {
                        buildMapFrom(organisaatio.children);
                    }
                }
            };
            buildMapFrom(vastaus.organisaatiot);

        });
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
            OrganisaatioService.nimi($scope.model.organisaatiot[0].oid).then(function(vastaus) {
                $modalInstance.close({
                    oid: $scope.model.organisaatiot[0].oid,
                    nimi: vastaus
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
        }
        ;
    }]);


