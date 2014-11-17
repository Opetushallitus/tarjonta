'use strict';

/* Services */
var app = angular.module('app.services', ['ngResource']);

app.factory('DataService', function() {
	var data = [];
	
	function set(name, value) {
		for (var i = 0; i < data.length; i++) {
			if (data[i].name == name) {
				data[i].value = value;
				return;
			}
		}
		data.push({
			name: name,
			value: value
		}); 
	}
	
	function get(name) {
		for (var i = 0; i < data.length; i++) {
			if (data[i].name == name) {
				return data[i].value;
			}
		}
		return undefined;
	}
	
	return {
		set: set,
		get: get
	}
});