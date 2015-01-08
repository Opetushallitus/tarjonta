angular.module('app.search.controllers', [
    'app.services',
    'localisation',
    'Organisaatio',
    'config',
    'ResultsTreeTable',
    'search.hakutulokset.rows',
    'search.hakutulokset.hakukohteet',
    'search.hakutulokset.searchparameters'
]).factory('Cache', function($cacheFactory) {
    return $cacheFactory('app.search.controllers');
}).controller('SearchController', function($scope, $routeParams, $location, $cacheFactory, LocalisationService,
                                           Koodisto, OrganisaatioService, TarjontaService,
                                           PermissionService, Config, loadingService, $modal, $window,
                                           SharedStateService, AuthService, $q, dialogService,
                                           HakukohdeKoulutukses, RowActions, HakukohderyhmatActions,
                                           SearchParameters, Cache) {
    'use strict';

    var OPH_ORG_OID = Config.env['root.organisaatio.oid'];
    var selectOrg;
    //organisaation vaihtuessa suoritettavat toimenpiteet
    $scope.$watch('selectedOrgOid', function(newObj) {
        if (newObj) {
            //päivitä permissio
            PermissionService.koulutus.canCreate(newObj).then(function(data) {
                $scope.koulutusActions.canCreateKoulutus = data;
            });
            //päivitä nimi
            OrganisaatioService.nimi(newObj).then(function(nimi) {
                $scope.selectedOrgName = nimi;
                //päivitä lokaatio
                updateLocation();
            });
        }
    });
    var getSearchStateFromScope = function() {
        var state = {
            advancedSearchOpen: $scope.isopen,
            koulutuksetActive: $scope.tabs.koulutukset.active,
            hakukohteetActive: $scope.tabs.hakukohteet.active
        };
        return state;
    };
    $scope.$on('$destroy', function() {
        Cache.put('app.search.controllers', getSearchStateFromScope());
    });
    function setDefaultHakuehdot() {
        $scope.hakuehdot = SearchParameters.getDefaultHakuehdot();
        $scope.koulutustyyppioptions = SearchParameters.fetchCodeElementsToObject('koulutustyyppi');
    }
    setDefaultHakuehdot();
    if (SharedStateService.state.puut && SharedStateService.state.puut.organisaatio &&
        SharedStateService.state.puut.organisaatio.selected) {
        $routeParams.oid = SharedStateService.state.puut.organisaatio.selected;
    }
    if (SharedStateService.state.puut && SharedStateService.state.puut.organisaatio.scope !== $scope) {
        SharedStateService.state.puut.organisaatio.scope = $scope;
    }
    var orgs = AuthService.getOrganisations([
        'APP_TARJONTA_CRUD',
        'APP_TARJONTA_UPDATE',
        'APP_TARJONTA_READ'
    ]);
    function getDefaultOrg() {
        if (orgs && orgs.length > 0) {
            return orgs[0];
        }
    }
    if (getDefaultOrg() && !$routeParams.oid) {
        selectOrg = getDefaultOrg();
        if (orgs.indexOf(OPH_ORG_OID) == -1) {
            OrganisaatioService.etsi({
                oidRestrictionList: orgs
            }).then(function(vastaus) {
                $scope.$root.tulos = vastaus.organisaatiot;
            });
        }
    }
    $scope.tabs = {
        koulutukset: {
            active: true
        },
        hakukohteet: {
            active: false
        }
    };
    $scope.selectedOrgOid = $routeParams.oid ? $routeParams.oid : selectOrg ? selectOrg : OPH_ORG_OID;
    $scope.hakukohdeResults = {};
    $scope.koulutusResults = {};
    $scope.oppilaitostyypit = SearchParameters.getOppilaitostyypit();
    $scope.organisaatiotyypit = SearchParameters.getOrganisaatiotyypit();
    $scope.spec = SearchParameters.getSpec();
    $scope.states = SearchParameters.getStates();
    $scope.years = SearchParameters.getYears();
    $scope.seasons = SearchParameters.fetchCodeElementsToObject('kausi');
    $scope.initAdditionalFields = function() {
        $scope.hakutapaoptions = SearchParameters.fetchCodeElementsToObject('hakutapa');
        $scope.hakutyyppioptions = SearchParameters.fetchCodeElementsToObject('hakutyyppi');
        $scope.koulutuslajioptions = SearchParameters.fetchCodeElementsToObject('koulutuslaji');
        $scope.kielioptions = SearchParameters.fetchCodeElementsToList('kieli');
        $scope.kohdejoukkooptions = SearchParameters.fetchCodeElementsToObject('haunkohdejoukko');
        $scope.oppilaitostyyppioptions = SearchParameters.fetchCodeElementsToObject('oppilaitostyyppi');
        $scope.kuntaoptions = SearchParameters.fetchCodeElementsToObject('kunta');
    };
    $scope.selectLanguage = function(item) {
        $scope.spec.addLanguage(item);
        $scope.languageSearch = '';
    };
    $scope.removeLanguage = function(key) {
        $scope.spec.removeLanguage(key);
    };
    var initFormSelections = function() {
        var state = Cache.get('app.search.controllers');
        if (state) {
            $scope.isopen = state.advancedSearchOpen;
            if ($scope.isopen) {
                $scope.initAdditionalFields();
            }
            $scope.tabs.koulutukset.active = state.koulutuksetActive;
            $scope.tabs.hakukohteet.active = state.hakukohteetActive;
        }
    };
    initFormSelections();
    $scope.organisaatio = {};
    $scope.$watch('organisaatio.currentNode', function() {
        if ($scope.organisaatio && angular.isObject($scope.organisaatio.currentNode)) {
            $scope.selectedOrgOid = $scope.organisaatio.currentNode.oid;
            $scope.selectedOrgName = $scope.organisaatio.currentNode.nimi;
            $scope.search();
        }
    }, false);
    $scope.organisaatioValittu = function() {
        return $routeParams.oid && $routeParams.oid !== OPH_ORG_OID;
    };
    $scope.hakukohdeColumns = [
        'hakutapa',
        'aloituspaikat',
        'koulutuslaji'
    ];
    $scope.koulutusColumns = ['koulutuslaji'];
    $scope.tuloksetGetContent = function(row, col) {
        switch (col) {
            case undefined:
            case null:
                return row.nimi;
            case 'tila':
                // HUOM! hakutulostun mukana tulevaa käännöstä ei voida käyttää koska tila voi muuttua riviä päivitettäessä
                return LocalisationService.t('tarjonta.tila.' + row.tila);
            case 'kausi':
                var ks = row.kausi ? row.kausi[LocalisationService.getLocale()] : '';
                var vs = row.vuosi | '';
                return ks + ' ' + vs;
            case 'aloituspaikat':
                if (row.koulutusasteTyyppi === 'KORKEAKOULUTUS') {
                    var locale = LocalisationService.getLocale();
                    return row.aloituspaikatKuvaukset[locale] || row.aloituspaikatKuvaukset.fi ||
                        row.aloituspaikatKuvaukset.sv || row.aloituspaikatKuvaukset.en ||
                        row.aloituspaikatKuvaukset[Object.keys(row.aloituspaikatKuvaukset)[0]];
                }
                else {
                    return row.aloituspaikat;
                }
                break;
            default:
                return row[col];
        }
    };
    $scope.tuloksetGetChildren = function(row) {
        return row.tulokset;
    };
    $scope.tuloksetGetIdentifier = function(row) {
        return row.tulokset === undefined && row.oid;
    };
    $scope.koulutusGetLink = function(row) {
        return row.tulokset === undefined && '#/koulutus/' + row.oid;
    };
    $scope.hakukohdeGetLink = function(row) {
        return row.tulokset === undefined && '#/hakukohde/' + row.oid;
    };
    $scope.submitOrg = function() {
        var hakutulos = OrganisaatioService.etsi($scope.hakuehdot);
        hakutulos.then(function(vastaus) {
            $scope.$root.tulos = vastaus.organisaatiot;
        });
    };
    $scope.setDefaultOrg = function() {
        $scope.selectedOrgOid = getDefaultOrg();
    };
    $scope.resetOrg = function() {
        setDefaultHakuehdot();
    };
    if (!$scope.selectedOrgName) {
        OrganisaatioService.nimi($scope.selectedOrgOid).then(function(nimi) {
            $scope.selectedOrgName = nimi;
        });
    }
    function copyIfSet(dst, key, value, def) {
        if (value !== null && value !== undefined && (value + '').length > 0 && value != '*') {
            dst[key] = value;
        }
        else if (def !== undefined) {
            dst[key] = def;
        }
    }
    function updateLocation() {
        var sargs = {};
        copyIfSet(sargs, 'terms', $scope.spec.attributes.terms, '*');
        copyIfSet(sargs, 'state', $scope.spec.attributes.state);
        copyIfSet(sargs, 'year', $scope.spec.attributes.year);
        copyIfSet(sargs, 'season', $scope.spec.attributes.season);
        if ($scope.selectedOrgOid !== null) {
            $location.path('/etusivu/' + $scope.selectedOrgOid);
        }
        else {
            $location.path('/etusivu');
        }
        $location.search(sargs);
    }
    $scope.clearOrg = function() {
        $scope.selectedOrgOid = OPH_ORG_OID;
        $scope.$broadcast('clearOrg');
    };
    $scope.reset = function() {
        $scope.spec.reset();
    };
    $scope.selection = {
        koulutukset: [],
        hakukohteet: []
    };
    $scope.$watch('selection.koulutukset', function(newObj, oldObj) {
        if (!newObj || newObj.length === 0) {
            //mitään ei valittuna
            $scope.koulutusActions.canMoveOrCopy = false;
            $scope.koulutusActions.canCreateHakukohde = false;
            return;
        }
        else if (newObj.length > 1) {
            //yksi valittuna
            $scope.koulutusActions.canMoveOrCopy = false;
        }
        else {
            PermissionService.koulutus.canMoveOrCopy(newObj).then(function(result) {
                $scope.koulutusActions.canMoveOrCopy = result;
            });
        }
        //lopullinen tulos tallennetaan tänne (on oikeus luoda hakukohde jos oikeus kaikkiin koulutuksiin):
        var r = {
            result: true
        };
        TarjontaService.haeKoulutukset({
            koulutusOid: newObj
        }).then(function(koulutukset) {
            _.each((koulutukset || {}).tulokset, function(tulos) {
                PermissionService.hakukohde.canCreate(tulos.oid).then(function(result) {
                    r.result = r.result && result;
                    $scope.koulutusActions.canCreateHakukohde = r.result;
                });
            });
        });
    }, true);
    $scope.menuOptions = [];
    $scope.koulutusActions = {
        canMoveOrCopy: false,
        canCreateHakukohde: false,
        canCreateKoulutus: false
    };
    $scope.koulutusGetOptions = function(row, actions) {
        return RowActions.get('koulutus', row, actions, $scope);
    };
    $scope.hakukohdeGetOptions = function(row, actions) {
        return RowActions.get('hakukohde', row, actions, $scope);
    };
    $scope.search = function() {
        var spec = $scope.spec.getSpecForSearchQuery($scope.selectedOrgOid);
        updateLocation();
        TarjontaService.haeKoulutukset(spec).then(function(data) {
            $scope.koulutusResults = data;
        });
        TarjontaService.haeHakukohteet(spec).then(function(data) {
            $scope.hakukohdeResults = data;
        });
    };
    function getResultCount(res) {
        return res && res.tuloksia && res.tuloksia > 0 ? ' (' + res.tuloksia + ')' : '';
    }
    $scope.getKoulutusResultCount = function() {
        return getResultCount($scope.koulutusResults);
    };
    $scope.getHakukohdeResultCount = function() {
        return getResultCount($scope.hakukohdeResults);
    };
    $scope.luoKoulutusDisabled = function() {
        return !($scope.organisaatioValittu() && $scope.koulutusActions.canCreateKoulutus);
    };
    $scope.luoHakukohdeEnabled = function() {
        return $scope.selection.koulutukset !== undefined && $scope.selection.koulutukset.length > 0 &&
            $scope.koulutusActions.canCreateHakukohde;
    };
    if ($scope.spec.attributes.terms == '*') {
        $scope.spec.attributes.terms = '';
    }
    if ($scope.selectedOrgOid !== OPH_ORG_OID || $scope.spec.filtersActive()) {
        // estää angularia tuhoamasta "liian nopeasti" haettua hakutuloslistausta
        // TODO ei toimi luotettavasti -> korjaa
        setTimeout($scope.search, 100);
    }
    $scope.luoUusiHakukohde = function() {
        if ($scope.selection.koulutukset.length === 0) {
            return; // napin pitäisi olla disabloituna, eli tätä ei pitäisi tapahtua, mutta varmuuden vuoksi..
        }
        var promises = [];
        angular.forEach($scope.selection.koulutukset, function(koulutusOid) {
            promises.push(TarjontaService.getKoulutusPromise(koulutusOid));
        });
        $q.all(promises).then(function(results) {
            var arrKomotoIds = [];
            _.each(results, function(res) {
                arrKomotoIds.push(res.result.oid);
            });
            HakukohdeKoulutukses.geValidateHakukohdeKomotos(arrKomotoIds).then(function(response) {
                if (response.status === 'OK' && $scope.selection.koulutukset.length === 1 && response.result &&
                    response.result.toteutustyyppis && response.result.toteutustyyppis.length === 1) {
                    SharedStateService.addToState('SelectedKoulutukses', arrKomotoIds);
                    SharedStateService.addToState('SelectedToteutusTyyppi', response.result.toteutustyyppis[0]);
                    SharedStateService.addToState('SelectedOrgOid', $scope.selectedOrgOid);
                    SharedStateService.addToState('firstSelectedKoulutus', results[0].result);
                    $location.path('/hakukohde/new/edit');
                }
                else {
                    var invalidSelection = $modal.open({
                        templateUrl: 'partials/hakukohde/dialog/valitse-koulutus-dialog.html',
                        controller: 'ValitseKoulutusDialogCtrl',
                        resolve: {
                            input: function() {
                                return {
                                    response: response,
                                    locale: LocalisationService.getLocale() ? LocalisationService.getLocale() : 'fi'
                                };
                            }
                        }
                    });
                    invalidSelection.result.then(function(response) {
                        /* ok */
                        SharedStateService.addToState('SelectedKoulutukses', response.oids);
                        SharedStateService.addToState('SelectedToteutusTyyppi', response.toteutustyyppi);
                        SharedStateService.addToState('SelectedOrgOid', $scope.selectedOrgOid);
                        $location.path('/hakukohde/new/edit');
                    }, function() {});
                }
            });
        });
    };
    /**
     * Avaa "luoKoulutus 1. dialogi"
     */
    $scope.openLuoKoulutusDialogi = function() {
        //aseta esivalittu organisaatio
        $scope.luoKoulutusDialogOrg = $scope.selectedOrgOid;
        $scope.luoKoulutusDialog = $modal.open({
            scope: $scope,
            templateUrl: 'partials/koulutus/luo-koulutus-dialogi.html',
            controller: 'LuoKoulutusDialogiController'
        });
    };
    $scope.siirraTaiKopioi = function() {
        var komotoOid = $scope.selection.koulutukset[0];
        //single select
        var koulutusNimi;
        var organisaatioNimi;
        var stop = false;
        for (var i = 0; i < $scope.koulutusResults.tulokset.length; i++) {
            var org = $scope.koulutusResults.tulokset;
            for (var c = 0; c < org[i].tulokset.length; c++) {
                if (komotoOid === org[i].tulokset[c].oid) {
                    koulutusNimi = org[i].tulokset[c].nimi;
                    stop = true;
                    break;
                }
            }
            if (stop) {
                break;
            }
        }
        $modal.open({
            templateUrl: 'partials/koulutus/copy/copy-move-koulutus.html',
            controller: 'CopyMoveKoulutusController',
            resolve: {
                targetKoulutus: function() {
                    return [{
                        oid: komotoOid,
                        nimi: koulutusNimi
                    }];
                },
                targetOrganisaatio: function() {
                    return {
                        oid: $scope.selectedOrgOid,
                        nimi: organisaatioNimi
                    };
                }
            }
        });
    };
    $scope.liitaHakukohteetRyhmaan = function() {
        HakukohderyhmatActions.liitaHakukohteetRyhmaan($scope);
    };
    $scope.hakukohteetSelected = function() {
        $scope.tabs.hakukohteet.active = true;
        $scope.tabs.koulutukset.active = false;
    };
    $scope.koulutuksetSelected = function() {
        $scope.tabs.hakukohteet.active = false;
        $scope.tabs.koulutukset.active = true;
    };
});