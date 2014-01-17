'use strict';

var app = angular.module('MultiSelect', ['pasvaz.bindonce','ngGrid', 'localisation']);

app.directive('multiSelect', function($log, $modal, LocalisationService) {

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

        $scope.errors = {
        		required:false,
        		pristine:true,
        		dirty:false,
        		$name:$scope.name
        }

        $scope.titles = [];
        $scope.items = [];
        $scope.preselection = [];
        $scope.names = {};
        
        function updateErrors() {
            $scope.errors.dirty = true;
            $scope.errors.pristine = false;
        	$scope.errors.required = $scope.isrequired && $scope.selection.length==0;
        }
       
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
        
        if (!$scope.ttShowAll) {
        	$scope.ttShowAll = "tarjonta.toiminnot.näytä_kaikki";
        }
    	$scope.txtShowAll = LocalisationService.t($scope.ttShowAll);
        
        if (!$scope.ttShowAllTitle) {
        	$scope.ttShowAllTitle = "tarjonta.toiminnot.valitse";
        }
    	$scope.txtShowAllTitle = LocalisationService.t($scope.ttShowAllTitle);
        
        if ($scope.ttShowAllHelp) {
        	$scope.txtShowAllHelp = LocalisationService.t($scope.ttShowAllHelp);
        }
        
        $scope.combo = {selection: ""};
        
        // autocomplete-valinta
        $scope.onComboSelect = function(v) {
        	
        	for (var i in $scope.names) {
        		if ($scope.names[i] == $scope.combo.selection) {
                	$scope.onPreselection([i]);
        			break;
        		}
        	}
        	
        	$scope.combo.selection = "";
        }
        
        $scope.onShowAll = function() {

        	var ns = $scope.$new();
        	ns.parent = $scope;

        	ns.selection = $scope.selection;
        	ns.items = $scope.items;
        	ns.selecteds = [];
        	
        	
        	for (var i in ns.items) {
        		var r = ns.items[i];
        		if ($scope.selection.indexOf(r.key)!=-1) {
        			ns.selecteds.push(r);
        		}
        	}

            $modal.open({
                controller: function($scope, $modalInstance) {
                	
                	$scope.gridOptions = {
                			data: "items",
                			columnDefs: [{field:'value'}],
                			headerRowHeight:0,
                			showSelectionCheckbox:true,//,
                			selectedItems: $scope.selecteds
                	};
                	
                	$scope.ok = function() {
                		var cs = [];
                		for (var i in $scope.selecteds) {
                			cs.push($scope.selecteds[i].key)
                		}
                		$scope.parent.onSelection(cs);
                        $modalInstance.dismiss();
                	};
                    $scope.cancel = function() {
                        $modalInstance.dismiss();
                    };
                },
                templateUrl: "js/shared/directives/multiSelect-chooser.html",
                scope: ns
            });
        	
        	
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
            updateErrors();
        }

        $scope.onSelection = function(selection) {
        	for (var i in $scope.selection) {
        		var s = $scope.selection[i];
        		if (selection.indexOf(s)==-1) {
        			$scope.selection.splice(i, 1);
        		}
        	}

        	$scope.onPreselection(selection);
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
            updateErrors();
        });
        	
        // checkbox-valinta
        $scope.toggle = function(k) {
            var p = $scope.selection.indexOf(k);
            if (p == -1) {
                $scope.selection.push(k);
            } else {
                $scope.selection.splice(p, 1);
            }
            updateErrors();
       }

        //a hack: scope is missing in promise function?
        var key = $scope.key;
        var value = $scope.value;
        var columns = $scope.columns;
    	var cw = $scope.orderWith();
    	
    	function toObjectArray(model) {
    		var ret = [];
        	for (var k in model) {
        		var e = {};
        		e[key] = k;
        		e[value] = model[k];
        		ret.push(e);
        	}
    		return ret;    		
    	}
        
        function init(model) {
        	
        	// jos model on muotoa key -> value, muunnetaan se muotoon {key: .., value: ..}
        	for (var k in model) {
        		if (!(model[k] instanceof Object)) {
        			model = toObjectArray(model);
        			break;
        		}
        	}

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
                
                $scope.titles.push(e[value]);
                
                $scope.items.push({
                    selected: $scope.selection.indexOf(e[key]) !== -1,
                    key: e[key],
                    value: e[value],
                    orderWith: w
                });
                $scope.names[e[key]] = e[value];
            }

            $scope.titles.sort();
            
            $scope.items.sort(function(a, b) {
                return a.orderWith < b.orderWith ? -1 : a.orderWith > b.orderWith ? 1 : a.value.localeCompare(b.value);
            });
            
            $scope.rows = columnize($scope.items, columns);
            updateErrors();
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
        require: '^form',
        link: function(scope, element, attrs, controller) {
        	if (scope.name) {
            	scope.isrequired = (attrs.required !== undefined);
            	scope.errors.required = scope.isrequired;
            	controller.$addControl({"$name": scope.name, "$error": scope.errors});
        	}
        },
        scope: {
            display: "@", // checklist | dualpane | combobox
            columns: "@", // sarakkeiden määrä (vain checklist)
            key: "@", // arvo-avain (vakio: koodiUri)
            value: "@", // nimi-avain (vakio: koodiNimi)
            orderWith: "&", // lista avaimista jotka järjestetään ensimmäisiksi
            model: "=", // map jossa arvo->nimi TAI array jossa {key: .., value: ..} -olioita,
            			// joissa key- ja value viittaavat samannimisten parametrien arvoihin
            promise: "=", // async TODO yhdistä modeliin
            selection: "=", // lista jonne valinnat päivitetään
            
            ttShowAll: "@", // näytä kaikki -tekstin käännösavain (combobox)
            ttShowAllTitle: "@", // näytä kaikki -dialogin otsikko (combobox)
            ttShowAllHelp: "@", // näytä kaikki -dialogin ohjeteksti (combobox)
                
	        // angular-form-logiikkaa varten
	        name: "@", // nimi formissa
	        required: "@" // jos tosi, vähintään yksi arvo vaaditaan
        }
    }

});
