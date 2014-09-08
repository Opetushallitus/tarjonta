var app = angular.module('app.kk.edit.hakukohde.ctrl')

app.controller('ValintakokeetController',
    function ($scope, $q, $filter, LocalisationService, OrganisaatioService, Koodisto, Hakukohde, Valintakoe, dialogService, HakuService, $modal, Config, $location, HakukohdeService) {

        $scope.kokeetModel = {
            opetusKielet: [],
            langs: [],
            selectedLangs: [],
            selectedTab: {},
            valintakoeLangs: [],
            valintakoetyypit: []
        };

        function newAjankohta() {
            return {
                lisatiedot: "",
                alkaa: null,
                loppuu: null,
                osoite: {
                    osoiterivi1: "",
                    postinumero: "",
                    postitoimipaikka: "",
                    postinumeroArvo: ""
                }
            }
        }

        function notEmpty(v) {
            if (v instanceof Array) {
                for (var i in v) {
                    if (!notEmpty(v[i])) {
                        return false;
                    }
                }
                return true;
            } else {
                return v && ("" + v).trim().length > 0;
            }
        }

        $scope.getLanguages = function () {
            var deferred = $q.defer();

            Koodisto.getAllKoodisWithKoodiUri('kieli', LocalisationService.getLocale()).then(function (koodis) {
                $scope.kokeetModel.langs = koodis;
                var arrKieliUris = [];

                //add valintakoe langs
                if ($scope.model.hakukohde.valintakokeet
                    && $scope.model.hakukohde.valintakokeet !== null
                    && $scope.model.hakukohde.valintakokeet.length > 0) {
                    arrKieliUris = _.pluck($scope.model.hakukohde.valintakokeet, 'kieliUri');
                }

                //add koulutus languages
                if ($scope.model.hakukohde.opetusKielet
                    && $scope.model.hakukohde.opetusKielet !== null
                    && $scope.model.hakukohde.opetusKielet.length > 0) {
                    arrKieliUris = arrKieliUris.concat($scope.model.hakukohde.opetusKielet);
                }

                //remove all dublicate lang uris
                if (arrKieliUris.length > 0) {
                    arrKieliUris = _.compact(_.uniq(arrKieliUris)); //remove all dublicate lang uris and clean undefined values
                }

                $scope.kokeetModel.selectedLangs = arrKieliUris;

                deferred.resolve(arrKieliUris);
            });

            return deferred.promise;
        };

        $scope.$watch('model.hakukohde.oid', function (n, o) {
            //only add
            if (n) {
                $scope.getLanguages().then(function (langUris) {
                    if (langUris.length > 0) {
                        var selected = false;
                        angular.forEach(langUris, function (uri) {
                            selected = $scope.addValintakoeTab(uri, $scope.kokeetModel.langs, selected);
                        });

                        //select any available language.
                        if (!selected && langUris.length > 0) {
                            $scope.selectedValintakoe(langUris[0]);
                        }
                    }
                });
            }
        });

        $scope.addValintakoeTab = function (kieliUri, koodistoKieliKoodis, selected) {
            if (!kieliUri) {
                return false;
            }

            //search koodi object by koodi object
            var koodi = _.find(koodistoKieliKoodis, function (koodi) {
                return  koodi.koodiUri === kieliUri;
            });

            if (koodi) {
                var tabLang = _.find($scope.kokeetModel.valintakoeLangs, function (koodi) {
                    return koodi.koodiUri === kieliUri;
                });
                if (!tabLang) { //remove duplicate objects
                    $scope.kokeetModel.valintakoeLangs.push(koodi);
                }

                $scope.kokeetModel.selectedTab[kieliUri] = true;
            }

            if (!$scope.findValintakoeLangTabObj(kieliUri)) {
                //add new unique valintakoe tab
                $scope.selectedValintakoe(kieliUri);
                var p = $scope.model.hakukohde.opetusKielet.indexOf(kieliUri);
                if (p !== -1 && !selected) {

                    return true; //selected
                }
            }

            return false;
        };

        $scope.selectedValintakoe = function (uri) {
            $scope.kokeetModel.selectedTab[uri] = true;

            var valintakoe = _.find($scope.model.hakukohde.valintakokeet, function (vk) {
                return  vk.kieliUri === uri;
            });

            if (valintakoe && !valintakoe.selectedAjankohta) {
                valintakoe.selectedAjankohta = newAjankohta();
            }
        };

        $scope.isValidAjankohta = function (ajankohta) {
            return notEmpty([ajankohta.alkaa, ajankohta.loppuu,
                ajankohta.osoite.osoiterivi1, ajankohta.osoite.postinumero]);
        };

        // kutsutaan parentista
        $scope.status.validateValintakokeet = function () {
            for (var i in $scope.model.hakukohde.valintakokeet) {
                var li = $scope.model.hakukohde.valintakokeet[i];
                var nimiEmptyAndTyyppiEmpty = !notEmpty(li.valintakoeNimi) && !notEmpty(li.valintakoetyyppi);
                var kuvausEmpty = !notEmpty($(
                        "<div>" + li.valintakokeenKuvaus.teksti + "</div>").text()
                    .trim());
                var ajankohtaEmpty = li.valintakoeAjankohtas.length == 0;

                if (nimiEmptyAndTyyppiEmpty && kuvausEmpty && ajankohtaEmpty && li.isNew) {
                    continue;
                    // uusi tyhjä
                }

                if (nimiEmptyAndTyyppiEmpty) {
                    return false;
                }
                if (kuvausEmpty) {
                    return false;
                }
                for (var j in li.valintakoeAjankohtas) {
                    if (!$scope.isValidAjankohta(li.valintakoeAjankohtas[j])) {
                        return false;
                    }
                }
            }

            return true;
        };

        $scope.addAjankohta = function (valintakoe) {
            valintakoe.valintakoeAjankohtas.push(newAjankohta());
            $scope.status.dirtify();
        };

        $scope.deleteAjankohta = function (valintakoe, ajankohta, confirm) {
            if (!ajankohta.alkaa && !ajankohta.loppuu
                && !ajankohta.osoite.osoiterivi1
                && !ajankohta.osoite.postinumero) {
                confirm = true;
            }
            if (confirm) {
                if (ajankohta == valintakoe.selectedAjankohta) {
                    valintakoe.selectedAjankohta = newAjankohta();
                }
                var p = valintakoe.valintakoeAjankohtas.indexOf(ajankohta);
                if (p != -1) {
                    valintakoe.valintakoeAjankohtas.splice(p, 1);
                }
                $scope.status.dirtify();
            } else {

                dialogService
                    .showDialog({
                        title: LocalisationService
                            .t("tarjonta.poistovahvistus.hakukohde.valintakoe.ajankohta.title"),
                        description: LocalisationService
                            .t(
                            "tarjonta.poistovahvistus.hakukohde.valintakoe.ajankohta",
                            [
                                valintakoe.valintakoeNimi,
                                    $filter("date")(ajankohta.alkaa,
                                        "d.M.yyyy H:mm")
                                    || "?",
                                    $filter("date")(ajankohta.loppuu,
                                        "d.M.yyyy H:mm")
                                    || "?"])
                    }).result.then(function (ret) {
                        if (ret) {
                            $scope.deleteAjankohta(valintakoe, ajankohta, true);
                        }
                    });
            }
        };

        $scope.deleteValintakoe = function (valintakoe, confirm) {
            if (confirm) {
                var p = $scope.model.hakukohde.valintakokeet.indexOf(valintakoe);
                if (p != -1) {
                    $scope.status.dirty = true;
                    $scope.model.hakukohde.valintakokeet.splice(p, 1);
                }
                $scope.status.dirtify();
            } else {
                dialogService.showDialog({
                    title: LocalisationService
                        .t("tarjonta.poistovahvistus.hakukohde.valintakoe.title"),
                    description: LocalisationService.t(
                        "tarjonta.poistovahvistus.hakukohde.valintakoe",
                        [valintakoe.valintakoeNimi])
                }).result.then(function (ret) {
                        if (ret) {
                            $scope.deleteValintakoe(valintakoe, true);
                        }
                    });
            }
        };

        $scope.addValintakoe = function (lc) {
            console.log("add valintakoe");
            return HakukohdeService.addValintakoe($scope.model.hakukohde, lc);
            console.log("add valintakoe-->");
        };

        $scope.getValintakokeetByKieli = function (lc) {
            var ret = [];

            if (!lc) {
                return;
            }

            for (var i in $scope.model.hakukohde.valintakokeet) {
                var li = $scope.model.hakukohde.valintakokeet[i];
                if (li.kieliUri == lc) {
                    ret.push(li);
                }
            }

            return ret;
        };

        $scope.findValintakoeLangTabObj = function (uri) {
            return _.find($scope.kokeetModel.valintakoeLangs, function (vk) {
                return  vk.koodiUri === uri;
            });
        };

        $scope.onLangSelection = function (uris) {
            for (var i in $scope.kokeetModel.liitteet) {
                var li = $scope.kokeetModel.liitteet[i];
                if ($scope.kokeetModel.selectedLangs.indexOf(li.kieliUri) == -1) {
                    $scope.kokeetModel.selectedLangs.push(li.kieliUri);
                }
            }

            /*
             * Valintakoe tab change handler:
             * - update tab data model
             * - update tarjonta data model
             */
            if (uris && uris.added !== null) {
                $scope.addValintakoeTab(uris.added, $scope.kokeetModel.langs, false);
            } else if (uris && uris.removed !== null) {
                //remove koodisto koodi -> remove tab
                var kieli = $scope.findValintakoeLangTabObj(uris.removed);
                if (kieli) {
                    $scope.kokeetModel.valintakoeLangs.splice(kieli, 1);
                }

                //remove valintakoe object from model
                var valintakoe = _.find($scope.model.hakukohde.valintakokeet, function (vk) {
                    return  vk.kieliUri === uris.removed;
                });
                if (valintakoe) {
                    $scope.model.hakukohde.valintakokeet.splice($scope.model.hakukohde.valintakokeet.indexOf(valintakoe), 1);
                }
            }
        };

        var setValintakoetyypit = function (toteutusTyyppi) {
            var valintakoetyypit = [];

            valintakoetyypit.push(Koodisto.getKoodi("valintakokeentyyppi", "valintakokeentyyppi_1", $scope.model.userLang));
            valintakoetyypit.push(Koodisto.getKoodi("valintakokeentyyppi", "valintakokeentyyppi_2", $scope.model.userLang));
            valintakoetyypit.push(Koodisto.getKoodi("valintakokeentyyppi", "valintakokeentyyppi_5", $scope.model.userLang));

            if(toteutusTyyppi === 'MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS' ||
                toteutusTyyppi === 'MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS' ||
                toteutusTyyppi === 'AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS' ||
                toteutusTyyppi === 'PERUSOPETUKSEN_LISAOPETUS' ||
                toteutusTyyppi === 'VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS' ||
                toteutusTyyppi === 'VAPAAN_SIVISTYSTYON_KOULUTUS' ||
                toteutusTyyppi === 'AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA') {
                valintakoetyypit.push(Koodisto.getKoodi("valintakokeentyyppi", "valintakokeentyyppi_6", $scope.model.userLang));
            }

            angular.forEach(valintakoetyypit, function(koodiPromise) {
                koodiPromise.then(function(koodi) {
                    var valintakoetyyppi = {
                        nimi: koodi.koodiNimi,
                        uri: koodi.koodiUri
                    };
                    $scope.kokeetModel.valintakoetyypit.push(valintakoetyyppi);
                });
            });
        };

        setValintakoetyypit($scope.model.hakukohde.toteutusTyyppi);
    });
