'use strict';

var app = angular.module('MonikielinenTextArea', ['Koodisto', 'localisation', 'RichTextArea']);

app.directive('mkRichTextarea', function(Koodisto, LocalisationService, $log, $modal) {
	
	function isEmpty(obj) {
		for (var i in obj) {
			return false;
		}
		return true;
	}

    function controller($scope) {

    	if ($scope.model instanceof Array || ((typeof $scope.model)!='object') || $scope.model==null || $scope.model==undefined) {
        	console.log("MODEL FAIL",$scope.model);
    		throw new Error("mkRichTextarea.model must be a non-array object");
    	}

    	$scope.langs = [];
    	$scope.userLangs = window.CONFIG.app.userLanguages;
    	$scope.selectedLangs = [];
    	
    	$scope.selectedTab = {"kieli_fi":true};
    	
    	if (isEmpty($scope.model)) {
    		for (var i in window.CONFIG.app.userLanguages) {
    			var lang = window.CONFIG.app.userLanguages[i];
    			$scope.selectedLangs.push(lang);
    			$scope.model[lang] = "";
    		}
		} else {
    		for (var i in $scope.model) {
    			$scope.selectedLangs.push(i);
    		}
    	}

        // kielikoodit koodistosta
    	$scope.langsPromise = Koodisto.getAllKoodisWithKoodiUri("kieli", LocalisationService.getLocale());
    	$scope.langsPromise.then(function(v) {
            var nc = {};
            for (var i in v) {
                nc[v[i].koodiUri] = v[i].koodiNimi;//{versio: v[i].koodiVersio, nimi: v[i].koodiNimi, uri: v[i].koodiUri};
            }
            $scope.langs = nc;
        });

    }

    return {
        restrict: 'E',
        replace: true,
        templateUrl: "js/shared/directives/mkRichTextarea.html",
        controller: controller,
        scope: {
            model: "=",  // map jossa kieliuri -> teksti, esim. {kieli_fi: "Suomeksi", kieli_sv: "På svenska"}
        	max: "@"	 // maksimimerkkimäärä (ohjeellinen); jos ei määritelty, ei näytetä
        }
    }

});
