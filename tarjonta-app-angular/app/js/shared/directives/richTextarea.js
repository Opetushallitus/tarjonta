'use strict';

var app = angular.module('RichTextArea', ['Koodisto', 'localisation', 'pasvaz.bindonce', 'ui.tinymce']);

app.directive('richTextarea',function(LocalisationService, $log) {
	
	function RichTextareaController($scope) {
		
		$scope.tinymceOptions = {
			height:"100%",
			statusbar:false,
			menubar:false,
			resize:false
			//content_css:"/css/bootstrap.css,/css/virkailija.css,/css/app.css"
		};
		
		$scope.model = $scope.model ? $scope.model : "";
		
		$scope.showMax = $scope.max != undefined && $scope.max!=null && $scope.max>0;
		$scope.edit = $scope.mode()===false;
		
		$scope.isEmpty = function() {
			return !$scope.model || $scope.model.trim().length==0;
		}

		$scope.charCount = function() {
			return $($scope.element).text().length;
		}
		
		$scope.startEdit = function($event) {
			$event.stopPropagation();
			$scope.edit = true;
			if ($scope.container) {
				$scope.container.stopExcept($scope);
			}
		}
		
		$scope.stopEdit = function() {
			if ($scope.mode()==null || $scope.mode()==undefined) {
				$scope.edit = false;
			}
		}

	}

    return {
        restrict:'E',
        replace:true,
        templateUrl : "js/shared/directives/richTextarea.html",
        require:"^?richTextareaContainer",
        controller: RichTextareaController,
        scope: {
        	model: "=",  // teksti
        	mode: "&",   // boolean, jonka mukaan editorikäyttöliittymä näytetään
        				 // - jos null tai undefined, editorin näytetään kun hiirikursori on kentän päällä
        				 // - jos false, editori näytetään (aina)
        				 // - jos true, editoria ei näytetä
        	max: "@"	 // maksimimerkkimäärä (ohjeellinen); jos ei määritelty, ei näytetä
        },
		link: function(scope, element, attrs, richTextareaContainer) {
			if (richTextareaContainer) {
				scope.container = richTextareaContainer;
				richTextareaContainer.areas.push(scope);
			}
			scope.element = $(".previewBody", element);
		}
    }
    
});

app.directive('richTextareaContainer',function($log) {
	
	function isInsideEditor($event) {
		var em = $event.originalEvent.originalTarget;
		while (em && em.getAttribute) {
			var cc = em.getAttribute("class");
			if (cc && cc.indexOf("mce-")!=-1) {
				return true;
			}
			em = em.parentNode;
		}
		
		return false;
	}

    return {
        restrict:'E',
        replace:true,
        template: "<div ng-transclude ng-click=\"stopAll($event)\"></div>",
        transclude: true,
        scope: {},
        controller: function RichTextareaContainerController($scope) {
        	$scope.areas = [];

        	$scope.stopExcept = function(src) {
        		for (var i in $scope.areas) {
        			var a = $scope.areas[i];
        			if (a!=src) {
        				a.stopEdit();
        			}
        		}
        	}
        	
        	$scope.stopAll = function($event) {
        		if (isInsideEditor($event)) {
        			return;
        		}
        		for (var i in $scope.areas) {
        			$scope.areas[i].stopEdit();
        		}
        	}
        	
        	return $scope;
        }
    }

});
