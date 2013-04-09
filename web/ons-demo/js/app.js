/*global d3, document∆*/


function sync() {
	var d = Date.now();

	updateModel(function (newModel) {
//		console.log('Update time: ' + (Date.now() - d)/1000 + 's');

		if (newModel) {
			var modelChanged = false;
			var newModelString = JSON.stringify(newModel);
			if (!modelString || newModelString != modelString) {
				modelChanged = true;
				model = newModel;
				modelString = newModelString;
			} else {
	//			console.log('no change');
			}

			if (modelChanged) {
				updateControllers();
				updateSelectedFlows();
				reconcilePendingLinks(model);
				updateLinkMap(links);
				updateTopology();
			}

			updateHeader(newModel);

			d3.select('#contents').style('visibility', 'visible');
		}

		// do it again in 1s
		setTimeout(function () {
			sync()
		}, 1000);
	});
}

appInit(function () {
	// workaround for Chrome v25 bug
	// if executed immediately, the view box transform logic doesn't work properly
	// fixed in Chrome v27
	setTimeout(function () {

		// workaround for another Chrome v25 bug
		// viewbox transform stuff doesn't work in combination with browser zoom
		// also works in Chrome v27
		d3.select('#svg-container').style('zoom',  window.document.body.clientWidth/window.document.width);

		sync();
	}, 100);
});


