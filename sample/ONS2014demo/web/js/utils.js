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
	d3.select('#lastUpdate').text(new Date().toLocaleString());

	var activeSwitchCount = 0;
	model.edgeSwitches.forEach(function (s) {
		if (s.state === 'ACTIVE') {
			activeSwitchCount += 1;
		}
	});
	model.aggregationSwitches.forEach(function (s) {
		if (s.state === 'ACTIVE') {
			activeSwitchCount += 1;
		}
	});
	model.coreSwitches.forEach(function (s) {
		if (s.state === 'ACTIVE') {
			activeSwitchCount += 1;
		}
	});

	d3.select('#activeSwitches').text(activeSwitchCount);



	d3.select('#activeFlows').text(model.flows.length);
	d3.select('#activeLinks').text(model.links.length);
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

/***************************************************************************************************
counts the number of flows which pass through each core<->core link
***************************************************************************************************/
function countCoreLinkFlows() {
	var allCounts = {};
	model.flows.forEach(function (f) {
		if (f.dataPath && f.dataPath.flowEntries && f.dataPath.flowEntries.length > 1) {
			var flowEntries = f.dataPath.flowEntries;
			var i;

			for (i = 0; i < flowEntries.length - 1; i += 1) {
				var linkKey = flowEntries[i].dpid.value + '=>' + flowEntries[i+1].dpid.value;
				if (!allCounts[linkKey]) {
					allCounts[linkKey] = 1;
				} else {
					allCounts[linkKey] += 1;
				}
			}
		}
	});

	var coreCounts = {};
	var i, j;
	for (i = 0; i < model.coreSwitches.length - 1; i += 1) {
		for (j = i + 1; j < model.coreSwitches.length; j += 1) {
			var si = model.coreSwitches[i];
			var sj = model.coreSwitches[j];
			var key1 =  si.dpid + '=>' + sj.dpid;
			var key2 =  sj.dpid + '=>' + si.dpid;
			var linkCount = 0;
			if (allCounts[key1]) {
				linkCount += allCounts[key1];
			}
			if (allCounts[key2]) {
				linkCount += allCounts[key2];
			}

			coreCounts[key1] = linkCount;
		}
	}

	return d3.entries(coreCounts);
}


/***************************************************************************************************

***************************************************************************************************/
function doConfirm(prompt, cb, options) {
	var confirm = d3.select('#confirm');
	confirm.select('#confirm-prompt').text(prompt);

	var select = d3.select(document.getElementById('confirm-select'));
	if (options) {
		select.style('display', 'block');
		select.text('');
		select.selectAll('option').
			data(options)
			.enter()
				.append('option')
					.attr('value', function (d) {return d})
					.text(function (d) {return d});
	} else {
		select.style('display', 'none');
	}

	function show() {
		confirm.style('display', '-webkit-box');
		confirm.style('opacity', 0);
		setTimeout(function () {
			confirm.style('opacity', 1);
		}, 0);
	}

	function dismiss() {
		confirm.style('opacity', 0);
		confirm.on('webkitTransitionEnd', function () {
			confirm.style('display', 'none');
			confirm.on('webkitTransitionEnd', null);
		});
	}

	confirm.select('#confirm-ok').on('click', function () {
		d3.select(this).on('click', null);
		dismiss();
		if (options) {
			cb(select[0][0].value);
		} else {
			cb(true);
		}
	});

	confirm.select('#confirm-cancel').on('click', function () {
		d3.select(this).on('click', null);
		dismiss();
		cb(false);
	});

	show();
}





