/*global d3*/

function createTopologyView() {
	return d3.select('#svg-container').append('svg:svg').attr('viewBox', '0 0 1000 1000').attr('preserveAspectRatio', 'none').
			attr('id', 'viewbox').append('svg:g').attr('transform', 'translate(500 500)');
}

function drawHeader(model) {
	d3.select('#lastUpdate').text(model.timestamp);
	d3.select('#activeSwitches').text(model.edgeSwitches.length + model.aggregationSwitches.length + model.coreSwitches.length);
	d3.select('#activeFlows').text(model.flows.length);
}

function toRadians (angle) {
  return angle * (Math.PI / 180);
}

function drawTopology(svg, model) {

	var rings = [{
		radius: 3,
		width: 4,
		switches: model.edgeSwitches,
		className: 'edge'
	}, {
		radius: 1.5,
		width: 32,
		switches: model.aggregationSwitches,
		className: 'aggregation'
	}, {
		radius: 1,
		width: 32,
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
				return data.className + i;
			})
			.attr("transform", function(_, i) {
				return "rotate(" + i * k + ")translate(" + data.radius * 150 + ")rotate(" + (-i * k) + ")";
			})
			.append("svg:circle")
			.attr('class', data.className)
			.attr("transform", function(_, i) {
				var m = document.querySelector('svg').getTransformToElement().inverse();
				if (data.scale) {
					m = m.scale(data.scale);
				}
				return "matrix( " + m.a + " " + m.b + " " + m.c + " " + m.d + " " + m.e + " " + m.f + " )";
			})
			.attr("x", -data.width / 2)
			.attr("y", -data.width / 2)
		// .attr("width", data.width)
		// .attr("height", data.width)
		.attr("r", data.width)
	}

	var ring = svg.selectAll("g")
		.data(rings)
		.enter().append("svg:g")
		.attr("class", "ring")
		.each(ringEnter);

	function zoom(d, i) {
		model.edgeSwitches.forEach(function (s) {
			s.scale = 1;
		});
		d.scale = 2;

		svg.selectAll('.edge').data(model.edgeSwitches).transition().duration(100)
			// .attr("transform", function(data, i) {
			// 	var m = document.querySelector('svg').getTransformToElement().inverse();
			// 		m = m.scale(data.scale);
			// 	return "matrix( " + m.a + " " + m.b + " " + m.c + " " + m.d + " " + m.e + " " + m.f + " )";
			// })
			.attr("r", function (data, i) {
				return rings[0].width * data.scale;
			})
	}

	svg.selectAll('.edge').on('mouseover', zoom);
	svg.selectAll('.edge').on('mousedown', zoom);
}

function sync(svg) {
	updateModel(function (model) {

		drawHeader(model);
		drawTopology(svg, model);

		// do it again in 1s
		setTimeout(function () {
			sync(svg)
		}, 5000);
	});
}

sync(createTopologyView());