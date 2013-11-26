'use strict';

/* Controllers */

var app = angular.module('app.koulutus.ctrl');

app.controller('LuoKoulutusDialogiController', ['$location', '$q', '$scope', 'Koodisto', '$modal', 'OrganisaatioService', 'SharedStateService', 'AuthService',
		function($location, $q, $scope, Koodisto, $modal, OrganisaatioService, SharedStateService, AuthService) {
	
	 // Tähän populoidaan formin valinnat:
	$scope.model={
		koulutustyyppi:undefined,
		organisaatiot:[]
	};

	//resolvaa tarvittavat koodit ja suhteet... rakentaa mapit validointia varten:
	// oppilaitostyyppi -> [koulutustyypit]
	// koulutustyyppi -> [oppilaitostyypit]
	
	//luo tarvittavat tietorakenteet valintojen validointia varten:
	SharedStateService.state.luoKoulutusaDialogi = SharedStateService.state.luoKoulutusaDialogi || {};
	SharedStateService.state.luoKoulutusaDialogi.oppilaitostyypit=SharedStateService.state.luoKoulutusaDialogi.oppilaitostyypit || {};
	SharedStateService.state.luoKoulutusaDialogi.koulutustyypit=SharedStateService.state.luoKoulutusaDialogi.koulutustyypit || {};


	// hätäkorjaus KJOH-670	
	if(SharedStateService.state.puut && SharedStateService.state.puut["lkorganisaatio"] && SharedStateService.state.puut["lkorganisaatio"].scope!==$scope) {
		
		console.log("puut:", SharedStateService.state.puut);
		SharedStateService.state.puut["lkorganisaatio"]=SharedStateService.state.puut["lkorganisaatio"]||{};
		SharedStateService.state.puut["lkorganisaatio"].scope = $scope;
	}

	var promises =[];
	
	if(!SharedStateService.state.luoKoulutusaDialogi.koulutustyyppikoodit){
		var deferred = $q.defer();
		promises.push(deferred.promise);
	}
	SharedStateService.state.luoKoulutusaDialogi.koulutustyyppikoodit = SharedStateService.state.luoKoulutusaDialogi.koulutustyyppikoodit || Koodisto.getAllKoodisWithKoodiUri('koulutustyyppi','fi').then(
		function(koodit){
			
			var subpromises =[];

			for(var i=0;i<koodit.length;i++) {
				console.log("koulutustyyppikoodi:", koodit[i]);
				SharedStateService.state.luoKoulutusaDialogi[koodit[i]]=[];
				var koulutustyyppi = koodit[i];

				//funktio joka rakentaa sopivat mapit koodistojen valintaan (koulutustyyppiuri->oppilaitostyyppi[], oppilaitostyyppiuri->koulutustyyppi[])
				var ylapuoliset = function(koulutustyyppi) { 
					return function(ylapuoliset) {
						for(var j=0;j<ylapuoliset.length;j++) {
							if("oppilaitostyyppi" === ylapuoliset[j].koodiKoodisto) {
								var oppilaitostyyppi = ylapuoliset[j];
								var kturi = koulutustyyppi.koodiUri + "#" + oppilaitostyyppi.koodiVersio;
								var oturi = oppilaitostyyppi.koodiUri + "#" + oppilaitostyyppi.koodiVersio;
								SharedStateService.state.luoKoulutusaDialogi.oppilaitostyypit[kturi]=SharedStateService.state.luoKoulutusaDialogi.oppilaitostyypit[kturi] || [];
								SharedStateService.state.luoKoulutusaDialogi.oppilaitostyypit[kturi].push(oppilaitostyyppi);
								SharedStateService.state.luoKoulutusaDialogi.koulutustyypit[oturi]=SharedStateService.state.luoKoulutusaDialogi.koulutustyypit[oturi] || [];
								SharedStateService.state.luoKoulutusaDialogi.koulutustyypit[oturi].push(koulutustyyppi);
								console.log(oppilaitostyyppi.koodiUri,"<->", koulutustyyppi.koodiUri);
							}
						}
					};
				};
				
				var promise=Koodisto.getYlapuolisetKoodit(koulutustyyppi.koodiUri, AuthService.getLanguage()).then(ylapuoliset(koulutustyyppi));
				subpromises.push(promise);
			}
			
			$q.all(subpromises).then(function(){
				console.log("all sub promises are now resolved!", deferred);
				deferred.resolve();
			});
			
		}		
	);
	
	$scope.lkorganisaatio=$scope.lkorganisaatio||{currentNode:undefined};
	// Watchi valitulle organisaatiolle
	$scope.$watch('lkorganisaatio.currentNode', function(organisaatio, oldVal) {
		console.log("oprganisaatio valittu", organisaatio);
		//XXX nyt vain yksi organisaatio valittavissa
	    if($scope.model.organisaatiot.length==0 && organisaatio!==undefined && organisaatio.oid!==undefined && $scope.model.organisaatiot.indexOf(organisaatio)==-1){
	    	lisaaOrganisaatio(organisaatio);
	    }
	});

	$scope.valitut=$scope.valitut||[];
	$scope.organisaatiomap=$scope.organisaatiomap||{};
	$scope.sallitutKoulutustyypit=$scope.sallitutKoulutustyypit||[];

//	console.log("organisaatio:", $scope.luoKoulutusDialogOrg);

	// haetaan organisaatihierarkia joka valittuna kälissä tai jos mitään ei ole valittuna organisaatiot joihin käyttöoikeus
	OrganisaatioService.etsi({oidRestrictionList:$scope.luoKoulutusDialogOrg||AuthService.getOrganisations()}).then(function(vastaus) {
		//console.log("asetetaan org hakutulos modeliin.");
		$scope.lkorganisaatiot = vastaus.organisaatiot;
		//rakennetaan mappi oid -> organisaatio jotta löydetään parentit helposti
		var buildMapFrom=function(orglist) {
			for(var i=0;i<orglist.length;i++) {
				var organisaatio = orglist[i];
				$scope.organisaatiomap[organisaatio.oid]=organisaatio;
				if(organisaatio.children) {
					buildMapFrom(organisaatio.children);
				}
			}
		};
		buildMapFrom(vastaus.organisaatiot);

		//hakee kaikki valittavissa olevat koulutustyypit
     	var oltUrit = [];

		for(var i=0;i<vastaus.organisaatiot.length;i++) {
	    	var oppilaitostyypit = haeOppilaitostyypit(vastaus.organisaatiot[i]);
	    	for(var i=0;i<oppilaitostyypit.length;i++) {
	    		if(oltUrit.indexOf(oppilaitostyypit[i])==-1) {
	    			oltUrit.push(oppilaitostyypit[i]);
	    		}
	    	}
		}
		
		//console.log("oppilaitostyyppejä:", oltUrit.length);
		
		//jos valittavissa vain yksi, 2. selectiä ei pitäisi näyttää.
		//$scope.piilotaKoulutustyyppi=oltUrit.length<2;

		
		$q.all(promises).then(function(){
			paivitaKoulutustyypit(oltUrit);
		    //console.log("all done!");
		 });

		/*
		//allaoleva bugaa koska tätä suorittaessa pitäisi olla koodistot ja relaatiot haettuna, disabloitu for now
		paivitaKoulutustyypit(oltUrit);
		*/

		
	});
	

	var lisaaOrganisaatio = function(organisaatio) {
    	$scope.model.organisaatiot.push(organisaatio);
    	console.log("lisaaOrganisaatio:", organisaatio);
    	var oppilaitostyypit = haeOppilaitostyypit(organisaatio);
    	console.log("oppilaitostyypit:", oppilaitostyypit);
		console.log("kaikki koulutustyypit:", SharedStateService.state.luoKoulutusaDialogi.koulutustyypit);
		paivitaKoulutustyypit(oppilaitostyypit);
	};
	
	var paivitaKoulutustyypit = function(oppilaitostyypit) {
		var sallitutKoulutustyypit=[];
		if(oppilaitostyypit!==undefined){
    	for(var i=0;i<oppilaitostyypit.length;i++) {
    		var oppilaitostyyppiUri = oppilaitostyypit[i];
    		console.log("getting koulutustyyppi for ", oppilaitostyyppiUri);
    		var koulutustyypit = SharedStateService.state.luoKoulutusaDialogi.koulutustyypit[oppilaitostyyppiUri];
    		//console.log("got:", koulutustyypit);
    		if(koulutustyypit) {
    			for(var j=0;j<koulutustyypit.length;j++) {
    				if(sallitutKoulutustyypit.indexOf(koulutustyypit[j])==-1){
    					sallitutKoulutustyypit.push(koulutustyypit[j]);
    				}
    			}
    		} else {
    			console.log("oppilaitostyypille: '", oppilaitostyyppiUri, "' ei löydy koulutustyyppejä");
    		}
    	
    	}
		//console.log("asetetaan koulutustyypit: ", sallitutKoulutustyypit);
		}
		$scope.sallitutKoulutustyypit = sallitutKoulutustyypit;
	};
	
	//alusta koulutustyypit (kaikki valittavissa olevat)
	paivitaKoulutustyypit();
	
	/*
	 * Hakee oppilaitostyypit organisaatiolle, koulutustoimijalle haetaan allaolevista oppilaitoksista,
	 * oppilaitoksen tyypit tulee oppilaitokselta, toimipisteen tyyppi typee ylemmän tason oppilaitokselta.
	 * TODO lisää testi
	 */
	var haeOppilaitostyypit=function(organisaatio) {
		var oppilaitostyypit=[];
		
		/*
		 * Lisää organisaation oppilaitostyyppin (koodin uri) arrayhin jos se != undefined ja ei jo ole siinä
		 */
		var addTyyppi=function(organisaatio){
			if(organisaatio.oppilaitostyyppi!==undefined && oppilaitostyypit.indexOf(organisaatio)==-1){
				oppilaitostyypit.push(organisaatio.oppilaitostyyppi);
			}
		};
		
		if(organisaatio.organisaatiotyypit.indexOf("KOULUTUSTOIMIJA")!=-1 && organisaatio.children!==undefined) {
    	//	koulutustoimija, kerää oppilaitostyypit lapsilta (jotka oletetaan olevan oppilaitoksia)
			for(var i=0;i<organisaatio.children.length;i++) {
				addTyyppi(organisaatio.children[i]);
			}
		}
		
		else if(organisaatio.organisaatiotyypit.indexOf("OPPILAITOS")!=-1 && organisaatio.oppilaitostyyppi!==undefined) {
			//oppilaitos, kerää tyyppi
			addTyyppi(organisaatio);
		}
    	
		else if(organisaatio.organisaatiotyypit.indexOf("OPETUSPISTE")!=-1) {
			//opetuspiste, kerää parentin tyyppi
			var parent = $scope.organisaatiomap[organisaatio.parentOid];
			if(undefined!== parent) {
				addTyyppi(parent);
			}
		} else {
			console.log( "Tuntematon organisaatiotyyppi:", organisaatio.organisaatiotyypit );
		}
		return oppilaitostyypit;
	};
	
	/**
	 * Peruuta nappulaa klikattu, sulje dialogi
	 */
	$scope.peruuta = function() {
		console.log("peruuta");
		$scope.luoKoulutusDialog.dismiss('cancel');
	};

	/**
	 * Jatka nappulaa klikattu, avaa seuraava dialogi TODO jos ei kk pitäisi mennä suoraan lomakkeelle?
	 */
	$scope.jatka = function() {
		$scope.tutkintoDialogModel={};
		
		//XXX nyt vain kk kovakoodattuna!!
		if($scope.model.koulutustyyppi.koodiUri==="koulutustyyppi_3"){
			
		
		var modalInstance = $modal.open({
			scope: $scope,
			templateUrl: 'partials/koulutus/edit/selectTutkintoOhjelma.html',
			controller: 'SelectTutkintoOhjelmaController'
		});
		
		modalInstance.result.then(function(selectedItem) {
			$scope.luoKoulutusDialog.close();
//			console.log('Ok, dialog closed: ' + selectedItem.koodiNimi);
//			console.log('Koodiarvo is: ' + selectedItem.koodiArvo);
			if (selectedItem.koodiUri != null) {
				console.log("org:", $scope.model.organisaatiot[0]);
				$location.path('/koulutus/edit/' + $scope.model.organisaatiot[0].oid + '/' + selectedItem.koodiArvo + '/');
			} 
		}, function() {
			$scope.tutkintoDialogModel.selected = null;
//			console.log('Cancel, dialog closed');
			$scope.luoKoulutusDialog.close();
		});
		} else {
			
			//ei toteutettu hässäkkä, positetaan kun muutkin tyypit on tuettu:
			$scope.dialog={
                title: "ei toteutettu",
                description: "",
                ok: "ok",
                cancel: "cancel"
            };
			
			$scope.eitoteutettu = $modal.open({
				scope: $scope,
				templateUrl: 'partials/common/dialog.html',
				controller: function(){
					$scope.onClose=function(){
						console.log("close!");
						$scope.eitoteutettu.close();
					};
					$scope.onAction=function(){
						console.log("close!");
						$scope.eitoteutettu.close();
					};
				}});
						
					
			//muussa tapauksessa sulje dialogi
			//$scope.luoKoulutusDialog.close();
		}

	};

	/**
	 * Jatka nappula enabloitu:
	 * -organisaatio valittu && koulutus valittu && valinta on validi, olettaa että vain yhden organisaation voi valita. 
	 */
	$scope.jatkaDisabled = function() {
		var jatkaEnabled= $scope.organisaatioValittu() && $scope.koulutustyyppiValidi(); 
		return !jatkaEnabled;
	};

	/**
	 * Tarkista että Koulutustyyppi valittu ja validi vrt valittu organisaatio
	 */
	$scope.koulutustyyppiValidi = function () {
		return $scope.sallitutKoulutustyypit.indexOf($scope.model.koulutustyyppi)!=-1;
	};
	/**
	 * Organisaatio valittu
	 */
	$scope.organisaatioValittu = function() {
		return $scope.model.organisaatiot.length>0;
	};
	
	/**
	 * Poista valittu organisaatio ruksista
	 */
	$scope.poistaValittu = function(organisaatio){
		var valitut = [];
		for(var i=0;i<$scope.model.organisaatiot.length;i++){
			if($scope.model.organisaatiot[i]!==organisaatio) {
				valitut.push($scope.model.organisaatiot[i]);
			}
		}
		$scope.model.organisaatiot = valitut;
	};
			
} ]);


