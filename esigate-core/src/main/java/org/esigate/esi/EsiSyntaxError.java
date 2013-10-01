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

package org.esigate.esi;

import org.apache.http.HttpStatus;
import org.esigate.HttpErrorPage;

/**
 * Exception thrown when there is a syntax error in ESI tags.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class EsiSyntaxError extends HttpErrorPage {

	private static final long serialVersionUID = -6461207665346395033L;

	public EsiSyntaxError(String message) {
		super(HttpStatus.SC_BAD_GATEWAY, "ESI syntax error", message);
	}
}
