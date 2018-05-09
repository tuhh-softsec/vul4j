package gov.la.coastal.cims.hgms.harvester.structuregages;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.requireNonNull;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import gov.la.coastal.cims.hgms.harvester.structuregages.directip.IridiumResponse;
import gov.la.coastal.cims.hgms.harvester.structuregages.directip.IridiumToHgmsData;
import gov.la.coastal.cims.hgms.harvester.structuregages.directip.SbdProcessor;
import gov.usgs.warc.mail.SMTP;

/**
 * Alternative listener implementation.
 *
 * @author mckelvym
 * @since Mar 1, 2018
 *
 */
@Component
public class ListenerImpl implements Listener
{
	/**
	 * @author mckelvym
	 * @since Mar 1, 2018
	 */
	private static final Logger								log	= LoggerFactory
			.getLogger(ListenerImpl.class);

	/**
	 * @see IridiumToHgmsData
	 * @since Feb 22, 2018
	 */
	private final IridiumToHgmsData							m_DataTransform;

	/**
	 * @author mckelvym
	 * @since Mar 5, 2018
	 */
	private final AtomicBoolean								m_IsShutdown;

	/**
	 * @see SbdProcessor
	 * @author mckelvym
	 * @since Jan 5, 2018
	 */
	private final SbdProcessor								m_Processor;

	/**
	 * @author mckelvym
	 * @since Mar 1, 2018
	 */
	private final Properties								m_Properties;

	/**
	 * @author mckelvym
	 * @since Mar 5, 2018
	 */
	private final AtomicReference<Optional<ServerSocket>>	m_ServerSocket;

	/**
	 * @author mckelvym
	 * @since Mar 1, 2018
	 */
	private final SMTP										m_SMTP;

	/**
	 * @param p_Properties
	 *            {@link Properties}
	 * @param p_SbdProcessor
	 *            {@link SbdProcessor}
	 * @param p_IridiumToHgmsData
	 *            {@link IridiumToHgmsData}
	 * @param p_SMTP
	 *            {@link SMTP}
	 * @author mckelvym
	 * @since Mar 1, 2018
	 */
	public ListenerImpl(final Properties p_Properties,
			final SbdProcessor p_SbdProcessor,
			final IridiumToHgmsData p_IridiumToHgmsData, final SMTP p_SMTP)
	{
		m_Properties = requireNonNull(p_Properties);
		m_Processor = requireNonNull(p_SbdProcessor);
		m_DataTransform = requireNonNull(p_IridiumToHgmsData);
		m_SMTP = requireNonNull(p_SMTP);
		m_ServerSocket = new AtomicReference<>(Optional.empty());
		m_IsShutdown = new AtomicBoolean(false);
	}

	@Override
	public void run()
	{
		final int port = m_Properties.getReceivePort();
		log.info(String.format("Listening on port %s", port));
		final ExecutorService pool = Executors
				.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		try (final ServerSocket server = new ServerSocket(port);)
		{
			m_ServerSocket.set(Optional.of(server));
			while (!m_IsShutdown.get())
			{
				try
				{
					final Socket socket = server.accept();
					pool.submit(() ->
					{
						try (final Socket client = socket;)
						{
							final InetAddress inetAddress = client
									.getInetAddress();
							final byte[] bytes = ByteStreams
									.toByteArray(client.getInputStream());
							client.close();

							if (bytes == null || bytes.length == 0)
							{
								return;
							}
							log.info(String.format("Client %s sent %s",
									inetAddress, Arrays.toString(bytes)));
							final Optional<IridiumResponse> response = m_Processor
									.process(bytes, throwable ->
									{
										final String message = "Response object was null.";
										log.warn(message);
										m_SMTP.bugReport("Empty Response",
												message, null);
									});
							if (!response.isPresent())
							{
								return;
							}
							try
							{
								m_DataTransform.ingest(response.get());
								m_DataTransform.save();
								m_DataTransform.clear();
							}
							catch (final Throwable t)
							{
								final String message = "Unable to process and store data.";
								log.error(message, t);
								m_SMTP.bugReport("Failed transformation",
										message, t);
							}
						}
						catch (final Throwable t)
						{
							final String message = "Unable to read from client.";
							log.error(message, t);
							m_SMTP.bugReport("Failed connection", message, t);
						}
					});
				}
				catch (final SocketException e)
				{
					/**
					 * No problem...
					 */
					checkNotNull(e);
					m_IsShutdown.set(true);
				}
			}
			log.info("Quitting.");
		}
		catch (final Throwable e)
		{
			log.error("Bad stuff happened.", e);
			m_SMTP.bugReport(
					"Unable to listen for connections: " + e.getMessage(),
					"Unknown error", e);
		}
		finally
		{
			pool.shutdown();
		}
	}

	@Override
	public void shutdown()
	{
		m_IsShutdown.set(true);
		m_ServerSocket.get().ifPresent(serverSocket ->
		{
			try
			{
				log.info("Socket close");
				serverSocket.close();
			}
			catch (final IOException e)
			{
				final String message = String.format("Unable to close socket.");
				log.error(message, e);
			}
		});
	}

}
