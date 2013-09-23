'use strict';


describe('koodistoServiceTest', function() {

    var koodistoMockData = [
        {"koodiUri":"koulutus_851301","resourceUri":"http://koodistopalvelu.opintopolku.fi/koulutus/koodi/koulutus_851301","versio":1,"koodisto":{"koodistoUri":"koulutus","organisaatioOid":"1.2.246.562.10.00000000001","koodistoVersios":[1]},"koodiArvo":"851301","paivitysPvm":1379314392378,"voimassaAlkuPvm":"1997-01-01","voimassaLoppuPvm":null,"tila":"LUONNOS","metadata":[{"nimi":"Tekn. lic., datateknik","kuvaus":"Tekn. lic., datateknik","lyhytNimi":"TkL, datateknik","kayttoohje":null,"kasite":null,"sisaltaaMerkityksen":null,"eiSisallaMerkitysta":null,"huomioitavaKoodi":null,"sisaltaaKoodiston":null,"kieli":"SV"},{"nimi":"Tekn. lis., tietotekniikka","kuvaus":"Tekn. lis., tietotekniikka","lyhytNimi":"TkL, tietotekniikka","kayttoohje":null,"kasite":null,"sisaltaaMerkityksen":null,"eiSisallaMerkitysta":null,"huomioitavaKoodi":null,"sisaltaaKoodiston":null,"kieli":"FI"},{"nimi":"LicSc, Information Tech.","kuvaus":"LicSc, Information Tech.","lyhytNimi":"Licentiate of Science (Technology), Information Technology","kayttoohje":null,"kasite":null,"sisaltaaMerkityksen":null,"eiSisallaMerkitysta":null,"huomioitavaKoodi":null,"sisaltaaKoodiston":null,"kieli":"EN"}]},
        {"koodiUri":"koulutus_851401","resourceUri":"http://koodistopalvelu.opintopolku.fi/koulutus/koodi/koulutus_851401","versio":1,"koodisto":{"koodistoUri":"koulutus","organisaatioOid":"1.2.246.562.10.00000000001","koodistoVersios":[1]},"koodiArvo":"851401","paivitysPvm":1379314408127,"voimassaAlkuPvm":"1997-01-01","voimassaLoppuPvm":null,"tila":"LUONNOS","metadata":[{"nimi":"Tekn. lis., kemian tekniikka","kuvaus":"Tekn. lis., kemian tekniikka","lyhytNimi":"TkL, kemian tekniikka","kayttoohje":null,"kasite":null,"sisaltaaMerkityksen":null,"eiSisallaMerkitysta":null,"huomioitavaKoodi":null,"sisaltaaKoodiston":null,"kieli":"FI"},{"nimi":"LicSc, Chemical Eng.","kuvaus":"LicSc, Chemical Eng.","lyhytNimi":"Licentiate of Science (Technology), Chemical Engineering","kayttoohje":null,"kasite":null,"sisaltaaMerkityksen":null,"eiSisallaMerkitysta":null,"huomioitavaKoodi":null,"sisaltaaKoodiston":null,"kieli":"EN"},{"nimi":"Tekn. lic., kemisk teknik","kuvaus":"Tekn. lic., kemisk teknik","lyhytNimi":"TkL, kemisk teknik","kayttoohje":null,"kasite":null,"sisaltaaMerkityksen":null,"eiSisallaMerkitysta":null,"huomioitavaKoodi":null,"sisaltaaKoodiston":null,"kieli":"SV"}]}
    ];



    beforeEach(module('Koodisto'));



    describe('Test koodi value converter',function(){

        it('should return array of 2',inject(function(Koodisto){
             var koodis = Koodisto.convertKoodistoKoodiToViewModelKoodi(koodistoMockData,'FI');
            expect(koodis.length).toBe(2);
        }));
    });

});