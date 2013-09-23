
var app = angular.module('app.kk.review.ctrl', ['ui.bootstrap']);

app.controller('KKReviewController', ['$scope', 'TarjontaService', '$routeParams',
    function FormTutkintoController($scope, tarjontaService, $routeParams) {
        $scope.routeParams = $routeParams;
        $scope.searchByOid = "1.2.246.562.5.2013091114080489552096";
        $scope.opetuskieli = 'kieli_fi';
        $scope.model = {};

        $scope.isCollapsed = false;
        $scope.dynamicPopover = "Hello, World!";
        $scope.dynamicPopoverText = "dynamic";
        $scope.dynamicPopoverTitle = "Title";


        $scope.basicInfoisCollapsed = false;
        $scope.basicInfoisCollapsed = false;
        $scope.basicInfoisCollapsed = false;
        $scope.basicInfoisCollapsed = false;
        $scope.basicInfoisCollapsed = false;


    <div>koulutuksen perustiedot</div>
    <div>koulutuksen kuvailevat tiedot</div>
    <div>sisältyvät opintokokonaisuudet</div>
    <div>hakukohteet</div>

        <div collapse="isCollapsed">
            <div class="well well-large">Some content</div>
        </div>


        $scope.search = function() {
            console.info("search()");
            tarjontaService.get({oid: $scope.searchByOid}, function(data) {
                $scope.model = data;
                $scope.model.koulutuksenAlkamisPvm = Date.parse(data.koulutuksenAlkamisPvm);
                console.info($scope.model)
            });
        };

        $scope.search();
    }]);
