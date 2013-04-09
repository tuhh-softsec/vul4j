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