// http://stackoverflow.com/a/17364565
angular.module('transclude', [])
 .directive('ngOnce', ['$timeout', function($timeout){
    return {
      restrict: 'EA',
      priority: 500,
      transclude: true,
      template: '<div ng-transclude></div>',
        compile: function (tElement, tAttrs, transclude) {
            return function postLink(scope, iElement, iAttrs, controller) {
                $timeout(scope.$destroy.bind(scope), 0);
            }
        }
    };
}]);