package net.webassembletool.parse;

import java.util.StringTokenizer;

/**
 * Represents a tag in a page. This can be an opening tag or closing tag, with
 * or without parameters. Begin and end index are relative to the original
 * String it was found in.
 * 
 * @author François-Xavier Bonnet
 */
public class Tag {
    public static final String WAT_START = "<!--$";
    public static final String WAT_END = "-->";
    private static final int WAT_START_LEN = WAT_START.length();
    private static final int WAT_END_LEN = WAT_END.length();

    private final int beginIndex;
    private final int endIndex;
    private final String[] tokens;

    public int getBeginIndex() {
        return beginIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public String[] getTokens() {
        return tokens;
    }

    public String getToken(int idx) {
        return tokens[idx];
    }

    public int countTokens() {
        return tokens.length;
    }

    private Tag(int beginIndex, int endIndex, String content) {
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
        StringTokenizer stringTokenizer = new StringTokenizer(content, "$");
        tokens = new String[stringTokenizer.countTokens()];
        int i = 0;
        while (stringTokenizer.hasMoreTokens()) {
            tokens[i] = stringTokenizer.nextToken();
            i++;
        }
    }

    /**
     * Finds tag named <code>what</code> from the start of the source
     * <code>where</code>
     * 
     * @param what tag name to find
     * @param where source data where to find tag
     * @return tag found or <code>null</code> in other case
     */
    public static Tag find(String what, String where) {
        return find(what, where, 0);
    }

    /**
     * Finds next tag named <code>what</code> in the source <code>where</code>
     * after the end of the tag <code>tag</code>
     * 
     * @param what tag name to find
     * @param where source data where to find tag
     * @param tag previous tag inthe source
     * @return tag found or <code>null</code> in other case
     */
    public static Tag findNext(String what, String where, Tag tag) {
        return find(what, where, tag.getEndIndex());
    }

    public static Tag find(String what, String where, int offset) {
        Template template = findTemplate(WAT_START + what, WAT_END, where,
                offset);
        if (template != null) {
            return create(WAT_START, WAT_END, template);
        }
        return null;
    }

    /**
     * Finds tag named <code>what</code> from the start of the source
     * <code>where</code>
     * 
     * @param what tag name to find
     * @param where source data where to find tag
     * @return tag found or <code>null</code> in other case
     */
    public static Tag find(String what, StringBuilder where) {
        return find(what, where, 0);
    }

    /**
     * Finds next tag named <code>what</code> in the source <code>where</code>
     * after the end of the tag <code>tag</code>
     * 
     * @param what tag name to find
     * @param where source data where to find tag
     * @param tag previous tag inthe source
     * @return tag found or <code>null</code> in other case
     */
    public static Tag findNext(String what, StringBuilder where, Tag tag) {
        return find(what, where, tag.getEndIndex());
    }

    public static Tag find(String what, StringBuilder where, int offset) {
        int begin = where.indexOf(WAT_START + what, offset);
        if (begin < 0)
            return null;
        int end = where.indexOf(WAT_END, begin);
        if (end < 0 || end < begin)
            return null;
        return new Tag(begin, end + WAT_END_LEN, where.substring(begin
                + WAT_START_LEN, end));
    }

    @Override
    public final String toString() {
        StringBuilder result = new StringBuilder("<");
        for (int i = 0; i < tokens.length; i++) {
            result.append(tokens[i]);
            if (i < tokens.length - 1)
                result.append(",");
        }
        result.append(">");
        return result.toString();
    }

    public static Tag create(String prefix, String suffix, Template template) {
        String content = template.getContent();
        int start = prefix.length();
        int end = content.length() - suffix.length();
        return new Tag(template.getStart(), template.getEnd(), content
                .substring(start, end));
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
}
