angular.module('loading', ['localisation'])

.factory('loadingService', function() {
  var service = {
    requestCount: 0,
    operationCount: 0,
    errors: 0,
    timeout: null,
    timeoutMinor: false,
    timeoutMajor: false,
    scope: null,
    
    timeoutShort: window.CONFIG.env["ui.timeout.short"],
    timeoutLong: window.CONFIG.env["ui.timeout.long"],
        
    isLoading: function() {
      return service.requestCount > 0 || service.operationCount > 0;
    },
    isModal: function() {
    	return service.requestCount > 0;
    },
    beforeOperation: function() {
    	service.operationCount++;
    },
    afterOperation: function() {
    	service.operationCount--;
    },
    beforeRequest: function() {
		service.modal = true;
		service.startTimeout();
    	service.requestCount++;
    },
    afterRequest: function(success, req) {
    	if (success) {
        	service.requestCount--;
    	} else {
    		console.log("REQUEST FAILED", req);
    		service.errors++;
    	}
    	clearTimeout();
    },
    commit: function() {
    	service.requestCount -= service.errors;
    	service.errors = 0;
    	clearTimeout();
    },
    clearTimeout: function() {
    	if (service.requestCount==0 && service.timeout!=null) {
    		window.clearTimeout(service.timeout);
    		service.timeout = null;
    		service.timeoutMinor = false;
    		service.timeoutMajor = false;
    	}
    },
    startTimeout: function() {
    	if (service.timeout!=null) {
    		return;
    	}
    	service.timeout = window.setTimeout(function(){
    		service.timeoutMinor = true;
    		service.scope.$apply();
    		
    		service.timeout = window.setTimeout(function(){
        		service.timeoutMajor = true;
        		service.scope.$apply();
        	}, service.timeoutShort);
    		
    	}, service.timeoutLong);
    }
  };
  
  return service;
})

.factory('onStartInterceptor', function(loadingService) {
    return function (data, headersGetter) {
        loadingService.beforeRequest();
        return data;
    };
})

.factory('onCompleteInterceptor', function(loadingService, $q) {
  return function(promise) {
    var decrementRequestCountSuccess = function(response) {
    	loadingService.afterRequest(true, response);
        return response;
    };
    var decrementRequestCountError = function(response) {
    	loadingService.afterRequest(false, response);
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

.controller('LoadingCtrl', function($scope, $rootElement, $modal, loadingService) {
	
	var ctrl = $scope;
	loadingService.scope = ctrl;
	
	$scope.restart = function() {
    	location.hash = "";
    	location.reload();
	}
	
	function showErrorDialog() {
		$modal.open({
	        controller: function($scope, $modalInstance) {
	        	$scope.commit = function() {
	        		loadingService.commit();
	                $modalInstance.dismiss();
	        	};
	            $scope.restart = function() {
	            	ctrl.restart();
	            };
	        },
	        templateUrl: "js/shared/loading-error-dialog.html"
	        //scope: ns
	    });
	}
	
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

    $scope.$watch(function() {
        return loadingService.errors;
    }, function(value, oldv) {
        if(value>0 && oldv==0) {
        	showErrorDialog();
        }
    });

    $scope.isModal = function() {
    	return loadingService.isModal();
    }

    $scope.isError = function() {
    	return loadingService.errors>0;
    }
    
    $scope.isTimeout = function(major) {
    	return major ? loadingService.timeoutMajor : loadingService.timeoutMinor;
    }
    
});