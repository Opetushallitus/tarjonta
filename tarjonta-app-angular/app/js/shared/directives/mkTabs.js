'use strict';

var app = angular.module('MonikielinenTabs', ['Koodisto', 'localisation', 'pasvaz.bindonce']);

app.directive('mkTabs', function(Koodisto, LocalisationService, $log, $modal) {
	
	var userLangs = window.CONFIG.app.userLanguages;

    function controller($scope) {

    	$scope.langs = [];
        $scope.codes = {};
        
        if (!$scope.model) {
            $scope.model = [];
        }       
        
        function updateLangs() {
        	var ret = [];
        	
        	for (var i in $scope.model) {
        		var kieliUri = $scope.model[i];
        		if (ret.indexOf(kieliUri)==-1) {
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
        	
        	if (!$scope.selection) {
        		$scope.selection = $scope.langs[0];
        	}
        }
        
        $scope.onSelect = function(kieli) {
        	$scope.selection = kieli;
        }
        
        // kielikoodit koodistosta
        Koodisto.getAllKoodisWithKoodiUri("kieli", LocalisationService.getLocale()).then(function(v) {
            var nc = {};
            for (var i in v) {
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
        templateUrl: "js/shared/directives/mkTabs.html",
        controller: controller,
        scope: {
            model: "=",			// lista kieliureista TAI syöte decode:lle
            selection: "="		// valittu kieliuri
        }
    };

});
