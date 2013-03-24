/*global d3*/

d3.selection.prototype.moveToFront = function() {
  return this.each(function(){
    this.parentNode.appendChild(this);
  });
};

var colors = [
	'color1',
	'color2',
	'color3',
	'color4',
	'color5',
	'color6',
	'color7',
	'color8',
	'color9',
	'color10',
	'color11',
	'color12',
]
colors.reverse();

var controllerColorMap = {};



function createTopologyView() {
	var svg = d3.select('#svg-container').append('svg:svg');

	svg.append("svg:defs").append("svg:marker")
	    .attr("id", "arrow")
	    .attr("viewBox", "0 -5 10 10")
	    .attr("refX", -1)
	    .attr("markerWidth", 5)
	    .attr("markerHeight", 5)
	    .attr("orient", "auto")
	  .append("svg:path")
	    .attr("d", "M0,-3L10,0L0,3");

	return svg.append('svg:svg').attr('id', 'viewBox').attr('viewBox', '0 0 1000 1000').attr('preserveAspectRatio', 'none').
			attr('id', 'viewbox').append('svg:g').attr('transform', 'translate(500 500)');
}

function updateHeader(model) {
	d3.select('#lastUpdate').text(new Date());
	d3.select('#activeSwitches').text(model.edgeSwitches.length + model.aggregationSwitches.length + model.coreSwitches.length);
	d3.select('#activeFlows').text(model.flows.length);
}

function toRadians (angle) {
  return angle * (Math.PI / 180);
}

function createRingsFromModel(model) {
	var rings = [{
		radius: 3,
		width: 6,
		switches: model.edgeSwitches,
		className: 'edge',
		angles: []
	}, {
		radius: 2.25,
		width: 12,
		switches: model.aggregationSwitches,
		className: 'aggregation',
		angles: []
	}, {
		radius: .75,
		width: 18,
		switches: model.coreSwitches,
		className: 'core',
		angles: []
	}];


	var aggRanges = {};

	// arrange edge switches at equal increments
	var k = 360 / rings[0].switches.length;
	rings[0].switches.forEach(function (s, i) {
		var angle = k * i;

		rings[0].angles[i] = angle;

		// record the angle for the agg switch layout
		var dpid = s.dpid.split(':');
		dpid[7] = '01'; // the last component of the agg switch is always '01'
		var aggdpid = dpid.join(':');
		var aggRange = aggRanges[aggdpid];
		if (!aggRange) {
			aggRange = aggRanges[aggdpid] = {};
			aggRange.min = aggRange.max = angle;
		} else {
			aggRange.max = angle;
		}
	});

	// arrange aggregation switches to "fan out" to edge switches
	k = 360 / rings[1].switches.length;
	rings[1].switches.forEach(function (s, i) {
//		rings[1].angles[i] = k * i;
		var range = aggRanges[s.dpid];

		rings[1].angles[i] = (range.min + range.max)/2;
	});

	// find the association between core switches and aggregation switches
	var aggregationSwitchMap = {};
	model.aggregationSwitches.forEach(function (s, i) {
		aggregationSwitchMap[s.dpid] = i;
	});

	// put core switches next to linked aggregation switches
	k = 360 / rings[2].switches.length;
	rings[2].switches.forEach(function (s, i) {
//		rings[2].angles[i] = k * i;
		var associatedAggregationSwitches = model.configuration.association[s.dpid];
		// TODO: go between if there are multiple
		var index = aggregationSwitchMap[associatedAggregationSwitches[0]];

		rings[2].angles[i] = rings[1].angles[index];
	});





	return rings;
}

function updateTopology(svg, model) {

	// DRAW THE SWITCHES
	var rings = svg.selectAll('.ring').data(createRingsFromModel(model));

	function ringEnter(data, i) {
		if (!data.switches.length) {
			return;
		}

		// create the nodes
		var nodes = d3.select(this).selectAll("g")
			.data(d3.range(data.switches.length).map(function() {
				return data;
			}))
			.enter().append("svg:g")
			.classed('nolabel', true)
			.attr("id", function (_, i) {
				return data.switches[i].dpid;
			})
			.attr("transform", function(_, i) {
				return "rotate(" + data.angles[i]+ ")translate(" + data.radius * 150 + ")rotate(" + (-data.angles[i]) + ")";
			});

		// add the cirles representing the switches
		nodes.append("svg:circle")
			.attr("transform", function(_, i) {
				var m = document.querySelector('#viewbox').getTransformToElement().inverse();
				if (data.scale) {
					m = m.scale(data.scale);
				}
				return "matrix( " + m.a + " " + m.b + " " + m.c + " " + m.d + " " + m.e + " " + m.f + " )";
			})
			.attr("x", -data.width / 2)
			.attr("y", -data.width / 2)
			.attr("r", data.width)

		// setup the mouseover behaviors
		function showLabel(data, index) {
			d3.select(document.getElementById(data.switches[index].dpid + '-label')).classed('nolabel', false);
		}

		function hideLabel(data, index) {
			d3.select(document.getElementById(data.switches[index].dpid + '-label')).classed('nolabel', true);
		}

		nodes.on('mouseover', showLabel);
		nodes.on('mouseout', hideLabel);
	}

	// append switches
	rings.enter().append("svg:g")
		.attr("class", "ring")
		.each(ringEnter);


	function ringUpdate(data, i) {
		nodes = d3.select(this).selectAll("circle");
		nodes.attr('class', function (_, i)  {
				if (data.switches[i].state == 'ACTIVE') {
					return data.className + ' ' + controllerColorMap[data.switches[i].controller];
				} else {
					return data.className + ' ' + 'colorInactive';
				}
			})
	}

	// update  switches
	rings.each(ringUpdate);


	// Now setup the labels
	// This is done separately because SVG draws in node order and we want the labels
	// always on top
	var labelRings = svg.selectAll('.labelRing').data(createRingsFromModel(model));

	function labelRingEnter(data, i) {
		if (!data.switches.length) {
			return;
		}

		// create the nodes
		var nodes = d3.select(this).selectAll("g")
			.data(d3.range(data.switches.length).map(function() {
				return data;
			}))
			.enter().append("svg:g")
			.classed('nolabel', true)
			.attr("id", function (_, i) {
				return data.switches[i].dpid + '-label';
			})
			.attr("transform", function(_, i) {
				return "rotate(" + data.angles[i]+ ")translate(" + data.radius * 150 + ")rotate(" + (-data.angles[i]) + ")";
			});

		// add the text nodes which show on mouse over
		nodes.append("svg:text")
				.text(function (d, i) {return d.switches[i].dpid})
				.attr("x", 0)
				.attr("y", 0)
				.attr("transform", function(_, i) {
					var m = document.querySelector('#viewbox').getTransformToElement().inverse();
					if (data.scale) {
						m = m.scale(data.scale);
					}
					return "matrix( " + m.a + " " + m.b + " " + m.c + " " + m.d + " " + m.e + " " + m.f + " )";
				})
	}

	labelRings.enter().append("svg:g")
		.attr("class", "textRing")
		.each(labelRingEnter);


	// switches should not change during operation of the ui so no
	// rings.exit()


	// do mouseover zoom on edge nodes
	function zoom(data, index) {
		var g = d3.select(document.getElementById(data.switches[index].dpid)).select('circle');
			g.transition().duration(100).attr("r", g.data()[0].width*3);
			// TODO: this doesn't work because the data binding is by index
//			d3.select(this.parentNode).moveToFront();
	}

	svg.selectAll('.edge').on('mouseover', zoom);
	svg.selectAll('.edge').on('mousedown', zoom);

	function unzoom(data, index) {
		var g = d3.select(document.getElementById(data.switches[index].dpid)).select('circle');
			g.transition().duration(100).attr("r", g.data()[0].width);
	}
	svg.selectAll('.edge').on('mouseout', unzoom);


	// DRAW THE LINKS
	var line = d3.svg.line()
	    .x(function(d) {
	    	return d.x;
	    })
	    .y(function(d) {
	    	return d.y;
	    });
//	    .interpolate("basis");

	// key on link dpids since these will come/go during demo
	var links = d3.select('svg').selectAll('.link').data(model.links, function (d) {
			return d['src-switch']+'->'+d['dst-switch'];
	});

	// add new links
	links.enter().append("svg:path")
	.attr("class", "link")
	.attr("d", function (d) {

		var src = d3.select(document.getElementById(d['src-switch']));
		var dst = d3.select(document.getElementById(d['dst-switch']));

		var srcPt = document.querySelector('svg').createSVGPoint();
		srcPt.x = src.attr('x');
		srcPt.y = src.attr('y');
		srcPt = srcPt.matrixTransform(src[0][0].getCTM());

		var dstPt = document.querySelector('svg').createSVGPoint();
		dstPt.x = dst.attr('x');
		dstPt.y = dst.attr('y'); // tmp: make up and down links distinguishable
		dstPt = dstPt.matrixTransform(dst[0][0].getCTM());

		var midPt = document.querySelector('svg').createSVGPoint();
		midPt.x = (srcPt.x + dstPt.x)/2;
		midPt.y = (srcPt.y + dstPt.y)/2;

		return line([srcPt, midPt, dstPt]);
	})
	.attr("marker-mid", function(d) { return "url(#arrow)"; });

	// remove old links
	links.exit().remove();
}

function updateControllers(model) {
	var controllers = d3.select('#controllerList').selectAll('.controller').data(model.controllers);
	controllers.enter().append('div')
		.each(function (c) {
			controllerColorMap[c] = colors.pop();
			d3.select(document.body).classed(controllerColorMap[c] + '-selected', true);
		})
		.text(function (d) {
			return d;
		});

	controllers.attr('class', function (d) {
			var color = 'colorInactive';
			if (model.activeControllers.indexOf(d) != -1) {
				color = controllerColorMap[d];
			}
			var className = 'controller ' + color;
			return className;
		});

	// this should never be needed
	// controllers.exit().remove();

	controllers.on('click', function (c, index) {
		var allSelected = true;
		for (var key in controllerColorMap) {
			if (!d3.select(document.body).classed(controllerColorMap[key] + '-selected')) {
				allSelected = false;
				break;
			}
		}
		if (allSelected) {
			for (var key in controllerColorMap) {
				d3.select(document.body).classed(controllerColorMap[key] + '-selected', key == c)
			}
		} else {
			for (var key in controllerColorMap) {
				d3.select(document.body).classed(controllerColorMap[key] + '-selected', true)
			}
		}

		// var selected = d3.select(document.body).classed(controllerColorMap[c] + '-selected');
		// d3.select(document.body).classed(controllerColorMap[c] + '-selected', !selected);
	});
}

var oldModel;
function sync(svg) {
	var d = Date.now();
	updateModel(function (newModel) {
		console.log('Update time: ' + (Date.now() - d)/1000 + 's');

		if (true || !oldModel || JSON.stringify(oldModel) != JSON.stringify(newModel)) {
			updateControllers(newModel);
			updateTopology(svg, newModel);
		} else {
			console.log('no change');
		}
		updateHeader(newModel);

		oldModel = newModel;

		// do it again in 1s
		setTimeout(function () {
			sync(svg)
		}, 1000);
	});
}

svg = createTopologyView();
// workaround for Chrome v25 bug
// if executed immediately, the view box transform logic doesn't work properly
// fixed in Chrome v27
setTimeout(function () {
	// workaround for another Chrome v25 bug
	// viewbox transform stuff doesn't work in combination with browser zoom
	// also works in Chrome v27
	d3.select('#svg-container').style('zoom',  window.document.body.clientWidth/window.document.width);
	sync(svg);
}, 100);
