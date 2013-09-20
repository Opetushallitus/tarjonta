'use strict';

/* Filters */

var app = angular.module('tarjontaApp.filters', []);


app.filter('interpolate', ['version', function(version) {
        return function(text) {
            return String(text).replace(/\%VERSION\%/mg, version);
        };
    }]);

/**
 *For simple paging, use "startFrom" filter to set the star page / beginning AND
 *basic "limitTo" filter to set the "page size".
 *
 *For example: ng-repeat="p in pics | startFrom: pageSize * page | limitTo: pageSize"
 */
app.filter('startFrom', function() {
    return function(input, start) {
        start = +start; //parse to int
        return input.slice(start);
    };
});

/**
 * @returns reversed string or array
 * @throws exception when input is not array or string
 */
app.filter('reverse', function() {
    return function(input) {
        // console.log("reverse() " + input + " type = "+ (typeof(input)));

        if (typeof input === "string") {
            return input.split("").reverse().join("");
        }

        if (input instanceof Array) {
            return input.reverse();
        }

        // console.log("  sheisse... not string or array, gonna fail now");

        throw "filter reverse expects arcument to be array or string";
    };

});
