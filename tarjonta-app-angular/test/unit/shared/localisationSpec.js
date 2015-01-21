describe('localisation', function() {
    var THE_OFFICIAL_TEST_KEY = 'this.is.a.test.too_XXX';
    var THE_OFFICIAL_TEST_VALUE_FI = '83989859385938593857ksdjfhskjfhskjhf_fi';

    var CONFIG_ENV_MOCK = {
        'env': {
            'key-env-1': 'mock-value-env-1',
            'key-env-2': 'mock-value-env-2',
            'tarjonta.localisations': [{key: THE_OFFICIAL_TEST_KEY, value: THE_OFFICIAL_TEST_VALUE_FI, locale: 'fi'}],
            'casUrl' : 'cas_myroles_tiimi2',
            cas:{userinfo:{lang:'fi'}}
        }, 'app': {
            'key-app-1': 'mock-value-app-1'
        }
    };

    //set mock data to module by using the value-method,
    var mockModule = angular.module('test.module', []);
    mockModule.value('globalConfig', CONFIG_ENV_MOCK);

    beforeEach(module('Organisaatio'));
    beforeEach(module('Logging'));
    beforeEach(module('SharedStateService'));
    beforeEach(module('localisation'));
    beforeEach(module('test.module'));
    beforeEach(module('auth'));

    describe('LocalisationCtrl', function() {
        var scope;
        var controller;

        // Create controller with scope
        beforeEach(inject(function($rootScope, $controller, $log) {
            scope = $rootScope.$new();
            controller = $controller('LocalisationCtrl', {$scope: scope});
        }));

        // When an unknown translation is referred to - a '['+key+']' value should be returned
        it('unknown keys should be wrapped to []', inject(function() {
            var key1 = 'this.is.a.test';
            expect(scope.t(key1)).toEqual('[' + key1 + '-fi]');

            var key2 = 'this.is.a.test.too';
            expect(scope.t(key2)).toEqual('[' + key2 + '-fi]');

            var key3 = 'this.is.a.test.too_XXX';
            expect(scope.t(key3)).toEqual(THE_OFFICIAL_TEST_VALUE_FI);
        }));

    });
});
