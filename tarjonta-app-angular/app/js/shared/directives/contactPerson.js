var app = angular.module('Contact', ['Yhteyshenkilo']);
app.directive('contactPerson', function(YhteyshenkiloService) {
    'use strict';
    function controller($scope) {
        $scope.editYhModel = {};
        if (!$scope.orgOid) {
            console.error('organisaatio Oid is unknown!!');
        }
        $scope.yhteyshenkilot = [];
        YhteyshenkiloService.etsi({
            organisaatioOids: [$scope.orgOid]
        }).then(function(yhteyshenkilot) {
            if (yhteyshenkilot !== undefined) {
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
                var yhteystiedotRyhma = data.yhteystiedotRyhma;
                if (yhteystiedotRyhma.length > 0) {
                    for (var r = 0; r < yhteystiedotRyhma.length; r++) {
                        for (var i = 0; i < yhteystiedotRyhma[r].yhteystieto.length; i++) {
                            var yt = yhteystiedotRyhma[r].yhteystieto[i];
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
            to.nimi = selectedUser.nimi;
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