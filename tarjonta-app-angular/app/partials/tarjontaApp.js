/*******************************************************/
/* Define all project sub-modules with no dependencies */
/*******************************************************/
angular.module('app.kk', [
    'app.kk.edit.hakukohde.ctrl',
    'app.kk.edit.hakukohde.review.ctrl',
    'app.kk.edit.valintaperustekuvaus.ctrl',
    'app.kk.search.valintaperustekuvaus.ctrl',
    'app.edit.ctrl',
    'app.edit.ctrl.kk',
    'app.edit.ctrl.kko',
    'app.edit.ctrl.generic',
    'app.edit.ctrl.amm',
    'app.edit.ctrl.alkamispaiva',
    'app.edit.ctrl.tutkintonimike',
    'app.edit.ctrl.laajuus',
    'app.komo.ctrl',
    'app.review.ctrl',
    'app.hakukohde.ctrl',
    'app.hakukohde.dialog.ctrl',
    'app.haku.ctrl',
    'ui.bootstrap',
    'ngRoute',
    'config',
    'auth',
    'KoulutusConverter',
    'imageupload'
]);
/*******************************************************
 * Main module dependecies                             *
 *******************************************************/
angular.module('app', [
    'Koulutus',
    'app.directives',
    'app.services',
    'app.search.controllers',
    'app.test.controllers',
    'app.kk',
    'app.koulutus.ctrl',
    'app.koulutus.sisaltyvyys.ctrl',
    'app.koulutus.remove.ctrl',
    'app.koulutus.kuvausRemove.ctrl',
    'app.koulutus.copy.ctrl',
    'app.koulutus.extend.ctrl',
    'app.dialog',
    'ngRoute',
    'ngResource',
    'ngSanitize',
    'ngAnimate',
    'ngCookies',
    'ui.bootstrap',
    'pasvaz.bindonce',
    'loading',
    'localisation',
    'Koodisto',
    'Organisaatio',
    'TarjontaPermissions',
    'TarjontaCache',
    'Tarjonta',
    'KoodistoCombo',
    'KoodistoArvoCombo',
    'SharedStateService',
    'CommonUtilServiceModule',
    'DateTimePicker',
    'Hakukohde',
    'Kuvaus',
    'KoodistoMultiSelect',
    'KoodistoTypeAhead',
    'orgAngularTreeview',
    'ResultsTreeTable',
    'imageupload',
    'MultiSelect',
    'OrderByNumFilter',
    'StartsWithFilter',
    'CommonDirectives',
    'MonikielinenTextField',
    'ImageDirective',
    'RichTextArea',
    'MonikielinenTextArea',
    'MonikielinenText',
    'MonikielinenTabs',
    'MultiLangSimpleTextArea',
    'ControlsLayout',
    'ShowErrors',
    'angularTreeview',
    'DateFormat',
    'TreeFieldDirective',
    'AiheetJaTeematChooser',
    'TarjontaDateTime',
    'TarjontaOsoiteField',
    'TarjontaInvalidHtml',
    'ExportToParent',
    'debounce',
    'Parameter',
    'Logging',
    'ValidDecimal',
    'ValidPositiveNumber',
    'taiPlaceholder',
    'Validator',
    'Oppiaineet',
    'app.import.ctrl'
]);
angular.module('app').value('globalConfig', window.CONFIG);
angular.module('app').factory('errorLogService', function($log, $window, $injector, Config) {
    'use strict';

    var errorsLoggingTimeout = Config.env['errorlog.timeout'] || 60000;
    var errorsLoggingSuspended = false;
    var loggedErrors = [];
    $log.info('*** errorLogService ***', window.url("tarjonta-service.permission.recordUiStacktrace"));
    function get_browser() {
        var N = navigator.appName;
        var ua = navigator.userAgent;
        var tem;
        var M = ua.match(/(opera|chrome|safari|firefox|msie)\/?\s*(\.?\d+(\.\d+)*)/i);
        if (M && (tem = ua.match(/version\/([\.\d]+)/i)) !== null) {
            M[2] = tem[1];
        }
        M = M ? [
            M[1],
            M[2]
        ] : [
            N,
            navigator.appVersion,
            '-?'
        ];
        return M[0];
    }
    function get_browser_version() {
        var N = navigator.appName;
        var ua = navigator.userAgent;
        var tem;
        var M = ua.match(/(opera|chrome|safari|firefox|msie)\/?\s*(\.?\d+(\.\d+)*)/i);
        if (M && (tem = ua.match(/version\/([\.\d]+)/i)) !== null) {
            M[2] = tem[1];
        }
        M = M ? [
            M[1],
            M[2]
        ] : [
            N,
            navigator.appVersion,
            '-?'
        ];
        return M[1];
    }
    // Log error to console (as normal) AND to the remote server
    function log(exception, cause) {
        // Default behaviour, log to console
        $log.error.apply($log, arguments);
        if (Config.env['errorlog.disabled'] || errorsLoggingSuspended) {
            return;
        }
        // Try to send stacktrace event to server
        try {
            $log.debug('logging error to server side...');
            var callerid = Config.env['callerid.tarjonta.tarjonta-app.frontend'];
            var errorMessage = exception.toString();
            var stackTrace = exception.stack.toString();
            var errorId = errorMessage + '---' + stackTrace;
            if (loggedErrors.indexOf(errorId) != -1) {
                // älä lähetä, jos jo lokitettu tai lokitus keskeytetty
                return;
            }
            var browserInfo = {
                browser: get_browser(),
                browserVersion: get_browser_version()
            };
            // Log the JavaScript error to the server.
            $.ajax({
                type: 'POST',
                url: window.url("tarjonta-service.permission.recordUiStacktrace"),
                contentType: 'application/json',
                xhrFields: {
                    withCredentials: true
                },
                headers: {
                    'Caller-Id': callerid,
                    'CSRF': $injector.get('$cookies')['CSRF']
                },
                data: angular.toJson({
                    errorUrl: $window.location.href,
                    errorMessage: errorMessage,
                    stackTrace: stackTrace,
                    cause: cause || '',
                    browserInfo: browserInfo
                }),
                success: function() {
                    loggedErrors.push(errorId);
                },
                error: function() {
                    errorsLoggingSuspended = true;
                    $log.debug('error logging suspended for ' + errorsLoggingTimeout + ' ms.');
                    setTimeout(function() {
                        $log.debug('error logging resumed.');
                        errorsLoggingSuspended = false;
                    }, errorsLoggingTimeout);
                }
            });
        }
        catch (loggingError) {
            // For Developers - log the log-failure.
            $log.warn('Error logging to server side failed');
            $log.log(loggingError);
        }
    }
    // Return the logging function.
    return log;
});
angular.module('app').provider('$exceptionHandler', {
    $get: function(errorLogService) {
        return errorLogService;
    }
});
angular.module('app').config([
    '$routeProvider', function($routeProvider) {
        'use strict';
        /**
         *
         * Helper functions for hakukohde resolvers
         *
         * */
        var resolveHakukohde = function(Hakukohde, $log, $route, SharedStateService, $q, OrganisaatioService,
                                        TarjontaService) {
            var deferred = $q.defer();
            function loadOhjausparametritAndResolveHakukohde(hakukohde) {
                TarjontaService.reloadParametersIfEmpty().then(function() {
                    deferred.resolve(hakukohde);
                });
            }
            if ('new' === $route.current.params.id) {
                var selectedTarjoajaOids;
                var selectedKoulutusOids;
                if (angular.isArray(SharedStateService.getFromState('SelectedOrgOid'))) {
                    selectedTarjoajaOids = SharedStateService.getFromState('SelectedOrgOid');
                }
                else {
                    selectedTarjoajaOids = [SharedStateService.getFromState('SelectedOrgOid')];
                }
                if (angular.isArray(SharedStateService.getFromState('SelectedKoulutukses'))) {
                    selectedKoulutusOids = SharedStateService.getFromState('SelectedKoulutukses');
                }
                else {
                    selectedKoulutusOids = [SharedStateService.getFromState('SelectedKoulutukses')];
                }
                //Initialize model and arrays inside it
                var hakukohde = new Hakukohde({
                    liitteidenToimitusOsoite: {},
                    tarjoajaOids: selectedTarjoajaOids,
                    hakukohteenNimet: {},
                    hakukelpoisuusvaatimusUris: [],
                    opintoOikeusUris: [],
                    hakukohdeKoulutusOids: selectedKoulutusOids,
                    hakukohteenLiitteet: [],
                    valintakokeet: [],
                    lisatiedot: {},
                    valintaperusteKuvaukset: {},
                    soraKuvaukset: {},
                    painotettavatOppiaineet: [],
                    yhteystiedot: [],
                    pohjakoulutusliitteet: [],
                    isNew: true,
                    overridesHaunHakulomakeUrl: false,
                    tunnistusKaytossa: false
                });
                var koulutus = SharedStateService.getFromState('firstSelectedKoulutus');
                SharedStateService.removeState('firstSelectedKoulutus');
                hakukohde.multipleOwners = koulutus.opetusTarjoajat.length > 1;
                hakukohde.opetusKielet = _.keys(koulutus.opetuskielis.uris);
                hakukohde.toteutusTyyppi = koulutus.toteutustyyppi;
                loadOhjausparametritAndResolveHakukohde(hakukohde);
            }
            else {
                Hakukohde.get({
                    oid: $route.current.params.id
                }).$promise.then(function(res) {
                    var tarjoajat = [];
                    if (res.result) {
                        angular.forEach(res.result.koulutusmoduuliToteutusTarjoajatiedot, function(tiedot) {
                            angular.forEach(tiedot.tarjoajaOids, function(oid) {
                                if (tarjoajat.indexOf(oid) === -1) {
                                    tarjoajat.push(oid);
                                }
                            });
                        });
                    }
                    if (tarjoajat.length) {
                        OrganisaatioService.getPopulatedOrganizations(tarjoajat).then(function(orgs) {
                            res.result.uniqueTarjoajat = orgs;
                            loadOhjausparametritAndResolveHakukohde(res);
                        });
                    }
                    else {
                        loadOhjausparametritAndResolveHakukohde(res);
                    }
                });
            }
            return deferred.promise;
        };
        var resolveCanEditHakukohde = function(Hakukohde, $log, $route, $q, PermissionService) {
            if ($route.current.params.id !== 'new') {
                var deferredPermission = $q.defer();
                Hakukohde.get({
                    oid: $route.current.params.id
                }, function(data) {
                        var promises = [];
                        var canEdit = false;
                        function checkCanEdit(tarjoaja) {
                            var defer = $q.defer();
                            PermissionService.canEdit(tarjoaja).then(function(permission) {
                                if (permission) {
                                    canEdit = true;
                                }
                                defer.resolve();
                            });
                            return defer.promise;
                        }
                        angular.forEach(data.result.koulutusmoduuliToteutusTarjoajatiedot, function(tarjoajat) {
                            angular.forEach(tarjoajat.tarjoajaOids, function(tarjoaja) {
                                promises.push(checkCanEdit(tarjoaja));
                            });
                        });
                        $q.all(promises).then(function() {
                            return deferredPermission.resolve(canEdit);
                        });
                    });
                return deferredPermission.promise;
            }
            else {
                return undefined;
            }
        };
        var resolveCanCreateHakukohde = function(Hakukohde, $log, $route, SharedStateService, PermissionService) {
            var selectedTarjoajaOids;
            if (angular.isArray(SharedStateService.getFromState('SelectedOrgOid'))) {
                selectedTarjoajaOids = SharedStateService.getFromState('SelectedOrgOid');
            }
            else {
                selectedTarjoajaOids = [SharedStateService.getFromState('SelectedOrgOid')];
            }
            if (selectedTarjoajaOids !== undefined && selectedTarjoajaOids.length > 0 &&
                selectedTarjoajaOids[0] !== undefined) {
                $log.debug('CHECKING FOR CREATE : ', selectedTarjoajaOids);
                var canCreateVar = PermissionService.canCreate(selectedTarjoajaOids[0]);
                $log.debug('CREATE VAR : ', canCreateVar);
                return canCreateVar;
            }
            else {
                return undefined;
            }
        };
        function resolveKoulutus(TarjontaService, OrganisaatioService, $log, $route, $q) {
            $log.info('/koulutus/ID', $route);
            var defer = $q.defer();
            TarjontaService.getKoulutus({
                oid: $route.current.params.id
            }).$promise.then(function(res) {
                var promises = [];

                function getNoopPromise() {
                    var deferred = $q.defer();
                    deferred.resolve();
                    return deferred.promise;
                }

                promises.push(OrganisaatioService.getPopulatedOrganizations(
                    res.result.opetusTarjoajat, res.result.organisaatio.oid));

                if (res.result.opetusJarjestajat && res.result.opetusJarjestajat.length) {
                    promises.push(OrganisaatioService.getPopulatedOrganizations(res.result.opetusJarjestajat));
                }
                else {
                    promises.push(getNoopPromise());
                }

                if (res.result.toteutustyyppi === 'KORKEAKOULUOPINTO'
                        && !res.result.tarjoajanKoulutus) {
                    promises.push(TarjontaService.getJarjestettavatKoulutukset(
                        res.result.oid,
                        res.result.opetusJarjestajat
                    ));
                }
                else {
                    promises.push(getNoopPromise());
                }

                $q.all(promises).then(function(data) {
                    var tarjoajat = data[0];
                    var jarjestajat = data[1];
                    var jarjestettavatKoulutukset = data[2] || {};
                    // tarjoajat
                    res.result.organisaatiot = tarjoajat;
                    var nimet = '';
                    angular.forEach(tarjoajat, function(org) {
                        nimet += ' | ' + org.nimi;
                    });
                    res.result.organisaatioidenNimet = nimet.substring(3);
                    res.result.jarjestavatOrganisaatiot = jarjestajat;
                    res.result.jarjestettavatKoulutukset = jarjestettavatKoulutukset;

                    var reformin2018Alku = new Date(Date.UTC(2017, 11, 31, 22, 0, 0));
                    res.result.koulutusOnEnnen2018Reformia = _.some(res.result.koulutuksenAlkamisPvms,
                        function (alkamisTimestamp) {
                            return alkamisTimestamp < reformin2018Alku.getTime();
                        }) || (res.result.koulutuksenAlkamisvuosi && res.result.koulutuksenAlkamisvuosi < 2018);

                    defer.resolve(res);
                });
            });
            return defer.promise;
        }
        var newKoulutus = {
            action: 'koulutus.edit',
            controller: 'KoulutusRoutingController',
            resolve: {
                koulutusModel: function($log, $route) {
                    return {
                        result: {
                            toteutustyyppi: $route.current.params.toteutustyyppi,
                            koulutustyyppi: $route.current.params.koulutustyyppi,
                            isNew: true,
                            isMinmax: true
                        }
                    };
                }
            }
        };
        $routeProvider.when('/etusivu', {
            action: 'home.default',
            reloadOnSearch: false
        }).when('/error', {
            action: 'error' //,
        }).when('/index', {
            action: 'index',
            reloadOnSearch: false
        }).when('/etusivu/:oid', {
            action: 'home.default',
            reloadOnSearch: false
        }).when('/helpers/localisations', {
            action: 'helpers.localisations'
        }).when('/kk/edit/hakukohde', {
            action: 'kk.hakukohde.create'
        }).when('/koulutus/:id', {
            action: 'koulutus.review',
            controller: 'KoulutusRoutingController',
            resolve: {
                koulutusModel: resolveKoulutus
            }
        }).when('/koulutus/:id/edit', {
            action: 'koulutus.edit',
            controller: 'KoulutusRoutingController',
            resolve: {
                koulutusModel: resolveKoulutus
            }
        }).when('/koulutus/:id/jarjesta/:organisaatioOid', {
            action: 'koulutus.jarjesta',
            controller: 'KoulutusRoutingController',
            resolve: {
                koulutusModel: resolveKoulutus
            }
        }).when('/koulutus/:toteutustyyppi/:koulutustyyppi/edit/:org/:koulutuskoodi?', newKoulutus)
        .when('/koulutus/:toteutustyyppi/:koulutustyyppi/:koulutuslaji/edit/:org/:koulutuskoodi?', newKoulutus)
        .when('/valintaPerusteKuvaus/edit/:oppilaitosTyyppi/:kuvausTyyppi/NEW', {
            action: 'valintaPerusteKuvaus.edit',
            controller: 'ValintaperusteEditController'
        }).when('/valintaPerusteKuvaus/edit/:oppilaitosTyyppi/:kuvausTyyppi/:kuvausId', {
            action: 'valintaPerusteKuvaus.edit',
            controller: 'ValintaperusteEditController',
            resolve: {
                resolvedValintaPerusteKuvaus: function($route, Kuvaus) {
                    if ($route.current.params.kuvausId !== undefined && $route.current.params.kuvausId !== 'NEW') {
                        var kuvausPromise = Kuvaus.findKuvausWithId($route.current.params.kuvausId);
                        return kuvausPromise;
                    }
                }
            }
        }).when('/valintaPerusteKuvaus/edit/:oppilaitosTyyppi/:kuvausTyyppi/:kuvausId/COPY', {
            action: 'valintaPerusteKuvaus.edit',
            controller: 'ValintaperusteEditController',
            resolve: {
                resolvedValintaPerusteKuvaus: function($route, Kuvaus) {
                    if ($route.current.params.kuvausId !== undefined && $route.current.params.kuvausId !== 'NEW') {
                        var kuvausPromise = Kuvaus.findKuvausWithId($route.current.params.kuvausId);
                        return kuvausPromise;
                    }
                },
                action: function() {
                    return 'COPY';
                }
            }
        }).when('/valintaPerusteKuvaus/search', {
            action: 'valintaPerusteKuvaus.search',
            controller: 'ValintaperusteSearchController'
        }).when('/hakukohde/:id', {
            action: 'hakukohde.review',
            controller: 'HakukohdeRoutingController',
            resolve: {
                hakukohdex: resolveHakukohde
            }
        }).when('/hakukohde/:id/edit/copy', {
            action: 'hakukohde.edit',
            controller: 'HakukohdeRoutingController',
            resolve: {
                isCopy: function() {
                    return true;
                },
                canEdit: resolveCanEditHakukohde,
                canCreate: resolveCanCreateHakukohde,
                hakukohdex: resolveHakukohde
            }
        }).when('/hakukohde/:id/edit', {
            action: 'hakukohde.edit',
            controller: 'HakukohdeRoutingController',
            resolve: {
                canEdit: resolveCanEditHakukohde,
                canCreate: resolveCanCreateHakukohde,
                hakukohdex: resolveHakukohde
            }
        }).when('/haku', {
            action: 'haku.list',
            controller: 'HakuRoutingController',
            resolve: {
                hakus: function($log, $route) {
                    $log.info('/haku', $route);
                    return [
                        'foo',
                        'bar',
                        'zyzzy'
                    ];
                }
            }
        }).when('/haku/NEW', {
            action: 'haku.edit',
            controller: 'HakuRoutingController',
            resolve: {
                hakux: function($log, HakuV1Service) {
                    $log.debug('/haku/NEW');
                    return HakuV1Service.createNewEmptyHaku();
                }
            }
        }).when('/haku/:id', {
            action: 'haku.review',
            controller: 'HakuRoutingController',
            resolve: {
                hakux: function($log, $route, HakuV1) {
                    $log.debug('/haku/ID', $route);
                    return HakuV1.get({
                        oid: $route.current.params.id
                    }).$promise;
                }
            }
        }).when('/haku/:id/edit', {
            action: 'haku.edit',
            controller: 'HakuRoutingController',
            resolve: {
                hakux: function($log, $route, HakuV1) {
                    $log.debug('/haku/ID/edit', $route);
                    return HakuV1.get({
                        oid: $route.current.params.id
                    }).$promise;
                }
            }
        }).when('/koodistoTest', {
            action: 'koodistoTest'
        }).when('/komo', {
            action: 'komo',
            controller: 'KomoController'
        }).when('/import', {
            controller: 'ImportController',
            templateUrl: 'partials/import/import.html'
        }).otherwise({
            redirectTo: '/etusivu'
        });
    }
]);
angular.module('app').controller('AppRoutingCtrl', [
    '$scope',
    '$route',
    '$routeParams',
    '$log',
    'PermissionService', function($scope, $route, $routeParams, $log, PermissionService) {
        'use strict';
        $log = $log.getInstance('AppRoutingCtrl');
        $log.debug('init');
        $scope.count = 0;
        PermissionService.permissionResource().authorize({}, function(response) {
            $log.debug('Authorization check : ' + response.result);
        });
        var render = function() {
            $log.debug('render()');
            var renderAction = $route.current.action;
            var renderPath = renderAction ? renderAction.split('.') : [];

            /**
             * index.html:ssä olevan ng-switch määrityksen takia controlleria
             * ei alusteta uudelleen, jos se päätyy aina samaan ng-switch-when kohtaan (katso index.html).
             * Tämä on ongelma, koska esim. koulutuksen tarkastelunäkymästä pitää pystyä siirtymään
             * suoraan toisen koulutuksen tarkastelunäkymään ja se edellyttää controllerin alustamista.
             * Tällä kikalla varmistetaan se, että ng-switch match vaihtuu.
             */
            if (renderPath[1] && _.contains(['koulutus', 'hakukohde'], renderPath[0])) {
                renderPath[1] += $scope.count % 2 === 0 ? '_0' : '_1';
            }

            // Store the values in the model.
            $scope.renderAction = renderAction;
            $scope.renderPath = renderPath;
            $scope.routeParams = $routeParams ? $routeParams : {};
            $scope.count++;
            $log.debug('  renderAction: ', $scope.renderAction);
            $log.debug('  renderPath: ', $scope.renderPath);
            $log.debug('  routeParams: ', $scope.routeParams);
            $log.debug('  count: ', $scope.count);
        };
        $scope.$on('$routeChangeSuccess', function($currentRoute, $previousRoute) {
            $log.debug('$routeChangeSuccess : from, to = ', $currentRoute, $previousRoute);
            render();
        });
    }
]);
//
// "Production" mode
//
angular.module('app').config(function($logProvider) {
    'use strict';
    $logProvider.debugEnabled(true);
});

angular.module('app').factory('ajaxInterceptor', function(Config, $cookies) {
    'use strict';
    var callerid = Config.env['callerid.tarjonta.tarjonta-app.frontend'];
    return {
        request: function(config) {
            if (callerid) {
                config.headers['Caller-Id'] = callerid;
            }
            if ($cookies['CSRF']) {
                config.headers['CSRF'] = $cookies['CSRF'];
            }
            // Fix IE caching AJAX-requests to tarjonta-service.
            if (config.method === 'GET' && config.url.indexOf('/tarjonta-service/') !== -1) {
                config.headers['If-Modified-Since'] = 'Mon, 26 Jul 1997 05:00:00 GMT';
            }
            return config;
        }
    };
}).config(function($httpProvider) {
    'use strict';
    $httpProvider.interceptors.push('ajaxInterceptor');

    $httpProvider.interceptors.push(function transformHakukohdeLiitteet() {

        function convertLiitteetFromRestResponse(liitteetResponse) {
            var tmp = _.groupBy(liitteetResponse, 'jarjestys');
            var liitteet = [];
            var commonFields = ['liitteenTyyppi', 'toimitettavaMennessa', 'kaytetaanHakulomakkeella'];
            _.each(tmp, function(liite) {
                var liiteWithLangs = _.indexBy(liite, 'kieliUri');
                liiteWithLangs.commonFields = {};
                _.each(commonFields, function(key) {
                    liiteWithLangs.commonFields[key] = liite[0][key];
                });
                liitteet.push(liiteWithLangs);
            });
            return liitteet;
        }

        function convertLiitteetToRestResponse(liitteet) {
            liitteet = removeEmptyLiites(liitteet);
            return _.reduce(liitteet, function(memo, liiteWithLangs) {
                memo.push.apply(memo, _.filter(liiteWithLangs, function(liite, kieli) {
                    _.extend(liite, liiteWithLangs.commonFields);
                    return typeof liite === 'object' && kieli.indexOf('kieli_') !== -1;
                }));
                return memo;
            }, []);
        }

        function removeEmptyLiites(liitteet) {
            _.each(liitteet, function(liiteWithLangs) {
                _.each(liiteWithLangs, function(liite, lang) {
                    if (typeof liite !== 'object' || lang.indexOf('kieli_') === -1) {
                        return;
                    }
                    if (liite.isEmpty(liiteWithLangs.commonFields)) {
                        delete liiteWithLangs[lang];
                    }
                });
            });

            var nonEmptyLiitteet = _.filter(liitteet, function(liite) {
                return _.keys(liite).length > 0;
            });

            return nonEmptyLiitteet;
        }

        var activeTabs;

        return {
            request: function(config) {
                if (config.url.indexOf('hakukohde') !== -1 && config.data && config.data.hakukohteenLiitteet) {
                    // Älä muokkaa alkuperäistä objektia -> kopioidaan se
                    config.data = angular.copy(config.data);

                    activeTabs = {};
                    _.each(config.data.hakukohteenLiitteet, function(liiteWithLangs, index) {
                        var activeTab = _.findWhere(liiteWithLangs,  {tabActive: true});
                        if (activeTab) {
                            activeTabs[index] = activeTab.kieliUri;
                        }
                    });
                    config.data.hakukohteenLiitteet = convertLiitteetToRestResponse(config.data.hakukohteenLiitteet);
                }
                return config;
            },

            response: function(response) {
                if (response.config.url.indexOf('hakukohde/') !== -1 && response.data && response.data.result
                    && response.data.result.hakukohteenLiitteet) {

                    response.data.result.hakukohteenLiitteet = convertLiitteetFromRestResponse(
                        response.data.result.hakukohteenLiitteet
                    );
                    try {
                        _.each(activeTabs, function(kieliUri, index) {
                            response.data.result.hakukohteenLiitteet[index][kieliUri].tabActive = true;
                        });
                    } catch (e) {}
                }

                return response;
            }
        };
    });
});
