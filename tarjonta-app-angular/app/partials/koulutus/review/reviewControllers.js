
var app = angular.module('app.review.ctrl', []);

app.controller('BaseReviewController', ['$scope', '$window', '$location', '$route', '$log', 'TarjontaService', '$routeParams', 'LocalisationService', 'dialogService', 'Koodisto', '$modal', 'KoulutusConverterFactory', 'HakukohdeKoulutukses', 'SharedStateService',
    function BaseReviewController($scope, $window, $location, $route, $log, tarjontaService, $routeParams, LocalisationService, dialogService, koodisto, $modal, KoulutusConverterFactory, HakukohdeKoulutukses,SharedStateService,AuthService) {
        $log.info("BaseReviewController()");

       if(angular.isUndefined( $scope.koulutusModel.result)){
           $location.path("/error");
           return;
       }


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

        $scope.model.showError = false;

        $scope.model.validationmsgs = [];

        $scope.model.userLangUri;


        console.log('KOULUTUS : ', $scope.model.koulutus);

        for(var kieliUri in $scope.model.koulutus.koulutusohjelma.tekstis) {

            if (kieliUri.indexOf(kieliUri) != -1) {
                $scope.model.userLangUri = kieliUri;
            }

        }

        console.log('USER LANGUAGE : ', $scope.model.userLangUri);

        var hakukohdePromise =  HakukohdeKoulutukses.getKoulutusHakukohdes($scope.model.koulutus.oid);

        hakukohdePromise.then(function(hakukohteet){
           $scope.model.hakukohteet = hakukohteet.result;

        });


        var checkIsOkToRemoveHakukohde = function(hakukohde) {

             var hakukohdeQueryPromise = HakukohdeKoulutukses.getHakukohdeKoulutukses(hakukohde.oid);

            hakukohdeQueryPromise.then(function(hakukohdeKoulutuksesResponse){

                if (hakukohdeKoulutuksesResponse.result.length > 1) {

                    var texts = {
                        title: LocalisationService.t("koulutus.review.perustiedot.remove.koulutus.title"),
                        description: LocalisationService.t("koulutus.review.perustiedot.remove.koulutus.desc"),
                        ok: LocalisationService.t("ok"),
                        cancel: LocalisationService.t("cancel")
                    };

                    var d = dialogService.showDialog(texts);
                    d.result.then(function(data){
                        if (data) {
                            reallyRemoveHakukohdeFromKoulutus(hakukohde);

                        }
                    });


                } else {

                    $scope.model.validationmsgs.push('koulutus.review.hakukohde.remove.exp.msg');
                    $scope.model.showError = true;

                }

            })

        };

        var reallyRemoveHakukohdeFromKoulutus = function(hakukohde) {



            var koulutusOids =[];

            koulutusOids.push($scope.model.koulutus.oid);

            HakukohdeKoulutukses.removeKoulutuksesFromHakukohde(hakukohde.oid,koulutusOids);

               angular.forEach($scope.model.hakukohteet,function(loopHakukohde){

                      if (loopHakukohde.oid === hakukohde.oid) {
                           var indx = $scope.model.hakukohteet.indexOf(loopHakukohde);
                          $scope.model.hakukohteet.splice(indx,1);

                      }

               });

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

        $scope.removeKoulutusFromHakukohde = function(hakukohde) {

            checkIsOkToRemoveHakukohde(hakukohde);
            /*
            if (checkIsOkToRemoveHakukohde(hakukohde)) {

                    var texts = {
                        title: LocalisationService.t("koulutus.review.perustiedot.remove.koulutus.title"),
                        description: LocalisationService.t("koulutus.review.perustiedot.remove.koulutus.desc"),
                        ok: LocalisationService.t("ok"),
                        cancel: LocalisationService.t("cancel")
                    };

                    var d = dialogService.showDialog(texts);
                    d.result.then(function(data){
                        if (data) {
                            reallyRemoveHakukohdeFromKoulutus(hakukohde);

                        }
                    });


            } else {

                $scope.model.validationmsgs.push('koulutus.review.hakukohde.remove.exp.msg');
                $scope.model.showError = true;

            }   */

        }

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
                if (data) {
                    // TODO actual delete!
                    $log.info("ACTUALLY DELETE IT NOW!");
                    dialogService.showNotImplementedDialog();
                }
            });

        };

        $scope.addHakukohde = function() {

               console.log('KOULUTUS : ', $scope.model.koulutus);

            var koulutusOids = [];
            koulutusOids.push($scope.model.koulutus.oid);



            SharedStateService.addToState('SelectedKoulutukses',koulutusOids);
            SharedStateService.addToState('SelectedOrgOid',$scope.model.koulutus.organisaatio.oid);
            $location.path('/hakukohde/new/edit');

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
            $window.location.href = window.CONFIG.env['web.url.oppija.preview'] + $scope.model.koulutus.oid;
            //example : https://itest-oppija.oph.ware.fi/app/preview.html#!/korkeakoulu/1.2.246.562.5.2014021318092550673640
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

