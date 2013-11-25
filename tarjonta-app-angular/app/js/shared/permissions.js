/**
 * All methods return promise, when fulfilled the actual result will be stored inside promise under key "data"
 */
angular.module('TarjontaPermissions', ['ngResource', 'config','Tarjonta']).factory('PermissionService', function($resource, $log, $q, Config, AuthService, TarjontaService) {
	
	var resolveData = function(promise) {
		if(promise===undefined) {
			throw "need a promise";
		}
		//fills promise.data with the actual value when it resolves.
		promise.then(function(data){
			promise.data = data;
		}, function(){
			promise.data = false;
		});
	};

	var canCreate = function(orgOid) {
		var result=AuthService.crudOrg(orgOid);
		resolveData(result);
		return result;
		
	};

	return {
		
		/**
		 * funktiot jotka ottavat organisaatio oidin ovat yhteisiä molemmille (hk + k)!:
		 */

		canDelete: function(orgOid) {
			var result=AuthService.crudOrg(orgOid);
			resolveData(result);
			return result;
		},
		
		canCreate: function(orgOid) {
			canCreate(orgOid);
		},
		
		canEdit: function(orgOid) {
			var result=AuthService.updateOrg(orgOid);
			resolveData(result);
			return result;
		},
		
		koulutus: {

			/**
			 * Saako käyttäjä luoda koulutuksen
			 * @param orgOid
			 * @returns
			 */
			canCreate: function(orgOid) {
				return canCreate(orgOid);
			},

			canMoveOrCopy: function(orgOid) {
				console.log("TODO koulutus.canMoveOrCopy", orgOid);
				return true;
			},
			canPreview: function(orgOid) {
				if(orgOid===undefined) {
					console.log("koulutus.canMoveOrCopy", orgOid);
					return false;
				}
				// TODO
				console.log("TODO koulutus.canPreview",orgOid);
				return true;
			},

			/**
			 * Saako käyttäjä muokata koulutusta
			 * @param koulutusOid koulutuksen oid
			 * @returns
			 */
			canEdit: function(koulutusOid) {
				
				var defer = $q.defer();
				
				//hae koulutus
				var result = TarjontaService.haeKoulutukset({koulutusOid:koulutusOid});
				
				//tarkista permissio tarjoajaoidilla
				result = result.then(function(hakutulos){
					console.log("hakutulos:", hakutulos);
					if(hakutulos.tulokset!=undefined && hakutulos.tulokset.length==1) {
						AuthService.updateOrg(hakutulos.tulokset[0].oid).then(function(result){
							defer.resolve(result);
						}, function(){
							defer.resolve(false);
							});
					} else {
						defer.resolve(false);
					}
				});
				return defer.promise;
			},
			canTransition: function(oid, from, to) {
				// TODO
				console.log("TODO koulutus.canTransition "+from+"->"+to, oid);
				return true;
			},
			/**
			 * Saako käyttäjä poistaa koulutuksen
			 * @param koulutusOid
			 * @returns
			 */
			canDelete: function(koulutusOid) {

				var defer = $q.defer();
				
				//hae koulutus
				var result = TarjontaService.haeKoulutukset({koulutusOid:koulutusOid});
				
				//tarkista permissio tarjoajaoidilla
				result = result.then(function(hakutulos){
					console.log("hakutulos:", hakutulos);
					if(hakutulos.tulokset!=undefined && hakutulos.tulokset.length==1) {
						AuthService.crudOrg(hakutulos.tulokset[0].oid).then(function(result){
							defer.resolve(result);
						}, function(){
							defer.resolve(false);
							});
					} else {
						defer.resolve(false);
					}
				});
				return defer.promise;
			}
		},
		
		hakukohde: {
			/**
			 * Saako käyttäjä luoda hakukohteen
			 * @param orgOid organisaatio oid
			 * @returns
			 */
			canCreate: function(orgOid) {
				return canCreate(orgOid);
			},
			canPreview: function(orgOid) {
				// TODO
				console.log("TODO hakukohde.canPreview",oid);
				return true;
			},
			/**
			 * Saako käyttäjä muokata hakukohdetta
			 * @param hakukohdeOid
			 * @returns
			 */
			canEdit: function(hakukohdeOid) {
				var defer = $q.defer();
				
				//hae koulutus
				var result = TarjontaService.haeHakukohteet({hakukohdeOid:hakukohdeOid});
				
				//tarkista permissio tarjoajaoidilla
				result = result.then(function(hakutulos){
					console.log("hakutulos:", hakutulos);
					if(hakutulos.tulokset!=undefined && hakutulos.tulokset.length==1) {
						AuthService.updateOrg(hakutulos.tulokset[0].oid).then(function(result){
							defer.resolve(result);
						}, function(){
							defer.resolve(false);
							});
					} else {
						defer.resolve(false);
					}
				});
				return defer.promise;
			},
			canTransition: function(oid, from, to) {
				// TODO
				console.log("TODO hakukohde.canTransition "+from+"->"+to, oid);
				return true;
			},
			/**
			 * Saako käyttäjä poistaa hakukohteen
			 * @param hakukohdeOid
			 * @returns
			 */
			canDelete: function(hakukohdeOid) {
				var defer = $q.defer();
				
				//hae koulutus
				var result = TarjontaService.haeHakukohteet({hakukohdeOid:hakukohdeOid});
				
				//tarkista permissio tarjoajaoidilla
				result = result.then(function(hakutulos){
					console.log("hakutulos:", hakutulos);
					if(hakutulos.tulokset!=undefined && hakutulos.tulokset.length==1) {
						AuthService.crudOrg(hakutulos.tulokset[0].oid).then(function(result){
							defer.resolve(result);
						}, function(){
							defer.resolve(false);
							});
					} else {
						defer.resolve(false);
					}
				});
				return defer.promise;
			}
		}
	};
});