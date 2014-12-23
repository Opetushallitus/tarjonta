var app = angular.module('app.kk.edit.valintaperustekuvaus.ctrl', [
    'app.services',
    'Haku',
    'Organisaatio',
    'Koodisto',
    'localisation',
    'Kuvaus',
    'auth',
    'config',
    'MonikielinenTextArea'
]);
app.controller('ValintaperusteEditController', function($scope, $rootScope, $route, $q, LocalisationService,
    OrganisaatioService, Koodisto, Kuvaus, AuthService, $modal, Config, $location, $timeout, YhteyshenkiloService) {
    /*

          --------------> Variable initializations

       */
    var commonExceptionMsgKey = 'tarjonta.common.unexpected.error.msg';
    $scope.model = {};
    $scope.model.years = [];
    $scope.model.validationmsgs = [];
    $scope.model.valintaperustekuvaus = {};
    $scope.model.valintaperustekuvaus.kuvauksenTyyppi = $route.current.params.kuvausTyyppi;
    $scope.model.valintaperustekuvaus.organisaatioTyyppi = $route.current.params.oppilaitosTyyppi;
    /**
       * 2.-asteen koulutukset eivät käytä organisaatioTyyppiä, vaan sen sijaan
       * avain-kenttää, joka on linkitetty koodistoon.
       */
    $scope.isToinenAste = $scope.model.valintaperustekuvaus.organisaatioTyyppi &&
        ($scope.model.valintaperustekuvaus.organisaatioTyyppi.indexOf('valintaperustekuvausryhma_') !== -1 ||
            $scope.model.valintaperustekuvaus.organisaatioTyyppi.indexOf('sorakuvaus_') !== -1);
    if ($scope.isToinenAste) {
        $scope.model.valintaperustekuvaus.avain = $scope.model.valintaperustekuvaus.organisaatioTyyppi;
        $scope.model.valintaperustekuvaus.organisaatioTyyppi = null;
    }
    //var kuvausId = $route.current.params.kuvausId;
    $scope.model.valintaperustekuvaus.kuvaukset = {};
    $scope.formControls = {};
    // controls-layouttia varten
    $scope.model.showError = false;
    $scope.model.showSuccess = false;
    $scope.model.nimiValidationFailed = false;
    /*

          -----------------> Helper and initialization functions etc.

       */
    var getYears = function() {
        var today = new Date();
        var currentYear = today.getFullYear();
        $scope.model.years.push(currentYear);
        var incrementYear = currentYear;
        var decrementYear = currentYear;
        for (var i = 0; i < 10; i++) {
            incrementYear++;
            if (i < 2) {
                decrementYear--;
                $scope.model.years.push(decrementYear);
            }
            $scope.model.years.push(incrementYear);
        }
        if ($scope.model.valintaperustekuvaus.vuosi === undefined) {
            $scope.model.valintaperustekuvaus.vuosi = currentYear;
        }
        $scope.model.years.sort();
    };
    var removeEmptyKuvaukses = function() {
        for (var langKey in $scope.model.valintaperustekuvaus.kuvaukset) {
            if ($scope.model.valintaperustekuvaus.kuvaukset[langKey].length < 1) {
                delete $scope.model.valintaperustekuvaus.kuvaukset[langKey];
            }
        }
    };
    var validateForm = function() {
        var retVal = true;
        var errorMsgs = [];
        if (!isNamesValid()) {
            errorMsg = {
                errorMessageKey: 'valintaperustekuvaus.validation.name.missing.exception'
            };
            errorMsgs.push(errorMsg);
            $scope.model.nimiValidationFailed = true;
            retVal = false;
        }
        if (!isKuvauksesValid()) {
            errorMsg = {
                errorMessageKey: 'valintaperustekuvaus.validation.kuvaus.missing.exception'
            };
            errorMsgs.push(errorMsg);
            $scope.model.nimiValidationFailed = true;
            retVal = false;
        }
        if (!$scope.valintaPerusteForm.$valid) {
            retVal = false;
        }
        if (errorMsgs.length > 0) {
            showError(errorMsgs);
        }
        return retVal;
    };
    var resetErrorMsgs = function() {
        $scope.model.validationmsgs = [];
        $scope.model.nimiValidationFailed = false;
        $scope.model.showError = false;
    };
    var isNamesValid = function() {
        for (var i in $scope.model.valintaperustekuvaus.kuvauksenNimet) {
            if ($scope.model.valintaperustekuvaus.kuvauksenNimet[i] &&
                $scope.model.valintaperustekuvaus.kuvauksenNimet[i].trim().length > 1) {
                return true;
            }
        }
        return false;
    };
    var isKuvauksesValid = function() {
        for (var langKey in $scope.model.valintaperustekuvaus.kuvaukset) {
            if ($scope.model.valintaperustekuvaus.kuvaukset[langKey] &&
                $scope.model.valintaperustekuvaus.kuvaukset[langKey].trim().length > 1) {
                return true;
            }
        }
        return false;
    };
    var showError = function(errorArray) {
        angular.forEach(errorArray, function(error) {
            if (error.errorTechnicalInformation &&
                    error.errorTechnicalInformation.indexOf('KUVAUS_ON_OLEMASSA_JO') !== -1) {
                $scope.model.validationmsgs.push('valintaperustekuvaus.validation.kuvaus_on_olemassa_jo');
                // TODO: poista alert ja käytä virhedialogia (controls-notify). Virhedialogissa on tällä hetkellä bugi,
                // mistä syystä tilapäinen ratkaisu on alert. - 2014-09-15
                alert(LocalisationService.t('valintaperustekuvaus.validation.kuvaus_on_olemassa_jo'));
            }
            else {
                var p = $scope.model.validationmsgs.indexOf(error.errorMessageKey);
                if (p == -1) {
                    $scope.model.validationmsgs.push(error.errorMessageKey);
                }
            }
        });
    };
    var showCommonUnknownErrorMsg = function() {
        var errors = [];
        var error = {};
        error.errorMessageKey = 'tarjonta.common.unexpected.error.msg';
        errors.push(error);
        showError(errors);
    };
    var createFormattedDateString = function(date) {
        return moment(date).format('DD.MM.YYYY HH:mm');
    };
    var initialializeForm = function() {
        $scope.model.userLang = AuthService.getLanguage();
        if ($scope.isToinenAste) {
            var koodisto = $scope.model.valintaperustekuvaus.avain.indexOf('sorakuvaus_') !== -1 ?
                'sorakuvaus' :
                'valintaperustekuvausryhma';
            Koodisto.getKoodi(koodisto, $scope.model.valintaperustekuvaus.avain).then(function(koodi) {
                $scope.model.valintaperustekuvausryhma = koodi.koodiNimi;
                $scope.model.valintaperustekuvaus.kuvauksenNimet = {
                    kieli_fi: koodi.koodiNimi
                };
            });
        }
        if ($route.current.locals.resolvedValintaPerusteKuvaus !== undefined) {
            $scope.model.valintaperustekuvaus = $route.current.locals.resolvedValintaPerusteKuvaus.result;
            if ($route.current.locals.action !== undefined && $route.current.locals.action === 'COPY') {
                $scope.model.valintaperustekuvaus.kuvauksenTunniste = undefined;
            }
            if ($scope.model.valintaperustekuvaus.modifiedBy !== undefined) {
                if ($scope.model.valintaperustekuvaus.created === undefined) {
                    $scope.model.valintaperustekuvaus.created = $scope.model.valintaperustekuvaus.modified;
                }
            }
        }
        else {
            console.log('DID NOT GET VALINTAPERUSTEKUVAUS');
        }
    };
    getYears();
    initialializeForm();
    /*

          ---------------->  Click etc. handlers

       */
    $scope.model.saveValmis = function() {
        var resultPromise;
        resetErrorMsgs();
        if (validateForm()) {
            removeEmptyKuvaukses();
            if ($scope.model.valintaperustekuvaus.kuvauksenTunniste === undefined) {
                resultPromise = Kuvaus.insertKuvaus($scope.model.valintaperustekuvaus.kuvauksenTyyppi,
                    $scope.model.valintaperustekuvaus);
                resultPromise.then(function(data) {
                    if (data.status === 'OK') {
                        $scope.model.valintaperustekuvaus = data.result;
                        $scope.model.showSuccess = true;
                    }
                    else {
                        showError(data.errors);
                        console.log('DID NOT GET OK : ', data);
                    }
                }, function(error) {
                        showCommonUnknownErrorMsg();
                    });
            }
            else {
                resultPromise = Kuvaus.updateKuvaus($scope.model.valintaperustekuvaus.kuvauksenTyyppi,
                    $scope.model.valintaperustekuvaus);
                resultPromise.then(function(data) {
                    if (data.status === 'OK') {
                        $scope.model.valintaperustekuvaus.modified = data.result.modified;
                        $scope.model.showSuccess = true;
                    }
                    else {
                        showError(data.errors);
                    }
                }, function(error) {
                        showCommonUnknownErrorMsg();
                    });
            }
        }
        else {
            $scope.model.showError = true;
            $scope.model.showSuccess = false;
        }
    };
    $scope.model.takaisin = function() {
        window.history.back(); // var oriUri = "/valintaPerusteKuvaus/search";
        // $location.path(oriUri);
    };
    $scope.model.canSaveVpk = function() {
        if ($scope.valintaPerusteForm !== undefined) {
            return $scope.valintaPerusteForm.$valid && validateForm();
        }
        else {
            return false;
        }
    };
    $scope.getNimetKey = function() {
        if ($scope.model.valintaperustekuvaus.kuvauksenTunniste === undefined) {
            return $scope.model.valintaperustekuvaus.kuvauksenTyyppi.toLowerCase() + '.edit.create.msg';
        }
        else {
            return $scope.model.valintaperustekuvaus.kuvauksenTyyppi.toLowerCase() + '.edit.update.msg';
        }
    };
    $scope.getNimet = function() {
        var nimi = '';
        if ($scope.model.valintaperustekuvaus.kuvauksenNimet !== undefined) {
            nimi = $scope.model.valintaperustekuvaus.kuvauksenNimet[LocalisationService.getLocale()];
            if (nimi === '' || nimi === undefined) {
                for (var key in $scope.model.valintaperustekuvaus.kuvauksenNimet) {
                    if ($scope.model.valintaperustekuvaus.kuvauksenNimet[key] !== '') {
                        nimi = $scope.model.valintaperustekuvaus.kuvauksenNimet[key];
                        break;
                    }
                }
            }
        }
        return nimi;
    };
});