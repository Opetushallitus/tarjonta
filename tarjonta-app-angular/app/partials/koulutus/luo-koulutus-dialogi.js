/* Controllers */
var app = angular.module('app.koulutus.ctrl');
app.controller('LuoKoulutusDialogiController', function($location, $q, $scope, Koodisto, $modal, OrganisaatioService,
                    SharedStateService, AuthService, $log, KoulutusConverterFactory, $timeout, LocalisationService,
                    KoulutusService, Config) {
    'use strict';

    $log = $log.getInstance('LuoKoulutusDialogiController');
    // Tähän populoidaan formin valinnat:
    $log.debug('resetting form selections');
    $scope.model = {
        koulutustyyppi: undefined,
        koulutusmoduuliTyyppi: $scope.spec.type !== '*' ? $scope.spec.type : undefined,  // inherit default value from parent
        organisaatiot: []
    };
    //resolvaa tarvittavat koodit ja suhteet... rakentaa mapit validointia varten:
    // oppilaitostyyppi -> [koulutustyypit]
    // koulutustyyppi -> [oppilaitostyypit]
    //luo tarvittavat tietorakenteet valintojen validointia varten:
    SharedStateService.state.luoKoulutusaDialogi = SharedStateService.state.luoKoulutusaDialogi || {};
    SharedStateService.state.luoKoulutusaDialogi.oppilaitostyypit =
        SharedStateService.state.luoKoulutusaDialogi.oppilaitostyypit || {};
    SharedStateService.state.luoKoulutusaDialogi.koulutustyypit =
        SharedStateService.state.luoKoulutusaDialogi.koulutustyypit || {};
    // hätäkorjaus KJOH-670
    SharedStateService.state.puut.lkorganisaatio = {};
    if (SharedStateService.state.puut && SharedStateService.state.puut.lkorganisaatio &&
        SharedStateService.state.puut.lkorganisaatio.scope !== $scope) {
        SharedStateService.state.puut.lkorganisaatio.scope = $scope;
    }
    var promises = [];
    if (!SharedStateService.state.luoKoulutusaDialogi.koulutustyyppikoodit) {
        var deferred = $q.defer();
        promises.push(deferred.promise);
    }
    SharedStateService.state.luoKoulutusaDialogi.koulutustyyppikoodit =
        SharedStateService.state.luoKoulutusaDialogi.koulutustyyppikoodit ||
        Koodisto.getAllKoodisWithKoodiUri('koulutustyyppi').then(function(koodit) {
        var subpromises = [];
        _.each(koodit, function(koulutustyyppi) {
            SharedStateService.state.luoKoulutusaDialogi[koulutustyyppi] = [];
            //funktio joka rakentaa sopivat mapit koodistojen valintaan (koulutustyyppiuri->oppilaitostyyppi[], oppilaitostyyppiuri->koulutustyyppi[])
            var ylapuoliset = function(koulutustyyppi) {
                return function(ylapuoliset) {
                    for (var j = 0; j < ylapuoliset.length; j++) {
                        if ('oppilaitostyyppi' === ylapuoliset[j].koodiKoodisto) {
                            var oppilaitostyyppi = ylapuoliset[j];
                            var kturi = koulutustyyppi.koodiUri + '#' + oppilaitostyyppi.koodiVersio;
                            var oturi = oppilaitostyyppi.koodiUri + '#' + oppilaitostyyppi.koodiVersio;
                            SharedStateService.state.luoKoulutusaDialogi.oppilaitostyypit[kturi] =
                                SharedStateService.state.luoKoulutusaDialogi.oppilaitostyypit[kturi] || [];

                            SharedStateService.state.luoKoulutusaDialogi.oppilaitostyypit[kturi].push(oppilaitostyyppi);

                            SharedStateService.state.luoKoulutusaDialogi.koulutustyypit[oturi] =
                                SharedStateService.state.luoKoulutusaDialogi.koulutustyypit[oturi] || [];

                            SharedStateService.state.luoKoulutusaDialogi.koulutustyypit[oturi].push(koulutustyyppi);
                        }
                    }
                };
            };
            var promise = Koodisto.getYlapuolisetKoodit(koulutustyyppi.koodiUri, AuthService.getLanguage())
                .then(ylapuoliset(koulutustyyppi));
            subpromises.push(promise);
        });
        $q.all(subpromises).then(function() {
            deferred.resolve();
        });
    });
    var userRootOrganization = null;
    $scope.lkorganisaatio = $scope.lkorganisaatio || {
        currentNode: undefined
    };
    // Watchi valitulle organisaatiolle
    $scope.$watch('lkorganisaatio.currentNode', function(organisaatio) {
        if (organisaatio && organisaatio.oid) {
            lisaaOrganisaatio(organisaatio);
            if (userRootOrganization === null) {
                userRootOrganization = organisaatio;
            }
        }
    });
    $scope.$watch('model.koulutustyyppi', function(val) {
        // If korkeakoulutus
        if (val && val.koodiUri === 'koulutustyyppi_3') {
            $scope.model.showOtherOrganizationsCheckbox = true;
        }
        else {
            $scope.model.showOtherOrganizations = false;
            $scope.model.showOtherOrganizationsCheckbox = false;
            $scope.toggleOtherOrganizations(true);
        }
        if (val) {
            // Hae pohjakoulutusvaatimus koodistosta
            Koodisto.getAlapuolisetKoodiUrit([val.koodiUri], 'pohjakoulutusvaatimustoinenaste').then(function(res) {
                $scope.pohjakoulutusvaatimusOptions = res.map;
                var keys = _.keys(res.map);
                $scope.showPohjakoulutusvaatimus = keys.length > 1;
                if (keys.length === 1) {
                    $scope.model.pohjakoulutusvaatimus = res.map[keys[0]].koodiUri;
                }
                else {
                    $scope.model.pohjakoulutusvaatimus = null;
                }
            });
        }
    });
    $scope.valitut = $scope.valitut || [];
    $scope.organisaatiomap = $scope.organisaatiomap || {};
    $scope.sallitutKoulutustyypit = $scope.sallitutKoulutustyypit || [];
    // haetaan organisaatihierarkia joka valittuna kälissä tai jos mitään ei ole valittuna organisaatiot joihin käyttöoikeus
    OrganisaatioService.etsi({
        aktiiviset: true,
        suunnitellut: true,
        oidRestrictionList: $scope.luoKoulutusDialogOrg || AuthService.getOrganisations()
    }).then(function(vastaus) {
        $scope.lkorganisaatiot = vastaus.organisaatiot;
        //rakennetaan mappi oid -> organisaatio jotta löydetään parentit helposti
        var buildMapFrom = function(orglist) {
            for (var i = 0; i < orglist.length; i++) {
                var organisaatio = orglist[i];
                $scope.organisaatiomap[organisaatio.oid] = organisaatio;
                if (organisaatio.children) {
                    buildMapFrom(organisaatio.children);
                }
            }
        };
        buildMapFrom(vastaus.organisaatiot);
        //hakee kaikki valittavissa olevat koulutustyypit
        var oltUrit = [];
        var oltpromises = [];
        _.each(vastaus.organisaatiot, function(org) {
            var oppilaitostyypit = OrganisaatioService.haeOppilaitostyypit(org.oid);
            promises.push(oppilaitostyypit);
            oppilaitostyypit.then(function(tyypit) {
                for (var i = 0; i < tyypit.length; i++) {
                    if (oltUrit.indexOf(tyypit[i]) == -1) {
                        oltUrit.push(tyypit[i]);
                    }
                }
            });
        });
        $q.all(oltpromises).then(function() {
            $q.all(promises).then(function() {
                paivitaKoulutustyypit(oltUrit);
            });
        });
    });
    var lisaaOrganisaatio = function(organisaatio) {
        // Tarkista, jos organisaatio on jo valittu -> älä tee mitään
        if (_.findWhere($scope.model.organisaatiot, {
                oid: organisaatio.oid
            })) {
            return;
        }
        // Jos ei näytetä muita organisaatioita -> voi valita vain yhden organisaation,
        // joten korvaa vanha valinta
        if (!$scope.model.showOtherOrganizations) {
            $scope.model.organisaatiot = [];
        }
        $scope.model.organisaatiot.push(organisaatio);
        var oppilaitostyypit = OrganisaatioService.haeOppilaitostyypit(organisaatio.oid);
        oppilaitostyypit.then(function(data) {
            paivitaKoulutustyypit(data);
        });
    };
    var paivitaKoulutustyypit = function(oppilaitostyypit) {
        var sallitutKoulutustyypit = [];
        if (oppilaitostyypit !== undefined) {
            for (var i = 0; i < oppilaitostyypit.length; i++) {
                var oppilaitostyyppiUri = oppilaitostyypit[i];
                $log.debug('getting koulutustyyppi for ', oppilaitostyyppiUri);
                var koulutustyypit = SharedStateService.state.luoKoulutusaDialogi.koulutustyypit[oppilaitostyyppiUri];
                if (koulutustyypit) {
                    for (var j = 0; j < koulutustyypit.length; j++) {
                        if (sallitutKoulutustyypit.indexOf(koulutustyypit[j]) == -1) {
                            sallitutKoulutustyypit.push(koulutustyypit[j]);
                        }
                    }
                }
                else {
                    $log.debug('oppilaitostyypille: \'', oppilaitostyyppiUri, '\' ei l\xF6ydy koulutustyyppej\xE4');
                }
            }
        }
        $scope.sallitutKoulutustyypit = sallitutKoulutustyypit;
    };
    //alusta koulutustyypit (kaikki valittavissa olevat)
    paivitaKoulutustyypit();
    /**
       * Peruuta nappulaa klikattu, sulje dialogi
       */
    $scope.peruuta = function() {
        $log.debug('peruuta');
        $scope.luoKoulutusDialog.dismiss('cancel');
    };
    /**
       * Jatka nappulaa klikattu, avaa seuraava dialogi TODO jos ei kk pitäisi mennä suoraan lomakkeelle?
       */
    $scope.jatka = function() {
        // Tarkista, että valittuna vähintään yksi oma organisaatio
        var firstOwnOrg;
        var ownOrgSelected = _.some($scope.model.organisaatiot, function(org) {
            var oidpath = org.parentOidPath.split('/');
            oidpath.push(org.oid);
            var isOwnOrg = oidpath.indexOf(userRootOrganization.oid) !== -1;
            if (isOwnOrg) {
                firstOwnOrg = org;
            }
            return isOwnOrg;
        });
        if (!ownOrgSelected) {
            // alert OK tässä (vaikka ei muuten käytetty), koska niin harvinainen tilanne
            alert(LocalisationService.t('luoKoulutusDialogi.valitseVahintaanOmaOrganisaatio'));
            return;
        }
        // Varmista, että oma organisaatio on ensimmäisenä
        var index = $scope.model.organisaatiot.indexOf(firstOwnOrg);
        if (index > 0) {
            $scope.model.organisaatiot.splice(index, 1);
            $scope.model.organisaatiot.unshift(firstOwnOrg);
        }
        $scope.tutkintoDialogModel = {};
        var toteutustyyppi = KoulutusConverterFactory.getToteutustyyppiByKoulutustyyppiKoodiUri(
            $scope.model.koulutustyyppi.koodiUri);
        if (!toteutustyyppi) {
            eiToteutettu();
            return;
        }
        if (toteutustyyppi === 'KORKEAKOULUTUS') {
            if (_.contains(['OPINTOKOKONAISUUS',
                            'OPINTOJAKSO'], $scope.model.koulutusmoduuliTyyppi)) {
                $scope.luoKoulutusDialog.close();
                KoulutusService.luoKorkeakouluOpinto(
                    $scope.model.koulutustyyppi.koodiUri,
                    $scope.model.organisaatiot[0].oid,
                    $scope.model.koulutusmoduuliTyyppi,
                    $scope.model.organisaatiot
                );
                return;
            }
            var olt = OrganisaatioService.haeOppilaitostyypit($scope.model.organisaatiot[0].oid);
            olt.then(function(oppilaitostyypit) {
                Koodisto.getAlapuolisetKoodiUrit(oppilaitostyypit, 'koulutusasteoph2002')
                    .then(function(koulutusasteKoodit) {
                    //valitun organisaation organisaatiotyyppiin liittyvät koulutusastekoodit on nyt resolvattu?
                    $log.debug('koulutusastekoodit:', koulutusasteKoodit.uris);
                    var modalInstance = $modal.open({
                        templateUrl: 'partials/koulutus/edit/korkeakoulu/selectTutkintoOhjelma.html',
                        controller: 'SelectTutkintoOhjelmaController',
                        resolve: {
                            targetFilters: function() {
                                return koulutusasteKoodit.uris;
                            }
                        }
                    });
                    modalInstance.result.then(function(selectedItem) {
                        $scope.luoKoulutusDialog.close();
                        if (selectedItem.koodiUri !== null) {
                            $log.debug('org:', $scope.model.organisaatiot[0]);
                            $location.path('/koulutus/KORKEAKOULUTUS/' + $scope.model.koulutustyyppi.koodiUri +
                                '/edit/' + $scope.model.organisaatiot[0].oid + '/' + selectedItem.koodiArvo);
                            $location.search('opetusTarjoajat', _.pluck($scope.model.organisaatiot, 'oid').join(','));
                        }
                    }, function() {
                            $scope.tutkintoDialogModel.selected = null;
                            $scope.luoKoulutusDialog.close();
                        });
                });
            });
        }
        else if ([
                'LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA',
                'EB_RP_ISH',
                'LUKIOKOULUTUS',
                'AMMATILLINEN_PERUSTUTKINTO',
                'AMMATILLINEN_PERUSTUTKINTO_ALK_2018',
                'AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA',
                'AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS',
                'AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA',
                'AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER',
                'MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS',
                'PERUSOPETUKSEN_LISAOPETUS',
                'VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS',
                'VAPAAN_SIVISTYSTYON_KOULUTUS',
                'MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS',
                'AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA',
                'ERIKOISAMMATTITUTKINTO',
                'AMMATTITUTKINTO',
                'AIKUISTEN_PERUSOPETUS',
                'PELASTUSALAN_KOULUTUS'
            ].indexOf(toteutustyyppi) !== -1) {
            var promise = Koodisto.getAlapuolisetKoodit($scope.model.koulutustyyppi.koodiUri);
            promise.then(function(koodis) {
                var koulutuslajiKoodis = _.where(koodis, {koodiKoodisto:CONFIG.env['koodisto-uris.koulutuslaji']});
                var toteutustyypitJoillaEiKoulutuslajia = ['AMMATILLINEN_PERUSTUTKINTO_ALK_2018', 'ERIKOISAMMATTITUTKINTO', 'AMMATTITUTKINTO']
                var onKoulutuslajillinenToteutustyyppi = toteutustyypitJoillaEiKoulutuslajia.indexOf(toteutustyyppi) == -1;

                var url = '/koulutus/' + toteutustyyppi + '/' + $scope.model.koulutustyyppi.koodiUri;
                if (koulutuslajiKoodis && koulutuslajiKoodis.length === 1 && onKoulutuslajillinenToteutustyyppi) {
                    url += '/' + koulutuslajiKoodis[0].koodiUri;
                }
                url += '/edit/' + $scope.model.organisaatiot[0].oid;
                $location.path(url);

                if ($scope.model.pohjakoulutusvaatimus) {
                    $location.search('pohjakoulutusvaatimus', $scope.model.pohjakoulutusvaatimus);
                }
                $scope.luoKoulutusDialog.close();
            });
        }
        else {
            eiToteutettu();
        }
    };
    /**
       * "Ei toteutettu" dialogi
       */
    var eiToteutettu = function() {
        //ei toteutettu hässäkkä, positetaan kun muutkin tyypit on tuettu:
        $scope.dialog = {
            title: 'ei toteutettu',
            description: '',
            ok: 'ok',
            cancel: 'cancel'
        };
        $scope.eitoteutettu = $modal.open({
            scope: $scope,
            templateUrl: 'partials/common/dialog.html',
            controller: function() {
                $scope.onClose = function() {
                    $log.debug('close!');
                    $scope.eitoteutettu.close();
                };
                $scope.onAction = function() {
                    $log.debug('close!');
                    $scope.eitoteutettu.close();
                };
            }
        });
    };
    /**
       * Jatka nappula enabloitu:
       * -organisaatio valittu && koulutus valittu && valinta on validi, olettaa että vain yhden organisaation voi valita.
       */
    $scope.jatkaDisabled = function() {
        var jatkaEnabled = $scope.organisaatioValittu() && $scope.koulutustyyppiValidi() // pohjakoulutus pitää olla valittuna osalle koulutuksista
            && !($scope.showPohjakoulutusvaatimus && !$scope.model.pohjakoulutusvaatimus && $scope.model.koulutustyyppi.koodiUri != 'koulutustyyppi_26');
        if ($scope.model.koulutustyyppi && $scope.model.koulutustyyppi.koodiUri === 'koulutustyyppi_3') {
            jatkaEnabled &= ($scope.model.koulutusmoduuliTyyppi !== undefined);
        }
        return !jatkaEnabled;
    };
    /**
       * Tarkista että Koulutustyyppi valittu ja validi vrt valittu organisaatio
       */
    $scope.koulutustyyppiValidi = function() {
        return $scope.sallitutKoulutustyypit.indexOf($scope.model.koulutustyyppi) != -1;
    };
    /**
       * Organisaatio valittu
       */
    $scope.organisaatioValittu = function() {
        return $scope.model.organisaatiot.length > 0;
    };
    /**
       * Poista valittu organisaatio ruksista
       */
    $scope.poistaValittu = function(organisaatio) {
        var valitut = [];
        for (var i = 0; i < $scope.model.organisaatiot.length; i++) {
            if ($scope.model.organisaatiot[i] !== organisaatio) {
                valitut.push($scope.model.organisaatiot[i]);
            }
        }
        $scope.model.organisaatiot = valitut;
    };
    var searchOrganizationTimeout = null;
    $scope.searchOrganizations = function(qterm) {
        if (searchOrganizationTimeout !== null) {
            $timeout.cancel(searchOrganizationTimeout);
        }
        searchOrganizationTimeout = $timeout(function() {
            OrganisaatioService.etsi({
                searchStr: qterm,
                lakkautetut: false,
                skipparents: false,
                suunnitellut: false
            }).then(function(result) {
                $scope.lkorganisaatiot = result.organisaatiot;
            });
        }, 500);
    };
    /**
       * Palauta organisaatiopuunäkymän oletusarvo, jos käyttäjä
       * poistaa ruksin "Näytä myös muut korkeakoulut".
       */
    var lkorganisaatiotInit = null;
    $scope.toggleOtherOrganizations = function(skipInit) {
        if (lkorganisaatiotInit === null && !skipInit && $scope.lkorganisaatiot) {
            lkorganisaatiotInit = angular.copy($scope.lkorganisaatiot);
        }
        if (!$scope.model.showOtherOrganizations && lkorganisaatiotInit) {
            $scope.lkorganisaatiot = lkorganisaatiotInit;
        }
    };
});
