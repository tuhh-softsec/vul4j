package gov.usgs.warc.iridium.sbd.decoder.parser;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.Payload;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.PayloadType;
import gov.usgs.warc.iridium.sbd.domain.SbdDataType;
import gov.usgs.warc.iridium.sbd.domain.SbdDecodeOrder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decoder for {@link PayloadType#SUTRON_STANDARD_CSV}
 *
 * @author mckelvym
 * @since Mar 21, 2019
 *
 */
@Data
public class SutronStandardCsvPayloadDecoder implements PayloadDecoder
{
	/**
	 * Reasons that a line was or was not processed.
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 *
	 */
	enum Status
	{
		/**
		 * Quality indicator was not good.
		 *
		 * @author mckelvym
		 * @since Mar 22, 2019
		 */
		BAD_QUALITY,
		/**
		 * Not a supported data type.
		 *
		 * @author mckelvym
		 * @since Mar 22, 2019
		 */
		NO_MATCHING_DATATYPE,
		/**
		 * Processed successfully
		 *
		 * @author mckelvym
		 * @since Mar 22, 2019
		 */
		SUCCESS,
		/**
		 * Expected "value" field cannot be parsed as double.
		 *
		 * @author mckelvym
		 * @since Mar 22, 2019
		 */
		UNPARSEABLE_VALUE,
		/**
		 * Number of fields not matching expections
		 *
		 * @author mckelvym
		 * @since Mar 22, 2019
		 */
		WRONG_FIELD_COUNT,
	}

	/**
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private static final Logger	log	= LoggerFactory
			.getLogger(SutronStandardCsvPayloadDecoder.class);

	/**
	 * 0-based index for date string
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private int					dateIndex;

	/**
	 * The number of expected fields per line.
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private int					fieldCount;

	/**
	 * 0-based index for name / type
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private int					nameIndex;

	/**
	 * String that indicates the reading is "good"
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private String				qualityGoodValue;

	/**
	 * 0-based index for quality indicator
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private int					qualityIndex;

	/**
	 * 0-based index for timestamp
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private int					timeIndex;

	/**
	 * 0-based index for units string
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private int					unitsIndex;

	/**
	 * 0-based index for data value
	 *
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	private int					valueIndex;

	/**
	 * @author mckelvym
	 * @since Mar 21, 2019
	 */
	public SutronStandardCsvPayloadDecoder()
	{
		int index = 0;
		setDateIndex(index++);
		setTimeIndex(index++);
		setNameIndex(index++);
		setValueIndex(index++);
		setUnitsIndex(index++);
		setQualityIndex(index++);
		setQualityGoodValue("G");
		setFieldCount(index);
	}

	@Override
	public Map<SbdDataType, Double> decode(final Payload p_Payload,
			final SortedSet<SbdDataType> p_DataTypes,
			final SortedSet<SbdDecodeOrder> p_DecodeOrder)
			throws UnsupportedOperationException
	{
		checkArgument(
				p_Payload.getPayloadType() == PayloadType.SUTRON_STANDARD_CSV,
				"Invalid payload type for this decoder.");

		final Map<SbdDataType, Double> dataMap = Maps.newLinkedHashMap();
		final String payload = new String(p_Payload.getPayload());
		final Splitter splitter = Splitter.on(',');
		try (Scanner scanner = new Scanner(payload))
		{
			while (scanner.hasNextLine())
			{
				final String nextLine = scanner.nextLine();
				final List<String> split = splitter.splitToList(nextLine);
				final Status status = processLine(split, p_DataTypes,
						dataMap::put);
				log.info(String.format("%s: %s", status, nextLine));
			}
		}
		return dataMap;
	}

	/**
	 * Process the given line using the supplied data types.
	 *
	 * @param p_Line
	 *            the line to process
	 * @param p_DataTypes
	 *            the {@link SbdDataType} set to use
	 * @param p_ProcessedValue
	 *            a {@link BiConsumer} that will accept the {@link SbdDataType}
	 *            and value upon a success parse.
	 * @return {@link Status}
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	Status processLine(final List<String> p_Line,
			final SortedSet<SbdDataType> p_DataTypes,
			final BiConsumer<SbdDataType, Double> p_ProcessedValue)
	{
		/**
		 * Too few fields
		 */
		final int numFields = p_Line.size();
		if (numFields != getFieldCount())
		{
			log.warn(String.format("Expected %s fields, but found %s.",
					getFieldCount(), numFields));
			return Status.WRONG_FIELD_COUNT;
		}

		/**
		 * Not good quality
		 */
		final String quality = p_Line.get(getQualityIndex());
		if (!quality.equalsIgnoreCase(getQualityGoodValue()))
		{
			log.warn(String.format(
					"Expected quality indicator '%s', but found '%s'.",
					getQualityGoodValue(), quality));
			return Status.BAD_QUALITY;
		}

		final String valueS = p_Line.get(getValueIndex());
		double value = Double.NaN;
		try
		{
			value = Double.parseDouble(valueS);
		}
		catch (final NumberFormatException e)
		{
			/**
			 * Unparseable value.
			 */
			log.warn(String.format("Value cannot be parsed as double: %s. %s",
					valueS, e.getMessage()));
			return Status.UNPARSEABLE_VALUE;
		}

		final String name = p_Line.get(getNameIndex());
		final String units = p_Line.get(getUnitsIndex());

		final Predicate<SbdDataType> csvTypeFilter = type -> type
				.getBytes() == 0 && type.getName().equalsIgnoreCase(name)
				&& type.getUnits().equals(units);
		final Optional<SbdDataType> findFirst = p_DataTypes.stream()
				.filter(csvTypeFilter).findFirst();
		if (!findFirst.isPresent())
		{
			log.warn(String.format(
					"No matching data type for (name: %s, units: %s) among:\n%s",
					name, units,
					p_DataTypes.stream()
							.map(type -> String.format(
									"(name: %s, units: %s, bytes: %s)",
									type.getName(), type.getUnits(),
									type.getBytes()))
							.collect(Collectors.joining("\n - "))));
			return Status.NO_MATCHING_DATATYPE;
		}

		final SbdDataType sbdDataType = findFirst.get();
		p_ProcessedValue.accept(sbdDataType, sbdDataType.transformValue(value));
		return Status.SUCCESS;
	}
}
