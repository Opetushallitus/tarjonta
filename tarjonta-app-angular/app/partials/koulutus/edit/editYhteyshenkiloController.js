/* Controllers */
var app = angular.module('app.edit.ctrl');
// Yhteyshenkilö
app.controller('EditYhteyshenkiloCtrl', [
    '$scope',
    '$compile',
    'YhteyshenkiloService',
    'KoulutusConverterFactory',
    'debounce',
    '$routeParams',
    '$log',
    '$route', function($scope, $compile, YhteyshenkiloService, converter, debounce, $routeParams, $log, $route) {
        $scope.init = function(extModel) {
            'use strict';
            $scope.editYhModel = {};
            var orgOid = $route.current.locals.koulutusModel.result.organisaatio !== undefined ?
                $route.current.locals.koulutusModel.result.organisaatio.oid :
                $routeParams.org;
            if (!orgOid) {
                $log.error('organisaatio Oid is unknown!!');
            }
            $log = $log.getInstance('EditYhteyshenkiloCtrl');
            $log.debug('init');
            $scope.yhteyshenkilot = [];
            YhteyshenkiloService.etsi({
                org: [orgOid]
            }).then(function(yhteyshenkilot) {
                if (yhteyshenkilot !== undefined) {
                    $scope.yhteyshenkilot = yhteyshenkilot;
                }
            });

            // Clearing of the contact person data.
            $scope.editYhModel.clearYh = function() {
                extModel.contactPerson = {
                    henkiloTyyppi: 'YHTEYSHENKILO'
                };
            };

            // Sets the contact person to be the one that the user selected from the autocomplete field.
            $scope.editYhModel.selectHenkilo = function(selectedUser) {
                var to = extModel.contactPerson;
                $scope.setValues(to, selectedUser);
            };

            // kopioi data modeliin
            $scope.setValues = function(to, selectedUser) {
                var henkiloOid = selectedUser.oidHenkilo;
                YhteyshenkiloService.haeHenkilo(henkiloOid).then(function(data) {
                    var yhteystiedotRyhma = data.yhteystiedotRyhma;
                    if (yhteystiedotRyhma.length > 0) {
                        for (var r = 0; r < yhteystiedotRyhma.length; r++) {
                            for (var i = 0; i < yhteystiedotRyhma[r].yhteystiedot.length; i++) {
                                var yt = yhteystiedotRyhma[r].yhteystiedot[i];
                                if ('YHTEYSTIETO_PUHELINNUMERO' == yt.yhteystietoTyyppi && yt.yhteystietoArvo) {
                                    to.puhelin = yt.yhteystietoArvo;
                                }
                                else if ('YHTEYSTIETO_SAHKOPOSTI' == yt.yhteystietoTyyppi && yt.yhteystietoArvo) {
                                    to.sahkoposti = yt.yhteystietoArvo;
                                }
                            }
                        }
                    }
                });

                // tehtavanimike
                YhteyshenkiloService.haeOrganisaatiohenkilo(henkiloOid).then(function(data) {
                    for (var i = 0; i < data.length; i++) {
                        if (data[i].organisaatioOid == orgOid) {
                            to.titteli = data[i].tehtavanimike;
                        }
                    }
                });
                if (selectedUser.etunimet) {
                    to.nimi = selectedUser.etunimet;
                }
                if (selectedUser.sukunimi) {
                    if (to.nimi.length > 0) {
                        to.nimi = to.nimi + " ";
                    }
                    to.nimi = to.nimi + selectedUser.sukunimi;
                }
            };
        };
    }
]);
// ECTS
app.controller('EditYhteyshenkiloECTSCtrl', [
    '$scope',
    '$compile',
    'OrganisaatioService',
    'KoulutusConverterFactory',
    '$routeParams',
    '$log',
    '$route', function($scope, $compile, OrganisaatioService, converter, $routeParams, $log, $route) {
        'use strict';
        $scope.editECTSModel = {};
        var orgOid = $route.current.locals.koulutusModel.result.organisaatio !== undefined ?
            $route.current.locals.koulutusModel.result.organisaatio.oid :
            $routeParams.org;
        $log = $log.getInstance('EditYhteyshenkiloECTSCtrl');
        $log.debug('init');
        OrganisaatioService.getECTS(orgOid).then(function(data) {
            if (data !== undefined) {
                $scope.ects = {
                    nimet: data.metadata.hakutoimistoEctsNimi ? data.metadata.hakutoimistoEctsNimi : {},
                    sahkoposti: data.metadata.hakutoimistoEctsEmail ? data.metadata.hakutoimistoEctsEmail : {},
                    puhelin: data.metadata.hakutoimistoEctsPuhelin ? data.metadata.hakutoimistoEctsPuhelin : {}
                };
            }
        });
        /*
         * Clearing of the ects coordinator data.
         */
        $scope.editECTSModel.clearEctsYh = function() {
            $scope.uiModel.ectsCoordinator = {
                henkiloTyyppi: 'ECTS_KOORDINAATTORI'
            };
        };
        /*
         * Sets the ects coordinator to be the one that the user selected
         * from the autocomplete field.
         */
        $scope.editECTSModel.selectEctsHenkilo = function(selectedUser) {
            if ($scope.ects) {
                $scope.uiModel.opetuskielis.uris.every(function(kieliUri) {
                    var kieliUriWithVersion = kieliUri + '#1';
                    if ($scope.ects.nimet[kieliUriWithVersion]) {
                        var info = {};
                        info.nimet = $scope.ects.nimet[kieliUriWithVersion];
                        info.sahkoposti = $scope.ects.sahkoposti[kieliUriWithVersion];
                        info.puhelin = $scope.ects.puhelin[kieliUriWithVersion];
                        angular.copy(info, $scope.uiModel.ectsCoordinator);
                        $scope.uiModel.ectsCoordinator.henkiloTyyppi = 'ECTS_KOORDINAATTORI';
                        return false;
                    }
                });
            }
        };
    }
]);