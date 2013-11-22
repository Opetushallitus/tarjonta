
var app = angular.module('app.review.ctrl', []);

app.controller('BaseReviewController', ['$scope', '$location', '$log', 'TarjontaService', '$routeParams', 'LocalisationService', 'dialogService', 'Koodisto',
    function BaseReviewController($scope, $location, $log, tarjontaService, $routeParams, LocalisationService, dialogService, koodisto) {
        $log.info("BaseReviewController()");
        $scope.model = {
            routeParams: $routeParams,
            collapse: {
                perusTiedot: false,
                kuvailevatTiedot: false,
                sisaltyvatOpintokokonaisuudet: false,
                hakukohteet: false,
                model: true
            },
            languages: [],
            koulutus: $scope.koulutusModel.result // preloaded in route resolve, see
        };

        $scope.lisatiedot = [
            {type: "TAVOITTEET", isKomo: true},
            {type: "LISATIETOA_OPETUSKIELISTA", isKomo: false},
            {type: "PAAAINEEN_VALINTA", isKomo: false},
            {type: "MAKSULLISUUS", isKomo: false},
            {type: "SIJOITTUMINEN_TYOELAMAAN", isKomo: false},
            {type: "PATEVYYS", isKomo: true},
            {type: "JATKOOPINTO_MAHDOLLISUUDET", isKomo: true},
            {type: "SISALTO", isKomo: false},
            {type: "KOULUTUKSEN_RAKENNE", isKomo: true},
            {type: "LOPPUKOEVAATIMUKSET", isKomo: false}, // leiskassa oli "lopputyön kuvaus"
            {type: "KANSAINVALISTYMINEN", isKomo: false},
            {type: "YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA", isKomo: false},
            {type: "TUTKIMUKSEN_PAINOPISTEET", isKomo: false},
            {type: "ARVIOINTIKRITEERIT", isKomo: false},
            {type: "PAINOTUS", isKomo: false},
            {type: "KOULUTUSOHJELMAN_VALINTA", isKomo: false},
            {type: "KUVAILEVAT_TIEDOT", isKomo: false}
        ];

        $scope.getKuvausApiModelLanguageUri = function(boolIsKomo, key, kieliuri) {
            var kuvaus = null;
            if (typeof boolIsKomo !== 'boolean') {
                converter.throwError('An invalid boolean variable : ' + boolIsKomo);
            }

            if (boolIsKomo) {
                kuvaus = $scope.model.koulutus.kuvausKomo;
            } else {
                kuvaus = $scope.model.koulutus.kuvausKomoto;
            }

            return kuvaus[key].tekstis[kieliuri];
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

        $scope.searchKoodi = function(obj, koodistouri, uri, locale) {
            var promise = koodisto.getKoodi(koodistouri, uri, locale);
            promise.then(function(data) {
                obj.name = data.koodiNimi;
                obj.versio = data.koodiVersio;
                obj.koodi_uri = data.koodiUri;
                obj.locale = data.koodiArvo;
            });
        };

        if (!angular.isUndefined($scope.model.koulutus) && !angular.isUndefined($scope.model.koulutus.oid)) {
            var map = {};
            angular.forEach(window.CONFIG.app.userLanguages, function(val) {
                map[val] = val;
            });

            angular.forEach($scope.model.koulutus.opetuskielis.meta, function(val, key) {
                map[key] = key;
            });

            angular.forEach(map, function(val, key) {
                var lang = {'koodi_uri': val};
                $scope.searchKoodi(lang, window.CONFIG.env['koodisto-uris.kieli'], key, "FI")
                $scope.model.languages.push(lang);
            });
        } else {
            console.error("No koulutus found?");
        }

    }]);

