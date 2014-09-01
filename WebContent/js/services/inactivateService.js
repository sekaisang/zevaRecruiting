'use strict'

app.factory('inactivateService', function($http){
   return {
         inactivate:function(setup,scope){
        	
            var $promise = $http.post('http://localhost:8080/loginService/rest/inactivate',setup);
           
            $promise.then(function(json){
            	scope.profiles = json.data;
               });
            }
          }
   });