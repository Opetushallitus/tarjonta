'use strict';

var app = angular.module('KoodistoCombo', ['ngResource', 'Logging']);

app.directive('koodistocombo', function(Koodisto, $log, $q) {

    $log = $log.getInstance("<koodistocombo>");

    var filterKoodis = function(koodistoFilterUri, koodisParam) {
        var filteredkoodis = [];

        angular.forEach(koodisParam, function(koodi) {
            console.log("relaatio " + koodistoFilterUri + " " + koodi.koodiUri);
            if (koodi.koodiKoodisto === koodistoFilterUri) {
                filteredkoodis.push(koodi);
            }
        });

        return filteredkoodis;
    };

    var findKoodiWithUri = function(koodi, koodis) {

        var foundKoodi;

        angular.forEach(koodis, function(koodiLoop) {
            if (koodiLoop.koodiUri === koodi) {
                foundKoodi = koodiLoop;
            }
        });


        return foundKoodi;
    };

    var processYlapuolisetKoodit = function(koodisParam) {
        $log.info('PARENT KOODI WAS DEFINED YLAPUOLISET KOODIT : ', koodisParam);
        if ($scope.version !== undefined && $scope.version) {
            angular.forEach(koodisParam, function(koodi) {
                koodi.koodiUri = koodi.koodiUri + "#" + koodi.koodiVersio;
            });
        }
        $scope.koodis = koodisParam;
        $scope.baseKoodis = $scope.koodis;
        $scope.checkForExcludeUris();
        $scope.checkForFilterUris();
    };



    return {
        restrict: 'EA',
        require: '^form',
        replace: true,
        templateUrl: "js/shared/directives/koodistoNimiCombo.html",
        scope: {
            koodistouri: "=",
            koodiuri: "=",
            locale: "=",
            isdependent: "=",
            filterwithkoodistouri: "=",
            version: "=",
            isrequired: "=",
            usearvocombo: "=",
            parentkoodiuri: "=",
            filteruris: "=",
            excludeuris: "=",
            prompt: "=",
            isalakoodi: "=",
            onchangecallback: "="

        },
        controller: function($scope, Koodisto) {

            var koodiSeparator = "#";
            $scope.baseKoodis = [];

            var checkForKoodiUriVersion = function() {


                if ($scope.koodiuri) {
                    if ($scope.koodiuri.indexOf(koodiSeparator) > -1) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }


            };

            var processAlapuolisetKoodit = function(koodisParam) {

                if ($scope.filterwithkoodistouri !== undefined || $scope.koodistouri) {


                    if ($scope.version !== undefined && $scope.version) {

                        addVersionToKoodis(koodisParam);

                    }

                    $scope.koodis = filterKoodis($scope.filterwithkoodistouri ? $scope.filterwithkoodistouri : $scope.koodistouri, koodisParam);
                    console.log(" $scope.koodis", $scope.koodis);


                } else {
                    addVersionToKoodis(koodisParam);
                    $scope.koodis = koodisParam;
                }

                $scope.baseKoodis = $scope.koodis;
                $scope.checkForExcludeUris();
                $scope.checkForFilterUris();
            };

            var processMultipleParentKoodis = function() {

            };

            var addKoodiToKoodiUri = function(koodiversio) {
                if ($scope.koodiuri) {
                    $scope.koodiuri = $scope.koodiuri + koodiSeparator + koodiversio;
                }
            };

            var addVersionToKoodis = function(koodis) {
                var koodienVersio = 0;
                if ($scope.version !== undefined && $scope.version) {
                    angular.forEach(koodis, function(koodi) {
                        if (koodi.koodiUri.indexOf("#") < 0) {
                            koodienVersio = koodi.koodiVersio;
                            koodi.koodiUri = koodi.koodiUri + "#" + koodi.koodiVersio;
                        } else {
                            $log.warn("addVersionToKoodis - tried to add version to already versioned URI!", koodi);
                        }
                    });
                    if (!checkForKoodiUriVersion()) {
                        addKoodiToKoodiUri(koodienVersio);
                    }
                }

            }

            if ($scope.isrequired !== undefined && $scope.isrequired === "true" || $scope.isrequired) {

                $scope.valuerequired = true;
            } else {
                $scope.valuerequired = false;

            }


            if ($scope.usearvocombo !== undefined) {
                $scope.combotype = {
                    value: "arvo"
                };
            } else {
                $scope.combotype = {
                    value: "nimi"
                };
            }

            if ($scope.isdependent) {



                if ($scope.parentkoodiuri !== undefined) {

                    //Default behaviour is to get alakoodis
                    if ($scope.isalakoodi === undefined || $scope.isalakoodi === null) {
                        $log.info('isalakoodi was undefined');
                        $scope.isalakoodi = true;
                    }

                    if ($scope.isalakoodi) {

                        if (angular.isArray($scope.parentkoodiuri)) {

                            var koodiPromises = [];

                            angular.forEach($scope.parentkoodiuri, function(parentKoodiUri) {

                                koodiPromises.push(Koodisto.getAlapuolisetKoodit(parentKoodiUri, $scope.locale));

                                var allPromises = $q.all(koodiPromises);
                                allPromises.then(function(data) {

                                    angular.forEach(data, function(koodis) {

                                        processAlapuolisetKoodit(koodis);
                                    });
                                });

                            });

                        } else {

                            var koodisPromise = Koodisto.getAlapuolisetKoodit($scope.parentkoodiuri, $scope.locale);

                            koodisPromise.then(processAlapuolisetKoodit);

                        }

                    } else {

                        if (angular.isArray($scope.parentkoodiuri)) {

                            var koodiPromises = [];

                            angular.forEach($scope.parentkoodiuri, function(parentKoodiUri) {

                                koodiPromises.push(Koodisto.getYlapuolisetKoodit($scope.parentkoodiuri, $scope.locale));

                                var allPromises = $q.all(koodiPromises);
                                allPromises.then(function(data) {

                                    angular.forEach(data, function(koodis) {

                                        processYlapuolisetKoodit(koodis);
                                    })
                                })

                            });

                        } else {
                            var koodisPromise = Koodisto.getYlapuolisetKoodit($scope.parentkoodiuri, $scope.locale);
                            koodisPromise.then(processYlapuolisetKoodit);
                        }
                    }


                }


            } else {
                var koodisPromise = Koodisto.getAllKoodisWithKoodiUri($scope.koodistouri, $scope.locale);
                koodisPromise.then(function(koodisParam) {
                    var cs = angular.copy(koodisParam);
                    addVersionToKoodis(cs);
                    $scope.koodis = cs;
                    $scope.baseKoodis = $scope.koodis;
                    $scope.checkForExcludeUris();
                    $scope.checkForFilterUris();

                });
            }

            //If filter uris is changed then query only those and show those koodis
            $scope.$watch('filteruris', function() {

                $scope.checkForFilterUris();

            });

            $scope.$watch('excludeuris', function() {

                $scope.checkForExcludeUris();

            });

            $scope.checkForFilterUris = function() {
                if ($scope.filteruris && $scope.filteruris.length > 0) {
                    console.log('filterurisexists : ', $scope.baseKoodis);
                    var filteredKoodis = [];
                    angular.forEach($scope.baseKoodis, function(koodi) {

                        angular.forEach($scope.filteruris, function(filterUri) {

                            if (koodi.koodiUri.substring(0, filterUri.length) === filterUri) {
                                filteredKoodis.push(koodi);
                            }
                        });

                    });
                    console.log('filterurisexists : ', filteredKoodis);
                    $scope.koodis = filteredKoodis;

                }
            };

            $scope.checkForExcludeUris = function() {

                if ($scope.excludeuris && $scope.excludeuris.length > 0) {
                    console.log('excludeurisexists : ', $scope.baseKoodis);
                    console.log('excludeurisexists : ', $scope.excludeuris);
                    var filteredKoodis = [];
                    angular.forEach($scope.baseKoodis, function(koodi) {

                        angular.forEach($scope.excludeuris, function(filterUri) {
                            if (koodi.koodiUri.substring(0, filterUri.length) !== filterUri) {
                                filteredKoodis.push(koodi);
                            }

                        });

                    });
                    console.log('excludeurisexists : ', filteredKoodis);
                    $scope.koodis = filteredKoodis;

                }

            };

            $scope.$watch('parentkoodiuri', function() {
                $log.info('Parent koodi uri changed');
                if ($scope.parentkoodiuri !== undefined) {
                    //Default behaviour is to get alakoodis
                    if ($scope.isalakoodi === undefined || $scope.isalakoodi === null) {
                        $log.info('isalakoodi was undefined');
                        $scope.isalakoodi = true;
                    }
                    if ($scope.isalakoodi) {
                        if (angular.isArray($scope.parentkoodiuri)) {

                            var koodiPromises = [];

                            angular.forEach($scope.parentkoodiuri, function(parentKoodiUri) {

                                koodiPromises.push(Koodisto.getAlapuolisetKoodit(parentKoodiUri, $scope.locale));

                                var allPromises = $q.all(koodiPromises);
                                allPromises.then(function(data) {

                                    angular.forEach(data, function(koodis) {
                                        processAlapuolisetKoodit(koodis);
                                    })
                                })

                            });

                        } else {

                            var koodisPromise = Koodisto.getAlapuolisetKoodit($scope.parentkoodiuri, $scope.locale);

                            koodisPromise.then(processAlapuolisetKoodit);

                        }
                    } else {
                        if (angular.isArray($scope.parentkoodiuri)) {

                            var koodiPromises = [];

                            angular.forEach($scope.parentkoodiuri, function(parentKoodiUri) {

                                koodiPromises.push(Koodisto.getYlapuolisetKoodit($scope.parentkoodiuri, $scope.locale));

                                var allPromises = $q.all(koodiPromises);
                                allPromises.then(function(data) {

                                    angular.forEach(data, function(koodis) {

                                        processYlapuolisetKoodit(koodis);
                                    })
                                })

                            });

                        } else {
                            var koodisPromise = Koodisto.getYlapuolisetKoodit($scope.parentkoodiuri, $scope.locale);
                            koodisPromise.then(processYlapuolisetKoodit);
                        }
                    }
                }
            });

            $scope.onKoodistoComboChange = function() {
                if ($scope.onchangecallback !== undefined) {
                    $log.info('Select koodiuri ');

                    $log.info($scope.koodiuri);

                    var koodi = findKoodiWithUri($scope.koodiuri, $scope.koodis);

                    $log.info('Found koodi : ', koodi);

                    $scope.onchangecallback(koodi);

                } else {
                    //$log.info('No onchangecallback defined');
                }
            };

        }

    }
});

app.directive('koodistocomboaiku', function(Koodisto, $log, $q) {

    $log = $log.getInstance("<koodistocomboaiku>");

    var filterKoodis = function(mapFilterDublicates, koodistoFilterUri, koodisParam) {
        var filteredkoodis = [];

        angular.forEach(koodisParam, function(koodi) {
            if (koodi.koodiKoodisto === koodistoFilterUri) {
                console.log("selected  " + koodistoFilterUri + " " + koodi.koodiUri);

                if (!mapFilterDublicates[koodi.koodiArvo]) {
                    filteredkoodis.push(koodi);
                    mapFilterDublicates[koodi.koodiArvo] = true;
                }
            }
        });

        return filteredkoodis;
    };

    var findKoodiWithUri = function(koodi, koodis) {
        var foundKoodi;
        angular.forEach(koodis, function(koodiLoop) {
            if (koodiLoop.koodiUri === koodi) {
                foundKoodi = koodiLoop;
            }
        });

        return foundKoodi;
    };

    var hasIdenticalKoodiArvos = function(arvo, arr) {
        if (!arvo || !arr || arvo === null || arr.length === 0) {
            return false;
        }

        for (var j = 0; j < arr.length; j++) {
            if (arr[j].koodiArvo !== arvo) {
                return  false;
            }
        }
        return true;
    };

    var filterHakukohdePerustutkinto = function(allKolutusKoodis, filterWithKoodistoUri) {
        if (!filterWithKoodistoUri) {
            throw new Error("Missing hakukohde koodisto filter!");
        }

        if (allKolutusKoodis) {
            for (var i = 0; i < allKolutusKoodis.length; i++) {
                if (allKolutusKoodis[i].koodiKoodisto === filterWithKoodistoUri) {
                    return allKolutusKoodis[i];
                }
            }
        }

        return null;
    };

    var aikuFilter = function(allHakukohdeKoodis, perustutkintoHakukohdeKoodi) {
        var resultUris = [];

        if (perustutkintoHakukohdeKoodi && perustutkintoHakukohdeKoodi !== null) {
            // add perustutkinto to result
            resultUris.push(perustutkintoHakukohdeKoodi);
        } else {
            //required koodisto relation is an invalid.
            return resultUris;
        }

        var filteredHakukohdeKoodis = [];
        if (perustutkintoHakukohdeKoodi && perustutkintoHakukohdeKoodi !== null) {
            //remove perustutkinto hakukohde from array of osaamisala hakukohde uris (if any)

            filteredHakukohdeKoodis = _.reject(allHakukohdeKoodis, function(koodi) {
                return koodi.koodiArvo === perustutkintoHakukohdeKoodi.koodiArvo;
            });
        }

        var hakukohdeArvo = filteredHakukohdeKoodis && filteredHakukohdeKoodis.length > 0 ? filteredHakukohdeKoodis[0].koodiArvo : null;

        if (hasIdenticalKoodiArvos(hakukohdeArvo, filteredHakukohdeKoodis)) {
            //all hakukohde uris should be indetical, remove duplicates by adding only the first one to array.
            resultUris.push(filteredHakukohdeKoodis[0]);
        }

        return resultUris;
    };

    return {
        restrict: 'EA',
        require: '^form',
        replace: true,
        templateUrl: "js/shared/directives/koodistoNimiCombo.html",
        scope: {
            koodistouri: "=",
            koodiuri: "=",
            locale: "=",
            isdependent: "=",
            filterwithkoodistouri: "=",
            version: "=",
            isrequired: "=",
            usearvocombo: "=",
            parentkoodiuri: "=",
            filteruris: "=",
            excludeuris: "=",
            prompt: "=",
            isalakoodi: "=",
            onchangecallback: "="

        },
        controller: function($scope, Koodisto) {
            var koodiSeparator = "#";
            $scope.baseKoodis = [];
            $scope.koodis = [];
            $scope.mapFilterDublicates = {};

            var checkForKoodiUriVersion = function() {
                if ($scope.koodiuri) {
                    if ($scope.koodiuri.indexOf(koodiSeparator) > -1) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            };

            var processAlapuolisetKoodit = function(koodisParam) {
                return filterKoodis($scope.mapFilterDublicates, $scope.filterwithkoodistouri ? $scope.filterwithkoodistouri : $scope.koodistouri, koodisParam);
            };

            var addKoodiToKoodiUri = function(koodiversio) {
                if ($scope.koodiuri) {
                    $scope.koodiuri = $scope.koodiuri + koodiSeparator + koodiversio;
                }
            };

            var addVersionToKoodis = function(koodis) {
                var koodienVersio = 0;
                if ($scope.version !== undefined && $scope.version) {
                    angular.forEach(koodis, function(koodi) {
                        if (koodi.koodiUri.indexOf("#") < 0) {
                            koodienVersio = koodi.koodiVersio;
                            koodi.koodiUri = koodi.koodiUri + "#" + koodi.koodiVersio;
                        } else {
                            $log.warn("addVersionToKoodis - tried to add version to already versioned URI!", koodi);
                        }
                    });
                    if (!checkForKoodiUriVersion()) {
                        addKoodiToKoodiUri(koodienVersio);
                    }
                }
            };

            if ($scope.isrequired !== undefined && $scope.isrequired === "true" || $scope.isrequired) {
                $scope.valuerequired = true;
            } else {
                $scope.valuerequired = false;
            }

            if ($scope.usearvocombo !== undefined) {
                $scope.combotype = {
                    value: "arvo"
                };
            } else {
                $scope.combotype = {
                    value: "nimi"
                };
            }

            $scope.$watch('parentkoodiuri', function(parentkoodiuri) {
                $log.info('Parent koodi uri changed');
                if (parentkoodiuri !== undefined && parentkoodiuri.length > 0) {
                    //Default behaviour is to get alakoodis
                    if ($scope.isalakoodi === undefined || $scope.isalakoodi === null) {
                        $log.info('isalakoodi was undefined');
                        $scope.isalakoodi = true;
                    }

                    if (angular.isArray(parentkoodiuri)) {
                        var koodiPromises = [];


                        var koulutus = _.filter(parentkoodiuri, function(uri) {
                            return uri.indexOf("koulutus") !== -1;
                        });

                        var osaamisala = _.filter(parentkoodiuri, function(uri) {
                            return uri.indexOf("osaamisala") !== -1;
                        });

                        Koodisto.getAlapuolisetKoodit(koulutus, $scope.locale).then(function(koulutusRelationResult) {
                            angular.forEach(osaamisala, function(uri) {
                                koodiPromises.push(Koodisto.getAlapuolisetKoodit(uri, $scope.locale));
                            });

                            $q.all(koodiPromises).then(function(data) {
                                var filteredOsaamisalaKoodis = [];

                                console.log($scope.parentkoodiuri);

                                angular.forEach(data, function(koodis) {
                                    filteredOsaamisalaKoodis = filteredOsaamisalaKoodis.concat(processAlapuolisetKoodit(koodis));
                                });

                                var arr = aikuFilter(filteredOsaamisalaKoodis, filterHakukohdePerustutkinto(koulutusRelationResult, $scope.koodistouri));
                                addVersionToKoodis(arr);
                                $scope.koodis = arr;
                            });
                        });
                    }
                }
            });



            $scope.onKoodistoComboChange = function() {
                if ($scope.onchangecallback !== undefined) {
                    $log.info('Select koodiuri ');

                    $log.info($scope.koodiuri);

                    var koodi = findKoodiWithUri($scope.koodiuri, $scope.koodis);

                    $log.info('Found koodi : ', koodi);

                    $scope.onchangecallback(koodi);

                } else {
                    //$log.info('No onchangecallback defined');
                }
            };

        }

    }
});
