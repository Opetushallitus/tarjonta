'use strict';

var app = angular.module('MonikielinenTextField', ['Koodisto', 'localisation', 'pasvaz.bindonce']);

app.directive('mkTextfield', function(Koodisto, LocalisationService, $log, $modal) {
	
	var userLangs = window.CONFIG.app.userLanguages;

    function defaultLangMapConverter(data) {
        var m = {};
        for (var i in data) {
            if (data[i] && data[i].value && data[i].value.length > 0) {
                m[data[i].uri] = data[i].value;
            }
        }
        return m;
    }

    function controller($scope) {

        $scope.codes = {};

        if (!$scope.model) {
            $scope.model = {};
        }

        $scope.data = [];

        $scope.errors = {
            required: false,
            pristine: true,
            dirty: false,
            invalid: false,
            $name: $scope.name
        }

        $scope.updateModel = function() {
            $scope.model = {};
            for (var i in $scope.data) {
                if ($scope.data[i] && $scope.data[i].value && $scope.data[i].value.length > 0) {
                	$scope.model[$scope.data[i].uri] = $scope.data[i].value;
                }
            }
            
            $scope.errors.dirty = true;
            $scope.errors.invalid = false;
            $scope.errors.pristine = false;

            if ($scope.isrequired) {

                $scope.errors.required = true;
                var length = Object.keys($scope.model).length;
                if (length === 0) {
                    $scope.errors.dirty = true;
                    $scope.errors.invalid = true;
                    $scope.errors.required = true;
                    return;
                }

                for (var i in $scope.model) {
                   // $log.info("updateModel() - i = " + i);
                    if ($scope.model[i] && $scope.model[i] != null && $scope.model[i].trim().length > 0) {
                        $scope.errors.required = false;
                    } else {
                        //invalid data
                        $scope.errors.required = true;
                        $scope.errors.invalid = true;
                        return;
                    }
                }
            }
        };

        $scope.sortData = function() {
        	$scope.data.sort(function(a,b){
        		var ap = userLangs.indexOf(a.uri);
        		var bp = userLangs.indexOf(a.uri);
        		if (ap!=-1 && bp!=-1) {
        			return ap>bp ? 1 : ap<bp ? -1 : 0;
        		}
        		if (ap!=-1) {
        			return 1;
        		}
        		if (bp!=-1) {
        			return -1;
        		}
        		
        		return $scope.codes[a.uri].nimi.localeCompare($scope.codes[b.uri].nimi);
        	});
        }

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
            $scope.data.push({uri: kieliUri, value: $scope.model[kieliUri], removable: userLangs.indexOf(kieliUri) == -1});
        }

        // vakiokielet näkyviin
        for (var i in userLangs) {
            var lang = userLangs[i];
            if (!$scope.model[lang]) {
                $scope.data.push({uri: lang, value: "", removable: false});
            }
        }
        $scope.sortData();

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

            ns.codes.sort(function(a, b) {
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
                        if ($scope.preselection == lang) {
                            $modalInstance.close();
                            $scope.data.push({uri: lang, value: "", removable: true});
                            $scope.sortData();
                            $scope.updateModel();
                        } else {
                            $scope.preselection = lang;
                        }
                    };
                },
                templateUrl: "js/shared/directives/mkTextfield-addlang.html",
                scope: ns
            });
        };

    }

    return {
        restrict: 'EA',
        require: '^form',
        replace: true,
        templateUrl: "js/shared/directives/mkTextfield.html",
        controller: controller,
        link: function(scope, element, attrs, controller) {
            if (scope.name) {
                scope.isrequired = (attrs.required !== undefined);

                controller.$addControl({'$name': scope.name, '$error': scope.errors});

                scope.$watch("errors.invalid", function(newVal, oldVal) {
                    scope.errors.required = newVal;
                    controller.$valid = !newVal;
                });
            }
        },
        scope: {
            model: "=", // map jossa kieliuri -> teksti tai decode/encode -funktioiden määräämässä muodossa

            // angular-form-logiikkaa varten
            name: "@", // nimi formissa
            required: "@" // jos tosi, vähintään yksi arvo vaaditaan
        }
    };

});
