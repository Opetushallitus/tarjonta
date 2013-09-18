var app = angular.module('tarjontaApp.controllers', ['tarjontaApp.services']);


app.controller('RootCtrl', function($scope, Localisation) {
    console.log("RootCtrl()");

    $scope.locale = "fi";
    $scope.localisations = [];

    Localisation.query(function(data) {
        console.log("Loaded: " + data);
        $scope.localisations = data;
    });

    // Returns translation if it exists
    $scope.t = function(key, params) {
        console.log("t(" + key + ", " + params + ")");
        var v = $scope.localisations[key];

        if (v != undefined) {
            var result = v.value;

            if (params != undefined) {
                result = result.replace(/{(\d+)}/g, function(match, number) {
                    return typeof params[number] != 'undefined' ? params[number] : match;
                });
            }

            return result;
        } else {
            // Unknown translation, maybe create placeholder for it?
            return "[" + key + "]";
        }
    }
});


app.controller('MyCtrl1', [function() {
        console.log("MyCtrl1()");
    }]);

app.controller('MyCtrl2', function($scope, instagram) {
    console.log("MyCtrl2()");

    $scope.pics = [];
    $scope.page = 0;
    $scope.pageSize = 2;

    instagram.fetchPopular(function(data) {
        $scope.pics = data;
    });
});
