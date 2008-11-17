package net.webassembletool.parse;

import java.io.IOException;

import net.webassembletool.RenderException;
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
     * @throws IOException
     * @throws RenderException
     */
    void render(StringOutput src) throws IOException, RenderException;
}
