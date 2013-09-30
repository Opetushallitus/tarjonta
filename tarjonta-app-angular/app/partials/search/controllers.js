
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

    	var sargs = {};
    	if ($scope.selectedOrgOid!=null && $scope.selectedOrgOid!=OPH_ORG_OID) {
    		sargs.oid = $scope.selectedOrgOid;
    	}
    	copyIfSet(sargs, "terms", $scope.spec.terms, "*");
    	copyIfSet(sargs, "state", $scope.spec.state);
    	copyIfSet(sargs, "year", $scope.spec.year);
    	copyIfSet(sargs, "season", $scope.spec.season);
    	
    	//$location.path("/etusivu/" + sargs.oid);
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
    
    function resultsToTable(results, props, prefix) {
    	/*
<tbody ng-repeat="tarjoaja in hakukohdeResults.tulokset">
        		<tr>
		        	<td colspan="4">{{tarjoaja.nimi}}</td>
        		</tr>
        		<tr ng-repeat="hakukohde in tarjoaja.tulokset">
	 		       	<ng-once>
        			<td><a href="#/koulutus/{{hakukohde.oid}}">{{hakukohde.nimi}}</a></td>
        			<td>{{hakukohde.kausiUri}} {{hakukohde.vuosi}}</td>
        			<td>{{hakukohde.hakutapa}}</td>
        			<td>{{hakukohde.aloituspaikat}}</td>
        			<td>{{hakukohde.koulutusLaji}}</td>
        			<td>{{hakukohde.tila}}</td>
        			</ng-once>
        		</tr>
        	</tbody>
    	 */
    	
    	var html = "";
    	for (var ti in results.tulokset) {
    		var tarjoaja = results.tulokset[ti];
    		html = html+"<tbody class=\"folded\" id=\""
    			+prefix+"_"+tarjoaja.oid
    			+"\">>"
    			+"<tr class=\"tgroup\"><th colspan=\""+(3 + props.length)+"\">"
    			+"<img src=\"img/triangle_down.png\" class=\"folded\"/>"
    			//+"<img src=\"img/triangle_right.png\" class=\"unfolded\"/>"
    			+"<input type=\"checkbox\"/>"
    			+tarjoaja.nimi // TODO lokalisointi
    			+"</th></tr>";
    		
    		for (var ri in tarjoaja.tulokset) {
    			var tulos = tarjoaja.tulokset[ri];
    			html = html+"<tr class=\"tresult\">"
					+"<td><input type=\"checkbox\"/>"
					+"<a href=\"#\"><img src=\"img/icon-treetable-button.png\"/></a>"
					+"<a href=\"#\">"
					+tulos.nimi
					+"</a></td>"
					+"<td>" + tulos.kausiUri + " " + tulos.vuosi + "</td>";

    			for (var pi in props) {
    				var prop = props[pi];
    				html = html + "<td>" + tulos[prop] + "</td>";
    			}
    			
    			html = html
    				+"<td>" + tulos.tila + "</td>"
    				+"</tr>";
    		}
    		
    		html = html+"</tbody>"
    	}

    	return html;
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
        	$("#koulutuksetResults").html(resultsToTable(data,[
                "koulutuslaji" // TODO koulutuslaji puuttuu hakutuloksista
            ],"koulutus"));
        	//$scope.koulutusResults = data;
        });
        TarjontaService.haeHakukohteet(spec).then(function(data){
        	$scope.hakukohdeResultCount = " ("+data.tuloksia+")";
        	$("#hakukohteetResults").html(resultsToTable(data,[
        		"hakutapa",
    			"aloituspaikat",
    			"koulutusLaji"
        	],"hakukohde"));
        	//$scope.hakukohdeResults = data;
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
