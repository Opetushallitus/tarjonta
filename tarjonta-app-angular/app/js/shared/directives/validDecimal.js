var app = angular.module('ValidDecimal', []);

app.directive('validDecimal', function () {

    var removeLastChar = function(str) {
        return str.substring(0, str.length - 1)
    };

    var replaceCommaWithDot = function(str) {
        return str.replace(/\,/g, '.');
    };

    var replaceWhitespaceWithEmptySpace = function(str) {
        return str.replace(/\s+/g, '');
    };

    var getLastChar = function(str) {
        return str.charAt(str.length - 1)
    };

    var getDotCount = function(str) {
        return (str.match(/\./g) || []).length;
    };

    return {
        require: '?ngModel',
        link: function (scope, element, attrs, ngModelCtrl) {
            if (!ngModelCtrl) {
                return;
            }

            ngModelCtrl.$parsers.push(function (val) {
                if (val.length === 0) {
                    return val;
                }

                var newValue = val;
                newValue = replaceCommaWithDot(newValue);
                newValue = replaceWhitespaceWithEmptySpace(newValue);

                var lastChar = getLastChar(newValue);
                if (isNaN(lastChar)) {
                    if ('.' === lastChar) {
                        if (getDotCount(newValue) > 1) {
                            newValue = removeLastChar(newValue);
                        }
                    } else {
                        newValue = removeLastChar(newValue);
                    }
                }

                if(newValue !== val) {
                    ngModelCtrl.$setViewValue(newValue);
                    ngModelCtrl.$render();
                }

                return newValue;
            });
        }
    };
});