function gui(data_source){
    var width = 960,
    height = 500;
    var color = d3.scale.category20();

    var svg = d3.select("body").append("svg:svg")
	.attr("width", width)
	.attr("height", height);

    var force = d3.layout.force()
	.charge(-500)
	.linkDistance(100)
	.size([width, height]);

    var path = svg.selectAll("path");
    var circle = svg.selectAll("circle");
    var text = svg.selectAll("g");


    d3.json(data_source,init);

    function init(json){
        nodes = force.nodes();
        links = force.links();

	json.nodes.forEach(function(item) {
            nodes.push(item);
	});
	json.links.forEach(function(item) {
            links.push(item);
	});
	draw(nodes, links);
    }

    function update(json) {
	Array.prototype.diff2 = function(arr) {
	    return this.filter(function(i) {
		for (var j = 0; j < arr.length ; j++) {
		    if (arr[j].source === i.source.index && 
			arr[j].target === i.target.index)
			return false;
		}
		return true;
	    });
	};

	Array.prototype.diff = function(arr) {
	    return this.filter(function(i) {
		for (var j = 0; j < arr.length ; j++) {
		    if (arr[j].source.index === i.source && 
			arr[j].target.index === i.target)
			return false;
		}
		return true;
	    });
	};

	Array.prototype.node_diff = function(arr) {
	    return this.filter(function(i) {
		for (var j = 0; j < arr.length ; j++) {
		    if (arr[j].name === i.name)
			return false;
		}
		return true;
	    });
	};


//        links.sort(function(a,b) {
//            if (a.source > b.source) {return 1;}
//            else if (a.source < b.source) {return -1;}
//            else {
//                if (a.target > b.target) {return 1;}
//                if (a.target < b.target) {return -1;}
//                else {return 0;}
//            }
//        });
//        for (var i=0; i<links.length; i++) {
//          if (i != 0 &&
//            links[i].source == links[i-1].source &&
//            links[i].target == links[i-1].target) {
//            links[i].linknum = links[i-1].linknum + 1;
//          }
//          else {links[i].linknum = 1;};
//        };



	function cdiff(topo) {
            var changed = false;
            var l_adds = topo.links.diff(links);
            var l_rems = links.diff2(topo.links);

            var n_adds = topo.nodes.node_diff(nodes);
            var n_rems = nodes.node_diff(topo.nodes);

            for (var i = 0; i < l_rems.length ; i++) {
		for (var j = 0; j < links.length; j++) {
                    if (links[j].source.index == l_rems[i].source.index &&
			links[j].target.index == l_rems[i].target.index) {
			links.splice(j,1);
			changed = true;
			break;
                    }
		}
            }
            for (var i = 0; i < l_adds.length; i++) {
		links.push(l_adds[i]);
		changed = true;
            }
            for (var i = 0; i < n_rems.length; i++) {
		for (var j = 0; j < nodes.length; j++) {
		    if ( nodes[j].name == n_rems[i].name ){
			nodes.splice(j,1);
			changed = true;
			break;
		    }
		}
            }
            for (var i = 0; i < n_adds.length; i++) {
		nodes.push(n_adds[i]);
		changed = true;
            }
	    return changed
	}

 

	var changed = cdiff(json);
        for (var i = 0; i < json.nodes.length; i++) {
	    nodes[i].group = json.nodes[i].group
	}

	console.log(circle);

	console.log("changed?");
	console.log(changed);


	if (changed){
            path = svg.selectAll("path").data(links)
            circle = svg.selectAll("circle").data(nodes);
	    text = svg.selectAll("text").data(nodes);

	    force.stop();

            path.enter().append("svg:path")
		.attr("class", function(d) { return "link"; })
		.attr("marker-end", "url(#Triangle)");

            circle.enter().append("svg:circle")
               .attr("r", 6)
               .call(force.drag);

/*	    text.enter().append("svg:text")
		.attr("x", 8)
		.attr("y", ".31em")
		.attr("class", "shadow")
		.text(function(d) { return d.name.split(":")[7]; }); */

	    text.enter().append("svg:text")
		.attr("x", 8)
		.attr("y", ".31em")
		.text(function(d) { return d.name.split(":")[7]; }); 

            circle.append("title")
	      .text(function(d) { return d.name; });

	    path.exit().remove();
            circle.exit().remove();
            text.exit().remove();

	    force.on("tick", tick);
            force.start();
	}
    }
    function draw(nodes, links){
        path = svg.append("svg:g").selectAll("path").data(links)
        circle = svg.append("svg:g").selectAll("circle").data(nodes);
	text = svg.append("svg:g").selectAll("text").data(nodes);

        path.enter().append("svg:path")
	    .attr("class", function(d) { return "link"; })
	    .attr("marker-end", "url(#Triangle)");

        circle.enter().append("svg:circle")
          .attr("r", 8)
          .call(force.drag);

/*	text.enter().append("svg:text")
	    .attr("x", 8)
	    .attr("y", ".31em")
	    .attr("class", "shadow")
	    .text(function(d) { return d.name.split(":")[7]; }); */

	text.enter().append("svg:text")
	    .attr("x", 8)
	    .attr("y", ".31em")
	    .text(function(d) { return d.name.split(":")[7]; }); 

	circle.append("title")
	    .text(function(d) { return d.name; });

	circle.attr("fill", function(d) {
	    if (d.group == 1){return "red";}
	    else if (d.group == 2){return "blue";}
	    else if (d.group == 3){return "green";}
	    else{ return "gray"; }
	});

	force.on("tick", tick);
	path.exit().remove();
        circle.exit().remove();
//        text.exit().remove();

	force.start();

	setInterval(function() {
            $.ajax({
//		url: 'http://onosnat.onlab.us:8080/topology',
		url: data_source,
		success: function(json) {
		    update(json)
		},
		dataType: "json"
            });
	}, 3000); 
    }
    function tick() {
	path.attr("d", function(d) {
	    var dx = d.target.x - d.source.x,
	    dy = d.target.y - d.source.y,
	    dr = 1/d.linknum;  //linknum is defined above
	    dr = 300;
	    return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
	});
//	circle.attr("cx", function(d) { return d.x; }).attr("cy", function(d) { return d.y; });
	circle.attr("transform", function(d) {
	    return "translate(" + d.x + "," + d.y + ")";
	})
	circle.attr("fill", function(d) {
	    if (d.group == 1){return "red";}
	    else if (d.group == 2){return "blue";}
	    else if (d.group == 3){return "green";}
	    else{ return "gray"; }
	});
//	text.attr("x", function(d) { return d.x; }).attr("y", function(d) { return d.y; });
//	text.attr("x", function(d) { return d.x; }).attr("y", function(d) { return d.y; });
	text.attr("transform", function(d) {
	    return "translate(" + d.x + "," + d.y + ")";
	});
    }
}
