angular.module('loading', ['localisation']).factory('loadingService', function() {
    function getRequest(config) {
        return config.method + ': ' + config.url;
    }
    var service = {
        requestCount: 0,
        requests: {},
        operationCount: 0,
        errors: 0,
        modal: false,
        timeout: null,
        timeoutMinor: false,
        timeoutMajor: false,
        scope: null,
        errorHandlingRequested: null,
        isEnabled: true,
        timeoutShort: window.CONFIG.env['ui.timeout.short'],
        timeoutLong: window.CONFIG.env['ui.timeout.long'],
        isLoading: function() {
            return (service.requestCount > 0 || service.operationCount > 0) && service.isEnabled;
        },
        isModal: function() {
            return service.requestCount > 0;
        },
        isError: function() {
            return service.errors !== 0;
        },
        beforeOperation: function() {
            //console.log("LOADING beforeOperation", service);
            service.operationCount++;
        },
        afterOperation: function() {
            //console.log("LOADING afterOperation", service);
            service.operationCount--;
        },
        beforeRequest: function(config) {
            //console.log("LOADING beforeRequest", service);
            service.modal = true;
            service.startTimeout();
            service.requestCount++;
            if (!service.requests[getRequest(config)]) {
                service.requests[getRequest(config)] = new Date();
            }
        },
        afterRequest: function(success, response) {
            //console.log("LOADING afterRequest "+success, service);
            if (success) {
                service.requestCount--;
            }
            else {
                service.errors++;
            }
            delete service.requests[getRequest(response.config)];
            service.clearTimeout();
        },
        commit: function() {
            //console.log("LOADING commit", service);
            service.requestCount -= service.errors;
            service.errors = 0;
            service.clearTimeout();
        },
        clearTimeout: function() {
            //console.log("LOADING clearTimeout", service);
            if (service.requestCount === 0 && service.timeout !== null) {
                window.clearTimeout(service.timeout);
                service.timeout = null;
                service.timeoutMinor = false;
                service.timeoutMajor = false;
            }
        },
        startTimeout: function() {
            //console.log("LOADING startTimeout", service);
            if (service.timeout !== null) {
                return;
            }
            service.timeout = window.setTimeout(function() {
                service.timeoutMinor = true;
                service.scope.$apply();
                service.logSlowRequests();
                service.timeout = window.setTimeout(function() {
                    service.timeoutMajor = true;
                    service.scope.$apply();
                }, service.timeoutShort);
            }, service.timeoutLong);
        },
        /**
         * Kutsutaan error-callbackissa; estää teknisen virheen dialogin näytön.
         */
        onErrorHandled: function() {
            if (service.errorHandlingRequested) {
                service.errorHandlingRequested = false;
            }
            else if (service.errorHandlingRequested === null) {
                throw 'loadingService.onErrorHandled called from outside of error callback';
            }
        },
        /**
         * Disabloi spinneri
         */
        setSpinnerEnabled: function(enabled) {
            service.isEnabled = enabled;
        },
        logSlowRequests: function() {
            _.each(service.requests, function(startTime, request) {
                var duration = new Date() - startTime;
                request = encodeURIComponent(request);
                $('<img />')
                    .attr('src', '/tarjonta-app/img/tracking.png?request=' + request + '&duration=' + duration)
                    .appendTo('body');
            });
        }
    };
    return service;
}).factory('onStartInterceptor', function(loadingService) {
    return {
        request: function(config) {
            loadingService.beforeRequest(config);
            return config;
        }
    };
}).factory('onCompleteInterceptor', function(loadingService, $q) {
    return function(promise) {
        function decrementRequestCountSuccess(response) {
            loadingService.afterRequest(true, response);
            return response;
        }
        function decrementRequestCountError(response) {
            var ret = $q.reject(response);
            return {
                then: function(callback, errback) {
                    ret.then(callback, function(reason) {
                        loadingService.errorHandlingRequested = true;
                        var ret = errback(reason);
                        loadingService.afterRequest(!loadingService.errorHandlingRequested, response);
                        loadingService.errorHandlingRequested = null;
                    });
                }
            };
        }
        return promise.then(decrementRequestCountSuccess, decrementRequestCountError);
    };
}).config(function($httpProvider) {
    $httpProvider.responseInterceptors.push('onCompleteInterceptor');
    $httpProvider.interceptors.push('onStartInterceptor');
}).controller('LoadingCtrl', function($scope, $rootElement, $modal, loadingService) {
    var ctrl = $scope;
    loadingService.scope = ctrl;
    $scope.restart = function() {
        location.hash = '';
        location.reload();
    };
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
            templateUrl: 'js/shared/loading-error-dialog.html' //scope: ns
        });
    }
    $scope.$watch(function() {
        return loadingService.isLoading();
    }, function(value) {
        $scope.loading = value;
        if (value) {
            $rootElement.addClass('spinner');
        }
        else {
            $rootElement.removeClass('spinner');
        }
    });
    $scope.$watch(function() {
        return loadingService.errors;
    }, function(value, oldv) {
        if (value > 0 && oldv === 0) {
            showErrorDialog();
        }
    });
    $scope.isModal = function() {
        return loadingService.isModal();
    };
    $scope.isError = function() {
        return loadingService.isError();
    };
    $scope.isTimeout = function(major) {
        return major ? loadingService.timeoutMajor : loadingService.timeoutMinor;
    };
});