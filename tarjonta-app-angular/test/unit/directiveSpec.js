'use strict';


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



    beforeEach(module('KoodistoCombo',function($provide,$q){
        $provide.provider('Koodisto',{

            $get: function () {
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
    beforeEach(module('js/shared/directives/koodistoCombo.html'));

    beforeEach(inject(function($compile,$rootScope){
        var scope = $rootScope;
        scope.locale = 'FI';
        el = angular.element('<koodistocombo isdependent="false" koodistouri="koodistouri" locale="locale" koodiuri="koodiuri"></koodistocombo>');
        $compile(el)(scope);
        scope.$digest();
        console.log(el[0].outerHTML);
    }));

    /*it('should render koodistocombo with mock data',function(){
        console.log('Testing IT');
        expect(el.text()).toContain('nimi');
    });*/

});