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

describe('Edit koulutus testeja', function() {
	beforeEach(module('ngGrid'));
	var CONFIG_ENV_MOCK = {
	    "env": {
	        "key-env-1": "mock-value-env-1",
	        "key-env-2": "mock-value-env-2"
	    }, "app": {
	        "key-app-1": "mock-value-app-1"
	    }
	}
	    
	//set mock data to module by using the value-method,
	var mockModule = angular.module('test.module', []);
	mockModule.value('globalConfig', CONFIG_ENV_MOCK);

	beforeEach(module('test.module')); //mock module with the mock data
	
	beforeEach(module('app.kk.edit.ctrl'));
	beforeEach(module('config'));
	var $scope, $modalInstance;
	beforeEach(inject(function($rootScope){
		$scope = $rootScope.$new();
		$modalInstance = {
			$scope: $scope,
			templateUrl: 'partials/kk/edit/selectTutkintoOhjelma.html',
			controller: 'SelectTutkintoOhjelmaController'
		};
	}));
	it('Testing the SelectTutkintoOhjelmaController initial values', inject(function($controller) {
		$controller('SelectTutkintoOhjelmaController', {
			$scope: $scope,
			$modalInstance: $modalInstance
			
		});
		expect($scope.stoModel.hakutulokset).toEqual([]);
		expect($scope.stoModel.koulutusala).toEqual({});
		expect($scope.stoModel.active).toEqual({});
		
	}));
	it('Testing the SelectTutkintoOhjelmaController clearCriteria', inject(function($controller) {
		$controller('SelectTutkintoOhjelmaController', {
			$scope: $scope,
			$modalInstance: $modalInstance
		});
		
		$scope.stoModel.hakulause = 'AMK';
		$scope.clearCriteria();
		expect($scope.stoModel.hakulause).toEqual('');
	}));
	it('Testing the EditYhteyshenkiloCtrl clearYh', inject(function($controller) {
		$controller('EditYhteyshenkiloCtrl', {
			$scope: $scope,
		});
		
		$scope.contactPerson = {};
		$scope.contactPerson.nimet = 'Testi nimi';
		$scope.contactPerson.sahkoposti = 'test@oph.fi';
        $scope.contactPerson.titteli = 'Herra';
        $scope.contactPerson.puhelin = '050432134534';
        $scope.contactPerson.etunimet = 'Testi';
        $scope.contactPerson.sukunimi = 'nimi';
		
		$scope.editYhModel.clearYh();
		expect($scope.contactPerson.nimet).toEqual('');
	}));
	
	
});
