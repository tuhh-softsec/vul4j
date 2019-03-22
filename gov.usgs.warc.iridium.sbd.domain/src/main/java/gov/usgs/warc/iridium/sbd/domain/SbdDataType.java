package gov.usgs.warc.iridium.sbd.domain;

import com.google.common.collect.ComparisonChain;
import org.nfunk.jep.JEP;

/**
 * Data types that Iridium SDB transmits
 *
 * @author mckelvym
 * @since Feb 2, 2018
 *
 */
public interface SbdDataType extends Comparable<SbdDataType>
{

	@Override
	public default int compareTo(final SbdDataType p_Other)
	{
		return ComparisonChain.start().compare(getId(), p_Other.getId())
				.result();
	}

	/**
	 * The number of bytes to read for this data type from the Short Burst Data
	 * payload
	 *
	 * @author mckelvym
	 * @return number of bytes to read for this data type
	 * @since Feb 16, 2018
	 */
	int getBytes();

	/**
	 * Unique data type ID
	 *
	 * @return the data type identifier
	 * @author mckelvym
	 * @since Feb 2, 2018
	 */
	Long getId();

	/**
	 * The name of the data type. This is the human-readable description, such
	 * as "Air Temperature"
	 *
	 * @return the data type name
	 * @author mckelvym
	 * @since Feb 2, 2018
	 */
	String getName();

	/**
	 * The transformation to apply to a decoded value before it should be
	 * units-converted or stored into the database. This is an expression to
	 * evaluate using JEP.
	 *
	 * See
	 * http://www.singularsys.com/jep/doc/javadoc/index.html?overview-summary.html
	 *
	 * @return the expression that transforms this data from one set of units to
	 *         another
	 * @author mckelvym
	 * @since Feb 2, 2018
	 */
	String getTransformation();

	/**
	 * Get an associated type name.
	 *
	 * @return an associated type name
	 * @author mckelvym
	 * @since May 9, 2018
	 */
	String getTypeName();

	/**
	 * The units for the data type
	 *
	 * @return the units string for this data type
	 * @author mckelvym
	 * @since Feb 2, 2018
	 */
	String getUnits();

	/**
	 * Used to rescale/adjust value after binary decode but before general use.
	 * For example, this may transform a value 10246 into 1024.6 based on the
	 * transformation expression
	 *
	 * @param p_Value
	 *            the value to use for 'x'
	 * @return the transformed value
	 * @author mckelvym
	 * @since Feb 2, 2018
	 */
	default double transformValue(final double p_Value)
	{
		final JEP parser = new JEP();
		parser.addStandardConstants();
		parser.addStandardFunctions();
		parser.addVariable("x", p_Value);
		parser.parseExpression(getTransformation());
		return parser.getValue();
	}
}
