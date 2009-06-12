package net.webassembletool.parse;

import java.util.LinkedList;
import java.util.List;

import net.webassembletool.parse.Tag.Template;

/**
 * {@linkplain IRegionParser} parser implementation used internally in
 * {@linkplain AggregateRenderer}.
 * <p>
 * Parses content to find tags to be replaced by contents from other providers.
 * Sample syntax used for includes :
 * <ul>
 * <li>&lt;!--$includeblock$provider$page$blockname$--&gt;</li>
 * <li>&lt;!--$beginincludetemplate$provider$page$templatename$--&gt;</li>
 * <li>&lt;!--$beginput$name$--&gt;</li>
 * </ul>
 * 
 * Sample syntax used inside included contents for template and block
 * definition:
 * <ul>
 * <li>&lt;!--$beginblock$name$--&gt;</li>
 * <li>&lt;!--$begintemplate$name$--&gt;</li>
 * <li>&lt;!--$beginparam$name$--&gt;</li>
 * </ul>
 * 
 * Aggregation is always in "proxy mode" that means cookies or parameters from
 * the original request are transmitted to the target server. <br/>
 * <b>NB: Cookies and parameters are not transmitted to templates or blocks
 * invoked by the page</b>.
 * 
 * @author Stanislav Bernatskyi
 */
public class AggregateRendererRegionParser implements IRegionParser {
    private static final Template LAST = new Template(Integer.MAX_VALUE,
            Integer.MAX_VALUE, null);
    private final boolean propagateJsessionId;

    public AggregateRendererRegionParser(boolean propagateJsessionId) {
        this.propagateJsessionId = propagateJsessionId;
    }

    /** {@inheritDoc} */
    public List<IRegion> parse(String content)
            throws AggregationSyntaxException {
        return doParse(content);
    }

    protected List<IRegion> doParse(String content)
            throws AggregationSyntaxException {
        List<IRegion> result = new LinkedList<IRegion>();
        Result found = find(content, 0);
        while (found != null) {
            result.add(found.getRegion());
            found = find(content, found.getPos());
        }
        return result;
    }

    /**
     * Finds next {@linkplain IRegion} in content starting from provided
     * <code>position</code>.
     * 
     * @return {@linkplain Result} object with next found {@linkplain IRegion}
     *         and its end position for further parsing or <code>null</code> if
     *         no more regions could not be found.
     */
    protected Result find(String content, int position)
            throws AggregationSyntaxException {
        if (position >= content.length()) {
            return null;
        }
        // esi parsing
        // 1. look for {<!--esi,-->} template
        // 2. look for {<esi:include,/>} template
        // 3. look for {<!--$,-->} template
        // find most recent from them and use it further
        final Template esiComment = Tag.findTemplate("<!--esi", Tag.WAT_END,
                content, position);
        final Template esi = Tag.findTemplate("<esi:include", "/>", content,
                position);
        final Template wat = Tag.findTemplate(Tag.WAT_START + "include",
                Tag.WAT_END, content, position);
        final Template first = findFirst(LAST, esiComment, esi, wat);
        if (LAST == first) {
            // nothing found -> all is plain content
            return new Result(new UnmodifiableRegion(content, position, content
                    .length()), content.length());
        } else if (first.getStart() > position) {
            // does not start with dynamic content -> report plain part
            return new Result(new UnmodifiableRegion(content, position, first
                    .getStart()), first.getStart());
        } else if (esiComment == first) {
            // <!--esi... -->
            String inner = first.getContent();
            inner = inner.substring("<!--esi".length(), inner.length()
                    - Tag.WAT_END.length());
            List<IRegion> parsed = doParse(inner);
            CompositeRegion result = new CompositeRegion();
            for (IRegion child : parsed) {
                result.add(child);
            }
            return new Result(result, first.getEnd());
        } else if (esi == first) {
            // <esi:include... />
            EsiIncludeTag esiTag = new EsiIncludeTag(first);
            return new Result(new IncludeBlockRegion(esiTag.getProvider(),
                    esiTag.getPage(), null, propagateJsessionId), esiTag
                    .getEnd());
        } else { // wat == first
            // <!--$include...-->
            // look for includeBlock or includeTemplate markers
            Tag openTag = Tag.create(Tag.WAT_START, Tag.WAT_END, wat);
            if (openTag.countTokens() < 3 || openTag.countTokens() > 4)
                throw new AggregationSyntaxException("Invalid syntax: "
                        + openTag);
            String provider = openTag.getToken(1);
            String page = openTag.getToken(2);
            String blockOrTemplate = (openTag.countTokens() == 4) ? openTag
                    .getToken(3) : null;
            if ("includeblock".equals(openTag.getToken(0))) {
                Tag closeTag = Tag.findNext("", content, openTag);
                if (closeTag == null
                        || !"endincludeblock".equals(closeTag.getToken(0))) {
                    closeTag = openTag;
                }
                return new Result(new IncludeBlockRegion(provider, page,
                        blockOrTemplate, propagateJsessionId), closeTag
                        .getEndIndex());
            } else if ("includetemplate".equals(openTag.getToken(0))) {
                Tag closeTag = Tag.findNext("endincludetemplate", content,
                        openTag);
                return new Result(new IncludeTemplateRegion(provider, page,
                        blockOrTemplate, propagateJsessionId, content
                                .substring(openTag.getEndIndex(), closeTag
                                        .getBeginIndex())), closeTag
                        .getEndIndex());
            } else {
                // False alert, wrong tag
                throw new AggregationSyntaxException("Unknown tag: " + openTag);
            }
        }
    }

    protected Template findFirst(Template base, Template... templates) {
        Template result = base;
        for (Template template : templates) {
            if (template != null && result.getStart() > template.getStart()) {
                result = template;
            }
        }
        return result;
    }

    protected static class Result {
        private final IRegion region;
        private final int pos;

        public Result(IRegion region, int pos) {
            this.region = region;
            this.pos = pos;
        }

        public IRegion getRegion() {
            return region;
        }

        public int getPos() {
            return pos;
        }
    }
}
