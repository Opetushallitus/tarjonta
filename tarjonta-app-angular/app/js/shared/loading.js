angular.module('loading', ['localisation'])

.factory('loadingService', function() {
  var service = {
    requestCount: 0,
    operationCount: 0,
    errors: 0,
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
    onFailure: function(req) {
    	console.log("FAIL", req);
    	service.errors++;
    },
    commit: function() {
    	service.requestCount -= service.errors;
    	service.errors = 0;
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
        //loadingService.requestCount--;
        loadingService.onFailure(response);
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
	
	//var ctrl = $scope;
	
	function showErrorDialog() {
		$modal.open({
	        controller: function($scope, $modalInstance) {
	        	$scope.commit = function() {
	        		loadingService.commit();
	                $modalInstance.dismiss();
	        	};
	            $scope.restart = function() {
	            	location.hash = "";
	            	location.reload();
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
        	console.log("SHOW ERROR DIALOG!!");
        	//loadingService.commit();
        }
    });

    $scope.isModal = function() {
    	return loadingService.isModal();
    }

    $scope.isError = function() {
    	return loadingService.errors>0;
    }
    
});