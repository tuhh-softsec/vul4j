(function () {

createTopologyView = function (cb) {

	window.addEventListener('resize', function () {
		// this is too slow. instead detect first resize event and hide the paths that have explicit matrix applied
		// either that or is it possible to position the paths so they get the automatic transform as well?
//		updateTopology();
	});

	var svg = createRootSVG();

	topology = svg.append('svg:svg').attr('id', 'viewBox').attr('viewBox', '0 0 1000 1000').attr('preserveAspectRatio', 'none').
			attr('id', 'viewbox').append('svg:g').attr('id', 'topology').attr('transform', 'translate(500 500)');

	flowLayer = d3.select('svg');

	// hack to make the shared flow drawing code work
	drawingRings = true;

	cb();
}

function updateLinkLines() {

	// key on link dpids since these will come/go during demo
	var linkLines = d3.select('svg').selectAll('.link').data(links, function (d) {
		return d['src-switch']+'->'+d['dst-switch'];
	});

	// add new links
	linkLines.enter().append("svg:path").attr("class", "link");

	linkLines.attr('id', function (d) {
			return makeLinkKey(d);
		}).attr("d", function (d) {
			var src = d3.select(document.getElementById(d['src-switch']));
			var dst = d3.select(document.getElementById(d['dst-switch']));

			if (src.empty() || dst.empty()) {
				return "M0,0";
			}

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
	linkLines.exit().remove();
}


function createRingTopologyModel(model) {
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

drawTopology = function () {
	// DRAW THE SWITCHES
	var rings = topology.selectAll('.ring').data(createRingTopologyModel(model));

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
	var labelRings = topology.selectAll('.labelRing').data(createRingTopologyModel(model));

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

	updateLinkLines();
}

})();
