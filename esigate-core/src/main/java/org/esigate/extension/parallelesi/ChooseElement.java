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
package org.esigate.extension.parallelesi;

import org.esigate.parser.future.FutureElementType;
import org.esigate.parser.future.FutureParserContext;

class ChooseElement extends BaseElement {

	public final static FutureElementType TYPE = new BaseElementType("<esi:choose", "</esi:choose") {
		@Override
		public ChooseElement newInstance() {
			return new ChooseElement();
		}

	};

	private boolean condition;
	private boolean hasConditionSet;

	ChooseElement() {
	}

	@Override
	protected void parseTag(Tag tag, FutureParserContext ctx) {
		condition = false;
		hasConditionSet = false;
	}

	public boolean hadConditionSet() {
		return hasConditionSet;
	}

	public boolean isCondition() {
		return condition;
	}

	public void setCondition(boolean condition) {
		this.condition = condition;
		this.hasConditionSet |= condition; // set to true if anyone of
											// conditions are true
	}

}
