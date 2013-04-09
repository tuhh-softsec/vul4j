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
	d3.select('#activeSwitches').text(model.edgeSwitches.length + model.aggregationSwitches.length + model.coreSwitches.length);
	d3.select('#activeFlows').text(model.flows.length);
}


