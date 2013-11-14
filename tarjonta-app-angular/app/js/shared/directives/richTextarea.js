'use strict';

var app = angular.module('RichTextArea', ['Koodisto', 'localisation', 'pasvaz.bindonce', 'ui.tinymce']);

app.directive('richTextarea',function(LocalisationService, $log) {
	
	function canCloseEditor() {
		// tutkitaan, onko tinymce-popup-valikko auki
		var mc = $(".mce-menu");
		if (mc.size()==0) {
			return true;
		}
		var ret = true;
		mc.each(function(i, e){
			if ($(e).css("display") != "none") {
				ret = false;
			}
		});
		
		return ret;
	}
		
	function controller($scope) {
		
		$scope.tinymceOptions = {
			height:"auto"
			//content_css:"/css/bootstrap.css,/css/virkailija.css,/css/app.css"
		};
		
		$scope.edit = false;
		
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
        	model: "=" // teksti
        }
    }
    
});
