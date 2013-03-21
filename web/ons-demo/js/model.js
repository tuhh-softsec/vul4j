/*global async, d3*/

function toD3(results) {
	var model = {
		edgeSwitches: [],
		aggregationSwitches: [],
		coreSwitches: [],
		flows: [],
		controllers: results.controllers,
		activeControllers: results.activeControllers,
		links: results.links
	}

	// sort the switches
	results.switches.sort(function (a, b) {
		var aA = a.dpid.split(':');
		var bB = b.dpid.split(':');
		for (var i=0; i<aA.length; i+=1) {
			if (aA[i] != bB[i]) {
				return aA[i] - bB[i];
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
		s.controller = results.mapping[s.dpid][0].controllerId;

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
	flows: '/wm/flow/getall/json',
	activeControllers: '/wm/registry/controllers/json',
	controllers: '/data/controllers.json',
	mapping: '/wm/registry/switches/json',
	configuration: 'data/configuration.json'
}

var mockURLs = {
	links: 'data/wm_core_topology_links_json.json',
	switches: 'data/wm_core_topology_switches_all_json.json',
	flows: 'data/wm_flow_getall_json.json',
	activeControllers: 'data/wm_registry_controllers_json.json',
	controllers: '/data/controllers.json',
	mapping: 'data/wm_registry_switches_json.json',
	configuration: 'data/configuration.json'
}

var proxyURLs = {
	links: '/proxy/wm/core/topology/links/json',
	switches: '/proxy/wm/core/topology/switches/all/json',
	flows: '/proxy/wm/flow/getall/json',
	activeControllers: '/proxy/wm/registry/controllers/json',
	controllers: 'data/controllers.json',
	mapping: '/proxy/wm/registry/switches/json',
	configuration: 'data/configuration.json'
}

var params = parseURLParameters();
if (params.mock) {
	urls = mockURLs;
}
if (params.proxy) {
	urls = proxyURLs;
}

function makeRequest(url) {
	return function (cb) {
		d3.json(url, function (error, result) {
			if (error) {
				error = url + ' : ' + error.status;
			}

			cb(error, result);
		});
	}
}


function updateModel(cb) {
	async.parallel({
	    links: makeRequest(urls.links),
	    switches: makeRequest(urls.switches),
	    controllers: makeRequest(urls.controllers),
	    activeControllers: makeRequest(urls.activeControllers),
	    mapping: makeRequest(urls.mapping),
	    configuration: makeRequest(urls.configuration)
//	    flows: makeRequest(urls.flows),
	},
	function(err, results) {
		if (!err) {
			var model = toD3(results);
			cb(model);
		} else {
			alert(JSON.stringify(err));
		}
	});
}