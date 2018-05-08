angular.module('Validator', [])
    .factory('ValidatorService', function(HakukohdeService, HakuService) {
        'use strict';

        function isValidUrl(url) {
            var pattern = new RegExp('^(https?:\\/\\/)?' + '((([a-z\\d]([a-z\\d-]*[a-z\\d])*)\\.)+[a-z]{2,}|'
            + '((\\d{1,3}\\.){3}\\d{1,3}))' + '(\\:\\d+)?(\\/[-a-z\\d%_.~+]*)*' + '(\\?[;&a-z\\d%_.~+=-]*)?'
            + '(\\#[-a-z\\d_]*)?$', 'i');
            return pattern.test(url);
        }
        function isValidEmail(email) {
            var pattern = new RegExp('[a-z0-9!#$%&\'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&\'*+/=?^_`{|}~-]+)*'
            + '@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?');
            return pattern.test(email);
        }
        function isValidLisatiedot(hakukohde, haku) {
            if (haku && !haku.jarjestelmanHakulomake && !haku.ataruLomakeAvain &&
                (!haku.hakulomakeUri || haku.hakulomakeUri.trim().length === 0)) {
                var empty = true;
                angular.forEach(hakukohde.lisatiedot, function(val) {
                    if (val && val.trim().length > 0) {
                        empty = false;
                        return;
                    }
                });
                if (empty) {
                    return false;
                }
            }
            return true;
        }
        function isValidAjankohta(ajankohta) {
            return notEmpty([
                ajankohta.alkaa,
                ajankohta.loppuu,
                ajankohta.osoite.osoiterivi1,
                ajankohta.osoite.postinumero
            ]);
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
        function isValidValintakokeet(valintakokeet) {
            for (var i in valintakokeet) {
                var valintakoe = valintakokeet[i];
                var nimiEmpty = !notEmpty(valintakoe.valintakoeNimi);
                var tyyppiEmpty = !notEmpty(valintakoe.valintakoetyyppi);
                var kuvausEmpty = !notEmpty(valintakoe.valintakokeenKuvaus.teksti);
                var ajankohtaEmpty = valintakoe.valintakoeAjankohtas.length === 0;
                if (nimiEmpty && tyyppiEmpty && kuvausEmpty && ajankohtaEmpty && valintakoe.isNew) {
                    continue;
                }
                if (nimiEmpty && tyyppiEmpty) {
                    return false;
                }
                if (kuvausEmpty) {
                    return false;
                }
                for (var j in valintakoe.valintakoeAjankohtas) {
                    if (!isValidAjankohta(valintakoe.valintakoeAjankohtas[j])) {
                        return false;
                    }
                }
            }
            return true;
        }

        function isValidToimitusOsoite(liite) {
            var isNotMuuOsoite = (liite.ensisijainenOsoiteTyyppi != 'MuuOsoite');
            var osoiteNotEmpty = liite.liitteenToimitusOsoite && notEmpty([
                liite.liitteenToimitusOsoite.osoiterivi1,
                liite.liitteenToimitusOsoite.postinumero
            ]);
            return (isNotMuuOsoite || osoiteNotEmpty);
        }

        function isValidSahkoinenOsoite(liite) {
            return (!liite.sahkoinenOsoiteEnabled && liite.ensisijainenOsoiteTyyppi != 'VainSahkoinenOsoite') || notEmpty(liite.sahkoinenToimitusOsoite);
        }

        function isValidLiitteet(liitteet, jatkuvaHaku) {
            var invalidLiite = _.find(liitteet, function(liiteWithLangs) {
                var commonFields = liiteWithLangs.commonFields;
                return _.find(liiteWithLangs, function(liite, lang) {
                    if (typeof(liite) !== 'object' || lang.indexOf('kieli_') === -1 || liite.isEmpty(commonFields)) {
                        return;
                    }
                    // empty ja empty tai (empty ja eijatkuva) tai eivalidi tai eivalidi
                    if (!notEmpty(liite.liitteenNimi)
                        && !notEmpty(commonFields.liitteenTyyppi)
                        || (!notEmpty(commonFields.toimitettavaMennessa) && !jatkuvaHaku)
                        || !isValidSahkoinenOsoite(liite)
                        || !isValidToimitusOsoite(liite)) {
                        return true;
                    }
                });
            });
            return invalidLiite === undefined;
        }
        function isValidPainotettavatOppiaineet(hakukohde) {
            for (var i in hakukohde.painotettavatOppiaineet) {
                var painokerroin = hakukohde.painotettavatOppiaineet[i].painokerroin;
                var oppiaine = hakukohde.painotettavatOppiaineet[i].oppiaineUri;
                if (!painokerroin || !oppiaine) {
                    return false;
                }
            }
            return true;
        }
        function needsOpintoOikeus(haku) {
            return HakuService.isSiirtohaku(haku);
        }
        function needsHakukelpoisuus(toteutusTyyppi, haku) {
            if (HakuService.isSiirtohaku(haku)) {
                return false;
            }

            return !_.contains([
                'PERUSOPETUKSEN_LISAOPETUS',
                'LUKIOKOULUTUS',
                'AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS',
                'MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS',
                'MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS',
                'VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS',
                'VAPAAN_SIVISTYSTYON_KOULUTUS',
                'AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA',
                'AMMATILLINEN_PERUSTUTKINTO',
                'AMMATILLINEN_PERUSTUTKINTO_ALK_2018',
                'KORKEAKOULUOPINTO',
                'AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA',
                'AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER',
                'PELASTUSALAN_KOULUTUS'
            ], toteutusTyyppi);
        }
        function needsLiitteidenToimitustiedot(toteutusTyyppi) {
            return _.contains([
                'AMMATILLINEN_PERUSTUTKINTO',
                'LUKIOKOULUTUS',
                'AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA'
            ], toteutusTyyppi);
        }
        function isValidNames(hakukohde) {
            for (var i in hakukohde.hakukohteenNimet) {
                if (hakukohde.hakukohteenNimet[i]) {
                    return true;
                }
            }
            return false;
        }
        function isValidHakukohdeToimitusOsoite(model) {
            return !model.liitteidenMuuOsoiteEnabled ||
                !_.isEmpty(model.hakukohde.liitteidenToimitusOsoite.osoiterivi1) &&
                !_.isEmpty(model.hakukohde.liitteidenToimitusOsoite.postinumero);
        }
        function isValidHakukohdeSahkoinenOsoite(model) {
            if (model.liitteidenSahkoinenOsoiteEnabled) {
                return isValidUrl(model.hakukohde.sahkoinenToimitusOsoite) ||
                    isValidEmail(model.hakukohde.sahkoinenToimitusOsoite);
            }
            else {
                return true;
            }
        }
        function isValidNameLengths(hakukohteenNimet) {
            var retval = true;
            angular.forEach(hakukohteenNimet, function(hakukohdeNimi) {
                if (hakukohdeNimi.length > 225) {
                    retval = false;
                }
            });
            return retval;
        }
        function validateAiku(hakukohde, haku) {
            var errors = [];
            if (!hakukohde.hakukohteenNimiUri || hakukohde.hakukohteenNimiUri.trim().length < 1) {
                errors.push({
                    errorMessageKey: 'hakukohde.edit.nimi.missing'
                });
            }
            if (!hakukohde.hakuOid || hakukohde.hakuOid.trim().length < 1) {
                errors.push({
                    errorMessageKey: 'hakukohde.edit.haku.missing'
                });
            }
            if (!isValidValintakokeet(hakukohde.valintakokeet)) {
                errors.push({
                    errorMessageKey: 'hakukohde.edit.valintakokeet.errors'
                });
            }
            if (!isValidLiitteet(hakukohde.hakukohteenLiitteet, (haku.hakutapaUri !== undefined && haku.hakutapaUri.split('#')[0] !== 'hakutapa_01'))) {
                errors.push({
                    errorMessageKey: 'hakukohde.edit.liitteet.errors'
                });
            }
            if (!isValidLisatiedot(hakukohde, haku)) {
                errors.push({
                    errorMessageKey: 'hakukohde.edit.lisatietoja-hakemisesta.required'
                });
            }
            return errors;
        }
        function validateHakukohde(model, haku, originalHakukohde) {
            var hakukohde = model.hakukohde;
            var errors = [];
            if (needsOpintoOikeus(haku)) {
                if (!hakukohde.opintoOikeusUris || hakukohde.opintoOikeusUris.length < 1) {
                    errors.push({
                        errorMessageKey: 'tarjonta.hakukohde.opintoOikeus.missing'
                    });
                }
            }
            if (needsHakukelpoisuus(hakukohde.toteutusTyyppi, haku)) {
                if (hakukohde.hakukelpoisuusvaatimusUris === undefined
                    || hakukohde.hakukelpoisuusvaatimusUris.length < 1) {
                    errors.push({
                        errorMessageKey: 'tarjonta.hakukohde.hakukelpoisuusvaatimus.missing'
                    });
                }
            }
            if (hakukohde.hakuOid === undefined || hakukohde.hakuOid.length < 1) {
                errors.push({
                    errorMessageKey: 'hakukohde.edit.haku.missing'
                });
            }
            if (needsLiitteidenToimitustiedot(hakukohde.toteutusTyyppi)) {
                if (!isValidHakukohdeToimitusOsoite(model)) {
                    errors.push({
                        errorMessageKey: 'hakukohde.edit.liitteet.toimitusosoite.errors'
                    });
                }
                if (!isValidHakukohdeSahkoinenOsoite(model)) {
                    errors.push({
                        errorMessageKey: 'hakukohde.edit.liitteet.sahkoinenosoite.errors'
                    });
                }
            }
            if (!HakukohdeService.config.isToisenAsteenKoulutus(hakukohde.toteutusTyyppi)) {
                if (!isValidNames(hakukohde)) {
                    errors.push({
                        errorMessageKey: 'hakukohde.edit.nimi.missing'
                    });
                }
                if (!isValidNameLengths(hakukohde.hakukohteenNimet)) {
                    errors.push({
                        errorMessageKey: 'hakukohde.edit.nimi.too.long'
                    });
                }
            }
            if (hakukohde.toteutusTyyppi === 'LUKIOKOULUTUS') {
                if (!isValidPainotettavatOppiaineet(hakukohde)) {
                    errors.push({
                        errorMessageKey: 'tarjonta.hakukohde.edit.painotettavatOppiaineet.errors'
                    });
                }
            }
            else if (!isValidValintakokeet(hakukohde.valintakokeet)) {
                errors.push({
                    errorMessageKey: 'hakukohde.edit.valintakokeet.errors'
                });
            }
            if (!isValidLiitteet(hakukohde.hakukohteenLiitteet, (haku != null && haku.hakutapaUri !== undefined && haku.hakutapaUri.split('#')[0] !== 'hakutapa_01'))) {
                errors.push({
                    errorMessageKey: 'hakukohde.edit.liitteet.errors'
                });
            }
            angular.forEach(hakukohde.aloituspaikatKuvaukset, function(arvo) {
                if (arvo.length > 20) {
                    errors.push({
                        errorMessageKey: 'hakukohde.edit.aloituspaikatKuvaukset.too.long'
                    });
                }
            });
            if (hakukohde.hakuaikaAlkuPvm || hakukohde.hakuaikaLoppuPvm) {
                var alku = hakukohde.hakuaikaAlkuPvm;
                var loppu = hakukohde.hakuaikaLoppuPvm;
                var error = !(alku && loppu);

                // Alla oleva tarkistus voisi varmaan olla kaikille toteutustyypeille?
                if (model.hakukohde.toteutusTyyppi === 'KORKEAKOULUOPINTO') {
                    var hakuaika = _.findWhere(model.hakuaikas, {hakuaikaId:hakukohde.hakuaikaId});
                    if (hakuaika && hakuaika.alkuPvm && hakuaika.alkuPvm > alku) {
                        error = true;
                    }
                    if (hakuaika && hakuaika.loppuPvm && hakuaika.loppuPvm < loppu) {
                        error = true;
                    }
                }

                if (error) {
                    errors.push({
                        errorMessageKey: 'hakukohde.edit.hakuaika.errors'
                    });
                }
            }
            return errors;
        }
        function validate(model, haku) {
            var hakukohde = model.hakukohde;
            if (_.contains(['LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA',
                            'EB_RP_ISH',
                            'AIKUISTEN_PERUSOPETUS',
                            'AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA',
                            'AMMATILLINEN_PERUSKOULUTUS',
                            'ERIKOISAMMATTITUTKINTO',
                            'AMMATTITUTKINTO'], hakukohde.toteutusTyyppi)) {
                return validateAiku(hakukohde, haku);
            } else {
                return validateHakukohde(model, haku);
            }
        }
        return {
            hakukohde: {
                validate: function(model, haku) {
                    return validate(model, haku);
                },
                valintakoe: {
                    isValidAjankohta: isValidAjankohta
                },
                isValidSahkoinenOsoite: isValidSahkoinenOsoite
            }
        };
    });