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
var app = angular.module('app.haku.edit.ctrl', []);
/**
 * Haku edit controllers.
 * Note: current haku is preloaded in "tarjontaApp.js" route definitions. Extracted in "init()"-method.
 */
app.controller('HakuEditController', function HakuEditController($q, $route, $scope, $location,
     $log, $modal, LocalisationService, HakuV1, ParameterService, Config, OrganisaatioService,
     AuthService, dialogService, KoodistoURI, PermissionService, HakuV1Service, HAKUTAPA,
     HAKUTYYPPI, Koodisto, TarjontaService) {

    $log = $log.getInstance('HakuEditController');
    $log.debug('initializing (scope, route)', $scope, $route);
    // Reset model to empty
    $scope.model = null;
    var clearErrors = function() {
        $scope.model.validationmsgs = [];
        // NOTE data model for formControl seems to accumulate errors, clear it here even if the doc says no
        $scope.model.formControls.notifs.errorDetail = [];
    };
    var checkIsOphAdmin = function() {
        if (AuthService.isUserOph()) {
            $scope.filteruris = undefined;
        }
        else {
            $scope.filteruris = [];
            $scope.filteruris.push('hakutapa_01');
            $log.info('filteruris : ', $scope.filteruris);
        }
    };
    var reportFormValidationErrors = function(form) {
        $log.debug('reportFormValidationErrors - form:::::', form);
        $log.debug('form', form);
        angular.forEach(form.$error, function(v, k) {
            for (var i = 0; i < v.length; i++) {
                if (v[i].$name) {
                    var key = 'error.validation.' + v[i].$name + '.' + k;
                    $log.debug('k:' + key);
                    $scope.model.validationmsgs.push({
                        errorMessageKey: key
                    });
                }
                else {
                    $log.debug('error found for field:', v[i], 'key=', k);
                }
            }
        });
        $scope.model.showError = true;
        $scope.model.showSuccess = false;
    };
    $scope.doRemoveHakuaika = function(hakuaika, index) {
        $log.info('doRemoveHakuaika()', hakuaika, index);
        if ($scope.model.hakux.result.hakuaikas.length > 1) {
            $scope.model.hakux.result.hakuaikas.splice(index, 1);
        }
        else {
            $log.info('  cowardly refusing to remove the last hakuaika...');
        }
    };
    $scope.doAddNewHakuaika = function() {
        $log.info('doAddNewHakuaika()');
        $scope.model.hakux.result.hakuaikas.push({
            nimi: '',
            alkuPvm: null,
            loppuPvm: null
        });
    };
    function isDirty() {
        return $scope.modelInitialState &&
            !_.isEqual(angular.copy($scope.model.hakux.result), $scope.modelInitialState);
    }
    $scope.goBack = function() {
        if (isDirty()) {
            dialogService.showModifedDialog().result.then(function(result) {
                if (result) {
                    $scope.navigateBack();
                }
            });
        }
        else {
            $scope.navigateBack();
        }
    };
    $scope.navigateBack = function() {
        // TODO old query parameters?
        $location.path('/haku');
    };
    $scope.saveLuonnos = function(event, form) {
        $log.info('saveLuonnos()', event, form);
        var haku = $scope.model.hakux.result;
        $scope.doSaveHakuAndParameters(haku, 'LUONNOS', true, form);
    };
    $scope.saveValmis = function(event, form) {
        $log.info('saveValmis()', event, form);
        var haku = $scope.model.hakux.result;
        $scope.doSaveHakuAndParameters(haku, 'VALMIS', true, form);
    };

    function validateOpintokokonaisuusHakulomake(form) {
        var invalidCombo = $scope.model.hakux.result.koulutusmoduuliTyyppi === 'OPINTOKOKONAISUUS' &&
            $scope.model.haku.hakulomake === 'SYSTEM';
        form.hakulomake.$setValidity('opintokokonaisuus', !invalidCombo);
    }

    $scope.doSaveHakuAndParameters = function(haku, tila, reload, form) {
        clearErrors();

        validateOpintokokonaisuusHakulomake(form);

        if (form.$invalid) {
            reportFormValidationErrors(form);
            return;
        }
        // Update haku's tila (state)
        if (haku.tila != 'JULKAISTU') {
            //älä muuta julkaistun tilaa
            haku.tila = tila;
        }
        PermissionService.permissionResource().authorize({}, function(authResponse) {
            if (authResponse.status !== 'OK') {
                $scope.model.showError = true;
                $scope.model.showSuccess = false;
                $scope.model.validationmsgs.push({
                    errorMessageKey: 'error.auth'
                });
                return;
            }
            HakuV1.save(haku, function(result) {
                if ($scope.model.validationmsgs && $scope.model.validationmsgs.length > 0) {
                    $scope.model.validationmsgs.splice(0, $scope.model.validationmsgs.length);
                }
                $log.debug('validation messages after splice:', $scope.model.validationmsgs);
                if (result.status == 'OK') {
                    $scope.model.showError = false;
                    $scope.model.showSuccess = true;
                    form.$dirty = false;
                    form.$pristine = true;
                    $scope.saveParameters(result.result);
                    $scope.model.hakux = result;
                    $location.path('/haku/' + result.result.oid + '/edit');
                }
                else {
                    $scope.model.showError = true;
                    $scope.model.showSuccess = false;
                    $scope.model.validationmsgs = result.errors;
                }
                $scope.modelInitialState = null;
            }, function() {
                $scope.model.showError = true;
            });
        });
    };
    $scope.goToReview = function(event, hakuForm) {
        var dirty = angular.isDefined(hakuForm.$dirty) ? hakuForm.$dirty : false;
        $log.debug('goToReview(), dirty?', dirty);
        if (dirty) {
            dialogService.showModifedDialog().result.then(function(result) {
                if (result) {
                    $scope.navigateToReview();
                }
            });
        }
        else {
            $scope.navigateToReview();
        }
    };
    $scope.navigateToReview = function(event) {
        $location.path('/haku/' + $scope.model.hakux.result.oid);
    };
    $scope.isNewHaku = function() {
        var result = !angular.isDefined($scope.model.hakux.result.oid);
        // $log.debug("isNewHaku()", result);
        return result;
    };
    $scope.checkHaunNimiValidity = function() {
        if (!$scope.model.showError) {
            return false;
        }
        // Count number of keys that have content
        var numKeys = 0;
        var result = true;
        angular.forEach($scope.model.hakux.result.nimi, function(value, key) {
            numKeys++;
            result = result && !value;
        });
        if (numKeys === 0) {
            result = true;
        }
        return result;
    };
    $scope.getHaunNimi = function() {
        var nimi = $scope.model.hakux.result.nimi;
        var kielet = [
            LocalisationService.getKieliUri(),
            'kieli_fi',
            'kieli_sv',
            'kieli_en'
        ];
        var result;
        // Take first matching name in sequence: [current locale, fi, sv, en]
        angular.forEach(kielet, function(kieli) {
            if (!angular.isDefined(result) && angular.isDefined(nimi[kieli])) {
                result = nimi[kieli];
            }
        });
        if (!angular.isDefined(result)) {
            result = 'HAUN NIMI EI TIEDOSSA';
        }
        return result;
    };

    function appendKohdejoukkosByOppilaitostyyppi(tyyppi) {
        var promise;

        if (tyyppi === 'ALL') {
            promise = Koodisto.getAllKoodisWithKoodiUri('haunkohdejoukko');
        }
        else {
            promise = Koodisto.getYlapuolisetKoodiUrit([tyyppi], 'haunkohdejoukko');
        }

        promise.then(function(response) {
            var koodis = response;
            if (response.hasOwnProperty('map')) {
                koodis = response.map;
            }
            var options = _.map(koodis, function(koodi) {
                return {
                    nimi: koodi.koodiNimi,
                    uri: koodi.koodiUri + '#' + koodi.koodiVersio
                };
            });
            appendKohdejoukkos(options);
        });
    }

    function appendKohdejoukkos(koodis) {
        _.each(koodis, function(koodi) {
            if (!_.findWhere($scope.model.kohdejoukkoUris, {uri: koodi.uri})) {
                $scope.model.kohdejoukkoUris.push(koodi);
            }
        });
    }

    $scope.fetchKohdejoukkos = function() {
        $scope.model.kohdejoukkoUris = [];

        if (AuthService.isUserOph()) {
            appendKohdejoukkosByOppilaitostyyppi('ALL');
        }
        else {
            _.each(AuthService.getOrganisations(), function(org) {

                AuthService.crudOrg(org).then(function(isCrud) {
                    if (!isCrud) {
                        return;
                    }

                    OrganisaatioService.haeOppilaitostyypit(org).then(function(oppilaitosTyypit) {
                        _.each(oppilaitosTyypit, function(tyyppi) {
                            appendKohdejoukkosByOppilaitostyyppi(oph.removeKoodiVersion(tyyppi));
                        });
                    });
                });
            });
        }
    };

    $scope.isJatkuvaHaku = function() {
        var result = $scope.isHakuJatkuvaHaku($scope.model.hakux.result);
        // $log.info("isJatkuvaHaku()", result);
        return result;
    };
    $scope.saveParameters = function(haku) {
        $log.info('saveParameters()');
        ParameterService.tallennaUUSI(haku.oid, $scope.model.parameter);
    };
    /**
               * Loop throuh list of selected / preselected organisations, fetch them and put them to the scope for display purposes.
               *
               * @returns {undefined}
               */
    $scope.updateSelectedOrganisationsList = function() {
        $log.info('updateSelectedOrganisationsList()');
        $scope.model.selectedOrganisations = [];
        angular.forEach($scope.model.hakux.result.organisaatioOids, function(organisationOid) {
            $log.info('  get ', organisationOid);
            OrganisaatioService.byOid(organisationOid).then(function(organisation) {
                $log.info('    got ', organisation);
                $scope.model.selectedOrganisations.push(organisation);
            });
        });
    };
    /**
               * Opens dialog for selecting organisations.
               * Updates model for the list of selected organisations.
               *
               * @returns {undefined}
               */
    $scope.doSelectOrganisations = function() {
        $log.info('doSelectOrganisations()');
        var modalInstance = $modal.open({
            controller: 'HakuEditSelectOrganisationsController',
            templateUrl: 'partials/haku/edit/select-organisations-dialog.html',
            resolve: {
                organisaatioOids: function() {
                    return $scope.model.hakux.result.organisaatioOids;
                },
                treeId: function() {
                    return 'org1';
                }
            } // , scope: $scope
        });
        modalInstance.result.then(function(oids) {
            $log.debug('OK - dialog closed with selected organisations: ', oids);
            $scope.model.hakux.result.organisaatioOids = oids;
            $scope.updateSelectedOrganisationsList();
        }, function(oids) {});
    };
    /**
               * Loop throuh list of selected / preselected tarjoaja organisations,
               * fetch them and put them to the scope for display purposes.
               *
               * @returns {undefined}
               */
    $scope.updateSelectedTarjoajaOrganisationsList = function() {
        $log.info('updateSelectedTarjoajaOrganisationsList()');
        $scope.model.selectedTarjoajaOrganisations = [];
        angular.forEach($scope.model.hakux.result.tarjoajaOids, function(organisationOid) {
            $log.info('  get ', organisationOid);
            OrganisaatioService.byOid(organisationOid).then(function(organisation) {
                $log.info('    got ', organisation);
                $scope.model.selectedTarjoajaOrganisations.push(organisation);
            });
        });
    };
    /**
               * Opens dialog for selecting tarjoaja organisations.
               * Updates model for the list of selected tarjoaja organisations.
               *
               * @returns {undefined}
               */
    $scope.doSelectTarjoajaOrganisations = function() {
        $log.info('doSelectTarjoajaOrganisations()');
        var modalInstance = $modal.open({
            controller: 'HakuEditSelectOrganisationsController',
            templateUrl: 'partials/haku/edit/select-organisations-dialog.html',
            resolve: {
                organisaatioOids: function() {
                    return $scope.model.hakux.result.tarjoajaOids;
                },
                treeId: function() {
                    return 'org2';
                }
            } // , scope: $scope
        });
        modalInstance.result.then(function(oids) {
            $log.debug('OK - dialog closed with selected tarjoaja organisations: ', oids);
            $scope.model.hakux.result.tarjoajaOids = oids;
            $scope.updateSelectedTarjoajaOrganisationsList();
        }, function(oids) {});
    };
    /**
               * Kutsutaan haen edit lomakkeelta kun haun priorisoinnin tilaan halutaan vaikuttaa "ulkopuolelta"
               * eli muuttamalla haun lomakkeen valintaa.
               *
               * @returns {undefined}
               */
    $scope.checkPriorisointi = function() {
        $log.debug('checkPriorisointi()');
        if ($scope.model.hakux.result.jarjestelmanHakulomake
            && $scope.model.hakux.result.sijoittelu) {
            $scope.model.hakux.result.usePriority = true;
        }
        if (!$scope.model.hakux.result.jarjestelmanHakulomake) {
            $scope.model.hakux.result.usePriority = false;
        }
    };
    /**
               * This method is called when halulomake selection changes.
               *
               * Accepted states are: SYSTEM, OTHER, NONE
               *
               * @returns {undefined}
               */
    $scope.updatedHakulomakeSelection = function() {
        $log.info('updatedHakulomakeSelection() - ', $scope.model.haku.hakulomake);
        switch ($scope.model.haku.hakulomake) {
            case 'SYSTEM':
                $log.info('  handle system.');
                $scope.model.hakux.result.jarjestelmanHakulomake = true;
                $scope.model.hakux.result.hakulomakeUri = null;
                break;
            case 'OTHER':
                $log.info('  handle other.');
                $scope.model.hakux.result.jarjestelmanHakulomake = false;
                $scope.model.hakux.result.maxHakukohdes = 0;
                break;
            case 'NONE':
                $log.info('  handle none.');
                $scope.model.hakux.result.jarjestelmanHakulomake = false;
                $scope.model.hakux.result.maxHakukohdes = 0;
                $scope.model.hakux.result.hakulomakeUri = null;
                break;
            default:
                $log.info('  handle WTF?.');
                throw new Exception('INVALID HAKULOMAKE TYPE');
        }
        // Update priorisointi information too
        $scope.checkPriorisointi();
    };
    /**
               * Use this method to sync UI state to model state
               *
               * @returns {undefined}
               */
    $scope.updatedHakulomakeSelectionFromModelToUI = function() {
        $log.info('updatedHakulomakeSelectionFromModelToUI()');
        if ($scope.model.hakux.result.jarjestelmanHakulomake) {
            $scope.model.haku.hakulomake = 'SYSTEM';
        }
        else {
            if ($scope.model.hakux.result.hakulomakeUri) {
                $scope.model.haku.hakulomake = 'OTHER';
            }
            else {
                $scope.model.haku.hakulomake = 'NONE';
            }
        }
    };
    /**
               * Strip version information from given koodisto uri.
               * Ie. "foo_1#2" becomes "foo_1".
               */
    $scope.stripVersionFromKoodistoUri = function(uri) {
        uri = uri || '';
        var result = uri.replace(/#.*/, '');
        // $log.info("stripVersionFromKoodistoUri()", uri, result);
        return result;
    };
    $scope.setInitialState = function(state) {
        $scope.modelInitialState = state;
    };
    /**
     * Tallenna modelin tila ennen käyttäjän tekemiä muutoksia, jotta
     * voidaan tarvittaessa ilmoittaa tallentamattomista tiedoista jne.
     */
    $scope.setDirtyListener = function() {
        $('body').on('focus mouseenter', '#editHaku .tab-content:first *', function(e) {
            e.stopPropagation();
            if (!$scope.modelInitialState) {
                $scope.setInitialState(angular.copy($scope.model.hakux.result));
            }
        });
    };
    $scope.initKoulutuksienTyypit = function() {
        $scope.koulutuksienTyypit = [];
        $scope.koulutuksienTyypit.push({key: 'TUTKINTO',
            label: LocalisationService.t('haku.edit.koulutuksenTyyppi.tutkinto')});
        $scope.koulutuksienTyypit.push({key: 'OPINTOKOKONAISUUS',
            label: LocalisationService.t('haku.edit.koulutuksenTyyppi.opintokokonaisuusOpintojakso')});
    };
    $scope.init = function() {
        var model = {
            formControls: {},
            showError: false,
            showSuccess: false,
            validationmsgs: [],
            collapse: {
                model: true
            },
            hakux: $route.current.locals.hakux,
            haku: {
                // Possible UI state for Haku
                hakulomake: 'SYSTEM' // Possible values SYSTEM, OTHER, NONE
            },
            parameter: {},
            selectedOrganisations: [],
            selectedTarjoajaOrganisations: [],
            config: Config.env
        };
        $scope.model = model;
        $scope.model.parentHakuCandidates = [];
        // Update UI state for radio buttons on load
        $scope.updatedHakulomakeSelectionFromModelToUI();
        if (!$scope.isNewHaku()) {
            // lataa nykyiset parametrit model.parameter objektiin
            ParameterService.haeParametritUUSI($route.current.params.id).then(function(parameters) {
                model.parameter = parameters;
            });
        }
        if ($scope.isNewHaku()) {
            $scope.model.hakux.result.organisaatioOids = AuthService.getOrganisations();
            $scope.model.hakux.result.tarjoajaOids = AuthService.getOrganisations();
        }
        $scope.updateSelectedOrganisationsList();
        $scope.updateSelectedTarjoajaOrganisationsList();
        $scope.fetchKohdejoukkos();
        checkIsOphAdmin();
        if ($scope.shouldSelectParentHaku()) {
            populateParentHakuCandidates();
        }
        $scope.setDirtyListener();
        $scope.initKoulutuksienTyypit();
    };
    $scope.isLuonnosOrNew = function() {
        return $scope.isNewHaku() || $scope.model.hakux.result.tila === 'LUONNOS';
    };
    var populateParentHakuCandidates = function() {
        $scope.model.parentHakuCandidates = [];
        var params = {};
        params.KOHDEJOUKKO = $scope.model.hakux.result.kohdejoukkoUri;
        params.HAKUKAUSI = $scope.model.hakux.result.hakukausiUri;
        params.HAKUVUOSI = $scope.model.hakux.result.hakukausiVuosi;
        params.TILA = 'NOT_POISTETTU';
        params.count = 1000;
        HakuV1Service.search(params).then(function(hakus) {
            // Palauta vain ne haut, joissa hakutapa on yhteishaku tai erillishaku
            // ja hakutyyppi on varsinainen haku
            var filtered = _.filter(hakus, function(haku) {
                return [
                        HAKUTAPA.YHTEISHAKU,
                        HAKUTAPA.ERILLISHAKU
                    ].indexOf(oph.removeKoodiVersion(haku.hakutapaUri)) !== -1 &&
                    oph.removeKoodiVersion(haku.hakutyyppiUri) === HAKUTYYPPI.VARSINAINEN_HAKU;
            });
            var promises = [];
            _.each(filtered, function(haku) {
                promises.push(PermissionService.haku.canEdit(haku));
            });
            $q.all(promises).then(function(data) {
                _.each(data, function(hasAccess, index) {
                    if (hasAccess === true) {
                        $scope.model.parentHakuCandidates.push(filtered[index]);
                    }
                });
            });
        });
    };
    var updateParentHakuFields = function() {
        $scope.model.hakux.result.parentHakuOid = undefined;
        if ($scope.shouldSelectParentHaku()) {
            populateParentHakuCandidates();
        }
    };
    var updateKoulutusmoduuliTyyppiField = function() {
        if ($scope.model.hakux.result.kohdejoukkoUri &&
            oph.removeKoodiVersion($scope.model.hakux.result.kohdejoukkoUri) === 'haunkohdejoukko_12') {
            $scope.showKoulutusmoduuliTyyppi = true;
        } else {
            $scope.showKoulutusmoduuliTyyppi = false;
            $scope.model.hakux.result.koulutusmoduuliTyyppi = undefined;
        }
    };
    $scope.$watch('model.hakux.result.hakutyyppiUri', function(nv, old) {
        if (nv !== old) {
            updateParentHakuFields();
        }
    });
    $scope.$watch('model.hakux.result.kohdejoukkoUri', function(nv, old) {
        if (nv !== old) {
            updateParentHakuFields();
        }
        updateKoulutusmoduuliTyyppiField();
    });
    $scope.$watch('model.hakux.result.hakukausiUri', function(nv, old) {
        if (nv !== old) {
            updateParentHakuFields();
        }
    });
    $scope.$watch('model.hakux.result.hakukausiVuosi', function(nv, old) {
        if (nv !== old) {
            updateParentHakuFields();
        }
    });
    $scope.shouldSelectParentHaku = function() {
        if (!$scope.model.hakux.result.hakutyyppiUri
            || !$scope.model.hakux.result.kohdejoukkoUri
            || !$scope.model.hakux.result.hakukausiUri
            || !$scope.model.hakux.result.hakukausiVuosi) {
            return false;
        }
        return $scope.model.hakux.result.hakutyyppiUri.indexOf(HAKUTYYPPI.LISAHAKU) !== -1;
    };
    $scope.init();
    var hakuOid = $route.current.params.id;
    var hasHakukohdes = false;
    if (!$scope.isNewHaku()) {
        $q.all([
            PermissionService.haku.canEdit(hakuOid),
            PermissionService.haku.canDelete(hakuOid),
            HakuV1Service.checkStateChange({
                oid: hakuOid,
                state: 'POISTETTU'
            })
        ]).then(function(results) {
            $scope.isMutable = results[0];
            $scope.isRemovable = results[1] && results[2];
        });
        PermissionService.getPermissions('haku', hakuOid).then(function(permissions) {
            $log.info('got permissions! ', permissions);
        });

        TarjontaService.haeHakukohteet({hakuOid: hakuOid}).then(function(result) {
            hasHakukohdes = result.tuloksia > 0;
        });
    }
    else {
        //uusi haku
        $scope.isMutable = true;
    }
    /**
     * Koulutuksen alkamiskautta ei saa muuttaa, jos hakuun on jo liitetty hakukohteita
     */
    $scope.isKoulutuksenAlkamiskausiLocked = function() {
        return hasHakukohdes && !AuthService.isUserOph();
    };
});