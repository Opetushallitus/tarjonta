(function() {
    'use stric';
    window.oph = {
        removeKoodiVersion: function(uri) {
            var hashPos = uri.indexOf('#');
            if (hashPos !== -1) {
                uri = uri.substring(0, hashPos);
            }
            return uri;
        }
    };
})();