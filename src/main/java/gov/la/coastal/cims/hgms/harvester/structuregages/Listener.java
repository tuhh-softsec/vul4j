package gov.la.coastal.cims.hgms.harvester.structuregages;

import org.springframework.stereotype.Component;

/**
 * Listener interface.
 *
 * @author mckelvym
 * @since Mar 1, 2018
 *
 */
@Component
public interface Listener extends Runnable
{
	/**
	 * Start listening for socket connections.
	 *
	 * @author mckelvym
	 * @since Mar 1, 2018
	 */
	@Override
	void run();

	/**
	 * Set a flag to shut down the listener
	 *
	 * @author mckelvym
	 * @since Mar 5, 2018
	 */
	void shutdown();
}
