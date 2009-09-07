package net.webassembletool.parse;

import java.util.LinkedList;
import java.util.List;

/**
 * {@linkplain RegionParser} parser implementation used internally in
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
public class AggregateRendererRegionParser implements RegionParser {
	private final boolean propagateJsessionId;

	public static class Template {
		private final int start;
		private final int end;
		private final String content;

		public Template(int start, int end, String content) {
			this.start = start;
			this.end = end;
			this.content = content;
		}

		public int getStart() {
			return start;
		}

		public int getEnd() {
			return end;
		}

		public String getContent() {
			return content;
		}
	}

	public AggregateRendererRegionParser(boolean propagateJsessionId) {
		this.propagateJsessionId = propagateJsessionId;
	}

	/** {@inheritDoc} */
	public List<Region> parse(String content) throws AggregationSyntaxException {
		return doParse(content);
	}

	protected List<Region> doParse(String content)
			throws AggregationSyntaxException {
		List<Region> result = new LinkedList<Region>();
		Result found = find(content, 0);
		while (found != null) {
			result.add(found.getRegion());
			found = find(content, found.getPos());
		}
		return result;
	}

	/**
	 * Finds next {@linkplain Region} in content starting from provided
	 * <code>position</code>.
	 * 
	 * @return {@linkplain Result} object with next found {@linkplain Region}
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
		// 3. look for {<!--$,$-->} template
		// find most recent from them and use it further
		final Template esiComment = findTemplate("<!--esi", "-->", content,
				position);
		final Template esiInclude = findTemplate("<esi:include", "/>", content,
				position);
		final Tag includeBlockTag = Tag.find("includeblock", content, position);
		final Tag includeTemplateTag = Tag.find("includetemplate", content,
				position);
		int firstPosition = Integer.MAX_VALUE; // No tag found yet
		if (esiComment != null)
			firstPosition = esiComment.getStart();
		if (esiInclude != null && esiInclude.getStart() < firstPosition)
			firstPosition = esiInclude.getStart();
		if (includeBlockTag != null
				&& includeBlockTag.getBeginIndex() < firstPosition)
			firstPosition = includeBlockTag.getBeginIndex();
		if (includeTemplateTag != null
				&& includeTemplateTag.getBeginIndex() < firstPosition)
			firstPosition = includeTemplateTag.getBeginIndex();
		if (firstPosition == Integer.MAX_VALUE) {
			// nothing found -> all is plain content
			return new Result(new UnmodifiableRegion(content, position, content
					.length()), content.length());
		} else if (firstPosition > position) {
			// does not start with dynamic content -> report plain part
			return new Result(new UnmodifiableRegion(content, position,
					firstPosition), firstPosition);
		} else if (esiComment != null && esiComment.getStart() == firstPosition) {
			// <!--esi... -->
			String inner = esiComment.getContent();
			inner = inner.substring("<!--esi".length(), inner.length()
					- "-->".length());
			List<Region> parsed = doParse(inner);
			CompositeRegion result = new CompositeRegion();
			for (Region child : parsed) {
				result.add(child);
			}
			return new Result(result, esiComment.getEnd());
		} else if (esiInclude != null && esiInclude.getStart() == firstPosition) {
			// <esi:include... />
			EsiIncludeTag esiTag = new EsiIncludeTag(esiInclude);
			return new Result(new IncludeBlockRegion(esiTag.getProvider(),
					esiTag.getPage(), null, propagateJsessionId), esiTag
					.getEnd());
		} else if (includeBlockTag != null
				&& includeBlockTag.getBeginIndex() == firstPosition) {
			if (includeBlockTag.countTokens() < 3
					|| includeBlockTag.countTokens() > 4)
				throw new AggregationSyntaxException("Invalid syntax: "
						+ includeBlockTag);
			String provider = includeBlockTag.getToken(1);
			String page = includeBlockTag.getToken(2);
			String block = (includeBlockTag.countTokens() == 4) ? includeBlockTag
					.getToken(3)
					: null;
			Tag closeTag = Tag.find("endincludeblock", content, includeBlockTag
					.getEndIndex());
			if (closeTag == null)
				closeTag = includeBlockTag;
			return new Result(new IncludeBlockRegion(provider, page, block,
					propagateJsessionId), closeTag.getEndIndex());
		} else if (includeTemplateTag != null
				&& includeTemplateTag.getBeginIndex() == firstPosition) {
			if (includeTemplateTag.countTokens() < 3
					|| includeTemplateTag.countTokens() > 4)
				throw new AggregationSyntaxException("Invalid syntax: "
						+ includeTemplateTag);
			String provider = includeTemplateTag.getToken(1);
			String page = includeTemplateTag.getToken(2);
			String template = (includeTemplateTag.countTokens() == 4) ? includeTemplateTag
					.getToken(3)
					: null;
			Tag closeTag = Tag.find("endincludetemplate", content,
					includeTemplateTag.getEndIndex());
			if (closeTag == null)
				closeTag = includeTemplateTag;
			return new Result(new IncludeTemplateRegion(provider, page,
					template, propagateJsessionId, content.substring(
							includeTemplateTag.getEndIndex(), closeTag
									.getBeginIndex())), closeTag.getEndIndex());
		}
		// should not happen !
		return null;
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
		private final Region region;
		private final int pos;

		public Result(Region region, int pos) {
			this.region = region;
			this.pos = pos;
		}

		public Region getRegion() {
			return region;
		}

		public int getPos() {
			return pos;
		}
	}

	public static Template findTemplate(String prefix, String suffix,
			String where, int offset) {
		int begin = where.indexOf(prefix, offset);
		if (begin < 0)
			return null;
		int end = where.indexOf(suffix, begin);
		if (end < 0)
			return null;
		return new Template(begin, end + suffix.length(), where.substring(
				begin, end + suffix.length()));
	}

}
