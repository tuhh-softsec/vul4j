package gov.la.coastal.cims.hgms.harvester.structuregages;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import gov.la.coastal.cims.hgms.common.HgmsProperties;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

/**
 * Properties class, set using the prefix iridium in application.properties
 *
 * @author mckelvym
 * @since Jan 5, 2018
 *
 */
@Data
@Getter
@ToString
@EnableConfigurationProperties
@ConfigurationProperties("iridium")
@Configuration
public class Properties implements HgmsProperties
{
	/**
	 * The HGMS provider ID
	 *
	 * @author mckelvym
	 * @since Mar 8, 2018
	 */
	private Long	hgmsProviderId;

	/**
	 * The maximum receive buffer size
	 *
	 * @author mckelvym
	 * @since Jan 5, 2018
	 */
	private int		maxMessageSize;

	/**
	 * Application name
	 *
	 * @author mckelvym
	 * @since Mar 1, 2018
	 */
	public String	name;

	/**
	 * The receive port number
	 *
	 * @author mckelvym
	 * @since Jan 5, 2018
	 */
	private int		receivePort;

	/**
	 * The remote service URL
	 *
	 * @author mckelvym
	 * @since Mar 8, 2018
	 */
	private String	serviceUrl;
}
