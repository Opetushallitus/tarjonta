(function() {
    'use strict';
    var optionCount = 0;
    setInterval(function() {
        var newOptionCount = $('select:visible option').length;
        if (optionCount !== newOptionCount) {
            // Tämä korjaa IE 9 ongelman, että valitun optionin teksti ei näy kokonaan
            $('select').css('width', '100%').css('width', '');
            optionCount = newOptionCount;
        }
    }, 200);
}());