'use strict'

app.factory('sendDataService', function($http){
   return {
         sendData:function(setup,scope){
            var $promise = $http.post('http://localhost:8080/loginService/rest/send',setup);
            $promise.then(function(output){
            	scope.output = output.data;
            });
         }
   };
})
         