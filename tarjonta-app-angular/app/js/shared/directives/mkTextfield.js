'use strict';

var app = angular.module('MonikielinenTextField', ['Koodisto', 'localisation', 'pasvaz.bindonce']);

app.directive('mkTextfield',function(Koodisto, LocalisationService, $log, $modal) {
		
	function controller($scope) {
		
		$scope.codes = {};
		
		if (!$scope.model) {
			$scope.model = {};
		}
		
		if (!$scope.init) {
			$scope.init = [];
		}
		
		$scope.data = [];
		
		$scope.updateModel = function(){
			var m = {};
			for (var i in $scope.data) {
				if ($scope.data[i].value.length>0) {
					m[$scope.data[i].uri] = $scope.data[i].value;
				}
			}
			$scope.model = m;
		};
		
		// kielikoodit koodistosta
		Koodisto.getAllKoodisWithKoodiUri("kieli", LocalisationService.getLocale()).then(function(v){
			var nc = {};
			for (var i in v) {
				nc[v[i].koodiUri] = v[i].koodiNimi;
			}
			$scope.codes = nc;
		});
		
		// data
		for (var i in $scope.model) {
			$scope.data.push({uri:i, value:$scope.model[i], removable: $scope.init.indexOf(i)==-1 });
		}
		
		// initissä annetut kielet näkyviin
		for (var i in $scope.init) {
			var lang = $scope.init[i];
			if (!$scope.model[lang]) {
				data.push({uri:lang, value:"", removable: false});
			}
		}
		
		// kielen poisto
		$scope.removeLang = function(uri) {
			var nm = [];
			for (var i in $scope.data) {
				if ($scope.data[i].uri != uri) {
					nm.push($scope.data[i]);
				}
			}
			$scope.data = nm;
			$scope.updateModel();
		}
		
		// kielen lisäys
		$scope.addLang = function() {
			var ps = $scope;
			var ns = $scope.$new();
			ns.codes = {};
			
			for (var i in $scope.codes) {
				if ($scope.model[i] == undefined) {
					ns.codes[i] = $scope.codes[i];
				}
			}

	    	$modal.open({
				controller: function($scope, $modalInstance) {
					$scope.cancel = function() {
						$modalInstance.dismiss();
					};
					$scope.select = function(lang) {
						$modalInstance.close();
						$scope.data.push({uri:lang, value:"", removable: true});
						$scope.updateModel();
					};					
				},
				templateUrl: "js/shared/directives/mkTextfield-addlang.html",
				scope: ns
			});		
			
		}
	}

    return {
        restrict:'E',
        replace:true,
        templateUrl : "js/shared/directives/mkTextfield.html",
        controller: controller,
        scope: {
        	init: "=", // lista kieli(urei)sta jotka näytetään vakiona (ja joita ei siis voi poistaa)
        	model: "=", // map jossa kieliuri -> teksti
        	//required: "@" // jos tosi, vähintään yksi arvo vaaditaan
        }
    }
    
});
