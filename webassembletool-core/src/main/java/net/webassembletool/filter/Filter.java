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

package net.webassembletool.filter;

import java.util.Properties;

import net.webassembletool.ResourceContext;
import net.webassembletool.extension.Extension;
import net.webassembletool.http.HttpClientRequest;
import net.webassembletool.http.HttpClientResponse;

/**
 * Interface for request filter.
 * 
 * @author Nicolas Richeton
 * 
 */
public interface Filter extends Extension {

	void preRequest(HttpClientRequest request, ResourceContext context);

	void postRequest(HttpClientResponse response, ResourceContext context);

	/**
	 * Does the filter need userContext ?
	 * 
	 * @return true if the filter needs the userContext to be setted beforehand
	 */
	boolean needUserContext();

	Filter EMPTY = new Filter() {
		public void init(Properties properties) {
		}

		public void preRequest(HttpClientRequest request,
				ResourceContext context) {
		}

		public void postRequest(HttpClientResponse response,
				ResourceContext context) {
		}

		public boolean needUserContext() {
			return false;
		}

	};
}
