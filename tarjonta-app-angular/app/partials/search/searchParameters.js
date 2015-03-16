angular.module('search.hakutulokset.searchparameters', [])
    .factory('SearchParameters', function(Koodisto, AuthService, Config, LocalisationService, $routeParams) {
        'use strict';

        var spec = {
            attributes: {
                terms: fromParams('terms', ''),
                state: fromParams('state', '*'),
                year: fromParams('year', '*'),
                season: fromParams('season', '*'),
                type: fromParams('type', '*'),
                hakutapa: fromParams('hakutapa', '*'),
                koulutustyyppi: fromParams('koulutustyyppi', '*'),
                hakutyyppi: fromParams('hakutyyppi', '*'),
                koulutuslaji: fromParams('koulutuslaji', '*'),
                kieli: fromParams('kieli', []),
                kohdejoukko: fromParams('kohdejoukko', '*'),
                oppilaitostyyppi: fromParams('oppilaitostyyppi', '*'),
                kunta: fromParams('kunta', '*'),
                hakukohderyhma: fromParams('hakukohderyhma', '*')
            },
            reset: function() {
                this.attributes.terms = '';
                this.attributes.state = '*';
                this.attributes.year = '*';
                this.attributes.season = '*';
                this.attributes.koulutustyyppi = '*';
                this.attributes.hakutapa = '*';
                this.attributes.type = '*';
                this.attributes.hakutyyppi = '*';
                this.attributes.koulutuslaji = '*';
                this.attributes.kieli = [];
                this.attributes.kohdejoukko = '*';
                this.attributes.oppilaitostyyppi = '*';
                this.attributes.kunta = '*';
                this.attributes.hakukohderyhma = '*';
            },
            addLanguage: function(language) {
                var found = _.find(this.attributes.kieli, function(element) {
                    return language.key === element.key;
                });
                if (found === undefined) {
                    this.attributes.kieli.push(language);
                }
            },
            removeLanguage: function(key) {
                var list = [];
                _.each(this.attributes.kieli, function(language) {
                    if (language.key != key) {
                        list.push(language);
                    }
                });
                this.attributes.kieli = list;
            },
            getSpecForSearchQuery: function(selectedOrgOid) {
                function languagesAsList(kielet) {
                    var list = [];
                    _.each(kielet, function(lang) {
                        list.push(lang.key);
                    });
                    return list;
                }
                function koulutusmoduuliTyypitAsList(tyyppiIndex) {
                    return Config.app['tarjonta.koulutusmoduuliTyypit'][tyyppiIndex];
                }
                return {
                    oid: selectedOrgOid,
                    terms: this.attributes.terms,
                    state: this.attributes.state == '*' ? null : this.attributes.state,
                    year: this.attributes.year == '*' ? null : this.attributes.year,
                    season: this.attributes.season == '*' ? null : this.attributes.season,
                    defaultTarjoaja: selectedOrgOid,
                    hakutapa: this.attributes.hakutapa == '*' ? null : this.attributes.hakutapa,
                    hakutyyppi: this.attributes.hakutyyppi == '*' ? null : this.attributes.hakutyyppi,
                    koulutustyyppi: this.attributes.koulutustyyppi == '*' ? null : this.attributes.koulutustyyppi,
                    type: koulutusmoduuliTyypitAsList(this.attributes.type),
                    koulutuslaji: this.attributes.koulutuslaji == '*' ? null : this.attributes.koulutuslaji,
                    kieli: languagesAsList(this.attributes.kieli),
                    kohdejoukko: this.attributes.kohdejoukko == '*' ? null : this.attributes.kohdejoukko,
                    oppilaitostyyppi: this.attributes.oppilaitostyyppi == '*' ? null : this.attributes.oppilaitostyyppi,
                    kunta: this.attributes.kunta == '*' ? null : this.attributes.kunta,
                    hakukohderyhma: this.attributes.hakukohderyhma == '*' ? null : this.attributes.hakukohderyhma
                };
            },
            filtersActive: function() {
                return this.attributes.state !== '*' ||
                this.attributes.year !== '*' ||
                this.attributes.season !== '*' ||
                this.attributes.koulutustyyppi !== '*' ||
                this.attributes.hakutapa !== '*' ||
                this.attributes.hakutyyppi !== '*' ||
                this.attributes.koulutuslaji !== '*' ||
                this.attributes.kieli.length > 0 ||
                this.attributes.kohdejoukko !== '*' ||
                this.attributes.oppilaitostyyppi !== '*' ||
                this.attributes.kunta !== '*' ||
                this.attributes.hakukohderyhma !== '*';
            }
        };
        function getDefaultHakuehdot() {
            return {
                'searchStr': '',
                'organisaatiotyyppi': '',
                'oppilaitostyyppi': undefined,
                'lakkautetut': false,
                'suunnitellut': false,
                'skipparents': true
            };
        }
        function fromParams(key, def) {
            return $routeParams[key] !== undefined ? $routeParams[key] : def;
        }

        function getSpec() {
            return spec;
        }
        function getYears() {
            var years = [];
            var lyr = new Date().getFullYear() + 10;
            for (var y = 2012; y < lyr; y++) {
                years.push({key: y, label: y});
            }
            return years;
        }
        function getStates() {
            var states = [];
            for (var s in CONFIG.env['tarjonta.tila']) {
                if (s !== 'POISTETTU') {
                    states.push({key: s, label: LocalisationService.t('tarjonta.tila.' + s)});
                }
            }
            return states;
        }
        function fetchCodeElementsToObject(koodisto) {
            var options = [];
            Koodisto.getAllKoodisWithKoodiUri(koodisto, AuthService.getLanguage()).then(function(koodit) {
                for (var i in koodit) {
                    var k = koodit[i];
                    options.push({key: k.koodiUri, label: k.koodiNimi});
                }
            });
            return options;
        }
        function fetchCodeElementsToList(koodisto) {
            var list = [];
            Koodisto.getAllKoodisWithKoodiUri(koodisto, AuthService.getLanguage()).then(function(koodit) {
                for (var i in koodit) {
                    var k = koodit[i];
                    var o = {};
                    o.key = k.koodiUri;
                    o.value = k.koodiNimi;
                    list.push(o);
                }
            });
            return list;
        }
        function getOppilaitostyypit() {
            var oppilaitostyypit = [];
            Koodisto.getAllKoodisWithKoodiUri('oppilaitostyyppi', AuthService.getLanguage())
                .then(function(koodit) {
                    angular.forEach(koodit, function(koodi) {
                        koodi.koodiUriWithVersion = koodi.koodiUri + '#' + koodi.koodiVersio;
                        oppilaitostyypit.push(koodi);
                    });
                });
            return oppilaitostyypit;
        }
        function getOrganisaatiotyypit() {
            return [
                {
                    nimi: LocalisationService.t('organisaatiotyyppi.koulutustoimija'),
                    koodi: 'Koulutustoimija'
                },
                {
                    nimi: LocalisationService.t('organisaatiotyyppi.oppilaitos'),
                    koodi: 'Oppilaitos'
                },
                {
                    nimi: LocalisationService.t('organisaatiotyyppi.toimipiste'),
                    koodi: 'Toimipiste'
                },
                {
                    nimi: LocalisationService.t('organisaatiotyyppi.oppisopimustoimipiste'),
                    koodi: 'Oppisopimustoimipiste'
                }
            ];
        }
        function getTypes() {
            var types = [];
            _.each(Config.app['tarjonta.koulutusmoduuliTyypit'], function(typeContainer, index) {
                var first = _.first(typeContainer);
                types.push({label: LocalisationService.t('tarjonta.tyyppi.' + first), key: index});
            });
            return types;
        }
        return {
            getDefaultHakuehdot: getDefaultHakuehdot,
            fetchCodeElementsToObject: fetchCodeElementsToObject,
            fetchCodeElementsToList: fetchCodeElementsToList,
            getOppilaitostyypit: getOppilaitostyypit,
            getOrganisaatiotyypit: getOrganisaatiotyypit,
            getSpec: getSpec,
            getStates: getStates,
            getYears: getYears,
            getTypes: getTypes
        };
    });
