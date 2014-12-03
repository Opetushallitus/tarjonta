var app = angular.module('ValidDecimal', []);

app.directive('validDecimal', function () {

    var removeLastChar = function(str) {
        return str.substring(0, str.length - 1)
    };

    var replaceCommaWithDot = function(str) {
        return str.replace(/\,/g, '.');
    };

    var getDotCount = function(str) {
        return (str.match(/\./g) || []).length;
    };

    var removeDuplicateDots = function(str) {
        if(getDotCount(str) > 1) {
            var result = "";
            var indexOfFirstDot = str.indexOf('.');
            for(var i = 0; i < str.length; i++) {
                var char = str.charAt(i);
                if(char !== '.' || indexOfFirstDot === i) {
                    result = result + char;
                }
            }
            return result;
        } else {
            return str;
        }
    };

    var removeExtraCharacters = function(str) {
        str = str.replace(/[^0-9.]/g, "");
        str = removeDuplicateDots(str);
        return str;
    };

    var removeDotFromStart = function(str) {
        if(str.indexOf('.') === 0) {
            str = str.substring(1);
        }
        return str;
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
                newValue = removeExtraCharacters(newValue);
                newValue = removeDotFromStart(newValue);

                if(newValue !== val) {
                    ngModelCtrl.$setViewValue(newValue);
                    ngModelCtrl.$render();
                }

                return newValue;
            });
        }
    };
});