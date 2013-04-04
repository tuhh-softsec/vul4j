/*global async, d3*/

function toD3(results) {
	var model = {
		edgeSwitches: [],
		aggregationSwitches: [],
		coreSwitches: [],
		flows: results.flows,
		controllers: results.controllers,
		activeControllers: results.activeControllers,
		links: results.links,
		configuration: results.configuration
	};

	// sort the switches
	results.switches.sort(function (a, b) {
		var aA = a.dpid.split(':');
		var bB = b.dpid.split(':');
		for (var i=0; i<aA.length; i+=1) {
			if (aA[i] != bB[i]) {
				return parseInt(aA[i], 16) - parseInt(bB[i], 16);
			}
		}
		return 0;
	});

	// identify switch types
	var coreSwitchDPIDs = {};
	results.configuration.core.forEach(function (dpid) {
		coreSwitchDPIDs[dpid] = true;
	});

	var aggregationSwitchDPIDs = {};
	results.configuration.aggregation.forEach(function (dpid) {
		aggregationSwitchDPIDs[dpid] = true;
	});

	results.switches.forEach(function (s) {
		var mapping = results.mapping[s.dpid]
		if (mapping) {
			s.controller = mapping[0].controllerId;
		}

		if (coreSwitchDPIDs[s.dpid]) {
			model.coreSwitches.push(s);
		} else if (aggregationSwitchDPIDs[s.dpid]) {
			model.aggregationSwitches.push(s);
		} else {
			model.edgeSwitches.push(s);
		}
	});

	return model;
}

var urls = {
	links: '/wm/core/topology/links/json',
	switches: '/wm/core/topology/switches/all/json',
	flows: '/wm/flow/getsummary/0/0/json?proxy',
	activeControllers: '/wm/registry/controllers/json',
	controllers: 'data/controllers.json',
	mapping: '/wm/registry/switches/json',
	configuration: 'data/configuration.json'
}

var mockURLs = {
	links: 'data/wm_core_topology_links_json.json',
	switches: 'data/wm_core_topology_switches_all_json.json',
	flows: 'data/wm_flow_getall_json.json',
	activeControllers: 'data/wm_registry_controllers_json.json',
	controllers: 'data/controllers.json',
	mapping: 'data/wm_registry_switches_json.json',
	configuration: 'data/configuration.json'
}

var proxyURLs = {
	links: '/wm/core/topology/links/json?proxy',
	switches: '/wm/core/topology/switches/all/json?proxy',
	flows: '/wm/flow/getsummary/0/0/json?proxy',
	activeControllers: '/wm/registry/controllers/json?proxy',
	controllers: 'data/controllers.json',
	mapping: '/wm/registry/switches/json?proxy',
	configuration: 'data/configuration.json'
}

var params = parseURLParameters();
if (params.mock) {
	urls = mockURLs;
}
if (params.proxy) {
	urls = proxyURLs;
}

function makeRequest(key) {
	var url = urls[key];
	if (url) {
		return function (cb) {
			d3.json(url, function (error, result) {
				if (error) {
					error = url + ' : ' + error.status;
				}

				cb(error, result);
			});
		}
	} else {
		return function (cb) {
			cb(null, []);
		}
	}
}


function updateModel(cb) {
	async.parallel({
	    links: makeRequest('links'),
	    switches: makeRequest('switches'),
	    controllers: makeRequest('controllers'),
	    activeControllers: makeRequest('activeControllers'),
	    mapping: makeRequest('mapping'),
	    configuration: makeRequest('configuration'),
	    flows: makeRequest('flows')
	},
	function(err, results) {
		if (!err) {
			var model = toD3(results);
			cb(model);
		} else {
			console.log(JSON.stringify(err));
			cb(null);
		}
	});
}
