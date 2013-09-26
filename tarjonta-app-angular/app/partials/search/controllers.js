
angular.module('app.controllers', ['app.services', 'localisation', 'Organisaatio', 'angularTreeview']).controller('SearchController', function($scope, $routeParams, $location, LocalisationService, Koodisto, OrganisaatioService, Config) {

    var TARJONTA_TILAT = Config.app['tarjonta.tilat'];
    var OPH_ORG_OID = Config.env["root.organisaatio.oid"]
    // 1. Organisaatiohaku

    $scope.hakuehdot = $scope.defaultHakuehdot = {
        "tekstihaku": "",
        "organisaatiotyyppi": "",
        "oppilaitostyyppi": "",
        "lakkautetut": false,
        "suunnitellut": false
    };

    //watchi valitulle organisaatiolle, tästä varmaan lähetetään "organisaatio valittu" eventti jonnekkin?
    $scope.$watch('organisaatio.currentNode', function(newObj, oldObj) {
        if ($scope.organisaatio && angular.isObject($scope.organisaatio.currentNode)) {

            $scope.selectedOrgOid = $scope.organisaatio.currentNode.oid;
            $scope.selectedOrgName = $scope.organisaatio.currentNode.nimi;

            updateLocation();
        }
    }, false);

    // organisaatiotyypit; TODO jostain jotenkin dynaamisesti
    $scope.organisaatiotyypit = [{
            nimi: LocalisationService.t("organisaatiotyyppi.koulutustoimija"),
            koodi: 'KOULUTUSTOIMIJA'
        }, {
            nimi: LocalisationService.t("organisaatiotyyppi.oppilaitos"),
            koodi: "OPPILAITOS"
        }, {
            nimi: LocalisationService.t("organisaatiotyyppi.toimipiste"),
            koodi: "TOIMIPISTE"
        }, {
            nimi: LocalisationService.t("organisaatiotyyppi.oppisopimustoimipiste"),
            koodi: "OPPISOPIMUSTOIMIPISTE"
        }];

    // Kutsutaan formin submitissa, käynnistää haun
    $scope.submitOrg = function() {
        //console.log("organisaatiosearch clicked!: " + angular.toJson($scope.hakuehdot));
        hakutulos = OrganisaatioService.etsi($scope.hakuehdot.tekstihaku);
        hakutulos.then(function(vastaus) {
            console.log("result returned, hits:", vastaus);
            $scope.tulos = vastaus.organisaatiot;
        });
    };

    // Kutsutaan formin resetissä, palauttaa default syötteet modeliin
    $scope.resetOrg = function() {
        //console.log("reset clicked!");
        $scope.hakuehdot = angular.copy($scope.defaultHakuehdot);
    };

    // 2. Koulutusten/Hakujen haku

    // hakuparametrit ja organisaatiovalinta
    function fromParams(key, def) {
        return $routeParams[key] != null ? $routeParams[key] : def;
    }

    $scope.selectedOrgOid = fromParams("oid", OPH_ORG_OID);
    $scope.searchTerms = fromParams("terms", "");
    $scope.selectedState = fromParams("state", "*");
    $scope.selectedYear = fromParams("year", "*");
    $scope.selectedSeason = fromParams("season", "*");

    var msgKaikki = LocalisationService.t("tarjonta.haku.kaikki");

    // tarjonnan tilat
    var stateMap = {"*": msgKaikki};
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
        if ($scope.selectedOrgOid != null && $scope.selectedOrgOid != OPH_ORG_OID) {
            sargs.oid = $scope.selectedOrgOid;
        }
        copyIfSet(sargs, "terms", $scope.searchTerms);
        copyIfSet(sargs, "state", $scope.selectedState);
        copyIfSet(sargs, "year", $scope.selectedYear);
        copyIfSet(sargs, "season", $scope.selectedSeason);

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
