package gov.usgs.warc.iridium.sbd.decoder.parser;

import static org.assertj.core.api.Assertions.assertThat;

import gov.usgs.warc.iridium.sbd.decoder.Tests;
import gov.usgs.warc.iridium.sbd.decoder.Tests.SkipMethod;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.Header;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.LocationInformation;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.Payload;
import gov.usgs.warc.iridium.sbd.decoder.parser.elements.PayloadType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test the {@link Message} class
 *
 * @author darceyj
 * @since Jan 8, 2018
 *
 */
public class MessageTest
{
	/**
	 * Assert that the test has all the required methods
	 *
	 * @throws java.lang.Exception
	 * @since Jan 5, 2018
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		final Class<?> classToTest = Message.class;
		final Class<?> testingClass = MessageTest.class;
		Tests.assertHasRequiredMethods(classToTest, testingClass,
				SkipMethod.BUILDER, SkipMethod.CAN_EQUAL,
				SkipMethod.EQUALS_AND_HASHCODE, SkipMethod.TO_STRING);

	}

	/**
	 * The header
	 *
	 * @since Jan 8, 2018
	 */
	private Header				m_Header;

	/**
	 * @since Jan 26, 2018
	 */
	private LocationInformation	m_LocationInformation;

	/**
	 * The payload
	 *
	 * @since Jan 8, 2018
	 */
	private Payload				m_Payload;

	/**
	 * The {@link Message} to test with
	 *
	 * @since Jan 8, 2018
	 */
	private Message				m_Testable;

	/**
	 * Setup the testable object
	 *
	 * @throws java.lang.Exception
	 * @since Jan 8, 2018
	 */
	@Before
	public void setUp() throws Exception
	{
		m_Header = Header.builder().cdrId(1).id('0').imei('1')
				.length((short) 10).momsn((short) 0).mtmsn((short) 0)
				.sessionTime(1081157732).status('1').build();
		m_Payload = Payload.builder(PayloadType.PSEUDOBINARY_B_DATA_FORMAT)
				.id((byte) 0x02).payload(new byte[] { (byte) 0x00 }).build();
		m_LocationInformation = LocationInformation.builder().id((byte) 0x03)
				.cepRadius(2000L).latitude(45.123).longitude(105.321).build();
		m_Testable = Message.builder().header(m_Header).payLoad(m_Payload)
				.locationInformation(m_LocationInformation)
				.confirmationElement(null).length(10000)
				.protocolVersion((byte) 1).build();
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.Message#getConfirmationElement()}.
	 */
	@Test
	public void testGetConfirmationElement()
	{
		assertThat(m_Testable.getConfirmationElement()).isNull();
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.Message#getHeader()}.
	 */
	@Test
	public void testGetHeader()
	{
		assertThat(m_Testable.getHeader()).isEqualTo(m_Header);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.Message#getLength()}.
	 */
	@Test
	public void testGetLength()
	{
		final int expected = 10000;
		assertThat(m_Testable.getLength()).isEqualTo(expected);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.Message#getLocationInformation()}.
	 */
	@Test
	public void testGetLocationInformation()
	{
		assertThat(m_Testable.getLocationInformation())
				.isEqualTo(m_LocationInformation);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.Message#getPayLoad()}.
	 */
	@Test
	public void testGetPayLoad()
	{

		assertThat(m_Testable.getPayLoad()).isEqualTo(m_Payload);
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.parser.Message#getProtocolVersion()}.
	 */
	@Test
	public void testGetProtocolVersion()
	{
		final byte b = (byte) 1;
		assertThat(m_Testable.getProtocolVersion()).isEqualTo(b);
	}

}
