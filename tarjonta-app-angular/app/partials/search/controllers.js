
angular.module('app.controllers', ['app.services','localisation','Organisaatio','angularTreeview', 'config'])
        .controller('SearchController', function($scope, $routeParams, $location, LocalisationService, Koodisto, OrganisaatioService, TarjontaService, PermissionService, Config, loadingService, $modal, $window) {

    var OPH_ORG_OID = Config.env["root.organisaatio.oid"];

	// 1. Organisaatiohaku
	function setDefaultHakuehdot(){
		$scope.hakuehdot={
			"searchStr" : "",
			"organisaatiotyyppi" : "",
			"oppilaitostyyppi" : "",
			"lakkautetut" : false,
			"suunnitellut" : false
		};
	}

	setDefaultHakuehdot();
	$scope.oppilaitostyypit={};

    Koodisto.getAllKoodisWithKoodiUri("oppilaitostyyppi", "FI").then(function(koodit) {
        console.log("oppilaitostyypit", koodit);
        angular.forEach(koodit, function(koodi){
        	koodi.koodiUriWithVersion=koodi.koodiUri + "#" + koodi.koodiVersio;
        });
        $scope.oppilaitostyypit=koodit;
    });

	
    $scope.organisaatio = {};

	//watchi valitulle organisaatiolle, tästä varmaan lähetetään "organisaatio valittu" eventti jonnekkin?
	$scope.$watch( 'organisaatio.currentNode', function( newObj, oldObj ) {

        console.log("$scope.$watch( 'organisaatio.currentNode')");

	    if( $scope.organisaatio && angular.isObject($scope.organisaatio.currentNode) ) {

	    	$scope.selectedOrgOid = $scope.organisaatio.currentNode.oid;
	    	$scope.selectedOrgName = $scope.organisaatio.currentNode.nimi;

	    	updateLocation();
	    	$scope.koulutusActions.canCreateKoulutus = PermissionService.koulutus.canCreate($scope.selectedOrgOid);
	    }
	}, false);

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
			console.log("result returned, hits:", vastaus);
			$scope.tulos = vastaus.organisaatiot;
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
    // TODO koodi-locale jostain
    Koodisto.getAllKoodisWithKoodiUri("kausi", "FI").then(function(koodit) {
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
        $scope.selectedOrgName = OrganisaatioService.nimi($scope.selectedOrgOid);
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
        updateSelection();
    }

    $scope.reset = function() {
        $scope.spec.terms = "";
        $scope.spec.state = "*";
        $scope.spec.year = "*";
        $scope.spec.season = "*";
    }
    
	$scope.selectedKoulutukset = [];
	$scope.selectedHakukohteet = [];

    $scope.menuOptions = [];
    
    $scope.koulutusActions = {
    		canMoveOrCopy: false,
    		canCreateHakukohde: false,
    		canCreateKoulutus: false
    };

    // taulukon renderöinti    
    function resultsToTable(results, props, prefix) {

    	var html = "";
    	for (var ti in results.tulokset) {
    		var tarjoaja = results.tulokset[ti];
    		html = html+"<tbody class=\"folded\" tarjoaja-oid=\""
    			+tarjoaja.oid
    			+"\">"
    			+"<tr class=\"tgroup\"><th colspan=\""+(3 + props.length)+"\">"
    			+"<a href=\"#\" class=\"fold\">"
    			+"<img src=\"img/triangle_down.png\" class=\"folded\"/>"
    			+"<img src=\"img/triangle_right.png\" class=\"unfolded\"/>"
    			+"</a>"
    			+"<input type=\"checkbox\" class=\"selectRows\"/>"
    			+tarjoaja.nimi
    			+"</th></tr>";

    		for (var ri in tarjoaja.tulokset) {
    			var tulos = tarjoaja.tulokset[ri];
    			html = html+"<tr class=\"tresult\" "+prefix+"-oid=\""+tulos.oid+"\" tila=\""+tulos.tila+"\">"
					+"<td><input type=\"checkbox\" class=\"selectRow\"/>"
					+"<a href=\"#\" class=\"options\"><img src=\"img/icon-treetable-button.png\"/></a>"
					+"<a href=\"#/"+prefix+"/"+tulos.oid+"\">"	// linkki
					+tulos.nimi
					+"</a></td>"
					+"<td>" + (tulos.kausi.fi||tulos.kausi.sv||tulos.kausi.en) + "&nbsp;" + tulos.vuosi + "</td>"; //TODO lokalisoi!

    			for (var pi in props) {
    				var prop = props[pi];
    				html = html + "<td>" + (tulos[prop]==undefined ? "" : (tulos[prop]+"").replace(" ", "&nbsp;")) + "</td>";
    			}

    			html = html
    				+"<td class=\"state\">" + tulos.tilaNimi + "</td>"
    				+"</tr>";
    		}

    		html = html+"</tbody>"
    	}

    	return html;
    }
    
    function box(a,b,c) {
    	return a<b ? b : a>c ? c : a;
    }
    
    function selectedOids(root) {
    	var oids = [];
    	$("input.selectRow:checked", root).each(function(i, em){
    		var tr = $(em.parentNode.parentNode);
    		oids.push(tr.attr("hakukohde-oid") || tr.attr("koulutus-oid"));
    	});
    	return oids;
    }
    
    function updateSelection() {
    	$scope.selectedKoulutukset = selectedOids("table#koulutuksetResults");
    	$scope.selectedHakukohteet = selectedOids("table#hakukohteetResults");
        
        $scope.koulutusActions.canMoveOrCopy = PermissionService.koulutus.canMoveOrCopy($scope.selectedKoulutukset);
        $scope.koulutusActions.canCreateHakukohde = PermissionService.hakukohde.canCreate($scope.selectedKoulutukset);
        $scope.koulutusActions.canCreateKoulutus = PermissionService.koulutus.canCreate($scope.selectedOrgOid);
    }
    
    function updateTableRowState(prefix, oid, nstate) {
    	var row = $("#searchResults tr["+prefix+"-oid='"+oid+"']");
    	row.attr("tila", nstate);
    	var em = $("td.state", row);
    	em.text(LocalisationService.t("tarjonta.tila."+nstate));
    }
    
    function rowActions(prefix, oid, tila) {
    	var ret = [];
    	var tt = TarjontaService.getTilat()[tila];
    	
    	var canRead = PermissionService[prefix].canPreview(oid);
    	
		// tarkastele
		if (canRead) {
			ret.push({url:"#/"+prefix+"/"+oid, title: LocalisationService.t("tarjonta.toiminnot.tarkastele")});
		}
		// muokkaa
		if (tt.mutable && PermissionService[prefix].canEdit(oid)) {
			ret.push({url:"#/"+prefix+"/"+oid+"/edit", title: LocalisationService.t("tarjonta.toiminnot.muokkaa")});
		}
		// näytä hakukohteet
		if (canRead) {
			ret.push({url:"#", title: LocalisationService.t("tarjonta.toiminnot."+prefix+".linkit"),
				action: function(ev) {
					console.log("NÄYTÄ LINKIT "+prefix+" / "+oid, ev);
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
							updateTableRowState(prefix, oid, ns);
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
							updateTableRowState(prefix, oid, ns);
						});
					}
				});
			}
			break;
		}
		// poista
		if (tt.removable && PermissionService[prefix].canDelete(oid)) {
			ret.push({url: "#", title: LocalisationService.t("tarjonta.toiminnot.poista"),
				action: function(){
					console.log("TODO poista "+prefix+" / "+oid);
				}
			});
		}
		
		return ret;
    }
    
    function initResultsTableBindings(em) {
    	// lapsinodejen valitse-kaikki
    	$("input.selectRows", em).click(function(ev){
			var sel = $(ev.currentTarget).is(":checked");
			//console.log("select="+sel, ev.currentTarget.parentNode.parentNode.parentNode);
			$("input.selectRow", $(ev.currentTarget.parentNode.parentNode.parentNode)).prop("checked", sel);
    	});
    	
    	$("input[type=checkbox]", em).click(function(ev){
    		updateSelection();
    		$scope.$apply();
    	});

    	// kirjapinovalikot
    	// - sisältö angularilla, sijoittelu jqueyryllä
    	$(".options", em).click(function(ev){
    		ev.preventDefault();
    		var menu = $("#dropdown");
    		
    		// popup-valikon sisältö
    		var row = $(ev.currentTarget.parentNode.parentNode);
    		var hkOid = row.attr("hakukohde-oid");
    		var kmOid = row.attr("koulutus-oid");
    		var tila = row.attr("tila");
    		
    		var menuOptions = [];
    		
    		if (hkOid) {
    			$scope.menuOptions = rowActions("hakukohde", hkOid, tila);
    		} else if (kmOid) {
    			$scope.menuOptions = rowActions("koulutus", kmOid, tila);
    		} else {
    			console.log("row has no oid", row);
    			$scope.menuOptions = [];
    			return; // ei oidia? -> ei näytetä valikkoa
    		}
    		
    		$scope.$apply();
    		    		
    		// sijoittelu
    		menu.toggleClass("display-block",true);
    		menu.css("left", box(ev.pageX-4, 0, $(window).width() - menu.width() - 4));
    		menu.css("top", box(ev.pageY-4, 0, $(window).height() - menu.height() - 4));
    		
    		    	
    		// automaattinen sulkeutuminen hiiren kursorin siirtyessä muualle
    		menu.mouseenter(function(){
    			var timer = menu.data("popupTimer");
    			if (timer!=null) {
    				clearTimeout(timer);
    				menu.data("timer", null);
    			}
    		});
    		
    		menu.mouseleave(function(){
    			menu.data("timer", setTimeout(function(){
    				menu.toggleClass("display-block",false);
    			}, 500));
    		});
    		
    		$("a", menu).click(function(ev){
    			// jos url on #, estetään selainta seuraamasta linkkiä (oletetaan, että action on määritelty)
    			if ($(ev.currentTarget).attr("href") == "#") {
    				ev.preventDefault();
    			}

        		// sulkeutuminen linkkiä klikkaamalla yms.
    			menu.toggleClass("display-block",false);
    		});

    	});

        // valinta riviä klikkaamalla
    	$("td, th", em).click(function(ev){
    		//console.log("select ", ev.currentTarget);
    		$("input[type=checkbox]", ev.currentTarget.parentNode).trigger("click");
    	});
    	
    	// foldaus
    	$("a.fold",em).click(function(ev){
    		//console.log("fold ", ev.currentTarget);
    		ev.preventDefault();
    		ev.stopPropagation();
    		$(ev.currentTarget.parentElement.parentElement.parentElement).toggleClass("folded");
    	});
   	
    }

    function forceClear(em) {
    	// angular koukuttaa jquery-kutsu $(...).clear():in, josta seuraa
    	// delete-tapahtuma joka dom-nodelle, joka puolestaan aiheuttaa
    	// vakavia suorituskykyongelmia (ui hyytyy n. minuutin ajaksi)
    	em.each(function(i, e) {
    		while (e.firstChild) {
    			e.removeChild(e.firstChild);
    		}
    	});
    }
    
    // tyhjentää taulukot hakusivulta poistuessa, estäen angularia jumittamasta ui:ta
    $scope.$on("$destroy", function(){
		forceClear($("#searchResults table"));
    });
    
    var rowsPerAppend = 20; // TODO konfiguraatioon?
    var delayPerAppend = 1; // TODO konfiguraatioon?
    
    var serial = 0;
    
    function appendTableRow(row, em, prefix, data, cols, sn) {
    	    	
    	if (serial!=sn) {
            //console.log("abort "+prefix+" @ "+new Date());
        	loadingService.afterOperation();
        	$scope.$apply();
    		return; // uusi haku -> keskeytetään
    	}
    	
    	var rdata = {
    			tuloksia:0,
    			tulokset:[]
    	};
    	  	
    	for (var i=0; i<rowsPerAppend && data.tulokset[row]; i++) {
    		rdata.tulokset.push(data.tulokset[row]);
    		row++;
    	}

		rdata.tuloksia = rdata.tulokset.length;

    	var html = $(resultsToTable(rdata, cols, prefix));
    	initResultsTableBindings(html);
    	em.append(html);
    	
    	if (data.tulokset.length>row) {
    		setTimeout(function(){
    			appendTableRow(row, em, prefix, data, cols, sn);
    		},delayPerAppend);
    	} else {
            //console.log("done "+prefix+" @ "+new Date());
        	em.toggleClass("loading", false);    	
        	loadingService.afterOperation();
        	if (!$scope.$$phase) {
            	$scope.$apply();
        	}
    	}
    }
    
    function createTableHeader(selector, prefix, cols) {
    	console.log("cols", cols);
    	var html = "<tr class=\"header\">"
    		+"<th class=\"nimi\"></th>"
    		+"<th class=\"kausi\">"+LocalisationService.t("tarjonta.hakutulokset.kausi")+"</th>";
    	
    	for (var i in cols) {
    		var c = cols[i];
    		html = html+"<th class=\""+c+"\">"+LocalisationService.t("tarjonta.hakutulokset."+c)+"</th>";
    	}
    	
    	return html
    		+"<th class=\"tila\">"+LocalisationService.t("tarjonta.hakutulokset.tila")+"</th>"
    		+"</tr>";
    }
    
    function initTable(selector, prefix, data, cols, sn) {
    	
    	if (sn!=serial) {
    		return; // uusi haku -> keskeytetään
    	}
    	
    	var em = $(selector);
    	    	
    	em.toggleClass("loading", true);
    	loadingService.beforeOperation();
    	
    	forceClear(em);
    	em.html(createTableHeader(em, prefix, cols));
    	
    	// valitse-kaikki-nappi päälle/pois tulosten mukaan
    	$("input.selectAll", em.parent())
    		.prop("disabled", data.tuloksia==0) // TODO ei toimi, miksi
    		.click(function(ev){
    			var sel = $(ev.currentTarget).is(":checked");
    			//console.log("select/unselect all", sel);
    			$("input.selectRows, input.selectRow", em).prop("checked", sel);

    			updateSelection();
        		$scope.$apply();
    		});
    	
        updateSelection();

        if (data.tuloksia==0) {
    		// TODO näytä "ei tuloksia" tjsp..
        	em.toggleClass("loading", false);
        	loadingService.afterOperation();
    	} else {
    		appendTableRow(0, em, prefix, data, cols, sn);
    	}
    }
    
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
        
        serial++;

        // valinnat
        $("input.selectAll").prop("checked", false);
        
        TarjontaService.haeKoulutukset(spec).then(function(data){
        	$scope.koulutusResultCount = " ("+data.tuloksia+")";
        	initTable("#koulutuksetResults", "koulutus", data,[
                "koulutuslaji" 
            ], serial);
        });
        
        TarjontaService.haeHakukohteet(spec).then(function(data){
        	$scope.hakukohdeResultCount = " ("+data.tuloksia+")";
        	initTable("#hakukohteetResults", "hakukohde", data,[
	       		"hakutapa",
				"aloituspaikat",
				"koulutuslaji"
            ], serial);
        });
        
    }

    if ($scope.spec.terms=="*") {
    	$scope.spec.terms="";
    	$scope.search();
    } else if ($scope.spec.terms!="") {
    	$scope.search();
    }

    $scope.report = function() {
        console.log("TODO raportti");
    }
    
    $scope.tutkintoDialogModel = {};
	
	$scope.tutkintoDialogModel.open = function() {
		
			var modalInstance = $modal.open({
				scope: $scope,
				templateUrl: 'partials/kk/edit/selectTutkintoOhjelma.html',
				controller: 'SelectTutkintoOhjelmaController'
			});
		
			modalInstance.result.then(function(selectedItem) {
				console.log('Ok, dialog closed: ' + selectedItem.koodiNimi);
				console.log('Koodiarvo is: ' + selectedItem.koodiArvo);
				if (selectedItem.koodiUri != null) {
					$window.location.href = '#/kk/edit/new/perus/' + $scope.selectedOrgOid + '/' + selectedItem.koodiArvo + '/';
				} 
			}, function() {
				$scope.tutkintoDialogModel.selected = null;
				console.log('Cancel, dialog closed');
			});
	};

});
