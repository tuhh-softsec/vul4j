package org.esigate.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The event manager.
 * <p>
 * Listener can register and unregister to specific events.
 * <p>
 * Events can be fired and propagated to listeners.
 * <p>
 * The following events are supported :
 * <p>
 * Proxy events : ESIGate process an incoming request (ESIGate configured as a
 * proxy).
 * <uL>
 * <li>{@link EventManager#EVENT_PROXY_PRE} : before processing an incoming
 * request.</li>
 * <li>{@link EventManager#EVENT_PROXY_POST} : after processing an incoming
 * request.</li>
 * </ul>
 * <p>
 * Fragment events : A fragment is required for inclusion (esi:include). ESIGate
 * will try to use its cache or fallback to an http call to the remote backend.
 * 
 * <ul>
 * <li>{@link EventManager#EVENT_FRAGMENT_PRE} : before retrieving a fragment.</li>
 * <li>{@link EventManager#EVENT_FRAGMENT_POST} : after retrieving a fragment.</li>
 * </ul>
 * <p>
 * Fetch events : An HTTP call is made to a remote backend.
 * <ul>
 * <li>{@link EventManager#EVENT_FETCH_PRE} : before creating the HTTP call.</li>
 * <li>{@link EventManager#EVENT_FETCH_POST} : after we receive the response.</li>
 * </ul>
 * <p>
 * Render events : Renderers are applied to the current page. This event can be
 * used to inject additional renderers.
 * <ul>
 * <li>{@link EventManager#EVENT_RENDER_PRE} : before applying renderers</li>
 * <li>{@link EventManager#EVENT_RENDER_POST} : after applying renderers</li>
 * </ul>
 * <p>
 * Encoding event : response is read using the charset declared by HTTP headers.
 * <ul>
 * <li>{@link EventManager#EVENT_ENCODING} : after reading response using the default encoding</li>
 * </ul>
 * 
 * @author Nicolas Richeton
 */
public class EventManager {

	public static EventDefinition EVENT_FRAGMENT_PRE = new EventDefinition(
			"org.esigate.fragement-pre", EventDefinition.TYPE_DEFAULT);
	public static EventDefinition EVENT_FRAGMENT_POST = new EventDefinition(
			"org.esigate.fragment-post", EventDefinition.TYPE_POST);

	public static EventDefinition EVENT_FETCH_PRE = new EventDefinition(
			"org.esigate.fetch-pre", EventDefinition.TYPE_DEFAULT);
	public static EventDefinition EVENT_FETCH_POST = new EventDefinition(
			"org.esigate.fetch-post", EventDefinition.TYPE_POST);

	public static EventDefinition EVENT_PROXY_PRE = new EventDefinition(
			"org.esigate.proxy-pre", EventDefinition.TYPE_DEFAULT);
	public static EventDefinition EVENT_PROXY_POST = new EventDefinition(
			"org.esigate.proxy-post", EventDefinition.TYPE_POST);

	public static EventDefinition EVENT_RENDER_PRE = new EventDefinition(
			"org.esigate.render-pre", EventDefinition.TYPE_DEFAULT);
	public static EventDefinition EVENT_RENDER_POST = new EventDefinition(
			"org.esigate.render-post", EventDefinition.TYPE_POST);

	public static EventDefinition EVENT_ENCODING = new EventDefinition(
			"org.esigate.encoding.", EventDefinition.TYPE_DEFAULT);
	
	private static final Logger LOG = LoggerFactory
			.getLogger(EventManager.class);

	public EventManager() {
	}

	/**
	 * Listener mappings. This saves times when an event is fired.
	 */
	Map<EventDefinition, List<IEventListener>> listeners = new HashMap<EventDefinition, List<IEventListener>>();
	/**
	 * Post events are stored in reverse order. This allows an extension to
	 * enclose the whole processing.
	 */
	Map<EventDefinition, List<IEventListener>> listenersPost = new HashMap<EventDefinition, List<IEventListener>>();

	private void register(
			Map<EventDefinition, List<IEventListener>> listenerMappings,
			EventDefinition eventDefinition, IEventListener listener,
			boolean reverseOrder) {
		List<IEventListener> eventListeners = listenerMappings
				.get(eventDefinition);

		// Create listener list for this event
		if (eventListeners == null) {
			eventListeners = new ArrayList<IEventListener>();
			listenerMappings.put(eventDefinition, eventListeners);
		}

		// Add listener
		if (reverseOrder) {
			eventListeners.add(eventListeners.size(), listener);
		} else {
			eventListeners.add(listener);
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("Registered {} on event {}",
					listener.getClass().getName(), eventDefinition);
		}
	}

	/**
	 * Start listening to an event.
	 * 
	 * @param eventDefinition
	 * @param listener
	 */
	public void register(EventDefinition eventDefinition,
			IEventListener listener) {
		if (eventDefinition.getType() == EventDefinition.TYPE_POST) {
			register(listenersPost, eventDefinition, listener, true);
		} else {
			register(listeners, eventDefinition, listener, false);
		}
	}

	/**
	 * Fire a new event and run all the listeners.
	 * 
	 * @param eventDefinition
	 * @param eventDetails
	 */
	public void fire(EventDefinition eventDefinition, Event eventDetails) {
		if (eventDefinition.getType() == EventDefinition.TYPE_POST) {
			fire(listenersPost, eventDefinition, eventDetails);
		} else {
			fire(listeners, eventDefinition, eventDetails);
		}
	}

	private void fire(
			Map<EventDefinition, List<IEventListener>> listenerMappings,
			EventDefinition eventDefinition, Event eventDetails) {
		List<IEventListener> eventListeners = listenerMappings
				.get(eventDefinition);

		// No listeners at all for this event
		if (eventListeners == null)
			return;

		// Loop on listeners
		for (IEventListener el : eventListeners) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Running {} on event {}", el, eventDefinition);
			}

			if (el.event(eventDefinition, eventDetails) == false)
				return;
		}

	}

	/**
	 * Stop listening to an event.
	 * 
	 * @param eventDefinition
	 * @param eventListener
	 */
	public void unregister(EventDefinition eventDefinition,
			IEventListener eventListener) {
		if (eventDefinition.getType() == EventDefinition.TYPE_POST) {
			unregister(listenersPost, eventDefinition, eventListener);
		} else {
			unregister(listeners, eventDefinition, eventListener);
		}
	}

	private void unregister(
			Map<EventDefinition, List<IEventListener>> listenerMappings,
			EventDefinition eventDefinition, IEventListener listener) {

		List<IEventListener> eventListeners = listenerMappings
				.get(eventDefinition);

		// Not listeners at all for this event
		if (eventListeners == null) {
			return;
		}

		boolean removed = eventListeners.remove(listener);

		if (LOG.isInfoEnabled() && removed) {
			LOG.info("Unregistered {} on event {}", listener.getClass()
					.getName(), eventDefinition);
		}
	}
}
