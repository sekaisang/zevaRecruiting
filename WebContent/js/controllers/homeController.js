'use strict'

app.controller('homeController',function($scope,$location,loginService,sendDataService,inactivateService,sessionService,getAgentsService){
	
	$scope.diceDefault = function (){
		
	$scope.setup= {company: 'dice',
		           username : sessionService.get('diceUsername'),
		           password : sessionService.get('dicePassword'),
		           //agent: sessionService.get(''),
		           maxCandidates : parseInt(sessionService.get('diceMaxCandidates')),
		           candidateIndex : parseInt(sessionService.get('diceCandidateIndex')),
		           //afterDate : sessionService.get('diceAfterDate'),
		           skipViewed : sessionService.get('diceSkipViewed'),
		           sortByDate : sessionService.get('diceSortByDate'),
		           sendEmail : sessionService.get('diceSendEmail'),
		           skipNoRelocation : sessionService.get('diceSkipNoRelocation'),
		           emailTemplate:sessionService.get('diceEmailTemplate')};
	//$scope.agent = {Name : sessionService.get('searchAgent')};

	};
	
	$scope.monsterDefault = function (){
		
		$scope.setup= {company: 'monster',
				   username : sessionService.get('monsterUsername'),
		           password : sessionService.get('monsterPassword'),
		           //agent: sessionService.get(''),
		           maxCandidates : parseInt(sessionService.get('monsterMaxCandidates')),
		           candidateIndex : parseInt(sessionService.get('monsterCandidateIndex')),
		           //afterDate : sessionService.get('monsterAfterDate'),
		           skipViewed : sessionService.get('monsterSkipViewed'),
		           sortByDate : sessionService.get('monsterSortByDate'),
		           sendEmail : sessionService.get('monsterSendEmail'),
		           skipNoRelocation : sessionService.get('monsterSkipNoRelocation'),
		           emailTemplate:sessionService.get('monsterEmailTemplate')};
		//$scope.agent = {Name : sessionService.get('searchAgent')};

		};
	
	
	$scope.sendData = function(setup){
		sendDataService.sendData(setup,$scope)
	};
	$scope.logout = function() {
		loginService.logout($scope,$location)};
		
    $scope.getAgents=function(setup){
	   getAgentsService.getAgents(setup,$scope,$location);};
	
	$scope.open = function() {
	    $scope.showModal = true;
	     };
	    
	$scope.ok = function() {
	    $scope.showModal = false;
	      };

	$scope.cancel = function() {
	    $scope.showModal = false;
	      };
	
	$scope.cancel2 = function() {
	  	$scope.chosenCompany = false;
	  	  };
	  	  
	$scope.inactivate = function(setup){
	     inactivateService.inactivate(setup,$scope)
		};   
	
	
})