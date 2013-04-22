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
    var pathlen = 0;
    var from;
    var to;
    var path_set = 0;


    d3.json(data_source,init);

    function init(json){
        nodes = force.nodes();
        links = force.links();

	json.nodes.forEach(function(item) {
            nodes.push(item);
	});
/*
        nodes.sort(function(a,b) {
            if (a.name > b.name) {return 1;}
            else if (a.name < b.name) {return -1;}
            else {return 0;}
        });
*/
	json.links.forEach(function(item) {
            links.push(item);
	});
	draw(nodes, links);
    }

    var node_drag = d3.behavior.drag()
        .on("dragstart", dragstart)
        .on("drag", dragmove)
        .on("dragend", dragend);

    function dragstart(d, i) {
        force.stop() // stops the force auto positioning before you start dragging
    }

    function dragmove(d, i) {
        d.px += d3.event.dx;
        d.py += d3.event.dy;
        d.x += d3.event.dx;
        d.y += d3.event.dy; 
        tick(); // this is the key to make it work together with updating both px,py,x,y on d !
    }

    function dragend(d, i) {
        d.fixed = true; // of course set the node to fixed so the force doesn't include the node in its auto positioning stuff
        tick();
        force.resume();
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


        links.sort(function(a,b) {
            if (a.source > b.source) {return 1;}
            else if (a.source < b.source) {return -1;}
            else {
                if (a.target > b.target) {return 1;}
                if (a.target < b.target) {return -1;}
                else {return 0;}
            }
        });

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

            var n_adds = topo.nodes.node_diff(nodes);
            var n_rems = nodes.node_diff(topo.nodes);
            for (var i = 0; i < n_adds.length; i++) {
		nodes.push(n_adds[i]);
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

            var l_adds = [];
            var l_rems = [];
	    l_adds = added_links(topo, links, l_adds);
            l_rems = gone_links(topo, links, l_rems);

            for (var i = 0; i < l_rems.length ; i++) {
		for (var j = 0; j < links.length; j++) {
                    if (links[j].source.name == l_rems[i].source.name &&
			links[j].target.name == l_rems[i].target.name) {
			links.splice(j,1);
			changed = true;
			break;
                    }
		}
            }
            for (var i = 0; i < l_adds.length; i++) {
		var s;
		var t;
		for (var j = 0; j < nodes.length; j++) {
		    if ( json.nodes[l_adds[i].source].name == nodes[j].name ){
			s = j;
			break;
		    }
		}
		for (var j = 0; j < nodes.length; j++) {
		    if ( json.nodes[l_adds[i].target].name == nodes[j].name ){
			t = j;
			break;
		    }
		}
		l_adds[i].source = s;
		l_adds[i].target = t;
		links.push(l_adds[i]);
		changed = true;
            }
	    return changed
	}


	var changed = cdiff(json);

        for (var i = 0; i < json.nodes.length; i++) {
	    nodes[i].group = json.nodes[i].group
	}
	var rewrite = 0;
	for (var i = 0; i < links.length; i++) {
            for (var j = 0; j < json.links.length; j++) {
	/*	
		console.log("link" + i);
		console.log(links[i].source.name + "->" + links[i].target.name);
		console.log("type " + links[i].type);
		console.log("json link" + j);
		console.log(json.nodes[json.links[j].source].name + "->" + json.nodes[json.links[j].target].name);
	*/
		if (links[i].target.name == json.nodes[json.links[j].target].name && 
		    links[i].source.name == json.nodes[json.links[j].source].name ){
		    links[i].type = json.links[j].type;
		    rewrite ++;
		}
	    }
	}
	console.log("changed?" + changed);
	if (changed){
	    console.log(nodes);
	    console.log(links);
            path = svg.selectAll("path").data(links)
            circle = svg.selectAll("circle").data(nodes);
	    text = svg.selectAll("text").data(nodes);

	    force.stop();

            path.enter().append("svg:path")
		.attr("class", function(d) { return "link"; })
		.attr("marker-end", function(d) {
		    if(d.type == 1){
			return "url(#TriangleRed)";
		    } else {
			return "url(#Triangle)";
		    }
		});

            circle.enter().append("svg:circle")
               .attr("r", 8)
/*		.on("click", function(d,i) {
		    if ( path_set == 0 || path_set == 2 ){
			fm = d.name;
			path_set = 1;
			alert("from set to " + d.name);
		    }else if ( path_set == 1 ){
			to = d.name;
			path_set = 2;
			alert("to set to " + d.name);
		    }
		}) */
	        .call(node_drag);
//               .call(force.drag);

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

	    path.attr("stroke", function(d) {
		if(d.type == 1){
		    return "red"
		} else {
		    return "black"
		}
	    })
	    .attr("stroke-width", function(d) {
		if(d.type == 1){
		    return "4px";
		} else {
		    return "1.5px";
		}
	    });


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
	    .attr("marker-end", function(d) {
		if(d.type == 1){
		    return "url(#TriangleRed)";
		} else {
		    return "url(#Triangle)";
		}
	    });

	path.attr("stroke", function(d) {
	    if(d.type == 1){
		return "red"
	    } else {
		return "black"
	    }
	}).attr("stroke-width", function(d) {
	    if(d.type == 1){
		return "4px";
	    } else {
		return "1.5px";
	    }
	}).attr("marker-end", function(d) {
	    if(d.type == 1){
		return "url(#TriangleRed)";
	    } else {
		return "url(#Triangle)";
	    }
	});


        circle.enter().append("svg:circle")
          .attr("r", 8)
/*	    .on("click", function(d,i) {
		if ( path_set == 0 || path_set == 2 ){
		    fm = d.name;
		    path_set = 1;
		    alert("from set to " + d.name);
		}else if ( path_set == 1 ){
		    to = d.name;
		    path_set = 2;
		    alert("to set to " + d.name);
		}
	    }) */
	    .call(node_drag);
//          .call(force.drag);

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
//	    dr = 1/d.linknum;  //linknum is defined above
	    dr = 300;
	    return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
	});

	path.attr("stroke", function(d) {
	    if(d.type == 1){
		return "red"
	    } else {
		return "black"
	    }
	}).attr("stroke-width", function(d) {
	    if(d.type == 1){
		return "4px";
	    } else {
		return "1.5px";
	    }
	}).attr("marker-end", function(d) {
	    if(d.type == 1){
		return "url(#TriangleRed)";
	    } else {
		return "url(#Triangle)";
	    }
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
