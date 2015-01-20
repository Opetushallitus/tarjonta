var app = angular.module('app.koulutus.extend.ctrl', []);

app.controller('ExtendKoulutusController', ['$modalInstance', 'targetKoulutus', 'targetOrganisaatio',
    'TarjontaService', 'LocalisationService', '$q', '$scope',
    'OrganisaatioService', 'AuthService', 'PermissionService', '$location',
    'KoulutusService',
    function($modalInstance, targetKoulutus, targetOrganisaatio,
            TarjontaService, LocalisationService, $q, $scope,
            OrganisaatioService, AuthService, PermissionService, $location, KoulutusService) {

        'use strict';

        // Tähän populoidaan formin valinnat:
        $scope.model = {
            text: {
                help: LocalisationService.t('koulutus.extend.help', [targetKoulutus[0].nimi])
            },
            errors: [],
            targetKoulutus: targetKoulutus,
            targetOrganisaatio: targetOrganisaatio,
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
        $scope.organisaatiomap = $scope.organisaatiomap || {};
        $scope.alkorganisaatiot = {};

        var promises = [];
        var deferred = $q.defer();
        promises.push(deferred.promise);

        /*
         * Hakee oppilaitostyypit organisaatiolle, koulutustoimijalle haetaan allaolevista oppilaitoksista,
         * oppilaitoksen tyypit tulee oppilaitokselta, toimipisteen tyyppi typee ylemmän tason oppilaitokselta.
         * TODO lisää testi
         */
        var haeOppilaitostyypit = function(organisaatio) {

            var deferred = $q.defer();
            var oppilaitostyypit = [];
            /*
             * Lisää organisaation oppilaitostyyppin (koodin uri) arrayhin jos se != undefined ja ei jo ole siinä
             */
            var addTyyppi = function(organisaatio) {
                if (organisaatio.oppilaitostyyppi !== undefined
                    && oppilaitostyypit.indexOf(organisaatio.oppilaitostyyppi) == -1) {
                    oppilaitostyypit.push(organisaatio.oppilaitostyyppi);
                }
            };
            if (organisaatio.organisaatiotyypit.indexOf('KOULUTUSTOIMIJA') != -1
                && organisaatio.children !== undefined) {
                //	koulutustoimija, kerää oppilaitostyypit lapsilta (jotka oletetaan olevan oppilaitoksia)
                for (var i = 0; i < organisaatio.children.length; i++) {
                    addTyyppi(organisaatio.children[i]);
                }
                deferred.resolve(oppilaitostyypit);
            } else if (organisaatio.organisaatiotyypit.indexOf('OPPILAITOS') != -1
                && organisaatio.oppilaitostyyppi !== undefined) {
                //oppilaitos, kerää tyyppi
                addTyyppi(organisaatio);
                deferred.resolve(oppilaitostyypit);
            } else if (organisaatio.organisaatiotyypit.indexOf('TOIMIPISTE') != -1) {
                //opetuspiste, kerää parentin tyyppi
                var parent = $scope.organisaatiomap[organisaatio.parentOid];
                if (undefined !== parent) {
                    addTyyppi(parent);
                    deferred.resolve(oppilaitostyypit);
                } else {
                    //parentti ei ole saatavilla, kysytään organisaatioservicestä
                    OrganisaatioService.etsi({oidRestrictionList: organisaatio.parentOid}).then(function(vastaus) {
                        $scope.organisaatiomap[organisaatio.parentoid] = vastaus.organisaatiot[0].oppilaitostyyppi;
                        deferred.resolve([vastaus.organisaatiot[0].oppilaitostyyppi]);
                    }, function() {
                        deferred.resolve([]);
                    });
                }
            } else {
                console.log('Tuntematon organisaatiotyyppi:', organisaatio.organisaatiotyypit);
            }
            return deferred.promise;
        };

        // haetaan organisaatihierarkia joka valittuna kälissä tai
        // jos mitään ei ole valittuna organisaatiot joihin käyttöoikeus
        TarjontaService.getKoulutus({
            oid: targetKoulutus[0].oid
        }).$promise.then(function(response) {
            var koulutus = response.result;
            var orgOids = koulutus.opetusJarjestajat;
            var ownOrganizations = AuthService.getOrganisations();
            var filteredOrganizations = [];
            var promises = [];

            // Looppaa kaikki organisaatiot ja hyväksy vain ne, joiden
            // oid tai parentOidPath sisältää oman organisaation
            _.each(ownOrganizations, function(orgOid) {
                var deferred = $q.defer();
                promises.push(deferred.promise);
                OrganisaatioService.byOid(orgOid).then(function(org) {
                    if (_.intersection(orgOids, org.oidAndParentOids).length > 0) {
                        filteredOrganizations.push(orgOid);
                        deferred.resolve();
                    }
                });
            });

            $q.all(promises).then(function() {
                if (filteredOrganizations.length > 0) {
                    OrganisaatioService.etsi({oidRestrictionList: filteredOrganizations}).then(function(vastaus) {
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
                        //hakee kaikki valittavissa olevat koulutustyypit
                        var oltUrit = [];
                        var oltpromises = [];
                        _.each(vastaus.organisaatiot, function(org) {
                            var oppilaitostyypit = haeOppilaitostyypit(org);
                            promises.push(oppilaitostyypit);
                            oppilaitostyypit.then(function(tyypit) {
                                for (var i = 0; i < tyypit.length; i++) {
                                    if (oltUrit.indexOf(tyypit[i]) == -1) {
                                        oltUrit.push(tyypit[i]);
                                    }
                                }
                            });
                        });
                        $q.all(oltpromises).then(function() {
                            $q.all(promises).then(function() {
                                // TODO: korjaa tämä
                                // paivitaKoulutustyypit(oltUrit);
                            });
                        });
                    });
                }
            });
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
    }]);