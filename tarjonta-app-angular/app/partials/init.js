
function tarjontaInit() {
	
	var loader = $("div#ajax-loader");

	var init_counter = 0;
	
	function initFail(id, xhr, status) {
	    console.log("Init failure: " + id + " -> "+status, xhr);
	    loader.toggleClass("fail", true);
	}
	
	function initFunction(id, xhr, status) {
	    init_counter--;
	
	    console.log("Got ready signal from: " + id + " -> "+status, xhr);
	
	    /*if (init_counter > 0) {
	        console.log("Got ready signal from: '" + id + "' -- still waiting for " + init_counter + " requests.");
	    } else {
	        console.log("OK! That was the last request, init the app!");*/
	    if (init_counter == 0) {
	    		    	
	        angular.element(document).ready(function() {
	            angular.module('myApp', ['app']);
	            angular.bootstrap(document, ['myApp']);
		    	loader.toggleClass("pre-init", false);
	        });
	    }
	};
	
	//
	// Get current users info (/cas/me)
	//
	console.log("** Loading user info; from: ", window.CONFIG.env.casUrl);
	window.CONFIG.env.cas = {}
	init_counter++;
	jQuery.ajax(window.CONFIG.env.casUrl, {
	    dataType: "json",
	    success: function(xhr, status) {
	        window.CONFIG.env.cas.userinfo = xhr;
	        initFunction("AUTHENTICATION", xhr, status);
	    },
	    error: function(xhr, status) {
	        initFail("AUTHENTICATION", xhr, status);
	    }
	});
	
	//
	// Preload "tarjonta.tila"???
	//
	console.log("** Loading tarjonta.tila info; from: " + window.CONFIG.env.tarjontaRestUrlPrefix + "tila");
	init_counter++;
	jQuery.ajax(window.CONFIG.env.tarjontaRestUrlPrefix + "tila", {
	    dataType: "json",
	    success: function(xhr, status) {
	        window.CONFIG.env["tarjonta.tila"] = xhr.result;
	        initFunction("tarjonta.tila", xhr, status);
	    },
	    error: function(xhr, status) {
	        window.CONFIG.env["tarjonta.tila"] = {};
	        initFail("tarjonta.tila", xhr, status);
	    }
	});
	
	//
	// Preload application localisations for tarjonta
	//
	var localisationUrl = window.CONFIG.env.tarjontaLocalisationRestUrl + "?category=tarjonta";
	console.log("** Loading localisation info; from: ", localisationUrl);
	init_counter++;
	jQuery.ajax(localisationUrl, {
	    dataType: "json",
	    complete: function(xhr, status) {
	        window.CONFIG.env["tarjonta.localisations"] = xhr.responseJSON;
	        initFunction("localisations", xhr, status);
	    },
	    error: function(xhr, status) {
	        window.CONFIG.env["tarjonta.localisations"] = [];
	        initFail("localisations", xhr, status);
	    }
	});
		
}
