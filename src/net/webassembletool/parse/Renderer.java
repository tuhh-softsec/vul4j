package net.webassembletool.parse;

import java.io.IOException;
import java.util.Map;

import net.webassembletool.RenderingException;
import net.webassembletool.ouput.StringOutput;

/**
 * Content rendering strategy.
 * 
 * @author Stanislav Bernatskyi
 */
public interface Renderer {

    /**
     * Renders provided source and writes results to the output
     * 
     * @param src source to be rendered
     * @param replaceRules replace rules
     * @throws IOException
     * @throws RenderingException
     */
    void render(StringOutput src, Map<String, String> replaceRules)
	    throws IOException, RenderingException;
}
