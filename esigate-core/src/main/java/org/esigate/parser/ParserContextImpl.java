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

package org.esigate.parser;

import java.io.IOException;
import java.util.Stack;

import org.apache.http.HttpResponse;
import org.esigate.HttpErrorPage;
import org.esigate.impl.DriverRequest;

/**
 * 
 * The stack of tags corresponding to the current position in the document
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
class ParserContextImpl implements ParserContext {
    private final RootAdapter root;
    private final DriverRequest httpRequest;
    private final HttpResponse httpResponse;

    private final Stack<ElementInfo> stack = new Stack<>();

    ParserContextImpl(Appendable root, DriverRequest httpRequest, HttpResponse httpResponse) {
        this.root = new RootAdapter(root);
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
    }

    @Override
    public <T> T findAncestor(Class<T> type) {
        T result = null;
        for (int i = stack.size() - 1; i > -1; i--) {
            Element currentElement = stack.elementAt(i).element;
            if (type.isInstance(currentElement)) {
                result = type.cast(currentElement);
                break;
            }
        }
        // try with root
        if (result == null && type.isInstance(root.root)) {
            result = type.cast(root.root);
        }

        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean reportError(Exception e) {
        boolean result = false;
        for (int i = stack.size() - 1; i > -1; i--) {
            Element element = stack.elementAt(i).element;
            if (element.onError(e, this)) {
                result = true;
                break;
            }
        }
        return result;
    }

    void startElement(ElementType type, Element element, String tag) throws IOException, HttpErrorPage {
        boolean skipContent = false;
        if (!stack.isEmpty()) {
            // Inherit from parent
            skipContent = stack.peek().skipContent;
        }
        boolean elementDoesNotSkipContent = element.onTagStart(tag, this);
        if (!skipContent) {
            skipContent = !elementDoesNotSkipContent;
        }
        stack.push(new ElementInfo(type, element, skipContent));
    }

    void endElement(String tag) throws IOException, HttpErrorPage {
        ElementInfo elementInfo = stack.pop();
        if (!elementInfo.skipContent) {
            elementInfo.element.onTagEnd(tag, this);
        }
    }

    boolean isCurrentTagEnd(String tag) {
        return !stack.isEmpty() && stack.peek().type.isEndTag(tag);
    }

    /** Writes characters into current writer. */
    public void characters(CharSequence cs) throws IOException {
        characters(cs, 0, cs.length());
    }

    /** Writes characters into current writer. */
    void characters(CharSequence csq, int start, int end) throws IOException {
        getCurrent().characters(csq, start, end);
    }

    @Override
    public Element getCurrent() {
        Element result = root;
        if (!stack.isEmpty()) {
            result = stack.peek().element;
        }
        return result;
    }

    @Override
    public DriverRequest getHttpRequest() {
        return this.httpRequest;
    }

    private static class ElementInfo {
        private final ElementType type;
        private final Element element;
        private final boolean skipContent;

        public ElementInfo(ElementType type, Element element, boolean skipContent) {
            this.type = type;
            this.element = element;
            this.skipContent = skipContent;
        }
    }

    private static class RootAdapter implements Element {
        private final Appendable root;

        public RootAdapter(Appendable root) {
            this.root = root;
        }

        @Override
        public boolean onTagStart(String tag, ParserContext ctx) {
            // Nothing to do, this is the root tag
            return true;
        }

        @Override
        public void onTagEnd(String tag, ParserContext ctx) {
            // Nothing to do, this is the root tag
        }

        @Override
        public boolean onError(Exception e, ParserContext ctx) {
            return false;
        }

        @Override
        public void characters(CharSequence csq, int start, int end) throws IOException {
            this.root.append(csq, start, end);
        }

    }

    @Override
    public HttpResponse getHttpResponse() {
        return this.httpResponse;
    }
}
