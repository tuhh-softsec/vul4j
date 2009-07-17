package net.webassembletool.parse;

import java.util.List;


/**
 * Implementations are intended to parse retrived content into
 * {@linkplain Region} collection.
 * 
 * @author Stanislav Bernatskyi
 */
public interface RegionParser {
    /**
     * Parses provided content into {@linkplain Region} collection
     * 
     * @param content which should be parsed
     * @return ordered collection of {@linkplain Region} elements
     * @throws AggregationSyntaxException in case when <code>content</code>
     *             contains invalid regions
     */
    List<Region> parse(String content) throws AggregationSyntaxException;
}
