'use strict';

var app = angular.module('app.edit.ctrl.alkamispaiva', ['localisation']);

app.directive('alkamispaivaJaKausi', ['$log', '$modal', 'LocalisationService', function($log, $modal, LocalisationService) {
        function controller($scope, $q, $element, $compile) {
            $scope.ctrl = {
                disabledDate: false,
                disableKausi: false,
                koodis: []
            };

            $scope.ctrl.koodis.push({koodiNimi: LocalisationService.t('koulutus.edit.alkamispaiva.ei-valittua-kautta'), koodiUri: -1})

            $scope.$watch("kausiUri", function(valNew, valOld) {
                $scope.ctrl.disabledDate = (valNew !== -1);
                $scope.kausiUiModel.uri = $scope.kausiUri;
            });

            $scope.clearKausiSelection = function() {
                $scope.kausiUri = -1
            }
            
            $scope.onEnableKausi = function($event) {
            	if ($scope.pvms.length>0) {
                	$event.preventDefault();
                	$event.stopImmediatePropagation();
                	
                	// alkamispvm:iä valittu -> näytä vahvistusdialogi
                	var ctrl = $scope.ctrl;
                	var modalInstance = $modal.open({
        				scope: $scope,
        				templateUrl: 'partials/koulutus/edit/alkamispaiva-dialog.html',
        				controller: function($scope) {
        					$scope.ok = function() {
        						ctrl.disableKausi = false;
        						modalInstance.dismiss();
        					}
        					$scope.cancel = function() {
        						modalInstance.dismiss();
        					}        					
        					return $scope;
        				}
        			});
            	}
            }

            $scope.$watch("ctrl.disabledKausi", function(valNew, valOld) {
                if (angular.isUndefined(valNew) || valNew === "" || valNew === true) {
                    if (!angular.isUndefined(valNew)) {
                        $scope.clearKausiSelection();
                    }
                }
            });

            $scope.kausiUiModel.promise.then(function(result) {
                for (var i = 0; i < $scope.kausiUiModel.koodis.length; i++) {
                    $scope.ctrl.koodis.push($scope.kausiUiModel.koodis[i]);
                }
            });
        }

        return {
            restrict: 'E',
            replace: true,
            templateUrl: "partials/koulutus/edit/alkamispaiva-ja-kausi.html",
            controller: controller,
            scope: {
                pvms: "=",
                vuosi: "=",
                kausiUiModel: "=",
                kausiUri: "="
            }
        };
    }]);
