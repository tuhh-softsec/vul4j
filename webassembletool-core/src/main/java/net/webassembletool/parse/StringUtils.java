package net.webassembletool.parse;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * Strings manipulation utility
 * 
 * @author Francois-Xavier Bonnet
 */
public class StringUtils {

    /**
     * Applies the replace rules to the final String to be rendered and returns
     * it. If there is no replace rule, returns the original string.
     * 
     * @param charSequence The original charSequence to apply the replacements
     *            to
     * 
     * @param replaceRules the replace rules
     * 
     * @return the result of the replace rules
     */
	public final static CharSequence replace(CharSequence charSequence,
			Map<String, String> replaceRules) {
		CharSequence result = charSequence;
		if (replaceRules != null && replaceRules.size() > 0) {
			for (Entry<String, String> replaceRule : replaceRules.entrySet()) {
				result = Pattern.compile(replaceRule.getKey()).matcher(result)
						.replaceAll(replaceRule.getValue());
			}
		}
		return result;
	}

}
