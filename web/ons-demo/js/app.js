/*global d3*/


var colors = [
	'#EC0033',
	'#FFBA00',
	'#3714B0',
	'#B12C49',
	'#BF9830',
	'#402C84',
	'#990021',
	'#A67900',
	'#F53D65',
	'#1F0772',
	'#F56E8B',
	'#FFCB40',
	'#6949D7',
	'#FFD973'
]

var controllerColorMap = {};



function createTopologyView() {
	return d3.select('#svg-container').append('svg:svg').append('svg:svg').attr('id', 'viewBox').attr('viewBox', '0 0 1000 1000').attr('preserveAspectRatio', 'none').
			attr('id', 'viewbox').append('svg:g').attr('transform', 'translate(500 500)');
}

function updateHeader(model) {
	d3.select('#lastUpdate').text(model.timestamp);
	d3.select('#activeSwitches').text(model.edgeSwitches.length + model.aggregationSwitches.length + model.coreSwitches.length);
	d3.select('#activeFlows').text(model.flows.length);
}

function toRadians (angle) {
  return angle * (Math.PI / 180);
}

function updateTopology(svg, model) {

	// DRAW THE NODES
	var rings = [{
		radius: 3,
		width: 4,
		switches: model.edgeSwitches,
		className: 'edge'
	}, {
		radius: 2.25,
		width: 8,
		switches: model.aggregationSwitches,
		className: 'aggregation'
	}, {
		radius: .75,
		width: 16,
		switches: model.coreSwitches,
		className: 'core'
	}];

	function ringEnter(data, i) {
		if (!data.switches.length) {
			return;
		}

		var k = 360 / data.switches.length;

		d3.select(this).selectAll("g")
			.data(d3.range(data.switches.length).map(function() {
			return data;
		}))
			.enter().append("svg:g")
			.attr("id", function (_, i) {
				return data.switches[i].dpid;
			})
			.attr("transform", function(_, i) {
				return "rotate(" + i * k + ")translate(" + data.radius * 150 + ")rotate(" + (-i * k) + ")";
			})
			.append("svg:circle")
			.attr('class', data.className)
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
			.attr("fill", function (_, i) {
				return controllerColorMap[data.switches[i].controller]
			})
	}

	var ring = svg.selectAll("g")
		.data(rings)
		.enter().append("svg:g")
		.attr("class", "ring")
		.each(ringEnter);

	function zoom(data, index) {
		svg.selectAll('.edge').data(model.edgeSwitches).transition().duration(100)
			.attr("r", function (data, i) {
				return rings[0].width * (index == i ? 2 : 1);
			})
	}

	svg.selectAll('.edge').on('mouseover', zoom);
	svg.selectAll('.edge').on('mousedown', zoom);

	// DRAW THE LINKS
	var line = d3.svg.line()
	    .x(function(d) {
	    	return d.x;
	    })
	    .y(function(d) {
	    	return d.y;
	    })
	    .interpolate("basis");

	d3.select('svg').selectAll('path').data(model.links).enter().append("svg:path").attr("d", function (d) {
		var src = d3.select(document.getElementById(d['src-switch']));
		var dst = d3.select(document.getElementById(d['dst-switch']));

		var srcPt = document.querySelector('svg').createSVGPoint();
		srcPt.x = src.attr('x');
		srcPt.y = src.attr('y');

		var dstPt = document.querySelector('svg').createSVGPoint();
		dstPt.x = dst.attr('x');
		dstPt.y = dst.attr('y');

		return line([srcPt.matrixTransform(src[0][0].getCTM()), dstPt.matrixTransform(dst[0][0].getCTM())]);
	});
}

function updateControllers(model) {
	var controllers = d3.select('#controllerList').selectAll('.controller').data(model.controllers);
	controllers.enter().append('div').attr('class', 'controller')
		.attr('style', function (d) {
			var color = controllerColorMap[d];
			if (!color) {
				color = controllerColorMap[d] = colors.pop();
			}
			return 'background-color:' + color;
		});
	controllers.text(function (d) {
		return d;
	});
	controllers.exit().remove();

	controllers.on('click', function (data, index) {

	});
}

function sync(svg) {
	updateModel(function (model) {

		updateHeader(model);
		updateControllers(model);
		updateTopology(svg, model);

		// do it again in 1s
		setTimeout(function () {
			sync(svg)
		}, 1000);
	});
}

sync(createTopologyView());