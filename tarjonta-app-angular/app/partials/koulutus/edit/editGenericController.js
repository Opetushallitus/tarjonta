var app = angular.module('app.edit.ctrl.generic', []);

app.controller('EditGenericController', function EditGenericController($scope) {

    $scope.init({
        childScope: $scope
    });

});