'use strict';

var app = angular.module('MonikielinenTextField', ['Koodisto', 'localisation', 'pasvaz.bindonce']);

app.directive('mkTextfield', function(Koodisto, LocalisationService, $log, $modal) {

    function defaultLangMapConverter(data) {
        var m = {};
        for (var i in data) {
            if (data[i].value.length > 0) {
                m[data[i].uri] = data[i].value;
            }
        }
        return m;
    }

    function defaultToDirectiveObjConverter(model, init, kieliUri) {
        return {uri: kieliUri, value: model[kieliUri], removable: init.indexOf(kieliUri) === -1};
    }
    
    function directiveModelTokoulutusApiModelConverter(data, model, codes) {
        for (var i in data) {
            model[data[i].uri] = {'koodi': {'arvo': data[i].value, 'uri': data[i].uri, 'versio': codes[ data[i].uri].versio}};
        }
    }
    
    function koulutusApiModelToDirectiveObjConverter(model, init, kieliUri) {
        return {uri: kieliUri, value: model[kieliUri].koodi.arvo, removable: init.indexOf(kieliUri) === -1};
    }

    function controller($scope) {

        $scope.codes = {};

        if (!$scope.model) {
            $scope.model = {};
        }
        
        $scope.init = window.CONFIG.app.userLanguages;
        $scope.data = [];
        
        $scope.errors = {
        		required:false,
        		pristine:true,
        		dirty:false,
        		$name:$scope.name
        }

        $scope.updateModel = function() {
            if ($scope.type === 'koulutus') {
                directiveModelTokoulutusApiModelConverter($scope.data, $scope.model, $scope.codes);
            } else {
                $scope.model = defaultLangMapConverter($scope.data);
            }
        
            $scope.errors.dirty = true;
            $scope.errors.pristine = false;

            if ($scope.isrequired) {
            	$scope.errors.required = true;
            	for (var i in $scope.model) {
            		if ($scope.model[i].trim().length>0) {
                    	$scope.errors.required = false;
            			break;
            		}
            	}
            }
        };

        // kielikoodit koodistosta
        Koodisto.getAllKoodisWithKoodiUri("kieli", LocalisationService.getLocale()).then(function(v) {
            var nc = {};
            for (var i in v) {
                nc[v[i].koodiUri] = {versio: v[i].koodiVersio, nimi: v[i].koodiNimi, uri: v[i].koodiUri};
            }
            $scope.codes = nc;
        });

        // data
        for (var kieliUri in $scope.model) {
            if ($scope.type === 'koulutus') {
                $scope.data.push(koulutusApiModelToDirectiveObjConverter($scope.model, $scope.init, kieliUri));
            } else {
                $scope.data.push(defaultToDirectiveObjConverter($scope.model, $scope.init, kieliUri));
            }
        }

        // initissä annetut kielet näkyviin
        for (var kieliUri in $scope.init) {
            var lang = $scope.init[kieliUri];
            if (angular.isUndefined($scope.model[lang])) {
                $scope.data.push({uri: lang, value: "", removable: false});
            }
        }

        // kielen poisto
        $scope.removeLang = function(uri) {
            var nm = [];
            for (var i in $scope.data) {
                if ($scope.data[i].uri != uri) {
                    nm.push($scope.data[i]);
                }
            }
            $scope.data = nm;
            $scope.updateModel();
        }

        // kielen lisäys
        $scope.addLang = function() {
            var ps = $scope;
            var ns = $scope.$new();
            ns.codes = [];
            ns.preselection = null;

            for (var i in $scope.codes) {
                if ($scope.model[i] === undefined) {
                    ns.codes.push($scope.codes[i]);
                }
            }
            
            ns.codes.sort(function(a,b){
            	return a.nimi.localeCompare(b.nimi);
            });

            $modal.open({
                controller: function($scope, $modalInstance) {
                	$scope.ok = function() {
                		$scope.select($scope.preselection);
                	};
                    $scope.cancel = function() {
                        $modalInstance.dismiss();
                    };
                    $scope.select = function(lang) {
                    	if ($scope.preselection==lang) {
                            $modalInstance.close();
                            $scope.data.push({uri: lang, value: "", removable: true});
                            $scope.updateModel();
                    	} else {
                    		$scope.preselection = lang;
                    	}
                    };
                },
                templateUrl: "js/shared/directives/mkTextfield-addlang.html",
                scope: ns
            });

        }
    }

    return {
        restrict: 'E',
        require: '^form',
        replace: true,
        templateUrl: "js/shared/directives/mkTextfield.html",
        controller: controller,
        link: function(scope, element, attrs, controller) {
        	
        	if (scope.name) {
            	scope.isrequired = (attrs.required !== undefined);
            	scope.errors.required = scope.isrequired;
            	controller.$addControl({"$name": scope.name, "$error": scope.errors});
        	}
        },
        scope: {
            type: "@", //Modelin suora convertointi tiettyyn objektiin. Jata tyhjaksi jos et tarvitse erikoiskasittelya.
            //init: "=", //lista kieli(urei)sta jotka näytetään vakiona (ja joita ei siis voi poistaa)
            model: "=", // map jossa kieliuri -> teksti
            
            // angular-form-logiikkaa varten
            name: "@", // nimi formissa
            required: "@" // jos tosi, vähintään yksi arvo vaaditaan
        }
    }

});
