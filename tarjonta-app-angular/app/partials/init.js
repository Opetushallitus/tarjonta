function tarjontaInit() {
    var loader = $('div#ajax-loader');
    var init_counter = 0;
    var fail = false;
    jQuery.support.cors = true;
    function initFail(id, xhr, status) {
        fail = true;
        console.log('Init failure: ' + id + ' -> ' + status, xhr);
        loader.toggleClass('fail', true);
    }
    function initFunction(id, xhr, status) {
        init_counter--;
        console.log('Got ready signal from: ' + id + ' -> ' + status + ' -> IC=' + init_counter /*, xhr*/ );
        if (!fail && init_counter === 0) {
            angular.element(document).ready(function() {
                angular.module('myApp', ['app']);
                angular.bootstrap(document, ['myApp']);
                loader.toggleClass('pre-init', false);
            });
        }
    }
    function logRequest(xhr, status) {
        console.log('LOG ' + status + ': ' + xhr.status + ' ' + xhr.statusText, xhr);
    }
    //
    // Get current users info (/cas/me)
    //
    console.log('** Loading user info; from: ', window.CONFIG.env.casUrl);
    window.CONFIG.env.cas = {};
    init_counter++;
    var callerId = '1.2.246.562.10.00000000001.tarjonta.tarjonta-app-angular'
    jQuery.ajaxSetup({
        headers: {
            'Caller-Id': callerId
        }
    });
    jQuery.ajax(window.CONFIG.env.casUrl, {
        dataType: 'json',
        crossDomain: true,
        complete: logRequest,
        success: function(xhr, status) {
            window.CONFIG.env.cas.userinfo = xhr;
            initFunction('AUTHENTICATION', xhr, status);
        },
        error: function(xhr, status) {
            initFail('AUTHENTICATION', xhr, status);
        }
    });
    //
    // Preload "tarjonta.tila"???
    //
    console.log('** Loading tarjonta.tila info; from: ' + window.url("tarjonta-service.tila"));
    init_counter++;
    jQuery.ajax(window.url("tarjonta-service.tila"), {
        dataType: 'json',
        crossDomain: true,
        complete: logRequest,
        success: function(xhr, status) {
            window.CONFIG.env['tarjonta.tila'] = xhr.result;
            initFunction('tarjonta.tila', xhr, status);
        },
        error: function(xhr, status) {
            window.CONFIG.env['tarjonta.tila'] = {};
            initFail('tarjonta.tila', xhr, status);
        }
    });
    //
    // Preload application localisations for tarjonta
    //
    var localisationUrl = window.urls().noEncode().url("lokalisointi.loadAll", {category:"tarjonta", value:"cached"});
    console.log('** Loading localisation info; from: ', localisationUrl);
    init_counter++;
    jQuery.ajax(localisationUrl, {
        dataType: 'json',
        crossDomain: true,
        complete: logRequest,
        success: function(xhr, status) {
            window.CONFIG.env['tarjonta.localisations'] = xhr;
            initFunction('localisations', xhr, status);
        },
        error: function(xhr, status) {
            window.CONFIG.env['tarjonta.localisations'] = [];
            initFail('localisations', xhr, status);
        }
    });

    // Init onr&ko sessions
    jQuery.get(window.url("oppijanumerorekisteri-service.henkilotypes"));
    jQuery.get(window.url("kayttooikeus-service.prequel"));
}