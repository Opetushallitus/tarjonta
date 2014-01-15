
angular.module('app.controllers', ['app.services','localisation','Organisaatio', 'config', 'ResultsTable'])
        .controller('SearchController', function($scope, $routeParams, $location, LocalisationService, Koodisto, OrganisaatioService, TarjontaService, PermissionService, Config, loadingService, $modal, $window, SharedStateService, AuthService) {

    var OPH_ORG_OID = Config.env["root.organisaatio.oid"];

	// 1. Organisaatiohaku
	function setDefaultHakuehdot(){
		$scope.hakuehdot={
			"searchStr" : "",
			"organisaatiotyyppi" : "",
			"oppilaitostyyppi" : "",
			"lakkautetut" : false,
			"suunnitellut" : false,
			"skipparents" : true
		};
	}
	
	if(SharedStateService.state.puut && SharedStateService.state.puut["organisaatio"].scope!==$scope) {
		console.log("scope has changed???");
		SharedStateService.state.puut["organisaatio"].scope = $scope;
	}
	
	setDefaultHakuehdot();

	$scope.oppilaitostyypit=Koodisto.getAllKoodisWithKoodiUri(Config.env["koodisto-uris.oppilaitostyyppi"], AuthService.getLanguage()).then(function(koodit) {
        //console.log("oppilaitostyypit", koodit);
        angular.forEach(koodit, function(koodi){
        	koodi.koodiUriWithVersion=koodi.koodiUri + "#" + koodi.koodiVersio;
        });
        $scope.oppilaitostyypit=koodit;
    });

	//valittu organisaatio populoidaan tänne
    $scope.organisaatio = {};
    

	//watchi valitulle organisaatiolle, tästä varmaan lähetetään "organisaatio valittu" eventti jonnekkin?
	$scope.$watch( 'organisaatio.currentNode', function( newObj, oldObj ) {

        //console.log("$scope.$watch( 'organisaatio.currentNode')");

	    if( $scope.organisaatio && angular.isObject($scope.organisaatio.currentNode) ) {

	    	$scope.selectedOrgOid = $scope.organisaatio.currentNode.oid;
	    	$scope.selectedOrgName = $scope.organisaatio.currentNode.nimi;

	    	updateLocation();
	    	PermissionService.koulutus.canCreate($scope.organisaatio.currentNode.oid).then(function(data){
	    		$scope.koulutusActions.canCreateKoulutus=data;
	    	});
	    	//$scope.koulutusActions.canCreateKoulutus = PermissionService.koulutus.canCreate($scope.organisaatio.currentNode.oid);
	    	
	    	$scope.search();
	    	
	    }
	}, false);

	$scope.organisaatioValittu=function(){
		return $scope.selectedOrgOid !==undefined && $scope.selectedOrgOid !== OPH_ORG_OID;
	};

	$scope.hakukohdeColumns =['hakutapa','aloituspaikat','koulutuslaji'];
	$scope.koulutusColumns = ['koulutuslaji'];

	// organisaatiotyypit; TODO jostain jotenkin dynaamisesti
	$scope.organisaatiotyypit = [{
		nimi : LocalisationService.t("organisaatiotyyppi.koulutustoimija"),
		koodi : 'Koulutustoimija'
	}, {
		nimi : LocalisationService.t("organisaatiotyyppi.oppilaitos"),
		koodi : "Oppilaitos"
	}, {
		nimi : LocalisationService.t("organisaatiotyyppi.toimipiste"),
		koodi : "Toimipiste"
	}, {
		nimi : LocalisationService.t("organisaatiotyyppi.oppisopimustoimipiste"),
		koodi : "Oppisopimustoimipiste"
	}];

	// Kutsutaan formin submitissa, käynnistää haun
	$scope.submitOrg = function() {
		//console.log("organisaatiosearch clicked!: " + angular.toJson($scope.hakuehdot));
		hakutulos = OrganisaatioService.etsi($scope.hakuehdot);
		hakutulos.then(function(vastaus){
//			console.log("result returned, hits:", vastaus);
			$scope.$root.tulos = vastaus.organisaatiot; //TODO, keksi miten tilan saa säästettyä ilman root scopea.
		});
    };

    // Kutsutaan formin resetissä, palauttaa default syötteet modeliin
    $scope.resetOrg = function() {
    	setDefaultHakuehdot();
    };

	// 2. Koulutusten/Hakujen haku

    // hakuparametrit ja organisaatiovalinta
    function fromParams(key, def) {
        return $routeParams[key] != null ? $routeParams[key] : def;
    }

    // Selected org from route path
    $scope.selectedOrgOid = $scope.routeParams.oid ? $scope.routeParams.oid : OPH_ORG_OID;
    
	$scope.hakukohdeResults = {};
	$scope.koulutusResults = {};
    
    $scope.spec = {
    		terms: fromParams("terms",""),
    	    state: fromParams("state","*"),
    	    year: fromParams("year","*"),
    	    season: fromParams("season","*")
    };

    var msgKaikki = LocalisationService.t("tarjonta.haku.kaikki");

    // tarjonnan tilat
    $scope.states = {"*": msgKaikki};
    for (var s in CONFIG.env["tarjonta.tila"]) {
    	$scope.states[s] = LocalisationService.t("tarjonta.tila." + s);
    }

    // alkamiskaudet
    $scope.seasons = {"*": msgKaikki};
    Koodisto.getAllKoodisWithKoodiUri("kausi", AuthService.getLanguage()).then(function(koodit) {
        console.log("koodit", koodit);
        $scope.seasons = {"*": msgKaikki};

        for (var i in koodit) {
            var k = koodit[i];
            $scope.seasons[k.koodiUri] = k.koodiNimi;
        }

    });

    // alkamisvuodet; 2012 .. nykyhetki + 10v
    $scope.years = {"*": msgKaikki};
    var lyr = new Date().getFullYear() + 10;
    for (var y = 2012; y < lyr; y++) {
        $scope.years[y] = y;
    }

    if (!$scope.selectedOrgName) {
        OrganisaatioService.nimi($scope.selectedOrgOid).then(function(nimi){$scope.selectedOrgName=nimi});
    }

    function copyIfSet(dst, key, value, def) {
        if (value != null && value != undefined && (value + "").length > 0 && value != "*") {
            dst[key] = value;
        } else if (def != undefined) {
            dst[key] = def;
        }
    }

    function updateLocation() {

        // Query parameters collected here
    	var sargs = {};

    	copyIfSet(sargs, "terms", $scope.spec.terms, "*");
    	copyIfSet(sargs, "state", $scope.spec.state);
    	copyIfSet(sargs, "year", $scope.spec.year);
    	copyIfSet(sargs, "season", $scope.spec.season);

        // Location should contain selected ORG oid if any
    	if ($scope.selectedOrgOid!=null && $scope.selectedOrgOid!=OPH_ORG_OID) {
        	$location.path("/etusivu/" + $scope.selectedOrgOid);
    	} else {
        	$location.path("/etusivu");
        }

        // Add query parameters
        $location.search(sargs);
    }

    $scope.clearOrg = function() {
        $scope.selectedOrgOid = OPH_ORG_OID;
        OrganisaatioService.nimi(OPH_ORG_OID).then(function(n) {
            $scope.selectedOrgName = n;
        });
        updateLocation();
    }

    $scope.reset = function() {
        $scope.spec.terms = "";
        $scope.spec.state = "*";
        $scope.spec.year = "*";
        $scope.spec.season = "*";
    }
    
	$scope.selection = {
			koulutukset: [],
			hakukohteet: []
	};

	$scope.$watch( 'selection.koulutukset', function( newObj, oldObj ) {
		$scope.koulutusActions.canMoveOrCopy = PermissionService.koulutus.canMoveOrCopy(newObj);
		$scope.koulutusActions.canCreateHakukohde=false;

		PermissionService.hakukohde.canCreate(newObj).then(function(result){
			$scope.koulutusActions.canCreateHakukohde = result;
		});


	}, true);

    $scope.menuOptions = [];
    
    $scope.koulutusActions = {
    		canMoveOrCopy: false,
    		canCreateHakukohde: false,
    		canCreateKoulutus: false
    };
    
    function rowActions(prefix, oid, tila, nimi, actions) {
    	var ret = [];
    	var tt = TarjontaService.getTilat()[tila];
    	
    	var canRead = PermissionService[prefix].canPreview(oid);
    	console.log("row actions can read",canRead);
    	
		// tarkastele
		if (canRead) {
			ret.push({url:"#/"+prefix+"/"+oid, title: LocalisationService.t("tarjonta.toiminnot.tarkastele")});
		}
		// muokkaa
		if (tt.mutable) {
			 PermissionService[prefix].canEdit(oid).then(function(result){ 
			 	console.log("row actions can edit", result);
			 	if(result) {
			 		ret.push({url:"#/"+prefix+"/"+oid+"/edit", title: LocalisationService.t("tarjonta.toiminnot.muokkaa")});
			 	}
			 });
		}
		// näytä hakukohteet
		if (canRead) {
			ret.push({url:"#", title: LocalisationService.t("tarjonta.toiminnot."+prefix+".linkit"),
				action: function(ev) {
					$scope.openLinksDialog(prefix, oid, nimi);
				}
			});
		}
		// tilasiirtymä
		switch (tila) {
		case "PERUTTU":
		case "VALMIS":
			if (PermissionService[prefix].canTransition(oid, tila, "JULKAISTU")) {
				ret.push({url:"#", title: LocalisationService.t("tarjonta.toiminnot.julkaise"),
					action: function(){
						TarjontaService.togglePublished(prefix, oid, true).then(function(ns){
							actions.update(ns);
							TarjontaService.evictHakutulokset();
						});
					}
				});
			}
			break;
		case "JULKAISTU":
			if (PermissionService[prefix].canTransition(oid, tila, "PERUTTU")) {
				ret.push({url:"#", title: LocalisationService.t("tarjonta.toiminnot.peruuta"),
					action: function(){
						TarjontaService.togglePublished(prefix, oid, false).then(function(ns){
							actions.update(ns);
							TarjontaService.evictHakutulokset();
						});
					}
				});
			}
			break;
		}
		// poista
		if (tt.removable) {
			PermissionService[prefix].canDelete(oid).then(function(result){
				if(result){
					ret.push({url: "#", title: LocalisationService.t("tarjonta.toiminnot.poista"),
						action: function(ev) {
							$scope.openDeleteDialog(prefix, oid, nimi, actions.remove);
						}
					});
				}
			});
		}
		
		return ret;
    }
    
    $scope.hakukohdeOptions = function(oid, tila, nimi, actions) {
		return rowActions("hakukohde", oid, tila, nimi, actions);
    };

    $scope.koulutusOptions = function(oid, tila, nimi, actions) {
		return rowActions("koulutus", oid, tila, nimi, actions);
    };
    
    $scope.search = function() {
    	var spec = {
            oid: $scope.selectedOrgOid,
            terms: $scope.spec.terms,
            state: $scope.spec.state == "*" ? null : $scope.spec.state,
            year: $scope.spec.year == "*" ? null : $scope.spec.year,
            season: $scope.spec.season == "*" ? null : $scope.spec.season
        };
    	
        console.log("search", spec);
        updateLocation();
        
        // valinnat
        TarjontaService.haeKoulutukset(spec).then(function(data){
        	$scope.koulutusResults = data;
        	$scope.koulutusResultCount = " ("+data.tuloksia+")";
        });
        
        TarjontaService.haeHakukohteet(spec).then(function(data){
        	$scope.hakukohdeResults = data;
        	$scope.hakukohdeResultCount = " ("+data.tuloksia+")";
        });
        
    };
    
    $scope.luoKoulutusDisabled=function(){
    	var disabled = !($scope.organisaatioValittu() && $scope.koulutusActions.canCreateKoulutus);
//    	console.log("luoKoulutusDisabled, organisaatioValittu:", $scope.organisaatioValittu(), "canCreateKoulutus:", $scope.koulutusActions.canCreateKoulutus);
        return disabled;
    };
    

    if ($scope.spec.terms!="" || $scope.selectedOrgOid != OPH_ORG_OID) {
    	if ($scope.spec.terms=="*") {
        	$scope.spec.terms="";
        }
    	// estää angularia tuhoamasta "liian nopeasti" haettua hakutuloslistausta
    	// TODO ei toimi luotettavasti -> korjaa
   		setTimeout($scope.search, 100);
    }

    $scope.report = function() {
        console.log("TODO raportti");
    };

    var DeleteDialogCtrl = function($scope, $modalInstance) {
    	
    	$scope.ok = function() {
    		$modalInstance.close();
    	};
    	
    	$scope.cancel = function() {
    		$modalInstance.dismiss();
    	};
    	
    };
    
    $scope.openDeleteDialog = function(prefix, oid, nimi, action) {
    	
    	var ns = $scope.$new();
    	ns.oid = oid;
    	ns.nimi = nimi;
    	ns.otsikko = "tarjonta.poistovahvistus.otsikko."+prefix;
    	ns.ohje = "tarjonta.poistovahvistus.ohje."+prefix;

    	var modalInstance = $modal.open({
			controller: DeleteDialogCtrl,
			templateUrl: "partials/search/delete-dialog.html",
			scope: ns
		});
    	
    	modalInstance.result.then(function(){

        	var promise = prefix=="hakukohde"
        		? TarjontaService.deleteHakukohde(oid)
    			: TarjontaService.deleteKoulutus(oid);
        		
        	promise.then(function(){
        		action();
            	TarjontaService.evictHakutulokset();
        	});
    		
    	});
    	
    };

    var LinksDialogCtrl = function($scope, $modalInstance) {
    	
    	$scope.otsikko = "tarjonta.linkit.otsikko."+$scope.prefix;
    	$scope.eohje = "tarjonta.linkit.eohje."+$scope.prefix;

    	$scope.items = [];
    	
    	$scope.ok = function() {
    		$modalInstance.close();
    	}
    	
    	var base = $scope.prefix=="koulutus" ? "hakukohde" : "koulutus";
    	
    	var ret = $scope.prefix=="koulutus"
    		? TarjontaService.getKoulutuksenHakukohteet($scope.oid)
			: TarjontaService.getHakukohteenKoulutukset($scope.oid);
    	
    	ret.then(function(ret){
    		for (var i in ret) {
    			var s = ret[i];
        		$scope.items.push({
        			url:"#/"+base+"/"+s.oid,
        			nimi:s.nimi
        		});
    		}
    	});
    	
    }
    
    $scope.openLinksDialog = function(prefix, oid, nimi) {
    	
    	var ns = $scope.$new();
    	ns.prefix = prefix;
    	ns.oid = oid;
    	ns.nimi = nimi;
    	
    	//console.log("LINKS p="+prefix+", o="+oid+", n="+nimi);
    	
    	var modalInstance = $modal.open({
			controller: LinksDialogCtrl,
			templateUrl: "partials/search/links-dialog.html",
			scope: ns
		});
    	
    };


    $scope.luoUusiHakukohde = function() {

        SharedStateService.addToState('SelectedKoulutukses',$scope.selection.koulutukset);
        SharedStateService.addToState('SelectedOrgOid',$scope.selectedOrgOid);
        $location.path('/hakukohde/new/edit');
    };
    
    
	/**
	 * Avaa "luoKoulutus 1. dialogi"
	 */
	$scope.openLuoKoulutusDialogi = function() {
		//aseta esivalittu organisaatio
		$scope.luoKoulutusDialogOrg=$scope.selectedOrgOid;
		$scope.luoKoulutusDialog = $modal.open({
			scope: $scope,
			templateUrl: 'partials/koulutus/luo-koulutus-dialogi.html',
			controller: 'LuoKoulutusDialogiController',
		});
	};
 
	
//	
//    
//    $scope.tutkintoDialogModel = {};
//	
//	$scope.tutkintoDialogModel.open = function() {
//		
//			var modalInstance = $modal.open({
//				scope: $scope,
//				templateUrl: 'partials/koulutus/edit/selectTutkintoOhjelma.html',
//				controller: 'SelectTutkintoOhjelmaController'
//			});
//		
//			modalInstance.result.then(function(selectedItem) {
//				console.log('Ok, dialog closed: ' + selectedItem.koodiNimi);
//				console.log('Koodiarvo is: ' + selectedItem.koodiArvo);
//				if (selectedItem.koodiUri != null) {
//					$window.location.href = '#/koulutus/edit/' + $scope.selectedOrgOid + '/' + selectedItem.koodiArvo + '/';
//				} 
//			}, function() {
//				$scope.tutkintoDialogModel.selected = null;
//				console.log('Cancel, dialog closed');
//			});
//	};

});
