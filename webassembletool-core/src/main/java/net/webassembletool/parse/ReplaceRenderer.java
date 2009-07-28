package net.webassembletool.parse;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
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
	 * IMPORTANT: Notice that replaceRules parameter is NOT copied, thus
	 *            Calls to addRule will alter the object directly  
	 * @param replaceRules
	 */
	public ReplaceRenderer(Map<String, String> replaceRules) {
		this.replaceRules = replaceRules;
	}
	
	/**
	 * Creates ReplaceRenderer withe empty ruleset
	 */
	public ReplaceRenderer(){
		this.replaceRules = new HashMap<String,String>();
	}
	
	/**
	 * Adds the given replace rule to the renderer
	 * @param regex
	 * @param replacement
	 */
	public void addRule(String regex, String replacement){
		replaceRules.put(regex, replacement);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void render(String src, Writer out) throws IOException,
			HttpErrorPage {
		out.write(StringUtils.replace(src, replaceRules).toString());
	}
}
