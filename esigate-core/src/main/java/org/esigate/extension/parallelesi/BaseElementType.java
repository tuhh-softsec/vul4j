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

abstract class BaseElementType implements FutureElementType {
	private final String startTag;
	private final String endTag;

	protected BaseElementType(String startTag, String endTag) {
		this.startTag = startTag;
		this.endTag = endTag;
	}

	public final boolean isStartTag(String tag) {
		return tag.startsWith(this.startTag);
	}

	public final boolean isEndTag(String tag) {
		return tag.startsWith(this.endTag);
	}

}
