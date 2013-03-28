/*global d3*/

function callURL(url) {
	d3.text(url, function (error, result) {
		if (error) {
			alert(url + ' : ' + error.status);
		} else {
			console.log(result);
		}
	});
}


var controllerFunctions = {
	linkCmd: function (cmd, link) {
		var url = '/proxy/gui/link/' + [cmd, link['src-switch'], link['src-port'], link['dst-switch'], link['dst-port']].join('/');
		callURL(url);

	},
	switchCmd: function (cmd, s) {
		var url = '/proxy/gui/switch/' + [cmd, s.dpid].join('/');
		callURL(url);
	},
	ctrlCmd: function (cmd, c) {
		var url = '/proxy/gui/controller/' + [cmd, c].join('/');
		callURL(url);
	},
	addFlowCmd: function (src, dst) {
		alert('add flow')
	},
	delFlowCmd: function (flow) {
		alert('delete flow')
	}
};

// http://gui3.onlab.us:8081/gui/addflow/<src_dpid>/<src_port>/<dst_dpid>/<dst_port>/<srcMAC>/<dstMAC>
// http://gui3.onlab.us:8081/gui/delflow/<flow_id>

function linkUp(link) {
	controllerFunctions.linkCmd('up', link);
}

function linkDown(link) {
	controllerFunctions.linkCmd('down', link);
}

function switchUp(s) {
	controllerFunctions.switchCmd('up', s);
}

function switchDown(s) {
	controllerFunctions.switchCmd('down', s);
}

function controllerUp(c) {
	controllerFunctions.ctrlCmd('up', c);
}

function controllerDown(c) {
	controllerFunctions.ctrlCmd('down', c);
}

function addFlow(src, dst) {
	controllerFunctions.addFlowCmd(src, dst);
}

function deleteFlow(flow) {
	controllerFunctions.delFlowCmd(flow);
}