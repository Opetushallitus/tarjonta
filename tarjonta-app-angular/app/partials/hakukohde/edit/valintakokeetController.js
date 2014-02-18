var app =  angular.module('app.kk.edit.hakukohde.ctrl')

app.controller('ValintakokeetController', function($scope,$q, LocalisationService, OrganisaatioService ,Koodisto,Hakukohde,Valintakoe,dialogService, HakuService, $modal ,Config,$location) {

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
    

    /*if ($scope.model.hakukohde.oid !== undefined) {

        var valintaKokeetResource = Valintakoe.getAll({ hakukohdeOid : $scope.model.hakukohde.oid });
        
        valintaKokeetResource.$promise.then(function(valintakokeet){
            console.log('GOT VALINTAKOE: ' , valintakokeet.result);

        	for (var i in valintakokeet.result) {
        		var vk = valintakokeet.result[i];
        		vk.selectedAjankohta = newAjankohta();
        		$scope.model.hakukohde.valintakokeet.push(vk);
        		if ($scope.kokeetModel.selectedLangs.indexOf(vk.kieliUri)==-1) {
   					$scope.kokeetModel.selectedLangs.push(vk.kieliUri);
   				}
        	}
            
        });
        
    }*/

	for (var i in $scope.model.hakukohde.valintakokeet) {
		var vk = $scope.model.hakukohde.valintakokeet[i];
		vk.selectedAjankohta = newAjankohta();
		if ($scope.kokeetModel.selectedLangs.indexOf(vk.kieliUri)==-1) {
				$scope.kokeetModel.selectedLangs.push(vk.kieliUri);
			}
	}
	
    $scope.saveAjankohta = function(valintakoe) {
    	var ajankohta = valintakoe.selectedAjankohta;
    	ajankohta.selected = undefined;

    	var unsaved = true;
		for (var i in valintakoe.valintakoeAjankohtas) {
			if (valintakoe.valintakoeAjankohtas[i].selected) {
				valintakoe.valintakoeAjankohtas[i] = ajankohta;
				unsaved = false;
				break;
			}
		}

    	if (unsaved) {
    		valintakoe.valintakoeAjankohtas.push(ajankohta);
    	}
    	
    	$scope.resetAjankohta(valintakoe);
    }
    
    $scope.canSaveAjankohta = function(valintakoe) {
    	var ajankohta = valintakoe.selectedAjankohta;
    	return ajankohta && notEmpty([ajankohta.alkaa, ajankohta.loppuu, ajankohta.osoite.osoiterivi1, ajankohta.osoite.postinumero]);
    }
    
    $scope.resetAjankohta = function(valintakoe) {
    	$scope.selectAjankohta(valintakoe, newAjankohta());
    	valintakoe.selectedAjankohta.selected = false;
    }
    
    $scope.deleteAjankohta = function(valintakoe, ajankohta) {
    	if (ajankohta == valintakoe.selectedAjankohta) {
    		valintakoe.selectedAjankohta = newAjankohta();
    	}
    	var p = valintakoe.valintakoeAjankohtas.indexOf(ajankohta);
    	if (p!=-1) {
    		valintakoe.valintakoeAjankohtas.splice(p, 1);
    	}
    }
    
    $scope.selectAjankohta = function(valintakoe, ajankohta) {
		for (var j in valintakoe.valintakoeAjankohtas) {
			valintakoe.valintakoeAjankohtas[j].selected = false;
		}

    	ajankohta.selected = true;
		valintakoe.selectedAjankohta = angular.copy(ajankohta);
    }

    $scope.addValintakoe = function(lc) {
    	for (var i in $scope.model.hakukohde.valintakokeet) {
    		if ($scope.model.hakukohde.valintakokeet[i].kieliUri==lc && !$scope.model.hakukohde.valintakokeet[i].oid) {
    			return;
    		}
    	}
    	$scope.model.hakukohde.valintakokeet.push({
    		hakukohdeOid:$scope.model.hakukohde.oid,
    		kieliUri:lc,
    		valintakoeNimi:"",
    		valintakokeenKuvaus: {uri: lc, teksti: ""},
    		valintakoeAjankohtas: []
    	});
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
    			//$scope.kokeetModel.selectedAjankohta[lc] = undefined;
			}
    	}
    	
    	//console.log("OKS WAS ", $scope.kokeetModel.opetusKielet);
    	
    	for (var i in $scope.kokeetModel.selectedLangs) {
    		var lc = $scope.kokeetModel.selectedLangs[i];
    		if (!containsOpetuskieli(lc)) {
        		for (var j in $scope.kokeetModel.langs) {
        			if ($scope.kokeetModel.langs[j].koodiUri == lc) {
        				$scope.kokeetModel.opetusKielet.push($scope.kokeetModel.langs[j]);
            			//$scope.kokeetModel.selectedAjankohta[lc] = newAjankohta();
        				break;
        			}
        		}
    		}
    	}

    	//console.log("OKS IS ", $scope.kokeetModel.opetusKielet);

    }
    
    
/*
   var kieliSet = new buckets.Set();




   $scope.model.validationmsgs = [];

   if ($scope.model.hakukohde.oid !== undefined) {

       var valintaKokeetResource = Valintakoe.getAll({ hakukohdeOid : $scope.model.hakukohde.oid });


       var valintaKokeetPromise  = valintaKokeetResource.$promise;
       valintaKokeetPromise.then(function(valintakokees){

           angular.forEach(valintakokees.result,function(valintakoe){
               console.log('GOT VALINTAKOE: ' , valintakoe);
               addValintakoeToList(valintakoe);
           });




       });

   }



  var addValintakoeToList = function(valintakoe) {
      if (valintakoe !== undefined) {
          checkForExistingValintaKoe(valintakoe);
          kieliSet.add(valintakoe.kieliNimi);
          $scope.model.valintakokees.push(valintakoe);

      }
      $scope.model.kielet = kieliSet.toArray();
  };

  var checkForExistingValintaKoe = function(valintakoe) {
      var foundValintakoe;
      angular.forEach($scope.model.valintakokees,function(loopValintakoe){

          if (loopValintakoe.oid === valintakoe.oid) {
             foundValintakoe = loopValintakoe;
          }

      });

      if (foundValintakoe !== undefined) {
          var index = $scope.model.valintakokees.indexOf(foundValintakoe);
          $scope.model.valintakokees.splice(index,1);
      }
  };

  var addToValintakokees = function(valintakoe) {

      var valintakoeFound = false;
      var foundValintakoe = undefined;
      angular.forEach($scope.model.valintakokees,function(loopValintakoe){
           if (loopValintakoe.oid === valintakoe.oid) {
               valintakoeFound = true;
               foundValintakoe = valintakoeFound;
           }
      });

      if (!valintakoeFound) {
          $scope.model.valintakokees.push(valintakoe);
      } else {
          var index  = $scope.model.valintakokees.indexOf(foundValintakoe);
          $scope.model.valintakokees.splice(index,1);
          $scope.model.valintakokees.push(valintakoe);

      }


  };

    $scope.model.poistaValintakoe = function(valintakoe) {

        var texts = {
            title: LocalisationService.t("hakukohde.valintakokeet.list.remove.title"),
            description: LocalisationService.t("hakukohde.valintakokeet.list.remove.desc"),
            ok: LocalisationService.t("ok"),
            cancel: LocalisationService.t("cancel")
        };

        var d = dialogService.showDialog(texts);

        d.result.then(function(data){
            if ("ACTION" === data) {
                var index =  $scope.model.valintakokees.indexOf(valintakoe);
                $scope.model.valintakokees.splice(index,1);
                valintakoe.hakukohdeOid = $scope.model.hakukohde.oid;
                valintakoe.valintakoeOid = valintakoe.oid;
                console.log('REMOVING VALINTAKOE :',valintakoe);
                var valintakoeResource = new Valintakoe(valintakoe);
                valintakoeResource.$delete();
            }
        });

       / * var index =  $scope.model.valintakokees.indexOf(valintakoe);
        $scope.model.valintakokees.splice(index,1);
        valintakoe.hakukohdeOid = $scope.model.hakukohde.oid;
        valintakoe.valintakoeOid = valintakoe.oid;
        console.log('REMOVING VALINTAKOE :',valintakoe);
        var valintakoeResource = new Valintakoe(valintakoe);
        valintakoeResource.$delete();
        * /
    };


   $scope.model.muokaaValintakoetta = function(valintakoe) {

       var modalInstance = $modal.open({
           templateUrl: 'partials/hakukohde/edit/valintakoeModal.html',
           controller: 'ValintakoeModalInstanceController',
           windowClass: 'valintakoe-modal',
           resolve: {
               valintakoe: function () {
                   return valintakoe;
               }
           }
       });

       modalInstance.result.then(function (selectedItem) {

              selectedItem.hakukohdeOid =  $scope.model.hakukohde.oid;
              console.log('SELECTED VALINTAKOE : ', selectedItem);
              var valintakoeResource = new Valintakoe(selectedItem);
           if (selectedItem.oid === undefined) {
              var returnResource = valintakoeResource.$save();
              returnResource.then(function(valintakoe){

                  addValintakoeToList(valintakoe.result);



              });
           } else {
               var returnResource =  valintakoeResource.$update();
               returnResource.then(function(valintakoe) {
                   addValintakoeToList(valintakoe.result);

               });
           }


       }, function () {
           $log.info('Modal dismissed at: ' + new Date());
       });
   };


   $scope.model.newValintakoe = function(){

       $scope.model.muokaaValintakoetta();

   };
*/
});

/*
 *
 *
 * Valintakoe modal controller
 *
 *
 */
/*
app.controller('ValintakoeModalInstanceController', function($scope, $modalInstance,LocalisationService,Koodisto,valintakoe) {

    $scope.model = {};

    $scope.model.validationmsgs = [];

    $scope.model.showAlert = false;

    $scope.model.selectedAjankohta = {
        osoite : {}
    };


    var selectedKieli = undefined;

    //Koodisto helper methods
    var findKoodiWithArvo = function(koodi,koodis)  {


        console.log('Trying to find with : ',koodi);
        console.log('From :', koodis.length);
        var foundKoodi;

        angular.forEach(koodis,function(koodiLoop){
            if (koodiLoop.koodiArvo === koodi){
                foundKoodi = koodiLoop;
            }
        });


        return foundKoodi;
    };

    var findKoodiWithUri = function(koodi,koodis)  {


        var foundKoodi;

        angular.forEach(koodis,function(koodiLoop){
            if (koodiLoop.koodiUri === koodi){
                foundKoodi = koodiLoop;
            }
        });


        return foundKoodi;
    };

    var koodistoPromise = Koodisto.getAllKoodisWithKoodiUri('posti','FI');

    koodistoPromise.then(function(koodisParam){
        $scope.model.koodis = koodisParam;

        if ($scope.model.selectedAjankohta.osoite.postinumero !== undefined) {

            var koodi =  findKoodiWithUri(postinumero,$scope.model.koodis);

            $scope.model.postinumeroarvo.arvo = koodi.koodiArvo;
        }
    });

    $scope.model.onKieliTypeAheadChange = function() {
        var koodi = findKoodiWithArvo($scope.model.selectedAjankohta.osoite.postinumeroArvo,$scope.model.koodis);

        $scope.model.selectedAjankohta.osoite.postinumeroArvo = koodi.koodiArvo;
        $scope.model.selectedAjankohta.osoite.postinumero = koodi.koodiUri;
        $scope.model.selectedAjankohta.osoite.postitoimipaikka = koodi.koodiNimi;

    };


    $scope.model.translations = {
        title : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.otsikko'),
        kuvausKieli : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.kuvauskieli'),
        valintakoeNimi : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.valintakoenimi'),
        valintakoeKuvaus : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.kuvaus'),
        valintakoeAjankohtaSijainti : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.ajankohta.sijainti'),
        valintakoeAjankohtaAika : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.ajankohta.aika'),
        valintakoeAjankohtaLisatieto : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.ajankohta.lisatieto'),
        valintakoeAjankohtaLisaa : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.ajankohta.lisaa'),
        valintakoeAjankohtaTauluTitle : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.ajankohta.table.title'),
        valintakoeAjankohtaTauluSijainti : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.ajankohta.table.sijaint'),
        valintakoeAjankohtaTauluAjankohta : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.ajankohta.table.aika'),
        valintakoeAjankohtaTauluLisatiedot : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.ajankohta.table.lisatietoja'),
        valintakoeAjankohtaTauluMuokkaaBtn : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.ajankohta.table.muokkaa'),
        valintakoeAjankohtaTauluPoistaBtn : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.ajankohta.table.poista'),
        ok : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.ok'),
        cancel : LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.cancel')
    }


    $scope.model.koodistoComboCallback  = function(kieli) {
        selectedKieli = kieli;
    };

    if (valintakoe !== undefined) {
        $scope.model.valintakoe = valintakoe;
    } else {
        $scope.model.valintakoe = {
            valintakoeAjankohtas : [],
            valintakokeenKuvaus : {

            }
        };
    }

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.model.muokkaaAjankohtaa = function(valintakoeAjankohta) {
        $scope.model.removeAjankohtaFromArray(valintakoeAjankohta);
         $scope.model.selectedAjankohta = valintakoeAjankohta;
    }

    $scope.model.removeAjankohtaFromArray = function(valintakoeAjankohta) {

        var index = $scope.model.valintakoe.valintakoeAjankohtas.indexOf(valintakoeAjankohta);
        $scope.model.valintakoe.valintakoeAjankohtas.splice(index,1);

    }

    $scope.lisaaTiedot = function() {

        $scope.model.valintakoe.valintakoeAjankohtas.push($scope.model.selectedAjankohta);

        $scope.model.selectedAjankohta = {
            osoite : {}
        };

    };

   var validateValintakoe = function(){
       $scope.model.validationmsgs.splice(0,$scope.model.validationmsgs.length);
       if (selectedKieli === undefined) {
           $scope.model.validationmsgs.push(LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.kieli.req.msg'));

       }

       if ($scope.model.valintakoe.valintakoeAjankohtas === undefined || $scope.model.valintakoe.valintakoeAjankohtas.length < 1) {
           $scope.model.validationmsgs.push(LocalisationService.t('tarjonta.hakukohde.valintakoe.modal.yksi.valintakoeaika.req.msg'));

       }

       if ($scope.model.validationmsgs.length > 0) {
           return false;
       }

       return true;
   };


    $scope.save = function() {
            if (validateValintakoe()) {

                $scope.model.valintakoe.kieliNimi = selectedKieli.koodiNimi;
                $scope.model.valintakoe.valintakokeenKuvaus.nimi = selectedKieli.koodiNimi;
                $scope.model.valintakoe.valintakokeenKuvaus.arvo  = selectedKieli.koodiArvo;
                $scope.model.valintakoe.valintakokeenKuvaus.versio = selectedKieli.koodiVersio;


                $scope.model.valintakoe.valintakokeenKuvaus.uri = $scope.model.valintakoe.kieliUri;
                $modalInstance.close($scope.model.valintakoe);
            } else {
                $scope.model.showAlert = true;
            }

    };


});
*/