package net.webassembletool.renderers;

import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.webassembletool.HttpErrorPage;
import net.webassembletool.Renderer;

/**
 * This renderer fixes links to resources, images and pages in pages retrieved
 * by the WAT. This enables use of WAT without any special modifications of the
 * generated urls on the provider side.
 * 
 * All href and src attributes are processed, except javascript links.
 * 
 * @author Nicolas Richeton
 * 
 */
public class ResourceFixupRenderer implements Renderer {

	public static final int ABSOLUTE = 0;
	public static final int RELATIVE = 1;
	public static final char SLASH = '/';
	private String server = null;
	private String pagePath = null;
	private int mode = ABSOLUTE;
	private String attributeSeparator = "\"";
	private Pattern pHref = Pattern.compile("<([^>]+)(src|href)="
			+ attributeSeparator + "([^" + attributeSeparator + "]+)"
			+ attributeSeparator + "([^>]*)>");

	/**
	 * Creates a renderer which fixes urls. The domain name and the url path are
	 * computed from the full url made of baseUrl + pageFullPath.
	 * 
	 * If mode is ABSOLUTE, all relative urls will be replaced by the full urls
	 * :
	 * <ul>
	 * <li>images/image.png is replaced by
	 * http://server/context/images/image.png</li>
	 * <li>/context/images/image.png is replaced by
	 * http://server/context/images/image.png</li>
	 * </ul>
	 * 
	 * If mode is RELATIVE, context will be added to relative urls :
	 * <ul>
	 * <li>images/image.png is replaced by /context/images/image.png</li>
	 * </ul>
	 * 
	 * @param baseUrl
	 * @param pageFullPath
	 * @param mode
	 *            ResourceFixupRenderer.ABSOLUTE or
	 *            ResourceFixupRenderer.RELATIVE
	 * @throws MalformedURLException
	 */
	public ResourceFixupRenderer(String baseUrl, String pageFullPath, int mode)
			throws MalformedURLException {
		this.mode = mode;
		
		// Clean up input
		String cleanBaseUrl  = baseUrl; 
		if( cleanBaseUrl.charAt(cleanBaseUrl.length() - 1 ) == SLASH){
			cleanBaseUrl = cleanBaseUrl.substring(0, cleanBaseUrl.length() -1 );
		}
		URL url = new URL(cleanBaseUrl + SLASH + pageFullPath);

		// Split url
		server = url.getProtocol() + "://" + url.getHost();
		if (url.getPort() > -1) {
			server += ":" + url.getPort();
		}
		this.pagePath = url.getPath();
		if (pagePath != null) {
			int indexSlash = pagePath.lastIndexOf(SLASH);
			if (indexSlash >= 0) {
				pagePath = pagePath.substring(0, indexSlash);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void render(String src, Writer out) throws IOException,
			HttpErrorPage {
		out.write(replace(src).toString());
	}

	/**
	 * Fix all resources urls and return the result.
	 * 
	 * @param charSequence
	 *            The original charSequence to be processed.
	 * 
	 * 
	 * @return the result of this renderer.
	 */
	private final CharSequence replace(CharSequence charSequence) {
		StringBuffer resultBuffer = new StringBuffer();
		Matcher m = pHref.matcher(charSequence);
		String url = null;
		String tagReplacement = null;
		while (m.find()) {
			url = fixUrl(m.group(3));
			tagReplacement = "<" + m.group(1) + m.group(2) + "=\"" + url + "\"";
			if (m.groupCount() > 3) {
				tagReplacement += m.group(4);
			}
			tagReplacement += ">";
			m.appendReplacement(resultBuffer, tagReplacement);
		}
		m.appendTail(resultBuffer);
		return resultBuffer.toString();
	}

	/**
	 * Fix an url according to the chosen mode.
	 * 
	 * @param urlParam
	 *            the url to fix.
	 * @return the fixed url.
	 */
	private String fixUrl(String urlParam) {
		String url = urlParam;

		// Do not process 0-length urls
		if (url.length() == 0)
			return url;

		// Keep absolute and javascript urls untouched.
		if (url.startsWith("http://") || url.startsWith("https://")
				|| url.startsWith("#") || url.startsWith("javascript:"))
			return url;

		// Add domain to context absolute urls
		if (url.charAt(0) == SLASH) {
			if (mode == ABSOLUTE)
				url = server + url;
			return url;
		}

		// Process relative urls
		if (mode == ABSOLUTE)
			url = server + pagePath + SLASH + url;
		else
			url = pagePath + SLASH + url;

		return url;
	}
}
