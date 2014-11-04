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
    function ($scope, $log, $routeParams, $route, $q, $modal, $location, Hakukohde, Koodisto, AuthService, HakuService, LocalisationService,
        OrganisaatioService, SharedStateService, TarjontaService, Kuvaus, CommonUtilService, PermissionService, dialogService, HakukohdeService) {

        var korkeakoulutusHakukohdePartialUri = "partials/hakukohde/edit/korkeakoulu/editKorkeakoulu.html";
        var aikuLukioHakukohdePartialUri = "partials/hakukohde/edit/aiku/lukio/editAiku.html";
        var aikuNayttoHakukohdePartialUri = "partials/hakukohde/edit/aiku/naytto/editAmmatillinenNaytto.html";

        var routing = {
            "KORKEAKOULUTUS": korkeakoulutusHakukohdePartialUri,
            "LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA": aikuLukioHakukohdePartialUri,
            "LUKIOKOULUTUS": aikuLukioHakukohdePartialUri,
            "AMMATILLINEN_PERUSKOULUTUS": aikuNayttoHakukohdePartialUri,
            "AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA": aikuNayttoHakukohdePartialUri,
            "ERIKOISAMMATTITUTKINTO": aikuNayttoHakukohdePartialUri,
            "AMMATTITUTKINTO": aikuNayttoHakukohdePartialUri
        };


        /**
         * Tila asetetetaan jos vanhaa tilaa ei ole tai se on luonnos/peruttu/kopioitu
         */
        function updateTila(tila) {
            var tilat = ["LUONNOS", "PERUTTU", "KOPIOITU"];
            if ($scope.model.hakukohde.tila === undefined || tilat.indexOf($scope.model.hakukohde.tila) !== -1) {
                console.log("asetetaan tila modeliin!", tila);
                // päivitä tila modeliin jos se voi muuttua
                $scope.model.hakukohde.tila = tila;
            }
        }


        $scope.controlModelCommandApi = {
            active: false,
            clear: function () {
                throw new Error("Component command link failed : ref not assigned!");
            }
        }; //clear

        /*
         *
         * Common hakukohde controller variables
         *
         */

        $scope.modifiedObj = {
            modifiedBy: '',
            modified: 0,
            tila: ''

        };

        $scope.status = {// ÄLÄ LAITA MODELIIN (pitää näkyä
            // alikontrollereille)
            dirty: false,
            dirtify: function () {
                console.log("DIRTIFY hakukohde");
                $scope.status.dirty = true;
            },
            // alikontrollerit ylikirjoittavat nämä
            validateLiitteet: function () {
                console.log("dummy liite validator, not validating anything!!");
                return true;
            },
            validateValintakokeet: function () {
                return true;
            }
        };

        $scope.showCommonUnknownErrorMsg = function () {

            var errors = [];

            var error = {};

            error.errorMessageKey = "Tuntematon virhe";

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
        $scope.model.hakukelpoisuusValidationErrMsg = false;
        $scope.model.tallennaValmiinaEnabled = true;
        $scope.model.tallennaLuonnoksenaEnabled = true;
        $scope.model.liitteidenToimitusOsoite = {};
        var deferredOsoite = $q.defer();
        $scope.model.liitteenToimitusOsoitePromise = deferredOsoite.promise; //not used it seems
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

        $scope.showSuccess = function () {
            $scope.model.showSuccess = true;
            $scope.model.showError = false;
            $scope.model.validationmsgs = [];
            $scope.model.hakukohdeTabsDisabled = false;
        };

        $scope.showError = function (errorArray) {

            $scope.controlModelCommandApi.clear();

            angular.forEach(errorArray, function (error) {
                if (error.errorMessageKey) {
                    $scope.model.validationmsgs.push(error.errorMessageKey);
                } else {
                    $scope.model.validationmsgs.push(angular.toJson(error));
                }
            });
            $scope.model.showError = true;
            $scope.model.showSuccess = false;
        };

        $scope.getHakukohdePartialUri = function () {

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

        $scope.validateNameLengths = function (hakukohteenNimet) {

            var retval = true;

            angular.forEach(hakukohteenNimet, function (hakukohdeNimi) {

                if (hakukohdeNimi.length > 225) {
                    retval = false;
                }

            });

            return retval;

        };

        $scope.checkJatkaBtn = function (hakukohde) {

            if (hakukohde === undefined || hakukohde.oid === undefined) {
                $log.debug('HAKUKOHDE OR HAKUKOHDE OID UNDEFINED');

                return false;
            } else {
                return true;
            }

        };

        $scope.canSaveParam = function (hakuOid) {

            if (hakuOid) {
                $log.info('CAN EDIT : ', hakuOid);
                var canEdit = TarjontaService.parameterCanEditHakukohdeLimited(hakuOid);
                $log.info('CAN EDIT : ', canEdit);
                $scope.model.isDeEnabled = !canEdit;
            }

            $log.info('IS DEENABLED : ', $scope.model.isDeEnabled);
        };

        $scope.emptyErrorMessages = function () {
            $scope.controlModelCommandApi.clear();
            $scope.model.showError = false;

        };

        $scope.checkCanCreateOrEditHakukohde = function (hakukohde) {

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

        $scope.checkIfSavingCopy = function (hakukohde) {

            if ($scope.model.isCopy) {

                if (hakukohde.oid !== undefined) {

                    $scope.model.isCopy = false;
                    $scope.isCopy = false;

                    $location.path('/hakukohde/' + hakukohde.oid + '/edit');
                }

            }

        };



        $scope.checkIsCopy = function (tilaParam) {

            // If scope or route has isCopy parameter defined as true remove
            // oid so that new hakukohde will be created

            if ($route.current.locals && $route.current.locals.isCopy) {
                $scope.model.hakukohde.oid = undefined;
                $scope.model.hakukohde.tila = tilaParam;

            }

            if ($scope.isCopy !== undefined && $scope.isCopy) {
                $scope.model.hakukohde.oid = undefined;
                $scope.model.hakukohde.tila = tilaParam;
            }

            $scope.model.isCopy = true;

        };

        $scope.createFormattedDateString = function (date) {

            return moment(date).format('DD.MM.YYYY HH:mm');

        };

        $scope.haeTarjoajaOppilaitosTyypit = function () {

            OrganisaatioService.etsi({
                oidRestrictionList: $scope.model.hakukohde.tarjoajaOids
            }).then(function (data) {

                getOppilaitosTyyppis(data.organisaatiot);

            });

        };

        $scope.validateHakukohde = function () {
            $scope.model.aloituspaikatKuvauksetFailed = false;
            $scope.model.hakuaikaValidationFailed = false;

            var errors = [];

            if ($scope.model.hakukohde.hakukelpoisuusvaatimusUris === undefined || $scope.model.hakukohde.hakukelpoisuusvaatimusUris.length < 1) {

                var error = {};
                error.errorMessageKey = 'tarjonta.hakukohde.hakukelpoisuusvaatimus.missing';
                $scope.model.hakukelpoisuusValidationErrMsg = true;
                errors.push(error);

            } else {
                $scope.model.hakukelpoisuusValidationErrMsg = false;
            }

            if ($scope.model.hakukohde.hakuOid === undefined || $scope.model.hakukohde.hakuOid.length < 1) {

                var error = {};
                error.errorMessageKey = 'hakukohde.edit.haku.missing';
                errors.push(error);

            }

            if (!validateNames()) {

                var err = {};
                err.errorMessageKey = 'hakukohde.edit.nimi.missing';
                $scope.model.nimiValidationFailed = true;
                errors.push(err);

            } else {
                $scope.model.nimiValidationFailed = false;
            }

            if (!$scope.validateNameLengths($scope.model.hakukohde.hakukohteenNimet)) {

                var err = {};
                err.errorMessageKey = 'hakukohde.edit.nimi.too.long';

                errors.push(err);
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

            angular.forEach($scope.model.hakukohde.aloituspaikatKuvaukset, function(arvo) {
                if(arvo.length > 20) {
                    errors.push({
                        errorMessageKey: "hakukohde.edit.aloituspaikatKuvaukset.too.long"
                    });
                    $scope.model.aloituspaikatKuvauksetFailed = true;
                    return;
                }
            });

            if($scope.model.hakukohde.hakuaikaAlkuPvm || $scope.model.hakukohde.hakuaikaLoppuPvm) {
                var alku = $scope.model.hakukohde.hakuaikaAlkuPvm;
                var loppu= $scope.model.hakukohde.hakuaikaLoppuPvm;

                if(!(alku && loppu)) {
                    errors.push({
                        errorMessageKey: "hakukohde.edit.hakuaika.errors"
                    });
                    $scope.model.hakuaikaValidationFailed = true;
                }
            }

            if (errors.length < 1 && $scope.editHakukohdeForm.$valid) {
                if (!$scope.model.canSaveHakukohde()) {
                    return false;
                }
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

        $scope.loadKoulutukses = function (hakuFilterFunction) {

            var koulutusSet = new buckets.Set();

            var spec = {
                koulutusOid: $scope.model.hakukohde.hakukohdeKoulutusOids
            };

            TarjontaService.haeKoulutukset(spec).then(function (data) {
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

        $scope.getHakukohteenNimet = function() {
            var ret = "";
            var ja = LocalisationService.t("tarjonta.yleiset.ja");

            for (var i in $scope.model.hakukohde.hakukohteenNimet) {
                if (i > 0) {
                    ret = ret + ((i == $scope.model.hakukohde.hakukohteenNimet.length - 1) ? " " + ja + " " : ", ");
                }
                ret = ret + "<b>" + $scope.model.hakukohde.hakukohteenNimet[i] + "</b>";
            }

            return ret;
        };

        $scope.getHakukohteenJaOrganisaationNimi = function() {
            return $scope.getHakukohteenNimet() + $scope.getOrganisaatioidenNimet();
        };

        $scope.getOrganisaatioidenNimet = function() {
            var ret = "";
            var uniqueTarjoajat = $scope.model.hakukohde.uniqueTarjoajat;
            var organisaationNimi = $scope.model.organisaatioNimet[0];

            // Kun luodaan uutta tämä muuttuja asetetaan true/false
            if ( $scope.model.hakukohde.multipleOwners ) {
                organisaationNimi = null;
            }

            else if (uniqueTarjoajat && uniqueTarjoajat.length) {
                if (uniqueTarjoajat.length === 1) {
                    organisaationNimi = uniqueTarjoajat[0].nimi;
                }
                else {
                    // Ei listata mitään organisaatioita, jos niitä on useita
                    organisaationNimi = null;
                }
            }

            // Kun uusi hakukohde on tallennettu uniqueTarjoajat ei ole asetettu
            else {
                try {
                    var map = $scope.model.hakukohde.koulutusmoduuliToteutusTarjoajatiedot;
                    var firstTarjoajaInMap = map[_.keys(map)[0]].tarjoajaOids[0];
                    var organizationsMatch = $scope.model.hakukohde.tarjoajaOids[0] === firstTarjoajaInMap;
                    if (!organizationsMatch) {
                        organisaationNimi = null;
                    }
                }
                catch(e){}
            }

            if (organisaationNimi) {
                var organisaatiolleMsg = LocalisationService.t("tarjonta.hakukohde.title.org");
                ret = ret + ". " + organisaatiolleMsg + " : <b>" + organisaationNimi + "</b>";
            }

            return ret;
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

            ret += $scope.getOrganisaatioidenNimet();

            return ret;
        };

        $scope.getKoulutustenNimetKey = function () {
            return $scope.model.koulutusnimet.length == 1 ? 'hakukohde.edit.header.single' : 'hakukohde.edit.header.multi';
        };

        $scope.checkIfFirstArraysOneElementExistsInSecond = function (array1, array2) {

            angular.forEach(array1, function (element) {
                if (array2.indexOf(element) != -1) {
                    return true;
                }
            });

        };

        $scope.getSelectedHakuaika = function() {
            if($scope.model.hakuaikas.length === 1) {
                return _.first($scope.model.hakuaikas);
            } else {
                var hakuaikaId = $scope.model.hakukohde.hakuaikaId;
                return _.find($scope.model.hakuaikas, function (hakuaika) { return hakuaika.hakuaikaId ===  hakuaikaId });
            }
        };

        $scope.handleConfigurableHakuaika = function() {

            if($scope.model.hakukohde.toteutusTyyppi === 'KORKEAKOULUTUS') {
                var haku = $scope.getHakuWithOid($scope.model.hakukohde.hakuOid);

                $scope.model.configurableHakuaika = !(haku.hakutapaUri.split('#')[0] === 'hakutapa_01' &&
                    haku.hakutyyppiUri.split('#')[0] === 'hakutyyppi_01');

                var hakuaika = $scope.getSelectedHakuaika();

                $scope.model.hakuaikaMin = hakuaika.alkuPvm;
                $scope.model.hakuaikaMax = hakuaika.loppuPvm;

                $scope.model.hakukohde.hakuaikaAlkuPvm = undefined;
                $scope.model.hakukohde.hakuaikaLoppuPvm = undefined;
            }
        };

        $scope.retrieveHakus = function (filterHakuFunction) {

            var hakuPromise = HakuService.getAllHakus();

            hakuPromise.then(function (hakuDatas) {
                $scope.model.hakus = [];

                angular.forEach(hakuDatas, function (haku) {
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
                    selectedHaku = _.find(hakuDatas, function (m) {
                        return m.oid === $scope.model.hakukohde.hakuOid;
                    });
                }

                var filteredHakus = filterHakuWithParams(filterHakuFunction(hakuDatas));
                $log.info('HAKUS FILTERED WITH GIVEN FUNCTION : ', filteredHakus);

                if (selectedHaku) {
                    var inFilteres = _.find(filteredHakus, function (m) {
                        return m.oid === selectedHaku.oid;
                    });
                    if (!inFilteres) {
                        filteredHakus.push(selectedHaku);
                    }
                }

                $scope.model.hakus = filteredHakus;

                if ($scope.model.hakukohde.hakuOid !== undefined && $scope.model.hakuChanged) {
                    $scope.model.hakuChanged();
                }
            });
        };

        var filterHakuWithParams = function (hakus) {

            var paramFilteredHakus = [];
            angular.forEach(hakus, function (haku) {

                if (TarjontaService.parameterCanAddHakukohdeToHaku(haku.oid)) {
                    paramFilteredHakus.push(haku);
                }
            });

            return paramFilteredHakus;

        };

        var checkIfOrgMatches = function (hakuOrganisaatioOids) {

            var doesMatch = false;

            angular.forEach(hakuOrganisaatioOids, function (hakuOrgOid) {

                angular.forEach($scope.model.hakukohde.tarjoajaOids, function (tarjoajaOid) {

                    if (hakuOrgOid === tarjoajaOid) {
                        doesMatch = true;
                    }

                });

            });

            return doesMatch;

        };

        $scope.filterHakuWithKohdejoukko = function (hakus, kohdejoukkoUriNimi) {

            var filteredHakus = [];
            angular.forEach(hakus, function (haku) {
                // rajaus kk-hakukohteisiin; ks. OVT-6452
                // TODO selvitä uri valitun koulutuksen perusteella

                var kohdeJoukkoUriNoVersion = $scope.splitUri(haku.kohdejoukkoUri);

                if (kohdeJoukkoUriNoVersion === window.CONFIG.app[kohdejoukkoUriNimi]) {
                    if (haku.koulutuksenAlkamiskausiUri && haku.koulutuksenAlkamisVuosi) {
                        if (haku.koulutuksenAlkamiskausiUri === $scope.koulutusKausiUri
                            && haku.koulutuksenAlkamisVuosi === $scope.model.koulutusVuosi) {
                            filteredHakus.push(haku);
                        }
                    }
                    // Esim. jatkuvalla haulla ei ole koulutuksen alkamiskautta/vuotta
                    else {
                        filteredHakus.push(haku);
                    }
                }
            });

            return filteredHakus;

        };

        $scope.filterPoistettuHaku = function (hakusParam) {

            var POISTETTU_TILA = "POISTETTU";

            var filteredHakus = [];

            angular.forEach(hakusParam, function (haku) {

                if (haku.tila !== POISTETTU_TILA) {
                    filteredHakus.push(haku);
                }

            });

            return filteredHakus;

        };

        $scope.filterHakusWithOrgs = function (hakus) {

            var filteredHakuArray = [];

            angular.forEach(hakus, function (haku) {
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

        $scope.getTarjoajaParentPathsAndHakus = function (tarjoajaOids, hakufilterFunction) {

            var orgPromises = [];

            angular.forEach(tarjoajaOids, function (tarjoajaOid) {

                var orgPromise = CommonUtilService.haeOrganisaationTiedot(tarjoajaOid);
                orgPromises.push(orgPromise);
            });

            $q.all(orgPromises).then(function (orgs) {
                angular.forEach(orgs, function (org) {
                    if (org.parentOidPath) {
                        angular.forEach(org.parentOidPath.split("|"), function (parentOid) {
                            if (parentOid.length > 1) {
                                parentOrgOids.add(parentOid);
                            }

                        });
                    }
                });
                $scope.retrieveHakus(hakufilterFunction);

            });

        };

        $scope.tryGetParentsApplicationOffice = function (currentOrg) {

            var isOppilaitos = false;

            var isKoulutusToimija = false;

            var oppilaitosTyyppi = "Oppilaitos";

            var koulutusToimijaTyyppi = "Koulutustoimija";

            angular.forEach(currentOrg.tyypit, function (tyyppi) {

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
                    anotherOrgPromise.then(function (data) {

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

        $scope.removeEmptyKuvaukses = function () {

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

        $scope.removeLisatieto = function (koodi) {

            var foundLisatieto;
            angular.forEach($scope.model.hakukohde.lisatiedot, function (lisatieto) {
                if (lisatieto.uri === koodi) {
                    foundLisatieto = lisatieto;
                }
            });

            if (foundLisatieto !== undefined) {
                var index = $scope.model.hakukohde.lisatiedot.indexOf(foundLisatieto);
                $scope.model.hakukohde.lisatiedot.splice(index, 1);
            }

        };

        $scope.naytaHaeValintaperusteKuvaus = function (type) {

            var modalInstance = $modal.open({
                templateUrl: 'partials/hakukohde/edit/haeValintaPerusteKuvausDialog.html',
                controller: 'ValitseValintaPerusteKuvausDialog',
                windowClass: 'valintakoe-modal',
                resolve: {
                    koulutusVuosi: function () {
                        return $scope.model.koulutusVuosi;
                    },
                    oppilaitosTyypit: function () {

                        return $scope.model.hakukohdeOppilaitosTyyppis;
                    },
                    tyyppi: function () {
                        return type;
                    }
                }

            });

            modalInstance.result.then(function (kuvaukset) {

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
                    $scope.setSelectedValintaPerusteKuvausByTunniste();
                } else if (type === "SORA") {

                    $scope.model.hakukohde.soraKuvaukset = nkuvaukset;
                    $scope.model.hakukohde.soraKuvausKielet = nkuvausKielet;
                    $scope.model.hakukohde.soraKuvausTunniste = nkuvausTunniste;
                    $scope.setSelectedSoraKuvausByTunniste();
                } else {
                    throw ("'valintaperustekuvaus' | 'SORA' != " + type);
                }

                $scope.status.dirty = true;

            });

        };

        $scope.model.isSoraEditable = function () {
            return $scope.model.hakukohde && !$scope.model.hakukohde.soraKuvausTunniste;
        };

        $scope.model.isValintaPerusteEditable = function () {
            return $scope.model.hakukohde && !$scope.model.hakukohde.valintaPerusteKuvausTunniste;
        };

        $scope.loadHakukelpoisuusVaatimukset = function () {
            $scope.model.hakukelpoisuusVaatimusPromise = Koodisto.getAllKoodisWithKoodiUri('pohjakoulutusvaatimuskorkeakoulut', AuthService.getLanguage());
        };

        $scope.enableOrDisableTabs = function () {

            if ($scope.model.hakukohde !== undefined && $scope.model.hakukohde.oid !== undefined) {
                $scope.model.hakukohdeTabsDisabled = false;
            } else {
                $scope.model.hakukohdeTabsDisabled = true;
            }

        };

        $scope.isHakukohdeRootScope = function (scope) {
            return scope == $scope;
        }

        function isDirty() {

            return $scope.status.dirty || ($scope.editHakukohdeForm && $scope.editHakukohdeForm.$dirty);
        }

        $scope.model.takaisin = function (confirm) {
            // console.log("LINK CONFIRM TAKAISIN", [confirm,
            // $scope.editHakukohdeForm, $scope]);
            if (!confirm && isDirty()) {
                dialogService.showModifedDialog().result.then(function (result) {
                    if (result) {
                        $scope.model.takaisin(true);
                    }
                });
            } else {
                $location.path('/etusivu');
            }
        };

        $scope.model.tarkastele = function (confirm) {
            // console.log("LINK CONFIRM TARKASTELE", [confirm,
            // $scope.editHakukohdeForm, $scope]);
            if (!confirm && isDirty()) {
                dialogService.showModifedDialog().result.then(function (result) {
                    if (result) {
                        $scope.model.tarkastele(true);
                    }
                });
            } else {
                $location.path('/hakukohde/' + $scope.model.hakukohde.oid);
            }
        };

        $scope.aContainsB = function (a, b) {
            return a.indexOf(b) >= 0;
        };

        $scope.haeValintaPerusteKuvaus = function () {

            $scope.naytaHaeValintaperusteKuvaus('valintaperustekuvaus');

        };

        $scope.haeSora = function () {

            $scope.naytaHaeValintaperusteKuvaus('SORA');

        };

        /*
         * -----> Helper functions
         */

        var validateNames = function () {
            for (var i in $scope.model.hakukohde.hakukohteenNimet) {
            	if ($scope.model.hakukohde.hakukohteenNimet[i]) {
                    return true;
				}
            }
            return false;
        };

        $scope.getHakuWithOid = function (hakuOid) {

            var foundHaku;

            angular.forEach($scope.model.hakus, function (haku) {
                if (haku.oid === hakuOid) {
                    foundHaku = haku;
                }
            });

            return foundHaku;

        };

        var checkAndAddHakutoimisto = function (data) {
            var hakutoimistoFound = false;
            if (data.metadata !== undefined && data.metadata.yhteystiedot !== undefined) {

                angular.forEach(data.metadata.yhteystiedot, function (yhteystieto) {

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

        $scope.model.saveParent = function (tila, hakukohdeValidationFunction) {
            if (!tila) {
                throw "tila cannot be undefuned!";
            } else {
                $log.debug("tallennetaan tila:", tila, hakukohdeValidationFunction);
            }
            $scope.model.showError = false;
            PermissionService.permissionResource().authorize({}, function (authResponse) {

                $log.debug('GOT AUTH RESPONSE : ', authResponse);
                $scope.emptyErrorMessages();

                HakukohdeService.removeEmptyLiites($scope.model.hakukohde.hakukohteenLiitteet);
                if (hakukohdeValidationFunction()) {
                    $scope.model.showError = false;

                    updateTila(tila);

                    $scope.model.hakukohde.modifiedBy = AuthService.getUserOid();
                    $scope.removeEmptyKuvaukses();

                    // Hakukohteiden liitteiden järjestys
                    angular.forEach($scope.model.hakukohde.hakukohteenLiitteet, function(liite, index) {
                        liite.jarjestys = index;
                    });

                    // Check if hakukohde is copy, then remove oid and
                    // save hakukohde as new
                    $scope.checkIsCopy($scope.luonnosVal);

                    if ($scope.model.hakukohde.oid === undefined) {

                        $log.debug('LISATIEDOT : ', $scope.model.hakukohde.lisatiedot);

                        // OVT-8199, OVT-8205 Fix "toteutusTyyppi", was not sent to server side... was validated though :)
                        var toteutusTyyppi = SharedStateService.getFromState('SelectedToteutusTyyppi');
                        $scope.model.hakukohde.toteutusTyyppi = toteutusTyyppi;

                        // KJOH-778, pitää tietää mille organisaatiolle ollaan luomassa hakukohdetta
                        var tarjoajatiedot = {};
                        angular.forEach($scope.hakukohdex.hakukohdeKoulutusOids, function(komotoOid) {
                            tarjoajatiedot[komotoOid] = {
                                tarjoajaOids: [AuthService.getUserDefaultOid()]
                            };
                        });
                        $scope.model.hakukohde.koulutusmoduuliToteutusTarjoajatiedot = tarjoajatiedot;

                        $log.debug('INSERTING MODEL: ', $scope.model.hakukohde);
                        var returnResource = $scope.model.hakukohde.$save();
                        returnResource.then(function (hakukohde) {
                            $log.debug('SERVER RESPONSE WHEN CREATING: ', hakukohde);
                            $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                            HakukohdeService.addValintakoe($scope.model.hakukohde, $scope.model.hakukohde.opetusKielet[0]);
                            HakukohdeService.addLiiteIfEmpty($scope.model.hakukohde, $scope.model.hakukohde.opetusKielet[0]);
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
                        }, function (error) {
                            $log.debug('ERROR INSERTING HAKUKOHDE : ', error);
                            $scope.showCommonUnknownErrorMsg();

                        });

                    } else {
                        $log.debug('UPDATE MODEL1 : ', $scope.model.hakukohde);
                        var returnResource = $scope.model.hakukohde.$update();
                        returnResource.then(function (hakukohde) {
                            $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                            if (hakukohde.status === 'OK') {
                                HakukohdeService.addValintakoe($scope.model.hakukohde, $scope.model.hakukohde.opetusKielet[0]);
                                HakukohdeService.addLiiteIfEmpty($scope.model.hakukohde);
                                $scope.status.dirty = false;
                                if ($scope.editHakukohdeForm) {
                                    $scope.editHakukohdeForm.$dirty = false;
                                }
                                $scope.showSuccess();
                            } else {
                                console.log("error", hakukohde);

                                if ($scope.model.hakukohde.valintaperusteKuvaukset === undefined) {
                                    $scope.model.hakukohde.valintaperusteKuvaukset = {};
                                }
                                if ($scope.model.hakukohde.soraKuvaukset === undefined) {
                                    $scope.model.hakukohde.soraKuvaukset = {};
                                }
                                $scope.showError(hakukohde.errors);
                            }
                        }, function (error) {
                            $log.debug('EXCEPTION UPDATING HAKUKOHDE: ', error);
                            $scope.showCommonUnknownErrorMsg();
                        });
                    }
                }
            });
        };

        /*

         ------>  Koodisto helper methods

         */
        var findKoodiWithArvo = function (koodi, koodis) {

            var foundKoodi;

            angular.forEach(koodis, function (koodiLoop) {
                if (koodiLoop.koodiArvo === koodi) {
                    foundKoodi = koodiLoop;
                }
            });

            return foundKoodi;
        };

        var processPermissions = function (resourcePermissions) {
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

        $scope.isHkDeEnabled = function () {

            return $scope.model.isDeEnabled;
        };

        $scope.checkPermissions = function (hakukohdeOid) {

            $log.info('HAKUKOHDE OID: ', hakukohdeOid);
            var permissionPromise = PermissionService.getPermissions("hakukohde", hakukohdeOid);

            permissionPromise.then(function (permissionResult) {

                processPermissions(permissionResult);

            });

        };

        $scope.findKoodiWithUri = function (koodi, koodis) {

            var foundKoodi;

            angular.forEach(koodis, function (koodiLoop) {
                if (koodiLoop.koodiUri === koodi) {
                    foundKoodi = koodiLoop;
                }
            });

            return foundKoodi;
        };

        var removeHashAndVersion = function (oppilaitosTyyppis) {

            var oppilaitosTyyppisWithOutVersion = [];

            angular.forEach(oppilaitosTyyppis, function (oppilaitosTyyppiUri) {
                angular.forEach(oppilaitosTyyppiUri, function (oppilaitosTyyppiUri) {
                    var splitStr = oppilaitosTyyppiUri.split("#");
                    oppilaitosTyyppisWithOutVersion.push(splitStr[0]);
                });

            });
            return oppilaitosTyyppisWithOutVersion;
        };

        $scope.splitUri = function (uri) {

            var tokenizedArray = uri.split("#");
            return tokenizedArray[0];

        };

        var getOppilaitosTyyppis = function (organisaatiot) {

            var oppilaitosTyyppiPromises = [];

            angular.forEach(organisaatiot, function (organisaatio) {

                var oppilaitosTyypitPromise = CommonUtilService.haeOppilaitostyypit(organisaatio);
                oppilaitosTyyppiPromises.push(oppilaitosTyypitPromise);

            });

            //Resolve all promises and filter oppilaitostyyppis with user types
            $q.all(oppilaitosTyyppiPromises).then(function (data) {
                $log.debug('RESOLVED OPPILAITOSTYYPPI : ', data);
                $scope.model.hakukohdeOppilaitosTyyppis = removeHashAndVersion(data);

            });

        };

        var getParentOrgMap = function (parentOrgSet) {

            var parentOrgMap = {};
            angular.forEach(parentOrgSet, function (parentOrg) {
                parentOrgMap[parentOrg] = 'X';
            });
            return parentOrgMap;
        };

        var checkIfParentOrgMatches = function (haku) {

            var hakuOrganisaatioOids = haku.organisaatioOids;
            var orgMatches = false;
            var parentOrgMap = getParentOrgMap(parentOrgOids);

            angular.forEach(hakuOrganisaatioOids, function (hakuOrganisaatioOid) {

                if (parentOrgMap[hakuOrganisaatioOid]) {
                    orgMatches = true;
                }

            });
            return orgMatches;

        };

        $scope.fnTemp = function () {
            //no nothing. A quick hack, this is here as some directives needs function parameters.
        };

        $scope.temp = null;

        $scope.getHakuByOid = function(oid) {
            var haku = null;
            angular.forEach($scope.model.hakus, function(element) {
               if(element.oid === oid) {
                   haku = element;
               }
            });
            return haku;
        }

        /*
         * Testaa onko haun 'Ei sähköistä hakua. Lisatietoa hakemisesta tarjotaan hakukohteen tiedoissa.' valittu.
         * Kaytossa : AIKU lukio / amm
         */
        $scope.validateIsHakuEisahkoistaHakuaRadioButtonSelected = function (errors) {
            var haku = $scope.getHakuByOid($scope.model.hakukohde.hakuOid);
            if (haku && !haku.jarjestelmanHakulomake &&
                (!haku.hakulomakeUri || haku.hakulomakeUri.trim().length === 0)) {
                var empty = true;
                angular.forEach($scope.model.hakukohde.lisatiedot, function (val) {
                    if (val && val.trim().length > 0) {
                        empty = false;
                        return;
                    }
                });

                if (empty) {
                    var err = {};
                    err.errorMessageKey = 'hakukohde.edit.lisatietoja-hakemisesta.required';
                    errors.push(err);
                }
            }
        };

        $scope.setSelectedValintaPerusteKuvausByTunniste = function() {
            if($scope.model.hakukohde.valintaPerusteKuvausTunniste !== undefined) {
                Kuvaus.findKuvausWithId($scope.model.hakukohde.valintaPerusteKuvausTunniste).then(function(data) {
                    $scope.model.selectedValintaperusteKuvaus = data.result;

                    $scope.model.selectedValintaperusteKuvaus.title = data.result.kuvauksenNimet["kieli_" + AuthService.getLanguage().toLowerCase()];

                    if($scope.model.selectedValintaperusteKuvaus.title === undefined) {
                        $scope.model.selectedValintaperusteKuvaus.title = Object.keys(data.result.kuvauksenNimet)[0];
                    }
                });
            } else {
                $scope.model.selectedValintaperusteKuvaus = undefined;
            }
        }

        $scope.setSelectedSoraKuvausByTunniste = function() {
            if($scope.model.hakukohde.soraKuvausTunniste !== undefined) {
                Kuvaus.findKuvausWithId($scope.model.hakukohde.soraKuvausTunniste).then(function(data) {
                    $scope.model.selectedSoraKuvaus = data.result;

                    $scope.model.selectedSoraKuvaus.title = data.result.kuvauksenNimet["kieli_" + AuthService.getLanguage().toLowerCase()];

                    if($scope.model.selectedSoraKuvaus.title === undefined) {
                        $scope.model.selectedSoraKuvaus.title = Object.keys(data.result.kuvauksenNimet)[0];
                    }
                });
            } else {
                $scope.model.selectedSoraKuvaus = undefined;
            }
        }

        $scope.setSelectedValintaPerusteKuvausByTunniste();
        $scope.setSelectedSoraKuvausByTunniste();

        $scope.removeValintaperustekuvaus = function() {
            var d = dialogService.showDialog({
                ok: LocalisationService.t("ok"),
                cancel: LocalisationService.t("cancel"),
                title: LocalisationService.t("tarjonta.tyhjennäValintaperustekuvausDialogi.otsikko"),
                description: LocalisationService.t("tarjonta.tyhjennäValintaperustekuvausDialogi.kuvaus")
            });

            d.result.then(function(data) {
                if (data) {
                    $scope.model.selectedValintaperusteKuvaus = undefined;
                    $scope.model.hakukohde.valintaPerusteKuvausTunniste = undefined;
                    $scope.model.hakukohde.valintaperusteKuvaukset = {};
                    $scope.model.hakukohde.valintaPerusteKuvausKielet = [];
                }
            });
        }

        $scope.removeSoraKuvaus = function() {
            var d = dialogService.showDialog({
                ok: LocalisationService.t("ok"),
                cancel: LocalisationService.t("cancel"),
                title: LocalisationService.t("tarjonta.tyhjennäSoraKuvausDialogi.otsikko"),
                description: LocalisationService.t("tarjonta.tyhjennäSoraKuvausDialogi.kuvaus")
            });

            d.result.then(function(data) {
                if (data) {
                    $scope.model.selectedSoraKuvaus = undefined;
                    $scope.model.hakukohde.soraKuvausTunniste = undefined;
                    $scope.model.hakukohde.soraKuvaukset = {};
                    $scope.model.hakukohde.soraKuvausKielet = [];
                }
            });
        }
    }]);
