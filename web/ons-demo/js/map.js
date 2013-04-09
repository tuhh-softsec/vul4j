


(function () {

createTopologyView = function (cb) {
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

	topology = svg.append('svg:svg').attr('id', 'viewBox').attr('viewBox', '0 0 1000 1000').
			attr('id', 'viewbox');

	var map = topology.append("g").attr('id', 'map');

	var projection = d3.geo.mercator()
	    .center([82, 45])
	    .scale(10000)
	    .rotate([-180,0]);

	var path = d3.geo.path().projection(projection);

	d3.json('data/world.json', function(error, topology) {
		map.selectAll('path')
			.data(topojson.object(topology, topology.objects.world).geometries)
		    	.enter()
		      		.append('path')
		      		.attr('d', path)

		cb();
	});


	// var map = topology.append('svg:g')
	// 	.attr('transform', 'scale(1.7 1.7)translate(-200, 0)');

	// d3.xml("assets/map.svg", "image/svg+xml", function(xml) {
	//   var importedNode = document.importNode(xml.documentElement, true);
	//   var paths = importedNode.querySelectorAll('path');
	//   var i;
	//   for (i=0; i < paths.length; i+=1) {
	//   	map.append('svg:path')
	//   		.attr('class', 'state')
	//   		.attr('d', d3.select(paths.item(i)).attr('d'))
	//   }

	//   cb();
	// });
}

drawTopology = function () {

}

})();