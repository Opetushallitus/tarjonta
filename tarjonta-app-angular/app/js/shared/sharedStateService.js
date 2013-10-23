var app = angular.module('SharedStateService', ['ngResource','config']);


app.service('SharedStateService',function($resource, $log,$q, Config){

    var sharedService = {};

    this.addToState = function(name,value) {
       sharedService[name]  = value;
    };

    this.getFromState = function(name) {
      return sharedService[name];
    };


    this.removeState = function(name) {
        sharedService[name] = undefined;
    };
    
    /**
     * palauta tila olio
     */
    this.state = function(){
    	return sharedService;
    };

});