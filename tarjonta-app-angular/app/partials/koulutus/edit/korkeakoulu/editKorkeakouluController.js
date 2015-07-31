var app = angular.module('app.edit.ctrl.kk', [
    'Koodisto',
    'Yhteyshenkilo',
    'ngResource',
    'ngGrid',
    'imageupload',
    'MultiSelect',
    'OrderByNumFilter',
    'localisation',
    'MonikielinenTextField',
    'ControlsLayout'
]);
app.controller('EditKorkeakouluController', function EditKorkeakouluController($scope, Config, $modal, $q,
                                            AuthService, OrganisaatioService, Koodisto) {

    $scope.tutkintoDialogModel = {};
    /**
     * Save koulutus data to tarjonta-service database.
     * TODO: strict data validation, exception handling and optimistic locking
     */
    $scope.saveLuonnos = function() {
        $scope.saveByStatus('LUONNOS', $scope.koulutusForm, $scope.CONFIG.TYYPPI, $scope.customCallbackAfterSave);
    };
    $scope.saveValmis = function() {
        $scope.saveByStatus('VALMIS', $scope.koulutusForm, $scope.CONFIG.TYYPPI, $scope.customCallbackAfterSave);
    };
    $scope.customCallbackAfterSave = function(saveResponse) {
        if (saveResponse.status === 'OK') {
            $scope.$broadcast('onImageUpload', '');
            //save images
            $scope.getLisatietoKielet($scope.model, $scope.uiModel, true);
        }
    };

    $scope.tutkintoDialogModel.open = function(type) {
        type = type || 'changeKoulutus';

        var org = $scope.model.organisaatio.oid;

        var orgPromises = [];

        var deferred = $q.defer();
        orgPromises.push(deferred.promise);

        OrganisaatioService.haeOppilaitostyypit(org).then(function(oppilaitostyypit) {
            Koodisto.getAlapuolisetKoodiUrit(oppilaitostyypit, 'koulutusasteoph2002')
                .then(function(koulutusasteKoodit) {
                    deferred.resolve(koulutusasteKoodit.uris);
                });
        });
        $q.all(orgPromises).then(function(data) {
            var uris = _.chain(data).flatten().uniq().value();

            $modal.open({
                scope: $scope,
                templateUrl: 'partials/koulutus/edit/korkeakoulu/selectTutkintoOhjelma.html',
                controller: 'SelectTutkintoOhjelmaController',
                resolve: {
                    targetFilters: function() {
                        return uris;
                    }
                }
            }).result.then(function(result) {

                var actions = {
                    sisaltyvaKoulutus: function() {
                        $scope.model.sisaltyvatKoulutuskoodit = $scope.model.sisaltyvatKoulutuskoodit || {};
                        _.defaults($scope.model.sisaltyvatKoulutuskoodit, {
                            meta: {},
                            uris: {}
                        });
                        $scope.model.sisaltyvatKoulutuskoodit.meta[result.koodiUri] = {
                            nimi: result.koodiNimi,
                            arvo: result.koodiArvo
                        };
                        $scope.model.sisaltyvatKoulutuskoodit.uris[result.koodiUri] = result.koodiVersio;
                    },
                    changeKoulutus: function() {
                        $scope.model.koulutuskoodi = {
                            arvo: result.koodiArvo,
                            nimi: result.koodiNimi,
                            uri: result.koodiUri,
                            versio: result.koodiVersio
                        };
                    }
                };

                if (result) {
                    actions[type]();
                }
            });
        });
    };

    $scope.removeSisaltyvaKoulutus = function(koodi) {
        delete $scope.model.sisaltyvatKoulutuskoodit.uris[koodi];
        delete $scope.model.sisaltyvatKoulutuskoodit.meta[koodi];
    };

    $scope.isKandiUri = function() {
        var kandiObj = $scope.model.kandidaatinKoulutuskoodi;
        return angular.isDefined(kandiObj) && kandiObj !== null && angular.isDefined(kandiObj.uri) &&
            kandiObj.uri.length > 0;
    };
    $scope.init({
        childScope: $scope
    });
});