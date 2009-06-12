package net.webassembletool.parse;

import net.webassembletool.parse.Tag.Template;

class EsiIncludeTag {
    private final int start;
    private final int end;
    private final String provider;
    private final String page;

    public EsiIncludeTag(Template tag) throws AggregationSyntaxException {
        this.start = tag.getStart();
        this.end = tag.getEnd();
        String body = tag.getContent();
        String providerUrl = getProviderUrl(body);
        String[] providerAndPage = getProviderAndPage(providerUrl);
        this.provider = providerAndPage[0];
        this.page = providerAndPage[1];
        if (page == null || page.length() == 0) {
            throw new AggregationSyntaxException(
                    "no page specified in 'src' attribute: " + body);
        }
    }

    private String[] getProviderAndPage(String url)
            throws AggregationSyntaxException {
        int idx = url.indexOf("$PROVIDER({");
        if (idx < 0) {
            throw new AggregationSyntaxException(
                    "PROVIDER variable is missing: " + url);
        }
        int startIdx = idx + "$PROVIDER({".length();
        int endIndex = url.indexOf("})", startIdx);
        String prov = url.substring(startIdx, endIndex);
        String p = url.substring(endIndex + "})".length());
        if (p.indexOf('/') == 0) {
            p = p.substring(1);
        }
        return new String[] { prov, p };
    }

    private String getProviderUrl(String body)
            throws AggregationSyntaxException {
        int idx = body.indexOf("src=");
        if (idx < 0) {
            throw new AggregationSyntaxException(
                    "mandatory attribute 'src' is missing: " + body);
        }
        char sep = body.charAt(idx + "src=".length());
        int startIdx = idx + "src=".length() + 1;
        int endIdx = body.indexOf(sep, startIdx);
        return body.substring(startIdx, endIdx);
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getProvider() {
        return provider;
    }

    public String getPage() {
        return page;
    }

    @Override
    public String toString() {
        return new StringBuffer("esi:include,").append(provider).append(',')
                .append(page).toString();
    }
}
