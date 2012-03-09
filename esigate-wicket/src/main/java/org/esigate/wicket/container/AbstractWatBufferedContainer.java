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

package org.esigate.wicket.container;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Response;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.esigate.wicket.utils.BlockUtils;
import org.esigate.wicket.utils.WATBufferedResponse;
import org.esigate.wicket.utils.WATWicketConfiguration;

/**
 * 
 * 
 * @author Nicolas Richeton
 * 
 */
public abstract class AbstractWatBufferedContainer extends WebMarkupContainer {

	/**
	 * Serialization ID.
	 */
	private static final long serialVersionUID = 1L;

	private int discardTagsLevel = 0;

	/**
	 * Create a parameter block
	 * 
	 * @param id
	 *            - Must be unique in the page
	 */
	public AbstractWatBufferedContainer(String id) {
		super(id);
	}

	/**
	 * @see AbstractWatBufferedContainer#setDiscardTags(int)
	 * 
	 * @return current level. 0 if disabled.
	 */
	public int getDiscardTags() {
		return discardTagsLevel;
	}

	/**
	 * 
	 * @see MarkupContainer#onComponentTagBody(MarkupStream, ComponentTag)
	 */
	@Override
	protected void onComponentTagBody(MarkupStream markupStream,
			ComponentTag openTag) {

		// For unit tests, WAT can be disabled. This component will then behave
		// like a standard MarkupContainer.
		if (WATWicketConfiguration.isDisableHttpRequests()) {
			super.onComponentTagBody(markupStream, openTag);
			return;
		}

		// Normal processing.
		Response r = getResponse();

		WATBufferedResponse watParamResponse = new WATBufferedResponse();

		// Set buffered response.
		getRequestCycle().setResponse(watParamResponse);
		// Render content.
		super.onComponentTagBody(markupStream, openTag);
		// Restore original response.
		getRequestCycle().setResponse(r);

		// Get output
		String blockOutput = watParamResponse.getContent();

		// Process discard tag
		if (discardTagsLevel > 0) {
			blockOutput = BlockUtils.discardTags(blockOutput, discardTagsLevel);
		}

		process(blockOutput);

	}

	protected abstract void process(String content);

	/**
	 * Discards first HTML tags from the block content. This is a work-around
	 * for wicket's &lt;head&gt; management which doesn't let WAT catch the
	 * content without getting the &lt;head&gt; &lt;/head&gt; tags at the same
	 * time.
	 * 
	 * <p>
	 * Note that the implementation is quite basic and does not try to
	 * understand HTML content. It just removes the first open tag and the last
	 * close tag from the content
	 * </p>
	 * 
	 * 
	 * @param level
	 *            - how many tags will be removed. 0 =
	 *            <strong>Disabled</strong>.
	 */
	public void setDiscardTags(int level) {
		this.discardTagsLevel = level;
	}
}
