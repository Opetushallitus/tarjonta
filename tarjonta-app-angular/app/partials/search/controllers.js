
angular.module('app.controllers', ['app.services','localisation','Organisaatio']).controller('SearchController', function($scope, $routeParams, $location, LocalisationService, Koodisto, OrganisaatioService) {

    // hakuparametrit ja organisaatiovalinta
    function fromParams(key, def) {
    	return $routeParams[key] != null ? $routeParams[key] : def;
    }

    $scope.selectedOrgOid = fromParams("oid", OPH_ORG_OID);
    $scope.searchTerms = fromParams("terms","");
    $scope.selectedState = fromParams("state","*");
    $scope.selectedYear = fromParams("year","*");
    $scope.selectedSeason = fromParams("season","*");

    var msgKaikki = LocalisationService.t("tarjonta.haku.kaikki");

    // tarjonnan tilat
    var stateMap = {"*": msgKaikki};
    for (var i in TARJONTA_TILAT) {
    	var s = TARJONTA_TILAT[i];
    	stateMap[s] = LocalisationService.t("tarjonta.tila."+s);
    }

    $scope.states = stateMap;

    // alkamiskaudet
    $scope.seasons = {
    		"*": msgKaikki,
    };
    // TODO koodi-locale jostain
    Koodisto.getAllKoodisWithKoodiUri("kausi", "FI").then(function(koodit){
    	console.log("koodit",koodit);
        $scope.seasons = {"*": msgKaikki};

        for (var i in koodit) {
        	var k = koodit[i];
        	$scope.seasons[k.koodiUri] = k.koodiNimi;
        }

    });

    // alkamisvuodet; 2012 .. nykyhetki + 10v
    $scope.years = {"*": msgKaikki};
    var lyr = new Date().getFullYear()+10;
    for (var y = 2012; y<lyr; y++) {
    	$scope.years[y] = y;
    }


    $scope.selectedOrgName = "OPH";  // TODO hae oidin mukaan
    
    function toUrl(base, params) {
    	var args = null;
    	for (var p in params) {
    		if (params[p]!=null && params[p]!=undefined && params[p]!="*" && params[p].trim().length>0) {
    			args = (args==null ? "?" : args+"&") + p + "=" + escape(params[p]);
    		}
    	}
    	return args==null ? base : base+args;
    }

    function updateLocation() {
    	var url = "/search/";
    	if ($scope.selectedOrgOid != null && $scope.selectedOrgOid != OPH_ORG_OID) {
    		url = url+$scope.selectedOrgOid+"/";
    	}
    	
    	$location.url(toUrl(url, {
    		terms: $scope.searchTerms,
    		state: $scope.selectedState,
    		year: $scope.selectedYear,
    		season: $scope.selectedSeason
    	}));

    }

    $scope.resetOrg = function() {
        $scope.selectedOrgOid = OPH_ORG_OID;
        $scope.selectedOrgName = "OPH";  // TODO hae oidin mukaan
        updateLocation();
    }

    $scope.reset = function() {
    	 $scope.searchTerms = "";
         $scope.selectedState = "*";
         $scope.selectedYear = "*";
         $scope.selectedSeason = "*";
    }

    $scope.search = function() {
    	console.log("search", {
    		oid: $scope.selectedOrgOid,
    		terms: $scope.searchTerms,
    		state: $scope.selectedState,
    		year: $scope.selectedYear,
    		season: $scope.selectedSeason
    	});
    	updateLocation();
    }

    $scope.report = function() {
    	console.log("TODO raportti");
    }

});
