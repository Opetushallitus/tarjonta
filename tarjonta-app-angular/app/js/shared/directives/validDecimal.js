var app = angular.module('ValidDecimal', []);

app.directive('validDecimal', function () {
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
                var lastChar = val.charAt(val.length - 1);
                if (isNaN(lastChar)) {
                    if ('.' === lastChar) {
                        var dotCount = (val.match(/\./g) || []).length;
                        if (dotCount > 1) {
                            newValue = val.substring(0, val.length - 1);
                        }
                    } else {
                        newValue = val.substring(0, val.length - 1);
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