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
                OrganisaatioService, $modal, AuthService, KoulutusConverterFactory, Koodisto) {
    $log = $log.getInstance('HakuReviewController');
    $scope.isMutable = false;
    $scope.isRemovable = false;
    var hakuOid = $route.current.params.id;
    //haku permissiot
    PermissionService.getPermissions('haku', hakuOid).then(function(permissiot) {
        $scope.isCopyable = permissiot.haku.copy;
        $scope.isMutable = permissiot.haku.update;
        $scope.isRemovable = permissiot.haku.remove;
    });
    $log.info('  init, args =', $scope, $route, $routeParams);
    // hakux : $route.current.locals.hakux, // preloaded, see "hakuApp.js" route resolve for "/haku/:id"
    $scope.model = null;
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
    $scope.init = function() {
        $log.info('HakuReviewController.init()...');
        $scope.model = {
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
            hakux: $route.current.locals.hakux,
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
            place: 'holder'
        };
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
        //
        // Get hakukohdes for current haku
        //
        TarjontaService.haeHakukohteet({
            hakuOid: hakuOid
        }).then(function(result) {
            $log.info('GOT HAKUKOHTEET: ', result.tulokset);
            // Datan rakenne :
            //            { "oid": "1.2.246.562.10.82388989657", "version": 0, "nimi": "Aalto-korkeakoulusäätiö",
            //              "tulokset": [ {
            //                "oid": "1.2.246.562.20.924214830310",
            //                "nimi": "ertert hkk",
            //                "kausi": { "fi": "Syksy", "sv": "Höst", "en": "Autumn" },
            //                "vuosi": 2014,
            //                "tila": "LUONNOS",
            //                "hakutapa": "Yhteishaku",
            //                "aloituspaikat": 56,
            //                "tilaNimi": "Luonnos"
            //              } ]
            //            }
            // Result list collected here
            var tmp = [];
            // Flatten the result list, its grouped by organisations
            angular.forEach(result.tulokset, function(orgGroup) {
                // Organisation group
                angular.forEach(orgGroup.tulokset, function(hakukohde) {
                    // Hakukohdes in organisation, extract name + oid
                    hakukohde.organisaatioNimi = orgGroup.nimi;
                    hakukohde.organisaatioOid = orgGroup.oid;
                    tmp.push(hakukohde);
                });
            });
            $scope.model.hakukohteet = tmp;
        }, function(error) {
                $log.error('Failed to get hakukohdes for current haku!', error);
                tmp.push({
                    organisaatioNimi: 'VIRHE HAKUKOHTEIDEN HAUSSA'
                });
            });
        $log.info('HakuReviewController.init()... done.');
    };
    $scope.downloadHakukohteetExcel = function() {
        Koodisto.getAllKoodisWithKoodiUri('koulutustyyppi', AuthService.getLanguage().toLowerCase())
            .then(function(data) {
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

                var koodis = _.indexBy(data, 'koodiUri');
                var wb = {
                    SheetNames: [],
                    Sheets: {}
                };
                var ws = {
                    A1: {t: 's', v: 'Hakukohteen OID'},
                    B1: {t: 's', v: 'Organisaatio'},
                    C1: {t: 's', v: 'Hakukohteen nimi'},
                    D1: {t: 's', v: 'Hakuaikaryhmä'},
                    E1: {t: 's', v: 'Hakuaika'},
                    F1: {t: 's', v: 'Kausi'},
                    G1: {t: 's', v: 'Vuosi'},
                    H1: {t: 's', v: 'Tila'},
                    I1: {t: 's', v: 'Hakutapa'},
                    J1: {t: 's', v: 'Aloituspaikat'},
                    K1: {t: 's', v: 'Toteutustyyppi'},
                    L1: {t: 's', v: 'Opetuskielet'}
                };
                var sheetName = 'Hakukohteet';
                _.each($scope.model.hakukohteet, function(hakukohde, i) {
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
                        K: getKoulutustyyppi(hakukohde.toteutustyyppiEnum),
                        L: (hakukohde.opetuskielet || []).join(', ')
                    };
                    _.each(row, function(val, key) {
                        ws[key + rowNumber] = {
                            t: parseInt(val) == val ? 'n' : 's',
                            v: val
                        };
                    });
                    ws['!ref'] = 'A1:L' + rowNumber;
                });
                wb.SheetNames.push(sheetName);
                wb.Sheets[sheetName] = ws;
                var wbout = XLSX.write(wb, {bookType:'xlsx', bookSST:false, type: 'binary'});
                saveAs(new Blob([s2ab(wbout)], {type:'application/octet-stream'}), 'hakukohteet.xlsx');
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