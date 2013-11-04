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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.esigate.HttpErrorPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is based on Parser.
 * 
 * @see org.esigate.parser.Parser
 * @author Nicolas Richeton
 * 
 */
public class FutureParser {
    private static final Logger LOG = LoggerFactory.getLogger(FutureParser.class);
    private final Pattern pattern;
    private final FutureElementType[] elementTypes;
    private HttpEntityEnclosingRequest httpRequest;
    private HttpResponse httpResponse;
    private Map<String, Object> data = null;

    /**
     * Creates a Parser with a given regular expression pattern and ElementTypes.
     * 
     * @param pattern
     *            The regular expression Pattern
     * @param elementTypes
     *            The element types
     */
    public FutureParser(Pattern pattern, FutureElementType... elementTypes) {
        this.pattern = pattern;
        this.elementTypes = elementTypes;
    }

    /**
     * Parses all the CharSequence.
     * 
     * @param in
     *            The CharSequence to parse
     * @param out
     *            The Writable to write the result to
     * @throws IOException
     * @throws HttpErrorPage
     */
    public void parse(CharSequence in, FutureAppendable out) throws IOException, HttpErrorPage {
        FutureParserContextImpl ctx = new FutureParserContextImpl(out, this.httpRequest, this.httpResponse, this.data);
        Matcher matcher = this.pattern.matcher(in);
        int currentPosition = 0;
        while (matcher.find()) {
            String tag = matcher.group();
            ctx.characters(new CharSequenceFuture(in.subSequence(currentPosition, matcher.start())));
            currentPosition = matcher.end();
            if (ctx.isCurrentTagEnd(tag)) {
                // check if this is the end tag for current element
                LOG.info("Processing end tag {}", tag);
                ctx.endElement(tag);
            } else {
                // if not, it is an opening tag for a new element
                LOG.info("Processing start tag {}", tag);
                FutureElementType type = null;
                for (FutureElementType t : this.elementTypes) {
                    if (t.isStartTag(tag)) {
                        type = t;
                        break;
                    }
                }
                if (type != null) {
                    FutureElement element = type.newInstance();
                    ctx.startElement(type, element, tag);
                    if (element.isClosed()) {
                        ctx.endElement(tag);
                    }
                } else {
                    // if no element matches, we just ignore it and write it to
                    // the output
                    ctx.characters(new CharSequenceFuture(tag));
                }
            }
        }
        // we reached the end of input
        ctx.characters(new CharSequenceFuture(in.subSequence(currentPosition, in.length())));
    }

    public void setHttpRequest(HttpEntityEnclosingRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public void setData(String key, Object o) {
        if (this.data == null) {
            this.data = new HashMap<String, Object>();
        }

        this.data.put(key, o);
    }

}
