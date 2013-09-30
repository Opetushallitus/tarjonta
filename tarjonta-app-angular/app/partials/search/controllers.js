
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
    $scope.selectedOrgOid = $scope.routeParams.id ? $scope.routeParams.id : OPH_ORG_OID;
    $scope.searchTerms = fromParams("terms","");
    $scope.selectedState = fromParams("state","*");
    $scope.selectedYear = fromParams("year","*");
    $scope.selectedSeason = fromParams("season","*");

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

    function copyIfSet(dst, key, value) {
        if (value != null && value != undefined && (value + "").length > 0 && value != "*") {
            dst[key] = value;
        }
    }

    function updateLocation() {

    	var sargs = {};
    	if ($scope.selectedOrgOid!=null && $scope.selectedOrgOid!=OPH_ORG_OID) {
    		sargs.oid = $scope.selectedOrgOid;
    	}
    	copyIfSet(sargs, "terms", $scope.searchTerms);
    	copyIfSet(sargs, "state", $scope.selectedState);
    	copyIfSet(sargs, "year", $scope.selectedYear);
    	copyIfSet(sargs, "season", $scope.selectedSeason);

    	$location.path("/etusivu/" + sargs.oid);
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
        $scope.searchTerms = "";
        $scope.selectedState = "*";
        $scope.selectedYear = "*";
        $scope.selectedSeason = "*";
    }

    $scope.search = function() {
    	var spec = {
            oid: $scope.selectedOrgOid,
            terms: $scope.searchTerms,
            state: $scope.selectedState == "*" ? null : $scope.selectedState,
            year: $scope.selectedYear == "*" ? null : $scope.selectedYear,
            season: $scope.selectedSeason == "*" ? null : $scope.selectedSeason
        };
        console.log("search", spec);
        updateLocation();
        TarjontaService.haeKoulutukset(spec).then(function(data){
        	$scope.koulutusResults = data;
        });
        TarjontaService.haeHakukohteet(spec).then(function(data){
        	$scope.hakukohdeResults = data;
        });
        
    }

    $scope.report = function() {
        console.log("TODO raportti");
    }

});
