'use strict';

/* Controllers */

var app = angular.module('app.haku.copy.ctrl', [ 'app.haku.ctrl', 'Process' ]);

app.controller('HakuCopyController', [ '$location', '$q', '$scope', 'Koodisto', '$modal', 'OrganisaatioService', 'AuthService', '$log', 'HakuV1Service',
    'ProcessV1Service', function($location, $q, $scope, Koodisto, $modal, OrganisaatioService, AuthService, $log, HakuV1Service, ProcessV1Service) {

      console.log("hakux:", $scope.model.hakux);

      $scope.view=0;

      //hae ensin kaikki prosessit ja tarkista ettei ole jo käynnissä
      
      ProcessV1Service.listProcesses().then(function(processes) {
        console.log("alive processes:", processes);
      }).then(function(){
        //kaikki ok ?
        
        //käynnistä 1. vaihe
        var oid = $scope.model.hakux.result.oid; //mistä kopioidaan
        console.log("hakuoid:", oid);
        HakuV1Service.copy(oid).then(function(res) {
          console.log("copy response:", res);
          if(res.status==="OK") {
            var id  = res.result;
            $scope.model.processId=id;
            ProcessV1Service.startPolling(id, 2000, function(res){
              console.log("response:", res);
              if(res.state===100){
                ProcessV1Service.stopPolling(id);
                console.log("done?");
              }
              $scope.progress=res.state;
              console.log("state:", $scope.model.progress);
            });
          }
        });
        
        //näytä näkymä 1 (kohteen valinta)
        $scope.view=1;
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
        var targetHakuOid=$scope.model.targetHaku.oid;
        var processId=$scope.model.processId;
        console.log("target:", targetHakuOid);
        console.log("target-process:", processId);
        $scope.progress=0;
        $scope.view=2; //flip view
        console.log("haku oid:", targetHakuOid);
        HakuV1Service.paste(targetHakuOid, processId).then(function(res) {
          var id  = res.result;
          $scope.model.processId=id;
          ProcessV1Service.startPolling(id, 2000, function(res){
            console.log("response:", res);
            if(res.state===100){
              ProcessV1Service.stopPolling(id);
              console.log("done? changing view to 3");
              $scope.view=3; //flip view
            }
            $scope.progress=res.state;
            console.log("state:", $scope.model.progress);
          });
        });
      };
    } ]);
