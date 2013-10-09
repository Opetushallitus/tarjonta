
var app = angular.module('app.review.ctrl', []);

app.controller('BaseReviewController', ['$scope', '$location', '$log', 'TarjontaService', '$routeParams', 'LocalisationService', '$modal',
    function BaseReviewController($scope, $location, $log, tarjontaService, $routeParams, LocalisationService, $modal) {
        $log.info("BaseReviewController()");

        $scope.searchByOid = "1.2.246.562.5.2013091114080489552096";
        $scope.opetuskieli = 'kieli_fi';
        $scope.model = {
            routeParams: $routeParams,
            collapse: {
                perusTiedot: false,
                kuvailevatTiedot: false,
                sisaltyvatOpintokokonaisuudet: false,
                hakukohteet: false,
                model: true
            },
            // TODO default languages from somewhere?
            languages: [
                {
                    name: "Suomi",
                    locale: "fi",
                    koodi_uri: "kieli_fi"
                },
                {
                    name: "Ruotsi",
                    locale: "sv",
                    koodi_uri: "kieli_sv"
                },
                {
                    name: "Englanti",
                    locale: "en",
                    koodi_uri: "kieli_en"
                },
            ],
            koulutus: $scope.koulutusx, // preloaded in route resolve
            foo: "bar"
        };

        // TODO respect my autoritai!
        // $scope.model.routeParams.id = "1.2.246.562.5.2013091015190138558153";

        $scope.doEdit = function(event, targetPart) {
            $log.info("doEdit()...", event, targetPart);
            $location.path("/kk/edit/load/" + targetPart + "/none/" + $scope.model.koulutus.oid + "/none");
        };

        $scope.goBack = function(event) {
            $log.info("goBack()...");
        };

        $scope.doDelete = function(event) {
            $log.info("doDelete()...");
        };

        $scope.doCopy = function(event) {
            $log.info("doCopy()...");
        };

        $scope.doMoveToBeSubPart = function(event) {
            $log.info("doMoveToBeSubPart()...");
        };

        $scope.doAddParallel = function(event) {
            $log.info("doAddParallel()...");
        };

        $scope.load = function(oid) {
            $log.info("load()...");

            if (!oid) {
                oid = $scope.model.routeParams.id;
            }

            tarjontaService.getKoulutus({oid: oid}, function(data) {
                $scope.model.koulutus = data;
                $log.info("  load got: ", $scope.model.koulutus);
            });
        };
    }]);

