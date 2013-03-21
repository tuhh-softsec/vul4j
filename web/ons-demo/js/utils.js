function parseURLParameters() {
	var parameters = {};

	var search = location.href.split('?')[1];
	if (search) {
		search.split('&').forEach(function (param) {
			var key = param.split('=')[0];
			var value = param.split('=')[1];
			parameters[key] = decodeURIComponent(value);
		});
	}

	return parameters;
}