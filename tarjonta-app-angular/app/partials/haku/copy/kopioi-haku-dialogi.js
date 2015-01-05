/* Controllers */
var app = angular.module('app.haku.copy.ctrl', [
    'app.haku.ctrl',
    'Process'
]);
app.controller('HakuCopyController', [
    '$injector',
    '$q',
    '$scope',
    'Koodisto',
    '$modal',
    'OrganisaatioService',
    'AuthService',
    '$log',
    'HakuV1Service',
    'ProcessV1Service', function($injector, $q, $scope, Koodisto, $modal, OrganisaatioService,
                                 AuthService, $log, HakuV1Service, ProcessV1Service) {
        'use strict';

        var loadingService = $injector.get('loadingService');
        var oid = $scope.model.hakux.result.oid;
        // mistä kopioidaan
        $scope.view = 0;
        $scope.progress = 0;
        // hae ensin kaikki prosessit ja tarkista ettei ole jo käynnissä
        ProcessV1Service.listProcesses().then(function(processes) {
            for (var i = 0; i < processes.length; i++) {
                var process = processes[i];
                var fromHaku = process.parameters['haku.oid.from'];
                console.log('from haku:', fromHaku);
                if (oid === fromHaku) {
                    console.log('kopiointi on jo k\xE4ynniss\xE4');
                    return process.id;
                }
            }
        }).then(function(processId) {
            //pollaa prosessin statusta
            function startPolling(id) {
                loadingService.setSpinnerEnabled(false);
                ProcessV1Service.startPolling(id, 2000, function(res) {
                    console.log('polling result:', res);
                    if (res.parameters) {
                        $scope.prepareKoulutusCount = res.parameters.prepare_komoto_processed;
                        $scope.prepareKoulutusMax = res.parameters.prepare_komoto_total;
                        $scope.prepareHakukohdeCount = res.parameters.prepare_hakukohde_processed;
                        $scope.prepareHakukohdeMax = res.parameters.prepare_hakukohde_total;
                        $scope.commitKoulutusCount = res.parameters.commit_komoto_processed;
                        $scope.commitKoulutusMax = res.parameters.commit_komoto_total;
                        $scope.commitHakukohdeCount = res.parameters.commit_hakukohde_processed;
                        $scope.commitHakukohdeMax = res.parameters.commit_hakukohde_total;
                        $scope.view = res.parameters.process_step;
                    }
                    if ($scope.view === 'COMMIT') {
                        $scope.progress = Math.round((Number($scope.commitKoulutusCount)
                            + Number($scope.commitHakukohdeCount)) * 100
                            / (Number($scope.commitKoulutusMax)
                            + Number($scope.commitHakukohdeMax)));
                    }
                    else if ($scope.view === 'PREPARE') {
                        $scope.progress = Math.round((Number($scope.prepareKoulutusCount)
                            + Number($scope.prepareHakukohdeCount)) * 100
                            / (Number($scope.prepareKoulutusMax)
                            + Number($scope.prepareHakukohdeMax)));
                    }
                    else if ($scope.view === 'DONE') {
                        $scope.progress = 100;
                        ProcessV1Service.stopPolling(id);
                        console.log('service says I am done?');
                        loadingService.setSpinnerEnabled(true);
                    }
                    else {
                        $scope.progress = 0;
                    }
                });
            }
            if (processId) {
                //prosessi oli jo olemassa ala pollaamaan statusta...
                startPolling(processId);
            }
            else {
                // käynnistä kopioinnin 1. vaihe
                HakuV1Service.copy(oid).then(function(res) {
                    if (res.status === 'OK') {
                        var id = res.result;
                        startPolling(id);
                    }
                });
            }
            // näytä näkymä 1 (kohteen valinta)
            // hae haut (vuosi +1)
            var vuosi = $scope.model.hakux.result.hakukausiVuosi;
            var kausi = $scope.model.hakux.result.hakukausiUri;
            HakuV1Service.search({
                HAKUKAUSI: kausi,
                HAKUVUOSI: vuosi + 1
            }).then(function(hakutulos) {
                $scope.hakutulos = hakutulos;
            });
        });
        $scope.hakuChanged = function(haku) {
            console.log('haku now:', haku);
        };
        $scope.close = function() {
            console.log('suljetaan dialogi');
            $scope.kopioiHakuDialog.close();
        };
    }
]);