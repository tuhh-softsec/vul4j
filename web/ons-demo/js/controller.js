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
	l: function (cmd, link) {
		var url = '/proxy/gui/link/' + [cmd, link['src-switch'], link['src-port'], link['dst-switch'], link['dst-port']].join('/');
		callURL(url);

	},
	s: function (cmd, s) {
		var url = '/proxy/gui/switch/' + [cmd, s.dpid].join('/');
		callURL(url);
	},
	c: function (cmd, c) {
		var url = '/proxy/gui/controller/' + [cmd, c].join('/');
		callURL(url);
	}
};


// if (parseURLParameters().mock) {
// 	urls = mockURLs;
// }


function linkUp(link) {
	controllerFunctions.l('up', link);
}

function linkDown(link) {
	controllerFunctions.l('down', link);
}

function switchUp(s) {
	controllerFunctions.s('up', s);
}

function switchDown(s) {
	controllerFunctions.s('down', s);
}

function controllerUp(c) {
	controllerFunctions.c('up', c);
}

function controllerDown(c) {
	controllerFunctions.c('down', c);
}

function createFlow(src, dst) {

}

function deleteFlow(src, dst) {

}