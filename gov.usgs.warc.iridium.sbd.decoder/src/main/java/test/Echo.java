package test;

import com.google.common.base.Charsets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple TCP echo server.
 *
 * @author mckelvym
 * @since Jan 29, 2018
 *
 */
public final class Echo
{
	/**
	 * @author mckelvym
	 * @since Jan 29, 2018
	 */
	private static final Logger log = LoggerFactory.getLogger(Echo.class);

	/**
	 * Simple echo server
	 *
	 * @param p_Args
	 *            one argument: port number to listen on.
	 * @throws NumberFormatException
	 * @throws UnknownHostException
	 * @throws IOException
	 * @author mckelvym
	 * @since Jan 29, 2018
	 */
	public static void main(final String... p_Args)
			throws NumberFormatException, UnknownHostException, IOException
	{
		if (p_Args.length != 1)
		{
			System.err.println("Provide one argument: port to listen on.");
			System.exit(1);
		}

		final String port = p_Args[0];
		log.info(String.format("Listening on port %s", port));
		try (final ServerSocket server = new ServerSocket(
				Integer.parseInt(port));)
		{
			while (true)
			{
				try (final Socket client = server.accept();)
				{
					log.info(String.format("Client connected: %s",
							client.getInetAddress()));
					try (OutputStream outputStream = client.getOutputStream();
						OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
								outputStream, Charsets.UTF_8);
						final PrintWriter w = new PrintWriter(
								outputStreamWriter, true);
						InputStream inputStream = client.getInputStream();
						InputStreamReader inputStreamReader = new InputStreamReader(
								inputStream, Charsets.UTF_8);
						BufferedReader bufferedReader = new BufferedReader(
								inputStreamReader);)
					{
						final String readLine = bufferedReader.readLine();
						log.info(String.format("Message: %s", readLine));
						w.println("ACK");
						w.println(readLine);
					}
				}
			}
		}
		catch (final Exception e)
		{
			log.error("Bad stuff happened.", e);
		}
	}
}