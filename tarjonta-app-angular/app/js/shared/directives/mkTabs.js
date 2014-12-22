'use strict';
var app = angular.module('MonikielinenTabs', [
    'Koodisto',
    'localisation',
    'pasvaz.bindonce',
    'Logging'
]);
app.directive('mkTabs', function(Koodisto, LocalisationService, $log, $modal) {
    $log = $log.getInstance('<mkTabs>');
    var userLangs = window.CONFIG.app.userLanguages;
    function controller($scope) {
        $scope.userLanguages = userLangs;
        $scope.langs = [];
        $scope.codes = null;
        $scope.codeList = [];
        //aktiivinen täbi
        $scope.active = {};
        $scope.active[$scope.selection] = true;
        if (!$scope.model) {
            $scope.model = [];
        }
        function updateLangs() {
            if ($scope.codes == null) {
                //console.log("UPDATE -> postpone");
                return;
            }
            //console.log("PRE UPDATE", [$scope.selection, $scope.langs, $scope.model]);
            var ret = [];
            for (var i in $scope.model) {
                var kieliUri = $scope.model[i];
                if (ret.indexOf(kieliUri) == -1) {
                    ret.push(kieliUri);
                }
            }
            ret.sort(function(a, b) {
                var ap = userLangs.indexOf(a);
                var bp = userLangs.indexOf(b);
                if (ap != -1 && bp != -1) {
                    return ap - bp;
                }
                if (ap != -1) {
                    return -1;
                }
                if (bp != -1) {
                    return 1;
                }
                return $scope.codes[a].nimi.localeCompare($scope.codes[b].nimi);
            });
            $scope.langs = ret;
            if ($scope.langs.indexOf($scope.selection) == -1) {
                //jos valittu kieli pooistunut
                $scope.selection = undefined;
            }
            if ($scope.selection === undefined || $scope.selection === null && !$scope.mutable) {
                $scope.onSelect($scope.langs.length == 0 ? null : $scope.langs[0], false);
                console.log('Preselected:', [
                    $scope.selection,
                    $scope.langs
                ]);
            } //console.log("POST UPDATE", [$scope.selection, $scope.langs, $scope.model]);
        }
        $scope.onSelect = function(kieli, user) {
            if (user && $scope.codes == null) {
                return;
            }
            /*if (user) {
                  	console.log("UPDATE BY USER", kieli); 
              	}*/
            for (var k in $scope.active) {
                $scope.active[k] = false;
            }
            $scope.active[kieli] = true;
            $scope.selection = kieli;
        };
        $scope.$watch('selection', function(nv, ov) {
            if (nv === ov || $scope.codes == null) {
                return;
            }
            $scope.onSelect(nv, false);
        });
        // kielikoodit koodistosta
        Koodisto.getAllKoodisWithKoodiUri('kieli', LocalisationService.getLocale()).then(function(v) {
            $scope.codeList = v;
            var nc = {};
            for (var i in v) {
                nc[v[i].koodiUri + '#' + v[i].koodiVersio] = {
                    versio: v[i].koodiVersio,
                    nimi: v[i].koodiNimi,
                    uri: v[i].koodiUri
                };
                nc[v[i].koodiUri] = {
                    versio: v[i].koodiVersio,
                    nimi: v[i].koodiNimi,
                    uri: v[i].koodiUri
                };
            }
            $scope.codes = nc;
            updateLangs();
        });
        $scope.$watchCollection('model', function(nv, ov) {
            updateLangs();
        });
    }
    return {
        restrict: 'E',
        replace: true,
        templateUrl: 'js/shared/directives/mkTabs.html',
        controller: controller,
        scope: {
            model: '=',
            // lista kieliureista
            selection: '=',
            // valittu kieliuri
            //  - null == kielivalintatabi (jos mutable), muutoin sama kuin undefined
            //  - undefined == ensimmäinen kieli modelissa
            mutable: '@'
        }
    };
});