'use strict';

var app = angular.module('ImageDirective', []);

app.directive('imageField', function($log, TarjontaService) {
    function controller($scope, $q, $element, $attrs, $compile) {
        /*
         * DEFAULT VARIABLES:
         */

        $scope.base64 = {};
        $scope.mime = {};
        $scope.filename = "";
        
        if(angular.isUndefined($scope.editable)){
            $scope.editable = true;
        }

        /*
         * METHODS:
         */

        $scope.loadImage = function() {
            var ResourceImage = TarjontaService.resourceImage($scope.oid, $scope.uri);
            var ret = $q.defer();
            ResourceImage.get({}, function(response) {
                ret.resolve(response);
            });

            ret.promise.then(function(response) {
                if (response.status === 'OK') {
                    console.log(response)
                    $scope.base64 = response.result.base64data;
                    $scope.mime = response.result.mimeType;
                    $scope.filename = response.result.filename;

                    var input = '<div><img width="300" height="300" src="data:' + $scope.mime + ';base64,' + $scope.base64 + '"></div>';
                    $element.find('div').replaceWith($compile(input)($scope));
                    $scope.crear(); //clear pre-uploaded image. 
                } else if (response.status === 'NOT_FOUND') {
                    console.info("Image not found.");
                } else {
                    console.error("Image upload failed.", response);
                }
            });
        };

        $scope.uploadImage = function(event, kieliUri, image) {
            TarjontaService.saveImage($scope.oid, $scope.uri, image, function() {
                console.log(image);
                $scope.loadImage(); // load uploaded image to page     
            }, function() {
                console.error("Image upload failed.");
            });
        };

        $scope.deleteImage = function() {
            var ResourceImage = TarjontaService.resourceImage($scope.oid, $scope.uri);
            var ret = $q.defer();
            ResourceImage.delete({}, function(response) {
                ret.resolve(response);
            });

            ret.promise.then(function(response) {
                var input = '<div><!-- image removed --></div>';
                $element.find('div').replaceWith($compile(input)($scope));
                $scope.crear();
                $scope.filename = null;
            });
        };

        /*
         * INIT ACTIONS:
         */

        if (!angular.isUndefined($scope.oid) && $scope.oid !== null) {
            //when page loaded, try to load img
            $scope.loadImage();
        }

        $scope.crear = function() {
            $scope.image = null; //clear pre-uploaded image. 
        }
    }

    return {
        restrict: 'E',
        replace: true,
        templateUrl: "js/shared/directives/imageField.html",
        controller: controller,
        scope: {
            editable: "@", //disable upload
            uri: "@", //kieli URI     
            oid: "@", //komoto OID
            model: "=" // map jossa arvo->nimi
        }
    }

});
