'use strict';

// routing
angular.module('app').config([ '$routeProvider', function($routeProvider) {
  console.log("adding hakukausi routes");
  $routeProvider.when("/hakukausi", {
    templateUrl : "partials/hakukausi/hakukausi.html"
  });
} ])


//form & model directive patching (https://github.com/angular/angular.js/issues/1404)

.config(function($provide) {
    $provide.decorator('ngModelDirective', function($delegate) {
      var ngModel = $delegate[0], controller = ngModel.controller;
      ngModel.controller = ['$scope', '$element', '$attrs', '$injector', function(scope, element, attrs, $injector) {
        var $interpolate = $injector.get('$interpolate');
        attrs.$set('name', $interpolate(attrs.name || '')(scope));
        $injector.invoke(controller, this, {
          '$scope': scope,
          '$element': element,
          '$attrs': attrs
        });
      }];
      return $delegate;
    });
    $provide.decorator('formDirective', function($delegate) {
      var form = $delegate[0], controller = form.controller;
      form.controller = ['$scope', '$element', '$attrs', '$injector', function(scope, element, attrs, $injector) {
        var $interpolate = $injector.get('$interpolate');
        attrs.$set('name', $interpolate(attrs.name || attrs.ngForm || '')(scope));
        $injector.invoke(controller, this, {
          '$scope': scope,
          '$element': element,
          '$attrs': attrs
        });
      }];
      return $delegate;
    });
  })

// controller
.controller("HakukausiController",
    [ "Koodisto", "$scope", "ParameterService", function HakukausiController(Koodisto, $scope, Parameter) {

      //validation pattern, used from form
      $scope.timePattern=/^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$/
      
      var vuosi = new Date().getFullYear();
      $scope.model={parameter:{}};
      
      $scope.kausivuosi={}
      //kausi-vuoden vuodet, kaksi vuotta taaksepäin
      $scope.vuodet = [];
      for (var v = vuosi-2; v < vuosi + 10; v++) {
        $scope.vuodet.push({vuosi:v,label:v});
      }

      var isVuosiKausiValid=function(){
        return $scope.kausivuosi.vuosi && $scope.kausivuosi.kausi;
      }
      
      $scope.isVuosiKausiValid=isVuosiKausiValid;
      
      var getKausiVuosiIdentifier=function(){
        return $scope.kausivuosi.kausi + $scope.kausivuosi.vuosi.vuosi
      }
      
      var  isKausiVuosiSelected=function() {
        if(isVuosiKausiValid()){
          var kausivuosi = getKausiVuosiIdentifier();
          console.log("loading data", kausivuosi);
          $scope.saved=false;
          $scope.model.parameter={};
          Parameter.haeHakukaudenParametrit(kausivuosi, $scope.model.parameter);
        }
      };
      
      
      var saveParameters=function(){
        $scope.saved=true;

        console.log("saving!!!, form:", $scope.hakukausiForm);
        if(!$scope.hakukausiForm.$valid) {
          console.log("invalid data, exiting");
          return;
        }
        var kausivuosi = getKausiVuosiIdentifier();
        Parameter.tallennaHakukaudenParametrit(kausivuosi, $scope.model.parameter);
      };
      
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
 * Required
 */
.directive('tIsRequired', function(){
  return {
    restrict: 'A',
    scope:true,
    link: function(scope, element, attrs){
      scope.tIsRequired = "true"===attrs.tIsRequired;
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

