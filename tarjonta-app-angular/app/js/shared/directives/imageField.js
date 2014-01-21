'use strict';

var app = angular.module('ImageDirective', []);

app.directive('imageField', function($log, TarjontaService, PermissionService) {
    function controller($scope, $q, $element, $compile) {
        /*
         * DEFAULT VARIABLES:
         */
        $scope.base64 = {};
        $scope.mime = {};
        $scope.filename = "";

        if (angular.isUndefined($scope.btnNameRemove)) {
            $scope.btnNameRemove = "remove";
        }

        if (angular.isUndefined($scope.btnNameSave)) {
            $scope.btnNameSave = "save";
        }

        if (angular.isUndefined($scope.editable)) {
            $scope.editable = true;
        }

        /*
         * METHODS:
         */

        $scope.loadImage = function(oid, uri) {
            if (angular.isUndefined(uri) || uri.length === 0) {
                throw new Error("Language uri cannot be undefined!");
            }

            if (angular.isUndefined(oid) || oid.length === 0) {
                throw new Error("Koulutus OID cannot be undefined!");
            }

            var ResourceImage = TarjontaService.resourceImage(oid, uri);
            var ret = $q.defer();
            ResourceImage.get({}, function(response) {
                ret.resolve(response);
            });

            ret.promise.then(function(response) {
                if (response.status === 'OK') {
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
            PermissionService.permissionResource().authorize({}, function(authResponse) {
                console.log("Authorization check : " + authResponse.result);

                if (authResponse.status !== 'OK') {
                    //not authenticated
                    console.error("User auth failed.", error);
                    return;
                }

                TarjontaService.saveImage($scope.oid, kieliUri, image, function() {
                    console.log(image);
                    $scope.loadImage($scope.oid, kieliUri); // load uploaded image to page     
                }, function(error) {
                    console.error("Image upload failed.", error);
                });
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

        if (!angular.isUndefined($scope.oid) &&
                $scope.oid.length > 0 &&
                !angular.isUndefined($scope.uri) &&
                $scope.uri.length > 0) {
            //when page loaded, try to load img
            $scope.loadImage($scope.oid, $scope.uri);
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
            btnNameSave: "@",
            btnNameRemove: "@"
        }
    }

});
