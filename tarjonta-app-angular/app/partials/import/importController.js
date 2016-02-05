var app = angular.module('app.import.ctrl', []);
app.controller('ImportController', function($scope, XLSXReaderService) {

    $scope.readFile = function(files) {
        var file = _.first(files);

        if (!file) {
            return;
        }

        XLSXReaderService
            .readFile(file, true)
            .then(function(xlsx) {
                console.log('Xls data:', xlsx);
            });
    };

});