/**
 * All methods return promise, when fulfilled the actual result will be stored inside promise under key "data"
 */
angular.module('TarjontaPermissions', ['ngResource', 'config','Tarjonta']).factory('PermissionService', function($resource, $log, $q, Config, AuthService, TarjontaService) {
	
	var resolveData = function(promise) {
		if(promise===undefined) {
			throw new {"error":"need a prtomise"};
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
		 * methods that take in orgOid are common to both!:
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
			canCreate: function(orgOid) {
				console.log("hk canCreate");
				return canCreate(orgOid);
			},
			canPreview: function(orgOid) {
				// TODO
				console.log("TODO hakukohde.canPreview",oid);
				return true;
			},
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
//				resolveData(result);
				return defer.promise;
			},
			canTransition: function(oid, from, to) {
				// TODO
				console.log("TODO hakukohde.canTransition "+from+"->"+to, oid);
				return true;
			},
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
	}
});