package net.webassembletool.parse;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import net.webassembletool.HttpErrorPage;

/**
 * Represents a <code>template</code> region which is located between either
 * <code>&lt;!--$includeblock...$--></code> and
 * <code>&lt;!--$endincludeblock$--></code> tags or by single
 * <code>&lt;!--$includeblock...$--></code> tag.
 * 
 * @author Stanislav Bernatskyi
 */
public class IncludeBlockRegion extends AbstractIncludeRegion {

    public IncludeBlockRegion(String provider, String page, String name,
            boolean propagateJsessionId) {
        super(provider, page, name, propagateJsessionId);
    }

    /** {@inheritDoc} */
    public void process(Writer out, HttpServletRequest request)
            throws IOException, HttpErrorPage {
        getDriver().renderBlock(page, name, out, request, null, null,
				propagateJsessionId, false);
	}

}
