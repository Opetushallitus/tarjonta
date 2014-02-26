'use strict';

// routing
angular.module('app').config([ '$routeProvider', function($routeProvider) {
  console.log("adding hakukausi routes");
  $routeProvider.when("/hakukausi", {
    templateUrl : "partials/hakukausi/hakukausi.html"
  });
} ])

// controller
.controller("HakukausiController",
    [ "Koodisto", "$scope", "ParameterService", function HakukausiController(Koodisto, $scope, Parameter) {

      var vuosi = new Date().getFullYear();
      $scope.model={parameter:{}};
      
      $scope.kausivuosi={}
      //kausi-vuoden vuodet, kaksi vuotta taaksepäin
      $scope.vuodet = [];
      for (var v = vuosi-2; v < vuosi + 10; v++) {
        $scope.vuodet.push({vuosi:v,label:v});
      }

      var getKausiVuosiIdentifier=function(){
        return $scope.kausivuosi.kausi + $scope.kausivuosi.vuosi.vuosi
      }
      
      var  isKausiVuosiSelected=function() {
        console.log("checking to see if vuosi kausi proper");
        if($scope.kausivuosi.vuosi && $scope.kausivuosi.kausi){
          var kausivuosi = getKausiVuosiIdentifier();
          console.log("loading data", kausivuosi);
          Parameter.haeHakukaudenParametrit(kausivuosi, $scope.model.parameter);
        } else {
          console.log("it was not!");
        }
      };
      
      
      var saveParameters=function(){
        console.log("saving!!!", $scope);
        var kausivuosi = getKausiVuosiIdentifier();
        Parameter.tallennaHakukaudenParametrit(kausivuosi, $scope.model.parameter);
      }
      
      $scope.saveParameters=saveParameters;

      $scope.vuosiChanged=function(data){
        console.log("vuosi changed");
        isKausiVuosiSelected();
      };

      $scope.kausiChanged=function(data){
        console.log("kausi changed");
        isKausiVuosiSelected();
      };
      

    }
    ]
)


// directives
/**
 * Päivämäärän editointi rivi
 */
.directive('tParamEditDate', function() {
  return {
    restrict: 'A',
    scope:true,
    templateUrl: 'partials/hakukausi/edit-date.html',
    link:function(scope, element, attrs){
      scope.name = attrs.name;
      scope.nameb = attrs.name + "AM";  //"aina valintojen..."
    }
      
  };
})
/**
 * Numeron editointi rivi
 */
.directive('tParamEditNumber', function() {
  return {
    restrict: 'A',
    scope:true,
    templateUrl: 'partials/hakukausi/edit-number.html',
    link:function(scope, element, attrs){
      scope.name = attrs.name;
    }
      
  };
})
/**
 * Stringin editointi
 */
.directive('tParamEditString', function() {
  return {
    restrict: 'A',
    scope:true,
    templateUrl: 'partials/hakukausi/edit-number.html',
    link:function(scope, element, attrs){
      scope.name = attrs.name;
    }
      
  };
})
/**
 * Päivämäärävälin editointi rivi
 */
.directive('tParamEditDateRange', function() {
  return {
    restrict: 'A',
    scope:true,
    templateUrl: 'partials/hakukausi/edit-date-range.html',
      link:function(scope, element, attrs){
        scope.names = attrs.name + "_S";
        scope.namee = attrs.name + "_E";
        scope.nameb = attrs.name + "M"; //"aina valintojen muuttuessa..."
      }
  }
})

/**
 * Väliotsikko rivi
 */
.directive('tSubTitle', function() {
  return {
    restrict: 'A',
    scope:true,
    templateUrl: 'partials/hakukausi/title.html',
      link:function(scope, element, attrs){
      }
  }
})

/**
 * Helppi rivi
 */
.directive('tHelp', function() {
  return {
    restrict: 'A',
    scope:true,
    templateUrl: 'partials/hakukausi/help.html',
      link:function(scope, element, attrs){
      }
  }
})

/**
 * Lokalisoinnin oletusarvo
 */
.directive('tUseDefaultTt', function(){
  return {
    restrict: 'A',
    scope:true,
    link: function(scope, element, attrs){
      scope.tUseDefaultTt = attrs.tUseDefaultTt;
      scope.tUseTtKey = attrs.tUseTtKey;
    }
  }
})


/**
 * Aina valintojen muuttuessa
 */
.directive('tAlways', function(){
  return {
    restrict: 'A',
    link: function(scope, element, attrs){
      scope.always=true;
      scope.tUseTtKey = attrs.tUseTtKey;
    }
  }
})

/**
 * Help key
 */
.directive('tHelp', function(){
  return {
    restrict: 'A',
    scope:true,
    link: function(scope, element, attrs){
      scope.help = attrs.tHelp;
    }
  }
})

