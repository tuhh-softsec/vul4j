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

package org.esigate.servlet;

/**
 * Servlet used to proxy requests from a remote application.
 * 
 * <p>
 * Parameters are :
 * <ul>
 * <li>provider (optional - deprecated): single provider name</li>
 * <li>providers (optional - deprecated): comma-separated list of provider
 * mappings based on host requested. Format is: host1=provider,host2=provider2</li>
 * <li>useMappings (optional - recommended): true or false : use mappings from
 * esigate.properties</li>
 * </ul>
 * 
 * <p>
 * Note : provider mappings should now be configured in esigate.properties.
 * 
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 * @deprecated use {@link ProxyServlet} instead.
 */
@Deprecated
public class AggregatorServlet extends ProxyServlet {
	// This is now the same exact class than ProxyServlet
	private static final long serialVersionUID = 1L;
}
