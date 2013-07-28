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
 */

package org.esigate.esi;

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
 * Retrieves a resource from the provider application and parses it to find ESI
 * tags to be replaced by contents from other applications.
 * 
 * For more information about ESI language specification, see <a
 * href="http://www.w3.org/TR/esi-lang">Edge Side Include</a>
 * 
 * @author Francois-Xavier Bonnet
 */
public class EsiRenderer implements Renderer, Appendable {

    private final static Logger LOG = LoggerFactory.getLogger(EsiRenderer.class);

    
 	private final static Pattern PATTERN = Pattern.compile("(<esi:\\w+((\\s+\\w+(\\s*=\\s*(?:\".*?\"|'.*?'|[^'\">\\s]+))?)+\\s*|\\s*)/?>)|(</esi:[^>]*>)");
	private final static Pattern PATTERN_COMMENTS = Pattern.compile("(<!--esi)|(-->)");

	private final Parser parser = new Parser(PATTERN, IncludeElement.TYPE, CommentElement.TYPE, RemoveElement.TYPE, VarsElement.TYPE, ChooseElement.TYPE, WhenElement.TYPE, OtherwiseElement.TYPE,
			TryElement.TYPE, AttemptElement.TYPE, ExceptElement.TYPE, InlineElement.TYPE, ReplaceElement.TYPE, FragmentElement.TYPE);

	private final Parser parserComments = new Parser(PATTERN_COMMENTS, Comment.TYPE);

    private Writer out;

    private Map<String, CharSequence> fragmentsToReplace;

    private final String page;

    private final String name;

    private boolean write = true;

    private boolean found = false;

    public String getName() {
        return name;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    /**
     * Constructor used to render a complete page
     */
    public EsiRenderer() {
        page = null;
        name = null;
    }

    /**
     * Constructor used to render a fragment Retrieves a fragment inside a page.<br />
     * 
     * Extracts html between <code>&lt;esi:fragment name="myFragment"&gt;</code>
     * and <code>&lt;/esi:fragment&gt;</code>
     * 
     * @param page
     * @param name
     */
    public EsiRenderer(String page, String name) {
        this.page = page;
        this.name = name;
        write = false;
    }

    public Map<String, CharSequence> getFragmentsToReplace() {
        return fragmentsToReplace;
    }

    public void setFragmentsToReplace(Map<String, CharSequence> fragmentsToReplace) {
        this.fragmentsToReplace = fragmentsToReplace;
    }

    @Override
	public void render(HttpEntityEnclosingRequest originalRequest, String content, Writer out) throws IOException, HttpErrorPage {
        if (name != null) {
            LOG.debug("Rendering fragment {} in page {}", name, page);
        }
        this.out = out;
        if (content == null) {
            return;
        }
        
        // Pass 1. Remove esi comments
        StringBuilder contentWithoutComments = new StringBuilder();
        parserComments.setHttpRequest(originalRequest);
        parserComments.parse(content, contentWithoutComments);

        // Pass 2. Process ESI
        parser.setHttpRequest(originalRequest);
        parser.parse(contentWithoutComments, this);
        
        if (name != null && this.found == false) {
            throw new HttpErrorPage(502, "Fragment " + name + " not found", "Fragment " + name + " not found");
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

    public boolean isWrite() {
        return this.write;
    }

    public void setFound(boolean found) {
        this.found = found;

    }

}