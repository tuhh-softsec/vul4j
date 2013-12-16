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

import java.io.IOException;
import java.util.concurrent.Future;

import org.esigate.HttpErrorPage;

/**
 * An element represents a tag inside a document.
 * 
 * <p>
 * This class is based on Element
 * 
 * @see org.esigate.parser.Element
 * @author Nicolas Richeton
 * 
 */
public interface FutureElement {
    /**
     * Method called by the parser when it finds an opening tag.
     * 
     * @param tag
     *            The tag
     * @param ctx
     *            The parser context
     * @throws IOException
     * @throws HttpErrorPage
     */
    void onTagStart(String tag, FutureParserContext ctx) throws IOException, HttpErrorPage;

    /**
     * Method called by the parser when it finds the matching closing tag.
     * 
     * @param tag
     *            The tag
     * @param ctx
     * @throws IOException
     * @throws HttpErrorPage
     */
    void onTagEnd(String tag, FutureParserContext ctx) throws IOException, HttpErrorPage;

    /**
     * @param e
     * @param ctx
     * @return <code>true</code> if error has been handled by this element and it should not be propagated further.
     */
    boolean onError(Exception e, FutureParserContext ctx);

    /**
     * Method called by the parser when it finds characters between starting and closing tags.
     * 
     * @param csq
     * @throws IOException
     */
    void characters(Future<CharSequence> csq) throws IOException;

    /**
     * @return Returns true if the tag is already closed, that means that it does not need a matching closing tag. Ex:
     *         &lt;br /&gt;
     */
    boolean isClosed();

    /**
     * Get the parent element of this element.
     * 
     * @return the parent Element or null if this item is the Root element.
     */
    FutureElement getParent();
}
