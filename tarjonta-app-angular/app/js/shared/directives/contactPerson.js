'use strict';
var app = angular.module('Contact', ['Yhteyshenkilo']);
app.directive('contactPerson', function(YhteyshenkiloService) {
    function controller($scope) {
        $scope.editYhModel = {};
        if (!$scope.orgOid) {
            $log.error('organisaatio Oid is unknown!!');
        }
        $log = $log.getInstance('contactPerson');
        $log.debug('init');
        $scope.yhteyshenkilot = [];
        YhteyshenkiloService.etsi({
            org: [$scope.orgOid]
        }).then(function(yhteyshenkilot) {
            if (yhteyshenkilot !== undefined) {
                for (var i = 0; i < yhteyshenkilot.length; i++) {
                    yhteyshenkilot[i].nimi = yhteyshenkilot[i].etunimet + ' ' + yhteyshenkilot[i].sukunimi;
                }
                $scope.yhteyshenkilot = yhteyshenkilot;
            }
        });
        /*
             * Clearing of the contact person data.
             */
        $scope.editYhModel.clearYh = function() {
            $scope.model.contactPerson = {
                henkiloTyyppi: 'YHTEYSHENKILO'
            };
        };
        /*
             * Sets the contact person to be the one that the user selected
             * from the autocomplete field.
             */
        $scope.editYhModel.selectHenkilo = function(selectedUser) {
            var to = $scope.model.contactPerson;
            $scope.setValues(to, selectedUser);
        };
        /**
             * kopioi data modeliin
             */
        $scope.setValues = function(to, selectedUser) {
            var henkiloOid = selectedUser.oidHenkilo;
            YhteyshenkiloService.haeHenkilo(henkiloOid).then(function(data) {
                // console.log("henkilo data", data);
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
            to.etunimet = selectedUser.etunimet;
            to.sukunimi = selectedUser.sukunimi;
        };
    }
    return {
        restrict: 'E',
        replace: true,
        templateUrl: 'js/shared/directives/contactPerson.html',
        controller: controller,
        scope: {
            model: '=',
            orgOid: '@'
        }
    };
});