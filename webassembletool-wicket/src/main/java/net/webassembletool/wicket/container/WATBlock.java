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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.webassembletool.Driver;
import net.webassembletool.DriverFactory;
import net.webassembletool.HttpErrorPage;
import net.webassembletool.wicket.utils.ResponseWriter;
import net.webassembletool.wicket.utils.WATNullResponse;
import net.webassembletool.wicket.utils.WATWicketConfiguration;

import org.apache.wicket.Response;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebMarkupContainer;
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
public class WATBlock extends WebMarkupContainer {

	private static final long serialVersionUID = 1L;
	private String blockName = null;
	private String page = null;
	private final boolean parseAbsoluteUrl = false;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.wicket.MarkupContainer#onComponentTagBody(org.apache.wicket
	 * .markup.MarkupStream, org.apache.wicket.markup.ComponentTag)
	 */
	@Override
	protected void onComponentTagBody(MarkupStream markupStream,
			ComponentTag openTag) {

		// For unit tests, WAT can be disabled. This component will then behave
		// like a standard MarkupContainer.
		if (WATWicketConfiguration.isDisableHttpRequests()) {
			super.onComponentTagBody(markupStream, openTag);
			return;
		}

		Response originalResponse = getRequestCycle().getResponse();

		// Drop block content
		WATNullResponse watResponse = new WATNullResponse();
		getRequestCycle().setResponse(watResponse);
		super.onComponentTagBody(markupStream, openTag);
		getRequestCycle().setResponse(originalResponse);

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
			HashMap<String, String> replaceRules = new HashMap<String, String>();

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
			driver.renderBlock(page, blockName, new ResponseWriter(
					originalResponse), request, response,
					new HashMap<String, String>(),
					new HashMap<String, String>(), false);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (HttpErrorPage e) {
			e.printStackTrace();
		}
	}

	public void setBlockName(String block) {
		this.blockName = block;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

}
