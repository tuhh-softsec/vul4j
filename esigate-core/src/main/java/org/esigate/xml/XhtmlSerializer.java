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

package org.esigate.xml;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

import nu.validator.htmlparser.sax.HtmlSerializer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Serializer that extends HtmlSerializer in order to close properly all html elements the xhtml way.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class XhtmlSerializer extends HtmlSerializer {
    private static final String[] VOID_ELEMENTS = { "area", "base", "basefont", "bgsound", "br", "col", "command",
            "embed", "frame", "hr", "img", "input", "keygen", "link", "meta", "param", "source", "track", "wbr" };
    private final TagClosingWriter tagClosingWriter;

    private static class TagClosingWriter extends FilterWriter {
        private boolean fix = false;

        protected TagClosingWriter(Writer out) {
            super(out);
        }

        @Override
        public void write(int c) throws IOException {
            if (fix && c == '>') {
                super.write(' ');
                super.write('/');
            }
            super.write(c);
        }

    }

    public XhtmlSerializer(Writer out) {
        this(new TagClosingWriter(out));
    }

    private XhtmlSerializer(TagClosingWriter out) {
        super(out);
        this.tagClosingWriter = out;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (Arrays.binarySearch(VOID_ELEMENTS, localName) > -1) {
            tagClosingWriter.fix = true;
        }
        super.startElement(uri, localName, qName, atts);
        tagClosingWriter.fix = false;
    }

    @Override
    public void startDocument() throws SAXException {
        // Don't generate DOCTYPE declaration
    }

}
