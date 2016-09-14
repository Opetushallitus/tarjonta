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
/**
 *
 * This controller acts as routing and parent controller of all hakukohdes,
 * it contains all common controller variables and functions
 * @type {module|*}
 */
var app = angular.module('app.hakukohde.ctrl', []);
app.controller('HakukohdeRoutingController', [
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
    'HakukohdeService', function($scope, $log, $routeParams, $route, $q, $modal, $location, Hakukohde, Koodisto,
             AuthService, HakuService, LocalisationService, OrganisaatioService, SharedStateService,
             TarjontaService, Kuvaus, CommonUtilService, PermissionService, dialogService, HakukohdeService) {
        $log.info('HakukohdeRoutingController()', $routeParams);
        $log.info('$route: ', $route);
        $log.info('$route action: ', $route.current.$$route.action);
        $log.info('SCOPE: ', $scope);
        $log.info('CAN EDIT : ', $route.current.locals.canEdit);
        $log.info('CAN CREATE : ', $route.current.locals.canCreate);
        $log.info('HAKUKOHDEX RESULT : ', $route.current.locals.hakukohdex.result);
        if ($route.current.locals.isCopy !== undefined) {
            $scope.isCopy = $route.current.locals.isCopy;
        }
        else {
            $scope.isCopy = false;
        }
        $scope.formControls = {};
        // controls-layouttia varten
        $scope.canCreate = $route.current.locals.canCreate;
        $scope.canEdit = $route.current.locals.canEdit;

        $scope.valintakoeAjankohtaToCurrentLocale = function(ajankohta) {
            var t = new Date(ajankohta),
                year = t.getFullYear(),
                month = t.getMonth() + 1,
                day = t.getDate(),
                hours = t.getHours(),
                minutes = t.getMinutes();
            return new Date(year, month, day, hours, minutes).getTime();
        };

        if ($route.current.locals.hakukohdex.result === undefined) {
            $scope.model = {
                collapse: {
                    model: true
                },
                hakukohdeTabsDisabled: true,
                hakukohde: {
                    valintaperusteKuvaukset: {},
                    soraKuvaukset: {},
                    kaytetaanJarjestelmanValintaPalvelua: false,
                    hakukohteenLiitteet: []
                }
            };
            $scope.model.hakukohde = $route.current.locals.hakukohdex;
        }
        else {
            var hakukohdeResource = new Hakukohde($route.current.locals.hakukohdex.result);

            angular.forEach(hakukohdeResource.valintakokeet, function(koe) {
                _.each(koe.valintakoeAjankohtas, function(ajankohta) {
                    ajankohta.alkaa = valintakoeAjankohtaToCurrentLocale(ajankohta.alkaa);
                    ajankohta.loppuu = valintakoeAjankohtaToCurrentLocale(ajankohta.loppuu);
                });
            });

            if ($route.current.locals.isCopy !== undefined) {
                SharedStateService.addToState('SelectedToteutusTyyppi', hakukohdeResource.toteutusTyyppi);
            }
            if (hakukohdeResource.valintaperusteKuvaukset === undefined) {
                hakukohdeResource.valintaperusteKuvaukset = {};
            }
            if (hakukohdeResource.soraKuvaukset === undefined) {
                hakukohdeResource.soraKuvaukset = {};
            }
            $scope.model = {
                collapse: {
                    model: true
                },
                hakukohdeTabsDisabled: false,
                hakukohde: hakukohdeResource
            };
        }
        $scope.hakukohdex = $route.current.locals.hakukohdex;
    }
]);