var app = angular.module('Oppiaineet', []);

app.directive('oppiaineet', function() {
    'use strict';
    return {
        templateUrl: 'js/shared/directives/oppiaineet.html',
        scope: false,
        controller: function($scope, $http, Config, LocalisationService) {
            $scope.model.oppiaineet = $scope.model.oppiaineet || [];
            $scope.oppiaineCtrl = {
                input: {},

                translate: LocalisationService,

                getOppiaineet: function(oppiaine, kieliKoodi) {
                    return $http.get(window.url("tarjonta-service.koulutus.oppiaineet"), {
                        params: {
                            oppiaine: oppiaine,
                            kieliKoodi: kieliKoodi
                        }
                    }).then(function(response) {
                        return response.data.result;
                    });
                },

                addOppiaine: function(oppiaine, kieliKoodi) {
                    if (!oppiaine) {
                        return;
                    }
                    if (typeof oppiaine === 'string') {
                        oppiaine = {
                            oppiaine: oppiaine,
                            kieliKoodi: kieliKoodi
                        };
                    }
                    oppiaine.oppiaine = $.trim(oppiaine.oppiaine).toLowerCase();
                    if (!_.findWhere($scope.model.oppiaineet, {
                            oppiaine: oppiaine.oppiaine,
                            kieliKoodi: oppiaine.kieliKoodi
                        })) {
                        $scope.model.oppiaineet.push(oppiaine);
                        $scope.oppiaineCtrl.input[kieliKoodi] = '';
                    }
                },

                removeOppiaine: function(oppiaine) {
                    var index = $scope.model.oppiaineet.indexOf(oppiaine);
                    $scope.model.oppiaineet.splice(index, 1);
                }
            };
        }
    };
});