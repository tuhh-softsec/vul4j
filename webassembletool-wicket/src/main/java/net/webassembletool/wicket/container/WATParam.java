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

package net.webassembletool.wicket.container;

import net.webassembletool.wicket.utils.BlockUtils;
import net.webassembletool.wicket.utils.WATParamResponse;
import net.webassembletool.wicket.utils.WATNullResponse;
import net.webassembletool.wicket.utils.WATWicketConfiguration;

import org.apache.wicket.Response;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebMarkupContainer;

/**
 * A container for a template parameter. It encloses a block which will be
 * inserted into the template.
 * 
 * <p>
 * Usage :
 * </p>
 * 
 * <p>
 * Page :
 * </p>
 * <code>
 * WATParam block1 = new WATParam( "block1" );
 * add( block1);
 * </code>
 * 
 * <p>
 * HTML : (Within an enclosing WAT Template block)
 * </p>
 * <code>
 * <div id='block1'>
 *   Block content. Can be pure html or other wicket components.
 *  </div>
 *  </code>
 * 
 * @author Nicolas Richeton
 * @see WATTemplate
 * 
 */
public class WATParam extends WebMarkupContainer {

	/**
	 * Serialization ID.
	 */
	private static final long serialVersionUID = 1L;

	private int discardTagsLevel = 0;
	private final String name;

	/**
	 * Create a parameter block
	 * 
	 * @param id
	 *            - Must be unique in the page
	 * @param name
	 *            - Name of the block in the template.
	 */
	public WATParam(String id, String name) {
		super(id);
		this.name = name;
	}

	/**
	 * @see WATParam#setDiscardTags(int)
	 * 
	 * @return current level. 0 if disabled.
	 */
	public int getDiscardTags() {
		return discardTagsLevel;
	}

	/**
	 * 
	 * @see org.apache.wicket.MarkupContainer#onComponentTagBody(org.apache.wicket
	 *      .markup.MarkupStream, org.apache.wicket.markup.ComponentTag)
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

		if (r instanceof WATNullResponse) {
			WATNullResponse watResponse = (WATNullResponse) r;
			WATParamResponse watParamResponse = new WATParamResponse();

			// Set buffered response.
			getRequestCycle().setResponse(watParamResponse);
			// Render content.
			super.onComponentTagBody(markupStream, openTag);
			// Restore original response.
			getRequestCycle().setResponse(watResponse);

			// Get output
			String blockOutput = watParamResponse.getContent();

			// Process discard tag
			if (discardTagsLevel > 0) {
				blockOutput = BlockUtils.discardTags(blockOutput,
						discardTagsLevel);
			}
			// Add param content to parent template.
			watResponse.getBlocks().put(name, blockOutput);
		} else {
			throw new RuntimeException(
					"WATParam can only be used within a WATTemplate.");
		}
	}

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
