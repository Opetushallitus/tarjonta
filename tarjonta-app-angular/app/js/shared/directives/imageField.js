'use strict';

var app = angular.module('ImageDirective', []);

app.directive('imageField', function($log, TarjontaService, PermissionService) {
    function controller($scope, $q, $element, $compile) {
        /*
         * DEFAULT VARIABLES:
         */
        $scope.ctrl = {
            images: {},
            base64: {},
            mime: {},
            filename: "",
            isSaveButtonVisible: true
        };

        if (angular.isUndefined($scope.btnNameRemove)) {
            $scope.btnNameRemove = "remove";
        }

        if (angular.isUndefined($scope.btnNameSave)) {
            $scope.ctrl.isSaveButtonVisible = false;
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
                    $scope.ctrl.base64 = response.result.base64data;
                    $scope.ctrl.mime = response.result.mimeType;
                    $scope.ctrl.filename = response.result.filename;

                    var input = '<div><img width="300" height="300" src="data:' + $scope.ctrl.mime + ';base64,' + $scope.ctrl.base64 + '"></div>';
                    $element.find('div').replaceWith($compile(input)($scope));
                    $scope.clearImage(); //clear pre-uploaded image. 
                } else if (response.status === 'NOT_FOUND') {
                    $scope.ctrl.base64 = {};
                    var input = '<div><!-- no image --></div>';
                    $element.find('div').replaceWith($compile(input)($scope));
                    $scope.clearImage();
                    console.info("Image not found.");
                } else {
                    console.error("Image upload failed.", response);
                }
            });
        };

        $scope.uploadImage = function(event, kieliUri, image) {
            var deferred = $q.defer();
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

                    deferred.resolve();
                }, function(error) {
                    console.error("Image upload failed.", error);

                    deferred.resolve();
                });
            });

            return deferred;
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
                $scope.clearImage();
                $scope.ctrl.filename = null;
            });
        };

        /*
         * INIT ACTIONS:
         */

        $scope.$watch('uri', function(uri, oldObj) {
            console.log(uri);
            if (!angular.isUndefined($scope.oid) &&
                    $scope.oid.length > 0 &&
                    !angular.isUndefined(uri) &&
                    uri.length > 0) {
                //when page loaded, try to load img
                $scope.loadImage($scope.oid, uri);
            }
        });


        $scope.$watch('image', function(image, oldObj) {
            if (!angular.isUndefined(image) && !angular.isUndefined($scope.uri)) {
                $scope.ctrl.images[$scope.uri] = image;
            }
        });

        $scope.$on('onImageUpload', function(res) {
            $scope.foo();
        });

        $scope.getNextMapKey = function(map) {
            for (var key in map) {
                return key;
            }
            return null;


            return key;
        }

        $scope.foo = function() {
            var key = $scope.getNextMapKey($scope.ctrl.images);

            if (key !== null && !angular.isUndefined(key) && !angular.isUndefined($scope.ctrl.images[key])) {
                var deferred = $scope.uploadImage(null, key, $scope.ctrl.images[key]);
                delete $scope.ctrl.images[key];

                deferred.promise.then(function(res) {
                    $scope.foo();
                });
            }
        };

        $scope.clearImage = function() {
            $scope.image = null; //clear pre-uploaded image. 
        };
    }

    return {
        restrict: 'E',
        replace: true,
        templateUrl: "js/shared/directives/imageField.html",
        controller: controller,
        scope: {
            editable: "@", //disable upload
            uri: "=", //kieli URI     
            oid: "@", //komoto OID
            btnNameSave: "@",
            btnNameRemove: "@"
        }
    };
});
