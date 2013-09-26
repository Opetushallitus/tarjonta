'use strict';

/* jasmine specs for controllers go here */

describe('controllers', function(){
  beforeEach(module('app.controllers'));


  it('should ....', inject(function() {
    //spec body
  }));

  it('should ....', inject(function() {
    //spec body
  }));
});

describe('TutkintoOhjelmaSelectOpenerCtrl testi', function() {
	beforeEach(module('app.kk.edit.ctrl'));
	beforeEach(module('config'));
	var $scope, $modalInstance, config;
	beforeEach(inject(function($rootScope){
		$scope = $rootScope.$new();
		$modalInstance = {
			$scope: $scope,
			templateUrl: 'partials/kk/edit/selectTutkintoOhjelma.html',
			controller: 'SelectTutkintoOhjelmaController'
		};
		config =  {
			
		};
	}));
	/*it('Testing the SelectTutkintoOhjelmaController initial values', inject(function($controller) {
		$controller('SelectTutkintoOhjelmaController', {
			$scope: $scope,
			$modalInstance: $modalInstance
			
		});
		expect($scope.stoModel.hakutulokset).toEqual([]);
		expect($scope.stoModel.koulutusala).toEqual({});
		expect($scope.stoModel.active).toEqual({});
		
	}));
	it('Testing the SelectTutkintoOhjelmaController toggleItem', inject(function($controller) {
		$controller('SelectTutkintoOhjelmaController', {
			$scope: $scope,
			$modalInstance: $modalInstance
		});
		var mockData = [{koodiUri: 'koodi_1'},{koodiUri: 'koodi_2'}];
		$scope.toggleItem(mockData[0]);
		expect($scope.stoModel.active).toEqual(mockData[0]);
	}));
	it('Testing the SelectTutkintoOhjelmaController isActive', inject(function($controller) {
		$controller('SelectTutkintoOhjelmaController', {
			$scope: $scope,
			$modalInstance: $modalInstance
		});
		
		var mockData = [{koodiUri: 'koodi_1'},{koodiUri: 'koodi_2'}];
		
		$scope.toggleItem(mockData[0]);
		
		expect($scope.isActive(mockData[1])).toEqual(false);
		expect($scope.isActive(mockData[0])).toEqual(true);
		
	}));
	/*it('Testing the SelectTutkintoOhjelmaController searchTutkinnot', inject(function($controller) {
		$controller('SelectTutkintoOhjelmaController', {
			$scope: $scope,
			$modalInstance: $modalInstance
		});
		
		expect($scope.stoModel.hakutulokset.length).toEqual(0);
		$scope.searchTutkinnot();
		expect($scope.stoModel.hakutulokset.length).toEqual($scope.rawData.length);
		$scope.stoModel.hakulause = 'AMK';
		$scope.searchTutkinnot();
		expect($scope.stoModel.hakutulokset.length).toEqual(3);
	}));
	it('Testing the SelectTutkintoOhjelmaController clearCriteria', inject(function($controller) {
		$controller('SelectTutkintoOhjelmaController', {
			$scope: $scope,
			$modalInstance: $modalInstance
		});
		
		$scope.stoModel.hakulause = 'AMK';
		$scope.clearCriteria();
		expect($scope.stoModel.hakulause).toEqual('');
	}));*/
});
