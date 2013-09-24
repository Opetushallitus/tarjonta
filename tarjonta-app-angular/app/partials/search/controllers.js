
angular.module('app.controllers', ['app.services']).controller('SearchController', function($scope, $routeParams, $location, LocalisationService) {

    // hakuparametrit ja organisaatiovalinta
    function fromParams(key, def="*") {
    	return $routeParams[key] != null ? $routeParams[key] : def;
    }
    
    $scope.selectedOrgOid = fromParams("oid", "12345");
    $scope.searchTerms = fromParams("terms","");
    $scope.selectedState = fromParams("state");
    $scope.selectedYear = fromParams("year");
    $scope.selectedSeason = fromParams("season");

    var msgKaikki = LocalisationService.t("tarjonta.haku.kaikki");

    // tarjonnan tilat
    // TODO Generoi automaattisesti enumin fi.vm.sade.tarjonta.shared.types.TarjontaTila mukaan
    var states = [ "LUONNOS", "VALMIS", "JULKAISTU", "PERUTTU", "KOPIOITU" ];
        
    var stateMap = {"*": msgKaikki};
        
    for (var i in states) {
    	var s = states[i];
    	stateMap[s] = LocalisationService.t("tarjonta.tila."+s); // TODO i18n
    }
    
    $scope.states = stateMap;
    
    // alkamiskaudet
    // TODO koodistosta
    $scope.seasons = {
    		"*": msgKaikki,
    		"kausi_kevat": "Kevät",
    		"kausi_syksy": "Syksy",
    };

    // alkamisvuodet; 2012 .. nykyhetki + 10v
    $scope.years = {"*": msgKaikki};
    var lyr = new Date().getFullYear()+10;
    for (var y = 2012; y<lyr; y++) {
    	$scope.years[y] = y;
    }


    $scope.selectedOrgName = "OPH";  // TODO hae oidin mukaan

    function updateLocation() {
    	var url = "/search/";
    	if ($scope.selectedOrgOid != null) {
    		url = url+$scope.selectedOrgOid+"/";
    	}
    	
    	url = url+"?terms="+$scope.searchTerms+"&state="+$scope.selectedState+"&year="+$scope.selectedYear+"&season="+$scope.selectedSeason;
    	
    	$location.url(url);    

    }

    $scope.resetOrg = function() {
        $scope.selectedOrgOid = "12345";  // TODO oph-oid?
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
