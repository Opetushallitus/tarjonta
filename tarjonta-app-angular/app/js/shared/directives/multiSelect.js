'use strict';

var app = angular.module('MultiSelect', ['ngResource']);

app.directive('multiSelect',function($log) {
	
	
	function columnize(values, cols) {
		var ret = [];
		var row = [];
		for (var k in values) {
			if (row.length==cols) {
				ret.push(row);
				row = [];
			}
			row.push(values[k]);
		}

		if (row.length>0) {
			ret.push(row);
		}
	
		return ret;
	}
	
	
	function controller($scope) {
		$scope.items = [];

		if ($scope.columns == null) {
			$scope.columns = 1;
		}		

		if ($scope.display == null) {
			$scope.display = "checklist";
		}		
		
		if ($scope.key == null) {
			$scope.key = "koodiUri";
		}
		
		if ($scope.value == null) {
			$scope.value = "koodiNimi";
		}
		
		$scope.toggle = function(k) {
			var p = $scope.selection.indexOf(k);
			if (p==-1) {
				$scope.selection.push(k);
			} else {
				$scope.selection.splice(p,1);
			}
		}
		
		for (var k in $scope.model) {
			var e = $scope.model[k];
			$scope.items.push({
				selected: $scope.selection.indexOf(e[$scope.key])!=-1,
				key: e[$scope.key],
				value: e[$scope.value]
			});
		}
		
		$scope.items.sort(function(a, b){
			return a.value.localeCompare(b.value);
		});
		
		$scope.rows = columnize($scope.items, $scope.columns);		
	}

    return {
        restrict:'E',
        replace:true,
        templateUrl : "js/shared/directives/multiSelect.html",
        controller: controller,
        scope: {
        	display: "@", // checklist
        	columns: "@", // sarakkeiden määrä
        	key: "@", // arvo-avain (vakio: koodiUri)
        	value: "@", // nimi-avain (vakio: koodiNimi)
        	model: "=", // map jossa arvo->nimi
        	selection: "=" // lista jonne valinnat päivitetään
        }
    }
    
});
