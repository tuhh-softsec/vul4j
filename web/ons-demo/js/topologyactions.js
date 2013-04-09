function mouseOverSwitch(data) {

	d3.event.preventDefault();

	d3.select(document.getElementById(data.dpid + '-label')).classed('nolabel', false);

	if (data.highlighted) {
		return;
	}

	// only highlight valid link or flow destination by checking for class of existing highlighted circle
	var highlighted = svg.selectAll('.highlight')[0];
	if (highlighted.length == 1) {
		var s = d3.select(highlighted[0]).select('circle');
		// only allow links
		// 	edge->edge (flow)
		//  aggregation->core
		//	core->core
		if (data.className == 'edge' && !s.classed('edge') ||
			data.className == 'core' && !s.classed('core') && !s.classed('aggregation') ||
			data.className == 'aggregation' && !s.classed('core')) {
			return;
		}

		// the second highlighted switch is the target for a link or flow
		data.target = true;
	}

	var node = d3.select(document.getElementById(data.dpid));
	node.classed('highlight', true).select('circle').transition().duration(100).attr("r", widths.core);
	data.highlighted = true;
	node.moveToFront();
}

function mouseOutSwitch(data) {
	d3.select(document.getElementById(data.dpid + '-label')).classed('nolabel', true);

	if (data.mouseDown)
		return;

	var node = d3.select(document.getElementById(data.dpid));
	node.classed('highlight', false).select('circle').transition().duration(100).attr("r", widths[data.className]);
	data.highlighted = false;
	data.target = false;
}

function mouseDownSwitch(data) {
	mouseOverSwitch(data);
	data.mouseDown = true;
	d3.select('#topology').classed('linking', true);

	d3.select('svg')
		.append('svg:path')
		.attr('id', 'linkVector')
		.attr('d', function () {
			var s = d3.select(document.getElementById(data.dpid));

			var pt = document.querySelector('svg').createSVGPoint();
			pt.x = s.attr('x');
			pt.y = s.attr('y');
			pt = pt.matrixTransform(s[0][0].getCTM());

			return line([pt, pt]);
		});


	if (data.className === 'core') {
		d3.selectAll('.edge').classed('nodrop', true);
	}
	if (data.className === 'edge') {
		d3.selectAll('.core').classed('nodrop', true);
		d3.selectAll('.aggregation').classed('nodrop', true);
	}
	if (data.className === 'aggregation') {
		d3.selectAll('.edge').classed('nodrop', true);
		d3.selectAll('.aggregation').classed('nodrop', true);
	}
}

function mouseUpSwitch(data) {
	if (data.mouseDown) {
		data.mouseDown = false;
		d3.select('#topology').classed('linking', false);
		d3.event.stopPropagation();
		d3.selectAll('.nodrop').classed('nodrop', false);
	}
}

function doubleClickSwitch(data) {
	var circle = d3.select(document.getElementById(data.dpid)).select('circle');
	if (data.state == 'ACTIVE') {
		var prompt = 'Deactivate ' + data.dpid + '?';
		if (confirm(prompt)) {
			switchDown(data);
			setPending(circle);
		}
	} else {
		var prompt = 'Activate ' + data.dpid + '?';
		if (confirm(prompt)) {
			switchUp(data);
			setPending(circle);
		}
	}
}

d3.select(document.body).on('mouseup', function () {
	function clearHighlight() {
		svg.selectAll('circle').each(function (data) {
			data.mouseDown = false;
			d3.select('#topology').classed('linking', false);
			mouseOutSwitch(data);
		});
		d3.select('#linkVector').remove();
	};

	d3.selectAll('.nodrop').classed('nodrop', false);

	function removeLink(link) {
		var path1 = document.getElementById(link['src-switch'] + '=>' + link['dst-switch']);
		var path2 = document.getElementById(link['dst-switch'] + '=>' + link['src-switch']);

		if (path1) {
			setPending(d3.select(path1));
		}
		if (path2) {
			setPending(d3.select(path2));
		}

		linkDown(link);
	}


	var highlighted = svg.selectAll('.highlight')[0];
	if (highlighted.length == 2) {
		var s1Data = highlighted[0].__data__;
		var s2Data = highlighted[1].__data__;

		var srcData, dstData;
		if (s1Data.target) {
			dstData = s1Data;
			srcData = s2Data;
		} else {
			dstData = s2Data;
			srcData = s1Data;
		}

		if (s1Data.className == 'edge' && s2Data.className == 'edge') {
			var prompt = 'Create flow from ' + srcData.dpid + ' to ' + dstData.dpid + '?';
			if (confirm(prompt)) {
				addFlow(srcData, dstData);

				var flow = {
					dataPath: {
						srcPort: {
							dpid: {
								value: srcData.dpid
							}
						},
						dstPort: {
							dpid: {
								value: dstData.dpid
							}
						}
					},
				        srcDpid: srcData.dpid,
				        dstDpid: dstData.dpid,
					createPending: true
				};

				selectFlow(flow);

				setTimeout(function () {
					deselectFlowIfCreatePending(flow);
				}, pendingTimeout);
			}
		} else {
			var map = linkMap[srcData.dpid];
			if (map && map[dstData.dpid]) {
				var prompt = 'Remove link between ' + srcData.dpid + ' and ' + dstData.dpid + '?';
				if (confirm(prompt)) {
					removeLink(map[dstData.dpid]);
				}
			} else {
				map = linkMap[dstData.dpid];
				if (map && map[srcData.dpid]) {
					var prompt = 'Remove link between ' + dstData.dpid + ' and ' + srcData.dpid + '?';
					if (confirm(prompt)) {
						removeLink(map[srcData.dpid]);
					}
				} else {
					var prompt = 'Create link between ' + srcData.dpid + ' and ' + dstData.dpid + '?';
					if (confirm(prompt)) {
						var link1 = {
							'src-switch': srcData.dpid,
							'src-port': 1,
							'dst-switch': dstData.dpid,
							'dst-port': 1,
							pending: true
						};
						pendingLinks[makeLinkKey(link1)] = link1;
						var link2 = {
							'src-switch': dstData.dpid,
							'src-port': 1,
							'dst-switch': srcData.dpid,
							'dst-port': 1,
							pending: true
						};
						pendingLinks[makeLinkKey(link2)] = link2;
						updateTopology();

						linkUp(link1);

						// remove the pending links after 10s
						setTimeout(function () {
							delete pendingLinks[makeLinkKey(link1)];
							delete pendingLinks[makeLinkKey(link2)];

							updateTopology();
						}, pendingTimeout);
					}
				}
			}
		}

		clearHighlight();
	} else {
		clearHighlight();
	}
});