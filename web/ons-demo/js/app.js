/*global d3*/

function createTopologyView() {
	return d3.select('#svg-container').append('svg:svg').attr('viewBox', '0 0 1000 1000').
			append('svg:g').attr('transform', 'translate(500 500)');
}



function drawHeader(model) {
	d3.select('#lastUpdate').text(model.timestamp);
}

function drawTopology(svg, model) {

	var rings = [{
		radius: 3,
		width: 16,
		switches: model.edgeSwitches
	}, {
		radius: 1.5,
		width: 32,
		switches: model.aggregationSwitches
	}, {
		radius: 1,
		width: 32,
		switches: model.coreSwitches
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
			.attr("class", "square")
			.attr("transform", function(_, i) {
			return "rotate(" + i * k + ")translate(" + data.radius*150 + ")";
		})
			.append("svg:rect")
			.attr("x", -data.width / 2)
			.attr("y", -data.width / 2)
			.attr("width", data.width)
			.attr("height", data.width);
	}

	var ring = svg.selectAll("g")
		.data(rings)
		.enter().append("svg:g")
		.attr("class", "ring")
		.each(ringEnter);
}

function sync(svg) {
	updateModel(function (model) {

		drawHeader(model);
		drawTopology(svg, model);

		// do it again in 1s
		setTimeout(function () {
			sync(svg)
		}, 1000);
	});
}

sync(createTopologyView());