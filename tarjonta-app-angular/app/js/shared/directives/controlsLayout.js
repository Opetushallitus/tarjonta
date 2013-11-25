'use strict';

var app = angular.module('ControlsLayout', ['localisation']);


app.directive('controlsLayout',function($log, LocalisationService) {
	
    return {
        restrict: 'E',
        templateUrl: "js/shared/directives/controlsLayout.html",
        replace: true,
        transclude: true,
        scope: {},
        controller: function($scope) {
        	$scope.t = function(v,a) {
        		return LocalisationService.t(v,a);
        	}
       		$scope.notifs = {
       				message: [],
       				success: [],
       				error: [],
       				errorDetail: []
       		};
       		$scope.buttons = [];
       		
       		function showMessage(msgs, msg) {
       			if (msg==undefined) {
       				for (var i in msgs) {
       					var ms = msgs[i].show();
       					//console.log(msgs[i].tt+" -> MS=",ms);
       					if (ms==undefined || ms==null || ms==true) {
       						return true;
       					}
       				}
       				return false;
       			} else {
   					var ms = msg.show();
   					return ms==undefined || ms==null || ms==true;
       			}
       		}

       		$scope.showErrorDetail = function(msg) {
       			return showMessage($scope.notifs.errorDetail, msg);
       		};

       		$scope.showError = function(msg) {
       			if (msg==undefined) {
           			return showMessage($scope.notifs.error) || showMessage($scope.notifs.errorDetail);
       			}
       			return showMessage($scope.notifs.error, msg);
       		};
       		
       		$scope.showSuccess = function(msg) {
       			return showMessage($scope.notifs.success, msg) && (msg!=null || !$scope.showError());
       		};
       		
       		$scope.showMessage = function(msg) {
       			return showMessage($scope.notifs.message, msg);
       		};
       		
       		return $scope;
        }
    }
    
});

app.directive('controlsButton',function($log) {
		
    return {
        restrict: 'E',
        //replace: true,
        require: "^controlsLayout",
        link: function (scope, element, attrs, controlsLayout) {
        	controlsLayout.buttons.push({
        		tt: scope.tt,
        		primary: scope.primary,
        		action: scope.action,
        		disabled: scope.disabled });
        },
        scope: {
        	tt: "@",	   // otsikko (lokalisaatioavain)
        	primary:"@",   // boolean; jos tosi, nappi on ensisijainen (vaikuttaa vain ulkoasuun)
        	action: "&",   // funktio jota klikatessa kutsutaan
        	disabled: "&"  // funktio jonka perusteella nappi disabloidaan palauttaessa true
        }
    }    
});

app.directive('controlsNotify',function($log) {
		
    return {
        restrict: 'E',
        ///replace: true,
        require: "^controlsLayout",
        link: function (scope, element, attrs, controlsLayout) {
        	
        	var notifs;
        	switch (scope.type) {
        	case "message":
        	case "success":
        	case "error":
        		notifs = controlsLayout.notifs[scope.type];
    			break;
        	case "error-detail":
        		notifs = controlsLayout.notifs.errorDetail;
    			break;
    		default:
    			console.log("INVALID NOTIF TYPE",scope.type);
    			break;
        	}
        	
        	notifs.push({
        		tt: scope.tt,
        		tp: scope.tp,
        		type: scope.type,
        		show: scope.show });
        },
        scope: {
        	tt: "@",	   // viesti (lokalisaatioavain)
        	tp: "&",	   // lokalisaatioavaimen parametrit
        	type: "@",     // ilmoituksen tyyli: message|success|error|error-detail
        	show: "&"      // funktio jonka perusteella viesti näytetään
        }
    }    
});

