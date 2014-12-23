'use strict';
var app = angular.module('ImageDirective', ['Logging']);
/**
 * Wrapperi, joka valitsee käytettävän imagefield-toteutuksen (html5 tai ie9/flash)
 */
app.directive('imageField', function($log) {
    function controller($scope, $q, $element, $compile) {
        $scope.isInternetExplorer9 = function() {
            return navigator.appVersion.indexOf('MSIE 9') > -1 ? true : false;
        };
    }
    return {
        restrict: 'E',
        replace: true,
        templateUrl: 'js/shared/directives/imageFieldWrapper.html',
        controller: controller,
        scope: {
            isDisabled: '&',
            //disable upload
            model: '=' // model (filename, mimeType, base64data)
        }
    };
});
/**
 * Html5 File API -versio
 */
app.directive('imageFieldHtml5', function($log) {
    $log = $log.getInstance('<imageField>');
    function controller($scope, $q, $element, $compile) {
        $scope.disabled = function() {
            var fn = $scope.isDisabled();
            return $scope.loading || fn && fn();
        };
        $scope.isImage = function() {
            return $scope.model && $scope.model.mimeType && $scope.model.base64data;
        };
        function base64prefix() {
            return 'data:' + $scope.model.mimeType + ';base64,';
        }
        $scope.imgSrc = function() {
            if (!$scope.model || !$scope.model.mimeType || !$scope.model.base64data) {
                return null;
            }
            return base64prefix() + $scope.model.base64data;
        };
        $scope.deleteImage = function(event) {
            $scope.model.mimeType = undefined;
            $scope.model.base64data = undefined;
            $scope.model.filename = undefined;
        };
        $scope.onUpload = function() {
            var fs = $scope.element[0].files;
            if (!fs || fs.length == 0) {
                return;
            }
            var reader = new FileReader();
            reader.onload = function(ev) {
                $scope.model.filename = fs[0].name;
                $scope.model.mimeType = fs[0].type;
                $scope.model.base64data = ev.target.result.substring(base64prefix().length);
                $scope.loading = false;
                $scope.$digest();
            };
            $scope.loading = true;
            reader.readAsDataURL(fs[0]);
        };
    }
    return {
        restrict: 'E',
        replace: true,
        templateUrl: 'js/shared/directives/imageFieldHtml5.html',
        controller: controller,
        link: function(scope, element, attrs, controller) {
            scope.element = $('input[type=file]', element);
            // IE-kikka (jotta file-input ei vaadi kahta klikkausta)
            if (navigator.userAgent.indexOf('MSIE') >= 0) {
                $(scope.element).mousedown(function() {
                    $(this).trigger('click');
                });
            }
        },
        scope: {
            isDisabled: '&',
            //disable upload
            model: '=' // model (filename, mimeType, base64data)
        }
    };
});
/**
 * Flash-pohjainen IE9-versio
 *
 *  - https://github.com/Jahdrien/FileReader
 *  - http://code.google.com/p/swfobject/
 *
 */
app.directive('imageFieldIe9', function($log) {
    $log = $log.getInstance('<imageField>');
    function controller($scope, $q, $element, $compile) {
        $scope.flashAvailable = typeof swfobject !== 'undefined' && swfobject.getFlashPlayerVersion().major !== 0;
        $scope.disabled = function() {
            var fn = $scope.isDisabled();
            return $scope.loading || fn && fn();
        };
        $scope.isImage = function() {
            return $scope.model && $scope.model.mimeType && $scope.model.base64data;
        };
        function base64prefix() {
            return 'data:' + $scope.model.mimeType + ';base64,';
        }
        $scope.imgSrc = function() {
            if (!$scope.model || !$scope.model.mimeType || !$scope.model.base64data) {
                return null;
            }
            return base64prefix() + $scope.model.base64data;
        };
        function relocateApplet() {
            // viivästetään jotta angular ehtii digestoida ensin
            setTimeout(function() {
                $(FileAPIProxy.container).css('top', $scope.element.offset().top);
                $(FileAPIProxy.container).css('left', $scope.element.offset().left);
                $scope.loading = false;
                $scope.$digest();
            }, 100);
        }
        $scope.deleteImage = function(event) {
            $scope.model.mimeType = undefined;
            $scope.model.base64data = undefined;
            $scope.model.filename = undefined;
            relocateApplet();
        };
        $scope.onUpload = function(event) {
            var fs = event.target.files;
            if (fs.length == 0) {
                return;
            }
            var reader = new FileReader();
            reader.onload = function(ev) {
                $scope.model.filename = fs[0].name;
                $scope.model.mimeType = fs[0].type;
                $scope.model.base64data = ev.target.result.substring(base64prefix().length);
                $scope.$digest();
                relocateApplet();
            };
            $scope.loading = true;
            reader.readAsDataURL(fs[0]);
        };
    }
    return {
        restrict: 'E',
        replace: true,
        templateUrl: 'js/shared/directives/imageFieldIe9.html',
        controller: controller,
        link: function(scope, element, attrs, controller) {
            scope.element = $('button.upload', element);
            if (!scope.element.fileReader) {
                return;
            }
            scope.element.fileReader({
                //debugMode: true,
                filereader: 'lib/filereader/filereader.swf',
                expressInstall: 'lib/swfobject/expressInstall.swf',
                accept: 'image/*',
                callback: function(ev) {
                    scope.ready = true;
                }
            });
            scope.element.change(function(ev) {
                scope.onUpload(ev);
            });
        },
        scope: {
            isDisabled: '&',
            //disable upload
            model: '=' // model (filename, mimeType, base64data)
        }
    };
});