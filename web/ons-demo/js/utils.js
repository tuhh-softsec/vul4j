/***************************************************************************************************
extract url parameters into a map
***************************************************************************************************/
function parseURLParameters() {
	var parameters = {};

	var search = location.href.split('?')[1];
	if (search) {
		search.split('&').forEach(function (param) {
			var key = param.split('=')[0];
			var value = param.split('=')[1];
			parameters[key] = decodeURIComponent(value);
		});
	}

	return parameters;
}

/***************************************************************************************************
convenience function for moving an SVG element to the front so that it draws on top
***************************************************************************************************/
d3.selection.prototype.moveToFront = function() {
  return this.each(function(){
    this.parentNode.appendChild(this);
  });
};

/***************************************************************************************************
standard function for generating the 'd' attribute for a path from an array of points
***************************************************************************************************/
var line = d3.svg.line()
    .x(function(d) {
    	return d.x;
    })
    .y(function(d) {
    	return d.y;
    });


/***************************************************************************************************
starts the "pending" animation
***************************************************************************************************/
function setPending(selection) {
	selection.classed('pending', false);
	setTimeout(function () {
		selection.classed('pending', true);
	}, 0);
}

/***************************************************************************************************
convert angle in degrees to radians
***************************************************************************************************/
function toRadians (degrees) {
  return degrees * (Math.PI / 180);
}

/***************************************************************************************************
used to generate DOM element id for this link
***************************************************************************************************/
function makeLinkKey(link) {
	return link['src-switch'] + '=>' + link['dst-switch'];
}

/***************************************************************************************************
used to generate DOM element id for this flow in the topology view
***************************************************************************************************/
function makeFlowKey(flow) {
	return flow.srcDpid + '=>' + flow.dstDpid;
}

/***************************************************************************************************
used to generate DOM element id for this flow in the selected flows table
***************************************************************************************************/
function makeSelectedFlowKey(flow) {
	return 'S' + makeFlowKey(flow);
}

/***************************************************************************************************
update the app header using the current model
***************************************************************************************************/
function updateHeader() {
	d3.select('#lastUpdate').text(new Date());

	var count = 0;
	model.edgeSwitches.forEach(function (s) {
		if (s.state === 'ACTIVE') {
			count += 1;
		}
	});
	model.aggregationSwitches.forEach(function (s) {
		if (s.state === 'ACTIVE') {
			count += 1;
		}
	});
	model.coreSwitches.forEach(function (s) {
		if (s.state === 'ACTIVE') {
			count += 1;
		}
	});

	d3.select('#activeSwitches').text(count);
	d3.select('#activeFlows').text(model.flows.length);
}

/***************************************************************************************************
update the global linkmap
***************************************************************************************************/
function updateLinkMap(links) {
	linkMap = {};
	links.forEach(function (link) {
		var srcDPID = link['src-switch'];
		var dstDPID = link['dst-switch'];

		var srcMap = linkMap[srcDPID] || {};

		srcMap[dstDPID] = link;

		linkMap[srcDPID]  = srcMap;
	});
}

/***************************************************************************************************
// removes links from the pending list that are now in the model
***************************************************************************************************/
function reconcilePendingLinks(model) {
	links = [];
	model.links.forEach(function (link) {
		links.push(link);
		delete pendingLinks[makeLinkKey(link)]
	})
	var linkId;
	for (linkId in pendingLinks) {
		links.push(pendingLinks[linkId]);
	}
}

/***************************************************************************************************
used by both ring and map models
***************************************************************************************************/
function createRootSVG() {
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

	return svg;
}


