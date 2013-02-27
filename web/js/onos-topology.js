function gui(data_source){
    var width = 960,
    height = 500;
    var radius = 8;
    var color = d3.scale.category20();

    var svg = d3.select("#topology").append("svg:svg")
	.attr("width", width)
	.attr("height", height);

    var force = d3.layout.force()
	.charge(-500)
	.linkDistance(100)
	.size([width, height]);

    var path = svg.selectAll("path");
    var circle = svg.selectAll("circle");
    var text = svg.selectAll("g");
    var node_drag = d3.behavior.drag()
        .on("dragstart", dragstart)
        .on("drag", dragmove)
        .on("dragend", dragend);

    d3.json(data_source, init);

/* For debugging  
    $("#more").click( function() {
        $.ajax({
	    url: 'http://gui.onlab.us:8080/topology_more',
	    success: function(json) {
		update(json);
	    },
	    dataType: "json"
        });
    });
    $("#less").click( function() {
        $.ajax({
	    url: 'http://gui.onlab.us:8080/topology_less',
	    success: function(json) {
		update(json);
	    },
	    dataType: "json"
        });
    });
*/

    function compare_link (a, b){
        if (a.source > b.source) {return 1;}
        else if (a.source < b.source) {return -1;}
        else {
            if (a.target > b.target) {return 1 ;}
            if (a.target < b.target) {return -1;}
            else {return 0;}
        }
    }

    function init(json){
        nodes = force.nodes();
        links = force.links();

	json.nodes.forEach(function(item) {
            nodes.push(item);
	});
	json.links.forEach(function(item) {
            links.push(item);
	});

        links.sort(compare_link);
        for (var i=1; i<links.length; i++) {
          if (links[i].source == links[i-1].source &&
            links[i].target == links[i-1].target) {
            links[i].linknum = links[i-1].linknum + 1;
          }
          else {
	      links[i].linknum = 1;
	  };
        };
	init_draw(nodes, links);
    }

    /* Return nodes that is not in the current list of nodes */
    Array.prototype.node_diff = function(arr) {
	return this.filter(function(i) {
	    for (var j = 0; j < arr.length ; j++) {
		if (arr[j].name === i.name)
		    return false;
	    }
	    return true;
	});
    };

    /* Return removed links */
    function gone_links (json, links, gone) {
	for (var i = 0; i < links.length ; i ++){
	    var found = 0;
	    for (var j = 0; j < json.links.length ; j ++){
		if (links[i].source.name == json.nodes[json.links[j].source].name && 
		    links[i].target.name == json.nodes[json.links[j].target].name ){
		    found = 1;
		    break;
		}
	    }
	    if ( found == 0 ){
		gone.push(links[i]);
	    }
	}
	return gone;
    }

    /* Return added links */
    function added_links (json, links, added) {
	for (var j = 0; j < json.links.length ; j ++){
	    var found = 0;
	    for (var i = 0; i < links.length ; i ++){
		if (links[i].source.name == json.nodes[json.links[j].source].name && 
		    links[i].target.name == json.nodes[json.links[j].target].name ){
		    found = 1;
		    break;
		}
	    }
	    if ( found == 0 ){
		added.push(json.links[j]);
	    }
	}
	return added;
    }

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

    /* check if toplogy has changed and update node[] and link[] accordingly */
    function cdiff(json) {
        var changed = false;

	var n_adds = json.nodes.node_diff(nodes);
        var n_rems = nodes.node_diff(json.nodes);
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
	l_adds = added_links(json, links, l_adds);
        l_rems = gone_links(json, links, l_rems);
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
	// Sorce/target of an element of l_adds[] are corresponding to the index of json.node[]
	// which is different from the index of node[] (new nodes are always added to the last)
	// So update soure/target node indexes of l_add[] need to be fixed to point to the proper
	// node in node[];
        for (var i = 0; i < l_adds.length; i++) {
	    for (var j = 0; j < nodes.length; j++) {
		if ( json.nodes[l_adds[i].source].name == nodes[j].name ){
		    l_adds[i].source = j; 
		    break;
		}
	    }
	    for (var j = 0; j < nodes.length; j++) {
		if ( json.nodes[l_adds[i].target].name == nodes[j].name ){
		    l_adds[i].target = j;
		    break;
		}
	    }
	    links.push(l_adds[i]);
	    changed = true;
        }

	// Update "group" attribute of nodes
	for (var i = 0; i < nodes.length; i++) {
            for (var j = 0; j < json.nodes.length; j++) {
		if ( nodes[i].name == json.nodes[j].name ){
		    if (nodes[i].group != json.nodes[j].group){
			nodes[i].group = json.nodes[j].group;
			changed = true;
		    }
		}
	    }
	}
	return changed
    }

    function draw(force, path, circle, text){
	force.stop();
        path.enter().append("svg:path")
	    .attr("class", function(d) { return "link"; });

        circle.enter().append("svg:circle")
	    .attr("r", radius)
	    .call(node_drag);
//            .call(force.drag);

	text.enter().append("svg:text")
	    .attr("x", radius)
	    .attr("y", ".31em")
	    .text(function(d) { return d.name.split(":")[5] + d.name.split(":")[6] + d.name.split(":")[7] });

        circle.append("title")
	    .text(function(d) { return d.name; });

	circle.attr("fill", function(d) {
	    if (d.group == 1){return "red";}
	    else if (d.group == 2){return "blue";}
	    else if (d.group == 3){return "green";}
	    else if (d.group == 4){return "orange";}
	    else{ return "gray"; }
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

	
	path.exit().remove();
        circle.exit().remove();
        text.exit().remove();
	
	force.on("tick", tick);
        force.start();

    }

    function update(json) {
	var changed = cdiff(json);

	console.log("changed? " + changed);

	if (changed){

            path = svg.selectAll("path").data(links)
            circle = svg.selectAll("circle").data(nodes);
	    text = svg.selectAll("text").data(nodes);

	    draw(force, path, circle, text);
	}
    }

    function init_draw(nodes, links){
        path = svg.append("svg:g").selectAll("path").data(links);
        circle = svg.append("svg:g").selectAll("circle").data(nodes);
	text = svg.append("svg:g").selectAll("text").data(nodes);

	draw(force, path, circle, text);

	setInterval(function() {
            $.ajax({
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
	    dr = 0;  // 0 for direct line
	    return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
	});
//	circle.attr("cx", function(d) { return d.x; }).attr("cy", function(d) { return d.y; });
	circle.attr("transform", function(d) {
	    x = Math.max(radius, Math.min(width - radius, d.x));
	    y = Math.max(radius, Math.min(height - radius, d.y)); 
//	    return "translate(" + d.x + "," + d.y + ")";
	    return "translate(" + x + "," + y + ")";
	})

	circle.attr("fill", function(d) {
	    ;	    if (d.group == 1){return "red";}
	    else if (d.group == 2){return "blue";}
	    else if (d.group == 3){return "green";}
	    else if (d.group == 4){return "orange";}
	    else{ return "gray"; }
	});
//	text.attr("x", function(d) { return d.x; }).attr("y", function(d) { return d.y; });
	text.attr("transform", function(d) {
	    return "translate(" + d.x + "," + d.y + ")";
	});
    }
}

