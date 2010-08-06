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
package net.webassembletool.wicket.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockUtils {
	// Matches the last HTML tag of a block
	private static final Pattern TAG_END = Pattern.compile("</[^>]+>[^>^<]*$");

	// Matches the first HTML tag of a block
	private static final Pattern TAG_START = Pattern
			.compile("^[^>^<]*<[^>^/]+>");

	/**
	 * Discards first HTML tags from the block content.
	 * 
	 * <p>
	 * Note that the implementation is quite basic and does not try to
	 * understand HTML content. It just removes the first open tag and the last
	 * close tag from the content.
	 * </p>
	 * 
	 * @param content
	 *            HTML content
	 * @param level
	 *            Number of tags levels to remove.
	 * @return content without the removed tages
	 */
	public static String discardTags(String content, int level) {
		for (int i = 0; i < level; i++) {
			// Remove first tag.
			Matcher matcher1 = TAG_START.matcher(content);
			content = matcher1.replaceFirst("");

			// Remove last tag.
			Matcher matcher2 = TAG_END.matcher(content);
			content = matcher2.replaceFirst("");
		}
		return content;
	}

	private BlockUtils() {

	}
}
