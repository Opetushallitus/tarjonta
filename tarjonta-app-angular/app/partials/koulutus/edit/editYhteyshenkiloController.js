'use strict';

/* Controllers */

var app = angular.module('app.edit.ctrl', ['Koodisto', 'Yhteyshenkilo', 'ngResource', 'ngGrid', 'imageupload', 'MultiSelect', 'OrderByNumFilter', 'localisation', 'MonikielinenTextField', 'ControlsLayout']);

app.controller('editYhteyshenkiloCtrl', ['$scope', '$compile', 'YhteyshenkiloService', 'KoulutusConverterFactory', 'debounce', '$routeParams', '$log',
    function($scope, $compile, YhteyshenkiloService, converter, debounce, $routeParams, $log) {

        $log = $log.getInstance("EditYhteyshenkiloCtrl");
        $log.debug("init");

        $scope.editYhModel = {data: []};

        $scope.getOrgOid = function() {
            var orgOid = !angular.isUndefined($scope.koulutusModel)
                    && !angular.isUndefined($scope.koulutusModel.result)
                    && !angular.isUndefined($scope.koulutusModel.result.organisaatio)
                    ? $scope.koulutusModel.result.organisaatio.oid : $routeParams.org;
            console.log("orgOid", orgOid);
            return orgOid;
        };

        var orgOid = $scope.getOrgOid();

        YhteyshenkiloService.etsi({org: [orgOid]}).then(function(data) {
            if (data !== undefined) {
                $scope.editYhModel.data = data.results;
                $scope.editYhModel.henkilotFetched = true;
            }
        });


        /*
         * Clearing of the contact person data.
         */
        $scope.editYhModel.clearYh = function() {
            $scope.uiModel.contactPerson = {henkiloTyyppi: 'YHTEYSHENKILO'};
        };

        /*
         * Clearing of the ects coordinator data.
         */
        $scope.editYhModel.clearEctsYh = function() {
            $scope.uiModel.ectsCoordinator = {henkiloTyyppi: 'ECTS_KOORDINAATTORI'};
        };

        /*
         * Sets the contact person to be the one that the user selected from the autocomplete field.
         */
        $scope.editYhModel.selectHenkilo = function SelectHenkilo(selectedUser) {
            var to = $scope.uiModel.contactPerson;
            $scope.setValues(to, selectedUser);
        };


        /*
         * Sets the ects coordinator to be the one that the user selected from the autocomplete field.
         */
        $scope.editYhModel.selectEctsHenkilo = function(selectedUser) {
            console.log("selecting ectshenkilö");
            var to = $scope.uiModel.ectsCoordinator;
            $scope.setValues(to, selectedUser);
        };


        /**
         * kopioi data modeliin
         */
        $scope.setValues = function(to, selectedUser) {
            var orgOid = $scope.getOrgOid();

            var henkiloOid = selectedUser.oidHenkilo;

            YhteyshenkiloService.haeHenkilo(henkiloOid).then(function(data) {
                //console.log("henkilo data", data);
                var yhteystiedotRyhma = data.yhteystiedotRyhma;
                if (yhteystiedotRyhma.length > 0) {
                    for (var i = 0; i < yhteystiedotRyhma[0].yhteystiedot.length; i++) {
                        var yt = yhteystiedotRyhma[0].yhteystiedot[i];
                        if ("YHTEYSTIETO_PUHELINNUMERO" == yt.yhteystietoTyyppi) {
                            to.puhelin = yt.yhteystietoArvo;
                        } else if ("YHTEYSTIETO_SAHKOPOSTI" == yt.yhteystietoTyyppi) {
                            to.sahkoposti = yt.yhteystietoArvo;
                        }
                    }
                }

            });

            //tehtavanimike
            YhteyshenkiloService.haeOrganisaatiohenkilo(henkiloOid).then(function(data) {
                for (var i = 0; i < data.length; i++) {
                    if (data[i].organisaatioOid == orgOid) {
                        to.titteli = data[i].tehtavanimike;
                    }
                }
            });

            to.etunimet = selectedUser.etunimet;
            to.sukunimi = selectedUser.sukunimi;
        };

    }]);