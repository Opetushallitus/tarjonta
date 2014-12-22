/*
 * AUTHOR: http://jsfiddle.net/Ftbkk/
 */
var app = angular.module('zippy', []);
app.directive('zippy', function() {
    return {
        restrict: 'C',
        replace: true,
        transclude: true,
        scope: {
            title: '@zippyTitle'
        },
        template: '<div>' + '<div class="title">{{title}}</div>' + '<div class="body" ng-transclude></div>' + '</div>',
        link: function(scope, element, attrs) {
            // Title element
            var title = angular.element(element.children()[0]);
            // Opened / closed state
            opened = false;
            // Clicking on title should open/close the zippy
            title.bind('click', toggle);
            // Toggle the closed/opened state
            function toggle() {
                opened = !opened;
                element.removeClass(opened ? 'closed' : 'opened');
                element.addClass(opened ? 'opened' : 'closed');
            }
            // initialize the zippy
            toggle();
        },
        controller: function($scope, $timeout) {
            console.log('zippy scope : ', $scope);
            console.log('zippy scope.title : ', $scope.title);
            $timeout(function() {
                console.log('zippy scope title :', $scope.title);
            }, 0);
        }
    };
});