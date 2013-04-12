var enableIPerfLog = false;

function iperfLog(message, flow) {
	if (enableIPerfLog) {
		console.log('flow: ' + flow.flowId + '===>' + message);
	}
}

function hasIPerf(flow) {
	return flow && flow.iperfFetchTimeout;
}

function clearIPerf(flow) {
	iperfLog('clearing iperf interval for: ' + flow.flowId, flow);
	clearTimeout(flow.iperfFetchTimeout);
	delete flow.iperfFetchTimeout;
	clearInterval(flow.iperfDisplayInterval);
	delete flow.iperfDisplayInterval;
	clearTimeout(flow.animationTimeout);
	delete flow.animationTimeout;
	stopFlowAnimation(flow);
	delete flow.iperfData.timestamp;

	delete flow.iperfData;
}

function startIPerfForFlow(flow) {
	var duration = 10000; // seconds
	var interval = 100; // ms. this is defined by the server
	var updateRate = 3000; // ms
	var pointsToDisplay = 1000;

	function makeGraph(iperfData) {
		var d = 'M0,0';

		var now = flow.iperfData.startTime + (Date.now() - flow.iperfData.localNow)/1000;

		if (iperfData.samples && iperfData.samples.length) {

			var lastX;
			var i = iperfData.samples.length - 1;
			while (i) {
				var sample = iperfData.samples[i];

				var x = (1000 - (now - sample.time)*10);
				// workaround for discontinuity in iperf data
				if (x < 0) {
					i -= 1;
					continue;
				}

				var y = 28 * sample.value/1000000;
				if (y > 28) {
					y = 28;
				}
				if (i == iperfData.samples.length - 1) {
					d = 'M' + x + ',30';
				}

				// handle gaps
				// 1.5 for rounding error
				if (lastX && lastX - x > 1.5) {
					d += 'L' + lastX + ',30';
					d += 'M' + x + ',30'
				}
				lastX = x;

				d += 'L' + x + ',' + (30-y);

				i -= 1;
			}
			d += 'L' + lastX + ',30';
		}
		return d;
	}

	if (flow.flowId) {
		iperfLog('starting iperf', flow);
		startIPerf(flow, duration, updateRate/interval);

		flow.iperfDisplayInterval = setInterval(function () {
			if (flow.iperfData) {
				var iperfPath = d3.select(document.getElementById(makeSelectedFlowKey(flow))).select('path');
				flow.iperfData.samples.sort(function (a, b) {
					return a.time - b.time;
				});
				iperfPath.attr('d', makeGraph(flow.iperfData));
			}


		}, interval);

		var animationTimeout;
		flow.iperfData = {
			samples: []
		}

		function resetFlowAnimationTimeout() {
			clearTimeout(flow.animationTimeout);
			// kill the animation if iperfdata stops flowing
			flow.animationTimeout = setTimeout(function () {
				stopFlowAnimation(flow);
			}, updateRate*1.5);
		}

		var lastTime;
		function fetchData() {
			iperfLog('Requesting iperf data', flow);
			var fetchTime = Date.now();
			getIPerfData(flow, function (data) {
				var requestTime = Date.now() - fetchTime;
				var requestTimeMessage = 'iperf request completed in: ' + requestTime + 'ms';
				if (requestTime > 1000) {
					requestTimeMessage = requestTimeMessage.toUpperCase();
				}
				iperfLog(requestTimeMessage, flow);

				if (!flow.iperfData) {
					iperfLog('iperf session closed', flow);
					return;
				}

				try {
					var iperfData = JSON.parse(data);

//				iperfLog(iperfData.timestamp, flow);

					// if the data is fresh
					if (!(flow.iperfData.timestamp && iperfData.timestamp != flow.iperfData.timestamp)) {
						if (!flow.iperfData.timestamp) {
							iperfLog('received first iperf buffer', flow);
						} else {
							iperfLog('received duplicate iperf buffer with timestamp: ' + iperfData.timestamp, flow);
						}
					} else {
						iperfLog('received new iperf buffer with timstamp: ' + iperfData.timestamp, flow);
						startFlowAnimation(flow);
						resetFlowAnimationTimeout();

						var endTime = Math.floor(iperfData['end-time']*10)/10;

						var startTime = endTime - (iperfData.samples.length * interval/1000);
						// set now on the first buffer
						if (!flow.iperfData.startTime) {
							flow.iperfData.startTime = startTime;
							flow.iperfData.localNow = Date.now();
						}

						iperfLog('iperf buffer start time: ' + startTime, flow);
						if (lastTime && (startTime - lastTime) > updateRate/1000) {
							iperfLog('iperf buffer gap: ' + (startTime - lastTime), flow);
						}
						lastTime = startTime;

						// clear out the old data
						while (flow.iperfData.samples.length > pointsToDisplay + iperfData.samples.length) {
							flow.iperfData.samples.shift();
						}

						// if the client gets too out of sync, resynchronize
						var clientNow = flow.iperfData.startTime + (Date.now() - flow.iperfData.localNow)/1000;
						if (Math.abs(clientNow - startTime) > (updateRate/1000) * 2) {
							iperfLog('resynchronizing now: ' + clientNow + ' => ' + startTime, flow);
							flow.iperfData.startTime = startTime;
							flow.iperfData.localNow = Date.now();
						}

						var time = startTime;
						iperfData.samples.forEach(function (s) {
							var sample = {
								time: time,
								value: s
							};
							flow.iperfData.samples.push(sample);
							time += interval/1000;
						});
					}
					flow.iperfData.timestamp = iperfData.timestamp;
				} catch (e) {
					iperfLog('bad iperf data: ' + data, flow);
				}
				flow.iperfFetchTimeout = setTimeout(fetchData, updateRate*.25); // over sample to avoid gaps
//				iperfLog(data, flow);
			});
		}

		// wait a buffer to make sure the old iperf session gets cleared out
		setTimeout(fetchData, updateRate);
	}
}