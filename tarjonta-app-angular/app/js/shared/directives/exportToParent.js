'use strict';

/*
 * Apudirektiivi scope-muuttujien julkaisemiseksi parent-konteksteissa; mahdollistaa esim. direktivissä
 * luotuun formiin käsiksipääsemisen direktiiviä kutsuvasta kontrollerista.
 * 
 * Käytännössä linkittää tämän direktiivin 'model'-parametrin osoittaman olion yläscopeen 'name' -parametrin osoittamalla nimellä.
 * Em. yläscope etsitään  käymällä scope-hierarkiaa läpi kunnes löytyy scope, jonka 'condition' -parametrin
 * palauttama funktio palauttaa toden ajettaessa sitä em. scope parametrinä. Jos sopiva scopea ei löydy, muuttujaa ei linkitetä.
 * Mikäli funktiota ei ole määritelty, linkitetään yhtä scope-tasoa ylempään tasoon, jos sellainen on olemassa.
 */
var app = angular.module('ExportToParent', []);
app.directive('exportToParent', function($log, $modal) {

    function controller($scope) {
    	
    	var match = $scope.condition();
    	if (!match) {
    		match = function(scope) {
    			return true;
    		}
    	}

    	$scope.linkedScope = null;
    	
    	for (var s = $scope.$parent.$parent; s!=null; s = s.$parent) {
    		if (match(s)) {
    			$scope.linkedScope = s;
    			break;
    		}
    	}
    	
    	if ($scope.linkedScope) {
    		$scope.$watch("model", function(n,o){
    			$scope.linkedScope[$scope.name] = $scope.model;
    		});
    	}
    	
    }

    return {
        restrict: 'E',
        replace: true,
        template: "<span></span>",
        controller: controller,
        scope: {
        	model: "=",		// model
        	name: "@",		// scope-muuttujan nimi
        	condition: "&"	// ehtofunktio
        }
    }

});
