'use strict';

var app = angular.module('ImageDirective', []);

app.directive('imageField', function($log, TarjontaService, PermissionService) {
    function controller($scope, $q, $element, $compile) {
        /*
         * DEFAULT VARIABLES:
         */
        $scope.ctrl = {
            imagesLoaded: {},
            images: {},
            attached: false,
            isSaveButtonVisible: true,
            image: null,
            html: {},
            filename: null,
            editable: false
        };

        if (angular.isUndefined($scope.btnNameRemove)) {
            $scope.btnNameRemove = "remove";
        }

        if (angular.isUndefined($scope.btnNameSave)) {
            $scope.ctrl.isSaveButtonVisible = false;
        }

        if (!angular.isUndefined($scope.editable)) {
            $scope.ctrl.editable = ($scope.editable === "true");
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
                    var base64 = response.result.base64data;
                    var mime = response.result.mimeType;
                    var filename = response.result.filename;
                    var kieliUri = response.result.kieliUri;
                    $scope.ctrl.imagesLoaded[uri] = response.result;

                    var html = '<div id="show"><img width="300" height="300" src="data:' + mime + ';base64,' + base64 + '"/>';
                    if ($scope.ctrl.editable) {
                        html += '<div>' + filename + '</div><a href="" class="btn" type="submit" ng-click="deleteImage($event, \'' + kieliUri + '\')">{{btnNameRemove}}</a>';
                    }
                    html += '</div>';
                    $element.find('#show').replaceWith($compile(html)($scope));
                } else if (response.status === 'NOT_FOUND') {
                    var input = '<div id="show"><!-- no image --></div>';
                    $element.find('#show').replaceWith($compile(input)($scope));
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


        $scope.deleteImage = function(event, uri) {
            if (!angular.isUndefined(uri)) {
                var ResourceImage = TarjontaService.resourceImage($scope.oid, uri);
          
                ResourceImage.delete({}, function(response) {
                    var input = '<div id="show"><!-- image removed --></div>';
                    $element.find('#show').replaceWith($compile(input)($scope));
                    $scope.removeUnsavedImage(event, uri);
                });
      
            } else {
                throw new Error("Language uri cannot be undefined!");
            }
        };

        $scope.isNewImageAttached = function(event, kieliUri) {
            return !angular.isUndefined(kieliUri) && !angular.isUndefined($scope.ctrl.images[kieliUri]);
        };

        $scope.removeUnsavedImage = function(event, uri) {
            if (!angular.isUndefined(uri)) {
                $element.find('#input_' + uri).remove();
                $element.find('#img_' + uri).remove();

                if (!angular.isUndefined($scope.ctrl.images[uri])) {
                    delete $scope.ctrl.images[uri];
                }
                if (!angular.isUndefined($scope.ctrl.imagesLoaded[uri])) {
                    delete $scope.ctrl.imagesLoaded[uri];
                }

                $scope.addElemInputFile(uri);
                $scope.addElemImg(uri);

            }
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

                if (angular.isUndefined($scope.ctrl.html[uri])) {
                    $scope.ctrl.html[uri] = {img: null, input: null};
                    $scope.ctrl.images[uri] = {};
                    $scope.addElemInputFile(uri);
                    $scope.addElemImg(uri);
                }
            }
        });

        $scope.addElemInputFile = function(uri) {
            var html = $scope.ctrl.html[uri];
            html.input = '<input id="input_' + uri + '" ng-show="\'' + uri + '\' === uri" type="file"  accept="image/*" image="ctrl.images.' + uri + '" resize-max-height="300" resize-max-width="250"resize-quality="0.9" />';
            $element.find('#edit').append($compile(html.input)($scope));
        };

        $scope.addElemImg = function(uri) {
            var html = $scope.ctrl.html[uri];
            html.img = '<img id="img_' + uri + '" ng-show="\'' + uri + '\' === uri"  ng-src="{{ctrl.images.' + uri + '.resized.dataURL}}"/>';
            $element.find('#preview').append($compile(html.img)($scope));
        };

        /*
         * Listener for top layout save buttons.
         * Upload all images to tarjonta service in ctrl.image map<uri, image>.
         */
        $scope.$on('onImageUpload', function(res) {
            $scope.saveAllImages();
        });

        $scope.getNextMapKey = function(map) {
            for (var key in map) {
                return key;
            }
            return null;
        };

        $scope.saveAllImages = function() {
            var key = $scope.getNextMapKey($scope.ctrl.images);

            if (key !== null && !angular.isUndefined(key) && !angular.isUndefined($scope.ctrl.images[key])) {
                var deferred = $scope.uploadImage(null, key, $scope.ctrl.images[key]);
                delete $scope.ctrl.images[key];

                deferred.promise.then(function(res) {
                    $scope.saveAllImages();
                });
            }
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
