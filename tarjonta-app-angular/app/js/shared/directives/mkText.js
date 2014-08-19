'use strict';

var app = angular.module('MonikielinenText', ['Koodisto', 'localisation', 'pasvaz.bindonce']);

app.directive('mkText', function(Koodisto, LocalisationService, $log, $modal) {
    
    $log = $log.getInstance("<mkText>");
	
    var userLangs = window.CONFIG.app.userLanguages;

    function controller($scope) {

    	$scope.langs = [];
        $scope.codes = {};
        
        if (!$scope.model) {
            $scope.model = {};
        }       
        
        if (!$scope.display) {
        	$scope.display = "rows";
        }
        
        $scope.getEmptyMessage = function() {
        	return $scope.ttEmpty ? LocalisationService.t($scope.ttEmpty) : null;
    	}
        
        $scope.getDisplay = function() {
        	for (var i in $scope.model) {
        		// model ei tyhjä -> display-arvon mukainen arvo
        		return $scope.display;
        	}
        	return null;
        }
        
        function updateLangs() {
        	var ret = [];
        	
        	for (var kieliUri in $scope.model) {
        		if ($scope.model[kieliUri]) {
            		ret.push(kieliUri);
        		}
            }

        	ret.sort(function(a,b){
        		var ap = userLangs.indexOf(a);
        		var bp = userLangs.indexOf(b);
        		if (ap!=-1 && bp!=-1) {
        			return ap-bp;
        		}
        		if (ap!=-1) {
        			return -1;
        		}
        		if (bp!=-1) {
        			return 1;
        		}
        		
        		return $scope.codes[a].nimi.localeCompare($scope.codes[b].nimi);
        	});
        	
        	$scope.langs = ret;
        }
        
        // kielikoodit koodistosta
        Koodisto.getAllKoodisWithKoodiUri("kieli", LocalisationService.getLocale()).then(function(v) {
            var nc = {};
            for (var i in v) {
                // Lokalisointiavaimet voivat olla kahta eri muotoa: "kieli_fi#1" tai "kieli_fi". Tämän takia ne
                // tallennetaan myös kahteen eri taulukkoon.
                nc[v[i].koodiUri + "#" + v[i].koodiVersio] = {versio: v[i].koodiVersio, nimi: v[i].koodiNimi, uri: v[i].koodiUri};
                nc[v[i].koodiUri] = {versio: v[i].koodiVersio, nimi: v[i].koodiNimi, uri: v[i].koodiUri};

            }
            $scope.codes = nc;
            updateLangs();
        });
        
        $scope.$watch("model", function(nv, ov){
        	if (!angular.equals(ov,nv)) {
        		updateLangs();
        	}
        });
        
    }

    return {
        restrict: 'E',
        replace: true,
        templateUrl: "js/shared/directives/mkText.html",
        controller: controller,
        scope: {
        	display: "@",		// näyttötapa: tabs | rows 
            model: "=",			// map jossa kieliuri -> teksti
            ttEmpty: "@"		// teksti, joka näytetään jos model on tyhjä
        }
    };

});
