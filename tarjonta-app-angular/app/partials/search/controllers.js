
angular.module('app.controllers', ['app.services','localisation','Organisaatio','angularTreeview', 'config'])

        .controller('SearchController', function($scope, $routeParams, $location, LocalisationService, Koodisto, OrganisaatioService, TarjontaService, Config) {

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

    $scope.organisaatio = {};

	//watchi valitulle organisaatiolle, tästä varmaan lähetetään "organisaatio valittu" eventti jonnekkin?
	$scope.$watch( 'organisaatio.currentNode', function( newObj, oldObj ) {

        console.log("$scope.$watch( 'organisaatio.currentNode')");

	    if( $scope.organisaatio && angular.isObject($scope.organisaatio.currentNode) ) {

	    	$scope.selectedOrgOid = $scope.organisaatio.currentNode.oid;
	    	$scope.selectedOrgName = $scope.organisaatio.currentNode.nimi;

	    	updateLocation();
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
    var stateMap = {"*": msgKaikki};
    var TARJONTA_TILAT = Config.app["tarjonta.tilat"];
    for (var i in TARJONTA_TILAT) {
        var s = TARJONTA_TILAT[i];
        if ((i / 1) != i) { // WTF? mistä epä-int tulee??
            continue;
        }
        stateMap[s] = LocalisationService.t("tarjonta.tila." + s);
    }

    $scope.states = stateMap;

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

    function toUrl(base, params) {
        var args = null;
        for (var p in params) {
            if (params[p] != null && params[p] != undefined && params[p] != "*" && params[p].trim().length > 0) {
                args = (args == null ? "?" : args + "&") + p + "=" + escape(params[p]);
            }
        }
        return args == null ? base : base + args;
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
    			html = html+"<tr class=\"tresult\" "+prefix+"-oid=\""+tulos[prefix+"Oid"]+"\">"
					+"<td><input type=\"checkbox\" class=\"selectRow\"/>"
					+"<a href=\"#\"><img src=\"img/icon-treetable-button.png\"/></a>"
					+"<a href=\"#/"+prefix+"/"+tulos.oid+"\">"	// linkki
					+tulos.nimi
					+"</a></td>"
					+"<td>" + tulos.kausiUri + "&nbsp;" + tulos.vuosi + "</td>";

    			for (var pi in props) {
    				var prop = props[pi];
    				html = html + "<td>" + (tulos[prop]==undefined ? "" : (tulos[prop]+"").replace(" ", "&nbsp;")) + "</td>";
    			}

    			html = html
    				+"<td>" + tulos.tila + "</td>"
    				+"</tr>";
    		}

    		html = html+"</tbody>"
    	}

    	return html;
    }
        
    function initTable(selector, prefix, data, cols) {
    	var em = $(selector);
    	
    	em.html(resultsToTable(data, cols, prefix));

    	// valitse-kaikki-nappi päälle/pois tulosten mukaan
    	$("input.selectAll", em.parent())
    		.prop("disabled", data.tuloksia==0) // TODO ei toimi, miksi
    		.click(function(ev){
    			var sel = $(ev.currentTarget).is(":checked");
    			//console.log("select/unselect all", sel);
    			$("input.selectRows, input.selectRow", em).prop("checked", sel);
    		});
    	
    	// lapsinodejen valitse-kaikki
    	$("input.selectRows", em).click(function(ev){
			var sel = $(ev.currentTarget).is(":checked");
			//console.log("select="+sel, ev.currentTarget.parentNode.parentNode.parentNode);
			$("input.selectRow", $(ev.currentTarget.parentNode.parentNode.parentNode)).prop("checked", sel);
    	});
    	
    	// foldaus
    	$("a.fold",em).click(function(ev){
    		ev.preventDefault();
    		$(ev.currentTarget.parentElement.parentElement.parentElement).toggleClass("folded");
    	});
    
    }
    
    $scope.canMoveOrCopy = function() {
    	return true;
    }

    $scope.canCreateHakukohde = function() {
    	return true;
    }

    $scope.canCreateKoulutus = function() {
    	return true;
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
        TarjontaService.haeKoulutukset(spec).then(function(data){
        	$scope.koulutusResultCount = " ("+data.tuloksia+")";
        	initTable("#koulutuksetResults", "koulutus", data,[
                "koulutuslaji" // TODO koulutuslaji puuttuu hakutuloksista
            ]);
        });
        TarjontaService.haeHakukohteet(spec).then(function(data){
        	$scope.hakukohdeResultCount = " ("+data.tuloksia+")";
        	initTable("#hakukohteetResults", "hakukohde", data,[
	       		"hakutapa",
				"aloituspaikat",
				"koulutusLaji"
            ]);
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

});
