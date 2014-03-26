'use strict';

/* Controllers  for selecting organisations */

var app = angular.module('app.haku.edit.organisations.ctrl', []);

app.controller('HakuEditSelectOrganisationsController', [
    '$modalInstance',
    '$q',
    '$scope',
    'OrganisaatioService',
    'AuthService',
    'PermissionService',
    '$location',
    '$log',
    'organisaatioOids', // injected from  hakuEditControllers.js
    function($modalInstance,
            $q,
            $scope,
            OrganisaatioService,
            AuthService,
            PermissionService,
            $location,
            $log,
            organisaatioOids) {

        $log = $log.getInstance("HakuEditSelectOrganisationsController");

        $log.info("HakuEditSelectOrganisationsController()...");

        $log.info("SCOPE: ", $scope);
        $log.info("injected organisaatioOids: ", organisaatioOids);

        $scope.model = {
            organisaatiot: []
        };
        $scope.lkorganisaatio = $scope.lkorganisaatio || {currentNode: undefined};

        // Watchi valitulle organisaatiolle
        $scope.$watch('lkorganisaatio.currentNode', function(organisaatio, oldVal) {
            $log.info("oprganisaatio valittu", organisaatio);
            if (!angular.isUndefined(organisaatio) && organisaatio !== null) {
                lisaaOrganisaatio(organisaatio);
            }
        });

        $scope.valitut = $scope.valitut || [];
        $scope.organisaatiomap = $scope.organisaatiomap || {};
        $scope.lkorganisaatiot = {};

        var promises = [];
        var deferred = $q.defer();
        promises.push(deferred.promise);

        // haetaan organisaatihierarkia joka valittuna kälissä tai jos mitään ei ole valittuna organisaatiot joihin käyttöoikeus
        OrganisaatioService.etsi({oidRestrictionList: AuthService.getOrganisations()}).then(function(vastaus) {
            $log.info("  X asetetaan org hakutulos modeliin.");
            $scope.lkorganisaatiot = vastaus.organisaatiot;

            //rakennetaan mappi oid -> organisaatio jotta löydetään parentit helposti
            var buildMapFrom = function(orglist) {
                for (var i = 0; i < orglist.length; i++) {
                    var organisaatio = orglist[i];
                    $scope.organisaatiomap[organisaatio.oid] = organisaatio;

                    // Check if this organisation is pre-selected?
                    if (organisaatioOids.indexOf(organisaatio.oid) > -1) {
                        // Add to selected organisation list
                        lisaaOrganisaatio(organisaatio);
                    }

                    if (organisaatio.children) {
                        buildMapFrom(organisaatio.children);
                    }
                }
            };
            buildMapFrom(vastaus.organisaatiot);
        });


        /**
         * Add organisation so selected list IFF not already selected.
         *
         * @param {type} organisaatio
         * @returns {undefined}
         */
        var lisaaOrganisaatio = function(organisaatio) {
            var arr = $scope.model.organisaatiot;

            var index = arr.indexOf(organisaatio);
            if (index > -1) {
                $log.info("ALREADY SELECTED!");
            } else {
                arr.push(organisaatio);
            }
        };

        /**
         * Dismiss, no changes.
         *
         * @returns {undefined}
         */
        $scope.doCancel = function() {
            $log.info("doCancel()");
            $modalInstance.dismiss();
        };

        /**
         * OK, send seledted org oids to the caller.
         *
         * @returns {undefined}
         */
        $scope.doOK = function() {
            $log.info("doOK()");

            var orgOids = [];
            for (var i = 0; i < $scope.model.organisaatiot.length; i++) {
                orgOids.push($scope.model.organisaatiot[i].oid);
            }

            // Send selected organisation oids to the caller
            $modalInstance.close(orgOids);
        };

        /**
         * Return true if organisations have been selected.
         *
         * @returns {Boolean}
         */
        $scope.hasSelectedOrganisations = function() {
            return $scope.model.organisaatiot.length > 0;
        };

        /**
         * Remove selected organisation ("X")
         */
        $scope.doRemoveSelected = function(organisaatio) {
            var valitut = [];
            for (var i = 0; i < $scope.model.organisaatiot.length; i++) {
                if ($scope.model.organisaatiot[i] !== organisaatio) {
                    valitut.push($scope.model.organisaatiot[i]);
                }
            }
            $scope.model.organisaatiot = valitut;
        };

        $log.info("HakuEditSelectOrganisationsController()... done.");

    }]);
