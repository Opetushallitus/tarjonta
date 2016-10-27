/*
 * Copyright (c) 2014 The Finnish Board of Education - Opetushallitus
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
var app = angular.module('app.haku.review.ctrl', [
    'app.haku.ctrl',
    'app.haku.copy.ctrl'
]);
app.controller('HakuReviewController', function($scope, $route, $log, $routeParams, ParameterService, $location,
                HakuV1Service, TarjontaService, dialogService, LocalisationService, $q, PermissionService,
                OrganisaatioService, $modal, AuthService, KoulutusConverterFactory, Koodisto, AtaruService) {
    $log = $log.getInstance('HakuReviewController');
    $scope.isMutable = false;
    $scope.isRemovable = false;
    var HAKUKOHDE_RESULTS_PER_PAGE = 100;
    var hakuOid = $route.current.params.id;
    var hakux = $route.current.locals.hakux;
    //haku permissiot
    PermissionService.getPermissions('haku', hakuOid).then(function(permissiot) {
        $scope.isMutable = permissiot.haku.update;
        $scope.isRemovable = permissiot.haku.remove;

        if (hakux.result.koulutusmoduuliTyyppi === 'OPINTOKOKONAISUUS') {
            $scope.isCopyable = false;

            OrganisaatioService.getAllowedKoulutustyypit(AuthService.getOrganisations()).then(function(koulutustyypit) {
                $scope.isCopyable = permissiot.haku.copy;

                // Tutkintoon johtamatonta hakua ei saa kopioida
                // jos ei ole oikeutta luoda tutkintoon johtamatonta koulutusta
                if (!_.contains(
                        koulutustyypit, KoulutusConverterFactory.STRUCTURE.KORKEAKOULUOPINTO.koulutustyyppiKoodiUri
                    )) {
                    $scope.isCopyable = false;
                }
            });
        }
        else {
            $scope.isCopyable = permissiot.haku.copy;
        }
    });
    $log.info('  init, args =', $scope, $route, $routeParams);
    // hakux : $route.current.locals.hakux, // preloaded, see "hakuApp.js" route resolve for "/haku/:id"
    $scope.model = {
        currentPage: 0,
        itemsPerPage: HAKUKOHDE_RESULTS_PER_PAGE
    };
    $scope.isJatkuvaHaku = function() {
        // Defined in "hakuControllers.js"
        var result = $scope.isHakuJatkuvaHaku($scope.model.hakux.result);
        // $log.info("isJatkuvaHaku()", result);
        return result;
    };
    $scope.goBack = function() {
        $location.path('/haku');
    };
    $scope.doEdit = function() {
        if (!$scope.isMutable) {
            return;
        }
        $location.path('/haku/' + hakuOid + '/edit');
    };
    $scope.doDelete = function(event) {
        if (!$scope.isRemovable) {
            return;
        }
        $log.info('doDelete()', event);
        // In "hakuControllers.js"
        $scope.doDeleteHaku($scope.model.hakux.result, true).then(function(result) {
            if (result) {
                // OK, delete done so cannot display review any more - go away
                $scope.goBack();
            }
            else {
                $log.info('delete failed - stay here.');
            }
        });
    };
    /**
     * Avaa "haun kopiointi dialogi"
     */
    $scope.doCopy = function() {
        console.log('initializing haku copy', $modal);
        //aseta esivalittu organisaatio
        $scope.kopioiHakuDialog = $modal.open({
            scope: $scope,
            templateUrl: 'partials/haku/copy/kopioi-haku-dialogi.html',
            controller: 'HakuCopyController'
        });
    };
    $scope.generateLomake = function() {
        $modal.open({
            templateUrl: 'partials/haku/generateLomake.html',
            controller: 'GenerateLomakeController',
            resolve: {
                hakuOid: function() {
                    return $scope.model.hakux.result.oid;
                }
            }
        });
    };
    function flattenHakukohteet(tulokset) {
        return _.reduce(tulokset, function(memo, orgGroup) {
            _.each(orgGroup.tulokset, function(hakukohde) {
                // Hakukohdes in organisation, extract name + oid
                hakukohde.organisaatioNimi = orgGroup.nimi;
                hakukohde.organisaatioOid = orgGroup.oid;
                memo.push(hakukohde);
            });
            return memo;
        }, []);
    }
    $scope.getHakukohteet = function() {
        var page = $scope.model.currentPage || 1;
        var offset = (page - 1) * HAKUKOHDE_RESULTS_PER_PAGE;
        TarjontaService.haeHakukohteet({
            hakuOid: hakuOid,
            offset: offset,
            limit: HAKUKOHDE_RESULTS_PER_PAGE
        }).then(function(result) {
            $scope.model.hakukohteetTotalRows = result.tuloksia;
            $scope.model.hakukohteet = flattenHakukohteet(result.tulokset);
            $scope.model.paginationNeeded = $scope.model.hakukohteetTotalRows > $scope.model.hakukohteet.length;
        }, function(error) {
            $log.error('Failed to get hakukohdes for current haku!', error);
            tmp.push({
                organisaatioNimi: 'VIRHE HAKUKOHTEIDEN HAUSSA'
            });
        });
    };
    $scope.initAtaruForm = function(ataruLomakeAvain) {
        AtaruService.getForms().then(function(forms) {
            var form = _.findWhere(forms, {'key': ataruLomakeAvain});
            if (form) {
                $scope.model.ataruFormName = form.name;
            }
        });
    };
    $scope.init = function() {
        $log.info('HakuReviewController.init()...');
        _.extend($scope.model, {
            formControls: {},
            collapse: {
                haunTiedot: false,
                haunAikataulut: true,
                haunMuistutusviestit: true,
                haunSisaisetHaut: true,
                haunHakukohteet: true,
                model: true
            },
            // Preloaded Haku result
            hakux: hakux,
            nimi: HakuV1Service.resolveLocalizedValue($route.current.locals.hakux.result.nimi),
            koodis: {
                koodiX: '...'
            },
            haku: {
                todo: 'TODO LOAD ME 1'
            },
            hakukohteet: [],
            tarjoajaOrganisations: [],
            // { tarjoajaOids : [...] }
            hakukohdeOrganisations: [],
            // { organisaatioOids : [...] }
            place: 'holder',
            ataruFormName: ''
        });
        //
        // Get organisation information
        //
        angular.forEach($scope.model.hakux.result.organisaatioOids, function(organisationOid) {
            $log.info('  get [organisaatioOids] ', organisationOid);
            OrganisaatioService.byOid(organisationOid).then(function(organisation) {
                $scope.model.hakukohdeOrganisations.push(organisation);
            });
        });
        angular.forEach($scope.model.hakux.result.tarjoajaOids, function(organisationOid) {
            $log.info('  get [tarjoajaOids] ', organisationOid);
            OrganisaatioService.byOid(organisationOid).then(function(organisation) {
                $scope.model.tarjoajaOrganisations.push(organisation);
            });
        });
        angular.forEach($scope.model.hakux.result.hakuaikas, function(hakuaika) {
            hakuaika.nimi = HakuV1Service.resolveLocalizedValue(hakuaika.nimet);
        });
        $scope.getHakukohteet();
        $scope.initAtaruForm($scope.model.hakux.result.ataruLomakeAvain);
    };
    $scope.downloadHakukohteetExcel = function() {
        Koodisto.getAllKoodisWithKoodiUri('koulutustyyppi', AuthService.getLanguage().toLowerCase())
            .then(function(data) {
                var koodis = _.indexBy(data, 'koodiUri');
                function getKoulutustyyppi(toteutustyyppi) {
                    if (KoulutusConverterFactory.STRUCTURE[toteutustyyppi]) {
                        var uri = KoulutusConverterFactory.STRUCTURE[toteutustyyppi].koulutustyyppiKoodiUri;
                        if (koodis[uri]) {
                            return koodis[uri].koodiNimi;
                        }
                    }
                    // Fallback
                    return toteutustyyppi;
                }

                TarjontaService.haeHakukohteet({
                    hakuOid: hakuOid
                }).then(function(result) {
                    var wb = {
                        SheetNames: [],
                        Sheets: {}
                    };
                    var ws = {
                        A1: {t: 's', v: 'Hakukohteen OID'},
                        B1: {t: 's', v: 'Organisaatio'},
                        C1: {t: 's', v: 'Hakukohteen nimi'},
                        D1: {t: 's', v: 'Hakuaikaryhmä'},
                        E1: {t: 's', v: 'Hakukohteen hakuaika'},
                        F1: {t: 's', v: 'Kausi'},
                        G1: {t: 's', v: 'Vuosi'},
                        H1: {t: 's', v: 'Tila'},
                        I1: {t: 's', v: 'Hakutapa'},
                        J1: {t: 's', v: 'Aloituspaikat'},
                        K1: {t: 's', v: 'Valintojen aloituspaikat'},
                        L1: {t: 's', v: 'Ensikertalaisten aloituspaikat'},
                        M1: {t: 's', v: 'Koulutustyyppi'},
                        N1: {t: 's', v: 'Opetuskielet'}
                    };
                    var sheetName = 'Hakukohteet';

                    _.each(flattenHakukohteet(result.tulokset), function(hakukohde, i) {
                        var rowNumber = i + 2;
                        var row = {
                            A: hakukohde.oid,
                            B: hakukohde.organisaatioNimi,
                            C: hakukohde.nimi,
                            D: hakukohde.hakuaikaRyhma,
                            E: hakukohde.hakuaikaString,
                            F: hakukohde.kausi.fi,
                            G: hakukohde.vuosi,
                            H: hakukohde.tilaNimi,
                            I: hakukohde.hakutapa,
                            J: $scope.getAloituspaikat(hakukohde),
                            K: hakukohde.valintojenAloituspaikat,
                            L: hakukohde.ensikertalaistenAloituspaikat,
                            M: getKoulutustyyppi(hakukohde.toteutustyyppiEnum),
                            N: (hakukohde.opetuskielet || []).join(', ')
                        };
                        _.each(row, function(val, key) {
                            ws[key + rowNumber] = {
                                t: parseInt(val) == val ? 'n' : 's',
                                v: val
                            };
                        });
                        ws['!ref'] = 'A1:M' + rowNumber;
                    });
                    wb.SheetNames.push(sheetName);
                    wb.Sheets[sheetName] = ws;
                    var wbout = XLSX.write(wb, {bookType:'xlsx', bookSST:false, type: 'binary'});
                    saveAs(new Blob([s2ab(wbout)], {type:'application/octet-stream'}), 'hakukohteet.xlsx');
                });
            });
    };
    $scope.init();
    $scope.parametrit = {};
    ParameterService.haeParametritUUSI(hakuOid).then(function(parameters) {
        $scope.parametrit = parameters;
    }); // ParameterService.haeHaunParametrit(hakuOid, $scope.parametrit);
    var lang = AuthService.getLanguage().toLowerCase();
    $scope.getAloituspaikat = function(hakukohde) {
        var aloituspaikat = hakukohde.aloituspaikatKuvaukset || {};
        return aloituspaikat[lang] || aloituspaikat[Object.keys(aloituspaikat)[0]] || hakukohde.aloituspaikat;
    };
    $scope.isKorkeakouluhaku = function() {
        return $scope.model.hakux.result.kohdejoukkoUri.indexOf('haunkohdejoukko_12#') !== -1;
    };
    $scope.isErillishaku = function() {
        return $scope.model.hakux.result.hakutapaUri.indexOf('hakutapa_02#') !== -1;
    };
    $scope.isKoulutuksetChecked = function() {
        if (_.isUndefined($scope.parametrit.PH_KVT)) {
            return true;
        } else {
            return $scope.parametrit.PH_KVT.booleanValue;
        }
    };
    $scope.url = window.url;
});

// From http://sheetjs.com/demos/Export2Excel.js
function s2ab(s) {
    var buf = new ArrayBuffer(s.length);
    var view = new Uint8Array(buf);
    for (var i = 0; i != s.length; ++i) {
        view[i] = s.charCodeAt(i) & 0xFF;
    }
    return buf;
}