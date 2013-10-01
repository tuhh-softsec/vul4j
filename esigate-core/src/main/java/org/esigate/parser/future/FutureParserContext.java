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

package org.esigate.parser.future;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

/**
 * The current context used during parsing.
 * <p>
 * This class is based on ParserContext
 * 
 * @see org.esigate.parser.ParserContext
 * @author Nicolas Richeton
 * 
 */
public interface FutureParserContext {

	/** @return {@linkplain HttpRequest} associated with current processing. */
	HttpEntityEnclosingRequest getHttpRequest();

	/** @return {@linkplain HttpResponse} associated with current processing. */
	HttpResponse getHttpResponse();

	/**
	 * @param e
	 * @return <code>true</code> if error has been handled by this element and
	 *         it should not be propagated further.
	 */
	boolean reportError(FutureElement element, Exception e);

	FutureElement getCurrent();

	<T> T findAncestor(Class<T> type);
}
