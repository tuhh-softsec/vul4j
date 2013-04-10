/***************************************************************************************************
functions for creating and interacting with the topology view of the webui

flow related topology is in flows.js
***************************************************************************************************/

(function () {

updateTopology = function() {

	/* currently rings.js and map.js can be included to define the topology display */

	drawTopology();

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
