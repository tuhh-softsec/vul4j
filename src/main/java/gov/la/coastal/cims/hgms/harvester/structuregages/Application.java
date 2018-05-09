package gov.la.coastal.cims.hgms.harvester.structuregages;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * TCP channel listener for Iridium SBD
 *
 * https://docs.spring.io/spring-integration/reference/html/ip.html
 *
 * @author mckelvym
 * @since Jan 5, 2018
 *
 */
@SpringBootApplication(
		scanBasePackages = { "gov.la.coastal.cims.hgms.common",
				"gov.la.coastal.cims.hgms.harvester.structuregages",
				"gov.usgs.warc" })
@EnableScheduling
@Profile({ "local", "development", "production" })
public class Application
{
	/**
	 * @author mckelvym
	 * @since Mar 2, 2018
	 */
	private static final Logger log = LoggerFactory
			.getLogger(Application.class);

	/**
	 * Run the {@link SpringApplication}
	 *
	 * @param p_Args
	 * @throws IOException
	 * @throws InterruptedException
	 * @since Jan 5, 2018
	 */
	public static void main(final String[] p_Args)
			throws IOException, InterruptedException
	{
		SpringApplication.run(Application.class, p_Args);
	}

	/**
	 * @author mckelvym
	 * @since Mar 2, 2018
	 */
	private final ApplicationContext	m_Context;

	/**
	 * @author mckelvym
	 * @since Mar 2, 2018
	 */
	private final AtomicBoolean			m_IsStarted;

	/**
	 * @author mckelvym
	 * @since Mar 5, 2018
	 */
	private Optional<Listener>			m_Listener;

	/**
	 * @author mckelvym
	 * @since Mar 5, 2018
	 */
	private Optional<Thread>			m_ListenerThread;

	/**
	 * @param p_Context
	 *            {@link ApplicationContext}
	 * @author mckelvym
	 * @since Mar 2, 2018
	 */
	public Application(final ApplicationContext p_Context)
	{
		m_Context = requireNonNull(p_Context);
		m_IsStarted = new AtomicBoolean(false);
		m_ListenerThread = Optional.empty();
	}

	/**
	 * Shut down the listener thread if it is started.
	 *
	 * @author mckelvym
	 * @since Mar 5, 2018
	 */
	@PreDestroy
	public void preDestroy()
	{
		log.info("Destroy...");
		m_Listener.ifPresent(Listener::shutdown);
		m_ListenerThread.ifPresent(Thread::interrupt);
	}

	/**
	 * @throws Exception
	 * @author mckelvym
	 * @since Mar 2, 2018
	 */
	@Scheduled(initialDelay = 1000, fixedDelay = 60 * 1000)
	public final void runOnce() throws Exception
	{
		@SuppressWarnings("null")
		final Properties properties = requireNonNull(
				m_Context.getBean(Properties.class));
		if (m_IsStarted.get())
		{
			log.info(String.format("%s still listening on port %s %s",
					properties.getName(), properties.getReceivePort(),
					Arrays.toString(
							m_Context.getEnvironment().getActiveProfiles())));
			return;
		}
		m_IsStarted.set(true);

		@SuppressWarnings("null")
		final Listener listener = checkNotNull(
				m_Context.getBean(Listener.class), "Listener cannot be null.");
		log.info(String.format("%s start listening on port %s %s",
				properties.getName(), properties.getReceivePort(),
				Arrays.toString(
						m_Context.getEnvironment().getActiveProfiles())));
		m_Listener = Optional.of(listener);
		m_ListenerThread = Optional.of(new Thread(listener));
		m_ListenerThread.ifPresent(Thread::start);
	}
}
