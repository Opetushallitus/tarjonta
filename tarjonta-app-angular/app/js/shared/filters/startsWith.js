angular.module('StartsWithFilter', []).filter('startsWith', function() {
    return function(input, key) {
        key = key.toLowerCase();
        var ret = [];
        for (var i in input) {
            var s = input[i];
            if (s && s.substring(0, key.length).toLowerCase() == key) {
                ret.push(s);
            }
        }
        //console.log("FLT -> ",ret);
        return ret;
    };
});