package net.webassembletool.parse;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import net.webassembletool.HttpErrorPage;

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
		out.write(StringUtils.replace(src, replaceRules).toString());
	}
}
