/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 */
/* Controllers */
var app = angular.module('app.kk.edit.hakukohde.ctrl', [
    'app.services',
    'Haku',
    'Organisaatio',
    'Koodisto',
    'localisation',
    'Hakukohde',
    'auth',
    'config',
    'MonikielinenTextArea',
    'MultiSelect',
    'ngGrid',
    'TarjontaOsoiteField',
    'ExportToParent'
]);
app.controller('HakukohdeEditController', function($scope, $q, $log, LocalisationService, OrganisaatioService,
               Koodisto, Hakukohde, AuthService, HakuService, $route, $modal, Config, $location, $timeout,
               TarjontaService, Kuvaus, CommonUtilService, HAKUTAPA) {
    'use strict';
    $log = $log.getInstance('HakukohdeEditController');
    $scope.model.canSaveHakukohde = function() {
        if ($scope.editHakukohdeForm !== undefined) {
            return $scope.editHakukohdeForm.$valid && $scope.checkCanCreateOrEditHakukohde($scope.model.hakukohde);
        }
        else {
            return false;
        }
    };
    $scope.model.canSaveAsLuonnos = function() {
        if ($scope.model.isDeEnabled && $scope.model.isPartiallyDeEnabled) {
            var canSave = !$scope.model.isDeEnabled;
            $log.info('CanSaveAsLuonnos, parameter says not ok');
            return canSave;
        }
        else {
            $log.info('CanSaveAsLuonnos, parameter says ok. Tila : ', $scope.model.hakukohde.tila);
            var canSaveAsLuonnosByTila = CommonUtilService.canSaveAsLuonnos($scope.model.hakukohde.tila);
            return canSaveAsLuonnosByTila;
        }
    };
    $scope.model.canSaveAsValmis = function() {
        return $scope.model.isDeEnabled && $scope.model.isPartiallyDeEnabled;
    };
    if ($scope.model.hakukohde.lisatiedot !== undefined) {
        angular.forEach($scope.model.hakukohde.lisatiedot, function(lisatieto) {
            $scope.model.selectedKieliUris.push(lisatieto.uri);
        });
    }
    var hakukohdeBeforeSyksy2015 = function() {
        return $scope.model.koulutusVuosi < 2015 ||
            ($scope.koulutusKausiUri.indexOf('kausi_k') !== -1 &&
            $scope.model.koulutusVuosi === 2015);
    };
    var filterHakuWithAikaAndKohdejoukko = function(hakus) {
        var filteredHakus = [];
        angular.forEach(hakus, function(haku) {
            var kohdeJoukkoUriNoVersion = $scope.splitUri(haku.kohdejoukkoUri);
            if (kohdeJoukkoUriNoVersion == window.CONFIG.app['haku.kohdejoukko.kk.uri']) {
                if (haku.koulutuksenAlkamiskausiUri && haku.koulutuksenAlkamisVuosi) {
                    //OVT-6800 --> Rajataan koulutuksen alkamiskaudella ja vuodella
                    if (haku.koulutuksenAlkamiskausiUri === $scope.koulutusKausiUri
                        && haku.koulutuksenAlkamisVuosi === $scope.model.koulutusVuosi) {
                        filteredHakus.push(haku);
                    }
                }
                else {
                    filteredHakus.push(haku);
                }
            }
        });
        return filteredHakus;
    };
    var filterHakusForAmmatillinenAndLukio = function(hakus) {
        return filterHakusByKohdejoukkoAndOrgs(hakus, 'haunkohdejoukko_11#1');
    };
    var filterHakusForAmmatillinenValmistavaAndLisaopetus = function(hakus) {
        return filterHakusByKohdejoukkoAndOrgs(hakus, 'haunkohdejoukko_17#1');
    };
    var filterHakusForValmentavaJaKuntouttavaOpetus = function(hakus) {
        if (hakukohdeBeforeSyksy2015()) {
            return filterHakusByKohdejoukkoAndOrgs(hakus, 'haunkohdejoukko_16#1');
        } else {
            return filterHakusByKohdejoukkoAndOrgs(hakus, 'haunkohdejoukko_20#1');
        }
    };
    var filterHakusForVapaanSivistystyonKoulutus = function(hakus) {
        return filterHakusByKohdejoukkoAndOrgs(hakus, 'haunkohdejoukko_18#1');
    };
    var filterHakusForAmmatillinenPeruskoulutusErityisopetuksena = function(hakus) {
        if (hakukohdeBeforeSyksy2015()) {
            return filterHakusByKohdejoukkoAndOrgs(hakus, 'haunkohdejoukko_15#1');
        } else {
            return filterHakusByKohdejoukkoAndOrgs(hakus, 'haunkohdejoukko_20#1');
        }
    };
    var hakuajanLoppuPvmInFuture = function(hakuaika) {
        return hakuaika.loppuPvm > new Date().getTime();
    };
    var filterHakusByKohdejoukkoAndOrgs = function(hakus, haunKohdejoukko) {
        hakus = $scope.filterHakusWithOrgs(hakus);
        var filteredHakus = [];
        angular.forEach(hakus, function(haku) {
            if (haku.kohdejoukkoUri === haunKohdejoukko) {
                if (haku.hakutapaUri.indexOf(HAKUTAPA.JATKUVA_HAKU) !== -1) {
                    var hakuaika = _.first(haku.hakuaikas);
                    if (hakuaika.loppuPvm !== undefined) {
                        if (hakuajanLoppuPvmInFuture(hakuaika)) {
                            filteredHakus.push(haku);
                        }
                    }
                    else {
                        filteredHakus.push(haku);
                    }
                }
                else if (haku.koulutuksenAlkamiskausiUri === $scope.koulutusKausiUri
                    && haku.koulutuksenAlkamisVuosi === $scope.model.koulutusVuosi) {
                    filteredHakus.push(haku);
                }
            }
        });
        return filteredHakus;
    };
    var filterHakus = function(hakus) {
        return filterHakuWithAikaAndKohdejoukko($scope.filterHakusWithOrgs(hakus));
    };
    //Placeholder for multiselect remove when refactored
    $scope.model.temp = {};
    $scope.model.ryhmaChange = function() {
        $log.info('ryhmaChange()', $scope.model.hakukohde.ryhmatX);
    };
    var loadKoodistoNimi = function() {
        if ($scope.model.hakukohde.hakukohteenNimiUri) {
            Koodisto.searchKoodi($scope.model.hakukohde.hakukohteenNimiUri, AuthService.getLanguage())
            .then(function(data) {
                $scope.model.koodistonimi = data;
            });
        }
    };
    var getHakusFilterFunctionBasedOnToteutusTyyppi = function(toteutusTyyppi) {
        if (toteutusTyyppi === 'AMMATILLINEN_PERUSTUTKINTO' || toteutusTyyppi === 'LUKIOKOULUTUS') {
            return filterHakusForAmmatillinenAndLukio;
        }
        else if (toteutusTyyppi === 'PERUSOPETUKSEN_LISAOPETUS'
            || toteutusTyyppi === 'AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS'
            || toteutusTyyppi === 'MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS'
            || toteutusTyyppi === 'MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS') {
            return filterHakusForAmmatillinenValmistavaAndLisaopetus;
        }
        else if (toteutusTyyppi === 'VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS') {
            return filterHakusForValmentavaJaKuntouttavaOpetus;
        }
        else if (toteutusTyyppi === 'VAPAAN_SIVISTYSTYON_KOULUTUS') {
            return filterHakusForVapaanSivistystyonKoulutus;
        }
        else if (toteutusTyyppi === 'AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA') {
            return filterHakusForAmmatillinenPeruskoulutusErityisopetuksena;
        }
        return filterHakus;
    };
    var init = function() {
        $log.info('init()');
        $scope.model.userLang = AuthService.getLanguage();
        if ($scope.model.userLang === undefined) {
            $scope.model.userLang = 'FI';
        }
        $log.debug('CHECKING PERMISSIONS : ', $scope.model.hakukohde);
        if ($scope.model.hakukohde.oid) {
            $scope.checkPermissions($scope.model.hakukohde.oid);
        }
        $scope.loadHakukelpoisuusVaatimukset();
        $scope.loadKoulutukses(getHakusFilterFunctionBasedOnToteutusTyyppi($scope.model.hakukohde.toteutusTyyppi));
        $scope.canSaveParam($scope.model.hakukohde.hakuOid);
        $scope.haeTarjoajaOppilaitosTyypit();
        $scope.model.continueToReviewEnabled = $scope.checkJatkaBtn($scope.model.hakukohde);
        $scope.checkIsCopy();
        if ($scope.model.hakukohde.hakukelpoisuusVaatimusKuvaukset === undefined) {
            $scope.model.hakukohde.hakukelpoisuusVaatimusKuvaukset = {};
        }
        if ($scope.model.hakukohde.kaytetaanJarjestelmanValintaPalvelua === undefined) {
            $scope.model.hakukohde.kaytetaanJarjestelmanValintaPalvelua = true;
        }

        if ($scope.model.hakukohde.hakuMenettelyKuvaukset === undefined) {
            $scope.model.hakukohde.hakuMenettelyKuvaukset = {};  // korkeakouluopinto
        }

        if ($scope.model.hakukohde.peruutusEhdotKuvaukset === undefined) {
            $scope.model.hakukohde.peruutusEhdotKuvaukset = {};  // korkeakouluopinto
        }

        if ($scope.model.hakukohde.toteutusTyyppi === 'KORKEAKOULUTUS') {
            // Mahdolliset Organisaatiopalvelun hakukohdetyhmät joissa hakukohde voi olla
            // [{ key: XXX, value: YYY}, ...]
            $scope.model.hakukohdeRyhmat = [];
            OrganisaatioService.getRyhmat().then(function(ryhmat) {
                var result = [];
                angular.forEach(ryhmat, function(ryhma) {
                    result.push({
                        key: ryhma.oid,
                        value: ryhma.nimi.fi
                    });
                });
                $scope.model.hakukohdeRyhmat = result;
            });
        }

        // Alusta ryhmälista tyhjäksi jos ei valintoja
        if (!$scope.model.hakukohde.organisaatioRyhmaOids) {
            $scope.model.hakukohde.organisaatioRyhmaOids = [];
        }
        $scope.enableOrDisableTabs();
        loadKoodistoNimi();
        if ($scope.model.hakukohde.toteutusTyyppi === 'LUKIOKOULUTUS') {
            $scope.loadPainotettavatOppiainevaihtoehdot();
        }
        var populateHakukohteenNimetByKoulutus = function(koulutus) {
            var pohjakoulutusvaatimus = koulutus.pohjakoulutusvaatimus;
            Koodisto.getAlapuolisetKoodit(koulutus.koulutusohjelma.uri, AuthService.getLanguage())
            .then(function(koulutusohjelmanKoodit) {
                angular.forEach(koulutusohjelmanKoodit, function(koulutusohjelmanKoodi) {
                    if (koulutusohjelmanKoodi.koodiKoodisto === 'hakukohteet') {
                        Koodisto.getYlapuolisetKoodit(koulutusohjelmanKoodi.koodiUri, AuthService.getLanguage())
                        .then(function(hakukohteenYlapuolisetKoodit) {
                            angular.forEach(hakukohteenYlapuolisetKoodit, function(hakukohteenYlapuolinenKoodi) {
                                if (hakukohteenYlapuolinenKoodi.koodiUri === pohjakoulutusvaatimus.uri) {
                                    var hakukohteenNimi = {
                                        uri: koulutusohjelmanKoodi.koodiUri + '#' + koulutusohjelmanKoodi.koodiVersio,
                                        label: koulutusohjelmanKoodi.koodiNimi
                                    };
                                    $scope.model.hakukohteenNimet.push(hakukohteenNimi);
                                }
                            });
                        });
                    }
                });
            });
        };
        if ($scope.toisenAsteenKoulutus($scope.model.hakukohde.toteutusTyyppi)) {
            $scope.model.hakukohteenNimet = [];
            angular.forEach($scope.model.hakukohde.hakukohdeKoulutusOids, function(koulutusOid) {
                TarjontaService.getKoulutusPromise(koulutusOid).then(function(response) {
                    populateHakukohteenNimetByKoulutus(response.result);
                });
            });
        }
    };
    init();
    $scope.model.kieliCallback = function(kieliUri) {
        if ($scope.model.allkieles !== undefined) {
            var lisatietoFound = false;
            //Check that selected kieli does not exist in list
            angular.forEach($scope.model.hakukohde.lisatiedot, function(lisatieto) {
                if (lisatieto.uri === kieliUri) {
                    lisatietoFound = true;
                }
            });
            if (!lisatietoFound) {
                var foundKoodi = $scope.findKoodiWithUri(kieliUri, $scope.model.allkieles);
                var newLisatieto = {
                    'uri': foundKoodi.koodiUri,
                    'nimi': foundKoodi.koodiNimi,
                    'teksti': ''
                };
                $scope.model.hakukohde.lisatiedot.push(newLisatieto);
            }
        }
    };
    $scope.model.kieliRemoveCallback = function(kieliUri) {
        $scope.removeLisatieto(kieliUri);
    };
    /*

          -----> Checkbox change listener to retrieve hakus end time if selected

       */
    $scope.model.checkboxChange = function() {
        if ($scope.model.hakukohde.kaytetaanHaunPaattymisenAikaa) {
            var haku = $scope.getHakuWithOid($scope.model.hakukohde.hakuOid);
            var hakuPaattymisAika;
            angular.forEach(haku.hakuaikas, function(hakuaika) {
                if (hakuPaattymisAika === undefined) {
                    hakuPaattymisAika = hakuaika.loppuPvm;
                }
                else {
                    if (hakuPaattymisAika < hakuaika.loppuPvm) {
                        hakuPaattymisAika = hakuaika.loppuPvm;
                    }
                }
            });
            if (hakuPaattymisAika !== undefined) {
                $scope.model.hakukohde.liitteidenToimitusPvm = hakuPaattymisAika;
            }
        }
    };
    /*

          ------> Haku combobox listener -> listens to selected haku to check whether it contains inner application periods

       */
    var resolveLocalizedValue = function(key) {
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
                    hakuaika.formattedNimi = resolveLocalizedValue(hakuaika.nimet) + ', '
                                                + formattedStartDate + ' - ' + formattedEndDate;
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
        }
    };
    $scope.model.saveValmis = function() {
        $scope.model.saveParent('VALMIS');
    };
    $scope.model.saveLuonnos = function() {
        $scope.model.saveParent('LUONNOS');
    };
    $scope.$watch(function() {
        return angular.toJson($scope.model.hakukohde.valintaperusteKuvaukset);
    }, function(n, o) {
            if (!angular.equals(n, o) && o != '{}') {
                $scope.status.dirty = true;
            }
        });
    $scope.$watch(function() {
        return angular.toJson($scope.model.hakukohde.soraKuvaukset);
    }, function(n, o) {
            if (!angular.equals(n, o) && o != '{}') {
                $scope.status.dirty = true;
            }
        });
});