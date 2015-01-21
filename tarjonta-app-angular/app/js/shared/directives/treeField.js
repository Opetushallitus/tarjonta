var app = angular.module('TreeFieldDirective', []);
app.factory('TreeFieldSearch', function($resource, $log, $q, Config, TarjontaService) {
    'use strict';
    return function() {
        var factoryScope = {};
        /*
             * Create and clear tree data objects.
             */
        factoryScope.createTreeData = function(oid) {
            if (angular.isUndefined(oid)) {
                throw new Error('Start oid cannot be null.');
            }
            factoryScope.requests = 0;
            factoryScope.tree = {
                map: {},
                //obj[oid].oids[]
                activePromises: [],
                'oid': oid
            };
        };
        /*
             * Find next node parents and store the items to a map.
             *
             * @param {string} komoOid
             * @returns promise
             */
        factoryScope.searchParentsByKomoOid = function(komoOid, deferred) {
            factoryScope.requests = factoryScope.requests + 1;
            var resource = TarjontaService.resourceLink.parents({
                oid: komoOid
            });
            return resource.$promise.then(function(res) {
                factoryScope.requests = factoryScope.requests - 1;
                if (res.result.length === 0) {
                    /*
                               * A TREE ROOT(s) one recursive loop end.
                               * a tree can have one or many parents...
                               */
                    if (angular.isUndefined(factoryScope.tree.map.ROOT)) {
                        factoryScope.tree.map.ROOT = {
                            childs: {}
                        };
                    }
                    factoryScope.tree.map.ROOT.childs[komoOid] = {
                        selected: factoryScope.isPreSelected(komoOid)
                    };
                }
                else {
                    /*
                               * Next parent items (go closer to root).
                               */
                    angular.forEach(res.result, function(result) {
                        if (angular.isUndefined(factoryScope.tree.map[result])) {
                            factoryScope.tree.map[result] = {
                                childs: {}
                            };
                        }
                        factoryScope.tree.map[result].childs[komoOid] = {
                            selected: factoryScope.isPreSelected(komoOid)
                        };
                        factoryScope.searchParentsByKomoOid(result, deferred);
                    });
                }
                if (factoryScope.requests === 0) {
                    deferred.resolve(factoryScope.tree.map);
                }
            });
        };
        /*
         * Find next node childs and store the items to a map.
         *
         * @param {string} komoOid
         * @returns promise
         */
        factoryScope.searchChildsByKomoOid = function(komoOid) {
            var deferred = $q.defer();
            var resource = TarjontaService.resourceLink.get({
                oid: komoOid
            });
            return resource.$promise.then(function(res) {
                angular.forEach(res.result, function(resultOid) {
                    if (angular.isUndefined(factoryScope.tree.map[komoOid])) {
                        factoryScope.tree.map[komoOid] = {
                            childs: {}
                        };
                    }
                    factoryScope.tree.map[komoOid].childs[resultOid] = {
                        selected: factoryScope.isPreSelected(resultOid)
                    };
                    deferred.resolve();
                });
            });
        };
        factoryScope.isPreSelected = function(komoOid) {
            return factoryScope.tree.oid === komoOid;
        };
        factoryScope.searchByKomoOid = function(oid) {
            factoryScope.createTreeData(oid);
            var deferred = $q.defer();
            //find childs, only one level up
            factoryScope.tree.activePromises.push(factoryScope.searchChildsByKomoOid(oid));
            factoryScope.tree.activePromises.push(deferred.promise);
            //find all parents to the root level
            factoryScope.searchParentsByKomoOid(oid, deferred);
            var deferredOut = $q.defer();
            $q.all(factoryScope.tree.activePromises).then(function() {
                deferredOut.resolve(factoryScope.tree.map);
            });
            return deferredOut.promise;
        };
        return factoryScope;
    };
});
app.directive('treeField', function($log, TarjontaService, TreeFieldSearch) {
    'use strict';
    function controller($scope, $q, $element, $compile) {
        /*
             * set default attribute values
             */
        if (angular.isUndefined($scope.lang)) {
            $scope.lang = 'fi';
        }
        if (angular.isUndefined($scope.styleParam)) {
            $scope.styleParam = '';
        }
        if (angular.isUndefined($scope.reviewMode)) {
            $scope.reviewMode = false;
        }
        /*
         * Create and clear tree data objects.
         */
        $scope.createTreeData = function() {
            $scope.requests = 0;
            $scope.tree = {
                map: {
                    ROOT: {
                        childs: {}
                    }
                },
                //obj[oid].oids[]
                activePromises: [],
                treedata: [],
                selectedOids: {},
                //map
                visibleOids: {} //map
            };
        };
        /*
         * Create data objects for a tree by recursive loop.
         */
        $scope.getCreateChildren = function(map, oid, tree, options, recursive) {
            $scope.tree.visibleOids[oid] = {};
            TarjontaService.haeKoulutukset({
                //search parameter object
                komoOid: oid
            }).then(function(result) {
                if (result.tulokset[0] === undefined) {
                    return;
                }
                var obj = {
                    nimi: result.tulokset[0].tulokset[0].nimi,
                    oid: result.tulokset[0].tulokset[0].komoOid,
                    children: [],
                    selected: options.selected
                };
                tree.push(obj);
                if (!angular.isUndefined(map[oid]) && recursive) {
                    angular.forEach(map[oid].childs, function(val, keyParentOid) {
                        $scope.getCreateChildren(map, keyParentOid, obj.children, val, recursive);
                    });
                }
                if ($scope.reviewMode && options.selected) {
                    //edit mode need also review oids.
                    //the new oids are liked as child items
                    //check is infinity loop
                    var resource = TarjontaService.resourceLink.test({
                        parent: oid,
                        children: $scope.reviewOids
                    });
                    resource.$promise.then(function(resp) {
                        //                        if (!angular.isUndefined(resp.errors)) {
                        //                            //exclude all infinity loop oids
                        //                            angular.forEach(resp.errors, function(objErrors) {
                        //                                angular.forEach(objErrors.errorMessageParameters, function(oid) {
                        //                                    for (var i = 0; i < $scope.reviewOids.length; i++) {
                        //                                        if ($scope.reviewOids[i] === oid) {
                        //                                            $scope.reviewOids.splice(i, 1);
                        //                                            break;
                        //                                        }
                        //                                    }
                        //                                });
                        //                            });
                        //                        }
                        angular.forEach($scope.reviewOids, function(oid) {
                            $scope.getCreateChildren(map, oid, obj.children, {
                                selected: null
                            }, false);
                        });
                    });
                }
            });
        };
        $scope.searchSinglePathToRootByOid = function(oid) {
            var tfs = new TreeFieldSearch();
            var promise = tfs.searchByKomoOid(oid);
            var deferred = $q.defer();
            $scope.tree.activePromises.push(deferred.promise);
            promise.then(function(map) {
                angular.forEach(map, function(val, parentKey) {
                    if (_.has($scope.tree.map, parentKey)) {
                        //data found by key oid, only add/override missing data
                        angular.forEach(map[parentKey].childs, function(val, key) {
                            $scope.tree.map[parentKey].childs[key] = val;
                        });
                    }
                    else {
                        //no data data -> full  data copy,
                        $scope.tree.map[parentKey] = map[parentKey];
                    }
                });
                deferred.resolve();
            });
        };
        /*
         * TODO : change 'treeid' to more dynamic.
         */
        $scope.$watch('treeid.currentNode', function(newObj, oldObj) {
            if ($scope.treeid && angular.isObject($scope.treeid.currentNode)) {
                var oid = $scope.treeid.currentNode.oid;
                var event = 'SELECTED';
                if (_.has($scope.tree.selectedOids, oid)) {
                    delete $scope.tree.selectedOids[oid];
                    event = 'DELETED';
                }
                else {
                    $scope.tree.selectedOids[$scope.treeid.currentNode.oid] = $scope.treeid.currentNode;
                }
                if (!angular.isUndefined($scope.fnClickHandler) && angular.isFunction($scope.fnClickHandler)) {
                    $scope.fnClickHandler($scope.treeid.currentNode, event);
                }
                $scope.selectedOids = _.keys($scope.tree.selectedOids); // to array of oids
            }
        }, false);
        /*
         * HANDLE SELECT DATA CHANGES
         * - the search process starts and ends here.
         */
        function initTree() {
            $scope.createTreeData();
            for (var i = 0; i < $scope.oids.length; i++) {
                $scope.searchSinglePathToRootByOid($scope.oids[i]);
            }
            $q.all($scope.tree.activePromises).then(function() {
                angular.forEach($scope.tree.map.ROOT.childs, function(val, key) {
                    $scope.getCreateChildren($scope.tree.map, key, $scope.tree.treedata, val, true);
                });
                if (!angular.isUndefined($scope.fnLoadedHandler) && $scope.fnLoadedHandler !== null) {
                    $scope.fnLoadedHandler($scope.tree.map);
                }
            }, true);
        }
        $scope.$on('sisaltyvyysChanges', function() {
            initTree();
        });
        $scope.$watch('oids', function(newValue, oldValue) {
            if (newValue.length > 0 && (newValue !== oldValue || angular.isUndefined($scope.tree))) {
                initTree();
            }
        });
    }
    return {
        restrict: 'E',
        replace: true,
        templateUrl: 'js/shared/directives/treeField.html',
        controller: controller,
        scope: {
            lang: '@',
            //lang code like 'fi'
            reviewMode: '@',
            // delfaut : false, allow edit
            oids: '=',
            //komo OIDs
            reviewOids: '=',
            //joined komo OIDs
            fnClickHandler: '=',
            //function for click event
            fnLoadedHandler: '=',
            //function for click event
            names: '&' //names in a list of objects {oid : 'xx', nimi : 'abc'}
        }
    };
});