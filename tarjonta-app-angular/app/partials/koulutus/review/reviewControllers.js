
var app = angular.module('app.review.ctrl', []);

app.controller('BaseReviewController', ['$scope', '$location', '$route', '$log', 'TarjontaService', '$routeParams', 'LocalisationService', 'dialogService', 'Koodisto', '$modal', 'KoulutusConverterFactory',
    function BaseReviewController($scope, $location, $route, $log, tarjontaService, $routeParams, LocalisationService, dialogService, koodisto, $modal, KoulutusConverterFactory) {
        $log.info("BaseReviewController()");

        $scope.formControls = {};
        $scope.model = {
            koodistoLocale: LocalisationService.getLocale(),
            routeParams: $routeParams,
            collapse: {
                perusTiedot: false,
                kuvailevatTiedot: false,
                sisaltyvatOpintokokonaisuudet: true,
                hakukohteet: true,
                model: true
            },
            languages: [],
            koulutus: $scope.koulutusModel.result, // preloaded in route resolve, see
            selectedKomoOid: [$scope.koulutusModel.result.komoOid]
        };

        var komoOid = $scope.koulutusModel.result.komoOid;

        tarjontaService.getChildKoulutuksetPromise(komoOid).then(function(children) {
            $scope.children = children;
            console.log("children:", children);
        });

        tarjontaService.getParentKoulutuksetPromise(komoOid).then(function(parents) {
            $scope.parents = parents;
            console.log("parents:", parents);
        });

        $scope.lisatiedot = KoulutusConverterFactory.KUVAUS_ORDER;

        $scope.getKuvausApiModelLanguageUri = function(boolIsKomo) {
            var kuvaus = null;
            if (typeof boolIsKomo !== 'boolean') {
                converter.throwError('An invalid boolean variable : ' + boolIsKomo);
            }

            if (boolIsKomo) {
                kuvaus = $scope.model.koulutus.kuvausKomo;
            } else {
                kuvaus = $scope.model.koulutus.kuvausKomoto;
            }

            return kuvaus;
        };

        $scope.doEdit = function(event, targetPart) {
            $log.info("doEdit()...", event, targetPart);

            if (targetPart === 'SISALTYVATOPINTOKOKONAISUUDET_LIITA') {
                $scope.luoKoulutusDialogOrg = $scope.selectedOrgOid;
                $scope.luoKoulutusDialog = $modal.open({
                    templateUrl: 'partials/koulutus/sisaltyvyys/liita-koulutuksia.html',
                    controller: 'LiitaSisaltyvyysCtrl',
                    resolve: {
                        targetKomo: function() {
                            return {oid: $scope.koulutusModel.result.komoOid, nimi: $scope.model.koulutus.koulutusohjelma.tekstis['kieli_' + $scope.model.koodistoLocale]};
                        },
                        organisaatioOid: function() {
                            return  {oid: $scope.model.koulutus.organisaatio.oid, nimi: $scope.model.koulutus.organisaatio.nimi}
                        }
                    }
                });
            } else if (targetPart === 'SISALTYVATOPINTOKOKONAISUUDET_POISTA') {
                $scope.luoKoulutusDialogOrg = $scope.selectedOrgOid;
                $scope.luoKoulutusDialog = $modal.open({
                    templateUrl: 'partials/koulutus/sisaltyvyys/poista-koulutuksia.html',
                    controller: 'PoistaSisaltyvyysCtrl',
                    resolve: {
                        targetKomo: function() {
                            return {oid: $scope.koulutusModel.result.komoOid, nimi: $scope.model.koulutus.koulutusohjelma.tekstis['kieli_' + $scope.model.koodistoLocale]};
                        },
                        organisaatioOid: function() {
                            return  {oid: $scope.model.koulutus.organisaatio.oid, nimi: $scope.model.koulutus.organisaatio.nimi}
                        }
                    }

                });

            } else {
                $location.path("/koulutus/" + $scope.model.koulutus.oid + "/edit");
            }
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
                $scope.searchKoodi(lang, window.CONFIG.env['koodisto-uris.kieli'], key, $scope.model.koodistoLocale)
                $scope.model.languages.push(lang);
            });
        } else {
            console.error("No koulutus found?");
        }

        $scope.treeClickHandler = function(obj, event) {
//            tarjontaService.haeKoulutukset({//search parameter object
//                komoOid: obj.oid
//            }).then(function(result) {
//                $location.path("/koulutus/" + result.tulokset[0].tulokset[0].oid);
//                $route.reload();
//            });
        };

    }]);

