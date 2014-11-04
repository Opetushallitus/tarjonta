var app =  angular.module('app.kk.edit.hakukohde.ctrl');

app.controller('LiitteetListController',function($scope,$q, LocalisationService, OrganisaatioService ,Koodisto,Hakukohde,Liite, dialogService , HakuService, $modal ,Config,$location, TarjontaService,HakukohdeService) {

    $scope.model = $scope.model||{};

	$scope.liitteetModel = {};
    $scope.liitteetModel.opetusKielet = [];
    $scope.liitteetModel.selectedTab = {};
    $scope.liitteetModel.langs = [];
    $scope.liitteetModel.selectedLangs = [];

    var initialTabSelected = false;
    var osoitteetReceived = false;

    function postProcessLiite(liite) {
		
    	if (liite.sahkoinenOsoiteEnabled === undefined) {
    		liite.sahkoinenOsoiteEnabled = liite.sahkoinenToimitusOsoite != null;
    	}
    	
    	if (liite.muuOsoiteEnabled === undefined && osoitteetReceived) {
    		if ($scope.model.liitteidenToimitusOsoite[liite.kieliUri]) {
    			var os1 = $scope.model.liitteidenToimitusOsoite[liite.kieliUri];
    			var os2 = liite.liitteenToimitusOsoite;
        		liite.muuOsoiteEnabled = (os1.osoiterivi1 != os2.osoiterivi1) || (os1.postinumero != os2.postinumero);
    		} else {
    			liite.muuOsoiteEnabled = true;
    		}
    		console.log("WTF LI", [ liite, angular.copy($scope.model.liitteidenToimitusOsoite), $scope.model.liitteidenToimitusOsoite[liite.kieliUri] ]);
    	}
    	
		return liite;
    }
    
    $scope.model.liitteenToimitusOsoitePromise.then(function(osoitteet) {
    	osoitteetReceived = true;
    });

    function containsOpetuskieli(lc) {
    	for (var i in $scope.liitteetModel.opetusKielet) {
    		if ($scope.liitteetModel.opetusKielet[i].koodiUri === lc) {
    			return true;
    		}
    	}
    	return false;
    }

    var kielet = Koodisto.getAllKoodisWithKoodiUri('kieli',LocalisationService.getLocale());
    kielet.then(function(ret){
    	
    	if (!$scope.model.hakukohde.opetusKielet) {
    		$scope.model.hakukohde.opetusKielet = [];
    	}

       	$scope.liitteetModel.langs = ret;
        updateLanguages();
    });

    function updateLanguages(){
        for (var i in $scope.model.hakukohde.hakukohteenLiitteet) {
            var li = $scope.model.hakukohde.hakukohteenLiitteet[i];
            if ($scope.liitteetModel.selectedLangs.indexOf(li.kieliUri) === -1) {
                $scope.liitteetModel.selectedLangs.push(li.kieliUri);
            }
        }

        $scope.liitteetModel.opetusKielet = [];

        for (var i in $scope.liitteetModel.langs) {
            var koodiUri = $scope.liitteetModel.langs[i].koodiUri;
            if($scope.model.hakukohde.opetusKielet.indexOf(koodiUri) !== -1 || $scope.liitteetModel.selectedLangs.indexOf(koodiUri) !== -1) {
                $scope.liitteetModel.opetusKielet.push($scope.liitteetModel.langs[i]);

                $scope.liitteetModel.selectedTab[koodiUri] = !initialTabSelected;
                initialTabSelected = true;
            }
        }
    }

    function doAfterLangSelection() {

    	// päivitä tabit ("opetuskielet")
    	// - poista poistuneet
    	for (var i in $scope.liitteetModel.opetusKielet) {
    		var k = $scope.liitteetModel.opetusKielet[i];
    		var si = $scope.liitteetModel.selectedLangs.indexOf(k.koodiUri);
    		if (si === -1) {
    			$scope.liitteetModel.opetusKielet.splice(i, 1);
                $scope.status.dirtify();
			}
    	}
    	
    	// - lisää lisätyt
    	for (var i in $scope.liitteetModel.selectedLangs) {
    		var lc = $scope.liitteetModel.selectedLangs[i];
    		
    		if (containsOpetuskieli(lc)) {
    			continue;
    		}
    		
    		for (var j in $scope.liitteetModel.langs) {
    			if ($scope.liitteetModel.langs[j].koodiUri == lc) {
    				$scope.liitteetModel.opetusKielet.push($scope.liitteetModel.langs[j]);
    	            $scope.status.dirtify();
    				break;
    			}
    		}
    	}
    }
    
    $scope.onLangSelection = function() {
    	
    	var dellangs = [];

    	// käy läpi poistetut kielet
    	for (var i in $scope.model.hakukohde.hakukohteenLiitteet) {
        	var li = $scope.model.hakukohde.hakukohteenLiitteet[i];
        	if ($scope.liitteetModel.selectedLangs.indexOf(li.kieliUri)==-1) {
        		// kieli, jolla liitteitä, poistettu
        		if (dellangs.indexOf(li.kieliUri)==-1) {
            		dellangs.push(li.kieliUri);
        		}
        	}
        }
    	
    	// varmista kielien poisto
    	if (dellangs.length>0) {
    		for (var i in dellangs) {
    			var lang = dellangs[i];
        		dialogService.showDialog({
        			title: LocalisationService.t("tarjonta.poistovahvistus.hakukohde.liitteet.title"),
        			description: LocalisationService.t("tarjonta.poistovahvistus.hakukohde.liitteet", [lang])
        		}).result.then(function(ret){
        			if (ret) {
        				// poista kaikki valitunkieliset liitteet
        		    	for (var i in $scope.model.hakukohde.hakukohteenLiitteet) {
        		        	var li = $scope.model.hakukohde.hakukohteenLiitteet[i];
        		        	if (li.kieliUri == lang) {
        		        		$scope.deleteLiite(li, true);
        		        		//$scope.model.hakukohde.hakukohteenLiitteet.splice(i, 1);
        		        	}
        		        }
        			} else {
        				// ei poistoa -> palauta selectediin
        				$scope.liitteetModel.selectedLangs.push(lang);
    				}
        			doAfterLangSelection();
        		});
    		}
    	}
		doAfterLangSelection();
    }
    
    $scope.resetOsoite = function(lc, liite){
    	liite.liitteenToimitusOsoite = angular.copy($scope.model.liitteidenToimitusOsoite[lc])
    }
    
    $scope.getLiitteetByKieli = function(lc) {
    	var ret = [];    	
    	for (var i in $scope.model.hakukohde.hakukohteenLiitteet) {
    		var li = $scope.model.hakukohde.hakukohteenLiitteet[i];    		
    		if (li.kieliUri == lc) {
    			ret.push(postProcessLiite(li));
    		}
    	}    	
    	return ret;
    }
    
    $scope.deleteLiite = function(liite, confirm) {
    	if (confirm) {
    		var index = $scope.model.hakukohde.hakukohteenLiitteet.indexOf(liite);
            liite.hakukohdeOid = $scope.model.hakukohde.oid;
            $scope.model.hakukohde.hakukohteenLiitteet.splice(index,1);
            $scope.status.dirtify();
    	} else {
    		dialogService.showDialog({
    			title: LocalisationService.t("tarjonta.poistovahvistus.hakukohde.liite.title"),
    			description: LocalisationService.t("tarjonta.poistovahvistus.hakukohde.liite", [liite.liitteenNimi])
    		}).result.then(function(ret){
    			if (ret) {
    				$scope.deleteLiite(liite, true);
    			}
    		});
     	}
    };

    $scope.createLiite = function(kieliUri, dirtify) {
    	HakukohdeService.addLiite($scope.model.hakukohde, kieliUri,  $scope.model.liitteidenToimitusOsoite[kieliUri]);
        if(dirtify!==false){
            $scope.status.dirtify();
        }
    }
    
    $scope.isValidToimitusOsoite = function(liite) {
    	var r= !liite.muuOsoiteEnabled
    		|| notEmpty([liite.liitteenToimitusOsoite.osoiterivi1,
    	                    liite.liitteenToimitusOsoite.postinumero]);
        return r;

    }
    
    $scope.isValidSahkoinenOsoite = function(liite) {
    	var r = !liite.sahkoinenOsoiteEnabled
    	|| notEmpty(liite.sahkoinenToimitusOsoite);

        return r;
    }

    // kutsutaan parentista
    $scope.status.validateLiitteet = function() {

    	for (var i in $scope.model.hakukohde.hakukohteenLiitteet) {
    		var li = $scope.model.hakukohde.hakukohteenLiitteet[i];

    		if (!notEmpty([li.liitteenNimi, li.toimitettavaMennessa])
    				|| !$scope.isValidSahkoinenOsoite(li)
    				|| !$scope.isValidToimitusOsoite(li)) {

                console.log("not valid!");
    			return false;
    		}
    	}
        return true;
    }

    function notEmpty(v) {
        if (v instanceof Array) {
            for (var i in v) {
    			if (!notEmpty(v[i])) {
    				return false;
    			}
    		}
    		return true;
    	} else {
    		return v && (""+v).trim().length>0;
    	}
    }
});
