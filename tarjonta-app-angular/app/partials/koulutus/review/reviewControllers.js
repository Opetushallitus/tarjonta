
var app = angular.module('app.review.ctrl', []);

app.controller('BaseReviewController', ['$scope', '$location', '$log', 'TarjontaService', '$routeParams', 'LocalisationService', 'dialogService',
    function BaseReviewController($scope, $location, $log, tarjontaService, $routeParams, LocalisationService, dialogService) {
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
            koulutus: $scope.koulutusx, // preloaded in route resolve, see
            foo: "bar"
        };

        $scope.doEdit = function(event, targetPart) {
            $log.info("doEdit()...", event, targetPart);
            $location.path("/koulutus/" + $scope.model.koulutus.oid + "/edit");
        };

        $scope.goBack = function(event) {
            $log.info("goBack()...");
            window.history.back();
        };

        $scope.doDelete = function(event) {
            $log.info("doDelete()...");

            var texts = {
                title: LocalisationService.t("koulutus.review.poista.confirm.title"),
                description: LocalisationService.t("koulutus.review.poista.confirm.description", [$scope.model.koulutus.koulutuskoodi.arvo]),
                ok: LocalisationService.t("ok"),
                cancel: LocalisationService.t("cancel")
            };

            var d = dialogService.showDialog(texts);
            d.result.then(function(data) {
                $log.info("GOT: ", data);
                if ("ACTION" === data) {
                    // TODO actual delete!
                    $log.info("ACTUALLY DELETE IT NOW!");
                    dialogService.showNotImplementedDialog();
                }
            });

        };

        $scope.doCopy = function(event) {
            $log.info("doCopy()...");
            dialogService.showNotImplementedDialog();
        };

        $scope.doMoveToBeSubPart = function(event) {
            $log.info("doMoveToBeSubPart()...");
            dialogService.showNotImplementedDialog();
        };

        $scope.doAddParallel = function(event) {
            $log.info("doAddParallel()...");
            dialogService.showNotImplementedDialog();
        };

        $scope.doPreview = function(event) {
            $log.info("doPreview()...");
            dialogService.showNotImplementedDialog();
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

