'use strict';

var app = angular.module('TarjontaDateTime', ['localisation']);

app.directive('tDateTime', function($log, $modal, LocalisationService) {

    function controller($scope) {
    	
    	var ctrl = $scope;
    	
    	$scope.errors = {};
    	
    	$scope.prompts = {
    			date: LocalisationService.t("tarjonta.kalenteri.prompt.pvm"),
    			time: LocalisationService.t("tarjonta.kalenteri.prompt.aika"),
    	}
    	
    	var omitUpdate = false;
    	
    	// model <-> ngModel muunnos olion ja aikaleiman välillä
    	if ($scope.type == "object") {
    		$scope.model = $scope.ngModel;
	    	$scope.$watch("ngModel", function(nv, ov){
	    	  $scope.model = nv;
	    	});
    	} else if ($scope.type == "long") {
    		$scope.model = $scope.ngModel==null ? null : new Date($scope.ngModel);
	    	$scope.$watch("ngModel", function(nv, ov){
	    	  $scope.model = $scope.ngModel==null ? null : new Date($scope.ngModel);
	    	});
    	} else {
    		throw ("Unknown type "+$scope.type);
    	}
    	
    	function asTimestamp(s) {
    		if (s==undefined || s==null) {
    			return false;
    		}
    		return s instanceof Date ? s.getTime() : s;
    	}
    	
    	function minTime() {
    		return asTimestamp($scope.min());
    	}
    	
    	function maxTime() {
    		return asTimestamp($scope.max());
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
            	  $scope.ngModel = $scope.type == "object" ? $scope.model : (isNaN($scope.model.getTime())?undefined:$scope.model.getTime());
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
    		var min = minTime();
    		var max = maxTime();
    		
    		if (min && d.getTime() < min) {
    			d.setTime(min);
    		} else if (max && d.getTime() > max) {
    			d.setTime(max);
    		}

    		return d;
    	}
    	
    	function roundToDay(d) {
    		if (d==false) {
    			return false;
    		}
    		var t = new Date(d);
    		return new Date(t.getFullYear(), t.getMonth(), t.getDate(), 0, 0, 0);
    	}

    	function violatesConstraints(d) {
    		d = roundToDay(d.getTime());
    		var min = roundToDay(minTime());
    		var max = roundToDay(maxTime());
    		var ret = (min && d < min) || (max && d > max);
    		return ret;
    	}

    	updateModels();
    	
    	$scope.$watch("model", function(nv, ov){
    		updateModels();
    		$scope.errors.required = $scope.isRequired && $scope.isRequired() ? $scope.model!=null : undefined;
    	});

    	$scope.$watch("min", function(nv, ov){
    		if ($scope.model) {
    			$scope.model = applyConstraints($scope.model);
    		}
    		updateModels();
    	});

    	$scope.$watch("max", function(nv, ov){
    		if ($scope.model) {
    			$scope.model = applyConstraints($scope.model);
    		}
    		updateModels();
    	});

    	var thisyear = new Date().getFullYear();

    	$scope.focusCount = 0;

    	$scope.onFocusIn = function() {
    		$scope.focusCount++;
    		//console.log("Focus++",$scope.focusCount);
    	}
    	
    	$scope.onFocusOut = function(isButton) {
    		$scope.focusCount--;
    		//console.log("Focus--",$scope.focusCount);
    		if (!isButton) {
        		omitUpdate = false;
        		updateModels();
    		}
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

    	$scope.openChooser = function() {
    		//console.log("Chooser?",$scope.focusCount);
    		if ($scope.focusCount==0) {
    			return;
    		}
    		var modalInstance = $modal.open({
				scope: $scope,
				templateUrl: 'js/shared/directives/dateTime-chooser.html',
				controller: function($scope) {
					
					$scope.months = [];
					for (var i=0; i<12; i++) {
						$scope.months.push({month:i, name:LocalisationService.t("tarjonta.kalenteri.kk."+(i+1))});
					}
					
					$scope.years = [];

					$scope.model = ctrl.model ? ctrl.model : new Date();
					
					var isValidDate=Object.prototype.toString.call($scope.model) == "[object Date]" && !isNaN($scope.model.getTime());
					  
                                        if(angular.isUndefined($scope.model) || $scope.model === null || !isValidDate){
                                            $scope.model = new Date();
                                        }
                                        
					$scope.select = {m:$scope.model.getMonth(), y:$scope.model.getFullYear()};
					$scope.calendar=[];
					
					function getWeekFromDate(d, dowOffset) {
						// https://gist.github.com/dblock/1081513
						
						// Create a copy of this date object
						var target = new Date(d.valueOf());
						// ISO week date weeks start on monday
						// so correct the day number
						var dayNr = (d.getDay() + 6) % 7;
						 
						// Set the target to the thursday of this week so the
						// target date is in the right year
						target.setDate(target.getDate() - dayNr + 3);
						 
						// ISO 8601 states that week 1 is the week
						// with january 4th in it
						var jan4 = new Date(target.getFullYear(), 0, 4);
						 
						// Number of days between target date and january 4th
						var dayDiff = (target - jan4) / 86400000;
						 
						// Calculate week number: Week 1 (january 4th) plus the
						// number of weeks between target date and january 4th
						var weekNr = 1 + Math.ceil(dayDiff / 7);
						 
						return weekNr;
					}
					
					$scope.ok = function() {						
						ctrl.model = $scope.model;
						updateModels();
						modalInstance.dismiss();
					}
					
					$scope.cancel = function() {
						modalInstance.dismiss();
					}
					
					function getMonday(d) {
						if (d.getDay()==1) { // maanantai
							return d;
						}
						var ret = new Date(d.getTime());

						ret.setDate(d.getDay()==0
								? ret.getDate()-6
								: ret.getDate()-d.getDay()+1);
						return ret;
					}

					function updateCalendar(){
						$scope.select.m = $scope.model.getMonth();
						$scope.select.y = $scope.model.getFullYear();
						
						var sd = new Date($scope.model.getFullYear(), $scope.model.getMonth(), 1);
						var ed = new Date($scope.model.getFullYear(), $scope.model.getMonth()+1, 1);
						
						var s = getWeekFromDate(sd);
						var e = getWeekFromDate(ed);
						
						//console.log("D: "+s+" -> "+e,$scope.model);
						var ret = [];
						//for (var i=s; i!=e; nextWeek(i)) {
						while (getMonday(sd).getTime()<ed.getTime()) {
							var i = getWeekFromDate(sd);
							var wd = {week:i, days:[]};
							var d = new Date($scope.model.getFullYear(), 0, 1+ 7*(i-1) );
							d.setDate(d.getDate() - d.getDay());
							for (var j=0; j<7; j++) {
								d.setDate(d.getDate()+1);
								wd.days.push({
									day: d.getDate(),
									month: d.getMonth(),
									year: d.getFullYear(),
									other: (d.getMonth() != $scope.model.getMonth()),
									vkl: (j>=5),
									disabled: violatesConstraints(d),
									selected: (d.getDate()==$scope.model.getDate())
										&& (d.getMonth()==$scope.model.getMonth())
										&& (d.getFullYear()==$scope.model.getFullYear())									
									});								
							}							
							ret.push(wd);							
							sd.setTime(sd.getTime() + 604800000);
						}
						$scope.calendar = ret;
						
						$scope.years = [];
						var y = $scope.model.getFullYear();
						for (var i = y-2; i<=y+2; i++) {
							var nd = new Date($scope.model.getTime());
							nd.setFullYear(i);
							if (!violatesConstraints(nd)) {
								$scope.years.push(i);
							}
						}
						
						return ret;
					}

					$scope.onSelect = function(d) {
						if (d.disabled) {
							return;
						}
						$scope.model.setDate(d.day);
						$scope.model.setMonth(d.month);
						$scope.model.setFullYear(d.year);
						updateCalendar();
					}
					
					$scope.incYear = function(v) {
						$scope.model.setFullYear($scope.model.getFullYear()+v);
						updateCalendar();
					}
					
					$scope.incMonth = function(v) {
						$scope.model.setMonth($scope.model.getMonth()+v);
						updateCalendar();
					}
					
					$scope.canIncYear = function(v) {
						var nd = new Date($scope.model.getTime());
						nd.setFullYear($scope.model.getFullYear()+v);
						return !violatesConstraints(nd);
					}
					
					$scope.canIncMonth = function(v) {
						var nd = new Date($scope.model.getTime());
						nd.setMonth($scope.model.getMonth()+v);
						return !violatesConstraints(nd);
					}
					
					$scope.getMonths = function() {
						var ret = [];
						for (var i in $scope.months) {
							var nd = new Date($scope.model.getTime());
							nd.setMonth($scope.months[i].month);
							if (!violatesConstraints(nd)) {
								ret.push($scope.months[i]);
							}
						}
						return ret;						
					}
					
					$scope.onComboSelect = function() {
						$scope.model.setMonth($scope.select.m);
						$scope.model.setFullYear($scope.select.y);
						updateCalendar();
					}
										
					updateCalendar();
					return $scope;
				}
			});
		}
    	
    }

    return {
        restrict: 'E',
        replace: true,
        templateUrl: "js/shared/directives/dateTime.html",
        controller: controller,
        require: '^?form',
        link: function(scope, element, attrs, controller) {
        	scope.isDisabled = function() {
        		return attrs.disabled || scope.ngDisabled();
        	}
        	scope.isRequired = function() {
        		return attrs.required || scope.ngRequired();
        	}
        	if (scope.name && !angular.isUndefined(controller)) {
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
