package gov.usgs.warc.iridium.sbd.decoder.parser.elements;

import gov.usgs.warc.iridium.sbd.decoder.parser.PayloadDecoder;
import gov.usgs.warc.iridium.sbd.decoder.parser.PseudobinaryBPayloadDecoder;
import gov.usgs.warc.iridium.sbd.decoder.parser.SutronStandardCsvPayloadDecoder;

/**
 * Payload type influences how the payload is decoded.
 *
 * @author mckelvym
 * @since Mar 21, 2019
 *
 */
public enum PayloadType
{
	/**
	 * This format is based on GOES pseudobinary format. It is used when the
	 * user selects Pseudobinary B as the choice for Tx Format. The format uses
	 * ASCII characters. Three bytes are used for each data value. To correctly
	 * decode the measurement, you need to know how many readings of each
	 * measurement are included in the transmission. There is no metadata that
	 * would describe which measurement is which.
	 *
	 * @author mckelvym
	 * @since Mar 21, 2019
	 */
	PSEUDOBINARY_B_DATA_FORMAT
	{
		@Override
		public PayloadDecoder getPayloadDecoder()
		{
			return new PseudobinaryBPayloadDecoder();
		}
	},
	/**
	 * This format is based on the pseudobinary B format. It uses slightly more
	 * bandwidth than the B format, but it is self-descriptive. It is used when
	 * the user selects Pseudobinary C as the choice for Tx Format.
	 *
	 * @author mckelvym
	 * @since Mar 21, 2019
	 */
	PSEUDOBINARY_C_DATA_FORMAT,
	/**
	 * This is another compact data format. It differes from Pseudobinary B in
	 * that it has a timestamp at the start of the message. The timestamp
	 * indicates when the transmission should have taken place and helps decode
	 * when the data was collected. Pseudobinary D is 4 bytes larger than format
	 * B.
	 *
	 * The timestamp is similar to the one in Pseudobinary C. Pseudobinary D is
	 * smaller than Pseudobinary C and it lacks detailed timestamps that would
	 * allow one to completely reconstruct the time the data was collected from
	 * the message itself. To correctly use Pseudobinary D, the decoder needs to
	 * know the measurement setup used.
	 *
	 * The benefit of using Pseudobinary D is being able to correctly decode
	 * data regardless of when it was sent or received. This allows stations to
	 * re-transmit old data and have it correctly interpreted by the decoder
	 * while keeping the message size at a minimum.
	 *
	 * @author mckelvym
	 * @since Mar 21, 2019
	 */
	PSEUDOBINARY_D_DATA_FORMAT,
	/**
	 * Standard logging format that conforms to:
	 *
	 * mm/dd/yyyy,hh:mm:ss,label,data[,units,qual][,label,da ta[,units,qual]]
	 *
	 * @author mckelvym
	 * @since Mar 21, 2019
	 */
	SUTRON_STANDARD_CSV
	{
		@Override
		public PayloadDecoder getPayloadDecoder()
		{
			return new SutronStandardCsvPayloadDecoder();
		}
	},;

	/**
	 * @return a {@link PayloadDecoder} for this {@link PayloadType}
	 * @author mckelvym
	 * @since Mar 21, 2019
	 */
	public PayloadDecoder getPayloadDecoder()
	{
		return (payload, dataTypes, decodeOrder) ->
		{
			throw new UnsupportedOperationException("Not implemented.");
		};
	}
}
