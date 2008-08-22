/**
 * 
 */
package net.webassembletool;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * TODO
 * 
 * @author FRBON, 22 août 2008
 */
public class StringUtils {

    /**
     * Applys the replace rules to the final String to be rendered and returns
     * it. If there is no replace rule, returns the original string.
     * 
     * @param charSequence
     * 
     * @param replaceRules
     *            the replace rules
     * 
     * @return the result of the replace rules
     */
    public final static CharSequence replace(CharSequence charSequence,
	    Map<String, String> replaceRules) {
	if (replaceRules != null && replaceRules.size() > 0) {
	    for (Entry<String, String> replaceRule : replaceRules.entrySet()) {
		charSequence = Pattern.compile(replaceRule.getKey()).matcher(
			charSequence).replaceAll(replaceRule.getValue());
	    }
	}
	return charSequence;
    }

}
