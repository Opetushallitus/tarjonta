'use strict';

/* Controllers */

var app = angular.module('app.koulutus.ctrl');

app.controller('LuoKoulutusDialogiController', ['$location', '$q', '$scope', 'Koodisto', '$modal', 'OrganisaatioService', 'SharedStateService', 'AuthService', '$log',
    function($location, $q, $scope, Koodisto, $modal, OrganisaatioService, SharedStateService, AuthService, $log) {

        $log = $log.getInstance("LuoKoulutusDialogiController");

        // Tähän populoidaan formin valinnat:
        $log.debug("resetting form selections");
        $scope.model = {
            koulutustyyppi: undefined,
            organisaatiot: []
        };

        //resolvaa tarvittavat koodit ja suhteet... rakentaa mapit validointia varten:
        // oppilaitostyyppi -> [koulutustyypit]
        // koulutustyyppi -> [oppilaitostyypit]

        //luo tarvittavat tietorakenteet valintojen validointia varten:
        SharedStateService.state.luoKoulutusaDialogi = SharedStateService.state.luoKoulutusaDialogi || {};
        SharedStateService.state.luoKoulutusaDialogi.oppilaitostyypit = SharedStateService.state.luoKoulutusaDialogi.oppilaitostyypit || {};
        SharedStateService.state.luoKoulutusaDialogi.koulutustyypit = SharedStateService.state.luoKoulutusaDialogi.koulutustyypit || {};


        // hätäkorjaus KJOH-670
        SharedStateService.state.puut["lkorganisaatio"] = {};
        if (SharedStateService.state.puut && SharedStateService.state.puut["lkorganisaatio"] && SharedStateService.state.puut["lkorganisaatio"].scope !== $scope) {
            SharedStateService.state.puut["lkorganisaatio"].scope = $scope;
        }

        var promises = [];

        if (!SharedStateService.state.luoKoulutusaDialogi.koulutustyyppikoodit) {
            var deferred = $q.defer();
            promises.push(deferred.promise);
        }
        SharedStateService.state.luoKoulutusaDialogi.koulutustyyppikoodit = SharedStateService.state.luoKoulutusaDialogi.koulutustyyppikoodit || Koodisto.getAllKoodisWithKoodiUri('koulutustyyppi', 'fi').then(
                function(koodit) {

                    var subpromises = [];

                    for (var i = 0; i < koodit.length; i++) {
                        $log.debug("koulutustyyppikoodi:", koodit[i]);
                        SharedStateService.state.luoKoulutusaDialogi[koodit[i]] = [];
                        var koulutustyyppi = koodit[i];

                        //funktio joka rakentaa sopivat mapit koodistojen valintaan (koulutustyyppiuri->oppilaitostyyppi[], oppilaitostyyppiuri->koulutustyyppi[])
                        var ylapuoliset = function(koulutustyyppi) {
                            return function(ylapuoliset) {
                                for (var j = 0; j < ylapuoliset.length; j++) {
                                    if ("oppilaitostyyppi" === ylapuoliset[j].koodiKoodisto) {
                                        var oppilaitostyyppi = ylapuoliset[j];
                                        var kturi = koulutustyyppi.koodiUri + "#" + oppilaitostyyppi.koodiVersio;
                                        var oturi = oppilaitostyyppi.koodiUri + "#" + oppilaitostyyppi.koodiVersio;
                                        SharedStateService.state.luoKoulutusaDialogi.oppilaitostyypit[kturi] = SharedStateService.state.luoKoulutusaDialogi.oppilaitostyypit[kturi] || [];
                                        SharedStateService.state.luoKoulutusaDialogi.oppilaitostyypit[kturi].push(oppilaitostyyppi);
                                        SharedStateService.state.luoKoulutusaDialogi.koulutustyypit[oturi] = SharedStateService.state.luoKoulutusaDialogi.koulutustyypit[oturi] || [];
                                        SharedStateService.state.luoKoulutusaDialogi.koulutustyypit[oturi].push(koulutustyyppi);
                                        $log.debug(oppilaitostyyppi.koodiUri, "<->", koulutustyyppi.koodiUri);
                                    }
                                }
                            };
                        };

                        var promise = Koodisto.getYlapuolisetKoodit(koulutustyyppi.koodiUri, AuthService.getLanguage()).then(ylapuoliset(koulutustyyppi));
                        subpromises.push(promise);
                    }

                    $q.all(subpromises).then(function() {
                        $log.debug("all sub promises are now resolved!", deferred);
                        deferred.resolve();
                    });

                }
        );

        $scope.lkorganisaatio = $scope.lkorganisaatio || {currentNode: undefined};
        // Watchi valitulle organisaatiolle
        $scope.$watch('lkorganisaatio.currentNode', function(organisaatio, oldVal) {
            $log.debug("oprganisaatio valittu", organisaatio);
            //XXX nyt vain yksi organisaatio valittavissa
            if ($scope.model.organisaatiot.length == 0 && organisaatio !== undefined && organisaatio.oid !== undefined && $scope.model.organisaatiot.indexOf(organisaatio) == -1) {
                lisaaOrganisaatio(organisaatio);
            }
        });

        $scope.valitut = $scope.valitut || [];
        $scope.organisaatiomap = $scope.organisaatiomap || {};
        $scope.sallitutKoulutustyypit = $scope.sallitutKoulutustyypit || [];

//	$log.debug("organisaatio:", $scope.luoKoulutusDialogOrg);

        $scope.lkorganisaatiot = {};
        // haetaan organisaatihierarkia joka valittuna kälissä tai jos mitään ei ole valittuna organisaatiot joihin käyttöoikeus
        OrganisaatioService.etsi({oidRestrictionList: $scope.luoKoulutusDialogOrg || AuthService.getOrganisations()}).then(function(vastaus) {
            //$log.debug("asetetaan org hakutulos modeliin.");
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

            for (var i = 0; i < vastaus.organisaatiot.length; i++) {
                var oppilaitostyypit = OrganisaatioService.haeOppilaitostyypit(vastaus.organisaatiot[i].oid);
                promises.push(oppilaitostyypit);
                oppilaitostyypit.then(function(tyypit) {
                    for (var i = 0; i < tyypit.length; i++) {
                        if (oltUrit.indexOf(tyypit[i]) == -1) {
                            oltUrit.push(tyypit[i]);
                        }
                    }
                });
            }

            //$log.debug("oppilaitostyyppejä:", oltUrit.length);

            //jos valittavissa vain yksi, 2. selectiä ei pitäisi näyttää.
            //$scope.piilotaKoulutustyyppi=oltUrit.length<2;

            $q.all(oltpromises).then(function() {
                $q.all(promises).then(function() {
                    paivitaKoulutustyypit(oltUrit);
                    //$log.debug("all done!");
                });
            });

//		$q.all(promises).then(function(){
//			paivitaKoulutustyypit(oltUrit);
//		    //$log.debug("all done!");
//		 });

            /*
             //allaoleva bugaa koska tätä suorittaessa pitäisi olla koodistot ja relaatiot haettuna, disabloitu for now
             paivitaKoulutustyypit(oltUrit);
             */


        });


        var lisaaOrganisaatio = function(organisaatio) {
            $scope.model.organisaatiot.push(organisaatio);
            $log.debug("lisaaOrganisaatio:", organisaatio);
            var oppilaitostyypit = OrganisaatioService.haeOppilaitostyypit(organisaatio.oid);

            oppilaitostyypit.then(function(data) {
                paivitaKoulutustyypit(data);
            });
            //$log.debug("oppilaitostyypit:", oppilaitostyypit);
            //    $log.debug("kaikki koulutustyypit:", SharedStateService.state.luoKoulutusaDialogi.koulutustyypit);
        };

        var paivitaKoulutustyypit = function(oppilaitostyypit) {
            var sallitutKoulutustyypit = [];
            if (oppilaitostyypit !== undefined) {
                for (var i = 0; i < oppilaitostyypit.length; i++) {
                    var oppilaitostyyppiUri = oppilaitostyypit[i];
                    $log.debug("getting koulutustyyppi for ", oppilaitostyyppiUri);
                    var koulutustyypit = SharedStateService.state.luoKoulutusaDialogi.koulutustyypit[oppilaitostyyppiUri];
                    //$log.debug("got:", koulutustyypit);
                    if (koulutustyypit) {
                        for (var j = 0; j < koulutustyypit.length; j++) {
                            if (sallitutKoulutustyypit.indexOf(koulutustyypit[j]) == -1) {
                                sallitutKoulutustyypit.push(koulutustyypit[j]);
                            }
                        }
                    } else {
                        $log.debug("oppilaitostyypille: '", oppilaitostyyppiUri, "' ei löydy koulutustyyppejä");
                    }

                }
                //$log.debug("asetetaan koulutustyypit: ", sallitutKoulutustyypit);
            }
            $scope.sallitutKoulutustyypit = sallitutKoulutustyypit;
        };

        //alusta koulutustyypit (kaikki valittavissa olevat)
        paivitaKoulutustyypit();

        function organisaatio(orgResult) {

        }

        /**
         * Peruuta nappulaa klikattu, sulje dialogi
         */
        $scope.peruuta = function() {
            $log.debug("peruuta");
            $scope.luoKoulutusDialog.dismiss('cancel');
        };

        /**
         * Jatka nappulaa klikattu, avaa seuraava dialogi TODO jos ei kk pitäisi mennä suoraan lomakkeelle?
         */
        $scope.jatka = function() {
            $scope.tutkintoDialogModel = {};

            /*
             koulutustyyppi_5	Valmentava ja kuntouttava opetus ja ohjaus
             koulutustyyppi_12	Erikoisammattitutkinto
             koulutustyyppi_10	Vapaan sivistystyön koulutus
             koulutustyyppi_11	Ammattitutkinto
             koulutustyyppi_2	Lukiokoulutus
             koulutustyyppi_13	ammatillinen perustutkinto näyttötutkintona
             koulutustyyppi_14	Lukiokoulutus, aikuisten oppimäärä
             koulutustyyppi_7	Ammatilliseen peruskoulutukseen ohjaava ja valmistava koulutus
             koulutustyyppi_4	Ammatillinen peruskoulutus erityisopetuksena
             koulutustyyppi_1	Ammatillinen perustutkinto
             koulutustyyppi_8	Maahanmuuttajien ammatilliseen peruskoulutukseen valmistava koulutus
             koulutustyyppi_3	Korkeakoulutus
             koulutustyyppi_9	Maahanmuuttajien ja vieraskielisten lukiokoulutukseen valmistava koulutus
             koulutustyyppi_6	Perusopetuksen lisäopetus
             */

            //XXX nyt vain kk kovakoodattuna!!
            if ($scope.model.koulutustyyppi.koodiUri === "koulutustyyppi_3") {
                var olt = OrganisaatioService.haeOppilaitostyypit($scope.model.organisaatiot[0].oid);
                olt.then(function(oppilaitostyypit) {
                    Koodisto.getAlapuolisetKoodiUrit(oppilaitostyypit, "koulutusasteoph2002").then(
                            function(koulutusasteKoodit) {
                                //valitun organisaation organisaatiotyyppiin liittyvät koulutusastekoodit on nyt resolvattu?
                                $log.debug("koulutusastekoodit:", koulutusasteKoodit.uris);

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
                                    if (selectedItem.koodiUri != null) {
                                        $log.debug("org:", $scope.model.organisaatiot[0]);
                                        $location.path('/koulutus/KORKEAKOULUTUS/' + $scope.model.koulutustyyppi.koodiUri + '/edit/' + $scope.model.organisaatiot[0].oid + '/' + selectedItem.koodiArvo + '/');
                                    }
                                }, function() {
                                    $scope.tutkintoDialogModel.selected = null;
                                    $scope.luoKoulutusDialog.close();
                                });
                            })
                })

            } else if ($scope.model.koulutustyyppi.koodiUri === "koulutustyyppi_2") {
                //LUKIO
                $location.path('/koulutus/LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA/' + $scope.model.koulutustyyppi.koodiUri + '/edit/' + $scope.model.organisaatiot[0].oid + '/NONE/');
                $scope.luoKoulutusDialog.close();
            } else if ($scope.model.koulutustyyppi.koodiUri === "koulutustyyppi_1") {
                //AMMATILLINEN
                $location.path('/koulutus/AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA/' + $scope.model.koulutustyyppi.koodiUri + '/edit/' + $scope.model.organisaatiot[0].oid + '/NONE/');
                $scope.luoKoulutusDialog.close();
            } else {
                eiToteutettu();
            }

        };

        /**
         * "Ei toteutettu" dialogi
         */
        var eiToteutettu = function() {
            //ei toteutettu hässäkkä, positetaan kun muutkin tyypit on tuettu:
            $scope.dialog = {
                title: "ei toteutettu",
                description: "",
                ok: "ok",
                cancel: "cancel"
            };

            $scope.eitoteutettu = $modal.open({
                scope: $scope,
                templateUrl: 'partials/common/dialog.html',
                controller: function() {
                    $scope.onClose = function() {
                        $log.debug("close!");
                        $scope.eitoteutettu.close();
                    };
                    $scope.onAction = function() {
                        $log.debug("close!");
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
            var jatkaEnabled = $scope.organisaatioValittu() && $scope.koulutustyyppiValidi();
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

    }]);


