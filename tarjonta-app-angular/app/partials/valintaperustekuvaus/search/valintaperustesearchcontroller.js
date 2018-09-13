var app = angular.module('app.kk.search.valintaperustekuvaus.ctrl', [
    'app.services',
    'Haku',
    'Organisaatio',
    'Koodisto',
    'localisation',
    'Kuvaus',
    'auth',
    'config'
]);
app.controller('ValintaperusteSearchController', function($scope, $rootScope, $route, $q, LocalisationService,
        Koodisto, Kuvaus, AuthService, $location, dialogService, OrganisaatioService, CommonUtilService, $modal, $log,
        PermissionService) {
    var oppilaitosKoodistoUri = 'oppilaitostyyppi';
    var kausiKoodistoUri = 'kausi';
    $scope.selection = {
        valintaperusteet: [],
        sorat: []
    };
    $scope.model = {};
    $scope.model.searchSpec = {};
    $scope.model.kuvaustyyppis = [
        'valintaperustekuvaus',
        'SORA'
    ];
    $scope.model.valintaperusteet = [];
    $scope.model.sorat = [];
    $scope.model.years = [];
    $scope.model.userLang = AuthService.getLanguage();
    $scope.model.userOrgTypes = [];
    $scope.valintaperusteColumns = [
        'kuvauksenNimi',
        'organisaatioTyyppi',
        'vuosikausi'
    ];
    var oppilaitosTyyppiPromises = [];
    var getUserOrgs = function() {
        $log.info('AUTH SERVICE : ', AuthService.getUsername());
        $log.info('AUTH SERVICE FIRST NAME : ', AuthService.getFirstName());
        if (!AuthService.isUserOph()) {
            $log.info('USER IS NOT OPH, GETTING ORGANIZATIONS....');
            $log.info('USER ORGANIZATIONS : ', AuthService.getOrganisations());
            OrganisaatioService.etsi({
                aktiiviset: true,
                suunnitellut: true,
                oidRestrictionList: AuthService.getOrganisations()
            }).then(function(data) {
                getOppilaitosTyyppis(data.organisaatiot);
            });
        }
    };
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
        $scope.model.years.sort();
    };
    var checkForUserOrgs = function() {
        //If user has not selected anything for oppilaitostyyppi and has oppilaitostyyppis in userOrgTypes, then
        //it is safe to assume that user is not ophadmin so restrict the query with first available oppilaitostyyppi
        if ($scope.model.searchSpec.oppilaitosTyyppi === undefined && $scope.model.userOrgTypes.length > 0) {
            $scope.model.searchSpec.oppilaitosTyyppi = $scope.model.userOrgTypes[0];
        }
    };
    var showCreateNewDialog = function(vpkTyyppiParam) {
        var modalInstance;
        var template = $scope.canEditValintaperustekuvaus2Aste() ?
            'uusi-valintaperuste-choose-type-dialog.html' :
            'uusi-valintaperuste-dialog.html';
        modalInstance = $modal.open({
            templateUrl: 'partials/valintaperustekuvaus/search/' + template,
            controller: 'LuoUusiValintaPerusteDialog',
            windowClass: 'valintakoe-modal',
            resolve: {
                filterUris: function() {
                    return $scope.model.userOrgTypes;
                },
                selectedUri: function() {
                    return $scope.model.searchSpec.oppilaitosTyyppi;
                },
                Tyyppi: function() {
                    return vpkTyyppiParam;
                }
            }
        });
        modalInstance.result.then(function(result) {
            var kuvausEditUri;
            if (result.valintaperustekuvausryhma) {
                kuvausEditUri = '/valintaPerusteKuvaus/edit/' + result.valintaperustekuvausryhma +
                    '/' + vpkTyyppiParam + '/NEW';
            }
            else if (result.selectedOrgType) {
                kuvausEditUri = '/valintaPerusteKuvaus/edit/' + result.selectedOrgType +
                    '/' + vpkTyyppiParam + '/NEW';
            }
            $location.path(kuvausEditUri);
        });
    };
    function koodistoToMap(res) {
        var ret = {};
        for (var i in res) {
            var k = res[i];
            ret[k.koodiUri] = k.koodiNimi;
            ret[k.koodiUri + '#' + k.koodiVersio] = k.koodiNimi;
        }
        return ret;
    }
    var resolveKausi = function(kuvaukset, doAfter) {
        var resolvedKuvaukset = [];
        Koodisto.getAllKoodisWithKoodiUri(kausiKoodistoUri, $scope.model.userLang).then(function(res) {
            var koodisto = koodistoToMap(res);
            for (var i in kuvaukset) {
                var v = kuvaukset[i];
                v.kausiNimi = koodisto[v.kausi] || v.kausi;
            }
            doAfter();
        });
    };
    var resolveOppilaitosTyyppi = function(kuvaukset, doAfter) {
        Koodisto.getAllKoodisWithKoodiUri(oppilaitosKoodistoUri, $scope.model.userLang).then(function(res) {
            var koodisto = koodistoToMap(res);
            for (var i in kuvaukset) {
                var v = kuvaukset[i];
                v.organisaatioTyyppiNimi = koodisto[v.organisaatioTyyppi] || v.organisaatioTyyppi;
            }
            doAfter();
        });
    };
    var getOppilaitosTyyppis = function(organisaatiot) {
        angular.forEach(organisaatiot, function(organisaatio) {
            var oppilaitosTyypitPromise = CommonUtilService.haeOppilaitostyypit(organisaatio);
            oppilaitosTyyppiPromises.push(oppilaitosTyypitPromise);
        });
        //Resolve all promises and filter oppilaitostyyppis with user types
        $q.all(oppilaitosTyyppiPromises).then(function(data) {
            $scope.model.userOrgTypes = removeHashAndVersion(data);
        });
    };
    var removeHashAndVersion = function(oppilaitosTyyppis) {
        var oppilaitosTyyppisWithOutVersion = new buckets.Set();
        angular.forEach(oppilaitosTyyppis, function(oppilaitosTyyppiUri) {
            angular.forEach(oppilaitosTyyppiUri, function(oppilaitosTyyppiUri) {
                var splitStr = oppilaitosTyyppiUri.split('#');
                oppilaitosTyyppisWithOutVersion.add(splitStr[0]);
            });
        });
        return oppilaitosTyyppisWithOutVersion.toArray();
    };
    var localizeKuvausNames = function(kuvaukses) {
        angular.forEach(kuvaukses, function(kuvaus) {
            for (var prop in kuvaus.kuvauksenNimet) {
                if (kuvaus.kuvauksenNimet.hasOwnProperty(prop)) {
                    if (prop.indexOf($scope.model.userLang)) {
                        if (kuvaus.kuvauksenNimet[prop] && kuvaus.kuvauksenNimet[prop].trim().length > 1) {
                            kuvaus.kuvauksenNimi = kuvaus.kuvauksenNimet[prop];
                        }
                    }
                }
            }
        });
    };
    var removeKuvaus = function(kuvaus, doAfter) {
        var modalInstance = $modal.open({
            templateUrl: 'partials/valintaperustekuvaus/remove/poista.html',
            controller: 'PoistaValintaperustekuvausCtrl',
            resolve: {
                selectedKuvaus: function() {
                    return kuvaus;
                }
            }
        });
        modalInstance.result.then(function() {
            doAfter();
        });
    };
    getUserOrgs();
    getYears();
    $scope.selectKuvaus = function(kuvaus) {
        if (kuvaus.organisaatioTyyppi !== undefined) {
            var kuvausEditUri = '/valintaPerusteKuvaus/edit/' + kuvaus.organisaatioTyyppi + '/' +
                kuvaus.kuvauksenTyyppi + '/' + kuvaus.kuvauksenTunniste;
            $location.path(kuvausEditUri);
        }
    };
    $scope.canEditValintaperustekuvaus2Aste = function() {
        return AuthService.isUserOph();
    };
    $scope.canCreateNew = function() {
        return PermissionService.kuvaus.canCreateToinenAste() || PermissionService.kuvaus.canCreateKK();
    };
    $scope.createNew = function(kuvausTyyppi) {
        if ($scope.model.userOrgTypes.length > 0 && $scope.model.userOrgTypes.length < 2) {
            var kuvausEditUri = '/valintaPerusteKuvaus/edit/' + $scope.model.userOrgTypes[0] + '/'
                + kuvausTyyppi + '/NEW';
            $location.path(kuvausEditUri);
        }
        else {
            showCreateNewDialog(kuvausTyyppi);
        }
    };
    $scope.removeKuvaus = function(kuvaus) {
        var texts = {
            title: LocalisationService.t('valintaperuste.list.remove.title'),
            description: LocalisationService.t('valintaperuste.list.remove.desc'),
            ok: LocalisationService.t('ok'),
            cancel: LocalisationService.t('cancel')
        };
        var d = dialogService.showDialog(texts);
        d.result.then(function(data) {
            if (data) {
                removeKuvaus(kuvaus);
            }
        });
    };
    $scope.copyKuvaus = function(kuvaus) {
        var kuvausEditUri = '/valintaPerusteKuvaus/edit/' + $scope.model.userOrgTypes[0] + '/' +
            kuvaus.kuvauksenTyyppi + '/' + kuvaus.kuvauksenTunniste + '/COPY';
        $location.path(kuvausEditUri);
    };
    $scope.search = function() {
        $scope.model.valintaperusteet = [];
        $scope.model.sorat = [];
        checkForUserOrgs();
        angular.forEach($scope.model.kuvaustyyppis, function(tyyppi) {
            var searchPromise = Kuvaus.findKuvauksesWithSearchSpec($scope.model.searchSpec, tyyppi);
            searchPromise.then(function(resultData) {
                if (resultData.status === 'OK') {
                    localizeKuvausNames(resultData.result);
                    resolveOppilaitosTyyppi(resultData.result, function() {
                        resolveKausi(resultData.result, function() {
                            if (tyyppi === $scope.model.kuvaustyyppis[0]) {
                                $scope.model.valintaperusteet = resultData.result;
                            }
                            else if (tyyppi === $scope.model.kuvaustyyppis[1]) {
                                $scope.model.sorat = resultData.result;
                            }
                        });
                    });
                }
            });
        });
    };
    $scope.reset = function() {
        $scope.model.searchSpec = {};
    };
    $scope.kuvauksetGetContent = function(row, col) {
        switch (col) {
            case 'kausi':
                return row.kausiNimi + ' ' + row.vuosi;
            case 'oppilaitostyyppi':
                return row.organisaatioTyyppiNimi;
            default:
                return row.kuvauksenNimi;
        }
    };
    $scope.kuvauksetGetIdentifier = function(row) {
        return row.kuvauksenTunniste;
    };
    $scope.kuvauksetGetLink = function(row) {
        var type = row.avain ? row.avain : $scope.model.userOrgTypes[0];
        return '#/valintaPerusteKuvaus/edit/' + type + '/' + row.kuvauksenTyyppi + '/' + row.kuvauksenTunniste;
    };
    var isToinenAste = function(row) {
        return row.avain !== undefined;
    };
    var getEditOption = function(row) {
        return {
            title: LocalisationService.t('tarjonta.toiminnot.muokkaa'),
            href: $scope.kuvauksetGetLink(row)
        };
    };
    var getDeleteOption = function(row) {
        return {
            title: LocalisationService.t('tarjonta.toiminnot.poista'),
            action: function() {
                removeKuvaus(row, row.$delete);
            }
        };
    };
    var getCopyOptions = function(row) {
        return {
            title: LocalisationService.t('tarjonta.toiminnot.kopioi'),
            action: function() {
                $scope.copyKuvaus(row);
            }
        };
    };
    $scope.kuvauksetGetOptions = function(row) {
        var options = [];
        if (isToinenAste(row)) {
            if (PermissionService.kuvaus.canUpdateToinenAste()) {
                options.push(getEditOption(row));
            }
            if (PermissionService.kuvaus.canDeleteToinenAste()) {
                options.push(getDeleteOption(row));
            }
            if (PermissionService.kuvaus.canCopyToinenAste()) {
                options.push(getCopyOptions(row));
            }
        } else {
            if (PermissionService.kuvaus.canUpdateKK()) {
                options.push(getEditOption(row));
            }
            if (PermissionService.kuvaus.canDeleteKK()) {
                options.push(getDeleteOption(row));
            }
            if (PermissionService.kuvaus.canCopyKK()) {
                options.push(getCopyOptions(row));
            }
        }
        return options;
    };
});
app.controller('LuoUusiValintaPerusteDialog', function($scope, $modalInstance, LocalisationService, Koodisto,
                                                        filterUris, selectedUri, Tyyppi) {
    $scope.dialog = {
        type: Tyyppi
    };
    $scope.dialog.userOrgTypes = [];
    var initializeDialog = function() {
        if (filterUris !== undefined && filterUris.length > 0) {
            $scope.dialog.userOrgTypes = filterUris;
        }
        if (selectedUri !== undefined) {
            $scope.dialog.selectedType = selectedUri;
        }
    };
    $scope.onCancel = function() {
        $modalInstance.dismiss('cancel');
    };
    $scope.onOk = function() {
        $modalInstance.close({
            selectedOrgType: $scope.dialog.selectedType,
            valintaperustekuvausryhma: $scope.dialog.valintaperustekuvausryhma
        });
    };
    initializeDialog();
});