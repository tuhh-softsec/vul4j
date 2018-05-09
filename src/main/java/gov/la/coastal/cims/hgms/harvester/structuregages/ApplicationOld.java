package gov.la.coastal.cims.hgms.harvester.structuregages;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.serializer.Deserializer;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.connection.AbstractConnectionFactory;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpListener;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayRawSerializer;
import org.springframework.integration.transformer.AbstractTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import gov.la.coastal.cims.hgms.harvester.structuregages.directip.IridiumResponse;
import gov.la.coastal.cims.hgms.harvester.structuregages.directip.IridiumToHgmsData;
import gov.la.coastal.cims.hgms.harvester.structuregages.directip.SbdProcessor;

/***
 * TCP channel listener for Iridium
 * SBD**https://docs.spring.io/spring-integration/reference/html/ip.html
 **
 *
 * @author mckelvym
 * @since Jan 5, 2018
 *
 */
// @SpringBootApplication(
// scanBasePackages = { "gov.la.coastal.cims.hgms.common",
// "gov.la.coastal.cims.hgms.harvester.structuregages" },
// exclude = { EmbeddedServletContainerAutoConfiguration.class,
// WebMvcAutoConfiguration.class })
public class ApplicationOld
{
	/**
	 * @author mckelvym
	 * @since Mar 1, 2018
	 */
	private static final Logger log = LoggerFactory
			.getLogger(ApplicationOld.class);

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
		SpringApplication.run(ApplicationOld.class, p_Args);
	}

	/**
	 * @param p_Message
	 *            {@link Message}
	 * @return the message payload
	 * @throws Exception
	 * @author mckelvym
	 * @since Jan 5, 2018
	 */
	private static Object receiveMessage(final Message<?> p_Message)
			throws Exception
	{
		log.info(String.valueOf(p_Message.getHeaders()));
		return p_Message.getPayload();
	}

	/**
	 * @see IridiumToHgmsData
	 * @since Feb 22, 2018
	 */
	@Autowired
	private IridiumToHgmsData	m_DataTransform;

	/**
	 * @see SbdProcessor
	 * @author mckelvym
	 * @since Jan 5, 2018
	 */
	@Autowired
	private SbdProcessor		m_Processor;

	/**
	 * Connection factory bean for TCP connections.
	 *
	 * @param p_Deserializer
	 *            the {@link Deserializer} to use for data
	 * @param p_Properties
	 *            {@link Properties}
	 * @return the {@link AbstractConnectionFactory}
	 * @author mckelvym
	 * @since Jan 5, 2018
	 */
	@Bean
	public AbstractConnectionFactory connectionFactory(
			final Deserializer<?> p_Deserializer, final Properties p_Properties)
	{
		final TcpNetServerConnectionFactory factory = new TcpNetServerConnectionFactory(
				p_Properties.getReceivePort());
		factory.setDeserializer(p_Deserializer);
		return factory;
	}

	/**
	 * @return a bean for deserializing data coming from the TCP connection
	 * @author mckelvym
	 * @param p_Properties
	 *            {@link Properties}
	 * @since Jan 5, 2018
	 */
	@Bean
	public Deserializer<?> deserializer(final Properties p_Properties)
	{
		final ByteArrayRawSerializer deserializer = new ByteArrayRawSerializer();
		deserializer.setMaxMessageSize(p_Properties.getMaxMessageSize());
		return deserializer;
	}

	/**
	 * The input {@link TcpListener} bean
	 *
	 * @param p_ConnectionFactory
	 * @return {@link TcpListener} bean
	 * @author mckelvym
	 * @since Jan 5, 2018
	 */
	@Bean
	public TcpListener inboundListener(
			final AbstractServerConnectionFactory p_ConnectionFactory)
	{
		final TcpReceivingChannelAdapter adapter = new TcpReceivingChannelAdapter();
		adapter.setConnectionFactory(p_ConnectionFactory);
		adapter.setOutputChannel(inputChannel());
		return adapter;
	}

	/**
	 * @return input {@link MessageChannel} bean
	 * @author mckelvym
	 * @since Jan 5, 2018
	 */
	@Bean
	public MessageChannel inputChannel()
	{
		return new DirectChannel();
	}

	/**
	 * Service that consumes bytes from the input stream
	 *
	 * @param p_Data
	 * @author mckelvym
	 * @since Jan 5, 2018
	 */
	@ServiceActivator(inputChannel = "listenerChannel")
	public void service(final byte[] p_Data)
	{
		final Optional<IridiumResponse> responseOpt = m_Processor
				.process(p_Data, null);
		if (!responseOpt.isPresent())
		{
			return;
		}
		final IridiumResponse response = responseOpt.get();
		m_DataTransform.ingest(response);
		m_DataTransform.save();
		m_DataTransform.clear();

	}

	/**
	 * @return an {@link AbstractTransformer} bean that will unpack the payload
	 *         of a message
	 * @author mckelvym
	 * @since Jan 5, 2018
	 */
	@Transformer(
			inputChannel = "inputChannel",
			outputChannel = "listenerChannel")
	@Bean
	public AbstractTransformer transformer()
	{
		return new AbstractTransformer()
		{
			@Override
			protected Object doTransform(final Message<?> p_Message)
					throws Exception
			{
				return receiveMessage(p_Message);
			}
		};
	}
}
