
var app = angular.module('app.helpers', ['app.services', 'localisation', 'config']);

app.controller('HelpersLocalisationCtrl', function($scope, Localisations, $q) {

    console.log("HelpersLocalisationCtrl()");

    $scope.model = {
        supported: ["fi", "en", "sv"],
        locale: "sv",
        localisations2: []
    };

    $scope.save = function(entry) {
        console.log("SAVE: ", entry);
        Localisations.save(entry, function(data, status, headers, config) {
            console.log("1FAILURE?", data);
            console.log("2FAILURE?", status);
            console.log("3FAILURE?", headers);
            console.log("4FAILURE?", config);
        }, function(data, status, headers, config) {
            console.log("1success?", data);
            console.log("2success?", status);
            console.log("3success?", headers);
            console.log("4success?", config);
        });
    };

    $scope.createNew2 = function(key) {
        console.log("createNew2()");

        var v_fi = {
            key: key,
            locale: "fi",
            value: "arvo"
        };
        var v_en = {
            key: key,
            locale: "en",
            value: "value"
        };
        var v_sv = {
            key: key,
            locale: "sv",
            value: "värdet"
        };

        // TODO how to chain these and then call reload?
        $scope.save(v_fi);
        $scope.save(v_en);
        $scope.save(v_sv);

        $scope.reloadData();
    };

    $scope.reloadData = function() {
        console.log("reloadData()")
        $scope.model.selected = undefined;
        $scope.model.locale = "fi";

        Localisations.query({}, function(data) {
            console.log("*************** LocalisationService - query: Success! ", data);

            $scope.model.localisations2 = data;
        });
    };


    // Loop tru all translations, make sure all contain FI, EN, SV translations
    $scope.createMissingTranslations = function() {

        // Translations with "key + _ + locale" key saved to a map for quick checking
        var m = {};
        var mkeys = {};
        for (localisationIndex in $scope.model.localisations2) {
            var tmp = $scope.model.localisations2[localisationIndex];
            m[tmp.key + "_" + tmp.locale] = "exists";
            mkeys[tmp.key] = "exists";
        }

        for (var key in mkeys) {
            console.log("  key = " + key);
            for (localeIndex in $scope.model.supported) {
                var locale = $scope.model.supported[localeIndex];

                if (!m[key + "_" + locale]) {
                    console.log("CREATE: " + key + " --> with locale " + locale);

                    var v = {
                        key: key,
                        locale: locale,
                        value: "arvo / value / värdet - ADDED"
                    };
                    $scope.save(v);

                    m[key + "_" + locale] = "added";
                }
            }
        }

    };

    $scope.addLanguage = function() {
        console.log("addLanguage()", $scope.model.locale);
        $scope.model.selected.values[$scope.model.locale] = "UUSI ARVO";
    };

    // Triggers model update / load translations
    $scope.reloadData();
});
