var app = angular.module('app');
app.service('koulutusHakukohdeListing', function($modal, $rootScope) {
    var LinksDialogCtrl = function($scope, $modalInstance, TarjontaService) {
        $scope.otsikko = 'tarjonta.linkit.otsikko.' + $scope.type;
        $scope.eohje = 'tarjonta.linkit.eohje.' + $scope.type;
        $scope.items = [];
        $scope.ok = $modalInstance.close;
        var base = $scope.type === 'koulutus' ? 'hakukohde' : 'koulutus';
        var ret = $scope.type === 'koulutus' ?
            TarjontaService.getKoulutuksenHakukohteet($scope.oid) :
            TarjontaService.getHakukohteenKoulutukset($scope.oid);
        ret.then(function(items) {
            $scope.items = _.map(items, function(item) {
                return _.extend(item, {
                    url: '#/' + base + '/' + item.oid
                });
            });
        });
    };

    function showListing(params) {
        var scope = $rootScope.$new(true);
        _.extend(scope, params);
        $modal.open({
            controller: LinksDialogCtrl,
            templateUrl: 'partials/common/koulutusHakukohdeListing.html',
            scope: scope
        });
    }

    return showListing;
});