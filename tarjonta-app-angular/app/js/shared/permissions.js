angular.module('TarjontaPermissions', ['ngResource', 'config']).factory('PermissionService', function($resource, $log, $q, Config) {
	
	return {
		
		koulutus: {
			canMoveOrCopy: function(koulutusOids) {
				// TODO
				console.log("TODO koulutus.canMoveOrCopy",koulutusOids);
				return koulutusOids.length>0;
			},
			canCreate: function(orgOid) {
				// TODO
				console.log("TODO koulutus.canCreate",orgOid);
				return orgOid!= Config.env["root.organisaatio.oid"];
			},
			canPreview: function(oid) {
				// TODO
				console.log("TODO koulutus.canPreview",oid);
				return true;
			},
			canEdit: function(oid) {
				// TODO
				console.log("TODO koulutus.canEdit",oid);
				return true;
			},
			canTransition: function(oid, from, to) {
				// TODO
				console.log("TODO koulutus.canTransition "+from+"->"+to, oid);
				return true;
			},
			canDelete: function(oid) {
				// TODO
				console.log("TODO koulutus.canDelete",oid);
				return true;
			}
		},
		
		hakukohde: {
			canCreate: function(koulutusOids) {
				// TODO
				console.log("TODO hakukohde.canCreate ",koulutusOids);
				return true;
			},
			canPreview: function(oid) {
				// TODO
				console.log("TODO hakukohde.canPreview",oid);
				return true;
			},
			canEdit: function(oid) {
				// TODO
				console.log("TODO hakukohde.canEdit",oid);
				return true;
			},
			canTransition: function(oid, from, to) {
				// TODO
				console.log("TODO hakukohde.canTransition "+from+"->"+to, oid);
				return true;
			},
			canDelete: function(oid) {
				// TODO
				console.log("TODO hakukohde.canDelete",oid);
				return true;
			}
		}
		
	}
	
});