package gov.usgs.warc.iridium.sbd.decoder.parser.elements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.google.common.collect.Sets;
import gov.usgs.warc.iridium.sbd.decoder.Tests;
import gov.usgs.warc.iridium.sbd.decoder.parser.PayloadDecoder;
import gov.usgs.warc.iridium.sbd.decoder.parser.PseudobinaryBPayloadDecoder;
import gov.usgs.warc.iridium.sbd.decoder.parser.SutronStandardCsvPayloadDecoder;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for {@link PayloadType}
 *
 * @author mckelvym
 * @since Mar 22, 2019
 *
 */
public class PayloadTypeTest
{

	/**
	 * @throws java.lang.Exception
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		final Class<?> classToTest = PayloadType.class;
		final Class<?> testingClass = PayloadTypeTest.class;
		Tests.assertHasRequiredMethods(classToTest, testingClass);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.elements.PayloadType#getPayloadDecoder()}.
	 */
	@Test
	public void testGetPayloadDecoder()
	{
		for (final PayloadType payloadType : PayloadType.values())
		{
			final PayloadDecoder payloadDecoder = payloadType
					.getPayloadDecoder();
			switch (payloadType)
			{
				case PSEUDOBINARY_B_DATA_FORMAT:
					assertThat(payloadDecoder)
							.isInstanceOf(PseudobinaryBPayloadDecoder.class);
					break;
				case SUTRON_STANDARD_CSV:
					assertThat(payloadDecoder).isInstanceOf(
							SutronStandardCsvPayloadDecoder.class);
					break;
				default:
					assertThatThrownBy(() -> payloadDecoder.decode(
							mock(Payload.class), Sets.newTreeSet(),
							Sets.newTreeSet())).isExactlyInstanceOf(
									UnsupportedOperationException.class);
			}
		}
	}

}
