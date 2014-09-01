'use strict'

app.factory('getAgentsService', function($http){
   return {
         getAgents:function(setup,scope){
        	
            var $promise = $http.post('http://localhost:8080/loginService/rest/agent',setup);
           
            $promise.then(function(json){
            	scope.profiles = json.data;
               });
            }
          }
   });