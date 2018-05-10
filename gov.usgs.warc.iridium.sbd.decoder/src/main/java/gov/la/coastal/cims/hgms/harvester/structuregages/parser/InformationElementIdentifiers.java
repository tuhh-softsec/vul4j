package gov.la.coastal.cims.hgms.harvester.structuregages.parser;

import java.util.Arrays;
import java.util.Optional;

/**
 * Information element Identifiers to determine the type of element
 *
 * @author darceyj
 * @since Jan 5, 2018
 *
 */
public enum InformationElementIdentifiers
{
	/**
	 * The confimation id
	 *
	 * @author darceyj
	 * @since Jan 5, 2018
	 *
	 */
	CONFIRMATION
	{
		@Override
		public Byte getByteId()
		{
			return Byte.valueOf((byte) 0x05);
		}
	},
	/**
	 * Header id
	 *
	 * @author darceyj
	 * @since Jan 5, 2018
	 *
	 */
	HEADER
	{
		@Override
		public Byte getByteId()
		{
			return Byte.valueOf((byte) 0x01);
		}
	},
	/**
	 * Location information id
	 *
	 * @author darceyj
	 * @since Jan 5, 2018
	 *
	 */
	LOCATION_INFORMATION
	{
		@Override
		public Byte getByteId()
		{
			return Byte.valueOf((byte) 0x03);
		}
	},
	/**
	 * Payload id
	 *
	 * @author darceyj
	 * @since Jan 5, 2018
	 *
	 */
	PAYLOAD
	{
		@Override
		public Byte getByteId()
		{
			return Byte.valueOf((byte) 0x02);
		}
	},;

	/**
	 * Return the corresponding the {@link InformationElementIdentifiers} as an
	 * {@link Optional}
	 *
	 * @param p_Byte
	 *            the byte to use.
	 * @return an {@link InformationElementIdentifiers} wrapped in an optional
	 *         since the byte could not match an id.
	 * @since Jan 11, 2018
	 */
	public static Optional<InformationElementIdentifiers> getFromByte(
			final byte p_Byte)
	{
		return Arrays.stream(values()).filter(
				id -> Byte.compare(p_Byte, id.getByteId().byteValue()) == 0)
				.findAny();
	}

	/**
	 * @return the information element id as a single {@link Byte}
	 * @since Jan 5, 2018
	 */
	public abstract Byte getByteId();
}
