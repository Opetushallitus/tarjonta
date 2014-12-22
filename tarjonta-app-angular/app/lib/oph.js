(function() {
    'use stric';
    window.oph = {
        getKoodistoUriWithoutVersion: function(uri) {
            var hashPos = uri.indexOf('#');
            if (hashPos !== -1) {
                uri = uri.substring(0, hashPos);
            }
            return uri;
        }
    };
})();