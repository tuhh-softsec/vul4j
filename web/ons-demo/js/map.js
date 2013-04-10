


(function () {

var projection = d3.geo.mercator()
    .center([82, 45])
    .scale(10000)
    .rotate([-180,0]);

function createMap(svg, cb) {
	topology = svg.append('svg:svg').attr('id', 'viewBox').attr('viewBox', '0 0 1000 1000').
			attr('id', 'viewbox');

	var map = topology.append("g").attr('id', 'map');

	var path = d3.geo.path().projection(projection);

	d3.json('data/world.json', function(error, topology) {
		map.selectAll('path')
			.data(topojson.object(topology, topology.objects.world).geometries)
		    	.enter()
		      		.append('path')
		      		.attr('d', path)

		cb();
	});
}


var projection
createTopologyView = function (cb) {
	var svg = createRootSVG();

	createMap(svg, cb);
}

function makeSwitchesModel(switches) {
	var switchesModel = [];
	switches.forEach(function (s) {
		switchesModel.push({
			dpid: s.dpid,
			state: s.state,
			className: 'core',
			controller: s.controller,
			geo: model.configuration.geo[s.dpid]
		});
	});

	return switchesModel;
}

drawTopology = function () {


	// enter
	function switchEnter(s) {
		var g = d3.select(this);

		g.append('svg:circle').attr('r', widths.core);
		g.append('svg:text').text(s.geo.label).attr('transform', 'translate(' + widths.core + ' ' + widths.core + ')');

	}

	var coreSwitches = topology.selectAll('.core').data(makeSwitchesModel(model.coreSwitches))
		.enter()
		.append('svg:g')
		.attr("id", function (d) {
			return d.dpid;
		})
		.classed('core', true)
		.attr("transform", function(d) {
			if (d.geo) {
				return "translate(" + projection([d.geo.lng, d.geo.lat]) + ")";
			}
		})
		.each(switchEnter);



	// update
	coreSwitches
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

})();