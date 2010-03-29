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
import net.webassembletool.wicket.utils.WATTemplateResponse;
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
	private String block = null;
	String page = null;

	/**
	 * Create an include block
	 * 
	 * @param id
	 * @param page
	 */
	public WATBlock(String id, String page, String block) {
		super(id);
		this.page = page;
		this.block = block;
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
		WATTemplateResponse watResponse = new WATTemplateResponse();
		getRequestCycle().setResponse(watResponse);
		super.onComponentTagBody(markupStream, openTag);
		getRequestCycle().setResponse(originalResponse);

		ServletWebRequest servletWebRequest = (ServletWebRequest) getRequest();
		HttpServletRequest request = servletWebRequest.getHttpServletRequest();

		WebResponse webResponse = (WebResponse) getResponse();
		HttpServletResponse response = webResponse.getHttpServletResponse();

		Driver driver = DriverFactory.getInstance();
		try {
			driver.renderBlock(page, block,
					new ResponseWriter(originalResponse), request, response,
					new HashMap<String, String>(),
					new HashMap<String, String>(), false);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (HttpErrorPage e) {
			e.printStackTrace();
		}
	}
}
