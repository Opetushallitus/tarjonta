var app = angular.module('app.haku.ctrl', [
    'app.haku.list.ctrl',
    'app.haku.review.ctrl',
    'app.haku.edit.ctrl',
    'app.haku.edit.organisations.ctrl'
]);
app.controller('HakuRoutingController', [
    '$scope',
    '$log',
    '$routeParams',
    '$route',
    'dialogService',
    'LocalisationService',
    'HakuV1Service',
    'HakuV1',
    'KoodistoURI', function HakukohdeRoutingController($scope, $log, $routeParams, $route,
                   dialogService, LocalisationService, HakuV1Service, HakuV1, KoodistoURI) {
        'use strict';

        $log = $log.getInstance('HakuRoutingController');
        $log.info('HakuRoutingController()', $routeParams);
        $log.info('$route: ', $route);
        $log.info('$route action: ', $route.current.$$route.action);
        $log.info('SCOPE: ', $scope);
        /**
         * Resolve haku name.
         *
         * TODO jossain on varmasti tähän joku utiliteetti jo olemassa?
         *
         * @param {type} haku
         * @returns String resolved haun nimi
         */
        $scope.getHaunNimi = function(haku) {
            if (!angular.isDefined(haku)) {
                return LocalisationService.t('haku.eiHakua');
            }
            // Is the name hashmap?
            if (!angular.isObject(haku.nimi)) {
                return haku.nimi;
            }
            // huoh... It's map then - try to resolve the name
            var nimi = haku.nimi['kieli_' + LocalisationService.getLocale()];
            // Try languages in preferred order
            nimi = angular.isDefined(nimi) ? nimi : haku.nimi.kieli_fi;
            nimi = angular.isDefined(nimi) ? nimi : haku.nimi.kieli_sv;
            nimi = angular.isDefined(nimi) ? nimi : haku.nimi.kieli_en;
            // Just get first language
            nimi = angular.isDefined(nimi) ? nimi : haku.nimi[0];
            // Still no name, give up
            nimi = angular.isDefined(nimi) ? nimi : LocalisationService.t('haku.eiNimea');
            return nimi;
        };
        /**
         * Delete haku. If verification is requested a dialog asking "really?" will be displayed.
         *
         * @param {type} haku
         * @param {type} verify if true or undefined the verification dialog will be show
         * @returns {unresolved}
         */
        $scope.doDeleteHaku = function(haku, verify) {
            $log.debug('doDeleteHaku()', haku, verify);
            if (verify || !angular.isDefined(verify)) {
                return dialogService.showSimpleDialog(
                    LocalisationService.t('haku.delete.confirmation'),
                    LocalisationService.t(
                        'haku.delete.confirmation.description',
                        [$scope.getHaunNimi(haku)]
                    ),
                    LocalisationService.t('ok'),
                    LocalisationService.t('cancel')
                ).result.then(function(deleteVerified) {
                    if (deleteVerified) {
                        $log.info('  delete verified.');
                        return $scope.doDeleteHakuAction(haku);
                    }
                    else {
                        $log.info('  delete cancelled.');
                        return false;
                    }
                });
            }
            else {
                // No verification requested
                $log.info('  delete verification not requested, just do it.');
                return $scope.doDeleteHakuAction(haku);
            }
        };
        /**
         * Request Haku deletion from server side.
         *
         * @param {type} haku
         * @returns {unresolved}
         */
        $scope.doDeleteHakuAction = function(haku) {
            $log.debug('doDeleteHakuAction()', haku);
            return HakuV1Service.delete(haku.oid).then(function(result) {
                $log.info('delete result', result);
                if (result.status == 'OK') {
                    $log.info('SHOW DELETE DONE DIALOG');
                    dialogService.showSimpleDialog(
                        LocalisationService.t('haku.delete.ok'),
                        LocalisationService.t('haku.delete.ok.description'),
                        LocalisationService.t('ok'),
                        undefined
                    );
                    return true;
                }
                else {
                    var errorMessage = '<ul>';
                    angular.forEach(result.errors, function(error) {
                        var msg = LocalisationService.t(
                            error.errorMessageKey,
                            error.errorMessageParameters
                        );
                        errorMessage = errorMessage + '<li>' + msg + '</li>';
                    });
                    errorMessage = errorMessage + '</ul>';
                    var desciptionParams = [errorMessage];
                    dialogService.showSimpleDialog(
                        LocalisationService.t('haku.delete.failed'),
                        LocalisationService.t('haku.delete.failed.description', desciptionParams),
                        LocalisationService.t('ok'),
                        undefined
                    );
                    return false;
                }
            });
        };
        /**
         * @returns true if current haku is JATKUVA_HAKU
         */
        $scope.isHakuJatkuvaHaku = function(haku) {
            // Ignore koodisto versions in comparison
            return KoodistoURI.compareKoodi(
                KoodistoURI.HAKUTAPA_JATKUVAHAKU,
                haku.hakutapaUri,
                true
            );
        };
    }
]);