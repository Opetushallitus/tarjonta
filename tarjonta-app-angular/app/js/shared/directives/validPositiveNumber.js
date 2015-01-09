var app = angular.module('ValidPositiveNumber', []);
app.directive('validPositiveNumber', function() {
    var removeExtraCharacters = function(str) {
        str = str.replace(/[^0-9]/g, '');
        return str;
    };
    return {
        require: '?ngModel',
        link: function(scope, element, attrs, ngModelCtrl) {
            if (!ngModelCtrl) {
                return;
            }
            // Näytä tyhjänä, jos rajapinta palauttaa 0
            scope.$watch(attrs.ngModel, function(nv, ov) {
                if (nv === 0 && (nv === ov || !ov)) {
                    ngModelCtrl.$setViewValue('');
                    ngModelCtrl.$render();
                }
            });
            ngModelCtrl.$parsers.push(function(val) {
                if (val.length === 0) {
                    ngModelCtrl.$setValidity('bounds', true);
                    return val;
                }
                var newValue = val;
                newValue = removeExtraCharacters(newValue);
                if (newValue !== val) {
                    ngModelCtrl.$setViewValue(newValue);
                    ngModelCtrl.$render();
                }

                var numberValue = parseInt(newValue);
                if (!isNaN(numberValue)) {
                    ngModelCtrl.$setValidity('bounds', numberValue < 1000 && numberValue > 0);
                }

                return newValue;
            });
        }
    };
});