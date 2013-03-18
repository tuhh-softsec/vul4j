function sync() {
	updateModel(function (model) {
		d3.select('#lastUpdate').text(model.timestamp);
		setTimeout(sync, 1000);
	});
}
sync();