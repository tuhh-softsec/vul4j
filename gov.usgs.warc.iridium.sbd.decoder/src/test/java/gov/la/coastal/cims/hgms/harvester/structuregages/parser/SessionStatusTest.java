package gov.la.coastal.cims.hgms.harvester.structuregages.parser;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Maps;

import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.la.coastal.cims.hgms.harvester.structuregages.Tests;

/**
 * Test the {@link SessionStatus}
 *
 * @author darceyj
 * @since Jan 8, 2018
 *
 */
public class SessionStatusTest
{
	/**
	 * Assert that the test has all the required methods
	 *
	 * @throws java.lang.Exception
	 * @since Jan 8, 2018
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		final Class<?> classToTest = SessionStatus.class;
		final Class<?> testingClass = SessionStatusTest.class;
		Tests.assertHasRequiredMethods(classToTest, testingClass);

	}

	/**
	 * The map to hold the session code and the status for testing
	 *
	 * @since Jan 8, 2018
	 */
	private Map<Integer, SessionStatus>	m_CodeMap;
	/**
	 * The map to hold the status and a partial error message
	 *
	 * @since Jan 8, 2018
	 */
	private Map<SessionStatus, String>	m_ErrorMap;

	/**
	 * Setup the test
	 *
	 * @since Jan 8, 2018
	 */
	@Before
	public void setUp()
	{
		m_ErrorMap = Maps.newHashMap();
		m_ErrorMap.put(SessionStatus.SUCCESS, "success");
		m_ErrorMap.put(SessionStatus.TOO_LARGE, "large");
		m_ErrorMap.put(SessionStatus.LINK_LOSS, "lost");
		m_ErrorMap.put(SessionStatus.PROHIBITED, "prohibited");
		m_ErrorMap.put(SessionStatus.TIMED_OUT, "timed out");
		m_ErrorMap.put(SessionStatus.UNACCEPTABLE_QUALITY, "quality");
		m_ErrorMap.put(SessionStatus.PROTOCOL_ANOMOLY, "protocol");
		m_ErrorMap.put(SessionStatus.UNKNOWN, "Unknown");

		m_CodeMap = Maps.newHashMap();
		m_CodeMap.put(0, SessionStatus.SUCCESS);
		m_CodeMap.put(1, SessionStatus.TOO_LARGE);
		m_CodeMap.put(2, SessionStatus.UNACCEPTABLE_QUALITY);
		m_CodeMap.put(10, SessionStatus.TIMED_OUT);
		m_CodeMap.put(12, SessionStatus.TOO_LARGE);
		m_CodeMap.put(13, SessionStatus.LINK_LOSS);
		m_CodeMap.put(14, SessionStatus.PROTOCOL_ANOMOLY);
		m_CodeMap.put(15, SessionStatus.PROHIBITED);

	}

	/**
	 * Test method for {@link SessionStatus#getErrorMessage()}
	 *
	 * @since Jan 8, 2018
	 */
	@Test
	public void testGetErrorMessage()
	{
		m_ErrorMap.forEach((status,
				msg) -> assertThat(status.getErrorMessage().contains(msg))
						.isTrue());
	}

	/**
	 * Test method for {@link SessionStatus#getStatus(int)}
	 *
	 * @since Jan 8, 2018
	 */
	@Test
	public void testGetStatus()
	{
		m_CodeMap.forEach(
				(code, status) -> assertThat(SessionStatus.getStatus(code))
						.isEqualTo(status));
	}
}
