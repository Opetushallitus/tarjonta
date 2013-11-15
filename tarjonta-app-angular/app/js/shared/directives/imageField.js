'use strict';

var app = angular.module('ImageDirective', []);

app.directive('imageField', function($log, TarjontaService) {
    function controller($scope) {
        $scope.base64 = {};
        $scope.mime = {};

        if (!angular.isUndefined($scope.oid)) {
            var promise = TarjontaService.resourceImage($scope.oid, $scope.uri);
            promise.then(function(response) {
                $scope.base64 = response.result.base64data;
                $scope.mime = response.result.mimeType;
            });
        }
    }

    return {
        restrict: 'E',
        replace: true,
        templateUrl: "js/shared/directives/imageField.html",
        controller: controller,
        scope: {
            uri: "@", //kieli URI     
            oid: "@", //komoto OID
            model: "=" // map jossa arvo->nimi
        }
    }

});
