/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package net.webassembletool.wicket.container;

import net.webassembletool.wicket.utils.WATParamResponse;
import net.webassembletool.wicket.utils.WATTemplateResponse;

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
	private final String name;

	/**
	 * Create a parameter block
	 * @param id
	 *            - Must be unique in the page
	 * @param name
	 *            - Name of the block in the template.
	 */
	public WATParam(String id, String name) {
		super(id);
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.wicket.MarkupContainer#onComponentTagBody(org.apache.wicket
	 * .markup.MarkupStream, org.apache.wicket.markup.ComponentTag)
	 */
	@Override
	protected void onComponentTagBody(MarkupStream markupStream,
			ComponentTag openTag) {

		Response r = getResponse();

		if (r instanceof WATTemplateResponse) {
			WATTemplateResponse watResponse = (WATTemplateResponse) r;
			WATParamResponse wATParamResponse = new WATParamResponse();

			getRequestCycle().setResponse(wATParamResponse);
			super.onComponentTagBody(markupStream, openTag);
			getRequestCycle().setResponse(watResponse);
			watResponse.getBlocks().put(name, wATParamResponse.getContent());
		}
	}
}
