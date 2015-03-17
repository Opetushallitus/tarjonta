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
    'config',
    'Logging'
]);
//TODO: after refactoring the rest to v1 change this
app.factory('Hakukohde', function($resource, Config) {
    var hakukohdeUri = Config.env.tarjontaRestUrlPrefix + 'hakukohde/:oid';
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
                url: Config.env.tarjontaRestUrlPrefix + 'hakukohde/:oid/stateChangeCheck',
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
app.factory('Liite', function($resource, Config) {
    var hakukohdeLiiteUri = Config.env.tarjontaRestUrlPrefix + 'hakukohde/:hakukohdeOid/liite/:liiteId';
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
app.factory('Valintakoe', function($resource, $log, $q, Config) {
    var hakukohdeValintakoeUri = Config.env.tarjontaRestUrlPrefix + 'hakukohde/:hakukohdeOid/valintakoe/:valintakoeOid';
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
app.factory('HakukohdeKoulutukses', function($http, Config, $q) {
    return {
        removeKoulutuksesFromHakukohde: function(hakukohdeOid, koulutusOids) {
            if (hakukohdeOid !== undefined && koulutusOids !== undefined) {
                var hakukohdeKoulutusUri = Config.env.tarjontaRestUrlPrefix + 'hakukohde/' +
                    hakukohdeOid + '/koulutukset';
                $http.post(hakukohdeKoulutusUri, koulutusOids, {
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
                var hakukohdeKoulutusUri = Config.env.tarjontaRestUrlPrefix + 'hakukohde/' + hakukohdeOid +
                    '/koulutukset/lisaa';
                $http.post(hakukohdeKoulutusUri, koulutusOids, {
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
            var serviceUrl = Config.env.tarjontaRestUrlPrefix + 'hakukohde/komotoSelectedCheck';
            $http({
                method: 'GET',
                url: serviceUrl,
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
app.factory('HakukohdeService', function($resource, Config, $http, $rootScope, KoulutusConverterFactory,
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
    * Lisää hakukohteeseen liitteen jos siinä ei ole vielä yhtään.
    */
    function addLiiteIfEmpty(hakukohde) {
        if (!hakukohde.hakukohteenLiitteet) {
            hakukohde.hakukohteenLiitteet = [];
        }
        if (hakukohde.hakukohteenLiitteet.length === 0) {
            var kieli = hakukohde.opetusKielet[0] || 'kieli_fi';
            addLiite(hakukohde, kieli, {});
        }
    }
    /**
    * Lisää hakukohteeseen liitteen (opetuskieli[0]).
    */
    function addLiite(hakukohde, kieliUri, liitteidenToimitusosoite) {
        var liite = newLiite(hakukohde, kieliUri, liitteidenToimitusosoite);
        hakukohde.hakukohteenLiitteet.push(liite);
        $rootScope.$broadcast('liiteAdded', liite);
    }
    /**
    * Luo liite objektin
    */
    function newLiite(hakukohde, kieliUri, liitteidenToimitusosoite) {
        var kuvaukset = {};
        kuvaukset[kieliUri] = '';
        var addr = liitteidenToimitusosoite;
        return {
            hakukohdeOid: hakukohde.oid,
            kieliUri: kieliUri,
            liitteenNimi: '',
            liitteenKuvaukset: kuvaukset,
            toimitettavaMennessa: null,
            //tmennessa,
            liitteenToimitusOsoite: addr ? angular.copy(addr) : {},
            muuOsoiteEnabled: !addr,
            sahkoinenOsoiteEnabled: false,
            kaytetaanHakulomakkeella: hakukohde.toteutusTyyppi === 'KORKEAKOULUTUS' ? false : true,
            isNew: true
        };
    }
    function removeEmptyLiites(liitteetArray) {
        var loopIndex = liitteetArray.length;
        while (loopIndex--) {
            var liite = liitteetArray[loopIndex];
            if (liite.isNew && !liite.liitteenNimi && !liite.liitteenTyyppi && !liite.toimitettavaMennessa) {
                liitteetArray.splice(loopIndex, 1);
            }
        }
    }
    function removeNotUsedYhteystiedot(yhteystiedotArray) {
        var loopIndex = yhteystiedotArray.length;
        while (loopIndex--) {
            var yhteystieto = yhteystiedotArray[loopIndex];
            if (!yhteystieto.kaytossa) {
                yhteystiedotArray.splice(loopIndex, 1);
            }
        }
    }

    var hakukohdeConfig = {
        setToteutustyyppi: function(toteutustyyppi) {
            this.toteutustyyppi = toteutustyyppi;
        },
        isToisenAsteenKoulutus: function(toteutustyyppi) {
            return _.contains([
                'AMMATILLINEN_PERUSTUTKINTO',
                'LUKIOKOULUTUS',
                'PERUSOPETUKSEN_LISAOPETUS',
                'AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS',
                'MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS',
                'MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS',
                'VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS',
                'VAPAAN_SIVISTYSTYON_KOULUTUS',
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
                'VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS',
                'PERUSOPETUKSEN_LISAOPETUS',
                'MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS',
                'VAPAAN_SIVISTYSTYON_KOULUTUS',
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
        }
    };

    return {
        addValintakoeIfEmpty: addValintakoeIfEmpty,
        addValintakoe: addValintakoe,
        addLiiteIfEmpty: addLiiteIfEmpty,
        addLiite: addLiite,
        removeEmptyLiites: removeEmptyLiites,
        removeNotUsedYhteystiedot: removeNotUsedYhteystiedot,
        addPainotettavaOppiaine: addPainotettavaOppiaine,
        findHakukohdesByKuvausId: function(kuvausId) {
            return $http.get(Config.env.tarjontaRestUrlPrefix + 'hakukohde/findHakukohdesByKuvausId/' + kuvausId);
        },
        config: hakukohdeConfig
    };
});