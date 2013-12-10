'use strict';

var app = angular.module('MultiSelect', ['pasvaz.bindonce']);

app.directive('multiSelect', function($log) {

    function columnize(values, cols) {
        var ret = [];
        var row = [];
        for (var k in values) {
            if (row.length == cols) {
                ret.push(row);
                row = [];
            }
            row.push(values[k]);
        }

        if (row.length > 0) {
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
                if ($scope.selection.indexOf(preselection[i]) == -1) {
                    $scope.selection.push(preselection[i]);
                }
            }
            // TODO orderWith -tuki
            $scope.selection.sort(function(a, b) {
                return $scope.names[a].localeCompare($scope.names[b]);
            });
        }

        // salli valintojen muuttaminen "ulkopuolelta"
        $scope.$watch('selection', function(newValue, oldValue){
           	for(var i=0;i<$scope.items.length;i++) {
            	var item = $scope.items[i];
           		if(newValue.indexOf(item.key)==-1 && item.selected){
       				item.selected = false;
           		} else if(newValue.indexOf(item.key)!=-1 && !item.selected) {
           			item.selected = true;
           		}            		
           	}
        });
        	
        // checkbox-valinta
        $scope.toggle = function(k) {
            var p = $scope.selection.indexOf(k);
            if (p == -1) {
                $scope.selection.push(k);
            } else {
                $scope.selection.splice(p, 1);
            }
        }

        //a hack: scope is missing in promise function?
        var key = $scope.key;
        var value = $scope.value;
        var columns = $scope.columns;
    	var cw = $scope.orderWith();
        
        var init = function(model) {


            for (var k in model) {
                var e = model[k];
                var w = 0;
                if (cw) {
                	w = cw.indexOf(e[key]);
                    if (w==-1) {
                    	w = cw.length;
                    }
                }
                //console.log("cw="+cw+" -> w="+w);
                $scope.items.push({
                    selected: $scope.selection.indexOf(e[key]) !== -1,
                    key: e[key],
                    value: e[value],
                    orderWith: w
                });
                $scope.names[e[key]] = e[value];
            }

            $scope.items.sort(function(a, b) {
                return a.orderWith < b.orderWith ? -1 : a.orderWith > b.orderWith ? 1 : a.value.localeCompare(b.value);
            });
            
            //console.log("ITEMS", $scope.items);

            $scope.rows = columnize($scope.items, columns);
        }

        if (!angular.isUndefined($scope.promise)) {
            $scope.promise.then(function(result) {
                init(result);
            });
        } else {
            init($scope.model);
        }
    }

    return {
        restrict: 'E',
        replace: true,
        templateUrl: "js/shared/directives/multiSelect.html",
        controller: controller,
        scope: {
            display: "@", // checklist | dualpane
            columns: "@", // sarakkeiden määrä (vain checklist)
            key: "@", // arvo-avain (vakio: koodiUri)
            value: "@", // nimi-avain (vakio: koodiNimi)
            orderWith: "&", // lista avaimista jotka järjestetään ensimmäisiksi
            model: "=", // map jossa arvo->nimi
            promise: "=", // async TODO yhdistä modeliin
            selection: "=" // lista jonne valinnat päivitetään
        }
    }

});
