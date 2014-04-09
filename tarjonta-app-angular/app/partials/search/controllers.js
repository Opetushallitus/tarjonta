
'use strict';

angular.module('app.search.controllers', ['app.services', 'localisation', 'Organisaatio', 'config', 'ResultsTable'])
        .controller('SearchController', function($scope, $routeParams, $location, LocalisationService, Koodisto, OrganisaatioService, TarjontaService, PermissionService, Config, loadingService, $modal, $window, SharedStateService, AuthService, $q, dialogService) {

            var OPH_ORG_OID = Config.env["root.organisaatio.oid"];
            var selectOrg;

            //organisaation vaihtuessa suoritettavat toimenpiteet
            $scope.$watch("selectedOrgOid", function(newObj, oldObj) {
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

            // 1. Organisaatiohaku
            function setDefaultHakuehdot() {
                $scope.hakuehdot = {
                    "searchStr": "",
                    "organisaatiotyyppi": "",
                    "oppilaitostyyppi": "",
                    "lakkautetut": false,
                    "suunnitellut": false,
                    "skipparents": true
                };
            }
            setDefaultHakuehdot();

            if (SharedStateService.state.puut && SharedStateService.state.puut["organisaatio"] && SharedStateService.state.puut["organisaatio"].selected) {
            	$routeParams.oid = SharedStateService.state.puut["organisaatio"].selected;
            }
            
            if (SharedStateService.state.puut && SharedStateService.state.puut["organisaatio"].scope !== $scope) {
                console.log("scope has changed???");
                SharedStateService.state.puut["organisaatio"].scope = $scope;
            }

            var orgs = AuthService.getOrganisations(["APP_TARJONTA_CRUD", "APP_TARJONTA_UPDATE", "APP_TARJONTA_READ"]);
            console.log("user orgs:", orgs);
            //käyttäjän oletusorganisaatio
            function getDefaultOrg() {
                if (orgs && orgs.length >0) {
                    return orgs[0];
                }
            }

            //jos organisaatiota ei ole urlissa määritelty ja käyttäjällä on oletusorganisaatio
            if (getDefaultOrg() && !$routeParams.oid) {
              
                selectOrg = getDefaultOrg();

                if(orgs.indexOf(OPH_ORG_OID)==-1) {
                  //hae orgsit jos ei oph
                  console.log("orgsit:", orgs);
                  OrganisaatioService.etsi({oidRestrictionList: orgs}).then(function(vastaus) {
                    $scope.$root.tulos = vastaus.organisaatiot;
                  });
                }

            }

            $scope.oppilaitostyypit = Koodisto.getAllKoodisWithKoodiUri(Config.env["koodisto-uris.oppilaitostyyppi"], AuthService.getLanguage()).then(function(koodit) {
                //console.log("oppilaitostyypit", koodit);
                angular.forEach(koodit, function(koodi) {
                    koodi.koodiUriWithVersion = koodi.koodiUri + "#" + koodi.koodiVersio;
                });
                $scope.oppilaitostyypit = koodit;
            });

            //valittu organisaatio populoidaan tänne
            $scope.organisaatio = {};

            //watchi valitulle organisaatiolle, tästä varmaan lähetetään "organisaatio valittu" eventti jonnekkin?
            $scope.$watch('organisaatio.currentNode', function(newObj, oldObj) {

                //console.log("$scope.$watch( 'organisaatio.currentNode')");

                if ($scope.organisaatio && angular.isObject($scope.organisaatio.currentNode)) {

                    $scope.selectedOrgOid = $scope.organisaatio.currentNode.oid;
                    $scope.selectedOrgName = $scope.organisaatio.currentNode.nimi;

                    $scope.search();

                }
            }, false);

            $scope.organisaatioValittu = function() {
                return $routeParams.oid && $routeParams.oid !== OPH_ORG_OID;
            };

            $scope.hakukohdeColumns = ['hakutapa', 'aloituspaikat', 'koulutuslaji'];
            $scope.koulutusColumns = ['koulutuslaji'];

            // organisaatiotyypit; TODO jostain jotenkin dynaamisesti
            $scope.organisaatiotyypit = [{
                    nimi: LocalisationService.t("organisaatiotyyppi.koulutustoimija"),
                    koodi: 'Koulutustoimija'
                }, {
                    nimi: LocalisationService.t("organisaatiotyyppi.oppilaitos"),
                    koodi: "Oppilaitos"
                }, {
                    nimi: LocalisationService.t("organisaatiotyyppi.toimipiste"),
                    koodi: "Toimipiste"
                }, {
                    nimi: LocalisationService.t("organisaatiotyyppi.oppisopimustoimipiste"),
                    koodi: "Oppisopimustoimipiste"
                }];

            // Kutsutaan formin submitissa, käynnistää haun
            $scope.submitOrg = function() {
                //console.log("organisaatiosearch clicked!: " + angular.toJson($scope.hakuehdot));
                var hakutulos = OrganisaatioService.etsi($scope.hakuehdot);
                hakutulos.then(function(vastaus) {
//			console.log("result returned, hits:", vastaus);
                    $scope.$root.tulos = vastaus.organisaatiot; //TODO, keksi miten tilan saa säästettyä ilman root scopea.
                });
            };

            $scope.setDefaultOrg = function() {
                $scope.selectedOrgOid = getDefaultOrg();
            }

            // Kutsutaan formin resetissä, palauttaa default syötteet modeliin
            $scope.resetOrg = function() {
                setDefaultHakuehdot();
            };

            // 2. Koulutusten/Hakujen haku

            // hakuparametrit ja organisaatiovalinta
            function fromParams(key, def) {
                return $routeParams[key] != null ? $routeParams[key] : def;
            }

            // Selected org from route path or based on user organisation or oph org
            $scope.selectedOrgOid = $routeParams.oid ? $routeParams.oid : selectOrg ? selectOrg : OPH_ORG_OID;

            $scope.hakukohdeResults = {};
            $scope.koulutusResults = {};

            $scope.spec = {
                terms: fromParams("terms", ""),
                state: fromParams("state", "*"),
                year: fromParams("year", "*"),
                season: fromParams("season", "*")
            };

            var msgKaikki = LocalisationService.t("tarjonta.haku.kaikki");

            // tarjonnan tilat
            $scope.states = {"*": msgKaikki};
            for (var s in CONFIG.env["tarjonta.tila"]) {
                $scope.states[s] = LocalisationService.t("tarjonta.tila." + s);
            }

            // alkamiskaudet
            $scope.seasons = {"*": msgKaikki};
            Koodisto.getAllKoodisWithKoodiUri("kausi", AuthService.getLanguage()).then(function(koodit) {
                console.log("koodit", koodit);
                $scope.seasons = {"*": msgKaikki};

                for (var i in koodit) {
                    var k = koodit[i];
                    $scope.seasons[k.koodiUri] = k.koodiNimi;
                }
            });

            // alkamisvuodet; 2012 .. nykyhetki + 10v
            $scope.years = {"*": msgKaikki};
            var lyr = new Date().getFullYear() + 10;
            for (var y = 2012; y < lyr; y++) {
                $scope.years[y] = y;
            }

            if (!$scope.selectedOrgName) {
                OrganisaatioService.nimi($scope.selectedOrgOid).then(function(nimi) {
                    $scope.selectedOrgName = nimi
                });
            }

            function copyIfSet(dst, key, value, def) {
                if (value != null && value != undefined && (value + "").length > 0 && value != "*") {
                    dst[key] = value;
                } else if (def != undefined) {
                    dst[key] = def;
                }
            }

            function updateLocation() {

                // Query parameters collected here
                var sargs = {};

                copyIfSet(sargs, "terms", $scope.spec.terms, "*");
                copyIfSet(sargs, "state", $scope.spec.state);
                copyIfSet(sargs, "year", $scope.spec.year);
                copyIfSet(sargs, "season", $scope.spec.season);

                // Location should contain selected ORG oid if any
                if ($scope.selectedOrgOid != null) {
                    $location.path("/etusivu/" + $scope.selectedOrgOid);
                } else {
                    $location.path("/etusivu");
                }

                // Add query parameters
                $location.search(sargs);
            }

            $scope.clearOrg = function() {
                $scope.selectedOrgOid = OPH_ORG_OID;
            }

            $scope.reset = function() {
                $scope.spec.terms = "";
                $scope.spec.state = "*";
                $scope.spec.year = "*";
                $scope.spec.season = "*";
            }

            $scope.selection = {
                koulutukset: [],
                hakukohteet: []
            };

            $scope.$watch('selection.koulutukset', function(newObj, oldObj) {
              
                if (!newObj || newObj.length == 0) {
                    //mitään ei valittuna
                    $scope.koulutusActions.canMoveOrCopy = false;
                    $scope.koulutusActions.canCreateHakukohde = false;
                    return;
                }else if(newObj.length > 1){
                    //yksi valittuna
                     $scope.koulutusActions.canMoveOrCopy = false;
                }else{
                    PermissionService.koulutus.canMoveOrCopy(newObj).then(function(result) {
                      $scope.koulutusActions.canMoveOrCopy = result;
                    }); 
                }

                //lopullinen tulos tallennetaan tänne (on oikeus luoda hakukohde jos oikeus kaikkiin koulutuksiin):
                var r = {result: true};

                TarjontaService.haeKoulutukset({koulutusOid: newObj}).then(function(koulutukset) {
                    if (koulutukset && koulutukset.tulokset && koulutukset.tulokset.length > 0) {
                        for (var i = 0; i < koulutukset.tulokset.length; i++) {
                            PermissionService.hakukohde.canCreate(koulutukset.tulokset[i].oid).then(function(result) {
                                r.result = r.result && result;
                                $scope.koulutusActions.canCreateHakukohde = r.result;
                            });
                        }
                    }
                });


            }, true);

            $scope.menuOptions = [];

            $scope.koulutusActions = {
                canMoveOrCopy: false,
                canCreateHakukohde: false,
                canCreateKoulutus: false
            };
            
            function rowActions(prefix, oid, tila, nimi, actions) {
                var ret = [];
                var tt = TarjontaService.getTilat()[tila];

                var canRead = PermissionService[prefix].canPreview(oid);
                console.log("row actions can read (" + prefix + ")", canRead);

                // tarkastele
                if (canRead) {
                  var url = "/" + prefix + "/" + oid;
                    ret.push({action:function(){
                     $location.path(url); 
                    }, title: LocalisationService.t("tarjonta.toiminnot.tarkastele")});
                }
                // muokkaa
                if (tt.mutable) {
                    PermissionService[prefix].canEdit(oid).then(function(result) {
                        console.log("row actions can edit (" + prefix + ")", result);
                        var url="/" + prefix + "/" + oid + "/edit";
                        if (result) {
                            ret.push({action:function(){
                              $location.path(url);
                            }, title: LocalisationService.t("tarjonta.toiminnot.muokkaa")});
                        }
                    });
                }
                // näytä hakukohteet
                if (canRead) {
                    ret.push({title: LocalisationService.t("tarjonta.toiminnot." + prefix + ".linkit"),
                        action: function(ev) {
                            $scope.openLinksDialog(prefix, oid, nimi);
                        }
                    });
                }
                // tilasiirtymä
                switch (tila) {
                    case "PERUTTU":
                    case "VALMIS":
                      PermissionService[prefix].canTransition(oid, tila, "JULKAISTU").then(function(canTransition){
                       console.log("row actions can transition (" + prefix + ")", tila, "JULKAISTU", canTransition);

                      if (canTransition) {
                          ret.push({title: LocalisationService.t("tarjonta.toiminnot.julkaise"),
                              action: function() {
                                  TarjontaService.togglePublished(prefix, oid, true).then(function(ns) {
                                      actions.update("JULKAISTU");
                                      TarjontaService.evictHakutulokset();
                                  });
                              }
                          });
                      }});
                      break;
                    case "JULKAISTU":
                      PermissionService[prefix].canTransition(oid, tila, "PERUTTU").then(function(canTransition) {
                        if (canTransition) {
                            ret.push({title: LocalisationService.t("tarjonta.toiminnot.peruuta"),
                                action: function() {
                                    TarjontaService.togglePublished(prefix, oid, false).then(function(ns) {
                                        actions.update("PERUTTU");
                                        TarjontaService.evictHakutulokset();
                                    });
                                }
                            });
                        }});
                        break;
                }
                // poista
                if (tt.removable) {
                    PermissionService[prefix].canDelete(oid).then(function(canDelete) {
                        if (canDelete) {
                            ret.push({title: LocalisationService.t("tarjonta.toiminnot.poista"),
                                action: function(ev) {
                                    $scope.openDeleteDialog(prefix, oid, nimi, actions.remove);
                                }
                            });
                        }
                    });
                }

                return ret;
            }

            $scope.hakukohdeOptions = function(oid, tila, nimi, actions) {
                return rowActions("hakukohde", oid, tila, nimi, actions);
            };

            $scope.koulutusOptions = function(oid, tila, nimi, actions) {
                return rowActions("koulutus", oid, tila, nimi, actions);
            };

            $scope.search = function() {
                var spec = {
                    oid: $scope.selectedOrgOid,
                    terms: $scope.spec.terms,
                    state: $scope.spec.state == "*" ? null : $scope.spec.state,
                    year: $scope.spec.year == "*" ? null : $scope.spec.year,
                    season: $scope.spec.season == "*" ? null : $scope.spec.season + '#1'
                };

                //console.log("search", spec);
                updateLocation();

                // valinnat
                TarjontaService.haeKoulutukset(spec).then(function(data) {
                    $scope.koulutusResults = data;
                    $scope.koulutusResultCount = " (" + data.tuloksia + ")";
                });

                TarjontaService.haeHakukohteet(spec).then(function(data) {
                    $scope.hakukohdeResults = data;
                    $scope.hakukohdeResultCount = " (" + data.tuloksia + ")";
                });

            };

            $scope.luoKoulutusDisabled = function() {
                var disabled = !($scope.organisaatioValittu() && $scope.koulutusActions.canCreateKoulutus);
//    	console.log("luoKoulutusDisabled, organisaatioValittu:", $scope.organisaatioValittu(), "canCreateKoulutus:", $scope.koulutusActions.canCreateKoulutus);
                return disabled;
            };

            $scope.luoHakukohdeEnabled = function() {
                return ($scope.selection.koulutukset !== undefined && $scope.selection.koulutukset.length > 0) && $scope.koulutusActions.canCreateHakukohde;
            };

            if ($scope.spec.terms != "" || $scope.selectedOrgOid != OPH_ORG_OID) {
                if ($scope.spec.terms == "*") {
                    $scope.spec.terms = "";
                }
                // estää angularia tuhoamasta "liian nopeasti" haettua hakutuloslistausta
                // TODO ei toimi luotettavasti -> korjaa
                setTimeout($scope.search, 100);
            }

            $scope.report = function() {
                console.log("TODO raportti");
            };

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

            $scope.openDeleteDialog = function(prefix, oid, nimi, action) {

                ns = {};
                ns.oid = oid;
                ns.nimi = nimi;
                ns.otsikko = LocalisationService.t("tarjonta.poistovahvistus.otsikko." + prefix);
                ns.ohje = LocalisationService.t("tarjonta.poistovahvistus.ohje." + prefix);

                if (prefix == "hakukohde") {
                    var modalInstance = $modal.open({
                        controller: DeleteDialogCtrl,
                        templateUrl: "partials/search/delete-dialog.html",
                        resolve: {
                            ns: function() {
                                return ns;
                            }

                        }
                    });

                    modalInstance.result.then(function() {

                        var promise = prefix == "hakukohde"
                                ? TarjontaService.deleteHakukohde(oid)
                                : TarjontaService.deleteKoulutus(oid);

                        promise.then(function() {
                            action();
                            TarjontaService.evictHakutulokset();
                        });

                    });
                } else {
                    var modalInstance = $modal.open({
                        templateUrl: 'partials/koulutus/remove/poista-koulutus.html',
                        controller: 'PoistaKoulutusCtrl',
                        resolve: {
                            targetKomoto: function() {
                                return {oid: oid, koulutuskoodi: '', nimi: nimi};
                            },
                            organisaatioOid: function() {
                                return  {oid: '', nimi: ''}
                            }
                        }
                    });

                    modalInstance.result.then(function() {
                        //$route.reload();
                        $location.path("/");
                    }, function() { /* dismissed */
                    })

                }

            };

            var LinksDialogCtrl = function($scope, $modalInstance) {

                $scope.otsikko = "tarjonta.linkit.otsikko." + $scope.prefix;
                $scope.eohje = "tarjonta.linkit.eohje." + $scope.prefix;

                $scope.items = [];

                $scope.ok = function() {
                    $modalInstance.close();
                }

                var base = $scope.prefix == "koulutus" ? "hakukohde" : "koulutus";

                var ret = $scope.prefix == "koulutus"
                        ? TarjontaService.getKoulutuksenHakukohteet($scope.oid)
                        : TarjontaService.getHakukohteenKoulutukset($scope.oid);

                ret.then(function(ret) {
                    for (var i in ret) {
                        var s = ret[i];
                        $scope.items.push({
                            url: "#/" + base + "/" + s.oid,
                            nimi: s.nimi
                        });
                    }
                });

            }

            $scope.openLinksDialog = function(prefix, oid, nimi) {

                var ns = $scope.$new();
                ns.prefix = prefix;
                ns.oid = oid;
                ns.nimi = nimi;

                //console.log("LINKS p="+prefix+", o="+oid+", n="+nimi);

                var modalInstance = $modal.open({
                    controller: LinksDialogCtrl,
                    templateUrl: "partials/search/links-dialog.html",
                    scope: ns
                });

            };


            $scope.luoUusiHakukohde = function() {
              console.log("koulutukset:", $scope.selection.koulutukset);
              var promises=[];
              angular.forEach($scope.selection.koulutukset, function(koulutusOid){
                promises.push(TarjontaService.getKoulutusPromise(koulutusOid));
              });
              $q.all(promises).then(function(results){

                var valid=true;
                
                //tila
                angular.forEach(results, function(res){
                  var koulutus = res.result;
                  if(koulutus.tila=="PERUTTU" || koulutus.tila=="POISTETTU") {
                    valid=false;
                    dialogService.showDialog({
                      title: LocalisationService.t("koulutuksen.tila.error"),
                      description: LocalisationService.t("koulutuksen.tila.error"),
                      ok: LocalisationService.t("ok"),
                      cancel: LocalisationService.t("cancel")
                    });
                    return;
                  }
                });

                if($scope.selection.koulutukset.length>1) {
                    var vuosi,kausi;
                    angular.forEach(results, function(res){
                      var koulutus = res.result;
                      if(!vuosi){
                        vuosi=koulutus.koulutuksenAlkamisvuosi;
                        kausi=koulutus.koulutuksenAlkamiskausi.uri
                      } else {
                        if(vuosi!==koulutus.koulutuksenAlkamisvuosi || kausi!==koulutus.koulutuksenAlkamiskausi.uri) {
                          valid=false;
                        }
                      }
                    });

                    if(!valid) {
                      //show dialog about mismatch
                      console.log("vuosi/kausi mismatch!");
                      dialogService.showDialog({
                          title: LocalisationService.t("vuosikausi.mismatch.dialog.title"),
                          description: LocalisationService.t("vuosikausi.mismatch.dialog.description"),
                          ok: LocalisationService.t("ok"),
                          cancel: LocalisationService.t("cancel")
                      });
                    }
                }
                
                if(valid) {
                  console.log("KOULUTUS:", $scope.selection.koulutukset);
                  SharedStateService.addToState('SelectedKoulutukses', $scope.selection.koulutukset);
                  SharedStateService.addToState('SelectedOrgOid', $scope.selectedOrgOid);
                  $location.path('/hakukohde/new/edit');
                }

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
                    controller: 'LuoKoulutusDialogiController',
                });
            };

            $scope.siirraTaiKopioi = function() {
                var komotoOid = $scope.selection.koulutukset[0]; //single select
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

                var modalInstance = $modal.open({
                    templateUrl: 'partials/koulutus/copy/copy-move-koulutus.html',
                    controller: 'CopyMoveKoulutusController',
                    resolve: {
                        targetKoulutus: function() {
                            return  [{oid: komotoOid, nimi: koulutusNimi}]
                        },
                        targetOrganisaatio: function() {
                            return  {oid: $scope.selectedOrgOid, nimi: organisaatioNimi}
                        }
                    }
                });
            };

        });
