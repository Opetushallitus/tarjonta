/**
 * Cache-palvelu ui-tarpeisiin.
 *
 * Cache-avain on joko string tai map.
 *  - String muotoinen avain k muunnetaan automaattisesti muotoon {key: k}
 *
 * Map-muotoisen avaimen rakenne:
 *  - key: tekstimuotoinen pääavain (pakollinen, paitsi evictoidessa)
 *  - pattern: regex, johon täsmäävät avaimet poistetaan cachesta tallennettaessa (valinnainen)
 *  - expires: aika jolloin arvo poistuu cachesta; voi olla joko absoluuttinen (Date) tai relatiivinen (int)
 *
 */
angular.module('TarjontaCache', [
    'ngResource',
    'config',
    'Logging'
]).factory('CacheService', function($resource, $log, $q, Config) {
    $log = $log.getInstance('TarjontaCache');
    var cacheData = {};
    var cacheService = {};
    var cacheRequests = {};
    /**
	 * Täydentää cache-avaimen.
	 *
	 *  - Muuttaa patternin RegExp-olioksi, jos määritelty
	 *  - Muuttaa ajan Dateksi, jos määritelty
	 *  - Jos parametri on RegExp tai String, muuttaa mapiksi.
	 */
    function prepare(key) {
        if (key instanceof RegExp) {
            return {
                pattern: key
            };
        }
        else if (!(key instanceof Object)) {
            return {
                key: '' + key
            };
        }
        if (key.pattern !== undefined && !(key.pattern instanceof RegExp)) {
            key.pattern = new RegExp(key.pattern);
        }
        if (key.expires !== undefined && !(key.expires instanceof Date)) {
            var d = new Date();
            d.setTime(d.getTime() + key.expires);
            key.expires = d;
        }
        return key;
    }
    /**
	 * Lisää tavaraa cacheen.
	 */
    cacheService.insert = function(key, value) {
        key = prepare(key);
        for (var rk in cacheData) {
            if (key.pattern !== undefined && key.pattern.test(rk)) {
                $log.debug('Evicted from cache during insert', rk);
                cacheData[rk] = undefined;
            }
        }
        cacheData[key.key] = {
            value: value,
            expires: key.expires
        };
        $log.debug('Cache insert ', key);
    };
    /**
	 * Hakee tavaraa cachesta.
	 */
    cacheService.find = function(key) {
        key = prepare(key);
        var rv = cacheData[key.key];
        if (rv === undefined) {
            //$log.debug("Cache miss",key);
            return null;
        }
        if (rv.expires !== undefined && rv.expires.getTime() < new Date().getTime()) {
            // expired
            $log.debug('Expired hit', key);
            cacheData[key.key] = null;
            return null;
        }
        //$log.debug("Cache hit", key);
        return rv.value;
    };
    /**
	 * Poistaa tavaraa cachesta.
	 * @param {type} key Cache-avain.
	 */
    cacheService.evict = function(key) {
        key = prepare(key);
        var now = new Date().getTime();
        for (var rk in cacheData) {
            if (key.key == rk || key.pattern !== undefined && key.pattern.test(rk) ||
                key.expires !== null && key.expires.getTime() < now) {
                $log.debug('Evicted from cache', rk);
                cacheData[rk] = undefined;
            }
        }
    };
    /**
	 * Hakee tavaraa cachesta avaimen mukaan tai delegoi getterille.
	 *
	 * @param {type} key Avain (ks. avaimen kuvaus tämän tiedoston alussa).
	 * @param {type} getter Funktio, jolle hakeminen delegoidaan jos arvoa ei löytynyt.
     * Parametriksi annetaan promise jonka funktio resolvaa.
	 * @returns promise
	 */
    cacheService.lookup = function(key, getter) {
        var ret = $q.defer();
        key = prepare(key);
        var res = cacheService.find(key);
        if (res !== undefined) {
            ret.resolve(res);
        }
        else {
            // palautetaan käynnissä oleva requesti jos sellainen on
            if (cacheRequests[key.key]) {
                $log.debug('Cache request hit', key);
                return cacheRequests[key.key];
            }
            else {
                cacheRequests[key.key] = ret.promise;
            }
            var query = $q.defer();
            query.promise.then(function(res) {
                cacheRequests[key.key] = undefined;
                ret.resolve(res);
            });
            getter(query);
        }
        return ret.promise;
    };
    /**
	 * Rest-apumetodi cachesta hakemiseen.
	 *
	 * @param {type} key Avain (ks. avaimen kuvaus tämän tiedoston alussa).
	 * @param {type} resource Rest-resurssi;  $resource(...)
	 * @param {type} args Rest-kutsun parametrit.
	 * @param {type} filter Valinnainen funktio jolla lopputulos käsitellään ennen palauttamista ja tallentamista cacheen..
	 * @return promise
	 */
    cacheService.lookupResource = function(key, resource, args, filter) {
        return cacheService.lookup(key, function(promise) {
            resource.get(args, function(ret) {
                promise.resolve(filter ? filter(ret) : ret);
            });
        });
    };
    return cacheService;
});