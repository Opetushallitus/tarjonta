var app = angular.module('SharedStateService', [
    'ngResource',
    'config',
    'Logging'
]);
app.service('SharedStateService', function($resource, $log, $q, Config) {
    $log = $log.getInstance('SharedStateService');
    var sharedService = {};
    this.addToState = function(name, value) {
        $log.debug('addToState()', name, value);
        sharedService[name] = value;
    };
    this.getFromState = function(name) {
        $log.debug('getFromState()', name);
        return sharedService[name];
    };
    this.removeState = function(name) {
        $log.debug('removeState()', name);
        sharedService[name] = undefined;
    };
    /**
       * palauta tila olio
       */
    this.state = function() {
        $log.debug('state', sharedService);
        return sharedService;
    };
});