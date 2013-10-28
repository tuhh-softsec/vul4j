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

package org.esigate.aggregator;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

import org.apache.http.HttpEntityEnclosingRequest;
import org.esigate.HttpErrorPage;
import org.esigate.Renderer;
import org.esigate.parser.Parser;

/**
 * Parses a page to find tags to be replaced by contents from other providers.
 * 
 * Sample syntax used for includes :
 * <ul>
 * <li>
 * &lt;!--$includeblock$provider$page$blockname$--&gt;&lt;!--$endincludeblock$ --&gt;</li>
 * <li>&lt;!--$includetemplate$provider$page$templatename$--&gt;&lt;!-- $endincludetemplate$--&gt;</li>
 * <li>&lt;!--$beginput$name$--&gt;&lt;!--$endput$--&gt;</li>
 * </ul>
 * 
 * Sample syntax used inside included contents for template and block definition:
 * <ul>
 * <li>&lt;!--$beginblock$name$--&gt;</li>
 * <li>&lt;!--$begintemplate$name$--&gt;</li>
 * <li>&lt;!--$beginparam$name$--&gt;</li>
 * </ul>
 * 
 * @author Stanislav Bernatskyi
 * @author Francois-Xavier Bonnet
 */
public class AggregateRenderer implements Renderer, Appendable {
    /** Generic pattern for all the tags we want to look for. */
    private static final  Pattern PATTERN = Pattern.compile("<!--\\$[^>]*\\$-->");

    private final Parser parser = new Parser(PATTERN, IncludeBlockElement.TYPE, IncludeTemplateElement.TYPE,
            PutElement.TYPE);
    private Writer out;

    /** {@inheritDoc} */
    @Override
    public void render(HttpEntityEnclosingRequest httpRequest, String content, Writer out) throws IOException,
            HttpErrorPage {
        this.out = out;
        if (content == null) {
            return;
        }
        parser.setHttpRequest(httpRequest);
        parser.parse(content, this);
    }

    @Override
    public Appendable append(CharSequence csq) throws IOException {
        out.append(csq);
        return this;
    }

    @Override
    public Appendable append(char c) throws IOException {
        out.append(c);
        return this;
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) throws IOException {
        out.append(csq, start, end);
        return this;
    }

}
