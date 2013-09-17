'use strict';

/* Filters */

var app= angular.module('tarjontaApp.filters', []);

app.filter('interpolate', ['version', function(version) {
    return function(text) {
      return String(text).replace(/\%VERSION\%/mg, version);
    }
  }]);


// http://jsfiddle.net/2ZzZB/56/
app.filter('startFrom', function() {
    return function(input, start) {
        start = +start; //parse to int
        return input.slice(start);
    }
});
