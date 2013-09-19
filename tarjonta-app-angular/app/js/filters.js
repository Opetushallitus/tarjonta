'use strict';

/* Filters */

angular.module('tarjontaApp.filters', [])

.filter('interpolate', ['version', function(version) {
    return function(text) {
      return String(text).replace(/\%VERSION\%/mg, version);
    };
  }])


// http://jsfiddle.net/2ZzZB/56/
.filter('startFrom', function() {
    return function(input, start) {
        start = +start; //parse to int
        return input.slice(start);
    };
})

.filter('reverse', function() {
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
