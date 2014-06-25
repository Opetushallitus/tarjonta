var app =  angular.module('app.kk.edit.hakukohde.ctrl')

app.controller('ValintakokeetController', function($scope,$q, $filter, LocalisationService, OrganisaatioService ,Koodisto,Hakukohde,Valintakoe,dialogService, HakuService, $modal ,Config,$location) {

	$scope.kokeetModel = {};

	$scope.kokeetModel.opetusKielet = [];
	//$scope.model.hakukohde.valintakokeet = [];
	   
	$scope.kokeetModel.langs = [];
	$scope.kokeetModel.selectedLangs = [];

    var initialTabSelected = false;
    $scope.kokeetModel.selectedTab = {};

    function newAjankohta() {
    	return {
    		lisatiedot: "",
    		alkaa: null,
    		loppuu: null,
    		osoite: {
    			osoiterivi1: "",
    			postinumero: "",
    			postitoimipaikka: "",
    			postinumeroArvo: ""    			
    		}
    	}
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

    var kielet = Koodisto.getAllKoodisWithKoodiUri('kieli',LocalisationService.getLocale());
    kielet.then(function(ret){
    	
    	if (!$scope.model.hakukohde.opetusKielet) {
    		$scope.model.hakukohde.opetusKielet = [];
    	}
    	    	
    	//console.log("KIELET = ", ret);
    	$scope.kokeetModel.langs = ret;
    	for (var i in ret) {
    		var lc = ret[i].koodiUri;
    		var p = $scope.model.hakukohde.opetusKielet.indexOf(lc);
    		if (p!=-1) {
    			$scope.kokeetModel.opetusKielet.push(ret[i]);
    			//$scope.kokeetModel.selectedAjankohta[lc] = newAjankohta();
    			$scope.kokeetModel.selectedTab[lc] = !initialTabSelected;
   				initialTabSelected = true;
   				
   				if ($scope.kokeetModel.selectedLangs.indexOf(lc)==-1) {
   					$scope.kokeetModel.selectedLangs.push(lc);
   				}
    		}
    	}
    	
    });

	for (var i in $scope.model.hakukohde.valintakokeet) {
		var vk = $scope.model.hakukohde.valintakokeet[i];
		vk.selectedAjankohta = newAjankohta();
		if ($scope.kokeetModel.selectedLangs.indexOf(vk.kieliUri)==-1) {
				$scope.kokeetModel.selectedLangs.push(vk.kieliUri);
			}
	}
	
	$scope.isValidAjankohta = function(ajankohta) {
    	return notEmpty([ajankohta.alkaa, ajankohta.loppuu, ajankohta.osoite.osoiterivi1, ajankohta.osoite.postinumero]);
	}
	
    // kutsutaan parentista
    $scope.status.validateValintakokeet = function() {
    	for (var i in $scope.model.hakukohde.valintakokeet) {
    		var li = $scope.model.hakukohde.valintakokeet[i];
    		if (!notEmpty(li.valintakoeNimi)) {
    			return false;
    		}
    		if (!notEmpty($("<div>"+li.valintakokeenKuvaus.teksti+"</div>").text().trim())) {
    			return false;
    		}
    		for (var j in li.valintakoeAjankohtas) {
    			if (!$scope.isValidAjankohta(li.valintakoeAjankohtas[j])) {
    				return false;
    			}
    		}
    	}
    	return true;
    }

	$scope.addAjankohta = function(valintakoe) {
		valintakoe.valintakoeAjankohtas.push(newAjankohta());
        $scope.status.dirtify();
	}
    
    $scope.deleteAjankohta = function(valintakoe, ajankohta, confirm) {
    	if (!ajankohta.alkaa && !ajankohta.loppuu && !ajankohta.osoite.osoiterivi1 && !ajankohta.osoite.postinumero) {
    		confirm = true;
    	}
    	if (confirm) {
        	if (ajankohta == valintakoe.selectedAjankohta) {
        		valintakoe.selectedAjankohta = newAjankohta();
        	}
        	var p = valintakoe.valintakoeAjankohtas.indexOf(ajankohta);
        	if (p!=-1) {
        		valintakoe.valintakoeAjankohtas.splice(p, 1);
        	}
            $scope.status.dirtify();
    	} else {
    		
    		dialogService.showDialog({
    			title: LocalisationService.t("tarjonta.poistovahvistus.hakukohde.valintakoe.ajankohta.title"),
    			description: LocalisationService.t("tarjonta.poistovahvistus.hakukohde.valintakoe.ajankohta",
    					[valintakoe.valintakoeNimi,
    					 $filter("date")(ajankohta.alkaa, "d.M.yyyy H:mm") || "?",
    					 $filter("date")(ajankohta.loppuu, "d.M.yyyy H:mm") || "?"])
    		}).result.then(function(ret){
    			if (ret) {
    				$scope.deleteAjankohta(valintakoe, ajankohta, true);
    			}
    		});
    	}
    }
    
    $scope.deleteValintakoe = function(valintakoe, confirm) {
    	if (confirm) {
        	var p = $scope.model.hakukohde.valintakokeet.indexOf(valintakoe);
        	if (p!=-1) {
                $scope.status.dirty = true;
        		$scope.model.hakukohde.valintakokeet.splice(p, 1);
        	}
            $scope.status.dirtify();
    	} else {
    		dialogService.showDialog({
    			title: LocalisationService.t("tarjonta.poistovahvistus.hakukohde.valintakoe.title"),
    			description: LocalisationService.t("tarjonta.poistovahvistus.hakukohde.valintakoe", [valintakoe.valintakoeNimi])
    		}).result.then(function(ret){
    			if (ret) {
    				$scope.deleteValintakoe(valintakoe, true);
    			}
    		});
    	}
    }
    
    $scope.addValintakoe = function(lc) {
        $scope.status.dirtify();
    	
    	var vk = {
        		hakukohdeOid:$scope.model.hakukohde.oid,
        		kieliUri:lc,
        		valintakoeNimi:"",
        		valintakokeenKuvaus: {uri: lc, teksti: ""},
        		valintakoeAjankohtas: []
        	};
    	$scope.model.hakukohde.valintakokeet.push(vk);
    	return vk;
    }

    $scope.getValintakokeetByKieli = function(lc) {
    	var ret = [];
    	
    	for (var i in $scope.model.hakukohde.valintakokeet) {
    		var li = $scope.model.hakukohde.valintakokeet[i];
    		if (li.kieliUri == lc) {
    			ret.push(li);
    		}
    	}

    	return ret;
    }
    function containsOpetuskieli(lc) {
    	for (var i in $scope.kokeetModel.opetusKielet) {
    		if ($scope.kokeetModel.opetusKielet[i].koodiUri==lc) {
    			return true;
    		}
    	}
    	return false;
    }
    
    $scope.onLangSelection = function() {
    	for (var i in $scope.kokeetModel.liitteet) {
        	var li = $scope.kokeetModel.liitteet[i];
        	if ($scope.kokeetModel.selectedLangs.indexOf(li.kieliUri)==-1) {
        		$scope.kokeetModel.selectedLangs.push(li.kieliUri);
        	}
        }
    	
    	for (var i in $scope.kokeetModel.opetusKielet) {
    		var k = $scope.kokeetModel.opetusKielet[i];
    		var si = $scope.kokeetModel.selectedLangs.indexOf(k.koodiUri);
    		if (si==-1) {
    			$scope.kokeetModel.opetusKielet.splice(i,1);
    			for (var j in $scope.model.hakukohde.valintakokeet) {
    				var vk = $scope.model.hakukohde.valintakokeet[j];
    				if (vk.kieliUri==k.koodiUri) {
    					$scope.model.hakukohde.valintakokeet.splice(j, 1);
    				}
    			}
			}
    	}
    	
    	for (var i in $scope.kokeetModel.selectedLangs) {
    		var lc = $scope.kokeetModel.selectedLangs[i];
    		if (!containsOpetuskieli(lc)) {
        		for (var j in $scope.kokeetModel.langs) {
        			if ($scope.kokeetModel.langs[j].koodiUri == lc) {
        				$scope.kokeetModel.opetusKielet.push($scope.kokeetModel.langs[j]);
        				break;
        			}
        		}
    		}
    	}

    }
    
});
