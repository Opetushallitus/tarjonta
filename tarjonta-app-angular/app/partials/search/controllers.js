
angular.module('app.controllers', ['app.services']).controller('SearchController', function($scope, $routeParams) {

    // tarjonnan tilat
    // TODO Generoi automaattisesti enumin fi.vm.sade.tarjonta.shared.types.TarjontaTila mukaan
    var states = [ "LUONNOS", "VALMIS", "JULKAISTU", "PERUTTU", "KOPIOITU" ];
    
    var stateMap = {};
    
    for (var i in states) {
    	var s = states[i];
    	stateMap[s] = "["+s+"]"; // TODO i18n
    }
    
    $scope.states = stateMap;
    
    // alkamiskaudet
    // TODO koodistosta
    $scope.seasons = {
    		"kausi_kevat#1": "Kevät",
    		"kausi_syksy#1": "Syksy",
    };

    // alkamisvuodet; 2012 .. nykyhetki + 10v
    $scope.years = [];
    var lyr = new Date().getFullYear()+10;
    for (var y = 2012; y<lyr; y++) {
    	$scope.years.push(y);
    }

    $scope.searchTerms = ""; // TODO parametrista
    $scope.selectedState = "*"; // TODO parametrista
    $scope.selectedYear = "*"; // TODO parametrista
    $scope.selectedSeason = "*"; // TODO parametrista
    
    $scope.selectedOrgOid = "12345";  // TODO oid-parametrista
    $scope.selectedOrgName = "OPH";  // TODO hae oidin mukaan

    $scope.resetOrg = function() {
        
        $scope.selectedOrgOid = "12345";  // TODO oph-oid
        $scope.selectedOrgName = "OPH";  // TODO hae oidin mukaan

    }
    
    $scope.reset = function() {
        
    	$scope.searchTerms = "";
        $scope.selectedState = "*";
        $scope.selectedYear = "*";
        $scope.selectedSeason = "*";

    }
    
    $scope.search = function() {
    	console.log("search!!");
    }
    
    $scope.report = function() {
    	console.log("TODO raportti");
    }
    

});
