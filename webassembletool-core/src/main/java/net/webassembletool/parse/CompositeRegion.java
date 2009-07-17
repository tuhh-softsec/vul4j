package net.webassembletool.parse;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.webassembletool.HttpErrorPage;

/**
 * Represents a region composed of several sub-regions.
 * 
 * @author Stanislav Bernatskyi
 */
public class CompositeRegion implements Region {
    private final List<Region> children = new LinkedList<Region>();

    /**
     * {@inheritDoc}
     * <p>
     * Processes all children by iteratively invoking
     * {@linkplain Region#process(Writer, HttpServletRequest)} on them.
     */
    public void process(Writer out, HttpServletRequest request)
            throws IOException, HttpErrorPage {
        for (Region child : children) {
            child.process(out, request);
        }
    }

    public void add(Region child) {
        children.add(child);
    }

    public void remove(Region child) {
        children.remove(child);
    }

    public List<Region> getChildren() {
        return children;
    }

}
