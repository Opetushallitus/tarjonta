/**
 * All methods return promise, when fulfilled the actual result will be stored inside promise under key "data"
 */
angular.module('TarjontaPermissions', ['ngResource', 'config']).factory('PermissionService', function($resource, $log, $q, Config, AuthService) {
	
	var resolveData = function(promise) {
		//fills promise.data with the actual value when it resolves.
		promise.then(function(data){
			promise.data = data;
		}, function(){
			promise.data = false;
		});
	};
	
	return {
		
		koulutus: {
			canMoveOrCopy: function(orgOid) {
				console.log("TODO koulutus.canMoveOrCopy", orgOid);
				return AuthService.orgoid!==undefined;
			},
			canCreate: function(orgOid) {
				var result=AuthService.crudOrg(orgOid);
				resolveData(result);
				return result;
				
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
			canEdit: function(orgOid) {
				var result=AuthService.updateOrg(orgOid);
				resolveData(result);
				return result;
			},
			canTransition: function(oid, from, to) {
				// TODO
				console.log("TODO koulutus.canTransition "+from+"->"+to, oid);
				return true;
			},
			canDelete: function(orgOid) {
				var result=AuthService.crudOrg(orgOid);
				resolveData(result);
				return result;
			}
		},
		
		hakukohde: {
			canCreate: function(orgOid) {
				var result=AuthService.crudOrg(orgOid);
				resolveData(result);
				return result;
			},
			canPreview: function(orgOid) {
				// TODO
				console.log("TODO hakukohde.canPreview",oid);
				return true;
			},
			canEdit: function(orgOid) {
				var result=AuthService.updateOrg(orgOid);
				resolveData(result);
				return result;
			},
			canTransition: function(oid, from, to) {
				// TODO
				console.log("TODO hakukohde.canTransition "+from+"->"+to, oid);
				return true;
			},
			canDelete: function(orgOid) {
				var result=AuthService.crudOrg(orgOid);
				resolveData(result);
				return result;
			}
		}
	}
});