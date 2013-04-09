/***************************************************************************************************
functions for creating and interacting with the topology view of the webui

flow related topology is in flows.js
***************************************************************************************************/

function createLinkMap(links) {
	var linkMap = {};
	links.forEach(function (link) {
		var srcDPID = link['src-switch'];
		var dstDPID = link['dst-switch'];

		var srcMap = linkMap[srcDPID] || {};

		srcMap[dstDPID] = link;

		linkMap[srcDPID]  = srcMap;
	});
	return linkMap;
}


// removes links from the pending list that are now in the model
function reconcilePendingLinks(model) {
	var links = [];
	model.links.forEach(function (link) {
		links.push(link);
		delete pendingLinks[makeLinkKey(link)]
	})
	var linkId;
	for (linkId in pendingLinks) {
		links.push(pendingLinks[linkId]);
	}
	return links
}


function createTopologyView() {

	window.addEventListener('resize', function () {
		// this is too slow. instead detect first resize event and hide the paths that have explicit matrix applied
		// either that or is it possible to position the paths so they get the automatic transform as well?
//		updateTopology();
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

// d3.xml("assets/map.svg", "image/svg+xml", function(xml) {
//   var importedNode = document.importNode(xml.documentElement, true);
//   var paths = importedNode.querySelectorAll('path');
//   var i;
//   for (i=0; i < paths.length; i+=1) {
//   	svg.append('svg:path')
//   		.attr('class', 'state')
//   		.attr('d', d3.select(paths.item(i)).attr('d'))
//   		.attr('transform', 'translate(-500 -500)scale(1 1.7)')
//   }
// });


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

updateTopology = function() {

	// DRAW THE SWITCHES
	var rings = svg.selectAll('.ring').data(createRingsFromModel(model));

	var links = reconcilePendingLinks(model);
	var linkMap = createLinkMap(links);

	function mouseOverSwitch(data) {

		d3.event.preventDefault();

		d3.select(document.getElementById(data.dpid + '-label')).classed('nolabel', false);

		if (data.highlighted) {
			return;
		}

		// only highlight valid link or flow destination by checking for class of existing highlighted circle
		var highlighted = svg.selectAll('.highlight')[0];
		if (highlighted.length == 1) {
			var s = d3.select(highlighted[0]).select('circle');
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

		var node = d3.select(document.getElementById(data.dpid));
		node.classed('highlight', true).select('circle').transition().duration(100).attr("r", widths.core);
		data.highlighted = true;
		node.moveToFront();
	}

	function mouseOutSwitch(data) {
		d3.select(document.getElementById(data.dpid + '-label')).classed('nolabel', true);

		if (data.mouseDown)
			return;

		var node = d3.select(document.getElementById(data.dpid));
		node.classed('highlight', false).select('circle').transition().duration(100).attr("r", widths[data.className]);
		data.highlighted = false;
		data.target = false;
	}

	function mouseDownSwitch(data) {
		mouseOverSwitch(data);
		data.mouseDown = true;
		d3.select('#topology').classed('linking', true);

		d3.select('svg')
			.append('svg:path')
			.attr('id', 'linkVector')
			.attr('d', function () {
				var s = d3.select(document.getElementById(data.dpid));

				var pt = document.querySelector('svg').createSVGPoint();
				pt.x = s.attr('x');
				pt.y = s.attr('y');
				pt = pt.matrixTransform(s[0][0].getCTM());

				return line([pt, pt]);
			});


		if (data.className === 'core') {
			d3.selectAll('.edge').classed('nodrop', true);
		}
		if (data.className === 'edge') {
			d3.selectAll('.core').classed('nodrop', true);
			d3.selectAll('.aggregation').classed('nodrop', true);
		}
		if (data.className === 'aggregation') {
			d3.selectAll('.edge').classed('nodrop', true);
			d3.selectAll('.aggregation').classed('nodrop', true);
		}
	}

	function mouseUpSwitch(data) {
		if (data.mouseDown) {
			data.mouseDown = false;
			d3.select('#topology').classed('linking', false);
			d3.event.stopPropagation();
			d3.selectAll('.nodrop').classed('nodrop', false);
		}
	}

	function doubleClickSwitch(data) {
		var circle = d3.select(document.getElementById(data.dpid)).select('circle');
		if (data.state == 'ACTIVE') {
			var prompt = 'Deactivate ' + data.dpid + '?';
			if (confirm(prompt)) {
				switchDown(data);
				setPending(circle);
			}
		} else {
			var prompt = 'Activate ' + data.dpid + '?';
			if (confirm(prompt)) {
				switchUp(data);
				setPending(circle);
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
		nodes.select('circle')
			.each(function (data) {
				// if there's a pending state changed and then the state changes, clear the pending class
				var circle = d3.select(this);
				if (data.state === 'ACTIVE' && circle.classed('inactive') ||
					data.state === 'INACTIVE' && circle.classed('active')) {
					circle.classed('pending', false);
				}
			})
			.attr('class', function (data)  {
				if (data.state === 'ACTIVE' && data.controller) {
					return data.className + ' active ' + controllerColorMap[data.controller];
				} else {
					return data.className + ' inactive ' + 'colorInactive';
				}
			});
	}

	// update  switches
	rings.each(ringUpdate);


	// Now setup the labels
	// This is done separately because SVG draws in node order and we want the labels
	// always on top
	var labelRings = svg.selectAll('.labelRing').data(createRingsFromModel(model));

	d3.select(document.body).on('mousemove', function () {
		if (!d3.select('#topology').classed('linking')) {
			return;
		}
		var linkVector = document.getElementById('linkVector');
		if (!linkVector) {
			return;
		}
		linkVector = d3.select(linkVector);

		var highlighted = svg.selectAll('.highlight')[0];
		var s1 = null, s2 = null;
		if (highlighted.length > 1) {
			var s1 = d3.select(highlighted[0]);
			var s2 = d3.select(highlighted[1]);

		} else if (highlighted.length > 0) {
			var s1 = d3.select(highlighted[0]);
		}
		var src = s1;
		if (s2 && !s2.data()[0].target) {
			src = s2;
		}
		if (src) {
			linkVector.attr('d', function () {
					var srcPt = document.querySelector('svg').createSVGPoint();
					srcPt.x = src.attr('x');
					srcPt.y = src.attr('y');
					srcPt = srcPt.matrixTransform(src[0][0].getCTM());

					var svg = document.getElementById('topology');
					var mouse = d3.mouse(viewbox);
					var dstPt = document.querySelector('svg').createSVGPoint();
					dstPt.x = mouse[0];
					dstPt.y = mouse[1];
					dstPt = dstPt.matrixTransform(viewbox.getCTM());

					return line([srcPt, dstPt]);
				});
		}
	});

	d3.select(document.body).on('mouseup', function () {
		function clearHighlight() {
			svg.selectAll('circle').each(function (data) {
				data.mouseDown = false;
				d3.select('#topology').classed('linking', false);
				mouseOutSwitch(data);
			});
			d3.select('#linkVector').remove();
		};

		d3.selectAll('.nodrop').classed('nodrop', false);

		function removeLink(link) {
			var path1 = document.getElementById(link['src-switch'] + '=>' + link['dst-switch']);
			var path2 = document.getElementById(link['dst-switch'] + '=>' + link['src-switch']);

			if (path1) {
				setPending(d3.select(path1));
			}
			if (path2) {
				setPending(d3.select(path2));
			}

			linkDown(link);
		}


		var highlighted = svg.selectAll('.highlight')[0];
		if (highlighted.length == 2) {
			var s1Data = highlighted[0].__data__;
			var s2Data = highlighted[1].__data__;

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
					addFlow(srcData, dstData);

					var flow = {
						dataPath: {
							srcPort: {
								dpid: {
									value: srcData.dpid
								}
							},
							dstPort: {
								dpid: {
									value: dstData.dpid
								}
							}
						},
					        srcDpid: srcData.dpid,
					        dstDpid: dstData.dpid,
						createPending: true
					};

					selectFlow(flow);

					setTimeout(function () {
						deselectFlowIfCreatePending(flow);
					}, pendingTimeout);
				}
			} else {
				var map = linkMap[srcData.dpid];
				if (map && map[dstData.dpid]) {
					var prompt = 'Remove link between ' + srcData.dpid + ' and ' + dstData.dpid + '?';
					if (confirm(prompt)) {
						removeLink(map[dstData.dpid]);
					}
				} else {
					map = linkMap[dstData.dpid];
					if (map && map[srcData.dpid]) {
						var prompt = 'Remove link between ' + dstData.dpid + ' and ' + srcData.dpid + '?';
						if (confirm(prompt)) {
							removeLink(map[srcData.dpid]);
						}
					} else {
						var prompt = 'Create link between ' + srcData.dpid + ' and ' + dstData.dpid + '?';
						if (confirm(prompt)) {
							var link1 = {
								'src-switch': srcData.dpid,
								'src-port': 1,
								'dst-switch': dstData.dpid,
								'dst-port': 1,
								pending: true
							};
							pendingLinks[makeLinkKey(link1)] = link1;
							var link2 = {
								'src-switch': dstData.dpid,
								'src-port': 1,
								'dst-switch': srcData.dpid,
								'dst-port': 1,
								pending: true
							};
							pendingLinks[makeLinkKey(link2)] = link2;
							updateTopology();

							linkUp(link1);

							// remove the pending links after 10s
							setTimeout(function () {
								delete pendingLinks[makeLinkKey(link1)];
								delete pendingLinks[makeLinkKey(link2)];

								updateTopology();
							}, pendingTimeout);
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
	var links = d3.select('svg').selectAll('.link').data(links, function (d) {
			return d['src-switch']+'->'+d['dst-switch'];
	});

	// add new links
	links.enter().append("svg:path")
	.attr("class", "link");

	links.attr('id', function (d) {
			return makeLinkKey(d);
		})
		.attr("d", function (d) {
			var src = d3.select(document.getElementById(d['src-switch']));
			var dst = d3.select(document.getElementById(d['dst-switch']));

			var srcPt = document.querySelector('svg').createSVGPoint();
			srcPt.x = src.attr('x');
			srcPt.y = src.attr('y');
			srcPt = srcPt.matrixTransform(src[0][0].getCTM());

			var dstPt = document.querySelector('svg').createSVGPoint();
			dstPt.x = dst.attr('x');
			dstPt.y = dst.attr('y');
			dstPt = dstPt.matrixTransform(dst[0][0].getCTM());

			var midPt = document.querySelector('svg').createSVGPoint();
			midPt.x = (srcPt.x + dstPt.x)/2;
			midPt.y = (srcPt.y + dstPt.y)/2;

			return line([srcPt, midPt, dstPt]);
		})
		.attr("marker-mid", function(d) { return "url(#arrow)"; })
		.classed('pending', function (d) {
			return d.pending;
		});


	// remove old links
	links.exit().remove();
}