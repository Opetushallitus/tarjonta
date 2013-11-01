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

angular.module('TarjontaCache', ['ngResource', 'config']).factory('CacheService', function($resource, $log, $q, Config) {
	
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
			return {pattern: key};
		} else if (!(key instanceof Object)) {
			return {key: ""+key};
		}
		if (key.pattern != undefined && !(key.pattern instanceof RegExp)) {
			key.pattern = new RegExp(key.pattern);
		}
		if (key.expires != undefined && !(key.expires instanceof Date)) {
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
			if (key.pattern!=undefined && key.pattern.test(rk)) {
				console.log("Evicted from cache during insert", rk);
				cacheData[rk] = undefined;
			}
		}
		
		cacheData[key.key] = {
				value: value,
				expires: key.expires
		}

		console.log("Cache insert",key.key);
	}
	
	/**
	 * Hakee tavaraa cachesta.
	 */
	cacheService.find = function(key) {
		key = prepare(key);
		var rv = cacheData[key.key];
		if (rv==undefined) {
			console.log("Cache miss",key);
			return null;
		}
		
		if (rv.expires!=undefined && rv.expires.getTime()<new Date().getTime()) {
			// expired
			console.log("Expired hit", key);
			cacheData[key.key] = null;
			return null;
		}

		console.log("Cache hit", key);

		return rv.value;
	}
	
	
	/**
	 * Poistaa tavaraa cachesta.
	 * @param key Cache-avain.
	 */
	cacheService.evict = function(key) {
		key = prepare(key);
		var now = new Date().getTime();
		
		for (var rk in cacheData) {
			if (key.key==rk
					|| (key.pattern!=undefined && key.pattern.test(rk))
					|| (key.expires!=null && key.expires.getTime()<now)) {
				console.log("Evicted from cache", rk);
				cacheData[rk] = undefined;
			}
		}
	}
	
	/**
	 * Hakee tavaraa cachesta avaimen mukaan tai delegoi getterille.
	 * 
	 * @param key Avain (ks. avaimen kuvaus tämän tiedoston alussa).
	 * @param getter Funktio, jolle hakeminen delegoidaan jos arvoa ei löytynyt. Parametriksi annetaan promise jonka funktio resolvaa.
	 * @returns promise
	 */
	cacheService.lookup = function(key, getter) {
        var ret = $q.defer();
		
        var res = cacheService.find(key);
        
		if (res!=undefined) {
			ret.resolve(res);
		} else {
			// palautetaan käynnissä oleva requesti jos sellainen on
			if (cacheRequests[key.key]) {
				console.log("Cache request hit", key);
				return cacheRequests[key.key];
			} else {
				cacheRequests[key.key] = ret.promise;
			}
			
			var query = $q.defer();
			
			query.promise.then(function(res){
				cacheService.insert(key, res);
				cacheRequests[key.key] = undefined;
				ret.resolve(res);
			}/*, function(res){
				// virhe -> tallennetaan null 10 sekunniksi jatkuvat toistamisen estämiseksi
				cacheService.insert({key: key.key, expires: 10000}, null);
				ret.resolve(null);
			}*/);
			getter(query);
		}
		return ret.promise;	
	};

	/**
	 * Rest-apumetodi cachesta hakemiseen.
	 * 
	 * @param key Avain (ks. avaimen kuvaus tämän tiedoston alussa).
	 * @param resource Rest-resurssi;  $resource(...)
	 * @param args Rest-kutsun parametrit.
	 * @param filter Valinnainen funktio jolla lopputulos käsitellään ennen palauttamista ja tallentamista cacheen..
	 * @return promise
	 */
	cacheService.lookupResource = function(key, resource, args, filter) {
		return cacheService.lookup(key, function(promise){
			resource.get(args, function(ret){
				promise.resolve(filter ? filter(ret) : ret);
			});
		});
	};
	
	return cacheService;
	
});