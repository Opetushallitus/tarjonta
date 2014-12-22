'use strict';
var app = angular.module('MonikielinenTextArea', [
    'Koodisto',
    'localisation',
    'RichTextArea',
    'Logging'
]);
app.directive('mkRichTextarea', function(Koodisto, LocalisationService, $log, $modal) {
    $log = $log.getInstance('<mkRichTextarea>');
    function isEmpty(obj) {
        for (var i in obj) {
            return false;
        }
        return true;
    }
    function controller($scope) {
        if ($scope.model instanceof Array || typeof $scope.model != 'object' || $scope.model == null || $scope.model == undefined) {
            $log.debug('MODEL FAIL', $scope.model);
            throw new Error('mkRichTextarea.model must be a non-array object');
        }
        $scope._isDisabled = function() {
            return $scope.isDisabled() === true;
        };
        $scope.langs = [];
        $scope.userLangs = window.CONFIG.app.userLanguages;
        $scope.selectedLangs = [];
        $scope.selectedTab = {
            'kieli_fi': true
        };
        function langOfUri(lang) {
            if (lang && lang.uri) {
                var l = $scope.langs[lang.uri];
                if (l) {
                    return l;
                }
            }
            return '';
        }
        function updateLangs() {
            var langs = [];
            if (isEmpty($scope.model)) {
                //$log.debug("EMPTY -> INIT");
                for (var i in window.CONFIG.app.userLanguages) {
                    var lang = window.CONFIG.app.userLanguages[i];
                    langs.push(lang);
                    $scope.model[lang] = '';
                }
            }
            else {
                for (var i in $scope.model) {
                    //$log.debug("INIT "+i+" -> ", $scope.model[i]);
                    if ($scope.model[i] !== undefined) {
                        // undefinedit pois
                        langs.push(i);
                    }
                }
            }
            langs.sort(function(a, b) {
                var ap = $scope.userLangs.indexOf(a);
                var bp = $scope.userLangs.indexOf(b);
                if (ap != -1 && bp != -1) {
                    return ap - bp;
                }
                if (ap != -1) {
                    return -1;
                }
                if (bp != -1) {
                    return 1;
                }
                return langOfUri(a).localeCompare(langOfUri(b));
            });
            $scope.selectedLangs = langs; /*$log.debug("MODEL = ",$scope.model);
        	$log.debug("TABS = ",$scope.selectedTab);
        	$log.debug("LANGS = ",$scope.selectedLangs);*/
        }
        updateLangs();
        // kielikoodit koodistosta
        $scope.langsPromise = Koodisto.getAllKoodisWithKoodiUri('kieli', LocalisationService.getLocale());
        $scope.langsPromise.then(function(v) {
            var nc = {};
            for (var i in v) {
                nc[v[i].koodiUri] = v[i].koodiNimi; //{versio: v[i].koodiVersio, nimi: v[i].koodiNimi, uri: v[i].koodiUri};
            }
            $scope.langs = nc;
        });
        $scope.updateLangs = updateLangs;
        $scope.$watch('model', function(nv, ov) {
            //$log.debug("model = ",$scope.model);
            updateLangs();
        }, false);
        $scope.$watch('isDisabled', function(nv, ov) {
            //$log.debug("disabled = ",$scope.disabled());
            updateLangs();
        });
    }
    return {
        restrict: 'E',
        replace: true,
        templateUrl: 'js/shared/directives/mkRichTextarea.html',
        controller: controller,
        scope: {
            model: '=',
            // map jossa kieliuri -> teksti, esim. {kieli_fi: "Suomeksi", kieli_sv: "På svenska"}
            max: '@',
            // maksimimerkkimäärä (ohjeellinen); jos ei määritelty, ei näytetä
            isDisabled: '&' // disablointi
        }
    };
});