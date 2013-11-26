'use strict';

var app = angular.module('ControlsLayout', ['localisation']);

app.directive('displayControls',function($log, LocalisationService) {
	
    return {
        restrict: 'E',
        templateUrl: "js/shared/directives/controlsLayout.html",
        replace: true,
        scope: {
        	model: "=",	// model johon controls-model viittaa
        	display: "@" // header|footer
        },
        controller: function($scope) {

        	switch ($scope.display) {
        	case "header":
        	case "footer":
        		break;
    		default:
    			throw new Error("Invalid display type: "+$scope.display);
        	}
        	
        	$scope.t = function(k, a) {
        		return LocalisationService.t(k, a);
        	}
        	
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
       			return showMessage($scope.model.notifs.errorDetail, msg);
       		};

       		$scope.showError = function(msg) {
       			if (msg==undefined) {
           			return showMessage($scope.model.notifs.error) || showMessage($scope.model.notifs.errorDetail);
       			}
       			return showMessage($scope.model.notifs.error, msg);
       		};
       		
       		$scope.showSuccess = function(msg) {
       			return showMessage($scope.model.notifs.success, msg) && (msg!=null || !$scope.showError());
       		};
       		
       		$scope.showMessage = function(msg) {
       			return showMessage($scope.model.notifs.message, msg);
       		};
       		
       		return $scope;
        }
    }
    
});

app.directive('controlsModel',function($log) {
	
    return {
        restrict: 'E',
        template: "<div style=\"display:none;\" ng-transclude></div>",
        replace: true,
        transclude: true,
        scope: {
        	model: "="
        },
        controller: function($scope) {
        	$scope.model.notifs = {
   				message: [],
   				success: [],
   				error: [],
   				errorDetail: []
       		}
        	$scope.model.buttons = [];
       		       		
       		return $scope;
        }
    }
    
});

app.directive('controlsButton',function($log) {
		
    return {
        restrict: 'E',
        //replace: true,
        require: "^controlsModel",
        link: function (scope, element, attrs, controlsLayout) {
        	controlsLayout.model.buttons.push({
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
        require: "^controlsModel",
        link: function (scope, element, attrs, controlsLayout) {
        	
        	var notifs;
        	switch (scope.type) {
        	case "message":
        	case "success":
        	case "error":
        		notifs = controlsLayout.model.notifs[scope.type];
    			break;
        	case "error-detail":
        		notifs = controlsLayout.model.notifs.errorDetail;
    			break;
    		default:
    			throw new Error("Invalid notification type: "+scope.type);
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

