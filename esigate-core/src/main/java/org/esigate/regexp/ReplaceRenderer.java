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

package org.esigate.regexp;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.http.HttpEntityEnclosingRequest;
import org.esigate.HttpErrorPage;
import org.esigate.Renderer;

/**
 * This renderer is only meant to replace a regex.
 * 
 * @author omben
 * 
 */
public class ReplaceRenderer implements Renderer {
    private final Map<String, String> replaceRules;

    /**
     * Creates a replace renderer initialized with the given replace rules.
     * 
     * @param replaceRules
     */
    public ReplaceRenderer(Map<String, String> replaceRules) {
        this.replaceRules = replaceRules;
    }

    @Override
    public void render(HttpEntityEnclosingRequest httpRequest, String src, Writer out) throws IOException,
            HttpErrorPage {
        out.write(replace(src, replaceRules).toString());
    }

    /**
     * Applies the replace rules to the final String to be rendered and returns it. If there is no replace rule, returns
     * the original string.
     * 
     * @param charSequence
     *            The original charSequence to apply the replacements to
     * 
     * @param replaceRules
     *            the replace rules
     * 
     * @return the result of the replace rules
     */
    private CharSequence replace(CharSequence charSequence, Map<String, String> replaceRules) {
        CharSequence result = charSequence;
        if (replaceRules != null && replaceRules.size() > 0) {
            for (Entry<String, String> replaceRule : replaceRules.entrySet()) {
                result = Pattern.compile(replaceRule.getKey()).matcher(result).replaceAll(replaceRule.getValue());
            }
        }
        return result;
    }

}
