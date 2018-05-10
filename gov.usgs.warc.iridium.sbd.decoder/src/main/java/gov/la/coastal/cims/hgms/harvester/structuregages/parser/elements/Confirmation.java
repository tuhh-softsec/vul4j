package gov.la.coastal.cims.hgms.harvester.structuregages.parser.elements;

import lombok.Builder;
import lombok.Getter;

/**
 * Confirmation information element.
 *
 * @author darceyj
 * @since Jan 8, 2018
 *
 */
@Builder
@Getter
public class Confirmation
{
	/**
	 *
	 * @return builder for new instances of Confirmation element
	 * @since Jan 8, 2018
	 */
	public static ConfirmationBuilder builder()
	{
		return new ConfirmationBuilder();
	}

	/**
	 * Confirmation information element id
	 *
	 * @since Jan 8, 2018
	 */
	private final byte	id;

	/**
	 * The length of the confirmation element
	 *
	 * @since Jan 8, 2018
	 */
	private final short	length;

	/**
	 * The confirmation status. Can be a 1 for success and 0 for failure.
	 *
	 * @since Jan 8, 2018
	 */
	private final byte	status;
}
