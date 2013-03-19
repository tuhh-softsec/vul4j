/*global async, d3*/

function toD3(results) {
	var model = {
		edgeSwitches: [],
		aggregationSwitches: [],
		coreSwitches: [],
		flows: results.flows,
		controllers: results.controllers,
		links: results.links
	}


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
		s.controller = results.mapping[s.dpid][0].controllerId

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

function updateModel(cb) {
	async.parallel({
	    links: function(cb) {
			d3.json('data/wm_core_topology_links_json.json', function (error, result) {
				cb(error, result);
			});
	    },
	    switches: function(cb) {
			d3.json('data/wm_core_topology_switches_all_json.json', function (error, result) {
				cb(error, result);
			});
	    },
	    flows: function(cb) {
			d3.json('data/wm_flow_getall_json.json', function (error, result) {
				cb(error, result);
			});
	    },
	    controllers: function(cb) {
			d3.json('data/wm_registry_controllers_json.json', function (error, result) {
				cb(error, result);
			});
	    },
	    mapping: function(cb) {
			d3.json('data/wm_registry_switches_json.json', function (error, result) {
				cb(error, result);
			});
	    },
	    configuration: function(cb) {
			d3.json('data/configuration.json', function (error, result) {
				cb(error, result);
			});
	    },
	},
	function(err, results) {
		var model = toD3(results);
		model.timestamp = new Date();
		cb(model);
	});
}