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
    'ValidatorService',
    'HAKUTAPA',
    function($scope, $log, $routeParams, $route, $q, $modal, $location, Hakukohde,
                 Koodisto, AuthService, HakuService, LocalisationService, OrganisaatioService,
                 SharedStateService, TarjontaService, Kuvaus, CommonUtilService, PermissionService,
                 dialogService, HakukohdeService, ValidatorService, HAKUTAPA) {

        var korkeakoulutusHakukohdePartialUri = 'partials/hakukohde/edit/korkeakoulu/editKorkeakoulu.html';
        var korkeakouluOpintoHakukohdePartialUri = 'partials/hakukohde/edit/korkeakouluopinto/' +
            'editKorkeakouluOpinto.html';
        var aikuLukioHakukohdePartialUri = 'partials/hakukohde/edit/aiku/lukio/editAiku.html';
        var aikuNayttoHakukohdePartialUri = 'partials/hakukohde/edit/aiku/naytto/editAmmatillinenNaytto.html';
        var toinenAsteHakukohdePartialUri = 'partials/hakukohde/edit/TOINEN_ASTE.html';
        var routing = {
            'KORKEAKOULUTUS': korkeakoulutusHakukohdePartialUri,
            KORKEAKOULUOPINTO: korkeakouluOpintoHakukohdePartialUri,
            'LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA': aikuLukioHakukohdePartialUri,
            EB_RP_ISH: aikuLukioHakukohdePartialUri,
            'AMMATILLINEN_PERUSKOULUTUS': aikuNayttoHakukohdePartialUri,
            'AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA': aikuNayttoHakukohdePartialUri,
            'ERIKOISAMMATTITUTKINTO': aikuNayttoHakukohdePartialUri,
            'AMMATTITUTKINTO': aikuNayttoHakukohdePartialUri,
            'AMMATILLINEN_PERUSTUTKINTO': toinenAsteHakukohdePartialUri,
            'LUKIOKOULUTUS': toinenAsteHakukohdePartialUri,
            'MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS': toinenAsteHakukohdePartialUri,
            'MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS': toinenAsteHakukohdePartialUri,
            'AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS': toinenAsteHakukohdePartialUri,
            'PERUSOPETUKSEN_LISAOPETUS': toinenAsteHakukohdePartialUri,
            'VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS': toinenAsteHakukohdePartialUri,
            'VAPAAN_SIVISTYSTYON_KOULUTUS': toinenAsteHakukohdePartialUri,
            'AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA': toinenAsteHakukohdePartialUri,
            AIKUISTEN_PERUSOPETUS: aikuLukioHakukohdePartialUri,
            AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA: toinenAsteHakukohdePartialUri,
            AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER: toinenAsteHakukohdePartialUri
        };

        $scope.config = HakukohdeService.config;
        $scope.config.setToteutustyyppi($scope.model.hakukohde.toteutusTyyppi);
        $scope.toisenAsteenKoulutus = HakukohdeService.config.isToisenAsteenKoulutus;

        $scope.needsHakukelpoisuus = function(toteutusTyyppi) {
            return !_.contains([
                'PERUSOPETUKSEN_LISAOPETUS',
                'LUKIOKOULUTUS',
                'AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS',
                'MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS',
                'MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS',
                'VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS',
                'VAPAAN_SIVISTYSTYON_KOULUTUS',
                'AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA',
                'AMMATILLINEN_PERUSTUTKINTO'
            ], toteutusTyyppi);
        };
        $scope.needsLiitteidenToimitustiedot = function(toteutusTyyppi) {
            return _.contains([
                'AMMATILLINEN_PERUSTUTKINTO',
                'LUKIOKOULUTUS',
                'AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA'
            ], toteutusTyyppi);
        };
        $scope.editableValintaperustekuvaus = function(toteutusTyyppi) {
            return _.contains([
                'PERUSOPETUKSEN_LISAOPETUS',
                'AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS',
                'MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS',
                'MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS',
                'VAPAAN_SIVISTYSTYON_KOULUTUS',
                'VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS',
                'AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA',
                'AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER'
            ], toteutusTyyppi);
        };
        $scope.CONFIGURATION = {
            LIITE: {
                showKaytetaanHakulomakkeella: {
                    'KORKEAKOULUTUS': true
                }
            },
            HAKUKOHDERYHMA: {
                showHakukohderyhmat: {
                    'KORKEAKOULUTUS': true,
                    KORKEAKOULUOPINTO: true
                }
            },
            YHTEYSTIEDOT: {
                showYhteystiedot: {
                    'KORKEAKOULUTUS': true,
                    'AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA': true,
                    'ERIKOISAMMATTITUTKINTO': true,
                    'AMMATTITUTKINTO': true
                }
            },
            disableConfigurableHakuaika: [
                'VAPAAN_SIVISTYSTYON_KOULUTUS',
                'AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA',
                'AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA',
                'AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER'
            ]
        };
        /**
             * Tila asetetetaan jos vanhaa tilaa ei ole tai se on luonnos/peruttu/kopioitu
             */
        function updateTila(tila) {
            var tilat = [
                'LUONNOS',
                'PERUTTU',
                'KOPIOITU'
            ];
            if ($scope.model.hakukohde.tila === undefined || tilat.indexOf($scope.model.hakukohde.tila) !== -1) {
                console.log('asetetaan tila modeliin!', tila);
                // päivitä tila modeliin jos se voi muuttua
                $scope.model.hakukohde.tila = tila;
            }
        }
        $scope.controlModelCommandApi = {
            active: false,
            clear: function() {
                throw new Error('Component command link failed : ref not assigned!');
            }
        };
        //clear
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
        $scope.status = {
            // ÄLÄ LAITA MODELIIN (pitää näkyä
            // alikontrollereille)
            dirty: false,
            dirtify: function() {
                console.log('DIRTIFY hakukohde');
                $scope.status.dirty = true;
            }
        };
        $scope.showCommonUnknownErrorMsg = function() {
            var errors = [];
            var error = {};
            error.errorMessageKey = 'Tuntematon virhe';
            errors.push(error);
            $scope.showError(errors);
        };
        $scope.model.today = new Date();
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
        $scope.model.hakukohde.muuYhteystieto = false;
        var deferredOsoite = $q.defer();
        $scope.model.liitteenToimitusOsoitePromise = deferredOsoite.promise;
        $scope.model.liitteidenToimitusPvm = new Date();
        $scope.userLangs = window.CONFIG.app.userLanguages;
        $scope.model.defaultLang = 'kieli_fi';
        $scope.model.showHakuaikas = false;
        $scope.model.configurableHakuaika = false;
        $scope.model.collapse.model = true;
        $scope.model.hakus = [];
        $scope.model.hakuaikas = [];
        $scope.model.valintakoe = {};
        $scope.model.liitteidenMuuOsoiteEnabled = true;
        $scope.model.liitteidenSahkoinenOsoiteEnabled = $scope.model.hakukohde.sahkoinenToimitusOsoite !== undefined
            && $scope.model.hakukohde.sahkoinenToimitusOsoite.length > 0;
        $scope.model.isDeEnabled = false;
        $scope.model.isPartiallyDeEnabled = false;
        var parentOrgOids = new buckets.Set();
        var orgSet = new buckets.Set();
        // All kieles is received from koodistomultiselect
        $scope.model.allkieles = [];
        $scope.model.selectedKieliUris = [];
        $scope.model.integerval = /^\d*$/;
        $scope.model.languages = [];
        $scope.julkaistuVal = 'JULKAISTU';
        $scope.luonnosVal = 'LUONNOS';
        $scope.valmisVal = 'VALMIS';
        $scope.peruttuVal = 'PERUTTU';
        $scope.showSuccess = function() {
            $scope.model.showSuccess = true;
            $scope.model.showError = false;
            $scope.model.validationmsgs = [];
            $scope.model.hakukohdeTabsDisabled = false;
        };
        $scope.showError = function(errorArray) {
            $scope.controlModelCommandApi.clear();
            angular.forEach(errorArray, function(error) {
                if (error.errorMessageKey) {
                    $scope.model.validationmsgs.push(error.errorMessageKey);
                }
                else {
                    $scope.model.validationmsgs.push(angular.toJson(error));
                }
            });
            $scope.model.showError = true;
            $scope.model.showSuccess = false;
        };
        $scope.getHakukohdePartialUri = function() {
            var toteutusTyyppi;
            // If hakukohdex is defined then we are updating it
            // otherwise try to get selected koulutustyyppi from shared
            // state
            if ($route.current.locals && $route.current.locals.hakukohdex.result
                && $route.current.locals.hakukohdex.result.toteutusTyyppi) {
                toteutusTyyppi = $route.current.locals.hakukohdex.result.toteutusTyyppi;
                if (routing[toteutusTyyppi]) {
                    return routing[toteutusTyyppi];
                }
            }
            else {
                toteutusTyyppi = SharedStateService.getFromState('SelectedToteutusTyyppi');
                // $scope.model.hakukohde.toteutusTyyppi=toteutusTyyppi;
                if (routing[toteutusTyyppi]) {
                    return routing[toteutusTyyppi];
                }
                $log.error('TOTEUTUSTYYPPI WAS: ', toteutusTyyppi, ' not returning template!!');
            }
        };
        $scope.checkJatkaBtn = function(hakukohde) {
            if (hakukohde === undefined || hakukohde.oid === undefined) {
                $log.debug('HAKUKOHDE OR HAKUKOHDE OID UNDEFINED');
                return false;
            }
            else {
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
            $scope.controlModelCommandApi.clear();
            $scope.model.showError = false;
        };
        $scope.checkCanCreateOrEditHakukohde = function(hakukohde) {
            if (hakukohde.oid !== undefined) {
                if ($scope.canEdit !== undefined) {
                    return $scope.canEdit;
                }
                else {
                    return true;
                }
            }
            else {
                if ($scope.canCreate !== undefined) {
                    return $scope.canCreate;
                }
                else {
                    return true;
                }
            }
        };
        $scope.checkIfSavingCopy = function(hakukohde) {
            if ($scope.model.isCopy) {
                if (hakukohde.oid !== undefined) {
                    $scope.model.isCopy = false;
                    $scope.isCopy = false;
                    $location.path('/hakukohde/' + hakukohde.oid + '/edit');
                }
            }
        };
        $scope.checkIsCopy = function(tilaParam) {
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
        $scope.loadKoulutukses = function() {
            var koulutusSet = new buckets.Set();
            var spec = {
                koulutusOid: $scope.model.hakukohde.hakukohdeKoulutusOids
            };
            TarjontaService.haeKoulutukset(spec).then(function(data) {
                var tarjoajaOidsSet = new buckets.Set();
                if (data !== undefined) {
                    angular.forEach(data.tulokset, function(tulos) {
                        if (tulos !== undefined && tulos.tulokset !== undefined) {
                            tarjoajaOidsSet.add(tulos.oid);
                            angular.forEach(tulos.tulokset, function(toinenTulos) {
                                $scope.koulutusKausiUri = toinenTulos.kausiUri;
                                $scope.model.koulutusVuosi = toinenTulos.vuosi;
                                $scope.model.koulutusmoduuliTyyppi = toinenTulos.koulutusmoduuliTyyppi;
                                koulutusSet.add(toinenTulos.nimi);
                            });
                        }
                    });
                    $scope.model.koulutusnimet = koulutusSet.toArray();
                    $scope.model.hakukohde.tarjoajaOids = tarjoajaOidsSet.toArray();
                    $scope.getTarjoajaParentPathsAndHakus($scope.model.hakukohde.tarjoajaOids);

                    OrganisaatioService.getPopulatedOrganizations($scope.model.hakukohde.tarjoajaOids)
                    .then(function(orgs) {
                        var counter = 0;
                        angular.forEach(orgs, function(data) {
                            orgSet.add(data.nimi);
                            if (counter === 0) {
                                var wasHakutoimistoFound = checkAndAddHakutoimisto(data);
                                if (wasHakutoimistoFound) {
                                    deferredOsoite.resolve($scope.model.liitteidenToimitusOsoite);
                                }
                                else {
                                    $scope.tryGetParentsApplicationOffice(data);
                                }
                            }
                            handleHakukohteenYhteystiedot(data);
                            counter++;
                        });
                        $scope.model.organisaatioNimet = orgSet.toArray();
                    });
                }
            });
        };
        $scope.getHakukohteenNimet = function() {
            var ret = '';
            var ja = LocalisationService.t('tarjonta.yleiset.ja');
            for (var i in $scope.model.hakukohde.hakukohteenNimet) {
                if (i > 0) {
                    ret = ret + (i == $scope.model.hakukohde.hakukohteenNimet.length - 1 ? ' ' + ja + ' ' : ', ');
                }
                ret = ret + '<b>' + $scope.model.hakukohde.hakukohteenNimet[i] + '</b>';
            }
            return ret;
        };
        $scope.getHakukohteenJaOrganisaationNimi = function() {
            return $scope.getHakukohteenNimet() + $scope.getOrganisaatioidenNimet();
        };
        $scope.getOrganisaatioidenNimet = function() {
            var ret = '';
            var uniqueTarjoajat = $scope.model.hakukohde.uniqueTarjoajat;
            var organisaationNimi = $scope.model.organisaatioNimet[0];
            // Kun luodaan uutta tämä muuttuja asetetaan true/false
            if ($scope.model.hakukohde.multipleOwners) {
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
            } // Kun uusi hakukohde on tallennettu uniqueTarjoajat ei ole asetettu
            else {
                try {
                    var map = $scope.model.hakukohde.koulutusmoduuliToteutusTarjoajatiedot;
                    var firstTarjoajaInMap = map[_.keys(map)[0]].tarjoajaOids[0];
                    var organizationsMatch = $scope.model.hakukohde.tarjoajaOids[0] === firstTarjoajaInMap;
                    if (!organizationsMatch) {
                        organisaationNimi = null;
                    }
                }
                catch (e) {}
            }
            if (organisaationNimi) {
                var organisaatiolleMsg = LocalisationService.t('tarjonta.hakukohde.title.org');
                ret = ret + '. ' + organisaatiolleMsg + ' : <b>' + organisaationNimi + '</b>';
            }
            return ret;
        };
        $scope.getKoulutustenNimet = function() {
            var ret = '';
            var ja = LocalisationService.t('tarjonta.yleiset.ja');
            for (var i in $scope.model.koulutusnimet) {
                if (i > 0) {
                    ret = ret + (i == $scope.model.koulutusnimet.length - 1 ? ' ' + ja + ' ' : ', ');
                }
                ret = ret + '<b>' + $scope.model.koulutusnimet[i] + '</b>';
            }
            ret += $scope.getOrganisaatioidenNimet();
            return ret;
        };
        $scope.getKoulutustenNimetKey = function() {
            return $scope.model.koulutusnimet.length == 1 ?
                'hakukohde.edit.header.single' :
                'hakukohde.edit.header.multi';
        };
        $scope.checkIfFirstArraysOneElementExistsInSecond = function(array1, array2) {
            angular.forEach(array1, function(element) {
                if (array2.indexOf(element) != -1) {
                    return true;
                }
            });
        };
        $scope.getSelectedHakuaika = function() {
            if ($scope.model.hakuaikas.length === 1) {
                return _.first($scope.model.hakuaikas);
            }
            else {
                var hakuaikaId = $scope.model.hakukohde.hakuaikaId;
                var hakuaika = _.find($scope.model.hakuaikas, function(hakuaika) {
                    return hakuaika.hakuaikaId === hakuaikaId;
                });
                if (hakuaika !== undefined) {
                    return hakuaika;
                }
                else {
                    return _.first($scope.model.hakuaikas);
                }
            }
        };
        $scope.clearHakuajat = function() {
            $scope.model.hakukohde.hakuaikaAlkuPvm = undefined;
            $scope.model.hakukohde.hakuaikaLoppuPvm = undefined;
            var hakuaika = $scope.getSelectedHakuaika();
            $scope.model.hakuaikaMin = hakuaika.alkuPvm;
            $scope.model.hakuaikaMax = hakuaika.loppuPvm;
        };
        var getToteutustyyppiFromHakukohdeOrSharedState = function() {
            if ($scope.model.hakukohde.toteutusTyyppi !== undefined) {
                return $scope.model.hakukohde.toteutusTyyppi;
            }
            else {
                return SharedStateService.getFromState('SelectedToteutusTyyppi');
            }
        };
        $scope.handleConfigurableHakuaika = function() {
            var hakuaika;
            if ($scope.model.hakukohde.hakuOid) {
                var toteutustyyppi = getToteutustyyppiFromHakukohdeOrSharedState();
                var haku = $scope.getHakuWithOid($scope.model.hakukohde.hakuOid);
                if ($scope.toisenAsteenKoulutus(toteutustyyppi)) {
                    hakuaika = getHakuaikaForToisenAsteenKoulutus(haku);
                    $scope.model.configurableHakuaika = oph.removeKoodiVersion(haku.hakutyyppiUri) === 'hakutyyppi_03'
                        || oph.removeKoodiVersion(haku.hakutapaUri) === 'hakutapa_02';
                    $scope.model.hakukohde.hakuaikaId = hakuaika.hakuaikaId;
                    $scope.model.hakuaikaMin = hakuaika.alkuPvm;
                    $scope.model.hakuaikaMax = hakuaika.loppuPvm;
                    hakuaika = $scope.getSelectedHakuaika();
                    $scope.model.hakuaikaMin = hakuaika.alkuPvm;
                    $scope.model.hakuaikaMax = hakuaika.loppuPvm;
                }
                else if (toteutustyyppi === 'KORKEAKOULUTUS') {
                    $scope.model.configurableHakuaika = !(oph.removeKoodiVersion(haku.hakutapaUri) === 'hakutapa_01'
                        && oph.removeKoodiVersion(haku.hakutyyppiUri) === 'hakutyyppi_01');
                    hakuaika = $scope.getSelectedHakuaika();
                    if ($scope.model.hakukohde.hakuaikaId !== hakuaika.hakuaikaId) {
                        $scope.model.hakukohde.hakuaikaId = hakuaika.hakuaikaId;
                    }
                    $scope.model.hakuaikaMin = hakuaika.alkuPvm;
                    $scope.model.hakuaikaMax = hakuaika.loppuPvm;
                }
                else if (toteutustyyppi === 'KORKEAKOULUOPINTO') {
                    $scope.model.configurableHakuaika = true;
                }

                var hasAlreadySavedCustomHakuaika = $scope.model.hakukohde.hakuaikaAlkuPvm ||
                    $scope.model.hakukohde.hakuaikaLoppuPvm;

                if (!hasAlreadySavedCustomHakuaika &&
                    _.contains($scope.CONFIGURATION.disableConfigurableHakuaika, toteutustyyppi)) {
                    $scope.model.configurableHakuaika = false;
                }
                if (!$scope.model.configurableHakuaika) {
                    $scope.clearHakuajat();
                }
            }
        };
        var getHakuaikaForToisenAsteenKoulutus = function(haku) {
            return haku.hakuaikas[0];
        };
        $scope.retrieveHakus = function() {
            var promises = [HakuService.getAllHakus(), $scope.config.getHaunKohdejoukko()];
            $q.all(promises).then(function(resolved) {
                var hakuDatas = resolved[0];
                var haunKohdejoukkoUris = resolved[1].uris;
                $scope.model.hakus = [];
                var userLang = AuthService.getLanguage();
                var hakuLang = userLang !== undefined ? userLang : $scope.model.defaultLang;
                angular.forEach(hakuDatas, function(haku) {
                    haku.lokalisoituNimi = haku.nimi['kieli_' + hakuLang];
                    // Jos ei löydy kälin kielellä => näytä jollain kielellä
                    if (!haku.lokalisoituNimi) {
                        haku.lokalisoituNimi = _.chain(haku.nimi).compact().first().value();
                    }
                });
                var selectedHaku;
                // Get selected haku if one is defined that must be shown
                // even if the filtering does not show it
                if ($scope.model.hakukohde.hakuOid) {
                    selectedHaku = _.findWhere(hakuDatas, {oid: $scope.model.hakukohde.hakuOid});
                }
                var filteredHakus = filterHakuWithParams(
                    $scope.filterHakusByKohdejoukkoAndOrgs(hakuDatas, haunKohdejoukkoUris)
                );
                if (selectedHaku && !_.findWhere(filteredHakus, {oid: selectedHaku.oid})) {
                    filteredHakus.push(selectedHaku);
                }
                $scope.model.hakus = filteredHakus;
                if ($scope.model.hakukohde.hakuOid !== undefined && $scope.model.hakuChanged) {
                    $scope.model.hakuChanged();
                }
                $scope.handleConfigurableHakuaika();
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
        /**
         * Suorita model.hakuChanged() jos haku-valinta muuttuu tai kun model.hakus
         * on asetettu (tämä ei ole saatavilla heti, tästä syystä watchi)
         */
        $scope.$watch(function() {
            return $scope.model.hakus.length && $scope.model.hakukohde.hakuOid;
        }, function(oid) {
            if (oid) {
                $scope.model.hakuChanged();
            }
        });
        $scope.resolveLocalizedValue = function(key) {
            var userKieliUri = LocalisationService.getKieliUri();
            return key[userKieliUri] || key.kieli_fi || key.kieli_sv || key.kieli_en || '[Ei nime\xE4]';
        };
        $scope.model.hakuChanged = function() {
            if ($scope.model.hakukohde.hakuOid !== undefined) {
                $scope.model.hakuaikas.splice(0, $scope.model.hakuaikas.length);
                var haku = $scope.getHakuWithOid($scope.model.hakukohde.hakuOid);
                if (haku.hakuaikas.length > 1) {
                    angular.forEach(haku.hakuaikas, function(hakuaika) {
                        var formattedStartDate = $scope.createFormattedDateString(hakuaika.alkuPvm);
                        var formattedEndDate = $scope.createFormattedDateString(hakuaika.loppuPvm);
                        hakuaika.formattedNimi = $scope.resolveLocalizedValue(hakuaika.nimet)
                            + ', ' + formattedStartDate + ' - ' + formattedEndDate;
                        $scope.model.hakuaikas.push(hakuaika);
                    });
                    $scope.model.showHakuaikas = true;
                }
                else {
                    var hakuaika = _.first(haku.hakuaikas);
                    $scope.model.hakuaikas.push(hakuaika);
                    $scope.model.hakukohde.hakuaikaId = hakuaika.hakuaikaId;
                    $scope.model.showHakuaikas = false;
                }
                $scope.handleConfigurableHakuaika();
                $scope.updateKaytaHaunPaattymisenAikaa($scope.model.useHaunPaattymisaikaForLiitteidenToimitusPvm);

                // Kun luodaan uutta ja virkailija ei ole koskenut arvoon => ota arvo haulta
                if (!$scope.model.hakukohde.oid && !$scope.model.yoHakukelpoisuusDirty) {
                    $scope.model.hakukohde.ylioppilastutkintoAntaaHakukelpoisuuden =
                        haku.ylioppilastutkintoAntaaHakukelpoisuuden;
                }
            }
        };
        $scope.filterHakusWithOrgs = function(hakus) {
            var filteredHakuArray = [];
            angular.forEach(hakus, function(haku) {
                // $log.info('HAKU ORGOID: ', haku.organisaatioOids);
                if (haku.organisaatioOids && haku.organisaatioOids.length > 0) {
                    if (checkIfOrgMatches(haku.organisaatioOids) || checkIfParentOrgMatches(haku)) {
                        filteredHakuArray.push(haku);
                    }
                }
                else {
                    filteredHakuArray.push(haku);
                }
            });
            return filteredHakuArray;
        };
        $scope.filterHakusByKohdejoukkoAndOrgs = function(hakus, haunKohdejoukot) {
            hakus = $scope.filterHakusWithOrgs(hakus);
            return _.filter(hakus, function(haku) {
                if (_.contains(haunKohdejoukot, window.oph.removeKoodiVersion(haku.kohdejoukkoUri))) {
                    if (haku.koulutusmoduuliTyyppi
                        && haku.koulutusmoduuliTyyppi !== $scope.model.koulutusmoduuliTyyppi) {
                        return;
                    }
                    if (haku.hakutapaUri.indexOf(HAKUTAPA.JATKUVA_HAKU) !== -1) {
                        var loppuPvms = _.pluck(haku.hakuaikas, 'loppuPvm');
                        var hasEmptyLoppuPvms = _.compact(loppuPvms).length < loppuPvms.length;
                        if (hasEmptyLoppuPvms || _.max(loppuPvms) > new Date().getTime()) {
                            return true;
                        }
                    }
                    else if (haku.koulutuksenAlkamiskausiUri === $scope.koulutusKausiUri
                        && haku.koulutuksenAlkamisVuosi === $scope.model.koulutusVuosi) {
                        return true;
                    }
                }
            });
        };
        $scope.getTarjoajaParentPathsAndHakus = function(tarjoajaOids) {
            var orgPromises = [];
            angular.forEach(tarjoajaOids, function(tarjoajaOid) {
                var orgPromise = CommonUtilService.haeOrganisaationTiedot(tarjoajaOid);
                orgPromises.push(orgPromise);
            });
            $q.all(orgPromises).then(function(orgs) {
                angular.forEach(orgs, function(org) {
                    if (org.parentOidPath) {
                        angular.forEach(org.parentOidPath.split('|'), function(parentOid) {
                            if (parentOid.length > 1) {
                                parentOrgOids.add(parentOid);
                            }
                        });
                    }
                });
                $scope.retrieveHakus();
            });
        };
        $scope.tryGetParentsApplicationOffice = function(currentOrg) {
            var isOppilaitos = false;
            var isKoulutusToimija = false;
            var oppilaitosTyyppi = 'Oppilaitos';
            var koulutusToimijaTyyppi = 'Koulutustoimija';
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
                        }
                        else {
                            deferredOsoite.resolve($scope.model.liitteidenToimitusOsoite);
                        }
                    });
                }
                else {
                    deferredOsoite.resolve($scope.model.liitteidenToimitusOsoite);
                }
            }
            else {
                deferredOsoite.resolve($scope.model.liitteidenToimitusOsoite);
            }
        };
        $scope.removeEmptyKuvaukses = function() {
            var langKey;
            for (langKey in $scope.model.hakukohde.valintaperusteKuvaukset) {
                if ($scope.model.hakukohde.valintaperusteKuvaukset[langKey].length < 1) {
                    delete $scope.model.hakukohde.valintaperusteKuvaukset[langKey];
                }
            }
            for (langKey in $scope.model.hakukohde.soraKuvaukset) {
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
                var nkuvausTunniste;
                for (var i in kuvaukset) {
                    var kuvaus = kuvaukset[i];
                    nkuvausTunniste = kuvaus.toimintoTyyppi == 'link' ? kuvaus.tunniste : undefined;
                    nkuvaukset[kuvaus.kieliUri] = kuvaus.teksti;
                    nkuvausKielet.push(kuvaus.kieliUri);
                }
                if (type === 'valintaperustekuvaus') {
                    $scope.model.hakukohde.valintaperusteKuvaukset = nkuvaukset;
                    $scope.model.hakukohde.valintaPerusteKuvausKielet = nkuvausKielet;
                    $scope.model.hakukohde.valintaPerusteKuvausTunniste = nkuvausTunniste;
                    $scope.setSelectedValintaPerusteKuvausByTunniste();
                }
                else if (type === 'SORA') {
                    $scope.model.hakukohde.soraKuvaukset = nkuvaukset;
                    $scope.model.hakukohde.soraKuvausKielet = nkuvausKielet;
                    $scope.model.hakukohde.soraKuvausTunniste = nkuvausTunniste;
                    $scope.setSelectedSoraKuvausByTunniste();
                }
                else {
                    throw '\'valintaperustekuvaus\' | \'SORA\' != ' + type;
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
            $scope.model.hakukelpoisuusVaatimusPromise = Koodisto.getAllKoodisWithKoodiUri(
                'pohjakoulutusvaatimuskorkeakoulut',
                AuthService.getLanguage()
            );
        };
        $scope.loadPainotettavatOppiainevaihtoehdot = function() {
            var painotettavatOppiaineet = [];
            var painotettavatOppiaineetLukiossaPromise = Koodisto.getAllKoodisWithKoodiUri(
                'painotettavatoppiaineetlukiossa',
                AuthService.getLanguage()
            );
            painotettavatOppiaineetLukiossaPromise.then(function(painotettavatOppiaineetLukiossa) {
                for (var i in painotettavatOppiaineetLukiossa) {
                    var uri = painotettavatOppiaineetLukiossa[i].koodiUri;
                    var nimi = painotettavatOppiaineetLukiossa[i].koodiNimi;
                    var versio = painotettavatOppiaineetLukiossa[i].koodiVersio;
                    var painotettavatOppiaine = {
                        oppiaineUri: uri + '#' + versio,
                        lokalisoituNimi: nimi
                    };
                    painotettavatOppiaineet.push(painotettavatOppiaine);
                }
                $scope.painotettavatOppiaineet = painotettavatOppiaineet;
            });
        };
        $scope.enableOrDisableTabs = function() {
            if ($scope.model.hakukohde !== undefined && $scope.model.hakukohde.oid !== undefined) {
                $scope.model.hakukohdeTabsDisabled = false;
            }
            else {
                $scope.model.hakukohdeTabsDisabled = true;
            }
        };
        $scope.isHakukohdeRootScope = function(scope) {
            return scope == $scope;
        };
        function isDirty() {
            return $scope.modelInitialState &&
                !_.isEqual(angular.copy($scope.model.hakukohde), $scope.modelInitialState);
        }
        $scope.setInitialState = function(state) {
            $scope.modelInitialState = state;
        };
        $scope.model.takaisin = function(confirm) {
            if (!confirm && isDirty()) {
                dialogService.showModifedDialog().result.then(function(result) {
                    if (result) {
                        $scope.model.takaisin(true);
                    }
                });
            }
            else {
                $location.path('/etusivu');
            }
        };
        $scope.model.tarkastele = function(confirm) {
            if (!confirm && isDirty()) {
                dialogService.showModifedDialog().result.then(function(result) {
                    if (result) {
                        $scope.model.tarkastele(true);
                    }
                });
            }
            else {
                $location.path('/hakukohde/' + $scope.model.hakukohde.oid);
            }
        };
        $scope.haeValintaPerusteKuvaus = function() {
            $scope.naytaHaeValintaperusteKuvaus('valintaperustekuvaus');
        };
        $scope.haeSora = function() {
            $scope.naytaHaeValintaperusteKuvaus('SORA');
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
        $scope.toimitusosoiteIsEmpty = function() {
            return _.isEmpty($scope.model.liitteidenToimitusOsoite);
        };
        var setToimitusOsoiteFromOrganisaatio = function() {
            if ($scope.model.liitteidenToimitusOsoite.kieli_fi !== undefined) {
                $scope.model.hakukohde.liitteidenToimitusOsoite = angular.copy(
                    $scope.model.liitteidenToimitusOsoite.kieli_fi
                );
            }
            else if ($scope.model.liitteidenToimitusOsoite.kieli_sv !== undefined) {
                $scope.model.hakukohde.liitteidenToimitusOsoite = angular.copy(
                    $scope.model.liitteidenToimitusOsoite.kieli_sv
                );
            }
            else if ($scope.model.liitteidenToimitusOsoite.kieli_en !== undefined) {
                $scope.model.hakukohde.liitteidenToimitusOsoite = angular.copy(
                    $scope.model.liitteidenToimitusOsoite.kieli_en
                );
            }
            else {
                $scope.model.hakukohde.liitteidenToimitusOsoite = {};
            }
        };
        var osoiteIsMatch = function(first, second) {
            return first && second && first.osoiterivi1 === second.osoiterivi1 &&
                first.postinumero === second.postinumero;
        };
        var liitteidenToimitusOsoiteIsMatchFromOrganisaatio = function() {
            var selectedOsoite = $scope.model.hakukohde.liitteidenToimitusOsoite;
            var osoiteFi = $scope.model.liitteidenToimitusOsoite.kieli_fi;
            var osoiteSv = $scope.model.liitteidenToimitusOsoite.kieli_sv;
            var osoiteEn = $scope.model.liitteidenToimitusOsoite.kieli_en;
            return osoiteIsMatch(osoiteFi, selectedOsoite) || osoiteIsMatch(osoiteSv, selectedOsoite) ||
                osoiteIsMatch(osoiteEn, selectedOsoite);
        };
        /**
             * Tallenna modelin tila ennen käyttäjän tekemiä muutoksia, jotta
             * voidaan tarvittaessa ilmoittaa tallentamattomista tiedoista jne.
             */
        $scope.setDirtyListener = function() {
            $('body').on('focus mouseenter', '#editHakukohde .tab-content:first *', function(e) {
                e.stopPropagation();
                if (!$scope.modelInitialState) {
                    $scope.setInitialState(angular.copy($scope.model.hakukohde));
                }
            });
        };
        $scope.setDirtyListener();
        $scope.handleKaksoistutkintoCheckbox = function() {
            if ($scope.model.hakukohde.toteutusTyyppi === 'AMMATILLINEN_PERUSTUTKINTO') {
                if ($scope.model.hakukohde.isNew) {
                    var koulutusOid = _.first($scope.model.hakukohde.hakukohdeKoulutusOids);
                    TarjontaService.getKoulutusPromise(koulutusOid).then(function(response) {
                        var pohjakoulutusvaatimus = response.result.pohjakoulutusvaatimus;
                        $scope.model.kaksoistutkintoIsPossible =
                            pohjakoulutusvaatimus.uri === 'pohjakoulutusvaatimustoinenaste_pk';
                    });
                }
                else {
                    var pkVaatimus = _.find($scope.model.hakukohde.hakukelpoisuusvaatimusUris, function(vaatimusUri) {
                        return vaatimusUri.indexOf('hakukelpoisuusvaatimusta_1') !== -1;
                    });
                    $scope.model.kaksoistutkintoIsPossible = pkVaatimus !== undefined;
                }
            }
            else {
                $scope.model.kaksoistutkintoIsPossible = false;
            }
        };
        $scope.handleKaksoistutkintoCheckbox();
        $scope.replaceLiitteidenToimitusOsoiteWithDefault = function() {
            setToimitusOsoiteFromOrganisaatio();
        };
        $scope.initHakukohdeLiitteidenToimitusOsoite = function() {
            if (_.isEmpty($scope.model.hakukohde.liitteidenToimitusOsoite)) {
                setToimitusOsoiteFromOrganisaatio();
            }
            if (liitteidenToimitusOsoiteIsMatchFromOrganisaatio()) {
                $scope.model.liitteidenMuuOsoiteEnabled = false;
            }
            else {
                $scope.model.liitteidenMuuOsoiteEnabled = true;
            }
        };
        $scope.isValidHakukohdeToimitusOsoite = function() {
            return !$scope.model.liitteidenMuuOsoiteEnabled ||
                !_.isEmpty($scope.model.hakukohde.liitteidenToimitusOsoite.osoiterivi1) &&
                !_.isEmpty($scope.model.hakukohde.liitteidenToimitusOsoite.postinumero);
        };
        function validUrl(url) {
            var pattern = new RegExp('^(https?:\\/\\/)?' + '((([a-z\\d]([a-z\\d-]*[a-z\\d])*)\\.)+[a-z]{2,}|'
                + '((\\d{1,3}\\.){3}\\d{1,3}))' + '(\\:\\d+)?(\\/[-a-z\\d%_.~+]*)*' + '(\\?[;&a-z\\d%_.~+=-]*)?'
                + '(\\#[-a-z\\d_]*)?$', 'i');
            return pattern.test(url);
        }
        function validEmail(email) {
            var pattern = new RegExp('[a-z0-9!#$%&\'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&\'*+/=?^_`{|}~-]+)*'
                            + '@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?');
            return pattern.test(email);
        }
        $scope.liitteidenSahkoinenOsoiteEnabledChanged = function() {
            if (!$scope.model.liitteidenSahkoinenOsoiteEnabled) {
                $scope.model.hakukohde.sahkoinenToimitusOsoite = '';
            }
        };
        $scope.isValidHakukohdeSahkoinenOsoite = function() {
            if ($scope.model.liitteidenSahkoinenOsoiteEnabled) {
                return validUrl($scope.model.hakukohde.sahkoinenToimitusOsoite) ||
                    validEmail($scope.model.hakukohde.sahkoinenToimitusOsoite);
            }
            else {
                return true;
            }
        };
        var checkAndAddHakutoimisto = function(data) {
            var hakutoimistoFound = false;
            if (data.metadata !== undefined && data.metadata.yhteystiedot !== undefined) {
                angular.forEach(data.metadata.yhteystiedot, function(yhteystieto) {
                    if (yhteystieto.osoiteTyyppi !== undefined && yhteystieto.osoiteTyyppi === 'posti') {
                        var kieliUris = yhteystieto.kieli.split('#');
                        var kieliUri = kieliUris[0];
                        $scope.model.liitteidenToimitusOsoite[kieliUri] = {};
                        $scope.model.liitteidenToimitusOsoite[kieliUri].osoiterivi1 = yhteystieto.osoite;
                        $scope.model.liitteidenToimitusOsoite[kieliUri].postinumero = yhteystieto.postinumeroUri;
                        $scope.model.liitteidenToimitusOsoite[kieliUri].postitoimipaikka = yhteystieto.postitoimipaikka;
                        $scope.initHakukohdeLiitteidenToimitusOsoite();
                        hakutoimistoFound = true;
                    }
                });
            }
            return hakutoimistoFound;
        };
        var addKielet = function(koodistoKielet) {
            $scope.model.opetusKielet = [];
            $scope.model.koodistoKielet = [];
            _.each($scope.model.hakukohde.opetusKielet, function(opetuskieliUri) {
                var kieliData = _.find(koodistoKielet, function(element) {
                    return element.koodiUri === opetuskieliUri;
                });
                $scope.model.opetusKielet.push(kieliData);
            });
            $scope.model.koodistoKielet = koodistoKielet;
        };
        var getLangTitle = function(kieliUri) {
            var kieli = _.find($scope.model.koodistoKielet, function(element) {
                return element.koodiUri === kieliUri;
            });
            return kieli.koodiNimi;
        };
        var addOrganisaationYhteystiedot = function(organisaatioData) {
            $scope.model.organisaationYhteystiedot = [];

            function convertForeignAddress(addressText) {
                addressText = addressText || '';
                var rows = addressText.split('\n');
                var postiParts = (rows[1] || '').split(' ');
                var postinumero = (postiParts[0] || '').replace(/[^0-9]/g, '');
                var postitoimipaikka = postiParts[1] || '';
                return {
                    osoiterivi1: rows[0],
                    postinumero: 'posti_' + postinumero,
                    postitoimipaikka: postitoimipaikka
                };
            }

            if (organisaatioData.metadata && organisaatioData.metadata.yhteystiedot) {
                var kayntiYhteystietos = {};

                angular.forEach(organisaatioData.metadata.yhteystiedot, function(yhteystieto) {
                    var kieliUri = oph.removeKoodiVersion(yhteystieto.kieli);
                    var newYhteystieto;
                    if (_.contains(['posti'], yhteystieto.osoiteTyyppi)) {
                        newYhteystieto = {
                            lang: kieliUri,
                            osoiterivi1: yhteystieto.osoite,
                            postinumero: yhteystieto.postinumeroUri,
                            postitoimipaikka: yhteystieto.postitoimipaikka
                        };
                        $scope.model.organisaationYhteystiedot.push(newYhteystieto);
                    }
                    else if (_.contains(['ulkomainen_posti'], yhteystieto.osoiteTyyppi)) {
                        newYhteystieto = convertForeignAddress(yhteystieto.osoite);
                        newYhteystieto.lang = kieliUri;
                        $scope.model.organisaationYhteystiedot.push(newYhteystieto);
                    }
                    else if (_.contains(['kaynti'], yhteystieto.osoiteTyyppi)
                                && !kayntiYhteystietos[kieliUri]) {
                        kayntiYhteystietos[kieliUri] = {
                            osoiterivi1: yhteystieto.osoite,
                            postinumero: yhteystieto.postinumeroUri,
                            postitoimipaikka: yhteystieto.postitoimipaikka
                        };
                    }
                    else if (_.contains(['ulkomainen_kaynti'], yhteystieto.osoiteTyyppi)
                        && !kayntiYhteystietos[kieliUri]) {
                        kayntiYhteystietos[kieliUri] = convertForeignAddress(yhteystieto.osoite);
                    }
                });

                var additionalFields = {
                    puhelinnumero: {
                        find: function(yhteystieto) {
                            return yhteystieto.tyyppi === 'puhelin';
                        },
                        key: 'numero'
                    },
                    sahkopostiosoite: {
                        find: function(yhteystieto) {
                            return yhteystieto.email;
                        },
                        key: 'email'
                    },
                    wwwOsoite: {
                        find: function(yhteystieto) {
                            return yhteystieto.www;
                        },
                        key: 'www'
                    }
                };

                _.each(additionalFields, function(mappedField, key) {
                    var yhteystieto = _.find(organisaatioData.metadata.yhteystiedot, function(yhteystieto) {
                        return mappedField.find(yhteystieto);
                    });
                    if (yhteystieto) {
                        var postiYhteystieto = _.findWhere($scope.model.organisaationYhteystiedot, {
                            lang: oph.removeKoodiVersion(yhteystieto.kieli)
                        });
                        if (postiYhteystieto) {
                            postiYhteystieto[key] = yhteystieto[mappedField.key];
                        }
                    }
                });

                _.each(kayntiYhteystietos, function(kayntiYhteystieto, lang)  {
                    var postiYhteystieto = _.findWhere($scope.model.organisaationYhteystiedot, {lang: lang});
                    if (postiYhteystieto) {
                        postiYhteystieto.kayntiosoite = kayntiYhteystieto;
                    }
                });

                _.each(organisaatioData.metadata.hakutoimistonNimi, function(nimi, kieliUri) {
                    var lang = oph.removeKoodiVersion(kieliUri);
                    var postiYhteystieto = _.findWhere($scope.model.organisaationYhteystiedot, {lang: lang});
                    if (postiYhteystieto) {
                        postiYhteystieto.hakutoimistonNimi = nimi;
                    }
                });

            }
        };
        var getOrganisaatioOsoiteByKieliUri = function(kieliUri) {
            return _.findWhere($scope.model.organisaationYhteystiedot, {
                lang: kieliUri
            });
        };
        var getHakukohteenYhteystietoByKieliUri = function(kieliUri) {
            return _.find($scope.model.hakukohde.yhteystiedot, function(hakukohteenYhteystieto) {
                return hakukohteenYhteystieto.lang === kieliUri;
            });
        };
        $scope.resetYhteystiedonOrganisaatioOsoite = function(yhteystieto) {
            var organisaatioOsoite = getOrganisaatioOsoiteByKieliUri(yhteystieto.lang);
            _.extend(yhteystieto, organisaatioOsoite, {
                kaytaOrganisaatioOsoitetta: true
            });
        };
        var populateHakukohteenYhteystiedot = function() {
            _.each($scope.model.hakukohde.yhteystiedot, function(yhteystieto) {
                var organisaatioOsoite = getOrganisaatioOsoiteByKieliUri(yhteystieto.lang);
                yhteystieto.langTitle = getLangTitle(yhteystieto.lang);
                yhteystieto.koodiUri = yhteystieto.lang;
                yhteystieto.organisaatioOsoiteOlemassa = organisaatioOsoite !== undefined;
                yhteystieto.kaytaOrganisaatioOsoitetta = false;
            });
            _.each($scope.model.opetusKielet, function(opetuskieli) {
                var newYhteystieto;
                var existingYhteystieto = getHakukohteenYhteystietoByKieliUri(opetuskieli.koodiUri);
                if (!existingYhteystieto) {
                    var opetuskielenOrganisaationYhteystiedot = getOrganisaatioOsoiteByKieliUri(opetuskieli.koodiUri);
                    if (opetuskielenOrganisaationYhteystiedot) {
                        newYhteystieto = _.extend({}, opetuskielenOrganisaationYhteystiedot, {
                            koodiUri: opetuskieli.koodiUri,
                            langTitle: opetuskieli.koodiNimi,
                            organisaatioOsoiteOlemassa: true,
                            kaytaOrganisaatioOsoitetta: true
                        });
                        $scope.model.hakukohde.yhteystiedot.push(newYhteystieto);
                    }
                    else {
                        newYhteystieto = {
                            lang: opetuskieli.koodiUri,
                            langTitle: opetuskieli.koodiNimi,
                            koodiUri: opetuskieli.koodiUri,
                            organisaatioOsoiteOlemassa: false,
                            kaytaOrganisaatioOsoitetta: false
                        };
                        $scope.model.hakukohde.yhteystiedot.push(newYhteystieto);
                    }
                }
            });
            $scope.model.hakukohde.yhteystiedot.sort($scope.sortLanguageTabs);
        };
        var handleHakukohteenYhteystiedot = function(organisaatioData) {
            if ($scope.CONFIGURATION.YHTEYSTIEDOT.showYhteystiedot[$scope.model.hakukohde.toteutusTyyppi]) {
                Koodisto.getAllKoodisWithKoodiUri('kieli', LocalisationService.getLocale()).then(function(ret) {
                    addKielet(ret);
                    addOrganisaationYhteystiedot(organisaatioData);
                    populateHakukohteenYhteystiedot();
                });
            }
        };
        var reloadHakukohdeModel = function() {
            $scope.$broadcast('addEmptyLitteet');
            $scope.$broadcast('addEmptyValintakokeet');
            $scope.$broadcast('reloadValintakokeet');
            if ($scope.CONFIGURATION.YHTEYSTIEDOT.showYhteystiedot[$scope.model.hakukohde.toteutusTyyppi]) {
                populateHakukohteenYhteystiedot();
            }
        };
        function initValintaperusteAndSoraKuvaukset() {
            if ($scope.model.hakukohde.valintaperusteKuvaukset === undefined) {
                $scope.model.hakukohde.valintaperusteKuvaukset = {};
            }
            if ($scope.model.hakukohde.soraKuvaukset === undefined) {
                $scope.model.hakukohde.soraKuvaukset = {};
            }
        }
        $scope.model.saveParent = function(tila) {
            if (!tila) {
                throw 'tila cannot be undefined!';
            }
            $scope.model.hakukohde.toteutusTyyppi = $scope.model.hakukohde.toteutusTyyppi ||
                SharedStateService.getFromState('SelectedToteutusTyyppi');
            $scope.model.showError = false;
            PermissionService.permissionResource().authorize({}, function() {
                $scope.emptyErrorMessages();
                HakukohdeService.removeEmptyLiites($scope.model.hakukohde.hakukohteenLiitteet);
                var errors = ValidatorService.hakukohde.validate(
                    $scope.model,
                    $scope.getHakuByOid($scope.model.hakukohde.hakuOid));
                if (errors.length === 0 && $scope.editHakukohdeForm.$valid) {
                    HakukohdeService.removeNotUsedYhteystiedot($scope.model.hakukohde.yhteystiedot);
                    updateTila(tila);
                    $scope.model.hakukohde.modifiedBy = AuthService.getUserOid();
                    $scope.removeEmptyKuvaukses();
                    angular.forEach($scope.model.hakukohde.hakukohteenLiitteet, function(liite, index) {
                        liite.jarjestys = index;
                    });
                    $scope.checkIsCopy($scope.luonnosVal);
                    if ($scope.model.hakukohde.oid === undefined) {
                        // KJOH-778, pitää tietää mille organisaatiolle ollaan luomassa hakukohdetta
                        var tarjoajatiedot = {};
                        angular.forEach($scope.hakukohdex.hakukohdeKoulutusOids, function(komotoOid) {
                            tarjoajatiedot[komotoOid] = {
                                tarjoajaOids: [AuthService.getUserDefaultOid()]
                            };
                        });
                        $scope.model.hakukohde.koulutusmoduuliToteutusTarjoajatiedot = tarjoajatiedot;
                        $scope.model.hakukohde.$save().then(function(hakukohde) {
                            $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                            initValintaperusteAndSoraKuvaukset();
                            reloadHakukohdeModel();
                            if (hakukohde.errors === undefined || hakukohde.errors.length < 1) {
                                $scope.model.hakukohdeOid = $scope.model.hakukohde.oid;
                                $scope.showSuccess();
                                $scope.checkIfSavingCopy($scope.model.hakukohde);
                            }
                            else {
                                $scope.showError(hakukohde.errors);
                            }
                            $scope.canEdit = true;
                            $scope.model.continueToReviewEnabled = true;
                            $scope.status.dirty = false;
                            $scope.modelInitialState = null;
                        }, function(error) {
                            $log.debug('ERROR INSERTING HAKUKOHDE : ', error);
                            $scope.showCommonUnknownErrorMsg();
                        });
                    }
                    else {
                        $scope.model.hakukohde.$update().then(function(hakukohde) {
                            $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                            initValintaperusteAndSoraKuvaukset();
                            reloadHakukohdeModel();
                            $scope.handleConfigurableHakuaika();
                            if (hakukohde.status === 'OK') {
                                $scope.status.dirty = false;
                                $scope.showSuccess();
                            }
                            else {
                                $scope.showError(hakukohde.errors);
                            }
                            $scope.modelInitialState = null;
                        }, function(error) {
                            $log.debug('EXCEPTION UPDATING HAKUKOHDE: ', error);
                            $scope.showCommonUnknownErrorMsg();
                        });
                    }
                }
                else {
                    $scope.model.nimiValidationFailed = _.findWhere(errors, {
                        errorMessageKey: 'hakukohde.edit.nimi.missing'
                    }) !== undefined;
                    $scope.showError(errors);
                    $scope.$broadcast('addEmptyLitteet');
                    $scope.$broadcast('addEmptyValintakokeet');
                }
            });
        };
        var processPermissions = function(resourcePermissions) {
            $log.info('PROCESSPERMISSIONS : ', resourcePermissions);
            if (resourcePermissions.hakukohde && resourcePermissions.hakukohde.update
                && resourcePermissions.hakukohde.updateLimited) {
                $log.info('TTKPP PARTIAL UPDATE');
                $scope.model.isDeEnabled = true;
                $scope.model.isPartiallyDeEnabled = false;
            }
            else if (resourcePermissions.hakukohde && resourcePermissions.hakukohde.update
                && !resourcePermissions.hakukohde.updateLimited) {
                $log.info('TTKPP FULL UPDATE');
                $scope.model.isDeEnabled = false;
                $scope.model.isPartiallyDeEnabled = false;
            }
            else if (resourcePermissions.hakukohde && !resourcePermissions.hakukohde.update) {
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
            var permissionPromise = PermissionService.getPermissions('hakukohde', hakukohdeOid);
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
                    var splitStr = oppilaitosTyyppiUri.split('#');
                    oppilaitosTyyppisWithOutVersion.push(splitStr[0]);
                });
            });
            return oppilaitosTyyppisWithOutVersion;
        };
        $scope.splitUri = function(uri) {
            var tokenizedArray = uri.split('#');
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
        $scope.fnTemp = function() {};
        $scope.temp = null;

        $scope.isOpinto = function(hakukohde) {
            var toteutusTyyppi = hakukohde.toteutusTyyppi;
            if (!toteutusTyyppi) {
                toteutusTyyppi = SharedStateService.getFromState('SelectedToteutusTyyppi');
            }
            if (!toteutusTyyppi) {
                $log.error('Cannot determine toteutusTyyppi', hakukohde);
                throw new Error('Cannot determine toteutusTyyppi');
            }
            return toteutusTyyppi === 'KORKEAKOULUOPINTO';
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
        var initLangs = function() {
            var map = {};
            angular.forEach(window.CONFIG.app.userLanguages, function(val) {
                map[val] = val;
            });
            angular.forEach($scope.model.hakukohde.opetusKielet, function(val) {
                map[val] = val;
            });
            angular.forEach(map, function(val, key) {
                var lang = {'koodi_uri': val};
                $scope.searchKoodi(lang, window.CONFIG.env['koodisto-uris.kieli'], key, $scope.model.koodistoLocale);
                $scope.model.languages.push(lang);
            });
        };
        initLangs();

        $scope.addPainotettavaOppiaine = function() {
            return HakukohdeService.addPainotettavaOppiaine($scope.model.hakukohde);
        };
        $scope.deletePainotettavaOppiaine = function(painotettavaOppiaine) {
            var p = $scope.model.hakukohde.painotettavatOppiaineet.indexOf(painotettavaOppiaine);
            if (p !== -1) {
                $scope.status.dirty = true;
                $scope.model.hakukohde.painotettavatOppiaineet.splice(p, 1);
                $scope.status.dirtify();
            }
        };
        $scope.getHakuByOid = function(oid) {
            var haku = null;
            angular.forEach($scope.model.hakus, function(element) {
                if (element.oid === oid) {
                    haku = element;
                }
            });
            return haku;
        };
        $scope.updateKaytaHaunPaattymisenAikaa = function(value) {
            if (value === true) {
                var haku = $scope.getHakuWithOid($scope.model.hakukohde.hakuOid);
                var hakuaika = getHakuaikaForToisenAsteenKoulutus(haku);
                $scope.model.hakukohde.liitteidenToimitusPvm = hakuaika.loppuPvm;
            }
        };
        $scope.setSelectedValintaPerusteKuvausByTunniste = function() {
            if ($scope.model.hakukohde.valintaPerusteKuvausTunniste !== undefined) {
                Kuvaus.findKuvausWithId($scope.model.hakukohde.valintaPerusteKuvausTunniste).then(function(data) {
                    $scope.model.selectedValintaperusteKuvaus = data.result;
                    $scope.model.selectedValintaperusteKuvaus.title
                        = data.result.kuvauksenNimet['kieli_' + AuthService.getLanguage().toLowerCase()];
                    if ($scope.model.selectedValintaperusteKuvaus.title === undefined) {
                        $scope.model.selectedValintaperusteKuvaus.title = Object.keys(data.result.kuvauksenNimet)[0];
                    }
                });
            }
            else {
                $scope.model.selectedValintaperusteKuvaus = undefined;
            }
        };
        $scope.setSelectedSoraKuvausByTunniste = function() {
            if ($scope.model.hakukohde.soraKuvausTunniste !== undefined) {
                Kuvaus.findKuvausWithId($scope.model.hakukohde.soraKuvausTunniste).then(function(data) {
                    $scope.model.selectedSoraKuvaus = data.result;
                    $scope.model.selectedSoraKuvaus.title
                        = data.result.kuvauksenNimet['kieli_' + AuthService.getLanguage().toLowerCase()];
                    if ($scope.model.selectedSoraKuvaus.title === undefined) {
                        $scope.model.selectedSoraKuvaus.title = Object.keys(data.result.kuvauksenNimet)[0];
                    }
                });
            }
            else {
                $scope.model.selectedSoraKuvaus = undefined;
            }
        };
        $scope.setSelectedValintaPerusteKuvausByTunniste();
        $scope.setSelectedSoraKuvausByTunniste();
        $scope.removeValintaperustekuvaus = function() {
            var d = dialogService.showDialog({
                ok: LocalisationService.t('ok'),
                cancel: LocalisationService.t('cancel'),
                title: LocalisationService.t('tarjonta.tyhjenn\xE4ValintaperustekuvausDialogi.otsikko'),
                description: LocalisationService.t('tarjonta.tyhjenn\xE4ValintaperustekuvausDialogi.kuvaus')
            });
            d.result.then(function(data) {
                if (data) {
                    $scope.model.selectedValintaperusteKuvaus = undefined;
                    $scope.model.hakukohde.valintaPerusteKuvausTunniste = undefined;
                    $scope.model.hakukohde.valintaperusteKuvaukset = {};
                    $scope.model.hakukohde.valintaPerusteKuvausKielet = [];
                }
            });
        };
        $scope.removeSoraKuvaus = function() {
            var d = dialogService.showDialog({
                ok: LocalisationService.t('ok'),
                cancel: LocalisationService.t('cancel'),
                title: LocalisationService.t('tarjonta.tyhjenn\xE4SoraKuvausDialogi.otsikko'),
                description: LocalisationService.t('tarjonta.tyhjenn\xE4SoraKuvausDialogi.kuvaus')
            });
            d.result.then(function(data) {
                if (data) {
                    $scope.model.selectedSoraKuvaus = undefined;
                    $scope.model.hakukohde.soraKuvausTunniste = undefined;
                    $scope.model.hakukohde.soraKuvaukset = {};
                    $scope.model.hakukohde.soraKuvausKielet = [];
                }
            });
        };
        $scope.sortLanguageTabs = function(a, b) {
            if (a.koodiUri === 'kieli_fi') {
                return -1;
            }
            else if (a.koodiUri === 'kieli_sv' && b.koodiUri !== 'kieli_fi') {
                return -1;
            }
            else if (a.koodiUri === 'kieli_en' && b.koodiUri !== 'kieli_fi' && b.koodiUri !== 'kieli_sv') {
                return -1;
            }
            else {
                if (b.koodiUri === 'kieli_fi' || b.koodiUri === 'kieli_sv' || b.koodiUri === 'kieli_en') {
                    return 1;
                }
                else {
                    return a.koodiUri.localeCompare(b.koodiUri);
                }
            }
        };
    }
]);