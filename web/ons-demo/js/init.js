function appInit(cb) {

	// populates selected flows with empty rows
	updateSelectedFlows();

	d3.select('#showFlowChooser').on('click', function () {
		showFlowChooser();
	});

	d3.select('#action-all').on('click', function () {
		var prompt = "Switch controllers to all?"
		if (confirm(prompt)) {
			switchAll();
		}
	});

	d3.select('#action-local').on('click', function () {
		var prompt = "Switch controllers to local?"
		if (confirm(prompt)) {
			switchLocal();
		}
	});

	d3.select('#action-scale').on('click', function () {
		alert('scale')
	});

	createTopologyView(cb);
}
