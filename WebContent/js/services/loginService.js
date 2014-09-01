'use strict'

app.factory('loginService', function($http,sessionService){
   return {
         login:function(user,scope,$location){
            var $promise = $http.post('http://localhost:8080/loginService/rest/auth',user);
            $promise.then(function(json){
            	
                if (json.data != null)
                     {scope.msgtxt= 'Login success';
                      sessionService.set('authenticated', true); //isLoggedIn
                      sessionService.set('diceUsername',json.data.diceUsername);
                      sessionService.set('dicePassword',json.data.dicePassword);
                      sessionService.set('diceSearchAgent',json.data.diceAgent);
                      sessionService.set('diceMaxCandidates',json.data.diceMaxCandidates);
                      sessionService.set('diceCandidateIndex',json.data.diceCandidateIndex);
                      sessionService.set('diceAfterDate',json.data.diceAfterDate);
                      sessionService.set('diceEmailTemplate',json.data.diceEmailTemplate);
                      sessionService.set('monsterUsername',json.data.monsterUsername);
                      sessionService.set('monsterPassword',json.data.monsterPassword);
                      sessionService.set('monsterSearchAgent',json.data.monsterAgent);
                      sessionService.set('monsterMaxCandidates',json.data.monsterMaxCandidates);
                      sessionService.set('monsterCandidateIndex',json.data.monsterCandidateIndex);
                      sessionService.set('monsterAfterDate',json.data.monsterAfterDate);
                      sessionService.set('monsterEmailTemplate',json.data.monsterEmailTemplate);
                      if (json.data.diceSkipViewed == 'Y')
                        sessionService.set('diceSkipViewed',"Yes");
                       else sessionService.set('diceSkipViewed',"No");
                      if (json.data.diceSortByDate == 'Y')
                          sessionService.set('diceSortByDate',"Yes");
                         else sessionService.set('diceSortByDate',"No");
                      if (json.data.diceSendEmail == 'Y')
                          sessionService.set('diceSendEmail',"Yes");
                         else sessionService.set('diceSendEmail',"No");
                      if (json.data.diceSkipNoRelocation == 'Y')
                          sessionService.set('diceSkipNoRelocation',"Yes");
                         else sessionService.set('diceSkipNoRelocation',"No");
                      if (json.data.monsterSkipViewed == 'Y')
                          sessionService.set('monsterSkipViewed',"Yes");
                         else sessionService.set('monsterSkipViewed',"No");
                      if (json.data.monsterSortByDate == 'Y')
                            sessionService.set('monsterSortByDate',"Yes");
                           else sessionService.set('monsterSortByDate',"No");
                      if (json.data.monsterSendEmail == 'Y')
                            sessionService.set('monsterSendEmail',"Yes");
                           else sessionService.set('monsterSendEmail',"No");
                      if (json.data.monsterSkipNoRelocation == 'Y')
                            sessionService.set('monsterSkipNoRelocation',"Yes");
                           else sessionService.set('monsterSkipNoRelocation',"No");
                      //sessionService.set('emailTemplate',json.data.emailTemplate);
                      //console.log(sessionService.get('emailTemplate'));
                      $location.path('/home');
                      }
                 else scope.msgtxt = 'Login fail';
                 });
               },
         logout:function(scope,$location) {
        	sessionService.unset('authenticated');
            $location.path('/login');
            },
         isLoggedIn: function() {
            return sessionService.get('authenticated');
              }
            }
    	 });