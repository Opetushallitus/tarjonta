/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 */
var app = angular.module('DateTimePicker', ['ngResource']);
app.directive('dateTimePicker', function() {
    return {
        restrict: 'E',
        replace: true,
        scope: {
            selecteddatetime: '='
        },
        controller: function($scope) {
            var getTimeAsString = function(timeAsMilliSeconds) {
                var momentDate = moment(timeAsMilliSeconds);
                var formattedDateStr = momentDate.format('DD.M.YYYY HH:mm');
                return formattedDateStr;
            };
            $scope.$watch('selecteddatetime', function(newVal, oldVal) {
                $scope.valueToShow = getTimeAsString(newVal);
            });
        },
        template: '<div>' + '<input type="text" ng-model="valueToShow" readonly data-date-format="yyyy-mm-dd hh:ii"' +
                        ' name="recipientDateTime" data-date-time required>' + '</div>',
        link: function(scope, element, attrs, ngModel) {
            var input = element.find('input');
            //If given time as initial value try parse it as string and show it as initial value
            var initialDateObj;
            if (scope.selecteddatetime !== undefined) {
                var momentDateObj = moment(scope.selecteddatetime);
                initialDateObj = momentDateObj.format('DD.M.YYYY HH:mm');
            }
            if (initialDateObj === undefined) {
                var initialMomentDate = moment();
                scope.selecteddatetime = initialMomentDate.valueOf();
                initialDateObj = initialMomentDate.format('DD.M.YYYY HH:mm');
            }
            scope.valueToShow = initialDateObj;
            input.datetimepicker({
                format: 'dd.m.yyyy hh:ii',
                showMeridian: false,
                language: 'fi',
                autoclose: true,
                todayBtn: true,
                todayHighlight: true,
                initialDate: scope.valueToShow
            });
            element.bind('blur keyup change', function() {
                var selectedDateTimeObject = moment(input.val(), 'DD.M.YYYY HH:mm');
                scope.selecteddatetime = selectedDateTimeObject.valueOf();
            });
        }
    };
});