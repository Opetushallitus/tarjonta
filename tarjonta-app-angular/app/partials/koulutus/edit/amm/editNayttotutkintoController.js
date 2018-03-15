var app = angular.module('app.edit.ctrl.amm', []);
app.controller('EditNayttotutkintoController', function($routeParams, $scope, $log,
                        TarjontaService, LocalisationService, KoulutusConverterFactory, $modal, dialogService) {
    var ENUMS = KoulutusConverterFactory.ENUMS;
    var reformin2018Alku = new Date(Date.UTC(2017, 11, 31, 22, 0, 0));
    /*
    * WATCHES
    */
    $scope.$watch('model.koulutusohjelma.uri', function(uri, oUri) {
        if (angular.isDefined(uri) && uri !== null && oUri !== uri) {
            if (angular.isDefined($scope.uiModel.koulutusohjelmaModules[uri])) {
                var komoOid = $scope.uiModel.koulutusohjelmaModules[uri].oid;
                TarjontaService.komo().tekstis({
                    oid: komoOid
                }, function(res) {
                    if (res.result.TAVOITTEET) {
                        $scope.uiModel.koulutusohjelmanTavoitteet = $scope.getLang(res.result.TAVOITTEET.tekstis);
                    }
                });
            }
        }
    });
    $scope.saveLuonnos = function() {
        $scope.saveByStatus('LUONNOS');
    };
    $scope.saveValmis = function() {
        $scope.saveByStatus('VALMIS');
    };
    $scope.saveByStatus = function(tila) {
        $scope.vkUiModel.showValidationErrors = true;
        var apiModel = angular.copy($scope.model);
        apiModel.toteutustyyppi = $scope.CONFIG.TYYPPI;
        if (apiModel.valmistavaKoulutus) {
            apiModel.valmistavaKoulutus = KoulutusConverterFactory.saveModelConverter(
                apiModel.valmistavaKoulutus,
                $scope.vkUiModel,
                ENUMS.ENUM_OPTIONAL_TOTEUTUS
            );
        }
        $scope.saveApimodelByStatus(apiModel, tila, $scope.koulutusForm, $scope.CONFIG.TYYPPI,
            $scope.callbackAfterSave);
    };
    $scope.getValmistavaKuvausApiModelLanguageUri = function(textEnum, kieliUri) {
        if (!kieliUri) {
            return {};
        }
        if (!$scope.uiModel.toggleTabs || $scope.model.valmistavaKoulutus === null) {
            return {};
        }
        var kuvaus = $scope.model.valmistavaKoulutus.kuvaus;
        if (angular.isUndefined(kuvaus[textEnum])) {
            kuvaus[textEnum] = {
                tekstis: {}
            };
            if (!angular.isUndefined(kieliUri)) {
                kuvaus[textEnum].tekstis[kieliUri] = '';
            }
        }
        return kuvaus[textEnum].tekstis;
    };
    $scope.openJarjestajaDialog = function() {
        var copyModalDialog = $modal.open({
            templateUrl: 'partials/koulutus/edit/amm/jarjestaja.html',
            controller: 'JarjestajaCtrl',
            resolve: {
                targetOrganisaatio: function() {
                    return {};
                }
            }
        });
        copyModalDialog.result.then(function(organisaatio) {
            /* ok */
            $scope.model.jarjestavaOrganisaatio = organisaatio;
        }, function() {});
    };
    $scope.$watch('model.valmistavaKoulutus.opintojenMaksullisuus', function(valNew) {
        if (!valNew && $scope.model.valmistavaKoulutus) {
            //clear price data field
            $scope.model.valmistavaKoulutus.hintaString = '';
        }
    });
    $scope.initValmistavaKoulutus = function(apimodel, uiModel, vkUiModel) {
        var model = {};
        $scope.commonNewModelHandler($scope.koulutusForm, model, vkUiModel, ENUMS.ENUM_OPTIONAL_TOTEUTUS);
        apimodel.valmistavaKoulutus = model;
        $scope.commonKoodistoLoadHandler(vkUiModel, ENUMS.ENUM_OPTIONAL_TOTEUTUS);
        vkUiModel.showValidationErrors = false;
        uiModel.toggleTabs = true;
    };
    $scope.$watch('uiModel.cbShowValmistavaKoulutus', function(valNew, valOld) {
        if (valNew && ($scope.model.valmistavaKoulutus === null ||
            !angular.isDefined($scope.model.valmistavaKoulutus))) {
            $scope.initValmistavaKoulutus($scope.model, $scope.uiModel, $scope.vkUiModel);
        }
        else if (valNew !== valOld && angular.isDefined($scope.model.valmistavaKoulutus)) {
            var modalInstance = $modal.open({
                scope: $scope,
                templateUrl: 'partials/koulutus/edit/amm/poista-valmistava-koulutus-dialog.html',
                controller: function($scope) {
                    $scope.ok = function() {
                        //delete
                        $scope.uiModel.cbShowValmistavaKoulutus = false;
                        $scope.uiModel.toggleTabs = false;
                        $scope.model.valmistavaKoulutus = null;
                        modalInstance.dismiss();
                    };
                    $scope.cancel = function() {
                        //do nothing.
                        $scope.uiModel.cbShowValmistavaKoulutus = true;
                        modalInstance.dismiss();
                    };
                    return $scope;
                }
            });
        }
    });
    $scope.onValmistavaLisatietoLangSelection = function(uris) {
        if (uris.removed && $scope.uiModel.opetuskielis.uris) {
            // ei opetuskieli -> varmista poisto dialogilla
            dialogService.showDialog({
                ok: LocalisationService.t('tarjonta.poistovahvistus.koulutus.lisatieto.poista'),
                title: LocalisationService.t('tarjonta.poistovahvistus.koulutus.lisatieto.title'),
                description: LocalisationService.t('tarjonta.poistovahvistus.koulutus.lisatieto',
                    [$scope.langs[uris.removed]])
            }).result.then(function(ret) {
                if (ret) {
                    $scope.deleteKuvausByStructureType($scope.CONFIG.TYYPPI, uris.removed);
                    if ($scope.model.valmistavaKoulutus && $scope.model.valmistavaKoulutus.kuvaus) {
                        for (var ki in $scope.model.valmistavaKoulutus.kuvaus) {
                            $scope.model.valmistavaKoulutus.kuvaus[ki].tekstis[uris.removed] = null;
                        }
                    }
                }
                else {
                    //cancelled remove, put uri back to the lang array
                    if ($scope.uiModel.lisatietoKielet.indexOf(uris.removed) === -1) {
                        $scope.uiModel.lisatietoKielet.push(uris.removed);
                    }
                }
            });
        }
        else if (uris.added && $scope.uiModel.lisatietoKielet) {
            if ($scope.uiModel.lisatietoKielet.indexOf(uris.added) === -1) {
                $scope.uiModel.lisatietoKielet.push(uris.added);
            }
        }
    };
    // Init generic edit controller (parent scope)
    $scope.init({
        model: {
            valmistavaKoulutus: null
        },
        uiModel: {
            //custom stuff
            toggleTabs: false,
            cbShowValmistavaKoulutus: false,
            enableOsaamisala: false,
            koulutusohjelma: [],
            tutkintoModules: {},
            koulutusohjelmaModules: {},
            valmistavaLisatiedot: KoulutusConverterFactory.STRUCTURE[ENUMS.ENUM_OPTIONAL_TOTEUTUS].KUVAUS_ORDER
        },
        childScope: $scope
    }, function initNayttotutkinto() {
            //valmistava koulutus
            var vkUiModel = {};
            var model = $scope.model;
            var uiModel = $scope.uiModel;
            /*
                 * HANDLE EDIT / CREATE NEW ROUTING
                 */
            if ($routeParams.id) {
                /*
                       *  SHOW KOULUTUS BY GIVEN KOMOTO OID
                       *  Look more info from koulutusController.js.
                       */
                if (model.valmistavaKoulutus) {
                    $scope.commonLoadModelHandler($scope.koulutusForm, model.valmistavaKoulutus, vkUiModel,
                        ENUMS.ENUM_OPTIONAL_TOTEUTUS);
                    $scope.commonKoodistoLoadHandler(vkUiModel, ENUMS.ENUM_OPTIONAL_TOTEUTUS);
                    vkUiModel.showValidationErrors = true;
                }
                uiModel.enableOsaamisala = angular.isDefined(model.koulutusohjelma.uri);
            }
            else if ($routeParams.org && model.koulutusOnEnnen2018Reformia) {
                $scope.initValmistavaKoulutus(model, uiModel, vkUiModel);
            }
            //Ui model for editValmistavaKoulutusPerustiedot and eeditValmistavaKoulutusLisatiedot pages (special case)
            $scope.vkUiModel = vkUiModel;
            if (model.valmistavaKoulutus) {
                $scope.uiModel.cbShowValmistavaKoulutus = true;
                $scope.uiModel.toggleTabs = true;
            }
            else {
                $scope.uiModel.cbShowValmistavaKoulutus = false;
            }
        });
});