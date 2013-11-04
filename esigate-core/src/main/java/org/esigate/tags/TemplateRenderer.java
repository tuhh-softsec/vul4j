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
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.HttpEntityEnclosingRequest;
import org.esigate.HttpErrorPage;
import org.esigate.Renderer;
import org.esigate.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Template renderer.
 * <p>
 * Retrieves a template from the provider application and renders it to the writer replacing the parameters with the
 * given map. If <code>name</code> param is <code>null</code>, the whole page will be used as the template.<br />
 * eg: The template <code>mytemplate</code> can be delimited in the provider page by comments
 * <code>&lt;!--$begintemplate$mytemplate$--&gt;</code> and <code>&lt;!--$endtemplate$mytemplate$--&gt;</code>.<br />
 * Inside the template, the parameters can be defined by comments.<br />
 * eg: parameter named <code>myparam</code> should be delimited by comments
 * <code>&lt;!--$beginparam$myparam$--&gt;</code> and <code>&lt;!--$endparam$myparam$--&gt;</code>
 * 
 * @author Stanislav Bernatskyi
 * @author Francois-Xavier Bonnet
 */
public class TemplateRenderer implements Renderer, Appendable {
    private static final Logger LOG = LoggerFactory.getLogger(TemplateRenderer.class);
    private static final Pattern PATTERN = Pattern.compile("<!--\\$[^>]*\\$-->");

    private final Parser parser = new Parser(PATTERN, TemplateElement.TYPE, ParamElement.TYPE);
    private final String page;
    private final String name;
    private final Map<String, String> params;
    private boolean write;
    private Writer out;

    public TemplateRenderer(String name, Map<String, String> params, String page) {
        this.name = name;
        this.params = params;
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
            if (params != null) {
                for (String value : params.values()) {
                    out.write(value);
                }
            }
        } else {
            parser.parse(content, this);
        }
    }

    public String getName() {
        return name;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public String getParam(String name) {
        return params.get(name);
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
