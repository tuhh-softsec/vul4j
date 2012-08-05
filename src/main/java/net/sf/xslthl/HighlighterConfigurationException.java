/**
 * 
 */
package net.sf.xslthl;

/**
 * Exception thrown on construction of a highlighter when the configuration is
 * incomplete
 */
public class HighlighterConfigurationException extends Exception {
	private static final long serialVersionUID = -4137958185614779128L;

	/**
	 * @param arg0
	 * @param arg1
	 */
	public HighlighterConfigurationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public HighlighterConfigurationException(String arg0) {
		super(arg0);
	}

}
