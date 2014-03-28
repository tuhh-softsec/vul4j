/***************************************************************************************************
timeout used by controller functions. after the timeout expires the "pending" action
is removed and the topology view is whatever is reported by the API
***************************************************************************************************/
/* var pendingTimeout = 30000; */
var pendingTimeout = 60000;

/***************************************************************************************************
CSS names for the pallette of colors used by the topology view
***************************************************************************************************/
var colors = [
	'color1',
	'color2',
	'color3',
	'color4',
	'color7',
	'color8',
	'color9',
//	'color11',
	'color12'
];
colors.reverse();

/***************************************************************************************************
Widths of each switch type
***************************************************************************************************/
var widths = {
	edge: 6,
	aggregation: 16,
	core: 20
}
