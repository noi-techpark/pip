var alps=angular.module('alps', ['ngRoute','ngFileUpload']);
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
alps.config(['$routeProvider',function($routeProvider) {
	$routeProvider.
	when('/', {
		templateUrl: 'partials/index.html',
		controller: 'RootCtrl'
	}).when('/ideas', {
		templateUrl: 'partials/ideas.html',
		controller: 'IdeaListCtrl'
	}).when('/ideas/:uuid', {
		templateUrl: 'partials/idea.html',
		controller: 'IdeaListCtrl'
	}).when('/idea/:uuid', {
		templateUrl: 'partials/idea-preview.html',
		controller: 'IdeaListCtrl'
	}).when('/topics', {
		templateUrl: 'partials/topics.html',
		controller: 'TopicCtrl'
	}).otherwise({
		redirectTo: '/'
	});
}]);

alps.controller('RootCtrl', function ($scope,$http,Upload) {
	var self = $scope;
	self.me = "http://production.digital.tis.bz.it:8080/alpenstaedte"
	self.createProjectIdea = function(){
		$http.post("create",self.idea).success(function(response,status,headers,config){
			if (self.files)
				self.uploadFiles(response.data);
            $('#idea-modal').modal('hide');

    	self.idea = {projectName:'',projectDesc:'',topics:[],fundings:[]};

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
		.attr("width", width + margin.right + margin.left)
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
					d._children = d.children;
					d._children.forEach(collapse);
					d.children = null;
				}
			}

			root.children.forEach(collapse);
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
	self.getIdeas = function(){
		$http.get("ideas").success(function(response,status,headers,config){
			self.ideas = response;
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}
	self.getIdea = function(){
		var params={uuid:$routeParams.uuid};
		$http.get("idea?"+$.param(params)).success(function(response,status,headers,config){
			self.idea = response;
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}
	self.getStatuses = function(){
		$http.get("statuses").success(function(response,status,headers,config){
			self.statuses = response;
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}
	self.updateIdea = function(){
		$http.post("update",self.idea).success(function(response,status,headers,config){
			self.syncFiles(response.data);
			self.ideaSaved = true;
			$timeout(function(){self.ideaSaved=false},2000);
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
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
});
alps.controller('TopicCtrl', function ($scope,$http) {
	var self = $scope;
	self.createTopic = function(){
		$http.post("topics",self.newTopic).success(function(response,status,headers,config){
			self.getTopics();
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
			self.getTopics();
			self.newTopic=undefined;
		}).error(function(data, status, headers, config) {
			console.log(status);
		});
	}
});
