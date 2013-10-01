
var app = angular.module('helpers.localisation', ['app.services', 'localisation', 'config']);

app.controller('HelpersLocalisatonCtrl', function($scope) {

    console.log("HelpersLocalisatonCtrl()");

    $scope.model = {
        localisations : {
            "foo" : {
                key : "foo",
                locale : "fi",
                value : "foovalue"
            }
        }
    };

});
