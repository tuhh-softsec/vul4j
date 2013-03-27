/*global d3, document∆*/

d3.selection.prototype.moveToFront = function() {
  return this.each(function(){
    this.parentNode.appendChild(this);
  });
};

var line = d3.svg.line()
    .x(function(d) {
    	return d.x;
    })
    .y(function(d) {
    	return d.y;
    });

var model;
var svg, selectedFlowsView;
var updateTopology;

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
	'color12'
];
colors.reverse();

var controllerColorMap = {};



function createTopologyView() {

	window.addEventListener('resize', function () {
		// this is too slow. instead detect first resize event and hide the paths that have explicit matrix applied
		// either that or is it possible to position the paths so they get the automatic transform as well?
//		updateTopology(svg, model);
	});

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

var selectedFlowsData = [
	{selected: false, flow: null},
	{selected: false, flow: null},
	{selected: false, flow: null}
];

function drawFlows() {
	// DRAW THE FLOWS
	var flows = d3.select('svg').selectAll('.flow').data(selectedFlowsData, function (d) {
		return d.flow ? d.flow.flowId.value : null;
	});

	flows.enter().append("svg:path")
	.attr('class', 'flow')
	.attr('d', function (d) {
		if (!d.flow) {
			return;
		}
		var pts = [];
		d.flow.dataPath.flowEntries.forEach(function (flowEntry) {
			var s = d3.select(document.getElementById(flowEntry.dpid.value));
			var pt = document.querySelector('svg').createSVGPoint();
			pt.x = s.attr('x');
			pt.y = s.attr('y');
			pt = pt.matrixTransform(s[0][0].getCTM());
			pts.push(pt);
		});
		return line(pts);
	})
	.attr('stroke-dasharray', '3, 10')
	.append('svg:animate')
	.attr('attributeName', 'stroke-dashoffset')
	.attr('attributeType', 'xml')
	.attr('from', '500')
	.attr('to', '-500')
	.attr('dur', '20s')
	.attr('repeatCount', 'indefinite');

	flows.style('visibility', function (d) {
		if (d) {
			return d.selected ? '' : 'hidden';
		}
	})

	// "marching ants"
	// TODO: this will only be true if there's an iperf session running
	flows.select('animate').attr('from', function (d) {
		if (d.flow) {
			if (d.selected) {
				return '500';
			} else {
				return '-500';
			}
		}
	});
}

function updateFlowView() {
	selectedFlowsView.data(selectedFlowsData);

	selectedFlowsView.classed('selected', function (d) {
		if (d.flow) {
			return d.selected;
		}
	});

	selectedFlowsView.select('.flowId')
		.text(function (d) {
			if (d.flow) {
				return d.flow.flowId.value;
			}
		});

	selectedFlowsView.select('.srcDPID')
		.text(function (d) {
			if (d.flow) {
				return d.flow.dataPath.srcPort.dpid.value;
			}
		});

	selectedFlowsView.select('.dstDPID')
		.text(function (d) {
			if (d.flow) {
				return d.flow.dataPath.dstPort.dpid.value;
			}
		});
}

function createFlowView() {
	function rowEnter(d, i) {
		var row = d3.select(this);

		row.on('click', function () {
			selectedFlowsData[i].selected = !selectedFlowsData[i].selected;
			updateFlowView();
			drawFlows();
		});

		row.append('div')
			.classed('flowIndex', true)
			.text(function () {
				return i+1;
			});

		row.append('div')
			.classed('flowId', true);

		row.append('div')
			.classed('srcDPID', true);

		row.append('div')
			.classed('dstDPID', true);

		row.append('div')
			.classed('iperf', true);
	}

	var flows = d3.select('#selectedFlows')
		.selectAll('.selectedFlow')
		.data(selectedFlowsData)
		.enter()
		.append('div')
		.classed('selectedFlow', true)
		.each(rowEnter);


	return flows;
}

function updateHeader(model) {
	d3.select('#lastUpdate').text(new Date());
	d3.select('#activeSwitches').text(model.edgeSwitches.length + model.aggregationSwitches.length + model.coreSwitches.length);
	d3.select('#activeFlows').text(model.flows.length);
}

function toRadians (angle) {
  return angle * (Math.PI / 180);
}

var widths = {
	edge: 6,
	aggregation: 12,
	core: 18
}

function createRingsFromModel(model) {
	var rings = [{
		radius: 3,
		width: widths.edge,
		switches: model.edgeSwitches,
		className: 'edge',
		angles: []
	}, {
		radius: 2.25,
		width: widths.aggregation,
		switches: model.aggregationSwitches,
		className: 'aggregation',
		angles: []
	}, {
		radius: 0.75,
		width: widths.core,
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

	// TODO: construct this form initially rather than converting. it works better because
	// it allows binding by dpid
	var testRings = [];
	rings.forEach(function (ring) {
		var testRing = [];
		ring.switches.forEach(function (s, i) {
			var testSwitch = {
				dpid: s.dpid,
				state: s.state,
				radius: ring.radius,
				width: ring.width,
				className: ring.className,
				angle: ring.angles[i],
				controller: s.controller
			};
			testRing.push(testSwitch);
		});


		testRings.push(testRing);
	});


//	return rings;
	return testRings;
}

function createLinkMap(model) {
	var linkMap = {};
	model.links.forEach(function (link) {
		var srcDPID = link['src-switch'];
		var dstDPID = link['dst-switch'];

		var srcMap = linkMap[srcDPID] || {};

		srcMap[dstDPID] = link;

		linkMap[srcDPID]  = srcMap;
	});
	return linkMap;
}

updateTopology = function(svg, model) {

	// DRAW THE SWITCHES
	var rings = svg.selectAll('.ring').data(createRingsFromModel(model));

	var linkMap = createLinkMap(model);
//	var flowMap = createFlowMap(model);

	function mouseOverSwitch(data) {
		if (data.highlighted) {
			return;
		}

		// only highlight valid link or flow destination by checking for class of existing highlighted circle
		var highlighted = svg.selectAll('circle.highlight')[0];
		if (highlighted.length == 1) {
			var s = d3.select(highlighted[0]);
			// only allow links
			// 	edge->edge (flow)
			//  aggregation->core
			//	core->core
			if (data.className == 'edge' && !s.classed('edge') ||
				data.className == 'core' && !s.classed('core') && !s.classed('aggregation') ||
				data.className == 'aggregation' && !s.classed('core')) {
				return;
			}

			// don't highlight if there's already a link or flow
			// var map = linkMap[data.dpid];
			// console.log(map);
			// console.log(s.data()[0].dpid);
			// console.log(map[s.data()[0].dpid]);
			// if (map && map[s.data()[0].dpid]) {
			// 	return;
			// }

			// the second highlighted switch is the target for a link or flow
			data.target = true;
		}


		d3.select(document.getElementById(data.dpid + '-label')).classed('nolabel', false);
		var node = d3.select(document.getElementById(data.dpid));
		node.select('circle').classed('highlight', true).transition().duration(100).attr("r", widths.core);
		data.highlighted = true;
		node.moveToFront();
	}

	function mouseOutSwitch(data) {
		if (data.mouseDown)
			return;

		d3.select(document.getElementById(data.dpid + '-label')).classed('nolabel', true);
		var node = d3.select(document.getElementById(data.dpid));
		node.select('circle').classed('highlight', false).transition().duration(100).attr("r", widths[data.className]);
		data.highlighted = false;
		data.target = false;
	}

	function mouseDownSwitch(data) {
		mouseOverSwitch(data);
		data.mouseDown = true;
	}

	function mouseUpSwitch(data) {
		if (data.mouseDown) {
			data.mouseDown = false;
			d3.event.stopPropagation();
		}
	}

	function doubleClickSwitch(data) {
		if (data.state == 'ACTIVE') {
			var prompt = 'Deactivate ' + data.dpid + '?';
			if (confirm(prompt)) {
				switchDown(data);
			}
		} else {
			var prompt = 'Activate ' + data.dpid + '?';
			if (confirm(prompt)) {
				switchUp(data);
			}
		}
	}

	function ringEnter(data, i) {
		if (!data.length) {
			return;
		}

		// create the nodes
		var nodes = d3.select(this).selectAll("g")
			.data(data, function (data) {
				return data.dpid;
			})
			.enter().append("svg:g")
			.attr("id", function (data, i) {
				return data.dpid;
			})
			.attr("transform", function(data, i) {
				return "rotate(" + data.angle+ ")translate(" + data.radius * 150 + ")rotate(" + (-data.angle) + ")";
			});

		// add the cirles representing the switches
		nodes.append("svg:circle")
			.attr("transform", function(data, i) {
				var m = document.querySelector('#viewbox').getTransformToElement().inverse();
				if (data.scale) {
					m = m.scale(data.scale);
				}
				return "matrix( " + m.a + " " + m.b + " " + m.c + " " + m.d + " " + m.e + " " + m.f + " )";
			})
			.attr("x", function (data) {
				return -data.width / 2;
			})
			.attr("y", function (data) {
				return -data.width / 2;
			})
			.attr("r", function (data) {
				return data.width;
			});

		// setup the mouseover behaviors
		nodes.on('mouseover', mouseOverSwitch);
		nodes.on('mouseout', mouseOutSwitch);
		nodes.on('mouseup', mouseUpSwitch);
		nodes.on('mousedown', mouseDownSwitch);

		// only do switch up/down for core switches
		if (i == 2) {
			nodes.on('dblclick', doubleClickSwitch);
		}
	}

	// append switches
	rings.enter().append("svg:g")
		.attr("class", "ring")
		.each(ringEnter);


	function ringUpdate(data, i) {
		var nodes = d3.select(this).selectAll("g")
			.data(data, function (data) {
				return data.dpid;
			});
		nodes.select('circle').attr('class', function (data, i)  {
				if (data.state === 'ACTIVE' && data.controller) {
					return data.className + ' ' + controllerColorMap[data.controller];
				} else {
					return data.className + ' ' + 'colorInactive';
				}
			});
	}

	// update  switches
	rings.each(ringUpdate);


	// Now setup the labels
	// This is done separately because SVG draws in node order and we want the labels
	// always on top
	var labelRings = svg.selectAll('.labelRing').data(createRingsFromModel(model));

	d3.select(document.body).on('mouseup', function () {
		function clearHighlight() {
			svg.selectAll('circle').each(function (data) {
				data.mouseDown = false;
				mouseOutSwitch(data);
			})
		};


		var highlighted = svg.selectAll('circle.highlight')[0];
		if (highlighted.length == 2) {
			var s1Data = d3.select(highlighted[0]).data()[0];
			var s2Data = d3.select(highlighted[1]).data()[0];

			var srcData, dstData;
			if (s1Data.target) {
				dstData = s1Data;
				srcData = s2Data;
			} else {
				dstData = s2Data;
				srcData = s1Data;
			}

			if (s1Data.className == 'edge' && s2Data.className == 'edge') {
				var prompt = 'Create flow from ' + srcData.dpid + ' to ' + dstData.dpid + '?';
				if (confirm(prompt)) {
					alert('do create flow');
				} else {
					alert('do not create flow');
				}
			} else {
				var map = linkMap[srcData.dpid];
				if (map && map[dstData.dpid]) {
					var prompt = 'Remove link between ' + srcData.dpid + ' and ' + dstData.dpid + '?';
					if (confirm(prompt)) {
						linkDown(map[dstData.dpid]);
					}
				} else {
					map = linkMap[dstData.dpid];
					if (map && map[srcData.dpid]) {
						var prompt = 'Remove link between ' + dstData.dpid + ' and ' + srcData.dpid + '?';
						if (confirm(prompt)) {
							linkDown(map[srcData.dpid]);
						}
					} else {
						var prompt = 'Create link between ' + srcData.dpid + ' and ' + dstData.dpid + '?';
						if (confirm(prompt)) {
							linkUp(srcData, dstData);
						}
					}
				}
			}

			clearHighlight();
		} else {
			clearHighlight();
		}

	});

	function labelRingEnter(data) {
		if (!data.length) {
			return;
		}

		// create the nodes
		var nodes = d3.select(this).selectAll("g")
			.data(data, function (data) {
				return data.dpid;
			})
			.enter().append("svg:g")
			.classed('nolabel', true)
			.attr("id", function (data) {
				return data.dpid + '-label';
			})
			.attr("transform", function(data, i) {
				return "rotate(" + data.angle+ ")translate(" + data.radius * 150 + ")rotate(" + (-data.angle) + ")";
			})

		// add the text nodes which show on mouse over
		nodes.append("svg:text")
				.text(function (data) {return data.dpid;})
				.attr("x", function (data) {
					if (data.angle <= 90 || data.angle >= 270 && data.angle <= 360) {
						if (data.className == 'edge') {
							return - data.width*3 - 4;
						} else {
							return - data.width - 4;
						}
					} else {
						if (data.className == 'edge') {
							return data.width*3 + 4;
						} else {
							return data.width + 4;
						}
					}
				})
				.attr("y", function (data) {
					var y;
					if (data.angle <= 90 || data.angle >= 270 && data.angle <= 360) {
						if (data.className == 'edge') {
							y = data.width*3/2 + 4;
						} else {
							y = data.width/2 + 4;
						}
					} else {
						if (data.className == 'edge') {
							y = data.width*3/2 + 4;
						} else {
							y = data.width/2 + 4;
						}
					}
					return y - 6;
				})
				.attr("text-anchor", function (data) {
					if (data.angle <= 90 || data.angle >= 270 && data.angle <= 360) {
						return "end";
					} else {
						return "start";
					}
				})
				.attr("transform", function(data) {
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


	// DRAW THE LINKS

	// key on link dpids since these will come/go during demo
	var links = d3.select('svg').selectAll('.link').data(model.links, function (d) {
			return d['src-switch']+'->'+d['dst-switch'];
	});

	// add new links
	links.enter().append("svg:path")
	.attr("class", "link");

	links
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


	drawFlows();
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

	controllers.on('click', function (c) {
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

function sync(svg, selectedFlowsView) {
	var d = Date.now();
	updateModel(function (newModel) {
//		console.log('Update time: ' + (Date.now() - d)/1000 + 's');

		if (!model || JSON.stringify(model) != JSON.stringify(newModel)) {
			updateControllers(newModel);

	// fake flows right now
	var i;
	for (i = 0; i < newModel.flows.length && i < selectedFlowsData.length; i+=1) {
		var selected = selectedFlowsData[i] ? selectedFlowsData[i].selected : false;
		selectedFlowsData[i].flow = newModel.flows[i];
		selectedFlowsData[i].selected = selected;
	}

			updateFlowView(newModel);
			updateTopology(svg, newModel);
		} else {
//			console.log('no change');
		}
		updateHeader(newModel);

		model = newModel;

		// do it again in 1s
		setTimeout(function () {
			sync(svg)
		}, 1000);
	});
}

svg = createTopologyView();
selectedFlowsView = createFlowView();
// workaround for Chrome v25 bug
// if executed immediately, the view box transform logic doesn't work properly
// fixed in Chrome v27
setTimeout(function () {
	// workaround for another Chrome v25 bug
	// viewbox transform stuff doesn't work in combination with browser zoom
	// also works in Chrome v27
	d3.select('#svg-container').style('zoom',  window.document.body.clientWidth/window.document.width);
	sync(svg, selectedFlowsView);
}, 100);
