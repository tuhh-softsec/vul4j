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

var widths = {
	edge: 6,
	aggregation: 12,
	core: 18
}

function createTopologyModel(model) {
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