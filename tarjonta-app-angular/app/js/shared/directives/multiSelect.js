'use strict';

var app = angular.module('MultiSelect', ['pasvaz.bindonce']);

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
		$scope.preselection = [];
		$scope.names = {};

		if ($scope.columns == undefined) {
			$scope.columns = 1;
		}		

		if ($scope.display == undefined) {
			$scope.display = "checklist";
		}
		
		if ($scope.key == undefined) {
			$scope.key = "koodiUri";
		}
		
		if ($scope.value == undefined) {
			$scope.value = "koodiNimi";
		}
		
		// (multi)select-valinta
		$scope.onPreselection = function(preselection) {
			for (var i in preselection) {
				if ($scope.selection.indexOf(preselection[i])==-1) {
					$scope.selection.push(preselection[i]);
				}
			}
			$scope.selection.sort(function(a, b){
				return $scope.names[a].localeCompare($scope.names[b]);
			});
		}

		// checkbox-valinta
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
			$scope.names[e[$scope.key]] = e[$scope.value];
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
        	display: "@", // checklist | dualpane
        	columns: "@", // sarakkeiden määrä (vain checklist)
        	key: "@", // arvo-avain (vakio: koodiUri)
        	value: "@", // nimi-avain (vakio: koodiNimi)
        	model: "=", // map jossa arvo->nimi
        	selection: "=" // lista jonne valinnat päivitetään
        }
    }
    
});
