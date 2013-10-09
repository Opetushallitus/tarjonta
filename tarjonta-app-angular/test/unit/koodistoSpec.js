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

    var koodistoMockData = [
        {"koodiUri":"koulutus_851301","resourceUri":"http://koodistopalvelu.opintopolku.fi/koulutus/koodi/koulutus_851301","versio":1,"koodisto":{"koodistoUri":"koulutus","organisaatioOid":"1.2.246.562.10.00000000001","koodistoVersios":[1]},"koodiArvo":"851301","paivitysPvm":1379314392378,"voimassaAlkuPvm":"1997-01-01","voimassaLoppuPvm":null,"tila":"LUONNOS","metadata":[{"nimi":"Tekn. lic., datateknik","kuvaus":"Tekn. lic., datateknik","lyhytNimi":"TkL, datateknik","kayttoohje":null,"kasite":null,"sisaltaaMerkityksen":null,"eiSisallaMerkitysta":null,"huomioitavaKoodi":null,"sisaltaaKoodiston":null,"kieli":"SV"},{"nimi":"Tekn. lis., tietotekniikka","kuvaus":"Tekn. lis., tietotekniikka","lyhytNimi":"TkL, tietotekniikka","kayttoohje":null,"kasite":null,"sisaltaaMerkityksen":null,"eiSisallaMerkitysta":null,"huomioitavaKoodi":null,"sisaltaaKoodiston":null,"kieli":"FI"},{"nimi":"LicSc, Information Tech.","kuvaus":"LicSc, Information Tech.","lyhytNimi":"Licentiate of Science (Technology), Information Technology","kayttoohje":null,"kasite":null,"sisaltaaMerkityksen":null,"eiSisallaMerkitysta":null,"huomioitavaKoodi":null,"sisaltaaKoodiston":null,"kieli":"EN"}]},
        {"koodiUri":"koulutus_851401","resourceUri":"http://koodistopalvelu.opintopolku.fi/koulutus/koodi/koulutus_851401","versio":1,"koodisto":{"koodistoUri":"koulutus","organisaatioOid":"1.2.246.562.10.00000000001","koodistoVersios":[1]},"koodiArvo":"851401","paivitysPvm":1379314408127,"voimassaAlkuPvm":"1997-01-01","voimassaLoppuPvm":null,"tila":"LUONNOS","metadata":[{"nimi":"Tekn. lis., kemian tekniikka","kuvaus":"Tekn. lis., kemian tekniikka","lyhytNimi":"TkL, kemian tekniikka","kayttoohje":null,"kasite":null,"sisaltaaMerkityksen":null,"eiSisallaMerkitysta":null,"huomioitavaKoodi":null,"sisaltaaKoodiston":null,"kieli":"FI"},{"nimi":"LicSc, Chemical Eng.","kuvaus":"LicSc, Chemical Eng.","lyhytNimi":"Licentiate of Science (Technology), Chemical Engineering","kayttoohje":null,"kasite":null,"sisaltaaMerkityksen":null,"eiSisallaMerkitysta":null,"huomioitavaKoodi":null,"sisaltaaKoodiston":null,"kieli":"EN"},{"nimi":"Tekn. lic., kemisk teknik","kuvaus":"Tekn. lic., kemisk teknik","lyhytNimi":"TkL, kemisk teknik","kayttoohje":null,"kasite":null,"sisaltaaMerkityksen":null,"eiSisallaMerkitysta":null,"huomioitavaKoodi":null,"sisaltaaKoodiston":null,"kieli":"SV"}]}
    ];



    beforeEach(module('Koodisto'));

    describe('Test koodi value converter returns localized nimi',function(){
        it('should return array with 2 elements with localized name',inject(function(Koodisto){
            var koodis = Koodisto.convertKoodistoKoodiToViewModelKoodi(koodistoMockData,'FI');

            var koodi = koodis[0];


            expect(koodi.koodiNimi).toBe('Tekn. lis., tietotekniikka');
        }));
    });

    describe('Test koodi value converter',function(){

        it('should return array of 2',inject(function(Koodisto){
             var koodis = Koodisto.convertKoodistoKoodiToViewModelKoodi(koodistoMockData,'FI');
            expect(koodis.length).toBe(2);
        }));
    });

});


describe('Koodisto Component directive test', function() {

    var el;


    var mockData = [
        {
            koodiUri : 'uriuriuriuriuri',
            koodiNimi : 'nimi'
        },
        {
            koodiUri : 'uri2uri2uri2uri2uri2',
            koodiNimi : 'nimi2'
        }
    ];



   beforeEach(module('KoodistoCombo', function($provide){

  $provide.provider('Koodisto',{



            $get: function ($q) {
                return {

                    getYlapuolisetKoodit : function(koodiUriParam,locale) {
                        var retval = $q.defer();

                        retval.resolve(mockData);

                        return retval.promise;
                    },
                    getAllKoodisWithKoodiUri : function(koodistoUriParam, locale) {
                       var retval = $q.defer();

                        retval.resolve(mockData);

                        return retval.promise;
                    }

                };
            }
        });


    }));


    beforeEach(module('js/shared/directives/koodistoNimiCombo.html'));

    beforeEach(inject(function($compile,$rootScope){

        var scope = $rootScope;
        scope.locale = 'FI';

        el = angular.element('<koodistocombo isdependent="false" koodistouri="koodistouri" locale="locale" koodiuri="koodiuri"></koodistocombo>');
        $compile(el)(scope);
        scope.$digest();
        console.log(el[0].outerHTML);
    }));

    it('should render koodistocombo with mock data',function(){
        console.log('Testing IT');
        expect(el.text()).toContain('nimi');
        //expect(true).toBe(true);
    });

});