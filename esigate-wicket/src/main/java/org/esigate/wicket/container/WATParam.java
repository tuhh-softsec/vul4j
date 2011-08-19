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


import org.apache.wicket.Response;
import org.esigate.wicket.utils.WATNullResponse;

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
public class WATParam extends AbstractWatBufferedContainer {

	/**
	 * Serialization ID.
	 */
	private static final long serialVersionUID = 1L;

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
	 * {@inheritDoc}
	 * 
	 * @see org.esigate.wicket.container.AbstractWatBufferedContainer#process(java.lang.String)
	 */
	@Override
	protected void process(String content) {
		// Normal processing.
		Response r = getResponse();

		if (r instanceof WATNullResponse) {
			WATNullResponse watResponse = (WATNullResponse) r;
			watResponse.getBlocks().put(name, content);
		} else {
			throw new RuntimeException(
					"WATParam can only be used within a WATTemplate.");
		}
	}

}
