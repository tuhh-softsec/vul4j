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

package org.esigate.tags;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

import org.apache.http.HttpEntityEnclosingRequest;
import org.esigate.HttpErrorPage;
import org.esigate.Renderer;
import org.esigate.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Block renderer.<br/>
 * 
 * Extracts data between <code>&lt;!--$beginblock$myblock$--&gt;</code> and <code>&lt;!--$endblock$myblock$--&gt;</code>
 * separators
 * 
 * @author Stanislav Bernatskyi
 * @author Francois-Xavier Bonnet
 */
public class BlockRenderer implements Renderer, Appendable {
    private static final Logger LOG = LoggerFactory.getLogger(BlockRenderer.class);
    private static final Pattern PATTERN = Pattern.compile("<!--\\$[^>]*\\$-->");

    private final Parser parser = new Parser(PATTERN, BlockElement.TYPE);
    private final String page;
    private final String name;
    private boolean write;
    private Writer out;

    public void setWrite(boolean write) {
        this.write = write;
    }

    public String getName() {
        return name;
    }

    public BlockRenderer(String name, String page) {
        this.name = name;
        this.page = page;
        if (name == null) {
            write = true;
        } else {
            write = false;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void render(HttpEntityEnclosingRequest httpRequest, String content, Writer out) throws IOException,
            HttpErrorPage {
        LOG.debug("Rendering block " + name + " in page " + page);
        this.out = out;
        if (content == null) {
            return;
        }
        if (name == null) {
            out.write(content);
        } else {
            parser.parse(content, this);
        }
    }

    @Override
    public Appendable append(CharSequence csq) throws IOException {
        if (write) {
            out.append(csq);
        }
        return this;
    }

    @Override
    public Appendable append(char c) throws IOException {
        if (write) {
            out.append(c);
        }
        return this;
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) throws IOException {
        if (write) {
            out.append(csq, start, end);
        }
        return this;
    }

}
