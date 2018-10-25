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
            //$log.info('CanSaveAsLuonnos, parameter says ok. Tila : ', $scope.model.hakukohde.tila);
            return CommonUtilService.canSaveAsLuonnos($scope.model.hakukohde.tila);
        }
    };
    $scope.model.hakutapaYhteishaku = false;

    $scope.model.isYhteishaku = function(){
        if ($scope.model.hakukohde.hakuOid != undefined) {
            $scope.model.hakutapaYhteishaku = false;
            angular.forEach($scope.model.hakus, function (h) {
                // yhteushaku valittu
                if (h.oid == $scope.model.hakukohde.hakuOid && (h.hakutapaUri != undefined && h.hakutapaUri.split('#')[0] == 'hakutapa_01')) {
                    $scope.model.hakutapaYhteishaku = true;
                }
            });
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
        $scope.model.opintoOikeusPromise = Koodisto.getAllKoodisWithKoodiUri(
            'opintooikeus',
            AuthService.getLanguage()
        );
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
        var appendOrReplaceHakukohteenNimi = function(currentUri, koulutusohjelmanKoodi) {
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
        };

        //Parsitaan taulukoksi koodit, jotka ovat yhteisiä kaikille hakukohteen koulutuksille
        $scope.model.findCommonNamesForHakukohde = function(nimetByKoulutus) {

            var source = JSON.parse(JSON.stringify(nimetByKoulutus));
            var commonKoodisFound = [];
            var ensimmaisenKoulutuksenKoodit = [];
            var muidenKoulutustenKoodit = [];
            if (source && source.length > 0) {
                ensimmaisenKoulutuksenKoodit = source.splice(0,1)[0];
                _.each(source, function(koulutuksenKoodit) {
                    muidenKoulutustenKoodit.push(koulutuksenKoodit);
                });
                console.log('alkuarvot asetettu, ', [ensimmaisenKoulutuksenKoodit, muidenKoulutustenKoodit]);
            }

            var koodiUriFoundInKoodiArray = function (koodiUri, koodiArray) {
                for(var i=0; i<koodiArray.length; i++) {
                    var vertailuUri = koodiArray[i].koodiUri;
                    if (koodiUri === vertailuUri) {
                        return true;
                    }
                }
                return false;
            };
            if (muidenKoulutustenKoodit.length > 0) {
                _.each(ensimmaisenKoulutuksenKoodit, function(koulutusKoodi) {
                    var vertailukoodiUri = koulutusKoodi.koodiUri;
                    var koodiOk = true;
                    _.each(muidenKoulutustenKoodit, function(muunKoulutuksenKoodit) {
                        if(!koodiUriFoundInKoodiArray(vertailukoodiUri, muunKoulutuksenKoodit)) {
                            koodiOk = false;
                        }
                    });
                    if (koodiOk) {
                        console.log('Löydettiin koodi, joka löytyi kaikista koodiTauluista. lisätään: ', koulutusKoodi);
                        commonKoodisFound.push(koulutusKoodi);
                    } else {
                        console.log('Hylätään koodi: ', koulutusKoodi);
                    }
                });
            } else {
                return ensimmaisenKoulutuksenKoodit; //Jos vain yksi koulutus, kaikki koodit ok!
            }
            console.log('Koulutuksille yhteiset koodit parsittu, palautetaan: ', commonKoodisFound);
            return commonKoodisFound;
        };

        var populateHakukohteenNimetByKoulutus = function(koulutus) {
            var uri = koulutus.koulutusohjelma.uri ? koulutus.koulutusohjelma.uri : koulutus.koulutuskoodi.uri;
            var pohjakoulutusvaatimus = koulutus.pohjakoulutusvaatimus;
            var relevantKooditForCurrentKoulutus = [];
            var currentUri = window.oph.removeKoodiVersion($scope.model.hakukohde.hakukohteenNimiUri || '');
            var kaytettavaKoodisto = $scope.model.hakukohde.toteutusTyyppi === 'AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA' ? 'aikuhakukohteet' : 'hakukohteet';
            Koodisto.getAlapuolisetKoodit(uri, AuthService.getLanguage())
            .then(function(koulutusohjelmanKoodit) {
                angular.forEach(koulutusohjelmanKoodit, function(koulutusohjelmanKoodi) {
                    $scope.model.hakukohteenNimetAll.push(koulutusohjelmanKoodi);
                    relevantKooditForCurrentKoulutus.push(koulutusohjelmanKoodi);
                    if (koulutusohjelmanKoodi.koodiKoodisto === kaytettavaKoodisto) {
                        if(kaytettavaKoodisto === 'aikuhakukohteet') {
                            appendOrReplaceHakukohteenNimi(currentUri, koulutusohjelmanKoodi);
                        } else {
                            Koodisto.getYlapuolisetKoodit(koulutusohjelmanKoodi.koodiUri, AuthService.getLanguage())
                                .then(function(hakukohteenYlapuolisetKoodit) {
                                    angular.forEach(hakukohteenYlapuolisetKoodit, function(hakukohteenYlapuolinenKoodi) {
                                        if (pohjakoulutusvaatimus && hakukohteenYlapuolinenKoodi.koodiUri === pohjakoulutusvaatimus.uri) {
                                            appendOrReplaceHakukohteenNimi(currentUri, koulutusohjelmanKoodi);
                                        }
                                    });
                                });
                        }
                    }
                });
            });
            //console.log('valmis, relevantKooditForCurrentKoulutus: ', relevantKooditForCurrentKoulutus);
            $scope.model.hakukohteenNimetByKoulutus.push(relevantKooditForCurrentKoulutus);
        };

        if ($scope.config.isToisenAsteenKoulutus()) {
            $scope.model.hakukohteenNimet = [];
            $scope.model.hakukohteenNimetAll = [];
            $scope.model.hakukohteenNimetByKoulutus = []; //Tänne kaikkien koulutusten koodit jaoteltuna koulutuksittain (taulukko taulukossa)
            $scope.model.hakukohteenNimetAlk2018 = [];
            $scope.model.pohjakoulutusvaatimusByKoulutus = null;
            angular.forEach($scope.model.hakukohde.hakukohdeKoulutusOids, function(koulutusOid) {
                TarjontaService.getKoulutusPromise(koulutusOid).then(function(response) {
                    populateHakukohteenNimetByKoulutus(response.result);
                });
            });
        }
    };
    init();


    var appendOrReplaceHakukohteenNimiAlk2018 = function(koulutusohjelmanKoodi) {
        var koodi = {
            uri: koulutusohjelmanKoodi.koodiUri + '#' + koulutusohjelmanKoodi.koodiVersio,
            label: koulutusohjelmanKoodi.koodiNimi,
            uriWithoutVersion: koulutusohjelmanKoodi.koodiUri,
            version: parseInt(koulutusohjelmanKoodi.koodiVersio)
        };
        // Default index -> append new item
        var index = $scope.model.hakukohteenNimetAlk2018.length;
        var sameKoodi = _.find($scope.model.hakukohteenNimetAlk2018, function(obj, i) {
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
        $scope.model.hakukohteenNimetAlk2018[index] = koodi;

        // Update model value to newest koodi version
        console.log("Check for need to update");
        var currentUri = window.oph.removeKoodiVersion($scope.model.hakukohde.hakukohteenNimiUri || '');
        if (currentUri === koodi.uriWithoutVersion) {
            var newUri = currentUri + '#' + koodi.version;
            console.log("model.hakukohde.hakukohtenNimiUri was: " + $scope.model.hakukohde.hakukohteenNimiUri);
            console.log("Updating model.hakukohde.hakukohteenNimiUri to " + newUri);
            $scope.model.hakukohde.hakukohteenNimiUri = newUri;
        }
    };

    $scope.model.loadPohjakoulutusvaatimus = function () {
        angular.forEach($scope.model.hakukohde.hakukohdeKoulutusOids, function (koulutusOid) {
            TarjontaService.getKoulutusPromise(koulutusOid).then(function (response) {
                var koulutus = response.result;
                if (koulutus.pohjakoulutusvaatimus !== undefined) {
                    $scope.model.pohjakoulutusvaatimusByKoulutus = koulutus.pohjakoulutusvaatimus;
                }
            });
        });
    };
    $scope.model.loadPohjakoulutusvaatimus();

    $scope.model.loadPohjakoulutusvaatimusFromHakukohde = function () {
        // jos pohjakoulutusvaatimus on tyhjä yritä ladata se hakukohteen kautta
        if($scope.model.hakukohde.pohjakoulutusvaatimus == undefined && $scope.model.hakukohde.hakukohteenNimiUri != undefined) {
            var uri = $scope.model.hakukohde.hakukohteenNimiUri.split('#')[0];
            Koodisto.getYlapuolisetKoodit(uri, AuthService.getLanguage())
                .then(function (hakukohteenYlapuolisetKoodit) {
                    angular.forEach(hakukohteenYlapuolisetKoodit, function (hakukohteenYlapuolinenKoodi) {
                        if(hakukohteenYlapuolinenKoodi.koodiKoodisto == "pohjakoulutusvaatimustoinenaste"){
                            $scope.model.hakukohde.pohjakoulutusvaatimus = hakukohteenYlapuolinenKoodi.koodiUri;
                        }
                    });
                });
        }
    };

    $scope.model.loadPohjakoulutusvaatimusFromHakukohde();

    $scope.model.populateHakukohteenNimetByHaku = function() {
        var koulutuksilleYhteisetKoodit = $scope.model.findCommonNamesForHakukohde($scope.model.hakukohteenNimetByKoulutus);
        var pohjakoulutusvaatimus = $scope.model.hakukohde.pohjakoulutusvaatimus;
        var promises = [HakuService.getAllHakus()];
        $q.all(promises).then(function(resolved) {
            var hakuDatas = resolved[0];
            var selectedHaku = _.findWhere(hakuDatas, {oid: $scope.model.hakukohde.hakuOid});
            var kaytettavaKoodisto = 'hakukohteet'; // pk/yo
            // jatkuva haku tai pohjakoulutusER
            if(selectedHaku.hakutapaUri == 'hakutapa_03#1' || (pohjakoulutusvaatimus !== undefined && pohjakoulutusvaatimus != null && pohjakoulutusvaatimus.uri == 'pohjakoulutusvaatimustoinenaste_er')){
                // aiku
                kaytettavaKoodisto = 'aikuhakukohteet';
            }
            $scope.model.hakukohteenNimetAlk2018 = [];
            angular.forEach(koulutuksilleYhteisetKoodit, function (koodi){
                if (koodi.koodiKoodisto == kaytettavaKoodisto) {
                    if (kaytettavaKoodisto == 'aikuhakukohteet') {
                        appendOrReplaceHakukohteenNimiAlk2018(koodi);
                    } else {
                        Koodisto.getYlapuolisetKoodit(koodi.koodiUri, AuthService.getLanguage())
                            .then(function (hakukohteenYlapuolisetKoodit) {
                                angular.forEach(hakukohteenYlapuolisetKoodit, function (hakukohteenYlapuolinenKoodi) {
                                    if (pohjakoulutusvaatimus != undefined && hakukohteenYlapuolinenKoodi.koodiUri == pohjakoulutusvaatimus) {
                                        appendOrReplaceHakukohteenNimiAlk2018(koodi);
                                    }
                                });
                            });
                    }
                }
            });
        });
    };

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