'use strict';

/* Controllers */

var app = angular.module('app.haku.copy.ctrl', [ 'app.haku.ctrl', 'Process' ]);

app.controller('HakuCopyController', [ '$injector', '$q', '$scope', 'Koodisto', '$modal', 'OrganisaatioService', 'AuthService', '$log', 'HakuV1Service',
    'ProcessV1Service', function($injector, $q, $scope, Koodisto, $modal, OrganisaatioService, AuthService, $log, HakuV1Service, ProcessV1Service) {

      console.log("hakux:", $scope.model.hakux);
      var loadingService = $injector.get('loadingService');
      var oid = $scope.model.hakux.result.oid; // mistä kopioidaan

      $scope.view = 0;

      // hae ensin kaikki prosessit ja tarkista ettei ole jo käynnissä

      ProcessV1Service.listProcesses().then(function(processes) {
        for ( var i = 0; i < processes.length; i++) {
          var process = processes[i];
          var fromHaku = process.parameters["haku.oid.from"];
          console.log("from haku:", fromHaku);
          if (oid === fromHaku) {
            console.log("kopiointi on jo käynnissä");
            return process.id;
          }
        }
      }).then(function(processId) {
        
        //pollaa prosessin statusta
        function startPolling(id){
          loadingService.setSpinnerEnabled(false);
          ProcessV1Service.startPolling(id, 2000, function(res) {
            if (res.state === 100) {
              ProcessV1Service.stopPolling(id);
              console.log("done?");
              loadingService.setSpinnerEnabled(true);
            }
            $scope.koulutusCount = res.parameters["count.komoto.processed"];
            $scope.koulutusMax = res.parameters["count.total.komoto"];
            $scope.hakukohdeCount = res.parameters["count.hakukohde.processed"];
            $scope.hakukohdeMax = res.parameters["count.total.hakukohde"];
          });
        }
        
        if(processId){ //prosessi oli jo olemassa ala pollaamaan statusta...
          startPolling(processId);
        } else {
          // käynnistä kopioinnin 1. vaihe
          HakuV1Service.copy(oid).then(function(res) {
            if (res.status === "OK") {
              var id = res.result;
              startPolling(id);
            }
          });
        }
        // näytä näkymä 1 (kohteen valinta)
        $scope.view = 1;
        // hae haut (vuosi +1)
        var vuosi = $scope.model.hakux.result.hakukausiVuosi;
        var kausi = $scope.model.hakux.result.hakukausiUri;

        HakuV1Service.search({
          HAKUKAUSI : kausi,
          HAKUVUOSI : vuosi + 1
        }).then(function(hakutulos) {
          $scope.hakutulos = hakutulos;
        });
      });

      $scope.hakuChanged = function(haku) {
        console.log("haku now:", haku);
      };

      $scope.close = function() {
        console.log("suljetaan dialogi");
        $scope.kopioiHakuDialog.close();
      };

      $scope.startPaste = function() {
        var targetHakuOid = $scope.model.targetHaku.oid;
        var processId = $scope.model.processId;
        console.log("target:", targetHakuOid);
        console.log("target-process:", processId);
        $scope.progress = 0;
        $scope.view = 2; // flip view
        console.log("haku oid:", targetHakuOid);
        HakuV1Service.paste(targetHakuOid, processId).then(function(res) {
          var id = res.result;
          $scope.model.processId = id;
          loadingService.setSpinnerEnabled(false);
          ProcessV1Service.startPolling(id, 2000, function(res) {
            console.log("response:", res);
            if (res.state === 100) {
              ProcessV1Service.stopPolling(id);
              console.log("done? changing view to 3");
              $scope.view = 3; // flip view
              loadingService.setSpinnerEnabled(true);
            }
            $scope.progress = res.state;
            console.log("state:", $scope.model.progress);
          });
        });
      };
    } ]);
