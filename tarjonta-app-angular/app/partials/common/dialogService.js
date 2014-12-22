var app = angular.module('app.dialog', [
    'ui.bootstrap',
    'ngAnimate',
    'ngSanitize',
    'localisation'
]);
/**
 * Very simplistinc dialog service. Simple yes now question at the moment.
 *
 * (Any more interaction in the UI, just use the $modal then)
 *
 * Usage example:
 * <pre>
 *       <div class="btn" ng-click="goBackCustom()">Back?</div>
 *
 *       $scope.goBackSimple = function() {
 *           var d = dialogService.showDialog();
 *           d.result.then(function(data) {
 *               if ("ACTION" === data) {
 *                   window.history.back();
 *               }
 *           });
 *       };
 *
 *       $scope.goBackCustom = function() {
 *           var texts = {title: "Really?", description: "<u>Really</u> go <b>back</b>?}", ok: "BACK!!!", cancel: "Nope..."};
 *           var opts = {backdrop: 'static', // true, false, 'static'
 *                       keyboard: false     // close with ESC?
 *           };
 *
 *           var d = dialogService.showDialog(texts, opts);
 *           d.result.then(function(data) {
 *               $log.info("GOT: ", data); // results: "ACTION" or "CANCEL"
 *               if ("ACTION" === data) {
 *                   window.history.back();
 *               }
 *           });
 *       };
 *
 * <pre>
 *
 * @param {type} $modal
 * @param {type} $log
 * @param {type} $rootScope
 * @param {type} LocalisationService
 */
app.service('dialogService', [
    '$modal',
    '$log',
    '$rootScope',
    'LocalisationService', function($modal, $log, $rootScope, LocalisationService) {
        var dialogDefaults = {
            templateUrl: 'partials/common/dialog.html',
            controller: 'DialogServiceController',
            backdrop: true,
            // true, "static", false
            keyboard: true // closable with ESC?
        };
        var dialogTextDefaults = {
            title: 'Really?',
            description: '<i>Really</i> do <b>that</b>?',
            ok: LocalisationService.t('ok'),
            cancel: LocalisationService.t('cancel')
        };
        /**
             * Returns ($modal) dialog instance which can then be used to wait for user interaction: dialogInstance.result.then(...
             *
             * customDialogTextDefaults:
             * <ul>
             *   <li>title: "foobar"</li>
             *   <li>description: "foobar <b>text</b>"</li>
             *   <li>ok: "Proceed"</li>
             *   <li>cancel: "abort"</li>
             * </ul>
             *
             * customDialogDefaults:
             * <ul>
             *   <li>backdrop: true, 'static', false</li>
             *   <li>keyboard: true / false</li>
             *   <li>templateUrl: 'MyController'</li>
             *   <li>templateUrl: 'MyTemplate.html'</li>
             * </ul>
             *
             * @param {type} customDialogTextDefaults
             * @param {type} customDialogDefaults
             * @returns {@exp;$modal@call;open}
             */
        this.showDialog = function(customDialogTextDefaults, customDialogDefaults) {
            var tempDialogDefaults = {};
            angular.extend(tempDialogDefaults, dialogDefaults, customDialogDefaults);
            var tempDialogTextDefaults = {};
            angular.extend(tempDialogTextDefaults, dialogTextDefaults, customDialogTextDefaults);
            if (!tempDialogDefaults.scope) {
                tempDialogDefaults.scope = $rootScope.$new();
            }
            if (!tempDialogDefaults.scope.dialog) {
                tempDialogDefaults.scope.dialog = tempDialogTextDefaults;
            }
            $log.info('showDialog(): ', tempDialogTextDefaults, tempDialogDefaults);
            return $modal.open(tempDialogDefaults);
        };
        this.showNotImplementedDialog = function() {
            var texts = {
                title: LocalisationService.t('notImplemented.title'),
                description: LocalisationService.t('notImplemented.description'),
                ok: LocalisationService.t('ok') // cancel: LocalisationService.t("cancel")
            };
            return this.showDialog(texts);
        };
        /**
             * Show dialog to inform user that form has unsaved information.
             * Result true means "ok, go anyway", cancel is "aha, I'll stay here".
             *
             * Usage example:
             * <pre>
             * dialogService.showModifedDialog().result.then(function(result) {
             *               if (true) {
             *                   $scope.navigateBack();
             *               }
             *           });
             * </pre>
             *
             *
             * @returns {unresolved}
             */
        this.showModifedDialog = function() {
            var texts = {
                title: LocalisationService.t('modified.title'),
                description: LocalisationService.t('modified.description'),
                ok: LocalisationService.t('ok'),
                cancel: LocalisationService.t('cancel')
            };
            return this.showDialog(texts);
        };
        /**
             * Show dialog, show text "as is". If cancel is undefined, only "ok" text will be show.
             *
             * @param {type} title
             * @param {type} description
             * @param {type} ok
             * @param {type} cancel
             * @returns {unresolved}
             */
        this.showSimpleDialog = function(title, description, ok, cancel) {
            var texts = {
                title: title,
                description: description,
                ok: ok,
                cancel: cancel
            };
            return this.showDialog(texts);
        };
    }
]);
/**
 * Simple default controller for the "partials/common/dialog.html" template.
 * Closes dialog with result of "ACTION" or "CANCEL".
 *
 * @param {type} param1
 * @param {type} param2
 */
app.controller('DialogServiceController', [
    '$scope',
    '$log',
    '$modalInstance', function($scope, $log, $modalInstance) {
        $log.info('dialogServiceController()');
        $scope.onAction = function() {
            $log.info('onAction()', $modalInstance);
            $modalInstance.close(true);
        };
        $scope.onClose = function() {
            $log.info('onClose()', $modalInstance);
            $modalInstance.close(false);
        };
    }
]);