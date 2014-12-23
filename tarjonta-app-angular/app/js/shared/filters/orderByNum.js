angular.module('OrderByNumFilter', []).filter('orderByNum', function() {
    return function(input, key) {
        if (angular.isUndefined(input) || input === null || angular.isUndefined(input.sort)) {
            return input;
        }
        input.sort(function(a, b) {
            var sa = 1 * (key ? a[key] : a);
            var sb = 1 * (key ? b[key] : b);
            return sa > sb ? 1 : sa < sb ? -1 : 0;
        });
        return input;
    };
});