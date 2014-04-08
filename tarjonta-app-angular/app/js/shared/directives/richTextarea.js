'use strict';

var app = angular.module('RichTextArea', ['Koodisto', 'localisation', 'pasvaz.bindonce', 'ui.tinymce', 'ngSanitize']);

app.directive('richTextarea',function(LocalisationService, $log, $sce) {
	
	function RichTextareaController($scope) {
		
		var fontSizes = [7.5,10,12,14,18,24,36];
		var validElements = "@[style|class|align|lang],p,h1,h2,h3,h4,h5,h6,a[href|target],strong,b,em,i,div,span,br,table,tbody,thead,tr,td[colspan|rowspan|width|valign],ul,ol,li,dd,dl,dt,img[src],sup,sub,font[face|size|color]";
		
		var sizeBase = 1.2;
		
		$scope.tinymceOptions = {
			height:"100%",
			statusbar:false,
			menubar:"format table insert",
			resize:false,
			schema:"html5",
			language:LocalisationService.getLocale(),
			plugins:"link table paste",
			extended_valid_elements: "span[style|class|lang],div[style|class|lang]",
			//valid_elements: validElements,
			paste_word_valid_elements: validElements,
			paste_preprocess: function(plugin, args) {
				console.log("Pasting:",args.content);
			},
			paste_postprocess: function(plugin, args) {
				// tyhjät kappaleet rivinvaihdoiksi <p></p> -> <br/>
				$("p", $(args.node)).each(function(i, em){
					var e = $(em);
					if (e.text().trim()=="") {
						e.replaceWith("<br/>");
					}
				});
				
				// font-tagit spaneiksi jos tyylejä määritelty
				$("font", $(args.node)).each(function(i, em){
					var e = $(em);
					if (e.text().trim()=="") {
						e.remove();
					} else {
						var span = $("<span></span>");
						var styled = false;
						if (e.attr("face")) {
							span.css("font-family", e.attr("face"));
							styled = true;
						}
						if (e.attr("size")) {
							span.css("font-size", fontSizes[e.attr("size")-1]+"pt");
							styled = true;
						}
						if (e.attr("color")) {
							span.css("color", e.attr("color"));
							styled = true;
						}
						
						if (styled) {
							e.wrap(span);
						}
						
						e.contents().unwrap();
						
					}
				});

				// align -> css
				$("[align]", $(args.node)).each(function(i, em){
					var e = $(em);
					var align = e.attr("align");
					e.attr("align", null);
					e.css("text-align", align);
				});
				
				// bgcolor -> css
				$("[bgcolor]", $(args.node)).each(function(i, em){
					var e = $(em);
					var bgcolor = e.attr("bgcolor");
					e.attr("bgcolor", null);
					e.css("background-color", bgcolor);
				});
				
				console.log("Pasted:",args.node);
			},
			toolbar: false, // tinymce4 ei tue taulukkoa toolbarissa
				//"styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist | outdent indent | link image table | media inserttable tableprops",
			tools:"inserttable"
			//toolbar_items_size:"small"
			//content_css:"/css/bootstrap.css,/css/virkailija.css,/css/app.css"
		};
		
		$scope.model = $scope.model ? $scope.model : "";
		
		$scope.showMax = $scope.max != undefined && $scope.max!=null && $scope.max>0;
		$scope.edit = $scope.mode()===false;
		
		$scope.isEdit = function() {
			return $scope.edit && $scope.mode()!==true;
		}
		
		$scope.html = function() {
			return $sce.trustAsHtml($scope.model);
		}
		
		$scope.isEmpty = function() {
			return !$scope.model || $scope.model.trim().length==0;
		}

		$scope.charCount = function() {
			return $($scope.element).text().length;
		}
		
		$scope.startEdit = function($event) {
			if ($scope.mode()===true) {
				return;
			}
			if ($event) {
				$event.stopPropagation();
			}
			$scope.edit = true;
			if ($scope.container) {
				$scope.container.stopExcept($scope);
			}
			// autofocus avattaessa
			/*setTimeout(function(){
				var em = $("iframe", $scope.rootElement)[0].contentDocument.body;
				$(em).focus();
				//console.log("FOCUS ",em);
			});*/
		}
		
		$scope.stopEdit = function() {
			if ($scope.mode()!==false) {
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
        				 // - jos null tai undefined, editorin näytetään klikattaessa
        				 // - jos false, editori näytetään (aina)
        				 // - jos true, editoria ei näytetä
        	max: "@"	 // maksimimerkkimäärä (ohjeellinen); jos ei määritelty, ei näytetä
        },
		link: function(scope, element, attrs, richTextareaContainer) {
			if (richTextareaContainer) {
				scope.container = richTextareaContainer;
				richTextareaContainer.areas.push(scope);
			}
			scope.rootElement = element;
			scope.element = $(".previewBody", element);
		}
    }
    
});

app.directive('richTextareaContainer',function($log) {
	
	function isInsideEditor($event) {
		var em = $event.originalEvent.originalTarget;
		if (!em) {
			// chrome
			em = $event.originalEvent.srcElement;
		}
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
