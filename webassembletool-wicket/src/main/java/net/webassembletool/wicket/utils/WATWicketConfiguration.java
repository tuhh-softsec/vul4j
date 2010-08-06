package net.webassembletool.wicket.utils;

/**
 * Holds specific WAT / Wicket configuration.
 * 
 * @author Nicolas Richeton
 */
public class WATWicketConfiguration {
	// Disable WAT enables using unit tests without using the local or remote
	// HTTP server for templates / blocks.
	private static boolean disableHttpRequests = false;

	/**
	 * @see WATWicketConfiguration#setDisableHttpRequests(boolean)
	 * 
	 * @return true if http calls / WAT are disabled.
	 */
	public static boolean isDisableHttpRequests() {
		return disableHttpRequests;
	}

	/**
	 * This lets you disable WAT processing and turn WAT components into basic
	 * HTTP containers. Typical usage is for unit testing, where these
	 * components would normally issue http calls to another server.
	 * 
	 * @param disable
	 *            : true if HTTP calls / WAT are disabled.
	 */
	public static void setDisableHttpRequests(boolean disable) {
		WATWicketConfiguration.disableHttpRequests = disable;
	}

	private WATWicketConfiguration() {

	}
}
