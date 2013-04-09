function appInit(cb) {

	// populates selected flows with empty rows
	updateSelectedFlows();

	d3.select('#showFlowChooser').on('click', function () {
		showFlowChooser();
	});

	createTopologyView(cb);
}
