package net.webassembletool.regexp;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import net.webassembletool.HttpErrorPage;
import net.webassembletool.Renderer;

/**
 * This renderer is only meant to replace a regex
 * @author omben
 *
 */
public class ReplaceRenderer implements Renderer {
	private Map<String,String> replaceRules;
	
	/**
	 * Creates a replace renderer initialized with the given
	 * replace rules. 
	 * @param replaceRules
	 */
	public ReplaceRenderer(Map<String, String> replaceRules) {
		this.replaceRules = replaceRules;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void render(String src, Writer out) throws IOException,
			HttpErrorPage {
		out.write(replace(src, replaceRules).toString());
	}
	
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
	private final CharSequence replace(CharSequence charSequence,
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
