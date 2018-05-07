/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 */
var app = angular.module('Hakukohde', [
    'ngResource',
    'Logging'
]);
//TODO: after refactoring the rest to v1 change this
app.factory('Hakukohde', function($resource) {
    var plainUrls = window.urls().noEncode();
    var hakukohdeUri = plainUrls.url("tarjonta-service.hakukohde.byOid", ":oid");
    return $resource(hakukohdeUri, {
        oid: '@oid'
    }, {
            update: {
                method: 'PUT',
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            },
            remove: {
                method: 'DELETE',
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            },
            save: {
                method: 'POST',
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            },
            checkStateChange: {
                url: plainUrls.url("tarjonta-service.hakukohde.checkStateChange", ":oid"),
                method: 'GET',
                withCredentials: true
            },
            get: {
                method: 'GET',
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            }
        });
});
app.factory('Liite', function($resource) {
    var plainUrls = window.urls().noEncode();
    var hakukohdeLiiteUri = plainUrls.url("tarjonta-service.hakukohde.liite", ":hakukohdeOid", ":liiteId");
    return $resource(hakukohdeLiiteUri, {
        hakukohdeOid: '@hakukohdeOid',
        liiteId: '@liiteId'
    }, {
            update: {
                method: 'PUT',
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            },
            save: {
                method: 'POST',
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            },
            getAll: {
                method: 'GET',
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            },
            remove: {
                method: 'DELETE',
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            }
        });
});
app.factory('Valintakoe', function($resource, $log, $q) {
    var plainUrls = window.urls().noEncode();
    var hakukohdeValintakoeUri = plainUrls.url("tarjonta-service.hakukohde.valintakoe", ":hakukohdeOid", ":valintakoeOid");
    return $resource(hakukohdeValintakoeUri, {
        hakukohdeOid: '@hakukohdeOid',
        valintakoeOid: '@valintakoeOid'
    }, {
            update: {
                method: 'PUT',
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            },
            save: {
                method: 'POST',
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            },
            getAll: {
                method: 'GET',
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            },
            remove: {
                method: 'DELETE',
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            }
        });
});
app.factory('HakukohdeKoulutukses', function($http, $q) {
    return {
        removeKoulutuksesFromHakukohde: function(hakukohdeOid, koulutusOids) {
            if (hakukohdeOid !== undefined && koulutusOids !== undefined) {
                $http.post(window.url("tarjonta-service.hakukohde.removeKoulutuksesFromHakukohde", hakukohdeOid), koulutusOids, {
                    withCredentials: true,
                    headers: {
                        'Content-Type': 'application/json; charset=UTF-8'
                    }
                }).success(function(data) {
                    return data;
                }).error(function(data) {
                    return false;
                });
            }
        },
        addKoulutuksesToHakukohde: function(hakukohdeOid, koulutusOids) {
            var promise = $q.defer();
            if (hakukohdeOid !== undefined && koulutusOids !== undefined) {
                $http.post(window.url("tarjonta-service.hakukohde.addKoulutuksesToHakukohde", hakukohdeOid), koulutusOids, {
                    withCredentials: true,
                    headers: {
                        'Content-Type': 'application/json; charset=UTF-8'
                    }
                }).success(function(data) {
                    promise.resolve('OK' === data.status);
                }).error(function(data) {
                    promise.resolve(false);
                });
            }
            return promise.promise;
        },
        geValidateHakukohdeKomotos: function(komotoIds) {
            var promise = $q.defer();
            $http({
                method: 'GET',
                url: window.url("tarjonta-service.hakukohde.validateHakukohdeKomotos"),
                params: {
                    oid: komotoIds
                }
            }).success(function(data) {
                promise.resolve(data);
            }).error(function(data) {
                promise.resolve(data);
            });
            return promise.promise;
        }
    };
});
app.factory('HakukohdeService', function($resource, $http, $rootScope, KoulutusConverterFactory,
                                         Koodisto, $q) {
    function addValintakoeIfEmpty(hakukohde) {
        if (hakukohde.valintakokeet.length === 0) {
            var kieli = hakukohde.opetusKielet[0];
            $scope.addValintakoe(kieli);
        }
    }
    function addPainotettavaOppiaine(hakukohde) {
        var po = {
            oppiaineUri: '',
            painokerroin: ''
        };
        hakukohde.painotettavatOppiaineet.push(po);
    }
    /**
    * Lisää hakukohteeseen valintakokeen
    */
    function addValintakoe(hakukohde, kieliUri) {
        var vk = {
            hakukohdeOid: hakukohde.oid,
            kieliUri: kieliUri,
            valintakoeNimi: undefined,
            valintakokeenKuvaus: {
                uri: kieliUri,
                teksti: undefined
            },
            valintakoeAjankohtas: [],
            isNew: true
        };
        hakukohde.valintakokeet.push(vk);
        return vk;
    }
    /**
    * Lisää hakukohteeseen liitteen (opetuskieli[0]).
    */
    function addLiite(hakukohde, kielet, liitteidenToimitusosoitteet, hakutoimistonNimi, liiteWithLangs) {
        var liite = liiteWithLangs || {
            isNew: true
        };
        _.defaults(liite, {
            commonFields: {}
        });

        _.each(kielet, function(kieli) {
            liite[kieli.koodiUri] = liite[kieli.koodiUri] || newLiite(hakukohde, kieli.koodiUri,
                liitteidenToimitusosoitteet[kieli.koodiUri], hakutoimistonNimi[kieli.koodiUri]);

            liite[kieli.koodiUri].isEmpty = function(commonFields) {
                commonFields = commonFields || {};
                var liite = this;
                var isEmpty = !commonFields.liitteenTyyppi && !liite.liitteenNimi &&
                    _.find(liite.liitteenKuvaukset, function(kuvaus) {
                        return !_.isEmpty(kuvaus);
                    }) === undefined;
                return isEmpty;
            };

            $rootScope.$broadcast('liiteAdded', liite[kieli.koodiUri]);
        });

        if (liiteWithLangs === undefined) {
            hakukohde.hakukohteenLiitteet.push(liite);
        }
    }
    /**
    * Luo liite objektin
    */
    function newLiite(hakukohde, kieliUri, liitteidenToimitusosoite, hakutoimistonNimi) {
        var kuvaukset = {
            kieliUri: ''
        };
        var addr = liitteidenToimitusosoite;
        var muuOsoiteEnabled = function() {
            return this.ensisijainenOsoiteTyyppi == 'MuuOsoite';
        };
        return {
            hakukohdeOid: hakukohde.oid,
            kieliUri: kieliUri,
            liitteenNimi: '',
            liitteenKuvaukset: kuvaukset,
            toimitettavaMennessa: null,
            //tmennessa,
            liitteenVastaanottaja: hakutoimistonNimi,
            liitteenToimitusOsoite: addr ? angular.copy(addr) : {},
            muuOsoiteEnabled: muuOsoiteEnabled,
            sahkoinenOsoiteEnabled: false,
            ensisijainenOsoiteTyyppi: addr ? 'OrganisaationOsoite' : 'MuuOsoite',
            kaytetaanHakulomakkeella: hakukohde.toteutusTyyppi === 'KORKEAKOULUTUS' ? false : true,
            isNew: true
        };
    }

    var hakukohdeConfig = {
        setToteutustyyppi: function(toteutustyyppi) {
            this.toteutustyyppi = toteutustyyppi;
        },
        isToisenAsteenKoulutus: function(toteutustyyppi) {
            return _.contains([
                'AMMATILLINEN_PERUSTUTKINTO',
                'AMMATILLINEN_PERUSTUTKINTO_ALK_2018',
                'LUKIOKOULUTUS',
                'PERUSOPETUKSEN_LISAOPETUS',
                'AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS',
                'MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS',
                'MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS',
                'VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS',
                'VAPAAN_SIVISTYSTYON_KOULUTUS',
                'PELASTUSALAN_KOULUTUS',
                'AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA',
                'AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA',
                'AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER'
            ], toteutustyyppi || this.toteutustyyppi);
        },
        hideSoraKuvaus: function(toteutustyyppi) {
            return _.contains([
                'AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA',
                'AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER',
                'LUKIOKOULUTUS',
                'LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA',
                'EB_RP_ISH',
                'VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS',
                'PERUSOPETUKSEN_LISAOPETUS',
                'MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS',
                'VAPAAN_SIVISTYSTYON_KOULUTUS',
                'PELASTUSALAN_KOULUTUS',
                'AIKUISTEN_PERUSOPETUS'
            ], toteutustyyppi || this.toteutustyyppi);
        },
        needsValinnoissaKaytettavatAloituspaikat: function(toteutustyyppi) {
            return _.contains([
                'AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA',
                'AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER',
                'AMMATILLINEN_PERUSTUTKINTO',
                'LUKIOKOULUTUS',
                'PERUSOPETUKSEN_LISAOPETUS',
                'AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS',
                'MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS',
                'MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS',
                'VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS',
                'AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA'
            ], toteutustyyppi || this.toteutustyyppi);
        },
        getOptionsFromKoodisto: function(toteutustyyppi, koodisto, lang) {
            var koulutustyyppiUri = KoulutusConverterFactory.STRUCTURE[toteutustyyppi].koulutustyyppiKoodiUri;
            var deferred = $q.defer();
            Koodisto.getAlapuolisetKoodiUrit([koulutustyyppiUri], koodisto, lang)
                .then(function(koodis) {
                    var options = _.map(koodis.map, function(koodi) {
                        return {
                            nimi: koodi.koodiNimi,
                            uri: koodi.koodiUri + '#' + koodi.koodiVersio
                        };
                    });
                    deferred.resolve(options);
                });
            return deferred.promise;
        },
        getHaunKohdejoukko: function(toteutustyyppi) {
            toteutustyyppi = toteutustyyppi || this.toteutustyyppi;
            var koulutustyyppiUri = KoulutusConverterFactory.STRUCTURE[toteutustyyppi].koulutustyyppiKoodiUri;
            return Koodisto.getYlapuolisetKoodiUrit([koulutustyyppiUri], 'haunkohdejoukko', 'fi');
        }
    };

    return {
        addValintakoeIfEmpty: addValintakoeIfEmpty,
        addValintakoe: addValintakoe,
        addLiite: addLiite,
        addPainotettavaOppiaine: addPainotettavaOppiaine,
        findHakukohdesByKuvausId: function(kuvausId) {
            return $http.get(window.url("tarjonta-service.hakukohde.byKuvausId"), kuvausId);
        },
        config: hakukohdeConfig
    };
});