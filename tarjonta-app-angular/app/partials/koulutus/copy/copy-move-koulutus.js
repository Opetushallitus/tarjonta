'use strict';

/* Controllers */

var app = angular.module('app.koulutus.copy.ctrl', []);

app.controller('CopyMoveKoulutusController', ['$modalInstance', 'targetKoulutus', 'targetOrganisaatio',
    'TarjontaService', 'LocalisationService', '$q', '$scope',
    'OrganisaatioService', 'AuthService', 'PermissionService',
    function($modalInstance, targetKoulutus, targetOrganisaatio,
            TarjontaService, LocalisationService, $q, $scope,
            OrganisaatioService, AuthService, PermissionService) {

        // Tähän populoidaan formin valinnat:
        console.log("resetting form selections");
        $scope.model = {
            errors: [],
            targetKoulutus: targetKoulutus,
            targetOrganisaatio: targetOrganisaatio,
            koulutustyyppi: undefined,
            organisaatiot: [],
            mode: 'COPY'
        };
        $scope.lkorganisaatio = $scope.lkorganisaatio || {currentNode: undefined};
        // Watchi valitulle organisaatiolle
        $scope.$watch('lkorganisaatio.currentNode', function(organisaatio, oldVal) {
            console.log("oprganisaatio valittu", organisaatio);
            //XXX nyt vain yksi organisaatio valittavissa
            if ($scope.model.organisaatiot.length == 0 && organisaatio !== undefined && organisaatio.oid !== undefined && $scope.model.organisaatiot.indexOf(organisaatio) == -1) {
                lisaaOrganisaatio(organisaatio);
            }
        });
        $scope.valitut = $scope.valitut || [];
        $scope.organisaatiomap = $scope.organisaatiomap || {};
        $scope.sallitutKoulutustyypit = $scope.sallitutKoulutustyypit || [];
        $scope.lkorganisaatiot = {};
        // haetaan organisaatihierarkia joka valittuna kälissä tai jos mitään ei ole valittuna organisaatiot joihin käyttöoikeus
        OrganisaatioService.etsi({oidRestrictionList: $scope.model.targetOrganisaatio.oid || AuthService.getOrganisations()}).then(function(vastaus) {
            //console.log("asetetaan org hakutulos modeliin.");
            $scope.lkorganisaatiot = vastaus.organisaatiot;
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
            for (var i = 0; i < vastaus.organisaatiot.length; i++) {
                var oppilaitostyypit = haeOppilaitostyypit(vastaus.organisaatiot[i]);
                promises.push(oppilaitostyypit);
                oppilaitostyypit.then(function(tyypit) {
                    for (var i = 0; i < tyypit.length; i++) {
                        if (oltUrit.indexOf(tyypit[i]) == -1) {
                            oltUrit.push(tyypit[i]);
                        }
                    }
                });
            }
            $q.all(oltpromises).then(function() {
                $q.all(promises).then(function() {
                    paivitaKoulutustyypit(oltUrit);
                    //console.log("all done!");
                });
            });
        });
        var lisaaOrganisaatio = function(organisaatio) {
            $scope.model.organisaatiot.push(organisaatio);
            var oppilaitostyypit = haeOppilaitostyypit(organisaatio);
            oppilaitostyypit.then(function(data) {
                paivitaKoulutustyypit(data);
            });
        };
        var paivitaKoulutustyypit = function(oppilaitostyypit) {
            var sallitutKoulutustyypit = [];
            if (oppilaitostyypit !== undefined) {
                for (var i = 0; i < oppilaitostyypit.length; i++) {
                    var oppilaitostyyppiUri = oppilaitostyypit[i];
                    console.log("getting koulutustyyppi for ", oppilaitostyyppiUri);
                    var koulutustyypit = []; //tarvitaanko???
                    //console.log("got:", koulutustyypit);
                    if (koulutustyypit) {
                        for (var j = 0; j < koulutustyypit.length; j++) {
                            if (sallitutKoulutustyypit.indexOf(koulutustyypit[j]) == -1) {
                                sallitutKoulutustyypit.push(koulutustyypit[j]);
                            }
                        }
                    } else {
                        console.log("oppilaitostyypille: '", oppilaitostyyppiUri, "' ei löydy koulutustyyppejä");
                    }

                }
                //console.log("asetetaan koulutustyypit: ", sallitutKoulutustyypit);
            }
            $scope.sallitutKoulutustyypit = sallitutKoulutustyypit;
        };
        //alusta koulutustyypit (kaikki valittavissa olevat)
        paivitaKoulutustyypit();
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
                if (organisaatio.oppilaitostyyppi !== undefined && oppilaitostyypit.indexOf(organisaatio.oppilaitostyyppi) == -1) {
                    oppilaitostyypit.push(organisaatio.oppilaitostyyppi);
                }
            };
            if (organisaatio.organisaatiotyypit.indexOf("KOULUTUSTOIMIJA") != -1 && organisaatio.children !== undefined) {
                //	koulutustoimija, kerää oppilaitostyypit lapsilta (jotka oletetaan olevan oppilaitoksia)
                for (var i = 0; i < organisaatio.children.length; i++) {
                    addTyyppi(organisaatio.children[i]);
                }
                deferred.resolve(oppilaitostyypit);
            }

            else if (organisaatio.organisaatiotyypit.indexOf("OPPILAITOS") != -1 && organisaatio.oppilaitostyyppi !== undefined) {
                //oppilaitos, kerää tyyppi
                addTyyppi(organisaatio);
                deferred.resolve(oppilaitostyypit);
            }

            else if (organisaatio.organisaatiotyypit.indexOf("OPETUSPISTE") != -1) {
                //opetuspiste, kerää parentin tyyppi
                var parent = $scope.organisaatiomap[organisaatio.parentOid];
                if (undefined !== parent) {
                    addTyyppi(parent);
                    deferred.resolve(oppilaitostyypit);
                } else {
                    //parentti ei ole saatavilla, kysytään organisaatioservicestä
                    console.log("organisaatio:", organisaatio);
                    OrganisaatioService.etsi({oidRestrictionList: organisaatio.parentOid}).then(function(vastaus) {
                        $scope.organisaatiomap[organisaatio.parentoid] = vastaus.organisaatiot[0].oppilaitostyyppi;
                        deferred.resolve([vastaus.organisaatiot[0].oppilaitostyyppi]);
                    }, function() {
                        deferred.resolve([]);
                    });
                }
            } else {
                console.log("Tuntematon organisaatiotyyppi:", organisaatio.organisaatiotyypit);
            }
            return deferred.promise;
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
                console.log("Authorization check : " + authResponse.result);

                if (authResponse.status !== 'OK') {
                    //not authenticated
                    return;
                }

                var apiModel = {
                    mode: $scope.model.mode,
                    organisationOids: orgOids
                };
                if ($scope.model.targetKoulutus.length === 1) {

                    TarjontaService.koulutus($scope.model.targetKoulutus[0].oid).copyAndMove(apiModel, function(response) {
                        if (response.status === 'OK') {
                            $modalInstance.close(response);
                        } else {
                            if (!angular.isUndefined(response.errors) && response.errors.length > 0) {

                                for (var i = 0; i < response.errors.length; i++) {
                                    $scope.model.errors.push({msg: LocalisationService.t(response.errors[i].errorMessageKey)});
                                }

                                $scope.model.errors.push({msg: LocalisationService.t("koulutus.copy.error.yleisvirhe", [])});
                                $scope.model.btnDisableRemove = true;
                            }
                        }
                    });
                } else {
//                    var komotoOids = [];
//                    for (var i = 0; i < $scope.model.targetKoulutus.length; i++) {
//                        komotoOids.push($scope.model.targetKoulutus[i].oid);
//                    }

                    apiModel.komotoOids = $scope.model.targetKoulutus;

                    TarjontaService.koulutus().copyAndMoveMultiple(apiModel, function(response) {
                        if (response.status === 'OK') {
                            $modalInstance.close(response);
                        } else {
                            if (!angular.isUndefined(response.errors) && response.errors.length > 0) {

                                for (var i = 0; i < response.errors.length; i++) {
                                    $scope.model.errors.push({msg: LocalisationService.t(response.errors[i].errorMessageKey)});
                                }

                                $scope.model.errors.push({msg: LocalisationService.t("koulutus.copy.error.yleisvirhe", [])});
                                $scope.model.btnDisableRemove = true;
                            }
                        }
                    });
                }
            });
        };
        /**
         * Tarkista että Koulutustyyppi valittu ja validi vrt valittu organisaatio
         */
        $scope.koulutustyyppiValidi = function() {
            return $scope.sallitutKoulutustyypit.indexOf($scope.model.koulutustyyppi) != -1;
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


