
var app = angular.module('app.helpers', ['app.services', 'localisation', 'config']);

app.controller('HelpersLocalisationCtrl', function($scope, $q, $log, LocalisationService) {

    console.log("HelpersLocalisationCtrl()");

    $scope.model = {
        supported: ["fi", "en", "sv"],
        locale: "sv",
        localisations: []
    };

    $scope.save = function(entry) {
        console.log("SAVE: ", entry);
        return LocalisationService.createMissingTranslation(entry.key, entry.locale, entry.value);
    };

    /**
     * CReate translations for all supported languages.
     *
     * @param {type} key
     */
    $scope.createNew = function(key) {
        console.log("createNew()");

        var promises = [];

        for(idx in $scope.model.supported) {
            var locale = $scope.model.supported[idx];
            promises.push(LocalisationService.createMissingTranslation(key, locale, "[" + key + " " + locale + "]"));
        }

        $q.all(promises).then(function(value) {
            // SUCCESS
            $log.info("Saved all three! ", value);
            $scope.reloadData();
        }, function(value) {
            // ERROR
            $log.error("FAILED TO SAVE! ", value);
            $scope.reloadData();
        }, function(value) {
            // timeout?
            $log.error("TIMEOUT TO SAVE! ", value);
            $scope.reloadData();
        });

    };

    /**
     * Reloads data from server.
     */
    $scope.reloadData = function() {
        console.log("reloadData()")

        LocalisationService.reload().then(function(data) {
            console.log("Reloaded translations");
            $scope.model.selected = undefined;
            $scope.model.locale = "fi";

            $scope.model.localisations = data;
            $scope.model.localisations_original = angular.copy(data);

            if ($scope.localisationsForm) {
                $scope.localisationsForm.$setPristine();
            }
        });
    };

    /**
     * Loop tru all translations, make sure all contain FI, EN, SV translations
     */
    $scope.createMissingTranslations = function() {

        // Translations with "key + _ + locale" key saved to a map for quick checking
        var m = {};
        var mkeys = {};
        for (localisationIndex in $scope.model.localisations) {
            var tmp = $scope.model.localisations[localisationIndex];
            m[tmp.key + "_" + tmp.locale] = "exists";
            mkeys[tmp.key] = "exists";
        }

        for (var key in mkeys) {
            console.log("  checking key = " + key);
            for (localeIndex in $scope.model.supported) {
                var locale = $scope.model.supported[localeIndex];

                if (!m[key + "_" + locale]) {
                    $log.info("CREATE: " + key + " --> with locale " + locale);

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

    $scope.saveAllModified = function() {
        $log.info("saveAllModified()");

        var mapOld = {};

        for (var idx in $scope.model.localisations_original) {
            var tmp = $scope.model.localisations_original[idx];
            mapOld[tmp.key + "_" + tmp.locale] = tmp;
        }

        for (var idx in $scope.model.localisations) {
            var tmp = $scope.model.localisations[idx];

            var oldValue = mapOld[tmp.key + "_" + tmp.locale];

            if (!oldValue || oldValue.value != tmp.value) {

                if (tmp.value === "_POISTA_") {
                    LocalisationService.delete(tmp);
                } else {
                    LocalisationService.save(tmp);
                }
            }
        }
    };


//    $scope.addLanguage = function() {
//        console.log("addLanguage()", $scope.model.locale);
//        $scope.model.selected.values[$scope.model.locale] = "UUSI ARVO";
//    };

    // Triggers model update / load translations
    $scope.reloadData();
});
