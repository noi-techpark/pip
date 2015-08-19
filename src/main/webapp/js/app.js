var alps=angular.module('alps', ['ngRoute','ngFileUpload','ngMessages','720kb.datepicker','ngCookies','ngSanitize']);
alps.directive('ngConfirmClick', [
function(){
      return {
          link: function (scope, element, attr) {
              var msg = attr.ngConfirmClick || "Are you sure?";
              var clickAction = attr.confirmedClick;
              element.bind('click',function (event) {
                      if ( window.confirm(msg) ) {
                          scope.$eval(clickAction)
                      }
              });
          }
      };
}]);
alps.filter('limitText', function() {
    return function(text, limit, tail) {

        var length = text.length;
        var finalString = text.length > limit ? text.substr(0, limit - 1) : text;
        if (text.length>limit && tail!= undefined && tail.length>0)
        	finalString += tail;
        return finalString;
    }
});
alps.filter('orgFilter', function() {
    return function(array, org) {
    	var finalArray=[];
    	$.each(array,function(index,value){
    		if (value.organizations[0].name==org.name)
    			finalArray.push(value);
    	});
        return finalArray;
    }
});
alps.run(function($rootScope,$http) {
	var self= $rootScope;
	self.getPrincipal = function(){
		$http.get("principal").success(function(data, status, headers, config){
			self.principal = data;
			self.isAdmin=self.checkRole("ADMIN");
			self.isManager=self.checkRole("MANAGER")||self.isAdmin;
		});
	}
	self.getMyUser = function(){
		$http.get("user").success(function(data, status, headers, config){
			self.myUser = data;
		});
	}
	self.objectIsEmpty = function(obj){
	    if (obj == null) return true;
	    if (obj.length > 0)    return false;
	    if (obj.length === 0)  return true;
	    for (var key in obj) {
	        if (hasOwnProperty.call(obj, key)) return false;
	    }
	    return true;
	}

	self.checkRole = function(roleString){
		var hasRole = false;
		$.each(self.principal.authorities,function(index,role){
			if (role.authority=="ROLE_"+roleString)
				hasRole= true;
		});
		return hasRole;
	}
	self.encode = function(string){
		return encodeURIComponent(string);
	};
});
alps.config(['$routeProvider',function($routeProvider) {
	$routeProvider.
	when('/', {
		templateUrl: 'partials/index.html',
		controller: 'RootCtrl'
	}).when('/ideas/:uuid', {
		templateUrl: 'partials/idea.html',
		controller: 'IdeaListCtrl'
	}).when('/idea/:uuid', {
		templateUrl: 'partials/idea-preview.html',
		controller: 'IdeaListCtrl'
	}).when('/topics', {
		templateUrl: 'partials/topics.html',
		controller: 'TopicCtrl'
	}).when('/user', {
		templateUrl: 'partials/user.html',
		controller: 'UserCtrl'
	}).when('/profile', {
		templateUrl: 'partials/profile.html',
		controller: 'UserCtrl'
	}).when('/profile/:uuid', {
		templateUrl: 'partials/profile-details.html',
		controller: 'UserCtrl'
	}).when('/contact', {
		templateUrl: 'partials/contact.html',
		controller: 'UserCtrl'
	}).when('/help', {
		templateUrl: 'partials/help.html',
		controller: 'UserCtrl'
	}).when('/pw-reset', {
		templateUrl: 'partials/password.html',
		controller: 'UserCtrl'
	}).otherwise({
		redirectTo: '/'
	});
}]);

alps.controller('RootCtrl', function ($scope,$http,Upload,$location,$cookies) {
	var self = $scope;
	if($cookies.redirect && $cookies.redirect.length>0){
		$location.path($cookies.redirect);
		$cookies.redirect='';
	}
	//self.me = "http://projectideas.tis.bz.it/alpenstaedte"
	self.me = "http://localhost:8080/alpenstaedte";
	self.isHidden = function(object){
		 return(self.topicFilter?object.name.toLowerCase().indexOf(self.topicFilter.toLowerCase()) < 0:false);
	}
	self.createProjectIdea = function(){
		if(self.projectidea.$valid){
			$http.post("create",self.idea).success(function(response,status,headers,config){
				if (self.files)
					self.uploadFiles(response.data);
	            $('#idea-modal').modal('hide');
	            $location.path("ideas/"+response.data);
	
	    	self.idea = {projectName:'',projectDesc:'',topics:[],fundings:[]};
	
			}).error(function(data, status, headers, config) {
				console.log(status);
			});
		}
	}
	self.getMyIdeas = function(){
		$http.get("myideas").success(function(response,status,headers,config){
			self.ideas = response;
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}
	self.getFavoriteIdeas = function(){
		$http.get("myfavorites").success(function(response,status,headers,config){
			self.ideas = response;
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}
	self.getIdeas = function(){
		$http.get("ideas").success(function(response,status,headers,config){
			self.ideas = response;
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}
	self.getTopics = function(){
		$http.get("topics").success(function(response,status,headers,config){
			self.availableTopics = response;
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}
	self.uploadFiles = function(uuid){
		Upload.upload({
			url:self.me+'/upload',
			file:self.files,
			fields:{uuid:uuid}
		}).progress(function(evt){
			  //console.log('progress: ' + parseInt(100.0 * evt.loaded / evt.total) + '% file :'+ evt.config.file.name);
		}).success(function(data, status, headers, config){
			  self.files = undefined;
		});
	}
	self.toggleTopic = function(topic){
			var isContained = false;
			$.each(self.idea.topics,function(index,object){
				if (object.name==topic.name){
					self.idea.topics.splice(index,1);
					isContained=true;
				}
			});
			if (!isContained)
				self.idea.topics.push(topic);
	}
	self.openModal=function(){
		$('#idea-modal').modal('show');
	};
	self.drawTree = function(graph){
		$("#display").empty();
		if (graph==undefined)
			graph='graph-data';
		var margin = {top: 20, right: 120, bottom: 20, left: 120},
		width = 960 - margin.right - margin.left,
		height = 800 - margin.top - margin.bottom;

		var i = 0,
		duration = 750,
		root;

		var tree = d3.layout.tree()
		.size([height, width]);

		var diagonal = d3.svg.diagonal()
		.projection(function(d) { return [d.y, d.x]; });

		var svg = d3.select("#display").append("svg")
		.attr("width", "100%")//.attr("width", width + margin.right + margin.left)
		.attr("height", height + margin.top + margin.bottom)
		.append("g")
		.attr("transform", "translate(" + margin.left + "," + margin.top + ")");

		d3.json(self.me+"/"+graph, function(error, flare) {
			if (error) throw error;

			root = flare;
			root.x0 = height / 2;
			root.y0 = 0;

			function collapse(d) {
				if (d.children) {
					console.log(d);
					d._children = d.children;
					d._children.forEach(collapse);
					d.children = null;
				}
			}
			$.each(root.children,function(index,value){
				value.children.forEach(collapse);
			});
			
			update(root);
		});

		d3.select(self.frameElement).style("height", "800px");

		function update(source) {

			// Compute the new tree layout.
			var nodes = tree.nodes(root).reverse(),
			links = tree.links(nodes);

			// Normalize for fixed-depth.
			nodes.forEach(function(d) { d.y = d.depth * 180; });

			// Update the nodes…
			var node = svg.selectAll("g.node")
			.data(nodes, function(d) { return d.id || (d.id = ++i); });

			// Enter any new nodes at the parent's previous position.
			var nodeEnter = node.enter().append("g")
			.attr("class", "node")
			.attr("transform", function(d) { return "translate(" + source.y0 + "," + source.x0 + ")"; })
			.on("click", click);
			
			nodeEnter.append("circle")
			.attr("r", 1e-6)
			.style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; });
			
			var link = nodeEnter.append("a")
			.attr("xlink:href",function(d,i) {if (d.uuid)return self.me+"/#/idea/" + d.uuid; else return "javascript:void(0)";});

			link.append("text")
			.attr("x", function(d) { return d.children || d._children ? -10 : 10; })
			.attr("dy", ".35em")
			.attr("text-anchor", function(d) { return d.children || d._children ? "end" : "start"; })
			.text(function(d) { return d.name; })
			.style("fill-opacity", 1e-6);

			// Transition nodes to their new position.
			var nodeUpdate = node.transition()
			.duration(duration)
			.attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; });

			nodeUpdate.select("circle")
			.attr("r", 4.5)
			.style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; });

			nodeUpdate.select("text")
			.style("fill-opacity", 1);

			// Transition exiting nodes to the parent's new position.
			var nodeExit = node.exit().transition()
			.duration(duration)
			.attr("transform", function(d) { return "translate(" + source.y + "," + source.x + ")"; })
			.remove();

			nodeExit.select("circle")
			.attr("r", 1e-6);

			nodeExit.select("text")
			.style("fill-opacity", 1e-6);

			// Update the links…
			var link = svg.selectAll("path.link")
			.data(links, function(d) { return d.target.id; });

			// Enter any new links at the parent's previous position.
			link.enter().insert("path", "g")
			.attr("class", "link")
			.attr("d", function(d) {
				var o = {x: source.x0, y: source.y0};
				return diagonal({source: o, target: o});
			});

			// Transition links to their new position.
			link.transition()
			.duration(duration)
			.attr("d", diagonal);

			// Transition exiting nodes to the parent's new position.
			link.exit().transition()
			.duration(duration)
			.attr("d", function(d) {
				var o = {x: source.x, y: source.y};
				return diagonal({source: o, target: o});
			})
			.remove();

			// Stash the old positions for transition.
			nodes.forEach(function(d) {
				d.x0 = d.x;
				d.y0 = d.y;
			});
		}

		// Toggle children on click.
		function click(d) {
			if (d.children) {
				d._children = d.children;
				d.children = null;
			} else {
				d.children = d._children;
				d._children = null;
			}
			update(d);
		}
	}
});

alps.controller('IdeaListCtrl', function ($scope,$http,Upload,$routeParams,$timeout,$location) {
	var self = $scope;
	self.checkIsOwner = function (){
		$http.get("idea/is-owner?uuid="+self.idea.uuid).success(function(response,status,headers,config){
			self.isOwner = response;
		}).error(function(data, status, headers, config) {
			console.log(status);
			self.isOwner = false;
		});
	}
	self.getIdea = function(){
		var params={uuid:$routeParams.uuid};
		$http.get("idea?"+$.param(params)).success(function(response,status,headers,config){
			self.idea = response;
			self.checkIsOwner();
			self.followsIdea();
			self.getProgress();
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}

	self.followsIdea = function(){
		var follows = false;
		$.each(self.idea.followers, function(index,value){
			if (value.email==self.myUser.email)
				follows=true;
		});
		self.follows =  follows;
	}
	self.getStatuses = function(){
		$http.get("statuses").success(function(response,status,headers,config){
			self.statuses = response;
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}
	self.updateIdea = function(move){
		if(self.projectidea.$valid){
			$http.post("update",self.idea).success(function(response,status,headers,config){
				self.syncFiles(response.data);
				if (move){
					$location.path("idea/"+self.idea.uuid);
				}else{
					self.ideaSaved = true;
					$timeout(function(){self.ideaSaved=false},2000);
				}
			}).error(function(data, status, headers, config) {
				console.log(status);
			});
		}
	}
	self.deleteIdea = function(){
		$http.delete("delete/"+self.idea.uuid).success(function(response,status,headers,config){
			$location.path("ideas");
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}

	self.syncFiles = function(uuid){
		Upload.upload({
			url:self.me+'/upload',
			file:self.files,
			fields:{uuid:uuid,alreadySavedFiles:self.idea.fileNames},
			data:{alreadysavedfiles:self.idea.filenames},
			sendFieldsAs:'blob-json'
		}).progress(function(evt){
			  //console.log('progress: ' + parseInt(100.0 * evt.loaded / evt.total) + '% file :'+ evt.config.file.name);
		}).success(function(data, status, headers, config){
			  self.files = undefined;
              self.getIdea();
		});
	}
	self.isTopic = function(topic){
		var value = false;
		$.each(self.idea.topics,function(index,object){
			if (object.name==topic)
				value = true;
		});
		return value;
	}
	self.toggleTopic = function(topic){
		var isContained = false;
		$.each(self.idea.topics,function(index,object){
			if (object.name==topic.name){
				self.idea.topics.splice(index,1);
				isContained=true;
			}
		});
		if (!isContained)
			self.idea.topics.push(topic);
	}
	self.comment = function(){
		if (self.commentText != undefined && self.commentText.length>0){
			$http.post("idea/comment/"+self.idea.uuid,self.commentText).success(function(response,status,headers,config){
				self.idea.comments.push(response);
				self.commentText= undefined;
			}).error(function(data, status, headers, config) {
				console.log(status);
			});
		}
	}
	self.follow = function(){
		$http.get("idea/"+self.idea.uuid+"/follow").success(function(response,status,headers,config){
			self.getIdea();
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}
	self.unfollow = function(){
		$http.get("idea/"+self.idea.uuid+"/unfollow").success(function(response,status,headers,config){
			self.getIdea();
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}
	self.blockComment= function(comment){
		$http.get("idea/comment/"+comment.uuid+"/block").success(function(response,status,headers,config){
			comment.banned=true;
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}
	self.unblockComment= function(comment){
		$http.get("idea/comment/"+comment.uuid+"/unblock").success(function(response,status,headers,config){
			comment.banned=false;
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}
	self.deleteComment = function(uuid,index){
		$http.delete("idea/comment/"+uuid).success(function(response,status,headers,config){
			self.getIdea();
		}).error(function(data, status, headers, config) {
			console.log(data);
		});
	}
	self.getProgress = function(){
		var width='0';
		if(self.idea.status == 'idea')
			width=16.666666667+'%';
		else if(self.idea.status == 'drafting')
			width=16.666666667*2+'%';
		else if(self.idea.status == 'application done')
			width=16.666666667*3+'%';
		else if(self.idea.status == 'funding not granted')
			width=16.666666667*4+'%';
		else if(self.idea.status == 'funding granted')
			width=16.666666667*5+'%';
		else if(self.idea.status == 'concluded')
			width='100%';
		var style={'width':width};
		
		self.progress=style;
	}
});
alps.controller('TopicCtrl', function ($scope,$http) {
	var self = $scope;
	self.createTopic = function(){
		$http.post("topics",self.newTopic).success(function(response,status,headers,config){
			self.getTopics();
			self.newTopic=undefined;
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}
	self.deleteTopic = function(uuid){
		$http.delete("topics?uuid="+uuid).success(function(response,status,headers,config){
			self.getTopics();
		}).error(function(data, status, headers, config) {
			if (status == 409)
				self.warning = "This topic is already asociated with ideas and therefore it can not be deleted"
		});
	}
	self.updateTopic = function(topic){
		$http.put("topics",topic).success(function(response,status,headers,config){
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}
});
alps.controller('UserCtrl', function ($scope,$http,$timeout,Upload,$routeParams) {
	var self = $scope;
	self.getUserByTopics = function(){
		$http.get("user/user-by-topics").success(function(response,status,headers,config){
			self.userByTopics = response;
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}
	self.createUser = function(){
		$http.post("user",self.user).success(function(response,status,headers,config){
			self.getUser();
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}
	self.deleteUser = function(email){
		$http.delete("user?email="+email).success(function(response,status,headers,config){
			self.getUser();
		}).error(function(data, status, headers, config) {
			if (status == 409)
				self.warning = "This topic is already asociated with ideas and therefore it can not be deleted"
		});
	}
	self.deactivateUser = function(email){
		$http.get("user/deactivate?email="+email).success(function(response,status,headers,config){
			self.getUser();
		}).error(function(data, status, headers, config) {
			console.log(data);
		});
	}
	self.activateUser = function(email){
		$http.get("user/activate?email="+email).success(function(response,status,headers,config){
			self.getUser();
		}).error(function(data, status, headers, config) {
			console.log(data);
		});
	}
	self.getUser = function(){
		$http.get("user/list").success(function(response,status,headers,config){
			self.users = response;
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}

	self.updateProfile = function(){
		if (self.profile.$valid){
			$http.put("user",self.user).success(function(response,status,headers,config){
				if (self.profilepic)
					self.uploadProfilePic();
				self.ideaSaved = true;
				$timeout(function(){self.ideaSaved=false},2000);
			}).error(function(data, status, headers, config) {
				console.log(status);
			});
		}
	}
	self.updateOrganisazion= function(user){
		$http.put("user/organization",user).success(function(response,status,headers,config){
			if (self.profilepic)
				self.uploadProfilePic();
			self.ideaSaved = true;
			$timeout(function(){self.ideaSaved=false},2000);
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}
	self.uploadProfilePic = function(){
		Upload.upload({	
			url:self.me + '/user/upload-profile-pic',
			file:self.profilepic,
			sendFieldsAs:'blob-json'
		}).progress(function(evt){
			  //console.log('progress: ' + parseInt(100.0 * evt.loaded / evt.total) + '% file :'+ evt.config.file.name);
		}).success(function(data, status, headers, config){
			  self.profilepic = undefined;
			self.getUser();
			self.ideaSaved=true;
		});
	}
	self.getProfile = function(){
		var url= "user";
		if ($routeParams.uuid!=undefined)
			url+='?uuid='+$routeParams.uuid;
		$http.get(url).success(function(response,status,headers,config){
			self.user = response;
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}
	self.isTopic = function(topic){
		var value = false;
		$.each(self.user.topics,function(index,object){
			if (object.name==topic)
				value = true;
		});
		return value;
	}
	self.toggleTopic = function(topic){
		var isContained = false;
		$.each(self.user.topics,function(index,object){
			if (object.name==topic.name){
				self.user.topics.splice(index,1);
				isContained=true;
			}
		});
		if (!isContained)
			self.user.topics.push(topic);
	}
	self.promote = function(userid){
		$http.put("user/promote",userid).success(function(){
			self.getUser();
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}
	self.demote = function(userid){
		$http.put("user/demote",userid).success(function(){
			self.getUser();
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}
	self.getOrganisations = function(){
		$http.get("user/organizations").success(function(response,status,headers,config){
			self.organisations = response;
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}
	self.languageSkills=["EN","DE","IT","FR","SL"];
	self.containsLanguage = function(lang){
		var value = false;
		$.each(self.user.languageSkills,function(index,object){
			if (object==lang)
				value = true;
		});
		return value;
	}

	self.toggleLang = function(lang){
		var isContained = false;
		$.each(self.user.languageSkills,function(index,object){
			if (object==lang){
				self.user.languageSkills.splice(index,1);
				isContained=true;
			}
		});
		if (!isContained)
			self.user.languageSkills.push(lang);
	}
	self.resetPassword= function(){
		var params ={
				oldpw: self.oldpw,
				newpw: self.newpw
		}
		$http.get("user/reset-password?"+$.param(params)).success(function(response,status,headers,config){
			self.success=true;
			self.newpw="";
			self.oldpw="";
		}).error(function(data, status, headers, config) {
			if (status==403){	
				self.newpw="";
				self.oldpw="";
				self.warning="Your current password was incorrect. Pls retry";
			
			}
		});
	}
	self.getHelps = function(){
		$http.get("helps").success(function(response,status,headers,config){
			self.helps = response;
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}
	self.addHelp = function(){
		var help = {
				name:self.files[0].name
		}
		$http.post("help",help).success(function(response,status,headers,config){
			self.uploadHelp();
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}
	self.uploadHelp = function(){
		Upload.upload({	
			url:self.me + '/help/upload',
			file:self.files,
		}).progress(function(evt){
			  //console.log('progress: ' + parseInt(100.0 * evt.loaded / evt.total) + '% file :'+ evt.config.file.name);
		}).success(function(data, status, headers, config){
			self.getHelps();
		});
	}
	self.deleteHelp = function(name){
		$http.delete("help/"+name).success(function(response,status,headers,config){
			self.getHelps();
		}).error(function(data, status, headers, config) {
			if (status == 409)
				self.warning = "This topic is already asociated with ideas and therefore it can not be deleted"
		});
	}
});
