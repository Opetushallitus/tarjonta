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

'use strict';

var app = angular.module('ShowErrors', ['localisation']);

/**
 * Usage:
 *
 * <show-errors form="hakuForm" field="hakuKausi" tt-prefix="haku.edit" />
 *
 * Will generate:
 *
 *   <p class="error" ttt="haku.edit.hakuForm.error">Tarkista kentän arvo!</p>
 *
 * IFF all of this applies:
 *
 * 1. Form has been modified
 * 2. Form has defined field (name="hakuKausi")
 * 3. Form field has "$error" map
 * 4. Error map has true value in: "required", "invalid", "max", "min", "url", "email", "number" fields.
 *
 * Translation key is generated from:
 *
 *   tt-prefix + "." + field + ".error"
 *
 */
app.directive('showErrors', function($log, LocalisationService) {

    return {
        restrict: 'E',
        templateUrl: "js/shared/directives/showErrors.html",
        replace: true,
        scope: {
            form: "=",
            field: "@",
            ttPrefix: "@"
        },
        controller: function($scope) {
            // $log.info("showErrors()", $scope);

            /**
             * Translation key that is needed to show the error message.
             */
            $scope.ttKey = $scope.ttPrefix + "." + $scope.field + ".error";

            /**
             * This method decides if the error message should be shown.
             *
             * @param {type} form
             * @param {type} field
             * @returns {form.$dirty|@var;result}
             */
            $scope.errorCheck = function(form, field) {
                // $log.info("errorCheck()", form, field);

                if (!angular.isDefined(form)) {
                    $log.info("*** Form is not defined!");
                    return false;
                }

                if (!angular.isDefined(form[field])) {
                    $log.info("*** Form field is not defined! field name = " + field);
                    return false;
                }

                if (!angular.isDefined(form[field].$error)) {
                    $log.info("*** Form field $error is not defined! field name = " + field);
                    return false;
                }

                // Only check when form is dirty
                var result = form.$dirty;

                // Any error map available?
                var result = result && angular.isDefined(form[field]);
                var result = result && angular.isDefined(form[field].$error);

                // Skip test if field is unmodified?
                result = result && (!form[field].$error.pristine || form[field].$error.dirty);

                // Check spesific errors if needed
                result = result && angular.isDefined(form[field].$error) && (
                        form[field].$error.required ||
                        form[field].$error.invalid ||
                        form[field].$error.min ||
                        form[field].$error.max ||
                        form[field].$error.url ||
                        form[field].$error.number
                        );

                if (result) {
                    $log.info("Field: " + field + " has errors! ", form[field].$error);
                }

                return result;
            };

            return $scope;
        }
    }

});
