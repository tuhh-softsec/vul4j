package net.webassembletool;

import java.util.Map;

/**
 * Utility class to generate URL and path for Resources
 * 
 * @author François-Xavier Bonnet
 */
public class ResourceUtils {

    private final static String buildQueryString(Target target) {
	StringBuilder queryString = new StringBuilder();
	Context context = target.getContext();
	Map<String, String> parameters = target.getParameters();
	if (context != null) {
	    for (Map.Entry<String, String> temp : context.getParameterMap()
		    .entrySet()) {
		queryString.append(temp.getKey()).append("=").append(
			temp.getValue()).append("&");
	    }
	}
	if (parameters != null) {
	    for (Map.Entry<String, String> temp : parameters.entrySet()) {
		queryString.append(temp.getKey()).append("=").append(
			temp.getValue()).append("&");
	    }
	}
	if (queryString.length() == 0)
	    return "";
	return queryString.substring(0, queryString.length() - 1);
    }

    private final static String concatUrl(String baseUrl, String relUrl) {
	StringBuilder url = new StringBuilder();
	if (baseUrl != null && relUrl != null
		&& (baseUrl.endsWith("/") || baseUrl.endsWith("\\"))
		&& relUrl.startsWith("/")) {
	    url.append(baseUrl.substring(0, baseUrl.length() - 1)).append(
		    relUrl);
	} else {
	    url.append(baseUrl).append(relUrl);
	}
	return url.toString();
    }

    public final static String getHttpUrlWithQueryString(String baseUrl,
	    Target target) {
	String url = concatUrl(baseUrl, target.getRelUrl());
	String queryString = ResourceUtils.buildQueryString(target);
	if ("".equals(queryString))
	    return url;
	else
	    return url + "?" + queryString;
    }

    public final static String getHttpUrl(String baseUrl, Target target) {
	String url = concatUrl(baseUrl, target.getRelUrl());
	return url;
    }

    public final static String getFileUrl(String localBase, Target target) {
	String url = concatUrl(localBase, target.getRelUrl());
	// Append queryString hashcode to supply different cache
	// filenames
	String queryString = ResourceUtils.buildQueryString(target);
	if ("".equals(queryString))
	    return url;
	else
	    return url + "_" + queryString.hashCode();
    }
}
