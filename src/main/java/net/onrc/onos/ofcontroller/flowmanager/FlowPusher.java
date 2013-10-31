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
	private Thread thread;
	
	/**
	 * Represents state of queue.
	 * This is used for calculation of rate.
	 * @author Naoki Shiota
	 *
	 */
	private static class QueueState {
		long last_sent_time = 0;
		long last_sent_size = 0;
		long max_rate;
	}

	private Map<IOFSwitch, Queue<OFMessage> > queues =
			new HashMap<IOFSwitch, Queue<OFMessage> >();
	private Map<Queue<OFMessage>, QueueState> queue_states =
			new HashMap<Queue<OFMessage>, QueueState>();

	private class FlowPusherProcess implements Runnable {
		@Override
		public void run() {
			while (true) {
				for (Map.Entry<IOFSwitch, Queue<OFMessage> > entry : queues.entrySet()) {
					// pick one FlowEntry event from a queue
					IOFSwitch sw = entry.getKey();
					Queue<OFMessage> queue = entry.getValue();
					if (entry == null || queue == null) {
						continue;
					}
					
					OFMessage msg = queue.poll();
					if (msg == null) {
						continue;
					}
					
					QueueState state = queue_states.get(queue);
					if (state == null) {
						continue;
					}
					
					// check sending rate and determine it to be sent or not
					long current_time = System.nanoTime();
					long rate = state.last_sent_size / (current_time - state.last_sent_time);
					
					// if need to send, call IOFSwitch#write()
					if (rate < state.max_rate) {
						try {
							sw.write(msg, context);
							state.last_sent_time = current_time;
							state.last_sent_size = msg.getLengthU();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
				// sleep while all queues are empty
				boolean sleep = true;
				do {
					// TODO check if queues are empty
				} while (sleep);
			}
		}
	}
	
	public FlowPusher(FloodlightContext context) {
		this.context = context;
	}
	
	public void assignQueue(IOFSwitch sw, Queue<OFMessage> queue, long max_rate) {
		queues.put(sw, queue);
		QueueState state = new QueueState();
		state.max_rate = max_rate;
		queue_states.put(queue, state);
	}

	public void startProcess() {
		thread = new Thread(new FlowPusherProcess());
		thread.start();
	}
	
	public void stopProcess() {
		// TODO tell thread to halt
	}
}
