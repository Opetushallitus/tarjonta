var app =  angular.module('app.kk.edit.hakukohde.ctrl');

app.controller('LiitteetListController',function($scope,$q, LocalisationService, OrganisaatioService ,Koodisto,Hakukohde,Liite, dialogService , HakuService, $modal ,Config,$location, TarjontaService) {

	$scope.liitteetModel = {};

    $scope.liitteetModel.opetusKielet = [];
    
    $scope.liitteetModel.selectedLiite = {};

    var initialTabSelected = false;
    $scope.liitteetModel.selectedTab = {};
    
    $scope.liitteetModel.langs = [];
    $scope.liitteetModel.selectedLangs = [];

    $scope.model.liitteenToimitusOsoitePromise.then(function(osoitteet) {

        console.log('LIITTEIDEN TOIMITUSOSOITTEET : ' , osoitteet);
        for(var osoiteLang in osoitteet) {

            if ($scope.liitteetModel.selectedLiite[osoiteLang] !== undefined) {
                $scope.liitteetModel.selectedLiite[osoiteLang].liitteenToimitusOsoite = osoitteet[osoiteLang];
            }


        }

        $scope.liitteetModel.selectedLiite[$scope.model.defaultLang].liitteenToimitusOsoite = osoitteet[$scope.model.defaultLang];



    });


    function getEmptyOsoite() {
        return {
            osoiterivi1 : "",
            postinumero : "",
            postitoimipaikka : ""
        };
    }

    function checkForOsoite(kieliUri) {

       if ($scope.model.liitteidenToimitusOsoite[kieliUri] !== undefined) {
           return true;
       } else {
           return false;
       }

    }

    function getDefaultOsoite(kieliUri) {
    	if (checkForOsoite(kieliUri)) {

            return {


                osoiterivi1 : $scope.model.liitteidenToimitusOsoite[kieliUri].osoiterivi1,
                postinumero : $scope.model.liitteidenToimitusOsoite[kieliUri].postinumero,
                postitoimipaikka : $scope.model.liitteidenToimitusOsoite[kieliUri].postitoimipaikka

                //osoiterivi1 : $scope.model.hakukohde.liitteidenToimitusOsoite.osoiterivi1,
                //postinumero : $scope.model.hakukohde.liitteidenToimitusOsoite.postinumero,
                //postitoimipaikka : $scope.model.hakukohde.liitteidenToimitusOsoite.postitoimipaikka
            };

        } else {



           if ($scope.model.liitteidenToimitusOsoite[$scope.model.defaultLang] !== undefined) {

               return {



                   osoiterivi1 : $scope.model.liitteidenToimitusOsoite[$scope.model.defaultLang].osoiterivi1,
                   postinumero : $scope.model.liitteidenToimitusOsoite[$scope.model.defaultLang].postinumero,
                   postitoimipaikka : $scope.model.liitteidenToimitusOsoite[$scope.model.defaultLang].postitoimipaikka

                   //osoiterivi1 : $scope.model.hakukohde.liitteidenToimitusOsoite.osoiterivi1,
                   //postinumero : $scope.model.hakukohde.liitteidenToimitusOsoite.postinumero,
                   //postitoimipaikka : $scope.model.hakukohde.liitteidenToimitusOsoite.postitoimipaikka
               };

           } else {
               return {};
           }


        }


    }


    function newLiite(lc) {
    	var tmennessa = 0;
    	for (var hn in $scope.liitteetModel.hakus) {
    		for (var an in $scope.liitteetModel.hakus[hn].hakuaikas) {
    			var ha = $scope.liitteetModel.hakus[hn].hakuaikas[an];
    			if (ha.hakuaikaId == $scope.model.hakukohde.hakuaikaId) {
    				tmennessa = ha.loppuPvm;
    				break;
    			}
    		}
    	}
    	
    	var kv = {};
    	kv[lc] = "";
    	
    	return {
    		hakukohdeOid:$scope.model.hakukohde.oid,
    		kieliUri: lc,
    		liitteenNimi: "",
    		liitteenKuvaukset:kv,
    		toimitettavaMennessa: null, //tmennessa,
    		liitteenToimitusOsoite: getDefaultOsoite(lc),
    		muuOsoiteEnabled: false,
    		sahkoinenOsoiteEnabled: false
    	};
    }
    
    /*if ($scope.model.hakukohde.oid !== undefined) {
    	var liitteetResource = Liite.get({hakukohdeOid: $scope.model.hakukohde.oid});
        var liitteetPromise = liitteetResource.$promise;

        liitteetPromise.then(function(liitteet){
            //console.log('LIITTEET GOT: ',liitteet);
            $scope.model.hakukohde.hakukohteenLiitteet = liitteet.result;
        });
    }*/

    for (var i in $scope.model.hakukohde.hakukohteenLiitteet) {
    	var li = $scope.model.hakukohde.hakukohteenLiitteet[i];
       	if ($scope.liitteetModel.selectedLangs.indexOf(li.kieliUri)==-1) {
    		$scope.liitteetModel.selectedLangs.push(li.kieliUri);
    	}
    }

    function containsOpetuskieli(lc) {
    	for (var i in $scope.liitteetModel.opetusKielet) {
    		if ($scope.liitteetModel.opetusKielet[i].koodiUri==lc) {
    			return true;
    		}
    	}
    	return false;
    }
    
    var kielet = Koodisto.getAllKoodisWithKoodiUri('kieli',LocalisationService.getLocale());
    kielet.then(function(ret){
    	
    	//console.log("KIELET = ", ret);
       	
       	$scope.liitteetModel.langs = ret;
    	for (var i in ret) {
    		var lc = ret[i].koodiUri;
    		var p = $scope.model.hakukohde.opetusKielet.indexOf(lc);
    		if (p!=-1) {

    			$scope.liitteetModel.selectedLiite[lc] = newLiite(lc);
    			$scope.liitteetModel.selectedTab[lc] = !initialTabSelected;
   				initialTabSelected = true;

    			if (!containsOpetuskieli(lc)) {
        			$scope.liitteetModel.opetusKielet.push(ret[i]);
    			}
    			
   				if ($scope.liitteetModel.selectedLangs.indexOf(lc)==-1) {
   					$scope.liitteetModel.selectedLangs.push(lc);
   				}
    		}
    	}
    	
    });
    
    $scope.onLangSelection = function() {
    	for (var i in $scope.model.hakukohde.hakukohteenLiitteet) {
        	var li = $scope.model.hakukohde.hakukohteenLiitteet[i];
        	if ($scope.liitteetModel.selectedLangs.indexOf(li.kieliUri)==-1) {
        		$scope.liitteetModel.selectedLangs.push(li.kieliUri);
        	}
        }
    	
    	for (var i in $scope.liitteetModel.opetusKielet) {
    		var k = $scope.liitteetModel.opetusKielet[i];
    		var si = $scope.liitteetModel.selectedLangs.indexOf(k.koodiUri);
    		if (si==-1) {
    			$scope.liitteetModel.opetusKielet.splice(i,1);
    			$scope.liitteetModel.selectedLiite[k.koodiUri] = undefined;
			}
    	}
    	
    	//console.log("OKS WAS ", $scope.liitteetModel.opetusKielet);
    	
    	for (var i in $scope.liitteetModel.selectedLangs) {
    		var lc = $scope.liitteetModel.selectedLangs[i];
    		if (!$scope.liitteetModel.selectedLiite[lc]) {
        		$scope.liitteetModel.selectedLiite[lc] = newLiite(lc);
        		
        		for (var j in $scope.liitteetModel.langs) {
            		
        			var kieliSelected = false;
            		/*for (var k in $scope.liitteetModel.opetusKielet) {
                		var ok = $scope.liitteetModel.opetusKielet[k];
                		if (ok.kieliUri == lc) {
                			kieliSelected = true;
                			break;
                		}
                	}*/
            		
        			if ($scope.liitteetModel.langs[j].koodiUri == lc && !containsOpetuskieli(lc)) {
        				$scope.liitteetModel.opetusKielet.push($scope.liitteetModel.langs[j]);
        				break;
        			}
        		}
    		}
    	}

    	//console.log("OKS IS ", $scope.liitteetModel.opetusKielet);

    }
    
    $scope.getLiitteetByKieli = function(lc) {
    	var ret = [];
    	
    	for (var i in $scope.model.hakukohde.hakukohteenLiitteet) {
    		var li = $scope.model.hakukohde.hakukohteenLiitteet[i];
    		if (li.kieliUri == lc) {
    			ret.push(li);
    		}
    	}
    	
    	return ret;
    }
    
    // valitsee liitteen listasta editoitavaksi
    $scope.selectLiite = function(liite) {
    	//console.log("select liite",liite);
    	
    	for (var i in $scope.model.hakukohde.hakukohteenLiitteet) {
    		if ($scope.model.hakukohde.hakukohteenLiitteet[i].kieliUri == liite.kieliUri) {
    			$scope.model.hakukohde.hakukohteenLiitteet[i].selected = false;
    		}
    	}
    	
		liite.muuOsoiteEnabled = liite.liitteenToimitusOsoite != getDefaultOsoite(liite.kieliUri);
		liite.sahkoinenOsoiteEnabled = liite.sahkoinenToimitusOsoite != null;
		
		liite.selected = true;
    	$scope.liitteetModel.selectedLiite[liite.kieliUri] = angular.copy(liite);
    }
 
    // palauttaa oletusarvot
    $scope.resetLiite = function(kieliUri) {
    	$scope.selectLiite(newLiite(kieliUri));
    	$scope.liitteetModel.selectedLiite[kieliUri].selected = false;
    }
    
    $scope.deleteLiite = function(liite, confirm) {
    	if (confirm) {
    		var index = $scope.model.hakukohde.hakukohteenLiitteet.indexOf(liite);
            liite.hakukohdeOid = $scope.model.hakukohde.oid;
            $scope.model.hakukohde.hakukohteenLiitteet.splice(index,1);
            $scope.status.dirty = true;
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

    // tallentaa liitteen
    $scope.saveLiite = function(kieliUri) {

        if ($scope.canSaveLiite(kieliUri)) {
            var ci = -1;
            for (var i in $scope.model.hakukohde.hakukohteenLiitteet) {
                var cl = $scope.model.hakukohde.hakukohteenLiitteet[i];
                if (cl.selected) {
                    ci = i;
                    break;
                }
            }

            var liite = angular.copy($scope.liitteetModel.selectedLiite[kieliUri]);
            liite.selected = true;
            if (ci != -1) {
                $scope.model.hakukohde.hakukohteenLiitteet[ci] = liite;
            } else {
                ci = $scope.model.hakukohde.hakukohteenLiitteet.length;
                $scope.model.hakukohde.hakukohteenLiitteet.push(liite);
            }

            liite.hakukohdeOid = $scope.model.hakukohde.oid;
            $scope.createLiite(kieliUri);
            $scope.status.dirty = true;
        }
    }
    

    // tosi, jos formi on valiidi
    $scope.canSaveLiite = function(kieliUri) {
        // validointi manuaalisesti; angularin formi ei toimi tässä tapauksessa
       	var liite = $scope.liitteetModel.selectedLiite[kieliUri];
    	return liite && notEmpty(liite.liitteenNimi)
    		&& notEmpty(liite.liitteenKuvaukset[kieliUri])
    		&& liite.toimitettavaMennessa!=null
    		&& (!liite.sahkoinenOsoiteEnabled || notEmpty(liite.sahkoinenToimitusOsoite))
    		//&& (!liite.muuOsoiteEnabled || );
            && (liite.liitteenToimitusOsoite
                && notEmpty([liite.liitteenToimitusOsoite.osoiterivi1,
                    liite.liitteenToimitusOsoite.postinumero]))
    }

    // avaa uuden liitteen editoitavaksi
    $scope.createLiite = function(kieliUri) {
    	$scope.selectLiite(newLiite(kieliUri));
    }

    $scope.setDefaultAddress = function(kieliUri) {
        $scope.liitteetModel.selectedLiite[kieliUri].liitteenToimitusOsoite = getDefaultOsoite(kieliUri);
    }

    $scope.emptyAddress = function(kieliUri) {

        $scope.liitteetModel.selectedLiite[kieliUri].liitteenToimitusOsoite = getEmptyOsoite();

    }

    $scope.isUnsaved = function(liite) {
    	var cl;
		for (var i in $scope.model.hakukohde.hakukohteenLiitteet) {
			cl = $scope.model.hakukohde.hakukohteenLiitteet[i];
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
