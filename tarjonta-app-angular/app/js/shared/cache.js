angular.module('TarjontaCache', ['ngResource', 'config']).factory('CacheService', function($resource, $log, $q, Config) {
	
	var cacheData = {};
	
	var cacheService = {}
	
	/**
	 * Hakee tavaraa cachesta avaimen mukaan tai delegoi getterille.
	 * 
	 * @param key Avain (mikä tahansa string).
	 * @param getter Funktio, jolle hakeminen delegoidaan jos arvoa ei löytynyt. Parametriksi annetaan promise jonka funktio resolvaa.
	 * @returns promise
	 */
	cacheService.lookup = function(key, getter) {
        var ret = $q.defer();
		
		if (cacheData[key]!=undefined) {
			console.log("Cache hit ",key);
			ret.resolve(cacheData[key]);
		} else {
			console.log("Cache miss", key);
			var query = $q.defer();
			query.promise.then(function(res){
				console.log("Cache insert ",key);
				cacheData[key] = res;
				ret.resolve(res);
			});
			getter(query);
		}
		return ret.promise;	
	};

	/**
	 * Rest-apumetodi cachesta hakemiseen.
	 * 
	 * @param key Avain (mikä tahansa string).
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