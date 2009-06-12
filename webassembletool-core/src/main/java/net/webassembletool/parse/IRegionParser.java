package net.webassembletool.parse;

import java.util.List;


/**
 * Implementations are intended to parse retrived content into
 * {@linkplain IRegion} collection.
 * 
 * @author Stanislav Bernatskyi
 */
public interface IRegionParser {
    /**
     * Parses provided content into {@linkplain IRegion} collection
     * 
     * @param content which should be parsed
     * @return ordered collection of {@linkplain IRegion} elements
     * @throws AggregationSyntaxException in case when <code>content</code>
     *             contains invalid regions
     */
    List<IRegion> parse(String content) throws AggregationSyntaxException;
}
