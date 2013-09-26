
Object.prototype.getName = function() {
    var funcNameRegex = /function (.{1,})\(/;
    var results = (funcNameRegex).exec((this).constructor.toString());
    return (results && results.length > 1) ? results[1] : "";
};


//angular.module('transclude', [])
// .directive('ngOnce', ['$timeout', function($timeout){
//    return {
//      restrict: 'EA',
//      priority: 500,
//      transclude: true,
//      template: '<div ng-transclude></div>',
//        compile: function (tElement, tAttrs, transclude) {
//            return function postLink(scope, iElement, iAttrs, controller) {
//                $timeout(scope.$destroy.bind(scope), 0);
//            }
//        }
//    };
//}]);
//

var app = angular.module('app.kk.review.ctrl', ['ui.bootstrap']);

app.controller('KKReviewController', ['$scope', '$location', 'TarjontaService', '$routeParams', 'LocalisationService',
    function KKReviewController($scope, $location, tarjontaService, $routeParams, LocalisationService) {
        $scope.routeParams = $routeParams;
        $scope.searchByOid = "1.2.246.562.5.2013091114080489552096";
        $scope.opetuskieli = 'kieli_fi';
        $scope.model = {};

        $scope.languages = [
            {
                name: "Suomi",
                locale: "fi",
                koodi_uri: "kieli_fi"
            },
            {
                name: "Ruotsi",
                locale: "sv",
                koodi_uri: "kieli_sv"
            },
            {
                name: "Englanti",
                locale: "en",
                koodi_uri: "kieli_en"
            },
        ];


        $scope.isCollapsed = false;
        $scope.dynamicPopover = "Hello, World!";
        $scope.dynamicPopoverText = "dynamic";
        $scope.dynamicPopoverTitle = "Title";


        $scope.basicInfoIsCollapsed = false;
        $scope.descriptionInfoIsCollapsed = false;
        $scope.includedKoulutusIsCollapsed = false;
        $scope.hakukohteetIsCollapsed = false;

        $scope.search = function() {
            console.info("search()");

            tarjontaService.get({oid: $scope.searchByOid}, function(data) {
                $scope.model = data;
                $scope.model.koulutuksenAlkamisPvm = Date.parse(data.koulutuksenAlkamisPvm);
                console.info($scope.model)
            });
        };


        for(i = 0; i < $scope.$watch; i++) {
            console.log("kello: " + $scope.$watch[i]);
        }

        $scope.search();
    }]);
