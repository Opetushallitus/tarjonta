'use strict';

var app = angular.module('TarjontaDateTime', ['localisation']);

app.directive('tDateTime', function($log, $modal, LocalisationService) {

    function controller($scope) {
    	
    	$scope.errors = {};
    	
    	var omitUpdate = false;
    	
    	// model <-> ngModel muunnos olion ja aikaleiman välillä
    	if ($scope.type == "object") {
    		$scope.model = $scope.ngModel;
	    	$scope.$watch("ngModel", function(nv, ov){
	    		$scope.model = nv;
	    	});
    	} else if ($scope.type == "long") {
    		$scope.model = new Date($scope.ngModel);
    	} else {
    		throw new ("Unknown type "+$scope.type);
    	}
    	
    	function zpad(v) {
    		return v>9 ? v : "0"+v;
    	}
    	
    	// model <-> date/time -> ngModel -muunnos
    	function updateModels() {
    		if (omitUpdate) {
    			omitUpdate = false;
    			return;
    		}
    		if ($scope.model==null) {
            	$scope.date = "";
            	$scope.time = "";
        		$scope.ngModel = null;
    		} else {
            	$scope.date = $scope.model.getDate()+"."+($scope.model.getMonth()+1)+"."+$scope.model.getFullYear();
            	$scope.time = $scope.model.getHours()+":"+zpad($scope.model.getMinutes());
        		$scope.ngModel = $scope.type == "object" ? $scope.model : $scope.model.getTime();
    		}
    	}
    	
    	function trimSplit(v,s) {
    		var ret = v.split(s);
    		for (var i in ret) {
    			if (ret[i].trim().length==0) {
    				ret.splice(i,1);
    			}
    		}
    		return ret;
    	}
    	
    	function applyConstraints(d) {
    		var min = $scope.min();
    		if (min && min.getTime) {
    			min = min.getTime();
    		}
    		var max = $scope.max();
    		if (max && max.getTime) {
    			max = min.getTime();
    		}
    		
    		if (min && d.getTime() < min) {
    			d.setTime(min);
    		} else if (max && d.getTime() > max) {
    			d.setTime(max);
    		}

    		return d;
    	}

    	updateModels();
    	$scope.$watch("model", function(nv, ov){
    		updateModels();
    		$scope.errors.required = $scope.isRequired() ? $scope.model!=null : undefined;
    	});
    	
    	var thisyear = new Date().getFullYear();

    	$scope.onFocusOut = function(){
    		omitUpdate = false;
    		updateModels();
    	}
    	
    	$scope.onModelChanged = function() {
    		var nd = $scope.model;
    		var dd=0, dm=0, dy=thisyear, th=0, tm=0;
    		if ($scope.model) {
    			dd = $scope.model.getDate();
    			dm = $scope.model.getMonth();
    			dy = $scope.model.getFullYear();
    			th = $scope.model.getHours();
    			tm = $scope.model.getMinutes();
    		}
    		
    		var isnull = true;
    		
    		if ($scope.date) {
    			var ds = trimSplit($scope.date,".");
    			if (ds.length>0) {
    				isnull = false;
    			}
    			
    			dd = ds.length>0 ? ds[0] : dd;
    			dm = ds.length>1 ? ds[1]-1 : dd;
    			dy = ds.length>2 ? ds[2] : thisyear;
    		}
    		if ($scope.timestamp && $scope.time) {
    			var ds = trimSplit($scope.time,":");
    			if (ds.length>0) {
    				isnull = false;
    			}

    			th = ds.length>0 ? ds[0] : th;
    			tm = ds.length>1 ? ds[1] : tm;
    		}
    		
    		//console.log("DD="+dd+" DM="+dm+" DY="+dy+" TH="+th+" TM="+tm);
    		
    		if (isnull) {
    			$scope.model = null;
    		} else {
    			var nd = new Date();
        		nd.setDate(dd);
        		nd.setMonth(dm);
        		nd.setFullYear(dy);
        		nd.setHours(th);
        		nd.setMinutes(tm);
        		
        		if (!isNaN(nd.getTime())) {
        			omitUpdate = true;
        			$scope.model = applyConstraints(nd);
        		}
    		}
    		    		
    		if ($scope.ngChange) {
    			$scope.ngChange();
    		}
    	}
    	
    }

    return {
        restrict: 'E',
        replace: true,
        templateUrl: "js/shared/directives/dateTime.html",
        controller: controller,
        require: '^form',
        link: function(scope, element, attrs, controller) {
        	scope.isDisabled = function() {
        		return attrs.disabled || scope.ngDisabled();
        	}
        	scope.isRequired = function() {
        		return attrs.required || scope.ngRequired();
        	}
        	if (scope.name) {
            	controller.$addControl({"$name": scope.name, "$error": scope.errors});
        	}
        },
        scope: {
        	ngModel: "=", // arvo
        	type: "@",  // ajan tietotyyppi
        				//   object: javascript Date
        				//   long: unix timestamp
        	
        	// minimi ja maksimi (js Date tai unix timestamp)
        	min: "&",
        	max: "&",

        	// disablointi
        	disabled: "@",
        	ngDisabled: "&",
        	
        	// muutos-listener
        	ngChange: "&",
        	
        	timestamp: "=", // jos tosi, niin aika+pvm, muuten pelkkä pvm

        	// angular-form-logiikkaa varten
	        name: "@", // nimi formissa
	        required: "@", // pakollisuus
	        ngRequired: "&" // vastaava ng
        }
    }

});
