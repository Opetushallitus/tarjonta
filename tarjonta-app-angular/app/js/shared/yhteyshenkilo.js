angular.module('Yhteyshenkilo', [
    'ngResource',
    'config',
    'Logging'
]) //"henkiloservice"
    .factory('YhteyshenkiloService', function($resource, $log, $q, Config, CacheService, $injector) {
        $log = $log.getInstance('YhteyshenkiloService');
        var baseUrl = Config.env['authentication-service.henkilo.rest.url'];
        var urlEtsi = baseUrl + '?count=2000&index=0&ht=VIRKAILIJA';
        var urlHaeTiedot = baseUrl + '/:oid';
        var urlHaeOrganisaatioHenkiloTiedot = baseUrl + '/:oid/organisaatiohenkilo';
        var henkHaku = $resource(urlEtsi, {}, {
            cache: true,
            get: {
                method: 'GET',
                withCredentials: true
            }
        });
        var henkilo = $resource(urlHaeTiedot, {}, {
            cache: true,
            get: {
                method: 'GET',
                withCredentials: true
            }
        });
        var organisaatioHenkilo = $resource(urlHaeOrganisaatioHenkiloTiedot, {}, {
            cache: true,
            get: {
                isArray: true,
                method: 'GET',
                withCredentials: true
            }
        });
        /**
        * Call this to disable system error dialog - note: only callable from ERROR handler of resource call!
        *
        * @returns {undefined}
        */
        function disableSystemErrorDialog() {
            var loadingService = $injector.get('loadingService');
            if (loadingService) {
                $log.debug('  disable system error dialog.');
                loadingService.onErrorHandled();
            }
            else {
                $log.warn('  FAILED TO disable system error dialog. Sorry about that.');
            }
        }
        return {
            /**
            * Etsii henkilöitä
            * @returns promise
            */
            etsi: function(hakuehdot) {
                var ret = $q.defer();
                henkHaku.get(hakuehdot, function(result) {
                    ret.resolve(result.results);
                }, function(err) {
                        disableSystemErrorDialog();
                        $log.debug('Error loading data', err);
                        ret.resolve([]);
                    });
                return ret.promise;
            },
            /**
            * Hakee henkilon tiedot(yhteystiedot)
            * @returns promise
            */
            haeHenkilo: function(oid) {
                var hakuehdot = {
                    oid: oid
                };
                var ret = $q.defer();
                $log.debug('haetaan henkilon tiedot, q:', hakuehdot);
                henkilo.get(hakuehdot, function(result) {
                    $log.info('haeHenkilo() -> ', result);
                    ret.resolve(result);
                }, function(err) {
                        $log.error('Error loading data', err);
                        disableSystemErrorDialog();
                        ret.reject('');
                    });
                return ret.promise;
            },
            /**
            * Hakee organisaatiohenkilon tiedot(tehtavanimike)
            * @returns promise
            */
            haeOrganisaatiohenkilo: function(oid) {
                var hakuehdot = {
                    oid: oid
                };
                var ret = $q.defer();
                $log.info('haetaan organisaatiohenkilon tiedot, q:', hakuehdot);
                organisaatioHenkilo.get(hakuehdot, function(result) {
                    $log.info('haeOrganisaatiohenkilo() -> ', hakuehdot, result);
                    ret.resolve(result);
                }, function(err) {
                        $log.error('Error loading data', err);
                        disableSystemErrorDialog();
                        ret.reject();
                    });
                return ret.promise;
            }
        };
    });