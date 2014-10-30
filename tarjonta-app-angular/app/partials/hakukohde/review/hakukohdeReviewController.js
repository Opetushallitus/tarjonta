var app = angular.module('app.kk.edit.hakukohde.review.ctrl', ['app.services', 'Haku', 'Organisaatio', 'Koodisto', 'localisation', 'Hakukohde', 'auth',
    'config', 'MonikielinenTextArea', 'MonikielinenText']);

app.controller('HakukohdeReviewController', function($scope, $q, $log, LocalisationService, OrganisaatioService, Koodisto, Hakukohde, AuthService,
        dialogService, HakuService, $modal, Config, $location, $timeout, $route, TarjontaService, HakukohdeKoulutukses, dialogService, SisaltyvyysUtil,
        TreeHandlers, PermissionService) {

    $log = $log.getInstance("HakukohdeReviewController");
    $log.debug("init...");

    // by default disable
    $scope.isMutable = false;
    $scope.isPartiallyMutable = false;
    $scope.isRemovable = false;
    $scope.showNimiUri = false;
    $scope.isAiku = false;
    $scope.isKK = false;
    $scope.hakukohteenNimiUri = undefined;
    // $log.debug("scope.model:", $scope.model);

    var aikuKoulutuslajiUri = "koulutuslaji_a";

    var kieliKoodistoUri = "kieli";

    $scope.model.hakukohteenKielet = [];
    $scope.model.koulutukses = [];
    $scope.model.hakukelpoisuusVaatimukses = [];
    $scope.model.painotettavatOppiaineet = [];

    // Try to get the user language and if for some reason it can't be
    // retrieved, use FI as default
    $scope.model.userLang = AuthService.getLanguage();

    // form controls
    $scope.formControls = {};

    $scope.model.validationmsgs = [];

    $scope.model.showError = false;

    $scope.model.organisaatioNimet = [];

    var orgSet = new buckets.Set();

    $scope.goBack = function(event) {
        window.history.back();
    };

    // liitteiden / valintakokeiden kielet
    function aggregateLangs(items) {
        var ret = [];
        for (var i in items) {
            var kieli = items[i].kieliUri;
            if (kieli && ret.indexOf(kieli) == -1) {
                ret.push(kieli);
            }
        }
        return ret;
    }

    $scope.model.valintakoeKielet = aggregateLangs($scope.model.hakukohde.valintakokeet);
    $scope.model.liiteKielet = aggregateLangs($scope.model.hakukohde.hakukohteenLiitteet);


    $scope.model.ryhmat = {};

    $scope.showLiitteidenToimitustiedot = function(toteutusTyyppi) {
        return toteutusTyyppi === 'AMMATILLINEN_PERUSTUTKINTO' ||
            toteutusTyyppi === 'LUKIOKOULUTUS' ||
            toteutusTyyppi === 'VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS' ||
            toteutusTyyppi === 'VAPAAN_SIVISTYSTYON_KOULUTUS' ||
            toteutusTyyppi === 'AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA';
    };

    if ($scope.model.hakukohde.organisaatioRyhmaOids) {
        for (var i = 0; i < $scope.model.hakukohde.organisaatioRyhmaOids.length; i++) {
            var oid = $scope.model.hakukohde.organisaatioRyhmaOids[i];
            (function(oid) {
                OrganisaatioService.nimi(oid).then(function(nimi) {
                    $scope.model.ryhmat[oid] = nimi
                });
            })(oid);
        }
    }

    /*
     * ----------------------------> Helper functions <
     * ----------------------------
     */
    /*
     *
     * ----------> This functions loops through hakukohde names and
     * lisätiedot to get hakukohdes languages
     *
     */

    var convertValintaPalveluValue = function() {

        if ($scope.model.hakukohde.kaytetaanJarjestelmanValintaPalvelua) {
            $scope.model.kaytetaanJarjestelmanValintaPalveluaArvo = LocalisationService.t('hakukohde.review.perustiedot.jarjestelmanvalinta.palvelu.kylla');
        } else {
            $scope.model.kaytetaanJarjestelmanValintaPalveluaArvo = LocalisationService.t('hakukohde.review.perustiedot.jarjestelmanvalinta.palvelu.ei');
        }

    };

    var convertKaksoistutkintoValue = function() {

        if ($scope.model.hakukohde.kaksoisTutkinto) {
            $scope.model.kaksoisTutkinto = LocalisationService.t('hakukohde.review.perustiedot.kaksoitutkinto.kylla');
        } else {
            $scope.model.kaksoisTutkinto = LocalisationService.t('hakukohde.review.perustiedot.kaksoitutkinto.ei');
        }
    };

    var loadKielesSetFromHakukohde = function() {

        //wtf???

        var koodiPromises = [];

        if ($scope.model.allkieles === undefined || $scope.model.allkieles.length < 1) {
            var allKieles = new buckets.Set();

            for (var kieliUri in $scope.model.hakukohde.hakukohteenNimet) {

                allKieles.add(kieliUri);
            }

            for (var kieliUri in $scope.model.hakukohde.lisatiedot) {
                allKieles.add(kieliUri);
            }

            angular.forEach($scope.model.hakukohde.valintakokeet, function(valintakoe) {

                allKieles.add(valintakoe.kieliUri);

            });

            angular.forEach($scope.model.hakukohde.hakukohteenLiitteet, function(liite) {

                allKieles.add(liite.kieliUri);

            });

            $scope.model.allkieles = allKieles.toArray();
        }

        if ($scope.model.allkieles !== undefined) {

            angular.forEach($scope.model.allkieles, function(hakukohdeKieli) {

                var koodi = Koodisto.getKoodi(kieliKoodistoUri, hakukohdeKieli, $scope.model.userLang);
                koodiPromises.push(koodi);
            });
        }

        angular.forEach(koodiPromises, function(koodiPromise) {
            koodiPromise.then(function(koodi) {
                var hakukohteenKieli = {
                    kieliUri: koodi.koodiUri,
                    kieliNimi: koodi.koodiNimi
                };
                $scope.model.hakukohteenKielet.push(hakukohteenKieli);
            });
        });

    };

    var initLanguages = function() {

        console.log('Init languages');

        // Get all kieles from hakukohdes names and additional informaty
        var allKieles = new buckets.Set();

        for (var kieliUri in $scope.model.hakukohde.hakukohteenNimet) {

            allKieles.add(kieliUri);
        }

        for (var kieliUri in $scope.model.hakukohde.lisatiedot) {
            allKieles.add(kieliUri);
        }
        $scope.model.allkieles = allKieles.toArray();

    };

    var checkIsOkToRemoveKoulutus = function() {
        if ($scope.model.koulutukses.length > 1) {

            return true;
        } else {
            return false;
        }

    };

    var createFormattedDateString = function(date) {

        return moment(date).format('DD.MM.YYYY HH:mm');

    };

    var filterNewKoulutukses = function(koulutukses) {

        var newKoulutukses = [];

        angular.forEach(koulutukses, function(koulutusOid) {
            var wasFound = false;

            angular.forEach($scope.model.koulutukses, function(koulutus) {
                if (koulutus.oid === koulutusOid) {
                    wasFound = true;
                }
            });

            if (!wasFound) {
                newKoulutukses.push(koulutusOid);
            }
        });
        return newKoulutukses;
    };

    var filterRemovedKoulutusFromHakukohde = function(koulutusOid) {

        angular.forEach($scope.model.hakukohde.hakukohdeKoulutusOids, function(hakukohdeKoulutusOid) {

            if (hakukohdeKoulutusOid === koulutusOid) {
                var index = $scope.model.hakukohde.hakukohdeKoulutusOids.indexOf(hakukohdeKoulutusOid);
                $scope.model.hakukohde.hakukohdeKoulutusOids.splice(index, 1);
            }

        });

    };

    var filterKoulutuksesToBeRemoved = function(newKoulutusOidArray) {

        var koulutuksesToRemove = [];

        angular.forEach($scope.model.koulutukses, function(koulutus) {
            var koulutusFound = false;
            angular.forEach(newKoulutusOidArray, function(newKoulutusOid) {
                if (koulutus.oid === newKoulutusOid) {
                    koulutusFound = true;
                }
            });

            if (!koulutusFound) {
                koulutuksesToRemove.push(koulutus.oid);
            }
        });

        return koulutuksesToRemove;

    };

    /*
     *
     * ------------> Function to get koodisto koodis and call
     * resultHandler to process those results
     *
     */

    var getKoodisWithKoodisto = function(koodistoUri, resultHandlerFunction) {

        var koodistoPromise = Koodisto.getAllKoodisWithKoodiUri(koodistoUri, $scope.model.userLang);

        koodistoPromise.then(function(koodis) {
            resultHandlerFunction(koodis);
        });

    };

    /*
     *
     * --------> Function to get specific koodi information and call
     * result handler to process that
     *
     *
     */
    var getKoodiWithUri = function(koodistoUri, koodiUri, resultHandlerFunction) {

        var koodiPromise = Koodisto.getKoodi(koodistoUri, koodiUri, $scope.model.userLang);

        koodiPromise.then(function(koodi) {
            resultHandlerFunction(koodi);
        });

    };

    /*
     *
     * ------------> This function retrieves haku and it's name so that it
     * can be shown
     *
     */

    var loadHakuInformation = function() {
        if ($scope.model.hakukohde.hakuOid) {

            var hakuPromise = HakuService.getHakuWithOid($scope.model.hakukohde.hakuOid);
            hakuPromise.then(function(haku) {
                {
                    $log.debug('HAKU: ', haku);
                    if (haku && haku.nimi)
                        for (var kieliUri in haku.nimi) {
                            var upperCaseKieliUri = kieliUri.toUpperCase();
                            var upperUserLang = $scope.model.userLang.toUpperCase();
                            if (upperCaseKieliUri.indexOf(upperUserLang) != -1) {
                                $scope.model.hakuNimi = haku.nimi[kieliUri];
                            }
                        }

                    if($scope.toisenAsteenKoulutus($scope.model.hakukohde.toteutusTyyppi) &&
                        $scope.model.hakukohde.hakuaikaAlkuPvm !== undefined &&
                        $scope.model.hakukohde.hakuaikaLoppuPvm !== undefined) {
                        $scope.model.hakuNimi = $scope.model.hakuNimi + "  ( " + createFormattedDateString($scope.model.hakukohde.hakuaikaAlkuPvm) + " - "
                            + createFormattedDateString($scope.model.hakukohde.hakuaikaLoppuPvm) + " ) ";
                    } else {
                        if (haku.hakuaikas !== undefined && haku.hakuaikas.length > 0 && $scope.model.hakukohde.hakuaikaId !== undefined) {
                            var valittuHakuAika = undefined;
                            angular.forEach(haku.hakuaikas, function(hakuaika) {

                                if (hakuaika.hakuaikaId === $scope.model.hakukohde.hakuaikaId) {
                                    valittuHakuAika = hakuaika;
                                }

                            });
                            if (valittuHakuAika !== undefined) {
                                var prefix = valittuHakuAika.nimi !== undefined ? valittuHakuAika.nimi + " : " : "";
                                $scope.model.hakuNimi = $scope.model.hakuNimi + "  ( " + prefix + createFormattedDateString(valittuHakuAika.alkuPvm) + " - "
                                        + createFormattedDateString(valittuHakuAika.loppuPvm) + " ) ";
                            }
                        }
                    }
                }
            });
        }
    };

    /*
     *
     * ---------> This function retrieves hakukelpoisuusVaatimukses and
     * adds results to model
     *
     */

    var loadHakukelpoisuusVaatimukses = function() {

        var koodistot = {
            LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA: "hakukelpoisuusvaatimusta",
            KORKEAKOULUTUS: "pohjakoulutusvaatimuskorkeakoulut",
            LUKIOKOULUTUS: "hakukelpoisuusvaatimusta",
            AMMATILLINEN_PERUSTUTKINTO: "hakukelpoisuusvaatimusta"
        };

        var koodisto = koodistot[$scope.model.hakukohde.toteutusTyyppi];
        if (!koodisto) {
            $log.error("don't know which koodisto to use??!? toteutusTyyppi:", $scope.model.hakukohde.toteutusTyyppi);
        }

        angular.forEach($scope.model.hakukohde.hakukelpoisuusvaatimusUris, function(hakukelpoisuusVaatimusUri) {
            getKoodiWithUri(koodisto, hakukelpoisuusVaatimusUri, function(hakukelpoisuusVaatimusKoodi) {
                $scope.model.hakukelpoisuusVaatimukses.push(hakukelpoisuusVaatimusKoodi.koodiNimi);

            });
        });

    };

    var loadPainotettavatOppiaineet = function() {

        var koodistoUri = "painotettavatoppiaineetlukiossa";

        angular.forEach($scope.model.hakukohde.painotettavatOppiaineet, function(painotettavaOppiaine) {
            getKoodiWithUri(koodistoUri, painotettavaOppiaine.oppiaineUri, function(oppiaineKoodi) {
                var oppiaine = { nimi: oppiaineKoodi.koodiNimi, painokerroin: painotettavaOppiaine.painokerroin};
                $scope.model.painotettavatOppiaineet.push(oppiaine);
            });
        });
    };

    var modelInit = function() {

        $scope.model.collapse = {
            perusTiedot: false,
            valintakokeet: true,
            liitteet: true,
            valintaperusteet: true,
            sorakuvaukset: true,
            koulutukset: false,
            model: true
        };
    };

    /*
     *
     * ---------> Load koulutukses to show hakukohde related koulutukses
     *
     */

    var loadKoulutukses = function() {

        if ($scope.model.hakukohde.hakukohdeKoulutusOids !== undefined) {

            var spec = {
                koulutusOid: $scope.model.hakukohde.hakukohdeKoulutusOids
            };

            TarjontaService.haeKoulutukset(spec).then(function(data) {

                var tarjoajaOidsSet = new buckets.Set();
                console.log('HAKUKOHDE REVIEW KOULUTUKSET : ', data);
                if (data.tulokset !== undefined) {
                    $scope.model.koulutukses.splice(0, $scope.model.koulutukses.length);
                    angular.forEach(data.tulokset, function(tulos) {
                        tarjoajaOidsSet.add(tulos.oid);
                        if (tulos.tulokset !== undefined) {
                            angular.forEach(tulos.tulokset, function(lopullinenTulos) {
                                if (lopullinenTulos.koulutuslajiUri && lopullinenTulos.koulutuslajiUri.indexOf(aikuKoulutuslajiUri) > -1) {
                                    $scope.isAiku = true;
                                }

                                $scope.isKK = lopullinenTulos.koulutusasteTyyppi == "KORKEAKOULUTUS";

                                var koulutus = {
                                    nimi: lopullinenTulos.nimi,
                                    oid: lopullinenTulos.oid
                                };

                                // KJOH-778 monta tarjoajaa
                                OrganisaatioService.getPopulatedOrganizations($scope.model.hakukohde.koulutusmoduuliToteutusTarjoajatiedot[lopullinenTulos.oid].tarjoajaOids).then(function(orgs) {
                                    var koulutusRef= koulutus;
                                    angular.forEach(orgs, function(org) {
                                        var copy = angular.copy(koulutusRef);
                                        copy.nimi += " (" + org.nimi + ")";
                                        copy.tarjoajaOid = org.oid;
                                        $scope.model.koulutukses.push(copy);
                                   });
                                });
                            });
                        }
                    });

                    $scope.model.hakukohde.tarjoajaOids = tarjoajaOidsSet.toArray();

                    var orgQueryPromises = [];

                    angular.forEach($scope.model.hakukohde.tarjoajaOids, function(tarjoajaOid) {

                        orgQueryPromises.push(OrganisaatioService.byOid(tarjoajaOid));

                    });

                    $q.all(orgQueryPromises).then(function(orgs) {

                        angular.forEach(orgs, function(data) {

                            orgSet.add(data.nimi);

                        });

                        $scope.model.organisaatioNimet = orgSet.toArray();

                    });

                }

            });

        }

    };

    var checkForHakuRemove = function() {

        var possibleStates = TarjontaService.getTilat()[$scope.model.hakukohde.tila];

        var canRemoveHakukohde = possibleStates.removable && TarjontaService.parameterCanRemoveHakukohdeFromHaku($scope.model.hakukohde.hakuOid);
        var canEditHakukohdeAtAll = TarjontaService.parameterCanEditHakukohde($scope.model.hakukohde.hakuOid);
        var canPartiallyEditHakukohde = TarjontaService.parameterCanEditHakukohdeLimited($scope.model.hakukohde.hakuOid);

        if (canRemoveHakukohde && canEditHakukohdeAtAll) {
            $scope.isRemovable = true;
            $scope.isMutable = true;
            $scope.isPartiallyMutable = true;
        } else if (canPartiallyEditHakukohde) {
            $scope.isMutable = false;
            $scope.isRemovable = false;
            $scope.isPartiallyMutable = true;
        } else {

            $scope.isMutable = false;
            $scope.isRemovable = false;
            $scope.isPartiallyMutable = false;
        }

    };

    var getPisterajat = function(valintakoe, targetPisterajaTyyppi) {
        for(var i in valintakoe.pisterajat) {
            var pisterajatyyppi = valintakoe.pisterajat[i].pisterajatyyppi;
            if(pisterajatyyppi === targetPisterajaTyyppi) {
                return valintakoe.pisterajat[i];
            }
        }
        return undefined;
    };

    var init = function() {
        var hakukohdeOid = $scope.model.hakukohde.oid;

        // permissiot
        $q.all([PermissionService.hakukohde.canEdit(hakukohdeOid), PermissionService.hakukohde.canDelete(hakukohdeOid), Hakukohde.checkStateChange({
                oid: hakukohdeOid,
                state: 'POISTETTU'
            }).$promise.then(function(r) {
                return r.$resolved;
            })]).then(function(results) {
            $scope.isMutable = results[0] === true;
            if ($scope.model.hakukohde.koulutusAsteTyyppi === 'LUKIOKOULUTUS') {

                $scope.isMutable = false;

            }
            $scope.isRemovable = results[1] === true && results[2] === true;
            checkForHakuRemove();

            var tila = $scope.model.hakukohde.tila;
            if(['JULKAISTU','POISTETTU'].indexOf(tila) != -1) {
                $scope.isRemovable = false;
            }
        });

        if ($scope.model.hakukohde.result) {
            $scope.model.hakukohde = new Hakukohde($scope.model.hakukohde.result);
        }

        console.log('REVIEW HAKUKOHDE :', $scope.model.hakukohde);
        if ($scope.model.hakukohde.hakukohteenNimiUri &&
            $scope.model.hakukohde.toteutusTyyppi !== 'VAPAAN_SIVISTYSTYON_KOULUTUS') {
            $scope.showNimiUri = true;
        }

        if($scope.model.hakukohde.toteutusTyyppi === 'LUKIOKOULUTUS') {
            $scope.model.hakukohde.valintakoe =  $scope.model.hakukohde.valintakokeet[0];
            $scope.model.hakukohde.paasykoe = getPisterajat($scope.model.hakukohde.valintakoe, 'Paasykoe');
            $scope.model.hakukohde.lisapisteet = getPisterajat($scope.model.hakukohde.valintakoe, 'Lisapisteet');
            $scope.model.hakukohde.kokonaispisteet = getPisterajat($scope.model.hakukohde.valintakoe, 'Kokonaispisteet');
        }

        initLanguages();
        convertValintaPalveluValue();
        convertKaksoistutkintoValue();
        loadKielesSetFromHakukohde();
        loadHakuInformation();
        modelInit();
        loadHakukelpoisuusVaatimukses();
        loadPainotettavatOppiaineet();
        loadKoulutukses();
    };

    init();

    /*
     *
     * -----------> Controller event/click handlers etc.
     * <------------------
     *
     */

    $scope.getHakukohteenJaOrganisaationNimi = function(locale) {

        console.log('ORGANISAATION NIMET : ', $scope.model.organisaatioNimet);

        var ret = "";
        var ja = LocalisationService.t("tarjonta.yleiset.ja");
        if($scope.showNimiUri) {
            if($scope.hakukohteenNimiUri === undefined) {
                Koodisto.searchKoodi($scope.model.hakukohde.hakukohteenNimiUri, locale).then(
                    function(data){
                        $scope.hakukohteenNimiUri = data;
                    }
                );
            }
            ret =  "<b>" + $scope.hakukohteenNimiUri + "</b>"
        } else if($scope.model.hakukohde.toteutusTyyppi === 'VAPAAN_SIVISTYSTYON_KOULUTUS') {
            ret = $scope.model.hakukohde.hakukohteenNimi;
        } else {
            for (var i in $scope.model.hakukohde.hakukohteenNimet) {
                if (i > 0) {
                    ret = ret + ((i == $scope.model.hakukohde.hakukohteenNimet.length - 1) ? " " + ja + " " : ", ");
                }
                ret = ret + "<b>" + $scope.model.hakukohde.hakukohteenNimet[i] + "</b>";
            }
        }

        if ($scope.model.organisaatioNimet.length < 2 && $scope.model.organisaatioNimet.length > 0) {

            var organisaatiolleMsg = LocalisationService.t("tarjonta.hakukohde.title.org");

            ret = ret + ". " + organisaatiolleMsg + " : <b>" + $scope.model.organisaatioNimet[0] + " </b>";

        } else {
            var counter = 0;
            var organisaatioilleMsg = LocalisationService.t("tarjonta.hakukohde.title.orgs");
            angular.forEach($scope.model.organisaatioNimet, function(organisaatioNimi) {

                if (counter === 0) {

                    ret = ret + ". " + organisaatioilleMsg + " : <b>" + organisaatioNimi + " </b>";

                } else {

                    // ret = ret +
                    // ((counter===$scope.model.organisaatioNimet.length-1) ? " "
                    // : ", ");

                    ret = ret + ", <b>" + organisaatioNimi + "</b>";

                }
                counter++;

            });

        }
        return ret;

    };

    $scope.getHakukohteenNimi = function() {

        if ($scope.model == undefined || $scope.model.hakukohde == undefined || $scope.model.hakukohde.hakukohteenNimet == undefined) {
            return null;
        }
        var lc = $scope.model.hakukohde.hakukohteenNimet[kieliKoodistoUri + "_" + $scope.model.userLang.toLowerCase()];
        if (lc) {
            return lc;
        }

        for (var i in $scope.model.hakukohde.hakukohteenNimet) {
            return $scope.model.hakukohde.hakukohteenNimet[i];
        }
        return null;

    };

    $scope.doEdit = function(event, targetPart) {
        $log.debug("doEdit()...", event, targetPart);
        var navigationUri = "/hakukohde/" + $scope.model.hakukohde.oid + "/edit";
        $location.path(navigationUri);
    };

    $scope.removeRyhma = function(ryhmaOid) {
        TarjontaService.poistaHakukohderyhma($scope.model.hakukohde.oid, ryhmaOid).then();
        delete $scope.model.ryhmat[ryhmaOid];
    }

    $scope.goBack = function(event) {
        // window.history.back();
        $location.path('/etusivu');
    };

    $scope.doCopy = function(event) {
        $location.path('/hakukohde/' + $scope.model.hakukohde.oid + '/edit/copy');
    }

    $scope.doDelete = function() {

        var texts = {
            title: LocalisationService.t("hakukohde.review.remove.title"),
            description: LocalisationService.t("hakukohde.review.remove.desc"),
            ok: LocalisationService.t("ok"),
            cancel: LocalisationService.t("cancel")
        };

        var d = dialogService.showDialog(texts);
        d.result.then(function(data) {
            if (data) {

                var hakukohdeResource = new Hakukohde($scope.model.hakukohde);
                var resultPromise = hakukohdeResource.$delete();
                resultPromise.then(function(result) {

                    $log.debug('GOT RESULT : ', result);

                    if (result.status === "OK") {

                        // TKatva, 18.3.2014. Commented confirmation dialog away, if
                        // needed return it.
                        /*
                         * var confTexts = { title:
                         * LocalisationService.t("hakukohde.review.remove.confirmation.title"),
                         * description:
                         * LocalisationService.t("hakukohde.review.remove.confirmation.desc"),
                         * ok: LocalisationService.t("ok")};
                         *
                         * var dd = dialogService.showDialog(confTexts);
                         *
                         * dd.result.then(function(daatta){
                         * $location.path('/etusivu'); });
                         */
                        $location.path('/etusivu');

                    } else {
                        // TODO: Show some error message
                    }

                });

            }
        });

    }

    $scope.getLocalizedValintakoe = function(kieliUri) {

        var localizedValintakokeet = [];

        angular.forEach($scope.model.hakukohde.valintakokeet, function(valintakoe) {

            if (valintakoe.kieliUri === kieliUri) {
                localizedValintakokeet.push(valintakoe);
            }

        });

        return localizedValintakokeet;

    };

    $scope.getLocalizedLiitteet = function(kieliUri) {

        var localizedLiitteet = [];

        angular.forEach($scope.model.hakukohde.hakukohteenLiitteet, function(liite) {
            if (liite.kieliUri === kieliUri) {
                localizedLiitteet.push(liite);
            }
        });

        return localizedLiitteet;

    };

    var removeKoulutusRelationsFromHakukohde = function(koulutuksesArray) {

        HakukohdeKoulutukses.removeKoulutuksesFromHakukohde($scope.model.hakukohde.oid, koulutuksesArray);

        if ($scope.model.koulutukses.length > 0) {

            var toBeRemoved = [];

            angular.forEach($scope.model.koulutukses, function(koulutusIndex) {
                angular.forEach(koulutuksesArray, function(koulutus) {
                    if (koulutusIndex.oid === koulutus.oid && koulutusIndex.tarjoajaOid === koulutus.tarjoajaOid) {
                        toBeRemoved.push({oid: koulutus.oid, tarjoajaOid: koulutus.tarjoajaOid});
                    }
                });
            });

            var tarjoajatiedot = $scope.model.hakukohde.koulutusmoduuliToteutusTarjoajatiedot;

            angular.forEach(toBeRemoved, function(koulutus) {
                $scope.model.koulutukses = _.without($scope.model.koulutukses,
                    _.findWhere($scope.model.koulutukses, {oid: koulutus.oid, tarjoajaOid: koulutus.tarjoajaOid}));

                if (tarjoajatiedot[koulutus.oid]){
                    var newTarjoajaOids = [];
                    _.each(tarjoajatiedot[koulutus.oid].tarjoajaOids, function(tarjoajaOid) {
                        if (tarjoajaOid !== koulutus.tarjoajaOid) {
                            newTarjoajaOids.push(tarjoajaOid);
                        }
                    });
                    if (newTarjoajaOids.length === 0) {
                        delete tarjoajatiedot[koulutus.oid];
                    }
                    else {
                        tarjoajatiedot[koulutus.oid].tarjoajaOids = newTarjoajaOids;
                    }
                }

                var index = $scope.model.hakukohde.hakukohdeKoulutusOids.indexOf(koulutus.oid);
                if (index !== -1) {
                    $scope.model.hakukohde.hakukohdeKoulutusOids.splice(index, 1);
                }
            });

        }
        else {
            $location.path("/etusivu");
        }
    };

    var reallyRemoveKoulutusFromHakukohde = function(koulutus) {
        var koulutukset = [];
        koulutukset.push({oid: koulutus.oid, tarjoajaOid: koulutus.tarjoajaOid});
        removeKoulutusRelationsFromHakukohde(koulutukset);
    };

    $scope.showKoulutusHakukohtees = function(koulutus) {

        var hakukohdePromise = HakukohdeKoulutukses.getKoulutusHakukohdes(koulutus.oid);

        hakukohdePromise.then(function(hakukohteet) {

            var modalInstance = $modal.open({
                templateUrl: 'partials/hakukohde/review/showKoulutusHakukohtees.html',
                controller: 'ShowKoulutusHakukohtees',
                windowClass: 'liita-koulutus-modal',
                resolve: {
                    hakukohtees: function() {
                        return hakukohteet.result;
                    },
                    selectedLocale: function() {
                        return $scope.model.userLang;
                    }

                }
            });
        });

    };

    $scope.removeKoulutusFromHakukohde = function(koulutus) {

        if (checkIsOkToRemoveKoulutus()) {

            var texts = {
                title: LocalisationService.t("hakukohde.review.remove.koulutus.title"),
                description: LocalisationService.t("hakukohde.review.remove.koulutus.desc"),
                ok: LocalisationService.t("ok"),
                cancel: LocalisationService.t("cancel")
            };

            var d = dialogService.showDialog(texts);
            d.result.then(function(data) {
                if (data) {
                    reallyRemoveKoulutusFromHakukohde(koulutus);
                }
            });

        } else {

            $scope.model.validationmsgs.push('hakukohde.review.remove.koulutus.exp.msg');
            $scope.model.showError = true;

        }

    };

    $scope.getLiitteenKuvaus = function(liite, kieliUri) {
        return liite.liitteenKuvaukset[kieliUri];
    };

    $scope.getValintaperusteKuvaus = function(kieliUri) {

        if ($scope.model.hakukohde.valintaperusteKuvaukset !== undefined) {
            return $scope.model.hakukohde.valintaperusteKuvaukset[kieliUri];
        }

    };

    $scope.getSoraKuvaus = function(kieliUri) {

        if ($scope.model.hakukohde.soraKuvaukset !== undefined) {
            return $scope.model.hakukohde.soraKuvaukset[kieliUri];
        }

    };

    $scope.getKomotoTarjoajatiedot = function() {
        var result = [];
        angular.forEach($scope.model.hakukohde.koulutusmoduuliToteutusTarjoajatiedot, function(value, koulutusOid) {
            angular.forEach(value.tarjoajaOids, function(tarjoajaOid) {
                var koulutus = {oid: koulutusOid, tarjoajaOid: tarjoajaOid}
                result.push(koulutus);
            })
        });
        return result;
    }

    $scope.openLiitaKoulutusModal = function() {

        var modalInstance = $modal.open({
            templateUrl: 'partials/hakukohde/review/hakukohdeLiitaKoulutus.html',
            controller: 'HakukohdeLiitaKoulutusModalCtrl',
            windowClass: 'liita-koulutus-modal',
            resolve: {
                organisaatioOids: function() {
                    return $scope.model.hakukohde.tarjoajaOids;
                },
                selectedLocale: function() {
                    return $scope.model.userLang;
                },
                selectedKoulutukses: function() {
                    return $scope.getKomotoTarjoajatiedot();
                }
            }
        });

        // First remove all existing relations and then add selected
        // relations
        modalInstance.result.then(function(liitettavatKoulutukset) {

            angular.forEach(liitettavatKoulutukset, function(liitettavaKoulutus) {

                $scope.model.hakukohde.hakukohdeKoulutusOids.push(liitettavaKoulutus.oid);

                var tarjoajatiedot = $scope.model.hakukohde.koulutusmoduuliToteutusTarjoajatiedot[liitettavaKoulutus.oid];
                if(tarjoajatiedot === undefined) {
                    var oids = [];
                    oids.push(liitettavaKoulutus.tarjoajaOid);
                    $scope.model.hakukohde.koulutusmoduuliToteutusTarjoajatiedot[liitettavaKoulutus.oid] = {tarjoajaOids: oids};
                } else {
                    tarjoajatiedot.tarjoajaOids.push(liitettavaKoulutus.tarjoajaOid);
                }
            });

            var liitaPromise = HakukohdeKoulutukses.addKoulutuksesToHakukohde($scope.model.hakukohde.oid, liitettavatKoulutukset);
            liitaPromise.then(function(data) {
                if (data) {
                    $log.debug('RETURN DATA : ', data);
                    loadKoulutukses();
                } else {
                    $log.debug('UNSUCCESFUL : ', data);
                }
            });
        });
    };
});

/*
 *
 * ----------------> Show koulutus hakukohdes modal controller definition
 * <-----------------
 *
 */

app.controller('ShowKoulutusHakukohtees', function($scope, $log, $modalInstance, LocalisationService, hakukohtees, selectedLocale) {

    $log = $log.getInstance("ShowKoulutusHakukohtees");
    $log.debug("init...");

    $scope.model = {};

    $scope.model.locale = selectedLocale;

    $scope.model.hakukohteet = hakukohtees;

    $scope.model.translations = {
        title: LocalisationService.t('hakukohde.review.koulutus.hakukohteet.title'),
        okBtn: LocalisationService.t('hakukohde.review.koulutus.hakukohteet.ok.btn')

    };

    $scope.model.cancel = function() {
        $log.debug("cancel");
        $modalInstance.dismiss('cancel');
    };

});

/*
 *
 * ----------------> Liita koulutus modal controller definition
 * <------------------
 *
 *
 */

app.controller('HakukohdeLiitaKoulutusModalCtrl', function($scope, $log, $modalInstance, LocalisationService, Config, TarjontaService, OrganisaatioService,  organisaatioOids,
        selectedLocale, selectedKoulutukses) {

    $log = $log.getInstance("HakukohdeLiitaKoulutusModalCtrl");
    $log.debug("init...");

    /*

     ----------> Init controller variables etc. <--------------

     */

    $scope.model = {};

    $scope.model.helper = {
        functions: {},
        allKoulutuksesMap: {}
    };

    $scope.model.translations = {
        title: LocalisationService.t('hakukohde.review.liita.koulutus.title'),
        poistaBtn: LocalisationService.t('hakukohde.review.liita.koulutus.poistaBtn'),
        cancelBtn: LocalisationService.t('tarjonta.hakukohde.liite.modal.peruuta.button'),
        saveBtn: LocalisationService.t('tarjonta.hakukohde.liite.modal.tallenna.button')

    };

    $scope.model.koodistoLocale = selectedLocale;

    $scope.model.selectedKoulutukses = [];

    $scope.model.searchKomoOids = [];

    $scope.model.hakutulos = [];

    /*

     ----------> Define "initialization functions <------------

     */
    var loadKomotos = function() {

        $scope.model.spec = {//search parameter object
            oid: organisaatioOids,
            terms: '', //search words
            state: null,
            year: null,
            season: null
        };

        $scope.model.selectedKoulutukses = [];
        $scope.gridOptions = {
            data: 'model.hakutulos',
            selectedItems: $scope.model.selectedKoulutukses,
            columnDefs: [{
                    field: 'koulutuskoodi',
                    displayName: LocalisationService.t('sisaltyvyys.hakutulos.arvo', $scope.koodistoLocale),
                    width: "20%"
                }, {
                    field: 'nimi',
                    displayName: LocalisationService.t('sisaltyvyys.koulutus.nimi', $scope.koodistoLocale),
                    width: "40%"
                }, {
                    field: 'tarjoaja',
                    displayName: LocalisationService.t('sisaltyvyys.hakutulos.tarjoaja', $scope.koodistoLocale),
                    width: "40%"
                }],
            showSelectionCheckbox: false,
            multiSelect: true
        };

        TarjontaService.haeKoulutukset({
            koulutusOid: selectedKoulutukses[0].oid
        }).then(function(result) {
            //vuosi/kausi rajoite
            $scope.model.spec.season = result.tulokset[0].tulokset[0].kausiUri;
            $scope.model.spec.year = result.tulokset[0].tulokset[0].vuosi;

            TarjontaService.haeKoulutukset($scope.model.spec).then(function(result) {

                var tarjoajaOids = [];
                _.each(result.tulokset, function(rootTulos){
                    _.each(rootTulos.tulokset, function(childTulos){
                        if(childTulos.tarjoajat && childTulos.tarjoajat.length > 0) {
                            _.each(childTulos.tarjoajat, function(tarjoajaOid){
                                if(tarjoajaOids.indexOf(tarjoajaOid) === -1) {
                                    tarjoajaOids.push(tarjoajaOid);
                                }
                            });
                        };
                    });
                });

                OrganisaatioService.getPopulatedOrganizations(tarjoajaOids).then(function(organizations) {
                    var hakutulos = [];
                    _.each(result.tulokset, function(rootTulos){
                        _.each(rootTulos.tulokset, function(childTulos){
                            _.each(childTulos.tarjoajat, function(tarjoaja) {
                                hakutulos.push({
                                    koulutuskoodi: childTulos.koulutuskoodi.split("#")[0].split("_")[1],
                                    nimi: childTulos.nimi,
                                    tarjoaja: _.find(organizations, function (org) {
                                        return org.oid === tarjoaja;
                                    }).nimi,
                                    tarjoajaOid: tarjoaja,
                                    oid: childTulos.oid
                                });
                            });
                        });
                    });
                    $scope.model.hakutulos = hakutulos;
                    $scope.model.hakutulos = $scope.model.hakutulos.reduce(function(prev, koulutus) {
                        var found =_.find(selectedKoulutukses, function(selected){
                            return selected.oid === koulutus.oid && selected.tarjoajaOid === koulutus.tarjoajaOid;
                        });
                        if (found === undefined) {
                            prev.push(koulutus);
                        }
                        return prev;
                    }, []);
                });
            });
        });

    };

    loadKomotos();

    $scope.model.cancel = function() {
        $log.debug("cancel()");
        $modalInstance.dismiss('cancel');
    };

    /**
     * Kerää ja palauta setti koulutus-oideja jotka valittu + vanhat
     */
    $scope.model.save = function() {
        var koulutukset = [];
        angular.forEach($scope.model.selectedKoulutukses, function(koulutus) {
            koulutukset.push({oid: koulutus.oid, tarjoajaOid: koulutus.tarjoajaOid});
        });
        $modalInstance.close(koulutukset);
    };

});
