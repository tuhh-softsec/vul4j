/***************************************************************************************************
functions for creating and interacting with the topology view of the webui

flow related topology is in flows.js
***************************************************************************************************/

(function () {

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

updateTopology = function() {

	/* currently rings.js and map.js can be included to define the topology display */

	drawTopology();



	/* the remainder should work regardless of the topology display */

//	updateLinkLines();

	// setup the mouseover behaviors
	var allSwitches = d3.selectAll('.edge, .core, .aggregation');

	allSwitches.on('mouseover', mouseOverSwitch);
	allSwitches.on('mouseout', mouseOutSwitch);
	allSwitches.on('mouseup', mouseUpSwitch);
	allSwitches.on('mousedown', mouseDownSwitch);

	// only do switch up/down for core switches
	d3.selectAll('.core').on('dblclick', doubleClickSwitch);
}

})();
