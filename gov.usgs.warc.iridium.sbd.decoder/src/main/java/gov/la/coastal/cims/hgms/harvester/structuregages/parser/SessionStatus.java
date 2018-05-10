package gov.la.coastal.cims.hgms.harvester.structuregages.parser;

import org.eclipse.jdt.annotation.NonNull;

/**
 * SBD transfer session status codes
 *
 * @author darceyj
 * @since Jan 8, 2018
 *
 */
public enum SessionStatus
{
	/**
	 * Link loss status
	 *
	 * @author darceyj
	 * @since Jan 8, 2018
	 *
	 */
	LINK_LOSS
	{
		@Override
		public String getErrorMessage()
		{
			return "The RF link was lost during the transfer.";
		}
	},
	/**
	 * Prohibited status
	 *
	 * @author darceyj
	 * @since Jan 8, 2018
	 *
	 */
	PROHIBITED
	{
		@Override
		public String getErrorMessage()
		{
			return "The message was rejected because it is prohibited from accessing the iridium gateway.";
		}
	},
	/**
	 * Protocol anomoly status
	 *
	 * @author darceyj
	 * @since Jan 8, 2018
	 *
	 */
	PROTOCOL_ANOMOLY
	{
		@Override
		public String getErrorMessage()
		{
			return "The message was rejected due to a protocol anomoly.";
		}
	},
	/**
	 * Success status
	 *
	 * @author darceyj
	 * @since Jan 8, 2018
	 *
	 */
	SUCCESS
	{
		@Override
		public String getErrorMessage()
		{
			return "Session transfer success.";
		}
	},
	/**
	 * Time out status
	 *
	 * @author darceyj
	 * @since Jan 8, 2018
	 *
	 */
	TIMED_OUT
	{
		@Override
		public String getErrorMessage()
		{
			return "Session timed out before message was transferred";
		}
	},
	/**
	 * too large session status
	 *
	 * @author darceyj
	 * @since Jan 8, 2018
	 *
	 */
	TOO_LARGE
	{
		@Override
		public String getErrorMessage()
		{
			return "Message that was trying to transfer is too large to complete in one transfer.";
		}
	},
	/**
	 * unacceptable quality status
	 *
	 * @author darceyj
	 * @since Jan 8, 2018
	 *
	 */
	UNACCEPTABLE_QUALITY
	{
		@Override
		public String getErrorMessage()
		{
			return "Message is rejected because it is of an unacceptable quality.";
		}
	},
	/**
	 * Unknown status
	 *
	 * @author darceyj
	 * @since Jan 8, 2018
	 *
	 */
	UNKNOWN
	{
		@Override
		public String getErrorMessage()
		{
			return "Unknown status code encountered.";
		}
	};

	/**
	 * Take in the status from the header and return the proper status as
	 * {@link SessionStatus}
	 *
	 * @param p_IncomingStatus
	 *            the status as an int from the header
	 * @return the {@link SessionStatus}
	 * @since Jan 8, 2018
	 */
	public @NonNull static SessionStatus getStatus(final int p_IncomingStatus)
	{
		switch (p_IncomingStatus)
		{
			case 0:
				return SessionStatus.SUCCESS;
			case 1:
				return SessionStatus.TOO_LARGE;
			case 2:
				return SessionStatus.UNACCEPTABLE_QUALITY;
			case 10:
				return SessionStatus.TIMED_OUT;
			case 12:
				return SessionStatus.TOO_LARGE;
			case 13:
				return SessionStatus.LINK_LOSS;
			case 14:
				return SessionStatus.PROTOCOL_ANOMOLY;
			case 15:
				return SessionStatus.PROHIBITED;
			default:
				return SessionStatus.UNKNOWN;
		}
	}

	/**
	 * @return the error message to show with the given status.
	 * @since Jan 8, 2018
	 */
	public abstract String getErrorMessage();

}
