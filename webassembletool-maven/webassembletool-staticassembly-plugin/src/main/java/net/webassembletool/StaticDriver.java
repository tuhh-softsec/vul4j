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
package net.webassembletool;

import java.util.HashMap;
import java.util.Properties;

import net.webassembletool.output.StringOutput;

/**
 * StaticDriver is initialized with API
 * 
 * @author Alexis Thaveau
 */
public class StaticDriver extends Driver {
	private final HashMap<String, StringOutput> resources = new HashMap<String, StringOutput>();

	public StaticDriver(String name, Properties props) {
		super(name, props);
		DriverFactory.put(name, this);
	}

	/**
	 * Add Resource with default charset ISO-8859-1
	 * @param relUrl
	 * @param content
	 */
	public void addResource(String relUrl, String content) {
		this.addResource(relUrl, content,"ISO-8859-1");
		
	}
	
	/**
	 * Add Resource
	 * @param relUrl
	 * @param content
	 * @param charset
	 */
	public void addResource(String relUrl, String content, String charset) {
		StringOutput stringOutput = new StringOutput();
		stringOutput.setStatusCode(200);
		stringOutput.setStatusMessage("OK");
		stringOutput.setCharsetName(charset);
		stringOutput.open();
		stringOutput.write(content);
		resources.put(relUrl, stringOutput);
	}

	@Override
	protected StringOutput getResourceAsString(ResourceContext target)
			throws HttpErrorPage {
		StringOutput result = resources.get(target.getRelUrl());
		if (result == null)
			throw new HttpErrorPage(404, "Not found" + "The page: "
					+ target.getRelUrl() + " does not exist", "The page: "
					+ target.getRelUrl() + " does not exist");
		return result;
	}

}
