var app = angular.module('AiheetJaTeematChooser', [
    'Koodisto',
    'localisation'
]);
app.directive('aiheetJaTeemat', function(LocalisationService, Koodisto, $log) {
    $log = $log.getInstance('aiheetJaTeemat');
    var locale = LocalisationService.getLocale();
    function comparator(a, b) {
        return a.title.localeCompare(b.title);
    }
    function resolveAiheet($scope, teema) {
        Koodisto.getAlapuolisetKoodit(teema.uri, locale).then(function(aiheet) {
            for (var j in aiheet) {
                var sel = $scope.model.indexOf(aiheet[j].koodiUri) != -1;
                if (sel) {
                    teema.size++;
                }
                teema.aiheet.push({
                    uri: aiheet[j].koodiUri,
                    title: aiheet[j].koodiNimi,
                    show: false,
                    selected: sel
                });
            }
            teema.aiheet.sort(comparator);
        });
    }
    function controller($scope) {
        $scope.errors = {
            required: false,
            pristine: true,
            dirty: false
        };
        $scope.teemat = [];
        function updateErrors() {
            var isInvalid = $scope.model.length === 0;
            $scope.errors.required = isInvalid;
            $scope.errors.dirty = true;
            $scope.errors.pristine = false;
            $scope.form.$setValidity('aihees', !isInvalid);
        }
        $scope.toggle = function(auri, turi) {
            var i = $scope.model.indexOf(auri);
            if (i == -1) {
                $scope.model.push(auri);
            }
            else {
                $scope.model.splice(i, 1);
            }
            for (var j in $scope.teemat) {
                var t = $scope.teemat[j];
                if (t.uri == turi) {
                    for (var j in t.aiheet) {
                        var a = t.aiheet[j];
                        if (a.uri == auri) {
                            a.selected = i == -1;
                            break;
                        }
                    }
                    t.size += i == -1 ? 1 : -1;
                    break;
                }
            }
            updateErrors();
        };
        $scope.unselect = function(uri) {
            var i = $scope.model.indexOf(uri);
            if (i == -1) {
                return;
            }
            $scope.model.splice(i, 1);
            for (var j in $scope.teemat) {
                var t = $scope.teemat[j];
                for (var j in t.aiheet) {
                    var a = t.aiheet[j];
                    if (a.uri == uri) {
                        a.selected = false;
                        t.size += i == -1 ? 1 : -1;
                        updateErrors();
                        return;
                    }
                }
            }
            updateErrors();
        };
        Koodisto.getAllKoodisWithKoodiUri('teemat', locale).then(function(teemakoodit) {
            for (var i in teemakoodit) {
                var teema = {
                    uri: teemakoodit[i].koodiUri,
                    title: teemakoodit[i].koodiNimi.trim(),
                    aiheet: [],
                    size: 0
                };
                $scope.teemat.push(teema);
                resolveAiheet($scope, teema);
            }
            $scope.teemat.sort(comparator);
            updateErrors();
        });
        return $scope;
    }
    return {
        restrict: 'E',
        templateUrl: 'partials/koulutus/edit/aiheet-teemat.html',
        replace: true,
        controller: controller,
        require: '^form',
        link: function(scope, element, attrs, controller) {
            scope.errors.required = true;
            controller.$addControl({
                '$name': 'aiheetJaTeemat',
                '$error': scope.errors
            });
            scope.form = controller;
        },
        scope: {
            model: '=',
            name: '@'
        }
    };
});