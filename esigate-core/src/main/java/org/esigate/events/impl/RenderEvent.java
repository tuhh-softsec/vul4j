package org.esigate.events.impl;

import java.util.List;

import org.esigate.Renderer;
import org.esigate.api.HttpRequest;
import org.esigate.events.Event;

/**
 * Render event : when renderers are applied on the page.
 * 
 * @author Nicolas Richeton
 * 
 */
public class RenderEvent extends Event {

	public List<Renderer> renderers;
	public String remoteUrl;
	public HttpRequest originalRequest;
}
