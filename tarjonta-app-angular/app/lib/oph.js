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

    $(function() {
        // https://jira.oph.ware.fi/jira/browse/OVT-9120
        $('body').on('keydown', 'input', function(e) {
            if (e.keyCode === 13) {
                e.stopPropagation();
                e.preventDefault();
            }
        });
    });
})();