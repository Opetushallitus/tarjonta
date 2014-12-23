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
var app = angular.module('ShowErrors', [
    'localisation',
    'Logging'
]);
/**
 * Usage:
 *
 * <show-errors form="hakuForm" field="hakuKausi" tt-prefix="haku.edit" />
 *   equals to
 * <show-errors form="hakuForm" field="hakuKausi" tt-prefix="haku.edit" field-check="true" custom-check="false" />
 *
 * Will generate in case of errors:
 *
 *   <p class="error" tt="haku.edit.hakuForm.error">Tarkista kentän arvo!</p>
 *
 * IFF all of this applies:
 *
 * 1. Form is modified
 * 2. Field is modified
 * 3. "custom-check" is true OR
 * 4. "field-check" is true AND field has error(s)
 *
 * Field has errors if field has $error map AND
 * it has true in any of fields: "required", "invalid", "max", "min", "url", "email", "number"
 *
 * Translation key is generated from:
 *
 *   tt-prefix + "." + field + ".error"
 *
 * @author mlyly
 */
app.directive('showErrors', function($log, LocalisationService) {
    'use strict';
    $log = $log.getInstance('<showErrors>');
    return {
        restrict: 'EA',
        templateUrl: 'js/shared/directives/showErrors.html',
        replace: true,
        scope: {
            form: '=',
            field: '@',
            ttPrefix: '@',
            // attribute "tt-prefix"
            fieldCheck: '=?',
            // attribute "field-check" default TRUE
            customCheck: '=?',
            // attribute "custom-check" default FALSE
            debug: '=?'
        },
        controller: function($scope) {
            // $log.info("showErrors()", $scope);
            // Perform field value validations by default, if false no normal "required" etc checks done.
            if (!angular.isDefined($scope.fieldCheck)) {
                $scope.fieldCheck = true;
            }
            if (!angular.isDefined($scope.debug)) {
                $scope.debug = false;
            }
            // If custom check by default finds no errors
            if (!angular.isDefined($scope.customCheck)) {
                $scope.customCheck = false;
            }
            /**
                   * Translation key that is needed to show the error message.
                   */
            $scope.ttKey = $scope.ttPrefix + '.' + $scope.field + '.error';
            $scope.ttValue = LocalisationService.t($scope.ttKey);
            /**
                   * This method decides if the error message should be shown.
                   * True means "yes - show the error".
                   *
                   * @param {type} form
                   * @param {type} field
                   * @returns true if field has error OR custom error handling has detected some errors
                   */
            $scope.errorCheck = function(form, field) {
                if ($scope.debug) {
                    $log.info('errorCheck()', form, field, $scope);
                }
                if (!angular.isDefined(form)) {
                    $log.info('*** Form is not defined!?');
                    return false;
                }
                if ($scope.fieldCheck && !angular.isDefined(form[field])) {
                    $log.info('*** errorCheck - Form field is not defined!? field name = ' + field);
                    return false;
                }
                // Simple check
                var result = angular.isDefined(form[field]) && form[field].$invalid;
                // 1. Is form dirty?
                // 2. Has the customcheck failed?
                // 3. Or is the form field check enabled AND field modified AND in error?
                result = form.$dirty && ($scope.customCheck || $scope.fieldCheck && form[field].$dirty &&
                    form[field].$invalid);
                // Enable logging if you have problems with error checking results...
                if ($scope.debug && result) {
                    $log.info('Field: ' + field + ' has errors! $scope.fieldCheck=', $scope.fieldCheck);
                    if (angular.isDefined(form[field])) {
                        $log.info('  field: ', form[field]);
                    }
                    if (angular.isDefined(form[field].$error)) {
                        $log.info('  errors: ', form[field].$error);
                    }
                }
                if ($scope.debug) {
                    $log.info('  result: ', result);
                }
                return result;
            };
            return $scope;
        }
    };
});