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
 * A container for a template parameter. It encloses a block which will be
 * inserted into the template.
 * 
 * <p>
 * Usage :
 * </p>
 * 
 * <p>
 * Page :
 * </p>
 * <code>
 * WATTemplate template = new WATTemplate( "template" );
 * add( template);
 * </code>
 * 
 * <p>
 * HTML :
 * </p>
 * <code>
 * <div wicket:id='template'>
 *  content here is ignored
 *     <div wicket:id='block1'>
 *         Block content using WATParam. Can be pure html or other wicket components.
 *     </div>
 *  </div>
 *  </code>
 * 
 * @author Nicolas Richeton
 * @see WATParam
 * 
 */
public class WATTemplate extends AbstractWatDriverContainer {
	private static Log logger = LogFactory.getLog(WATTemplate.class);
	private static final long serialVersionUID = 1L;
	private String name = null;
	private String page = null;

	/**
	 * Create a template block
	 * 
	 * @param id
	 * @param page
	 */
	public WATTemplate(String id, String page) {
		super(id);
		this.page = page;
	}

	public String getName() {
		return name;
	}

	@Override
	public void process(Map<String, String> blocks, Map<String, String> params,
			Map<String, String> replaceRules) {
		ServletWebRequest servletWebRequest = (ServletWebRequest) getRequest();
		HttpServletRequest request = servletWebRequest.getHttpServletRequest();

		WebResponse webResponse = (WebResponse) getResponse();
		HttpServletResponse response = webResponse.getHttpServletResponse();

		Driver driver = DriverFactory.getInstance();
		try {
			driver.renderTemplate(page, name, new ResponseWriter(webResponse),
					request, response, blocks, replaceRules, params, false);
		} catch (IOException e) {
			logger.error(e);
		} catch (HttpErrorPage e) {
			logger.error(e);
		}

	}

	public void setName(String name) {
		this.name = name;
	}

}
