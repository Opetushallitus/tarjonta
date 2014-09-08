/**
 * Created by Tuomas on 27.5.2014.
 */
var app = angular.module('app.hakukohde.ctrl');

app.controller('HakukohdeParentController', [
    '$scope',
    '$log',
    '$routeParams',
    '$route',
    '$q',
    '$modal',
    '$location',
    'Hakukohde',
    'Koodisto',
    'AuthService',
    'HakuService',
    'LocalisationService',
    'OrganisaatioService',
    'SharedStateService',
    'TarjontaService',
    'Kuvaus',
    'CommonUtilService',
    'PermissionService',
    'dialogService',
    'HakukohdeService',
    function($scope, $log, $routeParams, $route, $q, $modal, $location, Hakukohde, Koodisto, AuthService, HakuService, LocalisationService,
             OrganisaatioService, SharedStateService, TarjontaService, Kuvaus, CommonUtilService, PermissionService, dialogService, HakukohdeService) {

        var korkeakoulutusHakukohdePartialUri = "partials/hakukohde/edit/korkeakoulu/editKorkeakoulu.html";
        var aikuLukioHakukohdePartialUri = "partials/hakukohde/edit/aiku/lukio/editAiku.html";
        var aikuNayttoHakukohdePartialUri = "partials/hakukohde/edit/aiku/naytto/editAmmatillinenNaytto.html";
        var toinenAsteHakukohdePartialUri = "partials/hakukohde/edit/TOINEN_ASTE.html";

        var routing = {
            "KORKEAKOULUTUS": korkeakoulutusHakukohdePartialUri,
            "LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA": aikuLukioHakukohdePartialUri,
            "AMMATILLINEN_PERUSKOULUTUS": aikuNayttoHakukohdePartialUri,
            "AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA": aikuNayttoHakukohdePartialUri,
            "ERIKOISAMMATTITUTKINTO": aikuNayttoHakukohdePartialUri,
            "AMMATTITUTKINTO": aikuNayttoHakukohdePartialUri,
            "AMMATILLINEN_PERUSTUTKINTO": toinenAsteHakukohdePartialUri,
            "LUKIOKOULUTUS": toinenAsteHakukohdePartialUri,
            "MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS": toinenAsteHakukohdePartialUri,
            "MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS": toinenAsteHakukohdePartialUri,
            "AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS": toinenAsteHakukohdePartialUri,
            "PERUSOPETUKSEN_LISAOPETUS": toinenAsteHakukohdePartialUri,
            "VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS": toinenAsteHakukohdePartialUri,
            "VAPAAN_SIVISTYSTYON_KOULUTUS": toinenAsteHakukohdePartialUri,
            "AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA": toinenAsteHakukohdePartialUri
        };


        $scope.toisenAsteenKoulutus = function(toteutusTyyppi) {
            return toteutusTyyppi === 'AMMATILLINEN_PERUSTUTKINTO' ||
                toteutusTyyppi === 'LUKIOKOULUTUS' ||
                toteutusTyyppi === 'PERUSOPETUKSEN_LISAOPETUS' ||
                toteutusTyyppi === 'AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS' ||
                toteutusTyyppi === 'MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS' ||
                toteutusTyyppi === 'MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS' ||
                toteutusTyyppi === 'VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS' ||
                toteutusTyyppi === 'VAPAAN_SIVISTYSTYON_KOULUTUS' ||
                toteutusTyyppi === 'AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA';
        }

        $scope.needsHakukelpoisuus = function(toteutusTyyppi) {
            return toteutusTyyppi !== 'PERUSOPETUKSEN_LISAOPETUS' &&
                toteutusTyyppi !== 'AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS' &&
                toteutusTyyppi !== 'MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS' &&
                toteutusTyyppi !== 'MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS' &&
                toteutusTyyppi !== 'VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS' &&
                toteutusTyyppi !== 'VAPAAN_SIVISTYSTYON_KOULUTUS' &&
                toteutusTyyppi !== 'AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA';
        }

        /**
         * Tila asetetetaan jos vanhaa tilaa ei ole tai se on luonnos/peruttu/kopioitu
         */
        function updateTila(tila) {
            var tilat = ["LUONNOS", "PERUTTU", "KOPIOITU"];
            if ($scope.model.hakukohde.tila===undefined||tilat.indexOf($scope.model.hakukohde.tila)!==-1) {
              console.log("asetetaan tila modeliin!", tila);
              // päivitä tila modeliin jos se voi muuttua
              $scope.model.hakukohde.tila = tila;
            }
        }

        $scope.modifiedObj = {
            modifiedBy: '',
            modified: 0,
            tila: ''

        };

        $scope.status = {// ÄLÄ LAITA MODELIIN (pitää näkyä
            // alikontrollereille)
            dirty: false,
            dirtify: function() {
                console.log("DIRTIFY hakukohde");
                $scope.status.dirty = true;
            },
            // alikontrollerit ylikirjoittavat nämä
            validateLiitteet: function() {
                return true;
            },
            validateValintakokeet: function() {
                return true;
            }
        };

        $scope.showCommonUnknownErrorMsg = function() {

            var errors = [];

            var error = {};

            error.errorMessageKey = "tuntematon virhe.";

            errors.push(error);

            $scope.showError(errors);

        };

        $scope.model.showSuccess = false;
        $scope.model.showError = false;
        $scope.model.validationmsgs = [];
        $scope.model.hakukohdeTabsDisabled = false;
        $scope.model.koulutusnimet = [];
        $scope.model.continueToReviewEnabled = false;
        $scope.model.organisaatioNimet = [];
        $scope.model.hakukohdeOppilaitosTyyppis = [];
        $scope.model.nimiValidationFailed = false;
        $scope.model.painotettavatOppiaineetValidationFailed= false;
        $scope.model.hakukelpoisuusValidationErrMsg = false;
        $scope.model.tallennaValmiinaEnabled = true;
        $scope.model.tallennaLuonnoksenaEnabled = true;
        $scope.model.liitteidenToimitusOsoite = {};
        var deferredOsoite = $q.defer();
        $scope.model.liitteenToimitusOsoitePromise = deferredOsoite.promise;
        $scope.model.liitteidenToimitusPvm = new Date();
        $scope.userLangs = window.CONFIG.app.userLanguages; // liitteiden
        // ja
        // valintakokeiden
        // kielien
        // esijärjestystä
        // varten
        $scope.model.defaultLang = 'kieli_fi';
        $scope.model.showHakuaikas = false;
        $scope.model.collapse.model = true;
        $scope.model.hakus = [];
        $scope.model.hakuaikas = [];

        $scope.model.isDeEnabled = false;
        $scope.model.isPartiallyDeEnabled = false;

        var parentOrgOids = new buckets.Set();
        var orgSet = new buckets.Set();

        // All kieles is received from koodistomultiselect
        $scope.model.allkieles = [];
        $scope.model.selectedKieliUris = [];
        $scope.model.koulutusVuosi;
        $scope.model.integerval = /^\d*$/;

        $scope.koulutusKausiUri;

        $scope.julkaistuVal = "JULKAISTU";

        $scope.luonnosVal = "LUONNOS";

        $scope.valmisVal = "VALMIS";

        $scope.peruttuVal = "PERUTTU";

        $scope.showSuccess = function() {
            $scope.model.showSuccess = true;
            $scope.model.showError = false;
            $scope.model.validationmsgs = [];
            $scope.model.hakukohdeTabsDisabled = false;
        };

        $scope.showError = function(errorArray) {

            $scope.model.validationmsgs.splice(0, $scope.model.validationmsgs.length);

            angular.forEach(errorArray, function(error) {
                if(error.errorMessageKey){
                    $scope.model.validationmsgs.push(error.errorMessageKey);
                } else {
                    $scope.model.validationmsgs.push(angular.toJson(error));
                }
            });
            $scope.model.showError = true;
            $scope.model.showSuccess = false;
        };

        $scope.getHakukohdePartialUri = function() {

            // If hakukohdex is defined then we are updating it
            // otherwise try to get selected koulutustyyppi from shared
            // state

            if ($route.current.locals && $route.current.locals.hakukohdex.result && $route.current.locals.hakukohdex.result.toteutusTyyppi) {
                var toteutusTyyppi = $route.current.locals.hakukohdex.result.toteutusTyyppi;
                if (routing[toteutusTyyppi]) {
                    return routing[toteutusTyyppi];
                }
            } else {
                var toteutusTyyppi = SharedStateService.getFromState('SelectedToteutusTyyppi');
                // $scope.model.hakukohde.toteutusTyyppi=toteutusTyyppi;
                if (routing[toteutusTyyppi]) {
                    return routing[toteutusTyyppi];
                }
                $log.error('TOTEUTUSTYYPPI WAS: ', toteutusTyyppi, " not returning template!!");
            }
        };

        $scope.validateNameLengths = function(hakukohteenNimet) {

            var retval = true;

            angular.forEach(hakukohteenNimet, function(hakukohdeNimi) {

                if (hakukohdeNimi.length > 225) {
                    retval = false;
                }

            });

            return retval;

        };

        $scope.checkJatkaBtn = function(hakukohde) {

            if (hakukohde === undefined || hakukohde.oid === undefined) {
                $log.debug('HAKUKOHDE OR HAKUKOHDE OID UNDEFINED');

                return false;
            } else {
                return true;
            }

        };

        $scope.canSaveParam = function(hakuOid) {

            if (hakuOid) {
                $log.info('CAN EDIT : ', hakuOid);
                var canEdit = TarjontaService.parameterCanEditHakukohdeLimited(hakuOid);
                $log.info('CAN EDIT : ', canEdit);
                $scope.model.isDeEnabled = !canEdit;
            }

            $log.info('IS DEENABLED : ', $scope.model.isDeEnabled);
        };

        $scope.emptyErrorMessages = function() {

            $scope.model.validationmsgs.splice(0, $scope.model.validationmsgs.length);

            $scope.model.showError = false;

        };

        $scope.checkCanCreateOrEditHakukohde = function(hakukohde) {

            if (hakukohde.oid !== undefined) {

                if ($scope.canEdit !== undefined) {
                    return $scope.canEdit;
                } else {
                    return true;
                }

            } else {

                if ($scope.canCreate !== undefined) {

                    return $scope.canCreate;

                } else {

                    return true;

                }
            }
        };

        $scope.checkIfSavingCopy = function(hakukohde) {

            if ($scope.model.isCopy) {

                if (hakukohde.oid !== undefined) {

                    $scope.model.isCopy = false;

                    $location.path('/hakukohde/' + hakukohde.oid + '/edit');
                }

            }

        };



        $scope.checkIsCopy = function(tilaParam) {

            // If scope or route has isCopy parameter defined as true remove
            // oid,
            // so that new hakukohde will be created

            if ($route.current.locals && $route.current.locals.isCopy) {
                $log.debug('HAKUKOHDE IS COPY, SETTING OID UNDEFINED');
                $scope.model.hakukohde.oid = undefined;
                $scope.model.hakukohde.tila = tilaParam;

            }

            $log.debug('IS COPY : ', $scope.isCopy);
            if ($scope.isCopy !== undefined && $scope.isCopy) {
                $scope.model.hakukohde.oid = undefined;
                $scope.model.hakukohde.tila = tilaParam;

            }

            $scope.model.isCopy = true;

        };

        $scope.createFormattedDateString = function(date) {

            return moment(date).format('DD.MM.YYYY HH:mm');

        };

        $scope.haeTarjoajaOppilaitosTyypit = function() {

            OrganisaatioService.etsi({
                oidRestrictionList: $scope.model.hakukohde.tarjoajaOids
            }).then(function(data) {

                getOppilaitosTyyppis(data.organisaatiot);

            });

        };

        $scope.validateHakukohde = function(toteutusTyyppi) {

            $scope.model.painotettavatOppiaineetValidationFailed = false;

            if (!$scope.model.canSaveHakukohde()) {
                return false;
            }

            var errors = [];

            if($scope.needsHakukelpoisuus(toteutusTyyppi)) {
                if ($scope.model.hakukohde.hakukelpoisuusvaatimusUris === undefined || $scope.model.hakukohde.hakukelpoisuusvaatimusUris.length < 1) {

                    var error = {};
                    error.errorMessageKey = 'tarjonta.hakukohde.hakukelpoisuusvaatimus.missing';
                    $scope.model.hakukelpoisuusValidationErrMsg = true;
                    errors.push(error);

                }
            }

            if(!$scope.toisenAsteenKoulutus(toteutusTyyppi)) {
                if (!validateNames()) {

                    var err = {};
                    err.errorMessageKey = 'hakukohde.edit.nimi.missing';
                    $scope.model.nimiValidationFailed = true;
                    errors.push(err);

                }

                if (!$scope.validateNameLengths($scope.model.hakukohde.hakukohteenNimet)) {

                    var err = {};
                    err.errorMessageKey = 'hakukohde.edit.nimi.too.long';

                    errors.push(err);
                }
            }

            if(toteutusTyyppi === 'LUKIOKOULUTUS') {
                if(!validPainotettavatOppiaineet()) {
                    $scope.model.painotettavatOppiaineetValidationFailed = true;
                    errors.push({
                        errorMessageKey: "tarjonta.hakukohde.edit.painotettavatOppiaineet.errors"
                    });
                }
            }

            if (!$scope.status.validateValintakokeet()) {
                errors.push({
                    errorMessageKey: "hakukohde.edit.valintakokeet.errors"
                });
            }

            if (!$scope.status.validateLiitteet()) {
                errors.push({
                    errorMessageKey: "hakukohde.edit.liitteet.errors"
                });
            }
            if (errors.length < 1) {
                return true;
            } else {
                $scope.showError(errors);
                return false;
            }

        };

        /*
         *
         * ------> Load hakukohde koulutusnames
         *
         */

        $scope.loadKoulutukses = function(hakuFilterFunction) {

            var koulutusSet = new buckets.Set();

            var spec = {
                koulutusOid: $scope.model.hakukohde.hakukohdeKoulutusOids
            };

            TarjontaService.haeKoulutukset(spec).then(function(data) {
                var tarjoajaOidsSet = new buckets.Set();

                if (data !== undefined) {
                    angular.forEach(data.tulokset, function (tulos) {
                        if (tulos !== undefined && tulos.tulokset !== undefined) {
                            tarjoajaOidsSet.add(tulos.oid);
                            angular.forEach(tulos.tulokset, function (toinenTulos) {
                                $scope.koulutusKausiUri = toinenTulos.kausiUri;
                                $scope.model.koulutusVuosi = toinenTulos.vuosi;
                                koulutusSet.add(toinenTulos.nimi);
                            });
                        }
                    });
                    $scope.model.koulutusnimet = koulutusSet.toArray();
                    $scope.model.hakukohde.tarjoajaOids = tarjoajaOidsSet.toArray();
                    $scope.getTarjoajaParentPathsAndHakus($scope.model.hakukohde.tarjoajaOids, hakuFilterFunction);
                    var orgQueryPromises = [];

                    angular.forEach($scope.model.hakukohde.tarjoajaOids, function (tarjoajaOid) {
                        orgQueryPromises.push(OrganisaatioService.byOid(tarjoajaOid));
                    });

                    $q.all(orgQueryPromises).then(function (orgs) {

                        var counter = 0;
                        angular.forEach(orgs, function (data) {

                            orgSet.add(data.nimi);

                            if (counter === 0) {
                                var wasHakutoimistoFound = checkAndAddHakutoimisto(data);
                                if (wasHakutoimistoFound) {

                                    deferredOsoite.resolve($scope.model.liitteidenToimitusOsoite);
                                } else {
                                    $scope.tryGetParentsApplicationOffice(data);
                                }
                            }

                            counter++;

                        });
                        $scope.model.organisaatioNimet = orgSet.toArray();

                        $log.debug('ORGANISAATIO NIMET : ', $scope.model.organisaatioNimet);
                    });
                }
            });
        };

        $scope.getKoulutustenNimet = function() {
            var ret = "";
            var ja = LocalisationService.t("tarjonta.yleiset.ja");

            for (var i in $scope.model.koulutusnimet) {
                if (i > 0) {
                    ret = ret + ((i == $scope.model.koulutusnimet.length - 1) ? " " + ja + " " : ", ");
                }
                ret = ret + "<b>" + $scope.model.koulutusnimet[i] + "</b>";
            }

            if ($scope.model.organisaatioNimet.length < 2 && $scope.model.organisaatioNimet.length > 0) {

                var organisaatiolleMsg = LocalisationService.t("tarjonta.hakukohde.title.org");

                ret = ret + ". " + organisaatiolleMsg + " : <b>" + $scope.model.organisaatioNimet[0] + " </b>";

            } else {
                var counter = 0;
                var organisaatioilleMsg = LocalisationService.t("tarjonta.hakukohde.title.orgs");
                angular.forEach($scope.model.organisaatioNimet, function(organisaatioNimi) {

                    if (counter === 0) {

                        ret = ret + ". " + organisaatioilleMsg + " : <b>" + organisaatioNimi + " </b>";

                    } else {

                        // ret = ret +
                        // ((counter===$scope.model.organisaatioNimet.length-1) ?
                        // " " : ", ");

                        ret = ret + ", <b>" + organisaatioNimi + "</b>";

                    }
                    counter++;

                });

            }

            return ret;
        };

        $scope.getKoulutustenNimetKey = function() {
            return $scope.model.koulutusnimet.length == 1 ? 'hakukohde.edit.header.single' : 'hakukohde.edit.header.multi';
        };

        $scope.checkIfFirstArraysOneElementExistsInSecond = function(array1, array2) {

            angular.forEach(array1, function(element) {
                if (array2.indexOf(element) != -1) {
                    return true;
                }
            });

        };

        var checkForOph = function(hakuOrgs) {

            angular.forEach(hakuOrgs, function(hakuOrg) {

                if (hakuOrg === window.CONFIG.app['haku.hakutapa.erillishaku.uri']) {
                    return true;
                }

            });

        }

        $scope.retrieveHakus = function(filterHakuFunction) {

            var hakuPromise = HakuService.getAllHakus();

            hakuPromise.then(function(hakuDatas) {
                $scope.model.hakus = [];

                angular.forEach(hakuDatas, function(haku) {

                    var userLang = AuthService.getLanguage();

                    var hakuLang = userLang !== undefined ? userLang : $scope.model.defaultLang;

                    for (var kieliUri in haku.nimi) {

                        if (kieliUri.indexOf(hakuLang) != -1) {
                            haku.lokalisoituNimi = haku.nimi[kieliUri];
                        }

                    }

                });

                var selectedHaku;

                // Get selected haku if one is defined that must be shown
                // even if the filtering does not show it
                if ($scope.model.hakukohde.hakuOid) {
                    selectedHaku = _.find(hakuDatas, function(m) {
                        return m.oid === $scope.model.hakukohde.hakuOid;
                    });
                }

                var filteredHakus = filterHakuWithParams(filterHakuFunction(hakuDatas));
                $log.info('HAKUS FILTERED WITH GIVEN FUNCTION : ', filteredHakus);

                if (selectedHaku) {
                    var inFilteres = _.find(filteredHakus, function(m) {
                        return m.oid === selectedHaku.oid;
                    });
                    if (!inFilteres) {
                        filteredHakus.push(inFilteres);
                    }
                }

                $scope.model.hakus = filteredHakus;

                if ($scope.model.hakukohde.hakuOid !== undefined && $scope.model.hakuChanged) {
                    $scope.model.hakuChanged();
                }
            });
        };

        var filterHakuWithParams = function(hakus) {

            var paramFilteredHakus = [];
            angular.forEach(hakus, function(haku) {

                if (TarjontaService.parameterCanAddHakukohdeToHaku(haku.oid)) {
                    paramFilteredHakus.push(haku);
                }
            });

            return paramFilteredHakus;

        };

        var checkIfOrgMatches = function(hakuOrganisaatioOids) {

            var doesMatch = false;

            angular.forEach(hakuOrganisaatioOids, function(hakuOrgOid) {

                angular.forEach($scope.model.hakukohde.tarjoajaOids, function(tarjoajaOid) {

                    if (hakuOrgOid === tarjoajaOid) {
                        doesMatch = true;
                    }

                });

            });

            return doesMatch;

        };

        $scope.filterHakuWithKohdejoukko = function(hakus, kohdejoukkoUriNimi) {

            var filteredHakus = [];
            angular.forEach(hakus, function(haku) {
                // rajaus kk-hakukohteisiin; ks. OVT-6452
                // TODO selvitä uri valitun koulutuksen perusteella

                var kohdeJoukkoUriNoVersion = $scope.splitUri(haku.kohdejoukkoUri);

                if (kohdeJoukkoUriNoVersion == window.CONFIG.app[kohdejoukkoUriNimi]) {

                    filteredHakus.push(haku);

                }
            });

            return filteredHakus;

        };

        $scope.filterPoistettuHaku = function(hakusParam) {

            var POISTETTU_TILA = "POISTETTU";

            var filteredHakus = [];

            angular.forEach(hakusParam, function(haku) {

                if (haku.tila !== POISTETTU_TILA) {
                    filteredHakus.push(haku);
                }

            });

            return filteredHakus;

        };

        $scope.filterHakusWithOrgs = function(hakus) {

            var filteredHakuArray = [];

            angular.forEach(hakus, function(haku) {
                // $log.info('HAKU ORGOID: ', haku.organisaatioOids);
                if (haku.organisaatioOids && haku.organisaatioOids.length > 0) {

                    if (checkIfOrgMatches(haku.organisaatioOids) || checkIfParentOrgMatches(haku)) {
                        filteredHakuArray.push(haku);
                    }

                } else {
                    filteredHakuArray.push(haku);
                }

            });

            return filteredHakuArray;
        };

        $scope.getTarjoajaParentPathsAndHakus = function(tarjoajaOids, hakufilterFunction) {

            var orgPromises = [];

            angular.forEach(tarjoajaOids, function(tarjoajaOid) {

                var orgPromise = CommonUtilService.haeOrganisaationTiedot(tarjoajaOid);
                orgPromises.push(orgPromise);
            });

            $q.all(orgPromises).then(function(orgs) {
                angular.forEach(orgs, function(org) {
                    if (org.parentOidPath) {
                        angular.forEach(org.parentOidPath.split("|"), function(parentOid) {
                            if (parentOid.length > 1) {
                                parentOrgOids.add(parentOid);
                            }

                        });
                    }
                });
                $scope.retrieveHakus(hakufilterFunction);

            });

        };

        $scope.tryGetParentsApplicationOffice = function(currentOrg) {

            var isOppilaitos = false;

            var isKoulutusToimija = false;

            var oppilaitosTyyppi = "Oppilaitos";

            var koulutusToimijaTyyppi = "Koulutustoimija";

            angular.forEach(currentOrg.tyypit, function(tyyppi) {

                if (tyyppi === oppilaitosTyyppi) {
                    isOppilaitos = true;
                }
                if (tyyppi === koulutusToimijaTyyppi) {
                    isKoulutusToimija = true;
                }

            });

            if (!isOppilaitos && !isKoulutusToimija) {
                if (currentOrg.parentOid !== undefined) {

                    var anotherOrgPromise = OrganisaatioService.byOid(currentOrg.parentOid);
                    anotherOrgPromise.then(function(data) {

                        var wasHakutoimistoFoundNow = checkAndAddHakutoimisto(data);
                        if (wasHakutoimistoFoundNow) {
                            deferredOsoite.resolve($scope.model.liitteidenToimitusOsoite);
                        } else {
                            deferredOsoite.resolve($scope.model.liitteidenToimitusOsoite);
                        }

                    });

                } else {

                    deferredOsoite.resolve($scope.model.liitteidenToimitusOsoite);

                }

            } else {
                deferredOsoite.resolve($scope.model.liitteidenToimitusOsoite);
            }

        };

        $scope.removeEmptyKuvaukses = function() {

            for (var langKey in $scope.model.hakukohde.valintaperusteKuvaukset) {

                if ($scope.model.hakukohde.valintaperusteKuvaukset[langKey].length < 1) {
                    delete $scope.model.hakukohde.valintaperusteKuvaukset[langKey];
                }

            }

            for (var langKey in $scope.model.hakukohde.soraKuvaukset) {

                if ($scope.model.hakukohde.soraKuvaukset[langKey].length < 1) {
                    delete $scope.model.hakukohde.soraKuvaukset[langKey];
                }

            }

        };

        $scope.removeLisatieto = function(koodi) {

            var foundLisatieto;
            angular.forEach($scope.model.hakukohde.lisatiedot, function(lisatieto) {
                if (lisatieto.uri === koodi) {
                    foundLisatieto = lisatieto;
                }
            });

            if (foundLisatieto !== undefined) {
                var index = $scope.model.hakukohde.lisatiedot.indexOf(foundLisatieto);
                $scope.model.hakukohde.lisatiedot.splice(index, 1);
            }

        };

        $scope.naytaHaeValintaperusteKuvaus = function(type) {

            var modalInstance = $modal.open({
                templateUrl: 'partials/hakukohde/edit/haeValintaPerusteKuvausDialog.html',
                controller: 'ValitseValintaPerusteKuvausDialog',
                windowClass: 'valintakoe-modal',
                resolve: {
                    koulutusVuosi: function() {
                        return $scope.model.koulutusVuosi;
                    },
                    oppilaitosTyypit: function() {

                        return $scope.model.hakukohdeOppilaitosTyyppis;
                    },
                    tyyppi: function() {
                        return type;
                    }
                }

            });

            modalInstance.result.then(function(kuvaukset) {

                $log.debug('GOT KUVAUKSET : ', kuvaukset);
                if (!$scope.model.hakukohde.valintaPerusteKuvausKielet) {
                    $scope.model.hakukohde.valintaPerusteKuvausKielet = [];
                }

                if (!$scope.model.hakukohde.soraKuvausKielet) {
                    $scope.model.hakukohde.soraKuvausKielet = [];
                }

                var nkuvaukset = {};
                var nkuvausKielet = [];
                var nkuvausTunniste = undefined;

                for (var i in kuvaukset) {
                    var kuvaus = kuvaukset[i];
                    nkuvausTunniste = kuvaus.toimintoTyyppi == "link" ? kuvaus.tunniste : undefined;
                    nkuvaukset[kuvaus.kieliUri] = kuvaus.teksti;
                    nkuvausKielet.push(kuvaus.kieliUri);
                }

                if (type === "valintaperustekuvaus") {

                    $scope.model.hakukohde.valintaperusteKuvaukset = nkuvaukset;
                    $scope.model.hakukohde.valintaPerusteKuvausKielet = nkuvausKielet;
                    $scope.model.hakukohde.valintaPerusteKuvausTunniste = nkuvausTunniste;

                } else if (type === "SORA") {

                    $scope.model.hakukohde.soraKuvaukset = nkuvaukset;
                    $scope.model.hakukohde.soraKuvausKielet = nkuvausKielet;
                    $scope.model.hakukohde.soraKuvausTunniste = nkuvausTunniste;

                } else {
                    throw ("'valintaperustekuvaus' | 'SORA' != " + type);
                }

                $scope.status.dirty = true;

            });

        };

        $scope.model.isSoraEditable = function() {
            return $scope.model.hakukohde && !$scope.model.hakukohde.soraKuvausTunniste;
        };

        $scope.model.isValintaPerusteEditable = function() {
            return $scope.model.hakukohde && !$scope.model.hakukohde.valintaPerusteKuvausTunniste;
        };

        $scope.loadHakukelpoisuusVaatimukset = function() {
            $scope.model.hakukelpoisuusVaatimusPromise = Koodisto.getAllKoodisWithKoodiUri('pohjakoulutusvaatimuskorkeakoulut', AuthService.getLanguage());
        };

        $scope.loadPainotettavatOppiainevaihtoehdot = function() {
            var painotettavatOppiaineet = [];

            var painotettavatOppiaineetLukiossaPromise = Koodisto.getAllKoodisWithKoodiUri('painotettavatoppiaineetlukiossa', AuthService.getLanguage());
            painotettavatOppiaineetLukiossaPromise.then(function(painotettavatOppiaineetLukiossa){
                for (var i in painotettavatOppiaineetLukiossa) {
                    var uri = painotettavatOppiaineetLukiossa[i].koodiUri;
                    var nimi = painotettavatOppiaineetLukiossa[i].koodiNimi;
                    var versio = painotettavatOppiaineetLukiossa[i].koodiVersio;
                    var painotettavatOppiaine = { oppiaineUri: uri + "#" + versio, lokalisoituNimi: nimi };
                    painotettavatOppiaineet.push(painotettavatOppiaine);
                }
                $scope.painotettavatOppiaineet = painotettavatOppiaineet;
            });
        };

        $scope.enableOrDisableTabs = function() {

            if ($scope.model.hakukohde !== undefined && $scope.model.hakukohde.oid !== undefined) {
                $scope.model.hakukohdeTabsDisabled = false;
            } else {
                $scope.model.hakukohdeTabsDisabled = true;
            }

        };

        $scope.isHakukohdeRootScope = function(scope) {
            return scope == $scope;
        }

        function isDirty() {

            return $scope.status.dirty || ($scope.editHakukohdeForm && $scope.editHakukohdeForm.$dirty);
        }

        $scope.model.takaisin = function(confirm) {
            // console.log("LINK CONFIRM TAKAISIN", [confirm,
            // $scope.editHakukohdeForm, $scope]);
            if (!confirm && isDirty()) {
                dialogService.showModifedDialog().result.then(function(result) {
                    if (result) {
                        $scope.model.takaisin(true);
                    }
                });
            } else {
                $location.path('/etusivu');
            }
        };

        $scope.model.tarkastele = function(confirm) {
            // console.log("LINK CONFIRM TARKASTELE", [confirm,
            // $scope.editHakukohdeForm, $scope]);
            if (!confirm && isDirty()) {
                dialogService.showModifedDialog().result.then(function(result) {
                    if (result) {
                        $scope.model.tarkastele(true);
                    }
                });
            } else {
                $location.path('/hakukohde/' + $scope.model.hakukohde.oid);
            }
        };

        $scope.aContainsB = function(a, b) {
            return a.indexOf(b) >= 0;
        };

        $scope.haeValintaPerusteKuvaus = function() {

            $scope.naytaHaeValintaperusteKuvaus('valintaperustekuvaus');

        };

        $scope.haeSora = function() {

            $scope.naytaHaeValintaperusteKuvaus('SORA');

        };

        /*
         * -----> Helper functions
         */

        var validateNames = function() {
            for (var i in $scope.model.hakukohde.hakukohteenNimet) {
                return true;
            }
            return false;
        };

        var validPainotettavatOppiaineet = function() {
            for (var i in $scope.model.hakukohde.painotettavatOppiaineet) {
                var painokerroin = $scope.model.hakukohde.painotettavatOppiaineet[i].painokerroin;
                var painokerroinBlank = !painokerroin.trim();
                if (painokerroinBlank) {
                    return false;
                }
            }
            return true;
        };

        $scope.getHakuWithOid = function(hakuOid) {

            var foundHaku;

            angular.forEach($scope.model.hakus, function(haku) {
                if (haku && haku.oid === hakuOid) {
                    foundHaku = haku;
                }
            });

            return foundHaku;

        };

        var checkAndAddHakutoimisto = function(data) {
            var hakutoimistoFound = false;
            if (data.metadata !== undefined && data.metadata.yhteystiedot !== undefined) {

                angular.forEach(data.metadata.yhteystiedot, function(yhteystieto) {

                    if (yhteystieto.osoiteTyyppi !== undefined && yhteystieto.osoiteTyyppi === "posti") {
                        var kieliUris = yhteystieto.kieli.split('#');
                        var kieliUri = kieliUris[0];
                        $scope.model.liitteidenToimitusOsoite[kieliUri] = {};
                        $scope.model.liitteidenToimitusOsoite[kieliUri].osoiterivi1 = yhteystieto.osoite;
                        $scope.model.liitteidenToimitusOsoite[kieliUri].postinumero = yhteystieto.postinumeroUri;
                        $scope.model.liitteidenToimitusOsoite[kieliUri].postitoimipaikka = yhteystieto.postitoimipaikka;
                        // $scope.model.hakukohde.liitteidenToimitusOsoite.osoiterivi1
                        // = yhteystieto.osoite;
                        // $scope.model.hakukohde.liitteidenToimitusOsoite.postinumero
                        // = yhteystieto.postinumeroUri;
                        // $scope.model.hakukohde.liitteidenToimitusOsoite.postitoimipaikka
                        // = yhteystieto.postitoimipaikka;
                        hakutoimistoFound = true;

                    }

                });

            }

            return hakutoimistoFound;

        };

        $scope.model.saveParent = function(tila, hakukohdeValidationFunction) {
            if (!tila) {
                throw "tila cannot be undefuned!";
            } else {
                console.log("tallennetaan tila:", tila);
            }
            $scope.model.showError = false;
            PermissionService.permissionResource().authorize({}, function(authResponse) {

                $log.debug('GOT AUTH RESPONSE : ', authResponse);
                $scope.emptyErrorMessages();

                if (hakukohdeValidationFunction()) {
                    $scope.model.showError = false;

                    updateTila(tila);

                    $scope.model.hakukohde.modifiedBy = AuthService.getUserOid();
                    $scope.removeEmptyKuvaukses();

                    // Check if hakukohde is copy, then remove oid and
                    // save hakukohde as new
                    $scope.checkIsCopy($scope.luonnosVal);
                    if ($scope.model.hakukohde.oid === undefined) {

                        $log.debug('LISATIEDOT : ', $scope.model.hakukohde.lisatiedot);

                        // OVT-8199, OVT-8205 Fix "toteutusTyyppi", was not sent to server side... was validated though :)
                        var toteutusTyyppi = SharedStateService.getFromState('SelectedToteutusTyyppi');
                        $scope.model.hakukohde.toteutusTyyppi = toteutusTyyppi;

                        $log.debug('INSERTING MODEL: ', $scope.model.hakukohde);
                        var returnResource = $scope.model.hakukohde.$save();
                        returnResource.then(function(hakukohde) {
                            $log.debug('SERVER RESPONSE WHEN SAVING: ', hakukohde);
                            $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                            HakukohdeService.addValintakoe($scope.model.hakukohde, $scope.model.hakukohde.opetusKielet[0]);
                            if (hakukohde.errors === undefined || hakukohde.errors.length < 1) {
                                $scope.model.hakukohdeOid = $scope.model.hakukohde.oid;
                                $scope.showSuccess();
                                $scope.checkIfSavingCopy($scope.model.hakukohde);
                            } else {
                                $scope.showError(hakukohde.errors);
                            }
                            if ($scope.model.hakukohde.valintaperusteKuvaukset === undefined) {
                                $scope.model.hakukohde.valintaperusteKuvaukset = {};
                            }
                            if ($scope.model.hakukohde.soraKuvaukset === undefined) {
                                $scope.model.hakukohde.soraKuvaukset = {};
                            }
                            $scope.canEdit = true;
                            $scope.model.continueToReviewEnabled = true;
                            $scope.status.dirty = false;
                            $scope.editHakukohdeForm.$dirty = false;
                            $log.debug('SAVED MODEL : ', $scope.model.hakukohde);
                        }, function(error) {
                            $log.debug('ERROR INSERTING HAKUKOHDE : ', error);
                            $scope.showCommonUnknownErrorMsg();

                        });

                    } else {
                        $log.debug('UPDATE MODEL1 : ', $scope.model.hakukohde);
                        var returnResource = $scope.model.hakukohde.$update();
                        returnResource.then(function(hakukohde) {
                            if (hakukohde.status === 'OK') {
                                $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                                HakukohdeService.addValintakoe($scope.model.hakukohde, $scope.model.hakukohde.opetusKielet[0]);
                                $scope.status.dirty = false;
                                $scope.editHakukohdeForm.$dirty = false;
                                $scope.showSuccess();
                                //TODO jos tyhjät valintakokeet, lisää tässä
                            } else {
                                console.log("error", hakukohde);

                                $scope.model.hakukohde = hakukohde.result;

                                if (hakukohde.errors === undefined || hakukohde.errors.length < 1) {
                                    $scope.showSuccess();
                                } else {
                                    $scope.showError(hakukohde.errors);
                                }

                                if ($scope.model.hakukohde.valintaperusteKuvaukset === undefined) {
                                    $scope.model.hakukohde.valintaperusteKuvaukset = {};
                                }
                                if ($scope.model.hakukohde.soraKuvaukset === undefined) {
                                    $scope.model.hakukohde.soraKuvaukset = {};
                                }

                                $scope.showCommonUnknownErrorMsg();
                                $scope.showError(hakukohde.errors);

                            }
                        }, function(error) {
                            $log.debug('EXCEPTION UPDATING HAKUKOHDE: ', error);
                            $scope.showCommonUnknownErrorMsg();
                        });
                    }
                } else {
                    $scope.model.showError = true;
                    $log.debug('WHAAT : ', $scope.model.showError && $scope.editHakukohdeForm.aloituspaikatlkm.$invalid)
                }
            });
        };

        /*

         ------>  Koodisto helper methods

         */
        var findKoodiWithArvo = function(koodi, koodis) {

            var foundKoodi;

            angular.forEach(koodis, function(koodiLoop) {
                if (koodiLoop.koodiArvo === koodi) {
                    foundKoodi = koodiLoop;
                }
            });

            return foundKoodi;
        };

        var processPermissions = function(resourcePermissions) {
            $log.info('PROCESSPERMISSIONS : ', resourcePermissions);
            if (resourcePermissions.hakukohde && resourcePermissions.hakukohde.update && resourcePermissions.hakukohde.updateLimited) {
                $log.info('TTKPP PARTIAL UPDATE');
                $scope.model.isDeEnabled = true;
                $scope.model.isPartiallyDeEnabled = false;
            } else if (resourcePermissions.hakukohde && resourcePermissions.hakukohde.update && !resourcePermissions.hakukohde.updateLimited) {
                $log.info('TTKPP FULL UPDATE');
                $scope.model.isDeEnabled = false;
                $scope.model.isPartiallyDeEnabled = false;
            } else if (resourcePermissions.hakukohde && !resourcePermissions.hakukohde.update) {
                $log.info('TTKPP NO UPDATE');
                $scope.model.isDeEnabled = true;
                $scope.model.isPartiallyDeEnabled = true;
            }

        };

        $scope.isHkDeEnabled = function() {

            return $scope.model.isDeEnabled;
        };

        $scope.checkPermissions = function(hakukohdeOid) {

            $log.info('HAKUKOHDE OID: ', hakukohdeOid);
            var permissionPromise = PermissionService.getPermissions("hakukohde", hakukohdeOid);

            permissionPromise.then(function(permissionResult) {

                processPermissions(permissionResult);

            });

        };

        $scope.findKoodiWithUri = function(koodi, koodis) {

            var foundKoodi;

            angular.forEach(koodis, function(koodiLoop) {
                if (koodiLoop.koodiUri === koodi) {
                    foundKoodi = koodiLoop;
                }
            });

            return foundKoodi;
        };

        var removeHashAndVersion = function(oppilaitosTyyppis) {

            var oppilaitosTyyppisWithOutVersion = [];

            angular.forEach(oppilaitosTyyppis, function(oppilaitosTyyppiUri) {
                angular.forEach(oppilaitosTyyppiUri, function(oppilaitosTyyppiUri) {
                    var splitStr = oppilaitosTyyppiUri.split("#");
                    oppilaitosTyyppisWithOutVersion.push(splitStr[0]);
                });

            });
            return oppilaitosTyyppisWithOutVersion;
        };

        $scope.splitUri = function(uri) {

            var tokenizedArray = uri.split("#");
            return tokenizedArray[0];

        };

        var getOppilaitosTyyppis = function(organisaatiot) {

            var oppilaitosTyyppiPromises = [];

            angular.forEach(organisaatiot, function(organisaatio) {

                var oppilaitosTyypitPromise = CommonUtilService.haeOppilaitostyypit(organisaatio);
                oppilaitosTyyppiPromises.push(oppilaitosTyypitPromise);

            });

            //Resolve all promises and filter oppilaitostyyppis with user types
            $q.all(oppilaitosTyyppiPromises).then(function(data) {
                $log.debug('RESOLVED OPPILAITOSTYYPPI : ', data);
                $scope.model.hakukohdeOppilaitosTyyppis = removeHashAndVersion(data);

            });

        };

        var getParentOrgMap = function(parentOrgSet) {

            var parentOrgMap = {};
            angular.forEach(parentOrgSet, function(parentOrg) {
                parentOrgMap[parentOrg] = 'X';
            });
            return parentOrgMap;
        };

        var checkIfParentOrgMatches = function(haku) {

            var hakuOrganisaatioOids = haku.organisaatioOids;
            var orgMatches = false;
            var parentOrgMap = getParentOrgMap(parentOrgOids);

            angular.forEach(hakuOrganisaatioOids, function(hakuOrganisaatioOid) {

                if (parentOrgMap[hakuOrganisaatioOid]) {
                    orgMatches = true;
                }

            });
            return orgMatches;

        };

        $scope.fnTemp = function() {
            //no nothing. A quick hack, this is here as some directives needs function parameters.
        };

        $scope.temp = null;


        $scope.addPainotettavaOppiaine = function () {
            return HakukohdeService.addPainotettavaOppiaine($scope.model.hakukohde);
        };

        $scope.deletePainotettavaOppiaine = function (painotettavaOppiaine) {
            var p = $scope.model.hakukohde.painotettavatOppiaineet.indexOf(painotettavaOppiaine);
            if (p !== -1) {
                $scope.status.dirty = true;
                $scope.model.hakukohde.painotettavatOppiaineet.splice(p, 1);
                $scope.status.dirtify();
            }
        };
    }]);
