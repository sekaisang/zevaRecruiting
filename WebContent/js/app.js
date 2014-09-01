'use strict';


var app =  angular.module('recruiting',['ngRoute','ui.bootstrap.modal']);
app.config(['$routeProvider',function($routeProvider){
	$routeProvider.when('/login',{templateUrl:'partials/tpl/login.html',
		                          controller:'loginController'});
	$routeProvider.when('/home',{templateUrl:'partials/tpl/Home.html',
                                  controller:'homeController'});
	$routeProvider.when('/manageAgent',{templateUrl:'partials/tpl/ManageAgent.html',
                                        controller:'homeController'});
	$routeProvider.when('/test',{templateUrl:'partials/tpl/Test.html',
		                                  controller:'testController'});
	$routeProvider.otherwise({redirectTo:'/login'});
}]);


app.run(function($rootScope, $location, loginService) {
	
	  var routesThatRequireAuth = ['/home'];

	  $rootScope.$on('$routeChangeStart', function(event, next, current) {
	    if((routesThatRequireAuth).indexOf($location.path()) > -1 && !loginService.isLoggedIn()) {
	      $location.path('/login');
	    }
	  });
	});