/***************************************************************************************************
functions for creating and interacting with the topology view of the webui

flow related topology is in flows.js
***************************************************************************************************/

(function () {

updateTopology = function() {

	// DRAW THE SWITCHES
	var rings = svg.selectAll('.ring').data(createTopologyModel(model));

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
	var labelRings = svg.selectAll('.labelRing').data(createTopologyModel(model));

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
	var linkLines = d3.select('svg').selectAll('.link').data(links, function (d) {
			return d['src-switch']+'->'+d['dst-switch'];
	});

	// add new links
	linkLines.enter().append("svg:path")
	.attr("class", "link");

	linkLines.attr('id', function (d) {
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
	linkLines.exit().remove();
}

})();
