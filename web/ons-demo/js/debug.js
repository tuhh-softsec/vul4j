/***************************************************************************************************
find the links that include the switch with this dpid
***************************************************************************************************/
function debug_findlink(model, dpid) {
	var links = [];
	model.links.forEach(function (link) {
		if (link['src-switch'] == dpid || link['dst-switch'] == dpid) {
			links.push(link);
		}
	});
	return links;
}

function debug_findswitch(model, dpid) {
	var sw;

	model.edgeSwitches.forEach(function (s) {
		if (s.dpid == dpid)
			sw = s;
	});
	model.aggregationSwitches.forEach(function (s) {
		if (s.dpid == dpid)
			sw = s;
	});
	model.coreSwitches.forEach(function (s) {
		if (s.dpid == dpid)
			sw = s;
	});

	return sw;
}