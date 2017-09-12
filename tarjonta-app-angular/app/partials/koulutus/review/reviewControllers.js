var app = angular.module('app.review.ctrl', ['app.review.directives']);
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

        if($scope.isRemovable){
            $scope.isRemovable = noneOfJarjestettyKoulutusIsJulkaistu(koulutusModel.jarjestettavatKoulutukset);
        }
    });


    var julkaistutTilat = ['JULKAISTU', 'VALMIS', 'LUONNOS', 'PERUTTU'];
    // Onko "Poista"-painike aktiivinen
    var noneOfJarjestettyKoulutusIsJulkaistu = function (jarjestettavatKoulutukset) {
        if (jarjestettavatKoulutukset && jarjestettavatKoulutukset.koulutukset) {
            for (var i = 0; i < jarjestettavatKoulutukset.koulutukset.length; i++) {
                var koulutus = jarjestettavatKoulutukset.koulutukset[i];
                if (koulutus && julkaistutTilat.indexOf(koulutus.tila) > -1) {
                    return false;
                }
            }
        }
        return true;
    };

    $scope.formControls = {};
    $scope.model = {
        header: {},
        //review.html otsikkon tiedot
        koodistoLocale: LocalisationService.getLocale(),
        routeParams: $routeParams,
        collapse: {
            perusTiedot: false,
            kuvailevatTiedot: false,
            sisaltyvatOpintokokonaisuudet: false,
            hakukohteet: false,
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
    var koulutusStructure = KoulutusConverterFactory.STRUCTURE[$scope.model.koulutus.toteutustyyppi];
    $scope.koulutusStructure = koulutusStructure;
    TarjontaService.getKoulutuksenHakukohteet($scope.model.koulutus.oid)
        .then(function(hakukohteet) {
            $scope.model.hakukohteet = hakukohteet;
        });
    var checkIsOkToRemoveHakukohde = function(hakukohde) {
        TarjontaService.getHakukohteenKoulutukset(hakukohde.oid)
            .then(function(koulutukset) {
                if (koulutukset.length > 1) {
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
                    dialogService.showDialog({
                        title: '',
                        description: '<p class="errors">' +
                                        LocalisationService.t('koulutus.review.hakukohde.remove.exp.msg') + '</p>',
                        cancel: false
                    });
                }
            });
    };
    var reallyRemoveHakukohdeFromKoulutus = function(hakukohde) {
        var koulutukset = [{
            oid: $scope.model.koulutus.oid,
            tarjoajaOid: $scope.model.koulutus.organisaatio.oid
        }];
        HakukohdeKoulutukses.removeKoulutuksesFromHakukohde(hakukohde.oid, koulutukset);
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
                    var koulutuskoodi;
                    if ($scope.model.koulutus.koulutuskoodi) {
                        koulutuskoodi = $scope.model.koulutus.koulutuskoodi.arvo;
                    }
                    return {
                        oid: $scope.model.koulutus.oid,
                        koulutuskoodi: koulutuskoodi,
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
            $route.reload();
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
                    return {
                        oid: $scope.model.koulutus.oid,
                        nimi: $scope.getKoulutusohjelmaNimi()
                    };
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
            $window.location.href = window.url("koulutusinformaatio-app-web.preview", "korkeakoulu", $scope.model.koulutus.oid, $scope.model.koodistoLocale);
        }
        else if ($scope.model.koulutus.toteutustyyppi === 'LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA') {
            $window.location.href = window.url("koulutusinformaatio-app-web.preview", "aikuislukio", $scope.model.koulutus.oid, $scope.model.koodistoLocale);
        }
        else if ($scope.model.koulutus.toteutustyyppi === 'AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA' ||
            $scope.model.koulutus.toteutustyyppi === 'ERIKOISAMMATTITUTKINTO' ||
            $scope.model.koulutus.toteutustyyppi === 'AMMATTITUTKINTO') {
            $window.location.href = window.url("koulutusinformaatio-app-web.preview", "ammatillinenaikuiskoulutus", $scope.model.koulutus.oid, $scope.model.koodistoLocale);
        }
        else if ($scope.model.koulutus.toteutustyyppi === 'AIKUISTEN_PERUSOPETUS') {
            $window.location.href = window.url("koulutusinformaatio-app-web.preview", "aikuistenperusopetus", $scope.model.koulutus.oid, $scope.model.koodistoLocale);
        }
        else {
            $window.location.href = window.url("koulutusinformaatio-app-web.preview", "koulutus", $scope.model.koulutus.oid, $scope.model.koodistoLocale);
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
    $scope.getDefaultLang = function() {
        var userLang = 'kieli_' + AuthService.getLanguage().toLowerCase();
        var opetuskielet = _.keys($scope.model.koulutus.opetuskielis.meta || {});
        return _.findWhere(opetuskielet, userLang) || opetuskielet[0] ||
                    userLang || window.CONFIG.app.userLanguages[0];
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
                'koodi_uri': val,
                active: val === $scope.getDefaultLang()
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
    // Ylikirjoita koodistosta tuleva koulutusohjelman nimi paikallisella arvolla (jos olemassa)
    if ($scope.model.koulutus.koulutusohjelmanNimiKannassa) {
        var nimiKannassa = {};
        _.each($scope.model.koulutus.koulutusohjelmanNimiKannassa, function(val, key) {
            nimiKannassa[oph.removeKoodiVersion(key)] = val;
        });
        try {
            _.each($scope.model.koulutus.koulutusohjelma.meta, function(val, lang) {
                $scope.model.koulutus.koulutusohjelma.meta[lang].nimi = nimiKannassa[lang] || _.values(nimiKannassa)[0];
            });
        } catch (err) {
            console.error('Koulutusohjelman nimen ylikirjoitus fail', err);
        }
    }
    $scope.treeClickHandler = function(obj, event) {};
    $scope.canRemoveHakukohdeFromKoulutus = function(hakukohde) {
        // Not yet possible to remove hakukohde if koulutus has multiple tarjoaja
        var isMultiTarjoaja = $scope.model.koulutus.organisaatiot && $scope.model.koulutus.organisaatiot.length > 1;
        return !isMultiTarjoaja && TarjontaService.parameterCanRemoveHakukohdeFromHaku(hakukohde.hakuOid);
    };
    $scope.getKoulutusohjelmaNimi = function() {
        if (!$scope.model.header.nimi) {
            var koulutusohjelma = $scope.model.koulutus.koulutusohjelma;
            var result;

            if (_.contains(['KORKEAKOULUOPINTO',
                            'KORKEAKOULUTUS'], koulutusModel.toteutustyyppi)) {
                result = koulutusohjelma.tekstis[$scope.getDefaultLang()] ||
                            _.chain(koulutusohjelma.tekstis).values().first().value();
            }
            else if ($.inArray(koulutusModel.toteutustyyppi, [
                    'AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA',
                    'AMMATTITUTKINTO',
                    'ERIKOISAMMATTITUTKINTO',
                    'AMMATILLINEN_PERUSTUTKINTO',
                    'LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA'
                ])) {
                if (koulutusohjelma && koulutusohjelma.meta) {
                    var koodi = koulutusohjelma.meta[$scope.getDefaultLang()] ||
                                    _.chain(koulutusohjelma.meta).values().first().value();
                    result = koodi.nimi;
                }
            }

            // Fallback
            if (!result) {
                result = $scope.getKoulutuskoodiNimi();
            }

            $scope.model.header.nimi = result;
        }
        return $scope.model.header.nimi;
    };
    $scope.showKoulutuskoodiTitle = function() {
        return $scope.getKoulutuskoodiNimi() !== $scope.getKoulutusohjelmaNimi();
    };
    $scope.getKoulutuskoodiNimi = function() {
        if (!$scope.model.header.koodi) {
            var meta = $scope.model.koulutus.koulutuskoodi.meta;
            var koodi = meta[$scope.getDefaultLang()] || _.chain(meta).values().first().value();
            $scope.model.header.koodi = koodi.nimi;
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
        $scope.disableCopy = true;
        TarjontaService.getKoulutus({
            oid: $scope.model.koulutus.tarjoajanKoulutus
        }).$promise.then(function(response) {
            $scope.model.tarjoajanKoulutus = response.result;
        });
    }
    if ($scope.model.koulutus.opinnonTyyppiUri) {
        Koodisto.getKoodi('opinnontyyppi', $scope.model.koulutus.opinnonTyyppiUri).then(function(koodi) {
            $scope.model.koulutus.opinnonTyyppiKoodiNimi = koodi.koodiNimi;
        });
    }
    $scope.getMonikielinenNimi = function(field) {
        return field.kieli_fi || field.kieli_sv || field.kieli_en;
    };
    $scope.getTranslatedName = function(obj) {
        var correctMeta = _.findWhere(obj.meta, {kieliUri: $scope.getDefaultLang()});
        return correctMeta ? correctMeta.nimi : obj.nimi;
    }
});