var app =  angular.module('app.kk.edit.hakukohde.ctrl');

app.controller('LiitteetListController',function($scope,$q, LocalisationService, OrganisaatioService ,Koodisto,Hakukohde,Liite, dialogService , HakuService, $modal ,Config,$location, TarjontaService) {

    $scope.model.liitteet = [];
    $scope.model.opetusKielet = [];
    
    $scope.model.selectedLiite = {};
    
    function getDefaultOsoite() {
    	return {
            osoiterivi1 : $scope.model.hakukohde.liitteidenToimitusOsoite.osoiterivi1,
            postinumero : $scope.model.hakukohde.liitteidenToimitusOsoite.postinumero,
            postitoimipaikka : $scope.model.hakukohde.liitteidenToimitusOsoite.postitoimipaikka
        };
    }
    
    function newLiite(lc) {
    	var tmennessa = 0;
    	for (var hn in $scope.model.hakus) {
    		for (var an in $scope.model.hakus[hn].hakuaikas) {
    			var ha = $scope.model.hakus[hn].hakuaikas[an];
    			if (ha.hakuaikaId == $scope.model.hakukohde.hakuaikaId) {
    				tmennessa = ha.loppuPvm;
    				break;
    			}
    		}
    	}
    	
    	return {
    		kieliUri: lc,
    		liitteenNimi: "",
    		liitteenKuvaus: {teksti: ""},
    		toimitettavaMennessa: tmennessa,
    		liitteenToimitusOsoite: getDefaultOsoite(),
    		muuOsoiteEnabled: false,
    		sahkoinenOsoiteEnabled: false,
    	};
    }
    
    if ($scope.model.hakukohde.oid !== undefined) {
    	var liitteetResource = Liite.get({hakukohdeOid: $scope.model.hakukohde.oid});
        var liitteetPromise = liitteetResource.$promise;

        liitteetPromise.then(function(liitteet){
            console.log('LIITTEET GOT: ',liitteet);
            $scope.model.liitteet = liitteet.result;
        });
    }
    
    var kielet = Koodisto.getAllKoodisWithKoodiUri('kieli',LocalisationService.getLocale());
    kielet.then(function(ret){
    	
    	//console.log("KIELET = ", ret);
    	for (var i in ret) {
    		var lc = ret[i].koodiUri;
    		var p = $scope.model.hakukohde.opetusKielet.indexOf(lc);
    		if (p!=-1) {
    			$scope.model.opetusKielet.push(ret[i]);
    			$scope.model.selectedLiite[lc] = newLiite(lc);
    		}
    	}
    	
    });
    
    $scope.getLiitteetByKieli = function(lc) {
    	var ret = [];
    	
    	for (var i in $scope.model.liitteet) {
    		var li = $scope.model.liitteet[i];
    		if (li.kieliUri == lc) {
    			ret.push(li);
    		}
    	}
    	
    	return ret;
    }
    
    // valitsee liitteen listasta editoitavaksi
    $scope.selectLiite = function(liite) {
    	console.log("select liite",liite);
    	
    	for (var i in $scope.model.liitteet) {
    		if ($scope.model.liitteet[i].kieliUri == liite.kieliUri) {
    			$scope.model.liitteet[i].selected = false;
    		}
    	}
    	
		liite.muuOsoiteEnabled = liite.liitteenToimitusOsoite != getDefaultOsoite();
		liite.sahkoinenOsoiteEnabled = liite.sahkoinenToimitusOsoite != null;
		
		liite.selected = true;
		/*if ($scope.model.selectedLiite[liite.kieliUri]) {
			$scope.model.selectedLiite[liite.kieliUri].selected = false;
		}*/
		
    	$scope.model.selectedLiite[liite.kieliUri] = angular.copy(liite);
    }
 
    // palauttaa oletusarvot
    $scope.resetLiite = function(kieliUri) {
    	if ($scope.model.selectedLiite[kieliUri].oid) {
    		// olemassaoleva
    		for (var i in $scope.model.liitteet) {
    			var cl = $scope.model.liitteet[i];
    			if (cl.oid==$scope.model.selectedLiite[kieliUri].oid) {
    	        	$scope.selectLiite(cl);
    	        	return;
    			}
    		}
    	} else {
    		// uusi
        	$scope.selectLiite(newLiite(kieliUri));
    	}
    }
    
    $scope.deleteLiite = function(liite) {
        liite.hakukohdeOid = $scope.model.hakukohde.oid;
        var liiteResource = new Liite(liite);
        liiteResource.$delete().then(function(){
            var index = $scope.model.liitteet.indexOf(liite);
            if (index==-1) {
            	return;
            }
            $scope.model.liitteet.splice(index,1);
        });
    };

    // tallentaa liitteen
    $scope.saveLiite = function(kieliUri) {
    	var ci = -1;
		for (var i in $scope.model.liitteet) {
			var cl = $scope.model.liitteet[i];
			if (cl.selected) {
				ci = i;
				break;			
			}
		}
    	
    	var liite = angular.copy($scope.model.selectedLiite[kieliUri]);
    	liite.selected = true;
    	if (ci!=-1) {
    		$scope.model.liitteet[ci] = liite;
    	} else {
    		ci = $scope.model.liitteet.length;
        	$scope.model.liitteet.push(liite);
    	}
    	
    	liite.hakukohdeOid = $scope.model.hakukohde.oid;
    	
    	var res  = new Liite(liite);
    	(liite.oid ? res.$update() : res.$save()).then(function(resp){
    		$scope.model.liitteet[ci] = resp;
    		$scope.selectLiite(resp);
    	});
    }
    

    // tosi, jos formi on valiidi
    $scope.canSaveLiite = function(kieliUri) {
        // validointi manuaalisesti; angularin formi ei toimi tässä tapauksessa
       	var liite = $scope.model.selectedLiite[kieliUri];
    	return notEmpty(liite.liitteenNimi)
    		&& notEmpty(liite.liitteenKuvaus.teksti)
    		&& (!liite.sahkoinenOsoiteEnabled || notEmpty(liite.sahkoinenToimitusOsoite))
    		&& (!liite.muuOsoiteEnabled || (liite.liitteenToimitusOsoite
    				&& notEmpty(liite.liitteenToimitusOsoite.osoiterivi1,
    						liite.liitteenToimitusOsoite.postinumero,
    						liite.liitteenToimitusOsoite.postitoimipaikka)));
    }

    // avaa uuden liitteen editoitavaksi
    $scope.createLiite = function(kieliUri) {
    	$scope.selectLiite(newLiite(kieliUri));
    }
    
    $scope.isUnsaved = function(liite) {
    	var cl;
		for (var i in $scope.model.liitteet) {
			cl = $scope.model.liitteet[i];
			if (cl.selected) {
				break;
			} else {
				cl=null;
			}
		}
		return !cl || !angular.equals(cl, liite);		
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
