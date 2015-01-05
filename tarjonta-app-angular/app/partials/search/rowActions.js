angular.module('search.hakutulokset.rows', [])
    .factory('RowActions', function(TarjontaService, LocalisationService, PermissionService, AuthService,
                                     $modal, $location) {
        'use strict';

        var LinksDialogCtrl = function($scope, $modalInstance) {
            $scope.otsikko = 'tarjonta.linkit.otsikko.' + $scope.prefix;
            $scope.eohje = 'tarjonta.linkit.eohje.' + $scope.prefix;
            $scope.items = [];
            $scope.ok = function() {
                $modalInstance.close();
            };
            var base = $scope.prefix == 'koulutus' ? 'hakukohde' : 'koulutus';
            var ret = $scope.prefix == 'koulutus' ?
                TarjontaService.getKoulutuksenHakukohteet($scope.oid) :
                TarjontaService.getHakukohteenKoulutukset($scope.oid);
            ret.then(function(ret) {
                for (var i in ret) {
                    var s = ret[i];
                    $scope.items.push({
                        url: '#/' + base + '/' + s.oid,
                        nimi: s.nimi
                    });
                }
            });
        };

        function openLinksDialog(prefix, oid, nimi, $scope) {
            var ns = $scope.$new();
            ns.prefix = prefix;
            ns.oid = oid;
            ns.nimi = nimi;
            $modal.open({
                controller: LinksDialogCtrl,
                templateUrl: 'partials/search/links-dialog.html',
                scope: ns
            });
        }

        var DeleteDialogCtrl = function($scope, $modalInstance, ns) {
            var init = function() {
                $scope.oid = ns.oid;
                $scope.nimi = ns.nimi;
                $scope.otsikko = ns.otsikko;
                $scope.ohje = ns.ohje;
            };
            init();
            $scope.ok = function() {
                $modalInstance.close();
            };
            $scope.cancel = function() {
                $modalInstance.dismiss();
            };
        };

        function openDeleteDialog(prefix, oid, nimi, deleteAction, $scope) {
            var ns = {};
            ns.oid = oid;
            ns.nimi = nimi;
            ns.otsikko = LocalisationService.t('tarjonta.poistovahvistus.otsikko.' + prefix);
            ns.ohje = LocalisationService.t('tarjonta.poistovahvistus.ohje.' + prefix);
            if (prefix == 'hakukohde') {
                $modal.open({
                    controller: DeleteDialogCtrl,
                    templateUrl: 'partials/search/delete-dialog.html',
                    resolve: {
                        ns: function() {
                            return ns;
                        }
                    }
                }).result.then(function() {
                        TarjontaService.deleteHakukohde(oid).then(function() {
                            deleteAction();
                            // poistaa rivin hakutuloslistasta
                            $scope.hakukohdeResults.tuloksia--;
                            TarjontaService.evictHakutulokset();
                        });
                    });
            }
            else {
                $modal.open({
                    templateUrl: 'partials/koulutus/remove/poista-koulutus.html',
                    controller: 'PoistaKoulutusCtrl',
                    resolve: {
                        targetKomoto: function() {
                            return {
                                oid: oid,
                                koulutuskoodi: '',
                                nimi: nimi
                            };
                        },
                        organisaatioOid: function() {
                            return {
                                oid: '',
                                nimi: ''
                            };
                        }
                    }
                }).result.then(function() {
                        deleteAction();
                        // poistaa rivin hakutuloslistasta
                        $scope.koulutusResults.tuloksia--;
                        TarjontaService.evictHakutulokset();
                    });
            }
        }

        function canRemoveHakukohde(hakukohde) {
            return TarjontaService.parameterCanRemoveHakukohdeFromHaku(hakukohde.hakuOid) &&
                TarjontaService.parameterCanEditHakukohde(hakukohde.hakuOid);
        }

        function rowActions(prefix, row, actions, $scope) {
            var oid = row.oid;
            var tila = row.tila;
            var nimi = row.nimi;
            var ret = [];
            var tt = TarjontaService.getTilat()[tila];
            var actionsByPrefix = {
                hakukohde: function(row) {
                    tt.removable = tt.removable && canRemoveHakukohde(row);
                }
            };
            if (actionsByPrefix[prefix]) {
                actionsByPrefix[prefix](row);
            }
            var canRead = PermissionService[prefix].canPreview(oid);
            if (canRead) {
                var url = '/' + prefix + '/' + oid;
                ret.push({
                    action: function() {
                        $location.path(url);
                    },
                    title: LocalisationService.t('tarjonta.toiminnot.tarkastele')
                });
            }
            if (tt.mutable) {
                PermissionService[prefix].canEdit(oid, {
                    defaultTarjoaja: AuthService.getUserDefaultOid()
                }).then(function(result) {
                    var url = '/' + prefix + '/' + oid + '/edit';
                    if (result) {
                        ret.push({
                            action: function() {
                                $location.path(url);
                            },
                            title: LocalisationService.t('tarjonta.toiminnot.muokkaa')
                        });
                    }
                });
            }
            if (canRead) {
                ret.push({
                    title: LocalisationService.t('tarjonta.toiminnot.' + prefix + '.linkit'),
                    action: function() {
                        openLinksDialog(prefix, oid, nimi, $scope);
                    }
                });
            }
            switch (tila) {
                case 'PERUTTU':
                case 'VALMIS':
                    PermissionService[prefix].canTransition(oid, tila, 'JULKAISTU').then(function(canTransition) {
                        if (canTransition) {
                            ret.push({
                                title: LocalisationService.t('tarjonta.toiminnot.julkaise'),
                                action: function() {
                                    TarjontaService.togglePublished(prefix, oid, true).then(function(ns) {
                                        row.tila = 'JULKAISTU';
                                        actions.update();
                                        TarjontaService.evictHakutulokset();
                                    });
                                }
                            });
                        }
                    });
                    break;
                case 'JULKAISTU':
                    PermissionService[prefix].canTransition(oid, tila, 'PERUTTU').then(function(canTransition) {
                        if (canTransition) {
                            ret.push({
                                title: LocalisationService.t('tarjonta.toiminnot.peruuta'),
                                action: function() {
                                    TarjontaService.togglePublished(prefix, oid, false).then(function() {
                                        row.tila = 'PERUTTU';
                                        actions.update();
                                        TarjontaService.evictHakutulokset();
                                    });
                                }
                            });
                        }
                    });
                    break;
            }
            if (tt.removable) {
                PermissionService[prefix].canDelete(oid).then(function(canDelete) {
                    if (canDelete) {
                        ret.push({
                            title: LocalisationService.t('tarjonta.toiminnot.poista'),
                            action: function() {
                                openDeleteDialog(prefix, oid, nimi, actions.delete, $scope);
                            }
                        });
                    }
                });
            }
            return ret;
        }

        return {
            get: function(prefix, row, actions, $scope) {
                return rowActions(prefix, row, actions, $scope);
            }
        };
    });