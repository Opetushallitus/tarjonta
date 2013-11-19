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
		
		$scope.edit = false;
	
		$scope.charCount = function() {
			return $($scope.element).text().length;
		}
		
		$scope.startEdit = function() {
			$scope.edit = true;
		}
		
		$scope.stopEdit = function() {
			if (canCloseEditor()) {
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
        	model: "=", // teksti
        	max: "@"
        },
		link: function(scope, element, attrs, controller) {
			scope.element = $(".previewBody", element);
		}
    }
    
});
