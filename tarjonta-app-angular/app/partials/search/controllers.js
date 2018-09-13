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
            hakukohteetActive: $scope.tabs.hakukohteet.active,
            jarjestettavatActive: $scope.tabs.jarjestettavat.active
        };
        return state;
    };
    $scope.$on('$destroy', function() {
        Cache.put('app.search.controllers', getSearchStateFromScope());
    });
    function setDefaultHakuehdot() {
        $scope.hakuehdot = SearchParameters.getDefaultHakuehdot();
    }
    setDefaultHakuehdot();
    if (SharedStateService.state.puut && SharedStateService.state.puut.organisaatio &&
        SharedStateService.state.puut.organisaatio.selected) {
        $routeParams.oid = SharedStateService.state.puut.organisaatio.selected;
    }
    if (SharedStateService.state.puut && SharedStateService.state.puut.organisaatio
        && SharedStateService.state.puut.organisaatio.scope !== $scope) {
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
                aktiiviset: true,
                suunnitellut: true,
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
        },
        jarjestettavat: {
            active: false
        }
    };
    $scope.selectedOrgOid = $routeParams.oid ? $routeParams.oid : selectOrg ? selectOrg : OPH_ORG_OID;
    $scope.hakukohdeResults = {};
    $scope.koulutusResults = {};
    $scope.jarjestettavatResults = {};
    $scope.oppilaitostyypit = SearchParameters.getOppilaitostyypit();
    $scope.organisaatiotyypit = SearchParameters.getOrganisaatiotyypit();
    $scope.spec = SearchParameters.getSpec();
    $scope.states = SearchParameters.getStates();
    $scope.years = SearchParameters.getYears();
    $scope.seasons = SearchParameters.fetchCodeElementsToObject('kausi');
    $scope.types = SearchParameters.getTypes();
    $scope.initAdditionalFields = function() {
        $scope.hakutapaoptions = SearchParameters.fetchCodeElementsToObject('hakutapa');
        $scope.hakutyyppioptions = SearchParameters.fetchCodeElementsToObject('hakutyyppi');
        $scope.koulutuslajioptions = SearchParameters.fetchCodeElementsToObject('koulutuslaji');
        $scope.kielioptions = SearchParameters.fetchCodeElementsToList('kieli');
        $scope.kohdejoukkooptions = SearchParameters.fetchCodeElementsToObject('haunkohdejoukko');
        $scope.oppilaitostyyppioptions = SearchParameters.fetchCodeElementsToObject('oppilaitostyyppi');
        $scope.kuntaoptions = SearchParameters.fetchCodeElementsToObject('kunta');
        $scope.koulutustyyppioptions = SearchParameters.fetchCodeElementsToObject('koulutustyyppi');
        OrganisaatioService.getRyhmat().then(function(ryhmat) {
            var lang = AuthService.getLanguage().toLowerCase();
            $scope.hakukohderyhmat = _.map(ryhmat, function(ryhma) {
                return {
                    key: ryhma.oid,
                    label: ryhma.nimi[lang] || ryhma.nimi[Object.keys(ryhma.nimi)[0]]
                };
            });
        });
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
            $scope.tabs.jarjestettavat.active = state.jarjestettavatActive;
        }
    };
    initFormSelections();
    $scope.toggleAdvancedSearch = function() {
        $scope.isopen = !$scope.isopen;
        if ($scope.isopen) {
            $scope.initAdditionalFields();
        }
    };
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
                if (row.toteutustyyppiEnum === 'KORKEAKOULUTUS') {
                    var locale = LocalisationService.getLocale();
                    if (!row.aloituspaikatKuvaukset) {
                        return row.aloituspaikat || '';
                    }
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
        copyIfSet(sargs, 'type', $scope.spec.type);
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
        koulutukset: {},
        hakukohteetFromTreeTable: {},
        hakukohteet: []
    };
    if(!$scope.selectedKoulutusOids) {
        $scope.selectedKoulutusOids = [];
    }

    $scope.$watch('selection.hakukohteetFromTreeTable', function(newObj, oldObj) {
        //console.log('selection.hakukohteetFromTreeTable', newObj);
        var hakukohdeOids = [];
        for (var key in newObj) {
            if (newObj.hasOwnProperty(key)) {
                hakukohdeOids.push(key);
            }
        }
        $scope.selection.hakukohteet = hakukohdeOids;
    }, true);

    $scope.$watch('selection.koulutukset', function(newObj, oldObj) {
        //console.log('selection.koulutukset', newObj);
        //Parse object into two separate arrays for easier handling
        var koulutusOids = []; // Valitut koulutukset
        var parentOids = []; // Valittujen koulutusten vanhemmat. Tämän listan pohjalta tarkistetaan oikeus luoda hakukohde: luojalla täytyy olla oikeus kaikkiin parentOideihin.
        for (var key in newObj) {
            if(newObj.hasOwnProperty(key)) {
                koulutusOids.push(key);
                if (newObj[key]) {
                    _.each(newObj[key], function (parentOid) {
                        if (parentOids.indexOf(parentOid) === -1) {
                            parentOids.push(parentOid);
                        }
                    });
                }
            }
        }
        //console.log('done parsing, koulutusOids: ', koulutusOids );
        //console.log('done parsing, parentOids: ', parentOids );
        $scope.selectedKoulutusOids = koulutusOids;

        if (!koulutusOids || koulutusOids.length === 0) {
            //mitään ei valittuna
            $scope.koulutusActions.canMoveOrCopy = false;
            $scope.koulutusActions.canCreateHakukohde = false;
            return;
        }
        else if (koulutusOids.length > 1) {
            //yli yksi valittuna
            $scope.koulutusActions.canMoveOrCopy = false;
        }
        else {
            //tasan yksi valittuna
            PermissionService.koulutus.canMoveOrCopy($scope.selectedKoulutusOids).then(function(result) {
                $scope.koulutusActions.canMoveOrCopy = result;
            });
        }

        //lopullinen tulos tallennetaan tänne (on oikeus luoda hakukohde jos oikeus kaikkien koulutusten tarjoajiin (parentOids)):
        var r = {
            result: true
        };
        _.each(parentOids, function(pOid) {
            console.log('Checking permission for parentOid:', pOid);
            PermissionService.hakukohde.canCreate(pOid).then(function (result) {
                r.result = r.result && result;
                $scope.koulutusActions.canCreateHakukohde = r.result;
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
    $scope.jarjestaKoulutusGetOptions = function(row, actions) {
        return RowActions.get('jarjestaKoulutus', row, actions, $scope);
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
        var jarjestettavatSpec = _.extend({}, spec, {
            defaultTarjoaja: null,
            oid: null,
            opetusJarjestajat: spec.oid
        });
        TarjontaService.haeKoulutukset(jarjestettavatSpec).then(function(data) {
            $scope.jarjestettavatResults = data;
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
    $scope.getJarjestettavatResultCount = function() {
        return getResultCount($scope.jarjestettavatResults);
    };
    $scope.luoKoulutusDisabled = function() {
        return !($scope.organisaatioValittu() && $scope.koulutusActions.canCreateKoulutus);
    };
    $scope.luoHakukohdeEnabled = function() {
        return $scope.selectedKoulutusOids !== undefined && $scope.selectedKoulutusOids.length > 0 &&
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
        if ($scope.selectedKoulutusOids.length === 0) {
            return; // napin pitäisi olla disabloituna, eli tätä ei pitäisi tapahtua, mutta varmuuden vuoksi..
        }
        var promises = [];
        angular.forEach($scope.selectedKoulutusOids, function(koulutusOid) {
            promises.push(TarjontaService.getKoulutusPromise(koulutusOid));
        });
        $q.all(promises).then(function(results) {
            var arrKomotoIds = [];
            _.each(results, function(res) {
                arrKomotoIds.push(res.result.oid);
            });
            HakukohdeKoulutukses.geValidateHakukohdeKomotos(arrKomotoIds).then(function(response) {
                if (response.status === 'OK' && $scope.selectedKoulutusOids.length === 1 && response.result &&
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
                        SharedStateService.addToState('firstSelectedKoulutus', results[0].result);
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
        var komotoOid = $scope.selectedKoulutusOids[0];

        var koulutus = _.chain($scope.koulutusResults.tulokset)
                            .pluck('tulokset')
                            .flatten()
                            .findWhere({oid: komotoOid}).value();

        $modal.open({
            templateUrl: 'partials/koulutus/copy/copy-move-koulutus.html',
            controller: 'CopyMoveKoulutusController',
            resolve: {
                targetKoulutus: function() {
                    return koulutus;
                },
                targetOrganisaatio: function() {
                    return {
                        oid: $scope.selectedOrgOid
                    };
                }
            }
        });
    };
    $scope.liitaHakukohteetRyhmaan = function() {
        HakukohderyhmatActions.liitaHakukohteetRyhmaan($scope);
    };
    function clearActiveTabs() {
        _.each($scope.tabs, function(tab) {
            tab.active = false;
        });
    }
    $scope.hakukohteetSelected = function() {
        clearActiveTabs();
        $scope.tabs.hakukohteet.active = true;
    };
    $scope.koulutuksetSelected = function() {
        clearActiveTabs();
        $scope.tabs.koulutukset.active = true;
    };
    $scope.jarjestettavatSelected = function() {
        clearActiveTabs();
        $scope.tabs.jarjestettavat.active = true;
    };
    $scope.$watch('jarjestettavatResults.tulokset.length', function(length) {
        if (length === 0 && $scope.tabs.jarjestettavat.active) {
            $scope.koulutuksetSelected();
        }
    });
});