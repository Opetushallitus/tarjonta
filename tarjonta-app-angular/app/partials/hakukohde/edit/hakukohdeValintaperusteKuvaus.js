var app = angular.module('app.kk.edit.hakukohde.ctrl');
app.controller('ValitseValintaPerusteKuvausDialog', function($scope, $q, $log, $modalInstance, LocalisationService,
                    Kuvaus, Koodisto, oppilaitosTyypit, tyyppi, koulutusVuosi, AuthService) {
    $log = $log.getInstance('ValitseValintaPerusteKuvausDialog');
    var koodistoKieliUri = 'kieli';
    var defaultKieliUri = 'kieli_fi';
    $scope.dialog = {};
    $scope.dialog.kuvaukset = [];
    var kaikkiVpkKielet = {};
    var kaikkiKuvaukset = {};
    $scope.valittuKuvaus = null;
    $scope.dialog.kuvauksenKielet = {};
    $scope.dialog.valitutKuvauksenKielet = [];
    $scope.dialog.copySelection = 'link';
    $scope.showKieliSelectionCheckboxDisabled = true;
    $scope.showKieliSelection = false;

    function getTranslation(key) {
        return LocalisationService.t('tarjonta.valintaperustekuvaus.valinta.dialog.' + key);
    }

    $scope.dialog.titles = {
        toimintoTitle: getTranslation('toiminto.title'),
        tableValintaRyhma: getTranslation('table.valintaryhma.title'),
        tableKuvauskielet: getTranslation('table.kuvauskielet.title'),
        tuoMyosMuutkieletTitle: getTranslation('toiminto.muutkielet.title'),
        okBtn: getTranslation('btn.ok'),
        cancelBtn: getTranslation('btn.cancel'),
        kopioiHelp: getTranslation('toiminto.kopioi.help'),
        linkkausHelp: getTranslation('toiminto.linkkaus.help')
    };
    var getYear = function() {
        if (koulutusVuosi) {
            return koulutusVuosi;
        }
        else {
            var today = new Date();
            return today.getFullYear();
        }
    };
    var initializeGrid = function() {
        console.log('INITIALIZING GRID : ', $scope.dialog.kuvaukset);
        $scope.kuvausGrid = {
            data: 'dialog.kuvaukset',
            multiSelect: false,
            selectedItems: $scope.selectedKuvaus,
            afterSelectionChange: function(row, event) {
                if ($scope.selectedKuvaus[0]) {
                    $scope.selectKuvaus($scope.selectedKuvaus[0]);
                }
            },
            columnDefs: [
                {
                    field: 'nimi',
                    displayName: $scope.dialog.titles.tableValintaRyhma,
                    width: '75%'
                },
                {
                    field: 'kielet',
                    displayName: $scope.dialog.titles.tableKuvauskielet,
                    width: '25%'
                }
            ]
        };
    };
    var getTitle = function() {
        if (tyyppi === 'valintaperustekuvaus') {
            $scope.dialog.titles.title = getTranslation('title');
            $scope.dialog.titles.kopioTitle = getTranslation('toiminto.kopioi.title');
            $scope.dialog.titles.kopioiHelp = getTranslation('toiminto.kopioi.help');
            $scope.dialog.titles.linkkausTitle = getTranslation('toiminto.linkkaus.title');
            $scope.dialog.titles.linkkausHelp = getTranslation('toiminto.linkkaus.help');
        }
        else {
            $scope.dialog.titles.title = getTranslation('sora.title');
            $scope.dialog.titles.kopioTitle = getTranslation('toiminto.kopioi.sora.title');
            $scope.dialog.titles.kopioiHelp = getTranslation('toiminto.kopioi.sora.help');
            $scope.dialog.titles.linkkausTitle = getTranslation('toiminto.linkkaus.sora.title');
            $scope.dialog.titles.linkkausHelp = getTranslation('toiminto.linkkaus.sora.help');
        }
    };
    var checkObjectPropertiesLength = function(object) {
        var elementCount = 0;
        for (var e in object) {
            elementCount++;
        }
        return elementCount;
    };
    var haeValintaPerusteet = function() {
        //TODO: refactor this to more smaller functions and separate concerns
        $log.info('VALINTAPERUSTEET OPPILAITOSTYYPIT : ', oppilaitosTyypit);
        angular.forEach(oppilaitosTyypit, function(oppilaitosTyyppi) {
            var valintaPerustePromise = Kuvaus.findWithVuosiOppilaitostyyppiTyyppiVuosi(
                oppilaitosTyyppi,
                tyyppi,
                getYear()
            );
            valintaPerustePromise.then(function(valintaperusteet) {
                $log.info('VALINTAPERUSTEET : ', valintaperusteet);
                var userLang = AuthService.getLanguage();
                $log.info('VALINTAPERUSTE USER LANGUAGE : ', userLang);
                // All different kieli promises
                var kieliPromises = {};
                var kieliPromiseArray = [];
                //Loop through valintaperusteet and get all different kieli promises
                angular.forEach(valintaperusteet.result, function(valintaPeruste) {
                    console.log('VALINTABERUSTE : ', valintaPeruste);
                    var propLength = checkObjectPropertiesLength(valintaPeruste.kuvaukset);
                    if (propLength > 0) {
                        kaikkiKuvaukset[valintaPeruste.kuvauksenTunniste] = valintaPeruste;
                        var valintaPerusteObj = {};
                        valintaPerusteObj.kielet = '';
                        valintaPerusteObj.kieliUris = [];
                        valintaPerusteObj.tunniste = valintaPeruste.kuvauksenTunniste;
                        for (var kieli in valintaPeruste.kuvaukset) {
                            if (kieli.toString().indexOf(userLang) != -1) {
                                valintaPerusteObj.nimi = valintaPeruste.kuvauksenNimet[kieli];
                                console.log('VALINTABERUSTE : ', valintaPerusteObj.nimi);
                            }
                            if (valintaPerusteObj.nimi === undefined) {
                                valintaPerusteObj.nimi = valintaPeruste.kuvauksenNimet[defaultKieliUri];
                            }
                            valintaPerusteObj.kieliUris.push(kieli);
                            if (kieliPromises[kieli] === undefined) {
                                var kieliPromise = Koodisto.getKoodi(koodistoKieliUri, kieli, userLang);
                                kieliPromises[kieli] = kieli;
                                kieliPromiseArray.push(kieliPromise);
                            }
                        }
                        if (valintaPerusteObj.nimi === undefined) {
                            for (var kiali in valintaPeruste.kuvauksenNimet) {
                                valintaPerusteObj.nimi = valintaPeruste.kuvauksenNimet[kiali];
                            }
                        }
                        console.log('VALINTAPERUSTEOBJ : ', valintaPerusteObj);
                        $scope.dialog.kuvaukset.push(valintaPerusteObj);
                    }
                });
                //Wait all promises to complete and add those values to objects
                $q.all(kieliPromiseArray).then(function(kieliKoodis) {
                    $log.info('KIELIKOODIS: ', kieliKoodis);
                    angular.forEach(kieliKoodis, function(kieliKoodi) {
                        if (kaikkiVpkKielet[kieliKoodi.koodiUri] === undefined) {
                            kaikkiVpkKielet[kieliKoodi.koodiUri] = kieliKoodi.koodiNimi;
                        }
                    });
                    //Loop through kuvaukses and find suitable name for language from object
                    angular.forEach($scope.dialog.kuvaukset, function(kuvaus) {
                        for (var i = 0; i < kuvaus.kieliUris.length; i++) {
                            var counter = kuvaus.kieliUris.length - i;
                            if (counter != 1) {
                                kuvaus.kielet = kuvaus.kielet + kaikkiVpkKielet[kuvaus.kieliUris[i]] + ',';
                            }
                            else {
                                kuvaus.kielet = kuvaus.kielet + kaikkiVpkKielet[kuvaus.kieliUris[i]];
                            }
                        }
                    });
                });
            });
        });
    };
    getTitle();
    haeValintaPerusteet();
    $scope.selectedKuvaus = [];
    initializeGrid();
    $scope.isOk = function() {
        return $scope.valittuKuvaus && $scope.dialog.valitutKuvauksenKielet.length > 0;
    };
    $scope.selectKuvaus = function(kuvaus) {
        $log.debug('SELECT ', kuvaus);
        $scope.showKieliSelectionCheckboxDisabled = false;
        $scope.dialog.kuvauksenKielet = [];
        if ($scope.valittuKuvaus) {
            $scope.valittuKuvaus.selected = false;
        }
        $scope.valittuKuvaus = kuvaus;
        $scope.valittuKuvaus.selected = true;
        // $scope.dialog.kuvauksenKielet = {};
        angular.forEach(kuvaus.kieliUris, function(kuvausKieliUri) {
            var kieliNimi = kaikkiVpkKielet[kuvausKieliUri];
            //$scope.dialog.kuvauksenKielet[kuvausKieliUri] = kieliNimi;
            var kieliObj = {
                uri: kuvausKieliUri,
                nimi: kieliNimi
            };
            $scope.dialog.kuvauksenKielet.push(kieliObj);
        });
    };
    $scope.onKieliValittu = function() {
        angular.forEach($scope.dialog.kuvauksenKielet, function(kieliObj) {
            if (kieliObj.uri === $scope.dialog.valittuKuvausKieli) {
                $scope.dialog.valitutKuvauksenKielet.push(kieliObj);
            }
        });
    };
    $scope.toggle = function(kuvaus) {
        angular.forEach($scope.dialog.valitutKuvauksenKielet, function(valittuKuvaus) {
            if (kuvaus.uri === valittuKuvaus.uri) {
                var index = $scope.dialog.valitutKuvauksenKielet.indexOf(valittuKuvaus);
                $scope.dialog.valitutKuvauksenKielet.splice(index, 1);
            }
        });
    };
    $scope.onCancel = function() {
        $modalInstance.dismiss('cancel');
    };
    $scope.onOk = function() {
        var valitutKuvaukset = [];
        angular.forEach($scope.dialog.valitutKuvauksenKielet, function(valittuKieli) {
            if ($scope.valittuKuvaus !== undefined) {
                $log.debug('VALITTU KUVAUS: ', $scope.valittuKuvaus);
                var valittuKokoKuvaus = kaikkiKuvaukset[$scope.valittuKuvaus.tunniste];
                var kuvaus = {
                    toimintoTyyppi: $scope.dialog.copySelection,
                    tunniste: valittuKokoKuvaus.kuvauksenTunniste,
                    teksti: valittuKokoKuvaus.kuvaukset[valittuKieli],
                    kieliUri: valittuKieli
                };
                valitutKuvaukset.push(kuvaus);
            }
        });
        $modalInstance.close(valitutKuvaukset);
    };
});