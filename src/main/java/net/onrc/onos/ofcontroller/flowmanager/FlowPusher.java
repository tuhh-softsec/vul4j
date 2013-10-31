package net.onrc.onos.ofcontroller.flowmanager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.openflow.protocol.OFMessage;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IOFSwitch;

/**
 * FlowPusher intermediates flow_mod sent from FlowManager/FlowSync to switches.
 * FlowPusher controls the rate of sending flow_mods so that connection doesn't overflow.
 * @author Naoki Shiota
 *
 */
public class FlowPusher {
	private FloodlightContext context;
	private FlowQueueTable flowQueueTable = null;
	private Thread thread;
	
	/**
	 * Represents state of queue.
	 * This is used for calculation of rate.
	 * @author Naoki Shiota
	 *
	 */
	private static class RateInfo {
		long last_sent_time = 0;
		long last_sent_size = 0;
	}

	private Map<Long, RateInfo> queue_rateinfos =
			new HashMap<Long, RateInfo>();

	private class FlowPusherProcess implements Runnable {
		@Override
		public void run() {
			if (flowQueueTable == null) {
				return;
			}
			
			while (true) {
				for (IOFSwitch sw : flowQueueTable.getSwitches()) {
					// Skip if queue is suspended
					if (flowQueueTable.isQueueSusupended(sw)) {
						continue;
					}
					
					// Skip if queue is locked
					if (! flowQueueTable.lockQueueIfAvailable(sw)) {
						continue;
					}
					
					long dpid = sw.getId();
					Queue<OFMessage> queue = flowQueueTable.getQueue(sw);
					
					if (queue == null) {
						flowQueueTable.unlockQueue(sw);
						continue;
					}
					
					OFMessage msg = queue.poll();
					if (msg == null) {
						flowQueueTable.unlockQueue(sw);
						continue;
					}
					
					RateInfo state = queue_rateinfos.get(dpid);
					if (state == null) {
						queue_rateinfos.put(dpid, new RateInfo());
					}
					
					// check sending rate and determine it to be sent or not
					long current_time = System.nanoTime();
					long rate = state.last_sent_size / (current_time - state.last_sent_time);
					
					// if need to send, call IOFSwitch#write()
					if (rate < flowQueueTable.getQueueRate(sw)) {
						try {
							sw.write(msg, context);
							state.last_sent_time = current_time;
							state.last_sent_size = msg.getLengthU();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					flowQueueTable.unlockQueue(sw);
				}
				
				// sleep while all queues are empty
				boolean sleep = true;
				do {
					// TODO check if queues are empty
				} while (sleep);
			}
		}
	}
	
	public FlowPusher(FlowQueueTable table, FloodlightContext context) {
		flowQueueTable = table;
		this.context = context;
	}
	
	public void startProcess() {
		thread = new Thread(new FlowPusherProcess());
		thread.start();
	}
	
	public void stopProcess() {
		if (thread != null && thread.isAlive()) {
			// TODO tell thread to halt
		}
	}
	
}
