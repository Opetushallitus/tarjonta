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
    //Placeholder for multiselect remove when refactored
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
    var init = function() {
        $log.info('init()');
        $scope.model.userLang = AuthService.getLanguage();
        if ($scope.model.userLang === undefined) {
            $scope.model.userLang = 'FI';
        }
        $log.debug('CHECKING PERMISSIONS : ', $scope.model.hakukohde);
        $scope.checkIsCopy();
        if ($scope.model.hakukohde.oid) {
            $scope.checkPermissions($scope.model.hakukohde.oid);
        }
        $scope.loadHakukelpoisuusVaatimukset();
        $scope.loadKoulutukses();
        $scope.canSaveParam($scope.model.hakukohde.hakuOid);
        $scope.haeTarjoajaOppilaitosTyypit();
        $scope.model.continueToReviewEnabled = $scope.checkJatkaBtn($scope.model.hakukohde);
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

        if ($scope.CONFIGURATION.HAKUKOHDERYHMA.showHakukohderyhmat[$scope.model.hakukohde.toteutusTyyppi]) {
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
            var uri = koulutus.koulutusohjelma.uri ? koulutus.koulutusohjelma.uri : koulutus.koulutuskoodi.uri;
            var pohjakoulutusvaatimus = koulutus.pohjakoulutusvaatimus;

            var currentUri = window.oph.removeKoodiVersion($scope.model.hakukohde.hakukohteenNimiUri || '');

            Koodisto.getAlapuolisetKoodit(uri, AuthService.getLanguage())
            .then(function(koulutusohjelmanKoodit) {
                angular.forEach(koulutusohjelmanKoodit, function(koulutusohjelmanKoodi) {
                    if (koulutusohjelmanKoodi.koodiKoodisto === 'hakukohteet') {
                        Koodisto.getYlapuolisetKoodit(koulutusohjelmanKoodi.koodiUri, AuthService.getLanguage())
                        .then(function(hakukohteenYlapuolisetKoodit) {
                            angular.forEach(hakukohteenYlapuolisetKoodit, function(hakukohteenYlapuolinenKoodi) {
                                if (hakukohteenYlapuolinenKoodi.koodiUri === pohjakoulutusvaatimus.uri) {
                                    var koodi = {
                                        uri: koulutusohjelmanKoodi.koodiUri +
                                            '#' + koulutusohjelmanKoodi.koodiVersio,
                                        label: koulutusohjelmanKoodi.koodiNimi,
                                        uriWithoutVersion: koulutusohjelmanKoodi.koodiUri,
                                        version: parseInt(koulutusohjelmanKoodi.koodiVersio)
                                    };

                                    // Default index -> append new item
                                    var index = $scope.model.hakukohteenNimet.length;

                                    var sameKoodi = _.find($scope.model.hakukohteenNimet, function(obj, i) {
                                        if (obj.uriWithoutVersion === koodi.uriWithoutVersion) {
                                            index = i; // Prev koodi position in array
                                            return true;
                                        }
                                    });

                                    // Same koodi, but older version -> skip
                                    if (sameKoodi && koodi.version < sameKoodi.version) {
                                        return;
                                    }

                                    // Append or replace (depends on index)
                                    $scope.model.hakukohteenNimet[index] = koodi;

                                    // Update model value to newest koodi version
                                    if (currentUri === koodi.uriWithoutVersion) {
                                        $scope.model.hakukohde.hakukohteenNimiUri = currentUri + '#' + koodi.version;
                                    }
                                }
                            });
                        });
                    }
                });
            });
        };
        if ($scope.config.isToisenAsteenKoulutus()) {
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