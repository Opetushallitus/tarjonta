
'use strict';

var app = angular.module('app.haku.ctrl', ['app.haku.list.ctrl', 'app.haku.review.ctrl', 'app.haku.edit.ctrl', 'app.haku.edit.organisations.ctrl']);

app.controller('HakuRoutingController', ['$scope', '$log', '$routeParams', '$route', 'dialogService', 'LocalisationService', 'HakuV1Service',
    function HakukohdeRoutingController($scope, $log, $routeParams, $route, dialogService, LocalisationService, HakuV1Service) {

        $log = $log.getInstance("HakuRoutingController");

        $log.info("HakuRoutingController()", $routeParams);
        $log.info("$route: ", $route);
        $log.info("$route action: ", $route.current.$$route.action);
        $log.info("SCOPE: ", $scope);
        
        $scope.doDeleteHaku = function(haku) {
            $log.debug("doDeleteHaku()", haku);

            return HakuV1Service.delete(haku.oid).then(function(result) {
                $log.info("delete result", result);
                if (result.status == "OK") {
                    $log.info("SHOW DELETE DONE DIALOG");
                    dialogService.showSimpleDialog(
                            LocalisationService.t("haku.delete.ok"),
                            LocalisationService.t("haku.delete.ok.description"),
                            LocalisationService.t("ok"),
                            undefined);
                    return true;
                } else {
                    var errorMessage = "<ul>"
                    angular.forEach(result.errors, function(error) {
                        var msg = LocalisationService.t(error.errorMessageKey, error.errorMessageParameters);
                        errorMessage = errorMessage + "<li>" + msg + "</li>";
                    });
                    errorMessage = errorMessage + "</ul>";
                    var desciptionParams = [errorMessage];

                    dialogService.showSimpleDialog(
                            LocalisationService.t("haku.delete.failed"),
                            LocalisationService.t("haku.delete.failed.description", desciptionParams),
                            LocalisationService.t("ok"),
                            undefined);
                    return false;
                }
            });

            // dialogService.showNotImplementedDialog();
        };
        
    }
]);
