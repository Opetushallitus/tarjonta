angular.module('Yhteyshenkilo', [
    'ngResource',
    'config',
    'Logging'
]) //"henkiloservice"
    .factory('YhteyshenkiloService', function($resource, $log, $q, Config, CacheService, $injector) {
        var plainUrls = window.urls().noEncode();

        $log = $log.getInstance('YhteyshenkiloService');
        var henkHaku = $resource(plainUrls.url("oppijanumerorekisteri-service.henkilo", {page:1, count:2000, passivoitu:false, duplikaatti:false, tyyppi:"VIRKAILIJA"}), {}, {
            cache: true,
            get: {
                method: 'GET',
                withCredentials: true
            }
        });
        var henkilo = $resource(plainUrls.url("oppijanumerorekisteri-service.urlHaeTiedot", ":oid"), {}, {
            cache: true,
            get: {
                method: 'GET',
                withCredentials: true
            }
        });
        var organisaatioHenkilo = $resource(plainUrls.url("kayttooikeus-service.organisaatiohenkilo", ":oid"), {}, {
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