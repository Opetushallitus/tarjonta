'use strict';

var app = angular.module('TarjontaDateTime', ['localisation']);

app.directive('tDateTime', function($log, $modal, LocalisationService, dialogService) {

    function controller($scope) {
    	
    	var ctrl = $scope;
    	
    	$scope.errors = {};
    	
    	$scope.prompts = {
    			date: LocalisationService.t("tarjonta.kalenteri.prompt.pvm"),
    			time: LocalisationService.t("tarjonta.kalenteri.prompt.aika"),
    	}

    	var violation = null;
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
    	
    	function dateToString(d) {
    		return d.getDate()+"."+(d.getMonth()+1)+"."+d.getFullYear();
    	}
    	
    	function timeToString(d) {
    		return d.getHours()+":"+zpad(d.getMinutes());
    	}
    	
    	function dateTimeToString(d) {
    		if (d==null || d==undefined) {
    			return null;
    		}
    		if (!(d instanceof Date)) {
    			d = new Date(d); // timestamp
    		}
    		
    		var ret = dateToString(d);
    		
    		if ($scope.timestamp) {
    			ret = ret + " " + timeToString(d);
    		}
    		
    		return ret;    		
    	}
    	
    	// model <-> date/time -> ngModel -muunnos
    	function updateModels() {
    		//console.log("UPDATE MODEL", omitUpdate);
    		if (omitUpdate) {
    			omitUpdate = false;
    			return;
    		}
    		if ($scope.model==null) {
            	   $scope.date = "";
            	   $scope.time = "";
            	   $scope.ngModel = null;
    		} else {
            	  $scope.date = dateToString($scope.model); 
            	  $scope.time = timeToString($scope.model);
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
    			d = new Date(min);
    		} else if (max && d.getTime() > max) {
    			d = new Date(max);
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

    	$scope.onFocusOut = function() {
    		
       		omitUpdate = false;
       		updateModels();

			if ($scope.ttBounds && violation) {
				
				dialogService.showDialog({
	                ok: LocalisationService.t("ok"),
	                cancel: null,
	                title: LocalisationService.t("tarjonta.kalenteri"),
	                description: LocalisationService.t($scope.ttBounds,
	                		[dateTimeToString(violation),
	                		 dateTimeToString(minTime()),
	                		 dateTimeToString(maxTime())])
	            });
			}
			violation = null;

       		
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
    		
    		//console.log("WAS dd="+dd+", dm="+dm+", dy="+dy+", th="+th+", tm="+tm);
    		
    		var isnull = true;
    		
    		if ($scope.date) {
    			var ds = trimSplit($scope.date,".");
    			if (ds.length>0) {
    				isnull = false;
    			}
    			
    			dd = ds.length>0 ? ds[0] : dd;
    			dm = ds.length>1 && ds[1]>0 ? ds[1]-1 : dm;
    			dy = ds.length>2 ? ds[2] : thisyear;
    			
    			if (dd>31 || dd<1 || dm>11 || dm<0) {
    				$scope.date = "";
    				return;
    			}
    		}
    		if ($scope.timestamp && $scope.time) {
    			var ds = trimSplit($scope.time,":");
    			if (ds.length>0) {
    				isnull = false;
    			}

    			th = ds.length>0 ? ds[0] : th;
    			tm = ds.length>1 ? ds[1] : 0;
    			
    			if (th<0 || th>23 || tm<0 || tm>59) {
    				$scope.time = "";
    				return;
    			}
    		}
    		
    		//console.log("DD="+dd+" DM="+dm+" DY="+dy+" TH="+th+" TM="+tm);
    		
    		if (isnull) {
    			$scope.model = null;
    		} else {
    			var nd = new Date();
        		// HUOM! asetettava järjestyksessä vuosi-kuukausi-päivä
        		nd.setFullYear(dy);
        		nd.setMonth(dm);
        		nd.setDate(dd);
        		nd.setHours(th);
        		nd.setMinutes(tm);
        		
        		//console.log("ISN dd="+dd+", dm="+dm+", dy="+dy+", th="+th+", tm="+tm, nd);

        		if (!isNaN(nd.getTime())) {
    				omitUpdate = true;
    				var cd = applyConstraints(nd);
    				violation = cd.getTime() == nd.getTime() ? null : nd;
        			$scope.model = cd;
        		}
    		}
    		
    		if ($scope.ngChange) {
    			$scope.ngChange();
    		}
    	}
    	
    	$scope.openChooser = function() {
    		var modalInstance = $modal.open({
				scope: $scope,
				templateUrl: 'js/shared/directives/dateTime-chooser.html',
				controller: function($scope) {
					$scope.ctrl = {years : []};
					$scope.months = [];
					for (var i=0; i<12; i++) {
						$scope.months.push({month:i, name:LocalisationService.t("tarjonta.kalenteri.kk."+(i+1))});
					}
					
					$scope.calendar=[];
					$scope.model = ctrl.model instanceof Date ? new Date(ctrl.model.getTime()) : applyConstraints(new Date());
					
					$scope.select = {m:$scope.model.getMonth(), y:$scope.model.getFullYear()};
					
					$scope.ok = function() {
						ctrl.model = $scope.model;
						updateModels();
						modalInstance.dismiss();
					}
					
					$scope.cancel = function() {
						modalInstance.dismiss();
					}
					
					$scope.updateCalendar = function(){
						$scope.select.m = $scope.model.getMonth();
						$scope.select.y = $scope.model.getFullYear();

						var ret = [];

						var cal = new ISOCalendar($scope.model.getFullYear(), $scope.model.getMonth());
						
						var start = cal.getMonday();
						var end = cal.getLastDayOfMonth().getFriday();
						
						//console.log("START = ",start);
						//console.log("END = ",end);
						
						while (start.compareTo(end)<=0) {
							var wd = {week:start.getIsoWeek(), days:[]};
							var d = start.toDate();
							for (var j=0; j<7; j++) {
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
								d.setDate(d.getDate()+1);
							}
							
							ret.push(wd);
							start = start.getNextWeek();
						}
						$scope.calendar = ret;
						$scope.ctrl.years = [];
						
						var y = $scope.model.getFullYear();
						for (var i = y-2; i<=y+2; i++) {
							var nd = new Date($scope.model.getTime());
							nd.setFullYear(i);
							if (!violatesConstraints(nd)) {
								$scope.ctrl.years.push(i);
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
						$scope.updateCalendar();
					}
					
					$scope.incYear = function(v) {
                                            var fy = $scope.model.getFullYear();
						$scope.model.setFullYear(fy+v);
                                             fy = $scope.model.getFullYear();
                                                
						$scope.updateCalendar();
					}
					
					$scope.incMonth = function(v) {
						$scope.model.setMonth($scope.model.getMonth()+v);
						$scope.updateCalendar();
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
						$scope.updateCalendar();
					}
										
					$scope.updateCalendar();
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
        		return (attrs.required &&  attrs.required !== 'false')|| scope.ngRequired();
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
        	
        	// virheilmoitus, joka näytetään (jos määritelty), jos päivämäärä on min-max-arvojen ulkopuolella
        	ttBounds: "@",

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
