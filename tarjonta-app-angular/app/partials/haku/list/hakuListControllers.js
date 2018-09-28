var app = angular.module('app.haku.list.ctrl', [
    'app.services',
    'localisation',
    'Organisaatio',
    'config',
    'ResultsTreeTable',
    'app.haku.list.ctrl.searchparameters'
]).factory('Cache', function($cacheFactory) {
    return $cacheFactory('app.haku.list.ctrl');
}).controller('HakuListController', function($q, $scope, $location, $window, $modal,
                   LocalisationService, HakuV1, dialogService, HakuV1Service, Koodisto,
                   PermissionService, OrganisaatioService, AuthService, HakuSearchParameters, Cache) {

        $scope.selection = [];
        $scope.spec = HakuSearchParameters.getSpec();
        $scope.kausi = HakuSearchParameters.fetchCodeElementsToObject('kausi');
        $scope.vuosi = HakuSearchParameters.getYears();
        $scope.hakutyypit = HakuSearchParameters.fetchCodeElementsToObject('hakutyyppi');
        $scope.states = HakuSearchParameters.getStates();
        $scope.selectedOrganisation = undefined;

        var getSearchStateFromScope = function() {
            var state = {
                selectedOrganisation: $scope.selectedOrganisation,
            };
            return state;
        };
        $scope.$on('$destroy', function() {
            Cache.put('app.haku.list.ctrl', getSearchStateFromScope());
        });
        $scope.clearSearch = function() {
            $scope.spec.reset();
        };
        $scope.doCreateNew = function() {
            $location.path('/haku/NEW');
        };
        $scope.canCreateNew = function() {
            // TODO käyttöoikeustarkistus
            return true;
        };
        function kausiVuosiToString(kausi, vuosi) {
            if (kausi) {
                var kausiNimi = _.findWhere($scope.kausi, {key: oph.removeKoodiVersion(kausi)});
                kausiNimi = kausiNimi && kausiNimi.label;
                return kausiNimi + ' ' + vuosi;
            } else {
                return vuosi;
            }
        }
        function hakutyyppiToString(hakutyyppiUri) {
            var tyyppi = _.find($scope.hakutyypit, function(element) {
                if (element.key === hakutyyppiUri.split('#')[0]) {
                    return element;
                }
            });
            return tyyppi && tyyppi.label;
        }
        $scope.hakuGetContent = function(row, col) {
            switch (col) {
                case 'hakutyyppi':
                    return hakutyyppiToString(row.hakutyyppiUri);
                case 'hakukausi':
                    return kausiVuosiToString(row.hakukausiUri, row.hakukausiVuosi);
                case 'alkamiskausi':
                    return kausiVuosiToString(
                        row.koulutuksenAlkamiskausiUri,
                        row.koulutuksenAlkamisVuosi
                    );
                case 'tila':
                    return LocalisationService.t('tarjonta.tila.' + row.tila);
                default:
                    return row.nimi;
            }
        };
        $scope.hakuGetIdentifier = function(row) {
            return row.oid;
        };
        $scope.hakuGetLink = function(row) {
            return '#/haku/' + row.oid;
        };
        $scope.doDelete = function(haku, doAfter) {
            $scope.doDeleteHaku(haku).then(function(result) {
                if (result) {
                    doAfter();
                }
            });
        };
        $scope.doDeleteSelected = function() {
            HakuV1.mget({
                oid: $scope.selection
            }).$promise.then(function(hakus) {
                if (hakus.status === 'OK') {
                    angular.forEach(hakus.result, function(haku) {
                        $scope.doDelete(haku, function() {
                            for (var i in $scope.model.hakus) {
                                if ($scope.model.hakus[i].oid == haku.oid) {
                                    $scope.model.hakus[i].$delete();
                                }
                            }
                        });
                    });
                }
            });
        };
        $scope.canDeleteSelected = function() {
            // TODO käyttöoikeustarkistus -> jos ei saa poistaa mitään, palauta false
            return $scope.selection.length > 0;
        };
        $scope.review = function(haku) {
            $location.path('/haku/' + haku.oid);
        };
        function changeState(targetState, prefix) {
            var title = LocalisationService.t(prefix + '.confirmation.title');
            var description = LocalisationService.t(prefix + '.confirmation.description');
            var okAckTitle = LocalisationService.t(prefix + '.ack.title');
            var okAckDescription = LocalisationService.t(prefix + '.ack.description');
            var errorAckTitle = LocalisationService.t(prefix + '.error.ack.title');
            var errorAckDescription = LocalisationService.t(prefix + '.error.ack.description');
            return function(haku, doAfter, onlyHaku) {
                function after(ackTitle, ackDescription) {
                    dialogService.showSimpleDialog(
                        ackTitle,
                        ackDescription,
                        LocalisationService.t('ok')
                    ).result.then(function(ok) {});
                }
                function change(haku, doAfter) {
                    HakuV1.changeState({
                        oid: haku.oid,
                        state: targetState,
                        'onlyHaku': onlyHaku || false
                    }).$promise.then(function(result) {
                        if ('OK' === result.status) {
                            haku.tila = targetState;
                            doAfter();
                            after(okAckTitle, okAckDescription);
                        }
                        else {
                            after(errorAckTitle, errorAckDescription);
                        }
                    }, function(reason) {});
                }
                return dialogService.showSimpleDialog(
                    title,
                    description,
                    LocalisationService.t('ok'),
                    LocalisationService.t('cancel')
                ).result.then(function(ok) {
                    if (ok) {
                        return change(haku, doAfter);
                    }
                    else {
                        return false;
                    }
                });
            };
        }
        $scope.doPublish = changeState('JULKAISTU', 'haku.publish');
        $scope.doCancel = changeState('PERUTTU', 'haku.cancel');
        $scope.hakuGetOptions = function(haku) {
            var ret = [];
            $q.all([
                PermissionService.haku.canEdit(haku),
                PermissionService.haku.canDelete(haku),
                HakuV1.checkStateChange({
                    oid: haku.oid,
                    state: 'JULKAISTU'
                }).$promise,
                HakuV1.checkStateChange({
                    oid: haku.oid,
                    state: 'PERUTTU'
                }).$promise
            ]).then(function(results) {
                if (true === results[0]) {
                    ret.push({
                        title: LocalisationService.t('haku.menu.muokkaa'),
                        action: function() {
                            $location.path('/haku/' + haku.oid + '/edit');
                        }
                    });
                }
                ret.push({
                    title: LocalisationService.t('haku.menu.tarkastele'),
                    action: function() {
                        $scope.review(haku);
                    }
                });
                if (true === results[1] && haku.tila != 'JULKAISTU') {
                    ret.push({
                        title: LocalisationService.t('haku.menu.poista'),
                        action: function() {
                            $scope.doDelete(haku, haku.$delete);
                        }
                    });
                }
                if (true === results[0] && true === results[2].result) {
                    ret.push({
                        title: LocalisationService.t('haku.menu.julkaise'),
                        action: function() {
                            $scope.doPublish(haku, haku.$update, true);
                        }
                    });
                    if (AuthService.isUserOph()) {
                        ret.push({
                            title: LocalisationService.t('haku.menu.julkaise.rekursiivisesti'),
                            action: function() {
                                $scope.doPublish(haku, haku.$update);
                            }
                        });
                    }
                }
                if (true === results[0] && true === results[3].result && haku.tila != 'PERUTTU') {
                    ret.push({
                        title: LocalisationService.t('haku.menu.peruuta'),
                        action: function() {
                            $scope.doCancel(haku, haku.$update);
                        }
                    });
                }
            });
            return ret;
        };
        $scope.doSearch = function() {
            /**
            * Allow user only to select values included in the typeahead response. If value is not
            * in the response (user wrote manually something else) => clear the input to indicate that
            * the filter value is invalid.
            */
            if (typeof $scope.selectedOrganisation === 'string') {
                $scope.selectedOrganisation = '';
                $scope.spec.attributes.TARJOAJAOID = undefined;
            }
            var params = angular.copy($scope.spec.attributes);
            if (params.KAUSIVUOSI && params.KAUSI) {
                params[params.KAUSIVUOSI + 'KAUSI'] = params.KAUSI;
            }
            if (params.KAUSIVUOSI && params.VUOSI) {
                params[params.KAUSIVUOSI + 'VUOSI'] = params.VUOSI;
            }
            // pois turhat parametrit
            params.KAUSIVUOSI = undefined;
            params.KAUSI = undefined;
            params.VUOSI = undefined;
            // Oletuksena haetaan kaikki muut, paitsi poistetut
            if (!params.TILA) {
                params.TILA = 'NOT_POISTETTU';
            }
            HakuV1Service.search(params).then(function(haut) {
                // TODO järjestys backendiin?
                haut.sort(function(a, b) {
                    return a.nimi.localeCompare(b.nimi);
                });
                angular.forEach(haut, function(haku) {
                    if (haku.koulutuksenAlkamisVuosi === 0) {
                        haku.koulutuksenAlkamisVuosi = '';
                    }
                });
                $scope.model.hakus = haut;
            });
        };
        $scope.searchOrganisations = function(qterm) {
            /**
            * Recursively process the organizations results, so that all the children are also
            * included in the final result set.
            */
            function processOrganizations(alreadyAddedOrganizations, newOrganizations,
                                          hierarchyLevel) {
                hierarchyLevel = hierarchyLevel || 0;
                angular.forEach(newOrganizations, function(org) {
                    org.nimi = new Array(hierarchyLevel + 1).join('- ') + org.nimi;
                    alreadyAddedOrganizations.push(org);
                    if (angular.isArray(org.children)) {
                        alreadyAddedOrganizations = processOrganizations(
                            alreadyAddedOrganizations,
                            org.children,
                            hierarchyLevel + 1
                        );
                    }
                });
                return alreadyAddedOrganizations;
            }
            return OrganisaatioService.etsi({
                searchStr: qterm,
                lakkautetut: false,
                skipparents: false,
                suunnitellut: true,
                aktiiviset: true
            }).then(function(result) {
                return processOrganizations([], result.organisaatiot);
            });
        };
        $scope.filterByOrganisation = function(organization) {
            $scope.selectedOrganisation = organization;
            $scope.spec.attributes.TARJOAJAOID = organization.oid;
            function includeChildren(parent) {
                angular.forEach(parent.children, function(child) {
                    $scope.spec.attributes.TARJOAJAOID += ',' + child.oid;
                    includeChildren(child);
                });
            }
            includeChildren(organization);
        };
        $scope.initOrganisationFilter = function() {
            if (!$scope.selectedOrganisation) {
                var organisations = AuthService.getOrganisations();
                if (angular.isArray(organisations) && organisations.length > 0) {
                    var organisation = organisations[0];
                    OrganisaatioService.byOid(organisation).then(function(resultOrganisation) {
                        $scope.filterByOrganisation(resultOrganisation);
                        $scope.doSearch();
                    });
                }
            } else {
                $scope.doSearch();
            }
        };
        $scope.init = function() {
            var model = {
                collapse: {
                    model: true
                },
                search: {
                    tila: ''
                },
                hakus: [],
                place: 'holder'
            };
            var state = Cache.get('app.haku.list.ctrl');
            if (state) {
                $scope.selectedOrganisation = state.selectedOrganisation;
            }
            $scope.initOrganisationFilter();
            $scope.model = model;
        };
        $scope.init();
    }
);