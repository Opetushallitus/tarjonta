var app = angular.module('app.kk.edit.hakukohde.ctrl');
app.controller('ValintakokeetController', function($scope, $q, $filter, LocalisationService, OrganisaatioService,
                   Koodisto, Hakukohde, Valintakoe, dialogService, HakuService, $modal, Config, $location,
                   HakukohdeService) {
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
            lisatiedot: '',
            alkaa: null,
            loppuu: null,
            kellonaikaKaytossa: true,
            osoite: {
                osoiterivi1: '',
                postinumero: '',
                postitoimipaikka: '',
                postinumeroArvo: ''
            }
        };
    }
    function notEmpty(v) {
        if (v instanceof Array) {
            for (var i in v) {
                if (!notEmpty(v[i])) {
                    return false;
                }
            }
            return true;
        }
        else {
            return v && ('' + v).trim().length > 0;
        }
    }
    $scope.getLanguages = function() {
        var deferred = $q.defer();
        Koodisto.getAllKoodisWithKoodiUri('kieli', LocalisationService.getLocale()).then(function(koodis) {
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
    $scope.$watch('kokeetModel.valintakoeLangs.length', function() {
        $scope.kokeetModel.valintakoeLangs.sort($scope.sortLanguageTabs);
    });
    $scope.$watch('model.hakukohde.oid', function(n, o) {
        //only add
        if (n) {
            $scope.getLanguages().then(function(langUris) {
                if (langUris.length > 0) {
                    angular.forEach(langUris, function(uri) {
                        $scope.addValintakoeTab(uri, $scope.kokeetModel.langs, false);
                    });
                }
                addEmptyValintakokeet();
            });
        }
    });
    $scope.addValintakoeTab = function(kieliUri, koodistoKieliKoodis, selected) {
        if (!kieliUri) {
            return false;
        }
        //search koodi object by koodi object
        var koodi = _.find(koodistoKieliKoodis, function(koodi) {
            return koodi.koodiUri === kieliUri;
        });
        if (koodi) {
            var tabLang = _.find($scope.kokeetModel.valintakoeLangs, function(koodi) {
                return koodi.koodiUri === kieliUri;
            });
            if (!tabLang) {
                //remove duplicate objects
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
    $scope.selectedValintakoe = function(uri) {
        $scope.kokeetModel.selectedTab[uri] = true;
        var valintakoe = _.find($scope.model.hakukohde.valintakokeet, function(vk) {
            return vk.kieliUri === uri;
        });
        if (valintakoe && !valintakoe.selectedAjankohta) {
            valintakoe.selectedAjankohta = newAjankohta();
        }
    };
    $scope.addAjankohta = function(valintakoe) {
        valintakoe.valintakoeAjankohtas.push(newAjankohta());
        $scope.status.dirtify();
    };
    $scope.deleteAjankohta = function(valintakoe, ajankohta, confirm) {
        if (!ajankohta.alkaa && !ajankohta.loppuu && !ajankohta.osoite.osoiterivi1 && !ajankohta.osoite.postinumero) {
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
        }
        else {
            dialogService.showDialog({
                title: LocalisationService.t('tarjonta.poistovahvistus.hakukohde.valintakoe.ajankohta.title'),
                description: LocalisationService.t('tarjonta.poistovahvistus.hakukohde.valintakoe.ajankohta', [
                    valintakoe.valintakoeNimi,
                    $filter('date')(ajankohta.alkaa, 'd.M.yyyy H:mm') || '?',
                    $filter('date')(ajankohta.loppuu, 'd.M.yyyy H:mm') || '?'
                ])
            }).result.then(function(ret) {
                if (ret) {
                    $scope.deleteAjankohta(valintakoe, ajankohta, true);
                }
            });
        }
    };
    $scope.deleteValintakoe = function(valintakoe, confirm) {
        if (confirm) {
            var p = $scope.model.hakukohde.valintakokeet.indexOf(valintakoe);
            if (p != -1) {
                $scope.status.dirty = true;
                $scope.model.hakukohde.valintakokeet.splice(p, 1);
            }
            $scope.status.dirtify();
        }
        else {
            dialogService.showDialog({
                title: LocalisationService.t('tarjonta.poistovahvistus.hakukohde.valintakoe.title'),
                description: LocalisationService.t(
                    'tarjonta.poistovahvistus.hakukohde.valintakoe',
                    [valintakoe.valintakoeNimi]
                )
            }).result.then(function(ret) {
                if (ret) {
                    $scope.deleteValintakoe(valintakoe, true);
                }
            });
        }
    };
    $scope.addValintakoe = function(lc) {
        return HakukohdeService.addValintakoe($scope.model.hakukohde, lc);
    };
    $scope.getValintakokeetByKieli = function(lc) {
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
    $scope.findValintakoeLangTabObj = function(uri) {
        return _.find($scope.kokeetModel.valintakoeLangs, function(vk) {
            return vk.koodiUri === uri;
        });
    };
    function addEmptyValintakokeet() {
        _.each($scope.kokeetModel.valintakoeLangs, function(kieli) {
            var kieliUri = kieli.koodiUri;
            var found = _.find($scope.model.hakukohde.valintakokeet, function(valintakoe) {
                return valintakoe.kieliUri === kieliUri;
            });
            if (!found) {
                HakukohdeService.addValintakoe($scope.model.hakukohde, kieliUri);
            }
        });
    }
    $scope.$on('addEmptyValintakokeet', function() {
        addEmptyValintakokeet();
    });
    $scope.onLangSelection = function(uris) {
        if (uris && uris.added !== null) {
            $scope.addValintakoeTab(uris.added, $scope.kokeetModel.langs, false);
        }
        else if (uris && uris.removed !== null) {
            //remove koodisto koodi -> remove tab
            var kieli = $scope.findValintakoeLangTabObj(uris.removed);
            if (kieli) {
                $scope.kokeetModel.valintakoeLangs.splice($scope.kokeetModel.valintakoeLangs.indexOf(kieli), 1);
            }
            var valintakoe = _.find($scope.model.hakukohde.valintakokeet, function(vk) {
                return vk.kieliUri === uris.removed;
            });
            if (valintakoe) {
                $scope.model.hakukohde.valintakokeet.splice(
                    $scope.model.hakukohde.valintakokeet.indexOf(valintakoe),
                    1
                );
            }
        }
        addEmptyValintakokeet();
    };
    var setValintakoetyypit = function(toteutustyyppi) {
        HakukohdeService.config.getOptionsFromKoodisto(toteutustyyppi, 'valintakokeentyyppi', $scope.model.userLang)
            .then(function(options) {
                $scope.kokeetModel.valintakoetyypit = options;
            });
    };
    setValintakoetyypit($scope.model.hakukohde.toteutusTyyppi);
});