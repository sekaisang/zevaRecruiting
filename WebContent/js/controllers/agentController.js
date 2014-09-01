'use strict'

app.controller('agentController',function($scope,sessionService){
	 
	
      $scope.profiles = sessionService.get('profiles');
      
	
})
