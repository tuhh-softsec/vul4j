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
package org.esigate.esi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Tag {
    private static final Pattern TAG_CLOSE_PATTERN = Pattern.compile("\\A</([\\S]*)[\\s]*>\\z");
    private static final Pattern TAG_START_PATTERN = Pattern.compile("\\A<([\\S]*)[\\s|>]");
    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile("([^\\s=]+)\\s*=\\s*('[^']*'|\"[^\"]*\")");
    private static final Pattern AUTO_CLOSE_TAG = Pattern.compile("/[\\s]*>\\z");

    private final String name;
    private final Map<String, String> attributes;
    private final boolean closing;
    private final boolean openClosed;

    public static Tag create(String tag) {
        // check for close tag pattern firs as '/' is included into '\S' regexp in TAG_START_PATTERN
        Matcher closeMatcher = TAG_CLOSE_PATTERN.matcher(tag);
        if (closeMatcher.find()) {
            String name = closeMatcher.group(1);
            Map<String, String> attributes = Collections.emptyMap();

            return new Tag(name, true, false, attributes);
        }

        Matcher startMatcher = TAG_START_PATTERN.matcher(tag);
        if (startMatcher.find()) {
            String name = startMatcher.group(1);

            Map<String, String> attributes = new HashMap<String, String>();
            Matcher attributesMatcher = ATTRIBUTE_PATTERN.matcher(tag);
            while (attributesMatcher.find()) {
                // attributesMatcher.group(2) matches to the attribute value including surrounded quotes
                attributes.put(attributesMatcher.group(1),
                        tag.substring(attributesMatcher.start(2) + 1, attributesMatcher.end(2) - 1));
            }
            boolean openClosed = AUTO_CLOSE_TAG.matcher(tag).find();

            return new Tag(name, false, openClosed, attributes);
        }

        throw new IllegalArgumentException("invalid tag string: '" + tag + "'");
        // return null;
    }

    Tag(String name, boolean closing, boolean openClosed, Map<String, String> attributes) {
        this.name = name;
        this.closing = closing;
        this.openClosed = openClosed;
        this.attributes = attributes;
    }

    public String getName() {
        return name;
    }

    public String getAttribute(String name) {
        return attributes.get(name);
    }

    public boolean isClosing() {
        return closing;
    }

    public boolean isOpenClosed() {
        return openClosed;
    }

}
