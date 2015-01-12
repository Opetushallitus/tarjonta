angular.module('app.haku.list.ctrl.searchparameters', [])
    .factory('HakuSearchParameters', function(Koodisto, AuthService, Config, LocalisationService) {
        'use strict';

        var spec = {
            attributes: {
                HAKUSANA: undefined,
                TILA: undefined,
                KAUSI: undefined,
                VUOSI: undefined,
                KAUSIVUOSI: 'HAKU',
                HAKUTAPA: undefined,
                HAKUTYYPPI: undefined,
                KOHDEJOUKKO: undefined,
                TARJOAJAOID: undefined
            },
            reset: function() {
                this.attributes.HAKUSANA = undefined;
                this.attributes.TILA = undefined;
                this.attributes.KAUSI = undefined;
                this.attributes.VUOSI = undefined;
                this.attributes.KAUSIVUOSI = 'HAKU';
                this.attributes.HAKUTAPA = undefined;
                this.attributes.HAKUTYYPPI = undefined;
                this.attributes.KOHDEJOUKKO = undefined;
            }
        };
        function getSpec() {
            return spec;
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
        return {
            getSpec: getSpec,
            fetchCodeElementsToObject: fetchCodeElementsToObject,
            getYears: getYears,
            getStates: getStates
        };
    });
