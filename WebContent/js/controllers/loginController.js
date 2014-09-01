'use strict'

app.controller('loginController',function($scope,$location,loginService){
		$scope.msgtxt = '';
	    $scope.login=function(user){
	    loginService.login(user,$scope,$location)};	   
});