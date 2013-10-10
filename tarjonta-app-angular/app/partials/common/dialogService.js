
var app = angular.module('app.dialog', ['ui.bootstrap', 'ngAnimate']);

app.service('dialogService', ['$modal', '$log', '$rootScope',
    function($modal, $log, $rootScope) {

        var dialogDefaults = {
            templateUrl: 'partials/common/dialog.html',
            controller: "DialogServiceController",
            backdrop: true,
            keyboard: true
        };

        var dialogTextDefaults = {
            title: "Really?",
            description: "Really do <b>that</b>?",
            okText: "YEAS please!",
            closeText: "Naaaah..."
        };

        this.showDialog = function(customDialogTextDefaults, customDialogDefaults) {
            var tempDialogDefaults = {};
            angular.extend(tempDialogDefaults, dialogDefaults, customDialogDefaults);

            var tempDialogTextDefaults = {};
            angular.extend(tempDialogTextDefaults, dialogTextDefaults, customDialogTextDefaults);

            if (!tempDialogDefaults.scope) {
                tempDialogDefaults.scope = $rootScope.$new();
            }
            if (!tempDialogDefaults.scope.dialog) {
                tempDialogDefaults.scope.dialog = tempDialogTextDefaults;
            }

            $log.info("showDialog(): ", tempDialogTextDefaults, tempDialogDefaults);

            return $modal.open(tempDialogDefaults);
        };


    }]);

app.controller('DialogServiceController', ['$scope', '$log', '$modalInstance',
    function($scope, $log, $modalInstance) {

        $log.info("dialogServiceController()");

        $scope.onAction = function() {
            $log.info("onAction()", $modalInstance);
            $modalInstance.close("ACTION");
        };

        $scope.onClose = function() {
            $log.info("onClose()", $modalInstance);
            $modalInstance.close("CANCEL");
        };

    }]);
