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

package org.esigate.extension.parallelesi;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import org.apache.http.HttpEntityEnclosingRequest;
import org.esigate.HttpErrorPage;
import org.esigate.Renderer;
import org.esigate.parser.future.FutureAppendable;
import org.esigate.parser.future.FutureAppendableAdapter;
import org.esigate.parser.future.FutureParser;
import org.esigate.parser.future.StringBuilderFutureAppendable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieves a resource from the provider application and parses it to find ESI
 * tags to be replaced by contents from other applications.
 * 
 * For more information about ESI language specification, see <a
 * href="http://www.w3.org/TR/esi-lang">Edge Side Include</a>
 * 
 * <p>
 * This class is based on EsiRenderer
 * 
 * @see org.esigate.esi.EsiRenderer
 * 
 * @author Nicolas Richeton
 */
public class EsiRenderer implements Renderer, FutureAppendable {

	private final static Logger LOG = LoggerFactory.getLogger(EsiRenderer.class);
	public final static String DATA_EXECUTOR = "executor";

	private final static Pattern PATTERN = Pattern
			.compile("(<esi:\\w+((\\s+\\w+(\\s*=\\s*(?:\".*?\"|'.*?'|[^'\">\\s]+))?)+\\s*|\\s*)/?>)|(</esi:[^>]*>)");
	private final static Pattern PATTERN_COMMENTS = Pattern.compile("(<!--esi)|(-->)");

	private final FutureParser parser = new FutureParser(PATTERN, IncludeElement.TYPE, CommentElement.TYPE,
			RemoveElement.TYPE, VarsElement.TYPE, ChooseElement.TYPE, WhenElement.TYPE, OtherwiseElement.TYPE,
			TryElement.TYPE, AttemptElement.TYPE, ExceptElement.TYPE, InlineElement.TYPE, ReplaceElement.TYPE,
			FragmentElement.TYPE);

	private final FutureParser parserComments = new FutureParser(PATTERN_COMMENTS, Comment.TYPE);

	private Map<String, CharSequence> fragmentsToReplace;

	private final String page;

	private final String name;

	private boolean write = true;

	private boolean found = false;

	private FutureAppendableAdapter futureOut;

	private Executor executor;

	public String getName() {
		return name;
	}

	public void setWrite(boolean write) {
		this.write = write;
	}

	/**
	 * Constructor used to render a complete page
	 * 
	 * @param executor
	 *            Executor to use for background operations or null if
	 *            single-thread operations.
	 */
	public EsiRenderer(Executor executor) {
		this.page = null;
		this.name = null;
		this.executor = executor;
	}

	/**
	 * Constructor used to render a fragment Retrieves a fragment inside a page.<br />
	 * 
	 * Extracts html between <code>&lt;esi:fragment name="myFragment"&gt;</code>
	 * and <code>&lt;/esi:fragment&gt;</code>
	 * 
	 * @param page
	 * @param name
	 * @param executor
	 *            Executor to use for background operations or null if
	 *            single-thread operations.
	 */
	public EsiRenderer(String page, String name, Executor executor) {
		this.page = page;
		this.name = name;
		this.write = false;
		this.executor = executor;
	}

	public Map<String, CharSequence> getFragmentsToReplace() {
		return fragmentsToReplace;
	}

	public void setFragmentsToReplace(Map<String, CharSequence> fragmentsToReplace) {
		this.fragmentsToReplace = fragmentsToReplace;
	}

	public void render(HttpEntityEnclosingRequest originalRequest, String content, Writer out) throws IOException,
			HttpErrorPage {
		if (name != null) {
			LOG.debug("Rendering fragment {} in page {}", name, page);
		}
		this.futureOut = new FutureAppendableAdapter(out);
		if (content == null) {
			return;
		}

		try {
			// Pass 1. Remove esi comments
			StringBuilderFutureAppendable contentWithoutComments = new StringBuilderFutureAppendable();
			parserComments.setHttpRequest(originalRequest);
			parserComments.setData(DATA_EXECUTOR, this.executor);
			parserComments.parse(content, contentWithoutComments);
			CharSequence contentWithoutCommentsResult;

			contentWithoutCommentsResult = contentWithoutComments.get();

			// Pass 2. Process ESI
			parser.setHttpRequest(originalRequest);
			parserComments.setData(DATA_EXECUTOR, this.executor);
			parser.parse(contentWithoutCommentsResult, this);

			if (name != null && this.found == false) {
				throw new HttpErrorPage(502, "Fragment " + name + " not found", "Fragment " + name + " not found");
			}

			this.futureOut.performAppends();

		} catch (InterruptedException e) {
			throw new IOException(e);
		} catch (ExecutionException e) {
			throw new IOException(e);
		}
	}

	public FutureAppendable enqueueAppend(Future<CharSequence> csq) throws IOException {
		if (this.write) {
			this.futureOut.enqueueAppend(csq);
		}
		return this;
	}

	public boolean isWrite() {
		return this.write;
	}

	public void setFound(boolean found) {
		this.found = found;

	}

	public FutureAppendable performAppends() throws IOException, HttpErrorPage {
		return this.futureOut.performAppends();
	}

	public boolean hasPending() {
		return this.futureOut.hasPending();
	}

	public FutureAppendable performAppends(int timeout, TimeUnit unit) throws IOException, HttpErrorPage,
			TimeoutException {
		return this.futureOut.performAppends(timeout, unit);
	}

}