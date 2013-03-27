var controllerFunctions = {
	link: function (cmd, src, dst) {
		var url = '/proxy/gui/link/' + [cmd, src.dpid, 1, dst.dpid, 1].join('/');
		d3.json(url, function (error, result) {		
			if (error) {
				alert(url + ' : ' + error.status);
			}
		});
	}
}


// if (parseURLParameters().mock) {
// 	urls = mockURLs;
// }


function linkUp(src, dst) {
	controllerFunctions.link('up', src, dst);
}

function linkDown(src, dst) {
	controllerFunctions.link('down', src, dst);
}

function createFlow(src, dst) {

}

function deleteFlow(src, dst) {

}