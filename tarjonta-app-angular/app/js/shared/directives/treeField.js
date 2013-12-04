'use strict';

var app = angular.module('TreeFieldDirective', []);

app.factory('TreeFieldSearch', function($resource, $log, $q, Config, TarjontaService) {
    return function() {
        var factoryScope = {};

        /*
         * Create and clear tree data objects.
         */
        factoryScope.createTreeData = function(oid) {
            factoryScope.requests = 0;
            factoryScope.tree = {
                map: {}, //obj[oid].oids[]
                activePromises: [],
                'oid': oid
            };
        };

        /*
         * Find tree parents and store the items to a map.
         * 
         * @param {string} komoOid
         * @returns promise
         */
        factoryScope.getParentsByKomoOid = function(komoOid, deferred) {
            factoryScope.requests = factoryScope.requests + 1;
            var resource = TarjontaService.resourceLink.parents({oid: komoOid});

            return resource.$promise.then(function(res) {
                factoryScope.requests = factoryScope.requests - 1;

                if (res.result.length === 0) {
                    /*
                     * A TREE ROOT(s) one recursive loop end.
                     * a tree can have one or many parents...
                     */
                    if (angular.isUndefined(factoryScope.tree.map['ROOT'])) {
                        factoryScope.tree.map['ROOT'] = {childs: {}};
                    }
                    factoryScope.tree.map['ROOT'].childs[komoOid] = {selected: factoryScope.isPreSelected(komoOid)};
                } else {
                    /*
                     * Next parent items (go closer to root).
                     */
                    angular.forEach(res.result, function(result) {
                        if (angular.isUndefined(factoryScope.tree.map[result])) {
                            factoryScope.tree.map[result] = {childs: {}};
                        }
                        factoryScope.tree.map[result].childs[komoOid] = {selected: factoryScope.isPreSelected(komoOid)};
                        factoryScope.getParentsByKomoOid(result, deferred);
                    });
                }
                if (factoryScope.requests === 0) {
                    deferred.resolve(factoryScope.tree.map);
                }
            });
        };

        factoryScope.isPreSelected = function(komoOid) {
            return factoryScope.oid === komoOid;
        }

        factoryScope.searchByKomoOid = function(oid) {
            factoryScope.createTreeData(oid);
            var deferred = $q.defer();
            factoryScope.tree.activePromises.push(deferred.promise);
            factoryScope.getParentsByKomoOid(oid, deferred);

            var deferredOut = $q.defer();
            $q.all(factoryScope.tree.activePromises).then(function() {
                deferredOut.resolve(factoryScope.tree.map);
            });

            return deferredOut.promise;
        }

        return factoryScope;
    };
});

app.directive('treeField', function($log, TarjontaService, TreeFieldSearch) {

    function controller($scope, $q, $element, $compile) {

        if (angular.isUndefined($scope.lang)) {
            $scope.lang = "fi";
        }

        /*
         * Create and clear tree data objects.
         */
        $scope.createTreeData = function() {
            $scope.requests = 0;
            $scope.tree = {
                map: {ROOT: {childs: {}}}, //obj[oid].oids[]
                activePromises: [],
                treedata: [],
                selectedOids: {} //map
            };
        };

        /*
         * Create a tree item.
         */
        $scope.getCreateChildren = function(map, oid, tree, options) {

            var obj = {nimi: '---', oid: oid, children: [], selected: options.selected};
            $scope.searcNameByOid(oid, obj);
            tree.push(obj);

            if (!angular.isUndefined(map[oid])) {
                angular.forEach(map[oid].childs, function(val, key) {
                    $scope.getCreateChildren(map, key, obj.children, val);
                });
            }

            if (options.selected) {
                angular.forEach(factoryScope.newOids, function(oid) {
                    $scope.getCreateChildren(map, oid, obj.children, {selected: null});
                });
            }
        };

        $scope.searcNameByOid = function(oid, obj) {
            var array = $scope.names();

            /*
             * Quick name search
             */
            for (var i = 0; i < array.length; i++) {
                if (array[i].oid === oid) {
                    obj.nimi = array[i].nimi + ' (' + array[i].oid + ')';
                    return obj.nimi;
                }
            }

            /*
             * solr data search 
             */
            console.log("search:");
            console.log(oid);
            var id = oid;
            
            TarjontaService.haeKoulutukset({//search parameter object
                komoOid: oid
            }).then(function(result) {
                console.log("result:");
                console.log(result.tulokset[0].tulokset[0].komoOid);
                obj.nimi = result.tulokset[0].tulokset[0].nimi + ' (' + id + ')';
            });
        };


        /*
         * TODO : change 'treeid' to more dynamic.
         */
        $scope.$watch('treeid.currentNode', function(newObj, oldObj) {
            if ($scope.treeid && angular.isObject($scope.treeid.currentNode)) {
                var oid = $scope.treeid.currentNode.oid;

                if (_.has($scope.tree.selectedOids, oid)) {
                    delete $scope.tree.selectedOids[oid];
                    if (!angular.isUndefined($scope.fnClickHandler)) {
                        $scope.fnClickHandler($scope.treeid.currentNode, "DELETE");
                    }
                } else {
                    $scope.tree.selectedOids[$scope.treeid.currentNode.oid] = $scope.treeid.currentNode;
                    if (!angular.isUndefined($scope.fnClickHandler)) {

                        $scope.fnClickHandler($scope.treeid.currentNode, "SELECTED");
                    }
                }

                /*
                 var arr = _.keys($scope.tree.selectedOids);
                 
                 for (var i = 0; i < arr.length; i++) {
                 $scope.selectedOids.push(arr[i]) ; // to array of oids
                 }*/
                $scope.selectedOids = _.keys($scope.tree.selectedOids); // to array of oids
            }
        }, false);


        $scope.$watch('oids', function(newValue, oldValue) {
            if (newValue.length > 0) {
                $scope.createTreeData();

                for (var i = 0; i < $scope.oids.length; i++) {
                    var tfs = new TreeFieldSearch();
                    var promise = tfs.searchByKomoOid($scope.oids[i]);

                    promise.then(function(map) {
                        angular.forEach(map, function(val, parentKey) {
                            if (_.has($scope.tree.map, parentKey)) {
                                //data found by key oid, only add/override missing data 
                                angular.forEach(map[parentKey].childs, function(val, key) {
                                    $scope.tree.map[parentKey].childs[key] = val;
                                });
                            } else {
                                //no data data -> full  data copy, 
                                $scope.tree.map[parentKey] = map[parentKey];
                            }
                        });
                    });

                    $scope.tree.activePromises.push(promise);
                }

                $q.all($scope.tree.activePromises).then(function() {
                    console.log("DATA RENDERED");

                    angular.forEach($scope.tree.map['ROOT'].childs, function(val, key) {
                        $scope.getCreateChildren($scope.tree.map, key, $scope.tree.treedata, val);
                    });
                });

            }
        });
    }

    return {
        restrict: 'E',
        replace: true,
        templateUrl: "js/shared/directives/treeField.html",
        controller: controller,
        scope: {
            lang: "@", //lang code like 'fi'     
            oids: "=", //komo OIDs
            newOids: "@", //joined komo OIDs
            fnClickHandler: "=", //function for click event
            names: "&" //names in a list of objects {oid : 'xx', nimi : 'abc'}
        }
    };
});
