var alps=angular.module('alps', ['ngFileUpload']);
alps.controller('RootCtrl', function ($scope,$http,Upload) {
	var self = $scope;
	self.createProjectIdea = function(){
		console.log(self.idea);
		$http.post("create",self.idea).success(function(data,status,headers,config){
			console.log(data);
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}
	self.uploadFiles = function(){
	}
});

