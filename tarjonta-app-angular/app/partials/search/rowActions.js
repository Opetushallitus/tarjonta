angular.module('search.hakutulokset.rows', [])
    .factory('RowActions', function(TarjontaService, LocalisationService, PermissionService, AuthService,
                                     $modal, $location, KoulutusService, koulutusHakukohdeListing) {
        'use strict';

        function openLinksDialog(prefix, oid) {
            koulutusHakukohdeListing({
                type: prefix,
                oid: oid
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

        // Onko "Poista"-painike aktiivinen
        var checkIfAnyJarjestettyKoulutusJulkaistu = function () {
            if(koulutusModuuli.jarjestettavatKoulutukset && jkoulutusModuuli.arjestettavatKoulutukset.koulutukset) {
                for (var o in koulutusModuuli.jarjestettavatKoulutukset.koulutukset) {
                    if(koulutusModuuli.jarjestettavatKoulutukset.koulutukset[o].tila === 'JULKAISTU' || koulutusModuuli.jarjestettavatKoulutukset.koulutukset[o].tila === 'VALMIS'
                        || koulutusModuuli.jarjestettavatKoulutukset.koulutukset[o].tila === 'LUONNOS' || koulutusModuuli.jarjestettavatKoulutukset.koulutukset[o].tila === 'PERUTTU') {
                        return true;
                    }
                }
                return false;
            }
        };

        function rowActions(prefix, row, actions, $scope) {
            var oid = row.oid;
            var tila = row.tila;
            var nimi = row.nimi;
            var ret = [];
            var tt = angular.copy(TarjontaService.getTilat()[tila]);
            var actionsByPrefix = {
                hakukohde: function(row) {
                    tt.removable = tt.removable && canRemoveHakukohde(row);
                    tt.disablePublish = !TarjontaService.parameterCanAddHakukohdeToHaku(row.hakuOid);
                    tt.disableCancel = !TarjontaService.parameterCanRemoveHakukohdeFromHaku(row.hakuOid);
                    tt.mutable = tt.mutable && TarjontaService.parameterCanEditHakukohdeLimited(row.hakuOid);
                },
                jarjestaKoulutus: function() {
                    tt = {};
                    ret.push({
                        action: function() {
                            $location.path('/koulutus/' + oid);
                        },
                        title: LocalisationService.t('tarjonta.toiminnot.tarkastele')
                    });
                    ret.push({
                        action: function() {
                            TarjontaService.getKoulutus({oid: oid}).$promise.then(function(response) {
                                var koulutus = response.result;
                                KoulutusService.extendKorkeakouluOpinto(koulutus, AuthService.getLanguage() || 'fi');
                            });
                        },
                        title: LocalisationService.t('tarjonta.toiminnot.jarjesta')
                    });
                    // Resetoi, koska tulee muuten JS error ja ei merkitystä julkaistavissa oleville koulutuksille
                    tila = null;
                }
            };
            if (actionsByPrefix[prefix]) {
                actionsByPrefix[prefix](row);
            }
            var canRead = PermissionService[prefix] && PermissionService[prefix].canPreview(oid);
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
                        openLinksDialog(prefix, oid);
                    }
                });
            }
            switch (tila) {
                case 'PERUTTU':
                case 'VALMIS':
                    PermissionService[prefix].canTransition(oid, tila, 'JULKAISTU').then(function(canTransition) {
                        if (canTransition && !tt.disablePublish) {
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
                        if (canTransition && !tt.disableCancel) {
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
                if(!checkIfAnyJarjestettyKoulutusJulkaistu()){
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
            }
            return ret;
        }

        return {
            get: function(prefix, row, actions, $scope) {
                return rowActions(prefix, row, actions, $scope);
            }
        };
    });