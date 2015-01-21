var app = angular.module('app.review.ctrl', []);
app.controller('BaseReviewController', function BaseReviewController(PermissionService, $q, $scope, $window,
     $location, $route, $log, TarjontaService, $routeParams, LocalisationService, dialogService, Koodisto,
     KoodistoURI, $modal, KoulutusConverterFactory, HakukohdeKoulutukses, SharedStateService, AuthService,
     KoulutusService, OrganisaatioService) {
    $log = $log.getInstance('BaseReviewController');
    var koulutusModel = $route.current.locals.koulutusModel.result;

    // Näytetäänkö "Järjestä"-painike
    $scope.isJarjestettavaKoulutus = koulutusModel.toteutustyyppi === 'KORKEAKOULUOPINTO'
                                        && koulutusModel.opetusJarjestajat.length > 0;

    if (angular.isUndefined(koulutusModel)) {
        $location.path('/error');
        return;
    }
    //käyttöoikeudet
    PermissionService.koulutus.canEdit(koulutusModel.oid, {
        defaultTarjoaja: AuthService.getUserDefaultOid()
    }).then(function(data) {
        var tila = TarjontaService.getTilat()[koulutusModel.tila];
        $scope.isMutable = tila.mutable && data;
        $scope.isRemovable = tila.removable && data;
    });
    $scope.formControls = {};
    $scope.model = {
        header: {},
        //review.html otsikkon tiedot
        koodistoLocale: LocalisationService.getLocale(),
        routeParams: $routeParams,
        collapse: {
            perusTiedot: false,
            kuvailevatTiedot: false,
            sisaltyvatOpintokokonaisuudet: true,
            hakukohteet: true,
            model: true
        },
        tutkintoohjelma: {},
        //        koulutusohjelmanimi: "",
        languages: [],
        koulutus: koulutusModel,
        // preloaded in route resolve, see
        selectedKomoOid: [koulutusModel.komoOid]
    };
    $scope.reviewFields = KoulutusConverterFactory.STRUCTURE[koulutusModel.toteutustyyppi].reviewFields;
    var ammattinimikkeet = {};
    if (koulutusModel.ammattinimikkeet) {
        ammattinimikkeet.tekstis = {};
        angular.forEach(koulutusModel.ammattinimikkeet.meta, function(ammattinimike) {
            angular.forEach(ammattinimike.meta, function(meta, langCode) {
                if (!ammattinimikkeet.tekstis[langCode]) {
                    ammattinimikkeet.tekstis[langCode] = '';
                }
                ammattinimikkeet.tekstis[langCode] += ', ' + meta.nimi;
            });
        });
        // Strip the first comma
        angular.forEach(ammattinimikkeet.tekstis, function(text, key) {
            ammattinimikkeet.tekstis[key] = text.substring(1);
        });
    }
    $scope.reviewTexts = angular.extend({}, koulutusModel.kuvausKomo, koulutusModel.kuvausKomoto, {
        KOULUTUKSEN_TAVOITTEET: {
            tekstis: koulutusModel.koulutuksenTavoitteet
        },
        AMMATTINIMIKKEET: ammattinimikkeet
    });
    $scope.model.showError = false;
    $scope.model.validationmsgs = [];
    $scope.model.userLangUri = 'kieli_' + AuthService.getLanguage();
    var koulutusStructure = KoulutusConverterFactory.STRUCTURE[$scope.model.koulutus.toteutustyyppi];
    $scope.koulutusStructure = koulutusStructure;
    var hakukohdePromise = HakukohdeKoulutukses.getKoulutusHakukohdes($scope.model.koulutus.oid);
    hakukohdePromise.then(function(hakukohteet) {
        $scope.model.hakukohteet = hakukohteet.result;
    });
    var checkIsOkToRemoveHakukohde = function(hakukohde) {
        var hakukohdeQueryPromise = HakukohdeKoulutukses.getHakukohdeKoulutukses(hakukohde.oid);
        hakukohdeQueryPromise.then(function(hakukohdeKoulutuksesResponse) {
            if (hakukohdeKoulutuksesResponse.result.length > 1) {
                var texts = {
                    title: LocalisationService.t('koulutus.review.perustiedot.remove.koulutus.title'),
                    description: LocalisationService.t('koulutus.review.perustiedot.remove.koulutus.desc'),
                    ok: LocalisationService.t('ok'),
                    cancel: LocalisationService.t('cancel')
                };
                var d = dialogService.showDialog(texts);
                d.result.then(function(data) {
                    if (data) {
                        reallyRemoveHakukohdeFromKoulutus(hakukohde);
                    }
                });
            }
            else {
                $scope.model.validationmsgs.push('koulutus.review.hakukohde.remove.exp.msg');
                $scope.model.showError = true;
            }
        });
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
    TarjontaService.getKoulutuksetPromise(koulutusModel.children).then(function(children) {
        $scope.children = children;
    });
    TarjontaService.getKoulutuksetPromise(koulutusModel.parents).then(function(parents) {
        $scope.parents = parents;
    });
    $scope.lisatiedot = KoulutusConverterFactory.STRUCTURE[koulutusModel.toteutustyyppi].KUVAUS_ORDER;
    // Valmistavan koulutuksen sisältäviä koulutuksia varten
    $scope.valmistavaLisatiedot =
        KoulutusConverterFactory.STRUCTURE.AMMATILLINEN_NAYTTOTUTKINTONA_VALMISTAVA.KUVAUS_ORDER;
    var kieliUri;
    if (koulutusModel.toteutustyyppi === 'KORKEAKOULUTUS') {
        for (kieliUri in $scope.model.koulutus.koulutusohjelma.tekstis) {
            if (kieliUri.indexOf(kieliUri) != -1) {
                $scope.model.userLangUri = kieliUri;
            }
        }
        $scope.model.header.nimi = $scope.model.koulutus.koulutusohjelma.tekstis[$scope.model.userLangUri];
    }
    else if (koulutusModel.toteutustyyppi === 'LUKIOKOULUTUS') {
        for (kieliUri in $scope.model.koulutus.koulutusohjelma.meta) {
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
        }
        else {
            kuvaus = $scope.model.koulutus.kuvausKomoto;
        }
        return kuvaus;
    };
    $scope.getValmistavaKoulutusKuvausApiModelLanguageUri = function() {
        var kuvaus = null;
        if ($scope.model.koulutus.valmistavaKoulutus) {
            kuvaus = $scope.model.koulutus.valmistavaKoulutus.kuvaus;
        }
        return kuvaus;
    };
    $scope.doEdit = function(event, targetPart) {
        if (!$scope.isMutable) {
            return;
        }
        $log.info('doEdit()...', event, targetPart);
        if (targetPart === 'SISALTYVATOPINTOKOKONAISUUDET_LIITA') {
            var toteutusTyyppi = $scope.model.koulutus.toteutustyyppi;
            var koulutusLaji = toteutusTyyppi == 'KORKEAKOULUOPINTO' ? 'OPINTO' : 'TUTKINTO';
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
                            toteutustyyppi: $scope.model.koulutus.toteutustyyppi,
                            koulutusLaji: koulutusLaji,
                            nimi: $scope.model.koulutus.koulutusohjelma.tekstis['kieli_' + $scope.model.koodistoLocale]
                        };
                    },
                    organisaatioOid: function() {
                        return {
                            oid: $scope.model.koulutus.organisaatio.oid,
                            nimi: $scope.model.koulutus.organisaatio.nimi
                        };
                    }
                }
            });
        }
        else if (targetPart === 'SISALTYVATOPINTOKOKONAISUUDET_POISTA') {
            $scope.luoKoulutusDialogOrg = $scope.selectedOrgOid;
            $scope.luoKoulutusDialog = $modal.open({
                templateUrl: 'partials/koulutus/sisaltyvyys/poista-koulutuksia.html',
                controller: 'PoistaSisaltyvyysCtrl',
                resolve: {
                    targetKomo: function() {
                        return {
                            oid: $scope.model.koulutus.komoOid,
                            toteutustyyppi: $scope.model.koulutus.toteutustyyppi,
                            koulutusLaji: koulutusLaji,
                            nimi: $scope.model.koulutus.koulutusohjelma.tekstis['kieli_' + $scope.model.koodistoLocale]
                        };
                    },
                    organisaatioOid: function() {
                        return {
                            oid: $scope.model.koulutus.organisaatio.oid,
                            nimi: $scope.model.koulutus.organisaatio.nimi
                        };
                    }
                }
            });
        }
        else {
            $location.path('/koulutus/' + $scope.model.koulutus.oid + '/edit');
        }
    };
    $scope.goBack = function(event) {
        $log.info('goBack()...');
        $location.path('/');
    };
    $scope.removeKoulutusFromHakukohde = function(hakukohde) {
        checkIsOkToRemoveHakukohde(hakukohde);
    };
    $scope.getModalDialog = function() {
        return $scope.poistaModalDialog;
    };
    $scope.doDelete = function(event) {
        var poistaModalDialog = $modal.open({
            templateUrl: 'partials/koulutus/remove/poista-koulutus.html',
            controller: 'PoistaKoulutusCtrl',
            resolve: {
                targetKomoto: function() {
                    return {
                        oid: $scope.model.koulutus.oid,
                        koulutuskoodi: $scope.model.koulutus.koulutuskoodi.arvo,
                        nimi: $scope.model.koulutus.koulutusohjelma.tekstis['kieli_' + $scope.model.koodistoLocale]
                    };
                },
                organisaatioOid: function() {
                    return {
                        oid: $scope.model.koulutus.organisaatio.oid,
                        nimi: $scope.model.koulutus.organisaatio.nimi
                    };
                }
            }
        });
        poistaModalDialog.result.then(function() {
            //not working:
            // $route.reload();
            //$location.path("/koulutus/" + $scope.model.koulutus.oid);
            // force page reload, at least it works:
            window.location.reload();
        }, function() {});
    };
    $scope.addHakukohde = function() {
        if (!$scope.isMutable) {
            return;
        }
        var koulutusOids = [];
        koulutusOids.push($scope.model.koulutus.oid);
        SharedStateService.addToState('SelectedKoulutukses', koulutusOids);
        SharedStateService.addToState('firstSelectedKoulutus', $scope.model.koulutus);
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
                    if (angular.isDefined(ohjelma.tekstis) && ohjelma.tekstis !== null &&
                        Object.keys(ohjelma.tekstis).length > 0) {
                        //korkeakoulu etc.
                        strName = ohjelma.tekstis['kieli_' + $scope.model.koodistoLocale];
                    }
                    else {
                        //2 aste etc.
                        strName = ohjelma.meta['kieli_' + $scope.model.koodistoLocale].nimi;
                    }
                    return [{
                        oid: $scope.model.koulutus.oid,
                        koulutuskoodi: $scope.model.koulutus.koulutuskoodi.arvo,
                        nimi: strName
                    }];
                },
                targetOrganisaatio: function() {
                    return {
                        oid: $scope.model.koulutus.organisaatio.oid,
                        nimi: $scope.model.koulutus.organisaatio.nimi
                    };
                }
            }
        });
        copyModalDialog.result.then(function() {}, function() {});
    };
    $scope.doCreateLinked = function(event) {
        // Aseta esivalittu organisaatio
        $scope.luoKoulutusDialogOrg = $scope.selectedOrgOid;
        // Aseta lähde koulutus
        $scope.model.sourceKoulutus = $scope.model.koulutus;
        // Avaa luonti
        KoulutusService.extendKorkeakouluOpinto($scope.model.koulutus, $scope.model.koodistoLocale);
    };
    $scope.doMoveToBeSubPart = function(event) {
        $log.info('doMoveToBeSubPart()...');
        dialogService.showNotImplementedDialog();
    };
    $scope.doAddParallel = function(event) {
        $log.info('doAddParallel()...');
        dialogService.showNotImplementedDialog();
    };
    $scope.doPreview = function(event) {
        //example : https://<oppija-env>/app/preview.html#!/korkeakoulu/1.2.246.562.5.2014021318092550673640?lang=fi
        if ($scope.model.koulutus.toteutustyyppi === 'KORKEAKOULUTUS') {
            $window.location.href = window.CONFIG.env['web.url.oppija.preview'] + $scope.model.koulutus.oid +
                '?lang=' + $scope.model.koodistoLocale;
        }
        else if ($scope.model.koulutus.toteutustyyppi === 'LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA') {
            //TODO: fix the 'korkeakoulu' in the env config.
            $window.location.href = window.CONFIG.env['web.url.oppija.preview'].replace('korkeakoulu', 'aikuislukio') +
                $scope.model.koulutus.oid + '?lang=' + $scope.model.koodistoLocale;
        }
        else if ($scope.model.koulutus.toteutustyyppi === 'AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA' ||
            $scope.model.koulutus.toteutustyyppi === 'ERIKOISAMMATTITUTKINTO' ||
            $scope.model.koulutus.toteutustyyppi === 'AMMATTITUTKINTO') {
            //TODO: fix the 'korkeakoulu' in the env config.
            $window.location.href = window.CONFIG.env['web.url.oppija.preview']
                .replace('korkeakoulu', 'ammatillinenaikuiskoulutus') + $scope.model.koulutus.oid +
                '?lang=' + $scope.model.koodistoLocale;
        }
    };
    $scope.findHakukohdeNimi = function(lang, hakukohde) {
        var locale = lang && lang.locale || 'FI';
        //Try to get hakukohde nimi with tab language
        var hakukohdeNimi = _.find(hakukohde.nimi, function(val, key) {
            return key.toUpperCase() === locale.toUpperCase();
        });
        //If no name found according to lang => just get some name
        if (!hakukohdeNimi) {
            hakukohdeNimi = _.find(hakukohde.nimi, function(nimi) {
                return $.trim(nimi) !== '';
            });
        }
        return hakukohdeNimi;
    };
    $scope.searchKoodi = function(obj, koodistouri, uri, locale) {
        var promise = Koodisto.getKoodi(koodistouri, uri, locale);
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
            var lang = {
                'koodi_uri': val
            };
            $scope.searchKoodi(lang, window.CONFIG.env['koodisto-uris.kieli'], key, $scope.model.koodistoLocale);
            $scope.model.languages.push(lang);
        });
        if (koulutusModel.toteutustyyppi === 'KORKEAKOULUTUS') {
            // jos kyseessä korkeakoulun koulutus
            var userLangUri = 'kieli_' + AuthService.getLanguage();
            var opetusKieliUri;
            if (Object.keys($scope.model.koulutus.opetuskielis.uris).length == 1) {
                opetusKieliUri = Object.keys($scope.model.koulutus.opetuskielis.uris)[0];
            }
            // jos koulutuksella on tasan yksi opetuskieli, jolla löytyy koulutusohjelman nimi
            if (opetusKieliUri && $scope.model.koulutus.koulutusohjelma.tekstis[opetusKieliUri]) {
                $scope.model.opetusKieliUri = opetusKieliUri; // tallennetaan opetuskieli scopeen
            }
            // sijoitetaan scopen tutkintoohjelmaolioon kieli_xy-attribuutit ja niiden arvoiksi ui:ssa näytettävät koulutusohjelman nimet
            for (var i = 0; i < $scope.model.languages.length; i++) {
                var lang = $scope.model.languages[i];
                // jos mallista löytyy koulutusohjelman nimi loopin kohdalla olevalla kielellä (ensisijainen vaihtoehto)
                if ($scope.model.koulutus.koulutusohjelma.tekstis[lang.koodi_uri]) {
                    $scope.model.tutkintoohjelma[lang.koodi_uri] =
                        $scope.model.koulutus.koulutusohjelma.tekstis[lang.koodi_uri]; // jos mallista löytyy nimi käyttäjän omalla kielellä (2. vaihtoehto)
                }
                else if ($scope.model.koulutus.koulutusohjelma.tekstis[userLangUri]) {
                    $scope.model.tutkintoohjelma[lang.koodi_uri] =
                        $scope.model.koulutus.koulutusohjelma.tekstis[userLangUri]; // jos mallista löytyy nimi koulutuksen opetuskielellä (3. vaihtoehto)
                }
                else if (opetusKieliUri) {
                    $scope.model.tutkintoohjelma[lang.koodi_uri] =
                        $scope.model.koulutus.koulutusohjelma.tekstis[opetusKieliUri]; // muutoin haetaan ensimmäinen olemassa oleva nimi (viimeinen vaihtoehto)
                }
                else {
                    for (var j = 0; j < Object.keys($scope.model.koulutus.koulutusohjelma.tekstis).length; j++) {
                        var key = Object.keys($scope.model.koulutus.koulutusohjelma.tekstis)[j];
                        if ($scope.model.koulutus.koulutusohjelma.tekstis[key]) {
                            $scope.model.tutkintoohjelma[lang.koodi_uri] =
                                $scope.model.koulutus.koulutusohjelma.tekstis[key];
                            break;
                        }
                    }
                }
            } // for
        } // if ('KORKEAKOULUTUS')
    //        $scope.model.koulutusohjelmanimi = $scope.getKoulutusohjelmaNimi();
    }
    else {
        console.error('No koulutus found?');
    }
    /**
       * Kaikille koulutustyypeille ei ole asetettu opintojen laajutta koodistossa,
       * missä tapauksessa näytetään virkailijan tallentama paikallinen arvo (tarjonnan kannasta).
       */
    if (!$scope.model.koulutus.opintojenLaajuusarvo.meta && $scope.model.koulutus.opintojenLaajuusarvoKannassa) {
        $scope.model.koulutus.opintojenLaajuusarvo.meta = {};
        angular.forEach($scope.model.languages, function(lang) {
            $scope.model.koulutus.opintojenLaajuusarvo.meta[lang.koodi_uri] = {
                nimi: $scope.model.koulutus.opintojenLaajuusarvoKannassa
            };
        });
    }
    function removeVersion(uri) {
        if (uri.indexOf('#') != -1) {
            uri = uri.substring(0, uri.indexOf('#'));
        }
        return uri;
    }
    // Ylikirjoita koodistosta tuleva koulutusohjelman nimi paikallisella arvolla (jos olemassa)
    if ($scope.model.koulutus.koulutusohjelmanNimiKannassa) {
        try {
            angular.forEach($scope.model.koulutus.koulutusohjelmanNimiKannassa, function(value, key) {
                $scope.model.koulutus.koulutusohjelma.meta[removeVersion(key)].nimi = value;
            });
        } catch (err) {
            console.error('Koulutusohjelman nimen ylikirjoitus fail', err);
        }
    }
    $scope.treeClickHandler = function(obj, event) {};
    $scope.canRemoveHakukohdeFromKoulutus = function(hakukohde) {
        return TarjontaService.parameterCanRemoveHakukohdeFromHaku(hakukohde.relatedOid);
    };
    $scope.getKoulutusohjelmaNimi = function() {
        if (!$scope.model.header.nimi) {
            // Get user's language and update scope with it
            var userLangUri = 'kieli_' + AuthService.getLanguage();
            $scope.model.userLangUri = userLangUri;
            var result;
            if (koulutusModel.toteutustyyppi === 'KORKEAKOULUOPINTO') {
                //result = "[Opintojakson tai kokonaisuuden nimi...]";   // TODO
                if (!angular.isDefined($scope.model.koulutus.koulutusohjelma.tekstis[userLangUri])) {
                    // Just take first value for language
                    userLangUri = Object.keys($scope.model.koulutus.koulutusohjelma.tekstis)[0];
                }
                result = $scope.model.koulutus.koulutusohjelma.tekstis[userLangUri];
            }
            else if (koulutusModel.toteutustyyppi === 'KORKEAKOULUTUS') {
                if ($scope.model.opetusKieliUri) {
                    result = $scope.model.koulutus.koulutusohjelma.tekstis[$scope.model.opetusKieliUri];
                }
                else {
                    if (!$scope.model.koulutus.koulutusohjelma.tekstis[userLangUri]) {
                        // Just take first value for language
                        userLangUri = Object.keys($scope.model.koulutus.koulutusohjelma.tekstis)[0];
                    }
                    result = $scope.model.koulutus.koulutusohjelma.tekstis[userLangUri];
                }
            }
            else if ($.inArray(koulutusModel.toteutustyyppi, [
                    'AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA',
                    'AMMATTITUTKINTO',
                    'ERIKOISAMMATTITUTKINTO',
                    'AMMATILLINEN_PERUSTUTKINTO'
                ])) {
                if (angular.isDefined($scope.model.koulutus.koulutusohjelma) &&
                    angular.isDefined($scope.model.koulutus.koulutusohjelma.meta) && angular.isDefined(userLangUri)) {
                    if (!angular.isDefined($scope.model.koulutus.koulutusohjelma.meta[userLangUri])) {
                        // Just take first value for language
                        userLangUri = Object.keys($scope.model.koulutus.koulutusohjelma.meta)[0];
                    }
                    result = $scope.model.koulutus.koulutusohjelma.meta[userLangUri].nimi;
                }
            }
            else if (koulutusModel.toteutustyyppi === 'LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA') {
                if (!angular.isDefined($scope.model.koulutus.koulutusohjelma.meta[userLangUri])) {
                    // Just take first value for language
                    userLangUri = Object.keys($scope.model.koulutus.koulutusohjelma.meta)[0];
                }
                result = $scope.model.koulutus.koulutusohjelma.meta[userLangUri].nimi;
            }
            else {
                result = $scope.model.koulutus.koulutuskoodi.meta[userLangUri].nimi;
            }
            $scope.model.header.nimi = result;
        }
        return $scope.model.header.nimi;
    };
    $scope.getKoulutuskoodiNimi = function() {
        if (!$scope.model.header.koodi) {
            // Get user's language and update scope with it
            var userLangUri = 'kieli_' + AuthService.getLanguage();
            $scope.model.userLangUri = userLangUri;
            if (!angular.isDefined($scope.model.koulutus.koulutuskoodi.meta[userLangUri])) {
                $scope.model.header.koodi = $scope.model.koulutus.koulutuskoodi.meta.nimi;
            }
            else {
                $scope.model.header.koodi = $scope.model.koulutus.koulutuskoodi.meta[userLangUri].nimi;
            } // $log.info("getKoulutuskoodiNimi() lang, value", userLangUri, result);
        }
        return $scope.model.header.koodi;
    };
    $scope.getRakenneKuvaSrc = function(kieliUri) {
        var img = $scope.model.koulutus.opintojenRakenneKuvas[kieliUri];
        if (!img) {
            return false;
        }
        return 'data:' + img.mimeType + ';base64,' + img.base64data;
    };
    if ($scope.model.koulutus.tarjoajanKoulutus) {
        TarjontaService.getKoulutus({
            oid: $scope.model.koulutus.tarjoajanKoulutus
        }).$promise.then(function(response) {
            $scope.model.tarjoajanKoulutus = response.result;
        });
    }
    $scope.getMonikielinenNimi = function(field) {
        return field.kieli_fi || field.kieli_sv || field.kieli_en;
    };
});