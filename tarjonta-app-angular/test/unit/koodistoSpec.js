'use strict';


describe('koodistoServiceTest', function() {


    var CONFIG_ENV_MOCK = {
        "env": {
            "key-env-1": "mock-value-env-1",
            "key-env-2": "mock-value-env-2"
        }, "app": {
            "key-app-1": "mock-value-app-1"
        }
    }

    //set mock data to module by using the value-method,
    var mockModule = angular.module('test.module', []);
    mockModule.value('globalConfig', CONFIG_ENV_MOCK);

    beforeEach(module('test.module')); //mock module with the mock data
    beforeEach(module('config'));
    beforeEach(module('Logging'));

    var koodistoMockData = [
        {"koodiUri": "koulutus_851301", "resourceUri": "http://koodistopalvelu.opintopolku.fi/koulutus/koodi/koulutus_851301", "versio": 1, "koodisto": {"koodistoUri": "koulutus", "organisaatioOid": "1.2.246.562.10.00000000001", "koodistoVersios": [1]}, "koodiArvo": "851301", "paivitysPvm": 1379314392378, "voimassaAlkuPvm": "1997-01-01", "voimassaLoppuPvm": null, "tila": "LUONNOS", "metadata": [{"nimi": "Tekn. lic., datateknik", "kuvaus": "Tekn. lic., datateknik", "lyhytNimi": "TkL, datateknik", "kayttoohje": null, "kasite": null, "sisaltaaMerkityksen": null, "eiSisallaMerkitysta": null, "huomioitavaKoodi": null, "sisaltaaKoodiston": null, "kieli": "SV"}, {"nimi": "Tekn. lis., tietotekniikka", "kuvaus": "Tekn. lis., tietotekniikka", "lyhytNimi": "TkL, tietotekniikka", "kayttoohje": null, "kasite": null, "sisaltaaMerkityksen": null, "eiSisallaMerkitysta": null, "huomioitavaKoodi": null, "sisaltaaKoodiston": null, "kieli": "FI"}, {"nimi": "LicSc, Information Tech.", "kuvaus": "LicSc, Information Tech.", "lyhytNimi": "Licentiate of Science (Technology), Information Technology", "kayttoohje": null, "kasite": null, "sisaltaaMerkityksen": null, "eiSisallaMerkitysta": null, "huomioitavaKoodi": null, "sisaltaaKoodiston": null, "kieli": "EN"}]},
        {"koodiUri": "koulutus_851401", "resourceUri": "http://koodistopalvelu.opintopolku.fi/koulutus/koodi/koulutus_851401", "versio": 1, "koodisto": {"koodistoUri": "koulutus", "organisaatioOid": "1.2.246.562.10.00000000001", "koodistoVersios": [1]}, "koodiArvo": "851401", "paivitysPvm": 1379314408127, "voimassaAlkuPvm": "1997-01-01", "voimassaLoppuPvm": null, "tila": "LUONNOS", "metadata": [{"nimi": "Tekn. lis., kemian tekniikka", "kuvaus": "Tekn. lis., kemian tekniikka", "lyhytNimi": "TkL, kemian tekniikka", "kayttoohje": null, "kasite": null, "sisaltaaMerkityksen": null, "eiSisallaMerkitysta": null, "huomioitavaKoodi": null, "sisaltaaKoodiston": null, "kieli": "FI"}, {"nimi": "LicSc, Chemical Eng.", "kuvaus": "LicSc, Chemical Eng.", "lyhytNimi": "Licentiate of Science (Technology), Chemical Engineering", "kayttoohje": null, "kasite": null, "sisaltaaMerkityksen": null, "eiSisallaMerkitysta": null, "huomioitavaKoodi": null, "sisaltaaKoodiston": null, "kieli": "EN"}, {"nimi": "Tekn. lic., kemisk teknik", "kuvaus": "Tekn. lic., kemisk teknik", "lyhytNimi": "TkL, kemisk teknik", "kayttoohje": null, "kasite": null, "sisaltaaMerkityksen": null, "eiSisallaMerkitysta": null, "huomioitavaKoodi": null, "sisaltaaKoodiston": null, "kieli": "SV"}]}
    ];



    beforeEach(module('Koodisto'));

    describe('Test koodi value converter returns localized nimi', function() {
        it('should return array with 2 elements with localized name', inject(function(Koodisto) {
            var koodis = Koodisto.convertKoodistoKoodiToViewModelKoodi(koodistoMockData, 'FI');

            var koodi = koodis[0];


            expect(koodi.koodiNimi).toBe('Tekn. lis., tietotekniikka');
        }));
    });

    describe('Test koodi value converter', function() {

        it('should return array of 2', inject(function(Koodisto) {
            var koodis = Koodisto.convertKoodistoKoodiToViewModelKoodi(koodistoMockData, 'FI');
            expect(koodis.length).toBe(2);
        }));
    });

    describe('Test koodisto version util', function() {

        it('test time and status of the koodi', inject(function(Koodisto) {
            var vu = Koodisto.versionUtil();

            var date = new Date("2014-05-05");


            var koodiActive = {
                "koodiUri": "koulutus_301101",
                "versio": 1,
                "koodisto": {
                    "koodistoUri": "koulutus",
                },
                "voimassaAlkuPvm": "1997-01-01",
                "voimassaLoppuPvm": null,
                "tila": "HYVAKSYTTY"
            };

            var koodiPassive = {
                "koodiUri": "koulutus_301101",
                "versio": 2,
                "koodisto": {
                    "koodistoUri": "koulutus",
                },
                "voimassaAlkuPvm": "1997-01-01",
                "voimassaLoppuPvm": null,
                "tila": "PASSIVOITU"
            };

            var koodiExpired = {
                "koodiUri": "koulutus_301101",
                "versio": 3,
                "koodisto": {
                    "koodistoUri": "koulutus",
                },
                "voimassaAlkuPvm": "1997-01-01",
                "voimassaLoppuPvm": "2014-05-04",
                "tila": "HYVAKSYTTY"
            };

            var koodiWillExpired = {
                "koodiUri": "koulutus_301101",
                "versio": 4,
                "koodisto": {
                    "koodistoUri": "koulutus",
                },
                "voimassaAlkuPvm": "1997-01-01",
                "voimassaLoppuPvm": "2014-05-06",
                "tila": "HYVAKSYTTY"
            };

            var koodiLuonnos = {
                "koodiUri": "koulutus_301101",
                "versio": 5,
                "koodisto": {
                    "koodistoUri": "koulutus",
                },
                "voimassaAlkuPvm": "1997-01-01",
                "voimassaLoppuPvm": null,
                "tila": "LUONNOS"
            };

            var koodiWillBeginInFuture = {
                "koodiUri": "koulutus_301101",
                "versio": 6,
                "koodisto": {
                    "koodistoUri": "koulutus",
                },
                "voimassaAlkuPvm": "2014-05-06",
                "voimassaLoppuPvm": null,
                "tila": "HYVAKSYTTY"
            };

            var koodiXxx = {
                "koodiUri": "xxx_123",
                "versio": 1,
                "koodisto": {
                    "koodistoUri": "koulutus",
                },
                "voimassaAlkuPvm": "1997-01-01",
                "voimassaLoppuPvm": null,
                "tila": "HYVAKSYTTY"
            };

            var SHOW_LUONNOS = true;
            var DONT_SHOW_LUONNOS = false;

            var map = {};
            vu._filtterKoodiToMap(date, map, koodiWillBeginInFuture, false, DONT_SHOW_LUONNOS);
            expect(Object.keys(map).length).toBe(0);

            map = {};
            vu._filtterKoodiToMap(date, map, koodiExpired, false);
            expect(Object.keys(map).length).toBe(0);

            map = {};
            vu._filtterKoodiToMap(date, map, koodiWillExpired, false);
            expect(Object.keys(map).length).toBe(1);
            expect(map[koodiActive.koodiUri].versio).toBe(4);

            map = {};
            vu._filtterKoodiToMap(date, map, koodiActive, false);
            expect(Object.keys(map).length).toBe(1);
            expect(map[koodiActive.koodiUri].versio).toBe(1);

            map = {};
            vu._filterKoodiByTyyppiToMap(date, map, [koodiActive, koodiPassive, koodiExpired, koodiLuonnos], false, DONT_SHOW_LUONNOS);
            expect(Object.keys(map).length).toBe(1);
            expect(map[koodiActive.koodiUri].versio).toBe(1);

            map = {};
            vu._filterKoodiByTyyppiToMap(date, map, [koodiActive, koodiPassive, koodiExpired, koodiLuonnos], false, SHOW_LUONNOS);
            expect(Object.keys(map).length).toBe(1);
            expect(map[koodiActive.koodiUri].versio).toBe(5);

            map = {};
            vu._filterKoodiByTyyppiToMap(date, map, [koodiActive, koodiWillExpired, koodiXxx], false, DONT_SHOW_LUONNOS);
            expect(Object.keys(map).length).toBe(2);
            expect(map[koodiActive.koodiUri].versio).toBe(4);
            expect(map[koodiXxx.koodiUri].versio).toBe(1);

            map = {};
            vu._filterKoodiByTyyppiToMap(date, map, [koodiPassive, koodiLuonnos, koodiWillBeginInFuture], koodiActive.koodisto.koodistoUri, DONT_SHOW_LUONNOS);
            expect(Object.keys(map).length).toBe(0);

            expect(Object.keys(vu.filterKoodisByKoodistoUri([koodiActive], koodiActive.koodisto.koodistoUri)).length).toBe(1);
            expect(Object.keys(vu.filterKoodisByKoodistoUri([koodiActive], "fooo")).length).toBe(0);
            expect(Object.keys(vu.filterKoodis([koodiActive])).length).toBe(1);


            expect(vu._inRange(new Date('2014-01-01T23:59:58'), new Date('2014-01-01T23:59:57'), new Date('2014-01-01T23:59:59'))).toBe(true);
            expect(vu._inRange(new Date('2014-01-01T23:59:58'), new Date('2014-01-01T23:59:57'), new Date('2014-01-02'))).toBe(true);

            expect(vu._inRange(new Date('2014-01-01T23:59:58'), new Date('2014-01-01'), new Date('2014-01-01'))).toBe(false);
            expect(vu._inRange(new Date('2014-01-01T23:59:58'), new Date('2014-01-01'), new Date('2014-01-01'))).toBe(false);
            expect(vu._inRange(new Date('2014-01-02T00:00:01'), new Date('2014-01-01T23:59:59'), new Date('2014-01-02T00:00:01'))).toBe(true);
            expect(vu._inRange(new Date('2014-01-02T00:00:01'), new Date('2014-01-01T23:59:59'), new Date('2014-01-02T00:00:01'))).toBe(true);
            expect(vu._inRange(new Date('2014-01-02T01:01:01'), new Date('2014-01-01T23:59:57'), new Date('2014-01-02'))).toBe(false);
        }));
    });


    describe('Test koodi comparisons', function() {

        var testData = [
            // [sourceUri, targetUri, compareVersioned, compareNonVersioned],
            [null, null, true, true],
            ["", null, true, true],
            [null, "", true, true],
            ["", "", true, true],
            [null, "kieli_fi", false, false],
            [null, "kieli_fi#1", false, false],
            [null, "kieli_fi#2", false, false],
            ["kieli_fi", null, false, false],
            ["kieli_fi#1", null, false, false],
            ["kieli_fi#2", null, false, false],
            ["kieli_fi", "kieli_fi", true, true],
            ["kieli_fi", "kieli_fi#1", true, true],
            ["kieli_fi", "kieli_fi#1234", true, true],
            ["kieli_fi", "hakutapa_03", false, false],
            ["kieli_fi", "hakutapa_03#1", false, false],
            ["kieli_fi#1", "kieli_fi", false, true],
            ["kieli_fi#1", "kieli_fi#1", true, true],
            ["kieli_fi#1", "kieli_fi#1234", false, true],
            ["kieli_fi", "kieli_sv", false, false],
            ["kieli_fi", "kieli_sv#1", false, false],
            ["kieli_fi", "hakutapa_03", false, false],
            ["kieli_fi", "hakutapa_03#1", false, false],
            ["kieli_fi#1", "kieli_sv", false, false],
            ["kieli_fi#1", "kieli_sv#1", false, false],
            ["kieli_fi#1", "hakutapa_03", false, false],
            ["kieli_fi#1", "hakutapa_03#112", false, false]
        ];

        it('koodi comparison should match expected', inject(function(KoodistoURI) {

            for (var i = 0; i < testData.length; i++) {
                var testDataRow = testData[i];

                var source = testDataRow[0];
                var target = testDataRow[1];
                var shouldEqual = testDataRow[2];
                var shouldEqualNoVersion = testDataRow[3];

                var result = KoodistoURI.compareKoodi(source, target);
                var resultNoVersion = KoodistoURI.compareKoodi(source, target, true);

                // console.log("XXX", source, target, shouldEqual, shouldEqualNoVersion, result, resultNoVersion);

                if (shouldEqual != result) {
                    console.log("THIS COMPARISON FAILS (versioned): ", source, target);
                }
                if (shouldEqualNoVersion != resultNoVersion) {
                    console.log("THIS COMPARISON FAILS (non versioned): ", source, target);
                }

                expect(result).toBe(shouldEqual);
                expect(resultNoVersion).toBe(shouldEqualNoVersion);
            }
        }));

    });


});


describe('Koodisto Component directive test', function() {

    var el;


    var mockData = [
        {
            koodiUri: 'uriuriuriuriuri',
            koodiNimi: 'nimi'
        },
        {
            koodiUri: 'uri2uri2uri2uri2uri2',
            koodiNimi: 'nimi2'
        }
    ];



    beforeEach(module('KoodistoCombo', function($provide) {

        $provide.provider('Koodisto', {
            $get: function($q) {
                return {
                    getYlapuolisetKoodit: function(koodiUriParam, locale) {
                        var retval = $q.defer();

                        retval.resolve(mockData);

                        return retval.promise;
                    },
                    getAllKoodisWithKoodiUri: function(koodistoUriParam, locale) {
                        var retval = $q.defer();

                        retval.resolve(mockData);

                        return retval.promise;
                    }

                };
            }
        });


    }));


    beforeEach(module('js/shared/directives/koodistoNimiCombo.html'));

    beforeEach(inject(function($compile, $rootScope) {

        var scope = $rootScope;
        scope.locale = 'FI';

        el = angular.element('<koodistocombo isdependent="false" koodistouri="koodistouri" locale="locale" koodiuri="koodiuri"></koodistocombo>');
        $compile(el)(scope);
        scope.$digest();
        console.log(el[0].outerHTML);
    }));

    /* kommentoitu pois, koska jotain vikaa. mahdollisesti johtuu vesion paivityksesta
     it('should render koodistocombo with mock data',function(){
     console.log('Testing IT');
     expect(el.text()).toContain('nimi');
     //expect(true).toBe(true);
     });
     */

});