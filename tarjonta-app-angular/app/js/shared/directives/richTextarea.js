'use strict';

var app = angular.module('RichTextArea', ['Koodisto', 'localisation', 'pasvaz.bindonce', 'ui.tinymce']);

app.directive('richTextarea',function(LocalisationService, $log) {
	
	function windowContainsElements(selector) {
		var mc = $(selector);
		//console.log(selector+" mcs="+mc.size());
		if (mc.size()==0) {
			return false;
		}
		var ret = false;
		mc.each(function(i, e){
			//console.log(selector+" e["+i+"] = ",e);
			if ($(e).css("display") != "none") {
				ret = true;
			}
		});
		return ret;
	}
	
	function canCloseEditor() {
		return !windowContainsElements(".mce-menu")
			&& !windowContainsElements(".mce-tooltip");
	}
		
	function controller($scope) {
		
		$scope.tinymceOptions = {
			height:"100%",
			statusbar:false,
			menubar:false,
			resize:false
			//content_css:"/css/bootstrap.css,/css/virkailija.css,/css/app.css"
		};
		
		$scope.showMax = $scope.max != undefined && $scope.max!=null && $scope.max>0;
		$scope.edit = $scope.mode()===false;
		
		$scope.isEmpty = function() {
			return !$scope.model || $scope.model.trim().length==0;
		}

		$scope.charCount = function() {
			return $($scope.element).text().length;
		}
		
		$scope.startEdit = function() {
			$scope.edit = true;
		}
		
		$scope.stopEdit = function() {
			if (canCloseEditor() && ($scope.mode()==null || $scope.mode()==undefined)) {
				$scope.edit = false;
			}
		}

	}

    return {
        restrict:'E',
        replace:true,
        templateUrl : "js/shared/directives/richTextarea.html",
        controller: controller,
        scope: {
        	model: "=",  // teksti
        	mode: "&",   // boolean, jonka mukaan editorikäyttöliittymä näytetään
        				 // - jos null tai undefined, editorin näytetään kun hiirikursori on kentän päällä
        				 // - jos false, editori näytetään (aina)
        				 // - jos true, editoria ei näytetä
        	max: "@"	 // maksimimerkkimäärä (ohjeellinen); jos ei määritelty, ei näytetä
        },
		link: function(scope, element, attrs, controller) {
			scope.element = $(".previewBody", element);
		}
    }
    
});
