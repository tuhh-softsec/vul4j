package net.webassembletool.parse;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import net.webassembletool.RenderingException;
import net.webassembletool.output.StringOutput;

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
     * @param out output destination
     * @param replaceRules replace rules
     * @throws IOException
     * @throws RenderingException
     */
    void render(StringOutput src, Writer out, Map<String, String> replaceRules)
            throws IOException, RenderingException;
}
