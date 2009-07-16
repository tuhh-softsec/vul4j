package net.webassembletool.parse;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import net.webassembletool.HttpErrorPage;

/**
 * Represents some region in retrieved page
 * 
 * @author Stanislav Bernatskyi
 */
public interface IRegion {
    /**
     * Processes region and writes result to the provided output. Optionally may
     * use provided <code>request</code> to get information about user request.
     * 
     * @param out - output for data processed by the region
     * @param request - contains information about user request
     * @throws IOException if I/O error happens
     * @throws HttpErrorPage if error happens during local processing of
     *             result
     */
    void process(Writer out, HttpServletRequest request) throws IOException,
            HttpErrorPage;
}
