package gov.la.coastal.cims.hgms.harvester.structuregages.directip;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Processor to unpack bytes corresponding to MO DirectIP Header and Payload
 * data.
 *
 * @author mckelvym
 * @since Jan 5, 2018
 *
 */
public interface SbdProcessor
{
	/**
	 * @param p_Bytes
	 *            bytes corresponding to MO DirectIP Header IE
	 * @param p_ExceptionConsumer
	 *            A {@link Consumer} that wishes to be notified in the event the
	 *            return value is Optional.empty
	 * @author mckelvym
	 * @return an {@link IridiumResponse}
	 * @since Jan 5, 2018
	 */
	Optional<IridiumResponse> process(byte[] p_Bytes,
			Consumer<Throwable> p_ExceptionConsumer);
}
