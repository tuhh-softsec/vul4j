/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package net.webassembletool.wicket.container;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.webassembletool.Driver;
import net.webassembletool.DriverFactory;
import net.webassembletool.HttpErrorPage;
import net.webassembletool.wicket.utils.ResponseWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;

/**
 * A container for WAT block. Insert target content into the page.
 * 
 * <p>
 * Usage :
 * </p>
 * 
 * <p>
 * Page :
 * </p>
 * <code>
 * WATBlock block = new WATBlock( "block" );
 * add( block );
 * </code>
 * 
 * <p>
 * HTML :
 * </p>
 * <code>
 *  <div wicket:id='block'>
 *  Default content
 *  </div>
 *  </code>
 * 
 * @author Nicolas Richeton
 * 
 */
public class WATBlock extends AbstractWatDriverContainer {
	private static final Log LOG = LogFactory.getLog(WATBlock.class);

	private static final long serialVersionUID = 1L;
	private String blockName = null;
	private String page = null;
	private boolean parseAbsoluteUrl = false;
	private String provider = null;

	/**
	 * Create an include block
	 * 
	 * @param id
	 *            Wicket id
	 * @param page
	 *            relative url
	 */
	public WATBlock(String id, String page) {
		super(id);
		this.page = page;
	}

	/**
	 * Create an include block
	 * 
	 * @param id
	 *            Wicket id
	 * @param page
	 *            relative url
	 * @param blockName
	 *            block name in target content
	 */
	public WATBlock(String id, String page, String blockName) {
		super(id);
		this.page = page;
		this.blockName = blockName;
	}

	public String getBlockName() {
		return blockName;
	}

	public String getProvider() {
		return provider;
	}

	public boolean isParseAbsoluteUrl() {
		return parseAbsoluteUrl;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * 
	 * @see net.webassembletool.wicket.container.AbstractWatDriverContainer#process
	 *      (java.util.Map, java.util.Map, java.util.Map)
	 */
	@Override
	public void process(Map<String, String> blocks, Map<String, String> params,
			Map<String, String> replaceRules) {

		// Get web request and response.
		ServletWebRequest servletWebRequest = (ServletWebRequest) getRequest();
		HttpServletRequest request = servletWebRequest.getHttpServletRequest();

		WebResponse webResponse = (WebResponse) getResponse();
		HttpServletResponse response = webResponse.getHttpServletResponse();

		// Create driver
		Driver driver = null;

		if (provider == null) {
			driver = DriverFactory.getInstance();
		} else {
			driver = DriverFactory.getInstance(provider);
		}

		if (parseAbsoluteUrl) {

			String baseUrl = driver.getBaseURL();
			int baseUrlEnd = baseUrl.indexOf('/', baseUrl.indexOf("//") + 2);
			if (baseUrlEnd > 0) {
				baseUrl = baseUrl.substring(0, baseUrlEnd);
			}
			replaceRules.put("href=(\"|')/(.*)(\"|')", "href=$1" + baseUrl
					+ "/$2$3");
			replaceRules.put("src=(\"|')/(.*)(\"|')", "src=$1" + baseUrl
					+ "/$2$3");
		}

		// Render Block
		try {
			driver.renderBlock(page, blockName,
					new ResponseWriter(webResponse), request, response,
					new HashMap<String, String>(),
					new HashMap<String, String>(), false);
		} catch (IOException e) {
			this.sendErrorContent(blocks, webResponse, null);
			LOG.error(e.getMessage(), e);
		} catch (HttpErrorPage e) {
			// Insert default content
			this.sendErrorContent(blocks, webResponse, e.getStatusCode());
			LOG.warn(e.getMessage() + ": " + driver.getBaseURL() + page);
		}

	}

	public void setBlockName(String block) {
		this.blockName = block;
	}

	public void setParseAbsoluteUrl(boolean parseAbsoluteUrl) {
		this.parseAbsoluteUrl = parseAbsoluteUrl;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

}
