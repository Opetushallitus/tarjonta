
var app = angular.module('app.review.ctrl', []);

app.controller('BaseReviewController', [
    'PermissionService', '$q', '$scope',
    '$window', '$location', '$route',
    '$log', 'TarjontaService', '$routeParams',
    'LocalisationService', 'dialogService', 'Koodisto',
    'KoodistoURI', '$modal', 'KoulutusConverterFactory',
    'HakukohdeKoulutukses', 'SharedStateService', 'AuthService',
    function BaseReviewController(PermissionService, $q, $scope,
            $window, $location, $route,
            $log, TarjontaService, $routeParams,
            LocalisationService, dialogService, koodisto,
            KoodistoURI, $modal, KoulutusConverterFactory,
            HakukohdeKoulutukses, SharedStateService, AuthService) {

        $log = $log.getInstance("BaseReviewController");

        $log.info("BaseReviewController()");
        var koulutusModel = $route.current.locals.koulutusModel.result;
        if (angular.isUndefined(koulutusModel)) {
            $location.path("/error");
            return;
        }

        //käyttöoikeudet
        PermissionService.koulutus.canEdit(koulutusModel.oid).then(function(data) {
            var tila = TarjontaService.getTilat()[koulutusModel.tila];
            $scope.isMutable = tila.mutable && data;
            $scope.isRemovable = tila.removable && data;
        });

        $scope.formControls = {};
        $scope.model = {
            header: {}, //review.html otsikkon tiedot
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
            koulutus: koulutusModel, // preloaded in route resolve, see
            selectedKomoOid: [koulutusModel.komoOid]
        };
        $scope.model.showError = false;
        $scope.model.validationmsgs = [];
        $scope.model.userLangUri = "kieli_" + AuthService.getLanguage();

        var hakukohdePromise = HakukohdeKoulutukses.getKoulutusHakukohdes($scope.model.koulutus.oid);
        hakukohdePromise.then(function(hakukohteet) {
            $scope.model.hakukohteet = hakukohteet.result;
        });
        var checkIsOkToRemoveHakukohde = function(hakukohde) {

            var hakukohdeQueryPromise = HakukohdeKoulutukses.getHakukohdeKoulutukses(hakukohde.oid);
            hakukohdeQueryPromise.then(function(hakukohdeKoulutuksesResponse) {

                if (hakukohdeKoulutuksesResponse.result.length > 1) {

                    var texts = {
                        title: LocalisationService.t("koulutus.review.perustiedot.remove.koulutus.title"),
                        description: LocalisationService.t("koulutus.review.perustiedot.remove.koulutus.desc"),
                        ok: LocalisationService.t("ok"),
                        cancel: LocalisationService.t("cancel")
                    };
                    var d = dialogService.showDialog(texts);
                    d.result.then(function(data) {
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
            var koulutusOids = [];
            koulutusOids.push($scope.model.koulutus.oid);
            HakukohdeKoulutukses.removeKoulutuksesFromHakukohde(hakukohde.oid, koulutusOids);
            angular.forEach($scope.model.hakukohteet, function(loopHakukohde) {

                if (loopHakukohde.oid === hakukohde.oid) {
                    var indx = $scope.model.hakukohteet.indexOf(loopHakukohde);
                    $scope.model.hakukohteet.splice(indx, 1);
                }

            });
        };
        var komoOid = koulutusModel.komoOid;
        TarjontaService.getChildKoulutuksetPromise(komoOid).then(function(children) {
            $scope.children = children;
            $log.debug("children:", children);
        });
        TarjontaService.getParentKoulutuksetPromise(komoOid).then(function(parents) {
            $scope.parents = parents;
            $log.debug("parents:", parents);
        });

        $scope.lisatiedot = KoulutusConverterFactory.STRUCTURE[koulutusModel.toteutustyyppi].KUVAUS_ORDER;
        if (koulutusModel.toteutustyyppi === 'KORKEAKOULUTUS') {
            for (var kieliUri in $scope.model.koulutus.koulutusohjelma.tekstis) {
                if (kieliUri.indexOf(kieliUri) != -1) {
                    $scope.model.userLangUri = kieliUri;
                }
            }
            $scope.model.header.nimi = $scope.model.koulutus.koulutusohjelma.tekstis[$scope.model.userLangUri];
        } else if (koulutusModel.toteutustyyppi === 'LUKIOKOULUTUS') {
            for (var kieliUri in $scope.model.koulutus.koulutusohjelma.meta) {
                if (kieliUri.indexOf(kieliUri) != -1) {
                    $scope.model.userLangUri = kieliUri;
                }
            }
            $scope.model.header.nimi = $scope.model.koulutus.koulutusohjelma.meta[$scope.model.userLangUri].nimi;
        }

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

        $scope.getValmistavaKoulutusKuvausApiModelLanguageUri = function(boolIsKomo) {
            var kuvaus = null;
            if (angular.isDefined($scope.model.koulutus.valmistavaKoulutus)) {
                if (typeof boolIsKomo !== 'boolean') {
                    converter.throwError('An invalid boolean variable : ' + boolIsKomo);
                }

                if (boolIsKomo) {
                    kuvaus = $scope.model.koulutus.valmistavaKoulutus.kuvausKomo;
                } else {
                    kuvaus = $scope.model.koulutus.valmistavaKoulutus.kuvausKomoto;
                }
            }

            return kuvaus;
        };

        $scope.doEdit = function(event, targetPart) {
            if (!$scope.isMutable) {
                return;
            }
            $log.info("doEdit()...", event, targetPart);
            if (targetPart === 'SISALTYVATOPINTOKOKONAISUUDET_LIITA') {
                $scope.luoKoulutusDialogOrg = $scope.selectedOrgOid;
                $scope.luoKoulutusDialog = $modal.open({
                    templateUrl: 'partials/koulutus/sisaltyvyys/liita-koulutuksia.html',
                    controller: 'LiitaSisaltyvyysCtrl',
                    resolve: {
                        targetKomo: function() {
                            return {
                                vuosi: $scope.model.koulutus.koulutuksenAlkamisvuosi,
                                kausi: $scope.model.koulutus.koulutuksenAlkamiskausi,
                                oid: $scope.model.koulutus.komoOid,
                                nimi: $scope.model.koulutus.koulutusohjelma.tekstis['kieli_' + $scope.model.koodistoLocale]};
                        },
                        organisaatioOid: function() {
                            return  {
                                oid: $scope.model.koulutus.organisaatio.oid,
                                nimi: $scope.model.koulutus.organisaatio.nimi
                            };
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
                            return {oid: $scope.model.koulutus.komoOid, nimi: $scope.model.koulutus.koulutusohjelma.tekstis['kieli_' + $scope.model.koodistoLocale]};
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
            $location.path("/");
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

        $scope.getModalDialog = function() {
            return $scope.poistaModalDialog;
        }

        $scope.doDelete = function(event) {
            var poistaModalDialog = $modal.open({
                templateUrl: 'partials/koulutus/remove/poista-koulutus.html',
                controller: 'PoistaKoulutusCtrl',
                resolve: {
                    targetKomoto: function() {
                        return {oid: $scope.model.koulutus.oid, koulutuskoodi: $scope.model.koulutus.koulutuskoodi.arvo, nimi: $scope.model.koulutus.koulutusohjelma.tekstis['kieli_' + $scope.model.koodistoLocale]};
                    },
                    organisaatioOid: function() {
                        return  {oid: $scope.model.koulutus.organisaatio.oid, nimi: $scope.model.koulutus.organisaatio.nimi}
                    }
                }
            });

            poistaModalDialog.result.then(function() {
                //not working:
                // $route.reload();
                //$location.path("/koulutus/" + $scope.model.koulutus.oid);
                // force page reload, at least it works:
                window.location.reload();
            }, function() {
                /* dismissed */
            })
        };
        $scope.addHakukohde = function() {
            if (!$scope.isMutable) {
                return;
            }
            $log.debug('KOULUTUS : ', $scope.model.koulutus);
            var koulutusOids = [];
            koulutusOids.push($scope.model.koulutus.oid);
            SharedStateService.addToState('SelectedKoulutukses', koulutusOids);
            SharedStateService.addToState('SelectedToteutusTyyppi', $scope.model.koulutus.toteutustyyppi);
            SharedStateService.addToState('SelectedOrgOid', $scope.model.koulutus.organisaatio.oid);
            $location.path('/hakukohde/new/edit');
        };
        $scope.doCopy = function(event) {
            var copyModalDialog = $modal.open({
                templateUrl: 'partials/koulutus/copy/copy-move-koulutus.html',
                controller: 'CopyMoveKoulutusController',
                resolve: {
                    targetKoulutus: function() {
                        var ohjelma = $scope.model.koulutus.koulutusohjelma;
                        var strName = '';
                        if (angular.isDefined(ohjelma.tekstis) && ohjelma.tekstis != null && Object.keys(ohjelma.tekstis).length > 0) {
                            //korkeakoulu etc.
                            strName = ohjelma.tekstis['kieli_' + $scope.model.koodistoLocale];
                        } else {
                            //2 aste etc.
                            strName = ohjelma.meta['kieli_' + $scope.model.koodistoLocale].nimi;
                        }

                        return [{oid: $scope.model.koulutus.oid, koulutuskoodi: $scope.model.koulutus.koulutuskoodi.arvo, nimi: strName}];
                    },
                    targetOrganisaatio: function() {
                        return  {oid: $scope.model.koulutus.organisaatio.oid, nimi: $scope.model.koulutus.organisaatio.nimi}
                    }
                }
            });

            copyModalDialog.result.then(function() {
                /* ok */
            }, function() {
                /* dismissed */
            })


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
            //example : https://<oppija-env>/app/preview.html#!/korkeakoulu/1.2.246.562.5.2014021318092550673640?lang=fi

            if ($scope.model.koulutus.toteutustyyppi === 'KORKEAKOULUTUS') {
                $window.location.href = window.CONFIG.env['web.url.oppija.preview'] + $scope.model.koulutus.oid + "?lang=" + $scope.model.koodistoLocale;
            } else if ($scope.model.koulutus.toteutustyyppi === 'LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA') {
                //TODO: fix the 'korkeakoulu' in the env config.
                $window.location.href = window.CONFIG.env['web.url.oppija.preview'].replace("korkeakoulu", "aikuislukio") + $scope.model.koulutus.oid + "?lang=" + $scope.model.koodistoLocale;
            }
        };

        $scope.findHakukohdeNimi = function(lang, hakukohde) {

            var hakukohdeNimi;
            var fallbackLang = "fi";

            //Try to get hakukohde nimi with tab language
            for (var language in hakukohde.nimi) {
                if (lang.locale.toUpperCase() === language.toUpperCase()) {
                    hakukohdeNimi = hakukohde.nimi[language];
                }

            }
            //If not found then try to find in Finnish
            if (hakukohdeNimi === undefined) {
                hakukohdeNimi = hakukohde.nimi[fallbackLang];

                //If even that is not found, just get some name
                if (hakukohdeNimi === undefined || hakukohdeNimi.trim().length < 1) {

                    for (var fooLang in hakukohde.nimi) {
                        hakukohdeNimi = hakukohde.nimi[fooLang];
                    }

                }
            }

            return hakukohdeNimi;
        }

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

        /**
         * Kaikille koulutustyypeille ei ole asetettu opintojen laajutta koodistossa,
         * missä tapauksessa näytetään virkailijan tallentama paikallinen arvo (tarjonnan kannasta).
         */
        if ( ! $scope.model.koulutus.opintojenLaajuusarvo.meta
            && $scope.model.koulutus.opintojenLaajuusarvoKannassa ) {

            $scope.model.koulutus.opintojenLaajuusarvo.meta = {};

            angular.forEach($scope.model.languages, function(lang) {
                $scope.model.koulutus.opintojenLaajuusarvo.meta[lang.koodi_uri] = {
                    nimi: $scope.model.koulutus.opintojenLaajuusarvoKannassa
                };
            });
        }

        function removeVersion(uri)  {
            if (uri.indexOf('#') != -1) {
                uri = uri.substring(0, uri.indexOf('#'));
            }
            return uri;
        }

        // Ylikirjoita koodistosta tuleva koulutusohjelman nimi paikallisella arvolla (jos olemassa)
        if ( $scope.model.koulutus.koulutusohjelmanNimiKannassa ) {
            try {
                angular.forEach($scope.model.koulutus.koulutusohjelmanNimiKannassa, function (value, key) {
                    $scope.model.koulutus.koulutusohjelma.meta[removeVersion(key)].nimi = value;
                });
            }
            catch (err) {
                console.error("Koulutusohjelman nimen ylikirjoitus fail", err);
            }
        }

        $scope.treeClickHandler = function(obj, event) {
//            TarjontaService.haeKoulutukset({//search parameter object
//                komoOid: obj.oid
//            }).then(function(result) {
//                $location.path("/koulutus/" + result.tulokset[0].tulokset[0].oid);
//                $route.reload();
//            });
        };

        $scope.canRemoveHakukohdeFromKoulutus = function(hakukohde) {




            return TarjontaService.parameterCanRemoveHakukohdeFromHaku(hakukohde.relatedOid);



        };

        $scope.getKoulutusohjelmaNimi = function() {
            // Get user's language and update scope with it
            var userLangUri = "kieli_" + AuthService.getLanguage();
            $scope.model.userLangUri = userLangUri;

            var result = $scope.model.koulutus.koulutuskoodi.meta[userLangUri].nimi;

            if (koulutusModel.toteutustyyppi === 'KORKEAKOULUTUS') {
                if (!angular.isDefined($scope.model.koulutus.koulutusohjelma.tekstis[userLangUri])) {
                    // Just take first value for language
                    userLangUri = Object.keys($scope.model.koulutus.koulutusohjelma.tekstis)[0];
                }
                result = $scope.model.koulutus.koulutusohjelma.tekstis[userLangUri];
            } else if (
                $.inArray(koulutusModel.toteutustyyppi, [
                    'AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA',
                    'AMMATTITUTKINTO',
                    'ERIKOISAMMATTITUTKINTO',
                    'AMMATILLINEN_PERUSTUTKINTO'
                ])
            ) {
                if (angular.isDefined($scope.model.koulutus.koulutusohjelma) && angular.isDefined($scope.model.koulutus.koulutusohjelma.meta) && angular.isDefined(userLangUri)) {
                    if (!angular.isDefined($scope.model.koulutus.koulutusohjelma.meta[userLangUri])) {
                        // Just take first value for language
                        userLangUri = Object.keys($scope.model.koulutus.koulutusohjelma.meta)[0];
                    }
                    result = $scope.model.koulutus.koulutusohjelma.meta[userLangUri].nimi;
                }
            } else if (koulutusModel.toteutustyyppi === 'LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA') {
                if (!angular.isDefined($scope.model.koulutus.koulutusohjelma.meta[userLangUri])) {
                    // Just take first value for language
                    userLangUri = Object.keys($scope.model.koulutus.koulutusohjelma.meta)[0];
                }
                result = $scope.model.koulutus.koulutusohjelma.meta[userLangUri].nimi;
            }

            $scope.model.header.nimi = result;

            // $log.info("getKoulutusohjelmaNimi() lang, value", userLangUri, result);

            return result;
        };

        $scope.getKoulutuskoodiNimi = function() {
            // Get user's language and update scope with it
            var userLangUri = "kieli_" + AuthService.getLanguage();
            $scope.model.userLangUri = userLangUri;

            if (!angular.isDefined($scope.model.koulutus.koulutuskoodi.meta[userLangUri])) {
                result = $scope.model.koulutus.koulutuskoodi.meta.nimi;
            } else {
                result = $scope.model.koulutus.koulutuskoodi.meta[userLangUri].nimi;
            }

            // $log.info("getKoulutuskoodiNimi() lang, value", userLangUri, result);

            return result;
        };

        $scope.getRakenneKuvaSrc = function(kieliUri) {
            var img = $scope.model.koulutus.opintojenRakenneKuvas[kieliUri];
            if (!img) {
                return false;
            }
            return "data:" + img.mimeType + ";base64," + img.base64data;
        }

    }]);

