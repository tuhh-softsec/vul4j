function appInit(cb) {

	// populates selected flows with empty rows
	updateSelectedFlows();

	d3.select('#showFlowChooser').on('click', function () {
		showFlowChooser();
	});

	d3.select('#action-all').on('click', function () {
		var prompt = "Add backup controllers?"
		doConfirm(prompt, function (result) {
			if (result) {
				switchAll();
			}
		});
	});

	d3.select('#action-local').on('click', function () {
		var prompt = "Switch controllers to local?"
		doConfirm(prompt, function (result) {
			if (result) {
				switchLocal();
			}
		});
	});

	d3.select('#action-scale').on('click', function () {
		var prompt = "Scale network?"
		doConfirm(prompt, function (result) {
			if (result) {
				scaleNetwork();
			}
		});
	});

	d3.select('#action-reset').on('click', function () {
		var prompt = "Reset network?"
		doConfirm(prompt, function (result) {
			if (result) {
				resetNetwork();
			}
		});
	});

	d3.select('#action-kill').on('click', function () {
		var prompt = "Kill ONOS instance?";
		var options = model.activeControllers;
		doConfirm(prompt, function (result) {
			controllerDown(result);
		}, options);
	});

	createTopologyView(cb);
}
