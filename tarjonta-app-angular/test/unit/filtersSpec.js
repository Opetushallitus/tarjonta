'use strict';

/* jasmine specs for filters go here */

describe('filter', function() {
    beforeEach(module('tarjontaApp.filters'));

    describe('interpolate', function() {
        beforeEach(module(function($provide) {
            $provide.value('version', 'TEST_VER');
        }));

        it('should replace VERSION', inject(function(interpolateFilter) {
            expect(interpolateFilter('before %VERSION% after')).toEqual('before TEST_VER after');
        }));
    });

    //
    // Testing filter "reverse"
    //
    describe('reverse', function() {
        // console.log("TEST filter 'reverse'");

        beforeEach(module(function($provide) {
        }));

        it('should reverse string', inject(function(reverseFilter) {
            // console.log("  ...string");
            expect(reverseFilter("12345")).toEqual("54321");
        }));

        it('should reverse array', inject(function(reverseFilter) {
            // console.log("  ...array");
            var tmpOriginal = ["1", "2", "3"];
            var tmpResult = ["3", "2", "1"];

            expect(reverseFilter(tmpOriginal)).toEqual(tmpResult);
        }));

        it('should throw error with object', inject(function(reverseFilter) {
            // console.log("  ...object");
            try {
                reverseFilter(xxx);
                expect(true).toEqual(false);
            } catch (e) {
                expect(true).toEqual(true);
            }
        }));
    });

});
