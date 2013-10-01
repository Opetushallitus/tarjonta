angular.module('loading', [])

.factory('loadingService', function() {
  var service = {
    requestCount: 0,
    modal: false,
    isLoading: function() {
      return service.requestCount > 0;
    }
  };
  return service;
})

.factory('onStartInterceptor', function(loadingService) {
    return function (data, headersGetter) {
    	if (loadingService.requestCount==0) {
    		loadingService.modal = true;
    	}
        loadingService.requestCount++;
        return data;
    };
})

.factory('onCompleteInterceptor', function(loadingService, $q) {
  return function(promise) {
    var decrementRequestCountSuccess = function(response) {
        loadingService.requestCount--;
        return response;
    };
    var decrementRequestCountError = function(response) {
        loadingService.requestCount--;
        return $q.reject(response);
    };
    return promise.then(decrementRequestCountSuccess, decrementRequestCountError);
  };
})

.config(function($httpProvider) {
    $httpProvider.responseInterceptors.push('onCompleteInterceptor');
})

.run(function($http, onStartInterceptor) {
    $http.defaults.transformRequest.push(onStartInterceptor);
})

.controller('LoadingCtrl', function($scope, $rootElement, loadingService) {
    $scope.$watch(function() {
        return loadingService.isLoading();
    }, function(value) {
        $scope.loading = value;
        if(value) {
          $rootElement.addClass('spinner');
        } else {
          $rootElement.removeClass('spinner');
        }
    });
    
    $scope.isModal = function() {
    	return loadingService.modal;
    }
    
    $scope.demodalize = function() {
    	loadingService.modal = false;
    }

    
});