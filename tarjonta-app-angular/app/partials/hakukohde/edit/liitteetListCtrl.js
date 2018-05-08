var app = angular.module('app.kk.edit.hakukohde.ctrl');
app.controller('LiitteetListController', function($scope, $q, LocalisationService, OrganisaatioService, Koodisto,
                Hakukohde, Liite, dialogService, HakuService, $modal, Config, $location, TarjontaService,
                HakukohdeService) {
    $scope.model = $scope.model || {};
    $scope.tt = {
        liitteenVastaanottaja: LocalisationService.t('tarjonta.hakukohde.liite.modal.liitteenVastaanottaja')
    };
    $scope.liitteetModel = {};
    $scope.liitteetModel.opetusKielet = [];
    $scope.liitteetModel.langs = [];
    $scope.liitteetModel.selectedLangs = [];
    $scope.liitteetModel.liitetyypit = [];
    var osoitteetReceived = false;
    $scope.OsoiteTyyppiEnum = {
        ORGANISAATION: 'OrganisaationOsoite',
        MUU: 'MuuOsoite',
        VAIN_SAHKOINEN: 'VainSahkoinenOsoite',
    };

    function postProcessLiite(liite) {
        if (liite.sahkoinenOsoiteEnabled === undefined) {
            liite.sahkoinenOsoiteEnabled = liite.sahkoinenToimitusOsoite !== undefined;
        }
        if (liite.ensisijainenOsoiteTyyppi === undefined && osoitteetReceived) {
            if ($scope.model.liitteidenToimitusOsoite[liite.kieliUri]) {
                var nimiOrganisaatiolla = $scope.model.hakutoimistonNimi[liite.kieliUri];
                var nimiLiitteella = liite.liitteenVastaanottaja;
                var osoiteOrganisaatiolla = $scope.model.liitteidenToimitusOsoite[liite.kieliUri];
                var osoiteLiitteella = liite.liitteenToimitusOsoite;

                var vastaanottajatMatch = (nimiOrganisaatiolla == nimiLiitteella);
                var osoitteetMatch = (osoiteOrganisaatiolla && osoiteLiitteella && osoiteOrganisaatiolla.osoiterivi1 == osoiteLiitteella.osoiterivi1);
                var postinumerotMatch = (osoiteOrganisaatiolla && osoiteLiitteella && osoiteOrganisaatiolla.postinumero == osoiteLiitteella.postinumero);

                if (vastaanottajatMatch && osoitteetMatch && postinumerotMatch) {
                    liite.ensisijainenOsoiteTyyppi = $scope.OsoiteTyyppiEnum.ORGANISAATION;
                } else if (!nimiLiitteella && !osoiteLiitteella) {
                    liite.ensisijainenOsoiteTyyppi = $scope.OsoiteTyyppiEnum.VAIN_SAHKOINEN;
                } else {
                    liite.ensisijainenOsoiteTyyppi = $scope.OsoiteTyyppiEnum.MUU;
                }
            } else if (!nimiLiitteella && !osoiteLiitteella) {
                liite.ensisijainenOsoiteTyyppi = $scope.OsoiteTyyppiEnum.VAIN_SAHKOINEN;
            } else {
                liite.ensisijainenOsoiteTyyppi = $scope.OsoiteTyyppiEnum.MUU;
            }
        }
        liite.liitteenKuvaukset = liite.liitteenKuvaukset || {};
        return liite;
    }
    $scope.$on('liiteAdded', function(event, liite) {
        if (!liite.liitteenToimitusOsoite && !liite.sahkoinenToimitusOsoite) {
            liite.liitteenToimitusOsoite = angular.copy($scope.model.liitteidenToimitusOsoite[liite.kieliUri]) || {};
        }
        postProcessLiite(liite);
    });
    $scope.$on('addEmptyLitteet', function() {
        addEmptyLitteet();
    });
    $scope.model.liitteenToimitusOsoitePromise.then(function(osoitteet) {
        osoitteetReceived = true;
        _.each($scope.model.hakukohde.hakukohteenLiitteet, function(liiteWithKieliVersiot) {
            _.each(liiteWithKieliVersiot, function(liite, lang) {
                if (typeof liite !== 'object' || lang === 'commonFields') {
                    return;
                }
                if (!liite.sahkoinenToimitusOsoite) {
                    if (!liite.liitteenVastaanottaja) {
                        liite.liitteenVastaanottaja = $scope.model.hakutoimistonNimi[liite.kieliUri];
                    }
                    if (!liite.liitteenToimitusOsoite || Object.keys(liite.liitteenToimitusOsoite).length === 0) {
                        liite.liitteenToimitusOsoite = angular.copy($scope.model.liitteidenToimitusOsoite[liite.kieliUri]);
                        postProcessLiite(liite);
                    }
                }
            });
        });
        Koodisto.getAllKoodisWithKoodiUri('kieli', LocalisationService.getLocale()).then(function(ret) {
            if (!$scope.model.hakukohde.opetusKielet) {
                $scope.model.hakukohde.opetusKielet = [];
            }
            $scope.liitteetModel.langs = ret;
            updateLanguages();
            addEmptyLitteet();
        });
    });
    function containsOpetuskieli(lc) {
        for (var i in $scope.liitteetModel.opetusKielet) {
            if ($scope.liitteetModel.opetusKielet[i].koodiUri === lc) {
                return true;
            }
        }
        return false;
    }
    function addEmptyLitteet() {
        if ($scope.model.hakukohde.hakukohteenLiitteet.length === 0) {
            HakukohdeService.addLiite(
                $scope.model.hakukohde,
                $scope.liitteetModel.opetusKielet,
                $scope.model.liitteidenToimitusOsoite,
                $scope.model.hakutoimistonNimi
            );
        }
        else {
            _.each($scope.model.hakukohde.hakukohteenLiitteet, function(liiteWithLangs) {
                HakukohdeService.addLiite(
                    $scope.model.hakukohde,
                    $scope.liitteetModel.opetusKielet,
                    $scope.model.liitteidenToimitusOsoite,
                    $scope.model.hakutoimistonNimi,
                    liiteWithLangs
                );
            });
        }
    }
    function updateLanguages() {
        var selectedLangs = new buckets.Set();
        _.each($scope.model.hakukohde.opetusKielet, function(lang) {
            selectedLangs.add(lang);
        });
        _.each($scope.model.hakukohde.hakukohteenLiitteet, function(liiteWithLangs) {
            _.each(liiteWithLangs, function(liite, lang) {
                if (typeof(liite) === 'object' && lang.indexOf('kieli_') !== -1) {
                    selectedLangs.add(lang);
                }
            });
        });
        $scope.liitteetModel.selectedLangs = selectedLangs.toArray();
        $scope.liitteetModel.opetusKielet = _.filter($scope.liitteetModel.langs, function(lang) {
            return _.contains($scope.liitteetModel.selectedLangs, lang.koodiUri);
        });
    }
    function doAfterLangSelection() {
        // päivitä tabit ("opetuskielet")
        // - poista poistuneet
        var i;
        for (i in $scope.liitteetModel.opetusKielet) {
            var k = $scope.liitteetModel.opetusKielet[i];
            var si = $scope.liitteetModel.selectedLangs.indexOf(k.koodiUri);
            if (si === -1) {
                $scope.liitteetModel.opetusKielet.splice(i, 1);
                $scope.status.dirtify();
            }
        }
        // - lisää lisätyt
        for (i in $scope.liitteetModel.selectedLangs) {
            var lc = $scope.liitteetModel.selectedLangs[i];
            if (containsOpetuskieli(lc)) {
                continue;
            }
            for (var j in $scope.liitteetModel.langs) {
                if ($scope.liitteetModel.langs[j].koodiUri == lc) {
                    $scope.liitteetModel.opetusKielet.push($scope.liitteetModel.langs[j]);
                    $scope.status.dirtify();
                    break;
                }
            }
        }
        addEmptyLitteet();
    }
    $scope.onLangSelection = function() {
        var liiteLangs = new buckets.Set();
        _.each($scope.model.hakukohde.hakukohteenLiitteet, function(liiteWithLangs) {
            _.each(liiteWithLangs, function(liite, lang) {
                if (typeof liite === 'object' && lang.indexOf('kieli_') !== -1) {
                    liiteLangs.add(lang);
                }
            });
        });

        var deletedLangs = _.difference(liiteLangs.toArray(), $scope.liitteetModel.selectedLangs);

        // varmista kielien poisto
        _.each(deletedLangs, function(lang) {
            dialogService.showDialog({
                title: LocalisationService.t('tarjonta.poistovahvistus.hakukohde.liitteet.kielivalilehti.otsikko'),
                description: LocalisationService.t('tarjonta.poistovahvistus.hakukohde.liitteet.kielivalilehti', [lang])
            }).result.then(function(ret) {
                if (ret) {
                    // poista kaikki valitunkieliset liitteet
                    _.each($scope.model.hakukohde.hakukohteenLiitteet, function(liiteWithLangs, index) {
                        _.each(liiteWithLangs, function(liite, keyLang) {
                            if (lang === keyLang) {
                                delete liiteWithLangs[lang];
                            }
                        });
                        var hasLiite = _.find(liiteWithLangs, function(liite) {
                            return typeof liite === 'object';
                        });
                        if (!hasLiite) {
                            $scope.deleteLiite(index, true);
                        }
                    });
                }
                else {
                    // ei poistoa -> palauta selectediin
                    $scope.liitteetModel.selectedLangs.push(lang);
                }
                doAfterLangSelection();
            });
        });
        doAfterLangSelection();
    };
    $scope.resetOsoite = function(lc, liite) {
        liite.liitteenVastaanottaja = $scope.model.hakutoimistonNimi[lc];
        liite.liitteenToimitusOsoite = angular.copy($scope.model.liitteidenToimitusOsoite[lc]);
    };
    $scope.liitteenSahkoinenOsoiteEnabledChanged = function(liite) {
        if (!liite.sahkoinenOsoiteEnabled) {
            liite.sahkoinenToimitusOsoite = undefined;
            if (liite.ensisijainenOsoiteTyyppi == $scope.OsoiteTyyppiEnum.VAIN_SAHKOINEN) {
                // Ilman sähköistä osoitetta ei voi olla "Vain sähköinen lähetys"
                liite.ensisijainenOsoiteTyyppi = $scope.OsoiteTyyppiEnum.MUU;
            }
        }
    };
    $scope.liitteenVainSahkoinenOsoiteChanged = function(liite) {
            liite.sahkoinenOsoiteEnabled = true;
            liite.liitteenVastaanottaja = null;
            liite.liitteenToimitusOsoite = null;
    };
    $scope.deleteLiite = function(index, confirm) {
        if (confirm) {
            $scope.model.hakukohde.hakukohteenLiitteet.splice(index, 1);
            $scope.status.dirtify();
        }
        else {
            var liite = _.findWhere($scope.model.hakukohde.hakukohteenLiitteet[index], {tabActive: true});
            dialogService.showDialog({
                title: LocalisationService.t('tarjonta.poistovahvistus.hakukohde.liite.title'),
                description: LocalisationService.t('tarjonta.poistovahvistus.hakukohde.liite', [liite.liitteenNimi])
            }).result.then(function(ret) {
                if (ret) {
                    $scope.deleteLiite(index, true);
                }
            });
        }
    };
    $scope.createLiite = function() {
        HakukohdeService.addLiite(
            $scope.model.hakukohde,
            $scope.liitteetModel.opetusKielet,
            $scope.model.liitteidenToimitusOsoite,
            $scope.model.hakutoimistonNimi
        );
    };
    var setLiitetyypit = function(toteutustyyppi) {
        HakukohdeService.config.getOptionsFromKoodisto(toteutustyyppi, 'liitetyypitamm', $scope.model.userLang)
            .then(function(options) {
                options.unshift({
                    nimi: '',
                    uri: ''
                });
                $scope.liitteetModel.liitetyypit = options;
            });
    };
    setLiitetyypit($scope.model.hakukohde.toteutusTyyppi);
    $scope.$watch('liitteetModel.opetusKielet.length', function() {
        $scope.liitteetModel.opetusKielet.sort($scope.sortLanguageTabs);
    });
});