var app = angular.module('MultiLangSimpleTextArea', [
    'Koodisto',
    'localisation',
    'RichTextArea'
]);
app.directive('multiLangSimpleTextarea', function(Koodisto, LocalisationService, $log) {
    function controller($scope) {
        console.log('MULTILANGSIMPLE TEXTAREA : ', $scope.model);
        $scope.langs = {};
        $scope.selectedLangs = [];
        if ($scope.model instanceof Array || typeof $scope.model != 'object' || $scope.model == null
            || $scope.model == undefined) {
            console.log('MODEL FAIL', $scope.model);
            throw new Error('mkRichTextarea.model must be a non-array object');
        }
        //Loop through model map keys => these should be kieli uris
        for (var uri in $scope.model) {
            if ($scope.model.hasOwnProperty(uri)) {
                $scope.selectedLangs.push(uri);
            }
        }
        var langPromise = Koodisto.getAllKoodisWithKoodiUri('kieli', LocalisationService.getLocale());
        langPromise.then(function(kieliKoodis) {
            var kieliKoodi = {};
            angular.forEach(kieliKoodis, function(kk) {
                kieliKoodi[kk.koodiUri] = kk.koodiNimi;
            });
            $scope.langs = kieliKoodi;
        });
    }
    return {
        restrict: 'E',
        replace: true,
        templateUrl: 'js/shared/directives/multilangSimpleTextArea.html',
        controller: controller,
        scope: {
            model: '=',
            // map jossa kieliuri -> teksti, esim. {kieli_fi: "Suomeksi", kieli_sv: "På svenska"}
            max: '@' // maksimimerkkimäärä (ohjeellinen); jos ei määritelty, ei näytetä
        }
    };
});